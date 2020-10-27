package com.example.ibook;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

  private EditText ed_username;
  private EditText ed_password;
  private EditText ed_email;
  private EditText ed_phoneNumber;
  private EditText ed_confirmPassword;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_up);
    ed_username = findViewById(R.id.ed_username_signup);
    ed_email = findViewById(R.id.ed_email_signup);
    ed_password = findViewById(R.id.ed_password_signup);
    ed_phoneNumber = findViewById(R.id.ed_phoneNumber_signup);
    ed_confirmPassword = findViewById(R.id.ed_confirmPassword_signup);
    // Toast.makeText(getBaseContext(), "SignUp to be done", Toast.LENGTH_SHORT).show();
  }

  public void confirm_signup(View view) {
    String username = ed_username.getText().toString();
    String phoneNumber = ed_phoneNumber.getText().toString();
    String email = ed_email.getText().toString();
    String password = ed_password.getText().toString();
    String confirmPassword = ed_confirmPassword.getText().toString();
    if (username.length() > 0
        && phoneNumber.length() > 0
        && email.length() > 0
        && password.length() > 0
        && confirmPassword.length() > 0) {
      if (password.length() >= 5) {
        if (password.equals(confirmPassword)) {
          Toast.makeText(getBaseContext(), "Confirm -> iBook Home Page", Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(
                  getBaseContext(),
                  "The Confirm Password confirmation does not match",
                  Toast.LENGTH_SHORT)
              .show();
        }
      } else {
        Toast.makeText(getBaseContext(), "Improper passward", Toast.LENGTH_SHORT).show();
      }

      // Toast.makeText(getBaseContext(), "Confirm -> iBook Home Page", Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(getBaseContext(), "Please input full information", Toast.LENGTH_SHORT).show();
    }
  }

  public void cancel_signup(View view) {
    //Toast.makeText(getBaseContext(), "Cancel", Toast.LENGTH_SHORT).show();
    finish();
  }
}
