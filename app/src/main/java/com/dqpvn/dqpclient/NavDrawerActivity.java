package com.dqpvn.dqpclient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.content.SyncRequest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dqpvn.dqpclient.crudmanager.SyncCheck;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.restfullAPI;
import com.dqpvn.dqpclient.crudmanager.updateSoftwareService;
import com.dqpvn.dqpclient.models.Chi;
import com.dqpvn.dqpclient.models.ChuyenBien;
import com.dqpvn.dqpclient.models.DSTV;
import com.dqpvn.dqpclient.models.DebtBook;
import com.dqpvn.dqpclient.models.ImgStore;
import com.dqpvn.dqpclient.models.Thu;
import com.dqpvn.dqpclient.models.Ticket;
import com.dqpvn.dqpclient.models.Users;
import com.dqpvn.dqpclient.syncAdapter.SyncAdapter;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.CHO_PHEP_TRUY_CAP;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.IS_ADMIN;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_NAME;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_OK;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.MY_SERVER;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.RKEY_TICKET;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.SHARED_PREFERENCES_NAME;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.WHO_START;

import static com.dqpvn.dqpclient.crudmanager.SyncCheck.NGAY_LUU_ANH;
import static com.dqpvn.dqpclient.utils.utils.comPare;
import static com.dqpvn.dqpclient.utils.utils.doubleGet;
import static com.dqpvn.dqpclient.utils.utils.getCurrentDate;
import static com.dqpvn.dqpclient.utils.utils.getCurrentTimeMiliS;
import static com.dqpvn.dqpclient.utils.utils.getDurationFromMilisecond;
import static com.dqpvn.dqpclient.utils.utils.getStringLeft;
import static com.dqpvn.dqpclient.utils.utils.intGet;
import static com.dqpvn.dqpclient.utils.utils.isBad;
import static com.dqpvn.dqpclient.utils.utils.longGet;
import static com.dqpvn.dqpclient.utils.utils.readFromFile;
import static com.dqpvn.dqpclient.utils.utils.writeToFile;

public class NavDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final private String TAG = getClass().getSimpleName();
    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;

    final int REQUEST_SETTING=123;
    private long rkeyTicket;
    private SharedPreferences sharedpre;
    boolean moveFab=false;

    FloatingActionButton fab, fabTrai, fabPhai, fabTren, fabDuoi, fab45, fab135, fab225, fab315;
    Animation moveTrai, movePhai, moveTren, moveDuoi, move45, move135, move225, move315,
            backTrai, backPhai, backTren, backDuoi, back45, back135, back225, back315;

    private crudLocal crudLocaldb=crudLocal.getInstance(this);

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1==null || e2==null){
                return true;
            }
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Log.d(TAG, "Right to Left swipe performed");
            }
            // left to right swipe
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Log.d(TAG, "Left to Right swipe performed");
            }

            // Down to Up swipe performed
            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                if (!moveFab){
                    moveFab();
                    moveFab=true;
                }
                Log.d(TAG, "Down to Up swipe performed");
            }
            // Up to Down swipe performed
            else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                if (moveFab){
                    backFab();
                    moveFab=false;
                }
                Log.d(TAG, "Up to Down swipe performed");
            }

            return true;
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    //lam viec voi menu
    @Override
    public boolean onCreateOptionsMenu(Menu manu) {
        getMenuInflater().inflate(R.menu.nav_drawer, manu);
        if (!StringUtils.equals(StringUtils.left(LOGIN_NAME,5),"admin")){
            for (int i = 0; i < manu.size(); i++){
                if (manu.getItem(i).getItemId()!=R.id.NavSyncTinhVT){
                    manu.getItem(i).setVisible(false);
                }
            }
        }

        return true;
    }

    // Method này sử lý sự kiện khi MenuItem được chọn.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            //ten cua cac id khi thiet ke cac resource menu
            case R.id.NavSyncPull:
                requestInstantSync("wantPull");
                return true;
            case R.id.NavSyncPost:
                requestInstantSync("wantPost");
                return true;
            case R.id.NavSyncUpdate:
                requestInstantSync("wantUpdate");
                return true;
            case R.id.NavSyncDelete:
                requestInstantSync("wantDelete");
                return true;
            case R.id.NavSyncTinhVT:
                Intent i=new Intent(this,TinhVTActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.dqplogo2);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initdata();

        addControls();

        addEvents();

//        Timer timerObj = new Timer();
//        TimerTask timerTaskObj = new TimerTask() {
//            public void run() {
//                    if (!IS_SYNC_STILL_RUNNING){
//                        if (!mShowSync){
//                            mShowSync=true;
//                        }
//                    }else{
//                        if (mShowSync){
//                            mShowSync=false;
//                        }
//                    }
//                invalidateOptionsMenu();
//
//            }
//        };
//        timerObj.schedule(timerTaskObj, 0, 8*1000);
        // BroadCase Receiver Intent Object
