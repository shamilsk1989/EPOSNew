package com.alhikmahpro.www.e_pos.view;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.alhikmahpro.www.e_pos.AppUtils;
import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.adapter.SpinnerAdapter;
import com.alhikmahpro.www.e_pos.data.CartData;
import com.alhikmahpro.www.e_pos.data.DataContract;
import com.alhikmahpro.www.e_pos.data.ItemsModel;
import com.alhikmahpro.www.e_pos.data.SessionHandler;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;
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
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
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
public class PaymentFragment extends Fragment implements AdapterView.OnItemSelectedListener ,View.OnClickListener{


    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.btn_pay)
    Button btnPay;

    @BindView(R.id.txtTotal)
    TextView txtTotal;
    @BindView(R.id.txtEmployee)
    TextView txtEmployee;
    @BindView(R.id.txtCustomerName)
    TextView txtCustomerName;
    @BindView(R.id.txtPosNo)
    TextView txtPosNo;
    @BindView(R.id.edit_txt_pay)
    EditText editTxtPay;
    @BindView(R.id.txtUser)
    TextView txtUser;
    @BindView(R.id.txtNet)
    TextView txtNet;
    Unbinder unbinder;

    double discount = 0, Total, gTotal, paid, discountAmount;
    dbHelper helper;
    String customerName,cMob,mDate,invoiceNumber,deviceNo;
    static String TAG = "PaymentFragment";

    FragmentActionListener fragmentActionListener;
    static int ACTION_VALUE_FRAGMENT = 3;
    ProgressDialog progressDialog;



    public PaymentFragment() {
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
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        unbinder = ButterKnife.bind(this, view);
        helper = new dbHelper(getContext());

        customerName = getArguments().getString("CUS_NAME");
        cMob = getArguments().getString("CUS_MOB");
        String employeeName = getArguments().getString("EMP_NAME");
        String posNo = SessionHandler.getInstance(getContext()).getDeviceNo();
        Total = getArguments().getDouble("TOTAL");

        Log.d(TAG, "mobile number: "+cMob);

        txtCustomerName.setText(customerName+"  "+cMob);
        txtEmployee.setText(employeeName);
        txtPosNo.setText(posNo);
        txtTotal.setText(CurrencyFormatter(Total));
        txtNet.setText(CurrencyFormatter(Total));
        editTxtPay.setText(String.valueOf(Total));
        mDate = AppUtils.getDateTime();
        deviceNo = SessionHandler.getInstance(getContext()).getDeviceNo();
        progressDialog=new ProgressDialog(getActivity());
        loadSpinner();
        //grandTotal(total);

        editTxtPay.setOnClickListener(this);

        return view;
    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    private void grandTotal() {

        Log.d(TAG, "Total :" + Total);
        discountAmount = Total * (discount / 100);
        gTotal = Total - discountAmount;
        Log.d(TAG, "gTotal :" + gTotal);
        txtNet.setText(CurrencyFormatter(gTotal));
        editTxtPay.setText(String.valueOf(gTotal));


    }

    private void loadSpinner() {

        List<ItemsModel> tempList;
        ArrayList<String> Offer = new ArrayList<>();
        ArrayList<Double> Value = new ArrayList<>();
        Offer.add(0, "No Discount");
        Value.add(0, 0.0);
        helper = new dbHelper(getContext());
        tempList = helper.getItemByCategory("discount");
        for (ItemsModel model : tempList) {

            Offer.add(model.getItemName());
            Value.add(model.getItemPrice());


        }


//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Employee);
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(arrayAdapter);
//        spinner.setOnItemSelectedListener(this);

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getContext(), Offer, Value);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_pay)
    public void onViewClicked() {

        //hide soft key
        editTxtPay.onEditorAction(EditorInfo.IME_ACTION_DONE);
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTxtPay.getWindowToken(), 0);

        //Generate new Invoice number

        int inv_id = helper.getLastInvoiceId() + 1;
        inv_id = inv_id +1000;
        String type = "INV";
        invoiceNumber= type + "-" + deviceNo + "-" + inv_id;


        if (validate( editTxtPay.getText().toString())) {
            paid = Double.valueOf( editTxtPay.getText().toString());

            if(checkNetwork()){
                saveAppServer();
            }
            else{
                if(saveToLocalDataBase(DataContract.SYNC_STATUS_FAILED)){

                    Toast.makeText(getContext(), "invoice saved", Toast.LENGTH_LONG).show();
                    gotoNext();
                }
            }
        }
    }


    private void gotoNext(){
        SessionHandler.getInstance(getContext()).resetCustomer();
        Bundle bundle = new Bundle();
        bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT, ACTION_VALUE_FRAGMENT);
        bundle.putString("Invoice", invoiceNumber);
        bundle.putDouble("Total", Total);
        bundle.putDouble("gTotal", gTotal);
        bundle.putDouble("Paid", paid);
        bundle.putString("CUS_NAME",customerName);
        bundle.putString("MOBILE",cMob);
        bundle.putString("EMP_NAME",txtEmployee.getText().toString());
        fragmentActionListener.onActionPerformed(bundle);

    }
    private boolean saveToLocalDataBase(int sync){
        Log.d(TAG, "saveToLocalDataBase: ");

        boolean stat=false;
        boolean inv_res = helper.addInvoice(invoiceNumber, Total, gTotal, discount,paid,customerName, txtEmployee.getText().toString(), mDate,sync);
        if(inv_res){
            boolean res = helper.addInvoiceDetails(invoiceNumber, mDate);
            if(res){
                stat=true;
            }
        }
        return stat;

    }

    private void saveAppServer(){

        String Url=DataContract.SERVER_URL+"/"+"Invoice";

        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Saving..");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Log.d(TAG, "saveAppServer: ");
        JSONArray detailsArray=new JSONArray();
        JSONArray array=new JSONArray();
        JSONObject invoice=new JSONObject();

        JSONObject result=new JSONObject();
        try {
            invoice.put(DataContract.Invoice.COL_INVOICE_NUMBER,invoiceNumber);
            invoice.put(DataContract.Invoice.COL_TOTAL,Total);
            invoice.put(DataContract.Invoice.COL_DISCOUNT,discount);
            invoice.put(DataContract.Invoice.COL_GRAND_TOTAL,gTotal);
            invoice.put(DataContract.Invoice.COL_CASH,paid);
            invoice.put(DataContract.Invoice.COL_CUSTOMER,customerName);
            invoice.put(DataContract.Invoice.COL_EMPLOYEE,txtEmployee.getText().toString());
            invoice.put(DataContract.Invoice.COL_INVOICE_DATE,mDate);
            array.put(invoice);
            for (ItemsModel model: CartData.mCartData){
                JSONObject object=new JSONObject();
                object.put(DataContract.InvoiceDetails.COL_INVOICE_NUMBER,invoiceNumber);
                object.put(DataContract.InvoiceDetails.COL_ITEM_CODE,model.getItemCode());
                object.put(DataContract.InvoiceDetails.COL_ITEM_NAME,model.getItemName());
                object.put(DataContract.InvoiceDetails.COL_ITEM_QUANTITY,model.getQuantity());
                object.put(DataContract.InvoiceDetails.COL_ITEM_PRICE,model.getItemPrice());
                object.put(DataContract.InvoiceDetails.COL_TOTAL, model.getQuantity() * model.getItemPrice());
                object.put(DataContract.InvoiceDetails.COL_INVOICE_DATE,mDate);
                detailsArray.put(object);

            }

            result.put("Invoice",array);
            result.put("Details",detailsArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "saveAppServer: "+result);


        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST,Url, result, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.cancel();

                Log.d(TAG, "onResponse: "+response);

                try {

                    String res=response.getString("Status");
                    if(res.equals("OK")){
                        if(saveToLocalDataBase(DataContract.SYNC_STATUS_OK)){
                            gotoNext();

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
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String>headers=new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("deviceid",deviceNo);
                headers.put("branchcode",SessionHandler.getInstance(getContext()).getShopName());
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(20*1000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);
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
    private boolean validate(String amount) {
        if (TextUtils.isEmpty(amount)) {
            editTxtPay.setError("Enter amount");
            return false;
        }else if(Double.valueOf(amount)<gTotal){
            editTxtPay.setError("Enter valid amount");
            return false;
        }
        return true;
    }

    private boolean checkNetwork(){
        ConnectivityManager connectivityManager=(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return (networkInfo!=null && networkInfo.isConnected());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String offer = ((TextView) view.findViewById(R.id.name)).getText().toString();
        Log.d(TAG, "name :" + offer);
        if (spinner.getSelectedItemPosition() == 0) {
            discount = 0;
            grandTotal();
        } else {
            String val = ((TextView) view.findViewById(R.id.value)).getText().toString();
            discount = Double.valueOf(val);
            Log.d(TAG, "value :" + discount);
            grandTotal();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItemCart = menu.findItem(R.id.action_cart);
        menuItemCart.setVisible(false);
        MenuItem menuItemSearch = menu.findItem(R.id.action_search);
        menuItemSearch.setVisible(false);
        MenuItem menuItemPerson = menu.findItem(R.id.action_person);
        menuItemPerson.setVisible(false);
        MenuItem menuItemDelete = menu.findItem(R.id.action_delete);
        menuItemDelete.setVisible(false);


    }

    public String CurrencyFormatter(double val) {
        Log.d(TAG, "val" + val);
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
        System.out.println(nf.format(val).trim());
        String ft = nf.format(val).trim();
        return ft;

    }


    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick listener" );
        editTxtPay.getText().clear();
        editTxtPay.setFocusable(true);
        editTxtPay.setFocusableInTouchMode(true);
        editTxtPay.requestFocus();

    }
}
