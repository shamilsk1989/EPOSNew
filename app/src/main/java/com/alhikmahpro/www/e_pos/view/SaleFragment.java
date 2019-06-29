package com.alhikmahpro.www.e_pos.view;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.adapter.ItemAdapter;
import com.alhikmahpro.www.e_pos.adapter.SearchItemAdapter;
import com.alhikmahpro.www.e_pos.data.CartData;
import com.alhikmahpro.www.e_pos.data.ItemsModel;
import com.alhikmahpro.www.e_pos.data.UserModel;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;
import com.alhikmahpro.www.e_pos.interfaces.OnAdapterItemClickListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaleFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.tv_tickets)
    TextView tvTickets;
    @BindView(R.id.header_layout)
    LinearLayout headerLayout;


    Unbinder unbinder;
    //RecyclerView.Adapter adapter;
    SearchItemAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    List<ItemsModel> arrayList = new ArrayList<>();
    int itemId, spinnerPosition;
    String itemName, itemCode, categoryName, TAG = "SaleFragment";
    double price;
    MenuItem menuItemCart, menuItemSearch;
    TextView textCartItemCount;
    ImageView imageViewCart;
    Toolbar toolbar;

    dbHelper helper;
    String employee;
    boolean is_discount = false;
    private static final int ACTION_VALUE_FRAGMENT = 1;

    double sum=0;

    FragmentActionListener fragmentActionListener;



    public SaleFragment() {
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
        View view = inflater.inflate(R.layout.fragment_sale, container, false);
        toolbar = getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Sale");
        unbinder = ButterKnife.bind(this, view);

        helper = new dbHelper(getContext());
        LoadSpinner();
        LoadRecyclerView();
        return view;
    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    private void LoadSpinner() {
        List<UserModel> tempList;
        List<String> Employee = new ArrayList<>();
        Employee.add(0, "Select Employee");
        dbHelper helper = new dbHelper(getContext());
        tempList = helper.GetAllEmployee();
        for (UserModel model : tempList) {

            Employee.add(model.getName());


        }


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Employee);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);


    }

    private void LoadRecyclerView() {

        layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);

        arrayList = helper.getAllItem();


        adapter = new SearchItemAdapter(arrayList, new OnAdapterItemClickListener() {
            @Override
            public void OnItemClicked(int position) {
                ItemsModel model = arrayList.get(position);
                itemId = model.getItemId();
                itemName = model.getItemName();
                itemCode = model.getItemCode();
                price = model.getItemPrice();
                categoryName = model.getCategoryName();
                showDialog(itemName);
            }
        });
        rv.setAdapter(adapter);


    }


    private void showDialog(String name) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.custom_dialog, null);
        builder.setView(dialogView);

        final EditText edt = dialogView.findViewById(R.id.countEditText);
        final Button minus = dialogView.findViewById(R.id.btn_minus);
        final Button plus = dialogView.findViewById(R.id.btn_plus);

        builder.setTitle(name);
        builder.setCancelable(false);

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(edt.getText().toString());

                if (count > 0) {
                    count--;
                    edt.setText(String.valueOf(count));
                }

            }

        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int count = Integer.parseInt(edt.getText().toString());

                if (count < 1000) {
                    count++;
                    edt.setText(String.valueOf(count));
                }

            }
        });


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int count = Integer.parseInt(edt.getText().toString());
                addToCart(count);
                setupBadge(count);


            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
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

    private void addToCart(int qty) {

        int i,old_qty=0;
        boolean is_found = false;

        if (CartData.mCartData.size() > 0) {

            for (i = 0; i < CartData.mCartData.size(); i++) {
                ItemsModel cartItems = CartData.mCartData.get(i);
                if (cartItems.getItemId() == itemId) {
                    old_qty=cartItems.getQuantity();
                    is_found = true;
                    break;

                }

            }
            if (is_found) {
                //qty=qty+old_qty;
              //  Log.d(TAG, "update called");
                updateCart(qty, i,old_qty);
            } else {
              //  Log.d(TAG, "added called");
                insertCart(qty);
            }
        } else {
            //Log.d(TAG, "empty added called");
            insertCart(qty);
        }


    }

    private void updateCart(int count, int position,int old_count) {
        Log.d(TAG, "inside update quantity"+count);
        ItemsModel itemsModel = new ItemsModel();

        CartData.mCartData.remove(position);
        itemsModel.setItemId(itemId);
        itemsModel.setItemCode(itemCode);
        itemsModel.setItemName(itemName);
        itemsModel.setItemPrice(price);
        itemsModel.setCategoryName(categoryName);
        itemsModel.setQuantity(old_count+count);
        CartData.mCartData.add(position, itemsModel);
        double total = price * count;
        setTotal(total);

        //Log.d(TAG, "updated to cart" + CartData.mCartData.size());

    }


    private void insertCart(int count) {

        ItemsModel itemsModel = new ItemsModel();
        itemsModel.setItemId(itemId);
        itemsModel.setItemCode(itemCode);
        itemsModel.setItemName(itemName);
        itemsModel.setItemPrice(price);
        itemsModel.setCategoryName(categoryName);
        itemsModel.setQuantity(count);
        CartData.mCartData.add(itemsModel);
        double total = price * count;
        setTotal(total);
     //   Log.d(TAG, "added to cart" + CartData.mCartData.size());

    }


    private void setTotal(double total) {
        Log.d(TAG, " price:" + total);
        Log.d(TAG, " old sum :" + sum);


//            String amt=tvTotal.getText().toString();
//            double cartAmount = Double.parseDouble(amt);
//            cartAmount = cartAmount + total;
//            tvTotal.setText(CurrencyFormatter(cartAmount));

       // double cartAmount=sum;
        sum=sum+total;
        Log.d(TAG, " new sum :" + sum);
        tvTotal.setText(CurrencyFormatter(sum));
        //sum=cartAmount;





    }

    public String CurrencyFormatter(double val) {

       // Log.d(TAG,"val"+val);
//        NumberFormat nf = NumberFormat.getCurrencyInstance();
//        String pattern = ((DecimalFormat) nf).toPattern();
//        Log.d(TAG,"Pattern:"+pattern);
//        String newPattern = pattern.replace("\u00A4", "").trim();
//        Log.d(TAG,"newPattern:"+newPattern);
//        NumberFormat newFormat = new DecimalFormat(newPattern);
//        String ft = String.valueOf(newFormat.format(val));
//        Log.d(TAG,"Formatted:"+ft.substring(1));
//        return ft.substring(1);


        NumberFormat nf = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);
        String ft=nf.format(val).trim();
        return  ft;

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
        menuItemCart = menu.findItem(R.id.action_cart);
        menuItemCart.setVisible(false);
        menuItemSearch = menu.findItem(R.id.action_search);


