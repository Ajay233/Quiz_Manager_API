package com.apiTest.Quiz.model;

import lombok.Data;

import java.util.List;

@Data
public class QuizListItem {

    private String category;
    private List<Quiz> quizList;

    public QuizListItem(){}

    public QuizListItem(String category, List<Quiz> quizList){
        this.category = category;
        this.quizList = quizList;
    }

}
