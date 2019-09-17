package com.hashedin.marchantapp.Services.Models;

import java.lang.ref.SoftReference;

public class Credentials
{
    public String amount;
    public Meta meta;

    public Credentials(String a, String b){
        this.amount = a ;
        meta = new Meta();
        meta.reference =  b;
    }


}