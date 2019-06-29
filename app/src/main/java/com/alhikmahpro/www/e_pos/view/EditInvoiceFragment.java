package com.alhikmahpro.www.e_pos.view;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.adapter.EditInvoiceAdapter;
import com.alhikmahpro.www.e_pos.data.CartData;
import com.alhikmahpro.www.e_pos.data.DataContract;
import com.alhikmahpro.www.e_pos.data.ItemsModel;
import com.alhikmahpro.www.e_pos.data.PrinterCommands;
import com.alhikmahpro.www.e_pos.data.SessionHandler;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditInvoiceFragment extends Fragment {


    FragmentActionListener fragmentActionListener;
    Toolbar toolbar;
    dbHelper helper;
    String receiptNo, type;
    Unbinder unbinder;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    @BindView(R.id.txtTotal)
    TextView txtTotal;
    @BindView(R.id.discLayout)
    RelativeLayout discLayout;
    @BindView(R.id.txtEmployee)
    TextView txtEmployee;
    @BindView(R.id.txtPosNo)
    TextView txtPosNo;
    @BindView(R.id.rv_items)
    RecyclerView rvItems;
    @BindView(R.id.txtDiscName)
    TextView txtDiscName;
    @BindView(R.id.txtDiscAmount)
    TextView txtDiscAmount;
    @BindView(R.id.txtDate)
    TextView txtDate;
    @BindView(R.id.txtInvoice)
    TextView txtInvoice;
    @BindView(R.id.txtCustomerName)
    TextView txtCustomerName;
    @BindView(R.id.txtCash)
    TextView txtCash;
    @BindView(R.id.txtBalanceAmount)
    TextView txtBalanceAmount;
    @BindView(R.id.txtPaid)
    TextView txtPaid;
    @BindView(R.id.txtBalanceName)
    TextView txtBalanceName;


    String deviceNo="",cName="",cMob="",eName="",sDate="",sName="",sAddress="",sAddress2="",sMob="",sTel="",sFooter="";
    public static String TAG = "EditInvoiceFragment";
    double tot, gtot, discount, paid, bal,discount_percentage;

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket=null;
    BluetoothDevice bluetoothDevice;
    OutputStream outputStream=null;
    InputStream inputStream=null;
    Thread thread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;


    public EditInvoiceFragment() {
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
        View view = inflater.inflate(R.layout.fragment_edit_invoice, container, false);
        unbinder = ButterKnife.bind(this, view);
        toolbar = getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        helper = new dbHelper(getContext());
        deviceNo = SessionHandler.getInstance(getContext()).getDeviceNo();


        //get all values from bundle
        type = getArguments().getString("TYPE");
        cMob=getArguments().getString("MOBILE");
        cName=getArguments().getString("CUSTOMER");
        eName=getArguments().getString("EMPLOYEE");
        sDate=getArguments().getString("DATE");
        tot = getArguments().getDouble("TOTAL");
        gtot = getArguments().getDouble("GTOTAL");
        paid = getArguments().getDouble("CASH");
        discount_percentage=getArguments().getDouble("DISCOUNT");
        cMob = helper.getCustomerMobile(cName);

        if (type.equals("INV")) {
            receiptNo = getArguments().getString("INVOICE");
            CartData.mCartData = helper.GetAllInvoiceDetails(receiptNo);

        } else {
            receiptNo = getArguments().getString("REFUND");
            CartData.mCartData = helper.GetAllRefundDetails(receiptNo);
        }

        toolbar.setTitle(receiptNo);

        initView();


        return view;
    }

    private void initView() {

        List<ItemsModel> arrayList;

        txtCustomerName.setText(cName+"  "+cMob);
        txtEmployee.setText(eName);
        txtDate.setText(sDate);

        discount = tot - gtot;
        bal = paid - gtot;

        String Gtotal = CurrencyFormatter(gtot);
        txtTotal.setText(Gtotal);
        String dis = CurrencyFormatter(discount);
        txtDiscAmount.setText(dis);
        txtCash.setText(CurrencyFormatter(paid));
        txtBalanceAmount.setText(CurrencyFormatter(bal));

        if (discount == 0) {

            txtDiscName.setVisibility(View.GONE);
            txtDiscAmount.setVisibility(View.GONE);

        }
        if(bal==0){
            txtBalanceAmount.setVisibility(View.GONE);
            txtBalanceName.setVisibility(View.GONE);
        }


        txtInvoice.setText(getArguments().getString("INVOICE"));
        txtPosNo.setText(deviceNo);

        layoutManager = new LinearLayoutManager(getContext());
        rvItems.setLayoutManager(layoutManager);
        rvItems.setItemAnimator(new DefaultItemAnimator());
        rvItems.setHasFixedSize(true);

        adapter = new EditInvoiceAdapter();
        rvItems.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener) {
        this.fragmentActionListener = fragmentActionListener;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (type.equals("RET")) {
            MenuItem menuItemReturn = menu.findItem(R.id.action_refresh);
            menuItemReturn.setVisible(false);

        }

        MenuItem menuItemSync = menu.findItem(R.id.action_sync);
        menuItemSync.setVisible(false);
//        MenuItem menuItemDelete=menu.findItem(R.id.action_delete);
//        MenuItem menuItemCSV=menu.findItem(R.id.action_csv);
//        MenuItem menuItem_from_CSV=menu.findItem(R.id.action_from_csv);
//        menuItem_from_CSV.setVisible(false);
//        menuItemCSV.setVisible(false);
//        menuItemDelete.setVisible(false);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_print:

                Log.d(TAG,"print"+type);

                try {
                    FindBluetoothDevice();
                    openBluetoothPrinter();
                    printData();


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return true;
            case R.id.action_refresh://return

                Log.d(TAG,"return");
                Bundle bundle = new Bundle();
                bundle.putInt(FragmentActionListener.KEY_SELECTED_FRAGMENT,2);
                bundle.putString("INVOICE", receiptNo);
                bundle.putString("EMPLOYEE", eName);
                bundle.putString("CUSTOMER", cName);
                bundle.putString("MOBILE", cMob);
                bundle.putDouble("TOTAL", tot);
                bundle.putDouble("DISCOUNT",discount_percentage);
                bundle.putDouble("GTOTAL", gtot);
                bundle.putDouble("CASH", paid);
                bundle.putString("DATE", sDate);
                bundle.putString("TYPE", type);
                fragmentActionListener.onActionPerformed(bundle);
                return true;
            default:
                return false;

        }
    }

    public String CurrencyFormatter(double val) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        String pattern = ((DecimalFormat) nf).toPattern();
        String newPattern = pattern.replace("\u00A4", "").trim();
        NumberFormat newFormat = new DecimalFormat(newPattern);
        String ft = String.valueOf(newFormat.format(val));
        return ft;

    }

    private void FindBluetoothDevice() {


        try {
            String spDeviceAddress = SessionHandler.getInstance(getContext()).getPrinterName();
            Log.d(TAG, "spDeviceAddress" + spDeviceAddress);

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                Log.d(TAG, "Bluetooth Adapter not found");
            }
            if (!bluetoothAdapter.isEnabled()) {
                Log.d(TAG, "Bluetooth Adapter off");
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, 0);
            }

            Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

            if (pairedDevice.size() > 0) {
                for (BluetoothDevice pairedDev : pairedDevice) {

                    Log.d(TAG, "printer name:" + pairedDev.getName());
                    Log.d(TAG, "printer address:" + pairedDev.getAddress());

                    if (pairedDev.getAddress().equals(spDeviceAddress)) {
                        bluetoothDevice = pairedDev;
                        Log.d(TAG, "Printer Found " + pairedDev.getName());
                        break;
                    }
                }
            }

            //lblPrinterName.setText("Bluetooth Printer Attached");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Open Bluetooth Printer

    void openBluetoothPrinter() throws IOException {

        if (bluetoothDevice == null) {
            Toast.makeText(getContext(), "Printer not connected", Toast.LENGTH_LONG).show();
        } else {
            try {
                //Standard uuid from string //
                UUID uuidSting = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuidSting);
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();
                inputStream = bluetoothSocket.getInputStream();
                Log.d(TAG, "printer connected:");

                beginListenData();

            } catch (Exception ex) {
                ex.printStackTrace();

            }
        }


    }

    void beginListenData() {
        Log.d(TAG, "beginListenData:");
        try {

            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int byteAvailable = inputStream.available();
                            if (byteAvailable > 0) {
                                byte[] packetByte = new byte[byteAvailable];
                                inputStream.read(packetByte);

                                for (int i = 0; i < byteAvailable; i++) {
                                    byte b = packetByte[i];
                                    if (b == delimiter) {
                                        byte[] encodedByte = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedByte, 0,
                                                encodedByte.length
                                        );
                                        final String data = new String(encodedByte, "US-ASCII");
                                        readBufferPosition = 0;
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            stopWorker = true;
                        }
                    }

                }
            });

            thread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void printData() throws IOException {
        Log.d(TAG, "printing...:");

        final DecimalFormat decimalFormat = new DecimalFormat("0.00");
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = helper.getBillTitle(database);

        if (cursor.moveToFirst()) {
            sName = cursor.getString(cursor.getColumnIndex(DataContract.BillTitles.COL_SHOP_NAME));
            sAddress = cursor.getString(cursor.getColumnIndex(DataContract.BillTitles.COL_SHOP_ADDRESS1));
            sAddress2 = cursor.getString(cursor.getColumnIndex(DataContract.BillTitles.COL_SHOP_ADDRESS2));
            sMob = cursor.getString(cursor.getColumnIndex(DataContract.BillTitles.COL_MOBILE));
            sTel = cursor.getString(cursor.getColumnIndex(DataContract.BillTitles.COL_TEL));
            sFooter = cursor.getString(cursor.getColumnIndex(DataContract.BillTitles.COL_FOOTER));
        }

        cursor.close();
        database.close();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {


                    printCustom(sName, 2, 1);
                    printCustom(sAddress, 1, 1);
                    printCustom(sAddress2, 1, 1);
                    printCustom("Tel:" + sTel + " " + "Mob:" + sMob, 1, 1);
                    printNewLine();
                    printCustom(sDate, 1, 0);
                    printCustom("#" + receiptNo, 1, 0);
                    printCustom("Cashier:" + eName, 1, 0);
                    printCustom("Customer:" + cName + "  " + cMob, 1, 0);
                    printCustom("POS :" + deviceNo, 1, 0);

                    if(type.equals("RET")){
                        printCustom("REFUND", 2, 1);
                    }

                    printCustom(new String(new char[32]).replace("\0", "."), 1, 1);

                    for (ItemsModel mm : CartData.mCartData) {
                        String item = mm.getItemName();

                        String price = String.valueOf(decimalFormat.format(mm.getItemPrice()));
                        String item_format = price + " X " + mm.getQuantity();
                        printText(leftRightAlign(item, price));
                        printNewLine();
                        printText(leftRightAlign(item_format, " "));
                        printNewLine();
                    }

                    printCustom(new String(new char[32]).replace("\0", "."), 1, 1);

                    double discount_amount = tot - gtot;
                    Log.d(TAG, "discount" + discount_amount);

                    if (discount_amount > 0) {
                        printText(leftRightAlign("Discount", "-" + String.valueOf(decimalFormat.format(discount_amount))));
                        printNewLine();
                    }
                    //printText(leftRightAlign("Total", String.valueOf(decimalFormat.format(gTotal))));
                    leftRightAlignLarge("Total",String.valueOf(decimalFormat.format(gtot)));
                    printNewLine();
                    printCustom(new String(new char[32]).replace("\0", " "), 1, 1);
                    printText(leftRightAlign("Cash", String.valueOf(decimalFormat.format(paid))));
                    printNewLine();

                    double balance = paid - gtot;
                    if (balance > 0) {
                        printText(leftRightAlign("Balance", String.valueOf(decimalFormat.format(balance))));
                        printNewLine();
                    }
                    printCustom(new String(new char[32]).replace("\0", "."), 1, 1);
                    //printNewLine();
                    printCustom(sFooter, 1, 1);
                    printNewLine();
                    printNewLine();
                    outputStream.flush();


                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }).start();



    }

    void disconnectBT() throws IOException {
        Log.d(TAG, "Printer Disconnected called.");
        try {

            if(bluetoothSocket!=null){
                stopWorker = true;
                outputStream.close();
                inputStream.close();
                bluetoothSocket.close();
                Log.d(TAG, "Printer Disconnected.");
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /*-------------------------------printing format------------------------------------*/
    private void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B, 0x21, 0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B, 0x21, 0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B, 0x21, 0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B, 0x21, 0x10}; // 3- bold with large text
        try {
            switch (size) {
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb3);
                    break;
            }

            switch (align) {
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.LF);
            //outputStream.write(cc);
            //printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print new line
    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String leftRightAlign(String str1, String str2) {

        Log.d(TAG, "leftRightAlign called");
        if (str1.length() > 12) {
            str1 = str1.substring(0, Math.min(str1.length(), 12));
        } else {

            int no_space = 12 - str1.length();
            String space = addspace(no_space);
            str1 = str1 + space;

        }
        Log.d(TAG, "first str with space:" + str1 + "#");
        Log.d(TAG, "first str with space count:" + str1.length());
        if (str2.length() < 8) {
            int no_space = 7 - str2.length();
            String space = addspace(no_space);
            str2 = space + str2;

        }
        Log.d(TAG, "second str with space:" + str2);
        Log.d(TAG, "second str with space count:" + str2.length());
        String ans = str1 + str2;
        Log.d(TAG, "final:" + ans);

        if (ans.length() < 33) {
            int n = (32 - (str1.length() + str2.length()));
            Log.d(TAG, "n count:" + n);
            ans = str1 + new String(new char[n]).replace("\0", " ") + str2;
            Log.d(TAG, "final str with space count:" + ans.length());
        }

        return ans;
    }


    String addspace(int i) {
        String temp = " ";
        StringBuilder str1 = new StringBuilder();
        for (int j = 0; j < i; j++) {
            str1.append(" ");
        }
        //  str1.append(temp);
        return str1.toString();

    }


    private void leftRightAlignLarge(String str1, String str2) {

        byte[] bb3 = new byte[]{0x1B, 0x21, 0x10};


        Log.d(TAG, "leftRightAlign called");
        if (str1.length() > 12) {
            str1 = str1.substring(0, Math.min(str1.length(), 12));
        } else {

            int no_space = 12 - str1.length();
            String space = addspace(no_space);
            str1 = str1 + space;

        }
        Log.d(TAG, "first str with space:" + str1 + "#");
        Log.d(TAG, "first str with space count:" + str1.length());
        if (str2.length() < 8) {
            int no_space = 7 - str2.length();
            String space = addspace(no_space);
            str2 = space + str2;

        }
        Log.d(TAG, "second str with space:" + str2);
        Log.d(TAG, "second str with space count:" + str2.length());
        String ans = str1 + str2;
        Log.d(TAG, "final:" + ans);

        if (ans.length() < 33) {
            int n = (32 - (str1.length() + str2.length()));
            Log.d(TAG, "n count:" + n);
            ans = str1 + new String(new char[n]).replace("\0", " ") + str2;
            Log.d(TAG, "final str with space count:" + ans.length());
        }


        try {
            outputStream.write(bb3);
            outputStream.write(ans.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //print text
    private void printText(String msg) {

        try {
            Log.d(TAG, "printText called");
            outputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*-------------------------------printing format End ------------------------------------*/

    @Override
    public void onStop() {
        super.onStop();
                try {
            disconnectBT();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
