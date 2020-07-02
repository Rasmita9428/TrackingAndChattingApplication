package com.rasmitap.tailwebs_assigment2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rasmitap.tailwebs_assigment2.model.Datamodel;
import com.rasmitap.tailwebs_assigment2.model.LoginData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lalit on 9/12/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Tailwebs.db";

    // User Table Columns names
    public static String COLUMN_USER_ID = "user_id";
    public static String COLUMN_USER_NAME = "user_name";
    public static String COLUMN_USER_PASSWORD = "user_password";
    public static String COLUMN_USER_NUMBER = "user_phone";

    // data Table Columns names
    public static String COLUMN_STUDENT_NAME = "student_name";
    public static String COLUMN_SUBJECT = "subject";
    public static String COLUMN_MARKS = "marks";

    // User table name
    public static final String TABLE_USER = "login";
    public static final String TABLE_DATA = "data";
    public static final String TAG = DatabaseHelper.class.getSimpleName();


    // create table sql query
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT," + COLUMN_USER_PASSWORD + " TEXT," + COLUMN_USER_NUMBER + " TEXT"
           + ")";

    private String CREATE_TABLE_DATA = "CREATE TABLE " + TABLE_DATA + "("
            + COLUMN_STUDENT_NAME + " TEXT," + COLUMN_SUBJECT + " TEXT," + COLUMN_MARKS + " TEXT,"
            + COLUMN_USER_NAME + " TEXT"+ ")";



    // drop table sql query
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;
    private String DROP_DATA_TABLE = "DROP TABLE IF EXISTS " + TABLE_DATA;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null)
            instance = new DatabaseHelper(context);
        return instance;
    }
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(TAG, "onCreate: " + CREATE_USER_TABLE);
          db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_TABLE_DATA);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_DATA_TABLE);

        // Create tables again
        onCreate(db);
    }
    public void addUser(LoginData data) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, data.getUserid());
        values.put(COLUMN_USER_NAME, data.getUsername());
        values.put(COLUMN_USER_PASSWORD, data.getPassword());
        values.put(COLUMN_USER_NUMBER, data.getPhone());

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        Log.e(TAG, "addUser: Insert user");
        db.close();

    }
    public void adddata(Datamodel data) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_NAME, data.getStudentName());
        values.put(COLUMN_SUBJECT, data.getSubject());
        values.put(COLUMN_MARKS, data.getMarks());
        values.put(COLUMN_USER_NAME, data.getUserName());

        // Inserting Row
        db.insert(TABLE_DATA, null, values);
        Log.e(TAG, "adddatatable: Insert user");
        db.close();

    }

    public List<LoginData> getAllUser() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_NAME,
                COLUMN_USER_PASSWORD,
                COLUMN_USER_NUMBER,
        };
        // sorting orders
        String sortOrder = COLUMN_USER_NAME + " ASC";
        List<LoginData> userList = new ArrayList<LoginData>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                LoginData user = new LoginData();
                user.setUserid(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)));
                user.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NUMBER)));
                // Adding user record to list
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return userList;
    }
    public List<Datamodel> getAllData(String Username) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_STUDENT_NAME,
                COLUMN_SUBJECT,
                COLUMN_MARKS,
                COLUMN_USER_NAME
        };
        // sorting orders
        String sortOrder = COLUMN_STUDENT_NAME + " ASC";
        List<Datamodel> userList = new ArrayList<Datamodel>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        String selection = COLUMN_USER_NAME + " = ?" ;

        String[] selectionArgs = {Username};

        Cursor cursor = db.query(TABLE_DATA, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                ""); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Datamodel user = new Datamodel();
                user.setStudentName(cursor.getString(cursor.getColumnIndex(COLUMN_STUDENT_NAME)));
                user.setSubject(cursor.getString(cursor.getColumnIndex(COLUMN_SUBJECT)));
                user.setMarks(cursor.getString(cursor.getColumnIndex(COLUMN_MARKS)));
                user.setUserName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                // Adding user record to list
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return userList;
    }
    public void updateDAta(Datamodel user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_NAME, user.getStudentName());
        values.put(COLUMN_SUBJECT, user.getSubject());
        values.put(COLUMN_MARKS, user.getMarks());
        values.put(COLUMN_USER_NAME, user.getUserName());

        // updating row
        db.update(TABLE_DATA, values, COLUMN_STUDENT_NAME + " = ? AND " + COLUMN_SUBJECT + " =? ", new String[]{String.valueOf(user.getStudentName()),user.getSubject()});
        db.close();
    }

    public boolean checkUser(String username, String password) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_NAME,
                COLUMN_USER_PASSWORD
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_NAME + " LIKE ?" + " AND " + COLUMN_USER_PASSWORD + " = ? ";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }

        return false;
    }
    public boolean checkUserName(String username)
    {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_NAME,
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_NAME  + " LIKE ? ";
        String[] selectionArgs = {username };
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        Log.e("Register user quary", String.valueOf(cursor));

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

}
