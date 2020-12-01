package edu.uga.cs.roommateshoppingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is an adapter class for the RecyclerView to show all Shopping Lists.
 */
public class ItemsRecyclerAdapter extends RecyclerView.Adapter<ItemsRecyclerAdapter.itemListHolder> {

    public static final String DEBUG_TAG = "ItemsRecyclerAdapter";

    private List<Item> itemList;

    public ItemsRecyclerAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class itemListHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView cost;
        TextView roommate;
        ImageView delete;
        ImageView edit;
        MaterialCardView cardView;
        private final Context context;

        private AlertDialog.Builder dialogBuilder;
        private AlertDialog dialog;
        private EditText listNameView;
        private EditText costView;
        private EditText quantityView;
        private EditText roommateView;

        public itemListHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            title = (TextView) itemView.findViewById(R.id.itemTitle);
            cost = (TextView) itemView.findViewById(R.id.price);
            roommate = (TextView) itemView.findViewById(R.id.roommate);
            edit = (ImageView) itemView.findViewById(R.id.editImg);
            delete = (ImageView) itemView.findViewById(R.id.deleteBtn);

            // Clicking the cardview means it's purchased - start new dialog for price, quantity
            cardView = (MaterialCardView) itemView.findViewById(R.id.shoppingCard);
            cardView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Start
                    cardView.setChecked(!cardView.isChecked());
                }
            });

            // Delete item
            delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                    builder.setMessage("Are you sure you want to delete this?")
                            .setTitle("Delete an item");

                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User DELETES shopping list
                            deleteItem(getAdapterPosition());
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
            // Update item
            edit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialogBuilder = new AlertDialog.Builder(context);
                    final View popupView = LayoutInflater.from(context).inflate(R.layout.edit_item_popup, null);
                    listNameView = (EditText) popupView.findViewById( R.id.itemNameView );
                    costView = (EditText) popupView.findViewById( R.id.costView );
                    quantityView = (EditText) popupView.findViewById( R.id.quantityView );
                    roommateView = (EditText) popupView.findViewById( R.id.roommateView );

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
                                    String itemName = listNameView.getText().toString();
                                    String roommateName = roommateView.getText().toString();
                                    double cost = Double.parseDouble(costView.getText().toString());
                                    int quantity = Integer.parseInt(quantityView.getText().toString());
                                    // Check if valid fields
                                    if (TextUtils.isEmpty(itemName) || TextUtils.isEmpty(roommateName) || TextUtils.isEmpty(costView.getText().toString()) || TextUtils.isEmpty(quantityView.getText().toString())) {
                                        Toast.makeText(context, "Fill in all the texts!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    editItem(getAdapterPosition(), itemName, cost, quantity, roommateName);
                                }
                            });

                    dialogBuilder.setView(popupView);
                    dialog = dialogBuilder.create();
                    dialog.show();
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            });
        }

        public void deleteItem(final int position) {
            // update the database with new itemsList and remove from firebase database
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("ShoppingLists").child(ItemsActivity.shoppingTitle);

            Map<String, Object> shoppingListItemUpdate = new HashMap<>();
            // Remove from list and update on firebase
            itemList.remove(position);
            shoppingListItemUpdate.put("Items", itemList);

            myRef.updateChildren( shoppingListItemUpdate )
                    .addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Show a quick confirmation
                            Toast.makeText(context, "Item deleted",
                                    Toast.LENGTH_SHORT).show();
                            // update the adapter data
                            ItemsActivity.recyclerAdapter.notifyItemRemoved(position);
                        }
                    })
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Log.d(DEBUG_TAG, "deleteItem: cant delete");
                            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        public void editItem(final int position, String name, double cost, int quantity, String roommate) {
            // update the database with new itemsList and remove from firebase database
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("ShoppingLists").child(ItemsActivity.shoppingTitle);

            Map<String, Object> shoppingListItemUpdate = new HashMap<>();
            // Create a new item and replace it
            Item item = new Item(name, cost, quantity, roommate);
            itemList.set(position, item);
            shoppingListItemUpdate.put("Items", itemList);

            myRef.updateChildren( shoppingListItemUpdate )
                    .addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Show a quick confirmation
                            Toast.makeText(context, "Item updated",
                                    Toast.LENGTH_SHORT).show();
                            ItemsActivity.recyclerAdapter.notifyItemChanged(position);
                        }
                    })
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Log.d(DEBUG_TAG, "editItem: can't update item");
                            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
                        }
                    });
        }


    }

    @Override
    public itemListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new itemListHolder(view);
    }

    // This method fills in the values of the Views to show a Shopping List
    @Override
    public void onBindViewHolder(itemListHolder holder, int position) {
        Item item = itemList.get(position);

        Log.d(DEBUG_TAG, "onBindViewHolder: " + item);

        holder.title.setText(String.valueOf(item.getName()));
        holder.cost.setText(String.valueOf(item.getCost()) + " x" + String.valueOf(item.getQuantity()));
        if (String.valueOf(item.getRoommate()) == "null") {
            holder.roommate.setVisibility(View.GONE);
        }
        else {
            holder.roommate.setVisibility(View.VISIBLE);
            holder.roommate.setText(String.valueOf(item.getRoommate()));
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


}
