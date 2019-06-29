package com.alhikmahpro.www.e_pos.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.squareup.timessquare.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CalenderFragment extends DialogFragment {

    @BindView(R.id.timessquare_calender)
    CalendarPickerView timessquareCalender;
    @BindView(R.id.btnSelect)
    Button btnSelect;
    Unbinder unbinder;
    private static final String TAG = "CalenderFragment";

    public interface SendBackListener{
        public void sendMessage(ArrayList<Date>dates);
    }

    public SendBackListener mSendBackListener;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calender_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);

        Date today = new Date();
        Log.d(TAG, "Today :" + today);
        Calendar nextYear = Calendar.getInstance();
        Calendar previousYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        previousYear.add(Calendar.YEAR, -1);



        Log.d(TAG, "previousYear.getTime() :" + previousYear.getTime());

        timessquareCalender.init(previousYear.getTime(), nextYear.getTime())
                .withSelectedDate(today)
                .inMode(CalendarPickerView.SelectionMode.RANGE);




        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnSelect)
    public void onViewClicked() {

        ArrayList<Date> selectedDates = (ArrayList<Date>) timessquareCalender.getSelectedDates();
        mSendBackListener.sendMessage(selectedDates);
        getDialog().dismiss();


    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            //msendBackListener=(sendBackListener) activity;
            mSendBackListener=(SendBackListener) getActivity();

        }catch (ClassCastException e){
            Log.d(TAG, "onAttach: ClassCastException :"+e.getMessage());
            //throw  new ClassCastException(activity.toString()+"Must override method");
        }

    }
}
