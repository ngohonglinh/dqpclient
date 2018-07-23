package com.dqpvn.dqpclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.customadapters.CustomAdapterThuDetail;
import com.dqpvn.dqpclient.models.BanHSDetail;
import com.dqpvn.dqpclient.models.DMHaiSan;
import com.dqpvn.dqpclient.models.Thu;
import com.dqpvn.dqpclient.models.ThuDetail;
import com.dqpvn.dqpclient.models.WantDeleteFromServer;
import com.dqpvn.dqpclient.utils.MyContextWrapper;
import com.dqpvn.dqpclient.utils.NumberTextWatcher;
import com.dqpvn.dqpclient.utils.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.IS_ADMIN;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_NAME;
import static com.dqpvn.dqpclient.utils.utils.comPare;
import static com.dqpvn.dqpclient.utils.utils.doubleGet;
import static com.dqpvn.dqpclient.utils.utils.getCurrentDate;
import static com.dqpvn.dqpclient.utils.utils.getCurrentTimeMiliS;
import static com.dqpvn.dqpclient.utils.utils.hideSoftKeyboard;
import static com.dqpvn.dqpclient.utils.utils.intGet;
import static com.dqpvn.dqpclient.utils.utils.isBad;
import static com.dqpvn.dqpclient.utils.utils.longGet;

public class ThuDetailActivity extends AppCompatActivity {

    final private String TAG = getClass().getSimpleName();
    private GestureDetector mGestureDetector;

    private static final int SWIPE_MIN_DISTANCE = 300;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;
    private int lvPossition=-1;



    private boolean needRefresh, mShowMenuSave=false, editDGAtMaster=false, mHideAll=false, daChia=false;
    private int REQUEST_START_BANHS_DETAIL=500;
    private AutoCompleteTextView aedtTenHS;
    private EditText edtSoLuong,edtSoLuong2, edtDonGia, edtThanhTien, edtSoLuongSum;
    private TextView tvId,tvRkey, tvServerKey;
    private ListView lvThuDetail;
    private String soluongTong="", auToTenhs="";
    private LinearLayout lyDGTT1, lyDGTT2;
    private CheckBox chkAuto;

    //database
    private crudLocal crudLocaldb=crudLocal.getInstance(this);
    private ArrayList<String> DMHaiSan_listHaiSan=new ArrayList<>();
    //Khai báo ArrayAdapter cho Autocomplete
    private ArrayList<ThuDetail> arrThuDetail = new ArrayList<>();
    private CustomAdapterThuDetail customAdapter;
    private ArrayAdapter<String> adapterTenHS;
    private ArrayList<String> listDaCoHaiSanNay=new ArrayList<>();
    //Theo gioi cho phép Nhap lieu
    private String edit_mode="VIEW";
    private ArrayList<ThuDetail> customadapterData=new ArrayList<>();
    private long intentRkeyThuTong;
    private String intentPhanLoai;


