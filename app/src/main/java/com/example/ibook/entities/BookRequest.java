package com.example.ibook.entities;


import java.util.Date;

public class BookRequest {
    private String requestReceiverID;
    private String requestSenderID;
    private String requestedBookID;

    private String requestSenderUsername;
    private String requestedBookTitle;
    private String bookRequestID;
    private String requestStatus;
    private String datetime;
    private Date timeOfRequest;



    public BookRequest(){

    }// no arg constructor for database



    public BookRequest(String requestSenderID, String requestReceiverID, String requestedBook, String requestSenderUsername, String requestedBookTitle, String bookRequestID, String requestStatus, String datetime, Date timeOfRequest) {
        this.requestReceiverID = requestReceiverID;
        this.requestSenderID = requestSenderID;
        this.requestedBookID = requestedBook;
        this.requestSenderUsername = requestSenderUsername;
        this.requestedBookTitle = requestedBookTitle;
        this.bookRequestID = bookRequestID;
        this.requestStatus = requestStatus;
        this.datetime = datetime;
        this.timeOfRequest = timeOfRequest;
    }// constructor

    public Date getTimeOfRequest() {
        return timeOfRequest;
    }

    public void setTimeOfRequest(Date timeOfRequest) {
        this.timeOfRequest = timeOfRequest;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }


    public String getRequestSenderUsername() {
        return requestSenderUsername;
    }

    public String getBookRequestID() {
        return bookRequestID;
    }

    public void setBookRequestID(String bookRequestID) {
        this.bookRequestID = bookRequestID;
    }

    public void setRequestSenderUsername(String requestSenderUsername) {
        this.requestSenderUsername = requestSenderUsername;
    }

    public String getRequestedBookTitle() {
        return requestedBookTitle;
    }

    public void setRequestedBookTitle(String requestedBookTitle) {
        this.requestedBookTitle = requestedBookTitle;
    }


    public String getRequestReceiverID() {
        return requestReceiverID;
    }

    public void setRequestReceiverID(String requestReceiverID) {
        this.requestReceiverID = requestReceiverID;
    }

    public String getRequestSenderID() {
        return requestSenderID;
    }

    public void setRequestSenderID(String requestSenderID) {
        this.requestSenderID = requestSenderID;
    }

    public String getRequestedBookID() {
        return requestedBookID;
    }

    public void setRequestedBookID(String requestedBookID) {
        this.requestedBookID = requestedBookID;
    }


//use this two messages to populate the notification list in the user class when its appropriate

}// BookRequest class
