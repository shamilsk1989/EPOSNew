package com.alhikmahpro.www.e_pos.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements FragmentActionListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.settings_container)
    FrameLayout settingsContainer;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    String TAG="SettingsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if (findViewById(R.id.settings_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            Log.d(TAG,"inside container");

            SettingsFragment settingsFragment=new SettingsFragment();
            settingsFragment.setFragmentActionListener(this);
            fragmentTransaction.add(R.id.settings_container, settingsFragment, null).commit();

        }


    }

    @Override
    public void onActionPerformed(Bundle bundle) {
        fragmentTransaction = fragmentManager.beginTransaction();
        SetPinFragment setPinFragment=new SetPinFragment();
        setPinFragment.setFragmentActionListener(this);
        setPinFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.settings_container, setPinFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    @Override
    public void onBackActionPerformed() {
        onBackPressed();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
