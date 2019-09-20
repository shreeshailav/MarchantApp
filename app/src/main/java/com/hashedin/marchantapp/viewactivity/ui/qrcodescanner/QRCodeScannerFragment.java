package com.hashedin.marchantapp.viewactivity.ui.qrcodescanner;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.Models.Coupons;
import com.hashedin.marchantapp.Services.Repository.ApiResponse;
import com.hashedin.marchantapp.databinding.ActivityQrcodeScannerBinding;
import com.hashedin.marchantapp.viewactivity.LoginActivity;
import com.hashedin.marchantapp.viewactivity.RedeemActivity;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;
import com.hashedin.marchantapp.viewactivity.ui.support.SupportFragment;
import com.hashedin.marchantapp.viewmodel.CouponDetailViewModel;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class QRCodeScannerFragment extends Fragment implements RedeemFragment.OnFragmentInteractionListener{
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

    PopupWindow optionspu;

    private QRCodeScannerViewModel qrCodeScannerViewModel;

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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        PrefManager prefManager = new PrefManager(getContext());

        auth_token = "token "+prefManager.getKey();

        viewModel = ViewModelProviders.of(this).get(CouponDetailViewModel.class);
        getCouponDetail();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        qrCodeScannerViewModel =
                ViewModelProviders.of(this).get(QRCodeScannerViewModel.class);

        //View root = inflater.inflate(R.layout.activity_qrcode_scanner, container, false);


        activityQrcodeScannerBinding = DataBindingUtil.inflate(
                inflater, R.layout.activity_qrcode_scanner, container, false);

        View root = activityQrcodeScannerBinding.getRoot();


        final TextView textView = root.findViewById(R.id.text_dashboard);
        qrCodeScannerViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
               // textView.setText(s);
                Log.i("onChanged","changed");
            }
        });
        initialize();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    public void initialize(){
        requestPermission();
        lastText = "";
        activityQrcodeScannerBinding.editCoupon.setText("");
        activityQrcodeScannerBinding.editCoupon.clearFocus();
        activityQrcodeScannerBinding.barcodeScanner.setStatusText("");
        if (activityQrcodeScannerBinding.editCoupon.isFocused()) {
            activityQrcodeScannerBinding.editCoupon.clearFocus();
        }

        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        activityQrcodeScannerBinding.barcodeScanner.initializeFromIntent(getActivity().getIntent());
        activityQrcodeScannerBinding.barcodeScanner.decodeContinuous(callback);

        beepManager = new BeepManager(getActivity());

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
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
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



    }


    @Override
    public void onResume() {
        super.onResume();
        activityQrcodeScannerBinding.barcodeScanner.resume();



    }

    @Override
    public void onPause() {
        super.onPause();
        activityQrcodeScannerBinding.barcodeScanner.pause();
    }


    public void triggerScan(View view) {
        activityQrcodeScannerBinding.barcodeScanner.decodeSingle(callback);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return activityQrcodeScannerBinding.barcodeScanner.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
//    }


    void requestPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 0);
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

//                    activityQrcodeScannerBinding.standardBottomSheet.setVisibility(View.GONE);
//                    Intent intent = new Intent(getContext(), RedeemActivity.class);
//                    intent.putExtra("couponcode", couponcode); //"d5384662-4420-4731-99f1-d8b9c76fa487");
//                    intent.putExtra("coupons", apiResponse.coupons); //"d5384662-4420-4731-99f1-d8b9c76fa487");
//                    startActivityForResult(intent, mRequestCode);
                   // apiResponse.coupons = null;
                    //onClick2();

                    Bundle bundle = new Bundle();
                    bundle.putString("couponcode",couponcode);
                    bundle.putSerializable("coupons",apiResponse.coupons);

                    FragmentManager fragmentManager = getFragmentManager();
                    RedeemFragment redeemFragment = new RedeemFragment();
                    redeemFragment.setArguments(bundle);

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace( R.id.nav_host_fragment, redeemFragment ).addToBackStack( null ).commit();

                }else if(apiResponse.errorMessage!=null){
                    // errorMessage(apiResponse.errorMessage);
                    String msg = getResources().getString(R.string.invalid_qr_code_try_again);

                    showOptions(getContext(),msg);
                } else{
                    // call failed.
                    Throwable e = apiResponse.getError();
                    //Toast.makeText(QRCodeScannerActivity.this, "Error is " + e.getMessage(), Toast.LENGTH_SHORT).show();
                   // errorMessage("Unable to reach server");
                    showOptions(getContext(),"Unable to reach server");

                    //showOptions(getBaseContext());

                }
            }
        });
    }


    private void showOptions(Context mcon,String msg){
        try{

            activityQrcodeScannerBinding.barcodeScanner.pause();

            LayoutInflater inflater = (LayoutInflater) mcon.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.rq_error_popup,null);

            Button closebtn = layout.findViewById(R.id.closebtn);

            closebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(optionspu!=null)
                        optionspu.dismiss();

                    if (activityQrcodeScannerBinding.editCoupon.isFocused()) {
                        activityQrcodeScannerBinding.editCoupon.clearFocus();
                    }

                    //activityQrcodeScannerBinding.barcodeScanner.is

                }
            });

            optionspu = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            optionspu.setAnimationStyle(R.style.popup_window_animation);
            optionspu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            optionspu.setFocusable(true);
            optionspu.setOutsideTouchable(true);
            optionspu.update(0, 0, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            optionspu.showAtLocation(layout, Gravity.CENTER, 0, 0);

            optionspu.setOutsideTouchable(false);
            optionspu.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                   // lastText = "" ;
                   // activityQrcodeScannerBinding.barcodeScanner.resume();
                }
            });

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public void onClick2() {
//        RedeemFragment fragment2 = new RedeemFragment();
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.nav_host_fragment, fragment2);
//        fragmentTransaction.commit();

        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace( R.id.nav_host_fragment, new RedeemFragment() ).addToBackStack( null ).commit();

        //fragmentTransaction.addToBackStack("");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.i("onFragmentInteraction", String.valueOf(uri));
    }
}