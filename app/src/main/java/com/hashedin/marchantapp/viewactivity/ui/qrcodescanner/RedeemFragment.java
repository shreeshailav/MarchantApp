package com.hashedin.marchantapp.viewactivity.ui.qrcodescanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.models.Coupons;
import com.hashedin.marchantapp.Services.models.ReddemCoupon;
import com.hashedin.marchantapp.Services.Repository.ApiEndpoints;
import com.hashedin.marchantapp.Services.Repository.ApiResponse;
import com.hashedin.marchantapp.databinding.ActivityRedeemBinding;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;
import com.hashedin.marchantapp.viewmodel.CouponDetailViewModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RedeemFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RedeemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RedeemFragment extends Fragment {

    Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;

    private Coupons coupons = null ;

    private ActivityRedeemBinding activityRedeemBinding;
    private PopupWindow optionspu;


    ApiEndpoints marchentServices;
    ProgressDialog myDialog;

    String couponcode = null;

   // ActivityRedeemBinding activityLoginBinding;

    CouponDetailViewModel viewModel;
    String auth_token;

    boolean redeemed = false;

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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RedeemFragment.
     */
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
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_redeem, container, false);

        activityRedeemBinding = DataBindingUtil.inflate(
                inflater, R.layout.activity_redeem, container, false);

        View root = activityRedeemBinding.getRoot();

        Bundle bundle = getArguments();
        if(bundle!=null) {

            couponcode = bundle.getString("couponcode");
            coupons = (Coupons) bundle.getSerializable("coupons");

            Initializer();
            updateUI(coupons);
        }

        coordinatorLayout = (CoordinatorLayout) root.findViewById(R.id.coordinatorLayout);



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
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void Initializer(){

        activityRedeemBinding.redeembtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redeemcoupon();
            }
        });

        PrefManager prefManager = new PrefManager(getContext());

        auth_token = "token "+prefManager.getKey();

        viewModel = ViewModelProviders.of(this).get(CouponDetailViewModel.class);

    }



    private void updateUI(Coupons coupons){
        if(coupons.offer.name!=null)
            activityRedeemBinding.editName.setText(""+coupons.offer.name);
        if(coupons.offer.description!=null)
            activityRedeemBinding.editDescription.setText(""+coupons.offer.description);
        if(coupons.offer.points!=0)
            activityRedeemBinding.editPoints.setText(""+coupons.offer.points);
        if(coupons.offer.item!=null)
            activityRedeemBinding.editItem.setText(""+coupons.offer.item);
        if(coupons.offer.starts_at!=null)
            activityRedeemBinding.editStartdate.setText(""+coupons.offer.starts_at);
        if(coupons.offer.ends_at!=null)
            activityRedeemBinding.editEnddate.setText(""+coupons.offer.ends_at);

        activityRedeemBinding.editName.setPaintFlags(activityRedeemBinding.editName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        activityRedeemBinding.editDescription.setPaintFlags(activityRedeemBinding.editDescription.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        activityRedeemBinding.editPoints.setPaintFlags(activityRedeemBinding.editPoints.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        activityRedeemBinding.editItem.setPaintFlags(activityRedeemBinding.editItem.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        activityRedeemBinding.editStartdate.setPaintFlags(activityRedeemBinding.editStartdate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        activityRedeemBinding.editEnddate.setPaintFlags(activityRedeemBinding.editEnddate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


    }

    private void showOptions(Context mcon,String st,ReddemCoupon reddemCoupon){
        try{
            LayoutInflater inflater = (LayoutInflater) mcon.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.redeem_response_popup,null);

            TextView state = layout.findViewById(R.id.state);
            state.setText(st);

            if(reddemCoupon!=null){
                // TODO
            }else {
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
//                    Intent i = new Intent();
//                    setResult(RESULT_OK, i);
//                    finish();
                }
            });

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



    private void snackbarMessage(String msg){
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



    public void redeemcoupon(){

        if(coupons!=null){

            String start_at = coupons.offer.starts_at;
            String end_at = coupons.offer.ends_at;
            Date cuur_date = new Date();

            int redeem_limit = coupons.offer.redeem_limit;
            int total_redeem = coupons.redeemed;


            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


            try {
                Date start_date = dateFormat.parse(start_at);
                Date end_date = dateFormat.parse(end_at);

                if(total_redeem>=redeem_limit){
//                    snackbarMessage("Limit exceeded.");
//                    snackbar.show();
                    showOptions(getContext(),"Coupon Already Redeemed ");

                }else if(cuur_date.before(end_date) && cuur_date.after(start_date) ){


                    if(redeemed)
                        viewModel.getRedeem(couponcode,auth_token);
                    else
                        getReddemCoupon();

                }else {
//                    snackbarMessage("Invalid Coupon");
//                    snackbar.show();
                    showOptions(getContext(),"Invalid Coupon");

                }

            } catch (ParseException e) {
                Log.e("ERROR",e.getMessage());
                e.printStackTrace();
            }
        }else {
            Toast.makeText(getContext(),"Invalid Coupon",Toast.LENGTH_LONG).show();

        }
    }




    public void getReddemCoupon(){


        viewModel.getRedeem(couponcode,auth_token).observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse apiResponse) {
                if (apiResponse == null) {
                    // handle error here
                    return;
                }
                if (apiResponse.reddemCoupon!=null && apiResponse.getError() == null ) {
                    // call is successful
                    //Log.i(TAG, "Data response is " + apiResponse.getPosts());

                    ReddemCoupon reddemCoupon = apiResponse.reddemCoupon;
                    String successstr = getResources().getString(R.string.paid_successfully);
                    showOptions(getContext(),successstr,reddemCoupon);



                }else if(apiResponse.errorMessage!=null){
                    String failedstr = getResources().getString(R.string.paid_Failed);
                    showOptions(getContext(),failedstr,null);
                } else{
                    // call failed.
                    Throwable e = apiResponse.getError();
                    showOptions(getContext(),"Unable to reach server");
//                    snackbarMessage("Unable to reach server");
//                    snackbar.show();
                }
            }
        });

        redeemed = true;



    }
    private void showOptions(Context mcon,String msg){
        try{


            LayoutInflater inflater = (LayoutInflater) mcon.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.rq_error_popup,null);

            TextView textView = layout.findViewById(R.id.errormessage);

            textView.setText(msg);

            Button closebtn = layout.findViewById(R.id.closebtn);

            closebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(optionspu!=null)
                        optionspu.dismiss();
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

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
