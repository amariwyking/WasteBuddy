package com.example.wastebuddy.fragments;

import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.wastebuddy.R;
import com.example.wastebuddy.databinding.FragmentScanBinding;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ScannerFragment extends DialogFragment {

    FragmentScanBinding mBinding;

    private static String TAG = "ScanFragment";

    PreviewView mPreviewView;
    ImageButton mCloseButton;

    FirebaseVisionBarcodeDetector mDetector;
    FirebaseVisionBarcodeDetectorOptions mOptions;


    public ScannerFragment() {
        // Required empty public constructor
    }

    public interface BarcodeListener {
        void onBarcodeObserved(String barcode);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        // Inflate the layout for this fragment
        mBinding = FragmentScanBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind();
        setOnClickListeners();

        mOptions = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_UPC_A)
                .build();

        mDetector = FirebaseVision.getInstance().getVisionBarcodeDetector(mOptions);

        mPreviewView.post(this::startImageAnalysis);
    }

    @Override
    public int getTheme() {
        return R.style.ScannerTheme;
    }

    private void bind() {
        mPreviewView = mBinding.previewView;
        mCloseButton = mBinding.closeButton;
    }

    private void bindPreview(ProcessCameraProvider cameraProvider) {
        // Set up the camera preview
        Executor executor = Executors.newSingleThreadExecutor();

        CameraSelector cameraSelector = new CameraSelector
                .Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview
                .Builder()
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis
                .Builder()
                .setImageQueueDepth(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(executor, new MachineLearningAnalyzer());

        cameraProvider.unbindAll();

        Camera camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                imageAnalysis,
                preview);

        camera.getCameraControl().enableTorch(true);
        preview.setSurfaceProvider(mBinding.previewView.createSurfaceProvider());
    }

    private void setOnClickListeners() {
        mCloseButton.setOnClickListener(view -> dismiss());
    }

    private void startImageAnalysis() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(Objects.requireNonNull(getContext()));

        // Check for CameraProvider availability
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    // Send the observed barcode back to the parent fragment via the listener
    public void sendBackResult(String barcode) {
        BarcodeListener listener = (BarcodeListener) getTargetFragment();
        Objects.requireNonNull(listener).onBarcodeObserved(barcode);
        dismiss();
    }

    class MachineLearningAnalyzer implements ImageAnalysis.Analyzer {
        private boolean resultsFound = false;

        @Override
        @androidx.camera.core.ExperimentalGetImage

        public void analyze(@NonNull ImageProxy imageProxy) {
            if (hasHalfSecondPassed()) {
                Image mediaImage = imageProxy.getImage();

                FirebaseVisionImage visionImage =
                        FirebaseVisionImage.fromMediaImage(Objects.requireNonNull(mediaImage), 0);
                mDetector.detectInImage(visionImage)
                        .addOnSuccessListener(this::interpretResults)
                        .addOnFailureListener(e -> Log.d(TAG, "onFailure: "));
            }

            imageProxy.close();
        }

        private void interpretResults(List<FirebaseVisionBarcode> barcodeList) {
            for (FirebaseVisionBarcode barcode : barcodeList) {
                String rawValue = barcode.getRawValue();
                onBarcodeObserved(rawValue);
                break;
            }
        }

        private void onBarcodeObserved(String barcode) {
            if (barcode != null && !barcode.isEmpty() && !resultsFound) {
                resultsFound = true;
                Vibrator vibrator =
                        (Vibrator) Objects.requireNonNull(getActivity()).getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(250,
                            VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(250);
                }

                Log.i(TAG, "Barcode: " + barcode);
                sendBackResult(barcode);
            }
        }

        private boolean hasHalfSecondPassed() {
            long currentTimeMillis = System.currentTimeMillis();
            long lastAnalyzedTimestamp = 0L;
            return (currentTimeMillis - lastAnalyzedTimestamp >= 500);
        }
    }
}