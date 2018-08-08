package com.patientz.databases;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by sunil on 14/12/15.
 */
public class MySqliteAssetHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "org_branches.db";
    private static final int DATABASE_VERSION = 3;

    public MySqliteAssetHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }
}
