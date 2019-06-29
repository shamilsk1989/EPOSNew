package com.alhikmahpro.www.e_pos.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.util.Log;

import com.alhikmahpro.www.e_pos.AppUtils;
import com.google.gson.internal.bind.SqlDateTypeAdapter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class  dbHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "e-pos";
    public static final int DATABASE_VERSION = 1;
    public static final String TAG = "dbHelper";


    private final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + DataContract.Category.TABLE_NAME + " (" +
            DataContract.Category.COL_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DataContract.Category.COL_CATEGORY_NAME + " TEXT ," + DataContract.Category.COL_IS_SYNC + " INTEGER DEFAULT 0 "+");";

    private final String SQL_CREATE_ITEM_TABLE = "CREATE TABLE " + DataContract.Items.TABLE_NAME + " (" +
            DataContract.Items.COL_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DataContract.Items.COL_ITEM_CODE + " TEXT," +
            DataContract.Items.COL_ITEM_BARCODE + " TEXT," +
            DataContract.Items.COL_ITEM_NAME + " TEXT, " +
            DataContract.Items.COL_ITEM_PRICE + " REAL ," +
            DataContract.Items.COL_CATEGORY_ID + " TEXT, " +
            DataContract.Items.COL_POSITION + " INTEGER," +
            DataContract.Items.COL_ITEM_IMAGE + " BLOB NOT NULL ," +
            DataContract.Items.COL_IS_SYNC + " INTEGER DEFAULT 0 "+");";

    private final String SQL_CREATE_INVOICE_TABLE = "CREATE TABLE " + DataContract.Invoice.TABLE_NAME + " (" +
            DataContract.Invoice.COL_INVOICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DataContract.Invoice.COL_INVOICE_NUMBER + " TEXT," +
            DataContract.Invoice.COL_TOTAL + " REAL, " +
            DataContract.Invoice.COL_DISCOUNT + " REAL, " +
            DataContract.Invoice.COL_GRAND_TOTAL + " REAL, " +
            DataContract.Invoice.COL_CASH + " REAL, " +
            DataContract.Invoice.COL_CUSTOMER+ " TEXT, " +
            DataContract.Invoice.COL_EMPLOYEE + " TEXT, " +
            DataContract.Invoice.COL_INVOICE_DATE + " DATETIME ," +
            DataContract.Invoice.COL_IS_SYNC + " INTEGER DEFAULT 0 "+");";



    private final String SQL_CREATE_REFUND_TABLE = "CREATE TABLE " + DataContract.Refund.TABLE_NAME + " (" +
            DataContract.Refund.COL_REFUND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DataContract.Refund.COL_REFUND_NUMBER+ " TEXT," +
            DataContract.Refund.COL_INVOICE_NUMBER + " TEXT," +
            DataContract.Refund.COL_TOTAL + " REAL, " +
            DataContract.Refund.COL_EMPLOYEE + " TEXT, " +
            DataContract.Refund.COL_CUSTOMER + " TEXT, " +
            DataContract.Refund.COL_REFUND_DATE + " DATETIME ," +
            DataContract.Refund.COL_IS_SYNC + " INTEGER DEFAULT 0 "+");";


    private final String SQL_CREATE_INVOICE_DETAILS_TABLE = "CREATE TABLE " + DataContract.InvoiceDetails.TABLE_NAME + " (" +
            DataContract.InvoiceDetails.COL_INVOICE_DETAILS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DataContract.InvoiceDetails.COL_INVOICE_NUMBER + " TEXT," +
            DataContract.InvoiceDetails.COL_ITEM_CODE + " TEXT, " +
            DataContract.InvoiceDetails.COL_ITEM_NAME + " TEXT, " +
            DataContract.InvoiceDetails.COL_ITEM_PRICE + " REAL, " +
            DataContract.InvoiceDetails.COL_ITEM_QUANTITY + " INTEGER, " +
            DataContract.InvoiceDetails.COL_TOTAL + " REAL, " +
            DataContract.InvoiceDetails.COL_REFUND_COUNT + " INTEGER, " +
            DataContract.InvoiceDetails.COL_INVOICE_DATE + " DATETIME ," +
            DataContract.Category.COL_IS_SYNC + " INTEGER DEFAULT 0 "+");";

    private final String SQL_CREATE_REFUND_DETAILS_TABLE = "CREATE TABLE " + DataContract.RefundDetails.TABLE_NAME + " (" +
            DataContract.RefundDetails.COL_REFUND_DETAILS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DataContract.RefundDetails.COL_REFUND_NUMBER + " TEXT," +
            DataContract.RefundDetails.COL_INVOICE_NUMBER + " TEXT," +
            DataContract.RefundDetails.COL_ITEM_CODE + " TEXT, " +
            DataContract.RefundDetails.COL_ITEM_NAME + " TEXT, " +
            DataContract.RefundDetails.COL_ITEM_PRICE + " REAL, " +
            DataContract.RefundDetails.COL_ITEM_QUANTITY + " INTEGER, " +
            DataContract.RefundDetails.COL_IS_SYNC + " INTEGER DEFAULT 0 "+");";


    private final String SQL_CREATE_BILL_TABLE = "CREATE TABLE " + DataContract.BillTitles.TABLE_NAME + " (" +
            DataContract.BillTitles.COL_TITLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DataContract.BillTitles.COL_SHOP_NAME + " TEXT," +
            DataContract.BillTitles.COL_SHOP_ADDRESS1 + " TEXT, " +
            DataContract.BillTitles.COL_SHOP_ADDRESS2 + " TEXT, " +
            DataContract.BillTitles.COL_TEL + " TEXT, " +
            DataContract.BillTitles.COL_MOBILE + " TEXT, " +
            DataContract.BillTitles.COL_FOOTER + " TEXT ," +
        //    DataContract.RegisterShop.COL_LOGO + " BLOB NOT NULL ," +
            DataContract.BillTitles.COL_IS_SYNC + " INTEGER DEFAULT 0 "+");";


    private final String SQL_CREATE_SHOP_TABLE = "CREATE TABLE " + DataContract.BusinessDetails.TABLE_NAME + " (" +
            DataContract.BusinessDetails.COL_BUSINESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DataContract.BusinessDetails.COL_BUSINESS_NAME + " TEXT," +
            DataContract.BusinessDetails.COL_LOGO + " BLOB NOT NULL ," +
            DataContract.BillTitles.COL_IS_SYNC + " INTEGER DEFAULT 0 "+");";




    private final String SQL_CREATE_EMPLOYEE_TABLE = "CREATE TABLE " + DataContract.Employee.TABLE_NAME + " (" +
            DataContract.Employee.COL_EMPLOYEE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DataContract.Employee.COL_EMPLOYEE_CODE + " TEXT," +
            DataContract.Employee.COL_EMPLOYEE_NAME + " TEXT, " +
            DataContract.Employee.COL_MOBILE + " TEXT, " +
            DataContract.Employee.COL_IS_SYNC + " INTEGER DEFAULT 0 "+");";

    private final String SQL_CREATE_CUSTOMER_TABLE = "CREATE TABLE " + DataContract.Customer.TABLE_NAME + " (" +
            DataContract.Customer.COL_CUSTOMER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DataContract.Customer.COL_CUSTOMER_NAME + " TEXT," +
            DataContract.Customer.COL_CUSTOMER_ADDRESS + " TEXT, " +
            DataContract.Customer.COL_MOBILE + " TEXT, " +
            DataContract.Customer.COL_POINT + " INTEGER, " +
            DataContract.Customer.COL_SPEND_MONEY + " REAL, " +
            DataContract.Customer.COL_IS_SYNC + " INTEGER DEFAULT 0 "+");";


    private final String SQL_CREATE_LOGIN_TABLE = "CREATE TABLE " + DataContract.LoginCredential.TABLE_NAME + " (" +
            DataContract.LoginCredential.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DataContract.LoginCredential.COL_PIN + " TEXT," +
            DataContract.LoginCredential.COL_POWER + " TEXT, "+
            DataContract.LoginCredential.COL_IS_SYNC + " INTEGER DEFAULT 0 "+");";




    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "Database Created......... ");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ITEM_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_INVOICE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_INVOICE_DETAILS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SHOP_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CUSTOMER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_EMPLOYEE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REFUND_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REFUND_DETAILS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_LOGIN_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BILL_TABLE);

        Log.d(TAG, "Table Created......... ");


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DataContract.Items.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DataContract.Category.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DataContract.Invoice.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DataContract.InvoiceDetails.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DataContract.BillTitles.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DataContract.Refund.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DataContract.RefundDetails.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DataContract.BusinessDetails.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }


