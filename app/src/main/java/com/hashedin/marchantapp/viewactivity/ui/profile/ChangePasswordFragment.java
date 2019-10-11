package com.hashedin.marchantapp.viewactivity.ui.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.databinding.FragmentChangePasswordBinding;
import com.hashedin.marchantapp.viewactivity.ui.qrcodescanner.RedeemFragment;

public class ChangePasswordFragment extends Fragment implements RedeemFragment.OnFragmentInteractionListener {


    private FragmentChangePasswordBinding fragmentChangePasswordBinding;

    private boolean show_old_password = true;
    private boolean show_new_password = true;
    private boolean show_confirm_password = true;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        fragmentChangePasswordBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_change_password, container, false);

        View root = fragmentChangePasswordBinding.getRoot();


        initialize();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initialize() {


        fragmentChangePasswordBinding.oldPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= (fragmentChangePasswordBinding.oldPassword.getRight() - fragmentChangePasswordBinding.oldPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here


                        if (show_old_password) {
                            fragmentChangePasswordBinding.oldPassword.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                            fragmentChangePasswordBinding.oldPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_visiible), null);

                            show_old_password = false;
                        } else {
                            fragmentChangePasswordBinding.oldPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            fragmentChangePasswordBinding.oldPassword.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password
                            fragmentChangePasswordBinding.oldPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_invisiible), null);
                            show_old_password = true;

                        }


                        return true;
                    }
                }

                return false;
            }
        });
        fragmentChangePasswordBinding.newPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= (fragmentChangePasswordBinding.newPassword.getRight() - fragmentChangePasswordBinding.newPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here


                        if (show_new_password) {
                            fragmentChangePasswordBinding.newPassword.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                            fragmentChangePasswordBinding.newPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_visiible), null);

                            show_new_password = false;
                        } else {
                            fragmentChangePasswordBinding.newPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            fragmentChangePasswordBinding.newPassword.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password
                            fragmentChangePasswordBinding.newPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_invisiible), null);
                            show_new_password = true;

                        }


                        return true;
                    }
                }

                return false;
            }
        });
        fragmentChangePasswordBinding.confirmPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= (fragmentChangePasswordBinding.confirmPassword.getRight() - fragmentChangePasswordBinding.confirmPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {


                        if (show_confirm_password) {
                            fragmentChangePasswordBinding.confirmPassword.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                            fragmentChangePasswordBinding.confirmPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_visiible), null);

                            show_confirm_password = false;
                        } else {
                            fragmentChangePasswordBinding.confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            fragmentChangePasswordBinding.confirmPassword.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password
                            fragmentChangePasswordBinding.confirmPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_invisiible), null);
                            show_confirm_password = true;

                        }


                        return true;
                    }
                }

                return false;
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
}