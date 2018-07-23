package com.dqpvn.dqpclient;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.customadapters.CustomAdapterDebtBookDetail;
import com.dqpvn.dqpclient.models.ChuyenBien;
import com.dqpvn.dqpclient.models.DebtBook;
import com.dqpvn.dqpclient.models.Ticket;
import com.dqpvn.dqpclient.models.WantDeleteFromServer;
import com.dqpvn.dqpclient.utils.MyContextWrapper;
import com.dqpvn.dqpclient.utils.NumberTextWatcher;
import com.dqpvn.dqpclient.utils.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.function.Predicate;

import static com.dqpvn.dqpclient.utils.utils.comPare;
import static com.dqpvn.dqpclient.utils.utils.getCurrentTimeMiliS;
import static com.dqpvn.dqpclient.utils.utils.hideSoftKeyboard;
import static com.dqpvn.dqpclient.utils.utils.intGet;
import static com.dqpvn.dqpclient.utils.utils.longGet;

public class DebBookDetailActivity extends AppCompatActivity {

    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;



    private boolean needRefresh=false,daChia=false;
    private Calendar cal;
    final private String TAG = getClass().getSimpleName();

    private TextView tvId,tvRkey, tvServerKey, tvRkeyThuyenVien, tvRkeyTicket;

    private  int lvPossition=-1;
    private long intentRkeyTicket, intentRkeyChuyenBien,intentRkeyThuyenVien;
    private int mShowMenuSave=0;
    private String edit_mode="VIEW",intentUserName, intentChuyenBien, tenThuyenVien;

    private EditText edtNgayps, edtLydo, edtSotien, edtUserName, edtTen;
    private ListView lvDebtBook;

    private crudLocal crudLocaldb=crudLocal.getInstance(this);
    private ArrayList<DebtBook> arrDebtBook = new ArrayList<>();
    private ArrayList<DebtBook>customadapterData=new ArrayList<>();
    private CustomAdapterDebtBookDetail customAdapter;

