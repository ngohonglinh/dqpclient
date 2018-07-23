package com.dqpvn.dqpclient;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dqpvn.dqpclient.crudmanager.SyncCheck;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.restfullAPI;
import com.dqpvn.dqpclient.customadapters.CustomAdapterDSTV;
import com.dqpvn.dqpclient.customadapters.CustomAdapterDebtBookDetail;
import com.dqpvn.dqpclient.models.ChuyenBien;
import com.dqpvn.dqpclient.models.DSTV;
import com.dqpvn.dqpclient.models.DebtBook;
import com.dqpvn.dqpclient.models.ResponseFromServer;
import com.dqpvn.dqpclient.models.Ticket;
import com.dqpvn.dqpclient.models.WantDeleteFromServer;
import com.dqpvn.dqpclient.utils.MyContextWrapper;
import com.dqpvn.dqpclient.utils.utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import static com.dqpvn.dqpclient.crudmanager.SyncCheck.IS_ADMIN;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_NAME;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_OK;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.MY_SERVER;
import static com.dqpvn.dqpclient.utils.utils.comPare;
import static com.dqpvn.dqpclient.utils.utils.doubleGet;
import static com.dqpvn.dqpclient.utils.utils.getCurrentTimeMiliS;
import static com.dqpvn.dqpclient.utils.utils.hideSoftKeyboard;
import static com.dqpvn.dqpclient.utils.utils.intGet;
import static com.dqpvn.dqpclient.utils.utils.isBad;
import static com.dqpvn.dqpclient.utils.utils.longGet;
import static com.dqpvn.dqpclient.utils.utils.showSoftKeyboard;

public class DSTVActivity extends AppCompatActivity {

    final private String TAG = getClass().getSimpleName();
    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;
    private int lvPossition=-1;

    private boolean needRefresh;
    private int mShowMenuSave=0;
    private boolean mShowMenuLinhUngTien=true, daChia=false;

