package com.paoloprodossimolopes.libraryapidemo.service;

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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
        Mockito.when(repository.existsISBN(Mockito.anyString())).thenReturn(false);
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
        Mockito.when(repository.existsISBN(Mockito.anyString())).thenReturn(true);
        Exception exc = Assertions.assertThrows(BussinessException.class, () -> service.save(book) );

        Assertions.assertInstanceOf(BussinessException.class, exc);
        Assertions.assertEquals(exc.getMessage(), "Already have a other book with this ISBN");
        Mockito.verify(repository, Mockito.never()).save(book);
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
