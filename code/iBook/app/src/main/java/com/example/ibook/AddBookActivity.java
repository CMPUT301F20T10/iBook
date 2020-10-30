package com.example.ibook;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AddBookActivity extends AppCompatActivity {

    EditText bookNameEditText;
    EditText authorEditText;
    EditText dateEditText;
    EditText isbnEditText;
    Button cancelButton;
    Button addButton;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_book);

        bookNameEditText = findViewById(R.id.editTextBookName);
        authorEditText = findViewById(R.id.editTextAuthor);
        dateEditText = findViewById(R.id.editTextDate);
        isbnEditText = findViewById(R.id.editTextISBN);

        cancelButton = findViewById(R.id.cancelButton);
        addButton = findViewById(R.id.addButton);
        imageView = findViewById(R.id.imageView);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("cancel button clicked");
                //TODO:
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("image view clicked");
                // TODO: Open gallery or camera
            }
        });


    }

}
