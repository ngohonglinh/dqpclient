package com.dqpvn.dqpclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;


import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.customadapters.CustomAdapterChiByTicket;
import com.dqpvn.dqpclient.customadapters.CustomAdapterDebtBook;
import com.dqpvn.dqpclient.models.Chi;
import com.dqpvn.dqpclient.models.ChiByTicket;
import com.dqpvn.dqpclient.models.ChuyenBien;
import com.dqpvn.dqpclient.models.DebtBook;
import com.dqpvn.dqpclient.models.Ticket;
import com.dqpvn.dqpclient.utils.MyContextWrapper;
import com.dqpvn.dqpclient.utils.utils;

import java.util.ArrayList;

import static com.dqpvn.dqpclient.crudmanager.SyncCheck.IS_ADMIN;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_NAME;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.RKEY_TICKET;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.WHO_START;


public class ChiByTicketActivity extends AppCompatActivity {

    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;


    final private String TAG= getClass().getSimpleName();
    private ListView lvChiByTicket, lvDebtBookByTicket;
    private LinearLayout lyChiByTicket, lyDebtBookByTicket;
    private boolean needRefresh;
    private final int REQUEST_START_CHI=112;
    private final int REQUEST_START_DEBTBOOK=123;
    private long intentRkeyTicket;
    private String intentUserName;
    private int startFlag, whereTouch=0;
    //database
    private crudLocal crudLocaldb=crudLocal.getInstance(this);

    //Khai báo Datasource lưu trữ danh sách ticket
    private ArrayList<ChiByTicket> arrChiByTicket=new ArrayList<ChiByTicket>();
    private ArrayList<ChiByTicket>customadapterChiData=new ArrayList<>();
    private ArrayList<DebtBook> arrDebtBook=new ArrayList<>();
    private ArrayList<DebtBook>customadapterDebtBookData=new ArrayList<>();
    //Khai báo ArrayAdapter cho ListView
    private CustomAdapterDebtBook customAdapterDebtbook;
    private CustomAdapterChiByTicket customAdapterChi;

    //lam viec voi menu
    @Override
    public boolean onCreateOptionsMenu(Menu manu) {
        getMenuInflater().inflate(R.menu.thuchi_menu, manu);
        Intent intent=getIntent();
        MenuItem mAdd = manu.findItem(R.id.id_new);
        if (intent.hasExtra("startFlag")){
            startFlag=intent.getIntExtra("startFlag",0);
            if (startFlag==888){
                mAdd.setVisible(true);
            }else{
                if (WHO_START=="thuyenTruong" && !IS_ADMIN){
                    mAdd.setVisible(false);
                }else{
                    mAdd.setVisible(true);
                }
            }

        }else{
            mAdd.setVisible(false);
        }
        // return true so that the menu pop up is opened
        return true;
    }

