package com.alhikmahpro.www.e_pos.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.CartData;
import com.alhikmahpro.www.e_pos.data.DataContract;
import com.alhikmahpro.www.e_pos.data.ItemsModel;
import com.alhikmahpro.www.e_pos.data.PrinterCommands;
import com.alhikmahpro.www.e_pos.data.SessionHandler;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import butterknife.BindView;
import butterknife.ButterKnife;


public class InvoiceActivity extends AppCompatActivity implements FragmentActionListener {
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.invoice_container)
    FrameLayout invoiceContainer;
    String TAG = "InvoiceActivity";
    String customer, employee, Pos, customerNumber;
    String type = "INV";
    boolean is_printFirst;
    dbHelper helper;
    //BroadcastReceiver broadcastReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Pos = SessionHandler.getInstance(getApplication()).getDeviceNo();
        is_printFirst = true;
        helper = new dbHelper(this);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if (findViewById(R.id.invoice_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            ViewInvoiceFragment viewInvoiceFragment = new ViewInvoiceFragment();
            Bundle bundle = new Bundle();
            bundle.putString("TYPE", type);
            viewInvoiceFragment.setArguments(bundle);
            viewInvoiceFragment.setFragmentActionListener(this);
            fragmentTransaction.add(R.id.invoice_container, viewInvoiceFragment, null).commit();
        }
//        broadcastReceiver=new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                loadFragment();
//
//            }
//        };

    }

    private void loadFragment(){

        Toast.makeText(this, "updated", Toast.LENGTH_SHORT).show();


        }


    @Override
    public void onActionPerformed(Bundle bundle) {

        int action=bundle.getInt(FragmentActionListener.KEY_SELECTED_FRAGMENT);
        switch (action){
            case 1:

                fragmentTransaction = fragmentManager.beginTransaction();
                EditInvoiceFragment editInvoiceFragment = new EditInvoiceFragment();
                editInvoiceFragment.setFragmentActionListener(this);
                editInvoiceFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.invoice_container, editInvoiceFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 2:
                customer = bundle.getString("CUSTOMER");
                customerNumber = helper.getCustomerMobile(customer);
                bundle.putString("MOBILE", customerNumber);
                fragmentTransaction = fragmentManager.beginTransaction();
                RefundFragment refundFragment = new RefundFragment();
                refundFragment.setFragmentActionListener(this);
                refundFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.invoice_container, refundFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
                default:
        }
    }


    @Override
    public void onBackActionPerformed() {
        clearStack();
    }

    private void clearStack() {
        CartData.mCartData.clear();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sync_menu, menu);

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
      //  registerReceiver(broadcastReceiver,new IntentFilter(DataContract.UI_UPDATE_BROADCAST));

    }

    @Override
    protected void onPause() {
        super.onPause();
       // unregisterReceiver(broadcastReceiver);
    }
}
