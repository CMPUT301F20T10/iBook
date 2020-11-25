package com.example.ibook.entities;

public class Response {
    private String receiverID;
    private String bookID;
    private String message;

    public Response(String receiverID, String bookID, String message) {
        this.receiverID = receiverID;
        this.bookID = bookID;
        this.message = message;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
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
