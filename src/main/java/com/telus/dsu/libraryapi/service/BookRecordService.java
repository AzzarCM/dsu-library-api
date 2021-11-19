package com.telus.dsu.libraryapi.service;

import com.telus.dsu.libraryapi.entity.BookRecord;
import com.telus.dsu.libraryapi.exception.ResourceNotFoundException;
import com.telus.dsu.libraryapi.repository.BookRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookRecordService {
    @Autowired
    private BookRecordRepository bookRecordRepository;

    public List<BookRecord> getBookRecords(){
        return bookRecordRepository.findAll();
    }

    public BookRecord getBookRecordById(Integer transaction){
        return bookRecordRepository.findBookRecordByTransaction(transaction);
    }

    public BookRecord createBookRecord(BookRecord bookRecord){
        return bookRecordRepository.save(bookRecord);
    }

    public BookRecord updateBookRecord(BookRecord bookRecordToUpdate, BookRecord bookRecord){
        bookRecordToUpdate.setTookOn(bookRecord.getTookOn());
        bookRecordToUpdate.setReturnOn(bookRecord.getReturnOn());
        bookRecordToUpdate.setDueDate(bookRecord.getDueDate());
        bookRecordToUpdate.setIsReturned(bookRecord.getIsReturned());
        bookRecordToUpdate.setRenewalCont(bookRecord.getRenewalCont());
        bookRecordToUpdate.setDelayPenalization(bookRecord.getDelayPenalization());
        //TODO BookID
        //TODO UserId

        return bookRecordRepository.save(bookRecordToUpdate);
    }

    public void deleteBookRecord(Integer transaction){
        BookRecord bookRecordFound = bookRecordRepository.findBookRecordByTransaction(transaction);
        if(bookRecordFound == null){
            throw new ResourceNotFoundException("BookRecord not found with BookRecordId: " + transaction);
        }else{
            bookRecordRepository.delete(bookRecordFound);
        }
    }

    //TODO Individual update methods?
}
