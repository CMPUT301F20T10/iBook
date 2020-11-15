package com.example.ibook.entities;

public class BookRequest {
    private String requestReceiverID;
    private String requestSenderID;
    private String requestedBookID;


    public BookRequest(){

    }// no arg constructor for database

    public BookRequest(String requestSenderID, String requestReceiverID, String requestedBook) {
        this.requestReceiverID = requestReceiverID;
        this.requestSenderID = requestSenderID;
        this.requestedBookID = requestedBook;
    }// constructor

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
