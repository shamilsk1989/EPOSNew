package com.alhikmahpro.www.e_pos.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.CartData;
import com.alhikmahpro.www.e_pos.data.ItemsModel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditInvoiceAdapter extends RecyclerView.Adapter<EditInvoiceAdapter.ItemHolder> {




    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invice_item_rv, parent, false);
        ItemHolder itemHolder = new ItemHolder(view);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {



        ItemsModel model = CartData.mCartData.get(position);
        holder.tvDescription.setText(model.getItemName());
        holder.tvQty.setText(String.valueOf(model.getQuantity()));
        holder.tvPrice.setText(String.valueOf(model.getItemPrice()));
        double tot=model.getQuantity() * model.getItemPrice();
        String total=CurrencyFormatter(tot);
        holder.tvTotal.setText(total);

    }

    @Override
    public int getItemCount() {
        return CartData.mCartData.size();
    }



    static class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_description)
        TextView tvDescription;
        @BindView(R.id.tv_qty)
        TextView tvQty;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_Total)
        TextView tvTotal;


        ItemHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public String CurrencyFormatter(double val){
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        String pattern = ((DecimalFormat) nf).toPattern();
        String newPattern = pattern.replace("\u00A4", "").trim();
        NumberFormat newFormat = new DecimalFormat(newPattern);

        String formattedVal=String.valueOf(newFormat.format(val));
        return formattedVal;

    }



}
