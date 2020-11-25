package com.example.ibook.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
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

import com.example.ibook.BookListAdapter;
import com.example.ibook.R;
import com.example.ibook.UserListAdapter;
import com.example.ibook.activities.MainActivity;
import com.example.ibook.activities.MapsActivity;
import com.example.ibook.activities.ViewBookActivity;
import com.example.ibook.entities.Book;
import com.example.ibook.entities.BookRequest;
import com.example.ibook.entities.Database;
import com.example.ibook.entities.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    public BookRequest bookRequest;
    public String bookID;
    private FirebaseFirestore db;
    private ArrayList<String> notificationList;
    private ListView listView;
    private ArrayList<String> requestsList;
    private ArrayList<String> responseList;
    private RadioGroup radioGroup;
    private DocumentReference userDoc;
    public static String requestSenderID;
    private String currentUserID;
    private String currentUsername;

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
     ArrayAdapter adapter;

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
        radioGroup = root.findViewById(R.id.selectState);
        final DocumentReference docRef = db.collection("users").document(MainActivity.database.getCurrentUserUID());

        adapter = new ArrayAdapter<>(getContext(), R.layout.notification_list_content, R.id.userNameTextView, requestsList);
        listView.setAdapter(adapter);
        currentUserID = MainActivity.database.getCurrentUserUID();


        //radio button change checker
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                if (radioButton.getText().toString().equals("Responses")) {
                    //Go to user collection and get his notification/response list
                    MainActivity.database.getDb().collection("users").document(MainActivity.database.getCurrentUserUID())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot document = (DocumentSnapshot) task.getResult();
                                    User currentUser = document.toObject(User.class);
                                    adapter = new ArrayAdapter<>(getContext(), R.layout.notification_list_content, R.id.userNameTextView, currentUser.getNotificationList());
                                    listView.setAdapter(adapter);

                                }//onComplete
                            });

                    //setUpListListener();
                }
                if (radioButton.getText().toString().equals("Requests")) {
                    adapter = new ArrayAdapter<>(getContext(), R.layout.notification_list_content, R.id.userNameTextView, requestsList);
                    listView.setAdapter(adapter);
                }
            }
        });


        //get all requests made to the currently logged in user

//        MainActivity.database.getDb().collection("bookRequest")
//                .whereEqualTo("requestReceiverID",currentUserID)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                           @Override
//                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                               for (QueryDocumentSnapshot document : task.getResult()) {
//                                                   bookRequest = document.toObject(BookRequest.class);
//                                                   MainActivity.database.getDb().collection("users").document(bookRequest.getRequestSenderID())
//                                                           .get()
//                                                           .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                                               @Override
//                                                               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                                                               }
//                                                           })


//        FirebaseDatabase.getInstance().getReference("users")
//                .orderByChild("requestReceiverID").equalTo(currentUserID)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for(DataSnapshot bookRequestSnapshot : snapshot.getChildren()){
//                            bookRequest = bookRequestSnapshot.getValue(BookRequest.class);
//                            senderUsername = getUsernameFromDB(bookRequest.getRequestSenderID());
//                        }// for loop
//
//                    }//onDataChange
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

        //TODO: Delete later
//        MainActivity.database.getDb().collection("bookRequest")
//                .whereEqualTo("requestReceiverID",MainActivity.database.getCurrentUserUID())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
//                            bookRequest = documentSnapshot.toObject(BookRequest.class);
//                             getUsernameFromDB(bookRequest.getRequestSenderID(),bookRequest.getRequestedBookID());
//
//
//
//                            //System.out.println(senderUsername);
//                        }//for
//                    }
//                });


