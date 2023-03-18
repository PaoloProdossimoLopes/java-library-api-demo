package com.paoloprodossimolopes.libraryapidemo.api.model.repository;

import com.paoloprodossimolopes.libraryapidemo.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

}
