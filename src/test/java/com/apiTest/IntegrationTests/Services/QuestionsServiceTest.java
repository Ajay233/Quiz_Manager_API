package com.apiTest.IntegrationTests.Services;

import com.apiTest.Quiz.model.Answer;
import com.apiTest.Quiz.model.Question;
import com.apiTest.Quiz.repository.AnswersRepository;
import com.apiTest.Quiz.repository.QuestionRepository;
import com.apiTest.Quiz.service.QuestionsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
public class QuestionsServiceTest {

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    AnswersRepository answersRepository;

    @Autowired
    QuestionsService questionsService;

    private Question question1 = new Question((long) 1, 1, "Test question 1"); // id:1
    private Answer answer1 = new Answer((long) 1, 1, "Answer 1", false); // id:1
    private Answer answer2 = new Answer((long) 1, 2, "Answer 2", false); // id:2
    private Answer answer3 = new Answer((long) 1, 3, "Answer 3", true); // id:3
    private Answer answer4 = new Answer((long) 1, 4, "Answer 4", false); // id:4


    @BeforeEach
    public void setUpDatabase(){
        questionRepository.save(question1);
        answersRepository.save(answer1);
        answersRepository.save(answer2);
        answersRepository.save(answer3);
        answersRepository.save(answer4);
    }

    @AfterEach
    public void resetDatabase(){
        questionRepository.truncateTable();
        answersRepository.truncateTable();
    }

    @Test
    public void deleteAssociatedAnswersTest() {
        ArrayList<Question> questions = new ArrayList<>();
        questions.add(question1);
        questionsService.DeleteAll(questions);
        Assertions.assertFalse(questionRepository.existsById((long) 1));
        Assertions.assertFalse(answersRepository.existsById((long) 1));
        Assertions.assertFalse(answersRepository.existsById((long) 2));
        Assertions.assertFalse(answersRepository.existsById((long) 3));
        Assertions.assertFalse(answersRepository.existsById((long) 4));
    }

}
