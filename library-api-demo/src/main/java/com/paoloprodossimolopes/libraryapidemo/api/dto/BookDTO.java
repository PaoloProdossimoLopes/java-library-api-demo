package com.paoloprodossimolopes.libraryapidemo.api.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private String isbn;
}
