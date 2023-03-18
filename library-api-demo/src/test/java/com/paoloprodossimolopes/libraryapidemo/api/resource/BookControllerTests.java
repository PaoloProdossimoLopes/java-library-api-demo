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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.regex.Matcher;

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