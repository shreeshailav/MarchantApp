package com.hashedin.marchantapp.ViewActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.Models.Coupons;
import com.hashedin.marchantapp.Services.Repository.MarchentServices;
import com.hashedin.marchantapp.databinding.ActivityQrcodeScannerBinding;
import com.hashedin.marchantapp.databinding.ActivityRedeemResponseBinding;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.shreeshail.rxnetworkstate.ConnectionManager;
import com.shreeshail.rxnetworkstate.ConnectionTracer;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QRCodeScannerActivity extends AppCompatActivity  {

    DecoratedBarcodeView dbvScanner;
    private int mRequestCode = 100;

    private static final String TAG = LoginActivity.class.getSimpleName();
    //private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;

    private ActivityQrcodeScannerBinding activityQrcodeScannerBinding;


    MarchentServices marchentServices;





    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }

            lastText = result.getText();
            activityQrcodeScannerBinding.barcodeScanner.setStatusText(result.getText());


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MarchentServices.HTTPS_API_MARCHENT_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            marchentServices = retrofit.create(MarchentServices.class);
            //getcoupons();

            callredeem();


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



        activityQrcodeScannerBinding.editCoupon.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                      activityQrcodeScannerBinding.proceedbtn.setVisibility(View.VISIBLE);
                      activityQrcodeScannerBinding.barcodeScanner.setVisibility(View.GONE);
                }else {
                      activityQrcodeScannerBinding.proceedbtn.setVisibility(View.GONE);
                      activityQrcodeScannerBinding.barcodeScanner.setVisibility(View.VISIBLE);
                }
            }
        });
        activityQrcodeScannerBinding.proceedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (activityQrcodeScannerBinding.editCoupon!=null && activityQrcodeScannerBinding.editCoupon.getText().length()>3){
                        callredeem();
                }else {
                    activityQrcodeScannerBinding.editCoupon.setError("Please Enter Valid Code");
                }
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
            dbvScanner.resume();
        }
    }

     public void getcoupons(){

        Call<List<Coupons>> couponsdata = marchentServices.getProjectList("YEubfsPmTKYJkV9mvOuLrC38nwclNy6a2mbdw5saCNDZ2oq6muaVmcRdLGffRbiU","465d5955-de23-4220-a96f-8c3ffaa00ae5/");

         couponsdata.enqueue(new Callback<List<Coupons>>() {
            @Override
            public void onResponse(Call<List<Coupons>> call, Response<List<Coupons>> response) {
                Log.i("Responce","response"+response.toString());

                Intent intent = new Intent(getBaseContext(),RedeemActivity.class);
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<List<Coupons>> call, Throwable t) {
                Log.i("ERROR","Error : "+t.getMessage());
            }
        });
    }
    public void callredeem(){
        Intent intent = new Intent(getBaseContext(),RedeemActivity.class);
        startActivityForResult(intent,mRequestCode);
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
    public void onBackPressed()
    {

        if(activityQrcodeScannerBinding.editCoupon.isFocused()){
            activityQrcodeScannerBinding.editCoupon.clearFocus();
        }else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK) {
                activityQrcodeScannerBinding.editCoupon.setText("");
                activityQrcodeScannerBinding.editCoupon.clearFocus();
            }

    }


}
