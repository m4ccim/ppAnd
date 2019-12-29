package com.example.lb2.model;


import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable {
    private int Id;
    private String name;
    private String description;
    private Importance importance;
    private Date date;
    private int picResource;

    public Note(){}
    public Note(int id, String name, String description, Importance importance, Date date, int picResource){
        this.Id = id;
        this.name=name;
        this.description=description;
        this.importance=importance;
        this.date = date;
        this.picResource=picResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Importance getImportance() {
        return importance;
    }

    public void setImportance(Importance importance) {
        this.importance = importance;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getPicResource() {
        return picResource;
    }

    public void setPicResource(int picResource) {
        this.picResource = picResource;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
}


