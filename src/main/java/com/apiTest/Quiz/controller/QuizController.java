package com.apiTest.Quiz.controller;

import com.apiTest.Quiz.model.Quiz;
import com.apiTest.Quiz.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuizController {

    @Autowired
    QuizRepository quizRepository;

    @RequestMapping(value = "/quiz/create", method = RequestMethod.POST)
    private ResponseEntity<?> createQuiz(@RequestBody Quiz quiz){
        if(quiz.getName() != null && quiz.getDescription() != null && quiz.getCategory() != null) {
            quizRepository.save(quiz);
            return ResponseEntity.ok("CREATED");
        } else {
            return new ResponseEntity<String>("MISSING DETAILS", HttpStatus.BAD_REQUEST);
        }
    }

}
