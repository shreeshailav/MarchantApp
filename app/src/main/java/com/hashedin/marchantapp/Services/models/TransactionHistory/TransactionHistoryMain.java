package com.hashedin.marchantapp.Services.models.TransactionHistory;

import java.io.Serializable;
import java.util.List;

public class TransactionHistoryMain implements Serializable
{
    public String next ;
    public String previous ;
    public int count ;
    public int pages ;
    public int per_page ;
    public List<Result> results ;



}