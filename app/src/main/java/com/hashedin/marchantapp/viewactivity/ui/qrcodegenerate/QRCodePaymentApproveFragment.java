package com.hashedin.marchantapp.viewactivity.ui.qrcodegenerate;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.Repository.ApiResponse;
import com.hashedin.marchantapp.Services.models.TransacrionRequest.TransactionReq;
import com.hashedin.marchantapp.databinding.FragmentPaymentApproveBinding;
import com.hashedin.marchantapp.viewactivity.LoginActivity;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;
import com.hashedin.marchantapp.viewactivity.ui.qrcodescanner.RedeemFragment;
import com.hashedin.marchantapp.viewmodel.CouponDetailViewModel;

public class QRCodePaymentApproveFragment extends Fragment implements RedeemFragment.OnFragmentInteractionListener{


    private static final String TAG = LoginActivity.class.getSimpleName();



    FragmentPaymentApproveBinding fragmentPaymentApproveBinding;


    private PopupWindow optionspu;

    CouponDetailViewModel viewModel;


    String UUID ;


    String auth_token;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);


    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        fragmentPaymentApproveBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_payment_approve, container, false);

        View root = fragmentPaymentApproveBinding.getRoot();


        Bundle bundle = getArguments();
        if (bundle != null) {

            UUID = bundle.getString("UUID");


//            if(!TextUtils.isEmpty(generateQR.uuid)) {
//                UUID = generateQR.uuid;
//                new QRCodeScanAndPayFragment.loadqr().execute();
//            }

        }



        fragmentPaymentApproveBinding.paymentApproveCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null)
                    getFragmentManager().popBackStack();
            }
        });
        fragmentPaymentApproveBinding.paymentApproveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // showOptions(getContext(), "PAYMENT RECEIVED SUCCESSFULLY");

                getReddemCoupon();
            }
        });

        fragmentPaymentApproveBinding.backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null)
                    getFragmentManager().popBackStack();
            }
        });



        initialize();

        viewModel = ViewModelProviders.of(this).get(CouponDetailViewModel.class);



        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }


    private void initialize(){

    }

    @Override
    public void onResume() {
        super.onResume();



    }

    @Override
    public void onPause() {
        super.onPause();
     }





    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.i("onFragmentInteraction", String.valueOf(uri));
    }



    private void showOptions(Context mcon, String msg) {
        try {


            LayoutInflater inflater = (LayoutInflater) mcon.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.rq_error_popup, null);

            TextView textView = layout.findViewById(R.id.errormessage);
            AppCompatImageView appCompatImageView = layout.findViewById(R.id.imageViewstatus);
            textView.setText(msg.toUpperCase());

            if (msg.equalsIgnoreCase(getResources().getString(R.string.paid_successfully)))
                appCompatImageView.setImageResource(R.drawable.group_success_icon);
            else
                appCompatImageView.setImageResource(R.drawable.group_error_icon);

            TextView closebtn = layout.findViewById(R.id.closebtn);

            closebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (optionspu != null)
                        optionspu.dismiss();

                    if (getFragmentManager() != null)
                        getFragmentManager().popBackStack();
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


    public void getReddemCoupon() {

        PrefManager prefManager = new PrefManager(getContext());

        auth_token = "token " + "f2ddfb343b5793325ad74c841dfc7e3f4990f693";//prefManager.getKey();


        viewModel.getTransactionAcceptReq(UUID,auth_token).observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse apiResponse) {


//                if (myDialog != null)
////                    myDialog.dismiss();

                if (apiResponse == null) {
                    // handle error here
                    return;
                }
                if (apiResponse.generateQR != null && apiResponse.getError() == null) {
                    // call is successful
                    //Log.i(TAG, "Data response is " + apiResponse.getPosts());

                    TransactionReq transactionReq = apiResponse.transactionReq;

                     showOptions(getContext(), "PAYMENT RECEIVED SUCCESSFULLY");

                    //String successstr = getResources().getString(R.string.paid_successfully);
                    //showOptions(getContext(),successstr,reddemCoupon);
                    //showOptions(getContext(), successstr);

                   // Bundle bundle = new Bundle();
                    //bundle.putString("couponcode",couponcode);
                   // bundle.putSerializable("generateQR", apiResponse.transactionReq);

//                    FragmentManager fragmentManager = getFragmentManager();
//                    QRCodeScanAndPayFragment redeemFragment = new QRCodeScanAndPayFragment();
//                    redeemFragment.setArguments(bundle);
//
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.nav_host_fragment, redeemFragment).addToBackStack(null).commit();


                } else if (apiResponse.errorMessage != null) {
                    String failedstr = apiResponse.errorMessage; //getResources().getString(R.string.paid_Failed);
                    //showOptions(getContext(), failedstr);
                } else {
                    // call failed.
                    Throwable e = apiResponse.getError();
                    //showOptions(getContext(), "Unable to reach server");
//                    snackbarMessage("Unable to reach server");
//                    snackbar.show();
                }



            }
        });


//        viewModel.getQRUUID(userCredentials, auth_token).observe(this, new Observer<ApiResponse>() {
//            @Override
//            public void onChanged(ApiResponse apiResponse) {
////                if (myDialog != null)
////                    myDialog.dismiss();
//
//                if (apiResponse == null) {
//                    // handle error here
//                    return;
//                }
//                if (apiResponse.generateQR != null && apiResponse.getError() == null) {
//                    // call is successful
//                    //Log.i(TAG, "Data response is " + apiResponse.getPosts());
//
//                    GenerateQR generateQR = apiResponse.generateQR;
//                    //String successstr = getResources().getString(R.string.paid_successfully);
//                    //showOptions(getContext(),successstr,reddemCoupon);
//                    //showOptions(getContext(), successstr);
//
//                    Bundle bundle = new Bundle();
//                    //bundle.putString("couponcode",couponcode);
//                    bundle.putSerializable("generateQR", apiResponse.generateQR);
//
//                    FragmentManager fragmentManager = getFragmentManager();
//                    QRCodeScanAndPayFragment redeemFragment = new QRCodeScanAndPayFragment();
//                    redeemFragment.setArguments(bundle);
//
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.nav_host_fragment, redeemFragment).addToBackStack(null).commit();
//
//
//                } else if (apiResponse.errorMessage != null) {
//                    String failedstr = apiResponse.errorMessage; //getResources().getString(R.string.paid_Failed);
//                    //showOptions(getContext(), failedstr);
//                } else {
//                    // call failed.
//                    Throwable e = apiResponse.getError();
//                    //showOptions(getContext(), "Unable to reach server");
////                    snackbarMessage("Unable to reach server");
////                    snackbar.show();
//                }
//            }
//        });
    }


}