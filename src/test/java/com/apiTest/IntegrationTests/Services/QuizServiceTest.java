package com.apiTest.IntegrationTests.Services;

import com.apiTest.Quiz.model.Answer;
import com.apiTest.Quiz.model.Question;
import com.apiTest.Quiz.model.Quiz;
import com.apiTest.Quiz.repository.AnswersRepository;
import com.apiTest.Quiz.repository.QuestionRepository;
import com.apiTest.Quiz.repository.QuizRepository;
import com.apiTest.Quiz.service.QuizService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class QuizServiceTest {

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    QuizService quizService;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    AnswersRepository answersRepository;

    private Quiz quiz = new Quiz("Test Quiz", "Test Quiz Description", "Test category"); // id:1
    private Question question1 = new Question((long) 1, 1, "Test question 1"); // id:1
    private Question question2 = new Question((long) 1, 2, "Test question 2"); // id:2
    private Answer answer1 = new Answer((long) 1, 1, "Answer 1", false); // id:1
    private Answer answer2 = new Answer((long) 1, 2, "Answer 2", false); // id:2
    private Answer answer3 = new Answer((long) 1, 3, "Answer 3", true); // id:3
    private Answer answer4 = new Answer((long) 1, 4, "Answer 4", false); // id:4


    @BeforeEach
    public void setUpDatabase(){
        quizRepository.save(quiz);
        questionRepository.save(question1);
        answersRepository.save(answer1);
        answersRepository.save(answer2);
        answersRepository.save(answer3);
        answersRepository.save(answer4);
    }

    @AfterEach
    public void resetDatabase(){
        quizRepository.truncateTable();
        questionRepository.truncateTable();
        answersRepository.truncateTable();
    }

    @Test
    public void deleteQuizAndAssociationsTest(){
        quizService.deleteQuizAndAssociations(quiz);
        Assertions.assertFalse(quizRepository.existsById((long) 1));
        Assertions.assertFalse(questionRepository.existsById((long) 1));
        Assertions.assertFalse(answersRepository.existsById((long) 1));
        Assertions.assertFalse(answersRepository.existsById((long) 2));
        Assertions.assertFalse(answersRepository.existsById((long) 3));
        Assertions.assertFalse(answersRepository.existsById((long) 4));
    }

}
