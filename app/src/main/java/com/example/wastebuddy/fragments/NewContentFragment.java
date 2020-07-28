package com.example.wastebuddy.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;

import com.example.wastebuddy.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class NewContentFragment extends Fragment implements PopupMenu.OnMenuItemClickListener,
        ImageView.OnClickListener {

    private static final String TAG = "NewContentFragment";
    public static final int TAKE_PHOTO_CODE = 1042;
    public static final int PICK_PHOTO_CODE = 1046;


    Context mContext;
    ImageView mImageView;

    File mPhotoFile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO_CODE:
                    // by this point we have the camera photo on disk
                    Bitmap takenImage = rotateBitmapOrientation(mPhotoFile.getAbsolutePath());

                    // Load the taken image into a preview
                    mImageView.setPadding(0, 0, 0, 0);
                    mImageView.setImageBitmap(takenImage);
                    break;
            }
            if (requestCode == PICK_PHOTO_CODE) {
                Uri imageUri = Objects.requireNonNull(data).getData();
                Bitmap chosenImage = loadFromUri(imageUri);

                // Load the taken image into a preview
                mImageView.setPadding(0, 0, 0, 0);
                mImageView.setImageBitmap(chosenImage);
            }
        }
    }

    void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        mPhotoFile = getPhotoFileUri();

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(Objects.requireNonNull(getActivity()), "com" +
                ".example.fileprovider", mPhotoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, TAKE_PHOTO_CODE);
        }
    }

    void launchGallery() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        mPhotoFile = getPhotoFileUri();

        // If you call startActivityForResult() using an intent that no app can handle, your app
        // will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PHOTO_CODE);
        }
    }
    // Returns the File for a photo stored on disk given the fileName

    void notifyInvalidField(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

    File getPhotoFileUri() {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir =
                new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + "photo.jpg");
    }

    Bitmap loadFromUri(Uri photoUri) {
        try {
            InputStream inputStream =
                    Objects.requireNonNull(getContext()).getContentResolver().openInputStream(photoUri);
            FileUtils.copyInputStreamToFile(Objects.requireNonNull(inputStream), mPhotoFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap image = null;
        try {
            // check version of Android on device
            if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source =
                        ImageDecoder.createSource(getContext().getContentResolver(),
                                photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),
                        photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString =
                Objects.requireNonNull(exif).getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) :
                ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        // Return result
        return Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
    }

    // Create listener for image view to display menu
    @Override
    public void onClick(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.add_photo_menu);
        popup.show();
    }

    // Handle popup menu item click
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera_item:
                // do your code
                launchCamera();
                return true;
            case R.id.gallery_item:
                // do your code
                launchGallery();
            default:
                return false;
        }
    }
}
