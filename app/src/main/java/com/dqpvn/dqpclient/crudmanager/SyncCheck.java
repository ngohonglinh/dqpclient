package com.dqpvn.dqpclient.crudmanager;


import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;




import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.content.Context.ACCOUNT_SERVICE;
import static com.nostra13.universalimageloader.core.ImageLoader.TAG;
import static com.dqpvn.dqpclient.utils.utils.isBad;

/**
 * Created by linh3 on 21/03/2018.
 */

public class SyncCheck extends BroadcastReceiver {
    public static Boolean LOGIN_OK=false, IS_ADMIN=false, CHO_PHEP_TRUY_CAP=false;
    public static String MY_SERVER="",LOGIN_NAME="", WHO_START="", LOGIN_PASSWORD="";
    public static long RKEY_TICKET, NGAY_LUU_ANH;
    public static final String SHARED_PREFERENCES_NAME="dqpclient_preferences";
    private Context mContext;

    private String my_server="";
    private ArrayList<String> arrTableName = new ArrayList<String>();
    
    // Method gets called when Broad Case is issued from NavDrawerAcivity for every 180 seconds
    @Override
    public void onReceive(final Context context, Intent intent) {
        mContext=context;
        
        if (!isBad(intent.getAction())){
            //system time change detected
            //Toast.makeText(context, "time change " + intent.getAction(), Toast.LENGTH_SHORT).show();
        }
    }

}
