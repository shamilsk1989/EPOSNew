package com.alhikmahpro.www.e_pos.view;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.DataContract;
import com.alhikmahpro.www.e_pos.data.dbHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaperSettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    Toolbar toolbar;
    @BindView(R.id.mainEditText)
    EditText mainEditText;
    @BindView(R.id.sub1EditText)
    EditText sub1EditText;
    @BindView(R.id.sub2EditText)
    EditText sub2EditText;
    @BindView(R.id.mobileEditText)
    EditText mobileEditText;
    @BindView(R.id.telEditText)
    EditText telEditText;
    @BindView(R.id.buttonSave)
    Button buttonSave;
    Unbinder unbinder;
    String TAG = "PaperSettingsFragment";
    @BindView(R.id.footerEditText)
    EditText footerEditText;

    public PaperSettingsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_paper_settings, container, false);
        unbinder = ButterKnife.bind(this, view);


        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Add Titles");
//        List<String> list = new ArrayList<>();
//        list.add("80 mm");


//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(arrayAdapter);
//        spinner.setOnItemSelectedListener(this);

        initView();


        return view;
    }

    private void initView() {

        dbHelper helper = new dbHelper(getContext());
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = helper.getBillTitle(database);
        Log.d(TAG, "InitView" + cursor.getCount());
        if (cursor.moveToFirst()) {

            do {
                mainEditText.setText(cursor.getString(cursor.getColumnIndex(DataContract.BillTitles.COL_SHOP_NAME)));
                sub1EditText.setText(cursor.getString(cursor.getColumnIndex(DataContract.BillTitles.COL_SHOP_ADDRESS1)));
                sub2EditText.setText(cursor.getString(cursor.getColumnIndex(DataContract.BillTitles.COL_SHOP_ADDRESS2)));
                mobileEditText.setText(cursor.getString(cursor.getColumnIndex(DataContract.BillTitles.COL_MOBILE)));
                telEditText.setText(cursor.getString(cursor.getColumnIndex(DataContract.BillTitles.COL_TEL)));
                footerEditText.setText(cursor.getString(cursor.getColumnIndex(DataContract.BillTitles.COL_FOOTER)));
            } while (cursor.moveToNext());
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.buttonSave)
    public void onViewClicked() {
        String main = mainEditText.getText().toString();
        String sub1 = sub1EditText.getText().toString();
        String sub2 = sub2EditText.getText().toString();
        String mob = mobileEditText.getText().toString();
        String tel = telEditText.getText().toString();
        String footer=footerEditText.getText().toString();
        if (valid(main)) {

            dbHelper helper = new dbHelper(getContext());

            boolean res = helper.addBillTitle(main, sub1, sub2, mob, tel,footer);
            if (res) {
                Toast.makeText(getContext(), "saved successfully ", Toast.LENGTH_LONG).show();

                mainEditText.setText("");
                sub1EditText.setText("");
                sub2EditText.setText("");
                mobileEditText.setText("");
                telEditText.setText("");
                footerEditText.setText("");

            }

        }
    }

    private boolean valid(String main) {

        if (TextUtils.isEmpty(main)) {
            mainEditText.setError("Enter Title");
            return false;
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
