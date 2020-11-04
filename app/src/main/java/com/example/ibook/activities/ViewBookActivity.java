package com.example.ibook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ibook.R;
import com.example.ibook.entities.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Document;

public class ViewBookActivity extends AppCompatActivity {
    private String userID;
    private Book book;
    private int bookNumber;

    private TextView bookNameTextView;
    private TextView authorTextView;
    private TextView dateTextView;
    private TextView isbnTextView;
    private ImageView imageView;
    private Button edit_button;
    //private Button delete_button;
    private FirebaseFirestore db;
    private User user;
    private DocumentReference docRef;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_book);

        bookNameTextView = findViewById(R.id.ViewBookName);
        authorTextView = findViewById(R.id.ViewAuthor);
        dateTextView = findViewById(R.id.ViewDate);
        isbnTextView = findViewById(R.id.ViewISBN);
        edit_button = findViewById(R.id.btn_edit_book);

        Intent intent = getIntent();
        userID = intent.getStringExtra("USER_ID");
        bookNumber = intent.getIntExtra("BOOK_NUMBER", 0);
        // Toast.makeText(getBaseContext(), String.valueOf(bookNumber), Toast.LENGTH_SHORT).show();
        // Toast.makeText(getBaseContext(), userID, Toast.LENGTH_SHORT).show();

        user = new User();
        docRef = user.getDocumentReference();
        db = FirebaseFirestore.getInstance();
        getBookData();

        //TODO !: imageView

        //TODO 2: ViewBookActivity with 4 kinds of interaction

        //TODO 3: ViewBookActivity conducted by the owner of the book

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewBookActivity.this, EditBookActivity.class);
                intent.putExtra("ID", userID);
                intent.putExtra("bookNumber", bookNumber);
                startActivity(intent);
            }
        });
    }

    public void delete_book(View view) {
        DocumentReference docRef = db.collection("users").document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data;
                        data = document.getData();
                        ArrayList<Book> books = (ArrayList<Book>) document.getData().get("BookList");
                        books.remove(bookNumber);
                        data.put("BookList", books);
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

    //    TODO: Page could not refresh
    @Override
    protected void onResume() {
        super.onResume();
        getBookData();
    }


    private void getBookData() {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<Book> hashList = (ArrayList<Book>) document.get("BookList");
                        Map<String, Object> convertMap = (Map<String, Object>) hashList.get(bookNumber);
                        book = new Book(
                                String.valueOf(convertMap.get("title")),
                                String.valueOf(convertMap.get("author")),
                                String.valueOf(convertMap.get("date")),
                                (String.valueOf(convertMap.get("description"))),
                                //from_string_to_enum(String.valueOf(convertMap.get("status"))),
                                Book.Status.Available,
                                String.valueOf(convertMap.get("isbn"))
                        );
                        bookNameTextView.setText(book.getTitle());
                        authorTextView.setText(book.getAuthor());
                        dateTextView.setText(book.getDate());
                        isbnTextView.setText(book.getIsbn());
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
