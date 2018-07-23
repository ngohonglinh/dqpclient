package com.dqpvn.dqpclient;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.customadapters.CustomAdapterChuyenBien;
import com.dqpvn.dqpclient.customadapters.CustomAdapterDebtBook;
import com.dqpvn.dqpclient.models.DebtBook;
import com.dqpvn.dqpclient.utils.MyContextWrapper;
import com.dqpvn.dqpclient.utils.NumberTextWatcher;
import com.dqpvn.dqpclient.utils.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class DebtBookByUserActivity extends AppCompatActivity {
    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;
    final private String TAG = getClass().getSimpleName();
    private int NLS_Posittion, NPS_Posittion;

    private EditText edtTongTien, edtChuyenBien;
    private Spinner spnUserName, spnNgayUng;
    private ListView lvDebtBooByUser;
    private String intentTenChuyenBien;
    private crudLocal crudLocaldb=crudLocal.getInstance(this);
    private ArrayList<DebtBook>arrDebtBook=new ArrayList<>();
    private ArrayList<String>listUser=new ArrayList<>();
    private ArrayList<String>listNgayUng=new ArrayList<>();
    private CustomAdapterDebtBook adapterDebtBook;

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
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
        setContentView(R.layout.activity_debt_book_by_user);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);

        spnUserName=findViewById(R.id.spn_DebtBooByUserUserName);
        spnNgayUng=findViewById(R.id.spn_DebtBooByUserNgayUng);
        edtChuyenBien=findViewById(R.id.edt_DebtBookByUserTenTau);
        edtTongTien=findViewById(R.id.edt_DebtBooByUseTongTien);
        lvDebtBooByUser=findViewById(R.id.lv_DebtBooByUser);
        addEvents();

        Intent intent=getIntent();
        intentTenChuyenBien=intent.getStringExtra("tenChuyenBien");
        String intentUserName=intent.getStringExtra("userName");
        edtChuyenBien.setText(intentTenChuyenBien);

        listUser=crudLocaldb.DebtBook_getListNguoiDaLuuSoTrongChuyenBien(intentTenChuyenBien);
        listUser.add("Tất cả...");
        ArrayAdapter<String> adapterUser=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,listUser);
        spnUserName.setAdapter(adapterUser);
        int lvposition=-1;
        for (int i=0;i<listUser.size();i++){
            if (StringUtils.compare(listUser.get(i).toString(),intentUserName)==0){
                lvposition=i;
            }
        }
        gotoRec(lvposition);
    }
    private void addEvents(){
        edtTongTien.addTextChangedListener(new NumberTextWatcher(edtTongTien));

        spnUserName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (StringUtils.compareIgnoreCase(spnUserName.getItemAtPosition(position).toString(),"Tất cả...")==0){
                    NLS_Posittion=8000;
                }else{
                    NLS_Posittion=position;
                }

                gotoRec(position);
                ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(DebtBookByUserActivity.this, R.color.colorAccent))));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnNgayUng.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (StringUtils.compareIgnoreCase(spnNgayUng.getItemAtPosition(position).toString(),"Tất cả...")==0){
                    NPS_Posittion=8000;
                }else{
                    NPS_Posittion=position;
                }
                gotoRecKetHop(NLS_Posittion,position);
                //((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(DebtBookByUserActivity.this, R.color.colorAccent))));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        lvDebtBooByUser.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mGestureDetector.onTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    private void gotoRec(int poSition){
        if (poSition!=-1){
            spnUserName.setSelection(poSition);
            String NguoiLuuSo=listUser.get(poSition).toString();
            arrDebtBook.clear();
            if (StringUtils.compare(NguoiLuuSo,"Tất cả...")==0){
                listNgayUng=crudLocaldb.DebtBook_getListNgayUngTrongChuyenBien(intentTenChuyenBien);
                listNgayUng.add("Tất cả...");
                ArrayAdapter<String> adapterNgayUng=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,listNgayUng);
                spnNgayUng.setAdapter(adapterNgayUng);
                spnNgayUng.setSelection(listNgayUng.size()-1);

                arrDebtBook=crudLocaldb.DebtBook_getDebtBookByChuyenBien(intentTenChuyenBien);
                adapterDebtBook = new CustomAdapterDebtBook(DebtBookByUserActivity.this, R.layout.customlist_debtbook,arrDebtBook);
                lvDebtBooByUser.setAdapter(adapterDebtBook);
                edtTongTien.setText(crudLocaldb.DebtBook_SumForChuyenBien(intentTenChuyenBien));
                setTitle(utils.getStringLeft(intentTenChuyenBien,"@") + " | by Tất cả...");
            }else{
                listNgayUng=crudLocaldb.DebtBook_getListNgayUngTrongChuyenBienTheoNguoiLuuSo(intentTenChuyenBien,NguoiLuuSo);
                listNgayUng.add("Tất cả...");
                ArrayAdapter<String> adapterNgayUng=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,listNgayUng);
                spnNgayUng.setAdapter(adapterNgayUng);
                spnNgayUng.setSelection(listNgayUng.size()-1);

                arrDebtBook=crudLocaldb.DebtBook_getDebtBookByChuyenBienAndUserLuuSo(intentTenChuyenBien,NguoiLuuSo);
                adapterDebtBook = new CustomAdapterDebtBook(DebtBookByUserActivity.this, R.layout.customlist_debtbook,arrDebtBook);
                lvDebtBooByUser.setAdapter(adapterDebtBook);
                edtTongTien.setText(crudLocaldb.DebtBook_SumForChuyenBienAndNguoiLuuSo(intentTenChuyenBien,NguoiLuuSo));
                setTitle(utils.getStringLeft(intentTenChuyenBien,"@") + " | by " +utils.getStringLeft(NguoiLuuSo,"@") );
            }

        }
    }
    private void gotoRecKetHop(int NLS, int NPS){
        String NguoiLuuSo="", NgayPS="";
        if(NLS==8000){
            NguoiLuuSo="Tất cả...";
        }else{
            NguoiLuuSo=listUser.get(NLS).toString();
        }
        NgayPS=listNgayUng.get(NPS).toString();
        arrDebtBook.clear();
        arrDebtBook=crudLocaldb.DebtBook_getDebtBookByChuyenBienAndUserLuuSoAndNgayPS(intentTenChuyenBien,NguoiLuuSo,NgayPS);
        adapterDebtBook = new CustomAdapterDebtBook(DebtBookByUserActivity.this, R.layout.customlist_debtbook,arrDebtBook);
        lvDebtBooByUser.setAdapter(adapterDebtBook);
        edtTongTien.setText(crudLocaldb.DebtBook_SumForChuyenBienAndNguoiLuuSoAndNgayPS(intentTenChuyenBien,NguoiLuuSo,NgayPS));
        setTitle(utils.getStringLeft(intentTenChuyenBien,"@") + " | by " +utils.getStringLeft(NguoiLuuSo,"@") );
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

