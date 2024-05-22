package com.example.Giinie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Giinie.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String TABLE_CART = "cart";
    private static final String TABLE_SERVICES = "services";
    private static final String TABLE_ORDERS = "orders";
    private static final String TABLE_REVIEWS = "reviews";

    private static final String COLUMN_SERVICE_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE_BASIC = "price_basic";
    private static final String COLUMN_PRICE_STANDARD = "price_standard";
    private static final String COLUMN_PRICE_PREMIUM = "price_premium";

    private static final String COLUMN_USER_ID = "_id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_EMAIL = "user_email";

    private static final String COLUMN_CART_ID = "_id";
    private static final String COLUMN_SERVICE_NAME = "service_name";
    private static final String COLUMN_PLAN_NAME = "plan_name";
    private static final String COLUMN_DATE_TIME = "date_time";
    private static final String COLUMN_USER_ID_FK = "user_id";

    private static final String COLUMN_ORDER_ID = "_id";
    private static final String COLUMN_ORDER_USER_ID = "user_id";
    private static final String COLUMN_ORDER_SERVICE_NAME = "service_name";
    private static final String COLUMN_ORDER_PLAN_NAME = "plan_name";
    private static final String COLUMN_ORDER_DATE = "date_time";

    private static final String COLUMN_PHONE = "phone"; // Add this line
    private static final String COLUMN_ADDRESS = "address";

    private static final String COLUMN_REVIEW_ID = "_id";
    private static final String COLUMN_REVIEW_USER_ID = "user_id";
    private static final String COLUMN_REVIEW_SERVICE_PLAN_NAME = "service_plan_name";
    private static final String COLUMN_REVIEW_TEXT = "review_text";
    private static final String COLUMN_REVIEW_RATING = "rating";

    private static final String SQL_CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
            COLUMN_USER_ID + " INTEGER PRIMARY KEY," +
            COLUMN_USER_NAME + " TEXT," +
            COLUMN_EMAIL + " TEXT)";

    private static final String SQL_CREATE_CART_TABLE = "CREATE TABLE " + TABLE_CART + " (" +
            COLUMN_CART_ID + " INTEGER PRIMARY KEY," +
            COLUMN_SERVICE_NAME + " TEXT," +
            COLUMN_PLAN_NAME + " TEXT," +
            COLUMN_DATE_TIME + " INTEGER," +
            COLUMN_USER_ID_FK + " INTEGER," +
            "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";

    private static final String SQL_CREATE_SERVICES_TABLE = "CREATE TABLE " + TABLE_SERVICES + " (" +
            COLUMN_SERVICE_ID + " INTEGER PRIMARY KEY," +
            COLUMN_NAME + " TEXT," +
            COLUMN_PRICE_BASIC + " REAL," +
            COLUMN_PRICE_STANDARD + " REAL," +
            COLUMN_PRICE_PREMIUM + " REAL)";

    private static final String SQL_CREATE_ORDERS_TABLE = "CREATE TABLE " + TABLE_ORDERS + " (" +
            COLUMN_ORDER_ID + " INTEGER PRIMARY KEY," +
            COLUMN_ORDER_USER_ID + " INTEGER," +
            COLUMN_ORDER_SERVICE_NAME + " TEXT," +
            COLUMN_ORDER_PLAN_NAME + " TEXT," +
            COLUMN_ORDER_DATE + " INTEGER," +
            COLUMN_USER_NAME + " TEXT," +
            COLUMN_PHONE + " TEXT," +      // Add the address column
            COLUMN_ADDRESS + " TEXT," +    // Add the address column
            "FOREIGN KEY(" + COLUMN_ORDER_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";



    private static final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + TABLE_REVIEWS + " (" +
            COLUMN_REVIEW_ID + " INTEGER PRIMARY KEY," +
            COLUMN_REVIEW_USER_ID + " INTEGER," +
            COLUMN_REVIEW_SERVICE_PLAN_NAME + " TEXT," +
            COLUMN_REVIEW_TEXT + " TEXT," +
            COLUMN_REVIEW_RATING + " REAL," +
            "FOREIGN KEY(" + COLUMN_REVIEW_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USERS_TABLE);
        db.execSQL(SQL_CREATE_CART_TABLE);
        db.execSQL(SQL_CREATE_SERVICES_TABLE);
        db.execSQL(SQL_CREATE_ORDERS_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);

        // Insert initial user data
        insertInitialUsers(db);

        // Insert initial service data here
        insertInitialServices(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Here, you can handle database upgrades if needed.
        // Drop the old "cart" table if it exists and recreate it
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }

    private void insertInitialServices(SQLiteDatabase db) {
        // Insert service data into the table
        String sqlInsert = "INSERT INTO " + TABLE_SERVICES +
                " (" +
                COLUMN_NAME + ", " +
                COLUMN_PRICE_BASIC + ", " +
                COLUMN_PRICE_STANDARD + ", " +
                COLUMN_PRICE_PREMIUM +
                ") VALUES (?, ?, ?, ?)";

        // Replace the placeholders with actual service data
        db.execSQL(sqlInsert, new String[]{"Plumbing", "29.99", "49.99", "69.99"});
        db.execSQL(sqlInsert, new String[]{"Cleaning", "24.99", "34.99", "44.99"});
        db.execSQL(sqlInsert, new String[]{"Repairs", "39.99", "49.99", "59.99"});
        db.execSQL(sqlInsert, new String[]{"Gardening", "69.99", "89.99", "99.99"});
        db.execSQL(sqlInsert, new String[]{"Home Spa", "19.99", "29.99", "49.99"});
        db.execSQL(sqlInsert, new String[]{"Electrical", "34.99", "54.99", "64.99"});
    }

    private void insertInitialUsers(SQLiteDatabase db) {
        String sqlInsert = "INSERT INTO " + TABLE_USERS +
                " (" +
                COLUMN_USER_NAME + ", " +
                COLUMN_EMAIL +
                ") VALUES (?, ?)";

        // Replace the placeholders with actual user data
        db.execSQL(sqlInsert, new String[]{"Praveen Kumar", "praveen@mail.com"});
        db.execSQL(sqlInsert, new String[]{"Nithish Jagadeesan", "nithish@mail.com"});
        // Add more users as needed
    }

