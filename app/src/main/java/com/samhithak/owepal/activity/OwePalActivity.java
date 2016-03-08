package com.samhithak.owepal.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.skamaraju.owepalapplication.R;
import com.samhithak.owepal.fragment.AccountDetailsFragment;
import com.samhithak.owepal.fragment.AccountFragment;
import com.samhithak.owepal.fragment.CreateAccountFragment;
import com.samhithak.owepal.fragment.HistoryFragment;
import com.samhithak.owepal.fragment.NavigationDrawerFragment;
import com.samhithak.owepal.fragment.SettingsFragment;
import com.samhithak.owepal.model.OwePalAccount;
import com.samhithak.owepal.provider.AccountProvider;

public class OwePalActivity extends ActionBarActivity
        implements AccountFragment.OnAccountFragmentListener,
                   SettingsFragment.OnFragmentInteractionListener,
                   AccountDetailsFragment.OnAccountDetailsFragmentListener,
                   CreateAccountFragment.OnCreateAccountFragmentListener,
        NavigationDrawerFragment.NavigationDrawerCallbacks {

    private final static int ACCOUNT_ITEM = 0;
    private final static int HISTORY_ITEM = 1;
    private final static int SETTINGS_ITEM = 2;

    private final static int ACCOUNT_YOU_OWE = 0;
    private final static int ACCOUNT_OWED = 1;
    private final static int ACCOUNT_TOTAL = 2;

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;

    private CharSequence mTitle;

    private com.samhithak.owepal.model.OwePalAccount mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owe_pal);

        mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, mDrawerLayout);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Log.d("onNavDrawerItemSelected", String.valueOf(position));
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case ACCOUNT_ITEM:
                fragmentManager.beginTransaction().replace(R.id.container,(new AccountFragment())).commit();
                break;
            case HISTORY_ITEM:
                fragmentManager.beginTransaction().replace(R.id.container,(new HistoryFragment())).commit();
                break;
            case SETTINGS_ITEM:
                fragmentManager.beginTransaction().replace(R.id.container,(new SettingsFragment())).commit();
                break;
        }
    }

    public void createAccount(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container,((Fragment) new CreateAccountFragment())).addToBackStack(null).commit();
    }

    @Override
    public void createAccount(String name, int amount, int isOwed) {
        ContentValues values = new ContentValues();
        values.put(AccountProvider._NAME, name);
        values.put(AccountProvider._AMOUNT, amount);
        values.put(AccountProvider._IS_OWED, (isOwed));
        values.put(AccountProvider._TIMESTAMP, System.currentTimeMillis());

        //Insert the transaction in transaction table
        Uri uri2 = getContentResolver().insert(AccountProvider.CONTENT_URI_TRANSACTIONS, values);
        if (uri2 != null) {
            Toast.makeText(getBaseContext(), "Transaction successfully inserted.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "An error occured while inserting the transaction", Toast.LENGTH_SHORT).show();
        }

        //If account exists, update the account else create a new one
        Cursor c = getContentResolver().query(AccountProvider.CONTENT_URI_ACCOUNTS, null, AccountProvider._NAME + "=" + DatabaseUtils.sqlEscapeString(name), null, null);
        if (c.moveToFirst()) {
            // found in database, update the account balance
            if (c.getInt(c.getColumnIndex(AccountProvider._IS_OWED)) != 0) {
                int prevBalance = c.getInt((c.getColumnIndex(AccountProvider._AMOUNT))) * c.getInt((c.getColumnIndex(AccountProvider._IS_OWED)));
                int newBalance = (amount * isOwed) + prevBalance;
                values.put(AccountProvider._AMOUNT, newBalance > 0 ? newBalance : newBalance * -1 );
                values.put(AccountProvider._IS_OWED, (newBalance > 0 ? 1 : -1));

                getContentResolver().update(AccountProvider.CONTENT_URI_ACCOUNTS, values, "name=?", new String[] { name });
            }
        } else {
                //no existing account found
                Uri uri = getContentResolver().insert(AccountProvider.CONTENT_URI_ACCOUNTS, values);
                if (uri != null) {
                    Toast.makeText(getBaseContext(), "Account successfully created.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "An error occured while creating account. Try again later.", Toast.LENGTH_SHORT).show();
                }
            }

            //Go back to account fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.container, (new AccountFragment())).addToBackStack(null).commit();
    }

    @Override
    public int[] retrieveAccountSummary() {
        int[] values = new int[3];
        String URL = "content://com.samhithak.owepal.model.Account/accounts";
        Uri accounts = Uri.parse(URL);
        Cursor c = getContentResolver().query(accounts, null, null, null, "name");
        if (c.moveToFirst()) {
            do {
                if(c.getString(c.getColumnIndex(AccountProvider._IS_OWED)) != null && c.getString(c.getColumnIndex(AccountProvider._IS_OWED)).equals("-1")) {
                    int i = Integer.parseInt(c.getString(c.getColumnIndex(AccountProvider._AMOUNT)));

                    values[ACCOUNT_YOU_OWE] = values[ACCOUNT_YOU_OWE] + i;
                }else if(c.getString(c.getColumnIndex(AccountProvider._IS_OWED)) != null && c.getString(c.getColumnIndex(AccountProvider._IS_OWED)).equals("1")){
                        values[ACCOUNT_OWED] = values[ACCOUNT_OWED] + Integer.parseInt(c.getString(c.getColumnIndex(AccountProvider._AMOUNT)));
                }
            } while (c.moveToNext());
        }
        values[ACCOUNT_TOTAL] = values[ACCOUNT_YOU_OWE]*(-1) + values[ACCOUNT_OWED];
        return values;
    }

    @Override
    public void settleUp(String name, Integer amount){
        ContentValues values = new ContentValues();
        values.put(AccountProvider._NAME, name);
        values.put(AccountProvider._AMOUNT, amount);
        values.put(AccountProvider._IS_OWED, 0);
        values.put(AccountProvider._TIMESTAMP, System.currentTimeMillis());

        //Insert the transaction in transaction table
        Uri uri2 = getContentResolver().insert(AccountProvider.CONTENT_URI_TRANSACTIONS, values);
        if (uri2 != null) {
            Toast.makeText(getBaseContext(), "Transaction successfully inserted.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "An error occured while inserting the transaction", Toast.LENGTH_SHORT).show();
        }

        //Update the balance in accounts table
        Cursor c = getContentResolver().query(AccountProvider.CONTENT_URI_ACCOUNTS, null, AccountProvider._NAME + "=" + DatabaseUtils.sqlEscapeString(name), null, null);
        if (c.moveToFirst()) {
            // found in database, update the account balance
            if (c.getInt(c.getColumnIndex(AccountProvider._IS_OWED)) != 0) {
                values.put(AccountProvider._AMOUNT, 0);
                getContentResolver().update(AccountProvider.CONTENT_URI_ACCOUNTS, values, "name=?", new String[] { name });
            }
        }

        //Go back to account fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.container, (new AccountFragment())).addToBackStack(null).commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_accounts);
                break;
            case 2:
                mTitle = getString(R.string.title_history);
                break;
            case 3:
                mTitle = getString(R.string.title_settings);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {

            getMenuInflater().inflate(R.menu.owe_pal, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onGetAccountDetails(OwePalAccount account){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container,((Fragment) new AccountDetailsFragment().newInstance(account))).addToBackStack(null).commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case  R.id.action_settings:
                break;
            case  R.id.action_create:
                createAccount();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
