package com.dqpvn.dqpclient;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.dqpvn.dqpclient.crudmanager.crudLocal;

import com.dqpvn.dqpclient.customadapters.CustomAdapterDiemDD;
import com.dqpvn.dqpclient.models.ChuyenBien;
import com.dqpvn.dqpclient.models.DiemDD;;
import com.dqpvn.dqpclient.models.WantDeleteFromServer;
import com.dqpvn.dqpclient.utils.MyContextWrapper;
import com.dqpvn.dqpclient.utils.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.function.Predicate;

import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_NAME;
import static com.dqpvn.dqpclient.utils.utils.comPare;
import static com.dqpvn.dqpclient.utils.utils.getCurrentTimeMiliS;
import static com.dqpvn.dqpclient.utils.utils.hideSoftKeyboard;
import static com.dqpvn.dqpclient.utils.utils.intGet;
import static com.dqpvn.dqpclient.utils.utils.isBad;
import static com.dqpvn.dqpclient.utils.utils.longGet;

public class DiemDDActivity extends AppCompatActivity {
    final private String TAG= getClass().getSimpleName();
    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;
    private int lvPossition=-1;

    private RadioGroup group;
    private RadioButton radioTT, radioTV, radioLB;
    private Calendar cal;
    private AutoCompleteTextView aedtEater;
    private EditText edtNgayPs, edtLydo;
    private TextView tvId,tvRkey, tvServerKey,tvUserName;
    private ListView lvDiemDD;
    long rKeyChuyenBien;
    private String edit_mode="VIEW",chucVu;
    private int diemEater=0,diemFeeder=0;
    private ImageView imgLike;
    //database
    private crudLocal crudLocaldb=crudLocal.getInstance(this);


    //Khai báo ArrayAdapter cho Autocomplete
    private CustomAdapterDiemDD customAdapter;
    private ArrayList<DiemDD>customadapterData=new ArrayList<>();
    private ArrayList<DiemDD> arrDiemDD = new ArrayList<>();
    private ArrayList<ChuyenBien> arrWorkingChuyenBien = new ArrayList<>();
    private ArrayAdapter<String> adapterTen;
    private String[] listTen;
    private AutoCompleteTextView aedtChuyenBien;

    //lam viec voi menu
    @Override
    public boolean onCreateOptionsMenu(Menu manu) {
        getMenuInflater().inflate(R.menu.diemdd_activity_menu, manu);
        // return true so that the menu pop up is opened
        return true;
    }

