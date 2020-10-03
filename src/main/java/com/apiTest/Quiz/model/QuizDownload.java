package com.apiTest.Quiz.model;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class QuizDownload {

    private List<Question> questions;
    private HashMap<Long, List<Answer>> answers;

    public QuizDownload(){}

    public QuizDownload(List<Question> questions, HashMap<Long, List<Answer>> answers){
        this.questions = questions;
        this.answers = answers;
    }

}
