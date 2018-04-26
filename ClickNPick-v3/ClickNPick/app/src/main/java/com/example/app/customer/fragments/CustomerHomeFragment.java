package com.example.app.customer.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.app.AppPreferences;
import com.example.app.CategoriesActivity;
import com.example.app.R;
import com.example.app.Routes;
import com.example.app.models.MyResponse;
import com.example.utils.api.APITask;
import com.example.utils.api.ResponseCallback;

import okhttp3.Response;

public class CustomerHomeFragment extends Fragment {

    public CustomerHomeFragment() {
    }

    public static CustomerHomeFragment newInstance() {
        return new CustomerHomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_home, container, false);

        final ListView vendorsList = rootView.findViewById(R.id.vendorsListView);
        vendorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getContext(), CategoriesActivity.class)
                        .putExtra(CategoriesActivity.KEY_EXTRA_IS_CUSTOMER, true));
            }
        });

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading vendors..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        APITask apiTask = new APITask(null, new ResponseCallback() {
            @Override
            public void onResult(Response response) {
                progressDialog.dismiss();
                if (response == null || getContext() == null) return;
                MyResponse myResponse = MyResponse.from(response);
                if (myResponse.vendors == null) {
                    Toast.makeText(getContext(), myResponse.message, Toast.LENGTH_SHORT).show();
                }

                ArrayAdapter adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_list_item_1, myResponse.vendors);
                vendorsList.setAdapter(adapter);
            }
        });
        apiTask.setToken(AppPreferences.getUserToken(getContext()))
                .execute(Routes.getVendorListUrl(getContext()));

        return rootView;
    }

}
