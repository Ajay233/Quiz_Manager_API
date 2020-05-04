package com.apiTest.Quiz.service;

import com.apiTest.Quiz.model.Question;
import com.apiTest.Quiz.model.Quiz;
import com.apiTest.Quiz.repository.QuestionRepository;
import com.apiTest.Quiz.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizService {

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    QuestionsService questionsService;

    public void deleteQuizAndAssociations(Quiz quiz){
        List<Question> questions = questionRepository.findByQuizId(quiz.getId());
        if(!questions.isEmpty()){
            questionsService.deleteQuestionsAndAnswers(questions);
        }
        quizRepository.delete(quiz);
    }

}
