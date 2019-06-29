package com.alhikmahpro.www.e_pos.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.ItemsModel;
import com.alhikmahpro.www.e_pos.interfaces.OnAdapterClickListener;
import com.alhikmahpro.www.e_pos.interfaces.OnAdapterItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class  CategoryAdapter extends RecyclerView.Adapter <CategoryAdapter.ItemHolder>{

    List<ItemsModel> arrayList = new ArrayList<>();
    OnAdapterItemClickListener adapterItemClickListener;

    public CategoryAdapter(List<ItemsModel> arrayList, OnAdapterItemClickListener adapterItemClickListener) {
        this.arrayList = arrayList;
        this.adapterItemClickListener = adapterItemClickListener;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_rv_row, parent, false);
        ItemHolder itemHolder = new ItemHolder(view);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, final int position) {

        ItemsModel itemsModel=arrayList.get(position);
        holder.rvCategory.setText(itemsModel.getCategoryName());
       // holder.rvId.setText(String.valueOf(itemsModel.getCategoryId()));
        holder.rvLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterItemClickListener.OnItemClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rv_category)
        TextView rvCategory;
        @BindView(R.id.rv_id)
        TextView rvId;
        @BindView(R.id.rv_layout)
        LinearLayout rvLayout;


        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);


        }
    }

//    static class ItemHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.rv_category)
//        TextView rvCategory;
//        @BindView(R.id.rv_id)
//        TextView rvId;
//        @BindView(R.id.rv_layout)
//        LinearLayout rvLayout;
//
//
//}
}
