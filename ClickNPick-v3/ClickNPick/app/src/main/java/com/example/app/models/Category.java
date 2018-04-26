package com.example.app.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable {

    public int id;
    public String name;
    public String image;
    public int parentId;
    public ArrayList<Item> items;

}