// ... (previous code)

    public long insertCartItem(CartItem cartItem, long userId) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_FK, userId);
        values.put(COLUMN_SERVICE_NAME, cartItem.getServiceName());
        values.put(COLUMN_PLAN_NAME, cartItem.getServicePlan());
        values.put(COLUMN_DATE_TIME, cartItem.getDate().getTime());

        return db.insert(TABLE_CART, null, values);
    }

    public List<CartItem> getCartItems(long userId) {
        List<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                COLUMN_SERVICE_NAME,
                COLUMN_PLAN_NAME,
                COLUMN_DATE_TIME
        };

        String selection = COLUMN_USER_ID_FK + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.query(
                TABLE_CART,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndexServiceName = cursor.getColumnIndex(COLUMN_SERVICE_NAME);
            int columnIndexPlanName = cursor.getColumnIndex(COLUMN_PLAN_NAME);
            int columnIndexDate = cursor.getColumnIndex(COLUMN_DATE_TIME);

            do {
                String serviceName = cursor.getString(columnIndexServiceName);
                String planName = cursor.getString(columnIndexPlanName);
                long dateMillis = cursor.getLong(columnIndexDate);

                Date date = new Date(dateMillis);
                CartItem cartItem = new CartItem(serviceName, planName, date);
                cartItems.add(cartItem);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return cartItems;
    }

    public List<Service> getAllServices() {
        List<Service> serviceList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_SERVICES, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndexId = cursor.getColumnIndex(COLUMN_SERVICE_ID);
            int columnIndexName = cursor.getColumnIndex(COLUMN_NAME);
            int columnIndexBasicPrice = cursor.getColumnIndex(COLUMN_PRICE_BASIC);
            int columnIndexStandardPrice = cursor.getColumnIndex(COLUMN_PRICE_STANDARD);
            int columnIndexPremiumPrice = cursor.getColumnIndex(COLUMN_PRICE_PREMIUM);

            do {
                long serviceId = cursor.getLong(columnIndexId);
                String serviceName = cursor.getString(columnIndexName);
                double basicPrice = cursor.getDouble(columnIndexBasicPrice);
                double standardPrice = cursor.getDouble(columnIndexStandardPrice);
                double premiumPrice = cursor.getDouble(columnIndexPremiumPrice);

                Service service = new Service(serviceId, serviceName, basicPrice, standardPrice, premiumPrice);
                serviceList.add(service);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return serviceList;
    }

    public List<ServicePlan> getServicePlansByService(String serviceName) {
        List<ServicePlan> servicePlans = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                COLUMN_SERVICE_ID,
                COLUMN_NAME,
                COLUMN_PRICE_BASIC,
                COLUMN_PRICE_STANDARD,
                COLUMN_PRICE_PREMIUM
        };

        String selection = COLUMN_NAME + " = ?";
        String[] selectionArgs = {serviceName};

        Cursor cursor = db.query(
                TABLE_SERVICES,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndexId = cursor.getColumnIndex(COLUMN_SERVICE_ID);
            int columnIndexName = cursor.getColumnIndex(COLUMN_NAME);
            int columnIndexBasicPrice = cursor.getColumnIndex(COLUMN_PRICE_BASIC);
            int columnIndexStandardPrice = cursor.getColumnIndex(COLUMN_PRICE_STANDARD);
            int columnIndexPremiumPrice = cursor.getColumnIndex(COLUMN_PRICE_PREMIUM);

            do {
                long planId = cursor.getLong(columnIndexId);
                String planName = cursor.getString(columnIndexName);
                double basicPrice = cursor.getDouble(columnIndexBasicPrice);
                double standardPrice = cursor.getDouble(columnIndexStandardPrice);
                double premiumPrice = cursor.getDouble(columnIndexPremiumPrice);

                // Assuming you want to add plans for basic, standard, and premium prices
                ServicePlan basicPlan = new ServicePlan(planName + " Basic", basicPrice);
                ServicePlan standardPlan = new ServicePlan(planName + " Standard", standardPrice);
                ServicePlan premiumPlan = new ServicePlan(planName + " Premium", premiumPrice);

                servicePlans.add(basicPlan);
                servicePlans.add(standardPlan);
                servicePlans.add(premiumPlan);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return servicePlans;
    }

    public String getUserPhoneByUserId(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { COLUMN_PHONE };
        String selection = COLUMN_USER_ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        Cursor cursor = db.query(
                TABLE_USERS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        String userPhone = "";
        if (cursor != null && cursor.moveToFirst()) {
            userPhone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
            cursor.close();
        }

        return userPhone;
    }

    public String getUserAddressByUserId(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { COLUMN_ADDRESS };
        String selection = COLUMN_USER_ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        Cursor cursor = db.query(
                TABLE_USERS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        String userAddress = "";
        if (cursor != null && cursor.moveToFirst()) {
            userAddress = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
            cursor.close();
        }

        return userAddress;
    }


    public long getUserIdByEmail(String email) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { COLUMN_USER_ID };
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = { email };

        Cursor cursor = db.query(
                TABLE_USERS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        long userId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
            cursor.close();
        }

        return userId;
    }

    public long insertOrder(long userId, String serviceName, String planName, Date orderDate, String userName, String userPhone, String userAddress) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_USER_ID, userId);
        values.put(COLUMN_ORDER_SERVICE_NAME, serviceName);
        values.put(COLUMN_ORDER_PLAN_NAME, planName);
        values.put(COLUMN_ORDER_DATE, orderDate.getTime());
        values.put(COLUMN_USER_NAME, userName); // This line is correct

        // Store phone and address directly in the orders table
        values.put(COLUMN_PHONE, userPhone); // This line might be problematic
        values.put(COLUMN_ADDRESS, userAddress); // This line might be problematic

        return db.insert(TABLE_ORDERS, null, values);
    }




    public List<CartItem> getCartItemsForUser(long userId) {
        List<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                COLUMN_SERVICE_NAME,
                COLUMN_PLAN_NAME,
                COLUMN_DATE_TIME
        };

        String selection = COLUMN_USER_ID_FK + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        Cursor cursor = db.query(
                TABLE_CART,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndexServiceName = cursor.getColumnIndex(COLUMN_SERVICE_NAME);
            int columnIndexPlanName = cursor.getColumnIndex(COLUMN_PLAN_NAME);
            int columnIndexDate = cursor.getColumnIndex(COLUMN_DATE_TIME);

            do {
                String serviceName = cursor.getString(columnIndexServiceName);
                String planName = cursor.getString(columnIndexPlanName);
                long dateMillis = cursor.getLong(columnIndexDate);

                Date date = new Date(dateMillis);
                CartItem cartItem = new CartItem(serviceName, planName, date);
                cartItems.add(cartItem);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return cartItems;
    }

    public List<Order> getOrdersForUser(long userId) {
        List<Order> orderList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                COLUMN_ORDER_ID,
                COLUMN_ORDER_SERVICE_NAME,
                COLUMN_ORDER_PLAN_NAME,
                COLUMN_ORDER_DATE,
                COLUMN_PHONE,     // Add this line
                COLUMN_ADDRESS    // Add this line
        };

        String selection = COLUMN_ORDER_USER_ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        Cursor cursor = db.query(
                TABLE_ORDERS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndexOrderId = cursor.getColumnIndex(COLUMN_ORDER_ID);
            int columnIndexServiceName = cursor.getColumnIndex(COLUMN_ORDER_SERVICE_NAME);
            int columnIndexPlanName = cursor.getColumnIndex(COLUMN_ORDER_PLAN_NAME);
            int columnIndexDate = cursor.getColumnIndex(COLUMN_ORDER_DATE);
            int columnIndexPhone = cursor.getColumnIndex(COLUMN_PHONE);       // Add this line
            int columnIndexAddress = cursor.getColumnIndex(COLUMN_ADDRESS);   // Add this line

            do {
                long orderId = cursor.getLong(columnIndexOrderId);
                String serviceName = cursor.getString(columnIndexServiceName);
                String planName = cursor.getString(columnIndexPlanName);
                long dateMillis = cursor.getLong(columnIndexDate);
                String phone = cursor.getString(columnIndexPhone);           // Add this line
                String address = cursor.getString(columnIndexAddress);       // Add this line

                Date date = new Date(dateMillis);
                String userName = getUserNameByUserId(userId);
                Order order = new Order(orderId, userId, serviceName, planName, date, userName, phone, address);
                orderList.add(order);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return orderList;
    }


    public String getUserNameByUserId(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { COLUMN_USER_NAME };
        String selection = COLUMN_USER_ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        Cursor cursor = db.query(
                TABLE_USERS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        String userName = "";
        if (cursor != null && cursor.moveToFirst()) {
            userName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME));
            cursor.close();
        }

        return userName;
    }



    public void clearCartForUser(long userId) {
        SQLiteDatabase db = getWritableDatabase();

        String selection = COLUMN_USER_ID_FK + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        db.delete(TABLE_CART, selection, selectionArgs);
        db.close();
    }

    public long insertReview(long userId, String servicePlanName, String reviewText, float rating) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REVIEW_USER_ID, userId);
        values.put(COLUMN_REVIEW_SERVICE_PLAN_NAME, servicePlanName);
        values.put(COLUMN_REVIEW_TEXT, reviewText);
        values.put(COLUMN_REVIEW_RATING, rating);

        return db.insert(TABLE_REVIEWS, null, values);
    }

    public List<Review> getReviewsForOrder(long orderId) {
        List<Review> reviews = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                COLUMN_REVIEW_ID,
                COLUMN_REVIEW_USER_ID,
                COLUMN_REVIEW_SERVICE_PLAN_NAME,
                COLUMN_REVIEW_TEXT,
                COLUMN_REVIEW_RATING
        };

        String selection = COLUMN_REVIEW_ID + " = ?";
        String[] selectionArgs = { String.valueOf(orderId) };

        Cursor cursor = db.query(
                TABLE_REVIEWS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndexReviewId = cursor.getColumnIndex(COLUMN_REVIEW_ID);
            int columnIndexUserId = cursor.getColumnIndex(COLUMN_REVIEW_USER_ID);
            int columnIndexServicePlanName = cursor.getColumnIndex(COLUMN_REVIEW_SERVICE_PLAN_NAME);
            int columnIndexReviewText = cursor.getColumnIndex(COLUMN_REVIEW_TEXT);
            int columnIndexRating = cursor.getColumnIndex(COLUMN_REVIEW_RATING);

            do {
                long reviewId = cursor.getLong(columnIndexReviewId);
                long retrievedUserId = cursor.getLong(columnIndexUserId);
                String retrievedServicePlanName = cursor.getString(columnIndexServicePlanName);
                String reviewText = cursor.getString(columnIndexReviewText);
                float rating = cursor.getFloat(columnIndexRating);

                Review review = new Review(reviewId, retrievedUserId, retrievedServicePlanName, reviewText, rating, "");
                reviews.add(review);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return reviews;
    }
    public long getServiceIdByName(String serviceName) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { COLUMN_SERVICE_ID };
        String selection = COLUMN_NAME + " = ?";
        String[] selectionArgs = { serviceName };

        Cursor cursor = db.query(
                TABLE_SERVICES,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        long serviceId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            serviceId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_ID));
            cursor.close();
        }

        return serviceId;
    }

    public double getItemPriceFromDatabase(long serviceId) {
        SQLiteDatabase db = this.getReadableDatabase();
        double itemPrice = -1; // Default value if not found

        String[] projection = { COLUMN_PRICE_BASIC, COLUMN_PRICE_STANDARD, COLUMN_PRICE_PREMIUM };
        String selection = COLUMN_SERVICE_ID + " = ?";
        String[] selectionArgs = { String.valueOf(serviceId) };

        Cursor cursor = db.query(
                TABLE_SERVICES,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndexBasicPrice = cursor.getColumnIndex(COLUMN_PRICE_BASIC);
            int columnIndexStandardPrice = cursor.getColumnIndex(COLUMN_PRICE_STANDARD);
            int columnIndexPremiumPrice = cursor.getColumnIndex(COLUMN_PRICE_PREMIUM);

            double basicPrice = cursor.getDouble(columnIndexBasicPrice);
            double standardPrice = cursor.getDouble(columnIndexStandardPrice);
            double premiumPrice = cursor.getDouble(columnIndexPremiumPrice);

            // You can choose the appropriate price based on some criteria here
            // For example, choose basic, standard, or premium based on user selection

            // For this example, let's assume you're choosing basic price
            itemPrice = basicPrice;

            cursor.close();
        }

        return itemPrice;
    }

    // Method to get user name by email
    public String getUserNameByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String userName = "";

        String[] projection = {COLUMN_USER_NAME};
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(
                TABLE_USERS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int userNameIndex = cursor.getColumnIndex(COLUMN_USER_NAME);
            if (userNameIndex >= 0) {
                userName = cursor.getString(userNameIndex);
            }
            cursor.close();
        }

        db.close();

        return userName;
    }

    public long insertUser(String userName, String email) {
        SQLiteDatabase db = getWritableDatabase();

        // First, check if the user with the same email already exists
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID}, COLUMN_EMAIL + " = ?", new String[]{email}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            // User already exists, return the existing user's ID
            int columnIndex = cursor.getColumnIndex(COLUMN_USER_ID);
            if (columnIndex != -1) {
                long userId = cursor.getLong(columnIndex);
                cursor.close();
                return userId;
            }
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, userName);
        values.put(COLUMN_EMAIL, email);

        return db.insert(TABLE_USERS, null, values);
    }






}

