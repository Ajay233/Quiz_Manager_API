package com.apiTest.Quiz.controller;

import com.apiTest.Quiz.model.Quiz;
import com.apiTest.Quiz.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @RequestMapping(value = "/quiz/delete", method = RequestMethod.DELETE)
    private ResponseEntity<?> deleteQuiz(@RequestBody Quiz quiz){
        if(quiz.getName() != null && quiz.getDescription() != null && quiz.getCategory() != null) {
            quizRepository.delete(quiz);
            return ResponseEntity.ok("DELETED");
        } else {
            return new ResponseEntity<String>("MISSING DETAILS", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/quiz/getAll", method = RequestMethod.GET)
    private  ResponseEntity<?> getAllQuizes(){
        return new ResponseEntity<List>(quizRepository.findAll(), HttpStatus.OK);
    }

    @RequestMapping(value = "/quiz/findByCategory", method = RequestMethod.GET)
    private ResponseEntity<?> getQuizesByCategory(@RequestBody String category){
        List<Quiz> quizes = quizRepository.findByCategory(category);
        if(quizes.size() != 0){
            return new ResponseEntity<List>(quizes, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("CATEGORY DOES NOT EXIST", HttpStatus.BAD_REQUEST);
        }
    }

}