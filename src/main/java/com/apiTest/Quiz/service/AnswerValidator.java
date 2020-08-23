package com.apiTest.Quiz.service;

import com.apiTest.Quiz.model.Answer;
import com.apiTest.Quiz.repository.AnswersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnswerValidator {

    @Autowired
    AnswersRepository answersRepository;

    private boolean answerQuestionIdsValid(List<Answer> answers){
        return answers.stream().allMatch((answer) -> answer.getQuestionId().getClass().equals(Long.class));
    }

    // Find out how to validate that an int has been entered
//    private boolean answerNumbersValid(List<Answer> answers){
//        return answers.stream().allMatch((answer) -> answer.getAnswerNumber())
//    }

    private boolean answerDescriptionsValid(List<Answer> answers){
        return answers.stream().allMatch((answer) -> !answer.getDescription().isEmpty());
    }

    private boolean correctAnswersValid(List<Answer> answers){
        return answers.stream().allMatch((answer) -> answer.getCorrectAnswer().getClass().equals(Boolean.class));
    }

    public boolean validateAnswer(List<Answer> answers){
        return answerQuestionIdsValid(answers) && answerDescriptionsValid(answers) && correctAnswersValid(answers);
    }

    public boolean answersExist(List<Answer> answers){
        return answers.stream().allMatch((answer) -> answersRepository.existsById(answer.getId()));
    }

    public List<Answer> capitalise(List<Answer> answers){
        ArrayList<Answer> list = new ArrayList<>();
        answers.stream().forEach(answer -> {
            if(answer.getAnswerIndex().matches("[a-z]")){
                answer.setAnswerIndex(answer.getAnswerIndex().toUpperCase());
                list.add(answer);
            } else {
                list.add(answer);
            }
        });
        return list;
    }

}
