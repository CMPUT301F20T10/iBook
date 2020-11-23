package com.example.ibook.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
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

import com.example.ibook.R;
import com.example.ibook.activities.MainActivity;
import com.example.ibook.activities.MapsActivity;
import com.example.ibook.activities.ViewBookActivity;
import com.example.ibook.entities.BookRequest;
import com.example.ibook.entities.Database;
import com.example.ibook.entities.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The class for the notification fragment
 */
public class NotificationsFragment extends Fragment {

    User currentUser;
    private FirebaseFirestore db;
    private ArrayList<String> notificationList;
    private ListView listView;
    private ArrayList<String> requestsList;
    private ArrayList<String> responseList;
    private RadioGroup radioGroup;
    private DocumentReference userDoc;
    public static String requestSenderID;
    private String currentUserID;
    BookRequest bookRequest;

    //for updating all other requestSenders notification when someone else's request gets accepted
    public User requestSender;
    public Boolean isAccepted = false;


    private RadioButton requestButton;
    private RadioButton responseButton;

    //Maps
    private Marker marker;
    public static LatLng markerLoc = null;
    public static String markerText;
    public static final int ADD_EDIT_LOCATION_REQUEST_CODE = 455;
    public static final int VIEW_LOCATION_REQUEST_CODE = 456;
    public static final int ADD_EDIT_LOCATION_RESULT_CODE = 457;
    public static final int VIEW_LOCATION_RESULT_CODE = 458;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        View root = inflater.inflate(R.layout.fragment_notification, container, false);
        listView = root.findViewById(R.id.listView);
        requestButton = root.findViewById(R.id.requestButton);
        responseButton = root.findViewById(R.id.responseButton);
        requestsList = new ArrayList<>();
        responseList = new ArrayList<>();
        final DocumentReference docRef = db.collection("users").document(MainActivity.database.getCurrentUserUID());

        final ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.notification_list_content, R.id.userNameTextView, requestsList);
        listView.setAdapter(adapter);

        currentUserID = MainActivity.database.getCurrentUserUID();
        //get all requests made to the currently logged in user
        MainActivity.database.getDb().collection("bookRequest")
                .whereEqualTo("requestReceiverID",currentUserID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        SystemClock.sleep(500);
                        String bookTitle;
                        String userName;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            bookRequest = document.toObject(BookRequest.class);

                        }// for loop

                    }
                });

        if(bookRequest.getRequestSenderID() != null){
            System.out.println("Bookrequest SenderID " + bookRequest.getRequestSenderID());

        }

        //get users request list
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    //user object intialized
                    currentUser = documentSnapshot.toObject(User.class);
                    notificationList = currentUser.getNotificationList();
                    for (String notif : notificationList) {
                        requestsList.add(notif);
                    }// for loop
                    Collections.reverse(requestsList);
                    adapter.notifyDataSetChanged();

                }// if
            }

        });


        //alert dialog on click item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String[] notification = requestsList.get(position).split(" ");
                final String requestSenderUsername = notification[0];
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Would you like to accept or decline this request?")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //going to the maps
                                Intent mapsIntent = new Intent(getContext(), MapsActivity.class);
                                mapsIntent.putExtra(MapsActivity.MAP_TYPE, MapsActivity.ADD_EDIT_LOCATION);
                                if (markerLoc != null) {
                                    mapsIntent.putExtra("locationIncluded", true);
                                    mapsIntent.putExtra("markerLoc", markerLoc);
                                    mapsIntent.putExtra("markerText", markerText);
                                } else {
                                    mapsIntent.putExtra("locationIncluded", false);
                                }
                                startActivityForResult(mapsIntent, ADD_EDIT_LOCATION_REQUEST_CODE);
                            }
                        })


