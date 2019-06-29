package com.alhikmahpro.www.e_pos.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.SessionHandler;
import com.alhikmahpro.www.e_pos.data.UserModel;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;
import com.alhikmahpro.www.e_pos.network.VolleySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginActivity extends AppCompatActivity implements FragmentActionListener {


    @BindView(R.id.login_container)
    FrameLayout loginContainer;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    List<UserModel> arrayList;
    private ConnectivityManager connectivityManager;

    String TAG = "LoginActivity";

    String deviceID, pin, shopName;
    ProgressDialog progressDialog;
    String AndroidDeviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        arrayList = new ArrayList<>();
        progressDialog = new ProgressDialog(this );
        AndroidDeviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);




        if (findViewById(R.id.login_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            SignInFragment signInFragment = new SignInFragment();
            signInFragment.setFragmentActionListener(this);
            fragmentTransaction.add(R.id.login_container, signInFragment, null).commit();

        }


    }


    @Override
    public void onActionPerformed(Bundle bundle) {

        closeKey();
        int actionPerformed = bundle.getInt(FragmentActionListener.KEY_SELECTED_FRAGMENT);
        switch (actionPerformed) {
            case 1:
                addSignUpFragment();
                break;
            case 2:
                Register(bundle);
                break;
            case 3:
                SignIn(bundle);
                break;

        }
    }


    @Override
    public void onBackActionPerformed() {

    }


    private void addSignUpFragment() {

        fragmentTransaction = fragmentManager.beginTransaction();
        SignUpFragment signUpFragment = new SignUpFragment();
        signUpFragment.setFragmentActionListener(this);
        fragmentTransaction.replace(R.id.login_container, signUpFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void Register(Bundle bundle) {

        String serverUrl = "http://samarecycle.com/EPOS/Registration.php";

        Log.d(TAG, "Device number :" + AndroidDeviceId);
        shopName = bundle.getString("NAME");
//        UserModel model = new UserModel();
//        model.setName(shopName);
//        model.setEmail(bundle.getString("EMAIL"));
//        model.setDeviceId(AndroidDeviceId);
//        model.setPassword(bundle.getString("PASSWORD"));

       // arrayList.add(model);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {


            progressDialog.setMessage("Registering...");
            progressDialog.show();

            //Gson gson = new Gson();
            //final String newDataArray = gson.toJson(arrayList);
            // Register(newDataArray);
            final JSONObject req = new JSONObject();
            try {
                req.put("shopName", shopName);
                req.put("Email", bundle.getString("EMAIL"));
                req.put("Password",bundle.getString("PASSWORD"));
                req.put("deviceId",AndroidDeviceId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG,"sending data :"+req);


            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, serverUrl, req,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d(TAG,"Response "+response);
                            try {
                                String res=response.getString("Status");
                                String device_id=response.getString("Id");
                                Log.d(TAG,"Response "+res+device_id);
                                if(res.contains("exists")){
                                    showAlert("Email Id already exists");

                                }
                                else if(res.contains("success")){
                                    SessionHandler.getInstance(getApplication()).setDeviceNo(device_id);
                                    SessionHandler.getInstance(getApplication()).setUserType("Owner");
                                    SessionHandler.getInstance(getApplication()).setShopName(shopName);
                                    gotoHome();
                                }
                                else {
                                    showAlert("Something went wrong please try again later!");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG,"Error");
                    error.printStackTrace();
                    progressDialog.dismiss();

                }
            });

            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

        } else {
            arrayList.clear();
            Snackbar.make(findViewById(R.id.loginActivity), "No internet connection", Snackbar.LENGTH_LONG).show();
        }
    }


    private void SignIn(Bundle bundle) {



        final String email = (bundle.getString("EMAIL"));
        final String pass = (bundle.getString("PASSWORD"));
        String Url = "http://samarecycle.com/EPOS/login.php";


        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            progressDialog.setMessage("Login....");
            progressDialog.show();

            final JSONObject req = new JSONObject();
            try {
                req.put("email", email);
                req.put("password", pass);
                req.put("deviceId",AndroidDeviceId);
            } catch (Exception e) {
                e.printStackTrace();
            }
           // Log.d(TAG, "sending to server : " + req);


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Url,req,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();

                            Log.d("response", "response : " + response); //when response come i will log it
                            try {
                                //VolleyLog.v("Response:%n %s", response.toString(4));
                                if(response.length()==0){
                                    showAlert("Wrong Details..");
                                }
                                else{
                                    shopName=response.getString("Name");
                                    SessionHandler.getInstance(getApplication()).setUserType("Admin");
                                    SessionHandler.getInstance(getApplication()).setDeviceNo(response.getString("Id"));
                                    SessionHandler.getInstance(getApplication()).setShopName(shopName);

                                    gotoHome();

                                }
                                //Log.d(TAG,"response from server:"+response.getString("Id")+"/"+response.getString("Name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    showAlert("something went wrong");
                    VolleyLog.e("Error: ", error.getMessage());
                }
            });

            request.setRetryPolicy(new DefaultRetryPolicy(8000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

        } else {
            Snackbar.make(findViewById(R.id.loginActivity), "No internet connection", Snackbar.LENGTH_LONG).show();
        }
    }

    private void showAlert(String Message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error Message");
        builder.setMessage(Message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();


            }
        });


        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });
        alertDialog.show();


    }


    private void gotoHome() {
        Log.d(TAG,"GOTO HOME");
        dbHelper helper=new dbHelper(getApplication());
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.epos_icon);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] img_data = stream.toByteArray();
        helper.addBusinessDetails(shopName,img_data);
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }



    private void closeKey(){
        View view=this.getCurrentFocus();
        if(view!=null){
            InputMethodManager methodManager=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            methodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }

    }


}
