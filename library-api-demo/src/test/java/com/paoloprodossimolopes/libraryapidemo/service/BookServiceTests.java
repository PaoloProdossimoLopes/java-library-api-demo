package com.paoloprodossimolopes.libraryapidemo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paoloprodossimolopes.libraryapidemo.api.exceptions.BussinessException;
import com.paoloprodossimolopes.libraryapidemo.api.model.repository.BookRepository;
import com.paoloprodossimolopes.libraryapidemo.api.service.BookService;
import com.paoloprodossimolopes.libraryapidemo.api.service.implementation.BookServiceImpl;
import com.paoloprodossimolopes.libraryapidemo.model.entity.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTests {

    @MockBean
    BookRepository repository;
    BookService service;

    @BeforeEach
    void setUp() {
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    void saveBookTest() {
        Book book = makeBook();
        Book bookMocked =  makeBookFromDataBase(book);
        mockRepositoryResponse(book, bookMocked);
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Book saved = service.save(book);

        Assertions.assertEquals(saved.getId(), bookMocked.getId());
        Assertions.assertEquals(saved.getAuthor(), book.getAuthor());
        Assertions.assertEquals(saved.getTitle(), book.getTitle());
        Assertions.assertEquals(saved.getIsbn(), book.getIsbn());
    }

    @Test
    @DisplayName("Dee lancar BussnessExpcetion ao tentar salvar um livro com ISBN duplicado")
    void shouldNotSaveBookWithDuplicatedISBN() {
        Book book = makeBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
        Exception exc = Assertions.assertThrows(BussinessException.class, () -> service.save(book) );

        Assertions.assertInstanceOf(BussinessException.class, exc);
        Assertions.assertEquals(exc.getMessage(), "Already have a other book with this ISBN");
        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve obter um livro por ID")
    void getByIDTest() {
        Long id = 1L;
        Book book = makeBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        Optional<Object> foundObject = service.getByID(id);
//        Book foundBook = new ObjectMapper().convertValue(foundObject, Book.class);
//
//        Assertions.assertTrue(foundObject.isPresent());
//        Assertions.assertEquals(foundBook.getId(), book.getId());
//        Assertions.assertEquals(foundBook.getTitle(), book.getTitle());
//        Assertions.assertEquals(foundBook.getAuthor(), book.getAuthor());
//        Assertions.assertEquals(foundBook.getIsbn(), book.getIsbn());
    }

    @Test
    @DisplayName("Deve retornar vazio quando obter um livro nao cadastrado na base")
    void getNullByIDTest() {
        Long id = 1L;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Object> object = service.getByID(id);

        Assertions.assertFalse(object.get() == null);
    }

    @Test
    @DisplayName("Deve filtrar livros pelas props")
    void findBookTest() {
        final Book book = makeBook();
        final List<Book> books = Arrays.asList(book);
        final int currentPage = 0;
        final int totalElementsOnPage = 1;
        final int totalElememtsDelivers = 1;
        final PageRequest pageRequest = PageRequest.of(currentPage, totalElementsOnPage);
        final Page<Book> page = new PageImpl<Book>(books, pageRequest, totalElememtsDelivers);
        Mockito
                .when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        final Page<Book> result = service.find(book, pageRequest);

        Assertions.assertEquals(result.getTotalElements(), totalElememtsDelivers);
        Assertions.assertEquals(result.getContent(), books);
        Assertions.assertEquals(result.getPageable().getPageNumber(), currentPage);
        Assertions.assertEquals(result.getPageable().getPageSize(), totalElementsOnPage);
    }
    private Book makeBook() {
        return Book.builder()
                .isbn("any valid isbn")
                .author("any valid author name")
                .title("any valid book title")
                .build();
    }

    private Book makeBookFromDataBase(Book book) {
        return Book.builder()
                .id(1L)
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .build();
    }

    private void mockRepositoryResponse(Book book, Book mocked) {
        OngoingStubbing<Book> saveMocked = getSaveMethodMocked(book);
        saveMocked.thenReturn(mocked);
    }

    private OngoingStubbing<Book> getSaveMethodMocked(Book book) {
        OngoingStubbing<Book> saveMocked = Mockito.when(repository.save(book));
        return saveMocked;
    }
}
