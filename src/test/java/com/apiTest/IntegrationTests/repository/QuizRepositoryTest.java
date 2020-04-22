package com.apiTest.IntegrationTests.repository;

import com.apiTest.Quiz.model.Quiz;
import com.apiTest.Quiz.repository.QuizRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class QuizRepositoryTest {

    @Autowired
    QuizRepository quizRepository;

    private Quiz quiz1;
    private Quiz quiz2;
    private Quiz quiz3;


    @BeforeEach
    public void setUpDatabase(){
        quiz1 = new Quiz("quiz1", "Test of quiz1", "Test");
        quiz2 = new Quiz("quiz2", "Test of quiz2", "NotTest");
        quiz3 = new Quiz("quiz3", "Test of quiz3", "Test");
        quizRepository.save(quiz1);
        quizRepository.save(quiz2);
        quizRepository.save(quiz3);
    }

    @AfterEach
    public void resetDatabase(){
        List<Quiz> quizes = quizRepository.findAll();
        quizes.stream().forEach((quiz) -> quizRepository.delete(quiz));
    }

    @Test
    public void findByNameTest(){
        Assertions.assertEquals(quizRepository.findByName(quiz1.getName()), quiz1);
    }

    @Test
    public void findByCategoryTest(){
        ArrayList<Quiz> quizes = new ArrayList<>();
        quizes.add(quiz1);
        quizes.add(quiz3);

        Assertions.assertEquals(quizRepository.findByCategory("test"), quizes);
    }

}
