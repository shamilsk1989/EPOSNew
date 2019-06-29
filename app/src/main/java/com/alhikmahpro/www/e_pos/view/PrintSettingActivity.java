package com.alhikmahpro.www.e_pos.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrintSettingActivity extends AppCompatActivity implements FragmentActionListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    String TAG = "PrintSettingActivity";
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_setting);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        if (findViewById(R.id.print_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            Log.d(TAG, "Loading ItemActivity");
            PrintSettingsFragment printSettingsFragment = new PrintSettingsFragment();
            printSettingsFragment.setFragmentActionListener(this);
            fragmentTransaction.add(R.id.print_container, printSettingsFragment, null).commit();

        }
    }

    @Override
    public void onActionPerformed(Bundle bundle) {
        int actionPerformed = bundle.getInt(FragmentActionListener.KEY_SELECTED_FRAGMENT);
        switch (actionPerformed){
            
            case 1:
                loadPaperSettings(bundle);
                break;
            case 2:
                loadAddPrinter();
                break;
                default:
                    break;
        }
        
    }

    private void loadPaperSettings(Bundle bundle) {

        fragmentTransaction = fragmentManager.beginTransaction();
        PaperSettingsFragment paperSettingsFragment = new PaperSettingsFragment();
       // paperSettingsFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.print_container, paperSettingsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }
    private void loadAddPrinter() {

        Intent intent=new Intent(PrintSettingActivity.this,AddPrinterActivity.class);
        startActivity(intent);



    }
    @Override
    public void onBackActionPerformed() {

    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG, "onSupportNavigateUp");

        //fragmentManager.popBackStack();
        onBackPressed();
        return true;
    }
}
