package com.alhikmahpro.www.e_pos.view;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.adapter.UserAdapter;
import com.alhikmahpro.www.e_pos.data.SessionHandler;
import com.alhikmahpro.www.e_pos.data.UserModel;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.alhikmahpro.www.e_pos.interfaces.OnAdapterItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListCustomerFragment extends DialogFragment {


    @BindView(R.id.editSearch)
    EditText editSearch;
    @BindView(R.id.rvCustomerList)
    RecyclerView rvCustomerList;
    Unbinder unbinder;
    //RecyclerView.Adapter adapter;
    UserAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    List<UserModel> adapterList = new ArrayList<>();
    String cusName,cusMobile;
    String TAG = "ListCustomerFragment";
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    public ListCustomerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_customer, container, false);
        unbinder = ButterKnife.bind(this, view);

        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog=getDialog();
                dialog.dismiss();
            }
        });
        toolbar.setTitle("Customers");
        LoadRecyclerView();



        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {



            }

            @Override
            public void afterTextChanged(Editable s) {

                //filter(s.toString());
                adapter.getFilter().filter(s.toString());
                //return false;


            }
        });


        return view;
    }



    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void LoadRecyclerView() {

        layoutManager = new LinearLayoutManager(getActivity());
        rvCustomerList.setLayoutManager(layoutManager);
        rvCustomerList.setItemAnimator(new DefaultItemAnimator());
        rvCustomerList.setHasFixedSize(true);
        dbHelper helper = new dbHelper(getContext());
        adapterList = helper.GetAllCustomer();

        adapter = new UserAdapter(adapterList, new OnAdapterItemClickListener() {
            @Override
            public void OnItemClicked(int position) {
                UserModel model = adapterList.get(position);
                cusName=model.getName();
                cusMobile=model.getMobile();
                editSearch.setText(cusName);
                Log.d(TAG, "OnItemClicked: "+cusMobile);

                SessionHandler.getInstance(getContext()).setCustomer(cusName,cusMobile);
                Toast.makeText(getContext(),cusName+" Selected",Toast.LENGTH_LONG).show();
                Log.d(TAG, "sharedpreference: "+SessionHandler.getInstance(getContext()).getCustomerMobile());
                Dialog dialog=getDialog();
                dialog.dismiss();




            }
        });
        rvCustomerList.setAdapter(adapter);

    }


    private void filter(String s) {
        ArrayList<UserModel>arrayList=new ArrayList<>();
        for (UserModel model:adapterList) {

            if(model.getName().toLowerCase().contains(s.toLowerCase())){
                arrayList.add(model);

            }
            
        }
        adapter.filterList(arrayList);
    }

}
