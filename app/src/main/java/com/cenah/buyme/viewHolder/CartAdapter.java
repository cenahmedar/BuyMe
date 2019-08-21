package com.cenah.buyme.viewHolder;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cenah.buyme.Models.Common;
import com.cenah.buyme.Models.Order;
import com.cenah.buyme.R;
import com.cenah.buyme.interfaces.ItemClickListner;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    public ImageView im_image;
    public TextView tx_cart_name, tx_cart_price, im_count;
    private ItemClickListner itemClickListner;


    public void setTx_cart_name(TextView tx_cart_name) {
        this.tx_cart_name = tx_cart_name;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        im_count = itemView.findViewById(R.id.im_count);
        im_image = itemView.findViewById(R.id.im_image);
        tx_cart_name = itemView.findViewById(R.id.tx_cart_name);
        tx_cart_price = itemView.findViewById(R.id.tx_cart_price);

        itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v, getAdapterPosition(), false);

    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select action");
        menu.add(0, 0, getAdapterPosition(), Common.DELETE);
    }
}


public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    private List<Order> list = new ArrayList<>();
    private Context context;


    public CartAdapter(List<Order> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_item, viewGroup, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i) {

        Locale locale = new Locale("en", "US");
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);

        int price = (Integer.parseInt(list.get(i).getPrice()) * Integer.parseInt(list.get(i).getQuantity()));
        cartViewHolder.tx_cart_price.setText(format.format(price));

        cartViewHolder.tx_cart_name.setText(list.get(i).getProductName());
        cartViewHolder.im_count.setText(list.get(i).getQuantity());

        Picasso.get().load(list.get(i).getImage()).into(cartViewHolder.im_image);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
