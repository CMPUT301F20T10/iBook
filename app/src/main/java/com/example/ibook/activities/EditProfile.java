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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * Activity for letting users update their profile information
 * such as username, email-id, phone number
 * updates the database with the edited information and stores it
 */
public class EditProfile extends AppCompatActivity {


    Button saveButton,backButton;
    EditText usernameEditText, phoneEditText, emailEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide the top bar and make it full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar

        setContentView(R.layout.activity_edit_profile);

        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        saveButton = findViewById(R.id.saveButton);
        backButton=findViewById(R.id.backButton);


        usernameEditText.setText(username);
        emailEditText.setText(email);
        phoneEditText.setText(phone);

        final String userID = MainActivity.database.getCurrentUserUID();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if  left empty
                if (usernameEditText.getText().toString().isEmpty() || emailEditText.getText().toString().isEmpty() || phoneEditText.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfile.this, "Cannot be left empty", Toast.LENGTH_SHORT).show();
                    return;
                }// if
                if(!phoneIsValid(phoneEditText.getText().toString())){
                    Toast.makeText(getBaseContext(), "Phone number is not valid", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .whereEqualTo("userName", usernameEditText.getText().toString()).whereNotEqualTo("userName",username)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            //check if username already exists
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (!task.getResult().isEmpty()) {
                                    Toast.makeText(getBaseContext(), "Username exists", Toast.LENGTH_SHORT).show();
                                } else {
                                    String email = emailEditText.getText().toString();
                                    MainActivity.database.getuAuth().getCurrentUser().updateEmail(email)
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }//onFailure
                                            })
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // DocumentReference documentReference = db.collection("users").document(userID);
                                                    MainActivity.user.setEmail(emailEditText.getText().toString());
                                                    MainActivity.user.setUserName(usernameEditText.getText().toString());
                                                    MainActivity.user.setPhoneNumber(phoneEditText.getText().toString());

                                                    MainActivity.database.getUserDocumentReference().set(MainActivity.user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(EditProfile.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        }
                                                    }); //update the database
                                                }// onClick
                                            });
                                }
                            }
                        });

            }//onClick
        });
    }//onCreate
    public boolean phoneIsValid(String phoneNumber){
        return phoneNumber.matches("[0-9]+") && phoneNumber.length() == 10;
    }

}// Class - EditProfile
