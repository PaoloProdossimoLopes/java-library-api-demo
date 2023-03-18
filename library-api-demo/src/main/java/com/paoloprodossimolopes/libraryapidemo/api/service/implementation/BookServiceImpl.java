package com.paoloprodossimolopes.libraryapidemo.api.service.implementation;

import com.paoloprodossimolopes.libraryapidemo.api.exceptions.BussinessException;
import com.paoloprodossimolopes.libraryapidemo.api.model.repository.BookRepository;
import com.paoloprodossimolopes.libraryapidemo.api.service.BookService;
import com.paoloprodossimolopes.libraryapidemo.model.entity.Book;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if (repository.existsISBN(book.getIsbn())) throw new BussinessException("Already have a other book with this ISBN");
        return repository.save(book);
    }
}
