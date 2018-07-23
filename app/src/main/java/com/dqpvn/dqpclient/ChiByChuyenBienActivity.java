package com.dqpvn.dqpclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.dqpvn.dqpclient.crudmanager.SyncCheck;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.customadapters.CustomAdapterChiByChuyenBien;
import com.dqpvn.dqpclient.models.Chi;
import com.dqpvn.dqpclient.models.ChiByChuyenBien;
import com.dqpvn.dqpclient.models.ChuyenBien;
import com.dqpvn.dqpclient.models.Ticket;
import com.dqpvn.dqpclient.utils.MyContextWrapper;
import com.dqpvn.dqpclient.utils.utils;

import java.util.ArrayList;

import static com.dqpvn.dqpclient.crudmanager.SyncCheck.IS_ADMIN;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_NAME;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.RKEY_TICKET;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.WHO_START;
import static com.dqpvn.dqpclient.utils.utils.longGet;

public class ChiByChuyenBienActivity extends AppCompatActivity {

    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;


    final private String TAG= getClass().getSimpleName();
    private ListView lvChiByChuyenBien;
    private boolean needRefresh=false,daChia=false;
    private final int REQUEST_START_CHI=112;
    private long intentIDChuyenBien;
    private String intentUserName;
    //database
    private crudLocal crudLocaldb=crudLocal.getInstance(this);

    //Khai báo Datasource lưu trữ danh sách chuyenbien
    private ArrayList<ChiByChuyenBien> arrChiByChuyenBien=new ArrayList<ChiByChuyenBien>();
    private ArrayList<ChiByChuyenBien>customadapterData=new ArrayList<>();
    //Khai báo ArrayAdapter cho ListView
    private CustomAdapterChiByChuyenBien customAdapter;

    //lam viec voi menu
    @Override
    public boolean onCreateOptionsMenu(Menu manu) {
        getMenuInflater().inflate(R.menu.thuchi_menu, manu);

        if (daChia){
            for (int i = 0; i < manu.size(); i++){
                manu.getItem(i).setVisible(false);
            }
            return true;
        }

        MenuItem mAdd = manu.findItem(R.id.id_new);
        if (WHO_START=="thuyenTruong" && !IS_ADMIN){
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
                String tenChuyenBien=crudLocaldb.ChuyenBien_getTenChuyenBien(intentIDChuyenBien);
                //find right Ticket....
                ArrayList<Ticket>arrTicket=new ArrayList<>();
                arrTicket=crudLocaldb.Ticket_getOpenTicketByUser(intentUserName);
                long rkeyTicket=0;
                for (int i=0;i<arrTicket.size();i++){
                    if (arrTicket.get(i).getFinished()==0){
                        rkeyTicket=arrTicket.get(i).getRkey();
                        break;
                    }
                }
                Intent intent = new Intent(ChiByChuyenBienActivity.this,ChiActivity.class);
                intent.putExtra("rkeyTicket", rkeyTicket);
                intent.putExtra("userName", intentUserName);
                intent.putExtra("tenChuyenBien", tenChuyenBien);
                intent.putExtra("makeNew",true);
                startActivityForResult(intent,REQUEST_START_CHI);
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
        setContentView(R.layout.activity_chi_by_chuyen_bien);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);

        initialization();
        addControls();
        MakeChiByChuyeBien();
        setAdapter();
        addEvents();
    }

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener{
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

                int position=lvChiByChuyenBien.pointToPosition( (int) e1.getX(), (int) e1.getY() );
                if (position<0){return true ;}
                if (arrChiByChuyenBien.size()>=1){
                    ChiByChuyenBien chibychuyenbien = arrChiByChuyenBien.get(position);
                    Intent intent = new Intent(ChiByChuyenBienActivity.this, ChiActivity.class);
                    intent.putExtra("chiRkey",chibychuyenbien.getmRkeyChi());
                    intent.putExtra("userName",intentUserName);
                    intent.putExtra("rkeyTicket", RKEY_TICKET);
                    intent.putExtra("daChia",daChia);
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
        lvChiByChuyenBien=findViewById(R.id.lv_ChiByChuyenBien);
    }

    private void initialization() {
        Intent intent = getIntent();
        intentIDChuyenBien = intent.getLongExtra("rkeyChuyenBien", 0);
        intentUserName=intent.getStringExtra("userName");
        daChia=intent.getBooleanExtra("daChia",false);
        String tenChuyenBien=crudLocaldb.ChuyenBien_getTenChuyenBien(intentIDChuyenBien);
        setTitle(utils.getStringLeft(tenChuyenBien,"@") +" | Sở phí");
    }

    private void MakeChiByChuyeBien(){
        arrChiByChuyenBien.clear();
        ArrayList<Chi> arrChi=new ArrayList<>();
        arrChi=crudLocaldb.Chi_getChiByChuyenBien(intentIDChuyenBien);
        if (arrChi.size()>=1){
            //crudLocaldb.deleteAllChiByChuyenBien();
            Chi chi =new Chi();
            for (int i=0;i<arrChi.size();i++){
                chi=arrChi.get(i);
                ChiByChuyenBien chibychuyenbien=new ChiByChuyenBien();
                chibychuyenbien.setmRkeyChi(chi.getRkey());
                chibychuyenbien.setmDoiTac(crudLocaldb.DoiTac_getTenDoiTac(chi.getRkeydoitac()));
                chibychuyenbien.setmLydo(chi.getLydo());
                chibychuyenbien.setmNgayPS(utils.DinhDangNgay(chi.getNgayps(),"dd/mm/yyyy"));
                chibychuyenbien.setmGiaTri(chi.getGiatri());
                chibychuyenbien.setmDaTra(chi.getDatra());

                long conlai=longGet(chi.getGiatri())-longGet(chi.getDatra());

                chibychuyenbien.setmConLai(String.valueOf(conlai));

                arrChiByChuyenBien.add(chibychuyenbien);
            }
            customadapterData.addAll(arrChiByChuyenBien);
        }

    }

    private void addEvents() {

        lvChiByChuyenBien.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mGestureDetector.onTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_START_CHI ) {
            boolean needRefresh = data.getBooleanExtra("needRefresh",false);
            // Refresh ListView
            if(needRefresh) {
                this.needRefresh=true;
                MakeChiByChuyeBien();
                if (arrChiByChuyenBien.size()==0){
                    this.finish();
                }
                // Thông báo dữ liệu thay đổi (Để refresh ListView).
                updatelistChiByChuyenBien();
                //da co thay doi du lieu tu chi gan gia trị de thong bao cho DoiTac bit khi quay lai no
            }
        }
    }

    private void setAdapter() {
        if (customAdapter == null) {
            // gan data source cho adapter
            customAdapter = new CustomAdapterChiByChuyenBien(this, R.layout.customlist_thuchibychuyenbien, customadapterData);
            //gan adapter cho spinner
            lvChiByChuyenBien.setAdapter(customAdapter);
        }else{
            updatelistChiByChuyenBien();
            //cho troi xg record duoi cung
            lvChiByChuyenBien.setSelection(customAdapter.getCount()-1);
        }
    }
    private  void updatelistChiByChuyenBien(){
        customadapterData.clear();
        customadapterData.addAll(arrChiByChuyenBien);
        customAdapter.notifyDataSetChanged();
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

    // Khi Activity này hoàn thành,
    // có thể cần gửi phản hồi gì đó về cho Activity đã gọi nó.
    @Override
    public void finish() {
        Intent data = new Intent();
        if (this.needRefresh){
            data.putExtra("needRefresh", this.needRefresh);
        }else{
            data.putExtra("needRefresh", false);
        }
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
