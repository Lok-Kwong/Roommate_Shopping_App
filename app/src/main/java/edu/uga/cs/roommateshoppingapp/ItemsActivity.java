package edu.uga.cs.roommateshoppingapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemsActivity extends AppCompatActivity {

    public static final String DEBUG_TAG = "ItemsActivity";

    // Non-purchased items recyclerview
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public static RecyclerView.Adapter recyclerAdapter;

    private View divider;

    // Purchased items recyclerview
    private RecyclerView recyclerView2;
    private RecyclerView.LayoutManager layoutManager2;
    public static RecyclerView.Adapter recyclerAdapter2; // Static to update from recycler

    private TextView titleView;
    private Button calculateTotalBtn; // implement items parceable
    private TextView recentlyPurchasedView;

    public static ArrayList<Item> itemList;
    public static ArrayList<Item> purchasedItemList;
    public static ArrayList<Item> nonPurchasedItemList;

    private FloatingActionButton fabNewPost; // Add item
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText itemNameView;
    public static String shoppingTitle; // Static for itemRecycleAdapter to grab title for fb

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        // Get Shopping List object for future use
        Intent intent = getIntent();
        ShoppingList shoppingList = intent.getExtras().getParcelable("shoppingListObject");

        // For non-purchased items
        recyclerView = (RecyclerView) findViewById( R.id.recycleItems );
        // use a linear layout manager for the recycler view
        layoutManager = new LinearLayoutManager(this );
        recyclerView.setLayoutManager( layoutManager );

        // For purchased items
        recyclerView2 = (RecyclerView) findViewById( R.id.recyclerView );
        // use a linear layout manager for the recycler view
        layoutManager2 = new LinearLayoutManager(this );
        recyclerView2.setLayoutManager( layoutManager2 );

        // Make recently purchased list
//      textview.setVisibility(textview.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        recentlyPurchasedView = (TextView) findViewById(R.id.textView6);
//        recentlyPurchasedView.setVisibility(View.GONE);
        divider = (View) findViewById(R.id.divider);
//        divider.setVisibility(View.GONE);

        // Set Title of shopping list
        titleView = (TextView) findViewById(R.id.textView5);
        shoppingTitle = shoppingList.getShoppingListName();
        titleView.setText(shoppingTitle);

        // Add new item
        fabNewPost = (FloatingActionButton) findViewById( R.id.fabSubmitPost );
        fabNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });

        // get a Firebase DB instance reference
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("ShoppingLists").child(shoppingTitle).child("Items");
        itemList = new ArrayList<Item>();
        purchasedItemList = new ArrayList<Item>();
        nonPurchasedItemList = new ArrayList<Item>();

        calculateTotalBtn = findViewById(R.id.calculateBtn);

        // Set up a listener (event handler) to receive a value for the database reference, but only one time.
        // This type of listener is called by Firebase once by immediately executing its onDataChange method.
        // We can use this listener to retrieve the current list of shopping lists.
        // Other types of Firebase listeners may be set to listen for any and every change in the database
        // i.e., receive notifications about changes in the data in real time (hence the name, Realtime database).
        // This listener will be invoked asynchronously, as no need for an AsyncTask, as in the previous apps
        // to maintain job leads.
        myRef.addListenerForSingleValueEvent( new ValueEventListener() {

            @Override
            public void onDataChange( DataSnapshot snapshot ) {
                // Once we have a DataSnapshot object, knowing that this is a list,
                // we need to iterate over the elements and place them on a List.
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    Item item = postSnapshot.getValue(Item.class);
                    if (item == null) {
                        Log.d( DEBUG_TAG, "No items");
                        break;
                    }
                    else {
                        itemList.add(item);
                    }

                    if(item.isPurchased()) {
                        purchasedItemList.add(item);
                    }
                    else {
                        nonPurchasedItemList.add(item);
                    }
                    Log.d( DEBUG_TAG, "ItemsActivity.onCreate(): added: " + item.getName() );
                }
                Log.d( DEBUG_TAG, "ItemsActivity.onCreate(): setting recyclerAdapter" );

                Log.d( DEBUG_TAG, "itemList size " + itemList.size());
                Log.d( DEBUG_TAG, "purchasedItemList size " + purchasedItemList.size());
                Log.d( DEBUG_TAG, "nonPurchasedItemList size " + nonPurchasedItemList.size());



                // create a ItemsRecyclerAdapter to populate a ReceyclerView to display the items
                recyclerAdapter = new ItemsRecyclerAdapter( nonPurchasedItemList );
                recyclerView.setAdapter( recyclerAdapter );
                // create a purchased ItemsRecyclerAdapter to populate a ReceyclerView to display the purchased items
                recyclerAdapter2 = new ItemsRecyclerAdapter( purchasedItemList );
                recyclerView2.setAdapter( recyclerAdapter2 );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        } );

    }

    // Add item dialog
    public void createDialog() {
        dialogBuilder = new AlertDialog.Builder(ItemsActivity.this);
        final View popupView = getLayoutInflater().inflate(R.layout.add_item_popup, null);
        itemNameView = (EditText) popupView.findViewById( R.id.itemNameView);

        dialogBuilder.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );

        dialogBuilder.setPositiveButton(
                "Submit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addItem();
                    }
                });

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    // Adds item to itemList and updates the db
    public void addItem() {
        Log.d(DEBUG_TAG, "Adding Item");
        String itemName = itemNameView.getText().toString();
        if (TextUtils.isEmpty(itemName)) {
            Toast.makeText( getApplicationContext(), "Item needs a name. Try again. ",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // Add into specific shopping list
        final Item item = new Item (itemName);
        itemList.add(item);

        // Add a new element (item) to the list of shoppinglists in Firebase.
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("ShoppingLists").child(shoppingTitle);
        Map<String, Object> shoppingListItemUpdate = new HashMap<>();
        shoppingListItemUpdate.put("Items", itemList);

        myRef.updateChildren( shoppingListItemUpdate )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Show a quick confirmation
                        Log.d(DEBUG_TAG, "addItem: added " + item.getName());
                        Toast.makeText(getApplicationContext(), "Item created for " + item.getName(),
                                Toast.LENGTH_SHORT).show();
                        // update the adapter data
                        nonPurchasedItemList.add(item);
                        recyclerAdapter.notifyItemInserted(nonPurchasedItemList.size());
                    }
                })
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.d(DEBUG_TAG, "deleteShoppingList: cant delete");
                        Toast.makeText(getApplicationContext(), "Failed to add " +  item.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}