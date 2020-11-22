package com.example.ibook.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    FirebaseAuth uAuth;

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
        uAuth = FirebaseAuth.getInstance();
        userID = uAuth.getCurrentUser().getUid();

        bookNumber = intent.getIntExtra("bookNumber", 0);

        getBookData();

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String bookName = bookNameEditText.getText().toString();
                final String authorName = authorEditText.getText().toString();
                final String date = dateEditText.getText().toString();
                final String isbn = isbnEditText.getText().toString();
                final String description = descriptionEditText.getText().toString();

                //Toast.makeText(getBaseContext(), authorName, Toast.LENGTH_SHORT).show();

                if (bookName.length() > 0
                        && authorName.length() > 0
                        && date.length() > 0
                        && isbn.length() > 0) {
//                    TODO:add more value

                    Book currentBook = new Book(bookName, authorName, date, description, originalBook.getStatus(), isbn, userID, originalBook.getBookID());
                    Intent intent = new Intent();
                    // update book if there's a change
                    if ((!currentBook.equals(originalBook)) || imageAdded) {
                        updateBook(currentBook);
                        if(imageAdded) {
                            try{
                                MainActivity.database.uploadImage(openFileInput(MainActivity.database.tempFileName), currentBook.getBookID());
                                intent.putExtra("CHANGED_IMAGE", MainActivity.database.tempFileName);
                            }catch (Exception e){
                                Toast.makeText(getBaseContext(), "Image upload failed please try again", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }
                    setResult(4, intent);

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
                                String.valueOf(convertMap.get("authors")),
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

                        if(book.getDescription()!= null) {
                            descriptionEditText.setText(book.getDescription());
                        }
                        MainActivity.database.downloadImage(imageView, book.getBookID(), true);

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

                        Map<String, Object> data;
                        data = document.getData();
                        ArrayList<Book> books = (ArrayList<Book>) document.getData().get("bookList");
                        books.set(bookNumber, book);
                        data.put("bookList", books);
                        db.collection("users").document(userID).set(data);

                        // also update book info to the book collection
                        data = new HashMap();
                        data.put("authors", book.getAuthors());
                        data.put("date", book.getDate());
                        data.put("description", book.getDescription());
                        data.put("isbn", book.getIsbn());
                        data.put("owner", book.getOwner());
                        data.put("status", "Available");
                        data.put("title", book.getTitle());
                        db.collection("books").document(book.getBookID()).update(data);
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
                storeLocally(bitmap);
                imageView = scaleAndSetImage(bitmap, imageView);
                imageAdded = true;
            }

        } else if (requestCode == REQ_GALLERY_IMAGE) {
            if (resultCode == RESULT_OK) {
                // get image data
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    storeLocally(bitmap);
                    imageView = scaleAndSetImage(bitmap, imageView);
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
            Log.i("image" , "Stored locally "+ MainActivity.database.tempFileName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static ImageView scaleAndSetImage(Bitmap bitmap, ImageView imageView) {
        //bitmap = Bitmap.createScaledBitmap(bitmap, imageView.getWidth(), imageView.getHeight(), true);
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        imageView.setAdjustViewBounds(true);
        //Centers the image and fits it to the imageView
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageBitmap(bitmap);
        return imageView;
    }

}
