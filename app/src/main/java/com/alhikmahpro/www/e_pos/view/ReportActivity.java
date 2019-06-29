package com.alhikmahpro.www.e_pos.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.AppUtils;
import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.adapter.ReportAdapter;
import com.alhikmahpro.www.e_pos.data.ReportModel;
import com.alhikmahpro.www.e_pos.data.UserModel;
import com.alhikmahpro.www.e_pos.data.dbHelper;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReportActivity extends AppCompatActivity implements CalenderFragment.SendBackListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    String TAG = "ReportActivity";
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    @BindView(R.id.txtFrom)
    TextView txtFrom;
    //    @BindView(R.id.txtTo)
//    EditText txtTo;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.rvReport)
    RecyclerView rvReport;
    String mDate, reports;
    int mDay, mMonth, mYear;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ReportModel> reportList = new ArrayList<>();
    @BindView(R.id.txt_head1)
    TextView txtHead1;
    @BindView(R.id.txt_head2)
    TextView txtHead2;
    @BindView(R.id.txt_head3)
    TextView txtHead3;
    @BindView(R.id.txt_head4)
    TextView txtHead4;
    @BindView(R.id.txt_head5)
    TextView txtHead5;
    @BindView(R.id.mainLayout)
    CoordinatorLayout mainLayout;
    private int STORAGE_PERMISSION_CODE = 1;
    ProgressDialog dialog;
    String filename;
    ArrayList<String> StringDates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent myIntent = getIntent();
        reports = myIntent.getStringExtra("Report");

        dialog = new ProgressDialog(this);
        initView();


    }

    private void initView() {


        if (reports.equals("summery")) {

            txtHead1.setText("Date");
            txtHead2.setText("Gross");
            txtHead3.setText("Refund");
            txtHead4.setText("Discount");
            txtHead5.setText("Net");

        } else if (reports.equals("employee")) {

            txtHead1.setText("Staff");
            txtHead2.setText("Gross");
            txtHead3.setText("Refund");
            txtHead4.setText("Discount");
            txtHead5.setText("Net");


        } else if (reports.equals("customer")) {
            txtHead1.setText("Customer");
            txtHead2.setText("First Visit");
            txtHead3.setText("Last Visit");
            txtHead4.setText("Total Visit");
            txtHead5.setText("Spent");


        }

    }

    @OnClick(R.id.btn_search)
    public void onBtnSearchClicked() {
        CalenderFragment calenderFragment = new CalenderFragment();
        calenderFragment.show(getFragmentManager(), "CalenderFragment");
    }


    private void salesSummery() {


        dbHelper helper = new dbHelper(this);
        for (String mDate : StringDates) {
            Log.d(TAG, "salesSummery: With :"+mDate);
            Cursor cursor = helper.getinvoicebyDate(mDate);
            if (cursor.moveToFirst()) {
                String dt = cursor.getString(cursor.getColumnIndex("date"));
                if (dt != null && !dt.isEmpty()) {
                    double refund = helper.getRefundTotal(mDate);
                    double tot = cursor.getDouble(cursor.getColumnIndex("Total"));
                    double grant = cursor.getDouble(cursor.getColumnIndex("Grant"));
                    double dis = tot - grant;
                    double net = tot - dis - refund;


                    ReportModel reportModel = new ReportModel();
                    reportModel.setDate(cursor.getString(cursor.getColumnIndex("date")));
                    reportModel.setTotal(tot);
                    reportModel.setRefund(refund);
                    reportModel.setDiscount(dis);
                    reportModel.setNet(net);
                    reportList.add(reportModel);
                }


            } else {
                Toast.makeText(this, "No Data Available", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
            initRecycler();
        }
    }

    public void initRecycler() {

        layoutManager = new LinearLayoutManager(this);
        rvReport.setLayoutManager(layoutManager);
        rvReport.setItemAnimator(new DefaultItemAnimator());
        rvReport.setHasFixedSize(true);
        adapter = new ReportAdapter(reportList, reports);
        rvReport.setAdapter(adapter);

    }


    private void employeeReport(String startDate,String endDate) {

        dbHelper helper = new dbHelper(this);
        List<UserModel> empList;
        empList = helper.GetAllEmployee();
        Log.d(TAG, "start:" + startDate);
        Log.d(TAG, "end:" + endDate);
        for (UserModel model : empList) {
            String name = model.getName();

            Log.d(TAG, "Employee:" + name);

            Cursor cursor = helper.salebyEmployee(startDate, endDate, name);
            if (cursor.moveToFirst()) {

                double refund = helper.getRefundbyEmployee(startDate, endDate, name);
                double tot = cursor.getDouble(cursor.getColumnIndex("Total"));
                double grant = cursor.getDouble(cursor.getColumnIndex("Grant"));

                double dis = tot - grant;
                double net = tot - dis - refund;


                ReportModel reportModel = new ReportModel();

                reportModel.setEmployee(name);
                reportModel.setTotal(tot);
                reportModel.setRefund(refund);
                reportModel.setDiscount(dis);
                reportModel.setNet(net);
                reportList.add(reportModel);


            }
            cursor.close();
        }
        initRecycler();
    }

    private void customerReport(String startDate,String endDate) {

        dbHelper helper = new dbHelper(this);
        List<UserModel> cusList;

        cusList = helper.GetAllCustomer();
        Log.d(TAG, "start:" + startDate);
        Log.d(TAG, "end:" + endDate);
        for (UserModel model : cusList) {
            String name = model.getName();

            Log.d(TAG, "customer:" + name);

            double ref = helper.getRefundbyCustomer(startDate, endDate, name);
            Log.d(TAG, "refund amount: " + name + "=" + ref);
            Cursor cursor = helper.salebyCustomer(startDate, endDate, name);
            if (cursor.moveToFirst()) {

                double tot = cursor.getDouble(cursor.getColumnIndex("Total"));
                double grant = tot - ref;

                ReportModel reportModel = new ReportModel();
                // use date field as a customer name
                reportModel.setCustomer(name);
                reportModel.setTotal(grant);
                reportModel.setFirstVisit(cursor.getString(cursor.getColumnIndex("First")));
                reportModel.setLastVisit(cursor.getString(cursor.getColumnIndex("Last")));
                reportModel.setTotalVisit(cursor.getInt(cursor.getColumnIndex("visit")));
                reportList.add(reportModel);
            }
            Log.d(TAG, "customerReport: Cursor null");
            cursor.close();
        }
        initRecycler();
    }


    @Override
    public void onBackPressed() {
        reportList.clear();

        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragment_menu, menu);
        MenuItem menuItemOk = menu.findItem(R.id.action_refresh);
        MenuItem menuItemDelete = menu.findItem(R.id.action_delete);
        MenuItem menuItemPrinter = menu.findItem(R.id.action_print);
        menuItemOk.setVisible(false);
        menuItemDelete.setVisible(false);
        menuItemPrinter.setVisible(false);

        MenuItem menuItemCSV = menu.findItem(R.id.action_csv);

        //Log.d(TAG, "onCreateOptionsMenu: " + StringDates.size());


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_csv:
                //writToCSV();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissions();
                } else {
                    String folder_path = createDirectory(getPath(null), "EPOS");
                    Log.d(TAG, "folder Path" + folder_path);
                    try {
                        exportToCSV();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private void checkPermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Already Granted", Toast.LENGTH_SHORT).show();

            String folder_path = createDirectory(getPath(null), "EPOS");
            System.out.println("Path isss...." + folder_path);
            try {
                exportToCSV();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            requestPermission();
        }

    }


    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission denied")
                    .setMessage("App need permission to continue")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ReportActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                String folder_path = createDirectory(getPath(null), "EPOS");
                System.out.println("Path isss...." + folder_path);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
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


    public void exportToCSV() throws Exception {

        Log.d(TAG, "inside exportToCSV");


        String type = ".csv";
        String file_path = getPath("EPOS");
        filename = reports + AppUtils.getDateAndTime();
        File file = new File(file_path + "/" + filename + ".csv");
        final String csvFilename = file_path + filename + ".csv";
        dialog.setTitle("Exporting...");
        dialog.setMessage("Please wait");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();

        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

            }
        };
        new Thread() {
            public void run() {

                try {

                    FileWriter fileWriter = new FileWriter(csvFilename);
                    fileWriter.append(txtHead1.getText().toString());
                    fileWriter.append(',');
                    fileWriter.append(txtHead2.getText().toString());
                    fileWriter.append(',');
                    fileWriter.append(txtHead3.getText().toString());
                    fileWriter.append(',');
                    fileWriter.append(txtHead4.getText().toString());
                    fileWriter.append(',');
                    fileWriter.append(txtHead5.getText().toString());
                    fileWriter.append(',');
                    fileWriter.append('\n');


                    if (reports.equals("summery")) {
                        for (ReportModel model : reportList) {
                            fileWriter.append(model.getDate());
                            fileWriter.append(',');
                            fileWriter.append(CurrencyFormatter(model.getTotal()));
                            fileWriter.append(',');
                            fileWriter.append(CurrencyFormatter(model.getRefund()));
                            fileWriter.append(',');
                            fileWriter.append(CurrencyFormatter(model.getDiscount()));
                            fileWriter.append(',');
                            fileWriter.append(CurrencyFormatter(model.getNet()));
                            fileWriter.append(',');
                            fileWriter.append('\n');

                        }

                    }
                    if (reports.equals("employee")) {
                        for (ReportModel model : reportList) {
                            fileWriter.append(model.getEmployee());
                            fileWriter.append(',');
                            fileWriter.append(CurrencyFormatter(model.getTotal()));
                            fileWriter.append(',');
                            fileWriter.append(CurrencyFormatter(model.getRefund()));
                            fileWriter.append(',');
                            fileWriter.append(CurrencyFormatter(model.getDiscount()));
                            fileWriter.append(',');
                            fileWriter.append(CurrencyFormatter(model.getNet()));
                            fileWriter.append(',');
                            fileWriter.append('\n');

                        }

                    }
                    if (reports.equals("customer")) {
                        for (ReportModel model : reportList) {
                            fileWriter.append(model.getCustomer());
                            fileWriter.append(',');
                            fileWriter.append(model.getFirstVisit());
                            fileWriter.append(',');
                            fileWriter.append(model.getLastVisit());
                            fileWriter.append(',');
                            fileWriter.append(String.valueOf(model.getTotalVisit()));
                            fileWriter.append(',');
                            fileWriter.append(CurrencyFormatter(model.getTotal()));
                            fileWriter.append(',');
                            fileWriter.append('\n');

                        }

                    }


                    fileWriter.close();

                } catch (Exception e) {
                    //e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
                dialog.dismiss();


            }
        }.start();

        //Snackbar.make(findViewById(R.id.loginActivity), "Imported", Snackbar.LENGTH_LONG).show();
        // Toast.makeText(ReportActivity.this,"Exported",Toast.LENGTH_LONG).show();
        showSnackbar();


    }

    private void showSnackbar() {

        Snackbar snackbar = Snackbar.make(mainLayout, "Download completed", Snackbar.LENGTH_INDEFINITE)
                .setAction("Open", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String file_path = getPath("EPOS");

                        File file = new File(file_path, filename + ".csv");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), "text/csv");
                        startActivity(intent);


                    }
                });
        snackbar.show();

    }


    public String CurrencyFormatter(double val) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        String pattern = ((DecimalFormat) nf).toPattern();
        String newPattern = pattern.replace("\u00A4", "").trim();
        NumberFormat newFormat = new DecimalFormat(newPattern);

        String ft = String.valueOf(newFormat.format(val));
        return ft;

    }


    @Override
    public void sendMessage(ArrayList<Date> dates) {

        String firstDate,lastDate;
        if(!StringDates.isEmpty()){
            StringDates.clear();
        }

        if(!dates.isEmpty()){

            for (Date date_by : dates) {
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                String formatted = format1.format(date_by);
                //Log.d(TAG, "sendMessage: " + formatted);
                StringDates.add(formatted);
            }
            firstDate=StringDates.get(0);
            lastDate=StringDates.get(StringDates.size()-1);

            txtFrom.setText(firstDate+" TO "+lastDate);

            reportList.clear();
            switch (reports){
                case "summery":
                    salesSummery();
                    break;
                case "employee":
                    employeeReport(firstDate,lastDate);
                    break;
                case "customer":
                    customerReport(firstDate,lastDate);
                    break;
                default:
                    break;
            }

        }





//        if (reports.equals("summery"))
//            salesSummery();
//        else if (reports.equals("employee"))
//            employeeReport();
//        else if (reports.equals("customer"))
//            customerReport();
//

    }
}

