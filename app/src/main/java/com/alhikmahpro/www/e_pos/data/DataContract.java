package com.alhikmahpro.www.e_pos.data;

public class  DataContract {

    private DataContract(){}

    public static final int SYNC_STATUS_OK=1;
    public static final int SYNC_STATUS_FAILED=0;
    public static final String SERVER_URL="http://epos.alhikmahpro.com/DataUpdater";
    public static final String UI_UPDATE_BROADCAST="com.alhikmahpro.com.e_pos.updatebroadcast";


    public static class Category{

        public static final String TABLE_NAME="category";
        public static final String COL_CATEGORY_ID="category_id";
        public static final String COL_CATEGORY_NAME="category_name";
        public static final String COL_IS_SYNC="is_sync";

    }


    public static class Items{

        public static final String TABLE_NAME="item_details";
        public static final String COL_ITEM_ID="item_id";
        public static final String COL_ITEM_CODE="item_code";
        public static final String COL_ITEM_NAME="item_name";
        public static final String COL_ITEM_BARCODE="item_barcode";
        public static final String COL_ITEM_PRICE="item_price";
        public static final String COL_ITEM_IMAGE="item_image";
        public static final String COL_CATEGORY_ID="category_id";
        public static final String COL_POSITION="position";
        public static final String COL_IS_SYNC="is_sync";

    }
    public static class InvoiceDetails{

        public static final String TABLE_NAME="invoice_details";
        public static final String COL_INVOICE_DETAILS_ID="invoice_details_id";
        public static final String COL_INVOICE_NUMBER="invoice_number";
        public static final String COL_INVOICE_DATE="date";
        public static final String COL_ITEM_CODE="item_code";
        public static final String COL_ITEM_NAME="item_name";
        public static final String COL_ITEM_QUANTITY="item_quantity";
        public static final String COL_ITEM_PRICE="item_price";
        public static final String COL_TOTAL="total_amount";
        public static final String COL_REFUND_COUNT="refund_count";
        public static final String COL_INVOICE_STATUS="invoice_status";
        public static final String COL_IS_SYNC="is_sync";

    }

    public static class Invoice{

        public static final String TABLE_NAME="invoice";
        public static final String COL_INVOICE_ID="invoice_id";
        public static final String COL_INVOICE_NUMBER="invoice_number";
        public static final String COL_TOTAL="total";
        public static final String COL_GRAND_TOTAL="grant_total";
        public static final String COL_DISCOUNT="discount";
        public static final String COL_CASH="cash";
        public static final String COL_CUSTOMER="customer";
        public static final String COL_EMPLOYEE="employee";
        public static final String COL_INVOICE_DATE="date";
        public static final String COL_TYPE="type";
        public static final String COL_IS_SYNC="is_sync";

    }

    public static class Refund{

        public static final String TABLE_NAME="refund";
        public static final String COL_REFUND_ID="refund_id";
        public static final String COL_REFUND_NUMBER="refund_number";
        public static final String COL_INVOICE_NUMBER="invoice_number";
        public static final String COL_TOTAL="total";
        public static final String COL_CUSTOMER="customer";
        public static final String COL_EMPLOYEE="employee";
        public static final String COL_REFUND_DATE="date";
        public static final String COL_IS_SYNC="is_sync";

    }

    public static class RefundDetails{

        public static final String TABLE_NAME="refund_details";
        public static final String COL_REFUND_DETAILS_ID="refund_details_id";
        public static final String COL_REFUND_NUMBER="refund_number";
        public static final String COL_INVOICE_NUMBER="invoice_number";
        public static final String COL_ITEM_CODE="item_code";
        public static final String COL_ITEM_NAME="item_name";
        public static final String COL_ITEM_QUANTITY="item_quantity";
        public static final String COL_ITEM_PRICE="item_price";
        public static final String COL_IS_SYNC="is_sync";

    }



    public static class BillTitles{

        public static final String TABLE_NAME="bill_titles";
        public static final String COL_TITLE_ID="title_id";
        public static final String COL_SHOP_NAME="shop_name";
        public static final String COL_SHOP_ADDRESS1="address1";
        public static final String COL_SHOP_ADDRESS2="address2";
        public static final String COL_MOBILE="mobile";
        public static final String COL_TEL="tel";
        public static final String COL_FOOTER="footer";
        public static final String COL_IS_SYNC="is_sync";

    }

    public static class BusinessDetails{

        public static final String TABLE_NAME="business_details";
        public static final String COL_BUSINESS_ID="business_id";
        public static final String COL_BUSINESS_NAME="business_name";
        public static final String COL_LOGO="business_logo";
    }


    public static class LoginCredential{

        public static final String TABLE_NAME="login_details";
        public static final String COL_ID="_id";
        public static final String COL_PIN="pin";
        public static final String COL_POWER="power";
        public static final String COL_IS_SYNC="is_sync";
    }


    public static class Employee{

        public static final String TABLE_NAME="employee_details";
        public static final String COL_EMPLOYEE_ID="employee_id";
        public static final String COL_EMPLOYEE_CODE="employee_code";
        public static final String COL_EMPLOYEE_NAME="employee_name";
        public static final String COL_MOBILE="mobile";
        public static final String COL_IS_SYNC="is_sync";

    }

    public static class Customer{

        public static final String TABLE_NAME="customer_details";
        public static final String COL_CUSTOMER_ID="customer_id";
        public static final String COL_CUSTOMER_NAME="customer_name";
        public static final String COL_CUSTOMER_ADDRESS="customer_address";
        public static final String COL_MOBILE="mobile";
        public static final String COL_POINT="customer_point";
        public static final String COL_SPEND_MONEY="customer_money";
        public static final String COL_IS_SYNC="is_sync";


    }



}
