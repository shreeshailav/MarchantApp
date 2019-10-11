package com.hashedin.marchantapp.Services.Repository;

import com.hashedin.marchantapp.Services.models.QRCodeScanModel.Coupons;
import com.hashedin.marchantapp.Services.models.QRCodeGenerateModel.GenerateQR;
import com.hashedin.marchantapp.Services.models.RedeemCoupon.RedeemCoupon;
import com.hashedin.marchantapp.Services.models.TransacrionRequest.TransactionReq;
import com.hashedin.marchantapp.Services.models.TransactionHistory.TransactionHistoryMain;

public class ApiResponse {

    public Coupons coupons;
    public RedeemCoupon redeemCoupon;
    public TransactionHistoryMain transactionHistoryMain;
    public GenerateQR generateQR ;
    public TransactionReq transactionReq ;
    private Throwable error;
    public String errorMessage;

    public ApiResponse(Coupons coupons) {
        this.coupons = coupons;
        this.error = null;
    }

    public ApiResponse(RedeemCoupon redeemCoupon) {
        this.redeemCoupon = redeemCoupon;
        this.error = null;
        this.coupons = null;
    }


    public ApiResponse(GenerateQR generateQR) {
        this.generateQR = generateQR;
        this.error = null;
        this.coupons = null;
    }

    public ApiResponse(TransactionReq transactionReq) {
        this.transactionReq = transactionReq;
        this.error = null;
        this.coupons = null;
    }


    public ApiResponse(TransactionHistoryMain transactionHistoryMain) {
        this.transactionHistoryMain = transactionHistoryMain;
        this.error = null;
        this.coupons = null;
    }


    public ApiResponse(Throwable error) {
        this.error = error;
        this.coupons = null;
    }

    public ApiResponse(String errorMessage){
        this.errorMessage = errorMessage ;
        this.coupons = null ;
        this.error = null ;
    }

    public Coupons getPosts() {
        return coupons;
    }

    public void setPosts(Coupons posts) {
        this.coupons = posts;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}