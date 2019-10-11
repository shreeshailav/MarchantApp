package com.hashedin.marchantapp.Services.models.TransactionHistory;

import java.util.List;

public class Offer
{
    public int id ;
    public Merchant merchant ;
    public String created_on ;
    public String updated_at ;
    public Boolean is_active ;
    public Boolean is_deleted ;
    public String name ;
    public String description ;
    public String t_and_c ;
    public String item ;
    public String image ;
    public String starts_at ;
    public String ends_at ;
    public int points ;
    public Boolean is_city_mart ;
    public Boolean accepts_wallet ;
    public String category ;
    public int coupons_issued ;
    public int max_coupons_allowed ;
    public Boolean allow_coupons ;
    public int redeem_limit ;
    public Boolean all_locations ;
    public List<Integer> exempted_locations ;
}