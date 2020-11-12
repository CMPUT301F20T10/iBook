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
import com.google.android.gms.tasks.OnSuccessListener;
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

                if (bookName.length() > 0
                        && authorName.length() > 0
                        && date.length() > 0
                        && isbn.length() > 0) {
//                    TODO:add more value
                    originalBook.setTitle(bookName);
                    originalBook.setAuthor(authorName);
                    originalBook.setDate(date);
                    originalBook.setIsbn(isbn);

                    updateBook(originalBook);
                    finish();
                } // if
                else {
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

        DocumentReference docRef = MainActivity.database.getUserDocumentReference();

        Map<String, Object> convertMap = (Map<String, Object>) MainActivity.user.getBookList().get(bookNumber);
        Book book = new Book(
                                String.valueOf(convertMap.get("title")),
                                String.valueOf(convertMap.get("author")),
                                String.valueOf(convertMap.get("date")),
                                String.valueOf(convertMap.get("description")),
                                //from_string_to_enum(String.valueOf(convertMap.get("status"))),
                                Book.Status.Available,
                                String.valueOf(convertMap.get("isbn"))
                                );

//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//
//                        ArrayList<Book> hashList = (ArrayList<Book>) document.get("BookList");
//                        Map<String, Object> convertMap = (Map<String, Object>) hashList.get(bookNumber);

//
                        originalBook = book;

                        bookNameEditText.setText(book.getTitle());
                        authorEditText.setText(book.getAuthor());
                        dateEditText.setText(book.getDate());
                        isbnEditText.setText(book.getIsbn());
                        // TODO: forgot to let the user edit description, improve it later
//                      // descriptionEditText.setText(book.getDescription());

                        // photoEditText todo: photo format path

    }//getBookData






    // update book info
    private void updateBook(final Book book) {
        DocumentReference docRef = MainActivity.database.getUserDocumentReference();
        MainActivity.user.getBookList().set(bookNumber,book);
        docRef.set(MainActivity.user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Successfully updated users collection", Toast.LENGTH_SHORT).show();

            }

        });
        String bookId = book.getBookID();
        DocumentReference bookDocRef = MainActivity.database.getBookDocumentReference(bookId);
        bookDocRef.set(book).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Successfully updated book collection", Toast.LENGTH_SHORT).show();

            }
        });


    }// updateBook

    @Override
    public void onOkPressed(String ISBN) {
        isbnEditText.setText(ISBN);
    }
}
