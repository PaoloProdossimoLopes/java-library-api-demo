package com.paoloprodossimolopes.libraryapidemo.api.resource;

import com.paoloprodossimolopes.libraryapidemo.api.dto.BookDTO;
import com.paoloprodossimolopes.libraryapidemo.api.exceptions.BussinessException;
import com.paoloprodossimolopes.libraryapidemo.api.exceptions.InvalidParamsException;
import com.paoloprodossimolopes.libraryapidemo.api.service.BookService;
import com.paoloprodossimolopes.libraryapidemo.model.entity.Book;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService service;
    private final ModelMapper mapper;

    public BookController(BookService service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    BookDTO createBook(@RequestBody @Valid  BookDTO dto) {
        Book entity = mapper.map(dto, Book.class);
        Book savedBook = service.save(entity);
        return mapper.map(savedBook, BookDTO.class);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    InvalidParamsException handleValidationExpeptions(MethodArgumentNotValidException exeption) throws Exception{
        BindingResult result = exeption.getBindingResult();
        return new InvalidParamsException(result);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BussinessException.class)
    InvalidParamsException handleBussinessExpecetion(BussinessException exeption) throws Exception {
        return new InvalidParamsException(exeption);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{id}")
    BookDTO getBookByID(@PathVariable Long id) {
        return service.getByID(id)
                .map(book -> mapper.map(book, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteByID(@PathVariable Long id) {
        final Book book = mapper.map(getBookByID(id), Book.class);
        service.delete(book);
    }
}
