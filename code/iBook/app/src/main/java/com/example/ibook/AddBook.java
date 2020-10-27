package com.example.ibook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class AddBook extends AppCompatActivity {

    EditText bookNameEditText;
    EditText authorEditText;
    EditText dateEditText;
    EditText isbnEditText;
    Button editButton;
    Button deleteButton;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_screen);

        bookNameEditText = findViewById(R.id.editTextBookName);
        authorEditText = findViewById(R.id.editTextAuthor);
        dateEditText = findViewById(R.id.editTextDate);
        isbnEditText = findViewById(R.id.editTextISBN);

        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        imageView = findViewById(R.id.imageView);

        clickImageView();
        clickEditButton();
        clickDeleteButton();

    }

    public void clickImageView() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("image view clicked");
                // TODO: Open gallery or camera
            }

        });
    }

    public void clickEditButton() {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("edit button clicked");
                //TODO:
            }

        });
    }

    public void clickDeleteButton() {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("delete button clicked");
                //TODO: Delete book
            }

        });
    }
}