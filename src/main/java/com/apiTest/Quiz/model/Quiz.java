package com.apiTest.Quiz.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String category;
    private String status;
    private String imgUrl;
    private String author;
    private Long authorId;

    public Quiz(){}

    public Quiz(String name, String description, String category, String author, Long authorId){
        this.name = name;
        this.description = description;
        this.category = category;
        this.status = "DRAFT";
        this.author = author;
        this.authorId = authorId;
    }

}
