package com.example.ibook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.ibook.BookListAdapter;
import com.example.ibook.R;
import com.example.ibook.activities.SearchedBooksActivity;
import com.example.ibook.activities.ViewBookActivity;
import com.example.ibook.entities.Book;
import com.example.ibook.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



public class HomeFragment extends Fragment {

    //Private variables
    private ListView bookListView;
    private BookListAdapter adapter;
    private ArrayList<Book> datalist;
    private SearchView searchBar;
    private ArrayList<Book> resultList;
    private ArrayList<Book> bookList;
    private FirebaseFirestore db;
    private ProgressBar searchProgressBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Set up the view
        db = FirebaseFirestore.getInstance();
        bookListView = root.findViewById(R.id.bookList);
        searchBar = root.findViewById(R.id.searchButton);
        searchProgressBar = root.findViewById(R.id.progressBar);

        datalist = new ArrayList<>();

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                resultList = new ArrayList<>();
                bookList = new ArrayList<>();
                searchProgressBar.setVisibility(View.VISIBLE);
                searchData(query);
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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


        // view book on the list
//        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getContext(), ViewBookActivity.class);
//                User user = new User();
//                intent.putExtra("BOOK_NUMBER", position);
//                intent.putExtra("USER_ID", user.getUserID());
//                startActivityForResult(intent, 0);
//            }
//        });

        return root;
    }

    public void searchData(final String query) {
        //searches for owner
        db.collection("books").whereEqualTo("owner", query)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot document : task.getResult()) {

                            Book book = new Book(document.getString("title"),
                                    document.getString("authors"),
                                    document.getString("date"),
                                    document.getString("description"),
                                    Book.Status.valueOf(document.getString("status")),
                                    document.getString("isbn"));

                            resultList.add(book);
                        }
                    }
                });
        //searches for keyword in title, author or description
        db.collection("books").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot document : task.getResult()) {

                    Book book = new Book(document.getString("title"),
                            document.getString("authors"),
                            document.getString("date"),
                            document.getString("description"),
                            Book.Status.valueOf(document.getString("status")),
                            document.getString("isbn"));

                    bookList.add(book);

                }
                for (Book book : bookList) {
                    String author = book.getAuthor();
                    String desc = book.getDescription();
                    String title = book.getTitle();
                    //make one whole string that contains title author and description
                    String string = author.concat(desc).concat(title).toLowerCase();

                    if (string.contains(query.toLowerCase()) && book.getStatus() != Book.Status.Borrowed && book.getStatus() != Book.Status.Accepted) {
                        resultList.add(book);

                    }
                }
                if (resultList.isEmpty()){
                    Toast.makeText(getContext(),"No results found", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getContext(), SearchedBooksActivity.class);
                    intent.putExtra("books", resultList);
                    startActivity(intent);
                }
                searchProgressBar.setVisibility(View.GONE);

            }
        });
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