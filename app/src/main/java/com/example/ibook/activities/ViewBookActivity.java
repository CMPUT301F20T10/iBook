package com.example.ibook.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ibook.R;
import com.example.ibook.RequestAdapter;
import com.example.ibook.entities.Book;
import com.example.ibook.entities.BookRequest;
import com.example.ibook.entities.User;
import com.example.ibook.fragment.ScanFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.DocumentTransform;
import com.google.protobuf.Timestamp;
import com.google.rpc.context.AttributeContext;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


import io.grpc.Server;

public class ViewBookActivity extends AppCompatActivity implements ScanFragment.OnFragmentInteractionListener, OnMapReadyCallback {

    private String userID;
    private ArrayList<BookRequest> requests;
    private String bookID;
    private String ownerID;
    private String isbn;
    private final int REQ_CAMERA_IMAGE = 1;
    private final int REQ_GALLERY_IMAGE = 2;

    private TextView bookNameTextView;
    private TextView authorTextView;
    private TextView dateTextView;
    private TextView isbnTextView;
    private TextView descriptionTextView;
    private TextView ownerTextView;
    private ImageView imageView;

    private TextView edit_button;
    private Button backButton;
    private Button delete_button;
    private Button request_button;
    private Button return_button;
    private ListView requestList;

    private FirebaseFirestore db;
    FirebaseAuth uAuth;
    private Book selectedBook;
    private String owner;
    private String status;

    public static User requestReceiver;
    private User currentUser;

    private boolean isRelated = false;
    private static boolean imageChanged;

    private String requestSenderID;
    private String bookRequestID;
    private BookRequest bookReq;
    private int requestPosition;

    private RequestAdapter requestAdapter;
    private FloatingActionButton scanButton;

