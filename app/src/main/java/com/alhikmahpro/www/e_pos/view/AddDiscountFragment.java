package com.alhikmahpro.www.e_pos.view;


import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.DataContract;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddDiscountFragment extends Fragment {
    FragmentActionListener fragmentActionListener;


    @BindView(R.id.discountEditText)
    EditText discountEditText;
    @BindView(R.id.percentageEditText)
    EditText percentageEditText;
    @BindView(R.id.btn_save)
    Button btnSave;
    Unbinder unbinder;
    String type, TAG = "AddDiscountFragment";
    int selectedId;
    @BindView(R.id.imageView_item)
    ImageView imageViewItem;
    dbHelper helper;
    @BindView(R.id.codeEditText)
    EditText codeEditText;
    Toolbar toolbar;


    public AddDiscountFragment() {
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
        View view = inflater.inflate(R.layout.fragment_add_discount, container, false);
        unbinder = ButterKnife.bind(this, view);
        type = getArguments().getString("TYPE");
        selectedId = getArguments().getInt("SELECTED_ID");
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(type+" "+"Discount");
        helper=new dbHelper(getContext());

        if(type.equals("Edit")){
            LoadValues();
        }
        return view;
    }

    private void LoadValues() {

        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = helper.getItemByID(database, selectedId);
        if (cursor.moveToFirst()) {

            do {
                //  obj.setItemId(cursor.getInt(cursor.getColumnIndex(DataContract.Items.COL_ITEM_ID)));
                String name = cursor.getString(cursor.getColumnIndex(DataContract.Items.COL_ITEM_NAME));
                discountEditText.setText(name);
                codeEditText.setText(cursor.getString(cursor.getColumnIndex(DataContract.Items.COL_ITEM_CODE)));
                percentageEditText.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex(DataContract.Items.COL_ITEM_PRICE))));                ;



            } while (cursor.moveToNext());
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_save)
    public void onViewClicked() {

        String name = discountEditText.getText().toString();
        String rate = percentageEditText.getText().toString();
        String code=codeEditText.getText().toString();
        if (validate(name, rate)) {

            double percentage = Double.valueOf(rate);
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            String numberAsString = decimalFormat.format(percentage);
            double formatted_rate = Double.valueOf(numberAsString);
            Log.d(TAG, "formatted_rate" + formatted_rate);

            //use barcode as spinner selected item position

            //  String barcode = "";

            imageViewItem.setDrawingCacheEnabled(true);
            imageViewItem.buildDrawingCache();
            Bitmap bitmap = imageViewItem.getDrawingCache();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] data = stream.toByteArray();
            String barcode="";
            String category="discount";
            Log.d(TAG,"image"+data);

            if(type.equals("Add")){
                helper.addItems(barcode,code,name.trim(),formatted_rate,category,data,0);
                Toast.makeText(getContext(), "Discount added", Toast.LENGTH_LONG).show();
                discountEditText.setText("");
                percentageEditText.setText("");
                codeEditText.setText("");

            }
            else{

                helper.updateItem(barcode,code, name.trim(), formatted_rate, category, data, 0,selectedId);
                Toast.makeText(getActivity(),"Discount updated",Toast.LENGTH_LONG).show();
               // ItemActivity.fragmentManager.popBackStack();
            }



        }




    }

    private void deleteItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Discount");
        builder.setMessage("Are you sure ! Do you want to delete this discount?");
        builder.setCancelable(false);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                helper.deleteItem(selectedId);
                Toast.makeText(getContext(), "Item Deleted", Toast.LENGTH_LONG).show();
               // ItemActivity.fragmentManager.popBackStack();
                fragmentActionListener.onBackActionPerformed();


            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });
        alertDialog.show();


    }








    private boolean validate(String name, String price) {
        if (TextUtils.isEmpty(name)) {
            discountEditText.setError("Enter Name");
            return false;
        } else if (TextUtils.isEmpty(price)) {
            percentageEditText.setError("Enter Percentage");
            return false;
        }
        return true;

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItemOk=menu.findItem(R.id.action_refresh);
        menuItemOk.setVisible(false);
        MenuItem menuItemPrint=menu.findItem(R.id.action_print);
        menuItemPrint.setVisible(false);
        if(type.equals("Add")){
            MenuItem menuItemDelete=menu.findItem(R.id.action_delete);
            menuItemDelete.setVisible(false);
        }
        MenuItem menuItemCSV=menu.findItem(R.id.action_csv);
        menuItemCSV.setVisible(false);
        MenuItem menuItem_from_CSV=menu.findItem(R.id.action_from_csv);
        menuItem_from_CSV.setVisible(false);




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteItem();
                return true;
            default:
                return false;


        }

    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }
}