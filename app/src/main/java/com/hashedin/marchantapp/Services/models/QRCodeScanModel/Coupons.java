package com.hashedin.marchantapp.Services.models.QRCodeScanModel;


import java.io.Serializable;

public class Coupons implements Serializable {

    public int id ;
    public Offer offer ;
    public String created_on ;
    public String updated_at ;
    public int redeemed ;
    public String uuid ;
    public int user ;
    public int purchased_transaction ;
}
