package com.example.app.customer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.Routes;
import com.example.app.models.MyResponse;
import com.example.utils.api.APITask;
import com.example.utils.api.ResponseCallback;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import okhttp3.Response;

public class CustomerSignUpActivity extends AppCompatActivity {

    private EditText nameField, usernameField, numberField, passwordField;
    private ProgressBar progressBar;

    private APITask signUpTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_sign_up);

        nameField = findViewById(R.id.nameField);
        usernameField = findViewById(R.id.usernameField);
        numberField = findViewById(R.id.numberField);
        passwordField = findViewById(R.id.passwordField);
        progressBar = findViewById(R.id.progressBar);
    }

    public void onClickSubmit(final View view) {
        String name = nameField.getText().toString().trim();
        String username = usernameField.getText().toString().trim();
        String number = numberField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (name.equals("")) {
            Toast.makeText(this, "Enter your name!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.equals("")) {
            Toast.makeText(this, "Enter your username!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.length() < 6) {
            Toast.makeText(this, "Username must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (number.equals("")) {
            Toast.makeText(this, "Enter your number!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (number.length() < 6) {
            Toast.makeText(this, "Number must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.equals("")) {
            Toast.makeText(this, "Enter your password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 4) {
            Toast.makeText(this, "Password must be at least 4 characters long!", Toast.LENGTH_SHORT).show();
            return;
        }

        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        if (fcmToken == null) {
            Toast.makeText(this, "Not registered with firebase," +
                    " please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        view.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        UserSignUpRequest request = new UserSignUpRequest(name, username, number, password);
        request.fcmToken = fcmToken;
        String requestJson = new Gson().toJson(request);

        signUpTask = new APITask(requestJson, new ResponseCallback() {
            @Override
            public void onResult(Response response) {
                view.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                System.err.println("Response: " + String.valueOf(response));

                if (response == null) {
                    Toast.makeText(CustomerSignUpActivity.this, "Unable to connect!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.isSuccessful()) {
                    Toast.makeText(CustomerSignUpActivity.this,
                            "Account created. Use username and password to login",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    MyResponse myResponse = MyResponse.from(response);
                    Toast.makeText(CustomerSignUpActivity.this, myResponse.message,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        signUpTask.execute(Routes.getCustomerSignUpUrl(this));
    }

    @SuppressWarnings("unused")
    private static class UserSignUpRequest {
        private String name;
        private String username;
        private String contact;
        private String password;
        private String fcmToken;

        UserSignUpRequest(String name, String username, String number, String password) {
            this.name = name;
            this.username = username;
            this.contact = number;
            this.password = password;
        }
    }

    @Override
    protected void onDestroy() {
        if (signUpTask != null) {
            signUpTask.destroy();
        }
        super.onDestroy();
    }

}
