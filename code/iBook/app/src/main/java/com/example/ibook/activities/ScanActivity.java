package com.example.ibook.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ibook.GLOBAL_CONSTANT;
import com.example.ibook.R;
import com.example.ibook.fragment.HomeFragment;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;
    private Button rescanButton;
    private Button backButton;
    private Button completeButton;
    private TextView isbnView;

    private String ISBN;
    private String fromActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        scannerView = findViewById(R.id.scan);
        rescanButton = findViewById(R.id.rescan_button);
        backButton = findViewById(R.id.back_button);
        completeButton = findViewById(R.id.complete_button);
        isbnView = findViewById(R.id.isbn_result);

        // Only when the user scan the isbn will these two buttons display.
        rescanButton.setVisibility(View.INVISIBLE);
        completeButton.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        fromActivity = intent.getStringExtra("ACTIVITY");

        // Get the camera permission and handel other thing
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                scannerView.setResultHandler(ScanActivity.this);
                scannerView.startCamera();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(ScanActivity.this, "You must accept the permission to use the camera", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

            }
        }).check();

        rescanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isbnView.setText("");
                rescanButton.setVisibility(View.INVISIBLE);
                completeButton.setVisibility(View.INVISIBLE);
                scannerView.startCamera();
                scannerView.resumeCameraPreview(ScanActivity.this);
            }
        });

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                if (GLOBAL_CONSTANT.ADD_BOOK_ACTIVITY.equals(fromActivity)) {
                    intent.setClass(ScanActivity.this, HomeFragment.class);
                    //TODO: For other activities or fragment, mostly the fragment is slow, so it is also better to return to activity
                }
                intent.putExtra("ISBN", ISBN);
                startActivity(intent);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void handleResult(Result rawResult) {
        isbnView.setText(rawResult.getText());
        ISBN = isbnView.getText().toString();
        rescanButton.setVisibility(View.VISIBLE);
        completeButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        scannerView.startCamera();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        scannerView.stopCamera();
        super.onDestroy();
    }
}
