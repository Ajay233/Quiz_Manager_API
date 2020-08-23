package com.apiTest.IntegrationTests.Services;

import com.apiTest.Quiz.model.Answer;
import com.apiTest.Quiz.service.AnswerValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class AnswerValidatorTest {

    @Autowired
    AnswerValidator answerValidator;

    @Test
    public void capitaliseTest(){
        Answer answer1 = new Answer((long) 1, "A", "test", false);
        Answer answer2 = new Answer((long) 1, "b", "test", false);
        Answer answer3 = new Answer((long) 1, "c", "test", true);
        Answer answer4 = new Answer((long) 1, "D", "test", false);

        ArrayList<Answer> list = new ArrayList<>();
        list.add(answer1);
        list.add(answer2);
        list.add(answer3);
        list.add(answer4);

        List<Answer> list2 = answerValidator.capitalise(list);
        Assertions.assertEquals(list2.get(0).getAnswerIndex(), "A");
        Assertions.assertEquals(list2.get(1).getAnswerIndex(), "B");
        Assertions.assertEquals(list2.get(2).getAnswerIndex(), "C");
        Assertions.assertEquals(list2.get(3).getAnswerIndex(), "D");
    }

}
