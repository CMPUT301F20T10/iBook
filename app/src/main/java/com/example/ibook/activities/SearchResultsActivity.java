package com.example.ibook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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


        bookList = (ArrayList<Book>)getIntent().getSerializableExtra("books");
        userList = (ArrayList<User>)getIntent().getSerializableExtra("users");
        adapter = new BookListAdapter(bookList,getApplicationContext());
        listView.setAdapter(adapter);

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

        // set toggle buttons
        radioGroup = findViewById(R.id.selectState);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                if(radioButton.getText().toString().equals("Books")){
                    BookListAdapter adapter = new BookListAdapter(bookList,getApplicationContext());
                    listView.setAdapter(adapter);
                }
                if(radioButton.getText().toString().equals("Users")){
                    UserListAdapter adapter = new UserListAdapter(userList,getApplicationContext());
                    listView.setAdapter(adapter);
                }
            }
        });



    }
}