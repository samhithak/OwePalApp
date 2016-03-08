package com.samhithak.owepal.model;

/**
 *
 * @author skamaraju
 */
public class OwePalAccount {
    private String mName;
    private int mAmount;
    private int mIsOwed;
    private int mTimestamp;

    public OwePalAccount(String name, int amount, int isOwed, int timestamp) {
        mName = name;
        mAmount = amount;
        mIsOwed = isOwed;
        mTimestamp = timestamp;
    }

    public String getName() {
        return mName;
    }

    public int getAmount() {
        return mAmount;
    }

    public int getIsOwed() {
        return mIsOwed;
    }

    public int getTimestam() {
        return mTimestamp;
    }
}
