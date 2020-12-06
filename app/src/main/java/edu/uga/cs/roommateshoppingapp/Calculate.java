package edu.uga.cs.roommateshoppingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Calculate extends AppCompatActivity {

    public static final String DEBUG_TAG = "Calculate Activity";

    public double total;
    public Button accept;
    public String temp;
    public String mainString;
    public double totalCost;
    public double indTotalCost;
    public String totalCostString;

    public TextView totalCostView;
    public TextView mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);

        Intent intent = getIntent();
        ArrayList<Item> purchasedList = intent.getExtras().getParcelableArrayList("purchasedItemList");
        ArrayList<Item> nonPurchasedList = intent.getExtras().getParcelableArrayList("nonPurchasedItemList");

        mainView = findViewById(R.id.main);
        totalCostView = findViewById(R.id.TotalCost);
        accept = findViewById(R.id.accept);

        accept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ShoppingListActivity.class);
                startActivity(intent);
            }
        });

        Log.d( DEBUG_TAG, "purchasedItemList size " + purchasedList.size());
        Log.d( DEBUG_TAG, "nonPurchasedItemList size " + nonPurchasedList.size());

        mainString = "";
        totalCostString = "";
        indTotalCost = 0;
        String [] users = new String[10];

        if (purchasedList.size() > 0) {
            temp = purchasedList.get(0).getRoommate();
            //users[0] = temp;
            // First user stored in array
        } else {
            Log.d( DEBUG_TAG, "No items purchased");
        }

        for (int i = 0; i < purchasedList.size(); i++) {
            totalCost += purchasedList.get(i).getCost() * purchasedList.get(i).getQuantity();
        }
        totalCostString += "Total Cost for All Users: $" + totalCost;

        totalCostView.setText(totalCostString);
        //mainView.setText(mainString);
        // Count how many different users there are in the list
        for (int i = 0; i < purchasedList.size(); i++) {
            users[i] = purchasedList.get(i).getRoommate();
        }

        // Array contains the unique users
        String [] uniqueUsers = new HashSet<String>(Arrays.asList(users)).toArray(new String[0]);

        Log.d(DEBUG_TAG, "Length of purchasedList array: " + purchasedList.size());
        for (int i = 1; i < uniqueUsers.length; i++) {
            Log.d(DEBUG_TAG, purchasedList.get(i).getRoommate());
            mainString += uniqueUsers[i] + "\n";
            for (int k = 0; k < purchasedList.size(); k++) {
                if (uniqueUsers[i].equals(purchasedList.get(k).getRoommate())) {
                    indTotalCost += purchasedList.get(k).getCost() * purchasedList.get(k).getQuantity();
                }
            }
            mainString += "Total cost for this user: $" + indTotalCost + "\n\n\n";
            indTotalCost = 0;
        }
        Log.d(DEBUG_TAG, "mainString");
        mainView.setText(mainString);


    }
}