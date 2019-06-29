package com.alhikmahpro.www.e_pos.view;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.alhikmahpro.www.e_pos.AppUtils;
import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.dbHelper;

import java.text.DateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewReportFragment extends Fragment implements DatePickerDialog.OnDateSetListener {


    @BindView(R.id.btn_search)
    Button btnSearch;

    @BindView(R.id.rvReport)
    RecyclerView rvReport;
    Unbinder unbinder;
    @BindView(R.id.txtFrom)
    EditText txtFrom;
    @BindView(R.id.txtTo)
    EditText txtTo;
    String mDate;
    String TAG = "ViewReportFragment";
    Calendar myCalendar = Calendar.getInstance();

    public ViewReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_report, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();

        txtFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    private void initView() {


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_search)
    public void onViewClicked() {

//        dbHelper helper = new dbHelper(getContext());
//        helper.getinvoicebyDate(mDate);

        Log.d(TAG, "Month" + AppUtils.getMonth() + "year" + AppUtils.getYear() + "Date" + AppUtils.getDate());

        DialogFragment datePicker=new DatePickerFragment();
        datePicker.show(getFragmentManager(),"Date Picker");


    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);


        String currentDateString= DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
        txtFrom.setText(currentDateString);


    }

    @OnClick(R.id.txtFrom)
    public void showFromDate() {




    }
}
