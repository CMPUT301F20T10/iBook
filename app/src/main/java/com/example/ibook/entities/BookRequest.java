package com.example.ibook.entities;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.FieldValue;
import com.google.protobuf.Timestamp;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.text.DateFormat.getDateTimeInstance;

public class BookRequest {
    private String requestReceiverID;
    private String requestSenderID;
    private String requestedBookID;

    private String requestSenderUsername;
    private String requestedBookTitle;
    private String bookRequestID;
    private String requestStatus;
    private String datetime;



    public BookRequest(){

    }// no arg constructor for database

    public BookRequest(String requestSenderID, String requestReceiverID, String requestedBook, String requestSenderUsername, String requestedBookTitle, String bookRequestID, String requestStatus,String datetime) {
        this.requestReceiverID = requestReceiverID;
        this.requestSenderID = requestSenderID;
        this.requestedBookID = requestedBook;
        this.requestSenderUsername = requestSenderUsername;
        this.requestedBookTitle = requestedBookTitle;
        this.bookRequestID = bookRequestID;
        this.requestStatus = requestStatus;
        this.datetime = datetime;
    }// constructor

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
