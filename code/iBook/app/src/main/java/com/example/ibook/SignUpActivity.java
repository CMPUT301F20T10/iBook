package com.example.ibook;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toast.makeText(getBaseContext(), "SignUp to be done", Toast.LENGTH_SHORT).show();

    }

    public void confirm_signup(View view) {
        Toast.makeText(getBaseContext(), "Confirm", Toast.LENGTH_SHORT).show();
    }

    public void cancel_signup(View view) {
        Toast.makeText(getBaseContext(), "Cancel", Toast.LENGTH_SHORT).show();
    }

}