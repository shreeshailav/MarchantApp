package com.hashedin.marchantapp.viewactivity.ui.qrcodegenerate;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.Repository.ApiResponse;
import com.hashedin.marchantapp.Services.models.QRCodeGenerateModel.QRInfo;
import com.hashedin.marchantapp.databinding.ActivityQrcodeGeneratorBinding;
import com.hashedin.marchantapp.viewactivity.MerchantMainActivity;
import com.hashedin.marchantapp.viewactivity.Utility.DecimalDigitsInputFilter;
import com.hashedin.marchantapp.viewactivity.Utility.DialogsUtils;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;
import com.hashedin.marchantapp.viewmodel.CouponDetailViewModel;

public class QRCodeGenerateFragment extends Fragment {


    final static int QRcodeWidth = 500;
    private String auth_token;
    private Boolean isRequestsent = false;
    private ActivityQrcodeGeneratorBinding activityQrcodeGeneratorBinding;
    private CouponDetailViewModel viewModel;
    private ProgressDialog myDialog;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MerchantMainActivity.currentFragment = "QRCodeGenerateFragment";
        activityQrcodeGeneratorBinding = DataBindingUtil.inflate(
                inflater, R.layout.activity_qrcode_generator, container, false);
        View root = activityQrcodeGeneratorBinding.getRoot();
        activityQrcodeGeneratorBinding.editTransactionAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(10, 2)});
        activityQrcodeGeneratorBinding.backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null)
                    getFragmentManager().popBackStack();

            }
        });
        activityQrcodeGeneratorBinding.editTransactionId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(activityQrcodeGeneratorBinding.editTransactionId.getText().toString()) || TextUtils.isEmpty(activityQrcodeGeneratorBinding.editTransactionAmount.getText().toString())) {
                    activityQrcodeGeneratorBinding.generateQrCodeSubmit.setAlpha(.5f);
                    activityQrcodeGeneratorBinding.generateQrCodeSubmit.setEnabled(false);
                } else if (!TextUtils.isEmpty(activityQrcodeGeneratorBinding.editTransactionId.getText().toString()) && !TextUtils.isEmpty(activityQrcodeGeneratorBinding.editTransactionAmount.getText().toString())) {
                    activityQrcodeGeneratorBinding.generateQrCodeSubmit.setAlpha(1f);
                    activityQrcodeGeneratorBinding.generateQrCodeSubmit.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        activityQrcodeGeneratorBinding.editTransactionAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(activityQrcodeGeneratorBinding.editTransactionId.getText().toString()) || TextUtils.isEmpty(activityQrcodeGeneratorBinding.editTransactionAmount.getText().toString())) {
                    activityQrcodeGeneratorBinding.generateQrCodeSubmit.setAlpha(.5f);
                    activityQrcodeGeneratorBinding.generateQrCodeSubmit.setEnabled(false);
                } else if (!TextUtils.isEmpty(activityQrcodeGeneratorBinding.editTransactionId.getText().toString()) && !TextUtils.isEmpty(activityQrcodeGeneratorBinding.editTransactionAmount.getText().toString())) {
                    activityQrcodeGeneratorBinding.generateQrCodeSubmit.setAlpha(1f);
                    activityQrcodeGeneratorBinding.generateQrCodeSubmit.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        activityQrcodeGeneratorBinding.generateQrCodeSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QRInfo userCredentials = new QRInfo(activityQrcodeGeneratorBinding.editTransactionAmount.getText().toString(), activityQrcodeGeneratorBinding.editTransactionId.getText().toString());
                PrefManager prefManager = new PrefManager(getContext());
                auth_token = "token " + prefManager.getKey();
                myDialog = DialogsUtils.showProgressDialog(getContext(), "Loading please wait");
                if (isRequestsent) {
                    viewModel.getQRUUID(userCredentials, auth_token);
                } else {
                    RequistQRCode(userCredentials);
                }
            }
        });
        viewModel = ViewModelProviders.of(this).get(CouponDetailViewModel.class);
        return root;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void RequistQRCode(QRInfo userCredentials) {
        isRequestsent = true;
        viewModel.getQRUUID(userCredentials, auth_token).observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse apiResponse) {
                if (myDialog != null)
                    myDialog.dismiss();
                if (apiResponse == null) {
                    // handle error here
                    return;
                }
                if (apiResponse.generateQR != null && apiResponse.getError() == null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("generateQR", apiResponse.generateQR);
                    FragmentManager fragmentManager = getFragmentManager();
                    QRCodeScanAndPayFragment redeemFragment = new QRCodeScanAndPayFragment();
                    redeemFragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, redeemFragment).addToBackStack(null).commit();


                } else if (apiResponse.errorMessage != null) {
                    String failedstr = apiResponse.errorMessage; //getResources().getString(R.string.paid_Failed);
                } else {
                    Throwable e = apiResponse.getError();

                }
            }
        });


    }

}