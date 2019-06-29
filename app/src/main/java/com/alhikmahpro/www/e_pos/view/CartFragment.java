package com.alhikmahpro.www.e_pos.view;


import android.app.FragmentManager;
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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.adapter.CartAdapter;
import com.alhikmahpro.www.e_pos.adapter.ItemAdapter;
import com.alhikmahpro.www.e_pos.data.CartData;
import com.alhikmahpro.www.e_pos.data.ItemsModel;
import com.alhikmahpro.www.e_pos.data.SessionHandler;
import com.alhikmahpro.www.e_pos.data.UserModel;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;
import com.alhikmahpro.www.e_pos.interfaces.OnAdapterClickListener;
import com.alhikmahpro.www.e_pos.interfaces.OnAdapterItemClickListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import android.app.DialogFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment{


    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.cart_item_rv)
    RecyclerView cartItemRv;

    Unbinder unbinder;
    Toolbar toolbar;


    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
//    @BindView(R.id.spinner)
//    Spinner spinner;
    @BindView(R.id.layout_charge)
    LinearLayout layoutCharge;
    EditText nameEditText, mobEditText, addEditText;
    dbHelper helper;
//    @BindView(R.id.layout_head)
//    LinearLayout layoutHead;
    @BindView(R.id.tv_charge)
    TextView tvCharge;
    int itemId, quantity, cartPosition;
    double price,tot;
    String itemName, itemCode, categoryName, customerName, employeeName,cMob;
    static int ACTION_VALUE_FRAGMENT=2;
    FragmentActionListener fragmentActionListener;
    String TAG="CartFragment";

    public CartFragment() {
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
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        unbinder = ButterKnife.bind(this, view);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // toolbar.setNavigationIcon(null);

        employeeName = getArguments().getString("EMPLOYEE");
        toolbar.setTitle(employeeName);
        tvTotal.setText(" ");


        Calculate();
        //loadSpinner();
        LoadRecycler();



        Log.d(TAG,"cart count:"+CartData.mCartData.size());
        return view;
    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener){
        this.fragmentActionListener = fragmentActionListener;
    }

    private void LoadRecycler() {

        layoutManager = new LinearLayoutManager(getActivity());
        cartItemRv.setLayoutManager(layoutManager);
        cartItemRv.setItemAnimator(new DefaultItemAnimator());
        cartItemRv.setHasFixedSize(true);
        adapter = new CartAdapter(new OnAdapterClickListener() {
            @Override
            public void OnItemClicked(int position) {

                ItemsModel model = CartData.mCartData.get(position);
                cartPosition = position;
                itemId = model.getItemId();
                itemCode = model.getItemCode();
                itemName = model.getItemName();
                categoryName = model.getCategoryName();
                quantity = model.getQuantity();
                price = model.getItemPrice();
                categoryName = model.getCategoryName();
                showDialog(itemName, quantity);


            }

            @Override
            public void OnDeleteClicked(int position) {

                CartData.mCartData.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, CartData.mCartData.size());
                Calculate();


            }
        });
        cartItemRv.setAdapter(adapter);
    }


    private void showDialog(String name, int qty) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.custom_dialog, null);
        builder.setView(dialogView);

        final EditText edt = dialogView.findViewById(R.id.countEditText);
        final Button minus = dialogView.findViewById(R.id.btn_minus);
        final Button plus = dialogView.findViewById(R.id.btn_plus);

        builder.setTitle(name);
        builder.setCancelable(false);
        edt.setText(String.valueOf(qty));

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
                updateCart(count);


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

    private void updateCart(int count) {

        ItemsModel newCartModel = CartData.mCartData.get(cartPosition);
        newCartModel.setItemName(itemName);
        newCartModel.setItemPrice(price);
        newCartModel.setQuantity(count);
        CartData.mCartData.set(cartPosition, newCartModel);
        adapter.notifyDataSetChanged();
        Calculate();


    }


    private void  Calculate() {
        tot = 0;
        for (ItemsModel model : CartData.mCartData) {

            Log.d(TAG,"Item qty in cart:"+model.getQuantity());
            tot = tot + model.getQuantity() * model.getItemPrice();
        }
        tvTotal.setText(CurrencyFormatter(tot));

    }

    private void showDialog() {
//        ListCustomerFragment listCustomerFragment=new ListCustomerFragment();
//        listCustomerFragment.show(getFragmentManager(),"Customer");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Remove User");
        builder.setMessage("Are you sure ! Do you want to remove customer?");
        builder.setCancelable(false);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SessionHandler.getInstance(getContext()).resetCustomer();

                Toast.makeText(getContext(), "Customer Removed", Toast.LENGTH_LONG).show();


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

    private void saveCustomer(String name, String mob, String add) {
        helper.addCustomer(name, add, mob);
        addEditText.setText("");
        nameEditText.setText("");
        addEditText.setText("");
       // loadSpinner();
       // int position = spinner.getAdapter().getCount();
        //spinner.setSelection(position - 1);

    }


    private boolean validate(String name) {
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Enter Name");
            return false;
        }


        return true;

    }

    private void deleteCart() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Cancel Order");
        builder.setMessage("Are you sure ! Do you want to delete this cart?");
        builder.setCancelable(false);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //CartData.mCartData.clear();
                fragmentActionListener.onBackActionPerformed();

                Toast.makeText(getContext(), "Cart Deleted", Toast.LENGTH_LONG).show();


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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItemCart = menu.findItem(R.id.action_cart);
        menuItemCart.setVisible(false);
        MenuItem menuItemSearch = menu.findItem(R.id.action_search);
        menuItemSearch.setVisible(false);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_delete) {
            deleteCart();
            return true;

        } else if (id == R.id.action_person) {
            String cus=SessionHandler.getInstance(getContext()).getCustomerName();
            if(cus==null || TextUtils.isEmpty(cus)){

                ListCustomerFragment listCustomerFragment=new ListCustomerFragment();
                listCustomerFragment.show(getFragmentManager(),"Customer");
            }
            else{
                showDialog();
            }


        }


        return super.onOptionsItemSelected(item);
    }


