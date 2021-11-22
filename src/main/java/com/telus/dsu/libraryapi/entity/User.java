package com.telus.dsu.libraryapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    @Column(name = "USER_CODE", unique = true, updatable = false)
    @NotNull(message = "the UserCode is required")
    private Integer userCode;
    private String firstName;
    private String lastName;
    private Integer borrowedBooks;
    private String email;
    private String phone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USERTYPE_ID")
    private UserType userType;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<BookRecord> bookRecordList;

    public User(Integer userId) {
        this.userId = userId;
    }

    public User() {

    }
}
