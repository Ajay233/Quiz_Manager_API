package com.apiTest.Quiz.controller;

import com.apiTest.Quiz.model.Answer;
import com.apiTest.Quiz.repository.AnswersRepository;
import com.apiTest.Quiz.repository.QuestionRepository;
import com.apiTest.Quiz.service.AnswerValidator;
import com.apiTest.util.SortingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AnswerController {

    @Autowired
    AnswersRepository answersRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    AnswerValidator answerValidator;

    @Autowired
    SortingUtil sortingUtil;

    @RequestMapping(value = "/answer/create", method = RequestMethod.POST)
    private ResponseEntity<?> createAnswer(@RequestBody List<Answer> answers){
        if(answerValidator.validateAnswer(answers)){
//            answers.stream().forEach((answer) -> answersRepository.save(answer));
            List<Answer> capitalisedAnswers = answerValidator.capitalise(answers);
            List<Answer> savedAnswers = answersRepository.saveAll(capitalisedAnswers);
            return new ResponseEntity<List>(savedAnswers, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("MISSING FIELDS", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/answer/findByQuestionId", method = RequestMethod.GET)
    private ResponseEntity<?> getAnswerByQuestionId(@RequestParam Long questionId){
        if(questionRepository.existsById(questionId)){
            List<Answer> answers = answersRepository.findByQuestionId(questionId);
            sortingUtil.AnswerSelectSort(answers, answers.size());
            return new ResponseEntity<List>(answers, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/answer/update", method = RequestMethod.PUT)
    private ResponseEntity<?> updateAnswers(@RequestBody List<Answer> answers){
        if(!answerValidator.answersExist(answers)){
            return new ResponseEntity<String>("NOT FOUND", HttpStatus.NOT_FOUND);
        } else if(!answerValidator.validateAnswer(answers)){
            return new ResponseEntity<String>("INVALID FIELD", HttpStatus.BAD_REQUEST);
        } else {
            List<Answer> capitalisedAnswers = answerValidator.capitalise(answers);
            List<Answer> savedAnswers = answersRepository.saveAll(capitalisedAnswers);
            return new ResponseEntity<List>(savedAnswers, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/answer/delete", method = RequestMethod.DELETE)
    private ResponseEntity<?> deleteAnswers(@RequestBody List<Answer> answers){
        if(!answerValidator.answersExist(answers)){
            return new ResponseEntity<String>("NOT FOUND", HttpStatus.NOT_FOUND);
        } else if(!answerValidator.validateAnswer(answers)){
            return new ResponseEntity<String>("INVALID FIELD", HttpStatus.BAD_REQUEST);
        } else {
            answers.stream().forEach((answer) -> answersRepository.delete(answer));
            return ResponseEntity.ok("DELETED");
        }
    }

}
