package com.example.ibook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ibook.activities.EditProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ResetPassword extends AppCompatActivity {

    EditText newPassword;
    EditText confirmPassword;
    Button applyChangesButton;
    FirebaseAuth uAuth; // to get user id
    FirebaseFirestore db; // to get database/ retireval
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        newPassword = findViewById(R.id.newPasswordEditText);
        confirmPassword = findViewById(R.id.confirmPasswordEditText);
        applyChangesButton = findViewById(R.id.saveChangesButton);

        applyChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid(newPassword.getText().toString(),confirmPassword.getText().toString())){
                    uAuth = FirebaseAuth.getInstance();
                    db = FirebaseFirestore.getInstance();
                    currentUser = uAuth.getCurrentUser();
                    currentUser.updatePassword(newPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ResetPassword.this, "Password Updated", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                }

            }
        });


    }//onCreate


    public Boolean isValid(String updatedPassword, String confirmedPassword){


        if(updatedPassword.isEmpty() || confirmedPassword.isEmpty()){
            Toast.makeText(ResetPassword.this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
            return false;
        }// if

        if(updatedPassword.equals(confirmedPassword)){

            if(updatedPassword.length() < 6){
                Toast.makeText(ResetPassword.this, "The password should have atleast 6 characters", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }

        //else
        Toast.makeText(ResetPassword.this, "The entered passwords don't match", Toast.LENGTH_SHORT).show();
        return false;
    }//isValid


}