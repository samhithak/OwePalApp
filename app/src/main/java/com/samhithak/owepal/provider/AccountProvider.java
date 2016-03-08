package com.samhithak.owepal.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * TODO: Write Javadoc for AccountProvider.
 *
 * @author skamaraju
 */
public class AccountProvider extends ContentProvider {

    // fields for my content provider
    public static final String PROVIDER_NAME = "com.samhithak.owepal.model.Account";
    public static final Uri CONTENT_URI_ACCOUNTS = Uri.parse("content://" + PROVIDER_NAME + "/accounts");
    public static final Uri CONTENT_URI_TRANSACTIONS = Uri.parse("content://" + PROVIDER_NAME + "/transactions");

    // fields for the database
    public static final String _ID = "_id";
    public static final String _NAME = "name";
    public static final String _AMOUNT = "amount";
    public static final String _IS_OWED = "isowed";
    public static final String _TIMESTAMP = "timestamp";

    // integer values used in content URI
    static final int ACCOUNTS = 1;
    static final int ACCOUNTS_ID = 2;
    static final int TRANSACTIONS = 3;
    static final int TRANSACTIONS_ID = 4;

    DBHelper dbHelper;

    // projection map for a query
    private static HashMap<String, String> AccountMap;
    private static HashMap<String, String> TransactionsMap;

    // maps content URI "patterns" to the integer values that were set above
    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "accounts", ACCOUNTS);
        uriMatcher.addURI(PROVIDER_NAME, "accounts/#", ACCOUNTS_ID);
        uriMatcher.addURI(PROVIDER_NAME, "transactions", TRANSACTIONS);
        uriMatcher.addURI(PROVIDER_NAME, "transactions/#", TRANSACTIONS_ID);
    }

    // database declarations
    private SQLiteDatabase database;
    static final String DATABASE_NAME = "OwePal";
    static final String TABLE_NAME_ACCOUNTS = "accountTable";
    static final String TABLE_NAME_TRANSACTIONS = "transactionTable";
    static final int DATABASE_VERSION = 1;

    static final String CREATE_TABLE_ACCOUNTS = " CREATE TABLE " + TABLE_NAME_ACCOUNTS +" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + " name TEXT NOT NULL, " + " amount TEXT NOT NULL, " + " isowed INTEGER NOT NULL, " + " timestamp INTEGER NOT NULL);";
    static final String CREATE_TABLE_TRANSACTION = " CREATE TABLE " + TABLE_NAME_TRANSACTIONS +" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + " name TEXT NOT NULL, " + " amount TEXT NOT NULL, " + " isowed INTEGER NOT NULL, " + " timestamp INTEGER NOT NULL);";
    // class that creates and manages the provider's database
    private static class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_ACCOUNTS);
            db.execSQL(CREATE_TABLE_TRANSACTION);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(DBHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ". Old data will be destroyed");
            db.execSQL("DROP TABLE IF EXISTS " +  TABLE_NAME_ACCOUNTS);
            db.execSQL("DROP TABLE IF EXISTS " +  TABLE_NAME_TRANSACTIONS);
            onCreate(db);
        }
    }
    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DBHelper(context);
        // permissions to be writable
        database = dbHelper.getWritableDatabase();
        if(database == null)
            return false;
        else
            return true;
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        // the TABLE_NAME to query on
        switch (uriMatcher.match(uri)) {
            // maps all database column names
            case ACCOUNTS:
                queryBuilder.setTables(TABLE_NAME_ACCOUNTS);
                queryBuilder.setProjectionMap(AccountMap);
                break;
            case ACCOUNTS_ID:
                queryBuilder.appendWhere( _ID + "=" + uri.getLastPathSegment());
                break;
            case TRANSACTIONS:
                queryBuilder.setTables(TABLE_NAME_TRANSACTIONS);
                queryBuilder.setProjectionMap(TransactionsMap);
                break;
            case TRANSACTIONS_ID:
                queryBuilder.appendWhere( _ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder == ""){
            // No sorting-> sort by timestamp
            sortOrder = _TIMESTAMP;
        }
        Cursor cursor = queryBuilder.query(database, projection, selection,
                selectionArgs, null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        try{
            switch (uriMatcher.match(uri)){
                case ACCOUNTS:
                    long row = database.insert(TABLE_NAME_ACCOUNTS, "", values);
                    // If record is added successfully
                    if(row > 0) {
                        Uri newUri = ContentUris.withAppendedId(CONTENT_URI_ACCOUNTS, row);
                        getContext().getContentResolver().notifyChange(newUri, null);
                        return newUri;
                    }
                    break;
                case TRANSACTIONS:
                    long transactionsRow = database.insert(TABLE_NAME_TRANSACTIONS, "", values);
                    // If record is added successfully
                    if(transactionsRow > 0) {
                        Uri newUri = ContentUris.withAppendedId(CONTENT_URI_TRANSACTIONS, transactionsRow);
                        getContext().getContentResolver().notifyChange(newUri, null);
                        return newUri;
                    }
                    break;
            }

            throw new SQLException("Fail to add a new record into " + uri);
        }
        catch (SQLException e) {
            android.widget.Toast toast = android.widget.Toast.makeText(getContext(), e.getMessage(), android.widget.Toast.LENGTH_SHORT);
            toast.show();
            return null;
        }
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case ACCOUNTS:
                count = database.update(TABLE_NAME_ACCOUNTS, values, selection, selectionArgs);
                break;
            case ACCOUNTS_ID:
                count = database.update(TABLE_NAME_ACCOUNTS, values, _ID +" = " + uri.getLastPathSegment() +(!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;
            case TRANSACTIONS:
                count = database.update(TABLE_NAME_TRANSACTIONS, values, selection, selectionArgs);
                break;
            case TRANSACTIONS_ID:
                count = database.update(TABLE_NAME_TRANSACTIONS, values, _ID +" = " + uri.getLastPathSegment() +(!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case ACCOUNTS:
                // delete all the records of the table
                count = database.delete(TABLE_NAME_ACCOUNTS, selection, selectionArgs);
                break;
            case ACCOUNTS_ID:
                String id = uri.getLastPathSegment(); //gets the id
                count = database.delete( TABLE_NAME_ACCOUNTS, _ID +  " = " + id +
                (!TextUtils.isEmpty(selection) ? " AND (" +
                selection + ')' : ""), selectionArgs);
                break;
            case TRANSACTIONS:
                // delete all the records of the table
                count = database.delete(TABLE_NAME_TRANSACTIONS, selection, selectionArgs);
                break;
            case TRANSACTIONS_ID:
                String id2 = uri.getLastPathSegment(); //gets the id
                count = database.delete( TABLE_NAME_TRANSACTIONS, _ID +  " = " + id2 +
                (!TextUtils.isEmpty(selection) ? " AND (" +
                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            // Get all friend-account records
            case ACCOUNTS:
                return "vnd.android.cursor.dir/vnd.example.accounts";
            // Get a particular friend
            case ACCOUNTS_ID:
                return "vnd.android.cursor.item/vnd.example.accounts";
            case TRANSACTIONS:
                return "vnd.android.cursor.dir/vnd.example.transactions";
            // Get a particular friend
            case TRANSACTIONS_ID:
                return "vnd.android.cursor.item/vnd.example.transactions";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}
