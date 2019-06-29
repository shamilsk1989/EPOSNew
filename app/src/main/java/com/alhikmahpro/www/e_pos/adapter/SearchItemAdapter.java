package com.alhikmahpro.www.e_pos.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.ItemsModel;
import com.alhikmahpro.www.e_pos.interfaces.OnAdapterItemClickListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchItemAdapter extends RecyclerView.Adapter<SearchItemAdapter.ItemHolder> implements Filterable {
    List<ItemsModel> arrayList = new ArrayList<>();
    List<ItemsModel> arrayListFull = new ArrayList<>();
    OnAdapterItemClickListener onItemAdapterClickListener;

    public SearchItemAdapter(List<ItemsModel> arrayList, OnAdapterItemClickListener onItemAdapterClickListener) {
        this.arrayList = arrayList;
        arrayListFull = new ArrayList<>(arrayList);
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

        final ItemsModel currentItem = arrayList.get(position);
        holder.rvName.setText(currentItem.getItemName());
        holder.rvItemDescription.setText(currentItem.getItemCode());
        holder.rvPrice.setText(CurrencyFormatter(currentItem.getItemPrice()));
        byte[] image = currentItem.getItemImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        holder.rvImg.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 50, 50, false));
        holder.rvLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Toast.makeText(v.getContext(),currentItem.getItemName(),Toast.LENGTH_LONG).show();
                onItemAdapterClickListener.OnItemClicked(position);

            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public Filter getFilter() {
        return arrayFilter;
    }


    private Filter arrayFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<ItemsModel> filteredList = new ArrayList<>();
            if (constraint == null || (constraint.length() == 0)) {
                filteredList.addAll(arrayListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ItemsModel model : arrayListFull) {
                    if (model.getItemName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(model);
                    }
                }

            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList.clear();
            arrayList.addAll((List) results.values);
            notifyDataSetChanged();

        }
    };

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

    public String CurrencyFormatter(double val) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        String pattern = ((DecimalFormat) nf).toPattern();
        String newPattern = pattern.replace("\u00A4", "").trim();
        NumberFormat newFormat = new DecimalFormat(newPattern);

        String ft = String.valueOf(newFormat.format(val));
        return ft;

    }


}
