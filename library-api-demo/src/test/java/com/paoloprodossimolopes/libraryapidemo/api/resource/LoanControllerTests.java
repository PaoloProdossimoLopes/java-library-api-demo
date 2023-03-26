package com.paoloprodossimolopes.libraryapidemo.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paoloprodossimolopes.libraryapidemo.api.dto.LoanDTO;
import com.paoloprodossimolopes.libraryapidemo.model.entity.Loan;
import com.paoloprodossimolopes.libraryapidemo.api.service.BookService;
import com.paoloprodossimolopes.libraryapidemo.api.service.LoanService;
import com.paoloprodossimolopes.libraryapidemo.model.entity.Book;
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

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WebMvcTest(controllers = LoanController.class)
public class LoanControllerTests {
    
    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mvc;

    @MockBean
    private BookService bookService;
    @MockBean
    private LoanService loanService;

    @Test
    @DisplayName("Deve realizar um meprestimo")
    void createLoanTest() throws Exception {
        LoanDTO dto = LoanDTO
                .builder()
                .isbn("any isbn")
                .customer("any customer")
                .build();
        String json = new ObjectMapper().writeValueAsString(dto);
        Book delieveredBook = Book.builder().id(1L).isbn(dto.getIsbn()).build();

        Loan loan = Loan.builder()
                .id(1L)
                .customer("any customer name")
                .book(delieveredBook)
                .loanDate(LocalDate.now())
                .build();
        BDDMockito
                .given(bookService.getBookByIsbn(dto.getIsbn()))
                .willReturn(Optional.of(delieveredBook));
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string(String.format("%d", delieveredBook.getId())));
    }
}