//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//        int pos=spinner.getSelectedItemPosition();
//        if(pos>0){
//            customerName = parent.getItemAtPosition(position).toString();
//        }
//
//
//
//    }
//
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }

    @OnClick({R.id.layout_charge, R.id.tv_total, R.id.tv_charge})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_charge:
                LoadPayment();
                break;
            case R.id.tv_total:
                LoadPayment();
                break;
            case R.id.tv_charge:
                LoadPayment();
                break;
        }
    }

    private void LoadPayment() {

        customerName=SessionHandler.getInstance(getContext()).getCustomerName();
        cMob=SessionHandler.getInstance(getContext()).getCustomerMobile();
        Log.d(TAG,"customer:"+customerName+"mob"+cMob);

        if (TextUtils.isEmpty(customerName)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Warning");
            builder.setMessage(" Do you want to continue without customer?");
            builder.setCancelable(false);
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    LoadFragment();
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

        } else {
            LoadFragment();
        }

    }

    private void LoadFragment() {


        if (fragmentActionListener!=null){
            Bundle bundle = new Bundle();
            bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT,ACTION_VALUE_FRAGMENT);
            bundle.putString("EMP_NAME", employeeName);
            bundle.putString("CUS_NAME", customerName);
            bundle.putString("CUS_MOB", cMob);
            bundle.putDouble("TOTAL",tot);
            fragmentActionListener.onActionPerformed(bundle);
        }


    }

    public String CurrencyFormatter(double val){
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        String pattern = ((DecimalFormat) nf).toPattern();
        String newPattern = pattern.replace("\u00A4", "").trim();
        NumberFormat newFormat = new DecimalFormat(newPattern);

        String ft=String.valueOf(newFormat.format(val));
        return  ft;

    }

    @Override
    public void onStop() {
        super.onStop();
        SessionHandler.getInstance(getContext()).resetCustomer();
    }
}
