package com.samhithak.owepal.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.skamaraju.owepalapplication.R;

import static android.view.View.OnClickListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateAccountFragment extends Fragment {

    private View mRoot;

    private OnCreateAccountFragmentListener mListener;

    private EditText mName;
    private EditText mAccount;
    private Button mAddButton;
    private Button mIOwe;
    private Button mYouOwe;
    boolean mIsOwed = false;


    public CreateAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRoot = inflater.inflate(R.layout.fragment_create_account, container, false);

        mName = (EditText)mRoot.findViewById(R.id.fragment_create_account_name);
        mAccount = (EditText)mRoot.findViewById(R.id.fragment_create_account_amount);
        mAddButton = (Button)mRoot.findViewById(R.id.fragment_create_account_create_button);
        mIOwe = (Button) mRoot.findViewById(R.id.fragment_create_account_radio_i_owe);
        mYouOwe = (Button) mRoot.findViewById(R.id.fragment_create_account_radio_you_owe);

        // set listeners
        mAddButton.setOnClickListener(mOnClickListener);

        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.equals("")) {
                    mAddButton.setEnabled(false);
                    mIOwe.setEnabled(false);
                    mYouOwe.setEnabled(false);

                } else {
                    mAddButton.setEnabled(true);

                    mIOwe.setEnabled(true);
                    mIOwe.setText("I Owe" + " " + s.toString());

                    mYouOwe.setEnabled(true);
                    mYouOwe.setText(s.toString() + " " + "Owes Me");
                }
            }
        });

        mIOwe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIOwe.isActivated()) {
                    //disable youOweMe button
                    if (mYouOwe != null) {
                        mYouOwe.setActivated(false);
                        mYouOwe.setEnabled(false);
                    }
                    mIOwe.setBackgroundColor(getResources().getColor(R.color.primary_color));
                    mIOwe.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_redo_white_36dp, 0, 0);
                    mIOwe.setText("I Owe" + " " + mName.getText().toString());
                    mIOwe.setActivated(true);
                } else {
                    //if activated, toggle state
                    mIOwe.setBackgroundColor(0);
                    mIOwe.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_redo_grey600_36dp, 0, 0);
                    mIOwe.setActivated(false);

                    mYouOwe.setEnabled(true);
                }
                mIsOwed = false;
            }
        });


        mYouOwe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mYouOwe.isActivated()) {
                    //disable IOwe button
                    if (mIOwe != null) {
                        mIOwe.setActivated(false);
                        mIOwe.setEnabled(false);
                    }
                    mYouOwe.setBackgroundColor(getResources().getColor(R.color.primary_color));
                    mYouOwe.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_undo_white_36dp, 0, 0);
                    mYouOwe.setText(mName.getText().toString() + " " + "Owes Me");
                    mYouOwe.setActivated(true);
                } else {
                    //if activated, toggle state
                    mYouOwe.setBackgroundColor(0);
                    mYouOwe.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_undo_grey600_36dp, 0, 0);
                    mYouOwe.setActivated(false);

                    mIOwe.setEnabled(true);
                }
                mIsOwed = true;
            }
        });

        return mRoot;

    }

    private void toggleButtonState(Boolean on) {

    }

    OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(android.view.View v) {

            switch(v.getId()){
                case R.id.fragment_create_account_create_button:
                    mListener.createAccount(mName.getText().toString(), Integer.parseInt(mAccount.getText().toString()), mIsOwed ? new Integer(1) : new Integer(-1));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCreateAccountFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnCreateAccountFragmentListener {

        public void createAccount(String name, int account, int isOwed);

        public void onFragmentInteraction(android.net.Uri uri);

    }

}
