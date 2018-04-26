package com.example.app.vendor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.app.AppPreferences;
import com.example.app.Data;
import com.example.app.OrderActivity;
import com.example.app.OrdersAdapter;
import com.example.app.Routes;
import com.example.app.models.MyResponse;
import com.example.app.models.Order;
import com.example.utils.api.APITask;
import com.example.utils.api.ResponseCallback;

import java.util.ArrayList;

import okhttp3.Response;

public class VendorOrdersActivity extends AppCompatActivity {

    public static final String KEY_EXTRA_NOTIFCATION_ONLY = "notification_only";

    private ListView listView;
    private APITask apiTask;

    private boolean notificationsOnly;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView = new ListView(this);
        listView.setPadding(20, 20, 20, 20);

        notificationsOnly = getIntent().getBooleanExtra(KEY_EXTRA_NOTIFCATION_ONLY, false);
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        setContentView(progressBar);
        Data.reloadItems(this, false, new Runnable() {
            @Override
            public void run() {
                loadOrders();
            }
        });
    }

    @Override
    protected void onDestroy() {
        apiTask.destroy();
        super.onDestroy();
    }

    private ArrayList<Order> filterOrders(ArrayList<Order> orders) {
        ArrayList<Order> filtered = new ArrayList<>();
        for (Order order : orders) {
            if (order.accepted == 2 || order.canceled == 1) {
                filtered.add(order);
            }
        }
        return filtered;
    }

    private void loadOrders() {
        apiTask = new APITask(null, new ResponseCallback() {
            @Override
            public void onResult(Response response) {
                MyResponse myResponse = MyResponse.from(response);
                if (myResponse.orders == null) {
                    Toast.makeText(VendorOrdersActivity.this, myResponse.message, Toast.LENGTH_SHORT).show();
                    finish();
                } else if (myResponse.orders.size() == 0) {
                    Toast.makeText(VendorOrdersActivity.this, "No orders!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    setContentView(listView);
                    ArrayList<Order> orders = notificationsOnly ? filterOrders(myResponse.orders) : myResponse.orders;
                    final ArrayAdapter adapter = new OrdersAdapter(VendorOrdersActivity.this, orders);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Order order = (Order) adapter.getItem(position);
                            if (order == null) return;

                            Intent intent = new Intent(VendorOrdersActivity.this, OrderActivity.class);
                            intent.putExtra(OrderActivity.KEY_EXTRA_ORDER, order);
                            startActivityForResult(intent, 1001);
                        }
                    });
                }
            }
        });
        apiTask.setToken(AppPreferences.getVendorToken(this))
                .execute(Routes.getOrdersUrl(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            recreate();
        }
    }

}
