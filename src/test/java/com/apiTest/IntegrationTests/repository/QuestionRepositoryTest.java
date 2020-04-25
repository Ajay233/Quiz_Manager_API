package com.apiTest.IntegrationTests.repository;

import com.apiTest.Quiz.model.Question;
import com.apiTest.Quiz.repository.QuestionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class QuestionRepositoryTest {

    @Autowired
    QuestionRepository questionRepository;

    private Question question1;
    private Question question2;
    private Question question3;

    @BeforeEach
    public void setUpDatabase(){
        question1 = new Question((long) 1, 1, "test question number 1 for quiz1");
        question2 = new Question((long) 2, 1, "test question number 1 for quiz2");
        question3 = new Question((long) 1, 2, "test question number 2 for quiz1");
        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);
    }

    @AfterEach
    public void resetDatabase(){
        questionRepository.truncateTable();
    }

    @Test
    public void findByQuizIdTest(){
        Assertions.assertEquals(questionRepository.findByQuizId((long) 1).size(), 2);
        Assertions.assertEquals(questionRepository.findByQuizId((long) 1).get(0).getDescription(), question1.getDescription());
        Assertions.assertEquals(questionRepository.findByQuizId((long) 1).get(1).getDescription(), question3.getDescription());
    }

}
