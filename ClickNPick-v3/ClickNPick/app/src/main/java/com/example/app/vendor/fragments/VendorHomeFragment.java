package com.example.app.vendor.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.app.R;
import com.example.app.CategoriesActivity;
import com.example.app.vendor.VendorOrdersActivity;

public class VendorHomeFragment extends Fragment {

    public VendorHomeFragment() {
    }

    public static VendorHomeFragment newInstance() {
        return new VendorHomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vendor_home, container, false);

        Button receivedOrdersBtn = rootView.findViewById(R.id.receivedOrdersBtn);
        Button editItemsBtn = rootView.findViewById(R.id.editItemsBtn);

        receivedOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), VendorOrdersActivity.class));
            }
        });

        editItemsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), CategoriesActivity.class));
            }
        });

        return rootView;
    }

}
