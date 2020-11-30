package edu.uga.cs.roommateshoppingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

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
                            deleteItem();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                    builder.setMessage("Are you sure you want to delete this?")
                            .setTitle("Update item");

                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User DELETES shopping list
                            editItem();
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

        public void deleteItem() {
            // query the database to determine the key and remove from firebase database
//            final String shoppingTitle = title.getText().toString();
//            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
//            Query query = myRef.child("ShoppingLists").orderByChild("shoppingListName").equalTo(shoppingTitle);
//
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                        Log.d(DEBUG_TAG, "deleteShoppingList: in OnDataChange");
//                        postSnapshot.getRef().removeValue()
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        // Show a quick confirmation
//                                        Log.d(DEBUG_TAG, "deleteShoppingList: deleted " + getAdapterPosition());
//                                        Toast.makeText(itemView.getContext(), "Deleted " + shoppingTitle, Toast.LENGTH_SHORT).show();
//                                        // Hacky solution to dynamically remove cardview in the ShoppingList Activity
//                                        ShoppingListActivity.shoppingLists.remove(getAdapterPosition()); // Remove from shopping list
//                                        ShoppingListActivity.recyclerAdapter.notifyItemRemoved(getAdapterPosition()); // Notify recycle adapter
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(Exception e) {
//                                        Log.d(DEBUG_TAG, "deleteShoppingList: cant delete");
//                                        Toast.makeText(itemView.getContext(), "Failed to delete " + shoppingTitle, Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                    }
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.e(DEBUG_TAG, "onCancelled", databaseError.toException());
//                }
//            });
        }

        public void editItem() {

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
        holder.roommate.setText(String.valueOf(item.getRoommate()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


}
