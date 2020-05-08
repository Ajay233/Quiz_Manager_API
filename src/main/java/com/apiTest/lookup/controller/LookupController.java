package com.apiTest.lookup.controller;

import com.apiTest.lookup.model.Lookup;
import com.apiTest.lookup.repository.LookupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LookupController {

    @Autowired
    LookupRepository lookupRepository;

    @RequestMapping(value = "/lookup/quizCategories", method = RequestMethod.GET)
    public ResponseEntity<?> getListOfQuizCategories(){
        if(!lookupRepository.findByType("Quiz Category").isEmpty()){
            List<Lookup> results = lookupRepository.findByType("Quiz Category");
            ArrayList<String> categories = new ArrayList<>();
            results.stream().forEach(obj -> categories.add(obj.getName()));
            return new ResponseEntity<List>(categories, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("NO VALUES FOR THAT TYPE", HttpStatus.NOT_FOUND);
        }
    }

}
