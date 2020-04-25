package com.apiTest.Quiz.service;

import com.apiTest.Quiz.model.Question;
import com.apiTest.Quiz.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionValidator {

    @Autowired
    QuestionRepository questionRepository;

    public List<Question> validateQuestion(List<Question> questions){
       List<Question> nonMatches;
       nonMatches = questions.stream()
               .filter((question) -> questionRepository.findById(question.getId()).get() == null)
               .collect(Collectors.toList());
        return nonMatches;
    }

}
