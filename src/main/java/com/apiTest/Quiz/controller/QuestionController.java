package com.apiTest.Quiz.controller;

import com.apiTest.Quiz.model.Question;
import com.apiTest.Quiz.repository.QuestionRepository;
import com.apiTest.Quiz.service.QuestionValidator;
import com.apiTest.Quiz.service.QuestionsService;
import com.apiTest.util.AmazonClient;
import com.apiTest.util.SortingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    AmazonClient amazonClient;


    @RequestMapping(value = "/question/create", method = RequestMethod.POST)
    private ResponseEntity<?> createQuestions(
            @RequestParam(value = "quizId") Long quizId,
            @RequestParam(value = "questionNumber") int questionNumber,
            @RequestParam(value = "description") String description,
            @RequestParam(value = "file", required = false) MultipartFile file
    ){
        Question question = new Question(quizId, questionNumber, description);
        if(file != null){
            question.setImgUrl(amazonClient.uploadFile(file));
        } else {
            question.setImgUrl(null);
        }
        if(questionValidator.validateQuestion(question)) {
            Question savedQuestion = questionRepository.save(question);
            return new ResponseEntity<Question>(savedQuestion, HttpStatus.OK);
        }
        return new ResponseEntity<String>("Error creating question - Please check the fields and try again",
                HttpStatus.BAD_REQUEST
        );
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
    private ResponseEntity<?> updateQuestions(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "questionNumber") int questionNumber,
            @RequestParam(value = "description") String description,
            @RequestParam(value = "file", required = false) MultipartFile file
    ){
        if(questionRepository.existsById(id)){
            Question question = questionRepository.findById(id).get();
            question.setQuestionNumber(questionNumber);
            question.setDescription(description);
            if(file != null){
                question.setImgUrl(amazonClient.uploadFile(file));
            }
            Question updatedQuestion = questionRepository.save(question);
            return new ResponseEntity<Question>(updatedQuestion, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("Question not found", HttpStatus.BAD_REQUEST);
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

    @RequestMapping(value = "question/deleteImage", method = RequestMethod.DELETE)
    private ResponseEntity<?> deleteQuestionImage(@RequestParam Long questionId, @RequestParam String url){
        amazonClient.deleteFileFromS3(url); // might need to be in it's own thread
        Question question = questionRepository.findById(questionId).get();
        question.setImgUrl(null);
        Question updatedQuestion = questionRepository.save(question);
        return new ResponseEntity<Question>(updatedQuestion, HttpStatus.OK);
    }

}
