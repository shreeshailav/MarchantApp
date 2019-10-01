package com.hashedin.marchantapp.viewactivity.ui.qrcodegenerate;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.databinding.ActivityQrcodeGeneratorBinding;

public class QRCodeGenerateFragment extends Fragment {

    private QRCodeGenerateViewModel qrCodeGenerateViewModel;

    ImageView imageView;
    Button button;
    EditText editText, editunit;
    String EditTextValue;
    Thread thread;
    public final static int QRcodeWidth = 500;
    Bitmap bitmap;
    ActivityQrcodeGeneratorBinding activityQrcodeGeneratorBinding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        qrCodeGenerateViewModel =
                ViewModelProviders.of(this).get(QRCodeGenerateViewModel.class);


        activityQrcodeGeneratorBinding = DataBindingUtil.inflate(
                inflater, R.layout.activity_qrcode_generator, container, false);

        View root = activityQrcodeGeneratorBinding.getRoot();

         //final TextView textView = root.findViewById(R.id.text_notifications);
        qrCodeGenerateViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });


        activityQrcodeGeneratorBinding.backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null)
                    getFragmentManager().popBackStack();




            }
        });

        activityQrcodeGeneratorBinding.editTransactionId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (TextUtils.isEmpty(activityQrcodeGeneratorBinding.editTransactionId.getText().toString()) || TextUtils.isEmpty(activityQrcodeGeneratorBinding.editTransactionAmount.getText().toString())) {
                    activityQrcodeGeneratorBinding.generateQrCodeSubmit.setAlpha(.5f);
                    activityQrcodeGeneratorBinding.generateQrCodeSubmit.setEnabled(false);
                } else if (!TextUtils.isEmpty(activityQrcodeGeneratorBinding.editTransactionId.getText().toString()) && !TextUtils.isEmpty(activityQrcodeGeneratorBinding.editTransactionAmount.getText().toString())) {

                    activityQrcodeGeneratorBinding.generateQrCodeSubmit.setAlpha(1f);
                    activityQrcodeGeneratorBinding.generateQrCodeSubmit.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        activityQrcodeGeneratorBinding.editTransactionAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (TextUtils.isEmpty(activityQrcodeGeneratorBinding.editTransactionId.getText().toString()) || TextUtils.isEmpty(activityQrcodeGeneratorBinding.editTransactionAmount.getText().toString())) {
                    activityQrcodeGeneratorBinding.generateQrCodeSubmit.setAlpha(.5f);
                    activityQrcodeGeneratorBinding.generateQrCodeSubmit.setEnabled(false);
                } else if (!TextUtils.isEmpty(activityQrcodeGeneratorBinding.editTransactionId.getText().toString()) && !TextUtils.isEmpty(activityQrcodeGeneratorBinding.editTransactionAmount.getText().toString())) {

                    activityQrcodeGeneratorBinding.generateQrCodeSubmit.setAlpha(1f);
                    activityQrcodeGeneratorBinding.generateQrCodeSubmit.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        activityQrcodeGeneratorBinding.generateQrCodeSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(),"Coming soon",Toast.LENGTH_SHORT).show();

                FragmentManager fragmentManager = getFragmentManager();
                QRCodeScanAndPayFragment redeemFragment = new QRCodeScanAndPayFragment();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace( R.id.nav_host_fragment, redeemFragment ).addToBackStack( null ).commit();

            }
        });


//
//        imageView = (ImageView)root.findViewById(R.id.qrimage);
//        editText = (EditText)root.findViewById(R.id.edit_amount);
//        editunit = (EditText)root.findViewById(R.id.edit_unit);
//        button = (Button)root.findViewById(R.id.genqrbtn);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                EditTextValue = editText.getText().toString();
//
//                try {
//                    bitmap = TextToImageEncode(EditTextValue);
//
//                    imageView.setImageBitmap(bitmap);
//
//                } catch (WriterException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });


        return root;

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
        return bitmap;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}