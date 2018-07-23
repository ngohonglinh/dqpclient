package com.dqpvn.dqpclient;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.restfullAPI;
import com.dqpvn.dqpclient.customadapters.CustomAdapterDebtBook;
import com.dqpvn.dqpclient.models.ChuyenBien;
import com.dqpvn.dqpclient.models.DSTV;
import com.dqpvn.dqpclient.models.DebtBook;
import com.dqpvn.dqpclient.models.ResponseFromServer;
import com.dqpvn.dqpclient.models.Ticket;
import com.dqpvn.dqpclient.models.WantDeleteFromServer;
import com.dqpvn.dqpclient.utils.MyContextWrapper;
import com.dqpvn.dqpclient.utils.NumberTextWatcher;
import com.dqpvn.dqpclient.utils.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_OK;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.MY_SERVER;
import static com.dqpvn.dqpclient.utils.utils.comPare;
import static com.dqpvn.dqpclient.utils.utils.getCurrentTimeMiliS;
import static com.dqpvn.dqpclient.utils.utils.hideSoftKeyboard;
import static com.dqpvn.dqpclient.utils.utils.intGet;
import static com.dqpvn.dqpclient.utils.utils.isBad;
import static com.dqpvn.dqpclient.utils.utils.longGet;

public class DebtBookActivity extends AppCompatActivity {

    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;


    final private String TAG = getClass().getSimpleName();
    
    private final int REQUEST_START_DEBT=555;
    private int mShowMenuSave=0;
    private boolean needRefresh;
    private Calendar cal;
    private AutoCompleteTextView aedtChuyenBien, aedtThuyenVien;
    private EditText edtNgayps, edtLydo, edtSotien;
    private TextView tvId, tvRkey, tvServerKey, tvRkeyThuyenVien, tvRkeyTicket,tvUserName;
    private ListView lvDebtBook;
    private CheckBox chkAutoChuyenBien;
    private crudLocal crudLocaldb=crudLocal.getInstance(this);
    private ArrayList<DebtBook>customadapterData=new ArrayList<>();
    private ArrayList<DebtBook> arrDebtBook = new ArrayList<>();
    private ArrayList<ChuyenBien>arrWorkingChuyenBien=new ArrayList<>();
    private CustomAdapterDebtBook customAdapter;
    private ArrayAdapter<String> adapterCB, adapterTV;
    private String[] ChuyenBien_listChuyenBien;
    private String[] DSTV_listThuyenVien;

    private  int lvPossition=-1;
    private long intentRkeyTicket, intentRkeyChuyenBien, intentRkeyThuyenVien;
    private String edit_mode="VIEW",intentUserName, doingChuyenBien, tenThuyenVien, whoStart;
    private String autoChuyenBien, autoNgayps, autoLydo;


