package com.example.ibook.entities;

import java.io.Serializable;

public class Book implements Serializable {

    public enum Status{
        Available, Requested, Borrowed, Return
    }

    private String title;
    private String authors;
    private String date;
    private String description;
    private Status status;
    private String isbn;

    private String owner;
    private String holder;

//Need to add picture data somehow

    //Constructor
    public Book(String title, String authors, String date, String isbn) {
        this.title = title;
        this.authors = authors;
        this.date = date;
        this.status = Status.Available;    // Attribute status denotes the status of the book, and thus we don't actually need
        this.isbn = isbn;
    }

    public Book(String title, String authors, String date, String description, Status status, String isbn) {
        this.title = title;
        this.authors = authors;
        this.date = date;
        this.description = description;
        this.status = status;    // Attribute status denotes the status of the book, and thus we don't actually need
        this.isbn = isbn;
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

    public Status getStatus() {
        return status;
    }

    public boolean isAvailable() {
        return this.status == Status.Available;
    }

    public String getIsbn() { return isbn; }

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

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setIsbn(String isbn) { this.isbn = isbn; }

    public void setAvailable(boolean available) { this.status = Status.Available; }
}
