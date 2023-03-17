package com.paoloprodossimolopes.libraryapidemo.api.resource;

import com.paoloprodossimolopes.libraryapidemo.api.dto.BookDTO;
import com.paoloprodossimolopes.libraryapidemo.api.service.BookService;
import com.paoloprodossimolopes.libraryapidemo.model.entity.Book;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService service;

    BookController(BookService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    BookDTO createBook(@RequestBody BookDTO dto) {
        Book entity = Book.builder()
                .title(dto.getTitle())
                .isbn(dto.getIsbn())
                .author(dto.getAuthor())
                .build();
        entity = service.save(entity);
        return BookDTO.builder()
                .title(entity.getTitle())
                .isbn(entity.getIsbn())
                .author(entity.getAuthor())
                .id(entity.getId())
                .build();
    }
}
