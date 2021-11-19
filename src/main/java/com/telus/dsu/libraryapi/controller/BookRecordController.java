package com.telus.dsu.libraryapi.controller;

import com.telus.dsu.libraryapi.entity.BookRecord;
import com.telus.dsu.libraryapi.entity.User;
import com.telus.dsu.libraryapi.entity.dto.BookRecordDTO;
import com.telus.dsu.libraryapi.entity.dto.UserDTO;
import com.telus.dsu.libraryapi.exception.ResourceNotCreatedException;
import com.telus.dsu.libraryapi.exception.ResourceNotFoundException;
import com.telus.dsu.libraryapi.service.BookRecordService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/bookRecord")
public class BookRecordController {
    @Autowired
    private BookRecordService bookRecordService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("")
    public ResponseEntity<?> getAllBookRecords(){
        List<BookRecord> bookRecords = bookRecordService.getBookRecords();
        return new ResponseEntity<List<BookRecordDTO>>(convertListToDTO(bookRecords), HttpStatus.OK);
    }

    @GetMapping("/{transaction}")
    public ResponseEntity<?> getBookRecordByTransaction(@PathVariable Integer transaction){
        BookRecord bookRecordFound = bookRecordService.getBookRecordById(transaction);
        if(bookRecordFound == null){
            throw new ResourceNotFoundException("BookRecord not found with Transaction: " + transaction);
        }else{
            BookRecordDTO bookRecordDTO = convertToDTO(bookRecordFound);
            return new ResponseEntity<BookRecordDTO>(bookRecordDTO, HttpStatus.OK);
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createNewBookRecord(@Valid @RequestBody BookRecordDTO bookRecordDTO, BindingResult result){
        if(result.hasErrors()){
            throw new ResourceNotCreatedException("BookRecord was not created");
        }else{
            BookRecord bookRecord = bookRecordService.createBookRecord(convertToEntity(bookRecordDTO));
            return new ResponseEntity<BookRecordDTO>(convertToDTO(bookRecord), HttpStatus.OK);
        }
    }

    //TODO PUT DELETE

    private List<BookRecordDTO> convertListToDTO(List<BookRecord> bookRecords){
        List<BookRecordDTO> bookRecordDTOList = new ArrayList<>();
        for(BookRecord bookRecord : bookRecords){
            bookRecordDTOList.add(convertToDTO(bookRecord));
        }
        return bookRecordDTOList;
    }

    private BookRecordDTO convertToDTO(BookRecord bookRecord){
        return modelMapper.map(bookRecord, BookRecordDTO.class);
    }

    private BookRecord convertToEntity(BookRecordDTO bookRecordDTO){
        return modelMapper.map(bookRecordDTO, BookRecord.class);
    }
}
