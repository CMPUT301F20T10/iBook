package com.example.ibook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ibook.R;
import com.example.ibook.entities.Book;
import com.example.ibook.entities.User;
import com.example.ibook.fragment.ScanFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


/**
 *
 */
public class AddBookActivity extends AppCompatActivity implements ScanFragment.OnFragmentInteractionListener {
    private User user;
    private Book book;
    private EditText bookNameEditText;
    private EditText authorEditText;
    private EditText dateEditText;
    private EditText isbnEditText;
    private Button cancelButton;
    private Button completeButton;
    private Button scanButton;
    private ImageView imageView;
    private FirebaseFirestore db;
    private String userID;
    private ArrayList<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_book_screen);
        bookNameEditText = findViewById(R.id.editTextBookName);
        authorEditText = findViewById(R.id.editTextAuthor);
        dateEditText = findViewById(R.id.editTextDate);
        isbnEditText = findViewById(R.id.editTextISBN);

        cancelButton = findViewById(R.id.cancelButton);
        completeButton = findViewById(R.id.completeButton);
        scanButton = findViewById(R.id.scan_button);
        imageView = findViewById(R.id.imageView);

        db = FirebaseFirestore.getInstance();
        books = new ArrayList<>();

        Intent intent = getIntent();
        userID = intent.getStringExtra("USER_ID");

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String bookName = bookNameEditText.getText().toString();
                final String authorName = authorEditText.getText().toString();
                final String date = dateEditText.getText().toString();
                final String isbn = isbnEditText.getText().toString();
                //photo // todo: upload photo for the book

                if (bookName.length() > 0
                        && authorName.length() > 0
                        && date.length() > 0
                        && isbn.length() > 0) {


                    DocumentReference docRef = db.collection("users").document(userID);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();

                                if (document.exists()) {
                                    Map<String, Object> data = new HashMap();
                                    data = document.getData();

                                    //make a new book object
                                    Book book = new Book(bookName,authorName,date,isbn);

                                    // add book to current users booklist
                                    SignUpActivity.user.addBook(book);

                                    //Add to "book" collections in database
                                    SignUpActivity.database.getBookDocumentReference().set(book);
                                    //Add the book to  "user" Collections in database
                                    SignUpActivity.database.getUserDocumentReference().set(SignUpActivity.user);

//                                    books = (ArrayList<Book>) document.getData().get("BookList");
//                                    books.add(new Book(bookName, authorName, date, isbn));





//                                    data.put("BookList", books);
//                                    db.collection("users")
//                                            .document(userID).update(data);
                                    Toast.makeText(getBaseContext(), "Add book successfully!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent();
                                    setResult(1, intent);
                                    finish();
                                } else {
                                    Toast.makeText(getBaseContext(), "No such document", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getBaseContext(), "got an error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(getBaseContext(), "Please input full information", Toast.LENGTH_SHORT).show();
                }

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Image!", Toast.LENGTH_SHORT).show();
                // TODO: Open gallery or camera
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ScanFragment().show(getSupportFragmentManager(), "Scan ISBN");
            }
        });
    }

    @Override
    public void onOkPressed(String ISBN) {
        isbnEditText.setText(ISBN);
    }
}