    //Maps
    private static LatLng markerLoc = null;
    private static String markerText;
    private static boolean editMapsLocation = false;
    private GoogleMap mMap;
    private final LatLng defaultLocation = new LatLng(53.54685611047399, -113.49431332200767);
    private static final double DEFAULT_ZOOM = 5.0;
    private static final double MARKER_ZOOM = 15.0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Hide the top bar and make it full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_view_book);

        bookNameTextView = findViewById(R.id.ViewBookName);
        authorTextView = findViewById(R.id.ViewAuthor);
        dateTextView = findViewById(R.id.ViewDate);
        isbnTextView = findViewById(R.id.ViewISBN);
        ownerTextView = findViewById(R.id.ownerTextView);
        descriptionTextView = findViewById(R.id.descriptionView2);
        imageView = findViewById(R.id.imageView);
        edit_button = findViewById(R.id.editButton);
        request_button = findViewById(R.id.btn_request_book);
        backButton = findViewById(R.id.cancelButton);
        delete_button = findViewById(R.id.btn_delete_book);
        return_button = findViewById(R.id.btn_return_book);
        scanButton = findViewById(R.id.scan);
        requestList = findViewById(R.id.request_list);

        imageChanged = false;
        requests = new ArrayList<BookRequest>();
        requestAdapter = new RequestAdapter(requests, getApplicationContext());
        requestList.setAdapter(requestAdapter);

        uAuth = FirebaseAuth.getInstance();
        userID = uAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("users").document(userID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);
            }
        });

        Intent intent = getIntent();
        bookID = intent.getStringExtra("BOOK_ID");
        owner = intent.getStringExtra("OWNER");
        status = intent.getStringExtra("STATUS");

        getBookData();
        checkCases();
        setUpMaps();

        setUpEditButtonListener();
        setUpBackButtonListener();
        setUpScanButtonListener();
        setUpReturnButtonListener();
        setUpRequestButtonListener();
        setUpRequestListListener();

        // setting up the request list
        MainActivity.database
                .getDb()
                .collection("bookRequest")
                .whereEqualTo("requestedBookID", bookID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            requests.add(document.toObject(BookRequest.class));
                        }
                        requestAdapter = new RequestAdapter(requests, getApplicationContext());
                        requestList.setAdapter(requestAdapter);
                    }
                });
    }

    private void setUpRequestListListener() {
        requestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bookReq = requests.get(position);
                requestSenderID = bookReq.getRequestSenderID();
                bookRequestID = bookReq.getBookRequestID();
                requestPosition = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewBookActivity.this);
                builder.setMessage("Would you like to accept or decline this request?")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new ScanFragment().show(getSupportFragmentManager(), "Scan ISBN");
                            }
                        })
                        .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                declineRequest();
                                updateBookInf();
                                requests.remove(requestPosition);
                                requestAdapter = new RequestAdapter(requests, getApplicationContext());
                                requestList.setAdapter(requestAdapter);
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void setUpRequestButtonListener() {
        request_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add the book to requested list
                final DocumentReference docRefRequestReceiver = db.collection("users").document(owner);

                docRefRequestReceiver.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        requestReceiver = documentSnapshot.toObject(User.class);


                        // three requestStatus: Requested, Accepted, Confirmed
                        String bookRequestID = MainActivity.database.getDb().collection("bookRequest").document().getId();

                        BookRequest newRequest = new BookRequest(currentUser.getUserID(), requestReceiver.getUserID(), selectedBook.getBookID(), currentUser.getUserName(), selectedBook.getTitle(), bookRequestID, "Requested");
                        db.collection("bookRequest").document(bookRequestID).set(newRequest);



                        //change book status
                        System.out.println("Selected bookID: " + selectedBook.getBookID());

                        selectedBook.setStatus(Book.Status.Requested);

                        final DocumentReference bookRef = db.collection("books").document(selectedBook.getBookID());
                        bookRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                bookRef.set(selectedBook);
                                //TODO: Update the status of the book in the user collection bookList, the book collection has owner ID so you can use that to go to user collection
                                //TODO: and update his booklists' book status

                                //maybe don't have to do this if we are always using the book collection and bookRequestCollection but still something to think about

                            }
                        });
                    }
                });
                System.out.println("Coming before db");
                db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    }
                });

                Toast.makeText(getBaseContext(), "This function is coming soon!", Toast.LENGTH_SHORT).show();
                request_button.setBackgroundColor(Color.parseColor("#626363"));
                request_button.setClickable(false);
            }//onClick
        });
    }

    private void setUpReturnButtonListener() {
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //BookRequest newRequest = new BookRequest(currentUser.getUserID(),requestReceiver.getUserID(),selectedBook.getBookID(), "Requested");
                //db.collection("bookRequest").document().set(newRequest);

                MainActivity.database
                        .getDb()
                        .collection("bookRequest")
                        .whereEqualTo("requestSenderID", userID)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    if (!((String) documentSnapshot.get("requestedBookID")).equals(bookID)) {
                                        continue; // continue if not this book
                                    }
                                    BookRequest newRequest = documentSnapshot.toObject(BookRequest.class);
                                    // todo: so far, no need to change request status

                                    final DocumentReference docRef = db.collection("users").document(newRequest.getRequestReceiverID());

                                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            requestReceiver = documentSnapshot.toObject(User.class);
                                            requestReceiver.addToNotificationList(currentUser.getUserName() + " wants to return your book " + selectedBook.getTitle());
                                            docRef.set(requestReceiver);

                                            Toast.makeText(getBaseContext(), "raised a return request", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });


                                }
                            }
                        });


                // Q: finish the activity or not?
                //finish();
            }
        });
    }

    private void setUpScanButtonListener() {
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ScanFragment().show(getSupportFragmentManager(), "Scan ISBN");
            }
        });
    }

    private void setUpBackButtonListener() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void setUpEditButtonListener() {
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewBookActivity.this, EditBookActivity.class);
                intent.putExtra("BOOK_ID", bookID);
                startActivityForResult(intent, 3);
            }
        });
    }

    private void updateBookInf() {
        //update the response/notificationlist of the sender user
        MainActivity.database.getDb().collection("users").document(requestSenderID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = (DocumentSnapshot) task.getResult();
                        final User senderUser = document.toObject(User.class);

                        //Get the username of the current user/owner
                        MainActivity.database.getDb().collection("users").document(userID)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot currentUserDoc = (DocumentSnapshot) task.getResult();
                                        final User currentUser = currentUserDoc.toObject(User.class);
                                        String userName = currentUser.getUserName();
                                        senderUser.addToNotificationList(userName + " declined your " +
                                                "borrow request on the book named " + bookNameTextView.getText().toString());
                                        //update the sender's user collection with new notification list
                                        MainActivity.database.getDb().collection("users").document(requestSenderID).set(senderUser);
                                        MainActivity.database.getDb().collection("users").document(userID).set(currentUser);
                                    }
                                });
                    }//onComplete
                });
    }

    public void delete_book(View view) {
        MainActivity.database.deleteImage(selectedBook.getBookID());
        db.collection("books").document(selectedBook.getBookID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("bookRequest")
                                .whereEqualTo("requestedBookID", selectedBook.getBookID())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            documentSnapshot.getReference().delete();
                                        }
                                    }
                                });
                    }
                });
        finish();
    }


    /**
     * This method will be invoked when the user's focus comes back to ViewBookActivity
     * It will refresh the data from the database, so that if any data was updated, they will be displayed correctly
     */
    @Override
    protected void onResume() {
        super.onResume();
        // get data again when resume

        //getBookData();
    }

    /**
     * Get the results from Edit book so we can display the proper image.
     * This is because the database is asynchronous so it won't upload the image in time
     * for us to download. So we need to pass the bitmap through the intent.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MapsActivity.ADD_EDIT_LOCATION_RESULT_CODE && requestCode == MapsActivity.ADD_EDIT_LOCATION_REQUEST_CODE) {
            if (data.getBooleanExtra("locationIncluded", false)) {
                markerLoc = (LatLng) data.getExtras().getParcelable("markerLoc");
                markerText = data.getStringExtra("markerText");
                acceptRequest();
            }
            setUpMaps();
        }
        if (resultCode == 4 && requestCode == 3) {
            SystemClock.sleep(500);
            //Set new image if it changed.
            if (data.getExtras() != null) {
                try {
                    Log.i("image", "changed");
                    String tempFileName = data.getStringExtra("CHANGED_IMAGE");
                    FileInputStream is = this.openFileInput(tempFileName);
                    Bitmap new_pic = BitmapFactory.decodeStream(is);
                    imageView = findViewById(R.id.imageView);
                    imageView = EditBookActivity.scaleAndSetImage(new_pic, imageView);
                    is.close();
                    imageChanged = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            getBookData();
        }
    }

    private void acceptRequest() {
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
                        final User senderUser = document.toObject(User.class);

                        //Get the username of the current user/owner
                        MainActivity.database
                                .getDb()
                                .collection("users")
                                .document(MainActivity.database.getCurrentUserUID())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot currentUserDoc = (DocumentSnapshot) task.getResult();
                                        String userName = currentUser.getUserName();
                                        senderUser.addToNotificationList(userName + " accepted " +
                                                "your borrow request on the book named " + bookReq.getRequestedBookTitle());

                                        //update accept message to sender
                                        MainActivity.database.getDb().collection("users").document(senderUser.getUserID()).set(senderUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(), "Added accept message to " + senderUser.getUserName(), Toast.LENGTH_LONG).show();
                                            }//onSuccess
                                        });
                                    }//onComplete --CurrentUser
                                });//OnCompleteListener
                    }//onComplete -- RequestSender
                });

        //delete the request from the listview when that request is accepted
        requests.remove(requestPosition);
        requestAdapter = new RequestAdapter(requests, getApplicationContext());
        requestList.setAdapter(requestAdapter);

        //remove other bookRequests on the same book in the bookRequest collection
        MainActivity.database.getDb().collection("bookRequest")
                .whereEqualTo("requestedBookID", bookID)
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
        MainActivity.database.getDb().collection("books").document(bookID)
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

    /**
     * This method will retrieve the data from the database,
     * and assign the data to the TextViews, so that they are displayed correctly.
     */
    private void getBookData() {
        // if it's not owner's book, we cannot access the book from user
        // so find the book from book collection
        db.collection("books")
                .document(bookID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        selectedBook = documentSnapshot.toObject(Book.class);

                        bookNameTextView.setText(selectedBook.getTitle());
                        authorTextView.setText(selectedBook.getAuthors());
                        dateTextView.setText(selectedBook.getDate());
                        isbnTextView.setText(selectedBook.getIsbn());
                        isbn = selectedBook.getIsbn();
                        ownerID = selectedBook.getOwner();
                        if (selectedBook.getDescription() != null) {
                            descriptionTextView.setText(selectedBook.getDescription());
                        }
                        if (!imageChanged) {
                            MainActivity.database.downloadImage(imageView, selectedBook.getBookID(), true);
                        }
                        imageChanged = false;
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        MainActivity.database
                                .getDb()
                                .collection("users")
                                .document(ownerID)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        ownerTextView.setText(documentSnapshot.toObject(User.class).getUserName());
                                    }
                                });
                    }
                });
    }

    /**
     * This method will check whether the current user is the owner of the book
     * and then set the UIs accordingly.
     */
    private void checkCases() {
        final Book.Status bookStatus = Book.Status.valueOf(status);
        // owner
        if (userID.equals(owner)) {
            if (bookStatus.equals(Book.Status.Available) || bookStatus.equals(Book.Status.Requested)) {
                // if owner & book available/requested, edit allowed
                request_button.setVisibility(View.GONE);
                return_button.setVisibility(View.GONE);
            }
            // else if() //if owner & book accepted, nothing allowed
            // todo: later can show some information to let the owner know it's accepted
            else {
                // if owner & book accepted/borrowed, nothing allowed
                edit_button.setVisibility(View.GONE);
                delete_button.setVisibility(View.GONE);
                request_button.setVisibility(View.GONE);
                requestList.setVisibility(View.GONE);
                return_button.setVisibility(View.GONE);
                scanButton.setVisibility(View.VISIBLE);
            }
        } else {
            isRelated = false;
            MainActivity.database
                    .getDb()
                    .collection("bookRequest")
                    .whereEqualTo("requestSenderID", userID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if (!((String) documentSnapshot.get("requestedBookID")).equals(bookID)) {
                                    continue; // continue if not this book
                                }
                                if (documentSnapshot.contains("requestStatus")) {
                                    // already request the book
                                    if (((String) documentSnapshot.get("requestStatus")).equals("Requested")) {
                                        edit_button.setVisibility(View.GONE);
                                        delete_button.setVisibility(View.GONE);
                                        request_button.setVisibility(View.GONE);
                                        requestList.setVisibility(View.GONE);
                                        return_button.setVisibility(View.GONE);
                                        Toast.makeText(getBaseContext(), "Canceling requests to be done", Toast.LENGTH_SHORT).show();
                                    } else if (((String) documentSnapshot.get("requestStatus")).equals("Accepted")) {

                                        // todo: launch an activity with scanning to confirm it
                                        Toast.makeText(getBaseContext(), "launch an activity with scanning to confirm it", Toast.LENGTH_SHORT).show();
                                        edit_button.setVisibility(View.GONE);
                                        delete_button.setVisibility(View.GONE);
                                        request_button.setVisibility(View.GONE);
                                        requestList.setVisibility(View.GONE);
                                        return_button.setVisibility(View.GONE);
                                        scanButton.setVisibility(View.VISIBLE);

                                    } else if (((String) documentSnapshot.get("requestStatus")).equals("Confirmed")) {
                                        // may want to return the book
                                        edit_button.setVisibility(View.GONE);
                                        delete_button.setVisibility(View.GONE);
                                        request_button.setVisibility(View.GONE);
                                        requestList.setVisibility(View.GONE);
                                    }
                                    isRelated = true;
                                    break;
                                } else {
                                    Toast.makeText(getBaseContext(), "wrong request format", Toast.LENGTH_SHORT).show();
                                }

                            }
                            if (!isRelated) {
                                if (bookStatus.equals(Book.Status.Available) || bookStatus.equals(Book.Status.Requested)) {
                                    // if non-owner & book available/requested, request allowed
                                    edit_button.setVisibility(View.GONE);
                                    delete_button.setVisibility(View.GONE);
                                    requestList.setVisibility(View.GONE);
                                    return_button.setVisibility(View.GONE);
                                } else {
                                    // nothing can do
                                    edit_button.setVisibility(View.GONE);
                                    delete_button.setVisibility(View.GONE);
                                    request_button.setVisibility(View.GONE);
                                    requestList.setVisibility(View.GONE);
                                    return_button.setVisibility(View.GONE);
                                }
                            }
                        }
                    });


        }
    }

    private void declineRequest() {
        MainActivity.database
                .getDb()
                .collection("bookRequest")
                .document(bookRequestID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        ((DocumentSnapshot) task.getResult()).getReference().delete();
                        Toast.makeText(getApplicationContext(), "Deleted Document", Toast.LENGTH_SHORT).show();
                        MainActivity.database
                                .getDb()
                                .collection("bookRequest")
                                .whereEqualTo("requestedBookID", bookID)
                                .whereEqualTo("requestStatus", "Requested")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.getResult().isEmpty()) {
                                            MainActivity.database
                                                    .getDb()
                                                    .collection("books")
                                                    .document(bookID)
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            Book book = documentSnapshot.toObject(Book.class);
                                                            book.setStatus(Book.Status.Available);
                                                            MainActivity.database.getDb().collection("books").document(bookID).set(book);
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                });
    }

    @Override
    public void onOkPressed(String ISBN) {

        if (ISBN.equals(isbn)) {
            // if the book is requested.
            if (Book.Status.valueOf(status).equals(Book.Status.Requested)) {
                Intent mapsIntent = new Intent(getApplicationContext(), MapsActivity.class);
                mapsIntent.putExtra(MapsActivity.MAP_TYPE, MapsActivity.ADD_EDIT_LOCATION);
                if (markerLoc != null) {
                    mapsIntent.putExtra("locationIncluded", true);
                    mapsIntent.putExtra("markerLoc", markerLoc);
                    mapsIntent.putExtra("markerText", markerText);
                } else {
                    mapsIntent.putExtra("locationIncluded", false);
                }
                startActivityForResult(mapsIntent, MapsActivity.ADD_EDIT_LOCATION_REQUEST_CODE);
            }
            //if the book is accepted -> borrowed
            if (Book.Status.valueOf(status).equals(Book.Status.Accepted)) {
                MainActivity.database
                        .getDb()
                        .collection("books")
                        .document(bookID)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Book book = documentSnapshot.toObject(Book.class);
                                book.setStatus(Book.Status.Borrowed);
                                MainActivity.database.getDb().collection("books").document(bookID).set(book);
                                // TODO: Do we need to update the book list in the user list
                            }
                        });

            }
            // TODO: How you guys want to do that?
            // if the book is borrowed -> returned ??
            if (Book.Status.valueOf(status).equals(Book.Status.Borrowed)) {

            }
            // if the book is returned -> available ??
            if (Book.Status.valueOf(status).equals(Book.Status.Borrowed)) {

            }

        } else {
            Toast.makeText(getBaseContext(), "ISBN does not match", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Set up the google maps fragment so that the owner or borrower of a book can see the location to meet.
     * The owner can edit the location until the book has been confirmed as borrowed.
     * Once the book is confirmed as borrowed then the borrower can edit the location to eventually return the book.
     */
    private void setUpMaps() {
        if(userID.equals(owner) || isRelated) {
            //Set up the google maps fragment
            boolean abledToSetMapsUp = false;
            Button editViewButton = findViewById(R.id.EditViewMapsButton);
            ConstraintLayout mapsConstraintLayout = findViewById(R.id.mapsConstraintLayout);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapsSmallFragment);
            mapFragment.getMapAsync(this);
            mapsConstraintLayout.setVisibility(View.GONE);
            final Book.Status bookStatus = Book.Status.valueOf(status);

            //Set the visibility and properties of the maps and buttons
            if (userID.equals(owner)) {
                //The owner has accepted a request for the book so can still edit the location.
                if (bookStatus.equals(Book.Status.Accepted)) {
                    editViewButton.setText("Edit");
                    mapsConstraintLayout.setVisibility(View.VISIBLE);
                    editMapsLocation = true;
                    abledToSetMapsUp = true;
                }
                //Once the book is borrowed the owner can no longer edit the location
                else if (bookStatus.equals(Book.Status.Borrowed)) {
                    editViewButton.setText("View");
                    mapsConstraintLayout.setVisibility(View.VISIBLE);
                    abledToSetMapsUp = true;
                }
            }
            else if (isRelated){
                //The borrower can edit the location once the book is in his hands.
                //This is useful when trying to return the book as he can set the location to return it to.
                if (bookStatus.equals(Book.Status.Borrowed)) {
                    editViewButton.setText("Edit");
                    mapsConstraintLayout.setVisibility(View.VISIBLE);
                    editMapsLocation = true;
                    abledToSetMapsUp = true;
                }else  if (bookStatus.equals(Book.Status.Accepted)){
                    editViewButton.setText("View");
                    mapsConstraintLayout.setVisibility(View.VISIBLE);
                    abledToSetMapsUp = true;
                }
            }

            if(abledToSetMapsUp) {
                //Set the button click listener depending if the person view the book is currently
                //allowed to edit the location or only view it.
                editViewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mapsIntent = new Intent(getApplicationContext(), MapsActivity.class);
                        if (markerLoc != null) {
                            mapsIntent.putExtra("locationIncluded", true);
                            mapsIntent.putExtra("markerLoc", markerLoc);
                            mapsIntent.putExtra("markerText", markerText);
                        } else {
                            mapsIntent.putExtra("locationIncluded", false);
                        }
                        if (editMapsLocation) { //Start the activity so that the person can edit the location
                            mapsIntent.putExtra(MapsActivity.MAP_TYPE, MapsActivity.ADD_EDIT_LOCATION);
                            startActivityForResult(mapsIntent, MapsActivity.ADD_EDIT_LOCATION_REQUEST_CODE);
                        } else { //Start the maps activity in view only mode
                            mapsIntent.putExtra(MapsActivity.MAP_TYPE, MapsActivity.VIEW_LOCATION);
                            startActivityForResult(mapsIntent, MapsActivity.VIEW_LOCATION_REQUEST_CODE);
                        }
                    }
                });

            }
        }
        //Draw the map
        if(mMap!=null) {

            mMap.clear();
            addMarker();
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Disable scrolling when moving around the map to fix scrolling bug
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        addMarker();
    }

    /**
     * Add the google maps marker and zoom in on the location.
     */
    private void addMarker() {
        if(markerLoc!=null) {
            mMap.addMarker(new MarkerOptions().position(markerLoc).title(markerText));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLoc,(float) MARKER_ZOOM));
            //marker.showInfoWindow();
        }else{
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation,(float) DEFAULT_ZOOM));
        }
    }
}