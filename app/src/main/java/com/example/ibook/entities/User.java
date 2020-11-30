package com.example.ibook.entities;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * This is a model/entity class for the user
 * has getters for the attributes and is passed to the firestore cloud as a object
 */
public class User implements Serializable {
    private String userName;
    private String password;
    private String email;
    private String phoneNumber;
    private ArrayList<String> notificationList; //holds all the notifications for the user
    private String userID;
    private Date lastLoginTime;


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
    public User(String userName, String password, String email, String phoneNumber, String userID,Date loginTime) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.notificationList = new ArrayList<String>();
        this.userID = userID;
        this.lastLoginTime = loginTime;
    }// constructor


    public String getUserName() {
        return userName;
    }

    public String getUserID() {
        return userID;
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

    public void setNotificationList(ArrayList<String> notificationList) {
        this.notificationList = notificationList;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
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
     *
     * @return arraylist of books of the user
     */

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public ArrayList<String> getNotificationList() {
        return notificationList;
    }

    public void addToNotificationList(String message) {
        notificationList.add(message);
    }

    public void removeFromNotificationList(int position){
        this.notificationList.remove(position);
    }// removeFromNotificationList



}// users class
