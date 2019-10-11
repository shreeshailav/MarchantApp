package com.hashedin.marchantapp.viewactivity.ui.qrcodegenerate;

import android.app.Activity;
import android.app.ProgressDialog;
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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.Repository.ApiResponse;
import com.hashedin.marchantapp.Services.models.QRCodeGenerateModel.Payment;
import com.hashedin.marchantapp.Services.models.QRCodeGenerateModel.QRGenModel;
import com.hashedin.marchantapp.Services.models.TransacrionRequest.TransactionReq;
import com.hashedin.marchantapp.databinding.FragmentPaymentApproveBinding;
import com.hashedin.marchantapp.viewactivity.MerchantMainActivity;
import com.hashedin.marchantapp.viewactivity.Utility.DialogsUtils;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;
import com.hashedin.marchantapp.viewactivity.ui.qrcodescanner.RedeemFragment;
import com.hashedin.marchantapp.viewmodel.CouponDetailViewModel;

import java.util.List;

public class QRCodePaymentApproveFragment extends Fragment implements RedeemFragment.OnFragmentInteractionListener {


    private FragmentPaymentApproveBinding fragmentPaymentApproveBinding;
    private PopupWindow optionspu;
    private CouponDetailViewModel viewModel;
    private String UUID;
    private ProgressDialog myDialog;
    private String auth_token;
    private QRGenModel qrGenModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);


    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MerchantMainActivity.currentFragment = "QRCodePaymentApproveFragment";
        fragmentPaymentApproveBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_payment_approve, container, false);
        View root = fragmentPaymentApproveBinding.getRoot();
        Bundle bundle = getArguments();
        if (bundle != null) {
            UUID = bundle.getString("UUID");
            qrGenModel = (QRGenModel) bundle.getSerializable("qrGenModel");
            updateUI();
        }
        fragmentPaymentApproveBinding.paymentApproveCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeclineRequest();

            }
        });
        fragmentPaymentApproveBinding.paymentApproveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // showOptions(getContext(), "PAYMENT RECEIVED SUCCESSFULLY");
                getAcceptRequest();
            }
        });
        fragmentPaymentApproveBinding.backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }
        });
        initialize();
        viewModel = ViewModelProviders.of(this).get(CouponDetailViewModel.class);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }


    private void initialize() {
        PrefManager prefManager = new PrefManager(getContext());
        auth_token = "token " + prefManager.getKey();
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
                    if (getActivity() != null)
                        getActivity().onBackPressed();
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


    public void getAcceptRequest() {
        myDialog = DialogsUtils.showProgressDialog(getContext(), "Loading please wait");
        viewModel.getTransactionAcceptReq(UUID, auth_token).observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse apiResponse) {
                if (myDialog != null)
                    myDialog.dismiss();
                if (apiResponse == null) {
                    // handle error here
                    return;
                }
                if (apiResponse.transactionReq != null && apiResponse.getError() == null) {
                    // call is successful
                    TransactionReq transactionReq = apiResponse.transactionReq;
                    showOptions(getContext(), "PAYMENT RECEIVED SUCCESSFULLY");


                } else if (apiResponse.errorMessage != null) {
                    String failedstr = apiResponse.errorMessage; //getResources().getString(R.string.paid_Failed);
                    //showOptions(getContext(), failedstr);
                } else {
                    // call failed.
                    Throwable e = apiResponse.getError();
                }


            }
        });
    }


    public void getDeclineRequest() {
        myDialog = DialogsUtils.showProgressDialog(getContext(), "Loading please wait");
        viewModel.getTransactionDeclineReq(UUID, auth_token).observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse apiResponse) {
                if (myDialog != null)
                    myDialog.dismiss();
                if (apiResponse == null) {
                    // handle error here
                    return;
                }
                if (apiResponse.transactionReq != null && apiResponse.getError() == null) {
                    // call is successful
                    TransactionReq transactionReq = apiResponse.transactionReq;
                    showOptions(getContext(), "PAYMENT CANCELED SUCCESSFULLY");


                } else if (apiResponse.errorMessage != null) {
                    String failedstr = apiResponse.errorMessage; //getResources().getString(R.string.paid_Failed);
                    //showOptions(getContext(), failedstr);
                } else {
                    Throwable e = apiResponse.getError();
                }


            }
        });


    }

    private void updateUI() {
        if (qrGenModel != null) {
            if (qrGenModel.payed_to_object != null && qrGenModel.payed_to_object.market_rate != null) {
                String getperpointltrstart = getResources().getString(R.string._1_pt_1_mmk);
                String getperpointltrend = " " + getResources().getString(R.string._1_pt_1_mmk2);
                String mmkperpoint = getperpointltrstart + 2 + getperpointltrend;
                fragmentPaymentApproveBinding.mmkperpoint.setText(mmkperpoint);
            }
            float total_cash_paid = 0;
            float total_rewardspoint = 0;
            if (qrGenModel.meta != null && qrGenModel.meta.payments != null) {
                List<Payment> payments = qrGenModel.meta.payments;
                for (Payment payment : payments) {
                    if (payment.mode == 0) {
                        total_cash_paid += payment.value;
                    } else if (payment.mode == 2) {
                        total_rewardspoint += payment.value;
                    }
                }
                if (qrGenModel.payed_to_object != null && qrGenModel.payed_to_object.market_rate != null) {
                    try {
                        float marketrate = Float.parseFloat(qrGenModel.payed_to_object.market_rate);
                        total_rewardspoint = marketrate * total_rewardspoint;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
            String total_cash_paid_str = total_cash_paid + " MMK";
            String total_rewardspoint_str = total_rewardspoint + " MMK";
            fragmentPaymentApproveBinding.cashpaid.setText(total_cash_paid_str);
            fragmentPaymentApproveBinding.rewardpoint.setText(total_rewardspoint_str);
            if (qrGenModel.amount != null) {
                try {
                    String totalamtpaid = qrGenModel.amount.contains(".") ? qrGenModel.amount.replaceAll("0*$", "").replaceAll("\\.$", "") : qrGenModel.amount;
                    totalamtpaid = totalamtpaid + " MMK";
                    fragmentPaymentApproveBinding.totalamtpaid.setText(totalamtpaid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            fragmentPaymentApproveBinding.paidbycreditordebit.setText("0");
        }
    }

}