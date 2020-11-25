package com.example.ibook.entities;

public class Request {
    private String senderID;
    private String senderName;
    private String bookID;
    private String message;

    public Request(String senderID, String senderName, String bookID, String message) {
        this.senderID = senderID;
        this.senderName = senderName;
        this.bookID = bookID;
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
