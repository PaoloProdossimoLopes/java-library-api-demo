package com.paoloprodossimolopes.libraryapidemo.api.service.implementation;

import com.paoloprodossimolopes.libraryapidemo.api.exceptions.BussinessException;
import com.paoloprodossimolopes.libraryapidemo.api.model.repository.BookRepository;
import com.paoloprodossimolopes.libraryapidemo.api.service.BookService;
import com.paoloprodossimolopes.libraryapidemo.model.entity.Book;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if (repository.existsByIsbn(book.getIsbn())) throw new BussinessException("Already have a other book with this ISBN");
        return repository.save(book);
    }

    @Override
    public Optional<Object> getByID(Long id) {
        Optional<Book> book = repository.findById(id);
        return Optional.of((Object) book);
    }

    @Override
    public void delete(Book book) {
        if (book.getId() == null)
            throw new IllegalArgumentException("Book id cantr be null");

        repository.delete(book);
    }

    @Override
    public Book update(Book book) {
        if (book.getId() == null)
            throw new IllegalArgumentException("Book id cantr be null");

        return save(book);
    }

    @Override
    public Page<Book> find(Book filter, Pageable page) {
        Example<Book> example = Example.of(filter,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
        );
        return repository.findAll(example, page);
    }
}
