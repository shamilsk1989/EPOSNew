package com.alhikmahpro.www.e_pos.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.adapter.UserAdapter;
import com.alhikmahpro.www.e_pos.data.UserModel;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;
import com.alhikmahpro.www.e_pos.interfaces.OnAdapterItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerFragment extends Fragment {


    @BindView(R.id.customer_list_rv)
    RecyclerView customerListRv;
    @BindView(R.id.btn_fab)
    FloatingActionButton btnFab;
    Unbinder unbinder;
    Toolbar toolbar;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    List<UserModel> adapterList=new ArrayList<>();

    FragmentActionListener fragmentActionListener;
    int selected_id;


    public CustomerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cutomer, container, false);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Customer");
        unbinder = ButterKnife.bind(this, view);
        LoadRecyclerView();



        return view;
    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    private void LoadRecyclerView() {

        layoutManager=new LinearLayoutManager(getActivity());
        customerListRv.setLayoutManager(layoutManager);
        customerListRv.setItemAnimator(new DefaultItemAnimator());
        customerListRv.setHasFixedSize(true);
        dbHelper helper=new dbHelper(getContext());
        adapterList=helper.GetAllCustomer();

        adapter=new UserAdapter(adapterList, new OnAdapterItemClickListener() {
            @Override
            public void OnItemClicked(int position) {
                UserModel model=adapterList.get(position);
                selected_id=model.getId();
               // LoadFragment("Edit",selected_id);
                Bundle bundle=new Bundle();
                bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT,6);
                bundle.putString("ACTION","Edit");
                bundle.putInt("SELECTED_ID",selected_id);
                fragmentActionListener.onActionPerformed(bundle);


            }
        });
        customerListRv.setAdapter(adapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_fab)
    public void onViewClicked() {

        Bundle bundle=new Bundle();
        bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT,6);
        bundle.putString("ACTION","Add");
        bundle.putInt("SELECTED_ID",selected_id);
        fragmentActionListener.onActionPerformed(bundle);

        // LoadFragment("Add",0);
    }

//    private void LoadFragment(String action, int id) {
//
//
//
//
//        //AddCustomerFragment addCustomerFragment=new AddCustomerFragment();
//       // addCustomerFragment.setArguments(bundle);
//       // ItemActivity.fragmentManager.beginTransaction().replace(R.id.item_container, addCustomerFragment,null ).addToBackStack(null).commit();
//
//    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItemOk=menu.findItem(R.id.action_refresh);
        MenuItem menuItemDelete=menu.findItem(R.id.action_delete);
        MenuItem menuItemPrint=menu.findItem(R.id.action_print);
        MenuItem menuItemCSV=menu.findItem(R.id.action_csv);
        MenuItem menuItem_from_CSV=menu.findItem(R.id.action_from_csv);

        menuItem_from_CSV.setVisible(false);
        menuItemCSV.setVisible(false);
        menuItemPrint.setVisible(false);
        menuItemOk.setVisible(false);
        menuItemDelete.setVisible(false);
    }
}
