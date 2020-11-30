package com.example.ibook.activities;

import android.os.Bundle;
import android.view.Window;
import com.example.ibook.R;
import com.example.ibook.entities.BookRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

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
