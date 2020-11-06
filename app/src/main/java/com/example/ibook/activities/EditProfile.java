package com.example.ibook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ibook.R;
import com.example.ibook.fragment.UserFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    FirebaseAuth uAuth;
    FirebaseFirestore db;
    Button saveButton;
    EditText usernameEditText, phoneEditText, emailEditText;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide the top bar and make it full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar

        setContentView(R.layout.activity_edit_profile);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        saveButton = findViewById(R.id.saveButton);

        usernameEditText.setText(username);
        emailEditText.setText(email);
        phoneEditText.setText(phone);

        uAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = uAuth.getCurrentUser();
        final String userID = user.getUid();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if  left empty
                if (usernameEditText.getText().toString().isEmpty() || emailEditText.getText().toString().isEmpty()
                        || phoneEditText.getText().toString().isEmpty()) {

                    Toast.makeText(EditProfile.this, "Cannot be left empty", Toast.LENGTH_SHORT).show();
                    return;
                }// if

                String email = emailEditText.getText().toString();

                user.updateEmail(email)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference documentReference = db.collection("users").document(userID);

                        Map<String, Object> editedInfo = new HashMap();
                        editedInfo.put("userName", usernameEditText.getText().toString());
                        editedInfo.put("email", emailEditText.getText().toString());
                        editedInfo.put("phoneNumber", phoneEditText.getText().toString());

                        documentReference.update(editedInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditProfile.this, "Database successfully updated", Toast.LENGTH_SHORT).show();
                                finish();
                            }// onSuccess
                        }); //update the database

                        Toast.makeText(EditProfile.this, "Updated", Toast.LENGTH_SHORT).show();
                    }//onSuccess

                });


            }
        });


    }
}

