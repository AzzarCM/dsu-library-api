package com.telus.dsu.libraryapi;
import static org.mockito.Mockito.mock;

import com.telus.dsu.libraryapi.entity.BookRecord;
import com.telus.dsu.libraryapi.exception.ResourceNotCreatedException;
import com.telus.dsu.libraryapi.service.BookRecordService;
import org.apache.logging.log4j.core.util.Assert;
import com.telus.dsu.libraryapi.controller.BookController;
import com.telus.dsu.libraryapi.entity.dto.BookDTO;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import com.telus.dsu.libraryapi.entity.Book;
import com.telus.dsu.libraryapi.entity.User;
import com.telus.dsu.libraryapi.entity.UserType;
import com.telus.dsu.libraryapi.repository.BookRepository;
import com.telus.dsu.libraryapi.service.UserService;
import com.telus.dsu.libraryapi.service.UserTypeService;
import org.hamcrest.MatcherAssert;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.omg.CORBA.portable.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = LibraryApiApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
class LibraryApiApplicationTests {

    @Autowired
    private UserTypeService userTypeService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookController bookController;

    @Autowired
    private BookRecordService bookRecordService;

    @Test
    void createUser(){
        UserType userType = userTypeService.getUserTypeById(1);
        User user = new User();
        user.setUserType(userType);
        user.setUserCode(21356);
        user.setFirstName("Camila");
        user.setLastName("Celeste");
        user.setEmail("camiy@gmail.com");
        user.setPhone("2277-7777");
        user.setBorrowedBooks(0);
        userService.createUser(user);
        User getUser = userService.getUserByCode(21356);
        MatcherAssert.assertThat(getUser.getFirstName(),equalTo("Camila"));
    }

    @Test
    void getBookByISBN(){
        //with
        BookRepository bookRepositoryMOCK = mock(BookRepository.class);
        Book book = new Book();
        book.setIsbn("4050");
        book.setTitle("Lord of the rings");
        book.setIsAvailable(true);
        book.setCategory("Adventure");
        book.setAuthor("Brad Pitt");
        bookRepository.save(book);

        //when
        Mockito.when(bookRepositoryMOCK.findBookByIsbn("4050")).thenReturn(book);
        ResponseEntity<?> resultBookDTO = bookController.getBookByIsbn("4050");

        //then
        Assert.isNonEmpty(resultBookDTO);
    }

    @Test
    void createBookRecord(){
        ResourceNotCreatedException thrown = Assertions.assertThrows(ResourceNotCreatedException.class, () ->{
            Book book = bookRepository.findBookByIsbn("3399"); //getting data from data.sql
            User user = userService.getUserByCode(86917); //getting data from data.sql

            BookRecord bookRecord = new BookRecord();
            bookRecord.setBook(book);
            bookRecord.setUser(user);
            bookRecord.setTransaction(12040);
            bookRecordService.createBookRecord(bookRecord,"3399",86917);

        });
        Assertions.assertEquals("Book is not available", thrown.getMessage());
    }

    @Test
    void contextLoads() {

    }

}
