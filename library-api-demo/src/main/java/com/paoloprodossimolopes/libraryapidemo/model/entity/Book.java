package com.paoloprodossimolopes.libraryapidemo.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Book {

    @Id
    @Column //@Column(name = "name of table")
    @GeneratedValue(strategy = GenerationType.IDENTITY) //diz que esse campo é altu incremento (nesse caso feito pela base de dados)
    private Long id;

    @Column //Non nescessary
    private String title;

    @Column //Non nescessary
    private String author;

    @Column //Non nescessary
    private String isbn;
}
