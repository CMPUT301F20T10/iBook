package com.example.ibook.entities;

import android.provider.ContactsContract;
import android.widget.Toast;

import com.example.ibook.activities.EditProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Database {
    private FirebaseAuth uAuth;
    private FirebaseFirestore bb;
    private FirebaseUser currentUser;
    private DocumentReference userDocumentReference;
    private DocumentReference bookDocumentReference;
    private String currentUserUID;

    public Database(){
        this.uAuth = FirebaseAuth.getInstance();
        this.bb = FirebaseFirestore.getInstance();
        this.currentUser = uAuth.getCurrentUser();
        this.userDocumentReference = this.bb.collection("users").document(this.currentUser.getUid());
        this.bookDocumentReference = this.bb.collection("books").document();
        this.currentUserUID = this.currentUser.getUid();
    }

    public FirebaseUser getcurrentUser(){
        return currentUser;
    }

    public DocumentReference getUserDocumentReference() {
        return userDocumentReference;
    }

    public DocumentReference getBookDocumentReference() {
        return bookDocumentReference;
    }

    public FirebaseAuth getuAuth() {
        return uAuth;
    }

    public FirebaseFirestore getDb() {
        return bb;
    }

    public void addUser(User user){
        userDocumentReference.set(user);
    }

    public String getCurrentUserUID() {
        return currentUserUID;
    }





}
