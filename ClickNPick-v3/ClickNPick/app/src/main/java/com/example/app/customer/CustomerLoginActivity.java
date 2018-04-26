package com.example.app.customer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.app.AppPreferences;
import com.example.app.LoginActivity;
import com.example.app.models.MyResponse;

public class CustomerLoginActivity extends LoginActivity {

    private MyResponse profile;

    @Override
    @SuppressLint("SetTextI18n")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usernameField.setText("testuser");
        passwordField.setText("secret123");

        String userToken = AppPreferences.getUserToken(this);
        if (userToken != null) {
            showProgress();
            onTokenReceive(userToken);
        }
    }

    @Override
    protected void onProfileReceive(MyResponse myResponse) {
        if (myResponse.vendor == 0) {
            this.profile = myResponse;
            AppPreferences.setUserToken(this, myResponse.token, myResponse.id);
            Toast.makeText(this, "Login success!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, CustomerActivity.class));
        } else {
            Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show();
        }
    }

}
