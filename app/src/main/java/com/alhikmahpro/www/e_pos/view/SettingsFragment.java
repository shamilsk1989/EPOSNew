package com.alhikmahpro.www.e_pos.view;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.SessionHandler;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    FragmentActionListener fragmentActionListener;
    @BindView(R.id.switch1)
    Switch switch1;
    Unbinder unbinder;
    String TAG = "SettingsFragment";
    boolean user;
    Bundle bundle;
    dbHelper helper;


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        unbinder = ButterKnife.bind(this, view);
        bundle = new Bundle();
        helper = new dbHelper(getContext());
        user = helper.getAllLogin("User");
        Log.d(TAG, "session" + user);
        switch1.setOnCheckedChangeListener(null);
        if (user) {
            switch1.setChecked(true);
        }


        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user) {
                    disableMultiUser();
                } else {
                    enableMultiUser();
                }

            }
        });


//        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                if(switch1.isChecked()){
//
//
//                    Log.d(TAG,"already on");
//
////                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
////                    builder.setTitle("Warning");
////                    builder.setMessage("Are you sure to remove the user!");
////                    builder.setCancelable(false);
////                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
////                        @Override
////                        public void onClick(DialogInterface dialog, int which) {
////
////                            boolean delete=helper.deleteUser("User");
////                            if(delete){
////
////                                Toast.makeText(getContext(),"multi user disabled",Toast.LENGTH_LONG).show();
////                                //fragmentActionListener.onBackActionPerformed();
////                            }
////
////
////                        }
////
////
////                    });
////                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
////                        @Override
////                        public void onClick(DialogInterface dialog, int which) {
////                            dialog.cancel();
////
////
////                        }
////                    });
////
////
////
////                    final AlertDialog alertDialog = builder.create();
////
////                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
////                        @Override
////                        public void onShow(DialogInterface dialog) {
////                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
////                            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
////                        }
////                    });
////                    alertDialog.show();
//
//                }
//
//                checkUsers();
//
//                //Toast.makeText(getContext(),"switched to multi user",Toast.LENGTH_LONG).show();
//            }
//        });

        return view;


    }

    @Override
    public void onResume() {
        Log.d(TAG, " on resume");
        switch1.setOnCheckedChangeListener(null);

        if (!user) {
            switch1.setChecked(false);
        }

        super.onResume();
    }

    private void enableMultiUser() {

        boolean owner = helper.getAllLogin("Owner");
        if (!owner) {

            Log.d(TAG, "no user set");
            showAlert("Before activating MultiUser set pin for Owner");

        } else {

            if (!user) {
                bundle.putString("TITLE", "User");
                fragmentActionListener.onActionPerformed(bundle);
            }

            Log.d(TAG, " user set");
        }


    }

    private void disableMultiUser() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Warning");
        builder.setMessage("Are you sure to remove the user!");
        builder.setCancelable(false);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                boolean delete = helper.deleteUser("User");
                if (delete) {
                    switch1.setChecked(false);
                    Toast.makeText(getContext(), "multi user disabled", Toast.LENGTH_LONG).show();
                    //fragmentActionListener.onBackActionPerformed();
                }


            }


        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch1.setChecked(true);
                dialog.cancel();


            }
        });


        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            }
        });
        alertDialog.show();

    }


    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }


    private void showAlert(String Message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Message");
        builder.setMessage(Message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                bundle.putString("TITLE", "Owner");
                fragmentActionListener.onActionPerformed(bundle);


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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