//    public void registerShop(List<UserModel>list) {
//        SQLiteDatabase database = getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//
//        for (UserModel model:list
//             ) {
//
//
//            contentValues.put(DataContract.RegisterShop.COL_SHOP_NAME,model.name);
//            contentValues.put(DataContract.RegisterShop.COL_SHOP_ADDRESS1,model.address);
//            contentValues.put(DataContract.RegisterShop.COL_SHOP_ADDRESS2,model.address2);
//            contentValues.put(DataContract.RegisterShop.COL_MOBILE,model.mobile);
//            contentValues.put(DataContract.RegisterShop.COL_EMAIL,model.email);
//            contentValues.put(DataContract.RegisterShop.COL_DEVICE_ID,model.deviceId);
//            contentValues.put(DataContract.RegisterShop.COL_PASSWORD,model.password );
//        }
//
//        database.insert(DataContract.RegisterShop.TABLE_NAME, null, contentValues);
//        database.close();
//        Log.d(TAG, "one row inserted in registerUser table ......... ");
//    }

    public boolean addLoginCredential(String pin,String power){

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {
            contentValues.put(DataContract.LoginCredential.COL_PIN,pin);
            contentValues.put(DataContract.LoginCredential.COL_POWER,power);
            database.insert(DataContract.LoginCredential.TABLE_NAME,null,contentValues);
            database.close();
            Log.d(TAG, "one row inserted in Login table ......... ");
            return true;
        }catch (Exception e){
            return false;
        }


    }
     public void addCategory(String name) {
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(DataContract.Category.COL_CATEGORY_NAME,name);
        database.insert(DataContract.Category.TABLE_NAME,null,contentValues);
        database.close();
        Log.d(TAG,"one row inserted in category table ......... ");
    }

    public void updateCategory(String name,int id){

        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(DataContract.Category.COL_CATEGORY_NAME,name);
        //String selection=DataContract.Category.COL_CATEGORY_NAME+ " = "+name;
        database.update(DataContract.Category.TABLE_NAME, contentValues, DataContract.Category.COL_CATEGORY_ID +"="+id, null);
        //database.update(DataContract.Category.TABLE_NAME,contentValues,selection,null);
        database.close();
    }
    public boolean checkCategory(String name){
        SQLiteDatabase database=getReadableDatabase();
        String[] projections = {DataContract.Category.COL_CATEGORY_NAME};
        String selection=DataContract.Category.COL_CATEGORY_NAME + " LIKE ?";
        String[] selection_ars={name};
        Cursor cursor = database.query(DataContract.Category.TABLE_NAME, projections, selection,selection_ars,null,null,null);
        if(cursor.getCount()>0){
            cursor.close();
            database.close();
            return true;
        }
        else{
            cursor.close();
            database.close();
            return false;
        }
    }

    public void deleteCategory(int selectedVal) {
        SQLiteDatabase database=getWritableDatabase();
        String selection=DataContract.Category.COL_CATEGORY_ID+ " = "+selectedVal;
        database.delete(DataContract.Category.TABLE_NAME,selection,null);
        database.close();
    }

    public Cursor GetCategoryName(SQLiteDatabase database) {
        String[]projections={DataContract.Category.COL_CATEGORY_NAME};
        Cursor cursor= database.query(DataContract.Category.TABLE_NAME,projections,null,null,null,null,null);
        return cursor;
    }


    public List<ItemsModel> GetAllCategory() {


        List<ItemsModel>catArrayList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        String[] projections = {DataContract.Category.COL_CATEGORY_ID, DataContract.Category.COL_CATEGORY_NAME};
        Cursor cursor = database.query(DataContract.Category.TABLE_NAME, projections, null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                ItemsModel itemsModel=new ItemsModel();
                itemsModel.setCategoryId(cursor.getInt(cursor.getColumnIndex(DataContract.Category.COL_CATEGORY_ID)));
                itemsModel.setCategoryName(cursor.getString(cursor.getColumnIndex(DataContract.Category.COL_CATEGORY_NAME)));
                catArrayList.add(itemsModel);
            } while (cursor.moveToNext());

        }
        cursor.close();
        database.close();
        return catArrayList;

    }