    // Method này sử lý sự kiện khi MenuItem được chọn.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        switch(itemId)  {
            //ten cua cac id khi thiet ke cac resource menu
            case R.id.id_new :
                if (isBad(LOGIN_NAME)){
                    return true;
                }
                //wrtite ơn logic
                CheckAddNew();
                return true;
            case R.id.id_delete :
                //wrtite ơn logic
                if (arrDiemDD.size()==0){
                    return true;
                }
                if (isBad(LOGIN_NAME)){
                    return true;
                }
                if (!comPare(LOGIN_NAME,tvUserName.getText()+"") && !LOGIN_NAME.substring(0,5).equals("admin")){
                    Toast.makeText(DiemDDActivity.this, "Dữ liệu được bảo vệ...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                //lay ra index hien tai cua listview
                if (lvPossition==-1){
                    Toast.makeText(this, "Chưa chọn đúng dữ liệu cần xóa", Toast.LENGTH_SHORT).show();
                }else {
                    final DiemDD diemdd =customadapterData.get(lvPossition);
                    // Hỏi trước khi xóa.
                    new AlertDialog.Builder(this)
                            .setTitle("DQP Client")
                            .setMessage(diemdd.getEatername()+ "\n\n" + "Có chắc xóa?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    CheckDelete(diemdd);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
                return true;
            case R.id.like:
                if (edit_mode!="NEW"){
                    edit_mode="EDIT";
                }
                diemEater=1;
                SaveRecord();
                return true;
            case R.id.diskLike:
                if (edit_mode!="NEW"){
                    edit_mode="EDIT";
                }
                //Khi disLike thi nguo dc like bi tru 1 diem va nguoi disklike khong bi anh huong
                diemEater=-1;
                diemFeeder=0;
                SaveRecord();
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
            lvPossition=lvDiemDD.pointToPosition( (int) x, (int) y );
            if (lvPossition<0){
                return true;
            }
            gotoRec(lvPossition);
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
            lvPossition=lvDiemDD.pointToPosition( (int) e1.getX(), (int) e1.getY() );
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (lvPossition<0){
                    return true;
                }
                gotoRec(lvPossition);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diem_dd);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);

        addControls();imgLike.setVisibility(View.INVISIBLE);
        addEvents();
        initialization();
        setAdapter();
//        initData();
    }

    private void addControls() {
        tvId=findViewById(R.id.tv_DiemDDId);
        tvRkey=findViewById(R.id.tv_DiemDDRkey);
        tvServerKey=findViewById(R.id.tv_DiemDDServerKey);
        tvUserName=findViewById(R.id.tv_DiemDDUserName);
        group =findViewById(R.id.radiogroup_DiemDD);
        radioTT=findViewById(R.id.radio_TT);
        radioTV=findViewById(R.id.radio_TV);
        radioLB=findViewById(R.id.radio_LB);
        aedtEater=findViewById(R.id.aedt_DiemDDEater);
        edtNgayPs=findViewById(R.id.edt_DiemDDNgayPS);
        edtLydo=findViewById(R.id.edt_DiemDDLydo);
        lvDiemDD=findViewById(R.id.lv_DiemDD);
        aedtChuyenBien=findViewById(R.id.aedt_DiemDDChuyenBien);
        imgLike=findViewById(R.id.img_DiemDDLike);
    }

    private void initialization() {
        arrWorkingChuyenBien = crudLocaldb.ChuyenBien_getOnlyWorkingChuyenBien();
        arrDiemDD = crudLocaldb.DiemDD_getDiemDDByUserName(LOGIN_NAME);
        rKeyChuyenBien = crudLocaldb.ChuyenBien_getRkeyByShipmaster(LOGIN_NAME);

        ArrayAdapter<String> AdapterChuyenBien = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, crudLocaldb.ChuyenBien_listChuyenBien());
        aedtChuyenBien.setAdapter(AdapterChuyenBien);

        CheckAddNew();
    }


