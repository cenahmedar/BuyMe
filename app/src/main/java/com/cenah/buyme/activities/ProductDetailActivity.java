package com.cenah.buyme.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cenah.buyme.Models.Common;
import com.cenah.buyme.Models.Order;
import com.cenah.buyme.Models.Products;
import com.cenah.buyme.Models.Rating;
import com.cenah.buyme.R;
import com.cenah.buyme.database.Database;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductDetailActivity extends AppCompatActivity implements RatingDialogListener {

    private TextView tx_product_name, tx_product_price, product_info;
    private ImageView prdouct_image;
    private ElegantNumberButton number_btton;

    private String prodId;

    private DatabaseReference products;
    private DatabaseReference ratingTbl;
    private Products model;

    private RatingBar ratingBar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/AbrilFatface-Regular.otf").setFontAttrId(R.attr.fontPath).build());


        tx_product_name = findViewById(R.id.tx_product_name);
        tx_product_price = findViewById(R.id.tx_product_price);
        product_info = findViewById(R.id.product_info);
        prdouct_image = findViewById(R.id.prdouct_image);
        FloatingActionButton btnCart = findViewById(R.id.btnCart);
        FloatingActionButton btnRating = findViewById(R.id.btnRating);
        number_btton = findViewById(R.id.number_btton);
        ratingBar = findViewById(R.id.ratingBar);


        //init firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        products = database.getReference("Products");
        ratingTbl = database.getReference("Rating");

        if (getIntent() != null) prodId = getIntent().getStringExtra("prodId");
        if (prodId != null && !prodId.isEmpty()) {
            getProduct(prodId);
            getProductRating(prodId);
        }

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Database(getApplicationContext()).addToCard(new Order(prodId, model.getName(), number_btton.getNumber(), model.getPrice(), model.getDiscount(), model.getImage()));
                Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_LONG).show();
            }
        });

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

    }

    private void getProductRating(String prodId) {

        Query productRating = ratingTbl.orderByChild("productID").equalTo(prodId);

        productRating.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0, sum = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    if (item != null) {
                        sum += Integer.parseInt(item.getRateValue());
                        count++;
                    }

                }
                if (count != 0) {
                    float average = sum / count;
                    ratingBar.setRating(average);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("SAVE")
                .setNegativeButtonText("CANCEL")
                .setNoteDescriptions(Arrays.asList("very bad", "bad", "not bad", "good", "very good"))
                .setDefaultRating(1)
                .setTitle("Stars")
                .setDescription("Please share your thoughts")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Comment..")
                .setHintTextColor(R.color.whitecustom)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .create(ProductDetailActivity.this)
                .show();
    }

    private void getProduct(String prodId) {

        products.child(prodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                model = dataSnapshot.getValue(Products.class);
                Picasso.get().load(model.getImage()).into(prdouct_image);

                tx_product_name.setText(model.getName());
                product_info.setText(model.getDescription());
                tx_product_price.setText(model.getPrice());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int value, @NotNull String comment) {

        final Rating rating = new Rating(Common.currnetUser.getPhone(),
                prodId,
                String.valueOf(value),
                comment);

        ratingTbl.child(Common.currnetUser.getPhone()).child(prodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Common.currnetUser.getPhone()).exists()) {
                    //remove old value
                    ratingTbl.child(Common.currnetUser.getPhone()).removeValue();

                    // update new value
                    ratingTbl.child(Common.currnetUser.getPhone()).setValue(rating);
                    Toast.makeText(ProductDetailActivity.this,"Thank you for Feedback", Toast.LENGTH_SHORT).show();

                } else {
                    ratingTbl.child(Common.currnetUser.getPhone()).setValue(rating);
                    Toast.makeText(ProductDetailActivity.this,"Thank you for Feedback", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
