package com.alhikmahpro.www.e_pos.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.ItemsModel;
import com.alhikmahpro.www.e_pos.interfaces.OnAdapterItemClickListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {

    List<ItemsModel> arrayList = new ArrayList<>();
    OnAdapterItemClickListener onItemAdapterClickListener;

    public ItemAdapter(List<ItemsModel> arrayList, OnAdapterItemClickListener onItemAdapterClickListener) {

        this.arrayList = arrayList;
        this.onItemAdapterClickListener = onItemAdapterClickListener;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_row, parent, false);
        ItemHolder itemHolder = new ItemHolder(view);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, final int position) {

        ItemsModel itemsModel = arrayList.get(position);
        holder.rvName.setText(itemsModel.getItemName());
        holder.rvItemDescription.setText(itemsModel.getItemCode());
        holder.rvPrice.setText(CurrencyFormatter(itemsModel.getItemPrice()));
        byte[]image=itemsModel.getItemImage();
        Bitmap bitmap= BitmapFactory.decodeByteArray(image,0,image.length);
        holder.rvImg.setImageBitmap(Bitmap.createScaledBitmap(bitmap,50,50,false));

        holder.rvLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemAdapterClickListener.OnItemClicked(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rv_img)
        ImageView rvImg;
        @BindView(R.id.rv_name)
        TextView rvName;
        @BindView(R.id.rv_item_description)
        TextView rvItemDescription;
        @BindView(R.id.rv_price)
        TextView rvPrice;
        @BindView(R.id.rv_layout)
        LinearLayout rvLayout;


        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


        }
    }

    public void setFilter(ArrayList<ItemsModel>newList){
        arrayList=new ArrayList<>();
        arrayList.addAll(newList);
        notifyDataSetChanged();

    }

    public String CurrencyFormatter(double val){
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        String pattern = ((DecimalFormat) nf).toPattern();
        String newPattern = pattern.replace("\u00A4", "").trim();
        NumberFormat newFormat = new DecimalFormat(newPattern);

        String ft=String.valueOf(newFormat.format(val));
        return  ft;

    }

}
