package com.example.ibook.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ibook.R;
import com.example.ibook.activities.EditProfile;
import com.google.android.gms.tasks.OnFailureListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordActivity extends AppCompatActivity {

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
                    DocumentReference documentReference = db.collection("users").document(currentUser.getUid());

                    Map<String, Object> editedInfo = new HashMap();
                    editedInfo.put("password", newPassword.getText().toString());

                    documentReference.update(editedInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ResetPassword.this, "Database successfully updated", Toast.LENGTH_SHORT).show();
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


    }//onCreate


    public Boolean isValid(String updatedPassword, String confirmedPassword){


        if(updatedPassword.isEmpty() || confirmedPassword.isEmpty()){
            Toast.makeText(ResetPasswordActivity.this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
            return false;
        }// if

        if(updatedPassword.equals(confirmedPassword)){

            if(updatedPassword.length() < 6){
                Toast.makeText(ResetPasswordActivity.this, "The password should have atleast 6 characters", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }

        //else
        Toast.makeText(ResetPasswordActivity.this, "The entered passwords don't match", Toast.LENGTH_SHORT).show();
        return false;
    }//isValid


}