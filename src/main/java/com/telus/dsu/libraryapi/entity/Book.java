package com.telus.dsu.libraryapi.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookId;
    private String title;
    @NotNull(message = "the ISBN is required")
    private String isbn;
    private String author;
    private String category;
    private Boolean isAvailable;

    @OneToMany(mappedBy = "book")
    private List<BookRecord> bookRecordList;

    public Book(Integer bookId) {
        this.bookId = bookId;
    }

    public Book() {
    }
}
