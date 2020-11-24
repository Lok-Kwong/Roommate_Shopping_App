package edu.uga.cs.roommateshoppingapp;

public class Item {

    public String name;
    public double cost;
    public boolean purchased;
    public int quantity;

    public Item() {
        this.name = null;
        this.cost = 0.00;
        this.purchased = false;
        this.quantity = 0;
    }

    public Item(String name, float cost, boolean purchased, int quantity) {
        this.name = name;
        this.cost = cost;
        this.purchased = purchased;
        this.quantity = quantity;
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
}
