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

public class EmployeeActivity extends AppCompatActivity implements FragmentActionListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.emp_container)
    FrameLayout empContainer;

    MenuItem menuItemOk, menuItemDelete;
    String TAG = "EmployeeActivity";
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Employee");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        if (empContainer != null) {
            if (savedInstanceState != null) {
                return;
            }

            UserFragment fragment = new UserFragment();
            fragment.setFragmentActionListener(this);
            fragmentTransaction.add(R.id.emp_container, fragment, null).commit();

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragment_menu, menu);

        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG, "onSupportNavigateUp");

        //fragmentManager.popBackStack();
        onBackPressed();
        return true;
    }

    @Override
    public void onActionPerformed(Bundle bundle) {

        fragmentTransaction = fragmentManager.beginTransaction();
        AddUserFragment addCustomerFragment = new AddUserFragment();
        addCustomerFragment.setArguments(bundle);
        addCustomerFragment.setFragmentActionListener(this);
        fragmentTransaction.replace(R.id.emp_container, addCustomerFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackActionPerformed() {
        onBackPressed();

    }
}
