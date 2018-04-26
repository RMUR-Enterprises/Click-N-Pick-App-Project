package com.example.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

import com.example.app.models.Category;
import com.example.app.models.Item;
import com.example.app.models.MyResponse;
import com.example.app.models.Order;
import com.example.utils.api.APITask;
import com.example.utils.api.ResponseCallback;

import java.util.ArrayList;

import okhttp3.Response;

@SuppressWarnings("WeakerAccess")
public class Data {

    public static ArrayList<Category> CATEGORIES = new ArrayList<>();
    private static ArrayList<Item> ALL_ITEMS = new ArrayList<>();

    public static void reloadItems(final Activity activity, boolean customer) {
        reloadItems(activity, customer, null);
    }

    public static void reloadItems(final Activity activity, boolean customer, final Runnable callback) {
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading Items..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        APITask apiTask = new APITask(null, new ResponseCallback() {
            @Override
            public void onResult(Response response) {
                progressDialog.dismiss();
                MyResponse myResponse = MyResponse.from(response);
                if (myResponse.categories == null) {
                    Toast.makeText(activity, myResponse.message, Toast.LENGTH_SHORT).show();
                    activity.finish();
                    return;
                }
                CATEGORIES = filterCategories(myResponse.categories);
                ALL_ITEMS = getAllItems();
                if (callback != null) callback.run();
            }
        });
        String token = customer ?
                AppPreferences.getUserToken(activity) : AppPreferences.getVendorToken(activity);
        apiTask.setToken(token)
                .execute(Routes.getCategoriesUrl(activity));
    }

    private static ArrayList<Category> filterCategories(ArrayList<Category> categories) {
        ArrayList<Category> filtered = new ArrayList<>();
        for (Category category : categories) {
            if (category.items != null && category.items.size() > 0) {
                filtered.add(category);
            }
        }
        return filtered;
    }

    public static ArrayList<Item> getItems(int categoryId) {
        ArrayList<Item> items = new ArrayList<>();
        for (Category category : CATEGORIES) {
            if (category.id == categoryId) {
                items.addAll(category.items);
            }
        }
        return items;
    }

    private static ArrayList<Item> getAllItems() {
        ArrayList<Item> items = new ArrayList<>();
        for (Category category : CATEGORIES) {
            items.addAll(category.items);
        }
        return items;
    }

    private static Item findItem(int id) {
        for (Item item : ALL_ITEMS) {
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }

    private static String getItemString(int id) {
        Item item = findItem(id);
        if (item == null) {
            return "Unknown item, ID: " + id;
        } else {
            return item.name + ", ID:" + id + ", Price: " + item.price;
        }
    }

    public static String getItemNamesString(ArrayList<Integer> ids) {
        StringBuilder builder = new StringBuilder();
        for (int id : ids) {
            builder.append(getItemString(id)).append("\n");
        }
        return builder.toString();
    }

    public static void reOrder(Order order) {
        Cart.getItems().clear();
        for (int itemId : order.item_ids) {
            Item item = findItem(itemId);
            if (item != null) {
                Cart.addItem(item);
            }
        }
    }

}
