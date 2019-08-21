package com.cenah.buyme.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cenah.buyme.Models.Common;
import com.cenah.buyme.Models.Request;
import com.cenah.buyme.R;
import com.cenah.buyme.interfaces.ItemClickListner;
import com.cenah.buyme.viewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderStatusActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private DatabaseReference requests;

    private FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/AbrilFatface-Regular.otf").setFontAttrId(R.attr.fontPath).build());

        //init firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = findViewById(R.id.recyclerView);


        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(getIntent().getStringExtra("userPhone") == null)
            laodOrderlist(Common.currnetUser.getPhone());
        else
            laodOrderlist(getIntent().getStringExtra("userPhone"));





    }

    private void laodOrderlist(String phone) {

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(Request.class, R.layout.order_item, OrderViewHolder.class, requests.orderByChild("phone").equalTo(phone)) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.tx_orderid.setText(adapter.getRef(position).getKey());
                viewHolder.tx_order_adress.setText(model.getAddress());
                viewHolder.tx_order_phone.setText(model.getPhone());
                viewHolder.tx_order_statu.setText(convertCodeToSatus(model.getStatus()));

                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int pos, boolean isLongClick) {

                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private String convertCodeToSatus(String status) {

        switch (status) {
            case "0":
                return "was given";
            case "1":
                return "on the way";
            default:
                return "shipped";
        }

    }
}
