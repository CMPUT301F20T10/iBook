package com.example.ibook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ibook.R;
import com.example.ibook.entities.Book;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditBookActivity extends AppCompatActivity implements ScanFragment.OnFragmentInteractionListener {

    private EditText bookNameEditText;
    private EditText authorEditText;
    private EditText dateEditText;
    private EditText isbnEditText;
    private EditText descritionEditText;
    private Button cancelButton;
    private Button completeButton;
    private Button scanButton;
    private ImageView imageView;
    private FirebaseFirestore db;

    private String userID;
    private int bookNumber;
    private Book originalBook;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide the top bar and make it full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar

        setContentView(R.layout.activity_add_or_edit_book_screen);

        bookNameEditText = findViewById(R.id.titleEditor);
        authorEditText = findViewById(R.id.authorEditor);
        dateEditText = findViewById(R.id.dateEditor);
        isbnEditText = findViewById(R.id.isbnEditor);
        descritionEditText = findViewById(R.id.descriptionEditor);

        cancelButton = findViewById(R.id.cancelButton);
        completeButton = findViewById(R.id.completeButton);
        scanButton = findViewById(R.id.scanButton);
        imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();
        userID = intent.getStringExtra("ID");
        bookNumber = intent.getIntExtra("bookNumber", 0);

        getBookData();

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String bookName = bookNameEditText.getText().toString();
                final String authorName = authorEditText.getText().toString();
                final String date = dateEditText.getText().toString();
                final String isbn = isbnEditText.getText().toString();
                final String description =descritionEditText.getText().toString();
                if (bookName.length() > 0
                        && authorName.length() > 0
                        && date.length() > 0
                        && isbn.length() > 0) {
//                    TODO:add more value
                    Book currentBook = new Book(bookName, authorName, date,description, isbn,userID);

                    // update book if there's a change
                    if (!currentBook.equals(originalBook)) {
                        updateBook(currentBook);
                    }
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "Please input full information", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ScanFragment().show(getSupportFragmentManager(), "Scan ISBN");
            }
        });

    }

    private void getBookData() {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(userID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        ArrayList<Book> hashList = (ArrayList<Book>) document.get("bookList");
                        Map<String, Object> convertMap = (Map<String, Object>) hashList.get(bookNumber);
                        Book book = new Book(
                                String.valueOf(convertMap.get("title")),
                                String.valueOf(convertMap.get("author")),
                                String.valueOf(convertMap.get("date")),
                                String.valueOf(convertMap.get("description")),
                                //from_string_to_enum(String.valueOf(convertMap.get("status"))),
                                Book.Status.Available,
                                String.valueOf(convertMap.get("isbn")),
                                String.valueOf(convertMap.get("owner")),
                                String.valueOf(convertMap.get("bookID"))


                        );
                        originalBook = book;

                        bookNameEditText.setText(book.getTitle());
                        authorEditText.setText(book.getAuthors());
                        dateEditText.setText(book.getDate());
                        isbnEditText.setText(book.getIsbn());
                        // TODO: forgot to let the user edit description, improve it later
//                      // descriptionEditText.setText(book.getDescription());

                        // photoEditText todo: photo format path

                    } else {
                        //Log.d(TAG, "No such document");
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    // update book info
    private void updateBook(final Book book) {
        DocumentReference docRef = db.collection("users").document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = new HashMap();
                        data = document.getData();
                        ArrayList<Book> books = (ArrayList<Book>) document.getData().get("bookList");
                        books.set(bookNumber, book);
                        data.put("bookList", books);
                        db.collection("users").document(userID).set(data);
                    } else {
                        Toast.makeText(getApplicationContext(), "No such document", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "got an error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onOkPressed(String ISBN) {
        isbnEditText.setText(ISBN);
    }
}
