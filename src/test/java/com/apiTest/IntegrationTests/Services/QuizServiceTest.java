package com.apiTest.IntegrationTests.Services;

import com.apiTest.Quiz.model.Answer;
import com.apiTest.Quiz.model.Question;
import com.apiTest.Quiz.model.Quiz;
import com.apiTest.Quiz.model.QuizDownload;
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

import java.util.List;

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
    private Answer answer1 = new Answer((long) 1, "A", "Answer 1", false); // id:1
    private Answer answer2 = new Answer((long) 1, "B", "Answer 2", false); // id:2
    private Answer answer3 = new Answer((long) 1, "C", "Answer 3", true); // id:3
    private Answer answer4 = new Answer((long) 1, "D", "Answer 4", false); // id:4
    private Answer answer5 = new Answer((long) 2, "A", "Answer 1", true); // id:5
    private Answer answer6 = new Answer((long) 2, "B", "Answer 2", false); // id:6
    private Answer answer7 = new Answer((long) 2, "C", "Answer 3", false); // id:7
    private Answer answer8 = new Answer((long) 2, "D", "Answer 4", false); // id:8


    @BeforeEach
    public void setUpDatabase(){
        quizRepository.save(quiz);
        questionRepository.save(question1);
        questionRepository.save(question2);
        answersRepository.save(answer1);
        answersRepository.save(answer2);
        answersRepository.save(answer3);
        answersRepository.save(answer4);
        answersRepository.save(answer5);
        answersRepository.save(answer6);
        answersRepository.save(answer7);
        answersRepository.save(answer8);
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

    @Test
    public void quizReadyTest(){
        Assertions.assertEquals(quizService.quizReady((long)1), true);
    }

    @Test
    public void quizNotReadyTest(){
        Answer updatedAnswer = answersRepository.findById((long) 5).get();
        updatedAnswer.setCorrectAnswer(false);
        answersRepository.save(updatedAnswer);
        Assertions.assertEquals(quizService.quizReady((long)1), false);
    }

    @Test
    public void quizDownloadDataTest(){
        List<Question> questions = questionRepository.findByQuizId((long) 1);
        List<Answer> answerSet1 = answersRepository.findByQuestionId((long) 1);
        List<Answer> answerSet2 = answersRepository.findByQuestionId((long) 2);

        QuizDownload quizDownload = quizService.quizDownloadData((long) 1);

        Assertions.assertEquals(quizDownload.getQuestions().get(0), questions.get(0));
        Assertions.assertEquals(quizDownload.getQuestions().get(1), questions.get(1));
        Assertions.assertEquals(quizDownload.getAnswers().get(questions.get(0).getId()), answerSet1);
        Assertions.assertEquals(quizDownload.getAnswers().get(questions.get(1).getId()), answerSet2);
    }

}
