package com.telus.dsu.libraryapi;

import com.telus.dsu.libraryapi.entity.User;
import com.telus.dsu.libraryapi.entity.UserType;
import com.telus.dsu.libraryapi.service.UserService;
import com.telus.dsu.libraryapi.service.UserTypeService;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Disabled;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = LibraryApiApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
class LibraryApiApplicationTests {

    @Autowired
    private UserTypeService userTypeService;

    @Autowired
    private UserService userService;

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
    void contextLoads() {
    }

}
