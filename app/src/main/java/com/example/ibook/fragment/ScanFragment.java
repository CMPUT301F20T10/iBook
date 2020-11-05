package com.example.ibook.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.ibook.R;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


/**
 * The fragment class for the isbn scanning function
 */
public class ScanFragment extends DialogFragment implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;
    private Button rescanButton;
    private TextView isbnView;

    private String ISBN;

    private OnFragmentInteractionListener listener;

    /**
     * The listener to send the isbn to the activity
     */
    public interface OnFragmentInteractionListener {
        void onOkPressed(String ISBN);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ScanFragment.OnFragmentInteractionListener) {
            listener = (ScanFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_scan, null);


        scannerView = view.findViewById(R.id.scan);
        rescanButton = view.findViewById(R.id.rescan_button);
        isbnView = view.findViewById(R.id.isbn_result);

        // Only when the user scan the isbn will these two buttons display.
        rescanButton.setVisibility(View.INVISIBLE);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // Get the camera permission and handel other thing
        Dexter.withActivity(getActivity()).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                scannerView.setResultHandler(ScanFragment.this);
                scannerView.startCamera();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(getContext(), "You must accept the permission to use the camera", Toast.LENGTH_SHORT).show();
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
                scannerView.startCamera();
                scannerView.resumeCameraPreview(ScanFragment.this);
            }
        });

        return builder.setView(view)
                .setTitle("Scan ISBN")
                .setNegativeButton("BACK", null)
                .setPositiveButton(
                        "Complete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                listener.onOkPressed(ISBN);
                            }
                        }).create();
    }

    /**
     * This method is called when it get the message from scanning
     * It will deal with the message.
     *
     * @param rawResult The message get from scanning
     */
    @Override
    public void handleResult(Result rawResult) {
        isbnView.setText(rawResult.getText());
        ISBN = isbnView.getText().toString();
        rescanButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        scannerView.startCamera();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        scannerView.stopCamera();
        super.onDestroy();
    }
}
