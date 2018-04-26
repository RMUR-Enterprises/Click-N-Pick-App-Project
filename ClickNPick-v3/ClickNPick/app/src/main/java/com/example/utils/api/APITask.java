package com.example.utils.api;

import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APITask extends AsyncTask<String, Void, Response> {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();
    private Call call;

    private String requestJSON;
    private File profilePicFile;
    private ResponseCallback callback;

    private String token;

    public APITask(String requestJSON, ResponseCallback callback) {
        this.requestJSON = requestJSON;
        this.callback = callback;
    }

    public APITask setFileToUpload(File file) {
        this.profilePicFile = file;
        return this;
    }

    public APITask setToken(String token) {
        this.token = token;
        return this;
    }

    @Override
    protected Response doInBackground(String... params) {
        try {
            String url = params[0];
            Request.Builder builder = new Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/json");

            if (requestJSON != null) {
                RequestBody body = RequestBody.create(JSON, requestJSON);
                builder.post(body);

            } else if (profilePicFile != null) {
                RequestBody formBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("profilePic", profilePicFile.getName(),
                                RequestBody.create(MediaType.parse("image/jpeg"), profilePicFile))
                        .addFormDataPart("other_field", "other_field_value")
                        .build();
                builder.post(formBody);
            }

            if (token != null) {
                builder.addHeader("Authorization", "Bearer " + token);
            }

            Request request = builder.build();
            call = client.newCall(request);
            return call.execute();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Response result) {
        callback.onResult(result);
    }

    public void destroy() {
        if (call != null) {
            call.cancel();
        }
    }

}
