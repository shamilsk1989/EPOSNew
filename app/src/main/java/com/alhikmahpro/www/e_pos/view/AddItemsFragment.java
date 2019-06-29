package com.alhikmahpro.www.e_pos.view;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.DataContract;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_CANCELED;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddItemsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    Toolbar toolbar;
    String type,title;
    @BindView(R.id.itemEditText)
    EditText itemEditText;
    @BindView(R.id.spinner)

    Spinner spinner;
    @BindView(R.id.priceEditText)
    EditText priceEditText;
    @BindView(R.id.imageView_item)
    ImageView imageViewItem;
    @BindView(R.id.btn_save)
    Button btnSave;
    Unbinder unbinder;
    String TAG = "AddItemsFragment";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    @BindView(R.id.codeEditText)
    EditText codeEditText;
    private int GALLERY = 1, CAMERA = 2;
    Bitmap thumbnail;
    String selected_category;
    dbHelper helper;
    int selectedId;
    boolean is_new=false;
    FragmentActionListener fragmentActionListener;
    //List<String>category=new ArrayList<>();


    public AddItemsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_add_items, container, false);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        type = getArguments().getString("TYPE");
        selectedId = getArguments().getInt("SELECTED_ID");
        Log.d(TAG,"selected id"+ selectedId);
        toolbar.setTitle(type + " Items");
        unbinder = ButterKnife.bind(this, view);
        helper = new dbHelper(getContext());
        loadSpinner();

        // check type is edit or new
        if(type.equals("Add"))
            is_new=true;

    //if edit load values from database

        if(!is_new){
            LoadValues(selectedId);
        }

        return view;
    }

    // Load values from database

    private void LoadValues(int id) {


        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = helper.getItemByID(database, id);
        if (cursor.moveToFirst()) {

            do {
                //  obj.setItemId(cursor.getInt(cursor.getColumnIndex(DataContract.Items.COL_ITEM_ID)));
                String name = cursor.getString(cursor.getColumnIndex(DataContract.Items.COL_ITEM_NAME));
                int cate = cursor.getInt(cursor.getColumnIndex(DataContract.Items.COL_POSITION));
                itemEditText.setText(name);
                priceEditText.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex(DataContract.Items.COL_ITEM_PRICE))));
                codeEditText.setText(cursor.getString(cursor.getColumnIndex(DataContract.Items.COL_ITEM_CODE)));
                byte[] img = cursor.getBlob(cursor.getColumnIndex(DataContract.Items.COL_ITEM_IMAGE));
                Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                imageViewItem.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 60, 60, false));
                spinner.setSelection(cate);


            } while (cursor.moveToNext());
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick(R.id.btn_save)
    public void onBtnSaveClicked() {

        Log.d(TAG, "spinner" + selected_category);

        String item_name = itemEditText.getText().toString();
        String price = priceEditText.getText().toString();
        String code=codeEditText.getText().toString();
        int position=spinner.getSelectedItemPosition();
        if (validate(item_name, price)) {

            // set price in formatted rate

            double rate = Double.valueOf(price);
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            String numberAsString = decimalFormat.format(rate);
            double formatted_rate = Double.valueOf(numberAsString);
            Log.d(TAG, "formatted_rate" + formatted_rate);

            //use barcode as spinner selected item position

            String barcode = "";
            imageViewItem.setDrawingCacheEnabled(true);
            imageViewItem.buildDrawingCache();
            Bitmap bitmap = imageViewItem.getDrawingCache();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] img_data = stream.toByteArray();

            if (is_new) {

                if (helper.checkItem(item_name.toLowerCase().trim())) {
                    Toast.makeText(getContext(), "Item already added", Toast.LENGTH_LONG).show();
                } else {
                    helper.addItems(barcode,code,item_name.trim(), formatted_rate, selected_category,img_data,position);
                    Toast.makeText(getContext(), "Item added", Toast.LENGTH_LONG).show();

                    priceEditText.setText("");
                    itemEditText.setText("");
                    codeEditText.setText("");
                    spinner.setSelection(0);
                }
//
            } else  {

                helper.updateItem(barcode,code, item_name.trim(), formatted_rate, selected_category, img_data,position,selectedId);
                Toast.makeText(getActivity(),"Item updated",Toast.LENGTH_LONG).show();
             //   ItemActivity.fragmentManager.popBackStack();


            }


        }

    }

    @OnClick(R.id.imageView_item)
    public void onImageViewItemClicked() {
        Log.d(TAG, "inside image click");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkAndRequestPermissions()) {
                showOptions();
            }
        } else {
            showOptions();
        }

    }

    private void loadSpinner() {


        List<String> category = new ArrayList<>();
        category.add(0, "Select Category");
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = helper.GetCategoryName(database);
        if (cursor.moveToFirst()) {
            do {
                category.add(cursor.getString(cursor.getColumnIndex(DataContract.Category.COL_CATEGORY_NAME)));

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, category);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);


    }

    private boolean checkAndRequestPermissions() {

        int cameraPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        int writePermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;

    }

    private void showOptions() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);

    }

    private void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                final Uri contentURI = data.getData();

                if (contentURI != null) {

                    try {
                        final InputStream inputStream = getActivity().getContentResolver().openInputStream(contentURI);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(inputStream);
                        imageViewItem.setImageBitmap(selectedImage);
                        //bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                        //img.setImageBitmap(bitmap);
                        //String path = saveImage(bitmap);
                        //Toast.makeText(AddItemActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();


                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }

            }

        } else if (requestCode == CAMERA) {

            thumbnail = (Bitmap) data.getExtras().get("data");
            imageViewItem.setMaxWidth(60);
            imageViewItem.setImageBitmap(thumbnail);

        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        selected_category = parent.getItemAtPosition(position).toString();
        if (spinner.getSelectedItemPosition() > 0) {
            selected_category = parent.getItemAtPosition(position).toString();

        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);

        MenuItem menuItemOk=menu.findItem(R.id.action_refresh);
        menuItemOk.setVisible(false);
        MenuItem menuItemDelete=menu.findItem(R.id.action_delete);
        if(is_new){
            menuItemDelete.setVisible(false);
        }

        MenuItem menuItemPrint=menu.findItem(R.id.action_print);
        MenuItem menuItemCSV=menu.findItem(R.id.action_csv);
        MenuItem menuItem_from_CSV=menu.findItem(R.id.action_from_csv);
        menuItem_from_CSV.setVisible(false);
        menuItemPrint.setVisible(false);
        menuItemCSV.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteItem();
                return true;
            default:
                return false;


        }

    }

    private void deleteItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Item");
        builder.setMessage("Are you sure ! Do you want to delete this item?");
        builder.setCancelable(false);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                helper.deleteItem(selectedId);
                Toast.makeText(getContext(), "Item Deleted", Toast.LENGTH_LONG).show();
               //ItemActivity.fragmentManager.popBackStack();
                fragmentActionListener.onBackActionPerformed();


            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });
        alertDialog.show();


    }

    private boolean validate(String name, String price) {
        if (TextUtils.isEmpty(name)) {
            itemEditText.setError("Enter Category");
            return false;
        } else if (TextUtils.isEmpty(price)) {
            priceEditText.setError("Enter Price");
            return false;
        }
        return true;

    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }
}
