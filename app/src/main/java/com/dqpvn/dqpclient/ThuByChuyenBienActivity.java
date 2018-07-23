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
import android.widget.ListView;
import android.widget.Toast;

import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.customadapters.CustomAdapterThuByChuyenBien;
import com.dqpvn.dqpclient.models.Thu;
import com.dqpvn.dqpclient.models.ThuByChuyenBien;
import com.dqpvn.dqpclient.utils.MyContextWrapper;
import com.dqpvn.dqpclient.utils.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;


import static com.dqpvn.dqpclient.crudmanager.SyncCheck.IS_ADMIN;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.WHO_START;
import static com.dqpvn.dqpclient.utils.utils.longGet;

public class ThuByChuyenBienActivity extends AppCompatActivity {

    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;


    final private String TAG= getClass().getSimpleName();
    private ListView lvThuByChuyenBien;
    private boolean needRefresh=false,daChia=false;
    private final int REQUEST_START_THU=111;
    private long intentIDChuyenBien;
    private String intentUserName;
    crudLocal crudLocaldb=crudLocal.getInstance(this);
    //database

    //Khai báo Datasource lưu trữ danh sách chuyenbien
    private ArrayList<ThuByChuyenBien> arrThuByChuyenBien=new ArrayList<ThuByChuyenBien>();
    private ArrayList<ThuByChuyenBien>customadapterData=new ArrayList<>();
    //Khai báo ArrayAdapter cho ListView
    private CustomAdapterThuByChuyenBien customAdapter;

    //lam viec voi menu
    @Override
    public boolean onCreateOptionsMenu(Menu manu) {
        getMenuInflater().inflate(R.menu.thuchi_menu, manu);
        MenuItem mAdd = manu.findItem(R.id.id_new);
        if (daChia){
            for (int i = 0; i < manu.size(); i++){
                manu.getItem(i).setVisible(false);
            }
            return true;
        }
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
                Intent intent = new Intent(ThuByChuyenBienActivity.this,ThuActivity.class);
                intent.putExtra("userName", intentUserName);
                intent.putExtra("tenChuyenBien", tenChuyenBien);
                intent.putExtra("makeNew",true);
                startActivityForResult(intent,REQUEST_START_THU);
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
        setContentView(R.layout.activity_thu_by_chuyen_bien);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);

        initialization();
        addControls();
        MakeThuByChuyeBien();
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

                int position=lvThuByChuyenBien.pointToPosition( (int) e1.getX(), (int) e1.getY() );
                if (position<0){return true ;}
                if (arrThuByChuyenBien.size()>=1){
                    ThuByChuyenBien thubychuyenbien = arrThuByChuyenBien.get(position);
                    Intent intent = new Intent(ThuByChuyenBienActivity.this, ThuActivity.class);
                    intent.putExtra("thuRkey",thubychuyenbien.getmRkeyThu());
                    intent.putExtra("userName",intentUserName);
                    intent.putExtra("daChia",daChia);
                    startActivityForResult(intent,REQUEST_START_THU);
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
        lvThuByChuyenBien=findViewById(R.id.lv_ThuByChuyenBien);
    }

    private void initialization() {
        Intent intent = getIntent();
        intentIDChuyenBien = intent.getLongExtra("rkeyChuyenBien", 0);
        intentUserName=intent.getStringExtra("userName");
        daChia=intent.getBooleanExtra("daChia",false);
        String tenChuyenBien=crudLocaldb.ChuyenBien_getTenChuyenBien(intentIDChuyenBien);
        setTitle(utils.getStringLeft(tenChuyenBien,"@") +" | Doanh thu");
    }

    private void MakeThuByChuyeBien(){
        arrThuByChuyenBien.clear();
        ArrayList<Thu> arrThu=new ArrayList<>();
        crudLocal crudLocaldb=crudLocal.getInstance(this);
        arrThu=crudLocaldb.Thu_getThuByChuyenBien(intentIDChuyenBien);
        if (arrThu.size()>=1){
            //crudLocaldb.deleteAllThuByChuyenBien();
            Thu thu =new Thu();
            for (int i=0;i<arrThu.size();i++){
                thu=arrThu.get(i);
                ThuByChuyenBien thubychuyenbien=new ThuByChuyenBien();
                thubychuyenbien.setmRkeyThu(thu.getRkey());
                thubychuyenbien.setmKhachHang(crudLocaldb.KhachHang_getTenKhachHang(thu.getRkeykhachhang()));
                thubychuyenbien.setmLydo(thu.getLydo());
                thubychuyenbien.setmNgayPS(utils.DinhDangNgay(thu.getNgayps(),"dd/mm/yyyy"));
                thubychuyenbien.setmGiaTri(thu.getGiatri());
                thubychuyenbien.setmDaTra(thu.getDatra());

                long conlai=longGet(thu.getGiatri())-longGet(thu.getDatra());

                thubychuyenbien.setmConLai(String.valueOf(conlai));

                arrThuByChuyenBien.add(thubychuyenbien);
            }
            customadapterData.addAll(arrThuByChuyenBien);
        }

    }

    private void addEvents() {

        lvThuByChuyenBien.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mGestureDetector.onTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_START_THU ) {
            boolean needRefresh = data.getBooleanExtra("needRefresh",true);
            // Refresh ListView
            if(needRefresh) {
                this.needRefresh=true;
                MakeThuByChuyeBien();
                if (arrThuByChuyenBien.size()==0){
                    this.finish();
                }
                // Thông báo dữ liệu thay đổi (Để refresh ListView).
                updatelistThuByChuyenBien();
                //da co thay doi du lieu tu chi gan gia trị de thong bao cho DoiTac bit khi quay lai no
            }
        }
    }

    private void setAdapter() {
        if (customAdapter == null) {
            // gan data source cho adapter
            customAdapter = new CustomAdapterThuByChuyenBien(this, R.layout.customlist_thuchibychuyenbien, customadapterData);
            //gan adapter cho spinner
            lvThuByChuyenBien.setAdapter(customAdapter);
        }else{
            updatelistThuByChuyenBien();
            //cho troi xg record duoi cung
            lvThuByChuyenBien.setSelection(customAdapter.getCount()-1);
        }
    }
    private  void updatelistThuByChuyenBien(){
        customadapterData.clear();
        customadapterData.addAll(arrThuByChuyenBien);
        customAdapter.notifyDataSetChanged();
    }

    // Khi Activity này hoàn thành,
    // có thể cần gửi phản hồi gì đó về cho Activity đã gọi nó.
    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("needRefresh", this.needRefresh);
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
