package com.alhikmahpro.www.e_pos.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReturnActivity extends AppCompatActivity  implements FragmentActionListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.return_container)
    FrameLayout returnContainer;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;
    String type="RET";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        if (findViewById(R.id.return_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
          //  Log.d(TAG,"inside container");

            ViewInvoiceFragment viewInvoiceFragment = new ViewInvoiceFragment();
            Bundle bundle=new Bundle();
            bundle.putString("TYPE",type);
            viewInvoiceFragment.setArguments(bundle);
            viewInvoiceFragment.setFragmentActionListener(this);
            fragmentTransaction.add(R.id.return_container, viewInvoiceFragment, null).commit();

        }
    }

    @Override
    public void onActionPerformed(Bundle bundle) {

        fragmentTransaction=fragmentManager.beginTransaction();
        EditInvoiceFragment editInvoiceFragment=new EditInvoiceFragment();
        editInvoiceFragment.setFragmentActionListener(this);
        editInvoiceFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.return_container,editInvoiceFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackActionPerformed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sync_menu,menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
