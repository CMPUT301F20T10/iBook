package com.example.ibook.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ibook.R;
import com.example.ibook.activities.MainActivity;
import com.example.ibook.activities.MapsActivity;
import com.example.ibook.entities.Book;
import com.example.ibook.entities.BookRequest;
import com.example.ibook.entities.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * The class for the notification fragment
 */
public class NotificationsFragment extends Fragment implements ZXingScannerView.ResultHandler {

    public BookRequest bookRequest;
    public String bookID;
    private FirebaseFirestore db;
    private ArrayList<String> notificationList;
    private ListView listView;
    private ArrayList<String> requestsList;
    private ArrayList<String> responseList;
    private RadioGroup radioGroup;
    private DocumentReference userDoc;
    private String currentUserID;
    private String currentUsername;

    private RadioButton requestButton;
    private RadioButton responseButton;
    private TextView title;

    //Maps
    private Marker marker;
    public static LatLng markerLoc = null;
    public static String markerText;

    ArrayAdapter adapter;

    private String selectedBookISBN;
    private int selectedPosition;
    private String requestSenderID;
    private String requestedBookID;
    private String bookRequestID;
    private BookRequest bookReq;

    //scan the isbn
    private ZXingScannerView scannerView;
    private Button rescanButton;
    private Button cancelButton;
    private Button confirmButton;
    private EditText isbnView;
    private TextView textView;
    private String scanISBN;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        View root = inflater.inflate(R.layout.fragment_notification, container, false);
        listView = root.findViewById(R.id.listView);
        requestButton = root.findViewById(R.id.requestButton);
        responseButton = root.findViewById(R.id.responseButton);

        scannerView = root.findViewById(R.id.scan);
        rescanButton = root.findViewById(R.id.rescan);
        isbnView = root.findViewById(R.id.isbn_result);
        cancelButton = root.findViewById(R.id.cancel);
        confirmButton = root.findViewById(R.id.confirm);
        textView = root.findViewById(R.id.textView6);
        title = root.findViewById(R.id.header_notifications);
        scanISBN = "";

        requestsList = new ArrayList<>();
        responseList = new ArrayList<>();
        radioGroup = root.findViewById(R.id.selectState);
        final DocumentReference docRef = db.collection("users").document(MainActivity.database.getCurrentUserUID());

        adapter = new ArrayAdapter<>(getContext(), R.layout.notification_list_content, R.id.userNameTextView, requestsList);
        listView.setAdapter(adapter);
        currentUserID = MainActivity.database.getCurrentUserUID();

