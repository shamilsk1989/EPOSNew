package com.alhikmahpro.www.e_pos.view;


import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.adapter.ItemAdapter;
import com.alhikmahpro.www.e_pos.data.DataContract;
import com.alhikmahpro.www.e_pos.data.ItemsModel;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;
import com.alhikmahpro.www.e_pos.interfaces.OnAdapterItemClickListener;
import com.opencsv.CSVReader;

import java.awt.font.TextAttribute;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewItemFragment extends Fragment implements AdapterView.OnItemSelectedListener{


    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter recycleAdapter;
    Toolbar toolbar;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.item_list_rv)
    RecyclerView recyclerView;
    @BindView(R.id.btn_fab)
    FloatingActionButton btnFab;
    Unbinder unbinder;
    dbHelper helper;
    String selected_category;
    String title;
    int selectedItemCode;
    private static final String TAG = "ViewItemFragment";

    List<ItemsModel>adapterList=new ArrayList<>();
    FragmentActionListener fragmentActionListener;


    public ViewItemFragment() {
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
        View view = inflater.inflate(R.layout.fragment_view_item, container, false);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        title = getArguments().getString("TITLE");
        toolbar.setTitle(title);
        unbinder = ButterKnife.bind(this, view);
        helper=new dbHelper(getContext());

        if(title.equals("Discount")){
            spinner.setVisibility(View.GONE);
            selected_category=title;
            itemRecyclerView();

        }
        else{
            loadSpinner();

        }



        return view;
    }


    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    private void loadSpinner() {

        List<String> category = new ArrayList<>();
        category.add(0,"All Category");
        SQLiteDatabase database=helper.getReadableDatabase();

        Cursor cursor=helper.GetCategoryName(database);
        if(cursor.moveToFirst()){
            do{
                category.add(cursor.getString(cursor.getColumnIndex(DataContract.Category.COL_CATEGORY_NAME)));

            }while (cursor.moveToNext());
        }
        cursor.close();
        database.close();




        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, category);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_fab)
    public void onViewClicked() {
        if(title.equals("Discount")){
          //  LoadDiscountFragment("Add",0);

            Bundle bundle=new Bundle();
            bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT,7);
            bundle.putString("TYPE","Add");
            bundle.putString("TITLE",title);
            bundle.putInt("SELECTED_ID",selectedItemCode);
            fragmentActionListener.onActionPerformed(bundle);




        }
        else{
            //LoadItemFragment("Add",0);
            Bundle bundle=new Bundle();
            bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT,4);
            bundle.putString("TYPE","Add");
            bundle.putString("TITLE",title);
            bundle.putInt("SELECTED_ID",selectedItemCode);
            fragmentActionListener.onActionPerformed(bundle);
        }


    }
//    private void LoadDiscountFragment(String type,int id){
//        Bundle bundle=new Bundle();
//        bundle.putString("TYPE",type);
//        bundle.putString("TITLE",title);
//        bundle.putInt("SELECTED_ID",id);
//        AddDiscountFragment addDiscountFragment=new AddDiscountFragment();
//        addDiscountFragment.setArguments(bundle);
//        ItemActivity.fragmentManager.beginTransaction().replace(R.id.item_container, addDiscountFragment,null ).addToBackStack(null).commit();
//
//
//    }

