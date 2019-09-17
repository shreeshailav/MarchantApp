package com.hashedin.marchantapp.viewactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.Models.Coupons;
import com.hashedin.marchantapp.Services.Repository.MarchentServices;
import com.hashedin.marchantapp.databinding.ActivityQrcodeScannerBinding;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hashedin.marchantapp.Services.Repository.MarchentServices.HTTPS_API_MARCHENT_URL;

public class QRCodeScannerActivity extends AppCompatActivity {

    private int mRequestCode = 100;

    private boolean flash_state = false;

    private static final String TAG = LoginActivity.class.getSimpleName();
    //private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;

    private ActivityQrcodeScannerBinding activityQrcodeScannerBinding;


    BottomSheetBehavior sheetBehavior;

    Snackbar snackbar;



    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
//            if (result.getText() == null || result.getText().equals(lastText)) {
//                // Prevent duplicate scans
//                activityQrcodeScannerBinding.barcodeScanner.setStatusText(result.getText());
//                return;
//            }
//
//            lastText = result.getText();
            //activityQrcodeScannerBinding.barcodeScanner.setStatusText(result.getText());



            callredeem(result.getText());


            beepManager.playBeepSoundAndVibrate();

            //Added preview of scanned barcode
            //ImageView imageView = (ImageView) findViewById(R.id.barcodePreview);
            //imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        //setContentView(R.layout.activity_qrcode_scanner);
        activityQrcodeScannerBinding = DataBindingUtil.setContentView(this, R.layout.activity_qrcode_scanner);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        requestPermission();

        // barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        // barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        activityQrcodeScannerBinding.barcodeScanner.initializeFromIntent(getIntent());
        activityQrcodeScannerBinding.barcodeScanner.decodeContinuous(callback);

        beepManager = new BeepManager(this);

        activityQrcodeScannerBinding.proceedbtn.setVisibility(View.GONE);
        activityQrcodeScannerBinding.standardBottomSheet.setVisibility(View.GONE);



        activityQrcodeScannerBinding.editCoupon.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                activityQrcodeScannerBinding.barcodeScanner.setStatusText("");
                activityQrcodeScannerBinding.standardBottomSheet.setVisibility(View.GONE);

                if (b) {
                    activityQrcodeScannerBinding.proceedbtn.setVisibility(View.VISIBLE);
                    activityQrcodeScannerBinding.barcodeScanner.setVisibility(View.GONE);
                } else {
                    activityQrcodeScannerBinding.proceedbtn.setVisibility(View.GONE);
                    activityQrcodeScannerBinding.barcodeScanner.setVisibility(View.VISIBLE);
                }
            }
        });
        activityQrcodeScannerBinding.proceedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (activityQrcodeScannerBinding.editCoupon != null && activityQrcodeScannerBinding.editCoupon.getText().length() > 3) {
                    callredeem(activityQrcodeScannerBinding.editCoupon.getText().toString());
                } else {
                    activityQrcodeScannerBinding.editCoupon.setError("Please Enter Valid Code");
                }
            }
        });

        activityQrcodeScannerBinding.scannagainbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityQrcodeScannerBinding.standardBottomSheet.setVisibility(View.GONE);
                activityQrcodeScannerBinding.barcodeScanner.setStatusText("");
            }
        });


        activityQrcodeScannerBinding.flashimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (flash_state) {
                    activityQrcodeScannerBinding.flashimage.setImageResource(R.drawable.ic_flash_off);
                    flash_state = false;

                    activityQrcodeScannerBinding.barcodeScanner.setTorchOff();


                } else {
                    activityQrcodeScannerBinding.flashimage.setImageResource(R.drawable.ic_flash_on);
                    flash_state = true;

                    activityQrcodeScannerBinding.barcodeScanner.setTorchOn();

                }
            }
        });
        sheetBehavior = BottomSheetBehavior.from(activityQrcodeScannerBinding.standardBottomSheet);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        //TODO
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                     }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();

        activityQrcodeScannerBinding.barcodeScanner.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        activityQrcodeScannerBinding.barcodeScanner.pause();
    }


    public void triggerScan(View view) {
        activityQrcodeScannerBinding.barcodeScanner.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return activityQrcodeScannerBinding.barcodeScanner.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }


    void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && grantResults.length < 1) {
            requestPermission();
        } else {
            activityQrcodeScannerBinding.barcodeScanner.resume();

        }
    }



    public void callredeem(String couponstr) {

        if(couponstr.toLowerCase().contains("uuid") ){
            activityQrcodeScannerBinding.standardBottomSheet.setVisibility(View.GONE);
            Intent intent = new Intent(getBaseContext(), RedeemActivity.class);
            startActivityForResult(intent, mRequestCode);
        }else {

            activityQrcodeScannerBinding.barcodeScanner.setStatusText(couponstr);


            if (activityQrcodeScannerBinding.editCoupon.isFocused()) {
                activityQrcodeScannerBinding.editCoupon.clearFocus();
            }
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            activityQrcodeScannerBinding.scannerlayout.setVisibility(View.VISIBLE);

            activityQrcodeScannerBinding.standardBottomSheet.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {

        if (activityQrcodeScannerBinding.editCoupon.isFocused()) {
            activityQrcodeScannerBinding.editCoupon.clearFocus();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            activityQrcodeScannerBinding.editCoupon.setText("");
            activityQrcodeScannerBinding.editCoupon.clearFocus();
            activityQrcodeScannerBinding.barcodeScanner.setStatusText("");
            lastText = "";

            if(data!=null){
                String msg = data.getStringExtra("result");
                snackbarMessage(msg);
                snackbar.show();
                //Toast.makeText(getBaseContext(),""+msg,Toast.LENGTH_LONG).show();
            }

        }

    }

    private void snackbarMessage(String msg){
        snackbar = Snackbar
                .make(activityQrcodeScannerBinding.coordinatorLayout, msg, Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
        // Changing message text color
        snackbar.setActionTextColor(Color.RED);
    }

}
