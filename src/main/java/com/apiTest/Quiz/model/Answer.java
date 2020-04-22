package com.apiTest.Quiz.model;

import lombok.Data;

@Data
public class Answer {

    private Long id;
    private Long questionId;
    private int answerNumber;
    private String description;

    public Answer(){}

    public Answer(Long questionId, int answerNumber, String description){
        this.questionId = questionId;
        this.answerNumber = answerNumber;
        this.description = description;
    }

}
