package com.example.ibook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ibook.Book;
import com.example.ibook.BookListAdapter;
import com.example.ibook.R;

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
        bookListView = root.findViewById(R.id.bookList);
//        searchBar = (SearchView) root.findViewById(R.id.searchButton);
        datalist = new ArrayList<>();

        Book newBook = new Book("Watchmen", "Alan Moore, Dave Gibbons", "2014", "Psychologically moving comic book...", "Available", true);
        Book newBook2 = new Book("The Millionaire Maker", "Loral Langemeier", "2006", "You - A Millionaire? (It's true, and you might be closer than you think.)\n " +
                "Even financial woes and a limited income can't stop you from creating real wealth and the freedom in buys.", "Available", true);
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

//        searchBar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                searchBar.setIconified(false);
//            }
//        });

        return root;
    }




}
