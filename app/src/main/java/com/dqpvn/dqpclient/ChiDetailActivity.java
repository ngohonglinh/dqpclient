package com.dqpvn.dqpclient;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
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
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.customadapters.CustomAdapterChiDetail;
import com.dqpvn.dqpclient.customadapters.CustomAdapterImgPath;
import com.dqpvn.dqpclient.models.Chi;
import com.dqpvn.dqpclient.models.ChiDetail;
import com.dqpvn.dqpclient.models.ChuyenBien;
import com.dqpvn.dqpclient.models.ImgStore;
import com.dqpvn.dqpclient.models.WantDeleteFromServer;
import com.dqpvn.dqpclient.utils.MyContextWrapper;
import com.dqpvn.dqpclient.utils.NumberTextWatcher;
import com.dqpvn.dqpclient.utils.utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.dqpvn.dqpclient.utils.utils.comPare;
import static com.dqpvn.dqpclient.utils.utils.doubleGet;
import static com.dqpvn.dqpclient.utils.utils.getCurrentTimeMiliS;
import static com.dqpvn.dqpclient.utils.utils.getEditText;
import static com.dqpvn.dqpclient.utils.utils.hideSoftKeyboard;
import static com.dqpvn.dqpclient.utils.utils.intGet;
import static com.dqpvn.dqpclient.utils.utils.longGet;

public class ChiDetailActivity extends AppCompatActivity {

    final private String TAG = getClass().getSimpleName();
    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;


    private int lvPossition=-1;

    private int mShowMenuSave=0;
    private boolean needRefresh,mHideAll=false, daChia=false;
    private AutoCompleteTextView aedtChuyenBien, aedtSanPham;
    private EditText edtSoLuong, edtDonGia, edtThanhTien;
    private TextView tvIdChiDetail,tvRkeyChiDetail, tvServerKey, tvUserName, tvChiDetailRkeyChi, tvTenDoiTac, tvSanPham;
    private ListView lvChiDetail;
    private Spinner spnImgPath;

