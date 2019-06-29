package com.alhikmahpro.www.e_pos.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.ReportModel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {


    ArrayList<ReportModel> arrayList;
    String type;


    public ReportAdapter(ArrayList<ReportModel> arrayList, String type) {
        this.arrayList = arrayList;
        this.type = type;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ReportModel reportModel = arrayList.get(position);
        if (type.equals("summery")) {

            holder.rvTxtCol1.setText(reportModel.getDate());
            holder.rvTxtCol2.setText(CurrencyFormatter(reportModel.getTotal()));
            holder.rvTxtCol3.setText(CurrencyFormatter(reportModel.getRefund()));
            holder.rvTxtCol4.setText(CurrencyFormatter(reportModel.getDiscount()));
            holder.rvTxtCol5.setText(CurrencyFormatter(reportModel.getNet()));
        } else if (type.equals("employee")) {


            holder.rvTxtCol1.setText(reportModel.getEmployee());
            holder.rvTxtCol2.setText(CurrencyFormatter(reportModel.getTotal()));
            holder.rvTxtCol3.setText(CurrencyFormatter(reportModel.getRefund()));
            holder.rvTxtCol4.setText(CurrencyFormatter(reportModel.getDiscount()));
            holder.rvTxtCol5.setText(CurrencyFormatter(reportModel.getNet()));
        } else if (type.equals("customer")) {

            //set customer  name in txtDate
            holder.rvTxtCol1.setText(reportModel.getCustomer());
            holder.rvTxtCol2.setText(reportModel.getFirstVisit());
            holder.rvTxtCol3.setText(reportModel.getLastVisit());

            holder.rvTxtCol4.setText(String.valueOf(reportModel.getTotalVisit()) );
            holder.rvTxtCol5.setText(CurrencyFormatter(reportModel.getTotal()));
        }


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rv_txtCol1)
        TextView rvTxtCol1;
        @BindView(R.id.rv_txtCol2)
        TextView rvTxtCol2;
        @BindView(R.id.rv_txtCol3)
        TextView rvTxtCol3;
        @BindView(R.id.rv_txtCol4)
        TextView rvTxtCol4;
        @BindView(R.id.rv_txtCol5)
        TextView rvTxtCol5;

        ViewHolder(View view) {
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
