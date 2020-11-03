package com.example.ibook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {

    EditText usernameEditText;
    EditText passwordEditText;
    private FirebaseAuth uAuth;
    private ProgressBar signInProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        uAuth = FirebaseAuth.getInstance();
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


    public void setupSignInListener() {
        final Button signInButton = findViewById(R.id.signIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                signInProgressBar.setVisibility(View.VISIBLE);

                if (valid(username, password)) {

                    // TODO: Go to Home Page
                    uAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), PageActivity.class);
                                intent.putExtra("curr_username", username);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "Unsuccessful" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                signInProgressBar.setVisibility(GONE);
                                return;
                            }

                        }
                    });

                    // Intent intent = new Intent(MainActivity.this, PageActivity.class);
                    //startActivity(intent);
                }// if

                // this following else condition will actually be in validate function
                // since a different message should be displayed for different errors:
                // for example - if password left empty -> it should say "please enter password"
                // and if not in db, then should say user doesn't exist etc.
                else {
                    signInProgressBar.setVisibility(GONE);
                    Toast.makeText(getBaseContext(), "Invalid Input", Toast.LENGTH_LONG).show();
                }// else


            }// onClick
        }); // onClickListener
    }// setupSignInListener

    public Boolean valid(String username, String password) {
        /*
         *  convenience for testing
         * */
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

    public void setupSignUpListener() {
        Button signInButton = findViewById(R.id.signUp);
        signInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Clicked on Sign Up!");
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
                // TODO: Go to Sign Up Page

            }// onClick
        }); // onClickListener
    }
    //public void us

}