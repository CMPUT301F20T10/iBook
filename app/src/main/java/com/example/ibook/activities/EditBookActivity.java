package com.example.ibook.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ibook.R;
import com.example.ibook.entities.Book;
import com.example.ibook.fragment.ScanFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * The add book activity class holds the activity when user choose to add a new book in the
 * application. User need to input the required information, including title, isbn and author.
 * User can also use camera to scan the isbn, and upload the photo to the book.
 */
public class EditBookActivity extends AppCompatActivity implements ScanFragment.OnFragmentInteractionListener {
    // the edit book surface
    private EditText bookNameEditText;
    private EditText authorEditText;
    private TextView dateEditText;
    private EditText isbnEditText;
    private EditText descriptionEditText;
    private Button cancelButton;
    private Button completeButton;
    private Button scanButton;
    private ImageView imageView;

    private FirebaseFirestore db;
    private boolean imageAdded;
    private String userID;
    private String bookID;
    private Book originalBook;
    FirebaseAuth uAuth;

    private final int REQ_CAMERA_IMAGE = 1;
    private final int REQ_GALLERY_IMAGE = 2;

    private Calendar calendar = Calendar.getInstance();


    /**
     * The function to initialize the view
     */
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
        setDate();

        Intent intent = getIntent();
        bookID = intent.getStringExtra("BOOK_ID");

        db = FirebaseFirestore.getInstance();
        uAuth = FirebaseAuth.getInstance();
        userID = uAuth.getCurrentUser().getUid();

        getBookData();
        setUpCancelButtonListener();
        setUpCompleteButtonListener();
        setUpImageViewListener();
        setUpScanButtonListener();

    }

    /**
     * The function to set up the listener of the cancel button
     */
    private void setUpCancelButtonListener() {
        // go back when clicking cancel
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * The function to set up the listener of the scan button
     */
    private void setUpScanButtonListener() {
        // when the scan button clicked, open fragment to scan ISBN
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ScanFragment().show(getSupportFragmentManager(), "Scan ISBN");
            }
        });
    }

    /**
     * The function to set up the listener of the image view
     */
    private void setUpImageViewListener() {
        // when the image view clicked, choose a image or delete the image
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });
    }

    /**
     * The function to set up the listener of the complete button, it will update the information of
     * the book
     */
    private void setUpCompleteButtonListener() {
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
                        && isbnIsValid(isbn)) {
//                    TODO:add more value

                    Book currentBook = new Book(bookName, authorName, date, description, originalBook.getStatus(), isbn, userID, originalBook.getBookID());
                    Intent intent = new Intent();
                    // update book if there's a change
                    if ((!currentBook.equals(originalBook)) || imageAdded) {
                        updateBook(currentBook);
                        if (imageAdded) {
                            try {
                                MainActivity.database.uploadImage(openFileInput(MainActivity.database.tempFileName), currentBook.getBookID());
                                intent.putExtra("CHANGED_IMAGE", MainActivity.database.tempFileName);
                            } catch (Exception e) {
                                Toast.makeText(getBaseContext(), "Image upload failed please try again", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }
                    setResult(4, intent);

                    finish();
                } else if (!(bookName.length() > 0
                        && authorName.length() > 0
                        && date.length() > 0)) {
                    Toast.makeText(getBaseContext(), "Please input full information", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    /**
     * The function to get the data of the current book, and display the data in the application
     */
    private void getBookData() {
        db.collection("books").document(bookID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        originalBook = documentSnapshot.toObject(Book.class);
                        bookNameEditText.setText(originalBook.getTitle());
                        authorEditText.setText(originalBook.getAuthors());
                        dateEditText.setText(originalBook.getDate());
                        isbnEditText.setText(originalBook.getIsbn());

                        if (originalBook.getDescription() != null) {
                            descriptionEditText.setText(originalBook.getDescription());
                        }
                        MainActivity.database.downloadImage(imageView, originalBook.getBookID(), true);

                    }
                });

    }

    /**
     * The function to update book info
     *
     * @param book the new object to update
     */
    private void updateBook(final Book book) {
        db.collection("books").document(book.getBookID()).set(book);
    }


    /**
     * get and display the isbn in the application
     *
     * @param ISBN the isbn got from the isbn scan fragment
     */
    @Override
    public void onOkPressed(String ISBN) {
        isbnEditText.setText(ISBN);
    }


    /**
     * This method will show a dialog and prompts the user to select an image from gallery/camera
     * It invokes the API of MediaStore to finish the taking picture action.
     */
    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Upload Image")
                .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // second parameter : request code

                        //Toast.makeText(getBaseContext(), "There is a bug with camera, please use gallery", Toast.LENGTH_SHORT).show();
                        // TODO: there is a bug when using camera, maybe because of MediaStore library
                        // startActivityForResult(intent, REQ_CAMERA_IMAGE);

                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(cameraIntent, REQ_CAMERA_IMAGE);
                        }

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
                        //imageView.setImageResource(android.R.drawable.ic_input_add);
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_gallery);
                        storeLocally(bitmap);
                        imageView = EditBookActivity.scaleAndSetImage(bitmap, imageView);
                        imageAdded = true;
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * This method processes the image after the user finishes taking picture/selecting image
     * , based on the method used to upload the picture (camera/gallery).
     * It calls onSuccessChangePhoto to store the changed image into the database.
     */
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

    /**
     * The function to store the image in the firebase
     *
     * @param bitmap the image object
     */
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
            Log.i("image", "Stored locally " + MainActivity.database.tempFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The function to scale the set the image in the surface, user can see the image in the
     * image view if the image exist.
     *
     * @param bitmap    the image object
     * @param imageView the image view in the surface
     *
     * @return
     */
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

    /**
     * The function to validate the isbn, the isbn should be a 10 or 13 number value
     *
     * @param isbn the isbn to validate
     *
     * @return the validation result of the given isbn code
     */
    public boolean isbnIsValid(String isbn) {
        if (isbn.matches("[0-9]+") && (isbn.length() == 10 || isbn.length() == 13)) {
            return true;
        } else {
            Toast.makeText(getBaseContext(), "ISBN must be 10 or 13 digit number", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    /**
     * The function to set up the date picker dialog to validate date input
     */
    public void setDate() {
        final DatePickerDialog.OnDateSetListener pickDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                formatDate();
            }
        };
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditBookActivity.this, pickDate, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    /**
     * The function to format the date got from the date picker.
     * <p>
     * Format the date to the "yyyy-MM-dd" format
     */
    public void formatDate() {
        String dateFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.CANADA);

        dateEditText.setText(simpleDateFormat.format(calendar.getTime()));

    }


}
