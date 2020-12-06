package edu.uga.cs.roommateshoppingapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ShoppingList implements Parcelable {

    public String shoppingListName;
    public String date;
    public double total;
    public ArrayList<Item> items;

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

    public ShoppingList(String shoppingListName, String date, double total, ArrayList<Item> items) {
        this.shoppingListName = shoppingListName;
        this.date = date;
        this.total = total;
        this.items = items;
    }

    protected ShoppingList(Parcel in) {
        shoppingListName = in.readString();
        date = in.readString();
        total = in.readDouble();
        items = in.readArrayList(getClass().getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(shoppingListName);
        dest.writeString(date);
        dest.writeDouble(total);
        dest.writeList(items);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShoppingList> CREATOR = new Creator<ShoppingList>() {
        @Override
        public ShoppingList createFromParcel(Parcel in) {
            return new ShoppingList(in);
        }

        @Override
        public ShoppingList[] newArray(int size) {
            return new ShoppingList[size];
        }
    };

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

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }


}
