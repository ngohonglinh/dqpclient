package com.dqpvn.dqpclient.crudmanager;

import android.app.DownloadManager;
import android.app.IntentService;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.dqpvn.dqpclient.NavDrawerActivity;
import com.dqpvn.dqpclient.R;
import com.dqpvn.dqpclient.utils.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.MY_SERVER;
import static com.dqpvn.dqpclient.utils.utils.longGet;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class updateSoftwareService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent==null){
            return START_NOT_STICKY;
        }

        if (intent.hasExtra("checkUpdate")){
            try {
                if (Settings.canDrawOverlays(this) && networkOK()) {
                    Log.d("updateSoftware", "-------------by checkUpdate command-----------We will check for Update only");
                    checkNewUpdate();
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        //super.onStartCommand(intent,startId,startId);
        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private Boolean networkOK(){
        Boolean is=false;
        try {
            is= new utils.isInternetAvailable().execute("https://google.com:80").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return is;
    }


    //Check for update to new version***************************************************************

    private void checkNewUpdate() throws ExecutionException, InterruptedException {
        String version="";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (!version.equals("")){
            //ArrayList<?> res =new restfullAPI.checkUpdate().execute(MY_SERVER + "/dqpclient/checkupdate/").get();
            restfullAPI.checkUpdate  updatetask = new restfullAPI.checkUpdate();
            final String newVersion=version;
            final String finalVersion = StringUtils.replaceAll(version,"\\.","");
            updatetask.setUpdateListener(new restfullAPI.checkUpdate.OnUpdateListener() {
                @Override
                public void onUpdate(ArrayList<?> res) {
                    if (res==null){
                        return;
                    }

                    String fileUrl="";
                    String khung[];
                    String fileName="";
                    String fileExt="";

                    for (int i=0;i<res.size();i++){
                        fileUrl=res.get(i).toString();
                        khung=fileUrl.split("/");
                        fileExt=khung[khung.length-1].substring(khung[khung.length-1].length()-3);
                        fileName=khung[khung.length-1].substring(0,khung[khung.length-1].length()-4);
                        if (fileExt.equals("apk")){
                            try{
                                String StrNewVer=fileName.replaceAll("dqpclient_","");
                                long oldVer=longGet(finalVersion);
                                long newVer=longGet(StringUtils.replaceAll(StrNewVer,"\\.",""));
                                if (oldVer<100){
                                    oldVer=oldVer*10;//1.2=12=120 1.2.1=121 1.3=13=130
                                }
                                if (newVer<100){
                                    newVer=newVer*10;
                                }
                                if (newVer>oldVer){
                                    final String finalFileUrl = fileUrl;
                                    final String finalFileName = fileName;
                                    final String finalFileExt = fileExt;

                                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(updateSoftwareService.this, R.style.AppTheme_MaterialDialogTheme);
                                    dialogBuilder.setTitle("Nâng cấp fix lổi");
                                    dialogBuilder.setMessage("- Phiên bản đang dùng: "+ newVersion + "\n" +
                                                    "- Phiên bản vá lổi mới: " + fileName.replaceAll("dqpclient_","")
                                                    + "\n" + "\n"+ "Tải về cài đặt?");
                                    dialogBuilder.setCancelable(false);
                                    dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            int pid = android.os.Process.myPid();
                                            android.os.Process.killProcess(pid);
                                            Intent intent = new Intent(Intent.ACTION_MAIN);
                                            intent.addCategory(Intent.CATEGORY_HOME);
                                            startActivity(intent);
                                        }
                                    });
                                    dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                               public void onClick(DialogInterface dialog, int id) {
                                                    File dir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                                    String fileName = finalFileName+"."+finalFileExt;
                                                    final Uri uri =Uri.parse(dir.toURI()+fileName);
                                                    File file = new File(dir+"/"+fileName);
                                                    if (file.exists())
                                                       //file.delete() - test this, I think sometimes it doesnt work
                                                        file.delete();
                                                    //get url of app on server
                                                    String url = finalFileUrl;
                                                    //set downloadmanager
                                                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                                                   request.setTitle(updateSoftwareService.this.getString(R.string.app_name));
                                                    //set destination
                                                    request.setDestinationUri(uri);
                                                    // get download service and enqueue file
                                                    final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                                    final long downloadId = manager.enqueue(request);
                                                    //share uri.
                                                    final Uri uri2 = FileProvider.getUriForFile(updateSoftwareService.this, getApplicationContext().getPackageName() + ".provider", file);
                                                    //set BroadcastReceiver to install app when .apk is downloaded
                                                    BroadcastReceiver onComplete = new BroadcastReceiver() {
                                                        public void onReceive(Context ctxt, Intent intent) {

                                                            Intent install = new Intent(Intent.ACTION_VIEW);
                                                            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                            install.setDataAndType(uri2,
                                                                    manager.getMimeTypeForDownloadedFile(downloadId));
                                                            startActivity(install);

                                                            unregisterReceiver(this);
                                                            //finish();
                                                        }
                                                    };
                                                    //register receiver for when .apk download is compete
                                                    registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                                                }
                                            });

                                    final AlertDialog dialog = dialogBuilder.create();
                                    final Window dialogWindow = dialog.getWindow();
                                    final WindowManager.LayoutParams dialogWindowAttributes = dialogWindow.getAttributes();

                                    // Set fixed width (280dp) and WRAP_CONTENT height
                                    final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                    lp.copyFrom(dialogWindowAttributes);
                                    lp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
                                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                    dialogWindow.setAttributes(lp);

                                    // Set to TYPE_SYSTEM_ALERT so that the Service can display it
                                    dialogWindow.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                    dialog.show();
                                }
                            }catch(NumberFormatException e){
                                e.printStackTrace();
                            }
                        }
                    }

                }
            });
            updatetask.execute(new String[]{MY_SERVER + "/dqpclient/checkupdate/"});
        }
    }

}
