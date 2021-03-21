package com.example.wastebuddy.models;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

@SuppressWarnings("unchecked")
public class User {
    private static final String TAG = "User";

    public static final String KEY_UID = "uid";
    public static final String KEY_USERNAME = "name";
    public static final String KEY_FOLLOWERS = "followers";
    public static final String KEY_FOLLOWING = "following";
    public static final String KEY_LIKED_PROJECTS = "likedProjects";

    private final DocumentSnapshot user;
    private final DocumentReference docRef;

    public User(DocumentSnapshot document) {
        user = document;
        docRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(getObjectId());
    }

    public String getObjectId() {
        return user.getId();
    }

    public String getUsername() {
        return user.getString(KEY_USERNAME);
    }

    public void setUsername(String username) {
        docRef.update(KEY_USERNAME, username)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Username successfully updated"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating username", e));
    }

    public List<String> getFollowers() {
        return (List<String>) user.get(KEY_FOLLOWERS);
    }

    public void setFollowers(List<String> followers) {
        docRef.update(KEY_FOLLOWERS, followers)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Followers successfully updated"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating followers", e));
    }

    public List<String> getFollowing() {
        return (List<String>) user.get(KEY_FOLLOWING);
    }

    public void setFollowing(List<String> following) {
        docRef.update(KEY_FOLLOWING, following)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Following successfully updated"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating following", e));
    }

    public void follow(String userId) {
        List<String> following = getFollowing();
        following.add(userId);
        setFollowing(following);
    }

    public void unfollow(String userId) {
        List<String> following = getFollowing();
        following.remove(userId);
        setFollowing(following);
    }

    public List<String> getLikedProjects() {
        return (List<String>) user.get(KEY_LIKED_PROJECTS);
    }

    public void setLikedProjects(List<String> likedProjects) {
        docRef.update(KEY_FOLLOWERS, likedProjects)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Liked projects successfully updated"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating liked projects", e));
    }

    public void likeProject(String projectId) {
        List<String> likedProjects = getLikedProjects();
        likedProjects.add(projectId);
        docRef.update(KEY_LIKED_PROJECTS, likedProjects)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Project liked successfully"))
                .addOnFailureListener(e -> Log.w(TAG, "Failed to like project", e));
    }

    public void unlikeProject(String projectId) {
        List<String> likedProjects = getLikedProjects();
        likedProjects.remove(projectId);
        setLikedProjects(likedProjects);
    }

    public static boolean isSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }
}
