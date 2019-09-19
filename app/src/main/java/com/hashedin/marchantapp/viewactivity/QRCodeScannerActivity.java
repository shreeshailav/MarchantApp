package com.hashedin.marchantapp.viewactivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.Repository.ApiResponse;
import com.hashedin.marchantapp.databinding.ActivityQrcodeScannerBinding;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;
import com.hashedin.marchantapp.viewmodel.CouponDetailViewModel;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;



public class QRCodeScannerActivity extends AppCompatActivity {

    private int mRequestCode = 100;

    private boolean flash_state = false;

    private static final String TAG = LoginActivity.class.getSimpleName();
     private BeepManager beepManager;
    private String lastText;

    private ActivityQrcodeScannerBinding activityQrcodeScannerBinding;


    BottomSheetBehavior sheetBehavior;

    CouponDetailViewModel viewModel;
    String auth_token;

    private String couponcode = "";


    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {


            if (result.getText() != null && !result.getText().equals(lastText)) {

                if (!TextUtils.isEmpty(result.getText())) {
                    lastText = result.getText();
                    couponcode = result.getText();
                    beepManager.playBeepSoundAndVibrate();
                    viewModel.getData(result.getText(),auth_token);

                }
            }




            //Added preview of scanned barcode
            //ImageView imageView = (ImageView) findViewById(R.id.barcodePreview);
            //imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            //Log.i("possibleResultPoints",""+resultPoints.toString());
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

        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
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

                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }

                if (activityQrcodeScannerBinding.editCoupon != null && activityQrcodeScannerBinding.editCoupon.getText().length() > 3) {

                   // getCouponDetail(activityQrcodeScannerBinding.editCoupon.getText().toString());
                    viewModel.getData(activityQrcodeScannerBinding.editCoupon.getText().toString(),auth_token);


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
                lastText = "" ;
                activityQrcodeScannerBinding.barcodeScanner.resume();

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


        PrefManager prefManager = new PrefManager(this);

         auth_token = "token "+prefManager.getKey();

         viewModel = ViewModelProviders.of(this).get(CouponDetailViewModel.class);
         getCouponDetail();
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
        try{
            if (resultCode == RESULT_OK) {
                activityQrcodeScannerBinding.editCoupon.setText("");
                activityQrcodeScannerBinding.editCoupon.clearFocus();
                activityQrcodeScannerBinding.barcodeScanner.setStatusText("");
                // lastText = "";

                if(data!=null){
                    String msg = data.getStringExtra("result");
                    if(msg!= null && msg.length()>0) {
                        //  snackbarMessage(msg);
                        // snackbar.show();

                        activityQrcodeScannerBinding.barcodeScanner.setStatusText(lastText);

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

                        activityQrcodeScannerBinding.responsecode.setText(msg);


                    }
                    //Toast.makeText(getBaseContext(),""+msg,Toast.LENGTH_LONG).show();
                }
                 lastText = "";

            }
        }catch (Exception e){
            Log.e("ReturnError",e.getMessage());
        }
    }

    private void errorMessage(String msg){
        activityQrcodeScannerBinding.barcodeScanner.setStatusText(lastText);

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

        activityQrcodeScannerBinding.responsecode.setText(msg);


        activityQrcodeScannerBinding.barcodeScanner.pause();
    }


    public void getCouponDetail(){



        viewModel.getData(couponcode,auth_token).observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse apiResponse) {
                if (apiResponse == null) {
                    // handle error here
                    return;
                }
                if (apiResponse.coupons!=null && apiResponse.getError() == null ) {
                    // call is successful
                    //Log.i(TAG, "Data response is " + apiResponse.getPosts());

                    activityQrcodeScannerBinding.standardBottomSheet.setVisibility(View.GONE);
                    Intent intent = new Intent(getBaseContext(), RedeemActivity.class);
                    intent.putExtra("couponcode", couponcode); //"d5384662-4420-4731-99f1-d8b9c76fa487");
                    intent.putExtra("coupons", apiResponse.coupons); //"d5384662-4420-4731-99f1-d8b9c76fa487");
                    startActivityForResult(intent, mRequestCode);

                }else if(apiResponse.errorMessage!=null){
                    errorMessage(apiResponse.errorMessage);
                } else{
                    // call failed.
                    Throwable e = apiResponse.getError();
                    //Toast.makeText(QRCodeScannerActivity.this, "Error is " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    errorMessage("Unable to reach server");
                }
            }
        });
    }
}
