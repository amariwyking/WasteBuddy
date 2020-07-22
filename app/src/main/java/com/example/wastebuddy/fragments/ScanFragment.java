package com.example.wastebuddy.fragments;

import android.Manifest;
import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.wastebuddy.Navigation;
import com.example.wastebuddy.databinding.FragmentScanBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanFragment extends Fragment {

    FragmentScanBinding mBinding;

    private static String TAG = "ScanFragment";

    private PreviewView mPreviewView;

    FirebaseVisionBarcodeDetector mDetector;
    FirebaseVisionBarcodeDetectorOptions mOptions;

    public ScanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentScanBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind();


        mOptions = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_UPC_A)
                .build();

        mDetector = FirebaseVision.getInstance().getVisionBarcodeDetector(mOptions);

        mPreviewView.post(this::startImageAnalysis);
    }

    private void bind() {
        mPreviewView = mBinding.previewView;
    }

    private void bindPreview(ProcessCameraProvider cameraProvider) {
        Executor executor = Executors.newSingleThreadExecutor();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mBinding.previewView.getDisplay().getRealMetrics(displayMetrics);

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder()
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
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

    private void startImageAnalysis() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(getContext());

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

    class MachineLearningAnalyzer implements ImageAnalysis.Analyzer {
        private long lastAnalyzedTimestamp = 0L;
        private boolean resultsFound = false;

        @Override
        @androidx.camera.core.ExperimentalGetImage

        public void analyze(@NonNull ImageProxy imageProxy) {
            if (hasHalfSecondPassed()) {
                Image mediaImage = imageProxy.getImage();
                int degrees = imageProxy.getImageInfo().getRotationDegrees();

                FirebaseVisionImage visionImage =
                        FirebaseVisionImage.fromMediaImage(mediaImage, 0);
                mDetector.detectInImage(visionImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                        interpretResults(firebaseVisionBarcodes);
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: ");
                            }
                        });
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
            if (barcode != null && !barcode.isEmpty() && !resultsFound){
                resultsFound = true;
                Vibrator vibrator =
                        (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(250,
                            VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(250);
                }

                Log.i(TAG, "Barcode: " + barcode);

//                Bundle bundle = new Bundle();
//                bundle.putString("barcode", barcode);
//                CreateItemFragment fragment = new CreateItemFragment();
//                fragment.setArguments(bundle);
//                Navigation.switchFragment(getContext(), fragment);
            }
        }

        private boolean hasHalfSecondPassed() {
            long currentTimeMillis = System.currentTimeMillis();
            return (currentTimeMillis - lastAnalyzedTimestamp >= 500);
        }
    }
}