        //radio button change listener
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
                                    ArrayList<String> list = new ArrayList<String>();
                                    list = currentUser.getNotificationList();
                                    Collections.reverse(list);// reverse list to put the data in right order
                                    adapter = new ArrayAdapter<>(getContext(), R.layout.notification_list_content, R.id.userNameTextView, list);
                                    listView.setAdapter(adapter);
                                }//onComplete
                            });// onCompleteListener
                }// if - "Responses" toggle
                if (radioButton.getText().toString().equals("Requests")) {
                    adapter = new ArrayAdapter<>(getContext(), R.layout.notification_list_content, R.id.userNameTextView, requestsList);
                    listView.setAdapter(adapter);
                }// if "Requests" toggle
            }//onCheckedChanged
        });// onCheckedChangedListener

        //Retrieve requests for the current user from database (Making use of the bookRequest Collection)
        MainActivity.database.getDb().collection("bookRequest")
                .whereEqualTo("requestReceiverID", MainActivity.database.getCurrentUserUID())
                .whereEqualTo("requestStatus", "Requested")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //Arraylist to hold the BookRequest objects
                        final ArrayList<BookRequest> bookRequestArrayList = new ArrayList<BookRequest>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            bookRequest = document.toObject(BookRequest.class);
                            bookRequestArrayList.add(bookRequest);
                            //adding the notification or message to requestList
                            requestsList.add(bookRequest.getRequestSenderUsername() + " wants to borrow your book called " + bookRequest.getRequestedBookTitle());
                        }// for loop
                        //update the listView
                        adapter.notifyDataSetChanged();

                        //alert dialog on click item
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                //get the person who sent the request
                                requestSenderID = bookRequestArrayList.get(position).getRequestSenderID();
                                requestedBookID = bookRequestArrayList.get(position).getRequestedBookID();
                                bookRequestID = bookRequestArrayList.get(position).getBookRequestID();
                                bookReq = bookRequestArrayList.get(position);
                                selectedPosition = position;
                                //final String requestSenderUsername = notification[0];
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setMessage("Would you like to accept or decline this request?");
                                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        MainActivity.database.getDb().collection("books").document(requestedBookID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                selectedBookISBN = documentSnapshot.toObject(Book.class).getIsbn();
                                                getPermission();
                                                setVisible(true);
                                            }
                                        });
                                    }//onClick on Accept button in dialog
                                });
                                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        final String bookRequestID = bookRequestArrayList.get(position).getBookRequestID();
                                        final String requestedBookID = bookRequestArrayList.get(position).getRequestedBookID();
                                        final String bookTitle = bookRequestArrayList.get(position).getRequestedBookTitle();
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
                                                        MainActivity.database.getDb().collection("bookRequest")
                                                                .whereEqualTo("requestedBookID", requestedBookID)
                                                                .whereEqualTo("requestStatus", "Requested")
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        //check if anyother requests in bookReqest on same book
                                                                        if (task.getResult().isEmpty()) {
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
                                                                                        senderUser.addToNotificationList(currentUsername + " declined your borrow request on the book named " + bookTitle);
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
                                });//Accept
                                //on decline request
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        });
                    }//onComplete - BookRequest
                }); // onCompleteListener -- BookRequest


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisible(false);
                isbnView.setText("");
                scanISBN = "";
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get ISBN from the editText
                // test ISBN: 123651565616
                scanISBN = isbnView.getText().toString();
                if (scanISBN.equals(selectedBookISBN)) {
                    Toast.makeText(getContext(), "ISBN matches!", Toast.LENGTH_LONG).show();
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
                    setVisible(false); // end scanning part
                } else {
                    Toast.makeText(getContext(), "ISBN does not match", Toast.LENGTH_LONG).show();
                }
            }
        });

        rescanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isbnView.setText("");
                rescanButton.setVisibility(View.INVISIBLE);
                scannerView.startCamera();
                scannerView.resumeCameraPreview(NotificationsFragment.this);
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

    private void getPermission() {
        Dexter.withActivity(getActivity()).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            // deal with the camera when the permission is allowed
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                scannerView.setResultHandler(NotificationsFragment.this);
                scannerView.startCamera();
            }

            // display the message when the permission is denied
            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(getContext(), "You must accept the permission to use the camera", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
            }
        }).check();
    }

    private void setVisible(boolean visible) {
        if (visible) {
            scannerView.setVisibility(View.VISIBLE);
            isbnView.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            confirmButton.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);

            radioGroup.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
        } else {
            scannerView.setVisibility(View.INVISIBLE);
            rescanButton.setVisibility(View.INVISIBLE);
            isbnView.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);
            confirmButton.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);

            radioGroup.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Add new gear
        Toast.makeText(getContext(),String.valueOf(resultCode)+" "+String.valueOf(requestCode),Toast.LENGTH_SHORT).show();

        // todo: the resultCode = 0 here, don't know why, so I ignored it
        //if (resultCode == ADD_EDIT_LOCATION_RESULT_CODE && requestCode == ADD_EDIT_LOCATION_REQUEST_CODE) {
        if(requestCode == ADD_EDIT_LOCATION_REQUEST_CODE){
            //if (data.getBooleanExtra("locationIncluded", false)) {
            //    markerLoc = (LatLng) data.getExtras().getParcelable("markerLoc");
            //    markerText = data.getStringExtra("markerText");
            //}
            //TODO: fix data set
            //acceptRequest();
            //Clear the map so existing marker gets removed
            //mMap.clear();
            //addMarker();
            //addLocation.setText("Edit Location");
        }
        acceptRequest();
    }

    //TODO: fix data set
    private void acceptRequest() {

        // ISBN for test: 123651565616

        Toast.makeText(getContext(), "got location!", Toast.LENGTH_SHORT).show();
        bookReq.setRequestStatus("Accepted");
        MainActivity.database.getDb().collection("bookRequest").document(bookRequestID).set(bookReq);
        //get the request Sender information from database, since we need to notify that person
        MainActivity.database
                .getDb()
                .collection("users")
                .document(requestSenderID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = (DocumentSnapshot) task.getResult();
                        User senderUser = document.toObject(User.class);
                        senderUser.addToNotificationList(MainActivity.user.getUserName() + " accepted your " +
                                "borrow request on the book named " + bookRequest.getRequestedBookTitle());

                        //update accept message to sender
                        MainActivity.database.getDb().collection("users").document(senderUser.getUserID()).set(senderUser);
                    }//onComplete -- RequestSender
                });
        //delete the request from the listview when that request is accepted
        requestsList.remove(selectedPosition);
        adapter.notifyDataSetChanged();
        //remove other bookRequests on the same book in the bookRequest collection
        MainActivity.database.getDb().collection("bookRequest")
                .whereEqualTo("requestedBookID", requestedBookID)
                .whereNotEqualTo("requestSenderID", requestSenderID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //delete all documents that meet the query
                        BookRequest deleteRequest = null;
                        for (QueryDocumentSnapshot document : task.getResult()) {
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
    }


    @Override
    public void handleResult(Result rawResult) {
        isbnView.setText(rawResult.getText());
        scanISBN = isbnView.getText().toString();
        rescanButton.setVisibility(View.VISIBLE);
    }
}
