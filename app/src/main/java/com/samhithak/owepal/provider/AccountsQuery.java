package com.samhithak.owepal.provider;

/**
 * TODO: Write Javadoc for AccountsQueryBuilder.
 *
 * @author skamaraju
 */
public class AccountsQuery {

    public static final String[] PROJECTION = {
            AccountProvider._ID,
            AccountProvider._NAME,
            AccountProvider._AMOUNT,
            AccountProvider._IS_OWED,
            AccountProvider._TIMESTAMP };

    public static final String SELECTION = AccountProvider._NAME + "=?" ;

    // The query column numbers which map to each value in the projection
    public final static int ID = 0;
    public final static int NAME = 1;
    public final static int AMOUNT = 2;
    public final static int IS_OWED = 3;
    public final static int TIMESTAMP = 4;
    public final static int SORT_KEY = 5;
    public final static String SORT_ORDER =
                     AccountProvider._TIMESTAMP;
}