//    private void LoadItemFragment(String type,int id) {
//        Bundle bundle=new Bundle();
//        bundle.putString("TYPE",type);
//        bundle.putString("TITLE",title);
//        bundle.putInt("SELECTED_ID",id);
//        AddItemsFragment addItemsFragment=new AddItemsFragment();
//        addItemsFragment.setArguments(bundle);
//        ItemActivity.fragmentManager.beginTransaction().replace(R.id.item_container, addItemsFragment,null ).addToBackStack(null).commit();
//    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selected_category = parent.getItemAtPosition(position).toString();
        itemRecyclerView();

    }

    private void itemRecyclerView() {

        layoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        helper=new dbHelper(getContext());
        if(spinner.getSelectedItemPosition()==0){
            adapterList=helper.getAllItem();
        }
        else{
            adapterList=helper.getItemByCategory(selected_category);
        }

        recycleAdapter=new ItemAdapter(adapterList,new OnAdapterItemClickListener() {
            @Override
            public void OnItemClicked(int position) {
                ItemsModel model=adapterList.get(position);
                selectedItemCode=model.getItemId();

                if(title.equals("Discount")){
                    //LoadDiscountFragment("Edit",itemCode);
                    Bundle bundle=new Bundle();
                    bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT,7);
                    bundle.putString("TYPE","Edit");
                    bundle.putString("TITLE",title);
                    bundle.putInt("SELECTED_ID",selectedItemCode);
                    fragmentActionListener.onActionPerformed(bundle);

                }
                else{
                    //LoadItemFragment("Edit",itemCode);
                    Bundle bundle=new Bundle();
                    bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT,4);
                    bundle.putString("TYPE","Edit");
                    bundle.putString("TITLE",title);
                    bundle.putInt("SELECTED_ID",selectedItemCode);
                    fragmentActionListener.onActionPerformed(bundle);

                }
            }
        });
        recyclerView.setAdapter(recycleAdapter);




    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItemOk=menu.findItem(R.id.action_refresh);
        MenuItem menuItemDelete=menu.findItem(R.id.action_delete);
        MenuItem menuItemPrint=menu.findItem(R.id.action_print);
        MenuItem menuItemCSV=menu.findItem(R.id.action_csv);
        MenuItem menuItem_from_CSV=menu.findItem(R.id.action_from_csv);
        if(title.equals("Discount")){
            menuItem_from_CSV.setVisible(false);
        }
        menuItemCSV.setVisible(false);
        menuItemPrint.setVisible(false);
        menuItemOk.setVisible(false);
        menuItemDelete.setVisible(false);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_from_csv:
                //itemRecyclerView();
              //  Toast.makeText(getContext(),"Item list refreshed",Toast.LENGTH_LONG).show();
                Log.d(TAG,"Asyn task called");
                ReadFromCSV readFromCSV=new ReadFromCSV();
                readFromCSV.execute();
                itemRecyclerView();
                return true;
            default:return false;


        }

    }

    private class ReadFromCSV extends AsyncTask<String,String,String>{

        ProgressDialog progressDialog;
        String res;



        @Override
        protected String doInBackground(String... strings) {
            publishProgress("Uploading....");

            try{


                String file_path = getPath("EPOS");
                File file = new File(file_path+"/"+"items.csv");
                String csvFilename=file_path+"items.csv";
                String[] nextLine = null;
                Log.d(TAG,"File :"+file);
                Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.android);
                ByteArrayOutputStream bos=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] img=bos.toByteArray();
                CSVReader csvReader = null;
                csvReader = new CSVReader(new FileReader(csvFilename), ',', '\'', 1);
                while ((nextLine = csvReader.readNext()) != null) {
                  //  Log.d(TAG,"first row"+nextLine[0]);
                    //System.out.println(nextLine[0] + nextLine[1] + "etc...");
                    String code=nextLine[0];
                    String name=nextLine[1];
                    String category=nextLine[2];
                    String barcode=nextLine[3];
                    String price=nextLine[4];
                    int position=0;
                    double amt=0;

                    try{
                        amt=Double.valueOf(price);
                    }catch (Exception e){


                    }
                    if(!helper.checkItem(name.toLowerCase().trim())) {
                        helper.addItems(barcode,code,name.trim(), amt, category,img,position);
                    }
                    if(!helper.checkCategory(category.trim())){
                        helper.addCategory(category.trim());
                    }
                }
                csvReader.close();




                res="success";
            }catch (Exception e){
                Log.d(TAG,"file not found..."+e.getMessage());
                e.printStackTrace();
                res=e.getMessage();

            }


            return res;
        }

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();



            progressDialog = ProgressDialog.show(getActivity(),
                    "Importing...",
                    "Please Wait.....");



        }

        @Override
        protected void onPostExecute(String s) {
           //super.onPostExecute(s);
            progressDialog.dismiss();
            Toast.makeText(getContext(),s,Toast.LENGTH_LONG).show();

        }
        public String getPath(String folder_name) {
            return (folder_name != null) ? Environment.getExternalStorageDirectory() + "/" + folder_name + "/" : Environment.getExternalStorageDirectory().toString();
        }
    }
}
