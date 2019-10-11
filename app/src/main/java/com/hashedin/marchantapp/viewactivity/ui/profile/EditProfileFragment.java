package com.hashedin.marchantapp.viewactivity.ui.profile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.databinding.FragmentEditProfileBinding;
import com.hashedin.marchantapp.viewactivity.ui.qrcodescanner.RedeemFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditProfileFragment extends Fragment implements RedeemFragment.OnFragmentInteractionListener{


    private FragmentEditProfileBinding fragmentEditProfileBinding;

     private Calendar myCalendar;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        fragmentEditProfileBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_edit_profile, container, false);

        View root = fragmentEditProfileBinding.getRoot();

        initialize();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    public void initialize(){
        myCalendar = Calendar.getInstance();


        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        fragmentEditProfileBinding.editProfileDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        new DatePickerDialog(getContext(), date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();

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



    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.i("onFragmentInteraction", String.valueOf(uri));
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        fragmentEditProfileBinding.editProfileDob.setText(sdf.format(myCalendar.getTime()));
    }

}