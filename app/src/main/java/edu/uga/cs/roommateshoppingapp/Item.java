package edu.uga.cs.roommateshoppingapp;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class Item implements Parcelable {

    public String name;
    public double cost;
    public boolean purchased;
    public int quantity;
    public String roommate;

    public Item() {
        this.name = null;
        this.cost = 0.00;
        this.purchased = false;
        this.quantity = 0;
        this.roommate = null;
    }

    public Item(String name) {
        this.name = name;
        this.cost = 0.00;
        this.purchased = false;
        this.quantity = 0;
        this.roommate = "";
    }
    public Item(String name, double cost, int quantity, String roommate) {
        this.name = name;
        this.cost = cost;
        this.purchased = false;
        this.quantity = quantity;
        this.roommate = roommate;
    }

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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getRoommate() {
        return roommate;
    }

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
