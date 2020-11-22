package com.example.ibook.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ibook.R;
import com.example.ibook.entities.Book;
import com.example.ibook.entities.BookRequest;
import com.example.ibook.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ViewBookActivity extends AppCompatActivity {
    private String userID;
    private Book book;
    private int bookNumber;
    private int isOwner;
    private String bookISBN;

    private TextView bookNameTextView;
    private TextView authorTextView;
    private TextView dateTextView;
    private TextView isbnTextView;
    private TextView descriptionTextView;
    private ImageView imageView;

    private TextView edit_button;
    private Button backButton;
    //private Button delete_button;
    private Button delete_button;
    private Button request_button;

    private FirebaseFirestore db;
    private User user;
    private DocumentReference docRef;
    FirebaseAuth uAuth;
    Book selectedBook;

    public static String requestReceiverID;
    public static User requestReceiver;
    private User currentUser;
    String userName;

    private static boolean imageChanged;

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
        descriptionTextView = findViewById(R.id.descriptionView2);
        edit_button = findViewById(R.id.editButton);
        //delete_button = findViewById(R.id.btn_delete_book);
        request_button = findViewById(R.id.btn_request_book);

        imageView = findViewById(R.id.imageView);
        backButton = findViewById(R.id.cancelButton);
        delete_button = findViewById(R.id.btn_delete_book);

        imageChanged = false;

        uAuth = FirebaseAuth.getInstance();
        userID = uAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        docRef = db.collection("users").document(userID);//creating a document for the use

        Intent intent = getIntent();
        userID = intent.getStringExtra("USER_ID");

        // The number of clicked book on the booklist
        bookNumber = intent.getIntExtra("BOOK_NUMBER", 0);

        // if isOwner = 0, the activity is being visited from user's own bookList page
        // if isOwner = -1, the activity is being visited by other users (a random visit)
        isOwner = intent.getIntExtra("IS_OWNER", 0);

        if (isOwner == -1) {
            // we hide edit/delete button if it's not the owner
            edit_button.setVisibility(View.GONE);
            delete_button.setVisibility(View.GONE);
            delete_button.setEnabled(false); // make it disabled too
            // Toast.makeText(getBaseContext(), String.valueOf(bookNumber), Toast.LENGTH_SHORT).show();
            // Toast.makeText(getBaseContext(), userID, Toast.LENGTH_SHORT).show();

//            user = new User();
//            docRef = user.getDocumentReference();
//            db = FirebaseFirestore.getInstance();
        }
        else {
            // hide request button if the current user is the owner.
            request_button.setVisibility(View.GONE);
            request_button.setEnabled(false); // disable the button too
        }

        bookISBN = intent.getStringExtra("BOOK_ISBN");
        getBookData();

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewBookActivity.this, EditBookActivity.class);
                intent.putExtra("ID", userID);
                intent.putExtra("bookNumber", bookNumber);
                startActivityForResult(intent,3);
                //setResult(1, intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        request_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // add the book to requested list
                final DocumentReference docRef = db.collection("users").document(userID);

                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            //user object intialized
                            currentUser = documentSnapshot.toObject(User.class);
//                            ArrayList<Book> books;
//                            for(bookID in BOOK){
//                                db.collection("users").document(bookId).get().addOnSuccessListener(
//
//                                        newBook = toobect(Book.class);
//                                        books.add(newbook);
//                                );
//
//                                adapter.set(newBook);
//                            }
                            final DocumentReference docRefRequestReceiver = db.collection("users").document(requestReceiverID);

                            docRefRequestReceiver.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    requestReceiver = documentSnapshot.toObject(User.class);
                                    requestReceiver.addToNotificationList(currentUser.getUserName() + " wants to borrow your book " + selectedBook.getTitle());
                                    //updating notificaion list of the user in database
                                    docRefRequestReceiver.set(requestReceiver);
                                    Toast.makeText(getBaseContext(), "Coming here!", Toast.LENGTH_SHORT).show();


                                    BookRequest newRequest = new BookRequest(currentUser.getUserID(),requestReceiver.getUserID(),selectedBook.getBookID());
                                    db.collection("bookRequest").document().set(newRequest);

                                    //change book status
                                    System.out.println("Selected bookID: " + selectedBook.getBookID());

                                    selectedBook.setStatus(Book.Status.Requested);

                                    final DocumentReference bookRef = db.collection("books").document(selectedBook.getBookID());
                                    bookRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            bookRef.set(selectedBook);
                                            //TODO: Update the status of the book in the user collection bookList, the book collection has owner ID so you can use that to go to user collection
                                            //TODO: and uodate his booklist;s book status

                                            //maybe don't have to do this if we are always using the book collection and bookRequestCollection but still something to think about

                                        }
                                    });
                                }
                            });
                        }// if

                    }//onSuccess

                });


//                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                                       @Override
//                                                       public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                           if (task.isSuccessful()) {
//                                                               DocumentSnapshot document = task.getResult();
//                                                               if (document.exists()) {
//                                                                   ArrayList<Book> hashList = (ArrayList<Book>) document.get("requestedBookList");
//                                                                   String userName = (String) document.get("userName");
//                                                                   hashList.add(selectedBook);
//                                                                   docRef.update("requestedBookList", hashList).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                       @Override
//                                                                       public void onSuccess(Void aVoid) {
//                                                                           Toast.makeText(ViewBookActivity.this, "Added to request book list successfully", Toast.LENGTH_SHORT).show();
//                                                                       }
//                                                                   });
//                                                               }// if
//                                                           }
//                                                       }
//                                                   });
//
//                requestReceiverID = selectedBook.getOwnerID();
//
//                final DocumentReference docRefRequestReceiver = db.collection("users").document(requestReceiverID);
//
//                docRefRequestReceiver.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot document = task.getResult();
//                            if (document.exists()) {
//                                ArrayList<String> hashList = (ArrayList<String>) document.get("notificationList");
//                                String message = userName + "wants to borrow the book named" + selectedBook.getTitle();
//                                hashList.add(message);
//                                docRefRequestReceiver.update("notificationList", hashList).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        Toast.makeText(ViewBookActivity.this, "Added to request book list successfully", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }// if
//                        }
//                    }
//                });


                System.out.println("Coming before db");
               db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                   @Override
                   public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                   }
               });

