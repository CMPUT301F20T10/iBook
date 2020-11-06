package com.example.ibook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

/**
 * Activity for letting users update their profile information
 * such as username, email-id, phone number
 * updates the database with the edited information and stores it
 */
public class EditProfile extends AppCompatActivity {


    Button saveButton;
    EditText usernameEditText, phoneEditText, emailEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        final String userID = SignUpActivity.database.getCurrentUserUID();

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

                SignUpActivity.database.getcurrentUser().updateEmail(email)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // DocumentReference documentReference = db.collection("users").document(userID);
                                SignUpActivity.user.setEmail(emailEditText.getText().toString());
                                SignUpActivity.user.setUserName(usernameEditText.getText().toString());
                                SignUpActivity.user.setPhoneNumber(phoneEditText.getText().toString());

//                        Map<String, Object> editedInfo = new HashMap();
//                        editedInfo.put("userName", );
//                        editedInfo.put("email", emailEditText.getText().toString());
//                        editedInfo.put("phoneNumber", phoneEditText.getText().toString());

                                SignUpActivity.database.getUserDocumentReference().set(SignUpActivity.user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(EditProfile.this, "Database successfully updated", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }); //update the database

                            }// onClick
                        });


            }
        });

    }
}
