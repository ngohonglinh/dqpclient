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

import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.customadapters.CustomAdapterNoCty;
import com.dqpvn.dqpclient.models.Thu;
import com.dqpvn.dqpclient.utils.MyContextWrapper;

import java.util.ArrayList;

import static com.dqpvn.dqpclient.crudmanager.SyncCheck.RKEY_TICKET;

public class NoCtyActivity extends AppCompatActivity {

    final private String TAG= getClass().getSimpleName();
    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;
    private int lvPossition=-1;

    private ListView lvNoCty;
    private boolean needRefresh;
    private final int REQUEST_START_CHI_DETAIL=111;
    //database
    private crudLocal crudLocaldb=crudLocal.getInstance(this);
    //Khai báo Datasource lưu trữ danh sách chuyenbien
    private ArrayList<Thu> arrThu=new ArrayList<Thu>();
    private ArrayList<Thu>customadapterData=new ArrayList<>();
    //Khai báo ArrayAdapter cho ListView
    private CustomAdapterNoCty customAdapter;
    String intentUserName;

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap");
            return true;
        }
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int x = (int) e.getX();
            int y = (int) e.getY();
            lvPossition=lvNoCty.pointToPosition( (int) x, (int) y );
            Log.d(TAG, "onSingleTapUp");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(TAG, "onScroll");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(TAG, "onLongPress");
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            lvPossition=lvNoCty.pointToPosition( (int) e1.getX(), (int) e1.getY() );
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (lvPossition<0){
                    return true;
                }
                Thu thu = arrThu.get(lvPossition);
                Intent intent = new Intent(NoCtyActivity.this, ThuActivity.class);
                intent.putExtra("thuRkey",thu.getRkey());
                intent.putExtra("userName",intentUserName);
                intent.putExtra("rkeyTicket", RKEY_TICKET);
                startActivityForResult(intent,REQUEST_START_CHI_DETAIL);

                Log.d(TAG, "Right to Left swipe performed");
                return true;
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

    //lam viec voi menu
    @Override
    public boolean onCreateOptionsMenu(Menu manu) {
        getMenuInflater().inflate(R.menu.menu_exit_only, manu);
        // return true so that the menu pop up is opened
        return true;
    }


    // Method này sử lý sự kiện khi MenuItem được chọn.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        switch(itemId)  {
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
        setContentView(R.layout.activity_no_cty);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);

        lvNoCty=findViewById(R.id.lv_NoCty);
        //lấy intent gọi Activity này
        //Bundle bundleObject =getIntent().getExtras();
        Intent callerIntent=getIntent();
        intentUserName=callerIntent.getStringExtra("userName");
        //có intent rồi thì lấy Bundle dựa vào MyPackage
        Bundle packageFromCaller=
                callerIntent.getBundleExtra("ThuPackage");
        //Có Bundle rồi thì lấy các thông số dựa vào key
        arrThu = (ArrayList<Thu>) packageFromCaller.getSerializable("arrThu");
        String tenKhachHang=packageFromCaller.getString("tenKhachHang");
        //arrThu = (ArrayList<Thu>) bundleObject.getSerializable("arrThu");

        for (int i = 0; i < arrThu.size(); i++) {
            //them ten chuyen bien vao;
            arrThu.get(i).setmTenChuyenBien(crudLocaldb.ChuyenBien_getTenChuyenBien(arrThu.get(i).getRkeychuyenbien()));

        }
        customadapterData.addAll(arrThu);
        setAdapter();
        addEvents();
    }

    private void addEvents() {

        lvNoCty.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                 mGestureDetector.onTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_START_CHI_DETAIL ) {
            needRefresh = data.getBooleanExtra("needRefresh",false);
            long idKhachHang=data.getLongExtra("rkeyKhachHang",0);
            // Refresh ListView
            if(needRefresh) {
                this.arrThu.clear();
                ArrayList<Thu>listThu=this.crudLocaldb.Thu_getThuByKhachHang(idKhachHang);
                if (listThu.size()==0){
                    this.finish();
                }
                //final ArrayList<Thu>listThu=this.crudLocaldb.getThuByKhachHang(idKhachHang);
                this.arrThu.addAll(listThu);
                for (int i = 0; i < arrThu.size(); i++) {
                    //them ten chuyen bien vao;
                    arrThu.get(i).setmTenChuyenBien(crudLocaldb.ChuyenBien_getTenChuyenBien(arrThu.get(i).getRkeychuyenbien()));
                }
                // Thông báo dữ liệu thay đổi (Để refresh ListView).
                updateListThu();
                //da co thay doi du lieu tu chi gan gia trị de thong bao cho KhachHang bit khi quay lai no
                needRefresh=true;
            }
        }
    }

    private void setAdapter() {
        if (customAdapter == null) {
            // gan data source cho adapter
            customAdapter = new CustomAdapterNoCty(this, R.layout.customlist_ctyno, customadapterData);
            //gan adapter cho spinner
            lvNoCty.setAdapter(customAdapter);
        }else{
            updateListThu();
            //cho troi xg record duoi cung
            lvNoCty.setSelection(customAdapter.getCount()-1);
        }
    }

    private void updateListThu() {
        customadapterData.clear();
        customadapterData.addAll(arrThu);
        customAdapter.notifyDataSetChanged();
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
