package com.example.wastebuddy.models;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Item {
    private static final String TAG = "Item";

    public static final String KEY_AUTHOR = "author";
    public static final String KEY_BARCODE = "barcodeId";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_UPDATED_AT = "updatedAt";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_DISPOSAL = "disposal";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_NAME = "name";
    public static final String KEY_NAME_LOWERCASE = "name_lowercase";

    Map<String, Object> itemData = new HashMap<>();

    private DocumentSnapshot item;

    private String mBarcode;

    public Item(String barcode) {
        mBarcode = barcode;

        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("items")
                .document(mBarcode);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "Snapshot of item data: " + document.getData());
                    item = document;
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    public Item(QueryDocumentSnapshot document) {
        item = document;
    }

    public String getBarcodeId() {
        return item.getString(KEY_BARCODE);
    }

    public void setBarcodeId(String barcodeId) {
        itemData.put(KEY_BARCODE, barcodeId);
    }

    public String getName() {
        return item.getString(KEY_NAME);
    }

    public void setName(String name) {
        itemData.put(KEY_NAME, name);
        itemData.put(KEY_NAME_LOWERCASE, name.toLowerCase());
    }

    public String getDisposal() {
        return item.getString(KEY_DISPOSAL);
    }

    public void setDisposal(String disposal) {
        itemData.put(KEY_DISPOSAL, disposal);
    }

    public String getDescription() {
        return item.getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        itemData.put(KEY_DESCRIPTION, description);
    }

    public String getAuthorId() {
        return item.getString(KEY_AUTHOR);
    }

    public void setAuthor(String userId) {
        itemData.put(KEY_AUTHOR, userId);
    }

    public static void getImage(String barcode, Context context, ImageView imageView) {
        StorageReference storageReference =
                FirebaseStorage.getInstance().getReference().child("images").child(barcode).child(
                        "photo.jpg");

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // Got the download URL for the item image
            Glide.with(context).load(uri).into(imageView);
            Log.d(TAG, "Image retrieved successfully");
        }).addOnFailureListener(e -> {
            // Handle any errors
            Log.e(TAG, "Image retrieval failed", e);
        });
    }

    public void setImage(File image) {
        Uri file = Uri.fromFile(image);
        Log.d(TAG, file.getLastPathSegment());
        StorageReference imageStorageRef = FirebaseStorage.getInstance().getReference().child("images");

        UploadTask uploadTask =
                imageStorageRef.child(mBarcode).child(file.getLastPathSegment()).putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask
                .addOnFailureListener(e -> Log.d(TAG, "Item image successfully updated"))
                .addOnSuccessListener(taskSnapshot -> Log.w(TAG, "Error updating item image"));
    }

    public static void deleteImage(String barcode) {
        // Create a storage reference from our app
        StorageReference storageReference =
                FirebaseStorage.getInstance().getReference().child("images").child(barcode).child(
                        "photo.jpg");

        // Delete the file
        storageReference.delete().addOnSuccessListener(aVoid -> {
            // File deleted successfully
            Log.d(TAG, "Image for item w/ barcode \"" + barcode + "\" successfully deleted!");
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.e(TAG, "Failed to delete image for item w/ barcode \"" + barcode + "\".");
            }
        });
    }

    public void create() {
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("items")
                .document(mBarcode);

        docRef.set(itemData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Item uploaded successfully");
            } else {
                Log.d(TAG, "Item failed to upload", task.getException());
            }
        });

        Timestamp createdAt = Timestamp.now();
        docRef.update(KEY_UPDATED_AT, createdAt);
    }

    public static void delete(String barcode) {
        FirebaseFirestore.getInstance()
                .collection("items")
                .document(barcode).delete()
                .addOnSuccessListener(aVoid -> {
                    deleteImage(barcode);
                    Log.d(TAG, "Item w/ barcode \"" + barcode + "\" successfully deleted!");
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting item w/ barcode \"" + barcode + "\"", e));
    }

}


