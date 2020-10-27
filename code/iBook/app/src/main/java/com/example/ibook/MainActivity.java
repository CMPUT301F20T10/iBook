 package com.example.ibook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText usernameEditText;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setupSignInListener();
        setupSignUpListener();

    }// onCreate

    public void setupSignInListener() {
        Button signInButton = findViewById(R.id.signIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                usernameEditText = findViewById(R.id.usernameEditText);
                passwordEditText = findViewById(R.id.passwordEditText);
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (valid(username, password)) {

                    // TODO: Go to Home Page
                }// if

                // this following else condition will actually be in validate function
                // since a different message should be displayed for different errors:
                // for example - if password left empty -> it should say "please enter password"
                // and if not in db, then should say user doesn't exist etc.
                else {
                    Toast.makeText(getBaseContext(), "Invalid Input", Toast.LENGTH_LONG).show();
                }// else


            }// onClick
        }); // onClickListener
    }// setupSignInListener

    public Boolean valid(String username, String password) {
        // TODO: Validation Code Goes Here

        return true; // dummy

    }// valid

    public void setupSignUpListener() {
        Button signInButton = findViewById(R.id.signUp);
        signInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                System.out.println("Clicked on Sign Up!");
                // TODO: Go to Sign Up Page

            }// onClick
        }); // onClickListener
    }

}