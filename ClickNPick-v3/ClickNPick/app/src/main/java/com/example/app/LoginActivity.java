package com.example.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.app.models.MyResponse;
import com.example.utils.api.APITask;
import com.example.utils.api.ResponseCallback;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

public abstract class LoginActivity extends AppCompatActivity {

    protected EditText usernameField, passwordField;
    protected Button loginButton;
    protected ProgressBar progressBar;

    private APITask loginTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
    }

    protected void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.GONE);
    }

    protected void hideProgress() {
        progressBar.setVisibility(View.GONE);
        loginButton.setVisibility(View.VISIBLE);
    }

    public void onClickLogin(View view) {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        if (username.equals("")) {
            Toast.makeText(this, "Enter your username!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.equals("")) {
            Toast.makeText(this, "Enter your password!", Toast.LENGTH_SHORT).show();
            return;
        }

        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        if (fcmToken == null) {
            Toast.makeText(this, "Not registered with firebase," +
                    " please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress();
        doLogin(username, password, fcmToken);
    }

    public void doLogin(String username, String password, String fcmToken) {

        JSONObject loginRequest = new JSONObject();
        try {
            loginRequest.put("identifier", username);
            loginRequest.put("password", password);
            loginRequest.put("fcmToken", fcmToken);
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }

        loginTask = new APITask(loginRequest.toString(), new ResponseCallback() {
            @Override
            public void onResult(Response response) {
                MyResponse myResponse = MyResponse.from(response);
                if (myResponse.token == null) {
                    String message = myResponse.message;
                    if (message == null) message = "Login failed!";
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    hideProgress();
                } else {
                    onTokenReceive(myResponse.token);
                }
            }
        });

        loginTask.execute(Routes.getLoginUrl(this));
    }

    protected void onTokenReceive(final String token) {
        loginTask = new APITask(null, new ResponseCallback() {
            @Override
            public void onResult(Response response) {
                hideProgress();
                if (response == null) return;
                MyResponse myResponse = MyResponse.from(response);
                if (response.isSuccessful()) {
                    myResponse.token = token;
                    onProfileReceive(myResponse);
                } else {
                    Toast.makeText(LoginActivity.this, myResponse.message,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        loginTask.setToken(token).execute(Routes.getProfileUrl(this));
    }

    protected abstract void onProfileReceive(MyResponse myResponse);

    @Override
    protected void onDestroy() {
        if (loginTask != null) {
            loginTask.destroy();
        }
        super.onDestroy();
    }

}
