package com.example.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.app.models.Item;
import com.example.app.vendor.ItemActivity;

import java.util.ArrayList;
import java.util.List;

public class ItemsActivity extends AppCompatActivity {

    public static final String KEY_EXTRA_IS_CUSTOMER = "is_customer";
    public static final String KEY_EXTRA_CAT_ID = "cat_id";

    private boolean customer = false;

    private int categoryId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        customer = getIntent().getBooleanExtra(KEY_EXTRA_IS_CUSTOMER, false);
        categoryId = getIntent().getIntExtra(KEY_EXTRA_CAT_ID, 0);
        if (categoryId == 0) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            new Exception().printStackTrace();
            finish();
            return;
        }

        setContentView(R.layout.activity_items);
        refresh();

        if (customer) {
            findViewById(R.id.addItemBtn).setVisibility(View.GONE);
        }
    }

    private void refresh() {
        ArrayList<Item> items = Data.getItems(categoryId);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(new ItemsAdapter(this, android.R.layout.simple_list_item_1, items));
    }

    public void onClickAddItem(View view) {
        Intent intent = new Intent(this, ItemActivity.class);
        intent.putExtra(ItemActivity.KEY_EXTRA_CAT_ID, categoryId);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Data.reloadItems(this, false, new Runnable() {
                @Override
                public void run() {
                    refresh();
                }
            });
        }
    }

    private class ItemsAdapter extends ArrayAdapter<Item> {

        ItemsAdapter(@NonNull Context context, int resource, @NonNull List<Item> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        @SuppressLint("SetTextI18n")
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
            text2.setText("Rs. " + item.price);

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (customer) {
                        new AlertDialog.Builder(getContext())
                                .setMessage("Do you want to add " + item.name + " to cart?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Cart.addItem(item);
                                        Toast.makeText(ItemsActivity.this, "Item added.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .show();

                    } else {
                        Intent intent = new Intent(v.getContext(), ItemActivity.class);
                        intent.putExtra(ItemActivity.KEY_EXTRA_CAT_ID, categoryId);
                        intent.putExtra(ItemActivity.KEY_EXTRA_ITEM, item);
                        startActivityForResult(intent, 1);
                    }
                }
            });

            return rootView;
        }
    }

}
