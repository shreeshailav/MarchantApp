package com.hashedin.marchantapp.ViewActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.MenuItem;

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.databinding.ActivityRedeemResponseBinding;

public class RedeemResponseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");

        //  setContentView(R.layout.activity_redeem_response);
        ActivityRedeemResponseBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_redeem_response);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
