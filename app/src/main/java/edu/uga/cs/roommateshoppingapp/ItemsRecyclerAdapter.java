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
//            if (itemList != null && itemList.get(getAdapterPosition()).isPurchased()) {
//                cardView.setChecked(true);
//            }
            cardView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    cardView.setChecked(!cardView.isChecked());
                    // Is purchased
                    if(cardView.isChecked()) {
                        // update db then remove from recycle
                        createDialogPurchase();
                    }
                    // Item is unchecked/not purchased
                    else {
                        setUnpurchased(getAdapterPosition(), itemList.get(getAdapterPosition()).getName());
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
                    createDialogEdit();
                }
            });
        }

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

        // DELETE AND EDIT NOT WORKING BC OF ITEMLIST IS SEP
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

                            Log.d( DEBUG_TAG, "itemList size " + ItemsActivity.itemList.size());
                            Log.d( DEBUG_TAG, "purchasedItemList size " + ItemsActivity.purchasedItemList.size());
                            Log.d( DEBUG_TAG, "nonPurchasedItemList size " + ItemsActivity.nonPurchasedItemList.size());
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

        public void createDialogEdit() {
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

        public void editItem(final int position, String name, double cost, int quantity, String roommate) {
            // update the database with new itemsList and remove from firebase database
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("ShoppingLists").child(ItemsActivity.shoppingTitle);

            Map<String, Object> shoppingListItemUpdate = new HashMap<>();
            // Create a new item and replace it
            Item item = new Item(name, cost, quantity, roommate);



            ItemsActivity.itemList.set(ItemsActivity.itemList.indexOf(itemList.get(position)), item);




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
                            String itemName = itemList.get(getAdapterPosition()).getName();
                            String roommateName = roommateView.getText().toString();
                            double cost = Double.parseDouble(costView.getText().toString());
                            int quantity = Integer.parseInt(quantityView.getText().toString());
                            // Check if valid fields
                            if (TextUtils.isEmpty(roommateName) || TextUtils.isEmpty(costView.getText().toString()) || TextUtils.isEmpty(quantityView.getText().toString())) {
                                Toast.makeText(context, "Fill in all the texts!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            setPurchased(getAdapterPosition(), itemName, true, cost, quantity, roommateName);
                        }
                    });

            dialogBuilder.setView(popupView);
            dialog = dialogBuilder.create();
            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        public void setPurchased(final int position, String name, boolean purchased, double cost, int quantity, String roommate) {
            // update the database with new itemsList and remove from firebase database
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("ShoppingLists").child(ItemsActivity.shoppingTitle);

            Map<String, Object> shoppingListItemUpdate = new HashMap<>();
            // Create a new item and replace it
            final Item item = new Item(name, cost, purchased, quantity, roommate);
            // update the main item list
            Log.d(DEBUG_TAG, "setPurchased: pos " + position);
            ItemsActivity.itemList.set(ItemsActivity.itemList.indexOf(itemList.get(position)), item); // Index in overarching items is different from index in unpurchased itemList

            shoppingListItemUpdate.put("Items",  ItemsActivity.itemList);

            myRef.updateChildren( shoppingListItemUpdate )
                    .addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Show a quick confirmation
                            Toast.makeText(context, "Item moved to purchased",
                                    Toast.LENGTH_SHORT).show();
                            ItemsActivity.purchasedItemList.add(item);
                            ItemsActivity.nonPurchasedItemList.remove(getAdapterPosition());
                            ItemsActivity.recyclerAdapter.notifyItemRemoved(getAdapterPosition());
                            ItemsActivity.recyclerAdapter2.notifyItemInserted(ItemsActivity.purchasedItemList.size());

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
                            ItemsActivity.nonPurchasedItemList.add(item); // Add item to other list
                            ItemsActivity.purchasedItemList.remove(getAdapterPosition()); // Remove from current list
                            ItemsActivity.recyclerAdapter2.notifyItemRemoved(getAdapterPosition()); // Notify respective adapters
                            ItemsActivity.recyclerAdapter.notifyItemInserted(ItemsActivity.nonPurchasedItemList.size());
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
