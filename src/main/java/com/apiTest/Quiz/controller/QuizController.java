package com.apiTest.Quiz.controller;

import com.apiTest.Quiz.model.Quiz;
import com.apiTest.Quiz.model.QuizDownload;
import com.apiTest.Quiz.model.QuizListItem;
import com.apiTest.Quiz.repository.QuizRepository;
import com.apiTest.Quiz.service.QuizService;
import com.apiTest.util.AmazonClient;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
public class QuizController {

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    QuizService quizService;

    @Autowired
    AmazonClient amazonClient;

    @RequestMapping(value = "/quiz/create", method = RequestMethod.POST)
    private ResponseEntity<?> createQuiz(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "description") String description,
            @RequestParam(value = "category") String category,
            @RequestParam(value = "author") String author,
            @RequestParam(value = "authorId") Long authorId,
            @RequestParam(value = "file", required = false) MultipartFile file
    ){
        if(name != null && description != null && category != null) {
            Quiz quiz = new Quiz(name, description, category, author, authorId);
            if(file != null){
                quiz.setImgUrl(amazonClient.uploadFile(file));
            }
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
    private  ResponseEntity<?> updateQuizDetails(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "description") String description,
            @RequestParam(value = "category") String category,
            @RequestParam(value = "author") String author,
            @RequestParam(value = "authorId") Long authorId,
            @RequestParam(value = "file", required = false) MultipartFile file
    ){
        if(quizRepository.existsById(id)){
            Quiz quizToUpdate = quizRepository.findById(id).get();
            quizToUpdate.setName(name);
            quizToUpdate.setDescription(description);
            quizToUpdate.setCategory(category);
            quizToUpdate.setAuthor(author);
            quizToUpdate.setAuthorId(authorId);
            if(file != null){
                quizToUpdate.setImgUrl(amazonClient.uploadFile(file));
            }
            Quiz updatedQuiz = quizRepository.save(quizToUpdate);
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

    @RequestMapping(value = "/quiz/deleteImage", method = RequestMethod.DELETE)
    private ResponseEntity<?> deleteQuizImg(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "imgUrl") String imgUrl
    ){
        amazonClient.deleteFileFromS3(imgUrl);
        Quiz quiz = quizRepository.findById(id).get();
        quiz.setImgUrl(null);
        Quiz updatedQuiz = quizRepository.save(quiz);
        return new ResponseEntity<Quiz>(updatedQuiz, HttpStatus.OK);
    }

    @RequestMapping(value = "/quiz/download", method = RequestMethod.GET)
    private ResponseEntity<?> quizDownload(@RequestParam Long id){
        QuizDownload quizDownload = quizService.quizDownloadData(id);
        return new ResponseEntity<QuizDownload>(quizDownload, HttpStatus.OK);
    }

}
