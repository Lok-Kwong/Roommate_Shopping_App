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
 * This is an adapter class for the RecyclerView to show all items.
 */
public class ItemsRecyclerAdapter extends RecyclerView.Adapter<ItemsRecyclerAdapter.itemListHolder> {

    public static final String DEBUG_TAG = "ItemsRecyclerAdapter";

    private List<Item> itemList; // list of items for two recycle view(either for purchased list or unpurchased list)

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
                    cardView.setChecked(!cardView.isChecked());
                    // Is purchased
                    if(cardView.isChecked()) {
                        createDialogPurchase();  // update db and set as purchased then remove from recycle and add to 2nd recycle adapter
                    }
                    else {
                        setUnpurchased(getAdapterPosition(), itemList.get(getAdapterPosition()).getName());  // Item is unchecked/not purchased
                    }
                }
            });

            // Delete item
            delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    createDialogDelete();
                }
            });
            // Update item
            edit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(cardView.isChecked()) { // Twe edits
                        createDialogEdit(); // If checked, add more options to edit
                    }
                    else {
                        createUnpurchasedDialogEdit(); // else, can only edit item name
                    }
                }
            });
        }

        // Creates a popup for deleting
        public void createDialogDelete() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

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

        // DELETE an item
        public void deleteItem(final int position) {
            // update the database with new itemsList and remove from firebase database
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("ShoppingLists").child(ItemsActivity.shoppingTitle);
            final boolean purchased = itemList.get(position).isPurchased();

            Map<String, Object> shoppingListItemUpdate = new HashMap<>();

            // Remove from list and update on firebase
            ItemsActivity.itemList.remove(ItemsActivity.itemList.indexOf(itemList.get(position))); // Find and remove element from overarching itemlist based on item to be deleted in sublist(purchased/unpurchased)

            // Positional problem -> position in this itemList does not correspond to original itemList -> Find the actual item in real list then remove

            shoppingListItemUpdate.put("Items", ItemsActivity.itemList);

            myRef.updateChildren( shoppingListItemUpdate )
                    .addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Show a quick confirmation
                            Toast.makeText(context, "Item deleted",
                                    Toast.LENGTH_SHORT).show();
                            // update the adapter data based on purchased
                            if (purchased) {
                                ItemsActivity.purchasedItemList.remove(getAdapterPosition());
                                ItemsActivity.recyclerAdapter2.notifyItemRemoved(position);
                            }
                            else {
                                ItemsActivity.nonPurchasedItemList.remove(getAdapterPosition());
                                ItemsActivity.recyclerAdapter.notifyItemRemoved(position);
                            }
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

        // Creates the dialog when clicking edit on an unpurchased item
        public void createUnpurchasedDialogEdit() {
            dialogBuilder = new AlertDialog.Builder(context);
            final View popupView = LayoutInflater.from(context).inflate(R.layout.edit_item_unpurchased_popup, null);
            listNameView = (EditText) popupView.findViewById( R.id.itemNameView );

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
                            // Check if valid fields
                            if ( TextUtils.isEmpty(listNameView.getText().toString()) ) {
                                Toast.makeText(context, "Please enter an item name. Try again. ",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String itemName = listNameView.getText().toString();
                            editItem(getAdapterPosition(), itemName);
                        }
                    });

            dialogBuilder.setView(popupView);
            dialog = dialogBuilder.create();
            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // Edit for unpurchased item
        public void editItem(final int position, String name) {
            // update the database with new itemsList and remove from firebase database
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("ShoppingLists").child(ItemsActivity.shoppingTitle);

            Map<String, Object> shoppingListItemUpdate = new HashMap<>();
            // Create a new item and replace it
            final Item newItem = new Item(name);

            ItemsActivity.itemList.set(ItemsActivity.itemList.indexOf(itemList.get(position)), newItem); // Replace in main list

            shoppingListItemUpdate.put("Items", ItemsActivity.itemList);

            myRef.updateChildren( shoppingListItemUpdate ) // Update the db with new name
                    .addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Show a quick confirmation
                            Toast.makeText(context, "Item updated",
                                    Toast.LENGTH_SHORT).show();
                            ItemsActivity.nonPurchasedItemList.set(position, newItem);
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

        // Create edit popup for purchased item(more fields to edit)
        public void createDialogEdit() {
            dialogBuilder = new AlertDialog.Builder(context);
            final View popupView = LayoutInflater.from(context).inflate(R.layout.edit_item_popup, null);
            listNameView = (EditText) popupView.findViewById( R.id.itemNameView );
            costView = (EditText) popupView.findViewById( R.id.costView );
            quantityView = (EditText) popupView.findViewById( R.id.quantityView );
            roommateView = (EditText) popupView.findViewById( R.id.roommateView );

            // Populate fields with old values
            listNameView.setText(itemList.get(getAdapterPosition()).getName());
            costView.setText(String.valueOf(itemList.get(getAdapterPosition()).getCost()));
            quantityView.setText(String.valueOf(itemList.get(getAdapterPosition()).getQuantity()));
            roommateView.setText(itemList.get(getAdapterPosition()).getRoommate());

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
                            // Check if valid fields
                            if ( TextUtils.isEmpty(listNameView.getText().toString()) ) {
                                Toast.makeText(context, "Please enter an item name. Try again. ",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else if ( TextUtils.isEmpty(roommateView.getText().toString()) ) {
                                Toast.makeText(context, "Please enter a roommate name. Try again. ",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else if ( TextUtils.isEmpty(costView.getText().toString()) ) {
                                Toast.makeText(context, "Please enter a cost. Try again. ",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else if ( TextUtils.isEmpty(quantityView.getText().toString()) ) {
                                Toast.makeText(context, "Please enter a quantity. Try again. ",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String itemName = listNameView.getText().toString();
                            String roommateName = roommateView.getText().toString();
                            double cost = Double.parseDouble(costView.getText().toString());
                            int quantity = Integer.parseInt(quantityView.getText().toString());
                            boolean purchased = itemList.get(getAdapterPosition()).isPurchased();
                            editItem(getAdapterPosition(), itemName, cost, purchased, quantity, roommateName);
                        }
                    });

            dialogBuilder.setView(popupView);
            dialog = dialogBuilder.create();
            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // Edit for purchased item
        public void editItem(final int position, String name, double cost, boolean purchased, int quantity, String roommate) {
            // update the database with new itemsList and remove from firebase database
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("ShoppingLists").child(ItemsActivity.shoppingTitle);

            Map<String, Object> shoppingListItemUpdate = new HashMap<>();
            // Create a new item and replace it
            final Item newItem = new Item(name, cost, purchased, quantity, roommate);

            ItemsActivity.itemList.set(ItemsActivity.itemList.indexOf(itemList.get(position)), newItem); // Replace in main list

            shoppingListItemUpdate.put("Items", ItemsActivity.itemList);

            myRef.updateChildren( shoppingListItemUpdate )
                    .addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Show a quick confirmation
                            Toast.makeText(context, "Item updated",
                                    Toast.LENGTH_SHORT).show();
                            ItemsActivity.purchasedItemList.set(position, newItem);
                            ItemsActivity.recyclerAdapter2.notifyItemChanged(position); // Notify recycleadapter in purchased
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

        // When a cardview is clicked, this popup is created for the user to fill in the cost, quantity, and who bought it
        public void createDialogPurchase() {
            dialogBuilder = new AlertDialog.Builder(context);
            final View popupView = LayoutInflater.from(context).inflate(R.layout.purchase_item_popup, null);
            costView = (EditText) popupView.findViewById( R.id.costView );
            quantityView = (EditText) popupView.findViewById( R.id.quantityView );
            roommateView = (EditText) popupView.findViewById( R.id.roommateView );

            dialogBuilder.setNegativeButton(
                    "cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cardView.setChecked(!cardView.isChecked());
                            dialog.dismiss();
                        }
                    }
            );

            dialogBuilder.setPositiveButton(
                    "Submit",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if ( TextUtils.isEmpty(roommateView.getText().toString()) ) {
                                Toast.makeText(context, "Please enter a roommate name. Try again. ",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else if ( TextUtils.isEmpty(costView.getText().toString()) ) {
                                Toast.makeText(context, "Please enter a cost. Try again. ",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else if ( TextUtils.isEmpty(quantityView.getText().toString()) ) {
                                Toast.makeText(context, "Please enter a quantity. Try again. ",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String itemName = itemList.get(getAdapterPosition()).getName();
                            String roommateName = roommateView.getText().toString();
                            double cost = Double.parseDouble(costView.getText().toString());
                            int quantity = Integer.parseInt(quantityView.getText().toString());
                            // Check if valid fields
                            setPurchased(getAdapterPosition(), itemName, true, cost, quantity, roommateName);
                        }
                    });

            dialogBuilder.setView(popupView);
            dialog = dialogBuilder.create();
            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // When user submits values of what is checked/purchased then replace item in list with updated item and update children on db
        public void setPurchased(final int position, String name, boolean purchased, double cost, int quantity, String roommate) {
            // update the database with new itemsList and remove from firebase database
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("ShoppingLists").child(ItemsActivity.shoppingTitle);

            Map<String, Object> shoppingListItemUpdate = new HashMap<>();
            // Create a new item and replace it
            final Item item = new Item(name, cost, purchased, quantity, roommate);
            // update the main item list
            ItemsActivity.itemList.set(ItemsActivity.itemList.indexOf(itemList.get(position)), item); // Index in overarching items is different from index in unpurchased itemList

            shoppingListItemUpdate.put("Items",  ItemsActivity.itemList);

            myRef.updateChildren( shoppingListItemUpdate )
                    .addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Show a quick confirmation
                            Toast.makeText(context, "Item moved to purchased",
                                    Toast.LENGTH_SHORT).show();
                            cardView.setChecked(!cardView.isChecked());
                            ItemsActivity.purchasedItemList.add(item);
                            ItemsActivity.nonPurchasedItemList.remove(getAdapterPosition());
                            ItemsActivity.recyclerAdapter.notifyItemRemoved(getAdapterPosition());
                            ItemsActivity.recyclerAdapter2.notifyItemInserted(ItemsActivity.purchasedItemList.size());
                            Log.d(DEBUG_TAG, "NONPURCHASED LIST SIZE: " +  itemList.size());

                        }
                    })
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Log.d(DEBUG_TAG, "setPurchased: can't update item");
                            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // When user unchecks a cardview then remove all values but name and update list/db
        public void setUnpurchased(final int position, String name) {
            // update the database with new itemsList and remove from firebase database
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("ShoppingLists").child(ItemsActivity.shoppingTitle);

            Map<String, Object> shoppingListItemUpdate = new HashMap<>();
            // Create a new item and replace it
            final Item item = new Item(name);

            // update the main item list
            ItemsActivity.itemList.set(ItemsActivity.itemList.indexOf(itemList.get(position)), item);

            shoppingListItemUpdate.put("Items", ItemsActivity.itemList);

            myRef.updateChildren(shoppingListItemUpdate)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Show a quick confirmation
                            Toast.makeText(context, "Item moved to unpurchased",
                                    Toast.LENGTH_SHORT).show();
                            cardView.setChecked(!cardView.isChecked());
                            ItemsActivity.nonPurchasedItemList.add(item); // Add item to other list
                            ItemsActivity.purchasedItemList.remove(getAdapterPosition()); // Remove from current list
                            ItemsActivity.recyclerAdapter2.notifyItemRemoved(getAdapterPosition()); // Notify respective adapters
                            ItemsActivity.recyclerAdapter.notifyItemInserted(ItemsActivity.nonPurchasedItemList.size());
                            Log.d(DEBUG_TAG, "PURCHASED LIST SIZE: " +  itemList.size());

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
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

        holder.title.setText(String.valueOf(item.getName()));
        holder.cost.setText(String.valueOf(item.getName()));
        holder.cost.setText(String.valueOf(item.getCost()) + " x" + String.valueOf(item.getQuantity()));
        if (String.valueOf(item.getRoommate()) == "null") {
            holder.roommate.setVisibility(View.GONE); // On creation, don't show who purchased if it hasn't been purchased
        }
        else {
            holder.roommate.setVisibility(View.VISIBLE);
            holder.roommate.setText(String.valueOf(item.getRoommate()));
        }
        if (item.isPurchased()) {
            Log.d(DEBUG_TAG, "item isPurchased: TRUE");
            holder.cardView.setChecked(true); // Set checked initially if item has been purchased
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


}
