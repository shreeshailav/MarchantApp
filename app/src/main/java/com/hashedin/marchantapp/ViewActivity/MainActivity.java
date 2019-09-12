package com.hashedin.marchantapp.ViewActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.hashedin.marchantapp.R;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        //String jws = Jwts.builder().setSubject("Joe").signWith(key).compact();

        long nowMillis = System.currentTimeMillis()+300000;

        Date Expdate = new Date(nowMillis);

        String jws1 = Jwts.builder()
                .setAudience("cr_backend")
                .setIssuer("cr_app")
                .setExpiration(Expdate)
                .signWith(
                        SignatureAlgorithm.HS256,
                        "6f621311be513e1760664fe61edef239499a1b9a61672452d15b5ac01641b772"
                ).compact();


        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
