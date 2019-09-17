package com.hashedin.marchantapp.Services.Models;

import java.util.List;

public class Client
{
    public int id ;
    public String name ;
    public String client_type ;
    public String client_id ;
    public String client_secret ;
    public String jwt_alg ;
    public String date_created ;
    public String website_url ;
    public String terms_url ;
    public String contact_email ;
    public String logo ;
    public Boolean reuse_consent ;
    public Boolean require_consent ;
    public String _redirect_uris ;
    public String _post_logout_redirect_uris ;
    public String _scope ;
    public String owner ;
    public List<Integer> response_types ;
}