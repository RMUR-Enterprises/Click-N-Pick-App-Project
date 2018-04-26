package com.example.app.vendor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.app.AppPreferences;
import com.example.app.Data;
import com.example.app.FeedbackFragment;
import com.example.app.R;
import com.example.app.Routes;
import com.example.app.models.MyResponse;
import com.example.app.vendor.fragments.VendorHomeFragment;
import com.example.app.vendor.fragments.VendorProfileFragment;
import com.example.utils.api.APITask;
import com.example.utils.api.ResponseCallback;

import okhttp3.Response;

public class VendorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fragment_holder, VendorHomeFragment.newInstance()).commit();

        Data.reloadItems(this, false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        FragmentManager fm = getSupportFragmentManager();

        if (id == R.id.nav_home) {
            fm.beginTransaction().replace(R.id.fragment_holder, VendorHomeFragment.newInstance()).commit();

        } else if (id == R.id.nav_my_account) {
            showProfile(fm);

        } else if (id == R.id.nav_order_list) {
            startActivity(new Intent(this, VendorOrdersActivity.class));

        } else if (id == R.id.nav_notifications) {
            startActivity(new Intent(this, VendorOrdersActivity.class)
                    .putExtra(VendorOrdersActivity.KEY_EXTRA_NOTIFCATION_ONLY, true));


        } else if (id == R.id.nav_feedback) {
            fm.beginTransaction().replace(R.id.fragment_holder, FeedbackFragment.newInstance()).commit();

        } else if (id == R.id.nav_logout) {
            AppPreferences.setVendorToken(this, null, 0);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showProfile(final FragmentManager fm) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        APITask apiTask = new APITask(null, new ResponseCallback() {
            @Override
            public void onResult(Response response) {
                progressDialog.dismiss();
                if (response == null) return;
                MyResponse myResponse = MyResponse.from(response);

                if (response.isSuccessful()) {
                    Fragment vendorProfileFragment = VendorProfileFragment.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(VendorProfileFragment.KEY_EXTRA_PROFILE, myResponse);
                    vendorProfileFragment.setArguments(bundle);
                    fm.beginTransaction().replace(R.id.fragment_holder, vendorProfileFragment).commit();
                } else {
                    Toast.makeText(VendorActivity.this, myResponse.message, Toast.LENGTH_SHORT).show();
                }
            }
        });
        apiTask.setToken(AppPreferences.getVendorToken(this))
                .execute(Routes.getProfileUrl(this));
    }

}
