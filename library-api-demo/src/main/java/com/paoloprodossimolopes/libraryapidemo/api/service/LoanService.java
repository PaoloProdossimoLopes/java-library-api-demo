package com.paoloprodossimolopes.libraryapidemo.api.service;

import com.paoloprodossimolopes.libraryapidemo.api.resource.LoanFilterDTO;
import com.paoloprodossimolopes.libraryapidemo.model.entity.Book;
import com.paoloprodossimolopes.libraryapidemo.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO filterDTO, Pageable pageable);

    Page<Loan> getLoansByBook(Book book, Pageable pageable);

    List<Loan> getAllLateLoans();

}
