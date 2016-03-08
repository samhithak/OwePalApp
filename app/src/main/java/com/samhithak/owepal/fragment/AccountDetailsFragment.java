package com.samhithak.owepal.fragment;

import com.samhithak.owepal.adapter.AccountsAdapter;
import com.samhithak.owepal.model.OwePalAccount;
import com.samhithak.owepal.provider.AccountProvider;
import com.samhithak.owepal.provider.AccountsQuery;

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
import android.widget.Button;
import android.widget.TextView;

import com.example.skamaraju.owepalapplication.R;

public class AccountDetailsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private OnAccountDetailsFragmentListener mListener;


    private static final int LOADER_ID = 1;

    private static final String OWE_PAL_ACCOUNT_NAME = "Account Name";
    private static final String OWE_PAL_ACCOUNT_BALANCE = "Account Balance";
    private static final String OWE_PAL_ACCOUNT_IS_OWED = "Account Is Owed";

    public static OwePalAccount mAccount;
    private String mAccountName;
    private Integer mAccountBalance;
    private Integer mAccountIsOwed;

    private TextView mName;
    private TextView mBalance;
    private TextView mIsOwed;


    private Button mRemind;
    private Button mSettleUp;
    private AccountsAdapter mAdapter;


    public static AccountDetailsFragment newInstance(OwePalAccount account) {
        AccountDetailsFragment fragment = new AccountDetailsFragment();
        Bundle args = new Bundle();
        mAccount = account;
        args.putString(OWE_PAL_ACCOUNT_NAME, account.getName());
        args.putInt(OWE_PAL_ACCOUNT_BALANCE, account.getAmount());
        args.putInt(OWE_PAL_ACCOUNT_IS_OWED, account.getIsOwed());
        fragment.setArguments(args);
        return fragment;
    }

    public AccountDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAccountName = getArguments().getString(OWE_PAL_ACCOUNT_NAME);
            mAccountBalance = getArguments().getInt(OWE_PAL_ACCOUNT_BALANCE);
            mAccountIsOwed = getArguments().getInt(OWE_PAL_ACCOUNT_IS_OWED);
        }
        mAdapter = new AccountsAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_account_details, container, false);

        mName = (TextView) mRoot.findViewById(R.id.fragment_account_details_name);
        mBalance = (TextView) mRoot.findViewById(R.id.fragment_account_details_amount);
        mIsOwed = (TextView) mRoot.findViewById(R.id.fragment_account_details_who_owes_text);

        mSettleUp = (Button) mRoot.findViewById(R.id.fragment_account_details_settle_up);

        mRemind = (Button) mRoot.findViewById(R.id.fragment_account_details_remind);
        mRemind.setOnClickListener(mOnClickListener);

        mSettleUp.setOnClickListener(mOnClickListener);

        mName.setText(mAccountName);
        mAccountBalance =  mAccountBalance * mAccountIsOwed;

        if(mAccountBalance > 0){
            mIsOwed.setText("owes you");

        } else if(mAccountBalance < 0){
            mIsOwed.setText("you owe");
            mAccountBalance = mAccountBalance * mAccountIsOwed;
            mBalance.setTextColor(getResources().getColor(R.color.negative_balance_color));
            mIsOwed.setTextColor(getResources().getColor(R.color.negative_balance_color));
        } else{
            mIsOwed.setText("Settled Up");

        }

        mBalance.setText("$" + mAccountBalance.toString());

        // Inflate the layout for this fragment
          return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(mAdapter);

        //init loader
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnAccountDetailsFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(android.view.View v) {
            switch (v.getId()){
                case R.id.fragment_account_details_remind:
                    //TODO:send email or text?
                    break;
                case R.id.fragment_account_details_settle_up:
                    mListener.settleUp(mAccountName, mAccountBalance);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID) {
            String[] sortArgs = new String[1];
            sortArgs[0] = mAccountName;
            return new CursorLoader(getActivity(), AccountProvider.CONTENT_URI_TRANSACTIONS, AccountsQuery.PROJECTION, AccountsQuery.SELECTION, sortArgs, AccountsQuery.SORT_ORDER);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
      //This swaps the new cursor into the adapter.
        if (loader.getId() == LOADER_ID) {
            mAdapter.swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public interface OnAccountDetailsFragmentListener {
        public void settleUp(String name, Integer amount);

    }

}