////TODO: START OF YESTERDAY

        MainActivity.database.getDb().collection("bookRequest")
                .whereEqualTo("requestReceiverID", MainActivity.database.getCurrentUserUID())
                .whereEqualTo("requestStatus", "Requested")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        //Arraylist to hold the BookRequest objects
                        final ArrayList<BookRequest> bookRequestArrayList =new ArrayList<BookRequest>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            bookRequest = document.toObject(BookRequest.class);
                            bookRequestArrayList.add(bookRequest);
                            requestsList.add(bookRequest.getRequestSenderUsername() + " wants to borrow your book called " + bookRequest.getRequestedBookTitle());
                        }// for loop
                        //update the listView
                        adapter.notifyDataSetChanged();

                        //alert dialog on click item
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                //get the person who sent the request
                                final String requestSenderID = bookRequestArrayList.get(position).getRequestSenderID();
                                final String requestedBookID = bookRequestArrayList.get(position).getRequestedBookID();
                                final String bookRequestID = bookRequestArrayList.get(position).getBookRequestID();
                                final BookRequest bookReq = bookRequestArrayList.get(position);
                                //final String requestSenderUsername = notification[0];
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setMessage("Would you like to accept or decline this request?")
                                        //Accept
                                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //going to the maps
                                                bookReq.setRequestStatus("Accepted");
                                                MainActivity.database.getDb().collection("bookRequest").document(bookRequestID).set(bookReq)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                            }//onSuccess
                                                        });

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

                                                MainActivity.database.getDb().collection("users").document(requestSenderID)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                DocumentSnapshot document = (DocumentSnapshot) task.getResult();
                                                                final User senderUser = document.toObject(User.class);

                                                                //Get the username of the current user/owner
                                                                MainActivity.database.getDb().collection("users").document(MainActivity.database.getCurrentUserUID())
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                DocumentSnapshot currentUserDoc = (DocumentSnapshot) task.getResult();
                                                                                final User currentUser = currentUserDoc.toObject(User.class);
                                                                                currentUsername = currentUser.getUserName();
                                                                                currentUser.addToNotificationList("You accepted the borrow request sent by " + senderUser.getUserName() + " on the book titled " + bookRequest.getRequestedBookTitle());
                                                                                senderUser.addToNotificationList(currentUsername + "accepted your borrow request on the book named " + bookRequest.getRequestedBookTitle());

                                                                                //update accept message to owner who accepted the book request
