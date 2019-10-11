package com.hashedin.marchantapp.Services.models.QRCodeGenerateModel;

import java.io.Serializable;

public class QRGenModel implements Serializable
{
    public int id ;
    public String created_time ;
    public String updated_time ;
    public PayedByObject payed_by_object ;
    public PayedToObject payed_to_object ;
    public String created_on ;
    public String updated_at ;
    public String uuid ;
    public int payer_object_id ;
    public int receiver_object_id ;
    public int authorized_object_id ;
    public String amount ;
    public int unit ;
    public String type ;
    public int mode ;
    public String item ;

    public int status ;
    public Meta meta ;

    public int payer_content_type ;
    public int receiver_content_type ;
    public int authorized_by_content_type ;
    public String coupon ;
}