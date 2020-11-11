package com.example.ibook.entities;

import android.app.Notification;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a model/entity class for the user
 * has getters for the attributes and is passed to the firestore cloud as a object
 */
public class User {
    private String userName;
    private String password;
    private String email;
    private String phoneNumber;
    private ArrayList<Book> BookList; //made it capitalized B because someone named key in database colloction to be capital, have to see later
    private ArrayList<Book> requestedBookList;
    private ArrayList<Book> borrowedBookList;
    private ArrayList<String> notificationList; //holds all the notifications for the user
    /**
     *   no argument constructor for the firebase cloud
     */
    public User() {

    }// empty constructor

    /**
     * This constructor is called when a new user signs up
     * @param userName
     * @param password
     * @param email
     * @param phoneNumber
     */
    public User(String userName, String password, String email, String phoneNumber) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.BookList = new ArrayList<Book>();
    }// constructor


    public String getUserName() {
        return userName;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * This method returns the users booklist
     * @return arraylist of books of the user
     */
    public ArrayList<Book> getBooklist() {
        return BookList;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * This method is used to add books to the user's booklist
     * @param book
     */
    public void addBookToOwnedBooksList(Book book){

        BookList.add(book);
    }// addBook

    public void addBookToBorrowedBooksList(Book book){
        borrowedBookList.add(book);
    }// addBookToBorrowedBookList

    public void addBookToRequestedBooksList(Book book){
        requestedBookList.add(book);
    }//addBookToBorrowedBookList

    public void deleteFromRequestedBookList(Book book){
       requestedBookList.remove(book);
    }//deleteFromRequestedBookList

    public ArrayList<String> getNotificationList() {
        return notificationList;
    }

    public void addToNotificationList (String message){
        notificationList.add(message);
    }

    public void deleteFromOwnedBookList(Book book){
        BookList.remove(book);
    }//deleteFromRequestedBookList

    public void deleteFromBorrowedBookList(Book book){
        borrowedBookList.remove(book);
    }//deleteFromRequestedBookList



}// users class
