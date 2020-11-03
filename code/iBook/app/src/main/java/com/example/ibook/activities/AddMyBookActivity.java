package com.example.ibook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ibook.entities.Book;
import com.example.ibook.R;
import com.example.ibook.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AddMyBookActivity extends AppCompatActivity {
    private User user;
    private Book book;
    private EditText bookNameEditText;
    private EditText authorEditText;
    private EditText dateEditText;
    private EditText isbnEditText;
    private Button cancelButton;
    private Button addButton;
    private ImageView imageView;
    private FirebaseFirestore db;
    private String userID;
    private String userName;
    private ArrayList<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_screen);
        bookNameEditText = findViewById(R.id.editTextBookName);
        authorEditText = findViewById(R.id.editTextAuthor);
        dateEditText = findViewById(R.id.editTextDate);
        isbnEditText = findViewById(R.id.editTextISBN);

        cancelButton = findViewById(R.id.cancelButton);
        addButton = findViewById(R.id.addButton);
        imageView = findViewById(R.id.imageView);

        db = FirebaseFirestore.getInstance();
        books = new ArrayList<>();

        Intent intent = getIntent();
        userName = intent.getStringExtra("curr_username");

        userName = "yzhang24@gmail.com";

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String bookName = bookNameEditText.getText().toString();
                final String authorName = authorEditText.getText().toString();
                final String date = dateEditText.getText().toString();
                final String isbn = isbnEditText.getText().toString();
                //photo // todo: upload photo for the book

                if (bookName.length() > 0
                        && authorName.length() > 0
                        && date.length() > 0
                        && isbn.length() > 0) {
                    db.collection("users")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            // todo: change email key word to username
                                            if(document.getData().get("email").equals(userName)){
                                                Toast.makeText(getBaseContext(), "findMatch", Toast.LENGTH_SHORT).show();
                                                userID = document.getId();
                                                Map<String,Object> data = new HashMap();
                                                data = document.getData();
                                                books = (ArrayList<Book>)document.getData().get("BookList");
                                                books.add(new Book(bookName, authorName, date, isbn));
                                                data.put("BookList",books);
                                                db.collection("users")
                                                        .document(userID).set(data);
                                                Toast.makeText(getBaseContext(), "Add book successfully!", Toast.LENGTH_LONG).show();
                                                finish();
                                            }

                                        }
                                    } else {
                                        Toast.makeText(getBaseContext(), "got an error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                } else {
                    Toast.makeText(getBaseContext(), "Please input full information", Toast.LENGTH_SHORT).show();
                }

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Image!", Toast.LENGTH_SHORT).show();

            }
        });


    }
}