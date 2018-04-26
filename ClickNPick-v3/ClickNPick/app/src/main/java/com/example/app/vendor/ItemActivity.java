package com.example.app.vendor;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.AppPreferences;
import com.example.app.R;
import com.example.app.Routes;
import com.example.app.models.Item;
import com.example.app.models.MyResponse;
import com.example.utils.api.APITask;
import com.example.utils.api.ResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import okhttp3.Response;

public class ItemActivity extends AppCompatActivity {

    public static final String KEY_EXTRA_ITEM = "item";
    public static final String KEY_EXTRA_CAT_ID = "category_id";

    private Item item;
    private int categoryId;

    private EditText nameField, priceField;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryId = getIntent().getIntExtra(KEY_EXTRA_CAT_ID, 0);
        if (categoryId == 0) {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            new Exception().printStackTrace();
            finish();
            return;
        }

        setTitle("Add Item");
        setContentView(R.layout.activity_item);

        Button addOrModifyBtn = findViewById(R.id.addOrModifyBtn);
        nameField = findViewById(R.id.nameField);
        priceField = findViewById(R.id.priceField);
        TextView itemIdText = findViewById(R.id.itemIdText);

        item = (Item) getIntent().getSerializableExtra(KEY_EXTRA_ITEM);
        if (item != null) {
            setTitle("Modify Item");
            addOrModifyBtn.setText(R.string.modify);
            nameField.setText(item.name);

            nameField.setFocusable(false);
            nameField.setClickable(false);

            priceField.setText(String.valueOf(item.price));
            itemIdText.setText(String.format(Locale.US, getString(R.string.item_id_d), item.id));

            ViewGroup parent = (ViewGroup) addOrModifyBtn.getParent();
            Button deleteButton = new Button(this);
            deleteButton.setText(R.string.delete);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;

            deleteButton.setLayoutParams(params);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItemImpl();
                }
            });
            parent.addView(deleteButton);
        } else {
            itemIdText.setVisibility(View.GONE);
        }
    }

    public void onClickAddOrModifyItem(View view) {
        String name = nameField.getText().toString().trim();
        String priceText = priceField.getText().toString().trim();
        if (name.equals("")) {
            nameField.setError("Enter a name!");
            return;
        } else if (priceText.equals("")) {
            priceField.setError("Enter a price!");
            return;
        }

        try {
            int price = Integer.parseInt(priceText);
            addOrUpdateImpl(name, price, item != null);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addOrUpdateImpl(String name, int price, boolean update) throws JSONException {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(update ? "Updating item.." : "Adding Item..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("price", price);
        jsonObject.put("categoryId", categoryId);

        APITask task = new APITask(jsonObject.toString(), new ResponseCallback() {
            @Override
            public void onResult(Response response) {
                progressDialog.dismiss();
                if (response == null) return;
                MyResponse myResponse = MyResponse.from(response);
                Toast.makeText(ItemActivity.this, myResponse.message, Toast.LENGTH_SHORT).show();
                if (response.isSuccessful()) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
        String url = Routes.getCategoriesUrl(this); // categories url : /items
        if (update) {
            url = Routes.getItemUpdateUrl(this, item.id);
        }
        task.setToken(AppPreferences.getVendorToken(this))
                .execute(url);
    }

    private void deleteItemImpl() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Deleting Item..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        APITask apiTask = new APITask("", new ResponseCallback() {
            @Override
            public void onResult(Response response) {
                progressDialog.dismiss();
                if (response == null) return;
                MyResponse myResponse = MyResponse.from(response);
                Toast.makeText(ItemActivity.this, myResponse.message, Toast.LENGTH_SHORT).show();
                if (response.isSuccessful()) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
        apiTask.setToken(AppPreferences.getVendorToken(this))
                .execute(Routes.getItemDeleteUrl(this, item.id));
    }

}
