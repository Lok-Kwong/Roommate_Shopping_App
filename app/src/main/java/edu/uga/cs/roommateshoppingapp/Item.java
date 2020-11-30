package edu.uga.cs.roommateshoppingapp;

public class Item {

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
        this.roommate = null;
    }

    public Item(String name, float cost, boolean purchased, int quantity, String roommate) {
        this.name = name;
        this.cost = cost;
        this.purchased = purchased;
        this.quantity = quantity;
        this.roommate = roommate;
    }


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
}
