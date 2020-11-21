package com.example.ibook.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.IOException;
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
    private EditText descriptionEditText;
    private Button cancelButton;
    private Button completeButton;
    private Button scanButton;
    private ImageView imageView;
    private FirebaseFirestore db;
    private boolean imageAdded;
    private String userID;
    private int bookNumber;
    private Book originalBook;

    private final int REQ_CAMERA_IMAGE = 1;
    private final int REQ_GALLERY_IMAGE = 2;


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
        descriptionEditText = findViewById(R.id.descriptionEditor);

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
                String description = "";
                if(descriptionEditText.getText()!= null) {
                    description = descriptionEditText.getText().toString();
                }
                if (bookName.length() > 0
                        && authorName.length() > 0
                        && date.length() > 0
                        && isbn.length() > 0) {
//                    TODO:add more value
                    Book currentBook = new Book(bookName, authorName, date, description, originalBook.getStatus(), isbn, MainActivity.user.getUserID(), originalBook.getBookID());

                    // update book if there's a change
                    if ((!currentBook.equals(originalBook)) || imageAdded) {
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

        // choose a image
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
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
                        authorEditText.setText(book.getAuthor());
                        dateEditText.setText(book.getDate());
                        isbnEditText.setText(book.getIsbn());
                        // TODO: forgot to let the user edit description, improve it later
                        if(book.getDescription()!= null) {
                            descriptionEditText.setText(book.getDescription());
                        }
                        MainActivity.database.downloadImage(imageView, book.getBookID());

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
                        //Update the image if it changed
                        if(imageAdded) {
                            MainActivity.database.uploadImage(imageView, book.getBookID());
                        }
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



    /**
     * This method will show a dialog and prompts the user to select an image from gallery/camera
     * It invokes the API of MediaStore to finish the taking picture action.
     * */
    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Upload Image")
                .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // second parameter : request code

                        Toast.makeText(getBaseContext(), "There is a bug with camera, please use gallery", Toast.LENGTH_SHORT).show();
                        // TODO: there is a bug when using camera, maybe because of MediaStore library
                        // startActivityForResult(intent, REQ_CAMERA_IMAGE);

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

    /**
     * This method processes the image after the user finishes taking picture/selecting image
     * , based on the method used to upload the picture (camera/gallery).
     * It calls onSuccessChangePhoto to store the changed image into the database.
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CAMERA_IMAGE) {
            // result of camera
            if (resultCode == RESULT_OK) {
                // get image data
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);
                imageAdded = true;
            }

        } else if (requestCode == REQ_GALLERY_IMAGE) {
            if (resultCode == RESULT_OK) {
                // get image data
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    imageView.setImageBitmap(bitmap);
                    imageAdded = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //imageView.setImageURI(selectedImage);
            }
        }
    }

//    /**
//     * This method will process the given image and store it in the database.
//     * */
//    private void onSuccessChangePhoto(Bitmap bitmap) {
//        //Intent intent = new Intent();
//        //intent.putExtra("PHOTO_CHANGE", bitmap);
//        //setResult(1, intent);
//        // Comment: so far, we can only let user upload photo, but can't store it
//        //      Thus, unfortunately, it won't be passed back to the previous activity
//        // TODO: figure out how to scale image, compress it and store it to database
//    }

}