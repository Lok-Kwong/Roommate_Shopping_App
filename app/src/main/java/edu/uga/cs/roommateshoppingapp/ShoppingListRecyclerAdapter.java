package edu.uga.cs.roommateshoppingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an adapter class for the RecyclerView to show all Shopping Lists.
 */
public class ShoppingListRecyclerAdapter extends RecyclerView.Adapter<ShoppingListRecyclerAdapter.ShoppingListHolder> {

    public static final String DEBUG_TAG = "ShopListRecyclerAdapter";

    private ArrayList<ShoppingList> shoppingLists;


    public ShoppingListRecyclerAdapter(ArrayList<ShoppingList> shoppingLists) {
        this.shoppingLists = shoppingLists;
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class ShoppingListHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView date;
        TextView total;
        ImageView delete;
        MaterialCardView cardView;
        private final Context context;

        public ShoppingListHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            title = (TextView) itemView.findViewById(R.id.shoppingListTitle);
            date = (TextView) itemView.findViewById(R.id.date);
            total = (TextView) itemView.findViewById(R.id.total);
            delete = (ImageView) itemView.findViewById(R.id.deleteBtn);
            // Clicking the cardview starts the items activity
            cardView = (MaterialCardView) itemView.findViewById(R.id.shoppingCard);
            cardView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent( context, ItemsActivity.class );
                    intent.putExtra("shoppingListObject", shoppingLists.get(getAdapterPosition()));
                    context.startActivity( intent );

                    // Add items, add to total
                    // Update database with the query?
                    // Get items by putting snapshot into item class and add into shoppingList.get()
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                    builder.setMessage("Are you sure you want to delete this?")
                            .setTitle("Delete a Shopping List");

                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User DELETES shopping list
                            deleteShoppingList();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }

        public void deleteShoppingList() {
            // query the database to determine the key and remove from firebase database
            final String shoppingTitle = title.getText().toString();
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("ShoppingLists");
            myRef.child(shoppingTitle).removeValue()
                    .addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Show a quick confirmation
                            Log.d(DEBUG_TAG, "deleteShoppingList: deleted " + getAdapterPosition());
                            Toast.makeText(itemView.getContext(), "Deleted " + shoppingTitle, Toast.LENGTH_SHORT).show();
                            // Hacky solution to dynamically remove cardview in the ShoppingList Activity
                            ShoppingListActivity.shoppingLists.remove(getAdapterPosition()); // Remove from shopping list
                            ShoppingListActivity.recyclerAdapter.notifyItemRemoved(getAdapterPosition()); // Notify recycle adapter


                        }
                    })
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Log.d(DEBUG_TAG, "deleteShoppingList: can't delete");
                            Toast.makeText(itemView.getContext(), "Failed to delete " + shoppingTitle, Toast.LENGTH_SHORT).show();
                        }
                    });

        }


    }

    @Override
    public ShoppingListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list, parent, false);
        return new ShoppingListHolder(view);
    }

    // This method fills in the values of the Views to show a Shopping List
    @Override
    public void onBindViewHolder(ShoppingListHolder holder, int position) {
        ShoppingList shoppingList = shoppingLists.get(position);

        Log.d(DEBUG_TAG, "onBindViewHolder: " + shoppingList);

        holder.title.setText(String.valueOf(shoppingList.getShoppingListName()));
        holder.date.setText(String.valueOf(shoppingList.getDate()));
        holder.total.setText(String.valueOf(shoppingList.getTotal()));
    }

    @Override
    public int getItemCount() {
        return shoppingLists.size();
    }


}
