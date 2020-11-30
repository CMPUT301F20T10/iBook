package com.example.ibook.entities;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class Book implements Serializable, Comparable<Book>{

    public enum Status {
        Available, Requested, Borrowed, Return, Accepted, Returning
    }

    private String title;
    private String authors;
    private String date;
    private String description;
    private Status status;
    private String isbn;
    private String owner;
    private String bookID;
    private transient GeoPoint meetingLocation;
    private String meetingText;


    //Need to add picture data somehow
    public Book() {

    }

    //Constructor
    public Book(String title, String author, String date, String description, Status status, String isbn, String ownerID, String bookID) {
        this.title = title;
        this.authors = author;
        this.date = date;
        this.description = description;
        this.status = status;    // Attribute status denotes the status of the book, and thus we don't actually need
        this.isbn = isbn;
        this.owner = ownerID;
        this.bookID = bookID;
        this.meetingLocation = null;
        this.meetingText = "Meeting Location";
    }

    //Getters
    public String getTitle() {
        return title;
    }

    public String getAuthors() {
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

    public String getIsbn() {
        return isbn;
    }

    //Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthors(String authors) {
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

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setAvailable(boolean available) {
        this.status = Status.Available;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String ownerID) {
        this.owner = ownerID;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    @Override
    public int compareTo(Book o) {
        return this.title.toLowerCase().compareTo(o.title.toLowerCase());
    }

    public void setMeetingLocation(Double latitude, Double longitude) {
        meetingLocation = new GeoPoint(latitude,longitude);
    }
    public GeoPoint getMeetingLocation() {
        return this.meetingLocation;
    }
    public void setMeetingText(String text) {
        this.meetingText = text;
    }
    public String getMeetingText(){
        return meetingText;
    }
}
