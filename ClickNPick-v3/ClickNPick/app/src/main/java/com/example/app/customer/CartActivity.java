package com.example.app.customer;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.app.AppPreferences;
import com.example.app.Cart;
import com.example.app.R;
import com.example.app.Routes;
import com.example.app.models.Item;
import com.example.app.models.MyResponse;
import com.example.utils.api.APITask;
import com.example.utils.api.ResponseCallback;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Response;

public class CartActivity extends AppCompatActivity {

    ListView cartItemsList;
    TextView pickupTimeText, totalPriceText;
    private long pickupTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartItemsList = findViewById(R.id.cartItemsListView);
        pickupTimeText = findViewById(R.id.pickupTimeText);
        pickupTimeText.setText(String.format(Locale.US, getString(R.string.pickup_time_s), "None"));
        totalPriceText = findViewById(R.id.totalPriceText);

        refresh();
    }

    private void refresh() {
        totalPriceText.setText(String.format(Locale.US, getString(R.string.total_price_d),
                Cart.getTotalPrice()));

        ArrayAdapter adapter = new CartItemsAdapter(this, Cart.getItems());
        cartItemsList.setAdapter(adapter);
    }

    public void onClickCheckout(View view) {
        if (Cart.getItems().size() == 0) {
            Toast.makeText(this, "No items in the cart!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pickupTime == 0) {
            Toast.makeText(this, "Choose a pickup time!", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting order..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONObject jsonObject = new JSONObject();
        try {
            Gson gson = new Gson();
            String itemsJson = gson.toJson(Cart.getItems());
            JSONArray jsonArray = new JSONArray(itemsJson);

            jsonObject.put("items", jsonArray);
            jsonObject.put("pickUpTime", pickupTime / 1000L);
        } catch (JSONException ignored) {
        }

        APITask apiTask = new APITask(jsonObject.toString(), new ResponseCallback() {
            @Override
            public void onResult(Response response) {
                progressDialog.dismiss();
                if (response == null) return;
                MyResponse myResponse = MyResponse.from(response);
                Toast.makeText(CartActivity.this, myResponse.message, Toast.LENGTH_SHORT).show();
                if (response.isSuccessful()) {
                    Cart.getItems().clear();
                    refresh();
                }
            }
        });

        apiTask.setToken(AppPreferences.getUserToken(this))
                .execute(Routes.getOrdersUrl(this));
    }

    public void onClickChangeTime(View view) {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendar.set(Calendar.MINUTE, minute);

                if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
                    calendar.add(Calendar.HOUR, 24);
                }

                if (selectedHour < 10 || (selectedHour > 21 && minute > 0)) {
                    Toast.makeText(CartActivity.this, "Time should be between 10 AM to 10 PM.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    pickupTime = calendar.getTimeInMillis();
                    String timeStr = calendar.getTime().toString();
                    pickupTimeText.setText(String.format(Locale.US, getString(R.string.pickup_time_s), timeStr));
                }
            }
        };

        Calendar calendar = Calendar.getInstance();
        if (pickupTime != 0) {
            calendar.setTimeInMillis(pickupTime);
        } else {
            calendar.add(Calendar.HOUR, 1);
        }
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this, listener, hourOfDay, minute, true);
        dialog.show();
    }

    private class CartItemsAdapter extends ArrayAdapter<Item> {

        CartItemsAdapter(@NonNull Context context, @NonNull List<Item> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View rootView = convertView;
            if (rootView == null) {
                rootView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2,
                        parent, false);
            }

            final Item item = getItem(position);
            if (item == null) return rootView;

            TextView text1 = rootView.findViewById(android.R.id.text1);
            TextView text2 = rootView.findViewById(android.R.id.text2);

            text1.setText(item.name);
            text2.setText(String.valueOf(item.price));

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setMessage("Do you want to remove a " + item.name + " from the cart?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Cart.removeItem(item.id)) {
                                        Toast.makeText(CartActivity.this, "Removed.", Toast.LENGTH_SHORT).show();
                                        refresh();
                                    }
                                }
                            })
                            .show();
                }
            });

            return rootView;
        }
    }

}
