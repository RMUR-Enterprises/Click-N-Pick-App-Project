package com.example.app.vendor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.app.AppPreferences;
import com.example.app.LoginActivity;
import com.example.app.models.MyResponse;

public class VendorLoginActivity extends LoginActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usernameField.setText("harjibakers");
        passwordField.setText("secret123");

        String vendorToken = AppPreferences.getVendorToken(this);
        if (vendorToken != null) {
            showProgress();
            onTokenReceive(vendorToken);
        }
    }

    @Override
    protected void onProfileReceive(MyResponse myResponse) {
        if (myResponse.vendor == 1 && myResponse.token != null) {
            AppPreferences.setVendorToken(this, myResponse.token, myResponse.id);
            Toast.makeText(this, "Login success!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, VendorActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show();
        }
    }

}
