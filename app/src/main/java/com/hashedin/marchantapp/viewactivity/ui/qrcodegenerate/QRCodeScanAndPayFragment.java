package com.hashedin.marchantapp.viewactivity.ui.qrcodegenerate;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.databinding.FragmentScanAndPayBinding;
import com.hashedin.marchantapp.viewactivity.LoginActivity;
import com.hashedin.marchantapp.viewactivity.Utility.DialogsUtils;
import com.hashedin.marchantapp.viewactivity.ui.qrcodescanner.RedeemFragment;

import static com.hashedin.marchantapp.viewactivity.ui.qrcodegenerate.QRCodeGenerateFragment.QRcodeWidth;

public class QRCodeScanAndPayFragment extends Fragment implements RedeemFragment.OnFragmentInteractionListener {


    private static final String TAG = LoginActivity.class.getSimpleName();

    String UUID = "5122af09-be6d-4310-9e92-05635a06b063";
    Bitmap bitmap;
    ProgressDialog myDialog;


    FragmentScanAndPayBinding fragmentScanAndPayBinding;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);


    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        fragmentScanAndPayBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_scan_and_pay, container, false);

        View root = fragmentScanAndPayBinding.getRoot();


        new loadqr().execute();


        fragmentScanAndPayBinding.qrScanPayImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                QRCodePaymentApproveFragment redeemFragment = new QRCodePaymentApproveFragment();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, redeemFragment).addToBackStack(null).commit();
            }
        });


        fragmentScanAndPayBinding.scanPayCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null)
                    getFragmentManager().popBackStack();
            }
        });

        fragmentScanAndPayBinding.backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null)
                    getFragmentManager().popBackStack();
            }
        });

        initialize();


        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }


    private void initialize() {

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


    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor) : getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);

        myDialog.dismiss();

        return bitmap;
    }


    public class loadqr extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(getContext(), "Loading please wait");

        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {


                bitmap = TextToImageEncode(UUID);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fragmentScanAndPayBinding.qrScanPayImageview.setImageBitmap(bitmap);
                    }
                });


            } catch (WriterException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (myDialog != null)
                myDialog.dismiss();
        }
    }

}