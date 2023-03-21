package com.paoloprodossimolopes.libraryapidemo.api.resource;

import com.paoloprodossimolopes.libraryapidemo.api.dto.LoanDTO;
import com.paoloprodossimolopes.libraryapidemo.api.model.Loan;
import com.paoloprodossimolopes.libraryapidemo.api.service.BookService;
import com.paoloprodossimolopes.libraryapidemo.api.service.LoanService;
import com.paoloprodossimolopes.libraryapidemo.model.entity.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Long createLoan(@RequestBody LoanDTO dto) {
        Book book = bookService.getBookByIsbn(dto.getIsbn()).get();
        Loan loan = Loan.builder().customer(dto.getCustomer()).book(book).loanDate(LocalDate.now()).build();
        loan = loanService.save(loan);
        return loan.getId();
    }
}
