package com.example.ibook.activities;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.ibook.entities.Book;
import com.example.ibook.R;
import com.example.ibook.entities.BookRequest;
import com.example.ibook.entities.User;
import com.example.ibook.fragment.NotificationsFragment;
import com.example.ibook.fragment.ScanFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
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
    private AppBarConfiguration appBarConfiguration;
    private  BottomNavigationView navigationView;

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
        navigationView = findViewById(R.id.nav_view);

        // Build the navigation bar
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_notifications,
                R.id.navigation_booklist,
                R.id.navigation_user
        ).build();



        MainActivity.database.getDb().collection("bookRequest").
                whereEqualTo("requestReceiverID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               //Arraylist to hold the BookRequest objects
                                               //final ArrayList<BookRequest> bookRequestArrayList = new ArrayList<BookRequest>();
                                               int countNewNotif = 0;
                                               for (final QueryDocumentSnapshot document : task.getResult()) {
                                                   BookRequest bookRequest = document.toObject(BookRequest.class);
                                                   Date timeOfRequest= bookRequest.getTimeOfRequest();

                                                   System.out.println(MainActivity.lastLoginTime);
                                                   if(!(timeOfRequest == null)) {
                                                       if (timeOfRequest.compareTo(MainActivity.lastLoginTime) > 0) {
                                                           countNewNotif++;
                                                       }// of
                                                   }// if
                                               }//for
                                               navigationView.getOrCreateBadge(R.id.navigation_notifications).setNumber(countNewNotif);
                                           }//onComplete
                                       });



        // Set up the navigation bar controller

//        View rootView =  navigationView.findViewById(R.id.navigation_notifications);
//        rootView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                navigationView.getOrCreateBadge(R.id.navigation_notifications).setNumber(0);
//                FragmentManager fm = getSupportFragmentManager();
//                NotificationsFragment fragment = new NotificationsFragment();
//                fm.beginTransaction().replace(R.id.container,fragment).commit();
//
//            }
//        });


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


    }



    public void doNavigation(){




    }// doNavigation
    /**
     * This method prevents users from going back to login by clicking back button
     */
    @Override
    public void onBackPressed() {

    } //onBackPressed
}
