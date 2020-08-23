package com.apiTest.Quiz.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="answers")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long questionId;
    private String answerIndex;
    private String description;
    private Boolean correctAnswer;

    public Answer(){}

    public Answer(Long questionId, String answerIndex, String description, Boolean correctAnswer){
        this.questionId = questionId;
        this.answerIndex = answerIndex;
        this.description = description;
        this.correctAnswer = correctAnswer;
    }

}
