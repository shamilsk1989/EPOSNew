package com.alhikmahpro.www.e_pos.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.CartData;
import com.alhikmahpro.www.e_pos.data.DataContract;
import com.alhikmahpro.www.e_pos.data.SessionHandler;
import com.alhikmahpro.www.e_pos.data.dbHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    String TAG = "HomeActivity";

    ActionBarDrawerToggle toggle;
    public static FragmentManager fragmentManager;
    boolean session;
    ImageView imageViewLogo, imageViewAccount;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private int GALLERY = 1, CAMERA = 2;
    Bitmap thumbnail;
    String shop, user,folder_path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        View headerView = navView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.textShop);
        TextView navAddress = (TextView) headerView.findViewById(R.id.textAddress);
        imageViewLogo = (ImageView) headerView.findViewById(R.id.logo_img);
        imageViewAccount = (ImageView) headerView.findViewById(R.id.account_img);
        CartData.mCartData.clear();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkAndRequestPermissions()) {
                folder_path  = createDirectory(getPath(null), "EPOS");

            }
        } else {
            folder_path  = createDirectory(getPath(null), "EPOS");
        }

        Log.d(TAG,"folder Created"+folder_path);


        loadLogo();

        if (!SessionHandler.getInstance(getApplicationContext()).isSessionStarted()) {
            imageViewAccount.setVisibility(View.GONE);

        }

        imageViewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SessionHandler.getInstance(getApplicationContext()).resetUser();

                String user = SessionHandler.getInstance(getApplicationContext()).getUserType();
                Log.d(TAG, "after reset user:" + user);
                Intent myIntent = new Intent(HomeActivity.this, PasscodeActivity.class);
                HomeActivity.this.startActivity(myIntent);
                //startActivity(new Intent(this, PasscodeActivity.class));
                finish();
            }
        });

        imageViewLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkAndRequestPermissions()) {
                        showOptions();
                    }
                } else {
                    showOptions();
                }




            }
        });


        fragmentManager = getSupportFragmentManager();
        user = SessionHandler.getInstance(getApplicationContext()).getUserType();
        shop = SessionHandler.getInstance(getApplicationContext()).getShopName();
        Log.d(TAG, "user:" + user);
        Log.d(TAG, "shop:" + shop);
        if (user.contains("User")) {
            hideItems();
        }

        navUsername.setText(shop);
        navAddress.setText(user);

        navView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            Log.d(TAG, "Loading fragment");

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // SaleFragment saleFragment=new SaleFragment();
            HomeFragment homeFragment = new HomeFragment();
            fragmentTransaction.add(R.id.fragment_container, homeFragment, null).commit();


            //  navView.setCheckedItem(R.id.nav_sale);


            //hide admin options in navigation drawer
            // hideItems();
        }


    }



    private void loadLogo() {
        dbHelper helper = new dbHelper(this);
        byte[] img = helper.getLogo();
        Log.d(TAG, "Logo" + img.toString());
        //cursor.getBlob(cursor.getColumnIndex(DataContract.Items.COL_ITEM_IMAGE));

        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            imageViewLogo.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 60, 60, false));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void saveLogo() {

        Log.d(TAG,"Save Logo");

        imageViewLogo.setDrawingCacheEnabled(true);
        imageViewLogo.buildDrawingCache();
        Bitmap bitmap = imageViewLogo.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] img_data = stream.toByteArray();
        dbHelper helper = new dbHelper(this);
        helper.updateLogo(img_data, shop);


    }


    private void hideItems() {

        Menu navMenu = navView.getMenu();
        MenuItem admin = navMenu.findItem(R.id.nav_admin);
        MenuItem report = navMenu.findItem(R.id.nav_report);
        MenuItem receipt = navMenu.findItem(R.id.nav_receipt);
        MenuItem ret = navMenu.findItem(R.id.nav_return);
        receipt.setVisible(false);
        ret.setVisible(false);
        report.setVisible(false);
        admin.setVisible(false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "On resume");
        CartData.mCartData.clear();
        if (!SessionHandler.getInstance(getApplicationContext()).isSessionStarted()) {
            imageViewAccount.setVisibility(View.GONE);

        }



    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_sale:
                Intent saleIntent = new Intent(HomeActivity.this, SaleActivity.class);
                startActivity(saleIntent);
                break;


            case R.id.nav_item:
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ItemFragment()).addToBackStack(null).commit();
                Intent itemIntent = new Intent(HomeActivity.this, ItemActivity.class);
                startActivity(itemIntent);
                break;

            case R.id.nav_employee:
                Intent empIntent = new Intent(HomeActivity.this, EmployeeActivity.class);
                startActivity(empIntent);
                break;
            case R.id.nav_receipt:
                Intent invIntent = new Intent(HomeActivity.this, InvoiceActivity.class);
                startActivity(invIntent);
                break;
            case R.id.nav_return:
                Intent intentReturn = new Intent(HomeActivity.this, ReturnActivity.class);
                startActivity(intentReturn);
                break;
            case R.id.nav_settings:
                Intent intentSettings = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intentSettings);
                break;
            case R.id.nav_printers:
                Intent intentPrintSettings = new Intent(HomeActivity.this, PrintSettingActivity.class);
                startActivity(intentPrintSettings);
                break;
            case R.id.nav_summery_report:
                reportActivity("summery");
                break;
            case R.id.nav_cus_report:
                reportActivity("customer");
                break;
            case R.id.nav_staff_report:
                reportActivity("employee");
                break;


        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.d(TAG, "inside menu ");
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }


    public void reportActivity(String type) {

        Intent intentReport = new Intent(HomeActivity.this, ReportActivity.class);
        intentReport.putExtra("Report", type);
        startActivity(intentReport);


    }

    private boolean checkAndRequestPermissions() {

        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
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
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;

    }

    private void showOptions() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(this);
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
                        final InputStream inputStream = this.getContentResolver().openInputStream(contentURI);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(inputStream);
                        imageViewLogo.setImageBitmap(selectedImage);
                        saveLogo();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }

            }

        } else if (requestCode == CAMERA) {

            thumbnail = (Bitmap) data.getExtras().get("data");
            imageViewLogo.setMaxWidth(60);
            imageViewLogo.setImageBitmap(thumbnail);

        }
    }


    public String createDirectory(String path, String folder_name) {
        boolean success = true;
        File folder = new File(path, folder_name);
        if (!folder.exists()) {
            folder.mkdir();
        }
        return folder.getPath();


    }

    public String getPath(String folder_name) {
        return (folder_name != null) ? Environment.getExternalStorageDirectory() + "/" + folder_name + "/" : Environment.getExternalStorageDirectory().toString();
    }



}
