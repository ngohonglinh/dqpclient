package com.dqpvn.dqpclient;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dqpvn.dqpclient.crudmanager.SyncCheck;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.restfullAPI;
import com.dqpvn.dqpclient.customadapters.CustomAdapterChuyenBien;
import com.dqpvn.dqpclient.models.Chi;
import com.dqpvn.dqpclient.models.ChuyenBien;
import com.dqpvn.dqpclient.models.DSTV;
import com.dqpvn.dqpclient.models.Thu;
import com.dqpvn.dqpclient.models.Users;
import com.dqpvn.dqpclient.models.WantDeleteFromServer;
import com.dqpvn.dqpclient.utils.MyContextWrapper;
import com.dqpvn.dqpclient.utils.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.function.Predicate;

import static com.dqpvn.dqpclient.crudmanager.SyncCheck.IS_ADMIN;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_NAME;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.MY_SERVER;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.RKEY_TICKET;
import static com.dqpvn.dqpclient.utils.utils.comPare;
import static com.dqpvn.dqpclient.utils.utils.getCurrentDate;
import static com.dqpvn.dqpclient.utils.utils.getCurrentTimeMiliS;
import static com.dqpvn.dqpclient.utils.utils.hideSoftKeyboard;
import static com.dqpvn.dqpclient.utils.utils.intGet;
import static com.dqpvn.dqpclient.utils.utils.isBad;
import static com.dqpvn.dqpclient.utils.utils.longGet;


public class ChuyenBienActivity extends AppCompatActivity {

    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;



    //base
    final private String TAG= getClass().getSimpleName();
    private int mHideMenu = 1;
    private boolean menuHideAll=false;
    private Calendar cal;
    private TextView tvId, tvTongThu, tvTongChi, tvRkey,tvServerkey, tvChuyenBienDSTV, tvDaChia;
    private CheckBox chkDaChia;
    private EditText edtTenTau, edtNgayKhoiHanh, edtNgayKetChuyen, edtDiemDD;
    private Spinner spnChuyenBien;
    private AutoCompleteTextView aedtUsername;
    private final int REQUEST_START_THU=123;
    private  final int REQUEST_START_CHI=124;
    private  final int REQUEST_START_DSTV=125;
    private ArrayList<Users>arrUser=new ArrayList<>();
    private ArrayList<String> listUser=new ArrayList<>();
    //database
    private crudLocal crudLocaldb=crudLocal.getInstance(this);
    //Khai báo Datasource lưu trữ danh sách chuyenbien
    private ArrayList<ChuyenBien>arrChuyenBien=new ArrayList<ChuyenBien>();
    private ArrayList<ChuyenBien>customAdapterData=new ArrayList<>();
    //Khai báo ArrayAdapter cho ListView
    private CustomAdapterChuyenBien customAdapter;
    //Theo gioi cho phép Nhap lieu
    private String edit_mode="VIEW", userName;


