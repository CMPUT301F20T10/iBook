package com.example.ibook.activities;

import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.ibook.entities.Book;
import com.example.ibook.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


/**
 * The activity class for the pages, it controls the navigation bar and its four pages
 */
public class PageActivity extends AppCompatActivity {

    //Private variables
    private ListView bookList;
    private ArrayAdapter<Book> bookAdapter;
    private ArrayList<Book> bookDataList;
    private SearchView searchBar;
    private String username;

    /**
     * The onCreate method when activity is creating
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide the top bar and make it full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar

        //Set activity
        setContentView(R.layout.activity_page);

        // Set the navigation view
        BottomNavigationView navigationView = findViewById(R.id.nav_view);

        // Build the navigation bar
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_topic,
                R.id.navigation_notifications,
                R.id.navigation_booklist,
                R.id.navigation_user
        ).build();

        // Set up the navigation bar controller
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }


    /**
     * This method prevents users from going back to login by clicking back button
     */
    @Override
    public void onBackPressed() {
        // TODO: double click back button to exit the program
    } //onBackPressed
}

