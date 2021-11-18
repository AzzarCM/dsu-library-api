package com.telus.dsu.libraryapi.controller;


import com.telus.dsu.libraryapi.entity.UserType;
import com.telus.dsu.libraryapi.entity.dto.BookDTO;
import com.telus.dsu.libraryapi.entity.dto.UserTypeDTO;
import com.telus.dsu.libraryapi.exception.ResourceNotFoundException;
import com.telus.dsu.libraryapi.service.UserTypeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/userType")
public class UserTypeController {

    @Autowired
    private UserTypeService userTypeService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("")
    public ResponseEntity<?> getAllUserTypes() {
        List<UserType> userTypes = userTypeService.getUserTypes();
        return new ResponseEntity<List<UserTypeDTO>>(convertListToDTO(userTypes), HttpStatus.OK);
    }

    @GetMapping("/{userTypeId}")
    public ResponseEntity<?> getUserTypeById(@PathVariable Integer userTypeId) {
        UserType userTypeFound = userTypeService.getUserTypeById(userTypeId);
        if(userTypeFound == null){
            throw new ResourceNotFoundException("userType not found with id: " + userTypeId);
        }else{
            UserTypeDTO userTypeDTO = convertToDTO(userTypeFound);
            return new ResponseEntity<UserTypeDTO>(userTypeDTO, HttpStatus.OK);
        }
    }

    //TODO Create, Update and Delete

    private List<UserTypeDTO> convertListToDTO(List<UserType> userTypes){
        List<UserTypeDTO> userTypeDTOList = new ArrayList<>();
        for (UserType userType: userTypes) {
            userTypeDTOList.add(convertToDTO(userType));
        }
        return userTypeDTOList;
    }

    private UserTypeDTO convertToDTO(UserType userType) {
        return modelMapper.map(userType, UserTypeDTO.class);
    }
}
