package com.example.ibook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ibook.R;
import com.example.ibook.entities.Database;
import com.example.ibook.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {

    EditText usernameEditText;
    EditText passwordEditText;
    private FirebaseAuth uAuth;
    private ProgressBar signInProgressBar;
    public static Database database;
    public static User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Hide the top bar and make it full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar

        setContentView(R.layout.activity_login);

        signInProgressBar = findViewById(R.id.progressBar);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        setupSignInListener();
        setupSignUpListener();

    }// onCreate

    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        usernameEditText.setText(null);
        passwordEditText.setText(null);
    }// onResume

    /**
     * This method listens for click on sign-in button and then checks if the user input is valid
     * and if it exists in database or not
     */
    public void setupSignInListener() {
        final Button signInButton = findViewById(R.id.loginIn);

        signInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                signInProgressBar.setVisibility(View.VISIBLE);

                database = new Database();
                uAuth = database.getuAuth();

                if (valid(username, password)) {
                    // TODO: Go to Home Page
                    uAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();

                                //create a user object of existing user by loading info from database
                                createUserObject();

                                Intent intent = new Intent(getApplicationContext(), PageActivity.class);
                                intent.putExtra("curr_username", username);
                                startActivity(intent);
                            }// if
                            else {
                                Toast.makeText(MainActivity.this, "Unsuccessful" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                signInProgressBar.setVisibility(GONE);
                                return;
                            }// else
                        }// onComplete
                    });
                }// outer if
                else {
                    signInProgressBar.setVisibility(GONE);
                    Toast.makeText(getBaseContext(), "Invalid Input", Toast.LENGTH_LONG).show();
                }// else
            }// onClick
        }); // onClickListener
    }// setupSignInListener

    /**
     *
     * @param username - the username that the userInput while logging in (the var should actually
     *                 be called emailId --> will fix later)
     * @param password -
     * @return
     */
    public Boolean valid(String username, String password) {

        if (username.equals("1") && password.equals("1")) {
            uAuth.signInWithEmailAndPassword("yzhang24@gmail.com", "123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), PageActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Unsuccessful" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        signInProgressBar.setVisibility(GONE);
                        return;
                    }

                }
            });
            return false;
        }
        /**/

        if (username.equals("") || password.equals((""))) {
            return false;
        }// if
        else {
            return true;
        }// else
    }// valid

    /**
     * This method sets up the listener for signup button and takes the user to the signup activity
     */
    public void setupSignUpListener() {
        TextView signUpButton = findViewById(R.id.signUp);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Clicked on Sign Up!");
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
                // TODO: Go to Sign Up Page

            }// onClick
        }); // onClickListener
    }// setupSignUpListener

    /**
     * This method gets the user object from the database for the current user upon logging in
     */
    public void createUserObject(){

        //load data from database for existing user
        database.getUserDocumentReference().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    //user object intialized
                    user = documentSnapshot.toObject(User.class);

                }// if
            }//onSuccess
        });
    }//createUserObject

}// Database Class