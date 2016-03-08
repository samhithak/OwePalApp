package com.samhithak.owepal.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.skamaraju.owepalapplication.R;
import com.samhithak.owepal.adapter.AccountsAdapter;
import com.samhithak.owepal.model.OwePalAccount;
import com.samhithak.owepal.provider.AccountProvider;
import com.samhithak.owepal.provider.AccountsQuery;


public class AccountFragment extends ListFragment implements
        AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    // Bundle key for saving previously selected item
    private static final String STATE_PREVIOUSLY_SELECTED_KEY = "com.samhithak.owepal.fragment.AccountFragment.SELECTED_ITEM";
    private static final int LOADER_ID = 1;

    private OnAccountFragmentListener mListener;
    private AccountsAdapter mAdapter;
    private TextView mYouOwe;
    private TextView mYouAreOwed;
    private TextView mBalance;
    private TextView mTotalBalanceTextView;

    private int mPreviouslySelectedItem = 0;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            // If we're restoring state after this fragment was recreated then
            // retrieve previously selected item.
            mPreviouslySelectedItem =
                    savedInstanceState.getInt(STATE_PREVIOUSLY_SELECTED_KEY, 0);
        }
        mAdapter = new AccountsAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View retVal = inflater.inflate(R.layout.fragment_account, container, false);

        mYouOwe = (TextView) retVal.findViewById(R.id.fragment_account_youowe);
        mYouAreOwed = (TextView) retVal.findViewById(R.id.fragment_account_youareowed);
        mBalance = (TextView) retVal.findViewById(R.id.fragment_account_balance);
        mTotalBalanceTextView = (TextView) retVal.findViewById(R.id.fragment_account_total_text);

        int[] summary = mListener.retrieveAccountSummary();
        mYouOwe.setText("$" + Integer.toString(summary[0]));
        mYouOwe.setTextColor(getResources().getColor(R.color.negative_balance_color));
        mYouAreOwed.setText("$" + Integer.toString(summary[1]));

        if (summary[2] < 0) {
            mBalance.setText("$ " + Integer.toString(summary[2]));
            //mTotalBalanceTextView.setTextColor(getResources().getColor(R.color.negative_balance_color));
            mBalance.setTextColor(getResources().getColor(R.color.negative_balance_color));
        } else if(summary[2] == 0){
            mBalance.setText("$"+ Integer.toString(summary[2]));
        }


        return retVal;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(mAdapter);
        getListView().setOnItemClickListener(this);

        //init loader
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnAccountFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()+ " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID) {
            return new CursorLoader(getActivity(), AccountProvider.CONTENT_URI_ACCOUNTS, AccountsQuery.PROJECTION, null, null, AccountsQuery.SORT_ORDER);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//      //This swaps the new cursor into the adapter.
        if (loader.getId() == LOADER_ID) {
            mAdapter.swapCursor(cursor);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Cursor cursor = mAdapter.getCursor();
        cursor.moveToPosition(position);

        //TODO: HACK!!
        //final Uri uri = ContentUris.withAppendedId(Uri.withAppendedPath(AccountProvider.CONTENT_URI, cursor.getString(AccountProvider.ID)))

        OwePalAccount account = new OwePalAccount(cursor.getString(AccountsQuery.NAME),
                cursor.getInt(AccountsQuery.AMOUNT), cursor.getInt(AccountsQuery.IS_OWED), cursor.getInt(AccountsQuery.TIMESTAMP));

        if (mListener != null) {
            mListener.onGetAccountDetails(account);
        }

    }

    public interface OnAccountFragmentListener  {

        public void onGetAccountDetails(OwePalAccount account);

        public int[] retrieveAccountSummary();
    }

}
