package com.telus.dsu.libraryapi.service;

import com.telus.dsu.libraryapi.entity.User;
import com.telus.dsu.libraryapi.entity.UserType;
import com.telus.dsu.libraryapi.exception.ResourceNotCreatedException;
import com.telus.dsu.libraryapi.exception.ResourceNotFoundException;
import com.telus.dsu.libraryapi.repository.UserRepository;
import com.telus.dsu.libraryapi.repository.UserTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTypeRepository userTypeRepository;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserByCode(Integer userCode){
        return userRepository.findByUserCode(userCode);
    }

    public User createUser(User user){
        UserType userType = userTypeRepository.findByUserTypeId(user.getUserType().getUserTypeId());
        if(userType == null){
            throw new ResourceNotFoundException("User type with id: "+ user.getUserType().getUserTypeId() + " does not exist");
        }
        try{
            user.setUserType(userType);
            return userRepository.save(user);
        }catch (Exception e){
            throw new ResourceNotCreatedException("User with ID: "+user.getUserCode()+ " already exist");
        }

    }

    public User updateUser(User toUpdateUser, User user){
        toUpdateUser.setFirstName(user.getFirstName());
        toUpdateUser.setLastName(user.getLastName());
        toUpdateUser.setBorrowedBooks(user.getBorrowedBooks());
        toUpdateUser.setEmail(user.getEmail());
        toUpdateUser.setPhone(user.getPhone());
        toUpdateUser.setUserType(user.getUserType());

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
