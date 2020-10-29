package com.example.ibook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class PageActivity extends AppCompatActivity {

    //Private variables

    private ListView bookList;
    private ArrayAdapter<Book> bookAdapter;
    private ArrayList<Book> bookDataList;
    private SearchView searchBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide the top bar and make it full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar

        //Set activity
        setContentView(R.layout.activity_page);
        

        BottomNavigationView navigationView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_topic,
                R.id.navigation_notifications,
                R.id.navigation_booklist,
                R.id.navigation_user
        ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
//        //Get the list view
//        bookList = findViewById(R.id.bookList);
//
//        //Create the list
//        bookDataList = new ArrayList<>();
//
//        //Create the adapter
//        bookAdapter = new BookList(this, bookDataList);
//
//        //Populate list from database
//        Book newBook = new Book("Watchmen", "Alan Moore, Dave Gibbons", "2014", "Psychologically moving comic book...", "Available", true);
//        Book newBook2 = new Book("The Millionaire Maker", "Loral Langemeier", "2006", "You - A Millionaire? (It's true, and you might be closer than you think.)\n " +
//                "Even financial woes and a limited income can't stop you from creating real wealth and the freedom in buys.", "Available", true);
//        bookAdapter.add(newBook);
//        bookAdapter.add(newBook2);
//        bookAdapter.add(newBook);
//        bookAdapter.add(newBook2);
//        bookAdapter.add(newBook);
//        bookAdapter.add(newBook2);
//        bookAdapter.add(newBook);
//        bookAdapter.add(newBook2);
//
//
//        bookList.setAdapter(bookAdapter);
//
//        //Click camera button
//
//        //Click search button
//        //On click is used to make whole bar clickable and not just the icon
//        searchBar = (SearchView) findViewById(R.id.searchButton);
//        searchBar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                searchBar.setIconified(false);
//            }
//        });
//        //Click book item
//
//        //Navigation Bar
//

    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }// logout
}


/*
Resources:

Android Notes for Professionals


Websites
____________________________
Removing top title bar
Kumar, Manmohan. How do I remove the title bar in android studio? Stack Overflow. Stack Exchange Inc. Mar 10, 2016. License(CC BY-SA).
https://stackoverflow.com/questions/26492522/how-do-i-remove-the-title-bar-in-android-studio

Make search bar fully clickable
Liu, Eric. Custom SearchView whole clickable in android. Stack Overflow. Stack Exchange Inc. Nov 10, 2015. License(CC BY-SA).
https://stackoverflow.com/questions/17670685/custom-searchview-whole-clickable-in-android


 */