package com.apiTest.Quiz.service;

import com.apiTest.Quiz.model.Question;
import com.apiTest.Quiz.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionValidator {

    @Autowired
    QuestionRepository questionRepository;

    private boolean validateQuizIds(List<Question> questions){
        return questions.stream().allMatch((question) -> question.getQuizId().getClass().equals(Long.class));
    }

    private boolean validateQuestionDescriptions(List<Question> questions){
        return questions.stream().allMatch((question) -> !question.getDescription().isEmpty());
    }

    public boolean validateQuestionFields(List<Question> questions){
        return validateQuizIds(questions) && validateQuestionDescriptions(questions);
    }

    public boolean validateQuestionsExist(List<Question> questions){
        return questions.stream().allMatch((question) -> questionRepository.existsById(question.getId()));
    }

}