//                                                                                MainActivity.database.getDb().collection("users").document(MainActivity.database.getCurrentUserUID()).set(currentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                                    @Override
//                                                                                    public void onSuccess(Void aVoid) {
//                                                                                        Toast.makeText(getContext(), "Added accept message to yourself" , Toast.LENGTH_LONG).show();
//                                                                                    }
//                                                                                });

                                                                                //update accept message to sender
                                                                                MainActivity.database.getDb().collection("users").document(senderUser.getUserID()).set(senderUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        Toast.makeText(getContext(), "Added accept message to " + senderUser.getUserName(), Toast.LENGTH_LONG).show();
                                                                                    }
                                                                                });
                                                                            }
                                                                        });
                                                            }//onComplete
                                                        });
                                                //delete the request from the listview when a request is accepted
                                                requestsList.remove(position);
                                                adapter.notifyDataSetChanged();

                                               //remove other bookRequests on the same book in the bookRequest collection
                                                MainActivity.database.getDb().collection("bookRequest")
                                                        .whereEqualTo("requestedBookID",requestedBookID)
                                                        .whereNotEqualTo("requestSenderID", requestSenderID)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                //delete all documents that meet the query
                                                                BookRequest deleteRequest = null;
                                                                for(QueryDocumentSnapshot document : task.getResult()) {
                                                                    deleteRequest = document.toObject(BookRequest.class);
                                                                    //int index = bookRequestArrayList.indexOf(deleteRequest);
                                                                    //requestsList.remove(index);
                                                                   // adapter.notifyDataSetChanged();
                                                                    document.getReference().delete();
                                                                }//for loop
                                                            }//onComplete
                                                        });

                                                //update the book Status to be accepted
                                                MainActivity.database.getDb().collection("books").document(requestedBookID)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                DocumentSnapshot document = (DocumentSnapshot) task.getResult();
                                                                Book book = document.toObject(Book.class);
                                                                book.setStatus(Book.Status.Accepted);
                                                                MainActivity.database.getDb().collection("books").document(book.getBookID()).set(book);
                                                            }// onComplete
                                                        });

                                            }//onClick on Accept button in dialog

                                        })
                                        //on decline request
                                        .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                final String bookRequestID = bookRequestArrayList.get(position).getBookRequestID();
                                                final String requestedBookID = bookRequestArrayList.get(position).getRequestedBookID();
                                                //Delete the document if the request is declined
                                                MainActivity.database.getDb().collection("bookRequest").document(bookRequestID)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                DocumentSnapshot document = (DocumentSnapshot) task.getResult();
                                                                document.getReference().delete();
                                                                Toast.makeText(getContext(), "Deleted Document", Toast.LENGTH_SHORT).show();
                                                                //Update book status to "Available" if there are no more requests on that book
                                                                Toast.makeText(getContext(), "Changed book status before to Available (last req deleted)" + requestedBookID, Toast.LENGTH_LONG).show();
                                                                MainActivity.database.getDb().collection("bookRequest").document(bookRequestID)
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                //check if anyother requests in bookReqest on same book
                                                                                if(!task.getResult().exists()){
                                                                                    MainActivity.database.getDb().collection("books").document(requestedBookID)
                                                                                            .get()
                                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                    DocumentSnapshot document = (DocumentSnapshot) task.getResult();
                                                                                                    Book book = document.toObject(Book.class);
                                                                                                    //Set status Available since only request was declined
                                                                                                    book.setStatus(Book.Status.Available);
                                                                                                    MainActivity.database.getDb().collection("books").document(book.getBookID()).set(book).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                            //Toast.makeText(getContext(), "Changed book status actual to Available (last req deleted)" + requestedBookID, Toast.LENGTH_LONG).show();
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            });
                                                                                }// if
                                                                            }//onComplete
                                                                        }); // addOnCompleteListener

                                                                //update the response/notificationlist of the sender user
                                                                MainActivity.database.getDb().collection("users").document(requestSenderID)
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                DocumentSnapshot document = (DocumentSnapshot) task.getResult();
                                                                                final User senderUser = document.toObject(User.class);

                                                                                //Get the username of the current user/owner
                                                                                MainActivity.database.getDb().collection("users").document(currentUserID)
                                                                                        .get()
                                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                DocumentSnapshot currentUserDoc = (DocumentSnapshot) task.getResult();
                                                                                                final User currentUser = currentUserDoc.toObject(User.class);
                                                                                                currentUsername = currentUser.getUserName();
                                                                                                senderUser.addToNotificationList(currentUsername + " declined your borrow request on the book named " + bookRequest.getRequestedBookTitle());
                                                                                                //update the sender's user collection with new notification list
                                                                                                MainActivity.database.getDb().collection("users").document(requestSenderID).set(senderUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                        //Toast.makeText(getContext(), "Added accept message to " + senderUser.getUserName(), Toast.LENGTH_LONG).show();
                                                                                                    }
                                                                                                });

                                                                                                MainActivity.database.getDb().collection("users").document(currentUserID).set(currentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                       // Toast.makeText(getContext(), "Added accept message to owner to tell him who he accepted " + currentUsername, Toast.LENGTH_LONG).show();
                                                                                                    }
                                                                                                });


                                                                                            }
                                                                                        });
                                                                            }//onComplete
                                                                        });


                                                            }//onComplete
                                                        }); //addOnCompleteListener


                                                //delete the request from the listview when a request is declined
                                                requestsList.remove(position);
                                                adapter.notifyDataSetChanged();
                                            }// onClick
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        });

                        //String of message
