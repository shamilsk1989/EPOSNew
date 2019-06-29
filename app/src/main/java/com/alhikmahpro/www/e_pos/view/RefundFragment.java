package com.alhikmahpro.www.e_pos.view;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.AppUtils;
import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.adapter.RefundAdapter;
import com.alhikmahpro.www.e_pos.data.CartData;
import com.alhikmahpro.www.e_pos.data.DataContract;
import com.alhikmahpro.www.e_pos.data.ItemsModel;
import com.alhikmahpro.www.e_pos.data.SessionHandler;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;
import com.alhikmahpro.www.e_pos.interfaces.OnRefundAdapterClickListener;
import com.alhikmahpro.www.e_pos.network.VolleySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class RefundFragment extends Fragment {


    @BindView(R.id.rvRefund)
    RecyclerView rvRefund;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.bt_refund)
    Button btRefund;
    @BindView(R.id.tv_discount)
    TextView tvDiscount;

    Unbinder unbinder;
    String invoiceNo;
    dbHelper helper;
    ProgressDialog progressDialog;

    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    String item, itemCode, customer, employee, date, refundNumber, customerMobile;
    static final String TAG = "RefundFragment";
    int qty, previousRefundCount, newRefundCount, limit, tempRefundCounter;
    double price, total, refundAmount, discount, gTotal, discount_percentage;
    List<ItemsModel> tempList = new ArrayList<>();
    FragmentActionListener fragmentActionListener;
    boolean check = false;


    public RefundFragment() {
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
        View view = inflater.inflate(R.layout.fragment_refund, container, false);
        unbinder = ButterKnife.bind(this, view);

        invoiceNo = getArguments().getString("INVOICE");
        customer = getArguments().getString("CUSTOMER");
        employee = getArguments().getString("EMPLOYEE");
        date = getArguments().getString("DATE");
        total = getArguments().getDouble("TOTAL");
        discount_percentage = getArguments().getDouble("DISCOUNT");
        gTotal = getArguments().getDouble("GTOTAL");
        customerMobile = getArguments().getString("MOBILE");

        progressDialog = new ProgressDialog(getContext());
        helper = new dbHelper(getContext());
        refundNumber = GetRefundNumber();
        initRecyclerView();
        btRefund.setEnabled(false);
        return view;
    }

    private String GetRefundNumber() {

        int ref_id = helper.getLastRefundId() + 1;
        ref_id = ref_id + 1000;
        String refundId = "Ref-" + ref_id;
        return refundId;

    }

    private void initRecyclerView() {

        layoutManager = new LinearLayoutManager(getActivity());
        rvRefund.setLayoutManager(layoutManager);
        rvRefund.setItemAnimator(new DefaultItemAnimator());
        rvRefund.setHasFixedSize(true);

        adapter = new RefundAdapter(new OnRefundAdapterClickListener() {
            @Override
            public void OnItemClicked(int position) {
//                ItemsModel model = CartData.mCartData.get(position);
//                item = model.getItemName();
//                qty = model.getQuantity();
//                price = model.getItemPrice();
//                previousRefundCount=model.getRefundCount();
//
//                Log.d(TAG,"previous counter"+previousRefundCount);
//                showDialog(position);

            }

            @Override
            public void OnCheckBoxClicked(int position) {

                ItemsModel model = CartData.mCartData.get(position);
                itemCode = model.getItemCode();
                item = model.getItemName();
                qty = model.getQuantity();
                price = model.getItemPrice();
                check = model.isChecked();
                previousRefundCount = model.getRefundCount();
                tempRefundCounter = model.getTempRefundCounter();
                Log.d(TAG, "itemCode:" + itemCode);
                btRefund.setEnabled(true);

                //check refund checkbpx selected or not
                if (check) {
                    //if selected
                    showDialog(position);
                } else {

                    //if not selected
                    //remove selection from temporary array list
                    for (int i = 0; i < tempList.size(); i++) {
                        ItemsModel cartItems = tempList.get(i);
                        if (cartItems.getItemName().equals(item)) {

                            tempList.remove(i);


                            //add refund count into cart array list


                            ItemsModel itemsModel = new ItemsModel();
                            CartData.mCartData.remove(position);
                            itemsModel.setItemCode(itemCode);
                            itemsModel.setItemName(item);
                            itemsModel.setItemPrice(price);
                            itemsModel.setQuantity(qty);
                            itemsModel.setTempRefundCounter(0);//set old refund counter
                            CartData.mCartData.add(position, itemsModel);

                            // set refund amount


                            double discountAmount = (tempRefundCounter * price) * discount_percentage / 100;

                            // Log.d(TAG, "discountAmount(un):" + discountAmount);
                            Log.d(TAG, "discountAmount(un):" + discountAmount);


                            refundAmount = (tempRefundCounter * price) - discountAmount;


                            double refund_amount = Double.valueOf(tvTotal.getText().toString());
                            double discount_amount = Double.valueOf(tvDiscount.getText().toString());


                            refund_amount = refund_amount - refundAmount;
                            discount_amount = (double) Math.round(discount_amount - discountAmount);

                            tvTotal.setText(String.valueOf(refund_amount));
                            tvDiscount.setText(String.valueOf(discount_amount));


                            initRecyclerView();
                        }
                    }
                }
            }
        });

        rvRefund.setAdapter(adapter);
    }


    private void showDialog(int position) {

        final int pos = position;
        limit = qty - (previousRefundCount + tempRefundCounter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.custom_dialog, null);
        builder.setView(dialogView);

        final EditText edt = dialogView.findViewById(R.id.countEditText);
        final Button minus = dialogView.findViewById(R.id.btn_minus);
        final Button plus = dialogView.findViewById(R.id.btn_plus);
        // final int quantity = qty;

        builder.setTitle(item);
        builder.setCancelable(false);
        edt.setText("1");

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(edt.getText().toString());

                if (count > 1) {
                    count--;
                    edt.setText(String.valueOf(count));
                }

            }

        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int count = Integer.parseInt(edt.getText().toString());

                if (count < limit) {
                    count++;
                    edt.setText(String.valueOf(count));
                }

            }
        });


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int tempCount = Integer.parseInt(edt.getText().toString());
                //check current refund quantity is grater than the  limit
                if (tempCount < 1 || tempCount > limit) {
                    Toast.makeText(getContext(), "Enter valid quantity", Toast.LENGTH_LONG).show();
                } else {

                    //add return item details into a temporary  array list

                    ItemsModel temp = new ItemsModel();

                    temp.setItemCode(itemCode);
                    temp.setItemName(item);
                    //temp.setRefundCount(previousRefundCount);
                    // temp.setTempRefundCounter(tempCount);
                    temp.setQuantity(tempCount);
                    temp.setItemPrice(price);
                    tempList.add(temp);


                    // calculate refund amount

                    double discountAmount = (tempCount * price) * discount_percentage / 100;
                    Log.d(TAG, "count:(sel)" + tempCount + "price: " + price + "discount :" + discount_percentage);
                    Log.d(TAG, "discount amount:(sel)" + discountAmount);

                    refundAmount = (tempCount * price) - discountAmount;
                    // Log.d(TAG, "tempCount * price:(sel)" + tempCount * price);

                    // Log.d(TAG, "refund amount:(sel)" + refundAmount);
                    double refund_amount = Double.valueOf(tvTotal.getText().toString());
                    refund_amount = refund_amount + refundAmount;
                    tvTotal.setText(String.valueOf(refund_amount));

                    double set_discount_amount = Double.valueOf(tvDiscount.getText().toString());
                    set_discount_amount = set_discount_amount + discountAmount;
                    tvDiscount.setText(String.valueOf(set_discount_amount));


                    //add refund count into cart array list
                    //newRefundCount=previousRefundCount+tempCount;
                    // Log.d(TAG,"New counter "+newRefundCount);

                    //    Log.d(TAG, "setTempRefundCounter:" + item + tempCount);

                    ItemsModel itemsModel = new ItemsModel();
                    CartData.mCartData.remove(pos);
                    itemsModel.setItemCode(itemCode);
                    itemsModel.setItemName(item);
                    itemsModel.setItemPrice(price);
                    itemsModel.setQuantity(qty);
                    itemsModel.setRefundCount(previousRefundCount);
                    itemsModel.setTempRefundCounter(tempCount);
                    itemsModel.setChecked(true);
                    CartData.mCartData.add(pos, itemsModel);

                    //reload recycler view with refund count
                    initRecyclerView();

                    // set refund amount


                }


            }
        });


        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
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


    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }


    @OnClick(R.id.bt_refund)
    public void onViewClicked() {

        Log.d(TAG, "date:" + AppUtils.getDateTime());
        double total_amount = Double.valueOf(tvTotal.getText().toString());
        String mDate = AppUtils.getDateTime();

        if(checkNetwork()){
            saveAppServer(total_amount,mDate);
        } else{
            if(saveToLocalDataBase(total_amount,mDate,DataContract.SYNC_STATUS_FAILED)) {
                tempList.clear();
                fragmentActionListener.onBackActionPerformed();
            }
            else {
                Toast.makeText(getContext(), "Failed to save please try again", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void saveAppServer(final double total, final String date) {
        String Url=DataContract.SERVER_URL+"/"+"Refund";

        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Saving..");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();

        Log.d(TAG, "saveAppServer: ");
        JSONArray detailsArray = new JSONArray();
        JSONArray array = new JSONArray();
        JSONObject refund = new JSONObject();

        JSONObject result = new JSONObject();
        try {
            refund.put(DataContract.Refund.COL_REFUND_NUMBER,refundNumber);
            refund.put(DataContract.Refund.COL_INVOICE_NUMBER,invoiceNo);
            refund.put(DataContract.Refund.COL_TOTAL,total);
            refund.put(DataContract.Refund.COL_CUSTOMER, customer);
            refund.put(DataContract.Refund.COL_EMPLOYEE,employee);
            refund.put(DataContract.Refund.COL_REFUND_DATE,date);

            array.put(refund);
            for (ItemsModel model : tempList) {
                JSONObject object = new JSONObject();
                object.put(DataContract.RefundDetails.COL_REFUND_NUMBER,refundNumber);
                object.put(DataContract.RefundDetails.COL_INVOICE_NUMBER,invoiceNo);
                object.put(DataContract.RefundDetails.COL_ITEM_CODE, model.getItemCode());
                object.put(DataContract.RefundDetails.COL_ITEM_NAME, model.getItemName());
                object.put(DataContract.RefundDetails.COL_ITEM_PRICE, model.getItemPrice());
                object.put(DataContract.RefundDetails.COL_ITEM_QUANTITY, model.getQuantity());
                detailsArray.put(object);

            }

            result.put("Invoice", array);
            result.put("Details", detailsArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "saveAppServer: " + result);


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,Url, result, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.cancel();

                Log.d(TAG, "onResponse: " + response);

                try {

                    String res = response.getString("Status");
                    if (res.equals("OK")) {
                        if (saveToLocalDataBase(total,date,DataContract.SYNC_STATUS_OK)) {
                            tempList.clear();
                            fragmentActionListener.onBackActionPerformed();

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                VolleyErrorHandler(error);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("deviceid", SessionHandler.getInstance(getContext()).getDeviceNo());
                headers.put("branchcode", SessionHandler.getInstance(getContext()).getShopName());
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);
    }

    private void gotoNext() {
    }

    private boolean saveToLocalDataBase(double total_amount, String mDate,int sync) {


        boolean ins = helper.addRefund(refundNumber, invoiceNo, total_amount, employee, customer, mDate,sync);
        if(ins){
            helper.addRefundDetails(tempList, refundNumber, invoiceNo);
            for (ItemsModel model : tempList) {

                String item_name = model.getItemName();
                int qty = model.getRefundCount() + model.getQuantity();
                Log.d(TAG, "update" + item_name + " : " + qty);
                helper.updateInvoiceDetailByRefund(invoiceNo, item_name, qty);

            }
            return true;
        }
        else{
            return false;
        }
    }
    private void VolleyErrorHandler(VolleyError error){
        if(error instanceof TimeoutError){
            Log.d(TAG, "time out : "+error.getMessage());
        }
        if(error instanceof NoConnectionError){
            Log.d(TAG, "NoConnectionError : "+error.getMessage());
        }
        if(error instanceof AuthFailureError){
            Log.d(TAG, "AuthFailureError: "+error.getMessage());
        }
        if(error instanceof ServerError){
            Log.d(TAG, "ServerError: "+error.getMessage());
        }if(error instanceof NetworkError){
            Log.d(TAG, "NetworkError : "+error.getMessage());
        }
        if(error instanceof ParseError){
            Log.d(TAG, "ParseError : "+error.getMessage());
        }

    }
        private boolean checkNetwork(){
        ConnectivityManager connectivityManager=(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return (networkInfo!=null && networkInfo.isConnected());
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItemReturn = menu.findItem(R.id.action_refresh);
        MenuItem menuItemPrint = menu.findItem(R.id.action_print);
        MenuItem menuItemSync = menu.findItem(R.id.action_sync);
        menuItemReturn.setVisible(false);
        menuItemPrint.setVisible(false);
        menuItemSync.setVisible(false);


    }




}
