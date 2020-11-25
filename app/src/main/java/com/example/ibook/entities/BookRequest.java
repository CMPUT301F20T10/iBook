package com.example.ibook.entities;

public class BookRequest {
    private String requestReceiverID;
    private String requestSenderID;
    private String requestedBookID;
    private String requestStatus; // three requestStatus: Requested, Accepted, Confirmed


    public BookRequest(){

    }// no arg constructor for database

    public BookRequest(String requestSenderID, String requestReceiverID, String requestedBook) {
        this.requestReceiverID = requestReceiverID;
        this.requestSenderID = requestSenderID;
        this.requestedBookID = requestedBook;
    }// constructor

    public BookRequest(String requestSenderID, String requestReceiverID, String requestedBook, String requestStatus) {
        this.requestReceiverID = requestReceiverID;
        this.requestSenderID = requestSenderID;
        this.requestedBookID = requestedBook;
        this.requestStatus = requestStatus;
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

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }
//use this two messages to populate the notification list in the user class when its appropriate

}// BookRequest class
