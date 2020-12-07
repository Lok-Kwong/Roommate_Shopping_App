package edu.uga.cs.roommateshoppingapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * This is the shopping list class.
 */
public class ShoppingList implements Parcelable {

    public String shoppingListName;
    public String date;
    public double total;
    public ArrayList<Item> items;

    /**
     * Constructor.
     */
    public ShoppingList() {
        this.shoppingListName = null;
        this.date = null;
        this.total = 0.00;
        this.items = new ArrayList<>();
    }


    /**
     * Constructor.
     */
    public ShoppingList(String shoppingListName, String date, double total) {
        this.shoppingListName = shoppingListName;
        this.date = date;
        this.total = total;
        this.items = new ArrayList<>();
    }


    /**
     * Constructor.
     */
    public ShoppingList(String shoppingListName, String date, double total, ArrayList<Item> items) {
        this.shoppingListName = shoppingListName;
        this.date = date;
        this.total = total;
        this.items = items;
    }


    /**
     * Constructor.
     */
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


    /**
     * Returns the shopping list name.
     */
    public String getShoppingListName() {
        return shoppingListName;
    }


    /**
     * Sets the shopping list name.
     */
    public void setShoppingListName(String shoppingListName) {
        this.shoppingListName = shoppingListName;
    }


    /**
     * Returns the shopping list date.
     */
    public String getDate() {
        return date;
    }


    /**
     * Sets the shopping list date.
     */
    public void setDate(String date) {
        this.date = date;
    }


    /**
     * Returns the total of the shopping list.
     */
    public double getTotal() {
        return total;
    }


    /**
     * Sets the shopping list total.
     */
    public void setTotal(double total) {
        this.total = total;
    }


    /**
     * Returns all of the items in the shopping list.
     */
    public ArrayList<Item> getItems() {
        return items;
    }


    /**
     * Sets the items in the shopping list.
     */
    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }


}
