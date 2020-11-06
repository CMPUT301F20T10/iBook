package com.example.ibook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ibook.R;
import com.example.ibook.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

  private EditText ed_username;
  private EditText ed_password;
  private EditText ed_email;
  private EditText ed_phoneNumber;
  private EditText ed_confirmPassword;
  private ProgressBar ed_progressBar;
  private FirebaseAuth uAuth; // user authentication

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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
  public void confirm_signup(View view) {
    final String username = ed_username.getText().toString();
    final String phoneNumber = ed_phoneNumber.getText().toString();
    final String email = ed_email.getText().toString();
    final String password = ed_password.getText().toString();
    String confirmPassword = ed_confirmPassword.getText().toString();

    // verifying the user's input
    if (username.length() > 0
        && phoneNumber.length() > 0
        && email.length() > 0
        && password.length() > 0
        && confirmPassword.length() > 0) {
      if (password.length() >= 6) {
        if (password.equals(confirmPassword)) {
          Toast.makeText(getBaseContext(), "Confirm -> iBook Home Page", Toast.LENGTH_SHORT).show();
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

      // Toast.makeText(getBaseContext(), "Confirm -> iBook Home Page", Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(getBaseContext(), "Please input full information", Toast.LENGTH_SHORT).show();
      return;
    }

    // make the progressbar visible
    ed_progressBar.setVisibility(View.VISIBLE);

    //register the user
    uAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
      @Override
      public void onComplete(@NonNull Task<AuthResult> task) {
        if(task.isSuccessful()){
          User user = new User(username, password, email, phoneNumber);

          // store user info to database
          user.commit();
          Toast.makeText(SignUpActivity.this, "Created user successfully", Toast.LENGTH_SHORT).show();

          // go to iBook homepage
          Intent intent = new Intent(getApplicationContext(),PageActivity.class);
          startActivity(intent);
        }
        else{
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


  public void cancel_signup(View view) {
    //Toast.makeText(getBaseContext(), "Cancel", Toast.LENGTH_SHORT).show();
    finish();
  }
}
