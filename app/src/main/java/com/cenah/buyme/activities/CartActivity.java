package com.cenah.buyme.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cenah.buyme.Models.Common;
import com.cenah.buyme.Models.Order;
import com.cenah.buyme.Models.Request;
import com.cenah.buyme.R;
import com.cenah.buyme.database.Database;
import com.cenah.buyme.viewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tx_total;
    private DatabaseReference requests;
    private List<Order> cart = new ArrayList<>();


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/AbrilFatface-Regular.otf").setFontAttrId(R.attr.fontPath).build());

        //init firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = findViewById(R.id.recyclerView);
        tx_total = findViewById(R.id.tx_total);
        Button btn_order = findViewById(R.id.btn_order);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        LoadList();

        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cart.size() > 0)
                    ShowAlertDialog();

            }
        });


    }

    private void ShowAlertDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
        alertDialog.setTitle("Last step!");
        alertDialog.setMessage("Enter your address:  ");

        final EditText editText = new EditText(CartActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        editText.setLayoutParams(layoutParams);
        alertDialog.setView(editText);
        alertDialog.setIcon(R.drawable.shop_black);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Request request = new Request(Common.currnetUser.getPhone(),
                        Common.currnetUser.getName(),
                        editText.getText().toString().trim(),
                        tx_total.getText().toString(),
                        cart);

                requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);

                new Database(getBaseContext()).cleanCart();
                Toast.makeText(getBaseContext(), "thanks..", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();


    }

    private void LoadList() {
        cart = new Database(getApplicationContext()).getCarts();
        CartAdapter adapter = new CartAdapter(cart, getApplicationContext());
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //calculate total price
        int total = 0;
        for (Order order : cart)
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));

        Locale locale = new Locale("en", "US");
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);

        tx_total.setText(format.format(total));


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE)) {
            deleteChart(item.getOrder());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteChart(int order) {
        cart.remove(order);
        new Database(this).cleanCart();
        for (Order item : cart) {
            new Database(this).addToCard(item);
        }
        LoadList();

    }
}
