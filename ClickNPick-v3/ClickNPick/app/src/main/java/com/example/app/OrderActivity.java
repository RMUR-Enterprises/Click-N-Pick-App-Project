package com.example.app;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.models.MyResponse;
import com.example.app.models.Order;
import com.example.utils.AppUtils;
import com.example.utils.InputDialog;
import com.example.utils.api.APITask;
import com.example.utils.api.ResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import okhttp3.Response;

public class OrderActivity extends AppCompatActivity {

    public static final String KEY_EXTRA_ORDER = "order";
    public static final String KEY_EXTRA_IS_CUSTOMER = "is_customer";

    private Order order;

    Button acceptBtn, rejectBtn, cancelBtn, reOrderBtn;
    TextView idText, pickupTimeText, statusText, userIdText, itemsText, reasonText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isCustomer = getIntent().getBooleanExtra(KEY_EXTRA_IS_CUSTOMER, false);
        if (getIntent().hasExtra(KEY_EXTRA_ORDER)) {
            order = (Order) getIntent().getSerializableExtra(KEY_EXTRA_ORDER);
        } else {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_order);

        acceptBtn = findViewById(R.id.acceptBtn);
        rejectBtn = findViewById(R.id.rejectBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        reOrderBtn = findViewById(R.id.reOrderBtn);

        if (isCustomer) {
            acceptBtn.setVisibility(View.GONE);
            rejectBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.VISIBLE);
            reOrderBtn.setVisibility(View.VISIBLE);
        } else {
            acceptBtn.setVisibility(View.VISIBLE);
            rejectBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.GONE);
            reOrderBtn.setVisibility(View.GONE);
        }

        idText = findViewById(R.id.idText);
        pickupTimeText = findViewById(R.id.pickupTimeText);
        statusText = findViewById(R.id.statusText);
        userIdText = findViewById(R.id.userIdText);
        itemsText = findViewById(R.id.itemsText);
        reasonText = findViewById(R.id.reasonText);

        if (!order.getStatus().toLowerCase().equals("pending")) {
            acceptBtn.setEnabled(false);
            rejectBtn.setEnabled(false);
        }

        if (order.getStatus().toLowerCase().equals("canceled")) {
            cancelBtn.setEnabled(false);
        }

        String reason = order.getReason();
        if (reason == null) {
            reasonText.setVisibility(View.GONE);
        } else {
            reasonText.setText(String.format(Locale.US, getString(R.string.reason_s), reason));
        }

        idText.setText(String.format(getString(R.string.order_id_d), order.id));
        pickupTimeText.setText(String.format(getString(R.string.pickup_time_s),
                AppUtils.getDate(order.pickUpTime)));
        statusText.setText(String.format(getString(R.string.status_s), order.getStatus()));
        userIdText.setText(String.format(getString(R.string.user_id_d), order.userId));

        itemsText.setText(Data.getItemNamesString(order.item_ids));
    }

    public void onClickAccept(View view) {
        acceptOrReject("Accepting order..", null);
    }

    public void onClickReject(View view) {
        new InputDialog(this, new InputDialog.Callback() {
            @Override
            public void onDone(String string) {
                if (string != null) {
                    acceptOrReject("Rejecting order..", string);
                }
            }
        }).setTitle("Enter reason for rejection")
                .show();
    }

    private void acceptOrReject(String message, String reason) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
        progressDialog.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("accept", reason == null ? 1 : 0);
            jsonObject.put("reason", reason);
        } catch (JSONException ignored) {
        }

        APITask apiTask = new APITask(jsonObject.toString(), new ResponseCallback() {
            @Override
            public void onResult(Response response) {
                progressDialog.dismiss();
                if (response == null) return;
                MyResponse myResponse = MyResponse.from(response);
                Toast.makeText(OrderActivity.this, myResponse.message, Toast.LENGTH_SHORT).show();
                if (response.isSuccessful()) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        apiTask.setToken(AppPreferences.getVendorToken(this))
                .execute(Routes.getOrdersAcceptUrl(this, order.id));

    }

    public void onClickCancel(View view) {
        new InputDialog(this, new InputDialog.Callback() {
            @Override
            public void onDone(String string) {
                if (string != null) {
                    cancelOrComplete("Canceling order..", string);
                }
            }
        }).setTitle("Enter reason for cancellation")
                .show();
    }

    private void cancelOrComplete(String message, String reason) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
        progressDialog.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("complete", reason == null ? 1 : 0);
            jsonObject.put("reason", reason);
        } catch (JSONException ignored) {
        }

        APITask apiTask = new APITask(jsonObject.toString(), new ResponseCallback() {
            @Override
            public void onResult(Response response) {
                progressDialog.dismiss();
                if (response == null) return;
                MyResponse myResponse = MyResponse.from(response);
                Toast.makeText(OrderActivity.this, myResponse.message, Toast.LENGTH_SHORT).show();
                if (response.isSuccessful()) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        apiTask.setToken(AppPreferences.getUserToken(this))
                .execute(Routes.getOrdersCompleteUrl(this, order.id));
    }

    public void onClickReOrder(View view) {
        Data.reOrder(order);
        Toast.makeText(this, "Items of this order has been added to cart.",
                Toast.LENGTH_SHORT).show();
    }

}