    //lam viec voi menu
    @Override
    public boolean onCreateOptionsMenu(Menu manu) {
        getMenuInflater().inflate(R.menu.record_menu, manu);

        if (menuHideAll){
            for (int i = 0; i < manu.size(); i++){
                manu.getItem(i).setVisible(false);
            }
            return true;
        }

        MenuItem mSave = manu.findItem(R.id.save);
        if (mHideMenu==1){
            mSave.setVisible(false);
        }else{
            mSave.setVisible(true);
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
                if (!userName.substring(0,5).equals("admin")){
                    Toast.makeText(ChuyenBienActivity.this, "Dữ liệu được bảo vệ...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                //wrtite ơn logic
                edit_mode="NEW";
                setEditMod(true);
                CheckAddNew();
                return true;
            case R.id.id_edit :
                if (arrChuyenBien.size()==0){
                    return true;
                }
                if (!userName.substring(0,5).equals("admin")){
                    Toast.makeText(ChuyenBienActivity.this, "Dữ liệu được bảo vệ...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                edit_mode="EDIT";
                setEditMod(true);
                //wrtite ơn logic
                CheckEdit();
                return true;
            case R.id.id_delete :
                //wrtite on logic
                if (arrChuyenBien.size()==0){
                    return true;
                }
                if (!userName.substring(0,5).equals("admin")){
                    Toast.makeText(ChuyenBienActivity.this, "Dữ liệu được bảo vệ...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (checkDetail()){
                    Toast.makeText(this, "Không thể xóa vì có chi tiết liên quan", Toast.LENGTH_SHORT).show();
                    return true;
                }
                //lay ra index hien tai cua spnView
                int result =spnChuyenBien.getSelectedItemPosition();

                final ChuyenBien chuyenbien =arrChuyenBien.get(result);
                // Hỏi trước khi xóa.
                new AlertDialog.Builder(this)  
                        .setTitle("DQP Client")
                        .setMessage(chuyenbien.getChuyenbien()+ "\n\n" +"Có chắc xóa?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                CheckDelete(chuyenbien);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            case R.id.save:
                UpdateChuyenBien();
                hideSoftKeyboard(ChuyenBienActivity.this);
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
        setContentView(R.layout.activity_chuyen_bien);

        //utils.checkAndRequestPermissions(this);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.dqplogo2);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        addControls();

        addEvents();
        initialization();
        setAdapter();
        initData();
    }

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



    private void initialization(){
        Intent intent=getIntent();
        userName=intent.getStringExtra("userName");
        if (isBad(userName)){
            return;
        }
        if (!userName.substring(0,5).equals("admin")){
            if (isThuyenTruong()){
                arrChuyenBien=crudLocaldb.ChuyenBien_getChuyenBienByShipMaster(userName);
                //phong truong hop dang chia thi cung cho tt thay ma khong cho linh bo thay
                if (arrChuyenBien.size()==0){
                    arrChuyenBien=crudLocaldb.ChuyenBien_getAllChuyenBienByShipMaster(userName);
                }
            }else{
                arrChuyenBien=crudLocaldb.ChuyenBien_getOnlyShowChuyenBien();
            }
            tvDaChia.setVisibility(View.GONE);
            chkDaChia.setVisibility(View.GONE);
        }else{
            arrChuyenBien=crudLocaldb.ChuyenBien_getAllChuyenBien();
            tvDaChia.setVisibility(View.VISIBLE);
            chkDaChia.setVisibility(View.VISIBLE);

            //pull from server ******************************************************
            final String BASE_URL= MY_SERVER + "/dqpclient/user";
            restfullAPI.getUser  task = new restfullAPI.getUser();
            task.setUpdateListener(new restfullAPI.getUser.OnUpdateListener(){
                @Override
                public void onUpdate(ArrayList<Users> obj) {
                    if (obj!=null){
                        arrUser=obj;
                        for (int i=0; i<arrUser.size();i++){
                            listUser.add(arrUser.get(i).getEmail());
                        }
                    }else{
                        listUser = crudLocaldb.Users_listAllUsers();
                    }
                    updateUserSpiner();
                }
            });
            task.execute(BASE_URL);
        }

        if (!LOGIN_NAME.substring(0,5).equals("admin")){
            menuHideAll=true;
            invalidateOptionsMenu();
        }

    }

    private Boolean isThuyenTruong(){
        Boolean is =false;
        ArrayList<ChuyenBien>arrChuyenBien=new ArrayList<>();
        arrChuyenBien=crudLocaldb.ChuyenBien_getOnlyShowChuyenBien();
        for(int i=0;i<arrChuyenBien.size();i++){
            if (arrChuyenBien.get(i).getUsername().equals(userName) &&
                    !arrChuyenBien.get(i).getUsername().substring(0,5).equals("admin")){
                is=true;
                break;
            }
        }
        //find next dachia=1, truong hop dang chia thi cung cho tai cong xem, ma k cho linh bo xem
        if (!is){
            arrChuyenBien=crudLocaldb.ChuyenBien_getAllChuyenBien();
            for(int i=0;i<arrChuyenBien.size();i++){
                if (arrChuyenBien.get(i).getUsername().equals(userName) &&
                        !arrChuyenBien.get(i).getUsername().substring(0,5).equals("admin")){
                    is=true;
                    break;
                }
            }
        }
        return is;
    }

    private void updateUserSpiner(){
        ArrayAdapter<String> adapterUser;
        adapterUser = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listUser);
        aedtUsername.setAdapter(adapterUser);
    }


    private void addControls() {
        tvId=findViewById(R.id.tv_IdChuyenBien);
        tvRkey=findViewById(R.id.tv_RkeyChuyenBien);
        tvChuyenBienDSTV=findViewById(R.id.tv_ChuyenBienDSTV);
        tvServerkey=findViewById(R.id.tv_ChuyenBienServerKey);
        spnChuyenBien=findViewById(R.id.spn_ChuyenBien);
        aedtUsername=findViewById(R.id.aedt_ChuyenBienUsername);
        tvTongThu=findViewById(R.id.tv_TongThu);
        tvTongChi=findViewById(R.id.tv_TongChi);
        edtTenTau=findViewById(R.id.edt_TenTau);
        edtNgayKhoiHanh=findViewById(R.id.edt_NgayKhoiHanh);
        edtNgayKetChuyen=findViewById(R.id.edt_NgayKetChuyen);
        edtDiemDD=findViewById(R.id.edt_ChuyenBienDiemDD);
        tvDaChia=findViewById(R.id.tv_ChuyenBienDaChia);
        chkDaChia=findViewById(R.id.chk_ChuyenBienDaChia);

        tvChuyenBienDSTV.setPaintFlags(tvChuyenBienDSTV.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        tvTongChi.setPaintFlags(tvTongChi.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        tvTongThu.setPaintFlags(tvTongThu.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
    }

    private void addEvents() {
        aedtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    // on focus off
                    String str = aedtUsername.getText().toString();

                    ListAdapter listAdapter = aedtUsername.getAdapter();
                    for(int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if(str.compareToIgnoreCase(temp) == 0) {
                            aedtUsername.setText(temp);
                            return;
                        }
                    }

                    aedtUsername.setText("");

                }
            }
        });
        tvChuyenBienDSTV.setOnClickListener(new MyEvent());
        edtNgayKhoiHanh.setOnClickListener(new MyEvent());
        edtNgayKetChuyen.setOnClickListener(new MyEvent());
        tvTongChi.setOnClickListener(new MyEvent());
        tvTongThu.setOnClickListener(new MyEvent());
        edtNgayKhoiHanh.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                edtNgayKhoiHanh.setFocusable(true);
                edtNgayKhoiHanh.setFocusableInTouchMode(true);
                edtNgayKhoiHanh.requestFocus();
                return false;
            }
        });
        edtNgayKetChuyen.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                edtNgayKetChuyen.setFocusable(true);
                edtNgayKetChuyen.setFocusableInTouchMode(true);
                edtNgayKetChuyen.requestFocus();
                return false;
            }
        });
        //Xu ly su kien click vao spiner
        spnChuyenBien.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,View view, int position, long id) {
                //Toast.makeText(Example5Activity.this, spnNoteTitle.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                ChuyenBien chuyenbien = arrChuyenBien.get(position);
                tvId.setText(String.valueOf(chuyenbien.getId()));
                tvRkey.setText(String.valueOf(chuyenbien.getRkey()));
                tvServerkey.setText(String.valueOf(chuyenbien.getServerkey()));
                edtTenTau.setText(chuyenbien.getTentau());
                if (chuyenbien.getDachia()==1){
                    chkDaChia.setChecked(true);
                }else{
                    chkDaChia.setChecked(false);
                }
                edtNgayKhoiHanh.setText(utils.DinhDangNgay(chuyenbien.getNgaykhoihanh(),"dd/mm/yyyy"));
                edtNgayKetChuyen.setText(utils.DinhDangNgay(chuyenbien.getNgayketchuyen(),"dd/mm/yyyy"));
                aedtUsername.setText(chuyenbien.getUsername()+"");
                String SoTV=crudLocaldb.DSTV_CountByChuyenBien(longGet(tvRkey.getText()+""));
                tvChuyenBienDSTV.setText(SoTV+ " Nhân lực");
                tvTongThu.setText(chuyenbien.getTongthu()+"");
                tvTongChi.setText(chuyenbien.getTongchi()+"");
                formatNumber(tvTongChi);
                formatNumber(tvTongThu);

                tinhDiemDD();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        chkDaChia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!tvRkey.getText().equals("")){
                    if (arrChuyenBien.size()==0){
                        return;
                    }
                    //wrtite ơn logic
                    CheckEdit();
                }

            }
        });

    }




    private class MyEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent;
            String tenChuyenBien=crudLocaldb.ChuyenBien_getTenChuyenBien(longGet(tvRkey.getText()+""));
            boolean daChia=crudLocaldb.ChuyenBien_isChuyenBienDaChia(tenChuyenBien);
            switch (v.getId()) {
                case R.id.tv_ChuyenBienDSTV:
                    ArrayList<DSTV> arrDSTV=new ArrayList<>();
                    arrDSTV=crudLocaldb.DSTV_getDSTVByChuyenBien(longGet(tvRkey.getText()+""));
                    intent = new Intent(ChuyenBienActivity.this, DSTVActivity.class);
                    intent.putExtra("daChia",daChia);
                    intent.putExtra("rkeyChuyenBien", longGet(tvRkey.getText() + ""));
                    intent.putExtra("userName", userName);
                    intent.putExtra("rkeyTicket", RKEY_TICKET);
                    if (arrDSTV.size()==0){
                        intent.putExtra("makeNew", true);
                    }
                    startActivityForResult(intent,REQUEST_START_DSTV);
                    break;
                case R.id.edt_NgayKhoiHanh:
                   showDatePickerDialog(edtNgayKhoiHanh);
                    break;
                case R.id.edt_NgayKetChuyen:
                   showDatePickerDialog(edtNgayKetChuyen);
                    break;
                case R.id.tv_TongThu:
                   ArrayList<Thu> arrThu=new ArrayList<>();
                   arrThu=crudLocaldb.Thu_getThuByChuyenBien(longGet(tvRkey.getText()+""));
                   if (arrThu.size()==0){
                       if (isThuyenTruong() && !IS_ADMIN){
                           Toast.makeText(ChuyenBienActivity.this, "Authorized personnel only...", Toast.LENGTH_SHORT).show();
                           break;
                       }else {
                           if (longGet(tvTongThu.getText()+"")==0){
                               new android.app.AlertDialog.Builder(ChuyenBienActivity.this)
                                       .setMessage(edtTenTau.getText() + " Chưa phát sinh khoản thu nào " + "\n\n" + "Có muốn tạo mới?")
                                       .setCancelable(false)
                                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                           public void onClick(DialogInterface dialog, int id) {
                                               String tenChuyenBien=crudLocaldb.ChuyenBien_getTenChuyenBien(longGet(tvRkey.getText()+""));
                                               Intent intent = new Intent(ChuyenBienActivity.this,ThuActivity.class);
                                               intent.putExtra("userName", LOGIN_NAME);
                                               intent.putExtra("tenChuyenBien", tenChuyenBien);
                                               intent.putExtra("makeNew",true);
                                               intent.putExtra("rkeyTicket", RKEY_TICKET);
                                               startActivityForResult(intent,REQUEST_START_THU);
                                           }
                                       })
                                       .setNegativeButton("No", null)
                                       .show();
                           }
                       }
                   }else{
                       intent = new Intent(ChuyenBienActivity.this, ThuByChuyenBienActivity.class);
                       intent.putExtra("daChia",daChia);
                       intent.putExtra("rkeyChuyenBien",longGet(tvRkey.getText()+""));
                       intent.putExtra("userName", userName);
                       intent.putExtra("rkeyTicket", RKEY_TICKET);

                       startActivityForResult(intent,REQUEST_START_THU);
                   }

                    break;
                case R.id.tv_TongChi:
                    ArrayList<Chi> arrChi=new ArrayList<>();
                    arrChi=crudLocaldb.Chi_getChiByChuyenBien(longGet(tvRkey.getText()+""));
                    if (arrChi.size()==0){
                        if (isThuyenTruong() && !IS_ADMIN){
                            Toast.makeText(ChuyenBienActivity.this, "Authorized personnel only...", Toast.LENGTH_SHORT).show();
                            break;
                        }else {
                            if (longGet(tvTongChi.getText()+"")==0){
                                new android.app.AlertDialog.Builder(ChuyenBienActivity.this)
                                        .setMessage(edtTenTau.getText() + " Chưa phát sinh khoản chi nào " + "\n\n" + "Có muốn tạo mới?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                String tenChuyenBien=crudLocaldb.ChuyenBien_getTenChuyenBien(longGet(tvRkey.getText()+""));
                                                Intent intent = new Intent(ChuyenBienActivity.this,ChiActivity.class);
                                                intent.putExtra("userName", LOGIN_NAME);
                                                intent.putExtra("tenChuyenBien", tenChuyenBien);
                                                intent.putExtra("makeNew",true);
                                                intent.putExtra("rkeyTicket", RKEY_TICKET);
                                                startActivityForResult(intent,REQUEST_START_CHI);
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                            }
                        }
                    }else{
                        intent = new Intent(ChuyenBienActivity.this, ChiByChuyenBienActivity.class);
                        intent.putExtra("daChia",daChia);
                        intent.putExtra("rkeyChuyenBien",longGet(tvRkey.getText()+""));
                        intent.putExtra("userName", userName);
                        intent.putExtra("rkeyTicket", RKEY_TICKET);
                        startActivityForResult(intent,REQUEST_START_CHI);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && ( requestCode == REQUEST_START_THU || requestCode == REQUEST_START_CHI || requestCode == REQUEST_START_DSTV )) {
            boolean needRefresh = data.getBooleanExtra("needRefresh",false);
            // Refresh ListView
            if(needRefresh) {
                refeshScreenData();
            }
        }
    }

    private void refeshScreenData() {
        int position=-1;
        for (int j = 0; j < arrChuyenBien.size(); j++) {
            if (arrChuyenBien.get(j).getRkey() ==longGet(tvRkey.getText()+"")) {
                position = j;
                gotoRec(position);
                break;  // uncomment to get the first instance
            }
        }

    }

    private void gotoRec(int position){
        if (!userName.substring(0,5).equals("admin")){
            if (isThuyenTruong()){
                arrChuyenBien=crudLocaldb.ChuyenBien_getChuyenBienByShipMaster(userName);
                //phong truong hop dang chia thi cung cho tt thay ma khong cho linh bo thay
                if (arrChuyenBien.size()==0){
                    arrChuyenBien=crudLocaldb.ChuyenBien_getAllChuyenBienByShipMaster(userName);
                }
            }else{
                arrChuyenBien=crudLocaldb.ChuyenBien_getOnlyShowChuyenBien();
            }
        }else{
            arrChuyenBien=crudLocaldb.ChuyenBien_getAllChuyenBien();
        }

        if (arrChuyenBien.size()>=1) {
            ChuyenBien chuyenbien=arrChuyenBien.get(position);
            spnChuyenBien.setSelection(position);
            tvId.setText(chuyenbien.getId()+"");
            tvRkey.setText(chuyenbien.getRkey() + "");
            tvServerkey.setText(chuyenbien.getServerkey() + "");
            edtTenTau.setText(chuyenbien.getTentau() + "");
            if (chuyenbien.getDachia()==1){
                chkDaChia.setChecked(true);
            }else{
                chkDaChia.setChecked(false);
            }
            edtNgayKhoiHanh.setText(utils.DinhDangNgay(chuyenbien.getNgaykhoihanh(), "dd/mm/yyyy"));
            edtNgayKetChuyen.setText(utils.DinhDangNgay(chuyenbien.getNgayketchuyen(), "dd/mm/yyyy"));
            aedtUsername.setText(chuyenbien.getUsername()+"");
            String SoTV=crudLocaldb.DSTV_CountByChuyenBien(longGet(tvRkey.getText()+""));
            tvChuyenBienDSTV.setText(SoTV+ " Nhân lực");
            tvTongThu.setText(chuyenbien.getTongthu() + "");
            tvTongChi.setText(chuyenbien.getTongchi() + "");
            formatNumber(tvTongChi);
            formatNumber(tvTongThu);
            edit_mode = "VIEW";
            setEditMod(false);

        }
    }

    private boolean checkDetail(){
        Boolean thu,chi;
        ArrayList<Thu>arrThu=new ArrayList<>();
        ArrayList<Chi>arrChi=new ArrayList<>();
        long idChuyenBien=0;
        idChuyenBien=longGet(tvRkey.getText()+"");
        arrThu=crudLocaldb.Thu_getThuByChuyenBien(idChuyenBien);
        if (arrThu.size()>0) {
            thu= true;

        }else{
            thu= false;

        }
        arrChi=crudLocaldb.Chi_getChiByChuyenBien(idChuyenBien);
        if (arrThu.size()>0) {
            chi= true;

        }else{
            chi= false;

        }
        if (thu || chi){
            return true;
        } else{
            return false;
        }

    }


    public void showDatePickerDialog(final EditText edtViewDate) {
        hideSoftKeyboard(ChuyenBienActivity.this);
        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear,
                                  int dayOfMonth) {
                //Khi nguoi dung da chon thi callback chay set ngay da chon vao edt
                String ngay, thang;
                if (String.valueOf(dayOfMonth).length()<2){
                    ngay="0"+String.valueOf(dayOfMonth);
                } else {
                    ngay=String.valueOf(dayOfMonth);
                }
                if (String.valueOf(monthOfYear + 1).length()<2){
                    thang="0"+String.valueOf(monthOfYear + 1);
                }else {
                    thang=String.valueOf(monthOfYear + 1);
                }
                edtViewDate.setText(ngay + "/" + thang + "/" + year);
            }
        };
        //Duoi day la hien len giao dien de chon ngay
        String s;
        if (!utils.isDate(getEditText(edtViewDate))){
            //Dinh dang lai kieu ngay hien tai
            cal=Calendar.getInstance();
            SimpleDateFormat dft=new SimpleDateFormat("dd/MM/yyyy");
            //gan ngay thang hien tai da dc dinh dang cho s
            s=dft.format(cal.getTime());
        }else {
            s=getEditText(edtViewDate);
        }
        String strArrtmp[] = s.split("/");
        int ngay = Integer.parseInt(strArrtmp[0]);
        int thang = Integer.parseInt(strArrtmp[1]) - 1;
        int nam = Integer.parseInt(strArrtmp[2]);
        DatePickerDialog pic = new DatePickerDialog(
                this,
                callback, nam, thang, ngay);
        pic.setTitle("Chọn ngày");
        pic.show();

    }

    private void UpdateChuyenBien(){
        ChuyenBien chuyenbien=new ChuyenBien();
        String tentau=getEditText(edtTenTau);;
        String ngaykhoihanh=getEditText(edtNgayKhoiHanh);
        String ngayketchuyen=getEditText(edtNgayKetChuyen);
//        if (ngayketchuyen==null || ngayketchuyen.isEmpty()) {
//            ngayketchuyen=getCurrentDate();
//        }
        if(tentau.equals("") || !utils.isDate(ngaykhoihanh)) {
            Toast.makeText(getApplicationContext(), "Cần nhập vào Cặp tàu và Ngày khởi hành", Toast.LENGTH_SHORT).show();
            return;
        }
        String s=ngaykhoihanh.substring(0,6)+ngaykhoihanh.substring(8);
        String tenchuyenbien=tentau+"@"+(ngaykhoihanh).replace("/","");
        ngaykhoihanh=utils.DinhDangNgay(ngaykhoihanh,"yyyy/mm/dd");
        ngayketchuyen = utils.DinhDangNgay(ngayketchuyen,"yyyy/mm/dd");
        chuyenbien.setTentau(tentau);
        chuyenbien.setNgaykhoihanh(ngaykhoihanh);
        chuyenbien.setNgayketchuyen(ngayketchuyen);
        chuyenbien.setChuyenbien(tenchuyenbien);
        chuyenbien.setTongthu(longGet(tvTongThu.getText()+"")+"");
        chuyenbien.setTongchi(longGet(tvTongChi.getText()+"")+"");
        chuyenbien.setUpdatetime(getCurrentTimeMiliS());
        chuyenbien.setUsername(getEditText(aedtUsername));
        if (chkDaChia.isChecked()){
            chuyenbien.setDachia(1);
        }else{
            chuyenbien.setDachia(0);
        }

        if (edit_mode=="NEW" && tvRkey.getText().equals("")) {
                if (chuyenbien != null) {
                    chuyenbien.setServerkey(0);
                    chuyenbien.setRkey(longGet(getCurrentTimeMiliS()));
                    long i = crudLocaldb.ChuyenBien_addChuyenBien(chuyenbien);
                    if (i != -1) {
                       setEditMod(false);
                       updateListChuyenBien();
                       setAdapter();
                    }
                }

        }else{
            if (edit_mode=="EDIT"){
                //ChuyenBien chuyenBien = new ChuyenBien();
                chuyenbien.setId(intGet(tvId.getText()+""));
                chuyenbien.setRkey(longGet((String) tvRkey.getText()));
                chuyenbien.setServerkey(intGet(String.valueOf(tvServerkey.getText())));
                int result = crudLocaldb.ChuyenBien_updateChuyenBien(chuyenbien);
                if(result>0) {
                    updateListChuyenBien();
                    setEditMod(false);
                }
            }
        }
    }

    private void CheckAddNew(){
        tvId.setText("");
        tvRkey.setText("");
        tvServerkey.setText("");
        edtTenTau.setText("");
        edtNgayKhoiHanh.setText("");
        edtNgayKetChuyen.setText("");
        aedtUsername.setText("");
        tvChuyenBienDSTV.setText("");
        tvTongChi.setText("");
        tvTongThu.setText("");
        chkDaChia.setChecked(false);
        edtTenTau.requestFocus();
        edit_mode="NEW";

    }

    private void CheckEdit(){
        edit_mode="EDIT";
    }

    private void CheckDelete(ChuyenBien cb) {
        if (cb.getServerkey()!=0){
            WantDeleteFromServer wdfs=new WantDeleteFromServer();
            wdfs.setmServerkey(cb.getServerkey());
            wdfs.setmTablename("chuyenbien");
            crudLocaldb.WDFS_addWDFS(wdfs);
        }
        
        crudLocaldb.ChuyenBien_deleteChuyenbien(cb.getRkey());
        //remove from array list
        Predicate<ChuyenBien> personPredicate = p-> p.getId() == cb.getId();
        arrChuyenBien.removeIf(personPredicate);
        //refesh screen
        tvId.setText("");
        tvRkey.setText("");
        tvServerkey.setText("");
        edtTenTau.setText("");
        edtNgayKhoiHanh.setText("");
        tvChuyenBienDSTV.setText("");
        tvTongChi.setText("");
        tvTongThu.setText("");
        edtNgayKetChuyen.setText("");
        aedtUsername.setText("");
        edtTenTau.requestFocus();
        chkDaChia.setChecked(false);
        // Refresh ListView.
        updateListChuyenBien();
    }

    private String getEditText (EditText edtText){

        try{
            String txt;
            txt=edtText.getText().toString();
            return txt.trim();
        }
        catch(Exception e){
            Log.e("Error: ",e.toString());
            Toast.makeText(this, "Null...", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    private void setAdapter() {
        if (!userName.substring(0,5).equals("admin")){
            if (isThuyenTruong()){
                arrChuyenBien=crudLocaldb.ChuyenBien_getChuyenBienByShipMaster(userName);
                //phong truong hop dang chia thi cung cho tt thay ma khong cho linh bo thay
                if (arrChuyenBien.size()==0){
                    arrChuyenBien=crudLocaldb.ChuyenBien_getAllChuyenBienByShipMaster(userName);
                }
            }else{
                arrChuyenBien=crudLocaldb.ChuyenBien_getOnlyShowChuyenBien();
            }
        }else{
            arrChuyenBien=crudLocaldb.ChuyenBien_getAllChuyenBien();
        }
        customAdapterData.addAll(arrChuyenBien);
        if (customAdapter == null) {
            // gan data source cho adapter
            customAdapter = new CustomAdapterChuyenBien(this, R.layout.customlist_chuyenbien,customAdapterData);
            //gan adapter cho spinner
            spnChuyenBien.setAdapter(customAdapter);
        }else{
            updateListChuyenBien();
        }
    }
    //gett all to list
    public void updateListChuyenBien(){
        customAdapterData.clear();
        if (!userName.substring(0,5).equals("admin")){
            if (isThuyenTruong()){
                arrChuyenBien=crudLocaldb.ChuyenBien_getChuyenBienByShipMaster(userName);
                //phong truong hop dang chia thi cung cho tt thay ma khong cho linh bo thay
                if (arrChuyenBien.size()==0){
                    arrChuyenBien=crudLocaldb.ChuyenBien_getAllChuyenBienByShipMaster(userName);
                }
            }else{
                arrChuyenBien=crudLocaldb.ChuyenBien_getOnlyShowChuyenBien();
            }
        }else{
            arrChuyenBien=crudLocaldb.ChuyenBien_getAllChuyenBien();
        }
        customAdapterData.addAll(arrChuyenBien);
        customAdapter.notifyDataSetChanged();
        //cho troi xg record duoi cung
        spnChuyenBien.setSelection(customAdapter.getCount()-1);
    }

    private  void initData(){
        if (arrChuyenBien.size()>=1) { //phong truong hop null k co record nao
            spnChuyenBien.setSelection(0);
            //lay ra index cuoi cung cua spnView
            ChuyenBien chuyenbien =arrChuyenBien.get(0);
            tvId.setText(chuyenbien.getId()+"");
            tvRkey.setText(chuyenbien.getRkey()+"");
            tvServerkey.setText(chuyenbien.getServerkey()+"");
            edtTenTau.setText(chuyenbien.getTentau()+"");
            if (chuyenbien.getDachia()==1){
                chkDaChia.setChecked(true);
            }else{
                chkDaChia.setChecked(false);
            }
            edtNgayKhoiHanh.setText(utils.DinhDangNgay(chuyenbien.getNgayketchuyen(),"dd/mm/yyyy"));
            edtNgayKetChuyen.setText(utils.DinhDangNgay(chuyenbien.getNgayketchuyen(),"dd/mm/yyyy"));
            aedtUsername.setText(chuyenbien.getUsername()+"");
            String SoTV=crudLocaldb.DSTV_CountByChuyenBien(longGet(tvRkey.getText()+""));
            tvChuyenBienDSTV.setText(SoTV+ " Nhân lực");
            tvTongThu.setText(chuyenbien.getTongthu()+"");
            tvTongChi.setText(chuyenbien.getTongchi()+"");
            formatNumber(tvTongChi);
            formatNumber(tvTongThu);
            edit_mode="VIEW";
            setEditMod(false);

            //tinh diem danh du
            tinhDiemDD();
        }
    }

    private void formatNumber(TextView tv){
        try {
            DecimalFormat formatter = new DecimalFormat("#,###");
            Long lv=Long.valueOf(tv.getText()+"");
            String get_value = formatter.format(lv);
            tv.setText(get_value);
        }catch (NumberFormatException e){
            Log.e("error: ", tv.getText().toString());
        }
    }

    private void setEditMod(boolean chohaykhong) {
        if (chohaykhong == true) {
            aedtUsername.setThreshold(1);
            mHideMenu = 0; // setting state

        } else {
            aedtUsername.setThreshold(1000);
            mHideMenu = 1; // setting state
        }
        invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again
    }

    private void tinhDiemDD() {
        long thisEater=crudLocaldb.Users_getServerKeyByEmail(aedtUsername.getText()+"");
        int diemEater=crudLocaldb.DiemDD_SumDiemEater(thisEater);
        int diemFeeder=crudLocaldb.DiemDD_SumDiemFeeder(thisEater);
        double tongDiemDD=diemEater+diemFeeder*0.2;
        try {
            DecimalFormat formatter = new DecimalFormat("#,###.#");
            String get_value = formatter.format(tongDiemDD);
            edtDiemDD.setText(get_value+"♥");
        }catch (NumberFormatException e){
            edtDiemDD.setText(intGet(tongDiemDD+"♥"));
        }
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


