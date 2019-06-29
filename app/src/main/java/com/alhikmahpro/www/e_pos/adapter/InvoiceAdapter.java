package com.alhikmahpro.www.e_pos.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.DataContract;
import com.alhikmahpro.www.e_pos.data.InvoiceModel;
import com.alhikmahpro.www.e_pos.interfaces.OnAdapterItemClickListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceHolder> {


    List<InvoiceModel> list;
    OnAdapterItemClickListener adapterItemClickListener;
    String type;



    public InvoiceAdapter(List<InvoiceModel> list, String typ, OnAdapterItemClickListener adapterItemClickListener) {
        this.type = typ;
        this.list = list;
        this.adapterItemClickListener = adapterItemClickListener;
    }

    @Override
    public InvoiceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_view_rv, parent, false);
        InvoiceHolder invoiceHolder = new InvoiceHolder(view);
        return invoiceHolder;
    }

    @Override
    public void onBindViewHolder(InvoiceHolder holder, final int position) {
        InvoiceModel model = list.get(position);
        if (type.equals("INV")) {
            holder.rvInvoiceNumber.setText(model.getInvoiceNumber());
        } else {
            holder.rvInvoiceNumber.setText(model.getRefundNumber());
        }

        int syncStatus = model.getSync_status();
        if (syncStatus == DataContract.SYNC_STATUS_OK) {

            holder.imgSync.setImageResource(R.drawable.ic_check);
        }
        else {
            holder.imgSync.setImageResource(R.drawable.ic_sync);
        }


        holder.txtAmount.setText(CurrencyFormatter(model.getGrantTotal()));
        holder.rvDate.setText(model.getInvoiceDate());
        holder.rvLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterItemClickListener.OnItemClicked(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class InvoiceHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rv_invoice_number)
        TextView rvInvoiceNumber;
        @BindView(R.id.txt_amount)
        TextView txtAmount;
        @BindView(R.id.rv_img_edit)
        ImageView rvImgEdit;
        @BindView(R.id.rv_date)
        TextView rvDate;
        @BindView(R.id.layout)
        LinearLayout layout;
        @BindView(R.id.rv_layout)
        RelativeLayout rvLayout;
        @BindView(R.id.imgSync)
        ImageView imgSync;

        InvoiceHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
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
