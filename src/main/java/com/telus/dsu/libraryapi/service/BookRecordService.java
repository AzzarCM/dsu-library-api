package com.telus.dsu.libraryapi.service;

import com.telus.dsu.libraryapi.entity.Book;
import com.telus.dsu.libraryapi.entity.BookRecord;
import com.telus.dsu.libraryapi.entity.User;
import com.telus.dsu.libraryapi.exception.ResourceNotCreatedException;
import com.telus.dsu.libraryapi.exception.ResourceNotFoundException;
import com.telus.dsu.libraryapi.repository.BookRecordRepository;
import com.telus.dsu.libraryapi.repository.BookRepository;
import com.telus.dsu.libraryapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class BookRecordService {

    @Autowired
    private BookRecordRepository bookRecordRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public List<BookRecord> getBookRecords(){
        return bookRecordRepository.findAll();
    }

    public BookRecord getBookRecordByTransaction(Integer transaction){
        return bookRecordRepository.findBookRecordByTransaction(transaction);
    }

    public BookRecord createBookRecord(BookRecord bookRecord, String isbn, Integer userCode){
        Book book = bookRepository.findBookByIsbn(isbn);
        User user = userRepository.findByUserCode(userCode);
        if(book == null ){
            throw new ResourceNotFoundException("Book with ISBN "+isbn+" does not exist");
        }else
        if(user == null){
            throw new ResourceNotFoundException("User with code #"+userCode+"does not exist");
        }else
        if(!book.getIsAvailable()){
            throw new ResourceNotCreatedException("Book is not available");
        }else
        if(user.getBorrowedBooks()>3){
            throw new ResourceNotCreatedException("User has already borrow 3 books");
        }else{
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date currentDate = new Date();
            dateFormat.format(currentDate);

            Calendar c = Calendar.getInstance();
            c.setTime(currentDate);

            c.add(Calendar.DATE, 7);

            Date currentDataPlusSeven = c.getTime();

            bookRecord.setDueDate(currentDataPlusSeven);
            bookRecord.setRenewalCont(1);
            bookRecord.setIsReturned(false);
            bookRecord.setUser(user);
            bookRecord.setBook(book);
            book.setIsAvailable(false);
            user.setBorrowedBooks(user.getBorrowedBooks()+1);
            return bookRecordRepository.save(bookRecord);
        }
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