    //database
    private crudLocal crudLocaldb=crudLocal.getInstance(this);
    private ArrayList<ImgStore>arrImgPath;
    private String[] ChuyenBien_listChuyenBien;
    //Khai báo ArrayAdapter cho Autocomplete
    private ArrayAdapter<String> adapterCB, adapterSP;
    private ArrayList<ChiDetail> arrChiDetail = new ArrayList<>();
    private CustomAdapterChiDetail customAdapter;
    //Theo gioi cho phép Nhap lieu
    private String edit_mode="VIEW", TenDoiTac, intentUserName,intentChuyenBien, realPhotoPath;
    private ArrayList<ChiDetail> customadapterData=new ArrayList<>();
    private long intentRkeyChiTong, intentRkeyDoiTac, intentRkeyChuyenBien;


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
            lvPossition=lvChiDetail.pointToPosition( (int) x, (int) y );
            if (lvPossition<0){
                return true;
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
            lvPossition=lvChiDetail.pointToPosition( (int) e1.getX(), (int) e1.getY() );
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
        //mGestureDetector.onTouchEvent(event);
        //return super.onTouchEvent(event);
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }
    //lam viec voi menu
    @Override
    public boolean onCreateOptionsMenu(Menu manu) {
        getMenuInflater().inflate(R.menu.record_menu, manu);

        MenuItem mSave = manu.findItem(R.id.save);
        if (mHideAll || daChia){
            for (int i = 0; i < manu.size(); i++){
                manu.getItem(i).setVisible(false);
            }
            return true;
        }
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

        switch (itemId) {
            //ten cua cac id khi thiet ke cac resource menu
            case R.id.id_new:
                //wrtite ơn logic
                CheckAddNew();
                setEditMod(true);
                return true;
            case R.id.id_edit:
                if (arrChiDetail.size()==0){
                    return true;
                }
                if (!comPare(intentUserName,tvUserName.getText()+"") && !intentUserName.substring(0,5).equals("admin")){
                    Toast.makeText(ChiDetailActivity.this, "Dữ liệu được bảo vệ...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                CheckEdit();
                setEditMod(true);
                return true;
            case R.id.id_delete:
                if (!comPare(intentUserName,tvUserName.getText()+"") && !intentUserName.substring(0,5).equals("admin")){
                    Toast.makeText(ChiDetailActivity.this, "Dữ liệu được bảo vệ...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                //wrtite ơn logic
                if (arrChiDetail.size()==0){
                    return true;
                }
                //lay ra index hien tai cua listview
                // Hỏi trước khi xóa.
                new AlertDialog.Builder(this)
                        .setTitle("DQP Client")
                        .setMessage("Có chắc xóa?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                CheckDelete();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;


            case R.id.save:
                UpdateSave();
                hideSoftKeyboard(ChiDetailActivity.this);
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
        setContentView(R.layout.activity_chi_detail);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);

        addControls();
        addEvents();

        Intent intent=getIntent();
        intentRkeyChiTong=intent.getLongExtra("rkeyChi",0);
        intentRkeyDoiTac=intent.getLongExtra("rkeyDoiTac",0);
        intentRkeyChuyenBien=intent.getLongExtra("rkeyChuyenBien",0);
        intentUserName=intent.getStringExtra("userName");
        mHideAll=intent.getBooleanExtra("hideAllMenu",false);
        daChia=intent.getBooleanExtra("daChia",false);
        intentChuyenBien=crudLocaldb.ChuyenBien_getTenChuyenBien(intentRkeyChuyenBien);
        ChuyenBien_listChuyenBien = crudLocaldb.ChuyenBien_listChuyenBien();
        arrChiDetail=crudLocaldb.ChiDetail_getAllChiDetailofRkeyChiTong(intentRkeyChiTong);
        customadapterData.addAll(arrChiDetail);
        TenDoiTac=crudLocaldb.DoiTac_getTenDoiTac(intentRkeyDoiTac);

        tvTenDoiTac=(TextView) findViewById(R.id.tv_ChiDetaiTenDoiTac);
        tvTenDoiTac.setText(TenDoiTac);
        //arrChiDetail = crudLocaldb.ChiDetail_getAllChiDetailofIdChiTong(idChiTong);


        setAdapter();
        initData();

    }

    private Boolean isChuyenBienTong0Ther(){
        Boolean s =false;
        if (intentChuyenBien.contains("0ther")){
            s=true;
        }
        return s;
    }

    public void updateListChiDetail(){
        arrChiDetail=crudLocaldb.ChiDetail_getAllChiDetailofRkeyChiTong(intentRkeyChiTong);
        customadapterData.clear();
        customadapterData.addAll(arrChiDetail);
        customAdapter.notifyDataSetChanged();
    }


    private void setAdapter() {
        arrImgPath= crudLocaldb.ImgStore_getImgStoreByForTableAndStoreKey("chi",intentRkeyChiTong);
        CustomAdapterImgPath adapterImagePath = new CustomAdapterImgPath(this, R.layout.customlist_imgpath,arrImgPath);
        spnImgPath.setAdapter(adapterImagePath);

        if (customAdapter == null) {
            // gan data source cho adapter
            customAdapter = new CustomAdapterChiDetail(this, R.layout.customlist_chidetail,customadapterData);
            //gan adapter cho spinner
            lvChiDetail.setAdapter(customAdapter);
            // cho autocopletwe
            String[] listSanPham = getResources().getStringArray(R.array.lydo_array);

            adapterSP = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listSanPham);
            if (isChuyenBienTong0Ther()){
                adapterCB = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ChuyenBien_listChuyenBien);
                aedtChuyenBien.setAdapter(adapterCB);
                aedtChuyenBien.setFocusable(true);
                aedtChuyenBien.setFocusableInTouchMode(true);
            }else{
                aedtChuyenBien.setFocusable(false);
                aedtChuyenBien.setText(intentChuyenBien);
            }

            aedtSanPham.setAdapter(adapterSP);

        }else{
            updateListChiDetail();
            //cho troi xg record duoi cung
            lvChiDetail.setSelection(customAdapter.getCount()-1);
        }
    }

    private void initData() {
        ChiDetail chiDetail;
        arrChiDetail=crudLocaldb.ChiDetail_getAllChiDetailofRkeyChiTong(intentRkeyChiTong);
        if (arrChiDetail.size()>=1){
            chiDetail = arrChiDetail.get(arrChiDetail.size()-1);
            //Chi chi = arrChi.get(arrChi.size() - 1);
            tvIdChiDetail.setText(chiDetail.getId()+"");
            tvRkeyChiDetail.setText(chiDetail.getRkey() + "");
            tvServerKey.setText(chiDetail.getServerkey()+"");
            tvUserName.setText(chiDetail.getUsername());
            tvChiDetailRkeyChi.setText(intentRkeyChiTong+"");
            aedtChuyenBien.setText(chiDetail.getTenchuyenbien());
            tvTenDoiTac.setText(chiDetail.getTendoitac());
            aedtSanPham.setText(chiDetail.getSanpham());
            edtSoLuong.setText(formatNumber(chiDetail.getSoluong()));
            edtDonGia.setText(formatNumber(chiDetail.getDongia() + ""));
            //edtThanhTien.setText(chiDetail.getmThanhTien() + "/" + sumThoGhe());
            edtThanhTien.setText(sumThoGhe());
            //khjong cho drop donwn bậy bạ
            setEditMod(false);
            edtDonGia.requestFocus();
        }else{
            CheckAddNew();
        }
    }
    private String formatNumber(String tv) {
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        long lv = utils.longGet(tv);
        String get_value = formatter.format(lv);
        return get_value;
    }
    private String sumThoGhe(){
        long rkeychi=longGet(tvChiDetailRkeyChi.getText()+"");
        return crudLocaldb.ChiDetail_SumGiaTriChuyenBienAndChiTong(rkeychi,aedtChuyenBien.getText()+"");
    }


    private void gotoRec(int position){
        arrChiDetail = crudLocaldb.ChiDetail_getAllChiDetailofRkeyChiTong(intentRkeyChiTong);
        if (arrChiDetail.size() >= 1) { //phong truong hop null k co record nao
            ChiDetail chiDetail;
            chiDetail = arrChiDetail.get(position);
            //Chi chi = arrChi.get(arrChi.size() - 1);
            tvIdChiDetail.setText(chiDetail.getId()+"");
            tvRkeyChiDetail.setText(chiDetail.getRkey() + "");
            tvServerKey.setText(chiDetail.getServerkey() + "");
            tvUserName.setText(chiDetail.getUsername());
            tvChiDetailRkeyChi.setText(chiDetail.getRkeychi()+"");
            tvTenDoiTac.setText(chiDetail.getTendoitac());
            aedtChuyenBien.setText(chiDetail.getTenchuyenbien());
            aedtSanPham.setText(chiDetail.getSanpham());
            edtSoLuong.setText(formatNumber(chiDetail.getSoluong()));
            edtDonGia.setText(formatNumber(chiDetail.getDongia() + ""));
            //edtThanhTien.setText(chiDetail.getmThanhTien() + "/" + sumThoGhe());
            edtThanhTien.setText(sumThoGhe());
            //khjong cho drop donwn bậy bạ
            setEditMod(false);
            edtDonGia.requestFocus();
        }
    }

    private void addControls() {
        tvIdChiDetail=findViewById(R.id.tv_IdChiDetail);
        tvRkeyChiDetail = findViewById(R.id.tv_RkeyChiDetail);
        tvServerKey=findViewById(R.id.tv_ChiDetailServerKey);
        tvChiDetailRkeyChi = findViewById(R.id.tv_ChiDetailRkeyChi);
        tvUserName=findViewById(R.id.tv_ChiDetailUserName);
        aedtChuyenBien = (AutoCompleteTextView) findViewById(R.id.aedt_ChuyenBienChiDetail);
        tvTenDoiTac=findViewById(R.id.tv_ChiDetaiTenDoiTac);
        aedtSanPham = (AutoCompleteTextView)findViewById(R.id.aedt_SanPhamChiDeTail);
        edtSoLuong = findViewById(R.id.edt_SLChiDetail);
        edtDonGia = findViewById(R.id.edt_DGChiDetail);
        edtThanhTien = findViewById(R.id.edt_TTChiDetail);
        lvChiDetail = findViewById(R.id.lv_ChiDetail);
        spnImgPath=findViewById(R.id.spn_ChiDetailImagePath);
        tvSanPham=findViewById(R.id.tv_SanPhamChiDeTail);
    }


    private void addEvents() {
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

                }
            }
        });
        //edtSoLuong.addTextChangedListener(new NumberTextWatcher(edtSoLuong));
        //edtDonGia.addTextChangedListener(new NumberTextWatcher(edtDonGia));
        edtSoLuong.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    // on focus off
                    String str = edtSoLuong.getText().toString();
                    edtSoLuong.setText(formatNumber(str));

                }
            }
        });
        edtDonGia.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (edit_mode!="VIEW"){
                        UpdateSave();
                        CheckAddNew();
                    }
                }
                return false;
            }
        });
        edtThanhTien.addTextChangedListener(new NumberTextWatcher(edtThanhTien));
        lvChiDetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mGestureDetector.onTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
                // return true;
            }
        });

        spnImgPath.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ImgStore imgstore=arrImgPath.get(position);
                if (imgstore.getImgpath().length()>4){
                    realPhotoPath=imgstore.getImgpath();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tvSanPham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (realPhotoPath.substring(0,4).equals("http")){
                    Intent intent = new Intent(ChiDetailActivity.this, FullscreenImageActivity.class);
                    intent.putExtra("realPhotoPath", realPhotoPath);
                    startActivity(intent);
                }else{
                    File file=new File(realPhotoPath);
                    if (file.exists()){
                        viewHDImage(realPhotoPath);
                    }
                }
            }
        });
    }

    private void viewHDImage(String filePath){
        File outFile=new File(filePath);
        if (!outFile.exists()){
            return;
        }
        Intent i=new Intent(Intent.ACTION_VIEW);
        Uri outputUri= FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", outFile);
        i.setDataAndType(outputUri, "image/jpeg");
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(i);
        }
        catch (ActivityNotFoundException e) {
            Toast.makeText(this,"No any viewer install on this device", Toast.LENGTH_SHORT).show();
        }

    }

    private void CheckAddNew() {
        edit_mode = "NEW";

        tvIdChiDetail.setText("");
        tvRkeyChiDetail.setText("");
        tvServerKey.setText("");
        tvUserName.setText(intentUserName);
        tvChiDetailRkeyChi.setText(String.valueOf(intentRkeyChiTong));
        if (isChuyenBienTong0Ther()){
            aedtChuyenBien.setFocusable(true);
            aedtChuyenBien.setFocusableInTouchMode(true);
            aedtChuyenBien.setText("");
            aedtChuyenBien.requestFocus();
        }else{
            aedtChuyenBien.setFocusable(false);
            aedtChuyenBien.setText(intentChuyenBien);
            aedtSanPham.requestFocus();
        }
        aedtSanPham.setText("");
        edtSoLuong.setText("");
        edtDonGia.setText("");
        edtThanhTien.setText("");

    }

    private void CheckEdit() {
        edit_mode = "EDIT";
    }

    private void CheckDelete() {
        if (intGet(tvServerKey.getText() + "")!=0){
            WantDeleteFromServer wdfs=new WantDeleteFromServer();
            wdfs.setmServerkey(intGet(tvServerKey.getText() + ""));
            wdfs.setmTablename("chidetail");
            crudLocaldb.WDFS_addWDFS(wdfs);
        }
        intentRkeyChuyenBien=crudLocaldb.ChuyenBien_getRkeyChuyenBien(aedtChuyenBien.getText()+"");
        crudLocaldb.ChiDetail_deleteChiDetail(longGet(tvRkeyChiDetail.getText() + ""));
        capNhatChiTong();
        capNhatCongNo();
        capNhatSoHuiTau();
        this.needRefresh=true;
        //refesh screen
        tvIdChiDetail.setText("");
        tvRkeyChiDetail.setText("");
        tvServerKey.setText("");
        tvUserName.setText(intentUserName);
        tvChiDetailRkeyChi.setText(String.valueOf(intentRkeyChiTong));
        aedtChuyenBien.setText("");
        aedtSanPham.setText("");
        edtSoLuong.setText("");
        edtDonGia.setText("");
        edtThanhTien.setText("");
        edtDonGia.requestFocus();
        updateListChiDetail();
    }

    private void UpdateSave() {
        if (daChia){
            Toast.makeText(this, "Dự án củ, dữ liệu chỉ được view", Toast.LENGTH_SHORT).show();
            return;
        }
        ChiDetail chiDetail = new ChiDetail();
        intentRkeyChuyenBien= crudLocaldb.ChuyenBien_getRkeyChuyenBien(getEditText(this, aedtChuyenBien));
        if (intentRkeyChuyenBien == 0) {
            Toast.makeText(ChiDetailActivity.this, "Cần chính xác tên Dự án", Toast.LENGTH_SHORT).show();
            return;
        }

        String sanpham = aedtSanPham.getText() + "";
        double soluong = doubleGet(edtSoLuong.getText()+"");
        long dongia=longGet(this,edtDonGia);
        long thanhtien=Double.valueOf(soluong*dongia).longValue();

        if (intentRkeyChuyenBien == 0 || thanhtien==0 ) {
            Toast.makeText(getApplicationContext(), "Cần nhập đủ dữ liệu", Toast.LENGTH_SHORT).show();
            return;
        }
        //dua sẳn du lieuj  vao chi
        chiDetail.setRkeychi(intentRkeyChiTong);
        chiDetail.setTenchuyenbien(getEditText(this, aedtChuyenBien));
        chiDetail.setTendoitac(TenDoiTac);
        chiDetail.setSanpham(sanpham);
        chiDetail.setSoluong(soluong+"");
        chiDetail.setDongia(dongia+"");
        chiDetail.setThanhtien(String.valueOf(thanhtien));
        chiDetail.setUsername(intentUserName);
        chiDetail.setUpdatetime(getCurrentTimeMiliS());
        //neu la tao moi
        if (edit_mode == "NEW") {
            if (chiDetail != null) {
                chiDetail.setServerkey(0);
                chiDetail.setRkey(longGet(getCurrentTimeMiliS()));
                long i = crudLocaldb.ChiDetail_addChiDetail(chiDetail);
                if (i != -1) {
                    capNhatChiTong();
                    capNhatCongNo();
                    capNhatSoHuiTau();
                    this.needRefresh=true;
                    Toast.makeText(this, "Add new successfully", Toast.LENGTH_SHORT).show();
                    arrChiDetail=crudLocaldb.ChiDetail_getAllChiDetailofRkeyChiTong(intentRkeyChiTong);
                    gotoRec(arrChiDetail.size()-1);
                    //setEditMod(false);
                    updateListChiDetail();
                }
            }
        } else {
            if (edit_mode == "EDIT") {
                chiDetail.setId(intGet(tvIdChiDetail.getText()+""));
                chiDetail.setRkey(longGet(String.valueOf(tvRkeyChiDetail.getText())));
                chiDetail.setServerkey(intGet(String.valueOf(tvServerKey.getText())));
                int result = crudLocaldb.ChiDetail_updateChiDetail(chiDetail);
                if (result > 0) {
                    capNhatChiTong();
                    capNhatCongNo();
                    capNhatSoHuiTau();
                    this.needRefresh=true;
                    //setEditMod(false);
                    Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show();
                    int position = -1;
                    arrChiDetail = crudLocaldb.ChiDetail_getAllChiDetailofRkeyChiTong(intentRkeyChiTong);
                    for (int z = 0; z < arrChiDetail.size(); z++) {
                        if (arrChiDetail.get(z).getRkey() ==longGet(tvRkeyChiDetail.getText()+"")) {
                            position = z;
                            gotoRec(position);
                            break;  // uncomment to get the first instance
                        }
                    }
                    updateListChiDetail();
                }
            }
        }
    }

    private void capNhatChiTong(){
        String tongchi=crudLocaldb.ChiDetail_SumGiaTriChiTong(intentRkeyChiTong);
        crudLocaldb.Chi_CapNhatChi(intentRkeyChiTong,tongchi,"0", getCurrentTimeMiliS());
    }

    private void capNhatCongNo(){
        // tinh tong no cho Doitact
        String[] tongno = new String[2];
        tongno = crudLocaldb.Chi_SumGiaTriDoiTac(intentRkeyDoiTac);
        crudLocaldb.DoiTac_CapNhatNo(intentRkeyDoiTac, tongno[0], tongno[1], getCurrentTimeMiliS());
    }
    private void capNhatSoHuiTau(){
        ArrayList<Chi>arrChi=new ArrayList<>();
        ArrayList<ChuyenBien>arrChuyenBien=new ArrayList<>();
        arrChuyenBien=crudLocaldb.ChuyenBien_getOnlyWorkingChuyenBien();
        long rKey0ther=crudLocaldb.ChuyenBien_getRkeyChuyenBien("0ther");
        arrChi=crudLocaldb.Chi_getAllChi();
        arrChiDetail=crudLocaldb.ChiDetail_getAllChiDetail();
        for (int i=0;i<arrChuyenBien.size();i++){
            ChuyenBien chuyenbien=new ChuyenBien();
            chuyenbien=arrChuyenBien.get(i);
            long tongchiChuyenBien=0;
            for (int j=0;j<arrChi.size();j++){
                Chi chi=new Chi();
                chi=arrChi.get(j);
                if (chi.getRkeychuyenbien()==chuyenbien.getRkey()){
                    tongchiChuyenBien=tongchiChuyenBien+longGet(chi.getGiatri());
                }else{
                    if (chi.getRkeychuyenbien()==rKey0ther){
                        for (int h=0;h<arrChiDetail.size();h++){
                            ChiDetail chidetail=new ChiDetail();
                            chidetail=arrChiDetail.get(h);
                            if (chidetail.getRkeychi()==chi.getRkey() &&
                                    comPare(chidetail.getTenchuyenbien(),chuyenbien.getChuyenbien())){
                                tongchiChuyenBien=tongchiChuyenBien+longGet(chidetail.getThanhtien());
                            }
                        }
                    }
                }
            }
            if (!comPare(chuyenbien.getTongchi(),String.valueOf(tongchiChuyenBien))){
                crudLocaldb.ChuyenBien_CapNhatChi(chuyenbien.getRkey(),String.valueOf(tongchiChuyenBien),getCurrentTimeMiliS());
            }
        }
    }




    private void setEditMod(boolean chohaykhong) {
        if (chohaykhong == true) {
            mShowMenuSave=1;
            aedtChuyenBien.setThreshold(1);
        } else {
            mShowMenuSave=0;
            aedtChuyenBien.setThreshold(1000);
            edit_mode = "VIEW";
        }
        invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again
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
