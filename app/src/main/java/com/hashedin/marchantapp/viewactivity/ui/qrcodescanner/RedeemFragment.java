package com.hashedin.marchantapp.viewactivity.ui.qrcodescanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.Repository.ApiResponse;
import com.hashedin.marchantapp.Services.models.QRCodeScanModel.Coupons;
import com.hashedin.marchantapp.Services.models.RedeemCoupon.RedeemCoupon;
import com.hashedin.marchantapp.databinding.ActivityRedeemBinding;
import com.hashedin.marchantapp.viewactivity.MerchantMainActivity;
import com.hashedin.marchantapp.viewactivity.Utility.DialogsUtils;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;
import com.hashedin.marchantapp.viewmodel.CouponDetailViewModel;


public class RedeemFragment extends Fragment {

    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private Coupons coupons = null;
    private ActivityRedeemBinding activityRedeemBinding;
    private PopupWindow optionspu;
    private ProgressDialog myDialog;
    private String couponcode = null;
    private boolean offernameExpand = true, merchantnameExpand = true, descriptionExpand = true;
    private CouponDetailViewModel viewModel;
    private String auth_token;
    private boolean redeemed = false;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RedeemFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RedeemFragment newInstance(String param1, String param2) {
        RedeemFragment fragment = new RedeemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MerchantMainActivity.currentFragment = "RedeemFragment";
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_redeem, container, false);
        activityRedeemBinding = DataBindingUtil.inflate(
                inflater, R.layout.activity_redeem, container, false);
        View root = activityRedeemBinding.getRoot();
        Bundle bundle = getArguments();
        if (bundle != null) {
            couponcode = bundle.getString("couponcode");
            coupons = (Coupons) bundle.getSerializable("coupons");
            Initializer();
            updateUI(coupons);
        }
        coordinatorLayout = (CoordinatorLayout) root.findViewById(R.id.coordinatorLayout);
        activityRedeemBinding.backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null)
                    getFragmentManager().popBackStack();
            }
        });
        activityRedeemBinding.editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(),"clicked",Toast.LENGTH_LONG).show();
                if (offernameExpand) {
                    activityRedeemBinding.editName.setSingleLine(false);
                    offernameExpand = false;
                } else {
                    activityRedeemBinding.editName.setSingleLine(true);
                    offernameExpand = true;
                }
            }
        });
        activityRedeemBinding.editMerchantname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(),"clicked",Toast.LENGTH_LONG).show();
                if (merchantnameExpand) {
                    activityRedeemBinding.editMerchantname.setSingleLine(false);
                    merchantnameExpand = false;
                } else {
                    activityRedeemBinding.editMerchantname.setSingleLine(true);
                    merchantnameExpand = true;
                }
            }
        });
        activityRedeemBinding.editDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(),"clicked",Toast.LENGTH_LONG).show();
                if (descriptionExpand) {
                    activityRedeemBinding.editDescription.setSingleLine(false);
                    descriptionExpand = false;
                } else {
                    activityRedeemBinding.editDescription.setSingleLine(true);
                    descriptionExpand = true;
                }
            }
        });
        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void Initializer() {
        activityRedeemBinding.redeembtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redeemcoupon();
            }
        });
        PrefManager prefManager = new PrefManager(getContext());
        auth_token = "token " + prefManager.getKey();
        viewModel = ViewModelProviders.of(this).get(CouponDetailViewModel.class);

    }


    private void updateUI(Coupons coupons) {
        if (coupons.offer.name != null)
            activityRedeemBinding.editName.setText("" + coupons.offer.name);
        if (coupons.offer.description != null)
            activityRedeemBinding.editDescription.setText("" + coupons.offer.description);
        if (coupons.offer.points != 0)
            activityRedeemBinding.editPoints.setText("" + coupons.offer.points);
        if (coupons.offer.item != null)
            activityRedeemBinding.editItem.setText("" + coupons.offer.item);
        try {
            if (coupons.created_on != null) {
                if (coupons.created_on.contains("T") && coupons.created_on.contains("Z")) {
                    String[] str = coupons.created_on.split("T");
                    if (str.length >= 2) {
                        activityRedeemBinding.editStartdate.setText("" + str[0]);
                    } else {
                        activityRedeemBinding.editStartdate.setText("" + coupons.created_on);

                    }

                } else
                    activityRedeemBinding.editStartdate.setText("" + coupons.created_on);
            }
            if (coupons.offer.ends_at != null) {
                if (coupons.offer.ends_at.contains("T") && coupons.offer.starts_at.contains("Z")) {
                    String[] str = coupons.offer.ends_at.split("T");//replace("T", " ").replace("Z", " ")
                    if (str.length >= 2) {
                        activityRedeemBinding.editEnddate.setText("" + str[0]);
                        // activityRedeemBinding.editEnddate.setText("" + coupons.offer.ends_at.replace("T", " ").replace("Z", " "));
                    } else {
                        activityRedeemBinding.editEnddate.setText("" + coupons.offer.ends_at);
                    }


                } else {
                    activityRedeemBinding.editEnddate.setText("" + coupons.offer.ends_at);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (coupons.created_on != null) {
                activityRedeemBinding.editStartdate.setText("" + coupons.created_on);
            }
            if (coupons.offer.ends_at != null) {
                activityRedeemBinding.editEnddate.setText("" + coupons.offer.ends_at);
            }


        }
        if (coupons.offer.merchant.name != null)
            activityRedeemBinding.editMerchantname.setText("" + coupons.offer.merchant.name);
   }

    private void showOptions(Context mcon, String st, RedeemCoupon redeemCoupon) {
        try {
            LayoutInflater inflater = (LayoutInflater) mcon.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.redeem_response_popup, null);
            TextView state = layout.findViewById(R.id.state);
            state.setText(st);
            if (redeemCoupon != null) {
                // TODO
            } else {
                TextView amount = layout.findViewById(R.id.amount);
                amount.setVisibility(View.GONE);
            }
            Button closebtn = layout.findViewById(R.id.closebtn);
            closebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //onBackPressed();
                }
            });
            PopupWindow optionspu2 = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            optionspu2.setAnimationStyle(R.style.popup_window_animation);
            optionspu2.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            optionspu2.setFocusable(true);
            optionspu2.setOutsideTouchable(true);
            optionspu2.update(0, 0, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            optionspu2.showAtLocation(layout, Gravity.CENTER, 0, 0);
            optionspu2.setOutsideTouchable(false);
            optionspu2.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void snackbarMessage(String msg) {
        snackbar = Snackbar
                .make(coordinatorLayout, msg, Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
        // Changing message text color
        snackbar.setActionTextColor(Color.RED);
        snackbar.setDuration(5000);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.black));
        TextView message_textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        message_textView.setTextColor(getResources().getColor(R.color.white));


    }


    public void redeemcoupon() {
        if (coupons != null) {
            myDialog = DialogsUtils.showProgressDialog(getContext(), "Loading please wait");
            if (redeemed) {
                viewModel.getRedeem(couponcode, auth_token);
            } else {
                getReddemCoupon();
            }
        } else {
            Toast.makeText(getContext(), "Invalid Coupon", Toast.LENGTH_LONG).show();

        }
    }


    public void getReddemCoupon() {
        viewModel.getRedeem(couponcode, auth_token).observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse apiResponse) {
                if (myDialog != null)
                    myDialog.dismiss();
                if (apiResponse == null) {
                    // handle error here
                    return;
                }
                if (apiResponse.redeemCoupon != null && apiResponse.getError() == null) {
                    // call is successful
                    //Log.i(TAG, "Data response is " + apiResponse.getPosts());
                    RedeemCoupon redeemCoupon = apiResponse.redeemCoupon;
                    String successstr = getResources().getString(R.string.paid_successfully);
                    //showOptions(getContext(),successstr,redeemCoupon);
                    showOptions(getContext(), successstr);


                } else if (apiResponse.errorMessage != null) {
                    String failedstr = apiResponse.errorMessage; //getResources().getString(R.string.paid_Failed);
                    showOptions(getContext(), failedstr);
                } else {
                    // call failed.
                    Throwable e = apiResponse.getError();
                    showOptions(getContext(), "Unable to reach server");
//                    snackbarMessage("Unable to reach server");
//                    snackbar.show();
                }
            }
        });
        redeemed = true;


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

}
