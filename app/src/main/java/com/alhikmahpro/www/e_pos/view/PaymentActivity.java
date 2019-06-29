package com.alhikmahpro.www.e_pos.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;

import com.alhikmahpro.www.e_pos.R;

import butterknife.BindView;
import butterknife.ButterKnife;



public class PaymentActivity extends AppCompatActivity {

    @BindView(R.id.payment_container)
    FrameLayout paymentContainer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fragmentManager=getSupportFragmentManager();
        if(findViewById(R.id.item_container)!=null){
            if(savedInstanceState!=null){
                return;
            }

            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            PaymentFragment paymentFragment=new PaymentFragment();
            fragmentTransaction.add(R.id.item_container,paymentFragment,null).commit();

        }
    }
}
