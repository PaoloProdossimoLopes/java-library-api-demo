package com.paoloprodossimolopes.libraryapidemo;

import com.paoloprodossimolopes.libraryapidemo.api.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LibraryApiDemoApplication {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

//	@Bean
//	public BookService bookService() {}


	public static void main(String[] args) {
		SpringApplication.run(LibraryApiDemoApplication.class, args);
	}
}
