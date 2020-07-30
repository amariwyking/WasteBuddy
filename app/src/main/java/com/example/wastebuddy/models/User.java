package com.example.wastebuddy.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

@ParseClassName("User")
public class User {
    private ParseUser user;

    public static final String KEY_USERNAME = "username";
    public static final String KEY_FOLLOWERS = "followers";
    public static final String KEY_FOLLOWING = "following";

    public User(ParseUser obj) {
        user = obj;
    }

    public String getUsername() {
        return user.getString(KEY_USERNAME);
    }

    public void setUsername(String username) {
        user.put(KEY_USERNAME, username);
    }
    
    public String getFollowers() {
        return user.getString(KEY_FOLLOWERS);
    }

    public void setFollowers(String followers) {
        user.put(KEY_FOLLOWERS, followers);
        user.saveInBackground();
    }

    public JSONArray getFollowing() {
        return user.getJSONArray(KEY_FOLLOWING);
    }

    public void setFollowing(JSONArray following) {
        user.put(KEY_FOLLOWING, following);
        user.saveInBackground();
    }

    public void follow(ParseUser user) {
        JSONArray following = getFollowing();
        following.put(user.getUsername());
        setFollowing(following);
    }

    public void unfollow(ParseUser user) {
        String oldFollowing = getFollowing().toString();

        String regex1 = String.format(",\"%s\"", user.getUsername());
        String regex2 = String.format("\"%s\"", user.getUsername());

        String newFollowing = oldFollowing.replaceAll(regex1, "").replaceAll(regex2, "");
        try {
            setFollowing(new JSONArray(newFollowing));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fetch() {
        try {
            user = ParseUser.getCurrentUser().fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static boolean isSignedIn() {
        return ParseUser.getCurrentUser() != null;
    }
}
