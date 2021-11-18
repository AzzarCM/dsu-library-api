package com.telus.dsu.libraryapi.service;

import com.telus.dsu.libraryapi.entity.UserType;
import com.telus.dsu.libraryapi.repository.UserTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserTypeService {

    @Autowired
    private UserTypeRepository userTypeRepository;

    public List<UserType> getUserTypes() {
        return userTypeRepository.findAll();
    }

    public UserType getUserTypeById(Integer userTypeId) {
        return userTypeRepository.findByUserTypeId(userTypeId);
    }

    //TODO Create, Delete and Update
}
