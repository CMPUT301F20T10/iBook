package com.example.ibook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ibook.R;
import com.example.ibook.entities.Database;
import com.example.ibook.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for signing users up for the app
 * registers the users in database firebase authentication
 * creates a collection called "users" in cloud firestore
 */
public class SignUpActivity extends AppCompatActivity {

    private EditText ed_username;
    private EditText ed_password;
    private EditText ed_email;
    private EditText ed_phoneNumber;
    private EditText ed_confirmPassword;
    private ProgressBar ed_progressBar;
    private FirebaseAuth uAuth;
    // user authentication


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide the top bar and make it full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar

        setContentView(R.layout.activity_sign_up);
        ed_username = findViewById(R.id.ed_username_signup);
        ed_email = findViewById(R.id.ed_email_signup);
        ed_password = findViewById(R.id.ed_password_signup);
        ed_phoneNumber = findViewById(R.id.ed_phoneNumber_signup);
        ed_confirmPassword = findViewById(R.id.ed_confirmPassword_signup);
        ed_progressBar = findViewById(R.id.signUpProgressBar);
        uAuth = FirebaseAuth.getInstance();

    }// onCreate

    //method gets called when confirm button is clicked in sign-up activity

    /**
     * This method is called when the sign up button is clicked on
     * This method creates a user object and adds the user to the database(both cloud and authentication)
     *
     * @param view
     */
    public void confirm_signup(View view) {
        final String username = ed_username.getText().toString();
        final String phoneNumber = ed_phoneNumber.getText().toString();
        final String email = ed_email.getText().toString();
        final String password = ed_password.getText().toString();
        String confirmPassword = ed_confirmPassword.getText().toString();

        // verify the user's input
        if (username.length() > 0
                && phoneNumber.length() > 0
                && email.length() > 0
                && password.length() > 0
                && confirmPassword.length() > 0) {

            if (password.length() >= 6) {
                if (password.equals(confirmPassword)) {
                    // if not valid, the func will return
                    //Toast.makeText(getBaseContext(), "Confirm -> iBook Home Page", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(
                            getBaseContext(),
                            "The Confirm Password confirmation does not match",
                            Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
            } else {
                Toast.makeText(getBaseContext(), "Improper password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!phoneIsValid(phoneNumber)) {
                Toast.makeText(getBaseContext(), "Phone number is not valid", Toast.LENGTH_SHORT).show();
                return;
            }

        } else {
            Toast.makeText(getBaseContext(), "Please input full information", Toast.LENGTH_SHORT).show();
            return;
        }


        FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("userName", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    //check if username already exists
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.getResult().isEmpty()) {
                            Toast.makeText(getBaseContext(), "Username exists", Toast.LENGTH_SHORT).show();
                        } else {
                            // make the progress bar visible
                            ed_progressBar.setVisibility(View.VISIBLE);
                            //register the user
                            uAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        MainActivity.database = new Database();
                                        String currentUserID = MainActivity.database.getCurrentUserUID();
                                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd h:mm a");
                                        Date date = new Date();
                                        MainActivity.user = new User(username, password, email, phoneNumber, currentUserID,date);
                                        MainActivity.database.addUser(MainActivity.user);
                                        // store user info to database

                                        Toast.makeText(SignUpActivity.this, "Created user successfully", Toast.LENGTH_SHORT).show();
                                        // go to iBook homepage
                                        Intent intent = new Intent(getApplicationContext(), PageActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // when it goes unsuccessful,
                                        // We show the reason, and restart the sign up activity to let user sign up again.
                                        Toast.makeText(SignUpActivity.this, "Unsuccessful" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    }
                });

    }// confirm_signup

    /**
     * Method called when user clicks cancel button on sign up screen
     * Makes the user return to the log in activity
     *
     * @param view
     */
    public void cancel_signup(View view) {
        //Toast.makeText(getBaseContext(), "Cancel", Toast.LENGTH_SHORT).show();
        finish();
    }// cancel_signup

    /**
    *  Method to check if phone number is valid
    * */
    public boolean phoneIsValid(String phoneNumber) {
        return phoneNumber.matches("[0-9]+") && phoneNumber.length() == 10;
    }


} //Class - SignUpActivity
