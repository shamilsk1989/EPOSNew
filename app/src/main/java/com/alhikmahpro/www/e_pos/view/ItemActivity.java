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

public class ItemActivity extends AppCompatActivity implements FragmentActionListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.item_container)
    FrameLayout itemContainer;
    String TAG = "ItemActivity";
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    MenuItem menuItemOk, menuItemDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        if (findViewById(R.id.item_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            Log.d(TAG, "Loading ItemActivity");
            ItemFragment itemFragment = new ItemFragment();
            itemFragment.setFragmentActionListener(this);
            fragmentTransaction.add(R.id.item_container, itemFragment, null).commit();

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragment_menu, menu);
        menuItemOk = menu.findItem(R.id.action_refresh);
        menuItemDelete = menu.findItem(R.id.action_delete);
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



        int actionPerformed = bundle.getInt(FragmentActionListener.KEY_SELECTED_FRAGMENT);
     //   Log.d(TAG, "Listener clicked" + fragmentManager.getBackStackEntryCount());
        Log.d(TAG, "Listener clicked" + actionPerformed);
        switch (actionPerformed) {
            case 1:
                addViewItemFragment(bundle);//from ItemFragment
                break;
            case 2:
                addCategoryFragment(bundle);////from ItemFragment
                break;
            case 3:
                addCustomerFragment(bundle);////from ItemFragment
                break;
            case 4:
                addAddItemFragment(bundle);
                break;
            case 5:
                addAddCategoryFragment(bundle);
                break;
            case 6:
                addAddCustomerFragment(bundle);
                break;
            case 7:
                addDiscountFragment(bundle);
                break;

        }


    }

    private void addAddCustomerFragment(Bundle bundle) {

        fragmentTransaction = fragmentManager.beginTransaction();
        AddCustomerFragment addCustomerFragment = new AddCustomerFragment();
        addCustomerFragment.setArguments(bundle);
        addCustomerFragment.setFragmentActionListener(this);
        fragmentTransaction.replace(R.id.item_container, addCustomerFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    private void addDiscountFragment(Bundle bundle) {
        fragmentTransaction = fragmentManager.beginTransaction();
        AddDiscountFragment addDiscountFragment = new AddDiscountFragment();
        addDiscountFragment.setArguments(bundle);
        addDiscountFragment.setFragmentActionListener(this);
        fragmentTransaction.replace(R.id.item_container,addDiscountFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    private void addAddItemFragment(Bundle bundle) {
        fragmentTransaction = fragmentManager.beginTransaction();
        AddItemsFragment addItemsFragment = new AddItemsFragment();
        addItemsFragment.setArguments(bundle);
        addItemsFragment.setFragmentActionListener(this);
        fragmentTransaction.replace(R.id.item_container, addItemsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    private void addAddCategoryFragment(Bundle bundle) {
        fragmentTransaction = fragmentManager.beginTransaction();
        AddCategoryFragment addCategoryFragment = new AddCategoryFragment();
        addCategoryFragment.setArguments(bundle);
        addCategoryFragment.setFragmentActionListener(this);
        fragmentTransaction.replace(R.id.item_container, addCategoryFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    private void addCustomerFragment(Bundle bundle) {

        fragmentTransaction = fragmentManager.beginTransaction();
        CustomerFragment customerFragment = new CustomerFragment();
        customerFragment.setFragmentActionListener(this);
        customerFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.item_container, customerFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void addCategoryFragment(Bundle bundle) {
        fragmentTransaction = fragmentManager.beginTransaction();

        CategoryFragment categoryFragment = new CategoryFragment();
        categoryFragment.setFragmentActionListener(this);
        categoryFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.item_container, categoryFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    private void addViewItemFragment(Bundle bundle) {
        fragmentTransaction = fragmentManager.beginTransaction();
        ViewItemFragment viewItemFragment = new ViewItemFragment();
        viewItemFragment.setFragmentActionListener(this);
        viewItemFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.item_container, viewItemFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    @Override
    public void onBackActionPerformed() {
        onBackPressed();

    }
}
