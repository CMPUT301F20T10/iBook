package com.example.ibook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.ibook.BookListAdapter;
import com.example.ibook.R;
import com.example.ibook.activities.MainActivity;
import com.example.ibook.activities.SearchResultsActivity;
import com.example.ibook.entities.Book;
import com.example.ibook.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/***
 *This fragment contains the view for the home page and
 * the searching functionalities of the
 * searchView.
 */

public class HomeFragment extends Fragment {

    //Private variables
    private RecyclerView bookListView;
    private BookListAdapter adapter;
    private ArrayList<Book> datalist;
    private SearchView searchBar;
    private ArrayList<Book> resultList;
    private ArrayList<Book> bookList;
    private ArrayList<User> userList;
    private FirebaseFirestore db;
    private ProgressBar searchProgressBar;
    boolean searchBarClosed = true;

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
        searchBarClosed = true;

        //Makes fully clickable
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If its not been fully closed then we don't do anything
                if (searchBarClosed) {
                    searchBar.setIconified(false);
                }
            }
        });

        searchBar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //Let it be fully clickable so we can display it properly
                searchBarClosed = true;
                return false;
            }
        });

        // search functionality
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                resultList = new ArrayList<>();
                bookList = new ArrayList<>();
                userList = new ArrayList<>();
                searchProgressBar.setVisibility(View.VISIBLE);
                searchData(query);
                searchBar.clearFocus(); //Fixes enter key pressed down and up on keyboard
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        bookListView.setLayoutManager(manager);
        bookListView.setHasFixedSize(true);
        adapter = new BookListAdapter(datalist, getActivity());
        bookListView.setAdapter(adapter);

        return root;
    }

    /***
     *This method searches the database for the keyword entered
     * in the books collection and users collection.
     * If the keyword appears it adds the book object to a list that gets passed to
     * the activity that shows the results.
     */
    public void searchData(final String query) {
        //wait for method to complete

        //get all books from book collection
        //add to a bookList
        //searches for userName that matches query
        MainActivity.database
                .getDb()
                .collection("users")
                .whereEqualTo("userName", query)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentReference : task.getResult()) {
                            userList.add(documentReference.toObject(User.class));
                            Log.d("number", userList.size() + "");
                        }
                    }
                })
                // search for the book
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        MainActivity.database
                                .getDb()
                                .collection("books")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                            Book book = documentSnapshot.toObject(Book.class);
                                            if (book.getAuthors().toLowerCase().contains(query.toLowerCase())
                                                    || book.getTitle().toLowerCase().contains(query.toLowerCase())
                                                    || book.getDescription().toLowerCase().contains(query.toLowerCase())
                                            ) {
                                                if (book.getStatus() != Book.Status.Borrowed
                                                        && book.getStatus() != Book.Status.Accepted) {
                                                    resultList.add(book);
                                                }
                                            }
                                        }
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (resultList.isEmpty() && userList.isEmpty()) {
                                    Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
                                } else {
                                    //pass resultList and userList to SearchBooksActivity for adapter to display
                                    Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
                                    intent.putExtra("books", resultList);
                                    intent.putExtra("users", userList);

                                    intent.putExtra("query",query);
                                    searchProgressBar.setVisibility(View.GONE);
                                    startActivity(intent);
                                }
                                searchProgressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        datalist = new ArrayList<>();
        db.collection("books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                datalist.add(document.toObject(Book.class));
                                //Toast.makeText(getContext(), String.valueOf(datalist.size()), Toast.LENGTH_SHORT).show();
                            }
                            //adapter.notifyDataSetChanged();
                            adapter = new BookListAdapter(datalist, getActivity());
                            bookListView.setAdapter(adapter);
                        } else {
                            Toast.makeText(getContext(), "got an error", Toast.LENGTH_SHORT).show();
                        }
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