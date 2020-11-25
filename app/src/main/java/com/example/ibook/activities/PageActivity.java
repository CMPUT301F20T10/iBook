package com.example.ibook.activities;

import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.ibook.entities.Book;
import com.example.ibook.R;
import com.example.ibook.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
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
    private User currentUser;

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
        final BottomNavigationView navigationView = findViewById(R.id.nav_view);

        // Build the navigation bar
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_notifications,
                R.id.navigation_booklist,
                R.id.navigation_user
        ).build();

        MainActivity.database.getUserDocumentReference().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    //user object intialized
                    currentUser = documentSnapshot.toObject(User.class);
                    //set the notification count
                    navigationView.getOrCreateBadge(R.id.navigation_notifications).setNumber(currentUser.getNotificationList().size());
                }// if
            }//onSuccess
        });



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

    } //onBackPressed
}
