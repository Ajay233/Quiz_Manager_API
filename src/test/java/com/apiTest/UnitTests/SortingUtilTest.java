package com.apiTest.UnitTests;

import com.apiTest.Quiz.model.Question;
import com.apiTest.util.SortingUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
public class SortingUtilTest {

    @Autowired
    SortingUtil sortingUtil;

    private ArrayList<Question> questions = new ArrayList<>();
    private Question question = new Question((long) 1, 12, "Question number 12");
    private Question question2 = new Question((long) 1, 6, "Question number 6");
    private Question question3 = new Question((long) 1, 8, "Question number 8");
    private Question question4 = new Question((long) 1, 22, "Question number 22");
    private Question question5 = new Question((long) 1, 3, "Question number 3");
    private Question question6 = new Question((long) 1, 55, "Question number 55");
    private Question question7 = new Question((long) 1, 4, "Question number 4");
    private Question question8 = new Question((long) 1, 11, "Question number 11");
    private Question question9 = new Question((long) 1, 1, "Question number 1");
    private Question question10 = new Question((long) 1, 17, "Question number 17");

    @BeforeEach
    void createTestData(){
        questions.add(question);
        questions.add(question2);
        questions.add(question3);
        questions.add(question4);
        questions.add(question5);
        questions.add(question6);
        questions.add(question7);
        questions.add(question8);
        questions.add(question9);
        questions.add(question10);
    }

    @Test
    void selectSortTest(){
        sortingUtil.QuestionSelectSort(questions, questions.size());
        Assertions.assertEquals(questions.get(0), question9);
        Assertions.assertEquals(questions.get(1), question5);
        Assertions.assertEquals(questions.get(2), question7);
        Assertions.assertEquals(questions.get(3), question2);
        Assertions.assertEquals(questions.get(4), question3);
        Assertions.assertEquals(questions.get(5), question8);
        Assertions.assertEquals(questions.get(6), question);
        Assertions.assertEquals(questions.get(7), question10);
        Assertions.assertEquals(questions.get(8), question4);
        Assertions.assertEquals(questions.get(9), question6);
    }

}
