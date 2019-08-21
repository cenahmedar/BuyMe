package com.cenah.buyme.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.cenah.buyme.Models.Products;
import com.cenah.buyme.R;
import com.cenah.buyme.database.Database;
import com.cenah.buyme.interfaces.ItemClickListner;
import com.cenah.buyme.viewHolder.ProductsViewHolder;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private DatabaseReference products;
    private String categoryId;
    private FirebaseRecyclerAdapter<Products, ProductsViewHolder> adapter;

    private MaterialSearchBar searchBar;
    private FirebaseRecyclerAdapter<Products, ProductsViewHolder> searchAdapter;
    private List<String> suggestList = new ArrayList<>();
    private Database localDB;

    private ShareDialog shareDialog;

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //create photo for Bitmap
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap).build();
            if (ShareDialog.canShow(SharePhotoContent.class)) {
                SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();

                shareDialog.show(content);
            }

        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/AbrilFatface-Regular.otf").setFontAttrId(R.attr.fontPath).build());

        //init facebook
        //facebook share
        CallbackManager callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        //init firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        products = database.getReference("Products");

        localDB = new Database(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (getIntent() != null) categoryId = getIntent().getStringExtra("CategoryId");
        if (categoryId != null && !categoryId.isEmpty()) LoadList(categoryId);

        searchBar = findViewById(R.id.searchBar);
        searchBar.setHint("ürün ara");
        searchBar.setSpeechMode(false);

        loadSuggested();
        searchBar.setLastSuggestions(suggestList);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                searchBar.setLastSuggestions(suggest);
                searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                    @Override
                    public void onSearchStateChanged(boolean enabled) {
                        //when search bar close restore original

                        if (!enabled) {
                            recyclerView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onSearchConfirmed(CharSequence text) {
                        startSearch(text);
                    }

                    @Override
                    public void onButtonClicked(int buttonCode) {

                    }
                });
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<Products, ProductsViewHolder>(Products.class,
                R.layout.product_item,
                ProductsViewHolder.class,
                products.orderByChild("name").equalTo(text.toString())) { //select * from products where menuId  =
            @Override
            protected void populateViewHolder(ProductsViewHolder viewHolder, Products model, final int position) {
                viewHolder.tx_name.setText(model.getName().trim());
                viewHolder.tx_about.setText(model.getDescription().trim());
                Picasso.get().load(model.getImage()).into(viewHolder.iv_image);

                final Products local = model;

                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int pos, boolean isLongClick) {
                        Intent intent = new Intent(ProductsActivity.this, ProductDetailActivity.class);
                        intent.putExtra("FoodId", searchAdapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });

            }
        };
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggested() {
        products.orderByChild("menuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnap : dataSnapshot.getChildren()) {

                    Products item = postSnap.getValue(Products.class);
                    suggestList.add(item.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void LoadList(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Products, ProductsViewHolder>(Products.class,
                R.layout.product_item,
                ProductsViewHolder.class,
                products.orderByChild("menuId").equalTo(categoryId)) { //select * from products where menuId  =
            @Override
            protected void populateViewHolder(final ProductsViewHolder viewHolder, final Products model, final int position) {
                viewHolder.tx_name.setText(model.getName().trim());
                viewHolder.tx_about.setText(model.getDescription().trim());
                Picasso.get().load(model.getImage()).into(viewHolder.iv_image);


                if (localDB.isFavorite(adapter.getRef(position).getKey()))
                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_red_24dp);

                viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!localDB.isFavorite(adapter.getRef(position).getKey())) {
                            localDB.addToFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_red_24dp);
                        } else {
                            localDB.removeFromFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        }
                    }
                });

                final Products local = model;

                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int pos, boolean isLongClick) {
                        String temp = adapter.getRef(position).getKey();
                        Intent intent = new Intent(ProductsActivity.this, ProductDetailActivity.class);
                        intent.putExtra("prodId", adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });

                viewHolder.share_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.get().load(model.getImage()).into(target);
                    }
                });


            }
        };

        recyclerView.setAdapter(adapter);


    }
}
