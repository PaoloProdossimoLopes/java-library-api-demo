package com.paoloprodossimolopes.libraryapidemo.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paoloprodossimolopes.libraryapidemo.api.dto.BookDTO;
import com.paoloprodossimolopes.libraryapidemo.api.exceptions.BussinessException;
import com.paoloprodossimolopes.libraryapidemo.api.service.BookService;
import com.paoloprodossimolopes.libraryapidemo.model.entity.Book;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.swing.text.html.Option;
import java.util.Optional;

@WebMvcTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class BookControllerTests {

    static String BOOK_API = "/api/books";

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    @DisplayName("Deve criar um livro com sucesso.")
    void createBookTests() throws Exception {

        BookDTO bookDTO = makeBookDTO();
        String json = makeJSONString(bookDTO);

        Book book = makeBook(bookDTO);
        mockServiceResponse(book);

        MockHttpServletRequestBuilder request = mockRequest(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(book.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(bookDTO.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(bookDTO.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(bookDTO.getIsbn()));
    }

    @Test
    @DisplayName("Deve lancar erro de validaçao quando nao houver dados suficientes para a criaçao do livro.")
    void createInvalidBookTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new BookDTO());
        MockHttpServletRequestBuilder request = mockRequest(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar cadastrar um livro com isbn utilizado por outro")
    void createBookWithDuplicatedIsbn() throws Exception {
        String errrorMessage = "Already have a other book with this ISBN";
        mockResponse().willThrow(new BussinessException(errrorMessage));
        BookDTO dto = makeBookDTO();
        String json = makeJSONString(dto);

        mvc.perform(mockRequest(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(errrorMessage));
    }

    @Test
    @DisplayName("Deve obtr informaçoes de um livro")
    void getBookDetailsTests() throws Exception {
        Long id = 1L;
        Book book = makeBook(makeBookDTO());
        BDDMockito.given(service.getByID(id)).willReturn(Optional.of(book));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(book.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(book.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(book.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(book.getIsbn()));
        ;
    }

    @Test
    @DisplayName("Deve retornar NotFound quando nao encontrar a busca pelo ID")
    void shouldDelieverNotFoundIfCanNotGet() throws Exception {
        BDDMockito.given(service.getByID(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um livro")
    void shouldDeleveABook() throws Exception {
        final Long id = 1L;

        BDDMockito
                .given(service.getByID(id))
                .willReturn(Optional.of(makeBook(makeBookDTO())));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornara not found quando deletar e nao encontara o ID")
    void deleiversNotFoundWhenDeletesWithInvalidID() throws Exception {
        BDDMockito.given(service.getByID(Mockito.anyLong())).willReturn(Optional.empty());
        RequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar o livro")
    void updateBookToTest() throws Exception {
        final Book book = makeBook(makeBookDTO());
        final BookDTO newBook = BookDTO.builder().id(book.getId()).title("any new name").isbn("any new valid isbn").author("any valid new author").build();
        final String json = makeJSONString(newBook);
        BDDMockito.given(service.getByID(book.getId())).willReturn(Optional.of(newBook));
        BDDMockito.given(service.update(Mockito.any())).willReturn(Book.builder().id(book.getId()).title("any new name").isbn("any new valid isbn").author("any valid new author").build());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + book.getId()))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(book.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(newBook.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(newBook.getIsbn()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(newBook.getAuthor()))
        ;
    }

    @Test
    @DisplayName("Deve retornar 404 ao atualizar um livro inesistente")
    void updateBookFailsToTest() throws Exception {
        final Book book = makeBook(makeBookDTO());
        final BookDTO newBook = BookDTO.builder().id(book.getId()).title("any new name").isbn("any new valid isbn").author("any valid new author").build();
        final String json = makeJSONString(newBook);
        BDDMockito.given(service.getByID(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + book.getId()))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        ;
    }

    private BookDTO makeBookDTO() {
        return BookDTO.builder()
                .title("any book title")
                .author("any author name")
                .isbn("any valid ISBN")
                .build();
    }

    private Book makeBook(BookDTO dto) {
        return Book.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .isbn(dto.getIsbn())
                .id(1L)
                .build();
    }

    private String makeJSONString(BookDTO bookDTO) throws Exception {
        return new ObjectMapper().writeValueAsString(bookDTO);
    }

    private void mockServiceResponse(Book book) {
        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(book);
    }

    private BDDMockito.BDDMyOngoingStubbing<Book> mockResponse() {
        return BDDMockito.given(service.save(Mockito.any(Book.class)));
    }

    private MockHttpServletRequestBuilder mockRequest(String json) {
        return MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
    }
}