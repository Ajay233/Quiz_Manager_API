package com.apiTest.IntegrationTests.Services;

import com.apiTest.Quiz.model.Question;
import com.apiTest.Quiz.repository.QuestionRepository;
import com.apiTest.Quiz.service.QuestionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class QuestionValidatorTest {

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    QuestionValidator questionValidator;

    private Question question1;
    private Question question2;
    private Question question3;
    private Question question4;
    private Question question5;
    private Question question6;

    @BeforeEach
    public void setUpDatabase(){
        question1 = new Question((long) 1, 1, "test question number 1 for quiz1");
        question2 = new Question((long) 1, 2, "test question number 2 for quiz1");
        question3 = new Question((long) 2, 1, "test question number 1 for quiz2");
        question4 = new Question((long) 3, 1, "test question number 1 for quiz3");
        question5 = new Question((long) 1, 3, "test question number 3 for quiz1");
        question6 = new Question((long) 3, 2, "test question number 2 for quiz3");
        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);
        questionRepository.save(question4);
        questionRepository.save(question5);
        questionRepository.save(question6);
        question1.setId((long) 1);
        question2.setId((long) 2);
        question3.setId((long) 3);
        question4.setId((long) 4);
        question5.setId((long) 5);
        question6.setId((long) 6);
    }

    @AfterEach
    public void resetDatabase(){
        questionRepository.truncateTable();
    }

    @Test
    public void validateQuestionsTest() {

        // Needs improving to use a try catch block to catch the 'NoSuchElementException: No value present'
        // error when there is no corresponding record in the DB

//        Question dudQuestion1 = new Question((long) 10, 1, "Dud question");
//        Question dudQuestion2 = new Question((long) 10, 2, "Dud question2");
//        dudQuestion1.setId((long) 7);
//        dudQuestion2.setId((long) 8);
//        List<Question> invalidQuestions;
//        invalidQuestions = questionRepository.findByQuizId((long) 3);
//        invalidQuestions.add(dudQuestion1);
//        invalidQuestions.add(dudQuestion2);

        List<Question> questions;
        questions = questionValidator.validateQuestion(questionRepository.findByQuizId((long) 1));

//        List<Question> filteredQuestions;
//        filteredQuestions = questionValidator.validateQuestion(invalidQuestions);

        Assertions.assertTrue(questions.isEmpty());
//        Assertions.assertEquals(filteredQuestions.size(), 2);

    }

}
