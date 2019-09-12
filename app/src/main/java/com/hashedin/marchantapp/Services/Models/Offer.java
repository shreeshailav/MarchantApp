package com.hashedin.marchantapp.Services.Models;

import java.util.List;

public class Offer {

    public int id ;
    public String created_on ;
    public String updated_at ;
    public boolean is_active ;
    public boolean is_deleted ;
    public String name ;
    public String description ;
    public String t_and_c ;
    public String item ;
    public String image ;
    public String starts_at ;
    public String ends_at ;
    public int points ;
    public boolean is_city_mart ;
    public boolean accepts_wallet ;
    public String category ;
    public int coupons_issued ;
    public int max_coupons_allowed ;
    public boolean allow_coupons ;
    public int redeem_limit ;
    public boolean all_locations ;
    public int merchant ;
    public List<Integer> exempted_locations ;
}
