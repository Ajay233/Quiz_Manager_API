package com.apiTest.Quiz.controller;

import com.apiTest.Quiz.model.Quiz;
import com.apiTest.Quiz.model.QuizListItem;
import com.apiTest.Quiz.repository.QuizRepository;
import com.apiTest.Quiz.service.QuizService;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class QuizController {

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    QuizService quizService;

    @RequestMapping(value = "/quiz/create", method = RequestMethod.POST)
    private ResponseEntity<?> createQuiz(@RequestBody Quiz quiz){
        if(quiz.getName() != null && quiz.getDescription() != null && quiz.getCategory() != null) {
            Quiz savedQuiz = quizRepository.save(quiz);
            return new ResponseEntity<Quiz>(savedQuiz, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("MISSING DETAILS", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/quiz/delete", method = RequestMethod.DELETE)
    private ResponseEntity<?> deleteQuiz(@RequestBody Quiz quiz){
        if(quiz.getId() != null && quiz.getName() != null && quiz.getDescription() != null && quiz.getCategory() != null) {
            if(quizRepository.existsById(quiz.getId())){
                quizService.deleteQuizAndAssociations(quiz);
                return ResponseEntity.ok("DELETED");
            } else {
                return new ResponseEntity<String>("NOT FOUND", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<String>("MISSING DETAILS", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/quiz/getAll", method = RequestMethod.GET)
    private  ResponseEntity<?> getAllQuizes(){
        ArrayList<QuizListItem> list = quizService.getAllQuizesOrderedByCategory();
        return new ResponseEntity<ArrayList>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/quiz/findByCategory", method = RequestMethod.GET)
    private ResponseEntity<?> getQuizesByCategory(@RequestParam String category){
        List<Quiz> quizes = quizRepository.findByCategory(category);
        if(quizes.size() != 0){
            return new ResponseEntity<List>(quizes, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("CATEGORY DOES NOT EXIST", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/quiz/findByName", method = RequestMethod.GET)
    private ResponseEntity<?> getQuizByName(@RequestParam String name) throws JwtException {
        if(!quizRepository.findByName(name).isEmpty()){
            return new ResponseEntity<List>(quizRepository.findByName(name), HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/quiz/update", method = RequestMethod.PUT)
    private  ResponseEntity<?> updateQuizDetails(@RequestBody Quiz quiz){
        if(quizRepository.findById(quiz.getId()).get() != null){
            Quiz updatedQuiz = quizRepository.save(quiz);
            return new ResponseEntity<Quiz>(updatedQuiz, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/quiz/updateStatus", method = RequestMethod.PUT)
    private ResponseEntity<?> updateQuizStatus(@RequestBody Quiz quiz){
        if(quizRepository.findById(quiz.getId()).get() != null){
            if(quizService.quizReady(quiz.getId())){
                Quiz updatedQuiz = quizRepository.findById(quiz.getId()).get();
                updatedQuiz.setStatus(quiz.getStatus());
                Quiz response = quizRepository.save(updatedQuiz);
                return new ResponseEntity<Quiz>(response, HttpStatus.OK);
            } else {
                String msg = "A quiz must have a question with at least one correct answer and one wrong" +
                             " answer before the status can be changed to ready";
                return new ResponseEntity<String>(msg, HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<String>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

}
