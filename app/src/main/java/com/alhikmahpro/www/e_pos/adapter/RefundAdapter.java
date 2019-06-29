package com.alhikmahpro.www.e_pos.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.CartData;
import com.alhikmahpro.www.e_pos.data.ItemsModel;
import com.alhikmahpro.www.e_pos.interfaces.OnRefundAdapterClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RefundAdapter extends RecyclerView.Adapter<RefundAdapter.ViewHolder> {
    OnRefundAdapterClickListener adapterItemClickListener;




    public RefundAdapter(OnRefundAdapterClickListener adapterItemClickListener) {
        this.adapterItemClickListener = adapterItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.refund_rv_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final ItemsModel itemsModel = CartData.mCartData.get(position);
        //check the refund count & set the visibility
        int refunded_count = itemsModel.getRefundCount() ;
        int refund_count=+ itemsModel.getTempRefundCounter();
        Log.d("REFUND ADAPTER", "getRefundCount:" + itemsModel.getRefundCount() + "getTempRefundCounter:" + itemsModel.getTempRefundCounter());

        if(refunded_count>0){
            holder.refundedQty.setText(String.valueOf(refunded_count));
        }
        else{
            holder.rvTxtRefunded.setVisibility(View.GONE);
            holder.refundedQty.setVisibility(View.GONE);
            holder.txt1.setVisibility(View.GONE);
        }


        if (refund_count > 0) {

            holder.refundQty.setText(String.valueOf(refund_count));
        } else {
            holder.refundQty.setVisibility(View.GONE);
            holder.rvTxtRefund.setVisibility(View.GONE);
            holder.txt.setVisibility(View.GONE);
        }

        if (itemsModel.getQuantity() == itemsModel.getRefundCount()) {
            Log.d("REFUND ADAPTER", "inside if ");
            holder.chkBox.setEnabled(false);

        }


        holder.tvItem.setText(itemsModel.getItemName());
        holder.tvPrice.setText(String.valueOf(itemsModel.getItemPrice()*itemsModel.getQuantity()));
        holder.tvQty.setText(String.valueOf(itemsModel.getQuantity()));
        holder.chkBox.setOnCheckedChangeListener(null);
        holder.chkBox.setChecked(itemsModel.isChecked());
        holder.chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                itemsModel.setChecked(isChecked);
                adapterItemClickListener.OnCheckBoxClicked(position);
            }
        });
        holder.rvTxtRefund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterItemClickListener.OnItemClicked(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return CartData.mCartData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chkBox)
        CheckBox chkBox;
        @BindView(R.id.tv_item)
        TextView tvItem;
        @BindView(R.id.tv_qty)
        TextView tvQty;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.rv_txtRefund)
        TextView rvTxtRefund;
        @BindView(R.id.txt)
        TextView txt;
        @BindView(R.id.refund_qty)
        TextView refundQty;
        @BindView(R.id.header_layout)
        RelativeLayout headerLayout;
        @BindView(R.id.rv_txtRefunded)
        TextView rvTxtRefunded;
        @BindView(R.id.refunded_qty)
        TextView refundedQty;
        @BindView(R.id.txt1)
        TextView txt1;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