    //lam viec voi menu
    @Override
    public boolean onCreateOptionsMenu(Menu manu) {
        getMenuInflater().inflate(R.menu.record_menu, manu);
        MenuItem mSave = manu.findItem(R.id.save);
        if (mShowMenuSave==1){
            mSave.setVisible(true);
        }else{
            mSave.setVisible(false);
        }
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
                if (isBad(intentUserName)){
                    return true;
                }
                if (isOtherThuyenTruong()){
                    Toast.makeText(DebtBookActivity.this, "Anh không thể cho thuyền viên tàu khác ứng tiền :)", Toast.LENGTH_SHORT).show();
                    return true;
                }
                //wrtite ơn logic
                CheckAddNew();
                return true;
            case R.id.id_edit :
                if (arrDebtBook.size()==0){
                    return true;
                }
                if (isBad(intentUserName)){
                    return true;
                }
                if (!comPare(intentUserName,tvUserName.getText()+"") && !intentUserName.substring(0,5).equals("admin")){
                    Toast.makeText(DebtBookActivity.this, "Dữ liệu được bảo vệ...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                CheckEdit();
                return true;
            case R.id.id_delete :
                //wrtite ơn logic
                if (arrDebtBook.size()==0){
                    return true;
                }
                if (isBad(intentUserName)){
                    return true;
                }
                if (!TextUtils.equals(intentUserName,tvUserName.getText()+"") && !intentUserName.substring(0,5).equals("admin")){
                    Toast.makeText(DebtBookActivity.this, "Dữ liệu được bảo vệ...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                //lay ra index hien tai cua listview
                if (lvPossition==-1){
                    Toast.makeText(this, "Chưa chọn đúng dữ liệu cần xóa", Toast.LENGTH_SHORT).show();
                }else {
                    final DebtBook debtbook =arrDebtBook.get(lvPossition);
                    // Hỏi trước khi xóa.
                    new AlertDialog.Builder(this)
                            .setTitle("DQP Client")
                            .setMessage(debtbook.getTen()+ "\n\n" + "Có chắc xóa?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    CheckDelete(debtbook);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
                return true;
            case R.id.save:
                SaveRecord();
                hideSoftKeyboard(DebtBookActivity.this);
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
            lvPossition=lvDebtBook.pointToPosition( (int) x, (int) y );
            if (lvPossition<0){
                return true;
            }
            gotoOnSelect(lvPossition);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(TAG, "onLongPress");
        }


        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(TAG, "onScroll");
            return false;
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            lvPossition=lvDebtBook.pointToPosition( (int) e1.getX(), (int) e1.getY() );
            gotoOnSelect(lvPossition);

            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                gotoDebtBookDetail(lvPossition);
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

            return false;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void gotoOnSelect(int position){
        if (position<0){return;}
        lvPossition=position;
        DebtBook debtbook = new DebtBook();
        debtbook=arrDebtBook.get(position);
        tvId.setText(String.valueOf(debtbook.getId()));
        tvRkey.setText(String.valueOf(debtbook.getRkey()));
        tvServerKey.setText(String.valueOf(debtbook.getServerkey()));
        tvRkeyThuyenVien.setText(debtbook.getRkeythuyenvien()+"");
        tvRkeyTicket.setText(debtbook.getRkeyticket()+"");
        aedtChuyenBien.setText(debtbook.getChuyenbien());
        aedtThuyenVien.setText(crudLocaldb.DSTV_getTenThuyenVien(longGet(tvRkeyThuyenVien.getText()+"")));
        edtLydo.setText(debtbook.getLydo());
        edtNgayps.setText(debtbook.getNgayps());
        edtSotien.setText(formatNumber(debtbook.getSotien()+""));
        tvUserName.setText(debtbook.getUsername());
        setEditMod(false);
    }

    private void gotoDebtBookDetail(int position){
        if (position<0){return;}
        lvPossition=position;
        DebtBook debtBook = new DebtBook();
        debtBook=arrDebtBook.get(position);
        ArrayList<DebtBook>chiTietNoThuyenVien=new ArrayList<>();
        String TenChuyenBien=aedtChuyenBien.getText()+"";
        long rKeyChuyenBien=crudLocaldb.ChuyenBien_getRkeyChuyenBien(TenChuyenBien);
        chiTietNoThuyenVien=crudLocaldb.DebtBook_getDebtBookByChuyenBienAndThuyenVien(TenChuyenBien,debtBook.getRkeythuyenvien());
        if (chiTietNoThuyenVien.size()>0) {
            Intent intent=new Intent(DebtBookActivity.this, DebBookDetailActivity.class);
            //find right Ticket....
            ArrayList<Ticket>arrTicket=new ArrayList<>();
            arrTicket=crudLocaldb.Ticket_getOpenTicketByUser(intentUserName);
            long rkeyTicket=0;
            if (arrTicket.size()>0){
                // get last open ticket for user
                for (int i=0;i<arrTicket.size();i++){
                    if (arrTicket.get(i).getFinished()==0){
                        rkeyTicket=arrTicket.get(i).getRkey();
                        break;
                    }
                }
            }
            intent.putExtra("rkeyThuyenVien", debtBook.getRkeythuyenvien());
            intent.putExtra("tenChuyenBien", TenChuyenBien);
            intent.putExtra("rkeyChuyenBien", rKeyChuyenBien);
            intent.putExtra("rkeyTicket", rkeyTicket);
            intent.putExtra("userName", intentUserName);
            intent.putExtra("whoStart","thuyenTruong");
            startActivityForResult(intent, REQUEST_START_DEBT);
        }else {
            Toast.makeText(DebtBookActivity.this, "Nhân lực này vẫn chưa ứng tiền", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt_book);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        DebtBookActivity.CustomGestureDetector customGestureDetector = new DebtBookActivity.CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);

        addControls();
        addEvents();
        initialization();
    }


    private void addControls() {
        tvId=findViewById(R.id.tv_DebtBookId);
        tvRkey=findViewById(R.id.tv_DebtBookRkey);
        tvServerKey=findViewById(R.id.tv_DebtBookServerKey);
        tvRkeyThuyenVien=findViewById(R.id.tv_DebtBookRkeyTV);
        tvRkeyTicket=findViewById(R.id.tv_DebtBookRkeyTicket);
        tvUserName=findViewById(R.id.tv_DebtBookUserName);
        aedtChuyenBien=findViewById(R.id.aedt_DebtBookChuyenBien);
        aedtThuyenVien=findViewById(R.id.aedt_DebtBookTenTV);
        edtLydo=findViewById(R.id.edt_DebtBookLydo);
        edtNgayps=findViewById(R.id.edt_DebtBookNgayPS);
        edtSotien=findViewById(R.id.edt_DebtBookSoTien);
        lvDebtBook=findViewById(R.id.lv_DebtBook);
        chkAutoChuyenBien=findViewById(R.id.chk_DebtBookAutoChuyenBien);

    }

    private void addEvents() {

        chkAutoChuyenBien.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (chkAutoChuyenBien.isChecked()){
                    if (aedtChuyenBien.getText()+""==""|| edtLydo.getText()+""=="" || edtNgayps.getText()+""==""){
                        chkAutoChuyenBien.setChecked(false);
                        return;
                    }
                    autoChuyenBien=aedtChuyenBien.getText()+"";
                    autoLydo=edtLydo.getText()+"";
                    autoNgayps=edtNgayps.getText()+"";
                    aedtChuyenBien.setFocusable(false);
                    edtLydo.setFocusable(false);
                }else{
                    if (whoStart!="thuyenTruong"){
                        aedtChuyenBien.setFocusable(true);
                        aedtChuyenBien.setFocusableInTouchMode(true);
                    }
                    edtLydo.setFocusable(true);
                    edtLydo.setFocusableInTouchMode(true);
                }
            }
        });

        edtNgayps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(edtNgayps);
            }
        });
        edtNgayps.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edtNgayps.setText("");
                return true;
            }
        });

        //edtSotien.addTextChangedListener(new NumberTextWatcher(edtSotien));
        edtSotien.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    edtSotien.setText(formatNumber(edtSotien.getText()+""));
                }
            }
        });

        lvDebtBook.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mGestureDetector.onTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
                // return true;
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
                        long idChuyenBien=crudLocaldb.ChuyenBien_getRkeyChuyenBien(temp);
                        DSTV_listThuyenVien=crudLocaldb.DSTV_listThuyenVien(idChuyenBien);
                        adapterTV = new ArrayAdapter<String>(DebtBookActivity.this, android.R.layout.simple_list_item_1, DSTV_listThuyenVien);
                        aedtThuyenVien.setAdapter(adapterTV);
                        return;
                    }
                }
            }
        });
        aedtChuyenBien.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    // on focus off
                    String str = aedtChuyenBien.getText().toString();

                    ListAdapter listAdapter = aedtChuyenBien.getAdapter();
                    for(int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if(str.compareTo(temp) == 0) {
                            return;
                        }
                    }

                    aedtChuyenBien.setText("");
                    if (comPare(whoStart,"admin")){
                        updateListDebtBook();
                    }

                }
            }
        });

        aedtThuyenVien.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    // on focus off
                    String str = aedtThuyenVien.getText().toString();

                    ListAdapter listAdapter = aedtThuyenVien.getAdapter();
                    for(int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if(str.compareToIgnoreCase(temp) == 0) {
                            aedtThuyenVien.setText(temp);
                            return;
                        }
                    }

                    aedtThuyenVien.setText("");

                }
            }
        });

        edtSotien.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (edit_mode!="VIEW"){
                        SaveRecord();
                        CheckAddNew();
                    }
                }
                return false;
            }
        });

    }

    private void initialization() {
        Intent intent=getIntent();
        //arrWorkingChuyenBien=crudLocaldb.ChuyenBien_getOnlyWorkingChuyenBien();
        ChuyenBien_listChuyenBien=crudLocaldb.ChuyenBien_listChuyenBien();
        adapterCB = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ChuyenBien_listChuyenBien);
        aedtChuyenBien.setAdapter(adapterCB);

        whoStart=intent.getStringExtra("whoStart");
        intentUserName=intent.getStringExtra("userName");
        intentRkeyTicket = intent.getLongExtra("rkeyTicket",0);

            if (comPare(whoStart,"thuyenTruong")){
                //start by thuyen truong
                intentRkeyChuyenBien = intent.getLongExtra("rkeyChuyenBien",0);
                doingChuyenBien=crudLocaldb.ChuyenBien_getTenChuyenBien(intentRkeyChuyenBien);
                aedtChuyenBien.setText(doingChuyenBien);
                DSTV_listThuyenVien=crudLocaldb.DSTV_listThuyenVien(intentRkeyChuyenBien);
                aedtChuyenBien.setFocusable(false);
            }
        setAdapter();
        if (intent.getBooleanExtra("makeNew",false)){
            //re quest make new record
            CheckAddNew();
        }else{
            initData();
        }
    }

    private  void initData(){

        if (comPare(whoStart,"admin")){
            if (comPare(aedtChuyenBien.getText()+"","")){
                //arrDebtBook=crudLocaldb.DebtBook_getDebtBookByAllWorkingChuyenBien(arrWorkingChuyenBien);
                arrDebtBook=crudLocaldb.DebtBook_getDebtBookByTicket(intentRkeyTicket);
            }else{
                arrDebtBook=crudLocaldb.DebtBook_getDebtBookByChuyenBien(aedtChuyenBien.getText()+"");
            }
        }else if(comPare(whoStart,"thuyenTruong")){
            arrDebtBook=crudLocaldb.DebtBook_getDebtBookByChuyenBien(doingChuyenBien);
        }else{
            arrDebtBook=crudLocaldb.DebtBook_getDebtBookByTicket(intentRkeyTicket);
        }

        if (arrDebtBook.size()>=1) { //phong truong hop null k co record nao
            DebtBook debtbook =arrDebtBook.get(arrDebtBook.size()-1);
            lvDebtBook.setSelection(arrDebtBook.size()-1);
            tvRkey.setText(debtbook.getRkey()+"");
            tvId.setText(String.valueOf(debtbook.getId()));
            tvServerKey.setText(debtbook.getServerkey()+"");
            tvRkeyThuyenVien.setText(debtbook.getRkeythuyenvien()+"");
            tvRkeyTicket.setText(debtbook.getRkeyticket()+"");
            aedtChuyenBien.setText(debtbook.getChuyenbien());
            aedtThuyenVien.setText(debtbook.getTen());
            edtSotien.setText(formatNumber(debtbook.getSotien()));
            edtNgayps.setText(debtbook.getNgayps());
            edtLydo.setText(debtbook.getLydo());
            tvUserName.setText(debtbook.getUsername()+"");
            setEditMod(false);
        }else{
            CheckAddNew();
        }
    }

    private void CheckAddNew(){
        tvRkey.setText("");
        tvId.setText("");
        tvServerKey.setText("");
        tvRkeyThuyenVien.setText("");
        tvRkeyTicket.setText(intentRkeyTicket+"");
        if (!comPare(whoStart,"thuyenTruong")){
            aedtChuyenBien.setText("");
        }else{
            aedtChuyenBien.setText(doingChuyenBien);
        }
        aedtThuyenVien.setText("");
        edtSotien.setText("");
        edtNgayps.setText("");
        edtLydo.setText("");
        tvUserName.setText(intentUserName);
        edit_mode="NEW";
        setEditMod(true);
        if (chkAutoChuyenBien.isChecked()){
            aedtChuyenBien.setText(autoChuyenBien);
            edtNgayps.setText(autoNgayps);
            edtLydo.setText(autoLydo);
            aedtThuyenVien.requestFocus();
        }

    }

    private void CheckEdit(){
        edit_mode="EDIT";
        setEditMod(true);
    }

    private void CheckDelete(DebtBook debtbook) {
        if (debtbook.getServerkey()!=0){
            WantDeleteFromServer wdfs=new WantDeleteFromServer();
            wdfs.setmServerkey(debtbook.getServerkey());
            wdfs.setmTablename("debtbook");
            crudLocaldb.WDFS_addWDFS(wdfs);
        }
        //Store last value to calculate Data after delete
        intentRkeyThuyenVien=longGet(tvRkeyThuyenVien.getText()+"");
        intentRkeyChuyenBien=crudLocaldb.ChuyenBien_getRkeyChuyenBien(aedtChuyenBien.getText()+"");
        tenThuyenVien=aedtThuyenVien.getText()+"";
        doingChuyenBien=aedtChuyenBien.getText()+"";


        crudLocaldb.DebtBook_deleteDebtBook(debtbook.getRkey());
        //remove from array list
        Predicate<DebtBook> personPredicate = p-> p.getId() == debtbook.getId();
        arrDebtBook.removeIf(personPredicate);
        //refesh screen
        tvRkey.setText("");
        tvId.setText("");
        tvServerKey.setText("");
        tvRkeyThuyenVien.setText("");
        tvRkeyTicket.setText(intentRkeyTicket+"");
        aedtChuyenBien.setText("");
        aedtThuyenVien.setText("");
        edtSotien.setText("");
        edtNgayps.setText("");
        edtLydo.setText("");
        tvUserName.setText(intentUserName);
        // Refresh ListView.
        this.needRefresh=true;
        CapNhatHauCanh();
        updateListDebtBook();
    }

    private void SaveRecord(){
        DebtBook debtbook=new DebtBook();
        if(aedtThuyenVien.getText()+""=="" || longGet(edtSotien.getText()+"")==0 || aedtChuyenBien.getText()+""=="" || edtNgayps.getText()+""=="") {
            Toast.makeText(getApplicationContext(), "Cần nhập vào đủ thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }
        doingChuyenBien=aedtChuyenBien.getText()+"";
        intentRkeyChuyenBien=crudLocaldb.ChuyenBien_getRkeyChuyenBien(doingChuyenBien);
        tenThuyenVien=aedtThuyenVien.getText()+"";
        intentRkeyThuyenVien=crudLocaldb.DSTV_getRkeyThuyenVien(tenThuyenVien,intentRkeyChuyenBien);

        if (intentRkeyChuyenBien==0 || intentRkeyThuyenVien==0){
            Toast.makeText(this, "Cần chính xác tên Dự án và tên Nhân lực.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (edit_mode=="NEW" && longGet(tvRkey.getText()+"")==0){
            ArrayList<DebtBook>arrCuocBien=new ArrayList<>();
            arrCuocBien=crudLocaldb.DebtBook_getDebtBookByNgayPS(doingChuyenBien,edtNgayps.getText()+"");
            Boolean TimThay=false;
            if (arrCuocBien.size()>0){
                for (int i=0;i<arrCuocBien.size();i++){
                    if (utils.comPare(arrCuocBien.get(i).getTen(),tenThuyenVien) && comPare(arrCuocBien.get(i).getUsername(),intentUserName)){
                        TimThay=true;
                        break;
                    }
                }
            }

            if (TimThay){
                Toast.makeText(getApplicationContext(), tenThuyenVien + " | đã có trong danh sách cùng ngày", Toast.LENGTH_SHORT).show();
                return;
            }
        }



        long sotien=longGet(DebtBookActivity.this,edtSotien);

        debtbook.setRkeythuyenvien(intentRkeyThuyenVien);
        debtbook.setTen(aedtThuyenVien.getText()+"");
        debtbook.setChuyenbien(aedtChuyenBien.getText()+"");
        debtbook.setSotien(sotien+"");
        debtbook.setNgayps(edtNgayps.getText()+"");
        debtbook.setLydo(edtLydo.getText()+"");
        debtbook.setUpdatetime(getCurrentTimeMiliS());
        debtbook.setRkeyticket(intentRkeyTicket);
        debtbook.setUsername(intentUserName);
        if (edit_mode=="NEW" && longGet(tvRkey.getText()+"")==0) {
            //chuyenbien.set;);= new ChuyenBien(tenchuyenbien,tentau,ngaykhoihanh);
            debtbook.setServerkey(0);
            debtbook.setRkey(longGet(getCurrentTimeMiliS()));
            if (debtbook != null) {
                long i = crudLocaldb.DebtBook_addDebtBook(debtbook);
                if (i != -1) {
                    this.needRefresh=true;
                    setEditMod(false);
                    CapNhatHauCanh();
                    updateListDebtBook();
                }
            }
        }else{
            if (edit_mode=="EDIT") {
                debtbook.setId(intGet(tvId.getText()+""));
                debtbook.setRkey(longGet(tvRkey.getText()+""));
                debtbook.setServerkey(intGet(tvServerKey.getText()+""));
                int result = crudLocaldb.DebtBook_updateDebtBook(debtbook);
                if (result > 0) {
                    this.needRefresh=true;
                    setEditMod(false);
                    CapNhatHauCanh();
                    updateListDebtBook();
                }
            }
        }
    }
    private void CapNhatHauCanh(){
        //sum by ticket
        String chiByChi=crudLocaldb.Chi_SumDaChiTicket(intentRkeyTicket);
        String chiByDebt=crudLocaldb.DebtBook_SumDebtBookTicket(intentRkeyTicket);
        long tongchi=longGet(chiByChi)+longGet(chiByDebt);
        //Cap nhat Ticket
        crudLocaldb.Ticket_CapNhatChi(intentRkeyTicket, String.valueOf(tongchi), getCurrentTimeMiliS());

        //Cap nhat DSTV
        String tienmuon=crudLocaldb.DebtBook_SumForThuyenVien(intentRkeyThuyenVien, doingChuyenBien);
        crudLocaldb.DSTV_CapNhatTien(intentRkeyChuyenBien,tenThuyenVien,"0",tienmuon,"0",getCurrentTimeMiliS(), intentUserName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_START_DEBT ) {
            needRefresh = data.getBooleanExtra("needRefresh",false);
            // Refresh ListView
            if(needRefresh) {
                updateListDebtBook();
                this.needRefresh=true;
            }else{
                this.needRefresh=false;
            }
        }
    }



    private void setAdapter() {
        if (comPare(whoStart,"admin")){
            if (comPare(aedtChuyenBien.getText()+"","")){
                //arrDebtBook=crudLocaldb.DebtBook_getDebtBookByAllWorkingChuyenBien(arrWorkingChuyenBien);
                arrDebtBook=crudLocaldb.DebtBook_getDebtBookByTicket(intentRkeyTicket);
            }else{
                arrDebtBook=crudLocaldb.DebtBook_getDebtBookByChuyenBien(aedtChuyenBien.getText()+"");
            }
        }else if(comPare(whoStart,"thuyenTruong")){
            arrDebtBook=crudLocaldb.DebtBook_getDebtBookByChuyenBien(doingChuyenBien);
        }else{
            arrDebtBook=crudLocaldb.DebtBook_getDebtBookByTicket(intentRkeyTicket);
        }
        customadapterData.addAll(arrDebtBook);
        if (customAdapter == null) {
            // gan data source cho adapter
            customAdapter = new CustomAdapterDebtBook(DebtBookActivity.this, R.layout.customlist_debtbook,customadapterData);
            //gan adapter cho spinner
            lvDebtBook.setAdapter(customAdapter);
        }else{
            updateListDebtBook();
            lvDebtBook.setSelection(customAdapter.getCount()-1);
        }
    }
    //gett all to list
    public void updateListDebtBook(){
        customadapterData.clear();
        if (comPare(whoStart,"admin")){
            if (comPare(aedtChuyenBien.getText()+"","")){
                //arrDebtBook=crudLocaldb.DebtBook_getDebtBookByAllWorkingChuyenBien(arrWorkingChuyenBien);
                arrDebtBook=crudLocaldb.DebtBook_getDebtBookByTicket(intentRkeyTicket);
            }else{
                arrDebtBook=crudLocaldb.DebtBook_getDebtBookByChuyenBien(aedtChuyenBien.getText()+"");
            }
        }else if(comPare(whoStart,"thuyenTruong")){
            arrDebtBook=crudLocaldb.DebtBook_getDebtBookByChuyenBien(doingChuyenBien);
        }else{
            arrDebtBook=crudLocaldb.DebtBook_getDebtBookByTicket(intentRkeyTicket);
        }
        customadapterData.addAll(arrDebtBook);

        customAdapter.notifyDataSetChanged();
    }

    public void showDatePickerDialog(final EditText edtViewDate) {
        hideSoftKeyboard(DebtBookActivity.this);
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
    private String formatNumber(String str) {
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        long lv = utils.longGet(str);
        String get_value = formatter.format(lv);
        return get_value;
    }

    private  void setEditMod(boolean chohaykhong){
        if (chohaykhong==true){
            mShowMenuSave=1;
            aedtChuyenBien.setThreshold(1);
            aedtThuyenVien.setThreshold(1);
        }else {
            mShowMenuSave=0;
            aedtChuyenBien.setThreshold(100);
            aedtThuyenVien.setThreshold(100);
            edtNgayps.setFocusable(false);
            edit_mode="VIEW";
        }
        invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again
    }

    private Boolean isOtherThuyenTruong(){
        Boolean is =false;
        ArrayList<ChuyenBien>arrChuyenBien=new ArrayList<>();
        arrChuyenBien=crudLocaldb.ChuyenBien_getOnlyShowChuyenBien();
        for(int i=0;i<arrChuyenBien.size();i++){
            if (arrChuyenBien.get(i).getUsername().equals(intentUserName) &&
                    arrChuyenBien.get(i).getRkey()!=intentRkeyChuyenBien &&
                    !arrChuyenBien.get(i).getUsername().substring(0,5).equals("admin")){
                is=true;
                break;
            }
        }
        return is;
    }

    @Override
    public void finish() {
        // Chuẩn bị dữ liệu Intent.
        Intent data = new Intent();
        // Yêu cầu MainActivity refresh lại ListView hoặc không.
        data.putExtra("needRefresh", this.needRefresh);
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
