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
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.adapter.CategoryAdapter;
import com.alhikmahpro.www.e_pos.data.ItemsModel;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;
import com.alhikmahpro.www.e_pos.interfaces.OnAdapterClickListener;
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
// fragment show both item and and category list with add button
public class CategoryFragment extends Fragment {

    Toolbar toolbar;
    Unbinder unbinder;
    @BindView(R.id.item_list_rv)
    RecyclerView itemListRv;
    @BindView(R.id.btn_fab)
    FloatingActionButton btnFab;

    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    String selectedCate="";
    int cateId=0;


    List<ItemsModel>adapterList=new ArrayList<>();

    FragmentActionListener fragmentActionListener;
    public CategoryFragment() {
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
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        String Title=getArguments().getString("TITLE");

        toolbar.setTitle(Title);
        unbinder = ButterKnife.bind(this, view);

        LoadRecyclerView();
        return view;
    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener){
        this.fragmentActionListener = fragmentActionListener;
    }


    private void LoadRecyclerView() {

        layoutManager=new LinearLayoutManager(getActivity());
        itemListRv.setLayoutManager(layoutManager);
        itemListRv.setItemAnimator(new DefaultItemAnimator());
        itemListRv.setHasFixedSize(true);
        dbHelper helper=new dbHelper(getContext());
        adapterList=helper.GetAllCategory();


        adapter=new CategoryAdapter(adapterList, new OnAdapterItemClickListener() {
            @Override
            public void OnItemClicked(int position) {
                ItemsModel model=adapterList.get(position);
                selectedCate=model.getCategoryName();
                cateId=model.getCategoryId();
                Toast.makeText(getContext(),"selected category"+selectedCate,Toast.LENGTH_LONG).show();

               // LoadFragment("Edit",selectedCate,cateId);
                Bundle bundle=new Bundle();
                bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT,5);
                bundle.putString("TYPE","Edit");
                bundle.putString("SELECTED",selectedCate);
                bundle.putInt("SELECTED_ID",cateId);
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

        //LoadFragment("Add","",0);
        Bundle bundle=new Bundle();
        bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT,5);
        bundle.putString("TYPE","Add");
        bundle.putString("SELECTED",selectedCate);
        bundle.putInt("SELECTED_ID",cateId);
        fragmentActionListener.onActionPerformed(bundle);
    }

//    private void LoadFragment(String type, String selection,int id) {
//
//        Bundle bundle=new Bundle();
//        bundle.putString("TYPE",type);
//        bundle.putString("SELECTED",selection);
//        bundle.putInt("SELECTED_ID",id);
//
//
//
////        if(value.equals("Items")){
////            AddItemsFragment addItemsFragment=new AddItemsFragment();
////            addItemsFragment.setArguments(bundle);
////            ItemActivity.fragmentManager.beginTransaction().replace(R.id.item_container, addItemsFragment,null ).addToBackStack(null).commit();
////        }
////        else if(value.equals("Category")){
//            AddCategoryFragment addCategoryFragment=new AddCategoryFragment();
//            addCategoryFragment.setArguments(bundle);
//            ItemActivity.fragmentManager.beginTransaction().replace(R.id.item_container, addCategoryFragment,null ).addToBackStack(null).commit();
//       // }
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
