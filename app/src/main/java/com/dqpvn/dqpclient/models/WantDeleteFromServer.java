package com.dqpvn.dqpclient.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by linh3 on 06/12/2017.
 */

public class WantDeleteFromServer {
    int mid;
    String mTablename;
    int mServerkey;


    public WantDeleteFromServer() {
    }

    public int getmId() {
        return mid;
    }

    public void setmId(int mid) {
        this.mid = mid;
    }

    public String getmTablename() {
        return mTablename;
    }

    public void setmTablename(String mTablename) {
        this.mTablename = mTablename;
    }

    public int getmServerkey() {
        return mServerkey;
    }

    public void setmServerkey(int mServerkey) {
        this.mServerkey = mServerkey;
    }
}
