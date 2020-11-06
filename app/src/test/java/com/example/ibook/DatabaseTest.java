package com.example.ibook;

import org.junit.After;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
The user we use to test the MainActivity is:
4f5g5E162fh4bBnptMFlSS19GJp2
email : "ztan7@gmail.com"
password: "123456"
phoneNumber: "123456"
userName: "ztan7"
*/

import com.example.ibook.entities.Database;
import com.example.ibook.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DatabaseTest {
    private User currentUser;
    private FirebaseAuth uAuth;

    DatabaseTest() {
        currentUser = new User("ztan7", "123456", "ztan7@gmail.com", "123456");
        uAuth = FirebaseAuth.getInstance();
    }

    private Database MockDatabase() {
        uAuth.signInWithEmailAndPassword(this.currentUser.getEmail(), this.currentUser.getPassword());
        Database database = new Database();
        return database;
    }

    @After
    private void recoverDatabase() {
        String userID = uAuth.getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(userID).set(currentUser);
        uAuth.signOut();
    }

    @Test
    public void testGetUserDocumentReference() {
        Database database = MockDatabase();

    }
}
