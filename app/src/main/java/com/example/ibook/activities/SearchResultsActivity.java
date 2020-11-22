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

import com.example.ibook.BookListAdapter;
import com.example.ibook.R;
import com.example.ibook.UserListAdapter;
import com.example.ibook.entities.Book;
import com.example.ibook.entities.User;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

/***
 *This activity displays the results for the search results in a list view.
 * When the items are clicked on the book info is passed to the view
 * book activity and displayed there.
 */
public class SearchResultsActivity extends AppCompatActivity {
    private BookListAdapter adapter;
    private ArrayList<Book> bookList = new ArrayList<>();
    private ArrayList<User> userList = new ArrayList<>();
    private ListView listView;
    private RadioGroup radioGroup;
    private Button backButton;

    // for color change
    private RadioButton book;
    private RadioButton user;

    //list view getter
    public ListView getListView() {
        return listView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide the top bar and make it full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar

        setContentView(R.layout.activity_searched_books);
        listView = findViewById(R.id.listView);
        backButton = findViewById(R.id.cancelButton);


        bookList = (ArrayList<Book>) getIntent().getSerializableExtra("books");
        userList = (ArrayList<User>) getIntent().getSerializableExtra("users");
        adapter = new BookListAdapter(bookList, getApplicationContext());
        listView.setAdapter(adapter);


        // set toggle buttons
        radioGroup = findViewById(R.id.selectState);
        book = findViewById(R.id.search_book);
        user = findViewById(R.id.search_user);
        setUpListListener();


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                if (radioButton.getText().toString().equals("Books")) {
                    book.setTextColor(Color.WHITE);
                    user.setTextColor(Color.parseColor("#FF9900"));

                    BookListAdapter adapter = new BookListAdapter(bookList, getApplicationContext());
                    listView.setAdapter(adapter);
                    setUpListListener();
                }
                if (radioButton.getText().toString().equals("Users")) {
                    user.setTextColor(Color.WHITE);
                    book.setTextColor(Color.parseColor("#FF9900"));
                    UserListAdapter adapter = new UserListAdapter(userList, getApplicationContext());
                    listView.setAdapter(adapter);
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
        if (radioGroup.getCheckedRadioButtonId() == R.id.search_book) {
            //click on list view item
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    User user = new User();
                    Intent intent = new Intent(getApplicationContext(), ViewBookActivity.class);
                    intent.putExtra("USER_ID", MainActivity.database.getCurrentUserUID());
                    intent.putExtra("BOOK_NUMBER", position);
                    intent.putExtra("IS_OWNER", -1);
                    intent.putExtra("BOOK_ISBN", bookList.get(position).getIsbn());
                    startActivityForResult(intent, 0);
                }
            });
        } else {//click on list view item
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
}