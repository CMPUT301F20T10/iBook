package com.example.ibook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ibook.BookListAdapter;
import com.example.ibook.R;
import com.example.ibook.entities.Book;
import com.example.ibook.entities.User;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class SearchedBooksActivity extends AppCompatActivity {
    private BookListAdapter adapter;
    private ArrayList<Book> resultList = new ArrayList<>();
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_books);
        listView = findViewById(R.id.listView);


        resultList = (ArrayList<Book>)getIntent().getSerializableExtra("books");
        System.out.println(resultList);
        adapter = new BookListAdapter(resultList,getApplicationContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = new User();
                Intent intent = new Intent(getApplicationContext(), ViewBookActivity.class);
                intent.putExtra("USER_ID", MainActivity.database.getCurrentUserUID());
                intent.putExtra("BOOK_NUMBER", position);
                intent.putExtra("IS_OWNER", -1);
                intent.putExtra("BOOK_ISBN", resultList.get(position).getIsbn());
                startActivityForResult(intent, 0);
            }
        });



    }
}