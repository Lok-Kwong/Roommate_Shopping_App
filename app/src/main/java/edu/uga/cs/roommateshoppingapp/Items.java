package edu.uga.cs.roommateshoppingapp;

public class Items {

    public String name;
    public double cost;
    public boolean purchased;

    public Items() {
        this.name = null;
        this.cost = 0.00;
        this.purchased = false;

    }

    public Items(String name, float cost, boolean purchased) {
        this.name = name;
        this.cost = cost;
        this.purchased = purchased;
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
}