    //lam viec voi menu
    @Override
    public boolean onCreateOptionsMenu(Menu manu) {
        getMenuInflater().inflate(R.menu.thudetail_menu, manu);
        MenuItem mSave = manu.findItem(R.id.save);
        MenuItem mEdit = manu.findItem(R.id.id_edit);
        MenuItem mPrice = manu.findItem(R.id.newprice);
        if (mHideAll || daChia){
            for (int i = 0; i < manu.size(); i++){
                manu.getItem(i).setVisible(false);
            }
            return true;
        }
        if (!IS_ADMIN){
            mSave.setVisible(false);
            mEdit.setVisible(false);
        }else {
            mEdit.setVisible(true);
        }
        if(!LOGIN_NAME.substring(0,5).equals("admin")){
            mPrice.setVisible(false);
            return true;
        }else {
            mPrice.setVisible(true);
        }
        if (mShowMenuSave){
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
            case R.id.id_new:
                CheckAddNew();
                return true;
            //ten cua cac id khi thiet ke cac resource menu
            case R.id.id_delete :
                //wrtite ơn logic
                if (arrThuDetail.size()==0){
                    return true;
                }
                //lay ra index hien tai cua listview
                if (lvPossition==-1){
                    Toast.makeText(this, "Chưa chọn đúng dữ liệu cần xóa", Toast.LENGTH_SHORT).show();
                }else {
                    if (exitsBanHS()){
                        Toast.makeText(this, "Có chi tiết liên quan", Toast.LENGTH_SHORT).show();
                    }else{
                        final ThuDetail thuDetail =arrThuDetail.get(lvPossition);
                        // Hỏi trước khi xóa.
                        new AlertDialog.Builder(this)
                                .setTitle("DQP Client")
                                .setMessage(thuDetail.getTenhs()+ "\n\n"+"Có chắc xóa?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        CheckDelete();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();


                    }
                }
                return true;
            case R.id.id_edit :
                //wrtite ơn logic
                if (arrThuDetail.size()==0){
                    return true;
                }
                lyDGTT1.setVisibility(View.VISIBLE);
                lyDGTT2.setVisibility(View.VISIBLE);
                editDGAtMaster=true;
                edit_mode="EDIT";
                setEditMod(true);
                aedtTenHS.setFocusable(false);
                aedtTenHS.setFocusableInTouchMode(false);
                edtSoLuong.setFocusable(false);
                edtSoLuong.setFocusableInTouchMode(false);
                if (!comPare(edtDonGia.getText()+"","")){
                    edtDonGia.setSelectAllOnFocus(true);
                }
                edtDonGia.requestFocus();
                return true;
            case R.id.save :
                //wrtite ơn logic
                UpdateSave();
                if (!IS_ADMIN){
                    setEditMod(false);
                }
                edtDonGia.requestFocus();
                return true;
            case R.id.newprice:
                new AlertDialog.Builder(this)
                        .setTitle("DQP Client")
                        .setMessage("This function will re-apply new price from dmhaisan" + "\n\n"+ "Are u sure?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ApllyNewPrice();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

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
    private void ApllyNewPrice(){
        ArrayList<DMHaiSan>arrDMHSan=crudLocaldb.DMHaiSan_getAllDMHaiSan();
        arrThuDetail=crudLocaldb.ThuDetail_getAllThuDetailofRkeyThuTong(intentRkeyThuTong);
        ArrayList<ThuDetail>arrTemp=new ArrayList<>();
        if (arrThuDetail.size()>0 && arrDMHSan.size()>0){
            ThuDetail thudetail =new ThuDetail();
            DMHaiSan dmhs =new DMHaiSan();
            for (int i=0;i<arrThuDetail.size();i++){
                thudetail=arrThuDetail.get(i);
                for (int m=0;m<arrDMHSan.size();m++){
                    dmhs=arrDMHSan.get(m);
                    if (dmhs.getTenhs().compareToIgnoreCase(thudetail.getTenhs())==0){
                        String tt=String.valueOf(doubleGet(thudetail.getSoluong())*longGet(dmhs.getDongia()));
                        //vi tt se cho ra so khoa hoc dang 9.18E+09 nen can chuyen doi
                        long thanhtien=Double.valueOf(tt).longValue();
                        thudetail.setDongia(dmhs.getDongia());
                        thudetail.setThanhtien(String.valueOf(thanhtien));
                        thudetail.setUpdatetime(getCurrentTimeMiliS());
                        thudetail.setUsername(LOGIN_NAME);
                        arrTemp.add(thudetail);
                        break;
                    }
                }
            }
            if(arrTemp.size()>0){
                long xyz=0;
                for(int i=0;i<arrTemp.size();i++){
                    xyz+=longGet(arrTemp.get(i).getThanhtien());
                }
                new AlertDialog.Builder(this)
                        .setTitle("DQP Client")
                        .setMessage("Total amount after apply new price: " + formatNumber(xyz+"") + " | " +
                                "Want save result to database and sync? This important effect and not recommended.")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                boolean coUpdate=false;
                                for (int i=0;i<arrThuDetail.size();i++){
                                    for (int v=0;v<arrTemp.size();v++){
                                        if (arrTemp.get(v).getId()==arrThuDetail.get(i).getId()){
                                            int c=crudLocaldb.ThuDetail_updateThuDetail(arrTemp.get(v));
                                            if (c>0){
                                                coUpdate =true;
                                            }
                                            break;
                                        }
                                    }
                                }
                                if (coUpdate){
                                    capNhatThuTong();
                                    needRefresh=true;
                                    setEditMod(false);
                                    updateListThuDetail();
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }else{
                Toast.makeText(this, "Nothing happen :)", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Require data is null, cant fetch.", Toast.LENGTH_SHORT).show();
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
            lvPossition=lvThuDetail.pointToPosition( (int) x, (int) y );
            if (lvPossition<0){
                return true;
            }
            if (!IS_ADMIN ){
                setEditMod(false);
            }
            gotoRec(lvPossition);
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
            lvPossition=lvThuDetail.pointToPosition( (int) e1.getX(), (int) e1.getY() );
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (lvPossition<0){
                    return false;
                }
                gotoRec(lvPossition);
                if (checkBanHSDetail()){
                    ThuDetail thudetail=new ThuDetail();
                    thudetail=arrThuDetail.get(lvPossition);
                    Intent intent=new Intent(ThuDetailActivity.this, BanHSDetailActivity.class);
                    intent.putExtra("rkeyThu",thudetail.getRkeythu());
                    intent.putExtra("rkeyThuDetail",thudetail.getRkey());
                    intent.putExtra("tenHaiSan",thudetail.getTenhs());
                    startActivityForResult(intent,REQUEST_START_BANHS_DETAIL);
                }else{
                    ArrayList<Thu>arrThuTong=new ArrayList<>();
                    arrThuTong=crudLocaldb.Thu_getThuByRkey(intentRkeyThuTong);
                    if (longGet(arrThuTong.get(0).getDatra())==0){
                        Toast.makeText(ThuDetailActivity.this, "Chưa có chi tiết liên quan...  ", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ThuDetailActivity.this, "Chi tiết đã bị xóa trong quá trình tối ưu database...  ", Toast.LENGTH_SHORT).show();
                    }

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
            Log.d(TAG, "DISTANCE X: " + String.valueOf(e1.getX() - e2.getX()) + " ----- SWIPE_THRESHOLD_VELOCIT: "+String.valueOf(Math.abs(velocityX))   );
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
        setContentView(R.layout.activity_thu_detail);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);

        addControls();
        initialization();
        addEvents();
    }
    private void initialization(){

        Intent intent=getIntent();
        intentRkeyThuTong=intent.getLongExtra("rkeyThu",0);
        mHideAll=intent.getBooleanExtra("hideAllMenu",false);
        intentPhanLoai=intent.getStringExtra("phanLoai");
        daChia=intent.getBooleanExtra("daChia",false);
        arrThuDetail=crudLocaldb.ThuDetail_getAllThuDetailofRkeyThuTong(intentRkeyThuTong);
        listDaCoHaiSanNay=crudLocaldb.ThuDetail_getDSHaiSanbyRkeyTong(intentRkeyThuTong);

        //show tenchuyenbien on title
        long rKeyChuyenBien=crudLocaldb.Thu_getThuByRkey(intentRkeyThuTong).get(0).getRkeychuyenbien();
        setTitle(crudLocaldb.ChuyenBien_getTenChuyenBien(rKeyChuyenBien));

        //get current so luong tong
        soluongTong=crudLocaldb.ThuDetail_SumSLHaiSanByRkeyThuTong(intentRkeyThuTong);

        setAdapter();
        if (arrThuDetail.size()>0){
            gotoRec(arrThuDetail.size()-1);
            setEditMod(false);
        }else{
            CheckAddNew();
        }

//        if (StringUtils.containsIgnoreCase(intentPhanLoai,"Cá phân")){
//            chkAuto.setVisibility(View.VISIBLE);
//        }else{
//            chkAuto.setVisibility(View.INVISIBLE);
//        }
    }
    private boolean exitsBanHS(){
        boolean s=false;
        ArrayList<BanHSDetail>arrbanhs=new ArrayList<>();
        arrbanhs=crudLocaldb.BanHSDetail_getBanHSDetailByRkeyThuDetail(longGet(tvRkey.getText()+""));
        if (arrbanhs.size()>0){
            s=true;
        }
        return s;
    }

    private void setAdapter() {
        if (customAdapter == null) {
            customadapterData.addAll(arrThuDetail);
            // gan data source cho adapter
            customAdapter = new CustomAdapterThuDetail(this, R.layout.customlist_thudetail,customadapterData);
            //gan adapter cho spinner
            lvThuDetail.setAdapter(customAdapter);
            //dmhaisan
            if (intentPhanLoai.equals("All")){
                DMHaiSan_listHaiSan=crudLocaldb.DMHaiSan_listHaiSan();
            }else{
                DMHaiSan_listHaiSan=crudLocaldb.DMHaiSan_listHaiSanByPhanLoai(intentPhanLoai);
            }
            adapterTenHS = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, DMHaiSan_listHaiSan);
            aedtTenHS.setAdapter(adapterTenHS);

        }else{
            updateListThuDetail();
        }
        lvThuDetail.setSelection(customAdapter.getCount()-1);
    }
    //gett all to list
    public void updateListThuDetail(){
        customadapterData.clear();
        arrThuDetail=crudLocaldb.ThuDetail_getAllThuDetailofRkeyThuTong(intentRkeyThuTong);
        customadapterData.addAll(arrThuDetail);
        customAdapter.notifyDataSetChanged();
        lvThuDetail.setSelection(customAdapter.getCount()-1);
    }



    private void addControls() {
        tvId=findViewById(R.id.tv_ThuDetailId);
        tvRkey = findViewById(R.id.tv_ThuDetailRkey);
        tvServerKey=findViewById(R.id.tv_ThuDetailServerKey);
        aedtTenHS = (AutoCompleteTextView) findViewById(R.id.aedt_ThuDetailtenhs);
        edtSoLuong=findViewById(R.id.edt_ThuDetailSoluong);
        edtSoLuong2=findViewById(R.id.edt_ThuDetailSoluong2);
        edtDonGia = findViewById(R.id.edt_ThuDetailDonGia);
        edtThanhTien = findViewById(R.id.edt_THuDetailThanhTien);
        lvThuDetail = findViewById(R.id.lv_ThuDetail);
        edtSoLuongSum=findViewById(R.id.tv_ThuDetailSoluongTongCong);
        lyDGTT1=findViewById(R.id.ly_ThuDetailDGTT1);
        lyDGTT2=findViewById(R.id.ly_ThuDetailDGTT2);
        lyDGTT1.setVisibility(View.GONE);
        lyDGTT2.setVisibility(View.GONE);
        chkAuto=findViewById(R.id.chk_ThuDetailAuto);
    }

    private boolean checkBanHSDetail(){
        ArrayList<BanHSDetail>arrBanHsDetail=new ArrayList<>();
        long rkeyThuDetail=longGet(tvRkey.getText()+"");
        arrBanHsDetail= crudLocaldb.BanHSDetail_getBanHSDetailByRkeyThuDetail(rkeyThuDetail);
        if (arrBanHsDetail.size()>0) {
            return true;

        }else{
            return false;

        }
    }

    private void gotoRec(int posittion){
        if (posittion<0){
            return;
        }
        ThuDetail thuDetail=new ThuDetail();
        thuDetail=arrThuDetail.get(posittion);
        tvId.setText(thuDetail.getId()+"");
        tvRkey.setText(thuDetail.getRkey()+"");
        tvServerKey.setText(thuDetail.getServerkey()+"");
        aedtTenHS.setText(thuDetail.getTenhs());
        edtSoLuong2.setText(formatNumber(thuDetail.getSoluong()));
        String soluongTong=crudLocaldb.ThuDetail_SumSLHaiSanByRkeyThuTong(intentRkeyThuTong);
        edtSoLuongSum.setText(formatNumber(soluongTong));
        edtDonGia.setText(formatNumber(thuDetail.getDongia()+""));
        edtThanhTien.setText(thuDetail.getThanhtien());
    }

    private void addEvents(){
        aedtTenHS.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    // on focus off
                    if (isBad(aedtTenHS.getText()+"")){
                        return;
                    }
                    final String str = aedtTenHS.getText().toString();
                    ListAdapter listAdapter = aedtTenHS.getAdapter();
                    for(int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if(str.compareToIgnoreCase(temp) == 0) {
                            aedtTenHS.setText(temp);
                            for (int j=0;j<listDaCoHaiSanNay.size();j++){
                                if (temp.compareToIgnoreCase(listDaCoHaiSanNay.get(j).toString())==0){
                                    ThuDetail thudetail= new ThuDetail();
                                    thudetail=crudLocaldb.ThuDetail_getThuDetailByTenhsAndRkeyThuTong(temp,intentRkeyThuTong);
                                    tvId.setText(thudetail.getId()+"");
                                    tvRkey.setText(thudetail.getRkey()+"");
                                    tvServerKey.setText(thudetail.getServerkey()+"");
                                    edtSoLuong2.setText(formatNumber(crudLocaldb.ThuDetail_getSoluongByRkey(longGet(tvRkey.getText()+""))));
                                    edtSoLuongSum.setText(formatNumber(soluongTong));
                                    return;
                                }
                            }
                            listDaCoHaiSanNay.add(temp);
                            return;
                        }
                    }
                    //aedtTenHS.setText("");
                    long rkeyHS=crudLocaldb.DMHaiSan_getRkeyByTenhs(str);
                    if (rkeyHS==0){
                        new AlertDialog.Builder(ThuDetailActivity.this)
                                .setMessage(str + " Chưa có trong Danh Mục Sản Phẩm | Có thêm vào?")
                                .setCancelable(false)
                                .setPositiveButton("Thêm vào", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        long newDmhsRkey=longGet(getCurrentTimeMiliS());
                                        DMHaiSan dmhs =new DMHaiSan();
                                        dmhs.setServerkey(0);
                                        dmhs.setRkey(newDmhsRkey);
                                        dmhs.setTenhs(str);
                                        if (intentPhanLoai.equals("All")){
                                            dmhs.setPhanloai("Unknown");
                                        }else{
                                            dmhs.setPhanloai(intentPhanLoai);
                                        }
                                        dmhs.setPhanloai(intentPhanLoai);
                                        dmhs.setNgayps(getCurrentDate());
                                        dmhs.setUpdatetime(getCurrentTimeMiliS());
                                        dmhs.setUsername(LOGIN_NAME);
                                        long i=crudLocaldb.DMHaiSan_addDMHaiSan(dmhs);
                                        if (i!=-1){
                                            if (intentPhanLoai.equals("All")){
                                                DMHaiSan_listHaiSan=crudLocaldb.DMHaiSan_listHaiSan();
                                            }else{
                                                DMHaiSan_listHaiSan=crudLocaldb.DMHaiSan_listHaiSanByPhanLoai(intentPhanLoai);
                                            }
                                            adapterTenHS = new ArrayAdapter<String>(ThuDetailActivity.this, android.R.layout.simple_list_item_1, DMHaiSan_listHaiSan);
                                            aedtTenHS.setAdapter(adapterTenHS);
                                            listDaCoHaiSanNay.add(str);
                                        }

                                    }
                                })
                                .setNegativeButton("Không thêm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        aedtTenHS.setText("");
                                        aedtTenHS.requestFocus();
                                    }
                                }).show();
                    }
                }

            }
        });
        //edtSoLuong.addTextChangedListener(new NumberTextWatcher(edtSoLuong));
        edtDonGia.addTextChangedListener(new NumberTextWatcher(edtDonGia));
//        edtSoLuong2.addTextChangedListener(new NumberTextWatcher(edtSoLuong2));
        edtSoLuongSum.addTextChangedListener(new NumberTextWatcher(edtSoLuongSum));
        edtSoLuong.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (edit_mode!="VIEW"){
                        UpdateSave();
                        CheckAddNew();
                        //aedtTenHS.requestFocus();
                    }
                }

                return true;
            }
        });
        edtDonGia.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    edtDonGia.setText(formatNumber(edtDonGia.getText()+""));
                }
            }
        });

        edtDonGia.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (edit_mode!="VIEW"){
                        UpdateSave();
                        hideSoftKeyboard(ThuDetailActivity.this);
                    }
                }
                return false;
            }
        });
        edtThanhTien.addTextChangedListener(new NumberTextWatcher(edtThanhTien));

        lvThuDetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mGestureDetector.onTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
                // return true;
            }
        });

        chkAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (chkAuto.isChecked()){
                    if (aedtTenHS.getText()+""==""){
                        chkAuto.setChecked(false);
                        return;
                    }else{
                        if (StringUtils.containsIgnoreCase(intentPhanLoai,"Cá phân")){
                            auToTenhs=aedtTenHS.getText()+"" ;
                            CheckAddNew();
                        }else{
                            String s="Cá chợ";
                            if (StringUtils.containsIgnoreCase(intentPhanLoai,"Mực khô")){
                                s="Mực khô";
                            }
                            new android.support.v7.app.AlertDialog.Builder(ThuDetailActivity.this)
                                    .setMessage("Sản phẩm là: "+s+ "\n" + "Không nên bật auto Sản phẩm để tránh nhầm lẩn\n\n"
                                            +"Có cần thiết bật auto?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            auToTenhs=aedtTenHS.getText()+"" ;
                                            CheckAddNew();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            chkAuto.setChecked(false);
                                            return;
                                        }
                                    })
                                    .show();
                        }


                    }
                }else{
                    CheckAddNew();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_START_BANHS_DETAIL ) {
            needRefresh = data.getBooleanExtra("needRefresh",false);
            // Refresh ListView
            if(needRefresh) {
                soluongTong=crudLocaldb.ThuDetail_SumSLHaiSanByRkeyThuTong(intentRkeyThuTong);
                arrThuDetail=crudLocaldb.ThuDetail_getAllThuDetailofRkeyThuTong(intentRkeyThuTong);
                int posittion=-1;
                for (int i=0;i<arrThuDetail.size();i++){
                    if (arrThuDetail.get(i).getRkey()==longGet(tvRkey.getText()+"")){
                        posittion=i;
                        break;
                    }
                }
                gotoRec(posittion);
                updateListThuDetail();

                this.needRefresh=true;
            }else{
                this.needRefresh=false;
            }
        }
    }

    private void CheckAddNew() {
        edit_mode="NEW";
        editDGAtMaster=false;
        tvId.setText("");
        tvRkey.setText("");
        tvServerKey.setText("");
        edtSoLuong2.setText("");
        edtSoLuong.setText("");
        edtSoLuongSum.setText(formatNumber(soluongTong));
        edtDonGia.setText("");
        edtThanhTien.setText("");
        if (chkAuto.isChecked()){
            aedtTenHS.setText(auToTenhs);
            edtSoLuong.setFocusable(true);
            edtSoLuong.setFocusableInTouchMode(true);
            mShowMenuSave=true;
            invalidateOptionsMenu();
            edtSoLuong.requestFocus();
            //Set return true in edtSoLuong.setOnEditorActionListener to not auto hide keyboard
            final String str = aedtTenHS.getText().toString();
            ListAdapter listAdapter = aedtTenHS.getAdapter();
            for(int i = 0; i < listAdapter.getCount(); i++) {
                String temp = listAdapter.getItem(i).toString();
                if(str.compareToIgnoreCase(temp) == 0) {
                    aedtTenHS.setText(temp);
                    auToTenhs=temp;
                    for (int j=0;j<listDaCoHaiSanNay.size();j++){
                        if (temp.compareToIgnoreCase(listDaCoHaiSanNay.get(j).toString())==0){
                            ThuDetail thudetail= new ThuDetail();
                            thudetail=crudLocaldb.ThuDetail_getThuDetailByTenhsAndRkeyThuTong(temp,intentRkeyThuTong);
                            tvId.setText(thudetail.getId()+"");
                            tvRkey.setText(thudetail.getRkey()+"");
                            tvServerKey.setText(thudetail.getServerkey()+"");
                            edtSoLuong2.setText(formatNumber(crudLocaldb.ThuDetail_getSoluongByRkey(longGet(tvRkey.getText()+""))));
                            return;
                        }
                    }
                    listDaCoHaiSanNay.add(temp);
                    return;
                }
            }
        }else{
            setEditMod(true);
            aedtTenHS.setText("");
            aedtTenHS.requestFocus();
            //showSoftKeyboard(this);
        }

    }

    private void CheckDelete() {
        if (checkBanHSDetail()){
            Toast.makeText(this, "Không thể xóa vì có chi tiết liên quan", Toast.LENGTH_SHORT).show();
            return;
        }
        if (intGet(tvServerKey.getText() + "")!=0){
            WantDeleteFromServer wdfs=new WantDeleteFromServer();
            wdfs.setmServerkey(intGet(tvServerKey.getText() + ""));
            wdfs.setmTablename("thudetail");
            crudLocaldb.WDFS_addWDFS(wdfs);
        }
        crudLocaldb.ThuDetail_deleteThuDetail(longGet(tvRkey.getText() + ""));
        capNhatThuTong();
        this.needRefresh=true;

        arrThuDetail=crudLocaldb.ThuDetail_getAllThuDetailofRkeyThuTong(intentRkeyThuTong);
        if (arrThuDetail.size()==0){
            this.finish();
        }else{
            //refesh screen
            tvId.setText("");
            tvRkey.setText("");
            tvServerKey.setText("");
            aedtTenHS.setText("");
            edtSoLuong.setText("");
            edtSoLuong2.setText("");
            edtSoLuongSum.setText(formatNumber(soluongTong));
            edtDonGia.setText("");
            edtThanhTien.setText("");
            edtDonGia.requestFocus();
            updateListThuDetail();
        }
    }

    private void UpdateSave() {
        if (daChia){
            Toast.makeText(this, "Dự án củ, dữ liệu chỉ được view", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isBad(aedtTenHS.getText()+"") || isBad(edtSoLuong.getText()+"")) {
            if(isBad(aedtTenHS.getText()+"")){
                Toast.makeText(ThuDetailActivity.this, "Không được bỏ trống dữ liệu", Toast.LENGTH_SHORT).show();
                return;
            }else if (doubleGet(edtSoLuong.getText()+"")==0.0){
                if (!editDGAtMaster){
                    Toast.makeText(ThuDetailActivity.this, "Không được bỏ trống dữ liệu", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    edtSoLuong.setText(edtSoLuong2.getText());
                }
            }
        }
        boolean addTrigger=false;
        long myKey=-1;
        if (longGet(tvRkey.getText()+"")==0){
            myKey=longGet(getCurrentTimeMiliS());
            addTrigger=true;
        }else{
            myKey=longGet(tvRkey.getText()+"");
        }
        if (!editDGAtMaster){
            BanHSDetail banhsdetail =new BanHSDetail();
            banhsdetail.setServerkey(0);
            banhsdetail.setRkey(longGet(getCurrentTimeMiliS()));
            banhsdetail.setRkeythu(intentRkeyThuTong);
            banhsdetail.setRkeythudetail(myKey);
            banhsdetail.setTenhs(aedtTenHS.getText()+"");
            banhsdetail.setSoluong(doubleGet(edtSoLuong.getText()+"")+"");
            banhsdetail.setUpdatetime(getCurrentTimeMiliS());

            long b=crudLocaldb.BanHSDetail_addBanHSDetail(banhsdetail);
            if (b==-1){
                Toast.makeText(this, "Xảy ra lổi trong quá trình ghi dữ liệu, tác vụ không thể hoàn tất", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
        ThuDetail thudetail = new ThuDetail();
        String sumSoLuongHS=crudLocaldb.BanHSDetail_SumSLHaiSanByRkeyThuDetail(myKey);
        String Dgia = crudLocaldb.DMHaiSan_getDgiaHaiSanByTen(aedtTenHS.getText()+"");
        if (longGet(edtDonGia.getText()+"")==0){
            edtDonGia.setText(formatNumber(Dgia));
        }
        String tt=String.valueOf(doubleGet(sumSoLuongHS)*longGet(edtDonGia.getText()+""));
        //vi tt se cho ra so khoa hoc dang 9.18E+09 nen can chuyen doi
        long thanhtien=Double.valueOf(tt).longValue();
        edtThanhTien.setText(thanhtien+"");
        //dua sẳn du lieuj  vao
        thudetail.setRkeythu(intentRkeyThuTong);
        thudetail.setTenhs(aedtTenHS.getText()+"");
        thudetail.setRkeyhs(longGet(crudLocaldb.DMHaiSan_getRkeyByTenhs(aedtTenHS.getText()+"")+""));
        thudetail.setSoluong(sumSoLuongHS);
        thudetail.setDongia(longGet(this,edtDonGia)+"");
        thudetail.setThanhtien(thanhtien+"");
        thudetail.setUpdatetime(getCurrentTimeMiliS());
        thudetail.setUsername(LOGIN_NAME);
        //neu la tao moi
        if (addTrigger) {
                thudetail.setServerkey(0);
                thudetail.setRkey(myKey);
                long i = crudLocaldb.ThuDetail_addThuDetail(thudetail);
                if (i != -1) {
                    Toast.makeText(this, "Đã thêm sp mới vào đơn hàng", Toast.LENGTH_SHORT).show();
                    capNhatThuTong();
                    this.needRefresh=true;
                    arrThuDetail=crudLocaldb.ThuDetail_getAllThuDetailofRkeyThuTong(intentRkeyThuTong);
                    setEditMod(false);
                    updateListThuDetail();
                }
        } else {
                thudetail.setId(intGet(tvId.getText()+""));
                thudetail.setRkey(longGet(String.valueOf(tvRkey.getText())));
                thudetail.setServerkey(intGet(String.valueOf(tvServerKey.getText())));
                int result = crudLocaldb.ThuDetail_updateThuDetail(thudetail);
                if (result > 0) {
                    if (!editDGAtMaster){
                        Toast.makeText(this, "Đã tăng số lượng sp trong đơn hàng", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "Updated new price successfully", Toast.LENGTH_SHORT).show();
                    }

                    CapNhatGiaHS();
                    capNhatThuTong();
                    this.needRefresh=true;
                    arrThuDetail=crudLocaldb.ThuDetail_getAllThuDetailofRkeyThuTong(intentRkeyThuTong);
                    setEditMod(false);
                    updateListThuDetail();
                }
        }
        edtSoLuong2.setText(crudLocaldb.ThuDetail_getSoluongByRkey(longGet(tvRkey.getText()+"")));
        soluongTong=crudLocaldb.ThuDetail_SumSLHaiSanByRkeyThuTong(intentRkeyThuTong);
        edtSoLuongSum.setText(formatNumber(soluongTong));
    }

    private void CapNhatGiaHS(){
        ArrayList<DMHaiSan>arrDMHaSan=new ArrayList<>();
        arrDMHaSan=crudLocaldb.DMHaiSan_getAllDMHaiSan();
        DMHaiSan dmhaisan=new DMHaiSan();
        for (int i=0;i<arrDMHaSan.size();i++){
            dmhaisan=arrDMHaSan.get(i);
            if (comPare(dmhaisan.getTenhs(),aedtTenHS.getText()+"")){
                if (longGet(dmhaisan.getDongia())!=longGet(edtDonGia.getText()+"")){
                    dmhaisan.setDongia(longGet(edtDonGia.getText()+"")+"");
                    dmhaisan.setUpdatetime(getCurrentTimeMiliS());
                    crudLocaldb.DMHaiSan_updateDMHaiSan(dmhaisan);
                }
            }
        }
    }

    private void capNhatThuTong(){
        ArrayList<Thu>arrThu=new ArrayList<>();
        arrThu=crudLocaldb.Thu_getThuByRkey(intentRkeyThuTong);
        if (arrThu.size()<=0){
            return;
        }
        Thu thu=arrThu.get(0);
        if (longGet(thu.getDatra()+"")!=0){
            return;
        }
        long rKeyKhachHang=thu.getRkeykhachhang();
        long rkeyChuyenBien=thu.getRkeychuyenbien();
        //Cap Nhat Thu
        String GiaTriToa=crudLocaldb.ThuDetail_SumTHANHTIENbyRkeyThuTong(intentRkeyThuTong);
        thu.setGiatri(GiaTriToa);
        thu.setUpdatetime(getCurrentTimeMiliS());
        long i=crudLocaldb.Thu_updateThu(thu);
        if (i>0){
            //Cap Nhat KhachHang
            String[] tongno = new String[2];
            tongno = crudLocaldb.Thu_SumGiaTriKhachHang(rKeyKhachHang);
            crudLocaldb.KhachHang_CapNhatNo(rKeyKhachHang, tongno[0], tongno[1], getCurrentTimeMiliS());
            //Cap Nhat ChuyenBien
            String tongthuchuyenbien="";
            tongthuchuyenbien=crudLocaldb.Thu_SumGiaTriChuyenBien(rkeyChuyenBien);
            crudLocaldb.ChuyenBien_CapNhatThu(rkeyChuyenBien,tongthuchuyenbien,getCurrentTimeMiliS());
        }
        this.needRefresh=true;

    }

    private  void setEditMod(boolean chohaykhong){
        if (chohaykhong==true){
            mShowMenuSave=true;
            edtDonGia.setFocusable(true);
            edtDonGia.setFocusableInTouchMode(true);
            edtSoLuong.setFocusable(true);
            edtSoLuong.setFocusableInTouchMode(true);
            aedtTenHS.setFocusable(true);
            aedtTenHS.setFocusableInTouchMode(true);
//            aedtTenHS.setThreshold(1);
        }else {
            edit_mode="VIEW";
            mShowMenuSave=false;
            edtDonGia.setFocusable(false);
            edtDonGia.setFocusableInTouchMode(false);
            edtSoLuong.setFocusable(false);
            edtSoLuong.setFocusableInTouchMode(false);
            aedtTenHS.setFocusable(false);
            aedtTenHS.setFocusableInTouchMode(false);
//            aedtTenHS.setThreshold(1000)
        }
        invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again
    }

    private String formatNumber(String tv) {
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        double lv = utils.doubleGet(tv);
        String get_value = formatter.format(lv);
        return get_value;
    }


    @Override
    public void finish() {
        // Chuẩn bị dữ liệu Intent.
        Intent data = new Intent();
        // Yêu cầu MainActivity refresh lại ListView hoặc không.
        data.putExtra("needRefresh", needRefresh);
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


