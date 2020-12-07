package edu.uga.cs.roommateshoppingapp;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.util.Objects;

/**
 * Item class, items that can be added into a shopping cart.
 */
public class Item implements Parcelable {

    public String name;
    public double cost;
    public boolean purchased;
    public int quantity;
    public String roommate;

    /**
     * Constructor
     */
    public Item() {
        this.name = null;
        this.cost = 0.00;
        this.purchased = false;
        this.quantity = 0;
        this.roommate = null;
    }
    /**
     * Constructor
     */
    public Item(String name) {
        this.name = name;
        this.cost = 0.00;
        this.purchased = false;
        this.quantity = 0;
        this.roommate = "";
    }
    /**
     * Constructor
     */
    public Item(String name, double cost, int quantity, String roommate) {
        this.name = name;
        this.cost = cost;
        this.purchased = false;
        this.quantity = quantity;
        this.roommate = roommate;
    }

    /**
     * Constructor
     */
    public Item(String name, double cost, boolean purchased, int quantity, String roommate) {
        this.name = name;
        this.cost = cost;
        this.purchased = purchased;
        this.quantity = quantity;
        this.roommate = roommate;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected Item(Parcel in) {
        name = in.readString();
        cost = in.readDouble();
        purchased = in.readBoolean();
        quantity = in.readInt();
        roommate = in.readString();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(cost);
        dest.writeBoolean(purchased);
        dest.writeInt(quantity);
        dest.writeString(roommate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public Item createFromParcel(Parcel parcel) {
            return new Item(parcel);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
    /**
     * Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the cost of the item.
     */
    public double getCost() {
        return cost;
    }

    /**
     * Sets the cost of the item.
     */
    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * Returns whether or not the item has been purchased.
     */
    public boolean isPurchased() {
        return purchased;
    }

    /**
     * Sets if the item has been purchased.
     */
    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    /**
     * Returns the quantity of an item.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the item.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns the roommate that bought item.
     */
    public String getRoommate() {
        return roommate;
    }

    /**
     * Sets the roommate who bought the item.
     */
    public void setRoommate(String roommate) {
        this.roommate = roommate;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Double.compare(item.cost, cost) == 0 &&
                    purchased == item.purchased &&
                    quantity == item.quantity &&
                    Objects.equals(name, item.name) &&
                    Objects.equals(roommate, item.roommate);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(name, cost, purchased, quantity, roommate);
    }
}
