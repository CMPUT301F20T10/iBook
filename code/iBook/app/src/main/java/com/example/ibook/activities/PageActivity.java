package com.example.ibook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.ibook.entities.Book;
import com.example.ibook.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

// There is no need to modify this class, this class just set up the navigation function
public class PageActivity extends AppCompatActivity {

    //Private variables

    private ListView bookList;
    private ArrayAdapter<Book> bookAdapter;
    private ArrayList<Book> bookDataList;
    private SearchView searchBar;
    private String username;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide the top bar and make it full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar

        //Set activity
        setContentView(R.layout.activity_page);
        Intent intentGet= getIntent();
        username = intentGet.getStringExtra("curr_username");
        //Toast.makeText(getBaseContext(), username, Toast.LENGTH_LONG).show();

        Intent intentPut = new Intent(PageActivity.this, AddMyBookActivity.class);
        intentPut.putExtra("curr_username", username);
        /*
        * todo: think of how to transit data ↑
        * */
        
        // Set the navigation view
        BottomNavigationView navigationView = findViewById(R.id.nav_view);

        // Code from the official bottom navigation application of the Android Studio
        // So I don't understand how it actually works...
        // Add the fragments of the navigation view ???
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


 */