//        Intent alarmIntent = new Intent(getApplicationContext(), SyncCheck.class);
//        alarmIntent.putExtra("myServer",MY_SERVER);
//        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Pending Intent Object
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        // Alarm Manager Object
//        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        // Alarm Manager calls BroadCast for every Ten seconds (10 * 1000), BroadCase further calls service to check if new records are inserted in
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+8000, 3*60*1000, pendingIntent);
    }

    private void initdata(){
        ArrayList<Users>arrUser=crudLocaldb.Users_getAllUsers();
        boolean firstActivate=false;
        final boolean[] accOk = {false};
        if (arrUser.size()==0){
            firstActivate=true;
        }
        if (arrUser.size()>0){
            // Lay addmin right local phong truong hop khong co net
            for(int i=0;i<arrUser.size();i++){
                if (comPare(arrUser.get(i).getEmail(),LOGIN_NAME)){
                    accOk[0] =true;
                    if (arrUser.get(i).getAdmin()==1){
                        IS_ADMIN=true;
                    }else{
                        IS_ADMIN=false;
                        hideNavItem();
                    }
                    break;
                }
            }
            //find admin right tren mang cho chac.
            restfullAPI.getUser  task = new restfullAPI.getUser();
            task.setUpdateListener(new restfullAPI.getUser.OnUpdateListener(){
                @Override
                public void onUpdate(ArrayList<Users> arrServerUsers) {
                    if (arrServerUsers==null) {return;}
                    if (arrServerUsers.size()>0){
                        for (int i=0;i<arrServerUsers.size();i++){
                            if (comPare(arrServerUsers.get(i).getEmail(),LOGIN_NAME)){
                                accOk[0] =true;
                                if (arrServerUsers.get(i).getAdmin()==1){
                                    IS_ADMIN=true;
                                }else{
                                    IS_ADMIN=false;
                                    hideNavItem();
                                }
                                break;
                            }
                        }
                    }
                }
            });
            task.execute(MY_SERVER + "/dqpclient/user");
        }else{
            ArrayList<Users>arrServerUsers=new ArrayList<>();
            try {
                arrServerUsers= new restfullAPI.getUser().execute(MY_SERVER + "/dqpclient/user").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if (arrServerUsers.size()>0){
                for (int i=0;i<arrServerUsers.size();i++){
                    if (comPare(arrServerUsers.get(i).getEmail(),LOGIN_NAME)){
                        accOk[0] =true;
                        if (arrServerUsers.get(i).getAdmin()==1){
                            IS_ADMIN=true;
                        }else{
                            IS_ADMIN=false;
                            hideNavItem();
                        }
                        break;
                    }
                }
            }
        }

        if (LOGIN_OK) {
            CHO_PHEP_TRUY_CAP=true;
            //find right Ticket....
            if (LOGIN_NAME.substring(0,5).equals("admin")){
                WHO_START="admin";
            }else {
                if(isThuyenTruong()){
                    WHO_START="thuyenTruong";
                }else{
                    WHO_START="linhNha";
                }
            }
            ArrayList<Ticket>arrTicket=new ArrayList<>();
            arrTicket=crudLocaldb.Ticket_getOpenTicketByUser(LOGIN_NAME);
            rkeyTicket=0;
            if (arrTicket.size()>0){
                for (int i=0;i<arrTicket.size();i++){
                    if (arrTicket.get(i).getFinished()==0){
                        rkeyTicket=arrTicket.get(i).getRkey();
                        RKEY_TICKET=arrTicket.get(i).getRkey();
                        break;
                    }
                }
            }else{
                if (!firstActivate){
                    makeTempTicket();
                }else{
                    rkeyTicket=-1;
                }
            }

        }else{
            CHO_PHEP_TRUY_CAP=false;
        }

        if (!accOk[0]){
            CHO_PHEP_TRUY_CAP=false;
            Toast.makeText(this, "Account đã hết hạng sữ dụng...", Toast.LENGTH_SHORT).show();
            this.finish();
        }else{
            CHO_PHEP_TRUY_CAP=true;
        }
    }

    private void getPre(){
        sharedpre = getSharedPreferences(SHARED_PREFERENCES_NAME, this.MODE_PRIVATE);
        MY_SERVER=sharedpre.getString("Server","");
        NGAY_LUU_ANH=sharedpre.getLong("TuNgay",0);
        LOGIN_NAME=sharedpre.getString("LoginName","");
        LOGIN_OK =sharedpre.getBoolean("LoginOk",false);
    }

    private void makeTempTicket(){
        ArrayList<Users>arrUser=new ArrayList<>();
        arrUser=crudLocaldb.Users_getAllUsers();
        if (arrUser.size()>0){
            //find right ticket if had
            ArrayList<Ticket>arrTicket=new ArrayList<>();
            arrTicket=crudLocaldb.Ticket_getOpenTicketByUser(LOGIN_NAME);
            if (arrTicket.size()>0){
                for (int i=0;i<arrTicket.size();i++){
                    if (arrTicket.get(i).getFinished()==0){
                        rkeyTicket=arrTicket.get(i).getRkey();
                        RKEY_TICKET=arrTicket.get(i).getRkey();
                        break;
                    }
                }
            }else{
                    //add temp ticket
                    Ticket ticket=new Ticket();
                    ticket.setServerkey(0);
                    rkeyTicket=longGet(getCurrentTimeMiliS());
                    RKEY_TICKET=longGet(getCurrentTimeMiliS());
                    ticket.setRkey(rkeyTicket);
                    ticket.setAmount("0");
                    ticket.setUsed("0");
                    ticket.setComeback("0");
                    ticket.setOpendate(getCurrentDate());
                    ticket.setFinished(0);
                    ticket.setUpdatetime(getCurrentTimeMiliS());
                    ticket.setUsername(LOGIN_NAME);
                    ticket.setLydo("tmp Ticket by " +getStringLeft(LOGIN_NAME,"@") + " " + getDurationFromMilisecond(System.currentTimeMillis()));
                    crudLocaldb.Ticket_addTicket(ticket);

            }
        }
    }

    private void moveFab(){
        FrameLayout.LayoutParams paramsTrai =(FrameLayout.LayoutParams) fabTrai.getLayoutParams();
        paramsTrai.rightMargin= (int) (fabTrai.getWidth()*1.7);
        fabTrai.setLayoutParams(paramsTrai);
        fabTrai.startAnimation(moveTrai);

        FrameLayout.LayoutParams params315 =(FrameLayout.LayoutParams) fab315.getLayoutParams();
        params315.rightMargin = (int) (fab315.getWidth()*1.22);
        params315.bottomMargin=(int) (fab315.getWidth()*1.22);
        fab315.setLayoutParams(params315);
        fab315.startAnimation(move315);

        FrameLayout.LayoutParams paramsTren =(FrameLayout.LayoutParams) fabTren.getLayoutParams();
        paramsTren.bottomMargin = (int) (fabTren.getWidth()*1.7);
        fabTren.setLayoutParams(paramsTren);
        fabTren.startAnimation(moveTren);

        FrameLayout.LayoutParams params45 =(FrameLayout.LayoutParams) fab45.getLayoutParams();
        params45.leftMargin = (int) (fab45.getWidth()*1.22);
        params45.bottomMargin=(int) (fab45.getWidth()*1.22);
        fab45.setLayoutParams(params45);
        //fab45.startAnimation(move45);

        FrameLayout.LayoutParams paramsPhai =(FrameLayout.LayoutParams) fabPhai.getLayoutParams();
        paramsPhai.leftMargin= (int) (fabPhai.getWidth()*1.7);
        fabPhai.setLayoutParams(paramsPhai);
        //fabPhai.startAnimation(movePhai);

        FrameLayout.LayoutParams params135 =(FrameLayout.LayoutParams) fab135.getLayoutParams();
        params135.leftMargin = (int) (fab135.getWidth()*1.22);
        params135.topMargin=(int) (fab135.getWidth()*1.22);
        fab135.setLayoutParams(params135);
        //fab135.startAnimation(move135);

        FrameLayout.LayoutParams paramsDuoi =(FrameLayout.LayoutParams) fabDuoi.getLayoutParams();
        paramsDuoi.topMargin = (int) (fabDuoi.getWidth()*1.7);
        fabDuoi.setLayoutParams(paramsDuoi);
        //fabDuoi.startAnimation(moveDuoi);


        FrameLayout.LayoutParams params225 =(FrameLayout.LayoutParams) fab225.getLayoutParams();
        params225.rightMargin = (int) (fab225.getWidth()*1.22);
        params225.topMargin=(int) (fab225.getWidth()*1.22);
        fab225.setLayoutParams(params225);
        //fab225.startAnimation(move225);
    }

    private void backFab(){
        FrameLayout.LayoutParams paramsTrai =(FrameLayout.LayoutParams) fabTrai.getLayoutParams();
        paramsTrai.rightMargin -= (int) (fabTrai.getWidth()*1.7);
        fabTrai.setLayoutParams(paramsTrai);
        fabTrai.startAnimation(backTrai);

        FrameLayout.LayoutParams params315 =(FrameLayout.LayoutParams) fab315.getLayoutParams();
        params315.rightMargin -= (int) (fab315.getWidth()*1.22);
        params315.bottomMargin -=(int) (fab315.getWidth()*1.22);
        fab315.setLayoutParams(params315);
        fab315.startAnimation(back315);

        FrameLayout.LayoutParams paramsTren =(FrameLayout.LayoutParams) fabTren.getLayoutParams();
        paramsTren.bottomMargin -= (int) (fabTren.getWidth()*1.7);
        fabTren.setLayoutParams(paramsTren);
        fabTren.startAnimation(backTren);

        FrameLayout.LayoutParams params45 =(FrameLayout.LayoutParams) fab45.getLayoutParams();
        params45.leftMargin -= (int) (fab45.getWidth()*1.22);
        params45.bottomMargin -=(int) (fab45.getWidth()*1.22);
        fab45.setLayoutParams(params45);
        //fab45.startAnimation(back45);

        FrameLayout.LayoutParams paramsPhai =(FrameLayout.LayoutParams) fabPhai.getLayoutParams();
        paramsPhai.leftMargin -= (int) (fabPhai.getWidth()*1.7);
        fabPhai.setLayoutParams(paramsPhai);
        //fabPhai.startAnimation(backPhai);

        FrameLayout.LayoutParams params135 =(FrameLayout.LayoutParams) fab135.getLayoutParams();
        params135.leftMargin -= (int) (fab135.getWidth()*1.22);
        params135.topMargin -=(int) (fab135.getWidth()*1.22);
        fab135.setLayoutParams(params135);
        //fab135.startAnimation(back135);

        FrameLayout.LayoutParams paramsDuoi =(FrameLayout.LayoutParams) fabDuoi.getLayoutParams();
        paramsDuoi.topMargin -= (int) (fabDuoi.getWidth()*1.7);
        fabDuoi.setLayoutParams(paramsDuoi);
        //fabDuoi.startAnimation(backDuoi);


        FrameLayout.LayoutParams params225 =(FrameLayout.LayoutParams) fab225.getLayoutParams();
        params225.rightMargin -= (int) (fab225.getWidth()*1.22);
        params225.topMargin -=(int) (fab225.getWidth()*1.22);
        fab225.setLayoutParams(params225);
        //fab225.startAnimation(back225);
    }
    private void addControls(){

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabTrai = (FloatingActionButton) findViewById(R.id.fab_trai);
        fabPhai = (FloatingActionButton) findViewById(R.id.fab_phai);
        fabTren = (FloatingActionButton) findViewById(R.id.fab_tren);
        fabDuoi = findViewById(R.id.fab_duoi);
        fab45 = (FloatingActionButton) findViewById(R.id.fab_45Degrees);
        fab135 = (FloatingActionButton) findViewById(R.id.fab_135Degrees);
        fab225 = (FloatingActionButton) findViewById(R.id.fab_225Degrees);
        fab315 = (FloatingActionButton) findViewById(R.id.fab_315Degrees);


        moveTrai= AnimationUtils.loadAnimation(this,R.anim.move_270degrees);
        movePhai= AnimationUtils.loadAnimation(this,R.anim.move_90degrees);
        moveTren= AnimationUtils.loadAnimation(this,R.anim.move_360degrees);
        moveDuoi= AnimationUtils.loadAnimation(this,R.anim.move_180degrees);
        move45= AnimationUtils.loadAnimation(this,R.anim.move_45degrees);
        move135= AnimationUtils.loadAnimation(this,R.anim.move_135degrees);
        move225= AnimationUtils.loadAnimation(this,R.anim.move_225degrees);
        move315= AnimationUtils.loadAnimation(this,R.anim.move_315degrees);

        backTrai= AnimationUtils.loadAnimation(this,R.anim.back_270degrees);
        backPhai= AnimationUtils.loadAnimation(this,R.anim.back_90degrees);
        backTren= AnimationUtils.loadAnimation(this,R.anim.back_360degrees);
        backDuoi= AnimationUtils.loadAnimation(this,R.anim.back_180degrees);
        back45= AnimationUtils.loadAnimation(this,R.anim.back_45degrees);
        back135= AnimationUtils.loadAnimation(this,R.anim.back_135degrees);
        back225= AnimationUtils.loadAnimation(this,R.anim.back_225degrees);
        back315= AnimationUtils.loadAnimation(this,R.anim.back_315degrees);
    }

    private void addEvents(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                if (!moveFab){
                    moveFab();
                    moveFab=true;
                } else{
                    backFab();
                    moveFab=false;
                }
            }
        });
        DrawerLayout lyFab=findViewById(R.id.drawer_layout);

        lyFab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mGestureDetector.onTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
                // return true;
            }
        });

        fabTrai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CHO_PHEP_TRUY_CAP){
                    gotoSetting();
                    return;
                }
                if (rkeyTicket==-1){
                    Toast.makeText(NavDrawerActivity.this, "Chưa được.. cứ từ từ.....", Toast.LENGTH_SHORT).show();
                    makeTempTicket();
                    return;
                }
                Intent intent=new Intent(NavDrawerActivity.this, TicketActivity.class);
                intent.putExtra("rkeyTicket", rkeyTicket);
                intent.putExtra("userName", LOGIN_NAME);
                startActivity(intent);
            }
        });

        fabTren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CHO_PHEP_TRUY_CAP){
                    gotoSetting();
                    return;
                }
                if (!LOGIN_NAME.equals(null)){
                    gotoDebtBook();
                }else{
                    Toast.makeText(NavDrawerActivity.this, "Chưa đủ dữ liệu", Toast.LENGTH_SHORT).show();
                }

            }
        });

        fab315.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CHO_PHEP_TRUY_CAP){
                    gotoSetting();
                    return;
                }
                ArrayList<DSTV> arrDSTV=new ArrayList<>();
                long rkeyChuyenBien=crudLocaldb.ChuyenBien_getRkeyByShipmaster(LOGIN_NAME);
                arrDSTV=crudLocaldb.DSTV_getDSTVByChuyenBien(rkeyChuyenBien);
                Intent intent;
                if (rkeyTicket==-1){
                    Toast.makeText(NavDrawerActivity.this, "Wait for initialize...", Toast.LENGTH_SHORT).show();
                    makeTempTicket();
                    return;
                }
                if (WHO_START=="linhNha" || IS_ADMIN){
                    //linh nha
                    intent = new Intent(NavDrawerActivity.this, ChiActivity.class);
                    intent.putExtra("rkeyTicket", rkeyTicket);
                    intent.putExtra("userName", LOGIN_NAME);
                    intent.putExtra("makeNew", true);
                }else{
                    //thuyen truong
                    intent = new Intent(NavDrawerActivity.this, DSTVActivity.class);
                    intent.putExtra("rkeyTicket", rkeyTicket); //RKEY_TICKET must alwys same rkeyticket :)
                    intent.putExtra("rkeyChuyenBien", rkeyChuyenBien);
                    intent.putExtra("userName", LOGIN_NAME);
                    if (arrDSTV.size()==0){
                        intent.putExtra("makeNew", true);
                    }
                }
                startActivity(intent);
            }
        });

    }
    private void gotoDebtBook() {
        if (rkeyTicket==-1){
            Toast.makeText(NavDrawerActivity.this, "Wait for initialize...", Toast.LENGTH_SHORT).show();
            makeTempTicket();
            return;
        }
        Intent intent=new Intent(NavDrawerActivity.this, DebtBookActivity.class);
        long rkeyChuyenBien=crudLocaldb.ChuyenBien_getRkeyByShipmaster(LOGIN_NAME);
        if (LOGIN_NAME.substring(0,5).equals("admin")){
            intent.putExtra("whoStart","admin");
        }else if(isThuyenTruong()){
            intent.putExtra("whoStart","thuyenTruong");
            intent.putExtra("rkeyChuyenBien", rkeyChuyenBien);
        }else{
            intent.putExtra("whoStart","linhNha");
        }

        intent.putExtra("rkeyTicket", rkeyTicket);
        intent.putExtra("userName", LOGIN_NAME);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void hideNavItem() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_DoiTac).setVisible(false);
        nav_Menu.findItem(R.id.nav_KhachHang).setVisible(false);
        nav_Menu.findItem(R.id.nav_Ticket).setVisible(false);
        nav_Menu.findItem(R.id.nav_users).setVisible(false);
        if (isThuyenTruong()&& !IS_ADMIN){
            nav_Menu.findItem(R.id.nav_DMHS).setVisible(false);
        }

    }

    private void gotoSetting(){
        Intent intent = new Intent(NavDrawerActivity.this, SettingActivity.class);
        startActivityForResult(intent,REQUEST_SETTING);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent;

        switch (item.getItemId()) {
            case R.id.nav_ChuyenBien:
                if (!CHO_PHEP_TRUY_CAP){
                    gotoSetting();
                    return true;
                }
                if (rkeyTicket==-1){
                    Toast.makeText(NavDrawerActivity.this, "Wait for initialize...", Toast.LENGTH_SHORT).show();
                    makeTempTicket();
                    return true;
                }
                intent = new Intent(NavDrawerActivity.this,ChuyenBienActivity.class);
                intent.putExtra("rkeyTicket", rkeyTicket);
                intent.putExtra("userName", LOGIN_NAME);
                NavDrawerActivity.this.startActivity(intent);
                break;
            case R.id.nav_DoiTac:
                if (!CHO_PHEP_TRUY_CAP){
                    gotoSetting();
                    return true;
                }
                if (!IS_ADMIN){
                    Toast.makeText(this, "Authorized personnel only...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                intent = new Intent(NavDrawerActivity.this,DoiTacActivity.class);
                intent.putExtra("userName", LOGIN_NAME);
                intent.putExtra("rkeyTicket", rkeyTicket);
                NavDrawerActivity.this.startActivity(intent);
                break;
            case R.id.nav_KhachHang:
                if (!CHO_PHEP_TRUY_CAP){
                    gotoSetting();
                    return true;
                }
                if (!IS_ADMIN){
                    Toast.makeText(this, "Authorized personnel only...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                intent = new Intent(NavDrawerActivity.this,KhachHangActivity.class);
                intent.putExtra("userName", LOGIN_NAME);
                intent.putExtra("rkeyTicket", rkeyTicket);
                NavDrawerActivity.this.startActivity(intent);
                break;
            case R.id.nav_Chi:
                if (!CHO_PHEP_TRUY_CAP){
                    gotoSetting();
                    return true;
                }
                if (rkeyTicket==-1){
                    Toast.makeText(NavDrawerActivity.this, "Wait for initialize...", Toast.LENGTH_SHORT).show();
                    makeTempTicket();
                    return true;
                }
                if (WHO_START=="thuyenTruong" && !IS_ADMIN){
                    gotoChiAsThuyenTruong();
                }else{
                    gotolistChiByTicket(333);
                }
//                intent = new Intent(NavDrawerActivity.this,ChiActivity.class);
//                intent.putExtra("rkeyTicket", rkeyTicket);
//                intent.putExtra("userName", LOGIN_NAME);
//                if  (!LOGIN_NAME.substring(0,5).equals("admin")){
//                    intent.putExtra("makeNew",true);
//                }
//                NavDrawerActivity.this.startActivity(intent);
                break;
            case R.id.nav_Thu:
                if (!CHO_PHEP_TRUY_CAP){
                    gotoSetting();
                    return true;
                }
                if (WHO_START=="thuyenTruong" && !IS_ADMIN){
                    gotoThuAsThuyenTruong();
                    return true;
                }
                if (rkeyTicket==-1){
                    Toast.makeText(NavDrawerActivity.this, "Wait for initialize...", Toast.LENGTH_SHORT).show();
                    makeTempTicket();
                    return true;
                }
                intent = new Intent(NavDrawerActivity.this,ThuActivity.class);
                intent.putExtra("rkeyTicket", rkeyTicket);
                intent.putExtra("userName", LOGIN_NAME);
                if  (!LOGIN_NAME.substring(0,5).equals("admin")){
                    intent.putExtra("makeNew",true);
                }
                NavDrawerActivity.this.startActivity(intent);
                break;
            case R.id.nav_Ticket:
                if (!CHO_PHEP_TRUY_CAP){
                    gotoSetting();
                    return true;
                }
                if (WHO_START!="admin"){
                    Toast.makeText(this, "Authorized personnel only...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                intent = new Intent(NavDrawerActivity.this,TicketActivity.class);
                intent.putExtra("userName",LOGIN_NAME);
                NavDrawerActivity.this.startActivity(intent);
                break;
            case R.id.nav_DebtBook:
                if (!CHO_PHEP_TRUY_CAP){
                    gotoSetting();
                    return true;
                }
                if (rkeyTicket==-1){
                    Toast.makeText(NavDrawerActivity.this, "Wait for initialize...", Toast.LENGTH_SHORT).show();
                    makeTempTicket();
                    return true;
                }
                gotolistChiByTicket(888);
                break;
            case R.id.nav_DiemDD:
                if (!CHO_PHEP_TRUY_CAP){
                    gotoSetting();
                    return true;
                }
                if (!LOGIN_NAME.equals(null)){
                    intent = new Intent(NavDrawerActivity.this,DiemDDActivity.class);
                    intent.putExtra("userName", LOGIN_NAME);
                    intent.putExtra("rkeyTicket", RKEY_TICKET);
                    NavDrawerActivity.this.startActivity(intent);
                }else{
                    Toast.makeText(NavDrawerActivity.this, "Chưa đủ dữ liệu", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_DMHS:
                intent = new Intent(NavDrawerActivity.this,DMHaiSanActivity.class);
                intent.putExtra("userName", LOGIN_NAME);
                intent.putExtra("rkeyTicket", RKEY_TICKET);
                NavDrawerActivity.this.startActivity(intent);
                break;
            case R.id.nav_users:
                if (!CHO_PHEP_TRUY_CAP){
                    gotoSetting();
                    return true;
                }
                if (WHO_START!="admin"){
                    Toast.makeText(this, "Authorized personnel only...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                intent = new Intent(NavDrawerActivity.this,UserActivity.class);
                NavDrawerActivity.this.startActivity(intent);
                break;
            case R.id.nav_Setting:
                gotoSetting();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
    private void gotolistChiByTicket(int startFlag) {
        ArrayList<Chi> arrChi=new ArrayList<>();
        ArrayList<DebtBook> arrDebtBook=new ArrayList<>();
        Intent intent;
        arrChi=crudLocaldb.Chi_getChiByTicket(RKEY_TICKET);
        arrDebtBook=crudLocaldb.DebtBook_getDebtBookByTicket(RKEY_TICKET);
        intent = new Intent(NavDrawerActivity.this, ChiByTicketActivity.class);
        intent.putExtra("rkeyTicket", rkeyTicket);
        intent.putExtra("userName", LOGIN_NAME);
        intent.putExtra("startFlag",startFlag);
        startActivity(intent);
//        if (arrDebtBook.size()>0 || arrChi.size()>0){
//
//        }else{
//            Toast.makeText(this, "Chưa có chi tiết phát sinh", Toast.LENGTH_SHORT).show();
//        }
    }
    private void gotoThuAsThuyenTruong(){
        long rkeyChuyenBien=0;
        ArrayList<ChuyenBien> arrChuyenBien =crudLocaldb.ChuyenBien_getChuyenBienByShipMaster(LOGIN_NAME);
        rkeyChuyenBien=arrChuyenBien.get(arrChuyenBien.size()-1).getRkey();

        long tongthu=longGet(arrChuyenBien.get(arrChuyenBien.size()-1).getTongthu()+"");
        if (tongthu==0){
            Toast.makeText(NavDrawerActivity.this, "Chưa phát sinh khoản thu nào...", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(NavDrawerActivity.this, ThuByChuyenBienActivity.class);
            intent.putExtra("rkeyChuyenBien",rkeyChuyenBien);
            intent.putExtra("userName", LOGIN_NAME);
            intent.putExtra("rkeyTicket", rkeyTicket);
            startActivity(intent);
        }
    }
    private void gotoChiAsThuyenTruong(){
        long rkeyChuyenBien=0;
        ArrayList<ChuyenBien> arrChuyenBien =crudLocaldb.ChuyenBien_getChuyenBienByShipMaster(LOGIN_NAME);
        rkeyChuyenBien=arrChuyenBien.get(arrChuyenBien.size()-1).getRkey();

        long tongchi=longGet(arrChuyenBien.get(arrChuyenBien.size()-1).getTongchi()+"");
        if (tongchi==0){
            Toast.makeText(NavDrawerActivity.this, "Chưa phát sinh khoản chi nào...", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(NavDrawerActivity.this, ChiByChuyenBienActivity.class);
            intent.putExtra("rkeyChuyenBien",rkeyChuyenBien);
            intent.putExtra("userName", LOGIN_NAME);
            intent.putExtra("rkeyTicket", rkeyTicket);
            startActivity(intent);
        }
    }
    private Boolean isThuyenTruong(){
        Boolean is =false;
        ArrayList<ChuyenBien>arrChuyenBien=new ArrayList<>();
        arrChuyenBien=crudLocaldb.ChuyenBien_getOnlyShowChuyenBien();
        for(int i=0;i<arrChuyenBien.size();i++){
            if (arrChuyenBien.get(i).getUsername().equals(LOGIN_NAME)){
                is=true;
                break;
            }
        }
        return is;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SETTING ) {
            boolean needRefresh = data.getBooleanExtra("needRefresh",true);
            // Refresh ListView
            if(needRefresh) {
                getPre();
                syncImgStoreImageFileByDate();
                initdata();
            }
        }
    }

    private void syncImgStoreImageFileByDate(){
        //viec download nam ngoai kiem soat nodejs server, co url la down thoi
        //donwload image file from server ******************************************************
        String BASE_URL = MY_SERVER + "/dqpclient/imgstore";
        ArrayList<ImgStore> arrImgStore=new ArrayList<>();
        ImgStore imgstore=new ImgStore();
        arrImgStore = crudLocaldb.ImgStore_getAllImgStore();
        if (arrImgStore.size()>0){
            imgstore = new ImgStore();
            for (int i = 0; i < arrImgStore.size(); i++) {
                imgstore=arrImgStore.get(i);
                String s1="";
                String s2="";
                if (!isBad(imgstore.getImgpath())){
                    String [] arrImglocal=imgstore.getImgpath().split("/");
                    s1=arrImglocal[arrImglocal.length-1];
                    s2=imgstore.getImgpath();
                }
                final String imglocal=s1;
                final String imglocalPath=s2;
                final int finalI=i;
                if (longGet(imgstore.getUpdatetime())>=NGAY_LUU_ANH){
                    restfullAPI.getImgStore  getImgStoreTask = new restfullAPI.getImgStore();
                    getImgStoreTask.setUpdateListener(new restfullAPI.getImgStore.OnUpdateListener(){
                        @Override
                        public void onUpdate(ArrayList<ImgStore> obj) throws ExecutionException, InterruptedException {
                            if (obj==null) {return;}
                            ArrayList<ImgStore> arrServerImgStore=new ArrayList<>();
                            arrServerImgStore=obj;
                            if (arrServerImgStore.size()>0){
                                if (!isBad(arrServerImgStore.get(0).getImgpath())){
                                    //xet truong hop da dowwn roi thi bo qua
                                    String [] arrImgserver=arrServerImgStore.get(0).getImgpath().split("/");
                                    String imgserver=arrImgserver[arrImgserver.length-1];
                                    if (!imglocalPath.equals(null) && !imglocalPath.equals("")){
                                        if (imglocal.equals(imgserver) && !imglocalPath.substring(0,4).equals("http")){
                                            return;
                                        }
                                    }
                                    final String imageUrl=arrServerImgStore.get(0).getImgpath();
                                    ArrayList<ImgStore> arrImgStore=new ArrayList<>();
                                    arrImgStore=crudLocaldb.ImgStore_getAllImgStore();
                                    final ImgStore imgstore=arrImgStore.get(finalI);
                                    String device_dir=getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
                                    restfullAPI.downloadFile  downloadtask = new restfullAPI.downloadFile();
                                    downloadtask.setUpdateListener(new restfullAPI.downloadFile.OnUpdateListener() {
                                        @Override
                                        public void onUpdate(String realpath) throws ExecutionException, InterruptedException {
                                            imgstore.setImgpath(realpath);
                                            int u=crudLocaldb.ImgStore_updateImgStore(imgstore);
                                            if (u!=0){
                                                Log.d(TAG, "syncImgStoreImageFileByDate---------Fetch images from server sucessfully: "+ realpath);
                                            }
                                        }
                                    });
                                    downloadtask.execute(new String[]{imageUrl,device_dir});
                                }

                            }
                        }
                    });
                    getImgStoreTask.execute(BASE_URL+"/read/" + imgstore.getServerkey()+ "/");

                } else {
                    restfullAPI.getImgStore  getImgStoreTask = new restfullAPI.getImgStore();
                    getImgStoreTask.setUpdateListener(new restfullAPI.getImgStore.OnUpdateListener(){
                        @Override
                        public void onUpdate(ArrayList<ImgStore> obj) throws ExecutionException, InterruptedException {
                            if (obj==null) {return;}
                            ArrayList<ImgStore> arrServerImgStore=new ArrayList<>();
                            arrServerImgStore=obj;
                            if (arrServerImgStore.size()>0){
                                if (!isBad(arrServerImgStore.get(0).getImgpath())){
                                    String [] arrImgserver=arrServerImgStore.get(0).getImgpath().split("/");
                                    String imgserver=arrImgserver[arrImgserver.length-1];
                                    if (!imglocalPath.equals(null) && !imglocalPath.equals("")){
                                        if (imglocal.equals(imgserver) && imglocalPath.substring(0,4).equals("http")){
                                            return;
                                        }
                                    }
                                    final String imageUrl=arrServerImgStore.get(0).getImgpath();
                                    ArrayList<ImgStore> arrImgStore=new ArrayList<>();
                                    arrImgStore=crudLocaldb.ImgStore_getAllImgStore();
                                    final ImgStore imgstore=arrImgStore.get(finalI);
                                    String s=imgstore.getImgpath();
                                    if (s!=null){
                                        File file=new File(s);
                                        if (file.exists()) {
                                            file.delete();
                                        }
                                    }
                                    imgstore.setImgpath(imageUrl);
                                    int u=crudLocaldb.ImgStore_updateImgStore(imgstore);
                                    if (u!=0){
                                        Log.d(TAG, "syncImgStoreImageFileByDate:--- delete local: "+ s + " --- use server link: " + imageUrl);
                                    }
                                }
                            }
                        }
                    });
                    getImgStoreTask.execute(BASE_URL+"/read/" + imgstore.getServerkey()+ "/");
                }
            }

        }
    }

    @Override
    public void finish() {
        requestInstantSync("wantPost");
        addSyncAdapterAsPeriodic();
        super.finish();
    }
    private void requestInstantSync(String onCommand) {
        final String STRING_AUTHORITY = "com.dqpvn.dqpclient.syncAdapter.StubProvider";
        // The account name
        final String STRING_ACCOUNT = "PeriodicSync";
        Account mAccount = CreateSyncAccount(this, STRING_ACCOUNT);
        //do sync intantly, // Perform a manual sync by calling this:
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        settingsBundle.putString("onCommand", onCommand);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.setIsSyncable(mAccount, STRING_AUTHORITY, 1);
        ContentResolver.requestSync(mAccount, STRING_AUTHORITY, settingsBundle);
    }
    private void addSyncAdapterAsPeriodic(){
        final String STRING_AUTHORITY = "com.dqpvn.dqpclient.syncAdapter.StubProvider";
        final String STRING_ACCOUNT = "PeriodicSync";
        // Sync interval constants
        final long SECONDS_PER_MINUTE = 60L;
        final long SYNC_INTERVAL_IN_MINUTES = 30L;
        final long SYNC_INTERVAL =
                SYNC_INTERVAL_IN_MINUTES *
                        SECONDS_PER_MINUTE;
        //create SyncAccount if not exits otherise get it
        Account mAccount=CreateSyncAccount(this,STRING_ACCOUNT);
        ContentResolver.setIsSyncable(mAccount, STRING_AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(mAccount, STRING_AUTHORITY, true);
        // Schedule periodic sync based on current configuration
        ContentResolver.addPeriodicSync(mAccount, STRING_AUTHORITY, new Bundle(), SYNC_INTERVAL);
    }
    public static Account CreateSyncAccount(Context context, String STRING_ACCOUNT_NAME) {
        final String STRING_ACCOUNT_TYPE = "dqpvn.com-DQPCLIENT";
        // Create the account type and default account
        Account newAccount = new Account(
                STRING_ACCOUNT_NAME, STRING_ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }
}
