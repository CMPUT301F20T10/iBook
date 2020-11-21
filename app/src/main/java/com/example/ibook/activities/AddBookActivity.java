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
import com.example.ibook.entities.User;
import com.example.ibook.fragment.ScanFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 *
 */
public class AddBookActivity extends AppCompatActivity implements ScanFragment.OnFragmentInteractionListener {

    private EditText bookNameEditText;
    private EditText authorEditText;
    private EditText dateEditText;
    private EditText isbnEditText;
    private EditText descriptionEditText;
    private Button cancelButton;
    private Button completeButton;
    private Button scanButton;
    private ImageView imageView;

    private String userID;
    private String bookID;

    private final int REQ_CAMERA_IMAGE = 1;
    private final int REQ_GALLERY_IMAGE = 2;
    private boolean imageAdded;
    public static String bookID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        imageAdded = false;

        // go back when clicking cancel
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // complete editing
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = bookNameEditText.getText().toString();
                final String author = authorEditText.getText().toString();
                final String date = dateEditText.getText().toString();
                final String isbn = isbnEditText.getText().toString();
                final String description = descriptionEditText.getText().toString();

                if (validation(title, author, isbn, date)) {

                    userID = MainActivity.database.getCurrentUserUID();
                    bookID = MainActivity.database.getDb().collection("books").document().getId();

                    // A better manner to add the book: also add the book id when the book is
                    // added to the book list
                    Book book = new Book(title, author, date, description, isbn, userID);
                    book.setBookID(bookID);
                    if(imageAdded) {//Upload the image
                                        MainActivity.database.uploadImage(imageView,bookID);
                                    }

                    final DocumentReference userDoc =
                            MainActivity.database.getDb().collection("users").document(userID);
                    userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            // By checking the code, we can find that the document snapshot is
                            // the user class, but we just use it as the hash map. The method we
                            // did before is so complex but it did the same thing as below.
                            // Actually the previous method also update the whole user document.
                            // So why not just did it as below.

                            // Also we can update the user class of application
                            // it can update the book list in the user class
                            MainActivity.user = documentSnapshot.toObject(User.class);
                            MainActivity.user.addToBookList(book);
                            userDoc.set(MainActivity.user);
                        }
                    });

                    MainActivity.database.getDb().collection("books").document(bookID).set(book);
                    finish();
                }
            }
        });


        // choose a image
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });

        // scan ISBN
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ScanFragment().show(getSupportFragmentManager(), "Scan ISBN");
            }
        });
    }

    private boolean validation(String title, String author, String isbn, String data) {
        if (title.length() <= 0) {
            Toast.makeText(getApplicationContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (author.length() <= 0) {
            Toast.makeText(getApplicationContext(), "Author cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (isbn.length() <= 0) {
            Toast.makeText(getApplicationContext(), "ISBN cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        //TODO: Data format checking
        return true;
    }

    // let user choose to use camera or gallery
    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Upload Image")
                .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // second parameter : request code

                        Toast.makeText(getBaseContext(), "There is a bug with camera, please use " +
                                "gallery", Toast.LENGTH_SHORT).show();
                        // TODO: there is a bug when using camera, maybe because of MediaStore
                        //  library
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
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                            selectedImage);
                    imageView.setImageBitmap(bitmap);
                    imageAdded = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //imageView.setImageURI(selectedImage);
            }
        }
    }
//    private void onSuccessChangePhoto(Bitmap bitmap) {
//        //Intent intent = new Intent();
//        //intent.putExtra("PHOTO_CHANGE", bitmap);
//        //setResult(1, intent);
//        // Comment: so far, we can only let user upload photo, but can't store it
//        //      Thus, unfortunately, it won't be passed back to the previous activity
//        // TODO: figure out how to scale image, compress it and store it to database
//    }

    @Override
    public void onOkPressed(String ISBN) {
        isbnEditText.setText(ISBN);
    }
}