package com.hashedin.marchantapp.viewactivity.ui.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.databinding.FragmentProfileBinding;
import com.hashedin.marchantapp.viewactivity.LoginActivity;
import com.hashedin.marchantapp.viewactivity.MerchantMainActivity;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;
import com.hashedin.marchantapp.viewactivity.Utility.ProfilePrefManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding fragmentProfileBinding;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private ProfilePrefManager profilePrefManager;


    private Drawable small, medium, large, extralarge;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        MerchantMainActivity.currentFragment = "ProfileFragment";


        fragmentProfileBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_profile, container, false);

        View root = fragmentProfileBinding.getRoot();

        profilePrefManager = new ProfilePrefManager(getContext());


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
                Toast.makeText(getContext(), "changeNumber", Toast.LENGTH_LONG).show();
            }
        });
        fragmentProfileBinding.changeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                ChangeLannguageFragment redeemFragment = new ChangeLannguageFragment();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, redeemFragment).commit();
            }
        });
        fragmentProfileBinding.changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                ChangePasswordFragment redeemFragment = new ChangePasswordFragment();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, redeemFragment).commit();
            }
        });
        fragmentProfileBinding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                EditProfileFragment redeemFragment = new EditProfileFragment();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, redeemFragment).commit();
            }
        });

        fragmentProfileBinding.textsizeSmallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                smallButtonColor();
                // change_textsize_button_color();

                int themeID = R.style.FontSizeSmall;
                getActivity().setTheme(themeID);

                profilePrefManager.setTextsize("Small");

                reloadFragment();

            }
        });
        fragmentProfileBinding.textsizeExtralargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                extralargeButtonColor();

                //change_textsize_button_color();

                int themeID = R.style.FontSizeExtraLarge;
                getActivity().setTheme(themeID);

                profilePrefManager.setTextsize("ExtraLarge");


                reloadFragment();


            }
        });
        fragmentProfileBinding.textsizeLargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                largeButtonColor();

                int themeID = R.style.FontSizeLarge;
                getActivity().setTheme(themeID);

                profilePrefManager.setTextsize("Large");


                reloadFragment();


            }
        });
        fragmentProfileBinding.textsizeMediumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mediumButtonColor();


                int themeID = R.style.FontSizeMedium;
                getActivity().setTheme(themeID);

                profilePrefManager.setTextsize("Medium");


                reloadFragment();


            }
        });


        loadProfileData();
        loadImageFromStorage();


        change_textsize_button_color();


        if (!profilePrefManager.isUserProfileEmpty()) {
            String textsize = profilePrefManager.getTextsize();
            if (textsize.equalsIgnoreCase("Small"))
                smallButtonColor();
            else if (textsize.equalsIgnoreCase("Medium"))
                mediumButtonColor();
            else if (textsize.equalsIgnoreCase("Large"))
                largeButtonColor();
            else if (textsize.equalsIgnoreCase("ExtraLarge"))
                extralargeButtonColor();
        }


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
        } else {
            profilePrefManager.saveProfileDetails("HashedIn", "+95 91234567890", "English", "Male", "03-03-1993", "Medium");
        }


        String str = "";

    }


    private void change_textsize_button_color() {
        small = fragmentProfileBinding.textsizeSmallBtn.getBackground();
        small = DrawableCompat.wrap(small);
        //the color is a direct color int and not a color resource

        medium = fragmentProfileBinding.textsizeMediumBtn.getBackground();
        medium = DrawableCompat.wrap(medium);

        large = fragmentProfileBinding.textsizeLargeBtn.getBackground();
        large = DrawableCompat.wrap(large);

        extralarge = fragmentProfileBinding.textsizeExtralargeBtn.getBackground();
        extralarge = DrawableCompat.wrap(extralarge);


        DrawableCompat.setTint(small, getResources().getColor(R.color.profile_btn_color_1));
        DrawableCompat.setTint(medium, getResources().getColor(R.color.profile_btn_color_2));
        DrawableCompat.setTint(large, getResources().getColor(R.color.profile_btn_color_1));
        DrawableCompat.setTint(extralarge, getResources().getColor(R.color.profile_btn_color_1));

    }

    private void smallButtonColor() {
        DrawableCompat.setTint(small, getResources().getColor(R.color.profile_btn_color_2));
        DrawableCompat.setTint(medium, getResources().getColor(R.color.profile_btn_color_1));
        DrawableCompat.setTint(large, getResources().getColor(R.color.profile_btn_color_1));
        DrawableCompat.setTint(extralarge, getResources().getColor(R.color.profile_btn_color_1));
    }

    private void mediumButtonColor() {
        DrawableCompat.setTint(small, getResources().getColor(R.color.profile_btn_color_1));
        DrawableCompat.setTint(medium, getResources().getColor(R.color.profile_btn_color_2));
        DrawableCompat.setTint(large, getResources().getColor(R.color.profile_btn_color_1));
        DrawableCompat.setTint(extralarge, getResources().getColor(R.color.profile_btn_color_1));
    }

    private void largeButtonColor() {
        DrawableCompat.setTint(small, getResources().getColor(R.color.profile_btn_color_1));
        DrawableCompat.setTint(medium, getResources().getColor(R.color.profile_btn_color_1));
        DrawableCompat.setTint(large, getResources().getColor(R.color.profile_btn_color_2));
        DrawableCompat.setTint(extralarge, getResources().getColor(R.color.profile_btn_color_1));
    }

    private void extralargeButtonColor() {
        DrawableCompat.setTint(small, getResources().getColor(R.color.profile_btn_color_1));
        DrawableCompat.setTint(medium, getResources().getColor(R.color.profile_btn_color_1));
        DrawableCompat.setTint(large, getResources().getColor(R.color.profile_btn_color_1));
        DrawableCompat.setTint(extralarge, getResources().getColor(R.color.profile_btn_color_2));
    }

    private void reloadFragment() {
        // Reload current fragment
        if (getFragmentManager() != null) {
            Fragment frg = null;
            frg = this;
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(frg);
            ft.attach(frg);
            ft.commit();
        }
    }
}