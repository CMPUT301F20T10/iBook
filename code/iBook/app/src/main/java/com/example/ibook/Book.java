package com.example.ibook;

import android.service.autofill.DateValueSanitizer;

public class Book {

    private String title;
    private String authors;
    private String date;
    private String description;
    private String state;
    boolean available;

    //Need to add picture data somehow

    //Constructor
    Book(String title, String authors, String date, String description, String state, boolean available) {
        this.title = title;
        this.authors = authors;
        this.date = date;
        this.description = description;
        this.state = state;
        this.available = available;
    }

    //Getters
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return authors;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getState() {
        return state;
    }

    public boolean isAvailable() {
        return available;
    }

    //Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String authors) {
        this.authors = authors;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