    //lam viec voi menu
    @Override
    public boolean onCreateOptionsMenu(Menu manu) {
        getMenuInflater().inflate(R.menu.record_menu, manu);

        if (daChia){
            for (int i = 0; i < manu.size(); i++){
                manu.getItem(i).setVisible(false);
            }
            return true;
        }

        MenuItem mSave = manu.findItem(R.id.save);
        if (mShowMenuSave==1){
            mSave.setVisible(true);
        }else{
            mSave.setVisible(false);
        }
        // return true so that the menu pop up is opened
        return true;
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
            lvPossition=lvDebtBook.pointToPosition( (int) e1.getX(), (int) e1.getY() );
            gotoOnSelect(lvPossition);

            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Intent intent=new Intent(DebBookDetailActivity.this, DebtBookByUserActivity.class);
                intent.putExtra("tenChuyenBien",intentChuyenBien);
                intent.putExtra("userName",edtUserName.getText().toString());
                startActivity(intent);
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
    public void gotoOnSelect(int position){
        if (position<0){return;}
        lvPossition=position;
        DebtBook debtbook = new DebtBook();
        debtbook=arrDebtBook.get(position);
        tvId.setText(debtbook.getId()+"");
        tvRkey.setText(String.valueOf(debtbook.getRkey()));
        tvServerKey.setText(String.valueOf(debtbook.getServerkey()));
        tvRkeyThuyenVien.setText(debtbook.getRkeythuyenvien()+"");
        tvRkeyTicket.setText(debtbook.getRkeyticket()+"");
        edtLydo.setText(debtbook.getLydo());
        edtNgayps.setText(debtbook.getNgayps());
        edtTen.setText(debtbook.getTen());
        edtSotien.setText(formatNumber(debtbook.getSotien()+""));
        edtUserName.setText(debtbook.getUsername()+"");
        setTitle("Chi tiết nợ | "+debtbook.getTen());
        setEditMod(false);
    }

    // Method này sử lý sự kiện khi MenuItem được chọn.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId)  {
            //ten cua cac id khi thiet ke cac resource menu
            case R.id.id_new :

                if (isOtherThuyenTruong()){
                    Toast.makeText(DebBookDetailActivity.this, "Anh không thể cho thuyền viên tàu khác ứng tiền :)", Toast.LENGTH_SHORT).show();
                    return true;
                }
                //wrtite ơn logic
                CheckAddNew();
                return true;
            case R.id.id_edit :
                if (arrDebtBook.size()==0){
                    return true;
                }
                if (!comPare(intentUserName,edtUserName.getText()+"") && !intentUserName.substring(0,5).equals("admin")){
                    Toast.makeText(DebBookDetailActivity.this, "Dữ liệu được bảo vệ...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                CheckEdit();
                return true;
            case R.id.id_delete :
                //wrtite ơn logic
                if (arrDebtBook.size()==0){
                    return true;
                }
                if (!TextUtils.equals(intentUserName,edtUserName.getText()+"") && !intentUserName.substring(0,5).equals("admin")){
                    Toast.makeText(DebBookDetailActivity.this, "Dữ liệu được bảo vệ...", Toast.LENGTH_SHORT).show();
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
                hideSoftKeyboard(DebBookDetailActivity.this);
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
        setContentView(R.layout.activity_deb_book_detail);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);


        addControls();
        addEvents();
        initData();
    }



    private void addControls() {
        tvId=findViewById(R.id.tv_DebtBookDetailId);
        tvRkey=findViewById(R.id.tv_DebtBookDetailRkey);
        tvServerKey=findViewById(R.id.tv_DebtBookDetailServerKey);
        tvRkeyThuyenVien=findViewById(R.id.tv_DebtBookDetailRkeyTV);
        tvRkeyTicket=findViewById(R.id.tv_DebtBookDetailRkeyTicket);
        edtTen=findViewById(R.id.edt_DebtBookDetailTen);
        edtNgayps=findViewById(R.id.edt_DebtBookDetailNgayPS);
        edtLydo=findViewById(R.id.edt_DebtBookDetailLydo);
        edtSotien=findViewById(R.id.edt_DebtBookDetailSoTien);
        edtUserName=findViewById(R.id.edt_DebtBookDetailUserName);
        lvDebtBook=findViewById(R.id.lv_DebtBookDetail);
    }

    private void addEvents() {

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

        edtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DebBookDetailActivity.this, DebtBookByUserActivity.class);
                intent.putExtra("tenChuyenBien",intentChuyenBien);
                intent.putExtra("userName",edtUserName.getText().toString());
                startActivity(intent);
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

        lvDebtBook.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mGestureDetector.onTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
            }
        });


    }

    private  void initData(){
        Intent intent=getIntent();
        intentRkeyThuyenVien = intent.getLongExtra("rkeyThuyenVien",0);
        intentChuyenBien = intent.getStringExtra("tenChuyenBien");
        intentRkeyChuyenBien=intent.getLongExtra("rkeyChuyenBien",0);
        intentRkeyTicket=intent.getLongExtra("rkeyTicket",0);
        intentUserName=intent.getStringExtra("userName");
        daChia=intent.getBooleanExtra("daChia",false);
        tenThuyenVien=crudLocaldb.DSTV_getTenThuyenVien(intentRkeyThuyenVien);
        
        arrDebtBook=crudLocaldb.DebtBook_getDebtBookByChuyenBienAndThuyenVien(intentChuyenBien,intentRkeyThuyenVien);
        setAdapter();
        if (arrDebtBook.size()>=1) { //phong truong hop null k co record nao
            DebtBook debtbook = arrDebtBook.get(arrDebtBook.size() - 1);
            lvDebtBook.setSelection(arrDebtBook.size()-1);
            tvId.setText(debtbook.getId()+"");
            tvRkey.setText(debtbook.getRkey()+"");
            tvServerKey.setText(debtbook.getServerkey()+"");
            tvRkeyThuyenVien.setText(debtbook.getRkeythuyenvien()+"");
            tvRkeyTicket.setText(debtbook.getRkeyticket()+"");
            edtSotien.setText(formatNumber(debtbook.getSotien()+""));
            edtNgayps.setText(debtbook.getNgayps()+"");
            edtTen.setText(debtbook.getTen());
            edtLydo.setText(debtbook.getLydo()+"");
            edtUserName.setText(debtbook.getUsername() + "");
        }
    }

    private void setAdapter() {
        customadapterData.addAll(arrDebtBook);
        // gan data source cho adapter
        customAdapter = new CustomAdapterDebtBookDetail(DebBookDetailActivity.this, R.layout.customlist_debtbook,customadapterData);
        //gan adapter cho spinner
        lvDebtBook.setAdapter(customAdapter);
    }

    private void CheckAddNew(){
        tvRkey.setText("");
        tvId.setText("");
        tvServerKey.setText("");
        tvRkeyThuyenVien.setText("");
        tvRkeyTicket.setText(intentRkeyTicket+"");
        edtSotien.setText("");
        edtNgayps.setText("");
        edtTen.setText(tenThuyenVien);
        edtLydo.setText("");
        edtUserName.setText(intentUserName);
        edit_mode="NEW";
        setEditMod(true);

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
        edtSotien.setText("");
        edtNgayps.setText("");
        edtTen.setText(tenThuyenVien);
        edtLydo.setText("");
        edtUserName.setText(intentUserName+"");
        // Refresh ListView.
        this.needRefresh=true;
        CapNhatHauCanh();
        updateListDebtBook();
    }

    private void SaveRecord(){
        DebtBook debtbook=new DebtBook();
        if(edtNgayps.getText()+""=="" || longGet(edtSotien.getText()+"")==0 ) {
            Toast.makeText(getApplicationContext(), "Cần nhập vào đủ thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }
        long sotien=longGet(DebBookDetailActivity.this,edtSotien);

        debtbook.setRkeythuyenvien(intentRkeyThuyenVien);
        debtbook.setTen(tenThuyenVien);
        debtbook.setChuyenbien(intentChuyenBien);
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
        //cap nhat Ticket
        //sum by ticket
        String chiByChi=crudLocaldb.Chi_SumDaChiTicket(intentRkeyTicket);
        String chiByDebt=crudLocaldb.DebtBook_SumDebtBookTicket(intentRkeyTicket);
        long tongchi=longGet(chiByChi)+longGet(chiByDebt);
        //Cap nhat Ticket
        crudLocaldb.Ticket_CapNhatChi(intentRkeyTicket, String.valueOf(tongchi), getCurrentTimeMiliS());
        //Cap nhat DSTV
        String tienmuon=crudLocaldb.DebtBook_SumForThuyenVien(intentRkeyThuyenVien, intentChuyenBien);
        crudLocaldb.DSTV_CapNhatTien(intentRkeyChuyenBien,tenThuyenVien,"0",tienmuon,"0",getCurrentTimeMiliS(), intentUserName);
    }

    public void updateListDebtBook(){
        customadapterData.clear();
        arrDebtBook=crudLocaldb.DebtBook_getDebtBookByChuyenBienAndThuyenVien(intentChuyenBien,intentRkeyThuyenVien);
        customadapterData.addAll(arrDebtBook);
        customAdapter.notifyDataSetChanged();
    }

    private  void setEditMod(boolean chohaykhong){
        if (chohaykhong==true){
           mShowMenuSave=1;
        }else {
            mShowMenuSave=0;
            edtNgayps.setFocusable(false);
            edit_mode="VIEW";
        }
        invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again
    }
    private String formatNumber(String str) {
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        long lv = utils.longGet(str);
        String get_value = formatter.format(lv);
        return get_value;
    }

    public void showDatePickerDialog(final EditText edtViewDate) {
        hideSoftKeyboard(DebBookDetailActivity.this);
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
