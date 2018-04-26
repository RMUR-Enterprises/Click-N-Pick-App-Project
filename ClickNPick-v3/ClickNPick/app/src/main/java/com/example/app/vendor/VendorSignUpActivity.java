package com.example.app.vendor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app.R;

public class VendorSignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_sign_up);
    }

    public void onClickSubmit(View view) {

        EditText locationField = findViewById(R.id.shopLocationField);
        if (locationField.getText().toString().equals("")) {
            Toast.makeText(this, "Enter a location!", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText numberField = findViewById(R.id.numberField);
        if (numberField.getText().toString().equals("")) {
            Toast.makeText(this, "Enter a number!", Toast.LENGTH_SHORT).show();
            return;
        }

        view.setVisibility(View.GONE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(VendorSignUpActivity.this,
                        "Our team will contact and locate you shortly" +
                                " to provide you a vendor ID and password.", Toast.LENGTH_LONG).show();
                finish();
            }
        }, 1000);
    }

}
