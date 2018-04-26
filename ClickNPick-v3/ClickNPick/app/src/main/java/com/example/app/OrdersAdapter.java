package com.example.app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.app.models.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OrdersAdapter extends ArrayAdapter<Order> {

    ArrayList<Order> orders = new ArrayList<>();

    private void sortOrders(ArrayList<Order> orders) {
        Collections.sort(orders, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return (int) (o1.pickUpTime - o2.pickUpTime);
            }
        });
    }

    public OrdersAdapter(@NonNull Context context, @NonNull ArrayList<Order> orders) {
        super(context, R.layout.row_order, orders);
        sortOrders(orders);
        this.orders = orders;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View rootView = convertView;
        if (rootView == null) {
            rootView = LayoutInflater.from(getContext()).inflate(R.layout.row_order, parent, false);
        }

        Order order = getItem(position);
        if (order == null) return rootView;

        TextView itemsText = rootView.findViewById(R.id.itemsText);
        TextView pickupTimeText = rootView.findViewById(R.id.pickupTimeText);
        TextView statusText = rootView.findViewById(R.id.statusText);

        itemsText.setText(order.getItemsText());
        pickupTimeText.setText(order.getPickupTimeText());
        statusText.setText(order.getStatus());

        return rootView;
    }

}
