package com.example.ibook.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.ibook.R;
import com.example.ibook.SectionPageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.example.ibook.activities.MainActivity;
import com.example.ibook.activities.MapsActivity;
import com.example.ibook.activities.ViewBookActivity;
import com.example.ibook.entities.Database;
import com.example.ibook.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The class for the notification fragment
 */
public class NotificationsFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    User currentUser;
    private FirebaseFirestore db;
    private ArrayList<String> notificationList;
    private ListView listView;
    private ArrayList<String> requestsList;
    private ArrayList<String> responseList;
    private RadioGroup radioGroup;
    private DocumentReference userDoc;
    public static String requestSenderID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notification, container, false);
        viewPager = root.findViewById(R.id.viewPager);
        tabLayout = root.findViewById(R.id.tabLayout);

        //get users request list
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    //user object intialized
                    currentUser = documentSnapshot.toObject(User.class);
                    notificationList = currentUser.getNotificationList();
                    for (String notif:notificationList){
                        requestsList.add(notif);
                    }// for loop
                    Collections.reverse(requestsList);
                    adapter.notifyDataSetChanged();

                }// if
            }
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Would you like to accept or decline this request?")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //going to the maps
                                Intent intent = new Intent(getContext(), MapsActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String[] notification = requestsList.get(position).split(" ");
                                final String requestSenderUsername = notification[0];
                                MainActivity.database.getDb().collection("users")
                                        .whereEqualTo("userName", requestSenderUsername)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                //don't need the for loop since username will be unique in our app, so only 1 result with the match.
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        requestSenderID = document.getId();

                                                    }// for loop
                                                    System.out.println("UserID: " + requestSenderID);
                                                MainActivity.database.getDb().collection("bookRequest")
                                                        .whereEqualTo("requestSenderID",requestSenderID)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                String bookID;
                                                                //don't need the for loop since username will be unique in our app, so only 1 result with the match.
                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                    //delete the document
                                                                   bookID  = (String) document.get("requestedBookID");
                                                                    document.getReference().delete();
                                                                    //check if there are any other request on the same book, if no, then change book status
                                                                    final String finalBookID = bookID;
                                                                    MainActivity.database.getDb().collection("bookRequest")
                                                                            .whereEqualTo("requestedBookID",bookID)
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    // checks if there are no other documents with request on the book
                                                                                    if(task.getResult().isEmpty()){
                                                                                        //so update book status
                                                                                        MainActivity.database.getDb().collection("books").document(finalBookID)
                                                                                                .update("status", "Available");
                                                                                    }// if
                                                                                }
                                                                            });
                                                                }// for loop

                                                                //get current userDocument and remove request from the notification list
                                                                MainActivity.database.getUserDocumentReference().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                       User currentUser = documentSnapshot.toObject(User.class);
                                                                       currentUser.getNotificationList().remove(position);
                                                                       MainActivity.database.getUserDocumentReference().set(currentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                           @Override
                                                                           public void onSuccess(Void aVoid) {
                                                                               Toast.makeText(getContext(), "Declined request from " + requestSenderUsername, Toast.LENGTH_SHORT).show();
                                                                           }
                                                                       });
                                                                    }//onSuccess
                                                                });

                                                            }// inner onComplete
                                                        });// most outer onCompleteListener(so done)


                                                //add decline message notification to requestSender
                                                System.out.println("Checking if the userID of requestSender is correct : " + requestSenderID);
                                                MainActivity.database.getDb().collection("users").document(requestSenderID)
                                                        .get()
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        User requestSender = documentSnapshot.toObject(User.class);
                                                        requestSender.addToNotificationList(currentUser.getUserName() + " declined your request ");
                                                        //update database
                                                        MainActivity.database.getDb().collection("users").document(requestSenderID).set(requestSender);
                                                        Toast.makeText(getContext(), "Updated Notification of Sender", Toast.LENGTH_SHORT).show();
                                                    }//onSuccess
                                                });

                                                //now check if there are anyother requests on the same book, if no requests, then change book status


                                            }//onComplete

                                        });// addOnCompleteListener

                                System.out.println("Username " + requestSenderUsername + "| userID: " + requestSenderID);

                                requestsList.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }


        // set toggle buttons
        radioGroup = root.findViewById(R.id.selectState);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                if(radioButton.getText().toString().equals("Requests")){
                    ArrayAdapter adapter = new ArrayAdapter<>(getContext(),R.layout.notification_list_content,R.id.textView, requestsList);
                    listView.setAdapter(adapter);
                }// if
                else{
                    // TODO: display responses listview
                    // empty for now
                    ArrayAdapter adapter = new ArrayAdapter<>(getContext(),R.layout.notification_list_content,R.id.textView,new ArrayList());
                    listView.setAdapter(adapter);
                }// else
            }
        });
    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionPageAdapter adapter = new SectionPageAdapter(getChildFragmentManager());

        adapter.addFragment(new NotificationsSectionFragment(), "Request");
        adapter.addFragment(new NotificationsSectionFragment(), "Response");

        viewPager.setAdapter(adapter);
    }


}
