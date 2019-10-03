package com.hashedin.marchantapp.viewactivity.ui.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.databinding.FragmentProfileBinding;
import com.hashedin.marchantapp.viewactivity.LoginActivity;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;
import com.hashedin.marchantapp.viewactivity.Utility.ProfilePrefManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding fragmentProfileBinding;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    ProfilePrefManager profilePrefManager;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        fragmentProfileBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_profile, container, false);

        View root = fragmentProfileBinding.getRoot();


        fragmentProfileBinding.backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null)
                    getFragmentManager().popBackStack();
            }
        });


        fragmentProfileBinding.signoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrefManager prefManager = new PrefManager(getContext());
                prefManager.logout();

                if (prefManager.isUserLogedOut()) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    if (getActivity() != null)
                        getActivity().finish();
                }

            }
        });


        fragmentProfileBinding.captureimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        fragmentProfileBinding.changeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"changeNumber",Toast.LENGTH_LONG).show();
            }
        });fragmentProfileBinding.changeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                ChangeLannguageFragment redeemFragment = new ChangeLannguageFragment();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, redeemFragment).addToBackStack(null).commit();
            }
        });fragmentProfileBinding.changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"changePassword",Toast.LENGTH_LONG).show();
            }
        });fragmentProfileBinding.changeTextSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"changeTextSize",Toast.LENGTH_LONG).show();
            }
        });fragmentProfileBinding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                EditProfileFragment redeemFragment = new EditProfileFragment();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, redeemFragment).addToBackStack(null).commit();
            }
        });



        loadProfileData();
        loadImageFromStorage();


        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(getContext(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    //    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            fragmentProfileBinding.profilimage.setImageBitmap(photo);
//        }
//    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);


        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) intent.getExtras().get("data");
            saveprofile(photo);
            fragmentProfileBinding.profilimage.setImageBitmap(photo);
        }

    }

    private void saveprofile(Bitmap profileBitmap) {
        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir("profile", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File mypath = new File(directory, "myprofilepic.png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            profileBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            Log.e("SAVE_IMAGE", e.getMessage(), e);
        }
    }

    private void loadImageFromStorage() {

        try {
            ContextWrapper cw = new ContextWrapper(getContext());
            File path1 = cw.getDir("profile", Context.MODE_PRIVATE);
            File f = new File(path1, "myprofilepic.png");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            fragmentProfileBinding.profilimage.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            Log.e("SAVE_IMAGE", e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void loadProfileData() {
        profilePrefManager = new ProfilePrefManager(getContext());
        if (!profilePrefManager.isUserProfileEmpty()) {
            if (profilePrefManager != null) {
                if (!TextUtils.isEmpty(profilePrefManager.getName()))
                    fragmentProfileBinding.profileName.setText(profilePrefManager.getName());
                if (!TextUtils.isEmpty(profilePrefManager.getLanguage()))
                    fragmentProfileBinding.editChangeLanguage.setText(profilePrefManager.getLanguage());
                if (!TextUtils.isEmpty(profilePrefManager.getTextsize()))
                    fragmentProfileBinding.editChangeTextSize.setText(profilePrefManager.getTextsize());
                if (!TextUtils.isEmpty(profilePrefManager.getPhonenumber()))
                    fragmentProfileBinding.editChangeNumber.setText(profilePrefManager.getPhonenumber());
            } else {

            }
        }else {
            profilePrefManager.saveProfileDetails("HashedIn","+95 91234567890","English","Male","03-03-1993","Medium");
        }

    }

}