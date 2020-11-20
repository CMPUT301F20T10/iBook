package com.example.ibook.entities;

import java.util.ArrayList;

/**
 * This is a model/entity class for the user
 * has getters for the attributes and is passed to the firestore cloud as a object
 */
public class User {
    private String userName;
    private String password;
    private String email;
    private String phoneNumber;
    private ArrayList<Book> ownBookList; //made it capitalized B because someone named key in database colloction to be capital, have to see later
    private ArrayList<Book> requestedBookList;
    private ArrayList<Book> acceptBookList;
    private ArrayList<Book> borrowedBookList;
    private ArrayList<String> notificationList; //holds all the notifications for the user
    private String userID;


//
//
//    BookRequest
//
//    Sender;
//    Receiver;
//    Book;
//
//    for the current user, I will check in the book request collection.
//    for everyplace where currentUserID matches the requestsender  docoument in bookRequest document
//
//    if book.status is requested,
//    then i gather, then i display in requested toggle
//
//            the owner accepts it, delete all the documents where the bookID matches, except for the document where the senderID is the one i accepted of
//
//            if book.status is accepted
//    then I put them in accepeted booklist

    /**
     * no argument constructor for the firebase cloud
     */
    public User() {


    }// empty constructor

    /**
     * This constructor is called when a new user signs up
     *
     * @param userName
     * @param password
     * @param email
     * @param phoneNumber
     */
    public User(String userName, String password, String email, String phoneNumber, String userID) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.ownBookList = new ArrayList<Book>();
        this.borrowedBookList = new ArrayList<Book>();
        this.requestedBookList = new ArrayList<Book>();
        this.notificationList = new ArrayList<String>();
        this.acceptBookList = new ArrayList<>();
        this.userID = userID;
    }// constructor

    public ArrayList<Book> getAcceptBookList() {
        return acceptBookList;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Book> getOwnBookList() {
        return this.ownBookList;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * This method returns the users booklist
     *
     * @return arraylist of books of the user
     */

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * This method is used to add books to the user's booklist
     *
     * @param book
     */
    public void addBookToOwnedBooksList(Book book) {

        this.ownBookList.add(book);
    }// addBook


    public void addBookToBorrowedBooksList(Book book) {
        borrowedBookList.add(book);
    }// addBookToBorrowedBookList

    public ArrayList<Book> getRequestedBookList() {
        return requestedBookList;
    }

    public ArrayList<Book> getBorrowedBookList() {
        return borrowedBookList;
    }

    public void addBookToRequestedBooksList(Book book) {
        requestedBookList.add(book);
    }//addBookToBorrowedBookList

    public void setOwnBookList(ArrayList<Book> bookList) {
        this.ownBookList = bookList;
    }

    public void deleteFromRequestedBookList(Book book) {
        requestedBookList.remove(book);
    }//deleteFromRequestedBookList

    public ArrayList<String> getNotificationList() {
        return notificationList;
    }

    public void addToNotificationList(String message) {
        notificationList.add(message);
    }

    public void deleteFromOwnedBookList(Book book) {
        this.ownBookList.remove(book);
    }//deleteFromRequestedBookList

    public void deleteFromBorrowedBookList(Book book) {
        borrowedBookList.remove(book);
    }//deleteFromRequestedBookList


}// users class
