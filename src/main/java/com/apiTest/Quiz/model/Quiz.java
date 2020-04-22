package com.apiTest.Quiz.model;


public class Quiz {

    private Long quizId;
    private String name;
    private String description;
    private String category;

    public Quiz(){}

    public Quiz(String name, String description, String category){
        this.name = name;
        this.description = description;
        this.category = category;
    }

}
