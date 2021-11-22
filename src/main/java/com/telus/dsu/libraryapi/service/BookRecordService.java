package com.telus.dsu.libraryapi.service;

import com.telus.dsu.libraryapi.entity.Book;
import com.telus.dsu.libraryapi.entity.BookRecord;
import com.telus.dsu.libraryapi.entity.User;
import com.telus.dsu.libraryapi.exception.ResourceNotCreatedException;
import com.telus.dsu.libraryapi.exception.ResourceNotFoundException;
import com.telus.dsu.libraryapi.repository.BookRecordRepository;
import com.telus.dsu.libraryapi.repository.BookRepository;
import com.telus.dsu.libraryapi.repository.UserRepository;
import com.telus.dsu.libraryapi.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BookRecordService {

    @Autowired
    private BookRecordRepository bookRecordRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public List<BookRecord> getBookRecords() {
        return bookRecordRepository.findAll();
    }

    public BookRecord getBookRecordByTransaction(Integer transaction) {
        return bookRecordRepository.findBookRecordByTransaction(transaction);
    }

    public BookRecord createBookRecord(BookRecord bookRecord, String isbn, Integer userCode) {
        Book book = bookRepository.findBookByIsbn(isbn);
        User user = userRepository.findByUserCode(userCode);
        if (book == null) {
            throw new ResourceNotFoundException("Book with ISBN " + isbn + " does not exist");
        } else if (user == null) {
            throw new ResourceNotFoundException("User with code #" + userCode + " does not exist");
        } else if (!book.getIsAvailable()) {
            throw new ResourceNotCreatedException("Book is not available");
        } else if (user.getBorrowedBooks() >= Constants.MAX_RENEWALS) {
            throw new ResourceNotCreatedException("User has already borrow 3 books");
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date currentDate = new Date();
        dateFormat.format(currentDate);

        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);

        c.add(Calendar.DATE, 7);

        Date currentDataPlusSeven = c.getTime();

        bookRecord.setDueDate(currentDataPlusSeven);
        bookRecord.setRenewalCont(0);
        bookRecord.setIsReturned(false);
        bookRecord.setUser(user);
        bookRecord.setBook(book);
        book.setIsAvailable(false);
        user.setBorrowedBooks(user.getBorrowedBooks() + 1);
        try {
            return bookRecordRepository.save(bookRecord);
        } catch (Exception e) {
            throw new ResourceNotCreatedException("Invoice #" + bookRecord.getTransaction() + " already exists");
        }
    }

    public BookRecord returnBook(Integer invoice, String isbn, Integer userCode) {
        BookRecord bookRecord = bookRecordRepository.findBookRecordByTransaction(invoice);
        Book book = bookRepository.findBookByIsbn(isbn);
        User user = userRepository.findByUserCode(userCode);

        if (bookRecord == null) {
            throw new ResourceNotCreatedException("The invoice #" + invoice + " does not exist");
        } else if (book == null) {
            throw new ResourceNotFoundException("The book with ISBN: " + isbn + " does not exists");
        } else if (user == null) {
            throw new ResourceNotFoundException("The user with id: " + userCode + " does not exists");
        }
        if (bookRecord.getIsReturned()) {
            throw new ResourceNotCreatedException("The book: " + book.getTitle() + " was already returned");
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar c = Calendar.getInstance();

        user.setBorrowedBooks(user.getBorrowedBooks() - 1);
        book.setIsAvailable(true);
        bookRecord.setIsReturned(true);
        bookRecord.setReturnOn(new Date());
        Date tookOn = bookRecord.getTookOn();
        Date dueDate = bookRecord.getDueDate();
        Date returnOn = new Date();
        dateFormat.format(returnOn);
        c.setTime(returnOn);
        c.add(Calendar.DATE, 10);
        Date realReturn = c.getTime();
        Long difference = getDifferenceBetweenDays(realReturn, tookOn);
        if (difference > 7) {
            Long diffForPenalization = getDifferenceBetweenDays(realReturn, dueDate);
            bookRecord.setDelayPenalization((diffForPenalization) * 0.20);
        }
        return bookRecordRepository.save(bookRecord);
    }

    public BookRecord renewBook(Integer invoice, String isbn, Integer userCode){
        BookRecord bookRecord = bookRecordRepository.findBookRecordByTransaction(invoice);
        Book book = bookRepository.findBookByIsbn(isbn);
        User user = userRepository.findByUserCode(userCode);
        if (bookRecord == null) {
            throw new ResourceNotCreatedException("The invoice #" + invoice + " does not exist");
        } else if (book == null) {
            throw new ResourceNotFoundException("The book with ISBN: " + isbn + " does not exists");
        } else if (user == null) {
            throw new ResourceNotFoundException("The user with id: " + userCode + " does not exists");
        } else if (bookRecord.getIsReturned()) {
            throw new ResourceNotCreatedException("The book: " + book.getTitle() + " was already returned");
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar c = Calendar.getInstance();
        Date dueDateUpdated = new Date();
        dateFormat.format(dueDateUpdated);

        c.setTime(dueDateUpdated);
        c.add(Calendar.DATE,10);

        Date tookOn = bookRecord.getTookOn();
        Date dueDate = bookRecord.getDueDate();
        Date returnOn = new Date();
        dateFormat.format(returnOn);
        c.setTime(returnOn);
        c.add(Calendar.DATE, 10);
        Date realReturn2 = c.getTime();
        Long differenceBefore = getDifferenceBetweenDays(realReturn2,tookOn);
        if(differenceBefore > 7){
            Long penalization = getDifferenceBetweenDays(realReturn2,dueDate);
            book.setIsAvailable(true);
            user.setBorrowedBooks(user.getBorrowedBooks()-1);
            bookRecord.setIsReturned(true);
            bookRecord.setReturnOn(new Date());
            bookRecord.setDelayPenalization((penalization)*0.20);

            return bookRecordRepository.save(bookRecord);
        }

        bookRecord.setTookOn(new Date());
        bookRecord.setDueDate(dueDateUpdated);
        bookRecord.setRenewalCont(bookRecord.getRenewalCont()+1);
        Date realReturn = new Date();


        if(bookRecord.getRenewalCont() >= 3){
            Long difference = getDifferenceBetweenDays(bookRecord.getDueDate(), realReturn);
            bookRecord.setDelayPenalization((difference)*0.20);
            bookRecord.setReturnOn(returnOn);
            bookRecord.setIsReturned(true);
            book.setIsAvailable(true);
            user.setBorrowedBooks(user.getBorrowedBooks()-1);
        }

        return bookRecordRepository.save(bookRecord);
    }

    public BookRecord updateBookRecord(BookRecord bookRecordToUpdate, BookRecord bookRecord) {
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

    public void deleteBookRecord(Integer transaction) {
        BookRecord bookRecordFound = bookRecordRepository.findBookRecordByTransaction(transaction);
        if (bookRecordFound == null) {
            throw new ResourceNotFoundException("BookRecord not found with BookRecordId: " + transaction);
        } else {
            bookRecordRepository.delete(bookRecordFound);
        }
    }

    public Long getDifferenceBetweenDays(Date date1, Date date2) {
        Long days = date1.getTime() - date2.getTime();
        return TimeUnit.DAYS.convert(days, TimeUnit.MILLISECONDS);
    }

    //TODO Individual update methods?
}