    // Method này sử lý sự kiện khi MenuItem được chọn.
    @Override
    public boolean onOptionsItemSelected(MenuItem item)  {

        int itemId = item.getItemId();

        switch(itemId)  {
            //ten cua cac id khi thiet ke cac resource menu
            case R.id.id_new :
                if (startFlag==333){
                    Intent intent = new Intent(ChiByTicketActivity.this,ChiActivity.class);
                    intent.putExtra("rkeyTicket", RKEY_TICKET);
                    intent.putExtra("userName", LOGIN_NAME);
                    intent.putExtra("makeNew",true);
                    startActivityForResult(intent,REQUEST_START_CHI);
                }else if (startFlag==888){
                    Intent intent=new Intent(ChiByTicketActivity.this, DebtBookActivity.class);
                    long rkeyChuyenBien=crudLocaldb.ChuyenBien_getRkeyByShipmaster(LOGIN_NAME);
                    if (LOGIN_NAME.substring(0,5).equals("admin")){
                        intent.putExtra("whoStart","admin");
                    }else if(isThuyenTruong()){
                        intent.putExtra("whoStart","thuyenTruong");
                        intent.putExtra("rkeyChuyenBien", rkeyChuyenBien);
                    }else{
                        intent.putExtra("whoStart","linhNha");
                    }
                    intent.putExtra("makeNew",true);
                    intent.putExtra("rkeyTicket", RKEY_TICKET);
                    intent.putExtra("userName", LOGIN_NAME);
                    startActivityForResult(intent,REQUEST_START_DEBTBOOK);
                }

                return true;
            case R.id.id_exit:
                Intent ExitIntent=new Intent(getApplicationContext(),NavDrawerActivity.class);
                ExitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ExitIntent.putExtra("EXIT", true);
                startActivity(ExitIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_by_ticket);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);

        addControls();
        initialization();
        MakeChiByTicket();
        MakeDebtBookByTicket();
        setAdapter();
        addEvents();
        MakeLayout();
    }
    private void MakeLayout(){
        Intent intent=getIntent();
        //make layout
        LinearLayout.LayoutParams paramsChi = (LinearLayout.LayoutParams)
                lyChiByTicket.getLayoutParams();
        paramsChi.weight = 5.0f;
        LinearLayout.LayoutParams paramsDebt = (LinearLayout.LayoutParams)
                lyDebtBookByTicket.getLayoutParams();
        paramsDebt.weight = 5.0f;

        if (intent.hasExtra("startFlag")){
            startFlag=intent.getIntExtra("startFlag",0);
            if (startFlag==333){
                paramsDebt.weight = 0.0f;
                paramsChi.weight = 10.0f;
            }else if (startFlag==888){
                paramsDebt.weight = 10.0f;
                paramsChi.weight = 0.0f;
            }
        }else{
            if (arrDebtBook.size()>0 && arrChiByTicket.size()<=0){
                paramsDebt.weight = 10.0f;
                paramsChi.weight = 0.0f;
            }else if (arrDebtBook.size()<=0 && arrChiByTicket.size()>0){
                paramsDebt.weight = 0.0f;
                paramsChi.weight = 10.0f;

            }else if (arrDebtBook.size()>0 && arrChiByTicket.size()>0){
                paramsDebt.weight = 5.0f;
                paramsChi.weight = 5.0f;
            }
        }
        lyDebtBookByTicket.setLayoutParams(paramsDebt);
        lyChiByTicket.setLayoutParams(paramsChi);
    }

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap");
            return true;
        }
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(TAG, "onScroll");
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(TAG, "onLongPress");
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                int debtPos=lvDebtBookByTicket.pointToPosition( (int) e1.getX(), (int) e1.getY() );
                int chiPos=lvChiByTicket.pointToPosition( (int) e1.getX(), (int) e1.getY() );

                if (debtPos>=0 && whereTouch==2){
                    if (arrDebtBook.size()>0){
                        DebtBook debtbook = arrDebtBook.get(debtPos);
                        String TenChuyenBien=debtbook.getChuyenbien();
                        Intent intent=new Intent(ChiByTicketActivity.this, DebBookDetailActivity.class);
                        intent.putExtra("rkeyThuyenVien", debtbook.getRkeythuyenvien());
                        intent.putExtra("tenChuyenBien", TenChuyenBien);
                        intent.putExtra("rkeyChuyenBien", crudLocaldb.ChuyenBien_getRkeyChuyenBien(TenChuyenBien));
                        intent.putExtra("rkeyTicket", intentRkeyTicket);
                        intent.putExtra("userName", intentUserName);
                        if (isThuyenTruong()){
                            intent.putExtra("whoStart","thuyenTruong");
                        }else{
                            intent.putExtra("whoStart","linhNha");
                        }
                        startActivityForResult(intent, REQUEST_START_DEBTBOOK);
                    }
                }else if (chiPos>=0 && whereTouch==1){
                    ChiByTicket chibyticket = arrChiByTicket.get(chiPos);
                    Intent intent = new Intent(ChiByTicketActivity.this, ChiActivity.class);
                    intent.putExtra("chiRkey",chibyticket.getmRkeyChi());
                    intent.putExtra("rkeyTicket",intentRkeyTicket);
                    intent.putExtra("userName",intentUserName);
                    if (isThuyenTruong()){
                        intent.putExtra("whoStart","thuyenTruong");
                    }else{
                        intent.putExtra("whoStart","linhNha");
                    }
                    startActivityForResult(intent,REQUEST_START_CHI);
                }

                Log.d(TAG, "Right to Left swipe performed");
            }
            // left to right swipe
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                finish();
                Log.d(TAG, "Left to Right swipe performed");
            }

            // Down to Up swipe performed
            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                Log.d(TAG, "Down to Up swipe performed");
            }
            // Up to Down swipe performed
            else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                
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

    private void addControls() {
        lvChiByTicket=findViewById(R.id.lv_ChiByTicket);
        lvDebtBookByTicket=findViewById(R.id.lv_DebtBookByTicket);
        lyChiByTicket=findViewById(R.id.ly_ChiByTicket);
        lyDebtBookByTicket=findViewById(R.id.ly_DebtBookByTicket);
    }

    private void initialization() {
        Intent intent = getIntent();
        intentRkeyTicket = intent.getLongExtra("rkeyTicket", 0);
        intentUserName=intent.getStringExtra("userName");

        String mName=utils.getStringLeft(crudLocaldb.Ticket_getTicketByRkey(intentRkeyTicket).get(0).getUsername(),"@");
        String mDate=crudLocaldb.Ticket_getTicketByRkey(intentRkeyTicket).get(0).getOpendate();
        setTitle(mName+"'s ticket " + mDate);
    }

    private void MakeChiByTicket(){
        arrChiByTicket.clear();
        ArrayList<Chi> arrChi=new ArrayList<>();
        arrChi=crudLocaldb.Chi_getChiByTicket(intentRkeyTicket);
        if (arrChi.size()>=1){
            Chi chi =new Chi();
            for (int i=0;i<arrChi.size();i++){
                chi=arrChi.get(i);
                ChiByTicket chibyticket=new ChiByTicket();
                chibyticket.setmRkeyChi(chi.getRkey());
                chibyticket.setmDoitac(crudLocaldb.DoiTac_getTenDoiTac(chi.getRkeydoitac()));
                chibyticket.setmChuyenbien(crudLocaldb.ChuyenBien_getTenChuyenBien(chi.getRkeychuyenbien()));
                chibyticket.setmLydo(chi.getLydo());
                chibyticket.setmNgayPS(chi.getNgayps());
                chibyticket.setmGiaTri(chi.getGiatri());
                chibyticket.setmDaTra(chi.getDatra());
                arrChiByTicket.add(chibyticket);
            }
            customadapterChiData.addAll(arrChiByTicket);
            ArrayList<Chi> arr=crudLocaldb.Chi_getAllChi();
            Log.d(TAG, "MakeChiByTicket: -----------------" +arr.size() + "***************" + customadapterChiData.size());
        }
    }

    private void MakeDebtBookByTicket(){
        arrDebtBook.clear();
        arrDebtBook=crudLocaldb.DebtBook_getDebtBookByTicket(intentRkeyTicket);
        customadapterDebtBookData.addAll(arrDebtBook);
    }

    private void addEvents() {

        lvDebtBookByTicket.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                whereTouch=2;
                mGestureDetector.onTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
            }
        });
        lvChiByTicket.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                whereTouch=1;
                mGestureDetector.onTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_START_CHI ) {
            needRefresh = data.getBooleanExtra("needRefresh",false);
            // Refresh ListView
            if(needRefresh) {
                //da co thay doi du lieu tu chi gan gia trị de thong bao cho DoiTac bit khi quay lai no
                needRefresh=true;
                MakeChiByTicket();
                if (arrChiByTicket.size()==0 && arrDebtBook.size()==0){
                    this.finish();
                }
                // Thông báo dữ liệu thay đổi (Để refresh ListView).
                updatelistChiByTicket();
            }else{
                needRefresh=false;
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_START_DEBTBOOK ) {
            needRefresh = data.getBooleanExtra("needRefresh",false);
            // Refresh ListView
            if(needRefresh) {
                //da co thay doi du lieu tu chi gan gia trị de thong bao cho DoiTac bit khi quay lai no
                needRefresh=true;
                MakeDebtBookByTicket();
                if (arrChiByTicket.size()==0 && arrDebtBook.size()==0){
                    this.finish();
                }
                // Thông báo dữ liệu thay đổi (Để refresh ListView).
                updatelistDebtBookByTicket();
            }else{
                needRefresh=false;
            }
        }
        if (resultCode == Activity.RESULT_OK ){
            if (arrChiByTicket.size()<=0){
                lvChiByTicket.setVisibility(View.GONE);
            }else{
                lvChiByTicket.setVisibility(View.VISIBLE);
            }
            if (arrDebtBook.size()<=0){
                lvDebtBookByTicket.setVisibility(View.GONE);
            }else{
                lvDebtBookByTicket.setVisibility(View.VISIBLE);
            }
        }
    }

    private Boolean isThuyenTruong(){
        Boolean is =false;
        ArrayList<ChuyenBien>arrChuyenBien=new ArrayList<>();
        arrChuyenBien=crudLocaldb.ChuyenBien_getOnlyShowChuyenBien();
        for(int i=0;i<arrChuyenBien.size();i++){
            if (arrChuyenBien.get(i).getUsername().equals(intentUserName)){
                is=true;
                break;
            }
        }
        return is;
    }

    private void setAdapter() {
        if (customAdapterChi == null) {
            // gan data source cho adapter
            customAdapterChi = new CustomAdapterChiByTicket(this, R.layout.customlist_chibyticket, customadapterChiData);
            //gan adapter cho spinner
            lvChiByTicket.setAdapter(customAdapterChi);
        }else{
            updatelistChiByTicket();
            //cho troi xg record duoi cung
        }

        if (customAdapterDebtbook == null) {
            // gan data source cho adapter
            customAdapterDebtbook = new CustomAdapterDebtBook(this, R.layout.customlist_debtbook,customadapterDebtBookData);
            //gan adapter cho spinner
            lvDebtBookByTicket.setAdapter(customAdapterDebtbook);
        }else{
            updatelistDebtBookByTicket();
        }
        lvChiByTicket.setSelection(customAdapterChi.getCount()-1);
        lvDebtBookByTicket.setSelection(customAdapterDebtbook.getCount()-1);
    }
    private  void updatelistChiByTicket(){
        customadapterChiData.clear();
        customadapterChiData.addAll(arrChiByTicket);
        customAdapterChi.notifyDataSetChanged();
        lvChiByTicket.setSelection(customAdapterChi.getCount()-1);
    }

    private  void updatelistDebtBookByTicket(){
        customadapterDebtBookData.clear();
        customadapterDebtBookData.addAll(arrDebtBook);
        customAdapterDebtbook.notifyDataSetChanged();
        lvDebtBookByTicket.setSelection(customAdapterDebtbook.getCount()-1);
    }

    // Khi Activity này hoàn thành,
    // có thể cần gửi phản hồi gì đó về cho Activity đã gọi nó.
    @Override
    public void finish() {
        // Chuẩn bị dữ liệu Intent.
        Intent data = new Intent();
        // Yêu cầu MainActivity refresh lại ListView hoặc không.
        data.putExtra("needRefresh", needRefresh);
        // Activity đã hoàn thành OK, trả về dữ liệu.
        this.setResult(Activity.RESULT_OK, data);
        super.finish();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            super.attachBaseContext(MyContextWrapper.wrap(newBase, "en"));
        }
        else {
            super.attachBaseContext(newBase);
        }
    }
}
