package com.apiTest.Quiz.controller;

import com.apiTest.Quiz.model.Question;
import com.apiTest.Quiz.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuestionController {

    @Autowired
    QuestionRepository questionRepository;

    @RequestMapping(value = "/question/create", method = RequestMethod.POST)
    private ResponseEntity<?> createQuestion(@RequestBody Question question){
        if(question.getDescription() != null){
            questionRepository.save(question);
            return ResponseEntity.ok("CREATED");
        } else {
            return new ResponseEntity<String>("MISSING FIELDS", HttpStatus.BAD_REQUEST);
        }
    }

}