//                            MainActivity.database.getDb().collection("users")
//                                    .whereEqualTo("userID",requestSenderID)
//                                    .get()
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            System.out.println("Failed User entry");
//                                        }
//                                    })
//                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                            //String senderUsername;
//                                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                                //check if there are any other request on the same book, if no, then change book status
//                                                senderUsername = (String) document.get("userName");
//
//                                                //list.add(bookRequets)
//
//                                                //System.out.println("HELLOOO");
//                                               // senderUsername = (String) userDoc.get("userName");
//
//                                                System.out.println(senderUsername);
//                                                MainActivity.database.getDb().collection("books")
//                                                        .whereEqualTo("bookID",bookID)
//                                                        .get()
//                                                        .addOnFailureListener(new OnFailureListener() {
//                                                            @Override
//                                                            public void onFailure(@NonNull Exception e) {
//                                                                System.out.println("Failed Book entry");
//                                                            }
//                                                        })
//                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                                //String bookTitle;
//                                                                // checks if there are no other documents with request on the book
//                                                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                                                    bookTitle = (String) document.get("title");
//                                                                    requestsList.add(senderUsername + "wants to borrow your book called" + bookTitle);
//                                                                    adapter.notifyDataSetChanged();
//                                                                    System.out.println("Coming inside of books database");
//
//
//                                                                }
//
//
//
//
//                                                            }//onComplete -- books
//
//                                                        }); // onCompleteListener -- books
//                                                SystemClock.sleep(500);
//
//
//
//                                            }// for loop for users
//
//
//                                        }// onComplete -- users
//                                    }); //OncompleteListener -- users
//                            SystemClock.sleep(500);



                    }//onComplete - BookRequest
                }); // onCompleteListener -- BookRequest


//        //TODO: END OF YESTERDAY








        //get users request list
//        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    //user object intialized
//                    currentUser = documentSnapshot.toObject(User.class);
//                    notificationList = currentUser.getNotificationList();
//                    for (String notif : notificationList) {
//                        requestsList.add(notif);
//                    }// for loop
//                    Collections.reverse(requestsList);
//                    adapter.notifyDataSetChanged();
//
//                }// if
//            }
//
//        });



        //TODO: ACCEPT CODE BELOW FROM BEFORE VERY BEFORE


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



        // set toggle buttons
//        radioGroup = root.findViewById(R.id.selectState);
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                RadioButton radioButton = group.findViewById(checkedId);
//                if (radioButton.getText().toString().equals("Requests")) {
//                    ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.notification_list_content, R.id.userNameTextView, requestsList);
//                    listView.setAdapter(adapter);
//                }// if
//                else {
//                    // TODO: display responses listview
//                    // empty for now
//                    ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.notification_list_content, R.id.userNameTextView, new ArrayList());
//                    listView.setAdapter(adapter);
//                }// else
//            }
//        });

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

//    public void getUsernameFromDB(final String userID, final String bookID){
//
//        SystemClock.sleep(500);
//        MainActivity.database.getDb().collection("users").
//                whereEqualTo("userID",userID)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
//                            requestSender = documentSnapshot.toObject(User.class);
//                            senderUsername = requestSender.getUserName();
//
//                            MainActivity.database.getDb().collection("books")
//                                    .whereEqualTo("bookID",bookID)
//                                    .get()
//                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                            for(QueryDocumentSnapshot document : task.getResult()){
//                                                book = document.toObject(Book.class);
//
//                                                requestsList.add(senderUsername + "wants to borrow your called " + book.getTitle());
//
//
//                                                for(String list : requestsList){
//                                                    System.out.println("Printing requestsList contents inside");
//                                                    System.out.println(list);
//                                                }
//                                                adapter.notifyDataSetChanged();
//
//                                            }//for loop
//
//                                        }
//                                    });
//
//                            //System.out.println(senderUsername);
//                        }//for
//                        SystemClock.sleep(500);
//                    }
//                });
//
//        System.out.println("OUTSIDE");
//        for(String list : requestsList){
//            System.out.println("Printing requestsList contents outside");
//            System.out.println(list);
//        }
//
//
//
//        System.out.println("Made it to method, userID: " + userID);
//
//
//
//    }// getUsernameFromDB
//
//    public void pleaseWork(String bookID){
//
//    }


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
