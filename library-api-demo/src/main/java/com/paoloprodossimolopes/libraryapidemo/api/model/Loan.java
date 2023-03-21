package com.paoloprodossimolopes.libraryapidemo.api.model;

import com.paoloprodossimolopes.libraryapidemo.model.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Loan {
    private Long id;
    private String customer;
    private Book book;
    private LocalDate loanDate;
    private Boolean isReturned;
}



