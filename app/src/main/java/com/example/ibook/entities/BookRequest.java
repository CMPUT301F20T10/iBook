package com.example.ibook.entities;

public class BookRequest {
    private User requestReceiver;
    private User requestSender;
    private Book requestedBook;


    public BookRequest(){

    }// no arg constructor for database

    public BookRequest(User requestSender, User requestReceiver, Book requestedBook) {
        this.requestReceiver = requestReceiver;
        this.requestSender = requestSender;
        this.requestedBook = requestedBook;
    }// constructor

    public User getRequestReceiver() {
        return requestReceiver;
    }

    public void setRequestReceiver(User requestReceiver) {
        this.requestReceiver = requestReceiver;
    }

    public User getRequestSender() {
        return requestSender;
    }

    public void setRequestSender(User requestSender) {
        this.requestSender = requestSender;
    }

    public Book getRequestedBook() {
        return requestedBook;
    }

    public void setRequestedBook(Book requestedBook) {
        this.requestedBook = requestedBook;
    }

    //use this two messages to populate the notification list in the user class when its appropriate
    public String onRequestMessage(){
        return this.requestSender.getUserName() + " wants to borrow your book called " + this.requestedBook.getTitle();
    }//onRequestMessage

    public String onAcceptMessage(){
        return this.requestReceiver.getUserName() + " accepted your book request on the book called " + this.requestedBook.getTitle();
    }// onAcceptMessage
}// BookRequest class
