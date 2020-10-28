package com.example.ibook;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ViewBookFragment extends AppCompatActivity {
    private User user;
    private Book book;

    public ViewBookFragment(User user, Book book) {
        this.user = user;
        this.book = book;
    }

    public ViewBookFragment(int contentLayoutId, User user, Book book) {
        super(contentLayoutId);
        this.user = user;
        this.book = book;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_book);


    }
}
