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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShoppingListActivity extends AppCompatActivity {

    public static final String DEBUG_TAG = "ShoppingListActivity";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public static RecyclerView.Adapter recyclerAdapter;

    public static ArrayList<ShoppingList> shoppingLists;

    private FloatingActionButton fabNewPost;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText listNameView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shopping_list);

        recyclerView = (RecyclerView) findViewById( R.id.recyclerView );

        // use a linear layout manager for the recycler view
        layoutManager = new LinearLayoutManager(this );
        recyclerView.setLayoutManager( layoutManager );

        // get a Firebase DB instance reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("ShoppingLists");

        shoppingLists = new ArrayList<ShoppingList>();

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
                    ShoppingList shoppingItem = postSnapshot.getValue(ShoppingList.class);
                    shoppingLists.add(shoppingItem);
                    Log.d( DEBUG_TAG, "ShoppingListActivity.onCreate(): added: " + shoppingItem.getShoppingListName() );
                }
                Log.d( DEBUG_TAG, "ShoppingListActivity.onCreate(): setting recyclerAdapter" );

                // Now, create a JobLeadRecyclerAdapter to populate a ReceyclerView to display the shopping list
                recyclerAdapter = new ShoppingListRecyclerAdapter( shoppingLists );
                recyclerView.setAdapter( recyclerAdapter );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        } );

        fabNewPost = (FloatingActionButton) findViewById( R.id.fabSubmitPost );
        fabNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });
    }

    public void createDialog() {
        dialogBuilder = new AlertDialog.Builder(ShoppingListActivity.this);
        final View popupView = getLayoutInflater().inflate(R.layout.add_shopping_list_popup, null);
        listNameView = (EditText) popupView.findViewById( R.id.listName );

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
                        addShoppingList();
                    }
                });

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void addShoppingList() {
        Log.d(DEBUG_TAG, "Adding shopping list");
        String shoppingListName = listNameView.getText().toString();
        if (TextUtils.isEmpty(shoppingListName)) {
            listNameView.setError("Required");
            return;
        }
        String currentDate = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date());
//        ArrayList<Item> itemList = new ArrayList<Item>();
//        itemList.add( new Item("Lettuce", 9.99f, false, 2, "Lok"));
        final ShoppingList shoppingList = new ShoppingList( shoppingListName, currentDate, 0.00);

        Map<String, Object> stringShoppingListHashMap = new HashMap<>();
        stringShoppingListHashMap.put(shoppingListName, shoppingList);

        // Add a new element (shopping list) to the list of shopping lists in Firebase.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("ShoppingLists");

        // First, a call to push() appends a new node to the existing list (one is created
        // if this is done for the first time).  Then, we set the value in the newly created
        // list node to store the new job lead.
        // This listener will be invoked asynchronously, as no need for an AsyncTask, as in
        // the previous apps to maintain job leads.
        myRef.updateChildren( stringShoppingListHashMap )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Show a quick confirmation
                        Toast.makeText(getApplicationContext(), "Shopping List created for " + shoppingList.getShoppingListName(),
                                Toast.LENGTH_SHORT).show();

                        // Clear the EditTexts for next use.
                        listNameView.setText("");

                        // update the adapter data
                        shoppingLists.add(shoppingList);
                        recyclerAdapter.notifyItemInserted(shoppingLists.size());


                    }
                })
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText( getApplicationContext(), "Failed to create a Shopping List for " + shoppingList.getShoppingListName(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Log.d(DEBUG_TAG, "Signing out: " + user.getDisplayName());
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}