    private final int REQUEST_START_DEBT=124, REQUEST_TVCANCA=421;
    private TextView tvId,tvRkey, tvServerKey, tvRkeyChuyenBien, tvUsername, tvTienChia;
    private TextView tvTienMuon, tvTienCanCa, tvConLai, tvChuyenBien;
    private EditText edtTen, edtDiem, edtTongDiem;
    private AutoCompleteTextView edtNotes;
    private ListView lvTV;
    private ImageView img_Like;
    private TextView tv_like;
    //database
    private crudLocal crudLocaldb=crudLocal.getInstance(this);
    //Khai báo Datasource lưu trữ danh sách doi tac
    private ArrayList<DSTV> arrDSTV=new ArrayList<>();
    private ArrayList<DSTV>customadapterData=new ArrayList<>();
    //Khai báo ArrayAdapter cho ListView
    private CustomAdapterDSTV customAdapter;
    //Theo gioi cho phép Nhap lieu
    private String intentUserName, edit_mode, shipMasterName;
    private long intentIdChuyenBien;

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
            lvPossition=lvTV.pointToPosition( (int) x, (int) y );
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
            lvPossition=lvTV.pointToPosition( (int) e1.getX(), (int) e1.getY() );
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
        DSTV dstv = new DSTV();
        dstv=arrDSTV.get(position);
        tvId.setText(String.valueOf(dstv.getId()));
        tvRkey.setText(String.valueOf(dstv.getRkey()));
        tvServerKey.setText(String.valueOf(dstv.getServerkey()));
        tvRkeyChuyenBien.setText(dstv.getRkeychuyenbien()+"");
        tvUsername.setText(dstv.getUsername()+"");
        edtTen.setText(dstv.getTen());
        tvTienChia.setText(dstv.getTienchia());
        tvTienMuon.setText(dstv.getTienmuon());
        tvTienCanCa.setText(dstv.getTiencanca());
        tvConLai.setText(dstv.getConlai());
        edtNotes.setText(dstv.getNotes());
        edtDiem.setText(formatNumber(dstv.getDiem()));
        String TongDiem=String.valueOf(crudLocaldb.DSTV_SumTongDiem(intentIdChuyenBien));
        edtTongDiem.setText(formatNumber(TongDiem));
        setEditMod(false);
        tinhDiemDD();
    }

    private void gotoDebtBookDetail(int position){
        if (position<0){return;}
        lvPossition=position;
        DSTV dstv = new DSTV();
        dstv=arrDSTV.get(position);
        ArrayList<DebtBook>chiTietNoThuyenVien=new ArrayList<>();
        String TenChuyenBien=crudLocaldb.ChuyenBien_getTenChuyenBien(longGet(tvRkeyChuyenBien.getText()+""));
        chiTietNoThuyenVien=crudLocaldb.DebtBook_getDebtBookByChuyenBienAndThuyenVien(TenChuyenBien,dstv.getRkey());
        if (chiTietNoThuyenVien.size()>0) {
            Intent intent=new Intent(DSTVActivity.this, DebBookDetailActivity.class);
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
            intent.putExtra("rkeyThuyenVien", dstv.getRkey());
            intent.putExtra("tenChuyenBien", TenChuyenBien);
            intent.putExtra("rkeyChuyenBien", longGet(tvRkeyChuyenBien.getText()+""));
            intent.putExtra("rkeyTicket", rkeyTicket);
            intent.putExtra("daChia",daChia);
            intent.putExtra("userName", intentUserName);
            intent.putExtra("whoStart","thuyenTruong");
            startActivityForResult(intent, REQUEST_START_DEBT);
        }else {
            Toast.makeText(DSTVActivity.this, "Nhân lực này vẫn chưa ứng tiền", Toast.LENGTH_SHORT).show();
        }
    }

    //lam viec voi menu
    @Override
    public boolean onCreateOptionsMenu(Menu manu) {
        getMenuInflater().inflate(R.menu.dstv_activity_menu, manu);
        MenuItem mSave = manu.findItem(R.id.save_dstv);
        MenuItem mLinhUngTien=manu.findItem(R.id.id_dstv_ungtien);
        if (mShowMenuSave==1){
            mSave.setVisible(true);
        }else{
            mSave.setVisible(false);
        }
        if (mShowMenuLinhUngTien){
            mLinhUngTien.setVisible(true);
        }else{
            mLinhUngTien.setVisible(false);
        }
        // return true so that the menu pop up is opened
        return true;
    }


    // Method này sử lý sự kiện khi MenuItem được chọn.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        arrDSTV=  crudLocaldb.DSTV_getDSTVByChuyenBien(intentIdChuyenBien);
        switch(itemId)  {
            //ten cua cac id khi thiet ke cac resource menu
            case R.id.id_dstv_new :
                if (isOtherThuyenTruong() && !IS_ADMIN ){
                    Toast.makeText(DSTVActivity.this, "Dữ liệu được bảo vệ...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                //wrtite ơn logic
                CheckAddNew();
                return true;
            case R.id.id_dstv_edit :
                if (lvPossition<0){
                    Toast.makeText(this, "Chưa chọn đúng dữ liệu cần edit", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (!canWrite()){
                    Toast.makeText(DSTVActivity.this, "Dữ liệu được bảo vệ...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                CheckEdit();
                showSoftKeyboard(this);
                return true;
            case R.id.id_dstv_delete :
                //wrtite ơn logic
                if (arrDSTV.size()==0){
                    return true;
                }
                if (!canWrite()){
                    Toast.makeText(DSTVActivity.this, "Dữ liệu được bảo vệ...", Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (checkDebt()){
                    Toast.makeText(this, "Không thể xóa vì đang có chi tiết liên quan", Toast.LENGTH_SHORT).show();
                    return true;
                }
                //lay ra index hien tai cua listview
                if (lvPossition<0){
                    Toast.makeText(this, "Chưa chọn đúng dữ liệu cần xóa", Toast.LENGTH_SHORT).show();
                }else {
                    final DSTV dstv =arrDSTV.get(lvPossition);
                    // Hỏi trước khi xóa.
                    new AlertDialog.Builder(this)
                            .setTitle("DQP Client")
                            .setMessage(dstv.getTen()+ "\n\n" + "Có chắc xóa?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    CheckDelete(dstv);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                }
                return true;
            case R.id.id_dstv_ungtien :
                if (isOtherThuyenTruong()){
                    Toast.makeText(this, "Anh không thể cho thuyền viên tàu khác ứng tiền :)", Toast.LENGTH_SHORT).show();
                    return true;
                }
                gotoDebtBook();
                return true;
            case R.id.save_dstv:
                UpdateDSTV();
                hideSoftKeyboard(DSTVActivity.this);
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

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        getMenuInflater().inflate(R.menu.context_menu_dstv, menu);
//    }
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.id_DebtBook:
//                if (isOtherThuyenTruong()){
//                    Toast.makeText(this, "Anh không thể cho thuyền viên tàu khác ứng tiền :)", Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//                gotoDebtBook();
//                break;
//            case R.id.id_CanCa:
//                Intent intent=new Intent(DSTVActivity.this,TVCanCaActivity.class);
//                intent.putExtra("rkeyThuyenVien",longGet(tvRkey.getText()+""));
//                intent.putExtra("rkeyChuyenBien",longGet(tvRkeyChuyenBien.getText()+""));
//                startActivityForResult(intent,REQUEST_TVCANCA);
//                break;
//        }
//        return super.onContextItemSelected(item);
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dstv);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        DSTVActivity.CustomGestureDetector customGestureDetector = new DSTVActivity.CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);

        addControls();
        initialization();
        addEvents();
        setAdapter();
        initData();

        //Đăng ký ContextMenu cho lvTV
//        registerForContextMenu(lvTV);

    }

    private void initialization() {
        Intent intent=getIntent();
        img_Like.setVisibility(View.INVISIBLE);
        tv_like.setVisibility(View.INVISIBLE);

        if (intent.hasExtra("rkeyChuyenBien")) {
            intentIdChuyenBien = intent.getLongExtra("rkeyChuyenBien", 0);
            intentUserName=intent.getStringExtra("userName");
            daChia=intent.getBooleanExtra("daChia",false);
            if (intent.getBooleanExtra("makeNew",false)){
                //re quest make new record
                CheckAddNew();
                return;
            }
            arrDSTV=  crudLocaldb.DSTV_getDSTVByChuyenBien(intentIdChuyenBien);
            customadapterData.addAll(arrDSTV);
            tvChuyenBien.setText(crudLocaldb.ChuyenBien_getTenChuyenBien(intentIdChuyenBien));
            tvRkeyChuyenBien.setText(intentIdChuyenBien+"");
            shipMasterName=crudLocaldb.ChuyenBien_getShipMater(intentIdChuyenBien);
        }
    }

    private void addControls() {
        tvId=findViewById(R.id.tv_DSTVId);
        tvRkey=findViewById(R.id.tv_DSTVRkey);
        tvServerKey=findViewById(R.id.tv_DSTVServerkey);
        tvTienChia=findViewById(R.id.tv_DSTVTienChia);
        tvTienMuon=findViewById(R.id.tv_DSTVTienMuon);
        tvTienCanCa=findViewById(R.id.tv_DSTVTienCanCa);
        tvConLai=findViewById(R.id.tv_DSTVConLai);
        tvRkeyChuyenBien=(TextView)findViewById(R.id.tv_DSTVRkeyChuyenBien);
        tvUsername=findViewById(R.id.tv_DSTVUserName);
        edtTen=findViewById(R.id.edt_DSTVTen);
        edtNotes=findViewById(R.id.aedt_DSTVNotes);
        lvTV=findViewById(R.id.lv_DSTV);
        tvChuyenBien=findViewById(R.id.tv_DSTVChuyenBien);
        edtDiem=findViewById(R.id.edt_DSTVDiem);
        edtTongDiem=findViewById(R.id.edt_DSTVTongDiem);
        img_Like=findViewById(R.id.btn_DSTVLike);
        tv_like=findViewById(R.id.tv_DSTVLike);


    }

    private void addEvents() {
        edtNotes.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (edit_mode!="VIEW"){
                        UpdateDSTV();
                        CheckAddNew();
                    }
                }
                return false;
            }
        });

        edtDiem.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (edit_mode!="VIEW"){
                        UpdateDSTV();
                    }
                }
                return false;
            }
        });


        lvTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mGestureDetector.onTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
            }
        });

    }
    private  void initData(){
        if (arrDSTV.size()>=1) { //phong truong hop null k co record nao
            DSTV dstv =arrDSTV.get(arrDSTV.size()-1);
            lvTV.setSelection(arrDSTV.size()-1);
            tvId.setText(String.valueOf(dstv.getId()));
            tvRkey.setText(dstv.getRkey()+"");
            tvServerKey.setText(dstv.getServerkey()+"");
            tvUsername.setText(dstv.getUsername()+"");
            tvRkeyChuyenBien.setText(dstv.getRkeychuyenbien()+"");
            edtTen.setText(dstv.getTen());
            tvTienChia.setText(dstv.getTienchia());
            tvTienMuon.setText(dstv.getTienmuon());
            tvTienCanCa.setText(dstv.getTiencanca());
            tvConLai.setText(dstv.getConlai());
            edtNotes.setText(dstv.getNotes());
            edtDiem.setText(formatNumber(dstv.getDiem()));
            String TongDiem=String.valueOf(crudLocaldb.DSTV_SumTongDiem(intentIdChuyenBien));
            edtTongDiem.setText(formatNumber(TongDiem));
            setEditMod(false);

            tinhDiemDD();
        }

    }

    private boolean checkDebt(){
        ArrayList<DebtBook>arrDebtBook=new ArrayList<>();
        long idTv=longGet(tvRkey.getText()+"");
        arrDebtBook=crudLocaldb.DebtBook_getDebtBookByThuyenVien(idTv);
        if (arrDebtBook.size()>0) {
            return true;

        }else{
            return false;

        }
    }
    private Boolean canWrite(){
        if (comPare(shipMasterName,intentUserName) || intentUserName.substring(0,5).equals("admin")){
            return true;
        }else{
            return false;
        }
    }
    private Boolean isOtherThuyenTruong(){
        Boolean is =false;
        ArrayList<ChuyenBien>arrChuyenBien=new ArrayList<>();
        arrChuyenBien=crudLocaldb.ChuyenBien_getOnlyShowChuyenBien();
        for(int i=0;i<arrChuyenBien.size();i++){
            if (arrChuyenBien.get(i).getUsername().equals(intentUserName) &&
                    arrChuyenBien.get(i).getRkey()!=intentIdChuyenBien &&
                    !arrChuyenBien.get(i).getUsername().substring(0,5).equals("admin")){
                is=true;
                break;
            }
        }
        return is;
    }

    private Boolean isThuyenTruong(){
        Boolean is =false;
        ArrayList<ChuyenBien>arrChuyenBien=new ArrayList<>();
        arrChuyenBien=crudLocaldb.ChuyenBien_getOnlyShowChuyenBien();
        for(int i=0;i<arrChuyenBien.size();i++){
            if (arrChuyenBien.get(i).getUsername().equals(intentUserName) &&
                    !arrChuyenBien.get(i).getUsername().substring(0,5).equals("admin")){
                is=true;
                break;
            }
        }
        return is;
    }

    private void gotoDebtBook() {
        String chuyenbien=crudLocaldb.ChuyenBien_getTenChuyenBien(longGet(tvRkeyChuyenBien.getText()+""));

        //find right Ticket....
        ArrayList<Ticket>arrTicket=new ArrayList<>();
        arrTicket=crudLocaldb.Ticket_getOpenTicketByUser(intentUserName);
        long rkeyTicket=0;
        if (arrTicket.size()>0){
            // get last open ticket for user
            rkeyTicket=arrTicket.get(arrTicket.size()-1).getRkey();
        }
        Intent intent=new Intent(DSTVActivity.this, DebtBookActivity.class);
        intent.putExtra("rkeyChuyenBien", longGet(tvRkeyChuyenBien.getText()+""));
        intent.putExtra("rkeyTicket", rkeyTicket);
        intent.putExtra("userName", intentUserName);
        if (LOGIN_NAME.substring(0,5).equals("admin")){
            intent.putExtra("whoStart","admin");
        }else if(isThuyenTruong()){
            intent.putExtra("whoStart","thuyenTruong");
        }else{
            intent.putExtra("whoStart","linhNha");
        }
        intent.putExtra("makeNew", true);
        startActivityForResult(intent, REQUEST_START_DEBT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && (requestCode == REQUEST_START_DEBT || requestCode==REQUEST_TVCANCA) ) {
            needRefresh = data.getBooleanExtra("needRefresh",false);
            // Refresh ListView
            if(needRefresh) {
                int position = -1;
                arrDSTV=  crudLocaldb.DSTV_getDSTVByChuyenBien(intentIdChuyenBien);
                for (int j = 0; j < arrDSTV.size(); j++) {
                    if (arrDSTV.get(j).getRkey() ==longGet(tvRkey.getText()+"")) {
                        position = j;
                        gotoRec(position);
                        break;  // uncomment to get the first instance
                    }
                }
                updateListDSTV();
                this.needRefresh=true;
            }else{
                this.needRefresh=false;
            }
        }
    }

    private void CheckAddNew(){
        tvId.setText("");
        tvRkey.setText("");
        tvServerKey.setText("");
        edtTen.setText("");
        tvTienChia.setText("0");
        tvTienMuon.setText("0");
        tvTienCanCa.setText("0");
        tvConLai.setText("0");
        tvUsername.setText(intentUserName);
        crudLocaldb.ChuyenBien_getTenChuyenBien(intentIdChuyenBien);
        edtNotes.setText("");
        edtDiem.setText("");
        edtTen.requestFocus();
        edit_mode="NEW";
        setEditMod(true);

    }

    private void CheckEdit(){
        edit_mode="EDIT";
        if (!comPare(edtDiem.getText()+"","")){
            edtDiem.setSelectAllOnFocus(true);
        }
        setEditMod(true);
    }

    private void CheckDelete(DSTV dstv) {
        if (dstv.getServerkey()!=0){
            WantDeleteFromServer wdfs=new WantDeleteFromServer();
            wdfs.setmServerkey(dstv.getServerkey());
            wdfs.setmTablename("dstv");
            crudLocaldb.WDFS_addWDFS(wdfs);
        }
        crudLocaldb.DSTV_deleteDSTV(dstv.getRkey());
        //remove from array list
        Predicate<DSTV> personPredicate = p-> p.getId() == dstv.getId();
        arrDSTV.removeIf(personPredicate);
        //refesh screen
        tvId.setText("");
        tvRkey.setText("");
        tvServerKey.setText("");
        tvRkeyChuyenBien.setText(intentIdChuyenBien+"");
        tvUsername.setText(intentUserName);
        edtTen.setText("");
        tvTienChia.setText("0");
        tvTienMuon.setText("0");
        tvTienCanCa.setText("0");
        tvConLai.setText("0");
        edtNotes.setText("");
        edtDiem.setText("");
        String TongDiem=String.valueOf(crudLocaldb.DSTV_SumTongDiem(intentIdChuyenBien));
        edtTongDiem.setText(formatNumber(TongDiem));
        edtTen.requestFocus();
        // Refresh ListView.
        this.needRefresh=true;
        updateListDSTV();
    }

    private void gotoRec(int position){
        arrDSTV=  crudLocaldb.DSTV_getDSTVByChuyenBien(intentIdChuyenBien);
        if (arrDSTV.size() >= 1) { //phong truong hop null k co record nao
            DSTV dstv;
            dstv = arrDSTV.get(position);
            tvId.setText(String.valueOf(dstv.getId()));
            tvRkey.setText(dstv.getRkey()+"");
            tvServerKey.setText(dstv.getServerkey()+"");
            tvRkeyChuyenBien.setText(dstv.getRkeychuyenbien()+"");
            tvUsername.setText(dstv.getUsername()+"");
            edtTen.setText(dstv.getTen());
            tvTienChia.setText(dstv.getTienchia());
            tvTienMuon.setText(dstv.getTienmuon());
            tvTienCanCa.setText(dstv.getTiencanca());
            tvConLai.setText(dstv.getConlai());
            edtNotes.setText(dstv.getNotes());
            edtDiem.setText(formatNumber(dstv.getDiem()));
            String TongDiem=String.valueOf(crudLocaldb.DSTV_SumTongDiem(intentIdChuyenBien));
            edtTongDiem.setText(formatNumber(TongDiem));
            setEditMod(false);
        }
    }

    private void UpdateDSTV(){
        if (daChia){
            Toast.makeText(this, "Dự án củ, dữ liệu chỉ được view", Toast.LENGTH_SHORT).show();
            return;
        }
        DSTV dstv=new DSTV();
        if(edtTen.getText()+""=="" ) {
            Toast.makeText(getApplicationContext(), "Cần nhập vào Tên thuyền viên", Toast.LENGTH_SHORT).show();
            return;
        }
        String tenTv=edtTen.getText()+"".trim();
        String [] listTen=crudLocaldb.DSTV_listThuyenVien(intentIdChuyenBien);

        dstv.setRkeychuyenbien(intentIdChuyenBien);
        dstv.setNotes(edtNotes.getText()+"");
        double strdiem=doubleGet(edtDiem.getText()+"");
        if (strdiem==0){
            dstv.setDiem("");
        }else{
            dstv.setDiem(String.valueOf(doubleGet(edtDiem.getText()+"")));
        }
        dstv.setTen(tenTv);
        dstv.setTienchia(tvTienChia.getText()+"");
        dstv.setTienmuon(tvTienMuon.getText()+"");
        dstv.setTiencanca(tvTienCanCa.getText()+"");
        dstv.setConlai(tvConLai.getText()+"");
        dstv.setUpdatetime(getCurrentTimeMiliS());
        dstv.setUsername(intentUserName);
        if (edit_mode=="NEW" && longGet(tvRkey.getText()+"")==0) {

            if (Arrays.asList(listTen).contains(tenTv)){
                Toast.makeText(getApplicationContext(), "Tên này đã có trong danh sách", Toast.LENGTH_SHORT).show();
                return;
            }

            //chuyenbien.set;);= new ChuyenBien(tenchuyenbien,tentau,ngaykhoihanh);
            dstv.setServerkey(0);
            dstv.setRkey(longGet(getCurrentTimeMiliS()));
            if (dstv != null) {
                long i = crudLocaldb.DSTV_addDSTV(dstv);
                if (i !=-1) {
                    setEditMod(false);
                    this.needRefresh=true;
                    updateListDSTV();
                }
            }

        }else{
            if (edit_mode=="EDIT") {
                dstv.setRkey(longGet(tvRkey.getText()+""));
                dstv.setId(intGet(tvId.getText()+""));
                dstv.setServerkey(intGet(tvServerKey.getText()+""));
                int result = crudLocaldb.DSTV_updateDSTV(dstv);
                if (result > 0) {
                    ArrayList<DebtBook> arrDebtBook=new ArrayList<>();
                    arrDebtBook=crudLocaldb.DebtBook_getDebtBookByThuyenVien(longGet(tvRkey.getText()+""));
                    for (int i=0;i<arrDebtBook.size();i++){
                        DebtBook debtBook=new DebtBook();
                        debtBook=arrDebtBook.get(i);
                        if (!comPare(debtBook.getTen(),tenTv)){
                            debtBook.setTen(tenTv);
                            debtBook.setUpdatetime(getCurrentTimeMiliS());
                            int res = crudLocaldb.DebtBook_updateDebtBook(debtBook);
                        }
                    }
                    updateListDSTV();
                    setEditMod(false);
                    this.needRefresh=true;
                }
            }
        }
        String TongDiem=String.valueOf(crudLocaldb.DSTV_SumTongDiem(intentIdChuyenBien));
        edtTongDiem.setText(formatNumber(TongDiem));
    }

    private void setAdapter() {
        String[] listChucVu = getResources().getStringArray(R.array.chucvu_array);
        ArrayAdapter<String> adapterCV = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listChucVu);
        edtNotes.setAdapter(adapterCV);

        if (customAdapter == null) {
            // gan data source cho adapter
            customAdapter = new CustomAdapterDSTV(DSTVActivity.this, R.layout.customlist_dstv,customadapterData);
            //gan adapter cho spinner
            lvTV.setAdapter(customAdapter);
        }else{
            updateListDSTV();
            lvTV.setSelection(customAdapter.getCount()-1);
        }
    }
    //gett all to list
    public void updateListDSTV(){
        customadapterData.clear();
        arrDSTV=  crudLocaldb.DSTV_getDSTVByChuyenBien(intentIdChuyenBien);
        customadapterData.addAll(arrDSTV);
        customAdapter.notifyDataSetChanged();
    }

    private  void setEditMod(boolean chohaykhong){
        if (chohaykhong==true){
            mShowMenuSave=1;
            mShowMenuLinhUngTien=false;
            edtNotes.setThreshold(1);
        }else {
            edtNotes.setThreshold(1000);
            edtDiem.setSelectAllOnFocus(false);
            mShowMenuLinhUngTien=true;
            mShowMenuSave=0;
            edit_mode="VIEW";
        }
        invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again
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

    private void tinhDiemDD() {
        long thisEater=crudLocaldb.DSTV_getRkeyThuyenVien(edtTen.getText()+"",intentIdChuyenBien);
        int diemEater=crudLocaldb.DiemDD_SumDiemEaterTV(thisEater);
        tv_like.setText(intGet(diemEater+"")+"");
        if (diemEater==0){
            img_Like.setVisibility(View.INVISIBLE);
            tv_like.setVisibility(View.INVISIBLE);
        }else{
            if (diemEater>0){
                img_Like.setImageResource(R.drawable.like);
                tv_like.setTextColor(Color.parseColor("#3F51B5"));
            }else{
                img_Like.setImageResource(R.drawable.dislike);
                tv_like.setTextColor(Color.parseColor("#F44336"));
            }
            img_Like.setVisibility(View.VISIBLE);
            tv_like.setVisibility(View.VISIBLE);
        }
    }

    private String formatNumber(String tv) {
        if (!isBad(tv)){
            DecimalFormat formatter = new DecimalFormat("#,###.##");
            double lv = utils.doubleGet(tv);
            String get_value = formatter.format(lv);
            return get_value;
        }else{
            return "";
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
