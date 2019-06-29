package com.alhikmahpro.www.e_pos.view;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.AppUtils;
import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.adapter.EditInvoiceAdapter;
import com.alhikmahpro.www.e_pos.data.CartData;
import com.alhikmahpro.www.e_pos.data.DataContract;
import com.alhikmahpro.www.e_pos.data.ItemsModel;
import com.alhikmahpro.www.e_pos.data.PrinterCommands;
import com.alhikmahpro.www.e_pos.data.SessionHandler;
import com.alhikmahpro.www.e_pos.data.UnicodeFormatter;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.alhikmahpro.www.e_pos.interfaces.FragmentActionListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SuccessfulFragment extends Fragment {


    Unbinder unbinder;
    double total, gtotal, discount, balance, paid;
    static String TAG = "SuccessfulFragment";
    @BindView(R.id.txtDate)
    TextView txtDate;
    @BindView(R.id.txtInvoice)
    TextView txtInvoice;
    @BindView(R.id.txtEmployee)
    TextView txtEmployee;
    @BindView(R.id.txtCustomerName)
    TextView txtCustomerName;
    @BindView(R.id.txtPosNo)
    TextView txtPosNo;
    @BindView(R.id.rv_items)
    RecyclerView rvItems;
    @BindView(R.id.txtTotalAmount)
    TextView txtTotalAmount;
    @BindView(R.id.txtDiscAmount)
    TextView txtDiscAmount;
    @BindView(R.id.txtNetAmount)
    TextView txtNetAmount;
    @BindView(R.id.txtCashAmount)
    TextView txtCashAmount;
    @BindView(R.id.txtBalanceAmount)
    TextView txtBalanceAmount;
    @BindView(R.id.btn_print)
    Button btnPrint;
    @BindView(R.id.btn_new)
    Button btnNew;


    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    private ProgressDialog dialog;

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;

    OutputStream outputStream;
    InputStream inputStream;
    Thread thread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    private static final int REQUEST_CODE_ENABLING_BT = 1;
    String mDeviceAddress, mDeviceName, mDate;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothDevice mBluetoothDevice;


    boolean billStatus;


    String sName="", sAddress="", sAddress2="", sMob="", sTel="", invoiceId="", sFooter="",cName="",cMob="";
    FragmentActionListener fragmentActionListener;


    public SuccessfulFragment() {
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
        View view = inflater.inflate(R.layout.fragment_successfull, container, false);
        unbinder = ButterKnife.bind(this, view);

        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        dialog = new ProgressDialog(getContext());
        mDate = AppUtils.getDateAndTime();

        invoiceId = getArguments().getString("Invoice");
        total = getArguments().getDouble("Total");
        gtotal = getArguments().getDouble("gTotal");
        paid = getArguments().getDouble("Paid");
        discount = total - gtotal;
        balance = paid - gtotal;
        Log.d(TAG, "total" + total);

        cName=getArguments().getString("CUS_NAME");
        cMob=getArguments().getString("MOBILE");


        // String cName=SessionHandler.getInstance(getContext()).getCustomerName()+","+SessionHandler.getInstance(getContext()).getCustomerMobile();
        txtInvoice.setText(invoiceId);
        txtDate.setText(mDate);
        txtPosNo.setText(SessionHandler.getInstance(getContext()).getDeviceNo());
        txtCustomerName.setText(cName+"  "+cMob);
        txtEmployee.setText(getArguments().getString("EMP_NAME"));
        txtTotalAmount.setText(String.valueOf(total));
        txtDiscAmount.setText(CurrencyFormatter(discount));
        txtNetAmount.setText(CurrencyFormatter(gtotal));
        txtCashAmount.setText(CurrencyFormatter(paid));
        txtBalanceAmount.setText(CurrencyFormatter(balance));
        billStatus = false;
        initRecycler();
        return view;
    }

    private void initRecycler() {


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

    @OnClick(R.id.btn_print)
    public void onBtnPrintClicked() {

        try{
            FindBluetoothDevice();
            openBluetoothPrinter();
            printData();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void FindBluetoothDevice(){


        try{
            String spDeviceAddress = SessionHandler.getInstance(getContext()).getPrinterName();
            Log.d(TAG,"spDeviceAddress"+spDeviceAddress);

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter==null){
                Log.d(TAG,"Bluetooth Adapter not found");
            }
            if(!bluetoothAdapter.isEnabled()){
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT,REQUEST_CODE_ENABLING_BT);
            }

            Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

            if(pairedDevice.size()>0){
                for(BluetoothDevice pairedDev:pairedDevice){

                    Log.d(TAG,"printer name:"+pairedDev.getName());
                    Log.d(TAG,"printer address:"+pairedDev.getAddress());

                    if(pairedDev.getAddress().equals(spDeviceAddress)){
                        bluetoothDevice=pairedDev;
                        Log.d(TAG,"Printer Found "+pairedDev.getName());
                        break;
                    }
                }
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    // Open Bluetooth Printer

    void openBluetoothPrinter() throws IOException {
        try{

            //Standard uuid from string //
            UUID uuidSting = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            bluetoothSocket=bluetoothDevice.createRfcommSocketToServiceRecord(uuidSting);
            bluetoothSocket.connect();
            outputStream=bluetoothSocket.getOutputStream();
            inputStream=bluetoothSocket.getInputStream();

            Log.d(TAG,"printer connected:");

            beginListenData();

        }catch (Exception ex){
            ex.printStackTrace();

        }
    }
    void beginListenData(){
        try{

            final byte delimiter=10;
            stopWorker =false;
            readBufferPosition=0;
            readBuffer = new byte[1024];

            thread=new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker){
                        try{
                            int byteAvailable = inputStream.available();
                            if(byteAvailable>0){
                                byte[] packetByte = new byte[byteAvailable];
                                inputStream.read(packetByte);

                                for(int i=0; i<byteAvailable; i++){
                                    byte b = packetByte[i];
                                    if(b==delimiter){
                                        byte[] encodedByte = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer,0,
                                                encodedByte,0,
                                                encodedByte.length
                                        );
                                        final String data = new String(encodedByte,"US-ASCII");
                                        readBufferPosition=0;
                                    }else{
                                        readBuffer[readBufferPosition++]=b;
                                    }
                                }
                            }
                        }catch(Exception ex){
                            stopWorker=true;
                        }
                    }

                }
            });

            thread.start();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    private void printData() {

        final DecimalFormat decimalFormat = new DecimalFormat("0.00");

        dbHelper helper = new dbHelper(getContext());
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


        Log.d(TAG, "printBill");
        Thread thread = new Thread() {

            public void run() {

                try {
                   // byte[] printformat = new byte[]{0x1B, 0x21, 0x03};
                    // byte[] printformat = new byte[]{30,35,0};
                    //outputStream.write(printformat);

                    printCustom(sName, 2, 1);
                    printCustom(sAddress, 1, 1);
                    printCustom(sAddress2, 1, 1);
                    printCustom("Tel:" + sTel + "," + "Mob:" + sMob, 1, 1);
                    printNewLine();


                    //printText(leftRightAlign(date,"        "));
                    printCustom(mDate, 1, 0);
                    printCustom("#" + invoiceId, 1, 0);
                    printCustom("Cashier:" + txtEmployee.getText().toString(), 1, 0);
                    printCustom("Customer:" + txtCustomerName.getText().toString(), 1, 0);
                    printCustom("POS :" + txtPosNo.getText().toString(), 1, 0);

                    printCustom(new String(new char[32]).replace("\0", "."), 1, 1);

                    for (ItemsModel mm : CartData.mCartData) {
                        String item = mm.getItemName();
                        String qty = String.valueOf(mm.getQuantity());
                        String price = String.valueOf(decimalFormat.format(mm.getItemPrice()));
                        String sub_total = String.valueOf(decimalFormat.format(mm.getQuantity() * mm.getItemPrice()));
                        String item_format = price + " X " + mm.getQuantity();

                        printText(leftRightAlign(item, sub_total));
                        printNewLine();
                        printText(leftRightAlign(item_format, " "));
                        printNewLine();
                    }
                    printCustom(new String(new char[32]).replace("\0", "."), 1, 1);
                    if (discount > 0) {
                        printText(leftRightAlign("Dis ", "-" + String.valueOf(decimalFormat.format(discount))));
                        printNewLine();

                    }

                    leftRightAlignLarge("Total",String.valueOf(decimalFormat.format(gtotal)));
                    printNewLine();
                    printCustom(new String(new char[32]).replace("\0", " "), 1, 1);

                    printText(leftRightAlign("Cash", String.valueOf(decimalFormat.format(paid))));
                    printNewLine();
                    if (balance > 0) {
                        printText(leftRightAlign("Balance", String.valueOf(decimalFormat.format(balance))));
                        printNewLine();
                    }

                    printCustom(new String(new char[32]).replace("\0", "."), 1, 1);
                    printCustom(sFooter, 1, 1);

                    printNewLine();
                    printNewLine();
                    outputStream.flush();

                } catch (Exception e) {
                    Log.e(TAG, "Exe ", e);
                }

            }
        };
        thread.start();
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItemCart = menu.findItem(R.id.action_cart);
        menuItemCart.setVisible(false);
        MenuItem menuItemSearch = menu.findItem(R.id.action_search);
        menuItemSearch.setVisible(false);
        MenuItem menuItemPerson = menu.findItem(R.id.action_person);
        menuItemPerson.setVisible(false);
        MenuItem menuItemDelete = menu.findItem(R.id.action_delete);
        menuItemDelete.setVisible(false);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ENABLING_BT) {

            if (resultCode == RESULT_OK) {
                Log.d("onActivityResult", "Bluetooth activated");


            } else if (requestCode == RESULT_CANCELED) {
                Log.d("onActivityResult", "Bluetooth activate canceled");
            }

        }
    }

    // Disconnect Printer //
    void disconnectBT() throws IOException{
        try {
            stopWorker=true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();
            Log.d(TAG,"Printer Disconnected.");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }



    @OnClick(R.id.btn_new)
    public void onBtnNewClicked() {

        fragmentActionListener.onBackActionPerformed();
    }


    public String CurrencyFormatter(double val) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        String pattern = ((DecimalFormat) nf).toPattern();
        String newPattern = pattern.replace("\u00A4", "").trim();
        NumberFormat newFormat = new DecimalFormat(newPattern);

        String ft = String.valueOf(newFormat.format(val));
        return ft;

    }

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

    //print text
    private void printText(String msg) {
        try {
            // Print normal text
            outputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print text
    private void printLargeText(String msg) {
        byte[] bb = new byte[]{0x1B, 0x21, 0x08};
        try {
            // Print Large text

            outputStream.write(bb);
            outputStream.write(msg.getBytes());
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
        Log.d(TAG, "first str with space:"+str1+"#");
        Log.d(TAG, "first str with space count:"+str1.length());
        if (str2.length() < 8) {
            int no_space =  7- str2.length();
            String space = addspace(no_space);
            str2 = space + str2;

        }
        Log.d(TAG, "second str with space:"+str2);
        Log.d(TAG, "second str with space count:"+str2.length());
        String ans = str1 + str2;
        Log.d(TAG, "final:"+ans);

        if (ans.length() < 33) {
            int n = (32 - (str1.length() + str2.length()));
            Log.d(TAG, "n count:"+n);
            ans = str1 + new String(new char[n]).replace("\0", " ") + str2;
            Log.d(TAG, "final str with space count:"+ans.length());
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


    //print new line
    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
