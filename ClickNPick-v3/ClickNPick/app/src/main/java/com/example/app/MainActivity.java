package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.app.customer.CustomerLoginActivity;
import com.example.app.customer.CustomerSignUpActivity;
import com.example.app.vendor.VendorLoginActivity;
import com.example.app.vendor.VendorSignUpActivity;
import com.example.utils.InputDialog;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mainLayout;
    private LinearLayout loginLayout;
    private LinearLayout signUpLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.err.println("Firebase token: " + FirebaseInstanceId.getInstance().getToken());
        System.err.println("Server IP: " + AppPreferences.getBaseURL(this));

        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.mainLayout);
        loginLayout = findViewById(R.id.loginLayout);
        signUpLayout = findViewById(R.id.signUpLayout);

        String vendorToken = AppPreferences.getVendorToken(this);
        String userToken = AppPreferences.getUserToken(this);
        if (vendorToken != null) {
            startActivity(new Intent(this, VendorLoginActivity.class));
        } else if (userToken != null) {
            startActivity(new Intent(this, CustomerLoginActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_server_ip) {
            changeServerIP();
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeServerIP() {
        new InputDialog(this, new InputDialog.Callback() {
            @Override
            public void onDone(String ipAddress) {
                System.err.println("Here: " + ipAddress);
                if (ipAddress == null) return;
                if (ipAddress.equals("")) {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    AppPreferences.setServerIP(MainActivity.this, ipAddress);
                }
            }
        }).setTitle("Change Server IP")
                .setHint("Server IP")
                .setText(AppPreferences.getServerIP(this))
                .show();
    }

    public void onClickSignUp(View view) {
        mainLayout.setVisibility(View.GONE);
        signUpLayout.setVisibility(View.VISIBLE);
    }

    public void onClickLogin(View view) {
        mainLayout.setVisibility(View.GONE);
        loginLayout.setVisibility(View.VISIBLE);
    }

    public void onClickVendorSignUp(View view) {
        Intent intent = new Intent(this, VendorSignUpActivity.class);
        startActivityForResult(intent, 1);
    }

    public void onClickCustomerSignUp(View view) {
        Intent intent = new Intent(this, CustomerSignUpActivity.class);
        startActivityForResult(intent, 2);
    }

    public void onClickVendorLogin(View view) {
        Intent intent = new Intent(this, VendorLoginActivity.class);
        startActivityForResult(intent, 3);
    }

    public void onClickCustomerLogin(View view) {
        Intent intent = new Intent(this, CustomerLoginActivity.class);
        startActivityForResult(intent, 4);
    }

    @Override
    public void onBackPressed() {
        if (loginLayout.getVisibility() == View.VISIBLE || signUpLayout.getVisibility() == View.VISIBLE) {
            loginLayout.setVisibility(View.GONE);
            signUpLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recreate();
    }

}
