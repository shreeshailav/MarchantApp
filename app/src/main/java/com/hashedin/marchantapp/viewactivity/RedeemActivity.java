package com.hashedin.marchantapp.viewactivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.Models.Coupons;
import com.hashedin.marchantapp.Services.Models.Credentials;
import com.hashedin.marchantapp.Services.Models.ReddemCoupon;
import com.hashedin.marchantapp.Services.Repository.MarchentServices;
import com.hashedin.marchantapp.viewactivity.Utility.DialogsUtils;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;
import com.shreeshail.rxnetworkstate.ConnectionManager;
import com.shreeshail.rxnetworkstate.ConnectionTracer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hashedin.marchantapp.Services.Repository.MarchentServices.HTTPS_API_MARCHENT_URL;

public class RedeemActivity extends AppCompatActivity implements ConnectionTracer {

    private TextInputEditText edit_name,edit_description,edit_points,edit_item,edit_amt,edit_reference;
    private Button redeembtn;

    ConnectionManager connectionManager;
    Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;

    private Coupons coupons = null ;


    MarchentServices marchentServices;
    ProgressDialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);
        setTitle("Redeem");



//        connectionManager = new ConnectionManager.Builder()
//                .setContext(this)
//                .setStatusView(this)
//                .build();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        snackbarMessage("No internet connection!");



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edit_name = findViewById(R.id.edit_name);
        edit_description = findViewById(R.id.edit_description);
        edit_points = findViewById(R.id.edit_points);
        edit_item = findViewById(R.id.edit_item);

        redeembtn = findViewById(R.id.redeembtn);


        edit_amt = findViewById(R.id.edit_amt);
        edit_reference = findViewById(R.id.edit_reference);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HTTPS_API_MARCHENT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        marchentServices = retrofit.create(MarchentServices.class);
        getcoupons();

       // updateUI();

        redeembtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redeemcoupon();
            }
        });

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
        edit_name.setText(""+coupons.offer.name);
        edit_description.setText(""+coupons.offer.description);
        edit_points.setText(""+coupons.offer.points);
        edit_item.setText(""+coupons.offer.item);

    }


    private void showOptions(Context mcon,String st,ReddemCoupon reddemCoupon){
        try{
            LayoutInflater inflater = (LayoutInflater) mcon.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.redeem_response_popup,null);

            TextView state = layout.findViewById(R.id.state);
            state.setText(st);

            if(reddemCoupon!=null){
                String amountstr = getResources().getString(R.string.amount);
                TextView amount = layout.findViewById(R.id.amount);
                amount.setVisibility(View.VISIBLE);
                int pos = reddemCoupon.amount.indexOf(".")+3;
                String amt = " " + reddemCoupon.amount.substring(0,pos);
                amount.setText(amountstr+amt);
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
        catch (Exception e){e.printStackTrace();
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
       // connectionManager.unsubcribeme();
        super.onPause();
    }

    @Override
    protected void onResume() {
//        try {
//            connectionManager = new ConnectionManager.Builder()
//                    .setContext(this)
//                    .setStatusView(this)
//                    .build();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
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



        // Changing action button text color
        View sbView = snackbar.getView();
//        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//        textView.setTextColor(Color.YELLOW);
     }



    public void getcoupons(){

        PrefManager prefManager = new PrefManager(this);

         String str = "465d5955-de23-4220-a96f-8c3ffaa00ae5";
         String auth_token = "token "+prefManager.getKey();


         Call<Coupons> couponsdata = marchentServices.getProjectList(str,auth_token);
         myDialog = DialogsUtils.showProgressDialog(RedeemActivity.this, "Loading please wait");


        couponsdata.enqueue(new Callback<Coupons>() {
            @Override
            public void onResponse(Call<Coupons> call, Response<Coupons> response) {
                Log.i("Responce","response"+response.body());

                if (myDialog!=null)
                    myDialog.dismiss();

                if(response.code() ==200) {
                    coupons = response.body();
                    updateUI(coupons);
                }else {
                    //Toast.makeText(getBaseContext(),"Failed",Toast.LENGTH_LONG).show();
                    snackbarMessage(response.message());
                    snackbar.show();

                    Intent intent = new Intent();
                    intent.putExtra("result",response.message());
                    setResult(RESULT_OK, intent);
                    finish();


                }

            }

            @Override
            public void onFailure(Call<Coupons> call, Throwable t) {
                Log.i("ERROR","Error : "+t.getMessage());
                snackbarMessage("Error");
                snackbar.show();

                if (myDialog!=null)
                    myDialog.dismiss();


            }
        });
    }


    public void getredeem(String ref,String amt){

        PrefManager prefManager = new PrefManager(this);

        String str = "465d5955-de23-4220-a96f-8c3ffaa00ae5";
        String auth_token = "token "+prefManager.getKey();


        Credentials credentials = new Credentials(amt,ref);

        Call<ReddemCoupon> couponsdata = marchentServices.getredeem(str,credentials,auth_token);

        couponsdata.enqueue(new Callback<ReddemCoupon>() {
            @Override
            public void onResponse(Call<ReddemCoupon> call, Response<ReddemCoupon> response) {
                Log.i("Responce","response"+response.toString());


                if(response.code() == 201) {
                    ReddemCoupon reddemCoupon = response.body();
                    String successstr = getResources().getString(R.string.paid_successfully);
                    showOptions(getBaseContext(),successstr,reddemCoupon);

                    // updateUI(coupons);
                }else {
                    Toast.makeText(getBaseContext(),"Failed",Toast.LENGTH_LONG).show();
                    String failedstr = getResources().getString(R.string.paid_Failed);
                    showOptions(getBaseContext(),failedstr,null);

                }

            }

            @Override
            public void onFailure(Call<ReddemCoupon> call, Throwable t) {
                Log.i("ERROR","Error : "+t.getMessage());



            }
        });
    }


    public void redeemcoupon(){

        if(edit_amt.getText().toString().length()<=0 && !edit_amt.getText().toString().equalsIgnoreCase(" ")) {

            edit_amt.setError("Enter Valid Amount");

        }else if(edit_reference.getText().toString().length()<=0 && !edit_reference.getText().toString().equalsIgnoreCase(" ")) {

            edit_reference.setError("Enter Valid Reference");


        }else if(coupons!=null){

            String start_at = coupons.offer.starts_at;
            String end_at = coupons.offer.starts_at;
            String cuur_date = new Date().toString();;

            int redeem_limit = coupons.offer.redeem_limit;
            int total_redeem = coupons.redeemed;


            SimpleDateFormat formatter1=new SimpleDateFormat("yyyy/MM/dd");

            try {
                Date date1=formatter1.parse(start_at);
                Date date2=formatter1.parse(end_at);
                Date currrentdate=formatter1.parse(cuur_date);

                if(total_redeem>=redeem_limit){
                    snackbarMessage("Limit exceeded.");
                }else {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(HTTPS_API_MARCHENT_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    marchentServices = retrofit.create(MarchentServices.class);
                    getredeem(edit_reference.getText().toString(), edit_amt.getText().toString());
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }else {
            Toast.makeText(getBaseContext(),"Invalid Coupon",Toast.LENGTH_LONG).show();

        }

    }




}
