package com.example.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.app.models.Category;

import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

    public static final String KEY_EXTRA_IS_CUSTOMER = "is_customer";

    private ListView listView;
    private boolean customer = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView = new ListView(this);
        listView.setPadding(20, 20, 20, 20);
        setContentView(listView);

        customer = getIntent().getBooleanExtra(KEY_EXTRA_IS_CUSTOMER, false);

        loadCategories();
        refresh();
    }

    private void loadCategories() {
        CategoriesAdapter adapter = new CategoriesAdapter(CategoriesActivity.this,
                R.layout.row_category, Data.CATEGORIES);
        listView.setAdapter(adapter);
    }

    private class CategoriesAdapter extends ArrayAdapter<Category> {

        CategoriesAdapter(@NonNull Context context, int resource, @NonNull List<Category> categories) {
            super(context, resource, categories);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View rootView = convertView;
            if (rootView == null) {
                rootView = LayoutInflater.from(getContext()).inflate(R.layout.row_category, parent, false);
            }

            final Category category = getItem(position);
            if (category == null) return rootView;

            ImageView imageView = rootView.findViewById(R.id.imageView);
            TextView textView = rootView.findViewById(R.id.textView);

            if (category.image != null) {
                Glide.with(imageView).load(Routes.getImagePicUrl(getContext(), category.image)).into(imageView);
            }

            textView.setText(category.name);

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ItemsActivity.class);
                    intent.putExtra(ItemsActivity.KEY_EXTRA_IS_CUSTOMER, customer);
                    intent.putExtra(ItemsActivity.KEY_EXTRA_CAT_ID, category.id);
                    startActivityForResult(intent, 1001);
                }
            });

            return rootView;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            refresh();
        }
    }

    private void refresh() {
        Data.reloadItems(this, customer, new Runnable() {
            @Override
            public void run() {
                loadCategories();
            }
        });
    }

}
