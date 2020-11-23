package com.example.ibook.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ibook.activities.EditBookActivity;
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
import java.io.FileInputStream;

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
    public final static String tempFileName = "tempImage.png";

    public Database(FirebaseAuth uAuth, FirebaseFirestore db) {
        this.uAuth = uAuth;
        this.db = db;
    }

    public Database(){
        this.uAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.storageReference = FirebaseStorage.getInstance().getReference();
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

    /**
     * Upload an image to the database (Firebase Storage) by supplying the image as an image view
     * and the file title as the bookId. This works because there can only be one image per book.
     * The image is scaled down to a small size <1MB and an icon image gets created that is only a
     * few KB's for fast access in the array lists.
     * @param is
     * @param bookId
     * @return
     */
    public boolean uploadImage(final FileInputStream is, final String bookId) {
        final boolean[] success = {false};
        if(bookId == null || bookId == "null") {
            Log.i("image", "No bookID");
            return success[0];
        }
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            Bitmap bitmapSmall = bitmap;
            //Upload an icon image that is very small so we can access the book list faster
            int maxSmallSize = 100;
            double width = (double) bitmapSmall.getWidth()/bitmapSmall.getHeight()*maxSmallSize;
            bitmapSmall = Bitmap.createScaledBitmap(bitmapSmall, (int) width, maxSmallSize, true);
            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            bitmapSmall.compress(Bitmap.CompressFormat.PNG, 85, baos2);
            byte[] dataSmall = baos2.toByteArray();
            storageReference.child("coverImages/"+bookId+"icon").putBytes(dataSmall).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        success[0] = true;
                    }
                    Log.i("image", "Icon Image Upload succeeded");
                }
            });
            //Rescale the bitmap so that its smaller. We can't download images more than 1MB.
            int maxSize = 800;
            if(bitmap.getHeight() > maxSize) {
                double newWidth = (double) bitmap.getWidth()/bitmap.getHeight()*maxSize;
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) newWidth, maxSize, true);
            }
            if (bitmap.getWidth() > maxSize) {
                double newHeight = (double) bitmap.getHeight()/bitmap.getWidth()*maxSize;
                bitmap = Bitmap.createScaledBitmap(bitmap,  maxSize, (int) newHeight, true);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, baos);
            byte[] data = baos.toByteArray();
            storageReference.child("coverImages/" + bookId).putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        success[0] = true;
                    }
                    Log.i("image", "Full Image Upload succeeded");
                }
            });
            baos.close();
            baos2.close();
            bitmap.recycle();
            bitmapSmall.recycle();
        }catch  (Exception e){
            e.printStackTrace();
        }

        return success[0];
    }

    /**
     * Download an image from the database. This method takes in the ImageView of where to store the
     * image and the bookId, which is the filename for the image to get from Firebase Storage.
     * Can also download just an icon image for faster viewing in large booklists
     * @param imageView
     * @param bookId
     * @param fullSizedImage
     * @return
     */
    public boolean downloadImage(final ImageView imageView, final String bookId, boolean fullSizedImage) {
        final boolean[] success = {false};
        if(bookId == null || bookId == "null") {
            return success[0];
        }
        String imageName = bookId;
        if(!fullSizedImage) { //Download just the icon
            imageName = bookId + "icon";
        }
        try {
            storageReference.child("coverImages/" + imageName).getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    EditBookActivity.scaleAndSetImage(bitmap, imageView);
                    success[0] = true;
                    //Log.i("image", "Download succeeded");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("image", "Download failed, " + e.getMessage());
                    success[0] = false;
                }
            });
        }catch (Exception e) {
        }
        return success[0];
    }

    /**
     * Delete an image from firebase storage with bookId as the filename.
     * @param bookId
     * @return
     */
    public boolean deleteImage(final String bookId) {
        final boolean[] success = {false};
        if(bookId == null) {
            return success[0];
        }
        //Delete the image
        storageReference.child("coverImages/"+bookId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i("image", "Deleted Image");
                success[0] = true;
            }
        });
        //Delte the icon image as well
        //Delete the image
        storageReference.child("coverImages/"+bookId + "icon").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i("image", "Deleted Icon Image");
                success[0] = true;
            }
        });

        return success[0];
    }

}// Database
