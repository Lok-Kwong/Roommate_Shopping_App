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

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public static RecyclerView.Adapter recyclerAdapter;

    private TextView titleView;
    private Button calculateTotalBtn;
    private TextView recentlyPurchasedView;

    public static ArrayList<Item> itemList;

    private View divider;

    private FloatingActionButton fabNewPost;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText itemNameView;
    public static String shoppingTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        // Get Shopping List object for future use
        Intent intent = getIntent();
        ShoppingList shoppingList = intent.getExtras().getParcelable("shoppingListObject");

        recyclerView = (RecyclerView) findViewById( R.id.recycleItems );

        // use a linear layout manager for the recycler view
        layoutManager = new LinearLayoutManager(this );
        recyclerView.setLayoutManager( layoutManager );

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
                    itemList.add(item);
                    Log.d( DEBUG_TAG, "ItemsActivity.onCreate(): added: " + item.getName() );
                }
                Log.d( DEBUG_TAG, "ItemsActivity.onCreate(): setting recyclerAdapter" );

                // Now, create a JobLeadRecyclerAdapter to populate a ReceyclerView to display the shopping list
                recyclerAdapter = new ItemsRecyclerAdapter( itemList );
                recyclerView.setAdapter( recyclerAdapter );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        } );

    }

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

    public void addItem() {
        Log.d(DEBUG_TAG, "Adding Item");
        String itemName = itemNameView.getText().toString();
        if (TextUtils.isEmpty(itemName)) {
            itemNameView.setError("Required");
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
                        recyclerAdapter.notifyItemInserted(itemList.size());


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