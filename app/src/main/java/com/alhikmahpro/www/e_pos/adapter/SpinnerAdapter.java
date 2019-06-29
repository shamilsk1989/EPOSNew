package com.alhikmahpro.www.e_pos.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.ItemsModel;

import java.util.ArrayList;
import java.util.List;

public class SpinnerAdapter extends BaseAdapter {

    Context context;
    ArrayList<String>offer=new ArrayList<>();
    ArrayList<Double>rate=new ArrayList<>();
    LayoutInflater layoutInflater;

    public SpinnerAdapter(Context context, ArrayList<String> offer, ArrayList<Double> rate) {
        this.context = context;
        this.offer = offer;
        this.rate = rate;
        layoutInflater=(LayoutInflater.from(context));

    }

    @Override
    public int getCount() {
        return offer.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView=layoutInflater.inflate(R.layout.custom_spinner,null);

        TextView name=(TextView)convertView.findViewById(R.id.name);
        TextView value=(TextView)convertView.findViewById(R.id.value);
        name.setText(offer.get(position));
        Log.d("Adapter","position"+position);
        value.setText(rate.get(position).toString());




        return convertView;
    }
}
