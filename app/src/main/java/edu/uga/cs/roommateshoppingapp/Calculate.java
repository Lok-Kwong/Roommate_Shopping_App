package edu.uga.cs.roommateshoppingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Calculate extends AppCompatActivity {

    public static final String DEBUG_TAG = "Calculate Activity";

    public double total;
    public Button accept;
    public String temp;
    public String mainString;
    public double totalCost;
    public double indTotalCost;
    public String totalCostString;
    public ShoppingList shoppingList;

    public TextView totalCostView;
    public TextView mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);

        DecimalFormat df = new DecimalFormat( "####0.00" );

        Intent intent = getIntent();
        ArrayList<Item> purchasedList = intent.getExtras().getParcelableArrayList("purchasedItemList");
        ArrayList<Item> nonPurchasedList = intent.getExtras().getParcelableArrayList("nonPurchasedItemList");
        shoppingList = intent.getExtras().getParcelable("shoppingList"); // Grab for updating total in firebase

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

        mainString = "";
        totalCostString = "";
        indTotalCost = 0;
        String [] users = new String[10];

        if (purchasedList.size() > 0) {
            temp = purchasedList.get(0).getRoommate();
            for (int i = 0; i < purchasedList.size(); i++) {
                totalCost += purchasedList.get(i).getCost() * purchasedList.get(i).getQuantity();
            }
            totalCostString += "Total Cost for All Users: $" + df.format(totalCost);

            totalCostView.setText(totalCostString);
            //mainView.setText(mainString);
            // Count how many different users there are in the list
            for (int i = 0; i < purchasedList.size(); i++) {
                users[i] = purchasedList.get(i).getRoommate();
            }

            // Array contains the unique users
            String [] uniqueUsers = new HashSet<String>(Arrays.asList(users)).toArray(new String[0]);

            Double [] pay = new Double[uniqueUsers.length];
            Double average = totalCost / (uniqueUsers.length - 1);
            Double temp = 0.0;
            String owe = "";

            pay[0] = 0.0;
            Log.d(DEBUG_TAG, "Length of purchasedList array: " + purchasedList.size());
            for (int i = 1; i < uniqueUsers.length; i++) {
                mainString += uniqueUsers[i] + "\n";
                for (int k = 0; k < purchasedList.size(); k++) {
                    if (uniqueUsers[i].equals(purchasedList.get(k).getRoommate())) {
                        indTotalCost += purchasedList.get(k).getCost() * purchasedList.get(k).getQuantity();
                    }
                }
                pay[i] = indTotalCost;

                if (i == uniqueUsers.length - 1) {
                    Log.d(DEBUG_TAG, "HERE: " + pay[0] + " "  + pay[1] + " " + pay[2]);
                    if (pay[1] < pay[2]) {
                        temp = average - pay[1];
                        owe += uniqueUsers[1] + " owes " + uniqueUsers[2] + " $" + df.format(temp) + "\n\n";
                    } else if (pay[2] < pay[1]) {
                        temp = average - pay[2];
                        owe += uniqueUsers[2] + " owes " + uniqueUsers[1] + " $" + df.format(temp) + "\n\n";
                    }
                }
                mainString += "Has spent: $" + df.format(indTotalCost) + " out of $" + df.format(totalCost) + "\n\n" + owe;

                indTotalCost = 0;
            }
            mainView.setText(mainString);
        } else {
            Log.d( DEBUG_TAG, "No items purchased");
        }

        // Update the total in firebase
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("ShoppingLists").child(shoppingList.getShoppingListName());
        Map<String, Object> updates = new HashMap<>();
        updates.put("total", totalCost);

        myRef.updateChildren( updates )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Show a quick confirmation
                        Log.d(DEBUG_TAG, "Calculated: " + shoppingList.getTotal());
                    }
                })
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.d(DEBUG_TAG, "Can't calculate total ");
                    }
                });


    }
}