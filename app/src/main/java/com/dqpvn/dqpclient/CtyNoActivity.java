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
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.customadapters.CustomAdapterCtyNo;
import com.dqpvn.dqpclient.models.Chi;
import com.dqpvn.dqpclient.utils.MyContextWrapper;

import java.util.ArrayList;

import static com.dqpvn.dqpclient.crudmanager.SyncCheck.RKEY_TICKET;

public class CtyNoActivity extends AppCompatActivity {

    final private String TAG= getClass().getSimpleName();
    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;
    private int lvPossition=-1;


    private ListView lvCtyNo;
    private boolean needRefresh;
    private final int REQUEST_START_CHI_DETAIL=111;
    //database
    private crudLocal crudLocaldb=crudLocal.getInstance(this);
    //Khai báo Datasource lưu trữ danh sách chuyenbien
    private ArrayList<Chi> arrChi=new ArrayList<Chi>();
    private ArrayList<Chi>customadapterData=new ArrayList<>();
    //Khai báo ArrayAdapter cho ListView
    private CustomAdapterCtyNo customAdapter;
    private String intentUserName;

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
            lvPossition=lvCtyNo.pointToPosition( (int) x, (int) y );
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
            lvPossition=lvCtyNo.pointToPosition( (int) e1.getX(), (int) e1.getY() );
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (lvPossition<0){
                    return true;
                }
                    Chi chi = arrChi.get(lvPossition);
                    Intent intent = new Intent(CtyNoActivity.this, ChiActivity.class);
                    intent.putExtra("chiRkey",chi.getRkey());
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
        setContentView(R.layout.activity_cty_no);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);

        lvCtyNo=findViewById(R.id.lv_CtyNo);

        //lấy intent gọi Activity này
        //Bundle bundleObject =getIntent().getExtras();
        Intent callerIntent=getIntent();
        intentUserName=callerIntent.getStringExtra("userName");
        //có intent rồi thì lấy Bundle dựa vào MyPackage
        Bundle packageFromCaller=
                callerIntent.getBundleExtra("ChiPackage");
        //Có Bundle rồi thì lấy các thông số dựa vào key
        arrChi = (ArrayList<Chi>) packageFromCaller.getSerializable("arrChi");
        String tenDoiTac=packageFromCaller.getString("tenDoiTac");
        //arrChi = (ArrayList<Chi>) bundleObject.getSerializable("arrChi");

        for (int i = 0; i < arrChi.size(); i++) {
            //them ten chuyen bien vao;
            arrChi.get(i).setmTenChuyenBien(crudLocaldb.ChuyenBien_getTenChuyenBien(arrChi.get(i).getRkeychuyenbien()));
        }
        customadapterData.addAll(arrChi);
        setAdapter();
        addEvents();
    }

    private void addEvents() {

        lvCtyNo.setOnTouchListener(new View.OnTouchListener() {
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
            long idDoiTac=data.getLongExtra("rkeyDoiTac",0);
            // Refresh ListView
            if(needRefresh) {
                ArrayList<Chi>listChi=this.crudLocaldb.Chi_getChiByDoiTac(idDoiTac);
                if (listChi.size()==0){
                    this.finish();
                }
                //final ArrayList<Chi>listChi=this.crudLocaldb.getChiByDoiTac(idDoiTac);
                this.arrChi.clear();
                this.arrChi.addAll(listChi);
                for (int i = 0; i < arrChi.size(); i++) {
                    //them ten chuyen bien vao;
                    arrChi.get(i).setmTenChuyenBien(crudLocaldb.ChuyenBien_getTenChuyenBien(arrChi.get(i).getRkeychuyenbien()));
                }
                // Thông báo dữ liệu thay đổi (Để refresh ListView).
                updatelistChi();
                //da co thay doi du lieu tu chi gan gia trị de thong bao cho DoiTac bit khi quay lai no
                needRefresh=true;
            }else{
                needRefresh=false;
            }
        }
    }

    private void setAdapter() {
        if (customAdapter == null) {
            // gan data source cho adapter
            customAdapter = new CustomAdapterCtyNo(this, R.layout.customlist_ctyno, customadapterData);
            //gan adapter cho spinner
            lvCtyNo.setAdapter(customAdapter);
        }else{
            updatelistChi();
            //cho troi xg record duoi cung
            lvCtyNo.setSelection(customAdapter.getCount()-1);
        }
    }
    private  void updatelistChi(){
        customadapterData.clear();
        customadapterData.addAll(arrChi);
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
