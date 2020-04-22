package com.apiTest.Quiz.model;

import lombok.Data;

@Data
public class Question {

    private Long questionId;
    private Long quizId;
    private int questionNumber;
    private String description;

    public Question(){}

    public Question(int questionNumber, String description){
        this.questionNumber = questionNumber;
        this.description = description;
    }

}
