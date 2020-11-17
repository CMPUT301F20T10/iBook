package com.example.ibook.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ibook.BookListAdapter;
import com.example.ibook.R;
import com.example.ibook.activities.MainActivity;
import com.example.ibook.activities.ViewBookActivity;
import com.example.ibook.entities.Book;

import java.util.ArrayList;

public class BookListSectionFragment extends Fragment {

    private ArrayList<Book> bookList;
    private ListView listView;
    private BookListAdapter adapter;
    private String title;

    public BookListSectionFragment(String title) {
        this.title = title;
    }

    @Override
    public void onResume() {
        super.onResume();
        getBookList();
        adapter = new BookListAdapter(bookList, getContext());
        listView.setAdapter(adapter);
        Log.d("", title);
    }

    private void getBookList() {
        if (title.equals("Own")) {
            bookList = MainActivity.user.getOwnBookList();
        } else if (title.equals("Borrow")) {
            bookList = MainActivity.user.getBorrowedBookList();
        } else if (title.equals("Accept")) {
            bookList = MainActivity.user.getAcceptBookList();
        } else {
            bookList = MainActivity.user.getRequestedBookList();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_booklist_section, container, false);
        listView = root.findViewById(R.id.bookList);
        getBookList();
        adapter = new BookListAdapter(bookList, getContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ViewBookActivity.class);
                intent.putExtra("USER_ID", MainActivity.database.getCurrentUserUID());
                intent.putExtra("BOOK_NUMBER", position);
                startActivityForResult(intent, 0);
            }
        });

        return root;
    }
}