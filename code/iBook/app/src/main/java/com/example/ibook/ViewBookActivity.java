package com.example.ibook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ViewBookActivity extends AppCompatActivity {
    private User user;
    private Book book;

    TextView bookNameTextView;
    TextView authorTextView;
    TextView dateTextView;
    TextView isbnTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_book);

        Intent intent = getIntent();
        this.book = (Book) intent.getSerializableExtra("BOOK");
        this.user = (User) intent.getSerializableExtra("USER");

        bookNameTextView = findViewById(R.id.ViewBookName);
        authorTextView = findViewById(R.id.ViewAuthor);
        dateTextView = findViewById(R.id.ViewDate);
        isbnTextView = findViewById(R.id.ViewISBN);

        bookNameTextView.setText(book.getTitle());
        authorTextView.setText(book.getAuthor());
        dateTextView.setText(book.getDate());
        isbnTextView.setText(book.getIsbn());
    }
}
