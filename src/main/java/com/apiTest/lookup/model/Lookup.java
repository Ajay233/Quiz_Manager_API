package com.apiTest.lookup.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "lookup")
public class Lookup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;

    public Lookup(){};

    public Lookup(String name, String type){
        this.name = name;
        this.type = type;
    }

}
