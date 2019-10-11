package com.hashedin.marchantapp.viewactivity.ui.qrcodescanner;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.Repository.ApiResponse;
import com.hashedin.marchantapp.databinding.FragmentCouponEditBinding;
import com.hashedin.marchantapp.viewactivity.MerchantMainActivity;
import com.hashedin.marchantapp.viewactivity.Utility.DialogsUtils;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;
import com.hashedin.marchantapp.viewmodel.CouponDetailViewModel;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class QRCodeEditorFragment extends Fragment implements RedeemFragment.OnFragmentInteractionListener {


    private FragmentCouponEditBinding activityQrcodeScannerBinding;
    private ProgressDialog myDialog;
    private CouponDetailViewModel viewModel;
    private String auth_token;
    private String couponcode = "";
    private PopupWindow optionspu;


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
        MerchantMainActivity.currentFragment = "QRCodeEditorFragment";
        activityQrcodeScannerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_coupon_edit, container, false);
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
        String lastText = "";
        activityQrcodeScannerBinding.redeembtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                if (activityQrcodeScannerBinding.editName != null && activityQrcodeScannerBinding.editName.getText().length() > 3) {
                    // getCouponDetail(activityQrcodeScannerBinding.editCoupon.getText().toString());
                    couponcode = activityQrcodeScannerBinding.editName.getText().toString();
                    myDialog = DialogsUtils.showProgressDialog(getContext(), "Loading please wait");
                    viewModel.getData(activityQrcodeScannerBinding.editName.getText().toString(), auth_token);


                } else {
                    activityQrcodeScannerBinding.editName.setError("Please Enter Valid Code");
                }
            }
        });
        activityQrcodeScannerBinding.editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        activityQrcodeScannerBinding.editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(activityQrcodeScannerBinding.editName.getText().toString())) {
                    activityQrcodeScannerBinding.redeembtn.setAlpha(.5f);
                    activityQrcodeScannerBinding.redeembtn.setEnabled(false);
                } else if (!TextUtils.isEmpty(activityQrcodeScannerBinding.editName.getText().toString())) {
                    activityQrcodeScannerBinding.redeembtn.setAlpha(1f);
                    activityQrcodeScannerBinding.redeembtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        activityQrcodeScannerBinding.backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null)
                    getFragmentManager().popBackStack();
            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();
    }


    void requestPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 0);
        }
    }


    private void getCouponDetail() {
        viewModel.getData(couponcode, auth_token).observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse apiResponse) {
                if (myDialog != null)
                    myDialog.dismiss();
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
                    // errorMessage(apiResponse.errorMessage);
                    String msg = getResources().getString(R.string.invalid_qr_code_try_again);
                    showOptions(getContext(), msg);
                } else {
                    // call failed.
                    Throwable e = apiResponse.getError();
                    //Toast.makeText(QRCodeScannerActivity.this, "Error is " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // errorMessage("Unable to reach server");
                    showOptions(getContext(), "Unable to reach server");
                    //showOptions(getBaseContext());
                }
            }
        });
    }


    private void showOptions(Context mcon, String msg) {
        try {
            LayoutInflater inflater = (LayoutInflater) mcon.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.rq_error_popup, null);
            TextView closebtn = layout.findViewById(R.id.closebtn);
            closebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (optionspu != null) {
                        optionspu.dismiss();
                        if (activityQrcodeScannerBinding.editName.isFocused()) {
                            activityQrcodeScannerBinding.editName.clearFocus();
                        }


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