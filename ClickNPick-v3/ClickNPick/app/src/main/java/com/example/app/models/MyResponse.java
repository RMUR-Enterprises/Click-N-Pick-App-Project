package com.example.app.models;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import okhttp3.Response;
import okhttp3.ResponseBody;

@SuppressWarnings({"WeakerAccess", "unused"})
public class MyResponse implements Serializable {

    public String message;

    public String status;
    public String token;

    public int id;
    public String location;
    public String name;
    public String profilePic;
    public String username;
    public String contact;
    public int vendor;

    public ArrayList<Category> categories;
    public ArrayList<Order> orders;
    public MyResponse profile;
    public ArrayList<MyResponse> vendors;

    @Override
    public String toString() {
        return name == null ? super.toString() : name;
    }

    public static MyResponse from(Response response) {
        System.err.println("Response: " + response);
        MyResponse myResponse = new MyResponse();

        if (response == null) {
            myResponse.message = "Null Response.";
            return myResponse;
        }

        final ResponseBody body = response.body();
        if (body == null) {
            myResponse.message = response.toString();
            return myResponse;
        }

        try {
            final String[] result = new String[1];
            final boolean[] running = {true};
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        result[0] = body.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                        result[0] = e.getMessage();
                    }
                    running[0] = false;
                }
            }).start();

            while (running[0]) {
                Thread.sleep(50);
            }
            String responseString = result[0];

            System.err.println("Response body: " + responseString);
            myResponse = new Gson().fromJson(responseString, MyResponse.class);

            if (myResponse.message == null) {
                myResponse.message = response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            myResponse.message = e.getMessage();
        }

        return myResponse;
    }

}
