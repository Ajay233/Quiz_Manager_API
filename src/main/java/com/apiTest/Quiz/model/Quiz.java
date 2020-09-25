package com.apiTest.Quiz.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "quizes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String category;
    private String status;
    private String imgUrl;

    public Quiz(){}

    public Quiz(String name, String description, String category){
        this.name = name;
        this.description = description;
        this.category = category;
        this.status = "DRAFT";
    }

}
