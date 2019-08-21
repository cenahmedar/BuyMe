package com.cenah.buyme.viewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cenah.buyme.R;
import com.cenah.buyme.interfaces.ItemClickListner;

public class ProductsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tx_name,tx_about;
    public ImageView iv_image,share_image;
    public ImageView fav_image;
    private ItemClickListner itemClickListner;


    public ProductsViewHolder(@NonNull View itemView) {
        super(itemView);

        tx_name = itemView.findViewById(R.id.tx_name);
        tx_about = itemView.findViewById(R.id.tx_about);
        iv_image = itemView.findViewById(R.id.iv_image);
        fav_image = itemView.findViewById(R.id.fav);
        share_image = itemView.findViewById(R.id.share);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v,getAdapterPosition(),false);

    }

    public void setItemClickListner(ItemClickListner itemClickListner){
        this.itemClickListner = itemClickListner;
    }
}
