package com.alhikmahpro.www.e_pos.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.CartData;
import com.alhikmahpro.www.e_pos.data.ItemsModel;
import com.alhikmahpro.www.e_pos.interfaces.OnAdapterClickListener;
import com.alhikmahpro.www.e_pos.interfaces.OnAdapterItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

   OnAdapterClickListener adapterClickListener;

    public CartAdapter(OnAdapterClickListener adapterClickListener) {
        this.adapterClickListener = adapterClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_row, parent, false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ItemsModel itemsModel = CartData.mCartData.get(position);

        holder.rvItemName.setText(itemsModel.getItemName());
        holder.rvItemPrice.setText(String.valueOf(itemsModel.getItemPrice()));
        holder.txtQuantity.setText(String.valueOf(itemsModel.getQuantity()));
        String code=itemsModel.getCategoryName();
        Log.d("Adapter","category:"+code);
        holder.rvLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterClickListener.OnItemClicked(position);

            }
        });

        holder.rvImgDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterClickListener.OnDeleteClicked(position);
            }
        });








    }

    @Override
    public int getItemCount() {
        return CartData.mCartData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.rv_item_name)
        TextView rvItemName;
        @BindView(R.id.rv_item_price)
        TextView rvItemPrice;

        @BindView(R.id.txt_quantity)
        TextView txtQuantity;

        @BindView(R.id.rv_img_del)
        ImageView rvImgDel;
        @BindView(R.id.rv_layout)
        LinearLayout rvLayout;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
