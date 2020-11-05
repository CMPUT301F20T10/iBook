package com.example.ibook.entities;

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
import java.util.Map;

public class User {
    private String userName;
    private String password;
    private String email;
    private String phoneNumber;

    private FirebaseAuth uAuth; // user authentication
    private FirebaseFirestore db;
    private String userID;

    private DocumentReference documentReference;

    public User() {
        this.uAuth = FirebaseAuth.getInstance();
        this.userID = this.uAuth.getCurrentUser().getUid();
        this.db = FirebaseFirestore.getInstance();

        this.documentReference = db.collection("users").document(userID);//creating a document for the use
        while (this.userName == null) {
            this.fetch();
        }
    }

    /**
     * This constructor is used to create an user object
     * */
    public User(String userName, String password, String email, String phoneNumber) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;

        this.uAuth = FirebaseAuth.getInstance();
        this.userID = this.uAuth.getCurrentUser().getUid();
        this.db = FirebaseFirestore.getInstance();

        this.documentReference = db.collection("users").document(userID);//creating a document for the user

    }

    /**
     * This Method should update the data on database according to the data it has.
     * @Parameter - None
     * @Return - None
     * */
    public void commit() {

        Map<String,Object> user = new HashMap();
        //put info for the user in hashMap
        user.put("userName", this.userName);
        user.put("email", this.email);
        user.put("phoneNumber", this.phoneNumber);
        user.put("password", this.password);
        user.put("Booklist", new ArrayList<Book>());

        //update the document
        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("tag", "User profile is created for " + userID);
            }// onSuccess
        });
    }

    public void fetch() {

        Task get = this.documentReference.get();

        // Somehow on my device, the user Info page will not function properly,
        // If you find this unnecessary, feel free to remove
        SystemClock.sleep(50);
        if (get.isSuccessful()) {
            DocumentSnapshot document = (DocumentSnapshot) get.getResult();
            this.userName = document.getString("userName");
            this.email = document.getString("email");
            this.phoneNumber = document.getString("phoneNumber");
            this.password = document.getString("password");
        }
        else {
            Log.d("tag", "loading user info failed");
        }
    }

    /* getters and setters
    * */
    public DocumentReference getDocumentReference() {
        return documentReference;
    }

    public FirebaseAuth getuAuth() {
        return uAuth;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public String getUserID() {
        return userID;
    }

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

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
