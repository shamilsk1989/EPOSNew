package com.alhikmahpro.www.e_pos.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.alhikmahpro.www.e_pos.data.DataContract;
import com.alhikmahpro.www.e_pos.data.SessionHandler;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkMonitor extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        JSONArray invoiceDetailsArray = new JSONArray();
        JSONArray invoiceArray = new JSONArray();
        JSONArray refundArray = new JSONArray();
        JSONArray refundDetailsArray = new JSONArray();


        if(checkNetwork(context)){

            dbHelper helper=new dbHelper(context);
            SQLiteDatabase database=helper.getReadableDatabase();

            //select all non send data from invoice table
            Cursor cursorInvoice=helper.GetUnSyncInvoice(database);
            Log.d("Broadcast", "cursor size: "+cursorInvoice.getCount());
            if(cursorInvoice!=null){
                invoiceArray=parseInvoice(cursorInvoice);
                while (cursorInvoice.moveToNext()){
                Log.d("Broadcast", "onReceive: "+cursorInvoice.getString(cursorInvoice.getColumnIndex(DataContract.Invoice.COL_INVOICE_NUMBER)));

            }}
            cursorInvoice.close();

           // select all non send data from invoice_details table
            Cursor cursorinvoiceDetails=helper.GetUnSyncInvoiceDetails(database);
            if(cursorinvoiceDetails!=null){
                invoiceDetailsArray=parseInvoiceDetails(cursorinvoiceDetails);

            }
            cursorinvoiceDetails.close();

           // select all non send data from refund table
//            Cursor cursorRefund=helper.GetUnSyncRefund(database);
//            if(cursorRefund!=null){
//                refundArray=parseInvoiceDetails(cursorinvoiceDetails);
//            }
//            cursorinvoiceDetails.close();



            JSONObject result=new JSONObject();

            try {
                result.put("Invoice",invoiceArray);
                result.put("Details",invoiceDetailsArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            sendToServer(context,result);




        }

    }

    private JSONArray parseInvoiceDetails(Cursor cursor){
        JSONArray array = new JSONArray();

        try {
            while (cursor.moveToNext()){
                JSONObject object=new JSONObject();
                object.put(DataContract.InvoiceDetails.COL_INVOICE_NUMBER,cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_INVOICE_NUMBER)));
                object.put(DataContract.InvoiceDetails.COL_ITEM_CODE,cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_ITEM_CODE)));
                object.put(DataContract.InvoiceDetails.COL_ITEM_NAME,cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_ITEM_NAME)));
                object.put(DataContract.InvoiceDetails.COL_ITEM_QUANTITY,cursor.getInt(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_ITEM_QUANTITY)));
                object.put(DataContract.InvoiceDetails.COL_ITEM_PRICE,cursor.getDouble(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_ITEM_PRICE)));
                object.put(DataContract.InvoiceDetails.COL_TOTAL,cursor.getDouble(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_ITEM_PRICE)));
                object.put(DataContract.InvoiceDetails.COL_INVOICE_DATE,cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_INVOICE_DATE)));

                array.put(object);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return array;

    }



    private JSONArray parseInvoice(Cursor cursor){
        JSONArray array = new JSONArray();

        while (cursor.moveToNext()){
            JSONObject invoice=new JSONObject();
            try {
                invoice.put(DataContract.Invoice.COL_INVOICE_NUMBER,cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_NUMBER)));
                invoice.put(DataContract.Invoice.COL_TOTAL,cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_TOTAL)));
                invoice.put(DataContract.Invoice.COL_DISCOUNT,cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_DISCOUNT)));
                invoice.put(DataContract.Invoice.COL_GRAND_TOTAL,cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_GRAND_TOTAL)));
                invoice.put(DataContract.Invoice.COL_CASH,cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_CASH)));
                invoice.put(DataContract.Invoice.COL_CUSTOMER,cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_CUSTOMER)));
                invoice.put(DataContract.Invoice.COL_EMPLOYEE,cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_EMPLOYEE)));
                invoice.put(DataContract.Invoice.COL_INVOICE_DATE,cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_DATE)));
                array.put(invoice);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return array;


    }



    private JSONArray parseRefundDetails(Cursor cursor){
        JSONArray array = new JSONArray();

        try {
            while (cursor.moveToNext()){
                JSONObject object=new JSONObject();
                object.put(DataContract.Refund.COL_INVOICE_NUMBER,cursor.getColumnIndex(DataContract.Refund.COL_INVOICE_NUMBER));
                object.put(DataContract.InvoiceDetails.COL_ITEM_CODE,cursor.getColumnIndex(DataContract.InvoiceDetails.COL_ITEM_CODE));
                object.put(DataContract.InvoiceDetails.COL_ITEM_NAME,cursor.getColumnIndex(DataContract.InvoiceDetails.COL_ITEM_NAME));
                object.put(DataContract.InvoiceDetails.COL_ITEM_QUANTITY,cursor.getColumnIndex(DataContract.InvoiceDetails.COL_ITEM_QUANTITY));
                object.put(DataContract.InvoiceDetails.COL_ITEM_PRICE,cursor.getColumnIndex(DataContract.InvoiceDetails.COL_ITEM_PRICE));
                object.put(DataContract.InvoiceDetails.COL_TOTAL,cursor.getColumnIndex(DataContract.InvoiceDetails.COL_ITEM_PRICE));
                object.put(DataContract.InvoiceDetails.COL_INVOICE_DATE,cursor.getColumnIndex(DataContract.InvoiceDetails.COL_INVOICE_DATE));

                array.put(object);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return array;

    }





    private boolean checkNetwork(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return (networkInfo!=null && networkInfo.isConnected());
    }



    private void sendToServer(final Context context, JSONObject jsonObject){

        Log.d("Broadcast", "sendToServer: "+jsonObject);

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, DataContract.SERVER_URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Broadcast", "onResponse: "+response);
                try {
                    String res=response.getString("Status");
                    if(res.equals("OK")){

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                dbHelper helper=new dbHelper(context);
                                helper.updateInvoiceSync(DataContract.SYNC_STATUS_OK) ;
                                helper.updateInvoiceDetailsSync(DataContract.SYNC_STATUS_OK);

                            }
                        }).start();


                        //context.sendBroadcast(new Intent(DataContract.UI_UPDATE_BROADCAST));


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers=new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("deviceid",SessionHandler.getInstance(context).getDeviceNo());
                headers.put("companycode",SessionHandler.getInstance(context).getShopName());
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(20*1000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(request);

    }

    private void updateLocalDatabase() {

    }
}
