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

public class Database {
    private FirebaseAuth uAuth;
    private FirebaseFirestore db;


    public Database(){
        this.uAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();

    }
    
    public DocumentReference getUserDocumentReference() {
        return this.db.collection("users").document(getCurrentUserUID());
    }


    public FirebaseAuth getuAuth() {
        return uAuth;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public void addUser(User user){
        this.db.collection("users").document(getCurrentUserUID()).set(user);
    }

    public String getCurrentUserUID() {
        return this.uAuth.getCurrentUser().getUid();
    }





}
