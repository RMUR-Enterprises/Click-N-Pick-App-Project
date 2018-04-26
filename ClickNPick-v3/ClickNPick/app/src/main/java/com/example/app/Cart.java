package com.example.app;

import com.example.app.models.Item;

import java.util.ArrayList;

public class Cart {

    private static ArrayList<Item> ITEMS = new ArrayList<>();

    public static ArrayList<Item> getItems() {
        return ITEMS;
    }

    public static void addItem(Item item) {
        if (item != null) {
            ITEMS.add(item);
        }
    }

    public static int getTotalPrice() {
        int price = 0;
        for (Item item : ITEMS) {
            price += item.price;
        }
        return price;
    }

    public static boolean removeItem(int itemId) {
        Item itemToRemove = null;

        for (Item item : ITEMS) {
            if (item.id == itemId) {
                itemToRemove = item;
            }
        }

        if (itemToRemove == null) {
            return false;
        } else {
            ITEMS.remove(itemToRemove);
            return true;
        }
    }

    /*public static void removeAllItems(int itemId) {
        boolean removed = removeItem(itemId);
        while (removed) {
            removed = removeItem(itemId);
        }
    }*/

}
