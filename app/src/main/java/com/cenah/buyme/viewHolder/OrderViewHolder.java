package com.cenah.buyme.viewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cenah.buyme.R;
import com.cenah.buyme.interfaces.ItemClickListner;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tx_orderid,tx_order_statu,tx_order_phone,tx_order_adress;

    private ItemClickListner itemClickListner;


    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        tx_order_adress = itemView.findViewById(R.id.tx_order_adress);
        tx_order_phone = itemView.findViewById(R.id.tx_order_phone);
        tx_order_statu = itemView.findViewById(R.id.tx_order_statu);
        tx_orderid = itemView.findViewById(R.id.tx_orderid);

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
