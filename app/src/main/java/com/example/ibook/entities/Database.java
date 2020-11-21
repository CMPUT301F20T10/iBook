package com.example.ibook.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ibook.activities.EditProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import static com.google.android.gms.tasks.Tasks.await;

/**
 * This class is created to have "seperation of concerns", meaning most of the database actions will be
 * performed via this class and once the database object is created it can be used throughout the app
 * without having to make new firebase variables in each class.( makes an static object of it only once in
 * signup or in login, depending on where the user goes first and then we can use that static obj
 * anywhere in all other classes
 */
public class Database {
    private FirebaseAuth uAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private static boolean uploadingImage;

    public Database(FirebaseAuth uAuth, FirebaseFirestore db) {
        this.uAuth = uAuth;
        this.db = db;
    }

    public Database(){
        this.uAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.storageReference = FirebaseStorage.getInstance().getReference();
        uploadingImage = false;
    }

    /**
     *
     * @return DocumentReference - returns the document reference of the current user
     */
    public DocumentReference getUserDocumentReference() {
        return this.db.collection("users").document(this.getCurrentUserUID());
    }//getUserDocumentReference



    public FirebaseAuth getuAuth() {
        return uAuth;
    }

    public FirebaseFirestore getDb() {
        return db;
    }//getDb

    /**
     * adds a user to the database when the user signs up
     * @param user - a User class object
     */
    public void addUser(User user){
        this.db.collection("users").document(getCurrentUserUID()).set(user);
    }//addUser

    /**
     *
     * @return - returns the current user's unique ID
     */
    public String getCurrentUserUID() {

        return this.uAuth.getCurrentUser().getUid();
    }//getCurrentUserUID

    public DocumentReference getBookDocumentReference(String bookId){
        return this.db.collection("books").document(bookId);
    }//getBookDocumentReference


    //Upload image to database
    public boolean uploadImage(ImageView imageView, String bookId) {
        final boolean[] success = {false};
        if(bookId == null) {
            return success[0];
        }
        uploadingImage = true;
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageTask storageTask = storageReference.child("coverImages/"+bookId).putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()) {
                    success[0] = true;
                }
                uploadingImage = false;
                Log.i("download", "Upload succeeded");
            }
        });

        while(uploadingImage){
            try{
                wait(1000);
                await(storageTask);
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        return success[0];
    }

    //Download image from database and return bitmap
    public boolean downloadImage(final ImageView imageView, final String bookId) {
        final boolean[] success = {false};
        final int var = 0;
        if(bookId == null) {
            return success[0];
        }

        try {
            storageReference.child("coverImages/"+bookId).getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bitmap);
                    success[0] = true;
                    Log.i("download", "Download succeeded");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("download", "Download failed, "+ e.getMessage());
                    success[0] = false;
                }
            });
        }catch (Exception err){
            Log.i("download", "No Image");
        }

        return success[0];
    }

}// Database
