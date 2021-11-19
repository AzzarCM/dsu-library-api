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
public class UserType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USERTYPE_ID")
    private Integer userTypeId;

    @NotNull(message = "the UserType is required")
    private String userType;

    public UserType(Integer userTypeId) {
        this.userTypeId = userTypeId;
    }

    public UserType() {

    }
}
