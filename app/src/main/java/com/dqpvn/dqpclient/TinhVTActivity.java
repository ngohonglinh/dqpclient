package com.dqpvn.dqpclient;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.dqpvn.dqpclient.utils.utils.doubleGet;
import static com.dqpvn.dqpclient.utils.utils.hideSoftKeyboard;
import static com.dqpvn.dqpclient.utils.utils.intGet;
import static com.dqpvn.dqpclient.utils.utils.isBad;
import static com.dqpvn.dqpclient.utils.utils.longGet;
import static java.lang.Math.pow;

public class TinhVTActivity extends AppCompatActivity {

    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;
    final private String TAG = getClass().getSimpleName();

    private LinearLayout lyGach,lyThepTam, lyThepOng, lyTLR;
    private EditText edtVienPThung, edtDTYeuCau;
    private EditText edtTLR, edtThepTamT, edtThepTamL, edtThepTamW, edtThepOngL, edtThepOngT, edtThepOngD;
    private EditText edtDv1_1,edtDv1_2,edtDv2_1,edtDv2_2;
    private Spinner spnNhomVT, spnSizeGach, spnKhauHaoVieng;
    private ArrayList<String>arrNhomVT=new ArrayList<>();
    private ArrayList<String>arrSizeGach=new ArrayList<>();
    ArrayList<String>arrKhauHaoVieng=new ArrayList<>();
    private double DTGach, DTYeuCau;
    private long KhauHaoVieng;
    private int VienPThung;
    private double TLR;
    private double ThepTamL, ThepTamW,ThepTamT;
    private double ThepOngL, ThepOngD, ThepOngT;
    private int viewThep=0;



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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinhvattu);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);

        addControl();
        addEvents();
        initData();
    }
    private void addControl(){
        loadTabs();
        lyGach=findViewById(R.id.ly_TinhVTGach);
        lyThepTam=findViewById(R.id.ly_TinhVTThepTam);
        lyThepOng=findViewById(R.id.ly_TinhVTThepOng);
        lyTLR=findViewById(R.id.ly_TinhVTTLR);
        spnNhomVT=findViewById(R.id.spn_TinhTVNhomVT);
        spnKhauHaoVieng=findViewById(R.id.spn_TinhVTKhauHao);
        spnSizeGach=findViewById(R.id.spn_TinhVTSizeGach);
        edtVienPThung=findViewById(R.id.edt_TinhVTVienPThung);
        edtDTYeuCau=findViewById(R.id.edt_TinhVTYeuCau);
        //Thep Tam
        edtTLR=findViewById(R.id.edt_TinhVTThepTLR);
        edtThepTamL=findViewById(R.id.edt_TinhVTThepTamL);
        edtThepTamW=findViewById(R.id.edt_TinhVTThepTamW);
        edtThepTamT=findViewById(R.id.edt_TinhVTThepTamT);
        edtThepOngT=findViewById(R.id.edt_TinhVTThepOngT);
        edtThepOngD=findViewById(R.id.edt_TinhVTThepOngD);
        edtThepOngL=findViewById(R.id.edt_TinhVTThepOngL);
        //Quy ta tam suát
        edtDv1_1=findViewById(R.id.edt_tab2_dv1_1);
        edtDv1_2=findViewById(R.id.edt_tab2_dv1_2);
        edtDv2_1=findViewById(R.id.edt_tab2_dv2_1);
        edtDv2_2=findViewById(R.id.edt_tab2_dv2_2);
    }
    private void loadTabs(){
        final TabHost tab=(TabHost) findViewById(R.id.tabhost);
        //gọi lệnh setup
        tab.setup();
        TabHost.TabSpec spec;
        //Tạo tab1
        spec=tab.newTabSpec("t1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Tính vậ tư");
        tab.addTab(spec);
        //Tạo tab2
        spec=tab.newTabSpec("t2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Quy tắc tam suất");
        tab.addTab(spec);
        //Thiết lập tab mặc định được chọn ban đầu là tab 0
        tab.setCurrentTab(0);
        tab.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
//                String s="Tab tag ="+tabId +"; index ="+ tab.getCurrentTab();
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                if (tab.getCurrentTab()==0){
                    setTitle("Tính vật tư");
                }else if(tab.getCurrentTab()==1){
                    setTitle("Quy tắc tam suất");
                }
            }
        });

    }
    private void initData(){

        arrNhomVT.add("Gạch ốp lát");
        arrNhomVT.add("Thép tấm");
        arrNhomVT.add("Thép ống");

        arrSizeGach.add("25x25");
        arrSizeGach.add("25x40");
        arrSizeGach.add("30x30");
        arrSizeGach.add("30x45");
        arrSizeGach.add("30x60");
        arrSizeGach.add("40x40");
        arrSizeGach.add("50x50");
        arrSizeGach.add("60x60");
        arrSizeGach.add("80x80");



        arrKhauHaoVieng.add("0%");
        arrKhauHaoVieng.add("5%");
        arrKhauHaoVieng.add("10%");


        ArrayAdapter<String> adapterNhomVT=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,arrNhomVT);
        spnNhomVT.setAdapter(adapterNhomVT);
        spnNhomVT.setSelection(0);
        ArrayAdapter<String> adapterSizeGach=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,arrSizeGach);
        spnSizeGach.setAdapter(adapterSizeGach);
        spnSizeGach.setSelection(0);
        ArrayAdapter<String> adapterKhauHaoVieng=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,arrKhauHaoVieng);
        spnKhauHaoVieng.setAdapter(adapterKhauHaoVieng);
        spnKhauHaoVieng.setSelection(0);
    }
    private void addEvents(){
        spnNhomVT.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (arrNhomVT.get(position).toString().equals("Gạch ốp lát")){
                    setTitle("Tính số lượng gạch");
                    lyThepTam.setVisibility(View.GONE);
                    lyThepOng.setVisibility(View.GONE);
                    lyTLR.setVisibility(View.GONE);
                    lyGach.setVisibility(View.VISIBLE);
                    if (DTYeuCau!=0 && DTGach!=0 && VienPThung!=0){
                        TinhGach();
                    }
                }
                if (arrNhomVT.get(position).toString().equals("Thép tấm")){
                    lyThepOng.setVisibility(View.GONE);
                    lyGach.setVisibility(View.GONE);
                    lyThepTam.setVisibility(View.VISIBLE);
                    lyTLR.setVisibility(View.VISIBLE);
                    viewThep=1;
                    edtTLR.setText("7.85");
                }
                if (arrNhomVT.get(position).toString().equals("Thép ống")){
                    lyGach.setVisibility(View.GONE);
                    lyThepTam.setVisibility(View.GONE);
                    lyThepOng.setVisibility(View.VISIBLE);
                    lyTLR.setVisibility(View.VISIBLE);
                    viewThep=2;
                    edtTLR.setText("7.85");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnSizeGach.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String size=arrSizeGach.get(position).toString();
                double cd= doubleGet(StringUtils.left(size,2))/100;
                double cr= doubleGet(StringUtils.right(size,2))/100;
                DTGach=cd*cr;
                switch (position){
                    case 0:
                        edtVienPThung.setText("16");
                        VienPThung=16;
                        break;
                    case 1:
                        edtVienPThung.setText("10");
                        VienPThung=10;
                        break;
                    case 2:
                        edtVienPThung.setText("11");
                        VienPThung=11;
                        break;
                    case 3:
                        edtVienPThung.setText("7");
                        VienPThung=7;
                        break;
                    case 4:
                        edtVienPThung.setText("8");
                        VienPThung=8;
                        break;
                    case 5:
                        edtVienPThung.setText("6");
                        VienPThung=6;
                        break;
                    case 6:
                        edtVienPThung.setText("4");
                        VienPThung=4;
                        break;
                    case 7:
                        edtVienPThung.setText("4");
                        VienPThung=4;
                        break;
                    case 8:
                        edtVienPThung.setText("3");
                        VienPThung=3;
                        break;
                }
                if (DTYeuCau!=0 && DTGach!=0 && VienPThung!=0){
                    TinhGach();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnKhauHaoVieng.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KhauHaoVieng= longGet(StringUtils.replace(arrKhauHaoVieng.get(position).toString(),"%",""));
                if (DTYeuCau!=0 && DTGach!=0 && VienPThung!=0){
                    TinhGach();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        edtVienPThung.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                VienPThung=intGet(s.toString());
                if (DTYeuCau!=0 && DTGach!=0 && VienPThung!=0){
                    TinhGach();
                }
            }
        });
        edtVienPThung.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edtVienPThung.setFocusableInTouchMode(true);
                edtVienPThung.setFocusable(true);
                edtVienPThung.requestFocus();
                return true;
            }
        });
        edtTLR.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edtTLR.setFocusableInTouchMode(true);
                edtTLR.setFocusable(true);
                edtTLR.requestFocus();
                return true;
            }
        });
        edtDTYeuCau.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                DTYeuCau=doubleGet(s.toString());
                if (DTYeuCau!=0 && DTGach!=0 && VienPThung!=0){
                    TinhGach();
                }
            }
        });
        edtDTYeuCau.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    hideSoftKeyboard(TinhVTActivity.this);
                }
                return false;
            }
        });
        edtTLR.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TLR=doubleGet(s.toString());
                if (viewThep==1 && TLR!=0 && ThepTamL!=0 && ThepTamT!=0 && ThepTamW!=0){
                    TinhTamThep();
                }else if(viewThep==2 && TLR!=0 && ThepOngL!=0 && ThepOngT!=0 && ThepOngD!=0){
                    TinhOngThep();
                }else{
                    setTitle("Tính khối lượng thép");
                }
            }
        });
        edtThepTamT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ThepTamT=doubleGet(s.toString());
                if (TLR!=0 && ThepTamL!=0 && ThepTamT!=0 && ThepTamW!=0){
                    TinhTamThep();
                }
            }
        });
        edtThepTamW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ThepTamW=doubleGet(s.toString());
                if (TLR!=0 && ThepTamL!=0 && ThepTamT!=0 && ThepTamW!=0){
                    TinhTamThep();
                }
            }
        });
        edtThepTamL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ThepTamL=doubleGet(s.toString());
                if (TLR!=0 && ThepTamL!=0 && ThepTamT!=0 && ThepTamW!=0){
                    TinhTamThep();
                }
            }
        });
        edtThepOngL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ThepOngL=doubleGet(s.toString());
                if (TLR!=0 && ThepOngL!=0 && ThepOngT!=0 && ThepOngD!=0){
                    TinhOngThep();
                }
            }
        });
        edtThepOngD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ThepOngD=doubleGet(s.toString());
                if (TLR!=0 && ThepOngL!=0 && ThepOngT!=0 && ThepOngD!=0){
                    TinhOngThep();
                }
            }
        });
        edtThepOngT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ThepOngT=doubleGet(s.toString());
                if (TLR!=0 && ThepOngL!=0 && ThepOngT!=0 && ThepOngD!=0){
                    TinhOngThep();
                }
            }
        });

        edtDv1_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                QuyTacTamSuat();
            }
        });
        edtDv1_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                QuyTacTamSuat();
            }
        });
        edtDv2_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                QuyTacTamSuat();
            }
        });
    }
    private void QuyTacTamSuat(){
        double dv1_1=doubleGet(edtDv1_1.getText()+"");
        double dv1_2=doubleGet(edtDv1_2.getText()+"");
        double dv2_1=doubleGet(edtDv2_1.getText()+"");
        edtDv2_2.setText(formatNumber(String.valueOf((dv1_2*dv2_1)/dv1_1)));
    }
    private void TinhGach(){
        double x=DTYeuCau/DTGach;
        double kq1=x/VienPThung;
        double kq2=(KhauHaoVieng*kq1)/100;
        double kq=kq1+kq2;
        setTitle("Kết quả: " + formatNumber(kq+"") + " thùng");
    }
    private void TinhTamThep(){
        double kq=(ThepTamT*ThepTamL*ThepTamW*TLR)/1000000;
        if (kq>1000){
            setTitle("Kết quả: " + formatNumber(String.valueOf(kq/1000)) + " tấn");
        }else{
            setTitle("Kết quả: " + formatNumber(kq+"") + " kg");
        }

    }
    private void TinhOngThep(){
        //double kq=((ThepOngD-ThepOngT)*ThepOngT*3.141*TLR*ThepOngL)/1000000;
        double sBig=(pow(ThepOngD/2,2))*3.141;
        double sMall=pow((ThepOngD-(ThepOngT*2))/2,2)*3.141;
        double KLBig=(sBig*ThepOngL*TLR)/1000000;
        double KLsmall=(sMall*ThepOngL*TLR)/1000000;
        double kq=KLBig-KLsmall;
        if (kq>1000){
            setTitle("Kết quả: " + formatNumber(String.valueOf(kq/1000)) + " tấn");
        }else{
            setTitle("Kết quả: " + formatNumber(kq+"") + " kg");
        }
    }

    private String formatNumber(String tv) {
        if (!isBad(tv)){
            DecimalFormat formatter = new DecimalFormat("#,###.##");
            double lv = doubleGet(tv);
            String get_value = formatter.format(lv);
            return get_value;
        }else{
            return "";
        }
    }
}
