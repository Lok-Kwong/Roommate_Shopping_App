package edu.uga.cs.roommateshoppingapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ItemsActivity extends AppCompatActivity {

    public static final String DEBUG_TAG = "ItemsActivity";

    private TextView titleView;
    private Button calculateTotalBtn;
    private TextView recentlyPurchasedView;
    private View divider;
    private FloatingActionButton fabNewPost;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

//      textview.setVisibility(textview.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        recentlyPurchasedView = (TextView) findViewById(R.id.textView6);
        recentlyPurchasedView.setVisibility(View.GONE);
        divider = (View) findViewById(R.id.divider);
        divider.setVisibility(View.GONE);
        
        Intent intent = getIntent();
        titleView = (TextView) findViewById(R.id.textView5);
        titleView.setText(intent.getStringExtra("Title"));
    }
}