package com.example.ibook.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 *
 */
public class AddBookActivity extends AppCompatActivity implements ScanFragment.OnFragmentInteractionListener {
    //    private User user;
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
    private ArrayList<Book> books;
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

        books = new ArrayList<>();

        Intent intent = getIntent();
        userID = intent.getStringExtra("USER_ID");

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
                final String bookName = bookNameEditText.getText().toString();
                final String authorName = authorEditText.getText().toString();
                final String date = dateEditText.getText().toString();
                final String isbn = isbnEditText.getText().toString();
                final String description = descriptionEditText.getText().toString();

                // check full information
                if (bookName.length() > 0
                        && authorName.length() > 0
                        && dateIsValid(date)
                        && isbnCheck(isbn)) {
                    finish();

                    bookID = MainActivity.database.getDb().collection("books").document().getId();
                    final Book newBook = new Book(bookName, authorName, date, description, Book.Status.Available, isbn, userID, bookID);
                    MainActivity.database.getDb().collection("books").document(bookID).set(newBook);

                    Toast.makeText(getBaseContext(), "got book id inside the scope too" + bookID, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent();
                    // If an image was added we upload it or else it uploads the default image
                    try {
                        if (!imageAdded) { //Need to upload the proper stock image
                            //app:srcCompat="@android:drawable/ic_menu_gallery"
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_gallery);
                            storeLocally(bitmap);
                        }
                        MainActivity.database.uploadImage(openFileInput(MainActivity.database.tempFileName), bookID);
                        intent.putExtra("CHANGED_IMAGE", MainActivity.database.tempFileName);
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "Image upload failed please try again", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    //setResult(1, intent);
                } else if (!(bookName.length() > 0
                        && authorName.length() > 0)){
                    Toast.makeText(getBaseContext(), "Please input full information", Toast.LENGTH_SHORT).show();
                }
                //finish();
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

    // let user choose to use camera or gallery
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
                .setNeutralButton("Delete Image", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        imageView.setImageResource(android.R.color.transparent);
                        imageAdded = false;
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
                storeLocally(bitmap);
                imageView = EditBookActivity.scaleAndSetImage(bitmap, imageView);
                imageAdded = true;
            }

        } else if (requestCode == REQ_GALLERY_IMAGE) {
            if (resultCode == RESULT_OK) {
                // get image data
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    storeLocally(bitmap);
                    imageView = EditBookActivity.scaleAndSetImage(bitmap, imageView);
                    imageAdded = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //imageView.setImageURI(selectedImage);
            }
        }
    }

    private void storeLocally(Bitmap bitmap) {
        try {//Pass the image through a temporary link on local storage
            //Large bitmaps will crash the app.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            FileOutputStream fo = openFileOutput(MainActivity.database.tempFileName, Context.MODE_PRIVATE);
            fo.write(baos.toByteArray());
            // remember close file output
            baos.close();
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean isbnCheck(String isbn){
        boolean valid;
        if (isbn.matches("[0-9]+") && (isbn.length() == 10 || isbn.length() == 13)) {
            valid = true;
        }
        else{
            valid = false;
            Toast.makeText(getBaseContext(), "ISBN must be 10 or 13 digit number", Toast.LENGTH_SHORT).show();

        }
        return valid;
    }
    public boolean dateIsValid(String date) {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        format.setLenient(false);
        try {
            format.parse(date);
            return true;
        } catch (ParseException e) {
            Toast.makeText(getBaseContext(), "Date should be in specified format", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    @Override
    public void onOkPressed(String ISBN) {
        isbnEditText.setText(ISBN);
    }
}