package com.samhithak.owepal.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.skamaraju.owepalapplication.R;
import com.samhithak.owepal.adapter.AccountsAdapter;
import com.samhithak.owepal.provider.AccountProvider;
import com.samhithak.owepal.provider.AccountsQuery;

public class HistoryFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int LOADER_ID = 1;
    private int mPreviouslySelectedItem = 0;
    private static final String STATE_PREVIOUSLY_SELECTED_KEY = "com.samhithak.owepal.fragment.HistoryFragment.SELECTED_ITEM";


    private AccountsAdapter mAdapter;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // If we're restoring state after this fragment was recreated then
            // retrieve previously selected item.
            mPreviouslySelectedItem = savedInstanceState.getInt(STATE_PREVIOUSLY_SELECTED_KEY, 0);
        }
        mAdapter = new AccountsAdapter(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(mAdapter);

        //init loader
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID) {
            return new CursorLoader(getActivity(), AccountProvider.CONTENT_URI_TRANSACTIONS, AccountsQuery.PROJECTION, null, null, AccountsQuery.SORT_ORDER);
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<android.database.Cursor> loader, Cursor data) {
//      //This swaps the new cursor into the adapter.
        if (loader.getId() == LOADER_ID) {
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<android.database.Cursor> loader) {

    }

}
