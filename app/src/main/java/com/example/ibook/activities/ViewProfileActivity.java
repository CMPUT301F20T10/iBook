package com.example.ibook.activities;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.ibook.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ViewProfileActivity extends AppCompatActivity {
    private TextView name;
    private TextView email;
    private TextView phone;
    private Button backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide the top bar and make it full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar

        setContentView(R.layout.fragment_view_profile);
        email = findViewById(R.id.emailID);
        name = findViewById(R.id.username);
        phone = findViewById(R.id.phoneNumber);
        backButton = findViewById(R.id.cancelButton);

        email.setText(getIntent().getStringExtra("EMAIL"));
        phone.setText(getIntent().getStringExtra("PHONE"));
        name.setText(getIntent().getStringExtra("NAME"));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
