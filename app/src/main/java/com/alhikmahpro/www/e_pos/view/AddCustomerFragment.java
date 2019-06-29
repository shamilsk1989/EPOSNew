package com.alhikmahpro.www.e_pos.view;


import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.DataContract;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddCustomerFragment extends Fragment {


    @BindView(R.id.nameEditText)
    EditText nameEditText;
    @BindView(R.id.mobileEditText)
    EditText mobileEditText;
    @BindView(R.id.addressTextView)
    TextView addressTextView;
    @BindView(R.id.addressEditText)
    EditText addressEditText;
    @BindView(R.id.btn_save)
    Button btnSave;
    Unbinder unbinder;
    Toolbar toolbar;
    String action,title;
    int id;
    dbHelper helper;
    FragmentActionListener fragmentActionListener;

    public AddCustomerFragment() {
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
        View view = inflater.inflate(R.layout.fragment_add_customer, container, false);
        unbinder = ButterKnife.bind(this, view);
        action = getArguments().getString("ACTION");
        id = getArguments().getInt("SELECTED_ID");

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(action +" "+ "Customer");
        helper=new dbHelper(getContext());
        if(action.equals("Edit")){
            LoadValues();
        }
        return view;
    }

    private void LoadValues() {
        SQLiteDatabase database=helper.getReadableDatabase();
        Cursor cursor=helper.getCustomerById(database,id);
        if(cursor.moveToFirst()){
            do{
                nameEditText.setText(cursor.getString(cursor.getColumnIndex(DataContract.Customer.COL_CUSTOMER_NAME)));
                addressEditText.setText(cursor.getString(cursor.getColumnIndex(DataContract.Customer.COL_CUSTOMER_ADDRESS)));
                mobileEditText.setText(cursor.getString(cursor.getColumnIndex(DataContract.Customer.COL_MOBILE)));

            }while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_save)
    public void onViewClicked() {

        String name=nameEditText.getText().toString();
        String address=addressEditText.getText().toString();
        String mobile=mobileEditText.getText().toString();
        if(validate(name)){
            if(action.equals("Add")) {
                helper.addCustomer(name,address, mobile);
                Toast.makeText(getContext(), "Customer added", Toast.LENGTH_LONG).show();
                clearText();
            }
            else if(action.equals("Edit")){
                if(validate(name)){
                    helper.updateCustomer(name,address,mobile,id);
                    Toast.makeText(getContext(), "Customer updated", Toast.LENGTH_LONG).show();
                    clearText();
                   // ItemActivity.fragmentManager.popBackStack();
                }

            }

        }
    }

    private void clearText() {
        nameEditText.setText("");
        addressEditText.setText("");
        mobileEditText.setText("");
    }
    private boolean validate(String name) {
        if(TextUtils.isEmpty(name)){
            nameEditText.setError("Enter Name");
            return false;
        }


        return true;

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItemOk = menu.findItem(R.id.action_refresh);
        MenuItem menuItemDelete = menu.findItem(R.id.action_delete);
        MenuItem menuItemPrint=menu.findItem(R.id.action_print);
        MenuItem menuItem_from_CSV=menu.findItem(R.id.action_from_csv);
        MenuItem menuItemCSV=menu.findItem(R.id.action_csv);
        menuItem_from_CSV.setVisible(false);
        menuItemPrint.setVisible(false);
        menuItemOk.setVisible(false);
        menuItemCSV.setVisible(false);
        if(action.equals("Add")){
            menuItemDelete.setVisible(false);

        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                deleteCustomer();
                return true;
            default:return false;


        }

    }

    private void deleteCustomer() {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Customer");
        builder.setMessage("Are you sure ! Do you want to delete this customer?");
        builder.setCancelable(false);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                helper.deleteCustomer(id);
                Toast.makeText(getContext(),"Customer Deleted",Toast.LENGTH_LONG).show();
                //ItemActivity.fragmentManager.popBackStack();
                fragmentActionListener.onBackActionPerformed();



            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog=builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });
        alertDialog.show();
    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
         this.fragmentActionListener=fragmentActionListener;
    }
}
