package com.paoloprodossimolopes.libraryapidemo.api.resource;

import com.paoloprodossimolopes.libraryapidemo.api.dto.BookDTO;
import com.paoloprodossimolopes.libraryapidemo.api.exceptions.BussinessException;
import com.paoloprodossimolopes.libraryapidemo.api.exceptions.InvalidParamsException;
import com.paoloprodossimolopes.libraryapidemo.api.service.BookService;
import com.paoloprodossimolopes.libraryapidemo.model.entity.Book;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<BookDTO> find(BookDTO dto, Pageable page) {
        Book filterBook = mapper.map(dto, Book.class);
        Page<Book> result = service.find(filterBook, page);
        List<BookDTO> list = result.getContent()
                .stream()
                .map(entity -> mapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());
        return new PageImpl<BookDTO>(list, page, result.getTotalElements());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteByID(@PathVariable Long id) {
        final Book book = mapper.map(getBookByID(id), Book.class);
        service.delete(book);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    BookDTO update(@PathVariable Long id, @RequestBody BookDTO dto) {
        Book book = mapper.map(getBookByID(id), Book.class);
        book.setAuthor(dto.getAuthor());
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book = service.update(book);
        return mapper.map(book, BookDTO.class);
    }
}
