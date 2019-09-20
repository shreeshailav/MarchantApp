package com.hashedin.marchantapp.viewactivity.ui.qrcodescanner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QRCodeScannerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public QRCodeScannerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}