//                System.out.println(requestReceiver.getEmail() + " " + clickedBook.getTitle() + " " +MainActivity.user.getUserName());
//
//                Toast.makeText(ViewBookActivity.this, "title: " + clickedBook.getTitle() + "Username: " + MainActivity.user.getUserName() , Toast.LENGTH_SHORT).show();
//                requestReceiver.addToNotificationList(MainActivity.user.getUserName() + "wants to borrow the book" + clickedBook.getTitle());
//                MainActivity.database.getDb().collection("users").document(requestReceiverID).set(requestReceiver).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(ViewBookActivity.this, "Added to notification list successfully", Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                });
                Toast.makeText(getBaseContext(), "This function is coming soon!", Toast.LENGTH_SHORT).show();
                

            }//onClick
        }); //requestButton SetOnClickListener
    }



    public void delete_book(View view) {
        DocumentReference docRef = db.collection("users").document(userID);


        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // if the book exists, delete it
                    if (document.exists()) {
                        Map<String, Object> data;
                        data = document.getData();
                        ArrayList<Book> books = (ArrayList<Book>) document.getData().get("bookList");
                        //Need to delete image before book since image uses bookID for storage.
                        Map<String, Object> convertMap = (Map<String, Object>) books.get(bookNumber);
                        MainActivity.database.deleteImage(convertMap.get("bookID").toString());
                        books.remove(bookNumber);
                        data.put("bookList", books);
                        db.collection("users")
                                .document(userID).set(data);
                        Intent intent = new Intent();
                        setResult(1, intent);
                        finish();
                    } else {
                        //Log.d(TAG, "No such document");
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }


    /**
     * This method will be invoked when the user's focus comes back to ViewBookActivity
     * It will refresh the data from the database, so that if any data was updated, they will be displayed correctly
     * */
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

        if(resultCode == 4 && requestCode == 3){
            SystemClock.sleep(500);
            //Set new image if it changed.
            if(data.getExtras()!=null) {
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

    /**
     * This method will retrieve the data from the database,
     * and assign the data to the TextViews, so that they are displayed correctly.
     * */
    private void getBookData() {
        // if it's not owner's book, we cannot access the book from user
        // so find the book from book collection
        if (isOwner == -1) {
            db.collection("books")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                           selectedBook = null;
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String checkISBN = (String)document.get("isbn");
                                    requestReceiverID = (String)document.get("owner");
                                    if (checkISBN.equals(bookISBN)){
                                        selectedBook = new Book(
                                                String.valueOf(document.get("title")),
                                                String.valueOf(document.get("authors")),
                                                String.valueOf(document.get("date")),
                                                String.valueOf(document.get("description")),
                                                Book.Status.Available,
                                                String.valueOf(document.get("isbn")),
                                                String.valueOf(document.get("owner")),
                                                String.valueOf(document.get("bookID"))
                                        );


                                        //Toast.makeText(getBaseContext(), "match book!", Toast.LENGTH_SHORT).show();
                                        break;
                                    }

                                }
                                bookNameTextView.setText(selectedBook.getTitle());
                                authorTextView.setText(selectedBook.getAuthors());
                                dateTextView.setText(selectedBook.getDate());
                                isbnTextView.setText(selectedBook.getIsbn());
                                if(selectedBook.getDescription()!= null) {
                                    descriptionTextView.setText(selectedBook.getDescription());
                                }
                                MainActivity.database.downloadImage(imageView, selectedBook.getBookID(),true);

                            } else {
                                Toast.makeText(getBaseContext(), "got an error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        } else {
            // if it's owner's book, find it from user's collection
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            ArrayList<Book> hashList = (ArrayList<Book>) document.get("bookList");
                            Map<String, Object> convertMap = (Map<String, Object>) hashList.get(bookNumber);
                            book = new Book(
                                    String.valueOf(convertMap.get("title")),
                                    String.valueOf(convertMap.get("authors")),
                                    String.valueOf(convertMap.get("date")),
                                    (String.valueOf(convertMap.get("description"))),
                                    //from_string_to_enum(String.valueOf(convertMap.get("status"))),
                                    Book.Status.Available,
                                    String.valueOf(convertMap.get("isbn")),
                                    String.valueOf(convertMap.get("owner")),
                                    String.valueOf(convertMap.get("bookID"))
                            );
                            bookNameTextView.setText(book.getTitle());
                            authorTextView.setText(book.getAuthors());
                            dateTextView.setText(book.getDate());
                            isbnTextView.setText(book.getIsbn());
                            if(book.getDescription()!= null) {
                                descriptionTextView.setText(book.getDescription());
                            }
                            if(!imageChanged) {
                                MainActivity.database.downloadImage(imageView, book.getBookID(), true);
                            }imageChanged = false;

                        } else {
                            //Log.d(TAG, "No such document");
                        }
                    } else {
                        //Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
    }

}