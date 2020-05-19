package com.apiTest.Quiz.controller;

import com.apiTest.Quiz.model.Question;
import com.apiTest.Quiz.repository.QuestionRepository;
import com.apiTest.Quiz.service.QuestionValidator;
import com.apiTest.Quiz.service.QuestionsService;
import com.apiTest.util.SortingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class QuestionController {

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    QuestionValidator questionValidator;

    @Autowired
    QuestionsService questionsService;

    @Autowired
    SortingUtil sortingUtil;


    @RequestMapping(value = "/question/create", method = RequestMethod.POST)
    private ResponseEntity<?> createQuestions(@RequestBody List<Question> questions){
        if(questionValidator.validateQuestionFields(questions)){
            List<Question> savedQuestions = questionRepository.saveAll(questions);
            return new ResponseEntity<List>(savedQuestions, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("MISSING FIELDS", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/question/findByQuizId", method = RequestMethod.GET)
    private ResponseEntity<?> getQuestionsByQuizId(@RequestParam Long quizId){
        if(questionValidator.quizExists(quizId)) {
            if (!questionRepository.findByQuizId(quizId).isEmpty()) {
                List<Question> questions = questionRepository.findByQuizId(quizId);
                sortingUtil.QuestionSelectSort(questions, questions.size());
                return new ResponseEntity<List>(questions, HttpStatus.OK);
            } else {
                return ResponseEntity.ok("NO QUESTIONS");
            }
        } else {
            return new ResponseEntity<String>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/question/update", method = RequestMethod.PUT)
    private ResponseEntity<?> updateQuestions(@RequestBody List<Question> questions){
        if(questionValidator.validateQuestionsExist(questions)) {
            questions.stream().forEach((question) -> questionRepository.save(question));
            return ResponseEntity.ok("UPDATED");
        } else {
            return new ResponseEntity<String>("INVALID QUESTIONS PROVIDED", HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value = "/question/delete", method = RequestMethod.DELETE)
    private ResponseEntity<?> deleteQuestions(@RequestBody List<Question> questions){
        if(questionValidator.validateQuestionsExist(questions)) {
            questionsService.deleteQuestionsAndAnswers(questions);
            return ResponseEntity.ok("DELETED");
        } else {
            return new ResponseEntity<String>("INVALID QUESTIONS PROVIDED", HttpStatus.BAD_REQUEST);
        }
    }

}
