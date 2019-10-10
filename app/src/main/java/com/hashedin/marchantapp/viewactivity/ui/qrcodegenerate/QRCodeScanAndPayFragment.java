package com.hashedin.marchantapp.viewactivity.ui.qrcodegenerate;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.Repository.ApiEndpoints;
import com.hashedin.marchantapp.Services.models.GenerateQR;
import com.hashedin.marchantapp.Services.models.QRCodeGenerateModel.QRGenModel;
import com.hashedin.marchantapp.databinding.FragmentScanAndPayBinding;
import com.hashedin.marchantapp.viewactivity.LoginActivity;
import com.hashedin.marchantapp.viewactivity.MerchantMainActivity;
import com.hashedin.marchantapp.viewactivity.Utility.DialogsUtils;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;
import com.hashedin.marchantapp.viewactivity.ui.qrcodescanner.RedeemFragment;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hashedin.marchantapp.Services.Repository.ApiEndpoints.HTTPS_API_MARCHENT_URL;
import static com.hashedin.marchantapp.viewactivity.ui.qrcodegenerate.QRCodeGenerateFragment.QRcodeWidth;

public class QRCodeScanAndPayFragment extends Fragment implements RedeemFragment.OnFragmentInteractionListener {


    private static final String TAG = LoginActivity.class.getSimpleName();

    String UUID = null; //"5122af09-be6d-4310-9e92-05635a06b063";
    Bitmap bitmap;
    ProgressDialog myDialog;

    Boolean isUnsubscribed = false;


    FragmentScanAndPayBinding fragmentScanAndPayBinding;

    Scheduler temp1;

    GenerateQR generateQR;

    Retrofit retrofit;
    ApiEndpoints apiService;
    Disposable disposable;

    String auth_token;

    Observable<QRGenModel> observable;


    private static CountDownTimer countDownTimer;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);


    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        MerchantMainActivity.currentFragment = "QRCodeScanAndPayFragment" ;



        fragmentScanAndPayBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_scan_and_pay, container, false);

        View root = fragmentScanAndPayBinding.getRoot();





        Bundle bundle = getArguments();
        if (bundle != null) {

            //couponcode = bundle.getString("couponcode");
            generateQR = (GenerateQR) bundle.getSerializable("generateQR");

            if (!TextUtils.isEmpty(generateQR.uuid)) {
                UUID = generateQR.uuid;
                new loadqr().execute();
                updateUI();
            }

        }


        fragmentScanAndPayBinding.qrScanPayImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FragmentManager fragmentManager = getFragmentManager();
//                QRCodePaymentApproveFragment redeemFragment = new QRCodePaymentApproveFragment();
//
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.nav_host_fragment, redeemFragment).commit();
            }
        });


        fragmentScanAndPayBinding.scanPayCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (getFragmentManager() != null)
//                    getFragmentManager().popBackStack();

                if(getActivity()!=null)
                    getActivity().onBackPressed();
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

        scanResponse();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    private void updateUI(){
        fragmentScanAndPayBinding.qrScanPayTransactionId.setText(generateQR.uuid);
        fragmentScanAndPayBinding.qrScanPayBillAmount.setText(""+generateQR.amount);
        fragmentScanAndPayBinding.qrScanPayGeneratedBy.setText(generateQR.reference);
    }


    private void initialize() {
        //If CountDownTimer is null then start timer
        if (countDownTimer == null) {

            int noOfMinutes = 5 * 60 * 1000;//Convert minutes into milliseconds
            startTimer(noOfMinutes);//start countdown

         }

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
    public void onDestroyView() {
        super.onDestroyView();

        isUnsubscribed = true;
        stopCountdown();
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

                if (getContext() != null) {

                    pixels[offset + x] = bitMatrix.get(x, y) ?
                            getResources().getColor(R.color.QRCodeBlackColor) : getResources().getColor(R.color.QRCodeWhiteColor);
                }
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

                if (getActivity() != null) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragmentScanAndPayBinding.qrScanPayImageview.setImageBitmap(bitmap);
                        }
                    });
                }


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

    @Override
    public void onDestroy() {
        super.onDestroy();


        disposable.dispose();


    }

    private void scanResponse() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        retrofit = new Retrofit.Builder()
                .baseUrl(HTTPS_API_MARCHENT_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(ApiEndpoints.class);


        disposable = Observable.interval(1000, 5000,
                TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::callJokesEndpoint, this::onError);


    }

    private void callJokesEndpoint(Long aLong) {

        PrefManager prefManager = new PrefManager(getContext());

        auth_token = "token " + "f2ddfb343b5793325ad74c841dfc7e3f4990f693";//prefManager.getKey();


        if (!isUnsubscribed) {


            observable = apiService.getTransactionDetail(UUID, auth_token);

            temp1 = Schedulers.newThread();

            disposable = observable.subscribeOn(temp1).
                    observeOn(AndroidSchedulers.mainThread())
                    .map(result -> result)
                    .subscribe(this::handleResults, this::handleError);

        }


    }


    private void onError(Throwable throwable) {
        //Toast.makeText(getContext(), "OnError in Observable Timer", Toast.LENGTH_LONG).show();
    }

    private void handleResults(QRGenModel qrGenModel) {


        if (qrGenModel != null) {

            isUnsubscribed = true;
            stopCountdown();


            String rest = String.valueOf(qrGenModel.status);

            Bundle bundle = new Bundle();
           // bundle.putString("qrGenModel",qrGenModel);
            bundle.putSerializable("UUID", UUID);
            FragmentManager fragmentManager = getFragmentManager();
            QRCodePaymentApproveFragment redeemFragment = new QRCodePaymentApproveFragment();
            redeemFragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, redeemFragment).commit();

            //textView.setText(joke);


        } else {
//            Toast.makeText(getContext(), "NO RESULTS FOUND",
//                    Toast.LENGTH_LONG).show();
        }
    }

    private void handleError(Throwable t) {

        //Add your error here.
//        Toast.makeText(getContext(), "NO RESULTS FOUND",
//                Toast.LENGTH_LONG).show();

        observable.unsubscribeOn(temp1);
        if (temp1 != null) {
            temp1.shutdown();
        }


    }


    //Stop Countdown method
    private void stopCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    //Start Countodwn method
    private void startTimer(int noOfMinutes) {
        countDownTimer = new CountDownTimer(noOfMinutes, 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                //Convert milliseconds into hour,minute and seconds
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                fragmentScanAndPayBinding.timerText.setText(hms);//set text
            }

            public void onFinish() {

                fragmentScanAndPayBinding.timerText.setText("TIME'S UP!!"); //On finish change timer text
                countDownTimer = null;//set CountDownTimer to null


                if (getFragmentManager() != null)
                    getFragmentManager().popBackStack();

            }
        }.start();

    }


}