    private void addEvents() {
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_TT:
                        if (edit_mode == "NEW") {
                            aedtEater.setText("");
                            aedtChuyenBien.setFocusable(false);
                            aedtChuyenBien.setFocusableInTouchMode(false);
                            listTen = crudLocaldb.Users_listUsersHonourName(arrWorkingChuyenBien);
                            adapterTen = new ArrayAdapter<String>(DiemDDActivity.this, android.R.layout.simple_list_item_1, listTen);
                            aedtEater.setAdapter(adapterTen);
                            aedtEater.setText("");
                            edtNgayPs.setText("");
                            edtLydo.setText("");
                            imgLike.setVisibility(View.INVISIBLE);
                        }


                        break;
                    case R.id.radio_TV:
                        if (edit_mode == "NEW") {
                            aedtChuyenBien.setFocusable(true);
                            aedtChuyenBien.setFocusableInTouchMode(true);
                            listTen = crudLocaldb.DSTV_listThuyenVien(rKeyChuyenBien);
                            adapterTen = new ArrayAdapter<String>(DiemDDActivity.this, android.R.layout.simple_list_item_1, listTen);
                            aedtEater.setAdapter(adapterTen);
                            aedtEater.setText("");
                            edtNgayPs.setText("");
                            edtLydo.setText("");
                            imgLike.setVisibility(View.INVISIBLE);
                        }

                        break;
                    case R.id.radio_LB:
                        if (edit_mode == "NEW") {
                            aedtChuyenBien.setFocusable(false);
                            aedtChuyenBien.setFocusableInTouchMode(false);
                            listTen = crudLocaldb.Users_listLinhBoHonourName(arrWorkingChuyenBien);
                            adapterTen = new ArrayAdapter<String>(DiemDDActivity.this, android.R.layout.simple_list_item_1, listTen);
                            aedtEater.setAdapter(adapterTen);
                            aedtEater.setText("");
                            edtNgayPs.setText("");
                            edtLydo.setText("");
                            imgLike.setVisibility(View.INVISIBLE);
                        }
                        break;
                    default:
                }

            }
        });
        aedtChuyenBien.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();

                ListAdapter listAdapter = aedtChuyenBien.getAdapter();
                for(int i = 0; i < listAdapter.getCount(); i++) {
                    String temp = listAdapter.getItem(i).toString();
                    if(str.compareTo(temp) == 0) {
                        rKeyChuyenBien=crudLocaldb.ChuyenBien_getRkeyChuyenBien(str);
                        radioTV.setChecked(true);
                        listTen=crudLocaldb.DSTV_listThuyenVien(rKeyChuyenBien);
                        adapterTen = new ArrayAdapter<String>(DiemDDActivity.this, android.R.layout.simple_list_item_1, listTen);
                        aedtEater.setAdapter(adapterTen);
                        if (aedtEater.getText()+""!=""){
                            String str2 = aedtEater.getText()+"";
                            for(int j = 0; j < adapterTen.getCount(); i++) {
                                String temp2 = adapterTen.getItem(i).toString();
                                if(str2.compareTo(temp2) == 0) {
                                    return;
                                }
                            }
                            aedtEater.setText("");
                        }

                        return;
                    }
                }
            }
        });

        aedtChuyenBien.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (edit_mode=="VIEW"){return;}
                if(!hasFocus) {
                    String str = aedtChuyenBien.getText().toString();

                    ListAdapter listAdapter = aedtChuyenBien.getAdapter();
                    for(int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if(str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    aedtChuyenBien.setText("");

                }
            }
        });




        aedtEater.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (edit_mode=="VIEW"){return;}
                if(!hasFocus) {
                    // on focus off
                    String str = aedtEater.getText().toString();

                    ListAdapter listAdapter = aedtEater.getAdapter();
                    for(int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if(str.compareToIgnoreCase(temp) == 0) {
                            aedtEater.setText(temp);
                            return;
                        }
                    }
                    aedtEater.setText("");

                }
            }
        });

        edtNgayPs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(edtNgayPs);
            }
        });
        edtNgayPs.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edtNgayPs.setText("");
                return true;
            }
        });
        edtLydo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                hideSoftKeyboard(DiemDDActivity.this);
                return false;
            }
        });


        lvDiemDD.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                 mGestureDetector.onTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
            }
        });

    }

    private void gotoRec(int position){
        if (position<0){return;}
        DiemDD diemdd = new DiemDD();
        lvPossition=position;
        diemdd=arrDiemDD.get(position);
        if (comPare(diemdd.getChucvu(),"Nhân lực")){
            radioTV.setChecked(true);
            chucVu="Nhân lực";
        }else{
            if (comPare(diemdd.getChucvu(),"Thuyền trưởng")){
                radioTT.setChecked(true);
                chucVu="Thuyền trưởng";
            }
            else{
                radioLB.setChecked(true);
                chucVu="Lính trên bờ";
            }
        }
        tvId.setText(String.valueOf(diemdd.getId()));
        tvRkey.setText(String.valueOf(diemdd.getRkey()));
        tvServerKey.setText(String.valueOf(diemdd.getServerkey()));
        tvUserName.setText(diemdd.getUsername());
        aedtEater.setText(diemdd.getEatername());
        aedtChuyenBien.setText(diemdd.getChuyenbien());
        edtNgayPs.setText(diemdd.getNgayps()+"");
        edtLydo.setText(diemdd.getLydo());
        imgLike.setVisibility(View.VISIBLE);
        if (diemdd.getDiemeater()==1){
            imgLike.setImageResource(R.drawable.like);
        }else {
            imgLike.setImageResource(R.drawable.dislike);
        }
        edit_mode="VIEW";
        setEditMod(false);
    }

    private void CheckAddNew(){
        edit_mode="NEW";
        tvRkey.setText("");
        tvId.setText("");
        tvServerKey.setText("");
        tvUserName.setText(LOGIN_NAME);
        if (isThuyenTruong()){
            aedtChuyenBien.setText(crudLocaldb.ChuyenBien_getTenChuyenBien(rKeyChuyenBien)+"");
            aedtEater.requestFocus();
        }else{
            aedtChuyenBien.requestFocus();
        }
        if (radioTT.isChecked()){
            aedtEater.setText("");
            aedtChuyenBien.setFocusable(false);
            aedtChuyenBien.setFocusableInTouchMode(false);
            listTen = crudLocaldb.Users_listUsersHonourName(arrWorkingChuyenBien);
            adapterTen = new ArrayAdapter<String>(DiemDDActivity.this, android.R.layout.simple_list_item_1, listTen);
            aedtEater.setAdapter(adapterTen);
            aedtEater.setText("");
            edtNgayPs.setText("");
            edtLydo.setText("");
            imgLike.setVisibility(View.INVISIBLE);
        }else{
            radioTT.setChecked(true);
        }
        setEditMod(true);
    }

    private void CheckDelete(DiemDD diemdd) {
        if (diemdd.getServerkey()!=0){
            WantDeleteFromServer wdfs=new WantDeleteFromServer();
            wdfs.setmServerkey(diemdd.getServerkey());
            wdfs.setmTablename("diemdd");
            crudLocaldb.WDFS_addWDFS(wdfs);
        }
        crudLocaldb.DiemDD_deleteDiemDD(diemdd.getRkey());
        //remove from array list
        Predicate<DiemDD> personPredicate = p-> p.getId() == diemdd.getId();
        arrDiemDD.removeIf(personPredicate);
        //refesh screen
        tvRkey.setText("");
        tvId.setText("");
        tvServerKey.setText("");
        tvUserName.setText(LOGIN_NAME);
        aedtEater.setText("");
        aedtChuyenBien.setText("");
        edtNgayPs.setText("");
        edtLydo.setText("");
        imgLike.setVisibility(View.INVISIBLE);
        updateListDiemDD();
    }

    private void SaveRecord(){
        DiemDD diemdd=new DiemDD();
        if(aedtEater.getText()+""=="" || edtNgayPs.getText()+""=="") {
            Toast.makeText(getApplicationContext(), "Cần nhập vào đủ thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }
        String loginName=crudLocaldb.Users_getUserNameByHonourName(aedtEater.getText()+"");
        if(comPare(loginName,LOGIN_NAME)) {
            Toast.makeText(getApplicationContext(), "Không được tự sướng.. :).", Toast.LENGTH_SHORT).show();
            return;
        }
        if(isBad(edtLydo.getText()+"")) {
            Toast.makeText(getApplicationContext(), "Cần có lý do.. :).", Toast.LENGTH_SHORT).show();
            return;
        }
        int isChecked=group.getCheckedRadioButtonId();
        long eater=-1;
        long feeder=-1;
        String cv="";

        switch (isChecked){
            case R.id.radio_TT:
                cv="Thuyền trưởng";

                break;
            case R.id.radio_TV:
                cv="Nhân lực";
                break;
            case R.id.radio_LB:
                cv="Lính trên bờ";
                break;

        }
        String cvState="";
        if (edit_mode=="NEW"){
            cvState=cv;
        }else if (edit_mode=="EDIT"){
            cvState=chucVu;
        }
        switch (cvState){
               case "Thuyền trưởng":
                   eater=crudLocaldb.Users_getServerKeyByHonourName(aedtEater.getText()+"");
                   diemdd.setChuyenbien("");
                   break;
               case "Nhân lực":
                   if(aedtChuyenBien.getText()+""=="") {
                       Toast.makeText(getApplicationContext(), "Cần nhập vào đủ thông tin.", Toast.LENGTH_SHORT).show();
                       return;
                   }
                   long rKeyCb=crudLocaldb.ChuyenBien_getRkeyChuyenBien(aedtChuyenBien.getText()+"");
                   eater=crudLocaldb.DSTV_getRkeyThuyenVien(aedtEater.getText()+"",rKeyCb);
                   diemdd.setChuyenbien(aedtChuyenBien.getText()+"");
                   break;
               case "Lính trên bờ":
                   eater=crudLocaldb.Users_getServerKeyByHonourName(aedtEater.getText()+"");
                   diemdd.setChuyenbien("");
                   break;

        }
        diemdd.setEater(eater);
        diemdd.setEatername(aedtEater.getText()+"");
        feeder=crudLocaldb.Users_getServerKeyByEmail(LOGIN_NAME);
        diemdd.setFeeder(feeder);
        diemdd.setDiemeater(diemEater);
        if (diemEater==1 && edit_mode=="NEW"){ // user nhan nut like
            // neu nguoi dc like la thuyen truong thi user se co 1 diem
            if (cv=="Thuyền trưởng"){
                diemFeeder=1;
            }else{
                diemFeeder=0;
            }
        }
        if (diemEater==1 && edit_mode=="EDIT"){ // user nhan nut like
            // neu nguoi dc like la thuyen truong thi user se co 1 diem
            if (chucVu=="Thuyền trưởng"){
                diemFeeder=1;
            }else{
                diemFeeder=0;
            }
        }
        diemdd.setDiemfeeder(diemFeeder);
        diemdd.setLydo(edtLydo.getText()+"");
        diemdd.setNgayps(edtNgayPs.getText()+"");
        diemdd.setUpdatetime(getCurrentTimeMiliS());
        diemdd.setUsername(LOGIN_NAME);
        imgLike.setVisibility(View.VISIBLE);
        if (diemEater==1){
            imgLike.setImageResource(R.drawable.like);
        }else{
            imgLike.setImageResource(R.drawable.dislike);
        }

        if (edit_mode=="NEW" && longGet(tvRkey.getText()+"")==0) {
            diemdd.setServerkey(0);
            diemdd.setRkey(longGet(getCurrentTimeMiliS()));
            diemdd.setChucvu(cv);
            if (diemdd != null) {
                long i = crudLocaldb.DiemDD_addDiemDD(diemdd);
                if (i != -1) {
                    setEditMod(false);
                    updateListDiemDD();
                    arrDiemDD=crudLocaldb.DiemDD_getDiemDDByUserName(LOGIN_NAME);
                    gotoRec(arrDiemDD.size()-1);
                }
            }
        }else{
            if (edit_mode=="EDIT") {
                diemdd.setId(intGet(tvId.getText()+""));
                diemdd.setRkey(longGet(tvRkey.getText()+""));
                diemdd.setServerkey(intGet(tvServerKey.getText()+""));
                diemdd.setChucvu(chucVu);
                int result = crudLocaldb.DiemDD_updateDiemDD(diemdd);
                if (result > 0) {
                    setEditMod(false);
                    updateListDiemDD();
                }
            }
        }
    }

    private void setAdapter() {
        arrDiemDD=crudLocaldb.DiemDD_getDiemDDByUserName(LOGIN_NAME);
        customadapterData.addAll(arrDiemDD);
        if (customAdapter == null) {
            // gan data source cho adapter
            customAdapter = new CustomAdapterDiemDD(DiemDDActivity.this, R.layout.customlist_diemdd,customadapterData);
            //gan adapter cho spinner
            lvDiemDD.setAdapter(customAdapter);
        }else{
            updateListDiemDD();
        }
    }
    //gett all to list
    public void updateListDiemDD(){
        customadapterData.clear();
        arrDiemDD=crudLocaldb.DiemDD_getDiemDDByUserName(LOGIN_NAME);
        customadapterData.addAll(arrDiemDD);
        customAdapter.notifyDataSetChanged();
    }

    public void showDatePickerDialog(final EditText edtViewDate) {
        hideSoftKeyboard(DiemDDActivity.this);
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
        if (!utils.isDate(utils.getEditText(this,edtViewDate))){
            //Dinh dang lai kieu ngay hien tai
            cal=Calendar.getInstance();
            SimpleDateFormat dft=new SimpleDateFormat("dd/MM/yyyy");
            //gan ngay thang hien tai da dc dinh dang cho s
            s=dft.format(cal.getTime());
        }else {
            s=utils.getEditText(this,edtViewDate);
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

    private  void setEditMod(boolean chohaykhong){
        if (chohaykhong==true){
            aedtChuyenBien.setFocusable(true);
            aedtChuyenBien.setFocusableInTouchMode(true);
            aedtEater.setFocusable(true);
            aedtEater.setFocusableInTouchMode(true);
            edtLydo.setFocusable(true);
            edtLydo.setFocusableInTouchMode(true);
        }else {
            edit_mode="VIEW";
            aedtChuyenBien.setFocusable(false);
            aedtChuyenBien.setFocusableInTouchMode(false);
            aedtEater.setFocusable(false);
            aedtEater.setFocusableInTouchMode(false);
            edtLydo.setFocusable(false);
            edtLydo.setFocusableInTouchMode(false);
            edtNgayPs.setFocusable(false);
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
