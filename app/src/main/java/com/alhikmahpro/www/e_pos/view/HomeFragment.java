package com.alhikmahpro.www.e_pos.view;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alhikmahpro.www.e_pos.AppUtils;
import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    @BindView(R.id.rv_price)
    TextView rvPrice;
    @BindView(R.id.pieChart)
    PieChart pieChart;
    Unbinder unbinder;
    static String TAG = "HomeFragment";
    dbHelper helper;
    String mDate;
    @BindView(R.id.txtTotalInvoice)
    TextView txtTotalInvoice;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        mDate = AppUtils.getDateTime();
        helper = new dbHelper(getContext());



        setPieChart();
        return view;
    }

    private void setPieChart() {
        int c = helper.getTotalInvoice(mDate);
        txtTotalInvoice.setText(String.valueOf(c));


        ArrayList<PieEntry> pieEntries = new ArrayList<>();


        Cursor cursor = helper.getTopSaleItem(mDate);

        String item;
        int qty;
        if (cursor.moveToFirst()) {
            do {
                item = cursor.getString(cursor.getColumnIndex("item_name"));
                qty = cursor.getInt(cursor.getColumnIndex("sale"));
                pieEntries.add(new PieEntry(qty, item));

            } while (cursor.moveToNext());

        }
        cursor.close();


//        for (ItemsModel model:items) {
//            float price=(float)model.getItemPrice();
//            String item=model.getItemName();
//            pieEntries.add(new PieEntry(price,item));
//        }

        pieChart.setVisibility(View.VISIBLE);
        pieChart.animateXY(5000, 5000);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Items");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        Description description = new Description();
        description.setText("Today");
        pieChart.setDescription(description);
        pieChart.invalidate();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem menuItemDelete = menu.findItem(R.id.action_delete);
        menuItemDelete.setVisible(false);
        MenuItem menuUser = menu.findItem(R.id.action_person);
        menuUser.setVisible(false);
        MenuItem menuItemCart = menu.findItem(R.id.action_cart);
        menuItemCart.setVisible(false);
        MenuItem menuItemSearch = menu.findItem(R.id.action_search);
        menuItemSearch.setVisible(false);


    }

    @Override
    public void onResume() {
        super.onResume();
        setPieChart();
    }
}
