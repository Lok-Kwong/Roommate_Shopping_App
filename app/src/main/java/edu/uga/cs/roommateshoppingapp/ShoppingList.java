package edu.uga.cs.roommateshoppingapp;

import java.util.ArrayList;

public class ShoppingList {

    public String shoppingListName;
    public String date;
    public double total;
    public ArrayList<Items> items;

    public ShoppingList() {
        this.shoppingListName = null;
        this.date = null;
        this.total = 0.00;
        this.items = new ArrayList<>();
    }

    public ShoppingList(String shoppingListName, String date, double total) {
        this.shoppingListName = shoppingListName;
        this.date = date;
        this.total = total;
        this.items = new ArrayList<>();
    }

    public String getShoppingListName() {
        return shoppingListName;
    }

    public void setShoppingListName(String shoppingListName) {
        this.shoppingListName = shoppingListName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public ArrayList<Items> getItems() {
        return items;
    }

    public void addItem(Items item) {
        this.items.add(item);
    }


}
