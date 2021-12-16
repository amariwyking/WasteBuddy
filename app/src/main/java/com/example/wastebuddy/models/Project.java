package com.example.wastebuddy.models;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wastebuddy.Navigation;
import com.example.wastebuddy.fragments.ProjectDetailsFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.parse.ParseClassName;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParseClassName("Project")
public class Project {
    private static final String TAG = "Project";

    public static final String KEY_NAME = "name";
    public static final String KEY_NAME_LOWERCASE = "name_lowercase";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_STEPS = "steps";
    public static final String KEY_DIFFICULTY = "difficulty";
    public static final String KEY_LIKES = "likesCount";
    public static final String KEY_ITEMS = "items";
    public static final String KEY_AUTHOR_ID = "author";
    public static final String KEY_PROJECT_ID = "project_id";
    private static final String KEY_CREATED_AT = "createdAt";

    private Map<String, Object> projectData = new HashMap<>();

    private String mProjectId;

    public Project() {}

    public Project(Map<String, Object> data) {
        mProjectId = (String) data.get(KEY_PROJECT_ID);
        projectData = data;
    }

    public Project(String projectId) {
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("projects")
                .document(projectId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "Snapshot of project data: " + document.getData());
                    mProjectId = document.getId();
                    projectData = document.getData();
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "Get failed with ", task.getException());
            }
        });
    }

    public void create(File image, Context context) {
        FirebaseFirestore.getInstance()
                .collection("projects")
                .add(projectData)
                .addOnSuccessListener(docRef -> {
                    mProjectId = docRef.getId();
                    setImage(image, mProjectId);
                    docRef.update(KEY_PROJECT_ID, mProjectId);

                    Timestamp createdAt = Timestamp.now();
                    docRef.update(KEY_CREATED_AT, createdAt);

                    Navigation.switchFragment(context,
                            ProjectDetailsFragment.newInstance(Project.KEY_PROJECT_ID,
                                    mProjectId));
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public void update() {
        FirebaseFirestore.getInstance()
                .collection("projects")
                .document(mProjectId)
                .update(projectData);
    }

    public static void delete(String projectId) {
        FirebaseFirestore.getInstance()
                .collection("projects")
                .document(projectId).delete()
                .addOnSuccessListener(aVoid -> {
                    deleteImage(projectId);
                    Log.d(TAG, "Projects w/ id \"" + projectId + "\" successfully deleted!");
                })
                .addOnFailureListener(e -> Log.w(TAG,
                        "Error deleting item w/ barcode \"" + projectId + "\"", e));
    }

    public String getAuthorId() {
        return (String) projectData.get(KEY_AUTHOR_ID);
    }

    public String getDescription() {
        return (String) projectData.get(KEY_DESCRIPTION);
    }

    public String getDifficulty() {
        return (String) projectData.get(KEY_DIFFICULTY);
    }

    public static void getImage(String projectId, Context context, ImageView imageView) {
        StorageReference storageReference =
                FirebaseStorage.getInstance().getReference().child("images").child("projects").child(projectId).child(
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

    public List<String> getItemIdList() {
        return (List<String>) projectData.get(KEY_ITEMS);
    }

    public int getLikes() {
        Long likes = (long) projectData.get(KEY_LIKES);
        return likes.intValue();
    }

    public String getName() {
        return (String) projectData.get(KEY_NAME);
    }

    public String getProjectId() {
        return (String) projectData.get(KEY_PROJECT_ID);
    }

    public List<String> getSteps() {
        return (List<String>) projectData.get(KEY_STEPS);
    }

    public void setDescription(String description) {
        projectData.put(KEY_DESCRIPTION, description);
    }

    public void setDifficulty(String difficulty) {
        projectData.put(KEY_DIFFICULTY, difficulty);
    }

    public void setImage(File image, String projectId) {
        Uri file = Uri.fromFile(image);
        Log.d(TAG, file.getLastPathSegment());
        StorageReference projectStorageRef = FirebaseStorage.getInstance().getReference().child("images").child("projects");

        String s = file.getLastPathSegment();

        UploadTask uploadTask =
                projectStorageRef.child(projectId).child(s).putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask
                .addOnFailureListener(e -> Log.d(TAG, "Project image successfully updated"))
                .addOnSuccessListener(taskSnapshot -> Log.w(TAG, "Error updating project image"));
    }

    public void setItemIdList(List<String> items) {
        projectData.put(KEY_ITEMS, items);
    }

    public void setName(String name) {
        projectData.put(KEY_NAME, name);
        projectData.put(KEY_NAME_LOWERCASE, name.toLowerCase());
    }

    public void setSteps(List<String> steps) {
        projectData.put(KEY_STEPS, steps);
    }

    public void initLikes() {
        projectData.put(KEY_LIKES, 0);
    }

    public void like() {
        DocumentReference projectRef =
                FirebaseFirestore.getInstance().collection("projects").document(mProjectId);

        projectRef.update("KEY_LIKES", FieldValue.increment(1));
    }

    public void unlike() {
        DocumentReference projectRef =
                FirebaseFirestore.getInstance().collection("projects").document(mProjectId);

        projectRef.update("KEY_LIKES", FieldValue.increment(-1));
    }

    public static void deleteImage(String projectId) {
        // Create a storage reference from our app
        StorageReference storageReference =
                FirebaseStorage.getInstance().getReference().child("images").child("projects").child(projectId).child(
                        "photo.jpg");

        // Delete the file
        storageReference.delete().addOnSuccessListener(aVoid -> {
            // File deleted successfully
            Log.d(TAG, "Image for project \"" + projectId + "\" successfully deleted!");
        }).addOnFailureListener(exception -> {
            // Uh-oh, an error occurred!
            Log.e(TAG, "Failed to delete image for project w/ id \"" + projectId + "\".");
        });
    }

    public void setAuthorId(String userId) {
        projectData.put(KEY_AUTHOR_ID, userId);
    }
}
