package com.example.ibook.activities;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.ibook.R;
import com.example.ibook.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewProfileActivity extends AppCompatActivity {
    private TextView name;
    private TextView email;
    private TextView phone;
    private Button backButton;
    private TextView lastOnline;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide the top bar and make it full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar

        setContentView(R.layout.fragment_view_profile);
        email = findViewById(R.id.emailID);
        name = findViewById(R.id.username);
        phone = findViewById(R.id.phoneNumber);
        backButton = findViewById(R.id.cancelButton);
        lastOnline = findViewById(R.id.lastLoggedIn);

        final String emailID = getIntent().getStringExtra("EMAIL");

        MainActivity.database.getDb().collection("users")
                .whereEqualTo("email", emailID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //Arraylist to hold the BookRequest objects
                        //final ArrayList<BookRequest> bookRequestArrayList = new ArrayList<BookRequest>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            Date lastLoginTime = user.getLastLoginTime();

                            System.out.println(user.getUserName());
                            System.out.println(user.getLastLoginTime());
                            Date currentTime = new Date();
                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd h:mm a");
                            String dateTime = dateFormat.format(lastLoginTime);

                            if(lastLoginTime.compareTo(currentTime) == 0){
                                lastOnline.setText("Last logged in at: NOW ");
                            }//
                            else{
                                lastOnline.setText("Last logged in at: " + dateTime);

                            }// else

                        }// for loop


                        email.setText(emailID);
                        phone.setText(getIntent().getStringExtra("PHONE"));
                        name.setText(getIntent().getStringExtra("NAME"));

                    }// onSuccess
                });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
