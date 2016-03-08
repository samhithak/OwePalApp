package com.samhithak.owepal.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.skamaraju.owepalapplication.R;
import com.samhithak.owepal.provider.AccountsQuery;

/**
 * TODO: Write Javadoc for AccountsAdapter.
 *
 * @author skamaraju
 */
public class AccountsAdapter extends CursorAdapter {

    LayoutInflater mInflater;

    public AccountsAdapter(Context context) {
        super(context, null, 0);

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View itemLayout = mInflater.inflate(R.layout.account_list_item, parent, false);

        // Creates a new ViewHolder in which to store handles to each view resource. This
        // allows bindView() to retrieve stored references instead of calling findViewById for
        // each instance of the layout.
        final ViewHolder holder = new ViewHolder();
        holder.name = (TextView) itemLayout.findViewById(R.id.account_list_item_name);
        holder.amount = (TextView) itemLayout.findViewById(R.id.account_list_item_amount);
        holder.whoOwes = (TextView) itemLayout.findViewById(R.id.account_list_item_who_owes_text);

        // Stores the viewholder instance in itemLayout. This makes viewholder
        // available to bindView and other methods that receive a handle to the item view.
        itemLayout.setTag(holder);
        return itemLayout;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();

        //bind view to data from cursor
        final String displayName = cursor.getString(AccountsQuery.NAME);
        holder.name.setText(displayName);

        final String amount = cursor.getString(AccountsQuery.AMOUNT);
        holder.amount.setText("$" + amount);

        String whoOwes;
        if (cursor.getInt(AccountsQuery.IS_OWED) > 0) {
            whoOwes = "owes you";
        } else if(cursor.getInt(AccountsQuery.IS_OWED) < 0){
            whoOwes = "you owe";
            holder.amount.setTextColor(context.getResources().getColor(R.color.negative_balance_color));
            holder.whoOwes.setTextColor(context.getResources().getColor(R.color.negative_balance_color));
        } else{
            whoOwes = "settled up";
        }
        holder.whoOwes.setText(whoOwes);
    }

    private static class ViewHolder {
        ImageView icon;
        TextView name;
        TextView whoOwes;
        TextView amount;

    }
}