//        MenuItem search = menu.findItem(R.id.search);
//        SearchView searchView = (SearchView) search.getActionView();
        SearchView searchView = (SearchView) menuItemSearch.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItemSearch);



    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_search:
//
//                return true;
//            default:
//                return false;
//
//
//        }
//
//    }


    public void setupBadge(int counter) {
       // int mCount = Integer.parseInt(textCartItemCount.getText().toString());
        int mCount=Integer.parseInt(tvTickets.getText().toString());
     //   Log.d(TAG, "counter starting:" + mCount);

        mCount = mCount + counter;
        tvTickets.setText(String.valueOf(Math.min(mCount, 99)));


    }




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        employee = parent.getItemAtPosition(position).toString();
        spinnerPosition = spinner.getSelectedItemPosition();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {


    }

    @Override
    public void onResume() {
        Log.e("DEBUG", "onResume of SaleFragment" + spinnerPosition);
        int qty = 0;
        sum=0;

        for (ItemsModel model : CartData.mCartData) {
            sum = sum + (model.getItemPrice() * model.getQuantity());
            qty = qty + model.getQuantity();

        }
        tvTickets.setText(String.valueOf(Math.min(qty, 99)));
        Log.e("DEBUG", "onResume of SaleFragment items in cart:" + qty);
        tvTotal.setText(CurrencyFormatter(sum));
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e("DEBUG", "OnPause of SaleFragment");
        super.onPause();
    }

    @OnClick(R.id.header_layout)
    public void gotoCart() {

        if (spinner.getSelectedItemPosition() < 1) {
            Toast.makeText(getContext(), "select an Employee", Toast.LENGTH_LONG).show();
        } else {

            if (fragmentActionListener != null) {
                Bundle bundle = new Bundle();
                bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT, ACTION_VALUE_FRAGMENT);
                bundle.putString("EMPLOYEE", employee);
                fragmentActionListener.onActionPerformed(bundle);
            }

        }




    }
}
