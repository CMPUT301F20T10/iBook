package com.example.ibook.entities;

import android.provider.ContactsContract;
import android.widget.Toast;

import com.example.ibook.activities.EditProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This class is created to have "seperation of concerns", meaning most of the database actions will be
 * performed via this class and once the database object is created it can be used throughout the app
 * without having to make new firebase variables in each class.( makes an static object of it only once in
 * signup or in login, depending on where the user goes first and then we can use that static obj
 * anywhere in all other classes
 */
public class Database {
    private FirebaseAuth uAuth;
    private FirebaseFirestore db;

    public Database(FirebaseAuth uAuth, FirebaseFirestore db) {
        this.uAuth = uAuth;
        this.db = db;
    }

    public Database() {
        this.uAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * @return DocumentReference - returns the document reference of the current user
     */
    public DocumentReference getUserDocumentReference() {
        return this.db.collection("users").document(this.getCurrentUserUID());
    }//getUserDocumentReference

    public FirebaseAuth getuAuth() {
        return uAuth;
    }

    public FirebaseFirestore getDb() {
        return db;
    }//getDb

    /**
     * adds a user to the database when the user signs up
     *
     * @param user - a User class object
     */
    public void addUser(User user) {
        this.db.collection("users").document(getCurrentUserUID()).set(user);
    }//addUser

    /**
     * @return - returns the current user's unique ID
     */
    public String getCurrentUserUID() {

        return this.uAuth.getCurrentUser().getUid();
    }//getCurrentUserUID

    public DocumentReference getBookDocumentReference(String bookId) {
        return this.db.collection("books").document(bookId);
    }//getBookDocumentReference



}// Database
