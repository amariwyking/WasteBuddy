package com.example.wastebuddy.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Item")
public class Item extends ParseObject {

    public static final String KEY_BARCODE_ID = "barcodeId";
    public static final String KEY_NAME = "name";
    public static final String KEY_NAME_LOWERCASE = "name_lowercase";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_DISPOSAL = "disposal";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_CREATED_AT = "createdAt";

    public String getBarcodeId() {
        return getString(KEY_BARCODE_ID);
    }

    public void setBarcodeId(String barcodeId) {
        put(KEY_BARCODE_ID, barcodeId);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
        put(KEY_NAME_LOWERCASE, name.toLowerCase());
    }

    public String getDisposal() {
        return getString(KEY_DISPOSAL);
    }

    public void setDisposal(String disposal) {
        put(KEY_DISPOSAL, disposal);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public ParseUser getAuthor() {
        return getParseUser(KEY_AUTHOR);
    }

    public void setUser(ParseUser user) {
        put(KEY_AUTHOR, user);
    }

}
