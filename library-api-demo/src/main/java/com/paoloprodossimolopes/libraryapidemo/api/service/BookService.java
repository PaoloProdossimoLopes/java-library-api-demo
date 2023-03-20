package com.paoloprodossimolopes.libraryapidemo.api.service;

import com.paoloprodossimolopes.libraryapidemo.model.entity.Book;

import java.util.Optional;

public interface BookService {
    Book save(Book book);

    Optional<Object> getByID(Long id);

    void delete(Book book);

    Book update(Book book);
}
