package com.hashedin.marchantapp.ViewActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.google.android.material.textfield.TextInputEditText;
import com.hashedin.marchantapp.R;
import com.shreeshail.rxnetworkstate.ConnectionManager;
import com.shreeshail.rxnetworkstate.ConnectionTracer;

public class RedeemActivity extends AppCompatActivity implements ConnectionTracer {

    private TextInputEditText edit_name,edit_description,edit_points,edit_item;
    private Button redeembtn;

    ConnectionManager connectionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);
        setTitle("Redeem");

        connectionManager = new ConnectionManager.Builder()
                .setContext(this)
                .setStatusView(this)
                .build();




        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edit_name = findViewById(R.id.edit_name);
        edit_description = findViewById(R.id.edit_description);
        edit_points = findViewById(R.id.edit_points);
        edit_item = findViewById(R.id.edit_item);
        redeembtn = findViewById(R.id.redeembtn);

        updateUI();

        redeembtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getBaseContext(),RedeemResponseActivity.class);
//                startActivity(intent);
//                finish();

                showOptions(getBaseContext());
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent();
                setResult(RESULT_OK, i);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void updateUI(){
        edit_name.setText("BigB-Offer-1");
        edit_description.setText("NA");
        edit_points.setText("10");
        edit_item.setText("NA");

    }


    private void showOptions(Context mcon){
        try{
            LayoutInflater inflater = (LayoutInflater) mcon.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.redeem_response_popup,null);
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

        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ERROR !!");
            builder.setMessage("Sorry there was an error getting data from the Internet.\nNetwork Unavailable!");

            AlertDialog dialog = builder.create();
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();

                }
            });

            dialog.show();
        }
    }

    @Override
    protected void onPause() {
        connectionManager.unsubcribeme();
        super.onPause();
    }
}
