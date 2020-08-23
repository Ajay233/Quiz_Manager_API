package com.apiTest.IntegrationTests.repository;

import com.apiTest.Quiz.model.Answer;
import com.apiTest.Quiz.repository.AnswersRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AnswerRepositoryTest {

    @Autowired
    AnswersRepository answersRepository;


    private Answer answer1;
    private Answer answer2;
    private Answer answer3;
    private Answer answer4;

    @BeforeEach
    public void setupDatabase(){
        answer1 = new Answer((long) 1, "A", "Answer1 for questionId 1", false);
        answer2 = new Answer((long) 1, "B", "Answer2 for questionId 1", true);
        answer3 = new Answer((long) 1, "C", "Answer3 for questionId 1", false);
        answer4 = new Answer((long) 2, "A", "Answer1 for questionId 2", false);
        answersRepository.save(answer1);
        answersRepository.save(answer2);
        answersRepository.save(answer3);
        answersRepository.save(answer4);
    }

    @AfterEach
    public void resetDatabase(){
        answersRepository.truncateTable();
    }

    @Test
    public void findByQuestionIdTest(){
        Assertions.assertEquals(answersRepository.findByQuestionId((long) 1).size(), 3);
        Assertions.assertEquals(answersRepository.findByQuestionId((long) 1).get(0).getDescription(), answer1.getDescription());
        Assertions.assertEquals(answersRepository.findByQuestionId((long) 1).get(1).getDescription(), answer2.getDescription());
        Assertions.assertEquals(answersRepository.findByQuestionId((long) 1).get(2).getDescription(), answer3.getDescription());
    }

}
