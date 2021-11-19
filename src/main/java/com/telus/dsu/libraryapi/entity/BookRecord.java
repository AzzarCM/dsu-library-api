package com.telus.dsu.libraryapi.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
public class BookRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer recordId;
    private Integer transaction;
    private Date tookOn;
    private Date returnOn;
    private Date dueDate;
    private Boolean isReturned;
    private Integer renewalCont;
    private Double delayPenalization;
    //TODO Book Relation
    //TODO User Relation
}
