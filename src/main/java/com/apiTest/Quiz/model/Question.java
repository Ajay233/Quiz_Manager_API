package com.apiTest.Quiz.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long quizId;
    private int questionNumber;
    private String description;
    private String imgUrl;

    public Question(){}

    public Question(Long quizId, int questionNumber, String description){
        this.quizId = quizId;
        this.questionNumber = questionNumber;
        this.description = description;
    }

}
