package com.hashedin.marchantapp.viewactivity.ui.qrcodescanner;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
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
import com.hashedin.marchantapp.Services.Repository.ApiResponse;
import com.hashedin.marchantapp.databinding.ActivityQrcodeScannerBinding;
import com.hashedin.marchantapp.viewactivity.MerchantMainActivity;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;
import com.hashedin.marchantapp.viewmodel.CouponDetailViewModel;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class QRCodeScannerFragment extends Fragment implements RedeemFragment.OnFragmentInteractionListener {

    private boolean flash_state = false;
    private BeepManager beepManager;
    private String lastText;
    private ActivityQrcodeScannerBinding activityQrcodeScannerBinding;
    private BottomSheetBehavior sheetBehavior;
    private CouponDetailViewModel viewModel;
    private String auth_token;
    private String couponcode = "";
    private PopupWindow optionspu;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null && !result.getText().equals(lastText)) {
                if (!TextUtils.isEmpty(result.getText())) {
                    lastText = result.getText();
                    couponcode = result.getText();
                    beepManager.playBeepSoundAndVibrate();
                    viewModel.getData(result.getText(), auth_token);

                }
            }
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
        auth_token = "token " + prefManager.getKey();
        viewModel = ViewModelProviders.of(this).get(CouponDetailViewModel.class);
        getCouponDetail();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MerchantMainActivity.currentFragment = "QRCodeScannerFragment";
        activityQrcodeScannerBinding = DataBindingUtil.inflate(
                inflater, R.layout.activity_qrcode_scanner, container, false);
        View root = activityQrcodeScannerBinding.getRoot();

        initialize();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    public void initialize() {
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
        //activityQrcodeScannerBinding.proceedbtn.setVisibility(View.GONE);
        activityQrcodeScannerBinding.standardBottomSheet.setVisibility(View.GONE);
        activityQrcodeScannerBinding.editCouponCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                QRCodeEditorFragment redeemFragment = new QRCodeEditorFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, redeemFragment).addToBackStack(null).commit();
            }
        });
        activityQrcodeScannerBinding.proceedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                if (activityQrcodeScannerBinding.editCoupon != null && activityQrcodeScannerBinding.editCoupon.getText().length() > 3) {
                    // getCouponDetail(activityQrcodeScannerBinding.editCoupon.getText().toString());
                    couponcode = activityQrcodeScannerBinding.editCoupon.getText().toString();
                    viewModel.getData(activityQrcodeScannerBinding.editCoupon.getText().toString(), auth_token);


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
                lastText = "";
                activityQrcodeScannerBinding.barcodeScanner.resume();

            }
        });
        activityQrcodeScannerBinding.closeaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null)
                    getFragmentManager().popBackStack();
            }
        });
        activityQrcodeScannerBinding.flashlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flash_state) {
                    activityQrcodeScannerBinding.flashlight.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_flash_off), null, null, null);
                    activityQrcodeScannerBinding.flashlight.setText("TURN OF FLASH");
                    flash_state = false;
                    activityQrcodeScannerBinding.barcodeScanner.setTorchOff();


                } else {
                    activityQrcodeScannerBinding.flashlight.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_flash_on), null, null, null);
                    activityQrcodeScannerBinding.flashlight.setText("TURN ON FLASH");
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


    private void errorMessage(String msg) {
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


    public void getCouponDetail() {
        viewModel.getData(couponcode, auth_token).observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse apiResponse) {
                if (apiResponse == null) {
                    // handle error here
                    return;
                }
                if (apiResponse.coupons != null && apiResponse.getError() == null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("couponcode", couponcode);
                    bundle.putSerializable("coupons", apiResponse.coupons);
                    FragmentManager fragmentManager = getFragmentManager();
                    RedeemFragment redeemFragment = new RedeemFragment();
                    redeemFragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, redeemFragment).addToBackStack(null).commit();

                } else if (apiResponse.errorMessage != null) {
                    String msg = getResources().getString(R.string.invalid_qr_code_try_again);
                    showOptions(getContext(), msg);
                } else {
                    Throwable e = apiResponse.getError();
                    showOptions(getContext(), "Unable to reach server");


                }
            }
        });
    }


    private void showOptions(Context mcon, String msg) {
        try {
            activityQrcodeScannerBinding.barcodeScanner.pause();
            LayoutInflater inflater = (LayoutInflater) mcon.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.rq_error_popup, null);
            TextView textView = layout.findViewById(R.id.errormessage);
            AppCompatImageView appCompatImageView = layout.findViewById(R.id.imageViewstatus);
            textView.setText(msg);
            TextView closebtn = layout.findViewById(R.id.closebtn);
            closebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (optionspu != null) {
                        optionspu.dismiss();
                        if (activityQrcodeScannerBinding.editCoupon.isFocused()) {
                            activityQrcodeScannerBinding.editCoupon.clearFocus();
                        }
                        //activityQrcodeScannerBinding.barcodeScanner.is
                        // activityQrcodeScannerBinding.barcodeScanner.resume();
                    }


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
                    lastText = "";
                    activityQrcodeScannerBinding.barcodeScanner.resume();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.i("onFragmentInteraction", String.valueOf(uri));
    }
}