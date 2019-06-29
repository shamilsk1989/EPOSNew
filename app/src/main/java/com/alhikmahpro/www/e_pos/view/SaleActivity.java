package com.alhikmahpro.www.e_pos.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.CartData;
import com.alhikmahpro.www.e_pos.data.SessionHandler;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SaleActivity extends AppCompatActivity  implements FragmentActionListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.sale_container)
    FrameLayout saleContainer;
    String TAG="SaleActivity";
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState)   {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fragmentManager=getSupportFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();

        if(findViewById(R.id.sale_container)!=null){
            if(savedInstanceState!=null){
                return;
            }
            SaleFragment saleFragment=new SaleFragment();
            saleFragment.setFragmentActionListener(this);
            fragmentTransaction.add(R.id.sale_container,saleFragment,null).commit();

        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.d(TAG,"inside menu ");
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG,"onSupportNavigateUp");

        //fragmentManager.popBackStack();
        onBackPressed();
        return true;
    }


    @Override
    public void onBackPressed() {


        int index=fragmentManager.getBackStackEntryCount();
        if(index>2){
            clearStack();
        }
        else{
            super.onBackPressed();
        }

    }

    @Override
    public void onActionPerformed(Bundle bundle) {

        Log.d(TAG,"Listener clicked"+fragmentManager.getBackStackEntryCount());

        int actionPerformed = bundle.getInt(FragmentActionListener.KEY_SELECTED_FRAGMENT);
        switch (actionPerformed){
            case 1:
                addCartFragment(bundle);
                break;

            case 2:
                addPaymentFragment(bundle);
                break;
            case 3:
                addSuccessfulFragment(bundle);
                break;
        }


    }

    @Override
    public void onBackActionPerformed() {
        clearStack();
    }

    private void clearStack(){
//        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
//            fragmentManager.popBackStack();
       // }

        SessionHandler.getInstance(getApplication()).resetCustomer();

        CartData.mCartData.clear();
        Log.d(TAG,"clear stack"+CartData.mCartData.size());
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void addCartFragment(Bundle bundle){

        fragmentTransaction=fragmentManager.beginTransaction();

        CartFragment cartFragment=new CartFragment();
        cartFragment.setFragmentActionListener(this);
        cartFragment.setArguments(bundle);
       // cartFragment.setC
        fragmentTransaction.replace(R.id.sale_container,cartFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    private void addPaymentFragment(Bundle bundle){
        fragmentTransaction=fragmentManager.beginTransaction();
        PaymentFragment paymentFragment=new PaymentFragment();
        paymentFragment.setFragmentActionListener(this);
        paymentFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.sale_container,paymentFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void addSuccessfulFragment(Bundle bundle){

        fragmentTransaction=fragmentManager.beginTransaction();
        SuccessfulFragment successfulFragment=new SuccessfulFragment();
        successfulFragment.setFragmentActionListener(this);
        successfulFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.sale_container,successfulFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}

