package com.alhikmahpro.www.e_pos.view;


import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.adapter.InvoiceAdapter;
import com.alhikmahpro.www.e_pos.data.DataContract;
import com.alhikmahpro.www.e_pos.data.InvoiceModel;
import com.alhikmahpro.www.e_pos.data.ItemsModel;
import com.alhikmahpro.www.e_pos.data.SessionHandler;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;
import com.alhikmahpro.www.e_pos.interfaces.OnAdapterItemClickListener;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewInvoiceFragment extends Fragment {


    @BindView(R.id.invoice_rv)
    RecyclerView invoiceRv;
    Unbinder unbinder;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    List<InvoiceModel> arrayList;
    dbHelper helper;
    String TAG = "ViewInvoiceFragment";
    String type;
    Toolbar toolbar;
    ProgressDialog progressDialog;
    String Url;

    FragmentActionListener fragmentActionListener;

    public ViewInvoiceFragment() {
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
        View view = inflater.inflate(R.layout.fragment_view_invoice, container, false);
        unbinder = ButterKnife.bind(this, view);
        helper = new dbHelper(getContext());
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        type = getArguments().getString("TYPE");
        Log.d(TAG, "type" + type);
        if (type.equals("INV")) {
            toolbar.setTitle("Invoice");
        } else {
            toolbar.setTitle("Return");
        }
        progressDialog = new ProgressDialog(getActivity());

        LoadRecyclerView();
        return view;
    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    private void LoadRecyclerView() {

        Log.d(TAG, "Loading RV");

        if (type.equals("INV")) {
            arrayList = helper.GetAllInvoice();
        } else {
            arrayList = helper.GetAllRefund();
        }

        layoutManager = new LinearLayoutManager(getContext());
        invoiceRv.setLayoutManager(layoutManager);
        invoiceRv.setItemAnimator(new DefaultItemAnimator());
        invoiceRv.setHasFixedSize(true);

        adapter = new InvoiceAdapter(arrayList, type, new OnAdapterItemClickListener() {
            @Override
            public void OnItemClicked(int position) {

                if (fragmentActionListener != null) {
                    Bundle bundle = new Bundle();
                    InvoiceModel model = arrayList.get(position);
                    int inv_id = model.getInvoiceId();
                    String inv_number = model.getInvoiceNumber();

                    Log.d(TAG, "Invoice number" + inv_number);
                    String ref_Number = model.getRefundNumber();
                    String employee = model.getEmployeeName();
                    String customer = model.getCustomerName();
                    double total = model.getTotal();
                    double discount = model.getDiscount();
                    double gTotal = model.getGrantTotal();
                    double paid = model.getPaid();
                    String date = model.getInvoiceDate();


                    bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT, 1);
                    bundle.putString("INVOICE", inv_number);
                    bundle.putString("REFUND", ref_Number);
                    bundle.putString("EMPLOYEE", employee);
                    bundle.putString("CUSTOMER", customer);
                    bundle.putDouble("TOTAL", total);
                    bundle.putDouble("DISCOUNT", discount);
                    bundle.putDouble("GTOTAL", gTotal);
                    bundle.putDouble("CASH", paid);
                    bundle.putString("DATE", date);
                    bundle.putString("TYPE", type);
                    fragmentActionListener.onActionPerformed(bundle);
                }
            }
        });
        invoiceRv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);

        MenuItem menuItemReturn = menu.findItem(R.id.action_refresh);
        MenuItem menuItemPrint = menu.findItem(R.id.action_print);
        menuItemReturn.setVisible(false);
        menuItemPrint.setVisible(false);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_sync) {
            syncInvoice();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void syncInvoice() {
        if (checkNetwork()) {


            progressDialog.setTitle("Please wait");
            progressDialog.setMessage("Uploading..");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true);
            progressDialog.show();

            JSONArray invoiceDetailsArray = new JSONArray();
            JSONArray invoiceArray = new JSONArray();
            dbHelper helper = new dbHelper(getContext());
            SQLiteDatabase database = helper.getReadableDatabase();

            if (type.equals("INV")) {
                //set url for invoice update
                Url = DataContract.SERVER_URL + "/" + "Invoice";
                //select all non send data from invoice table
                Cursor cursorInvoice = helper.GetUnSyncInvoice(database);
                Log.d(TAG, "cursor size: " + cursorInvoice.getCount());
                if (cursorInvoice != null) {
                    invoiceArray = parseInvoice(cursorInvoice);
                    cursorInvoice.close();
                }


                // select all non send data from invoice_details table
                Cursor cursorinvoiceDetails = helper.GetUnSyncInvoiceDetails(database);
                if (cursorinvoiceDetails != null) {
                    invoiceDetailsArray = parseInvoiceDetails(cursorinvoiceDetails);
                    cursorinvoiceDetails.close();

                }

            } else if (type.equals("RET")) {
                Url = DataContract.SERVER_URL + "/" + "Refund";
                //select all non send data from invoice table
                Cursor refundInvoice = helper.GetUnSyncRefund(database);
                Log.d(TAG, "cursor size: " + refundInvoice.getCount());
                if (refundInvoice != null) {
                    invoiceArray = parseRefund(refundInvoice);
                    refundInvoice.close();
                }


                // select all non send data from invoice_details table
                Cursor refundInvoiceDetails = helper.GetUnSyncRefundDetails(database);
                if (refundInvoiceDetails != null) {
                    invoiceDetailsArray = parseRefundDetails(refundInvoiceDetails);
                    refundInvoiceDetails.close();

                }

            }
            JSONObject result = new JSONObject();

            try {
                result.put("Invoice", invoiceArray);
                result.put("Details", invoiceDetailsArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (result.length() > 0) {
                saveAppServer(result);
            }
        } else {
            Toast.makeText(getActivity(), "No Internet ", Toast.LENGTH_SHORT).show();
        }


    }

    private void saveAppServer(JSONObject object) {

        Log.d(TAG, "saveAppServer: " + object);

        Log.d(TAG, "url: " + Url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: " + response);
                try {
                    String res = response.getString("Status");
                    Log.d(TAG, "onResponse: " + res);
                    if (res.equals("OK")) {
                        dbHelper helper = new dbHelper(getActivity());
                        if(type.equals("INV")){
                            helper.updateInvoiceSync(DataContract.SYNC_STATUS_OK);
                            helper.updateInvoiceDetailsSync(DataContract.SYNC_STATUS_OK);
                        }else if(type.equals("RET")){
                            helper.updateRefundSync(DataContract.SYNC_STATUS_OK);
                            helper.updateRefundDetailsSync(DataContract.SYNC_STATUS_OK);
                        }

                        progressDialog.cancel();
                        LoadRecyclerView();
                    } else {
                        progressDialog.cancel();

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
                headers.put("deviceid", SessionHandler.getInstance(getActivity()).getDeviceNo());
                headers.put("branchcode", SessionHandler.getInstance(getActivity()).getShopName());
                Log.d(TAG, "getHeaders: " + headers);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }


    private boolean checkNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    private JSONArray parseInvoiceDetails(Cursor cursor) {
        JSONArray array = new JSONArray();

        try {
            while (cursor.moveToNext()) {
                JSONObject object = new JSONObject();
                object.put(DataContract.InvoiceDetails.COL_INVOICE_NUMBER, cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_INVOICE_NUMBER)));
                object.put(DataContract.InvoiceDetails.COL_ITEM_CODE, cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_ITEM_CODE)));
                object.put(DataContract.InvoiceDetails.COL_ITEM_NAME, cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_ITEM_NAME)));
                object.put(DataContract.InvoiceDetails.COL_ITEM_QUANTITY, cursor.getInt(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_ITEM_QUANTITY)));
                object.put(DataContract.InvoiceDetails.COL_ITEM_PRICE, cursor.getDouble(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_ITEM_PRICE)));
                object.put(DataContract.InvoiceDetails.COL_TOTAL, cursor.getDouble(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_ITEM_PRICE)));
                object.put(DataContract.InvoiceDetails.COL_INVOICE_DATE, cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_INVOICE_DATE)));

                array.put(object);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return array;

    }


    private JSONArray parseInvoice(Cursor cursor) {
        JSONArray array = new JSONArray();

        while (cursor.moveToNext()) {
            JSONObject invoice = new JSONObject();
            try {
                invoice.put(DataContract.Invoice.COL_INVOICE_NUMBER, cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_NUMBER)));
                invoice.put(DataContract.Invoice.COL_TOTAL, cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_TOTAL)));
                invoice.put(DataContract.Invoice.COL_DISCOUNT, cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_DISCOUNT)));
                invoice.put(DataContract.Invoice.COL_GRAND_TOTAL, cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_GRAND_TOTAL)));
                invoice.put(DataContract.Invoice.COL_CASH, cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_CASH)));
                invoice.put(DataContract.Invoice.COL_CUSTOMER, cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_CUSTOMER)));
                invoice.put(DataContract.Invoice.COL_EMPLOYEE, cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_EMPLOYEE)));
                invoice.put(DataContract.Invoice.COL_INVOICE_DATE, cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_DATE)));
                array.put(invoice);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return array;
    }


    private JSONArray parseRefund(Cursor cursor) {
        JSONArray array = new JSONArray();

        while (cursor.moveToNext()) {
            JSONObject object = new JSONObject();
            try {
                object.put(DataContract.Refund.COL_REFUND_NUMBER, cursor.getString(cursor.getColumnIndex(DataContract.Refund.COL_REFUND_NUMBER)));
                object.put(DataContract.Refund.COL_INVOICE_NUMBER, cursor.getString(cursor.getColumnIndex(DataContract.Refund.COL_INVOICE_NUMBER)));
                object.put(DataContract.Refund.COL_TOTAL, cursor.getDouble(cursor.getColumnIndex(DataContract.Refund.COL_TOTAL)));
                object.put(DataContract.Refund.COL_CUSTOMER, cursor.getString(cursor.getColumnIndex(DataContract.Refund.COL_CUSTOMER)));
                object.put(DataContract.Refund.COL_EMPLOYEE, cursor.getString(cursor.getColumnIndex(DataContract.Refund.COL_EMPLOYEE)));
                object.put(DataContract.Refund.COL_REFUND_DATE, cursor.getString(cursor.getColumnIndex(DataContract.Refund.COL_REFUND_DATE)));
                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return array;
    }

    private JSONArray parseRefundDetails(Cursor cursor) {
        JSONArray array = new JSONArray();

        while (cursor.moveToNext()) {
            JSONObject object = new JSONObject();
            try {
                object.put(DataContract.RefundDetails.COL_REFUND_NUMBER, cursor.getString(cursor.getColumnIndex(DataContract.RefundDetails.COL_REFUND_NUMBER)));
                object.put(DataContract.RefundDetails.COL_INVOICE_NUMBER, cursor.getString(cursor.getColumnIndex(DataContract.RefundDetails.COL_INVOICE_NUMBER)));
                object.put(DataContract.RefundDetails.COL_ITEM_CODE, cursor.getString(cursor.getColumnIndex(DataContract.RefundDetails.COL_ITEM_CODE)));
                object.put(DataContract.RefundDetails.COL_ITEM_NAME, cursor.getString(cursor.getColumnIndex(DataContract.RefundDetails.COL_ITEM_NAME)));
                object.put(DataContract.RefundDetails.COL_ITEM_PRICE, cursor.getDouble(cursor.getColumnIndex(DataContract.RefundDetails.COL_ITEM_PRICE)));
                object.put(DataContract.RefundDetails.COL_ITEM_QUANTITY, cursor.getInt(cursor.getColumnIndex(DataContract.RefundDetails.COL_ITEM_QUANTITY)));
                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return array;
    }

    private void VolleyErrorHandler(VolleyError error) {
        if (error instanceof TimeoutError) {
            Log.d(TAG, "time out : " + error.getMessage());
        }
        if (error instanceof NoConnectionError) {
            Log.d(TAG, "NoConnectionError : " + error.getMessage());
        }
        if (error instanceof AuthFailureError) {
            Log.d(TAG, "AuthFailureError: " + error.getMessage());
        }
        if (error instanceof ServerError) {
            Log.d(TAG, "ServerError: " + error.getMessage());
        }
        if (error instanceof NetworkError) {
            Log.d(TAG, "NetworkError : " + error.getMessage());
        }
        if (error instanceof ParseError) {
            Log.d(TAG, "ParseError : " + error.getMessage());
        }

    }
}
