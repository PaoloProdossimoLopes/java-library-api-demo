package com.paoloprodossimolopes.libraryapidemo.api.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BookDTO {
    private Long id;

    @NotEmpty
    @NonNull
    private String title;

    @NotEmpty
    @NonNull
    private String author;

    @NotEmpty
    @NonNull
    private String isbn;
}