//                                //the book is accepted, this boolean is used down below
//                                isAccepted = true;
//                                Toast.makeText(getContext(), "Accepted request from " + requestSenderUsername, Toast.LENGTH_SHORT).show();
//                                Toast.makeText(getContext(), "Waiting on maps to be fixed.. ",Toast.LENGTH_SHORT).show();
//
//                                //finding the document of the person I accepted the request of since I need to update his notification
//                                MainActivity.database.getDb().collection("users")
//                                        .whereEqualTo("userName", requestSenderUsername)
//                                        .get()
//                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                //don't need the for loop since username will be unique in our app, so only 1 result with the match.
//                                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                                    requestSenderID = document.getId();
//                                                }// for loop
//
//                                                System.out.println("UserID: " + requestSenderID);
//                                                MainActivity.database.getDb().collection("bookRequest")
//                                                        .whereEqualTo("requestSenderID",requestSenderID)
//                                                        .get()
//                                                        .addOnCompleteListener(new OnCompleteListener<QueryDocumentSnapshot>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<QueryDocumentSnapshot> task) {
//                                                                String bookID;
//                                                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                                                    //get the requested bookID
//                                                                    bookID = (String) document.get("requestedBookID");
//
//                                                                    MainActivity.database.getDb().collection("bookRequest")
//                                                                            .whereEqualTo("requestedBookID", bookID)
//                                                                            .whereNotEqualTo("requestSenderID", requestSenderID)
//                                                                            .get()
//                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                                                @Override
//                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                                                    String userID;
//                                                                                    //don't need the for loop since username will be unique in our app, so only 1 result with the match.
//                                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                                                                        //delete the document
//                                                                                        userID = (String) document.get("requestSenderID");
//
//                                                                                        //Maybe make a function for this ask TA how to make it not null
//
//                                                                                        // getNotificationList of others and update it
//                                                                                        MainActivity.database.getDb().collection("users").document(userID)
//                                                                                                .get()
//                                                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                                                                                    @Override
//                                                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                                                                        DocumentSnapshot document = (DocumentSnapshot) task.getResult();
//                                                                                                        requestSender = document.toObject(User.class);
//                                                                                                        requestSender.addToNotificationList(currentUser.getUserName() + "declined your request ");
//                                                                                                    }// onComplete
//                                                                                                });
//
//                                                                                        //now update the requestSender's notification list with decline message
//
//                                                                                        //delete the document from bookRequest(since it automatically declines it)
//                                                                                        document.getReference().delete();
//                                                                                    }// for loop
//                                                                                }//onComplete
//                                                                            }); //addOnCompleteListener
//                                                                    //check if there are any other request on the same book, if no, then change book status
//
//                                                                    final String finalBookID = bookID;
//                                                                    //update the book status to Borrowed
//                                                                    MainActivity.database.getDb().collection("books").document(finalBookID)
//                                                                            .update("status", "Borrowed");
//
//
//                                                                    //get current userDocument and remove request from the notification list
//                                                                    MainActivity.database.getUserDocumentReference().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                                                        @Override
//                                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                                                            User currentUser = documentSnapshot.toObject(User.class);
//                                                                            currentUser.getNotificationList().remove(position);
//                                                                            MainActivity.database.getUserDocumentReference().set(currentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                                @Override
//                                                                                public void onSuccess(Void aVoid) {
//                                                                                    Toast.makeText(getContext(), "Declined request from " + requestSenderUsername, Toast.LENGTH_SHORT).show();
//                                                                                }
//                                                                            });
//                                                                        }//onSuccess
//                                                                    });
//
//                                                                }// inner onComplete
//                                                            }
//                                                        });// most outer onCompleteListener(so done)
//
//
//                                                //add decline message notification to requestSender
////                                                System.out.println("Checking if the userID of requestSender is correct : " + requestSenderID);
////                                                MainActivity.database.getDb().collection("users").document(requestSenderID)
////                                                        .get()
////                                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
////                                                            @Override
////                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
////                                                                User requestSender = documentSnapshot.toObject(User.class);
////                                                                requestSender.addToNotificationList(currentUser.getUserName() + " declined your request ");
////                                                                //update database
////                                                                MainActivity.database.getDb().collection("users").document(requestSenderID).set(requestSender);
////                                                                Toast.makeText(getContext(), "Updated Notification of Sender", Toast.LENGTH_SHORT).show();
////                                                            }//onSuccess
////                                                        });
//
//                                                //now check if there are anyother requests on the same book, if no requests, then change book status
//
//
//                                            }//onComplete
//
//                                        });// addOnCompleteListener
//
//                                System.out.println("Username " + requestSenderUsername + "| userID: " + requestSenderID);
//
//                                requestsList.remove(position);
//                                adapter.notifyDataSetChanged();
//
//
//
//
//                                //going to the maps
//                                //Intent mapsIntent = new Intent(getContext(), MapsActivity.class);
////                                mapsIntent.putExtra(MapsActivity.MAP_TYPE, MapsActivity.ADD_EDIT_LOCATION);
////
////                                if(markerLoc!=null) {
////                                    mapsIntent.putExtra("locationIncluded", true);
////                                    mapsIntent.putExtra("markerLoc", markerLoc);
////                                    mapsIntent.putExtra("markerText", markerText);
////                                }else{
////                                    mapsIntent.putExtra("locationIncluded", false);
////                                }
////                                startActivityForResult(mapsIntent, ADD_EDIT_LOCATION_REQUEST_CODE);
//                            }//acceptOnclick
//                        })//acceptOnclickListeer

                        .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {




                                System.out.println("Username " + requestSenderUsername + "| userID: " + requestSenderID);

                                requestsList.remove(position);
                                adapter.notifyDataSetChanged();
                            }// onClick
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        // set toggle buttons
        radioGroup = root.findViewById(R.id.selectState);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                if (radioButton.getText().toString().equals("Requests")) {
                    ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.notification_list_content, R.id.userNameTextView, requestsList);
                    listView.setAdapter(adapter);
                }// if
                else {
                    // TODO: display responses listview
                    // empty for now
                    ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.notification_list_content, R.id.userNameTextView, new ArrayList());
                    listView.setAdapter(adapter);
                }// else
            }
        });

        responseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestButton.setTextColor(Color.parseColor("#FF9900"));
                responseButton.setTextColor(Color.WHITE);
            }
        });

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responseButton.setTextColor(Color.parseColor("#FF9900"));
                requestButton.setTextColor(Color.WHITE);
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Add new gear
        if (resultCode == ADD_EDIT_LOCATION_RESULT_CODE && requestCode == ADD_EDIT_LOCATION_REQUEST_CODE) {
            if (data.getBooleanExtra("locationIncluded", false)) {
                markerLoc = (LatLng) data.getExtras().getParcelable("markerLoc");
                markerText = data.getStringExtra("markerText");
            }
            //Clear the map so existing marker gets removed
            //mMap.clear();
            //addMarker();
            //addLocation.setText("Edit Location");
        }
    }

}
