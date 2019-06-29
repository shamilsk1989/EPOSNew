package com.alhikmahpro.www.e_pos.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemFragment extends Fragment {


    Toolbar mToolbar;
    @BindView(R.id.textItem)
    TextView textItem;
    @BindView(R.id.textCategory)
    TextView textCategory;
    @BindView(R.id.textCustomer)
    TextView textCustomer;
    @BindView(R.id.textDiscount)
    TextView textDiscount;
    private Unbinder unbinder;
    String TAG = "ItemFragment";
   // private static final int ACTION_VALUE_FRAGMENT=1;
    FragmentActionListener fragmentActionListener;

    public ItemFragment() {
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
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        unbinder = ButterKnife.bind(this, view);


        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbar.setTitle("Items");
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);


//        item.setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener){
        this.fragmentActionListener = fragmentActionListener;
    }


    @OnClick({R.id.textItem, R.id.textCategory, R.id.textCustomer,R.id.textDiscount})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.textItem:
               // LoadItemFragment("Item");
                if (fragmentActionListener!=null){
                    Bundle bundle = new Bundle();
                    bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT,1);
                    bundle.putString("TITLE","Item");
                    fragmentActionListener.onActionPerformed(bundle);
                }
                break;
            case R.id.textCategory:
               // LoadCategoryFragment();
                if (fragmentActionListener!=null){
                    Bundle bundle = new Bundle();
                    bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT,2);
                    bundle.putString("TITLE", "Category");
                    fragmentActionListener.onActionPerformed(bundle);
                }
                break;
            case R.id.textCustomer:
              // LoadCustomerFragment();
                if (fragmentActionListener!=null){
                    Bundle bundle = new Bundle();
                    bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT,3);
                    fragmentActionListener.onActionPerformed(bundle);
                }
                break;
            case R.id.textDiscount:
                //LoadItemFragment("Discount");
                if (fragmentActionListener!=null){
                    Bundle bundle = new Bundle();
                    bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT,1);
                    bundle.putString("TITLE", "Discount");
                    fragmentActionListener.onActionPerformed(bundle);
                }
                break;

            default:
                break;


        }
    }



//    private void LoadCustomerFragment() {
//
//
//        ItemActivity.fragmentManager.beginTransaction().replace(R.id.item_container, new CustomerFragment(), null).addToBackStack(null).commit();
//    }

//    private void LoadItemFragment(String title) {
//        ViewItemFragment viewItemFragment=new ViewItemFragment();
//        Bundle bundle=new Bundle();
//        bundle.putString("TITLE",title);
//        viewItemFragment.setArguments(bundle);
//
//        ItemActivity.fragmentManager.beginTransaction().replace(R.id.item_container, viewItemFragment, null).addToBackStack(null).commit();
//    }

//    private void LoadCategoryFragment() {
//        CategoryFragment categoryFragment = new CategoryFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("TITLE", "Category");
//        categoryFragment.setArguments(bundle);
//        ItemActivity.fragmentManager.beginTransaction().replace(R.id.item_container, categoryFragment, null).addToBackStack(null).commit();
//
//    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItemOk = menu.findItem(R.id.action_refresh);
        MenuItem menuItemDelete = menu.findItem(R.id.action_delete);
        MenuItem menuItemPrint=menu.findItem(R.id.action_print);
        MenuItem menuItemCSV=menu.findItem(R.id.action_csv);
        MenuItem menuItem_from_CSV=menu.findItem(R.id.action_from_csv);
        menuItem_from_CSV.setVisible(false);
        menuItemCSV.setVisible(false);
        menuItemPrint.setVisible(false);
        menuItemOk.setVisible(false);
        menuItemDelete.setVisible(false);
        menuItemPrint.setVisible(false);
    }


}
