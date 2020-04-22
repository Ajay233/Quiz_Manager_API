package com.apiTest.Quiz.model;

import lombok.Data;

@Data
public class Question {

    private Long id;
    private Long quizId;
    private int questionNumber;
    private String description;

    public Question(){}

    public Question(Long quizId, int questionNumber, String description){
        this.quizId = quizId;
        this.questionNumber = questionNumber;
        this.description = description;
    }

}
