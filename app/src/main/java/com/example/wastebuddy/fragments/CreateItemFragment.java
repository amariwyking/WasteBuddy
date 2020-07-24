package com.example.wastebuddy.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wastebuddy.R;
import com.example.wastebuddy.databinding.FragmentCreateItemBinding;
import com.example.wastebuddy.models.Item;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public class CreateItemFragment extends NewContentFragment implements ScannerFragment.BarcodeListener {

    private static final String TAG = "NewItemFragment";
    public static final int BARCODE_REQUEST_CODE = 47;

    FragmentCreateItemBinding mBinding;
    Context mContext;

    EditText mNameEditText;
    Spinner mDisposalSpinner;
    TextView mBarcodeTextView;
    EditText mDescriptionEditText;
    ImageButton mBarcodeButton;
    Button mShareButton;

    String mBarcode = "";

    public CreateItemFragment() {
        // Required empty public constructor
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        mBinding = FragmentCreateItemBinding.inflate(inflater, container, false);
        mContext = getContext();
        return mBinding.getRoot();
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bind();
        configureSpinner();
        setOnClickListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBarcodeTextView.setText(mBarcode);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    private void setOnClickListeners() {
        mImageView.setOnClickListener(view -> launchCamera());

        mShareButton.setOnClickListener(view -> {
            if (mNameEditText.getText().toString().isEmpty()) {
                notifyInvalidField("Name cannot be empty");
                return;
            }

            if (mDescriptionEditText.getText().toString().isEmpty()) {
                notifyInvalidField("Description cannot be empty");
                return;
            }

            if (mPhotoFile == null || mImageView.getDrawable() == null) {
                notifyInvalidField("There is no image");
                return;
            }

            ParseUser currentUser = ParseUser.getCurrentUser();
            saveItem(currentUser, mPhotoFile);
        });

        mBarcodeButton.setOnClickListener(view -> launchScanFragment());
    }

    private void launchScanFragment() {
        ScannerFragment scannerFragment = ScannerFragment.newInstance(ScannerFragment.TASK_READ);
        String tag = ScannerFragment.class.getSimpleName();

        scannerFragment.setTargetFragment(this, BARCODE_REQUEST_CODE);
        scannerFragment.show(Objects.requireNonNull(getFragmentManager()), tag);
    }

    private void saveItem(ParseUser currentUser, File mPhotoFile) {
        Item item = new Item();
        item.setName(mNameEditText.getText().toString());
        item.setDisposal(mDisposalSpinner.getSelectedItem().toString());
        item.setDescription(mDescriptionEditText.getText().toString());
        item.setBarcodeId(mBarcode);
        item.setImage(new ParseFile(mPhotoFile));
        item.setUser(currentUser);
        item.saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Error while saving item", e);
                Toast.makeText(getContext(), "Error while saving :(", Toast.LENGTH_SHORT).show();
            }
            Log.i(TAG, "Item saved successfully!");
            mNameEditText.setText("");
            mDescriptionEditText.setText("");
            mBarcodeTextView.setVisibility(View.GONE);
            mImageView.setPadding(16, 16, 16, 16);
            mImageView.setImageResource(R.drawable.ic_round_add_a_photo_64);
        });
    }

    private void bind() {
        mNameEditText = mBinding.nameEditText;
        mDisposalSpinner = mBinding.disposalSpinner;
        mBarcodeTextView = mBinding.barcodeTextView;
        mDescriptionEditText = mBinding.descriptionEditText;
        mImageView = mBinding.imageView;
        mBarcodeButton = mBinding.barcodeButton;
        mShareButton = mBinding.shareButton;
    }

    private void configureSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                R.array.disposal_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mDisposalSpinner.setAdapter(adapter);
    }

    @Override
    public void onBarcodeObserved(String barcode) {
        mBarcode = barcode;
        mBarcodeTextView.setText(String.format("Barcode: %s", barcode));
        mBarcodeTextView.setVisibility(View.VISIBLE);
    }
}