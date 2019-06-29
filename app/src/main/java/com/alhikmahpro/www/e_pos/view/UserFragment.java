package com.alhikmahpro.www.e_pos.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
public class UserFragment extends Fragment {
    Toolbar toolbar;
    @BindView(R.id.item_list_rv)
    RecyclerView itemListRv;
    @BindView(R.id.btn_fab)
    FloatingActionButton btnFab;
    Unbinder unbinder;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    List<UserModel> adapterList=new ArrayList<>();
    String title;
    FragmentActionListener fragmentActionListener;
    private static final String TAG = "UserFragment";

    public UserFragment() {
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
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        unbinder = ButterKnife.bind(this, view);
        LoadRecyclerView();
        return view;
    }

    private void LoadRecyclerView() {
        layoutManager=new LinearLayoutManager(getActivity());
        itemListRv.setLayoutManager(layoutManager);
        itemListRv.setItemAnimator(new DefaultItemAnimator());
        itemListRv.setHasFixedSize(true);
        dbHelper helper=new dbHelper(getContext());
        adapterList=helper.GetAllEmployee();
        Log.d(TAG,"All emp"+adapterList);



        adapter=new UserAdapter(adapterList, new OnAdapterItemClickListener() {
            @Override
            public void OnItemClicked(int position) {
                UserModel model=adapterList.get(position);
                String selected=model.getName();
                int Id=model.getId();
               // LoadFragment("Edit",Id);
                Bundle bundle=new Bundle();
                bundle.putString("ACTION","Edit");
                bundle.putInt("SELECTED_ID",Id);
                fragmentActionListener.onActionPerformed(bundle);


            }
        });
        itemListRv.setAdapter(adapter);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_fab)
    public void onViewClicked() {
        //LoadFragment("Add",0);
        Bundle bundle=new Bundle();
        bundle.putString("ACTION","Add");
        bundle.putInt("SELECTED_ID",0);
        fragmentActionListener.onActionPerformed(bundle);


    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItemOk=menu.findItem(R.id.action_refresh);
        MenuItem menuItemDelete=menu.findItem(R.id.action_delete);
        MenuItem menuItemPrint=menu.findItem(R.id.action_print);
        MenuItem menuItemCSV=menu.findItem(R.id.action_csv);
        MenuItem menuItem_from_CSV=menu.findItem(R.id.action_from_csv);
        menuItem_from_CSV.setVisible(false);

        menuItemOk.setVisible(false);
        menuItemPrint.setVisible(false);
        menuItemCSV.setVisible(false);
        menuItemDelete.setVisible(false);




    }
    public void setFragmentActionListener(FragmentActionListener fragmentActionListener){
        this.fragmentActionListener = fragmentActionListener;
    }



}
