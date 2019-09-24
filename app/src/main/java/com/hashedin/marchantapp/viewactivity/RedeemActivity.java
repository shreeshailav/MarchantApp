package com.hashedin.marchantapp.viewactivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.models.Coupons;
import com.hashedin.marchantapp.Services.models.ReddemCoupon;
import com.hashedin.marchantapp.Services.Repository.ApiEndpoints;
import com.hashedin.marchantapp.Services.Repository.ApiResponse;
import com.hashedin.marchantapp.databinding.ActivityRedeemBinding;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;
import com.hashedin.marchantapp.viewmodel.CouponDetailViewModel;
import com.shreeshail.rxnetworkstate.ConnectionTracer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RedeemActivity extends AppCompatActivity implements ConnectionTracer {

    Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;

    private Coupons coupons = null ;


    ApiEndpoints marchentServices;
    ProgressDialog myDialog;

    String couponcode = null;

    ActivityRedeemBinding activityLoginBinding;

    CouponDetailViewModel viewModel;
    String auth_token;

    boolean redeemed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Redeem");

        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_redeem);



        Intent intent_extra = getIntent();
        if (intent_extra != null) {
            if(intent_extra.hasExtra("couponcode")) {
                couponcode = intent_extra.getStringExtra("couponcode");

                coupons = (Coupons) intent_extra.getSerializableExtra("coupons");
               if(coupons!=null) {
                   Initializer();
                   updateUI(coupons);

                }
               else {
                   returntback();
               }
               Initializer();

            }
            else {
                returntback();
            }
         }else {
            returntback();
        }
    }


    private void Initializer(){
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        snackbarMessage("No internet connection!");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        activityLoginBinding.redeembtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redeemcoupon();
            }
        });

        PrefManager prefManager = new PrefManager(this);

        auth_token = "token "+prefManager.getKey();

        viewModel = ViewModelProviders.of(this).get(CouponDetailViewModel.class);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                //intent.putExtra("result","Success");
                setResult(RESULT_OK, intent);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void updateUI(Coupons coupons){
        if(coupons.offer.name!=null)
            activityLoginBinding.editName.setText(""+coupons.offer.name);
        if(coupons.offer.description!=null)
            activityLoginBinding.editDescription.setText(""+coupons.offer.description);
        if(coupons.offer.points!=0)
            activityLoginBinding.editPoints.setText(""+coupons.offer.points);
        if(coupons.offer.item!=null)
            activityLoginBinding.editItem.setText(""+coupons.offer.item);
        if(coupons.offer.starts_at!=null)
            activityLoginBinding.editStartdate.setText(""+coupons.offer.starts_at);
        if(coupons.offer.ends_at!=null)
            activityLoginBinding.editEnddate.setText(""+coupons.offer.ends_at);

        activityLoginBinding.editName.setPaintFlags(activityLoginBinding.editName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        activityLoginBinding.editDescription.setPaintFlags(activityLoginBinding.editDescription.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        activityLoginBinding.editPoints.setPaintFlags(activityLoginBinding.editPoints.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        activityLoginBinding.editItem.setPaintFlags(activityLoginBinding.editItem.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        activityLoginBinding.editStartdate.setPaintFlags(activityLoginBinding.editStartdate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        activityLoginBinding.editEnddate.setPaintFlags(activityLoginBinding.editEnddate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


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
                    onBackPressed();
                }
            });

            PopupWindow optionspu = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
                    Intent i = new Intent();
                    setResult(RESULT_OK, i);
                    finish();
                }
            });

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed()
    {

        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();

    }

    @Override
    public void connectionState(int isConnected) {
        if(isConnected == 1){
            if(snackbar.isShown())
                snackbar.dismiss();
        }else {
            if(!snackbar.isShown())
                snackbar.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                    snackbarMessage("Limit exceeded.");
                    snackbar.show();
                }else if(cuur_date.before(end_date) && cuur_date.after(start_date) ){


                    if(redeemed)
                        viewModel.getRedeem(couponcode,auth_token);
                    else
                        getReddemCoupon();

                }else {
                    snackbarMessage("Invalid Coupon");
                    snackbar.show();
                }

            } catch (ParseException e) {
                Log.e("ERROR",e.getMessage());
                e.printStackTrace();
            }
        }else {
            Toast.makeText(getBaseContext(),"Invalid Coupon",Toast.LENGTH_LONG).show();

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
                    showOptions(getBaseContext(),successstr,reddemCoupon);



                }else if(apiResponse.errorMessage!=null){
                    String failedstr = getResources().getString(R.string.paid_Failed);
                    showOptions(getBaseContext(),failedstr,null);
                } else{
                    // call failed.
                    Throwable e = apiResponse.getError();
                    snackbarMessage("Unable to reach server");
                    snackbar.show();                }
            }
        });

        redeemed = true;



    }

    private void returntback(){
        Intent intent = new Intent();
        intent.putExtra("result", "Invalid Coupon");
        setResult(RESULT_OK, intent);
        finish();
    }


}
