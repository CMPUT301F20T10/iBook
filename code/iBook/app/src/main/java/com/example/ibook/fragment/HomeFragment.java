package com.example.ibook.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ibook.Book;
import com.example.ibook.BookListAdapter;
import com.example.ibook.R;
import com.example.ibook.User;
import com.example.ibook.ViewBookActivity;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    //Private variables
    private ListView bookListView;
    private BookListAdapter adapter;
    private ArrayList<Book> datalist;
    private SearchView searchBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Set up the view
        bookListView = root.findViewById(R.id.bookList);
        searchBar = (SearchView) root.findViewById(R.id.searchButton);

        datalist = new ArrayList<>();


        // TODO: transfer into the database
        Book newBook = new Book("Watchmen", "Alan Moore, Dave Gibbons", "2014", "Psychologically moving comic book...", Book.Status.Available, "temp isbn 1");

        Book newBook2 = new Book("The Millionaire Maker", "Loral Langemeier", "2006", "You - A Millionaire? (It's true, and you might be closer than you think.)\n " +
                "Even financial woes and a limited income can't stop you from creating real wealth and the freedom in buys.", Book.Status.Available, "temp isbn 2");
        datalist.add(newBook2);
        datalist.add(newBook);
        datalist.add(newBook2);
        datalist.add(newBook);
        datalist.add(newBook2);
        datalist.add(newBook);
        datalist.add(newBook2);
        datalist.add(newBook);
        adapter = new BookListAdapter(datalist, getActivity());
        bookListView.setAdapter(adapter);



        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = new User();
                Intent intent = new Intent(getContext(), ViewBookActivity.class);
                intent.putExtra("BOOK", (Book) parent.getItemAtPosition(position));
                intent.putExtra("USERNAME", user.getUserName());
                startActivity(intent);
            }
        });
        /*

        * */
//        searchBar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                searchBar.setIconified(false);
//            }
//        });

        return root;
    }


}


/*
 * Resources:
 * Android Notes for Professionals
 *
 * Websites:
 *
 * Make search bar fully clickable
 * Liu, Eric. Custom SearchView whole clickable in android. Stack Overflow. Stack Exchange Inc. Nov 10, 2015. License(CC BY-SA).
 * https://stackoverflow.com/questions/17670685/custom-searchview-whole-clickable-in-android
 *
 *
 * */