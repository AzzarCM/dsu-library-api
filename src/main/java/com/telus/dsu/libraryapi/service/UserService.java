package com.telus.dsu.libraryapi.service;

import com.telus.dsu.libraryapi.entity.User;
import com.telus.dsu.libraryapi.exception.ResourceNotFoundException;
import com.telus.dsu.libraryapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserByCode(Integer userCode){
        return userRepository.findByUserCode(userCode);
    }

    public User createUser(User user){
        return userRepository.save(user);
    }

    public User updateUser(User toUpdateUser, User user){
        toUpdateUser.setFirstName(user.getFirstName());
        toUpdateUser.setLastName(user.getLastName());
        toUpdateUser.setBorrowedBooks(user.getBorrowedBooks());
        toUpdateUser.setEmail(user.getEmail());
        toUpdateUser.setEmail(user.getEmail());
        //TODO Set UserType

        return userRepository.save(toUpdateUser);
    }

    public void deleteUser(Integer userCode){
        User userFound = userRepository.findByUserCode(userCode);
        if(userFound == null){
            throw new ResourceNotFoundException("User not found with User Code: " + userCode);
        }else{
            userRepository.delete(userFound);
        }
    }


}
