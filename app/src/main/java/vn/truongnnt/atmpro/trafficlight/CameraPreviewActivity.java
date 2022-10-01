package vn.truongnnt.atmpro.trafficlight;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

import vn.truongnnt.qrcode.QRCodeFoundListener;
import vn.truongnnt.qrcode.QRCodeImageAnalyzer;

public class CameraPreviewActivity extends BasicActivity {

    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private String qrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);

        previewView = findViewById(R.id.activity_main_previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        startCamera();
    }

    @Override
    void preBackActivity(Intent i) {
        super.preBackActivity(i);
        i.putExtra(Utils.PARAM_QR_CODE, qrCode);
    }

    private void startCamera() {
        //todo
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                showMessage("Error starting camera " + e.getMessage());
                doCancelIntent();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider) {
        //previewView.setPreferredImplementationMode(PreviewView.ImplementationMode.SURFACE_VIEW);
        previewView.setImplementationMode(PreviewView.ImplementationMode.PERFORMANCE);
        Preview preview = new Preview.Builder()
                .build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        //preview.setSurfaceProvider(previewView.createSurfaceProvider());
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(
                ContextCompat.getMainExecutor(this),
//                Executors.newSingleThreadExecutor(),
                new QRCodeImageAnalyzer(new QRCodeFoundListener() {
                    @Override
                    public void onQRCodeFound(String _qrCode) {
                        qrCode = _qrCode;
                        //qrCodeFoundButton.setVisibility(View.VISIBLE);
                        handler.post(() -> {
                            doBackIntent(DeviceActivity.class);
                            Log.w(CameraPreviewActivity.class.getCanonicalName(), "QR-Code [" + qrCode + "]");
                        });
                    }

                    @Override
                    public void qrCodeNotFound() {
                        //qrCodeFoundButton.setVisibility(View.INVISIBLE);
                        Log.w(CameraPreviewActivity.class.getCanonicalName(), "Not found QR-Code");
                    }
                }));
        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);
    }
}