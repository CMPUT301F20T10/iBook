package com.example.ibook.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.ibook.BookListAdapter;
import com.example.ibook.R;
import com.example.ibook.UserListAdapter;
import com.example.ibook.entities.Book;
import com.example.ibook.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/***
 *This activity displays the results for the search results in a list view.
 * When the items are clicked on the book info is passed to the view
 * book activity and displayed there.
 */
public class SearchResultsActivity extends AppCompatActivity {
    private BookListAdapter booksAdapter;
    private ArrayList<Book> bookList = new ArrayList<>();
    private ArrayList<User> userList = new ArrayList<>();
    private RecyclerView bookListView;
    private ListView userListView;
    private RadioGroup radioGroup;
    private Button backButton;
    private TextView noResultText;

    // for color change
    private RadioButton book;
    private RadioButton user;
    private String query;

    //list view getter
    public RecyclerView getListView() {
        return bookListView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide the top bar and make it full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar

        setContentView(R.layout.activity_searched_books);
        //listView = findViewById(R.id.listView);
        bookListView = findViewById(R.id.searchedBooksView);
        userListView = findViewById(R.id.searchedUsersView);
        backButton = findViewById(R.id.cancelButton);
        noResultText = findViewById(R.id.noResultTextView);

        //get results from home fragment
        Intent intent = getIntent();
        query = intent.getStringExtra("query");
        bookList = (ArrayList<Book>) getIntent().getSerializableExtra("books");
        userList = (ArrayList<User>) getIntent().getSerializableExtra("users");

        if (bookList.isEmpty()) {
            noResultText.setVisibility(View.VISIBLE);
        }else {
            noResultText.setVisibility(View.GONE);
        }

        bookListView.setVisibility(View.VISIBLE);
        userListView.setVisibility(View.GONE);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        bookListView.setLayoutManager(manager);
        bookListView.setHasFixedSize(true);
        booksAdapter = new BookListAdapter(bookList, getApplicationContext());
        bookListView.setAdapter(booksAdapter);


        radioGroup = findViewById(R.id.selectState);
        book = findViewById(R.id.search_book);
        user = findViewById(R.id.search_user);
        setUpListListener();

        // set toggle buttons
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                if (radioButton.getText().toString().equals("Books")) {

                    updateBookResults();

                    book.setTextColor(Color.WHITE);
                    user.setTextColor(Color.parseColor("#FF9900"));



                    bookListView.setVisibility(View.VISIBLE);
                    userListView.setVisibility(View.GONE);

                    //setUpListListener();
                }
                if (radioButton.getText().toString().equals("Users")) {
                    if (userList.isEmpty()) {
                        noResultText.setVisibility(View.VISIBLE);
                    }else {
                        noResultText.setVisibility(View.GONE);
                    }
                    bookListView.setVisibility(View.GONE);
                    userListView.setVisibility(View.VISIBLE);
                    user.setTextColor(Color.WHITE);
                    book.setTextColor(Color.parseColor("#FF9900"));
                    UserListAdapter adapter = new UserListAdapter(userList, getApplicationContext());
                    userListView.setAdapter(adapter);
                    setUpListListener();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setUpListListener() {
        if (radioGroup.getCheckedRadioButtonId() != R.id.search_book) {
            userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), ViewProfileActivity.class);
                    User user = userList.get(position);
                    intent.putExtra("NAME", user.getUserName());
                    intent.putExtra("EMAIL", user.getEmail());
                    intent.putExtra("PHONE", user.getPhoneNumber());
                    startActivity(intent);
                }
            });
        }
    }

    // show searching result by a list
    public void updateBookResults(){
        bookList= new ArrayList<>();
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
                                    bookList.add(book);
                                }
                            }
                        }
                        if (bookList.isEmpty()) {
                            noResultText.setVisibility(View.VISIBLE);
                        }else {
                            noResultText.setVisibility(View.GONE);
                        }
                        booksAdapter = new BookListAdapter(bookList, getApplicationContext());
                        bookListView.setAdapter(booksAdapter);

                    }
                });

        }

    @Override
    public void onResume() {
        super.onResume();
        updateBookResults();
    }


}