//select all item in item table without discount
    public List<ItemsModel>getAllItem() {
        List<ItemsModel> ItemList = new ArrayList<>();
        ItemsModel obj;
        SQLiteDatabase database=getReadableDatabase();
        String[]projections={DataContract.Items.COL_ITEM_ID,DataContract.Items.COL_ITEM_CODE, DataContract.Items.COL_ITEM_NAME,DataContract.Items.COL_ITEM_PRICE,DataContract.Items.COL_ITEM_IMAGE};
        String selection=DataContract.Items.COL_CATEGORY_ID + " != ?";
        String[] selection_ars={"discount"};
        Cursor cursor=database.query(DataContract.Items.TABLE_NAME,projections,selection,selection_ars,null,null,null);
        if(cursor.moveToFirst())
        {
            do{
                obj=new ItemsModel();
                //ban.setItemId(localCursor.getString(localCursor.getColumnIndex("ItemId")));
                obj.setItemId(cursor.getInt(cursor.getColumnIndex(DataContract.Items.COL_ITEM_ID)));
                obj.setItemCode(cursor.getString(cursor.getColumnIndex(DataContract.Items.COL_ITEM_CODE)));
                obj.setItemName(cursor.getString(cursor.getColumnIndex(DataContract.Items.COL_ITEM_NAME)));
                obj.setItemPrice(cursor.getDouble(cursor.getColumnIndex(DataContract.Items.COL_ITEM_PRICE)));
                obj.setItemImage(cursor.getBlob(cursor.getColumnIndex(DataContract.Items.COL_ITEM_IMAGE)));

                ItemList.add(obj);
            }while (cursor.moveToNext());

        }

        cursor.close();
        database.close();
        return ItemList;


    }
    //select all item in item table with discount
    public List<ItemsModel>getSaleItem() {
        List<ItemsModel> ItemList = new ArrayList<>();
        ItemsModel obj;
        SQLiteDatabase database=getReadableDatabase();
        String[]projections={DataContract.Items.COL_ITEM_ID, DataContract.Items.COL_ITEM_NAME,DataContract.Items.COL_ITEM_PRICE,DataContract.Items.COL_ITEM_IMAGE,DataContract.Items.COL_CATEGORY_ID};

        Cursor cursor=database.query(DataContract.Items.TABLE_NAME,projections,null,null,null,null,null);
        if(cursor.moveToFirst())
        {
            do{
                obj=new ItemsModel();
                //ban.setItemId(localCursor.getString(localCursor.getColumnIndex("ItemId")));
                obj.setItemId(cursor.getInt(cursor.getColumnIndex(DataContract.Items.COL_ITEM_ID)));
                obj.setItemName(cursor.getString(cursor.getColumnIndex(DataContract.Items.COL_ITEM_NAME)));
                obj.setItemPrice(cursor.getDouble(cursor.getColumnIndex(DataContract.Items.COL_ITEM_PRICE)));
                obj.setItemImage(cursor.getBlob(cursor.getColumnIndex(DataContract.Items.COL_ITEM_IMAGE)));
                obj.setCategoryName(cursor.getString(cursor.getColumnIndex(DataContract.Items.COL_CATEGORY_ID)));
                ItemList.add(obj);
            }while (cursor.moveToNext());

        }

        cursor.close();
        database.close();
        return ItemList;


    }




    public Cursor  getItemByID(SQLiteDatabase database,int id){

        String[]projections={DataContract.Items.COL_ITEM_CODE,DataContract.Items.COL_ITEM_NAME,DataContract.Items.COL_ITEM_PRICE,DataContract.Items.COL_CATEGORY_ID,DataContract.Items.COL_ITEM_IMAGE,DataContract.Items.COL_POSITION};
        String selection=DataContract.Items.COL_ITEM_ID+ " = "+id;

        Cursor cursor= database.query(DataContract.Items.TABLE_NAME,projections,selection,null,null,null,null);
        return cursor;


    }

    public boolean checkItem(String name){
        SQLiteDatabase database=getReadableDatabase();
        String[] projections = {DataContract.Items.COL_ITEM_NAME};
        String selection=DataContract.Items.COL_ITEM_NAME.toLowerCase() + " LIKE ?";
        String[] selection_ars={name};
        Cursor cursor = database.query(DataContract.Items.TABLE_NAME, projections, selection,selection_ars,null,null,null);
        if(cursor.getCount()>0){
            cursor.close();
            database.close();
            return true;
        }
        else{
            cursor.close();
            database.close();
            return false;
        }
    }





    public List<ItemsModel>getItemByCategory(String category) {
        List<ItemsModel> ItemList = new ArrayList<>();
        ItemsModel obj;
        SQLiteDatabase database=getReadableDatabase();
        String[]projections={DataContract.Items.COL_ITEM_ID, DataContract.Items.COL_ITEM_NAME,DataContract.Items.COL_ITEM_PRICE,DataContract.Items.COL_ITEM_IMAGE};
        String selection=DataContract.Items.COL_CATEGORY_ID + " LIKE ?";
        String[] selection_ars={category};

        Cursor cursor=database.query(DataContract.Items.TABLE_NAME,projections,selection,selection_ars,null,null,null);
        if(cursor.moveToFirst())
        {
            do{
                obj=new ItemsModel();
                //ban.setItemId(localCursor.getString(localCursor.getColumnIndex("ItemId")));
                obj.setItemId(cursor.getInt(cursor.getColumnIndex(DataContract.Items.COL_ITEM_ID)));
                obj.setItemName(cursor.getString(cursor.getColumnIndex(DataContract.Items.COL_ITEM_NAME)));
                obj.setItemPrice(cursor.getDouble(cursor.getColumnIndex(DataContract.Items.COL_ITEM_PRICE)));
                obj.setItemImage(cursor.getBlob(cursor.getColumnIndex(DataContract.Items.COL_ITEM_IMAGE)));

                ItemList.add(obj);
            }while (cursor.moveToNext());

        }

        cursor.close();
        database.close();
        return ItemList;


    }

    public void addItems(String barcode, String code,String item_name, double rate, String selected_category, byte[] data,int position) {

        Log.d(TAG,"rate::"+rate);
        Log.d(TAG,"selected_category::"+selected_category);
        Log.d(TAG,"data::"+data);
        Log.d(TAG,"position::"+position);


        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();

        contentValues.put(DataContract.Items.COL_ITEM_BARCODE,barcode);
        contentValues.put(DataContract.Items.COL_ITEM_CODE,code);
        contentValues.put(DataContract.Items.COL_ITEM_NAME,item_name);
        contentValues.put(DataContract.Items.COL_ITEM_PRICE,rate);
        contentValues.put(DataContract.Items.COL_CATEGORY_ID,selected_category);
        contentValues.put(DataContract.Items.COL_ITEM_IMAGE,data);
        contentValues.put(DataContract.Items.COL_POSITION,position);
        database.insert(DataContract.Items.TABLE_NAME,null,contentValues);
        Log.d(TAG,"one row inserted in item table ......... ");
        database.close();

    }
    public void updateItem(String barcode,String code,String item_name, double rate, String selected_category, byte[] data,int position,int id){

        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(DataContract.Items.COL_ITEM_BARCODE,barcode);
        contentValues.put(DataContract.Items.COL_ITEM_CODE,code);
        contentValues.put(DataContract.Items.COL_ITEM_NAME,item_name);
        contentValues.put(DataContract.Items.COL_ITEM_PRICE,rate);
        contentValues.put(DataContract.Items.COL_CATEGORY_ID,selected_category);
        contentValues.put(DataContract.Items.COL_ITEM_IMAGE,data);
        contentValues.put(DataContract.Items.COL_POSITION,position);
        //String selection=DataContract.Category.COL_CATEGORY_NAME+ " = "+name;
        database.update(DataContract.Items.TABLE_NAME, contentValues, DataContract.Items.COL_ITEM_ID +"="+id, null);
        //database.update(DataContract.Category.TABLE_NAME,contentValues,selection,null);
        database.close();
    }

    public void deleteItem(int selectedId) {

        SQLiteDatabase database=getWritableDatabase();
        String selection=DataContract.Items.COL_ITEM_ID+ " = "+selectedId;
        database.delete(DataContract.Items.TABLE_NAME,selection,null);
        database.close();

    }




    public void addEmployee(String name,String code,String mobile) {
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(DataContract.Employee.COL_EMPLOYEE_CODE,code);
        contentValues.put(DataContract.Employee.COL_EMPLOYEE_NAME,name);
        contentValues.put(DataContract.Employee.COL_MOBILE,mobile);
        database.insert(DataContract.Employee.TABLE_NAME,null,contentValues);
        database.close();
        Log.d(TAG,"one row inserted in Employee table ......... ");
    }

    public void updateEmployee(String name,String code,String mobile,int id) {
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(DataContract.Employee.COL_EMPLOYEE_CODE,code);
        contentValues.put(DataContract.Employee.COL_EMPLOYEE_NAME,name);
        contentValues.put(DataContract.Employee.COL_MOBILE,mobile);
        database.update(DataContract.Employee.TABLE_NAME, contentValues, DataContract.Employee.COL_EMPLOYEE_ID +"="+id, null);
        database.close();
        Log.d(TAG,"one row inserted in Employee table ......... ");
    }
    public List<UserModel> GetAllEmployee() {
        List<UserModel>list=new ArrayList<>();
        SQLiteDatabase database=getReadableDatabase();
        String[]projections={DataContract.Employee.COL_EMPLOYEE_ID, DataContract.Employee.COL_EMPLOYEE_CODE,DataContract.Employee.COL_EMPLOYEE_NAME,DataContract.Employee.COL_MOBILE};
        Cursor cursor=database.query(DataContract.Employee.TABLE_NAME,projections,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {

                UserModel model=new UserModel();
                model.setId(cursor.getInt(cursor.getColumnIndex(DataContract.Employee.COL_EMPLOYEE_ID)));
                model.setCode(cursor.getString(cursor.getColumnIndex(DataContract.Employee.COL_EMPLOYEE_CODE)));
                model.setName(cursor.getString(cursor.getColumnIndex(DataContract.Employee.COL_EMPLOYEE_NAME)));
                model.setMobile(cursor.getString(cursor.getColumnIndex(DataContract.Employee.COL_MOBILE)));
                list.add(model);
            }while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return list;
    }

    public Cursor getEmployeeById(SQLiteDatabase database,int id) {

        String[]projections={DataContract.Employee.COL_EMPLOYEE_CODE,DataContract.Employee.COL_EMPLOYEE_NAME,DataContract.Employee.COL_MOBILE};
        String selection=DataContract.Employee.COL_EMPLOYEE_ID+ " = "+id;

        Cursor cursor= database.query(DataContract.Employee.TABLE_NAME,projections,selection,null,null,null,null);
        return cursor;

    }

    public boolean checkEmployee(String code,String name){
        SQLiteDatabase database=getReadableDatabase();
        String[] projections = {DataContract.Employee.COL_EMPLOYEE_CODE};
        //String selection=DataContract.Invoice.COL_INVOICE_NUMBER+ " LIKE ? AND "+DataContract.InvoiceDetails.COL_ITEM_NAME+ " LIKE ?";
        String selection=DataContract.Employee.COL_EMPLOYEE_CODE + " LIKE ? OR "+DataContract.Employee.COL_EMPLOYEE_NAME + " LIKE ?";
        String[] selection_ars={code,name};
        Cursor cursor = database.query(DataContract.Employee.TABLE_NAME, projections, selection,selection_ars,null,null,null);
        if(cursor.getCount()>0){
            cursor.close();
            database.close();
            return true;
        }
        else{
            cursor.close();
            database.close();
            return false;
        }
    }

    public void deleteEmployee(int selectedId) {

        SQLiteDatabase database=getWritableDatabase();
        String selection=DataContract.Employee.COL_EMPLOYEE_ID+ " = "+selectedId;
        database.delete(DataContract.Employee.TABLE_NAME,selection,null);
        database.close();

    }

    public void addCustomer(String name,String address,String mobile ) {
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        try{
            contentValues.put(DataContract.Customer.COL_CUSTOMER_NAME,name);
            contentValues.put(DataContract.Customer.COL_CUSTOMER_ADDRESS,address);
            contentValues.put(DataContract.Customer.COL_MOBILE,mobile);
            contentValues.put(DataContract.Customer.COL_SPEND_MONEY,0);
            contentValues.put(DataContract.Customer.COL_POINT,0);
            database.insert(DataContract.Customer.TABLE_NAME,null,contentValues);
            Log.d(TAG,"one row inserted in Customer table ......... ");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        database.close();

    }

    public List<UserModel> GetAllCustomer() {
        List<UserModel>list=new ArrayList<>();
        SQLiteDatabase database=getReadableDatabase();
        String[]projections={DataContract.Customer.COL_CUSTOMER_ID, DataContract.Customer.COL_CUSTOMER_NAME,DataContract.Customer.COL_MOBILE,DataContract.Customer.COL_POINT};
        Cursor cursor=database.query(DataContract.Customer.TABLE_NAME,projections,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {

                UserModel model=new UserModel();
                model.setId(cursor.getInt(cursor.getColumnIndex(DataContract.Customer.COL_CUSTOMER_ID)));
                model.setName(cursor.getString(cursor.getColumnIndex(DataContract.Customer.COL_CUSTOMER_NAME)));
                model.setMobile(cursor.getString(cursor.getColumnIndex(DataContract.Customer.COL_MOBILE)));
                model.setMoney(cursor.getDouble(cursor.getColumnIndex(DataContract.Customer.COL_POINT)));
                list.add(model);
            }while (cursor.moveToNext());
        }

        return list;
    }


    public Cursor getCustomerById(SQLiteDatabase database, int id) {

        String[]projections={DataContract.Customer.COL_CUSTOMER_NAME,DataContract.Customer.COL_CUSTOMER_ADDRESS,DataContract.Customer.COL_MOBILE};
        String selection=DataContract.Customer.COL_CUSTOMER_ID+ " = "+id;

        Cursor cursor= database.query(DataContract.Customer.TABLE_NAME,projections,selection,null,null,null,null);
        return cursor;

    }

    public void updateCustomer(String name,String address,String mobile,int id) {
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(DataContract.Customer.COL_CUSTOMER_NAME,name);
        contentValues.put(DataContract.Customer.COL_CUSTOMER_ADDRESS,address);
        contentValues.put(DataContract.Customer.COL_MOBILE,mobile);
        database.update(DataContract.Customer.TABLE_NAME, contentValues, DataContract.Customer.COL_CUSTOMER_ID +"="+id, null);
        database.close();
        Log.d(TAG,"one row updated in Customer table ......... ");
    }



    public void deleteCustomer(int selectedId) {

        SQLiteDatabase database=getWritableDatabase();
        String selection=DataContract.Customer.COL_CUSTOMER_ID+ " = "+selectedId;
        database.delete(DataContract.Customer.TABLE_NAME,selection,null);
        database.close();

    }


    public int getLastInvoiceId(){

        SQLiteDatabase database=getReadableDatabase();
        String []projections={DataContract.Invoice.COL_INVOICE_ID};
        Cursor cursor=database.query(DataContract.Invoice.TABLE_NAME,projections,null,null,null,null,DataContract.Invoice.COL_INVOICE_ID +" DESC ","1");

        int id=0;
        if(cursor.moveToFirst()){
            id=cursor.getInt(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_ID));

        }
        cursor.close();
        database.close();
        return id;
    }


    public boolean addInvoice(String invoiceNumber,Double total,Double gTotal,Double discount,Double paid,String customer,String employee,String date,int sync)
    {
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        boolean result=false;
        try{
            contentValues.put(DataContract.Invoice.COL_INVOICE_NUMBER,invoiceNumber);
            contentValues.put(DataContract.Invoice.COL_TOTAL,total);
            contentValues.put(DataContract.Invoice.COL_DISCOUNT,discount);
            contentValues.put(DataContract.Invoice.COL_GRAND_TOTAL,gTotal);
            contentValues.put(DataContract.Invoice.COL_CASH,paid);
            contentValues.put(DataContract.Invoice.COL_CUSTOMER,customer);
            contentValues.put(DataContract.Invoice.COL_EMPLOYEE,employee);
            contentValues.put(DataContract.Invoice.COL_INVOICE_DATE,date);
            contentValues.put(DataContract.Invoice.COL_IS_SYNC,sync);


            database.insert(DataContract.Invoice.TABLE_NAME,null,contentValues);
            Log.d(TAG,"one row inserted in invoice table ......... ");
            result=true;

        }catch (SQLException e){
            result=false;

        }
        database.close();
        return result;
    }

    public boolean addInvoiceDetails( String invoiceNo, String date)
    {

        SQLiteDatabase database=getReadableDatabase();
        ContentValues contentValues=new ContentValues();
        boolean result=false;

        try {
            for (ItemsModel model : CartData.mCartData) {

                contentValues.put(DataContract.InvoiceDetails.COL_INVOICE_NUMBER, invoiceNo);
                contentValues.put(DataContract.InvoiceDetails.COL_ITEM_CODE, model.getItemCode());
                contentValues.put(DataContract.InvoiceDetails.COL_ITEM_NAME, model.getItemName());
                contentValues.put(DataContract.InvoiceDetails.COL_ITEM_QUANTITY, model.getQuantity());
                contentValues.put(DataContract.InvoiceDetails.COL_ITEM_PRICE, model.getItemPrice());
                contentValues.put(DataContract.InvoiceDetails.COL_TOTAL, model.getQuantity() * model.getItemPrice());
                contentValues.put(DataContract.InvoiceDetails.COL_INVOICE_DATE, date);
                contentValues.put(DataContract.InvoiceDetails.COL_REFUND_COUNT,0);//add 0 as default refund count in invoice details
                database.insertOrThrow(DataContract.InvoiceDetails.TABLE_NAME, null, contentValues);
                Log.d(TAG, "one row inserted in invoiceDetails table ......... ");
                result=true;
             }


        } catch (SQLException e) {
            result=false;
        }
        database.close();
        return result;



    }

    public List<InvoiceModel> GetAllInvoice() {

        List<InvoiceModel>invArrayList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        String[] projections = {DataContract.Invoice.COL_INVOICE_ID, DataContract.Invoice.COL_INVOICE_NUMBER,DataContract.Invoice.COL_EMPLOYEE,DataContract.Invoice.COL_CUSTOMER
        ,DataContract.Invoice.COL_GRAND_TOTAL,DataContract.Invoice.COL_DISCOUNT,DataContract.Invoice.COL_TOTAL,DataContract.Invoice.COL_CASH,DataContract.Invoice.COL_INVOICE_DATE
        ,DataContract.Invoice.COL_IS_SYNC};
        //String selection=DataContract.Invoice.COL_TYPE + " LIKE ?";
        //String[] selection_ars={type};
        String OrderBy=DataContract.Invoice.COL_INVOICE_ID+" DESC ";

        Cursor cursor = database.query(DataContract.Invoice.TABLE_NAME, projections,null,null,null,null,OrderBy);
        if (cursor.moveToFirst()) {
            do {
                InvoiceModel model=new InvoiceModel();
                model.setInvoiceId(cursor.getInt(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_ID)));
                model.setInvoiceNumber(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_NUMBER)));
                //set refundNumber is null in invoice edit
                //model.setRefundNumber("");
                model.setEmployeeName(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_EMPLOYEE)));
                model.setCustomerName(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_CUSTOMER)));
                model.setGrantTotal(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_GRAND_TOTAL)));
                model.setDiscount(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_DISCOUNT)));
                model.setTotal(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_TOTAL)));
                model.setPaid(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_CASH)));
                model.setInvoiceDate(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_DATE)));
                model.setSync_status(cursor.getInt(cursor.getColumnIndex(DataContract.Invoice.COL_IS_SYNC)));


                invArrayList.add(model);
            } while (cursor.moveToNext());

        }
        cursor.close();
        database.close();
        return invArrayList;

    }

    public List<ItemsModel> GetAllInvoiceDetails(String number){
        List<ItemsModel>invArrayList = new ArrayList<>();

        SQLiteDatabase database=getReadableDatabase();
        String[] projections = {DataContract.InvoiceDetails.COL_ITEM_CODE,DataContract.InvoiceDetails.COL_ITEM_NAME,DataContract.InvoiceDetails.COL_ITEM_QUANTITY,DataContract.InvoiceDetails.COL_ITEM_PRICE,
        DataContract.InvoiceDetails.COL_REFUND_COUNT};
        String selection=DataContract.InvoiceDetails.COL_INVOICE_NUMBER + " LIKE ?";
        String[] selection_ars={number};

        Cursor cursor = database.query(DataContract.InvoiceDetails.TABLE_NAME, projections, selection,selection_ars,null,null,null);

        Log.d(TAG,"invoice details cursor size"+cursor.getCount());
        if(cursor.moveToFirst()){
            do{

                ItemsModel model=new ItemsModel();
                model.setItemCode(cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_ITEM_CODE)));
                model.setItemName(cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_ITEM_NAME)));
                model.setItemPrice(cursor.getDouble(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_ITEM_PRICE)));
                model.setQuantity(cursor.getInt(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_ITEM_QUANTITY)));
               //set refund count for each items before editing
                model.setRefundCount(cursor.getInt(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_REFUND_COUNT)));
                model.setTempRefundCounter(0);

                invArrayList.add(model);
            }while (cursor.moveToNext());

        }
        cursor.close();
        database.close();

        return invArrayList;

    }

    public int getLastRefundId(){

        SQLiteDatabase database=getReadableDatabase();
        String []projections={DataContract.Refund.COL_REFUND_ID};
        Cursor cursor=database.query(DataContract.Refund.TABLE_NAME,projections,null,null,null,null,DataContract.Refund.COL_REFUND_ID +" DESC ","1");

        int id=0;
        if(cursor.moveToFirst()){
            id=cursor.getInt(cursor.getColumnIndex(DataContract.Refund.COL_REFUND_ID));

        }
        cursor.close();
        database.close();
        return id;
    }



    public boolean addRefund(String refundNumber,String invoiceNumber,Double total,String emp,String cus,String date,int sync)
    {

        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        boolean result=false;
        try{
            contentValues.put(DataContract.Refund.COL_REFUND_NUMBER,refundNumber);
            contentValues.put(DataContract.Refund.COL_INVOICE_NUMBER,invoiceNumber);
            contentValues.put(DataContract.Refund.COL_TOTAL,total);
            contentValues.put(DataContract.Refund.COL_EMPLOYEE,emp);
            contentValues.put(DataContract.Refund.COL_CUSTOMER,cus);
            contentValues.put(DataContract.Refund.COL_REFUND_DATE,date);
            contentValues.put(DataContract.Refund.COL_IS_SYNC,sync);

            database.insert(DataContract.Refund.TABLE_NAME,null,contentValues);
            Log.d(TAG,"one row inserted in refund table ......... ");
            result=true;

        }catch (SQLException e){
            result=false;

        }
        database.close();
        return result;
    }

    public void updateInvoiceDetailByRefund(String invoiceNo,String itemName,int qty){

        SQLiteDatabase database=getWritableDatabase();
        Log.d(TAG,"update function ......... ");

        String selection=DataContract.Invoice.COL_INVOICE_NUMBER+ " LIKE ? AND "+DataContract.InvoiceDetails.COL_ITEM_NAME+ " LIKE ?";
        String[]arg={invoiceNo,itemName};
        ContentValues contentValues=new ContentValues();
        contentValues.put(DataContract.InvoiceDetails.COL_REFUND_COUNT,qty);
        database.update(DataContract.InvoiceDetails.TABLE_NAME,contentValues,selection,arg);
        database.close();



    }


    public List<InvoiceModel> GetAllRefund() {

        List<InvoiceModel>invArrayList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        String[] projections = {DataContract.Refund.COL_REFUND_ID, DataContract.Refund.COL_INVOICE_NUMBER,DataContract.Refund.COL_REFUND_NUMBER,
                DataContract.Refund.COL_CUSTOMER,DataContract.Refund.COL_EMPLOYEE,DataContract.Refund.COL_TOTAL,DataContract.Refund.COL_REFUND_DATE
        ,DataContract.Refund.COL_IS_SYNC };
        String OrderBy=DataContract.Refund.COL_REFUND_ID+" DESC ";

        Cursor cursor = database.query(DataContract.Refund.TABLE_NAME, projections, null,null,null,null,OrderBy);
        if (cursor.moveToFirst()) {
            do {
                InvoiceModel model=new InvoiceModel();
                model.setInvoiceId(cursor.getInt(cursor.getColumnIndex(DataContract.Refund.COL_REFUND_ID)));
                model.setInvoiceNumber(cursor.getString(cursor.getColumnIndex(DataContract.Refund.COL_INVOICE_NUMBER)));
                model.setRefundNumber(cursor.getString(cursor.getColumnIndex(DataContract.Refund.COL_REFUND_NUMBER)));
                model.setEmployeeName(cursor.getString(cursor.getColumnIndex(DataContract.Refund.COL_EMPLOYEE)));
                model.setCustomerName(cursor.getString(cursor.getColumnIndex(DataContract.Refund.COL_CUSTOMER)));
                model.setGrantTotal(cursor.getDouble(cursor.getColumnIndex(DataContract.Refund.COL_TOTAL)));
                model.setDiscount(0);
                model.setTotal(cursor.getDouble(cursor.getColumnIndex(DataContract.Refund.COL_TOTAL)));
                model.setInvoiceDate(cursor.getString(cursor.getColumnIndex(DataContract.Refund.COL_REFUND_DATE)));
                model.setSync_status(cursor.getInt(cursor.getColumnIndex(DataContract.Refund.COL_IS_SYNC)));
                invArrayList.add(model);
            } while (cursor.moveToNext());

        }
        cursor.close();
        database.close();
        return invArrayList;


    }




    public Cursor getinvoicebyDate(String from){
        SQLiteDatabase database = this.getReadableDatabase();
        String MY_QUERY="SELECT  SUM(total) as Total,SUM(grant_total) as Grant,date from invoice  WHERE date=? ";

        Cursor cursor=database.rawQuery(MY_QUERY, new String[]{from});


        //Log.d(TAG, "Cursor......... " + cursor.getCount());
        return cursor;


    }

    public double getRefundTotal(String date){
        SQLiteDatabase database = this.getReadableDatabase();
        double res=0;


        String QUERY=" SELECT SUM(total) as Refund FROM refund WHERE date=? ";
        Cursor cursor=database.rawQuery(QUERY,new String[]{date});
        if(cursor.moveToNext()){
            res=cursor.getDouble(cursor.getColumnIndex("Refund"));
        }
        cursor.close();
        return res;




    }


    public double getRefundbyEmployee(String from,String to,String name){
        Log.d(TAG, "refund .........from: "+from+"...To:"+to);
        SQLiteDatabase database = this.getReadableDatabase();
        double res=0;
        Cursor c = database.rawQuery("select SUM(total) as Refund from " + DataContract.Refund.TABLE_NAME
                + " where date BETWEEN '" + from + "' AND '" + to + "' AND employee = '" + name + "' ", null);
        if(c.moveToNext()){

            res=c.getDouble(c.getColumnIndex("Refund"));
            Log.d(TAG, "refund ......... "+res);
        }
        c.close();
        return res;
    }
    public double getRefundbyCustomer(String from,String to,String name){
        Log.d(TAG, "refund .........from: "+from+"...To:"+to+"Customer...."+name );
        SQLiteDatabase database = this.getReadableDatabase();
        double res=0;
        Cursor c = database.rawQuery("select SUM(total) as Refund from " + DataContract.Refund.TABLE_NAME
                + " where date BETWEEN '" + from + "' AND '" + to + "' AND customer = '" + name + "' ", null);
        if(c.moveToNext()){

            res=c.getDouble(c.getColumnIndex("Refund"));
            Log.d(TAG, "refund ......... "+res);
        }
        c.close();
        return res;
    }


    public boolean addRefundDetails( List<ItemsModel>arrayList,String refundNo, String invoiceNo)
    {

        SQLiteDatabase database=getReadableDatabase();
        ContentValues contentValues=new ContentValues();
        boolean result=false;

        try {
            for (ItemsModel model : arrayList) {

                contentValues.put(DataContract.RefundDetails.COL_REFUND_NUMBER, refundNo);
                contentValues.put(DataContract.RefundDetails.COL_INVOICE_NUMBER, invoiceNo);
                contentValues.put(DataContract.RefundDetails.COL_ITEM_CODE, model.getItemCode());
                contentValues.put(DataContract.RefundDetails.COL_ITEM_NAME, model.getItemName());
                contentValues.put(DataContract.RefundDetails.COL_ITEM_QUANTITY, model.getQuantity());
                contentValues.put(DataContract.RefundDetails.COL_ITEM_PRICE, model.getItemPrice());
                database.insertOrThrow(DataContract.RefundDetails.TABLE_NAME, null, contentValues);
                Log.d(TAG, "one row inserted in RefundDetails table ......... ");
                result=true;
            }


        } catch (SQLException e) {
            result=false;
        }
        database.close();
        return result;



    }

    public List<ItemsModel> GetAllRefundDetails(String number){
        List<ItemsModel>invArrayList = new ArrayList<>();

        SQLiteDatabase database=getReadableDatabase();
        String[] projections = {DataContract.RefundDetails.COL_ITEM_CODE,DataContract.RefundDetails.COL_ITEM_NAME,
                DataContract.RefundDetails.COL_ITEM_QUANTITY,DataContract.RefundDetails.COL_ITEM_PRICE};
        String selection=DataContract.RefundDetails.COL_REFUND_NUMBER + " LIKE ?";
        String[] selection_ars={number};

        Cursor cursor = database.query(DataContract.RefundDetails.TABLE_NAME, projections, selection,selection_ars,null,null,null);

        Log.d(TAG,"Refund details cursor size"+cursor.getCount());
        if(cursor.moveToFirst()){
            do{

                ItemsModel model=new ItemsModel();
                model.setItemCode(cursor.getString(cursor.getColumnIndex(DataContract.RefundDetails.COL_ITEM_CODE)));
                model.setItemName(cursor.getString(cursor.getColumnIndex(DataContract.RefundDetails.COL_ITEM_NAME)));
                model.setItemPrice(cursor.getDouble(cursor.getColumnIndex(DataContract.RefundDetails.COL_ITEM_PRICE)));
                model.setQuantity(cursor.getInt(cursor.getColumnIndex(DataContract.RefundDetails.COL_ITEM_QUANTITY)));
                //set refund count for each items before editing
                model.setRefundCount(0);
                model.setTempRefundCounter(0);

                invArrayList.add(model);
            }while (cursor.moveToNext());

        }
        cursor.close();
        database.close();

        return invArrayList;

    }

    public String getLogin(String pin) {

        SQLiteDatabase database=getReadableDatabase();
        String[] projections = {DataContract.LoginCredential.COL_PIN,DataContract.LoginCredential.COL_POWER,};
        String selection=DataContract.LoginCredential.COL_PIN + " LIKE ?";
        String[] selection_ars={pin};

        Cursor cursor = database.query(DataContract.LoginCredential.TABLE_NAME, projections, selection,selection_ars,null,null,null);
        if(cursor.moveToFirst()){
           String power=cursor.getString(cursor.getColumnIndex(DataContract.LoginCredential.COL_POWER));
           return power;
        }
        else{
            return null;
        }


    }


    public boolean getAllLogin(String power) {

        SQLiteDatabase database=getReadableDatabase();
        String[] projections = {DataContract.LoginCredential.COL_PIN,DataContract.LoginCredential.COL_POWER};
        String selection=DataContract.LoginCredential.COL_POWER+ " LIKE  ? " ;
        String[]arg={power};

        Cursor cursor = database.query(DataContract.LoginCredential.TABLE_NAME, projections, selection,arg,null,null,null);
        if(cursor.moveToFirst()){
            cursor.close();
            database.close();

            return true;
        }
        else{
            cursor.close();
            database.close();
            return false;
        }



    }

    public boolean deleteUser(String power) {
        SQLiteDatabase database=getReadableDatabase();
        return database.delete(DataContract.LoginCredential.TABLE_NAME, DataContract.LoginCredential.COL_POWER + "=?", new String[]{power}) > 0;
    }




    public boolean checkBillTitle(){
        SQLiteDatabase database=getReadableDatabase();
        String []projections={DataContract.BillTitles.COL_SHOP_NAME};
        Cursor cursor = database.query(DataContract.BillTitles.TABLE_NAME, projections, null,null,null,null,null);
        if(cursor.moveToFirst()){
            return true;
        }
        else{
            return false;
        }

    }

    public boolean addBillTitle(String main, String sub1, String sub2,String mob,String tel,String footer) {

        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        boolean result=false;
        try{
            contentValues.put(DataContract.BillTitles.COL_SHOP_NAME,main);
            contentValues.put(DataContract.BillTitles.COL_SHOP_ADDRESS1,sub1);
            contentValues.put(DataContract.BillTitles.COL_SHOP_ADDRESS2,sub2);
            contentValues.put(DataContract.BillTitles.COL_MOBILE,mob);
            contentValues.put(DataContract.BillTitles.COL_TEL,tel);
            contentValues.put(DataContract.BillTitles.COL_FOOTER,footer);

            //database.update(DataContract.RegisterShop.TABLE_NAME,null,contentValues,null,null,null);
            database.insert(DataContract.BillTitles.TABLE_NAME,null,contentValues);
            Log.d(TAG,"one row inserted in BillTitles table ......... ");
            result=true;

        }catch (SQLException e){
            result=false;

        }
        database.close();
        return result;


    }


    public Cursor getBillTitle(SQLiteDatabase database) {

        String []projections={DataContract.BillTitles.COL_SHOP_NAME,DataContract.BillTitles.COL_SHOP_ADDRESS1,DataContract.BillTitles.COL_SHOP_ADDRESS2,
                DataContract.BillTitles.COL_MOBILE,DataContract.BillTitles.COL_TEL,DataContract.BillTitles.COL_FOOTER};
        Cursor cursor=database.query(DataContract.BillTitles.TABLE_NAME,projections,null,
                null,null,null,DataContract.BillTitles.COL_TITLE_ID +" DESC ","1");

        // database.close();
        return cursor;


    }



    public void addBusinessDetails(String name,byte[]logo){
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        try{
            contentValues.put(DataContract.BusinessDetails.COL_BUSINESS_NAME,name);
            contentValues.put(DataContract.BusinessDetails.COL_LOGO,logo);
            database.insert(DataContract.BusinessDetails.TABLE_NAME,null,contentValues);
            Log.d(TAG,"one row inserted in addBusinessDetails table ......... ");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        database.close();

    }

    public void updateLogo(byte[] img,String name){
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();

        int id=1;
        contentValues.put(DataContract.BusinessDetails.COL_LOGO,img);

        //String selection=DataContract.Category.COL_CATEGORY_NAME+ " = "+name;
        try{
            database.update(DataContract.BusinessDetails.TABLE_NAME, contentValues, DataContract.BusinessDetails.COL_BUSINESS_ID +"="+id, null);
            Log.d(TAG,"Logo updated ......... ");
        }catch (Exception e){
            e.printStackTrace();
        }
        database.close();
    }

    public byte[] getLogo(){
        SQLiteDatabase database=getReadableDatabase();
        byte[]img=null;
        String []projections={DataContract.BusinessDetails.COL_LOGO};
        Cursor cursor=database.query(DataContract.BusinessDetails.TABLE_NAME,projections,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            img=cursor.getBlob(cursor.getColumnIndex(DataContract.BusinessDetails.COL_LOGO));
        }
        cursor.close();
        return img;


    }

    public Cursor salebyEmployee(String from,String to,String emp){
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor c = database.rawQuery("select SUM(total) as Total,SUM(grant_total) as Grant from " + DataContract.Invoice.TABLE_NAME
                + " where date BETWEEN '" + from + "' AND '" + to + "' AND employee = '" + emp + "' ", null);


        return c;


    }

    public Cursor salebyCustomer(String from,String to,String cus){
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor c = database.rawQuery("select sum(grant_total) as Total,max(date)as Last,min(date) as First,count(customer) as visit from " + DataContract.Invoice.TABLE_NAME
                + " where date BETWEEN '" + from + "' AND '" + to + "' AND customer = '" + cus + "'  order by invoice_id asc ", null);
        return c;


    }
    public Double returnbyCustomer(String from,String to,String cus){
        SQLiteDatabase database = this.getReadableDatabase();
        double refund=0;

        Cursor c = database.rawQuery("select sum(total) as Total from " + DataContract.Refund.TABLE_NAME
                + " where date BETWEEN '" + from + "' AND '" + to + "' AND customer = '" + cus + "' ", null);
        if(c.moveToFirst()){
            refund=c.getDouble(c.getColumnIndex("Total"));
        }
        return refund;


    }

    public Cursor getTopSaleItem(String from){
        SQLiteDatabase  database = this.getReadableDatabase();

        Cursor c = database.rawQuery("select item_name,sum(item_quantity) as sale from " + DataContract.InvoiceDetails.TABLE_NAME
                + " where date = '" + from + "' group by item_name order by sale desc limit 5 ", null);
        return c;


    }



    public int getTotalInvoice(String from){
        int count=0;
        SQLiteDatabase database=this.getWritableDatabase();

        Cursor c = database.rawQuery("select count(invoice_id) as total from " + DataContract.Invoice.TABLE_NAME
                + " where date = '" + from + "' ", null);
        if(c.moveToFirst()){
        count=c.getInt(c.getColumnIndex("total"));
        }
        c.close();
        database.close();
        return count;


    }


    public String getCustomerMobile(String name) {
        String mob="";
        SQLiteDatabase database=getWritableDatabase();
        String[] projections = {DataContract.Customer.COL_MOBILE};
        String selection=DataContract.Customer.COL_CUSTOMER_NAME + " LIKE ?";
        String[] selection_ars={name};

        Cursor cursor = database.query(DataContract.Customer.TABLE_NAME, projections, selection,selection_ars,null,null,null);
        if(cursor.moveToFirst()){
            mob=cursor.getString(cursor.getColumnIndex(DataContract.Customer.COL_MOBILE));

        }
        cursor.close();
        database.close();
        return mob;
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DataContract.RegisterShop.TABLE_NAME);
    }


    // sync


    public Cursor GetUnSyncInvoice(SQLiteDatabase database){

        String[] projections = {DataContract.Invoice.COL_INVOICE_NUMBER,DataContract.Invoice.COL_EMPLOYEE,DataContract.Invoice.COL_CUSTOMER
                ,DataContract.Invoice.COL_GRAND_TOTAL,DataContract.Invoice.COL_DISCOUNT,DataContract.Invoice.COL_TOTAL,DataContract.Invoice.COL_CASH,DataContract.Invoice.COL_INVOICE_DATE
                ,DataContract.Invoice.COL_IS_SYNC};

        Cursor cursor=database.query(DataContract.Invoice.TABLE_NAME,projections,DataContract.Invoice.COL_IS_SYNC+"=?",
                new String[]{String.valueOf(DataContract.SYNC_STATUS_FAILED)},null,null,null,null);
        return cursor;
    }
    public Cursor GetUnSyncInvoiceDetails(SQLiteDatabase database){
        Cursor cursor =  database.rawQuery("select * from " + DataContract.InvoiceDetails.TABLE_NAME + " where "
                + DataContract.InvoiceDetails.COL_IS_SYNC + " = "+DataContract.SYNC_STATUS_FAILED , null);
        return cursor;
    }


    public Cursor GetUnSyncRefund(SQLiteDatabase database){
        Cursor cursor =  database.rawQuery("select * from " + DataContract.Refund.TABLE_NAME + " where "
                + DataContract.InvoiceDetails.COL_IS_SYNC + " = "+DataContract.SYNC_STATUS_FAILED , null);
        return cursor;
    }

    public Cursor GetUnSyncRefundDetails(SQLiteDatabase database){
        Cursor cursor =  database.rawQuery("select * from " + DataContract.RefundDetails.TABLE_NAME + " where "
                + DataContract.RefundDetails.COL_IS_SYNC + " = "+DataContract.SYNC_STATUS_FAILED , null);
        return cursor;
    }

    public void updateInvoiceSync(int sync){
        try{
            SQLiteDatabase database = getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put(DataContract.Invoice.COL_IS_SYNC,sync);
            database.update(DataContract.Invoice.TABLE_NAME,values,DataContract.Invoice.COL_IS_SYNC+"=?",new String[]{String.valueOf(DataContract.SYNC_STATUS_FAILED)});

            Log.v(TAG, "invoice sync updated");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void updateInvoiceDetailsSync(int sync){
        try{
            SQLiteDatabase database = getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put(DataContract.InvoiceDetails.COL_IS_SYNC,sync);
            database.update(DataContract.InvoiceDetails.TABLE_NAME,values,DataContract.InvoiceDetails.COL_IS_SYNC+"=?",new String[]{String.valueOf(DataContract.SYNC_STATUS_FAILED)});

            Log.v(TAG, "invoice details sync updated");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void updateRefundSync(int sync){
        try{
            SQLiteDatabase database = getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put(DataContract.Refund.COL_IS_SYNC,sync);
            database.update(DataContract.Refund.TABLE_NAME,values,DataContract.Refund.COL_IS_SYNC+"=?",new String[]{String.valueOf(DataContract.SYNC_STATUS_FAILED)});

            Log.v(TAG, "refund sync updated");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void updateRefundDetailsSync(int sync){
        try{
            SQLiteDatabase database = getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put(DataContract.RefundDetails.COL_IS_SYNC,sync);
            database.update(DataContract.RefundDetails.TABLE_NAME,values,DataContract.RefundDetails.COL_IS_SYNC+"=?",new String[]{String.valueOf(DataContract.SYNC_STATUS_FAILED)});

            Log.v(TAG, "refund details sync updated");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }



}


























