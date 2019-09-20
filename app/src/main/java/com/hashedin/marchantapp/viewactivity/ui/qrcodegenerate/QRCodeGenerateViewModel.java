package com.hashedin.marchantapp.viewactivity.ui.qrcodegenerate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QRCodeGenerateViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public QRCodeGenerateViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is QR Code fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}