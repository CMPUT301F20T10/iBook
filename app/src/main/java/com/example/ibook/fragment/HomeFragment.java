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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/***
 *This fragment contains the view for the home page and
 * the searching functionalities of the
 * searchView.
 */

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

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                resultList = new ArrayList<>();
                bookList = new ArrayList<>();
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
        adapter = new BookListAdapter(datalist, getActivity());
        bookListView.setAdapter(adapter);

        db.collection("books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // todo: change email key word to username
                                datalist.add(document.toObject(Book.class));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "got an error", Toast.LENGTH_SHORT).show();

                        }
                    }
                });


        // view book on the list
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ViewBookActivity.class);
                User user = new User();
                intent.putExtra("BOOK_NUMBER", position);
                intent.putExtra("USER_ID", user.getUserName());
                intent.putExtra("IS_OWNER", -1);
                intent.putExtra("BOOK_ISBN", datalist.get(position).getIsbn());
                startActivityForResult(intent, 0);
            }
        });


        return root;
    }

    /***
     *This method searches the database for the keyword entered
     * in the books collection and users collection.
     * If the keyword appears it adds the book object to a list that gets passed to
     * the activity that shows the results.
     */
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
                                    document.getString("isbn"),
                                    document.getString("owner"));

                            //if book has owner specified add book to resultList
                            resultList.add(book);
                        }
                    }
                });
        //get all books from book collection
        //add to a bookList
        db.collection("books").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot document : task.getResult()) {
                    Book book = new Book(document.getString("title"),
                            document.getString("authors"),
                            document.getString("date"),
                            document.getString("description"),
                            Book.Status.valueOf(document.getString("status")),
                            document.getString("isbn"),
                            document.getString("owner"));

                    bookList.add(book);

                }
                for (Book book : bookList) {
                    String author = book.getAuthor();
                    String desc = book.getDescription();
                    String title = book.getTitle();
                    //make one whole string that contains title author and description
                    String string = author.concat(desc).concat(title).toLowerCase();
                    //if string contains keyword add it to the resultList
                    if (string.contains(query.toLowerCase()) && book.getStatus() != Book.Status.Borrowed && book.getStatus() != Book.Status.Accepted) {
                        resultList.add(book);

                    }
                }
                if (resultList.isEmpty()) {
                    Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
                } else {
                    //pass resultList to SearchBooksActivity for adapter to display
                    Intent intent = new Intent(getContext(), SearchedBooksActivity.class);
                    intent.putExtra("books", resultList);
                    startActivity(intent);
                }
                searchProgressBar.setVisibility(View.GONE);

            }
        });
    }

    public Book.Status from_string_to_enum(String input) {
        if (input.equals("Available"))
            return Book.Status.Available;

        if (input.equals("Available"))
            return Book.Status.Available;

        if (input.equals("Available"))
            return Book.Status.Available;

        if (input.equals("Available"))
            return Book.Status.Available;
        // todo: change later
        return Book.Status.Available;
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
