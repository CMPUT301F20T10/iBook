package com.example.ibook.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ibook.R;
import com.example.ibook.entities.Book;
import com.example.ibook.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();
        userID = intent.getStringExtra("USER_ID");
        bookNumber = intent.getIntExtra("BOOK_NUMBER", 0);
        // Toast.makeText(getBaseContext(), String.valueOf(bookNumber), Toast.LENGTH_SHORT).show();
        // Toast.makeText(getBaseContext(), userID, Toast.LENGTH_SHORT).show();

        user = new User();
        docRef = user.getDocumentReference();
        db = FirebaseFirestore.getInstance();
        getBookData();

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

    @Override
    protected void onResume() {
        super.onResume();
        SystemClock.sleep(500);
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

    public void changeBookPhoto(View view) {
        // Toast.makeText(getBaseContext(), "changePhoto", Toast.LENGTH_SHORT).show();
        showImagePickerDialog();

    }

    private void showImagePickerDialog() {
        final int REQ_CAMERA_IMAGE = 1;
        final int REQ_GALLERY_IMAGE = 2;
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Upload Image")
                .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // second parameter : request code

                        Toast.makeText(getBaseContext(), "There is a bug when using camera", Toast.LENGTH_SHORT).show();
                        // TODO: there is a bug when using camera!
                        //startActivityForResult(intent, REQ_CAMERA_IMAGE);

                    }
                })
                .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);

                        startActivityForResult(intent, REQ_GALLERY_IMAGE);
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final int REQ_CAMERA_IMAGE = 1;
        final int REQ_GALLERY_IMAGE = 2;
        if (requestCode == REQ_CAMERA_IMAGE) {
            // result of camera
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);
                onSuccessChangePhoto(bitmap);
            }

        } else if (requestCode == REQ_GALLERY_IMAGE) {
            if (resultCode == RESULT_OK) {

                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    imageView.setImageBitmap(bitmap);
                    onSuccessChangePhoto(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //imageView.setImageURI(selectedImage);
            }
        }
    }

    private void onSuccessChangePhoto(Bitmap bitmap) {
        //Intent intent = new Intent();
        //intent.putExtra("PHOTO_CHANGE", bitmap);
        //setResult(1, intent);
        // TODO: store images to database
    }
}
