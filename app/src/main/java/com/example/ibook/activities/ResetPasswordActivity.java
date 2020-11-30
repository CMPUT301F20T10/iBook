package com.example.ibook.activities;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ibook.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for allowing the the user to reset/change password
 * updates the database with the changed password
 */
public class ResetPasswordActivity extends AppCompatActivity {

    EditText newPassword;
    EditText confirmPassword;
    Button applyChangesButton;
    Button backButton;
    FirebaseAuth uAuth; // to get user id
    FirebaseFirestore db; // to get database/ retireval
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide the top bar and make it full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar

        setContentView(R.layout.activity_reset_password);

        newPassword = findViewById(R.id.newPasswordEditText);
        confirmPassword = findViewById(R.id.confirmPasswordEditText);
        applyChangesButton = findViewById(R.id.saveChangesButton);
        backButton = findViewById(R.id.backButton);

        // save changes
        applyChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid(newPassword.getText().toString(), confirmPassword.getText().toString())) {
                    uAuth = FirebaseAuth.getInstance();
                    db = FirebaseFirestore.getInstance();
                    currentUser = uAuth.getCurrentUser();
                    DocumentReference documentReference = db.collection("users").document(currentUser.getUid());

                    Map<String, Object> editedInfo = new HashMap();
                    editedInfo.put("password", newPassword.getText().toString());

                    documentReference.update(editedInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }// onSuccess
                    }); //update the database

                    currentUser.updatePassword(newPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ResetPasswordActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                }

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }//onCreate

    /**
     * Checks if the user-input is valid or not for resetting password
     * @param updatedPassword
     * @param confirmedPassword
     * @return boolean - True or False
     * True if the user input is valid
     * False if the user input is invalid
     */
    public Boolean isValid(String updatedPassword, String confirmedPassword) {


        if (updatedPassword.isEmpty() || confirmedPassword.isEmpty()) {
            Toast.makeText(ResetPasswordActivity.this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
            return false;
        }// if

        if (updatedPassword.equals(confirmedPassword)) {

            if (updatedPassword.length() < 6) {
                Toast.makeText(ResetPasswordActivity.this, "The password should have atleast 6 characters", Toast.LENGTH_SHORT).show();
                return false;
            }// if
            return true;
        }// outer if

        //else
        Toast.makeText(ResetPasswordActivity.this, "The entered passwords don't match", Toast.LENGTH_SHORT).show();
        return false;
    }//isValid


}// class -ResetPasswordActivity