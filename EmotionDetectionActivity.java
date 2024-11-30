package com.microsoft.projectoxford.emotionsample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmotionDetectionActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private FaceDetector faceDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion_detection);

        previewView = findViewById(R.id.previewView);
        cameraExecutor = Executors.newSingleThreadExecutor();

        // Configure Face Detection options
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();

        faceDetector = FaceDetection.getClient(options);

        // Request Camera Permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }

        findViewById(R.id.btnProceed).setOnClickListener(v -> {
            Toast.makeText(this, "Proceeding...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void startCamera() {
        ProcessCameraProvider.getInstance(this)
                .addListener(() -> {
                    try {
                        ProcessCameraProvider cameraProvider = ProcessCameraProvider.getInstance(this).get();

                        CameraSelector cameraSelector = new CameraSelector.Builder()
                                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                                .build();

                        androidx.camera.core.Preview preview = new androidx.camera.core.Preview.Builder().build();
                        preview.setSurfaceProvider(previewView.getSurfaceProvider());

                        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build();

                        imageAnalysis.setAnalyzer(cameraExecutor, new ImageAnalyzer());

                        cameraProvider.unbindAll();
                        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

                    } catch (Exception e) {
                        Toast.makeText(this, "Error initializing camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, ContextCompat.getMainExecutor(this));
    }


    @OptIn(markerClass = ExperimentalGetImage.class)
    private class ImageAnalyzer implements ImageAnalysis.Analyzer {
        @Override
        public void analyze(@NonNull androidx.camera.core.ImageProxy imageProxy) {
            try {
                if (imageProxy.getImage() != null) {
                    InputImage inputImage = InputImage.fromMediaImage(
                            imageProxy.getImage(),
                            imageProxy.getImageInfo().getRotationDegrees()
                    );

                    faceDetector.process(inputImage)
                            .addOnSuccessListener(faces -> {
                                for (Face face : faces) {
                                    if (face.getSmilingProbability() != null) {
                                        float smileProb = face.getSmilingProbability();
                                        String emotion = smileProb > 0.5 ? "Happy" : "Neutral";
                                        runOnUiThread(() ->
                                                Toast.makeText(EmotionDetectionActivity.this, "Emotion: " + emotion, Toast.LENGTH_SHORT).show()
                                        );
                                    }
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(EmotionDetectionActivity.this, "Detection Error: " + e.getMessage(), Toast.LENGTH_SHORT).show())
                            .addOnCompleteListener(task -> imageProxy.close());
                } else {
                    imageProxy.close();
                }
            } catch (Exception e) {
                imageProxy.close();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Call the parent class's implementation
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if the request code matches your camera permission request
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // If permission is granted, start the camera
            startCamera();
        } else {
            // If permission is denied, show a message
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        faceDetector.close();
    }
}
