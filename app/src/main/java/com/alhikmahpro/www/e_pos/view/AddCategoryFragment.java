package com.alhikmahpro.www.e_pos.view;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Binder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddCategoryFragment extends Fragment {

    Toolbar toolbar;
    String type;
    int selectedVal;
    @BindView(R.id.editCategory)
    EditText editCategory;
    @BindView(R.id.buttonSave)
    Button buttonSave;
    Unbinder unbinder;
    dbHelper helper;
    FragmentActionListener fragmentActionListener;


    public AddCategoryFragment() {
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
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);

         type= getArguments().getString("TYPE");
         selectedVal = getArguments().getInt("SELECTED_ID");

        unbinder = ButterKnife.bind(this, view);


        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(type+" Category");
        helper=new dbHelper(getContext());
        editCategory.setText(getArguments().getString("SELECTED"));
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItemOk = menu.findItem(R.id.action_refresh);
        MenuItem menuItemDelete = menu.findItem(R.id.action_delete);
        menuItemOk.setVisible(false);
        MenuItem menuItemPrint=menu.findItem(R.id.action_print);
        menuItemPrint.setVisible(false);
        if(type.equals("Add")){
            menuItemDelete.setVisible(false);

        }
        MenuItem menuItem_from_CSV=menu.findItem(R.id.action_from_csv);
        menuItem_from_CSV.setVisible(false);
        MenuItem menuItemCSV=menu.findItem(R.id.action_csv);
        menuItemCSV.setVisible(false);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                deleteCategory();
                return true;
                default:return false;


        }

    }

    private void deleteCategory() {

        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Cancel Order");
        builder.setMessage("Are you sure ! Do you want to delete this category?");
        builder.setCancelable(false);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                helper.deleteCategory(selectedVal);
                Toast.makeText(getContext(),"Category Deleted",Toast.LENGTH_LONG).show();
               // ItemActivity.fragmentManager.popBackStack();
                fragmentActionListener.onBackActionPerformed();


            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog=builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
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

    @OnClick(R.id.buttonSave)
    public void onViewClicked() {

        if(type.equals("Add")){
            saveCategory();

        }
        else if(type.equals("Edit")){
            EditCategory();
        }






    }

    private void saveCategory() {

        String name=editCategory.getText().toString();
        if(validate(name)){

            if(helper.checkCategory(name.trim())){
                Toast.makeText(getActivity(),"Category Already Added",Toast.LENGTH_LONG).show();
            }
            else{
                helper.addCategory(name.trim());
                Toast.makeText(getActivity(),"Category Added",Toast.LENGTH_LONG).show();
                editCategory.setText("");


            }


        }
    }
    private void EditCategory() {

        String newName=editCategory.getText().toString();
        if(validate(newName)){
            helper.updateCategory(newName.trim(),selectedVal);
            Toast.makeText(getActivity(),"Category updated",Toast.LENGTH_LONG).show();
           // ItemActivity.fragmentManager.popBackStack();

        }
    }



    private boolean validate(String name) {
        if(TextUtils.isEmpty(name)){
            editCategory.setError("Enter Category");
            return false;
        }
        return true;

    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }
}
