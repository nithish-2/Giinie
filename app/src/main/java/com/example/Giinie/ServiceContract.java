package com.example.Giinie;

import android.provider.BaseColumns;

public class ServiceContract {

    private ServiceContract() {
    }

    public static class ServiceEntry implements BaseColumns {
        public static final String TABLE_NAME = "services";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE_BASIC = "price_basic";
        public static final String COLUMN_PRICE_STANDARD = "price_standard";
        public static final String COLUMN_PRICE_PREMIUM = "price_premium";
    }

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME = "user_name";
        public static final String COLUMN_USER_ID = "_id";
        public static final String COLUMN_EMAIL = "user_email";
    }

    public static class CartItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "cart";
        public static final String COLUMN_USER_ID_FK = "user_id";
        public static final String COLUMN_SERVICE_NAME = "service_name";
        public static final String COLUMN_PLAN_NAME = "plan_name";
        public static final String COLUMN_SELECTED_DATE = "date_time";
    }

    public static final class OrderEntry implements BaseColumns {
        public static final String TABLE_NAME = "orders";
        public static final String COLUMN_ORDER_ID = "_id";
        public static final String COLUMN_ORDER_USER_ID = "user_id";
        public static final String COLUMN_ORDER_SERVICE_NAME = "service_name";
        public static final String COLUMN_ORDER_PLAN_NAME = "plan_name";
        public static final String COLUMN_ORDER_DATE = "date_time";
    }

}
