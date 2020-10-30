package com.example.ibook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ibook.Book;
import com.example.ibook.R;
import com.example.ibook.User;

public class ViewBookActivity extends AppCompatActivity {
    private User user;
    private Book book;

    private TextView bookNameTextView;
    private TextView authorTextView;
    private TextView dateTextView;
    private TextView isbnTextView;
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        this.book = (Book) intent.getSerializableExtra("BOOK");
        this.user = (User) intent.getSerializableExtra("USER");

        setContentView(R.layout.activity_view_book);

        bookNameTextView = findViewById(R.id.ViewBookName);
        authorTextView = findViewById(R.id.ViewAuthor);
        dateTextView = findViewById(R.id.ViewDate);
        isbnTextView = findViewById(R.id.ViewISBN);

        bookNameTextView.setText(book.getTitle());
        authorTextView.setText(book.getAuthor());
        dateTextView.setText(book.getDate());
        isbnTextView.setText(book.getIsbn());

        //TODO !: imageView

        //TODO 2: ViewBookActivity with 4 kinds of interaction

        //TODO 3: ViewBookActivity conducted by the owner of the book
    }
}
