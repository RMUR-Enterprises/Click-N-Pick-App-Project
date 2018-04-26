package com.example.app.models;

import java.io.Serializable;

public class Item implements Serializable {

    public int id;
    public String name;

    public int price;
    public int categoryId;
    public int vendorId;

    @Override
    public String toString() {
        return name;
    }

}
