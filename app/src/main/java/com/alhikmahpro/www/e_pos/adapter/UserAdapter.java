package com.alhikmahpro.www.e_pos.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.ItemsModel;
import com.alhikmahpro.www.e_pos.data.UserModel;
import com.alhikmahpro.www.e_pos.interfaces.OnAdapterItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>  implements Filterable{

    List<UserModel>userModelList=new ArrayList<>();
    List<UserModel>userModelListFull=new ArrayList<>();
    OnAdapterItemClickListener adapterItemClickListener;

    public UserAdapter(List<UserModel> userModelList, OnAdapterItemClickListener adapterItemClickListener) {
        this.userModelList = userModelList;
        this.adapterItemClickListener = adapterItemClickListener;
        userModelListFull=new ArrayList<>(userModelList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_rv_row, parent, false);
        UserAdapter.ViewHolder holder = new UserAdapter.ViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        UserModel userModel=userModelList.get(position);
        holder.rvCategory.setText(userModel.getName());
        holder.rvId.setText(userModel.getCode());
        holder.rvLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterItemClickListener.OnItemClicked(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }


    public void filterList(ArrayList<UserModel>filteredList){
        userModelList=filteredList;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return filterUserList;
    }


    private Filter filterUserList=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<UserModel> filteredList = new ArrayList<>();
            if (constraint == null || (constraint.length() == 0)) {
                filteredList.addAll(userModelListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (UserModel model : userModelListFull) {
                    if (model.getName().toLowerCase().contains(filterPattern)) {
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

            userModelList.clear();
            userModelList.addAll((List) results.values);
            notifyDataSetChanged();

        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.rv_category)
        TextView rvCategory;
        @BindView(R.id.rv_id)
        TextView rvId;
        @BindView(R.id.rv_layout)
        LinearLayout rvLayout;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }




}
