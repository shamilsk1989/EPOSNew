package com.alhikmahpro.www.e_pos.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionHandler {

    private static final String PREF_NAME = "EPOSSession";

    private static final String EMPLOYEE_ID = "employee_id";
    private static final String DEVICE_NO = "device_no";
    private static final String SYNC_ONE_TIME = "sync_one_time";
    private static final String SESSION_START = "session";
    private static final String SHOP_NAME = "shop_name";
    private static final String PRINTER_NAME="printer_name";
    private static final String CUSTOMER_NAME="customer_name";
    private static final String CUSTOMER_MOBILE="customer_mobile";

    private static final String USER_TYPE="user_type";

    private static SessionHandler sInstance;
    private final SharedPreferences mPref;


    public SessionHandler(Context context) {
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionHandler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SessionHandler(context);
        }
        return sInstance;
    }

    public void setEmployeeId(String id){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(EMPLOYEE_ID,id);
        editor.apply();
    }

    public void setUserType(String type){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(USER_TYPE,type);
        editor.apply();
    }
    public String getUserType() {
        return mPref.getString(USER_TYPE,"");
    }

    public void setCustomer(String name,String mobile){

        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(CUSTOMER_NAME,name);
        editor.putString(CUSTOMER_MOBILE,mobile);
        editor.apply();

    }
    public String getCustomerName(){
        return mPref.getString(CUSTOMER_NAME,"");
    }

    public String getCustomerMobile(){
        return mPref.getString(CUSTOMER_MOBILE,"");
    }


    public void resetCustomer(){
        SharedPreferences.Editor editor = mPref.edit();
        editor.remove(CUSTOMER_MOBILE);
        editor.remove(CUSTOMER_NAME);
        editor.apply();
    }



    public String getEmployeeId(){
        return mPref.getString(EMPLOYEE_ID,"");
    }

    public void setDeviceNo(String id){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(DEVICE_NO,id);
        editor.apply();
    }

    public void setShopName(String name){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(SHOP_NAME,name);
        editor.apply();
    }


    public String getShopName(){
        return mPref.getString(SHOP_NAME,"");
    }

    public String getDeviceNo(){
        return mPref.getString(DEVICE_NO,"");
    }

    public boolean isUserloggedIn(){
        return mPref.contains(DEVICE_NO);
    }


    public void setSyncOneTime(boolean id){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(SYNC_ONE_TIME,id);
        editor.apply();
    }

    public boolean isSyncOneTime(){
        return mPref.getBoolean(SYNC_ONE_TIME,false);
    }

    public void setSessionStart(boolean flag){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(SESSION_START,flag);
        editor.apply();
    }

    public boolean isSessionStarted(){
        return mPref.getBoolean(SESSION_START,false);
    }

    public void clearAll(){
        SharedPreferences.Editor editor = mPref.edit();
        editor.clear();
        editor.apply();
    }


    public void resetUser(){
        SharedPreferences.Editor editor = mPref.edit();
        editor.remove(USER_TYPE);
        editor.apply();
    }

    public void setPrinterName(String name){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(PRINTER_NAME,name);
        editor.apply();
    }


    public String getPrinterName(){
        return mPref.getString(PRINTER_NAME,"");
    }
    public void removePrinter(){
        SharedPreferences.Editor editor = mPref.edit();
        editor.remove(PRINTER_NAME);
        editor.apply();
    }



}
