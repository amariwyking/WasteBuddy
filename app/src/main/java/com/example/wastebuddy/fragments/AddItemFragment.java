package com.example.wastebuddy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.wastebuddy.databinding.FragmentAddItemBinding;
import com.google.android.material.button.MaterialButton;
import com.hipo.maskededittext.MaskedEditText;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AddItemFragment extends DialogFragment implements ScannerFragment.BarcodeListener {

    private static final int BARCODE_REQUEST_CODE = 47;
    FragmentAddItemBinding mBinding;

    private MaskedEditText mBarcodeEditText;
    MaterialButton mScanButton;
    private Button mConfirmButton;
    private Button mCancelButton;

    private String mBarcode;

    public AddItemFragment() {
        // Empty constructor is required for DialogFragment
    }

    @Override
    public void onBarcodeObserved(String barcode) {
        mBarcode = barcode;
        mBarcodeEditText.setText(formatBarcode(barcode));
    }

    private String formatBarcode(String barcode) {

        if (barcode.length() != 12) {
            return barcode;
        }

        char sub1 = barcode.charAt(0);
        String sub2 = barcode.substring(1,6);
        String sub3 = barcode.substring(6,11);
        char sub4 = barcode.charAt(barcode.length() - 1);
        return String.format("%c %s %s %c", sub1, sub2, sub3, sub4);
    }

    public interface AddItemDialogListener {
        void onFinishAddItemDialog(String barcode);
    }

    public static AddItemFragment newInstance(String title) {
        AddItemFragment frag = new AddItemFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentAddItemBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind();
        setOnClickListeners();
        // Fetch arguments from bundle and set title
        setDialogTitle();
        mBarcodeEditText.requestFocus();
    }

    private void setDialogTitle() {
        String title = Objects.requireNonNull(getArguments()).getString("title", "Enter Name");
        Objects.requireNonNull(getDialog()).setTitle(title);
    }

    @Override
    public void onResume() {
        makeDialogFullscreen();
        // Call super onResume after sizing
        super.onResume();
    }

    private void makeDialogFullscreen() {
        // Get existing layout params for the window
        WindowManager.LayoutParams params = Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        Objects.requireNonNull(getDialog().getWindow()).setAttributes(params);
    }

    private void setOnClickListeners() {
        mScanButton.setOnClickListener(view -> launchScanFragment());

        mConfirmButton.setOnClickListener(view -> {
            sendBackResult();
            dismiss();
        });

        mCancelButton.setOnClickListener(view -> dismiss());
    }

    private void bind() {
        // Bind views
        mBarcodeEditText = mBinding.barcodeEditText;
        mScanButton = mBinding.scanButton;
        mConfirmButton = mBinding.confirmButton;
        mCancelButton = mBinding.cancelButton;
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult() {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        AddItemDialogListener listener = (AddItemDialogListener) getTargetFragment();
        Objects.requireNonNull(listener).onFinishAddItemDialog(mBarcode);
        dismiss();
    }

    private void launchScanFragment() {
        ScannerFragment scannerFragment = ScannerFragment.newInstance(ScannerFragment.TASK_READ);
        String tag = ScannerFragment.class.getSimpleName();

        scannerFragment.setTargetFragment(this, BARCODE_REQUEST_CODE);
        scannerFragment.show(Objects.requireNonNull(getFragmentManager()), tag);
    }
}
