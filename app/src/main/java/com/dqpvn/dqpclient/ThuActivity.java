package com.dqpvn.dqpclient;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.restfullAPI;
import com.dqpvn.dqpclient.customadapters.CustomAdapterImgPath;
import com.dqpvn.dqpclient.models.BanHSDetail;
import com.dqpvn.dqpclient.models.ChuyenBien;
import com.dqpvn.dqpclient.models.ImgStore;
import com.dqpvn.dqpclient.models.Thu;
import com.dqpvn.dqpclient.models.ThuDetail;
import com.dqpvn.dqpclient.models.WantDeleteFromServer;
import com.dqpvn.dqpclient.utils.MyContextWrapper;
import com.dqpvn.dqpclient.utils.NumberTextWatcher;
import com.dqpvn.dqpclient.utils.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.dqpvn.dqpclient.crudmanager.SyncCheck.IS_ADMIN;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_NAME;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.RKEY_TICKET;
import static com.dqpvn.dqpclient.utils.utils.comPare;
import static com.dqpvn.dqpclient.utils.utils.getCurrentTimeMiliS;
import static com.dqpvn.dqpclient.utils.utils.getEditText;
import static com.dqpvn.dqpclient.utils.utils.hideSoftKeyboard;
import static com.dqpvn.dqpclient.utils.utils.intGet;
import static com.dqpvn.dqpclient.utils.utils.isBad;
import static com.dqpvn.dqpclient.utils.utils.isDate;
import static com.dqpvn.dqpclient.utils.utils.longGet;

public class ThuActivity extends AppCompatActivity {

    final private String TAG = getClass().getSimpleName();
    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;

    private int mShowMenuSave=0;
    private int ImgStoreWrokingID=-1;
    private boolean needRefresh,mHideAll=false, daChia=false;
    private Calendar cal;
    private ImageView imgThuHoaDon;
    private ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
    private final int REQUEST_TAKE_PHOTO = 123;
    private final int REQUEST_CHOOSE_PHOTO=132;
    private final int REQUEST_TAKE_PHOTO_BY_CMENU = 456;
    private final int REQUEST_CHOOSE_PHOTO_BY_CMENU=789;
    private final int REQUEST_VIEW_PHOTO = 321;
    private final int REQUEST_THU_DETAIL=124;
    private String realPhotoPath = "";
    private AutoCompleteTextView aedtThuChuyenBien, aedtThuKhachHang, aedtThuLyDo;
    private Button btnThuDetail, btnThuNewImg;
    private EditText edtThuNgayPs, edtThuGiaTri, edtThuDaTra;
    private TextView tvIdThu, tvRkeyThu, tvServerKey, tvThuHoaDon, tvThuUsername, tvThuDatra;
    private Spinner spnImgPath;
    long lastIDKhachHang, lastIDChuyenBien;

    //database
    private crudLocal crudLocaldb=crudLocal.getInstance(this);
    private String[] ChuyenBien_listChuyenBien;
    private String[] KhachHang_listKhachHang;
    //Khai báo ArrayAdapter cho Autocomplete
    private CustomAdapterImgPath adapterImagePath;
    private ArrayList<Thu> arrThu = new ArrayList<>();
    private ArrayList<ImgStore> arrImgPath =new ArrayList<>();
    private ArrayList<ImgStore>customImgPathAdapterData=new ArrayList<>();
    //Theo gioi cho phép Nhap lieu
    private String edit_mode="VIEW", intentUserName, imgStore_mode="VIEW";
    Uri imageUriFromShare;


    @Override
    public void onCreateContextMenu(ContextMenu manu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(manu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu_thuchi, manu);
        if (mHideAll || daChia){
            for (int i = 0; i < manu.size(); i++){
                manu.getItem(i).setVisible(false);
            }
        }
    }
    //Xử lý sự kiện khi click vào từng item
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.id_sendImg:
                if (realPhotoPath.length()>4 ) {
                    sendImage(realPhotoPath);
                }
                break;
            case R.id.id_replaceImgNew:
                dispatchTakePictureIntent(true);
                break;
            case R.id.id_replaceImgFromG:
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CHOOSE_PHOTO_BY_CMENU);
                break;
            case R.id.id_deleteImg:
                if (ImgStoreWrokingID>0){
                    int d=crudLocaldb.ImgStore_deleteImgStore(ImgStoreWrokingID);
                    if (d!=0){
                        ImgStore imgstore=crudLocaldb.ImgStore_getImgStoreById(ImgStoreWrokingID);
                        if (imgstore!=null){
                            if (imgstore.getServerkey()!=0){
                                WantDeleteFromServer wdfs=new WantDeleteFromServer();
                                wdfs.setmServerkey(imgstore.getServerkey());
                                wdfs.setmTablename("imgstore");
                                crudLocaldb.WDFS_addWDFS(wdfs);
                            }
                        }
                        imgThuHoaDon.setImageResource(0);
                        arrImgPath=crudLocaldb.ImgStore_getImgStoreByForTableAndStoreKey("thu",longGet(tvRkeyThu.getText()+""));
                        updateSpnImgPath();
                        spnImgPath.setSelection(customImgPathAdapterData.size()-1);

                    }
                }
                break;
        }
        return super.onContextItemSelected(item);
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
                return true;
            case R.id.id_edit:
                if (arrThu.size()==0){
                    return true;
                }
                if (isBad(intentUserName)){
                    return true;
                }
                if (!comPare(intentUserName,tvThuUsername.getText()+"") && !intentUserName.substring(0,5).equals("admin")){
                    Toast.makeText(ThuActivity.this, "Dữ liệu được bảo vệ...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (longGet(edtThuDaTra.getText()+"")!=0 && !intentUserName.substring(0,5).equals("admin")){
                    Toast.makeText(ThuActivity.this, "Dữ liệu được bảo vệ...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                CheckEdit();
                return true;
            case R.id.id_delete:
                //wrtite ơn logic
                if (arrThu.size()==0){
                    return true;
                }
                if (isBad(intentUserName)){
                    return true;
                }
                if (!comPare(intentUserName,tvThuUsername.getText()+"") && !intentUserName.substring(0,5).equals("admin")){
                    Toast.makeText(ThuActivity.this, "Dữ liệu được bảo vệ...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (checkThuDetail()){
                    Toast.makeText(ThuActivity.this, "Có chi tiết liên quan...", Toast.LENGTH_SHORT).show();
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
                hideSoftKeyboard(ThuActivity.this);
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
        setContentView(R.layout.activity_thu);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);

        if (isThuyenTruong() && !IS_ADMIN){
            mHideAll=true;
        }
        addControls();
        addEvents();
        initialization();
        setAdapter();
        registerForContextMenu(imgThuHoaDon);
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

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (checkThuDetail() && edit_mode=="VIEW") {
                    long rKeyKhachHang = crudLocaldb.KhachHang_getRkeyKhachHang(aedtThuKhachHang.getText() + "");
                    if (rKeyKhachHang == 0) {
                        aedtThuKhachHang.setText("0ther");
                        rKeyKhachHang = crudLocaldb.KhachHang_getRkeyKhachHang(aedtThuKhachHang.getText() + "");
                    }
                    long rkeyChuyenBien = crudLocaldb.ChuyenBien_getRkeyChuyenBien(getEditText(ThuActivity.this, aedtThuChuyenBien));
                    Intent intent = new Intent(ThuActivity.this, ThuDetailActivity.class);
                    intent.putExtra("rkeyThu", longGet(tvRkeyThu.getText() + ""));
                    intent.putExtra("rkeyKhachHang", rKeyKhachHang);
                    intent.putExtra("rkeyChuyenBien", rkeyChuyenBien);
                    String s="";
                    if (StringUtils.containsIgnoreCase(aedtThuLyDo.getText().toString().trim(),"Cá phân")){
                        s="Cá phân";
                    }else if (StringUtils.containsIgnoreCase(aedtThuLyDo.getText().toString().trim(),"Cá chợ")){
                        s="Cá chợ";
                    }else if (StringUtils.containsIgnoreCase(aedtThuLyDo.getText().toString().trim(),"Mực khô")){
                        s="Mực khô";
                    }else{
                        s="All";
                    }
                    intent.putExtra("phanLoai",s);
                    if (longGet(edtThuDaTra.getText() + "") != 0) {
                        intent.putExtra("hideAllMenu", true);
                    }
                    startActivityForResult(intent, REQUEST_THU_DETAIL);
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

    private void initialization() {
        utils.checkAndRequestPermissions(this);
        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .build();
        ImageLoader.getInstance().init(config);

        ChuyenBien_listChuyenBien = crudLocaldb.ChuyenBien_listChuyenBien();
        KhachHang_listKhachHang = crudLocaldb.KhachHang_listKhachHang();
        arrThu = crudLocaldb.Thu_getAllThu();


        Intent intent=getIntent();
        //có intent rồi thì lấy Bundle dựa vào MyPackage
        Bundle sharePackage=intent.getBundleExtra("SharePackage");
        intentUserName=intent.getStringExtra("userName");
        daChia=intent.getBooleanExtra("daChia",false);
        //Có Bundle rồi thì lấy các thông số dựa vào key

        if (sharePackage!=null){
            imageUriFromShare =sharePackage.getParcelable("imageUriFromShare");
            //imageLoader.displayImage(String.valueOf(imageUriFromShare), imgThuHoaDon);
            loadImage(String.valueOf(imageUriFromShare));
            edit_mode = "NEW";
            setEditMod(true);
            CheckAddNew();
        }else if (intent.getBooleanExtra("makeNew",false)){
            edit_mode = "NEW";
            setEditMod(true);
            CheckAddNew();
            if (intent.hasExtra("tenChuyenBien")){
                aedtThuChuyenBien.setText(intent.getStringExtra("tenChuyenBien"));
                aedtThuKhachHang.requestFocus();
            }
        }else{

            initData();
            if (edit_mode=="VIEW"){
                if (checkThuDetail()){
                    btnThuDetail.setEnabled(true);
                }else{
                    btnThuDetail.setEnabled(false);
                }
            }else{
                if (longGet(edtThuGiaTri.getText()+"")!=0){
                    if (checkThuDetail()){
                        edtThuGiaTri.setFocusable(false);
                        btnThuDetail.setEnabled(true);
                    }else{
                        edtThuGiaTri.setFocusable(true);
                        edtThuGiaTri.setFocusableInTouchMode(true);
                        btnThuDetail.setEnabled(false);
                    }
                }else{
                    btnThuDetail.setEnabled(true);
                    edtThuGiaTri.setFocusable(true);
                    edtThuGiaTri.setFocusableInTouchMode(true);
                }
            }
        }
    }

    private boolean checkThuDetail(){
        ArrayList<ThuDetail>arrThuDetail=new ArrayList<>();
        long rkreyThuTong=longGet(tvRkeyThu.getText()+"");
        arrThuDetail= crudLocaldb.ThuDetail_getAllThuDetailofRkeyThuTong(rkreyThuTong);
        if (arrThuDetail.size()>0) {
            return true;
        }else{
            return false;
        }
    }

    private void setAdapter() {
        if (adapterImagePath==null){
            customImgPathAdapterData.addAll(arrImgPath);
            adapterImagePath = new CustomAdapterImgPath(this, R.layout.customlist_imgpath,customImgPathAdapterData);
            spnImgPath.setAdapter(adapterImagePath);
        } else{
            updateSpnImgPath();
            spnImgPath.setSelection(adapterImagePath.getCount()-1);
        }
        String[] listLyDo = getResources().getStringArray(R.array.lydo_thu_array);
        ArrayAdapter<String> adapterCB = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ChuyenBien_listChuyenBien);
        ArrayAdapter<String> adapterKH = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, KhachHang_listKhachHang);
        ArrayAdapter<String> adapterLD = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listLyDo);
        aedtThuChuyenBien.setAdapter(adapterCB);
        aedtThuKhachHang.setAdapter(adapterKH);
        aedtThuLyDo.setAdapter(adapterLD);
        aedtThuKhachHang.setDropDownHeight(600);
    }

    private void updateSpnImgPath(){
        customImgPathAdapterData.clear();
        customImgPathAdapterData.addAll(arrImgPath);
        if(adapterImagePath!= null){
            adapterImagePath.notifyDataSetChanged();
        }
    }

    private void initData() {
        if (arrThu.size() >= 1) { //phong truong hop null k co record nao
            setEditMod(false);
            aedtThuChuyenBien.requestFocus();
            Intent intent=getIntent();
            Thu thu=new Thu();
            if (intent.hasExtra("thuRkey")){
                long rkeyThu=intent.getLongExtra("thuRkey",0);
                arrThu=crudLocaldb.Thu_getThuByRkey(rkeyThu);
                if (arrThu.size()>0){
                    thu = arrThu.get(0);
                    //Thu thu = arrThu.get(arrThu.size() - 1);
                    String tencb = crudLocaldb.ChuyenBien_getTenChuyenBien(thu.getRkeychuyenbien());
                    String tendt = crudLocaldb.KhachHang_getTenKhachHang(thu.getRkeykhachhang());
                    tvRkeyThu.setText(thu.getRkey() + "");
                    tvIdThu.setText(thu.getId() + "");
                    tvServerKey.setText(thu.getServerkey()+"");
                    tvThuUsername.setText(thu.getUsername()+"");
                    aedtThuChuyenBien.setText(tencb);
                    aedtThuKhachHang.setText(tendt);
                    aedtThuLyDo.setText(thu.getLydo());
                    edtThuNgayPs.setText(thu.getNgayps());
                    edtThuGiaTri.setText(formatNumber(thu.getGiatri() + ""));
                    edtThuDaTra.setText(formatNumber(thu.getDatra() + ""));
                    if (longGet(thu.getDatra())!=0 && !IS_ADMIN){
                        btnThuDetail.setVisibility(View.INVISIBLE);
                    }

                    arrImgPath= crudLocaldb.ImgStore_getImgStoreByForTableAndStoreKey("thu",longGet(thu.getRkey()+""));
                    if (arrImgPath.size()>0){
                        setAdapter();
                        //goto last
                        ImgStore imgstore=new ImgStore();
                        imgstore=arrImgPath.get(arrImgPath.size()-1);
                        realPhotoPath=imgstore.getImgpath();
                        ImgStoreWrokingID=imgstore.getId();
                        if (realPhotoPath.length()>4){
                            if (!realPhotoPath.substring(0,4).equals("http")){
                                grabImage(imgThuHoaDon,realPhotoPath);
                            }else{
                                try {
                                    //imageLoader.displayImage(realPhotoPath, imgThuHoaDon);
                                    loadImage(realPhotoPath);
                                } catch (Exception e){
                                    Log.e(TAG, "initData: "+e.toString() );
                                }

                            }
                        }
                    }
                }
            } else {
                //xg record cuoi cungcua spiner
                gotoRec(arrThu.size()-1);
            }
        }
    }

    private void gotoRec(int position){
        imgStore_mode="VIEW";
        arrThu = crudLocaldb.Thu_getAllThu();
        if (arrThu.size() >= 1) { //phong truong hop null k co record nao
            Thu thu;
            thu = arrThu.get(position);
            //Thu thu = arrThu.get(arrThu.size() - 1);
            String tencb = crudLocaldb.ChuyenBien_getTenChuyenBien(thu.getRkeychuyenbien());
            String tendt = crudLocaldb.KhachHang_getTenKhachHang(thu.getRkeykhachhang());
            String ckDathanh = thu.getDatra();
            tvRkeyThu.setText(thu.getRkey() + "");
            tvIdThu.setText(thu.getId() + "");
            tvServerKey.setText(thu.getServerkey() + "");
            tvThuUsername.setText(thu.getUsername()+"");
            aedtThuChuyenBien.setText(tencb);
            aedtThuKhachHang.setText(tendt);
            aedtThuLyDo.setText(thu.getLydo());
            edtThuNgayPs.setText(thu.getNgayps());
            edtThuGiaTri.setText(formatNumber(thu.getGiatri() + ""));
            edtThuDaTra.setText(formatNumber(thu.getDatra() + ""));

            arrImgPath= crudLocaldb.ImgStore_getImgStoreByForTableAndStoreKey("thu",longGet(thu.getRkey()+""));
            if (arrImgPath.size()>0){
                setAdapter();
                //goto last
                ImgStore imgstore=new ImgStore();
                imgstore=arrImgPath.get(arrImgPath.size()-1);
                realPhotoPath=imgstore.getImgpath();
                ImgStoreWrokingID=imgstore.getId();
                if (realPhotoPath.length()>4){
                    if (!realPhotoPath.substring(0,4).equals("http")){
                        grabImage(imgThuHoaDon,realPhotoPath);
                    }else{
                        try {
                            //imageLoader.displayImage(realPhotoPath, imgThuHoaDon);
                            loadImage(realPhotoPath);
                        } catch (Exception e){
                            Log.e(TAG, "initData: "+e.toString() );
                        }

                    }
                }
            }
            //khjong cho drop donwn bậy bạ
            setEditMod(false);
            aedtThuChuyenBien.requestFocus();
        }
    }

    private void addEvents() {
        aedtThuChuyenBien.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    // on focus off
                    String str = aedtThuChuyenBien.getText().toString();
                    try {
                        ListAdapter listAdapter = aedtThuChuyenBien.getAdapter();
                        for(int i = 0; i < listAdapter.getCount(); i++) {
                            String temp = listAdapter.getItem(i).toString();
                            if(str.compareTo(temp) == 0) {
                                return;
                            }
                        }
                        if (!crudLocaldb.ChuyenBien_isChuyenBienDaChia(str)){
                            aedtThuChuyenBien.setText("");
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        aedtThuLyDo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    // on focus off
                    String str = aedtThuLyDo.getText().toString().trim();
                    try {
                        long timthay=-1;
                        long rKeyChuyenBien=crudLocaldb.ChuyenBien_getRkeyChuyenBien(aedtThuChuyenBien.getText()+"");
                        ArrayList<Thu>arrThu=new ArrayList<>();
                        arrThu=crudLocaldb.Thu_getThuByChuyenBien(rKeyChuyenBien);
                        for (int i=0;i<arrThu.size();i++){
                            if (StringUtils.containsIgnoreCase(arrThu.get(i).getLydo(),str) &&
                                    longGet(arrThu.get(i).getDatra())==0 &&
                                    edit_mode!="VIEW"){
                                timthay=arrThu.get(i).getRkey();
                                break;
                            }
                        }
                        if (timthay>0){
                            final long timthay2=timthay;
                            new android.app.AlertDialog.Builder(ThuActivity.this)
                                    .setMessage("Khoản thu " + aedtThuLyDo.getText() + "đã có sẳn trong chuyến biển" + "\n\n" + "Có muốn cập nhật lại? ")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            ArrayList<Thu>arrThu2=new ArrayList<>();
                                            arrThu2 = crudLocaldb.Thu_getAllThu();
                                            int posittion=-1;
                                            for (int j=0;j<arrThu2.size();j++){
                                                if (arrThu2.get(j).getRkey()==timthay2){
                                                    posittion=j;
                                                    break;
                                                }
                                            }
                                            if (posittion>0){
                                                gotoRec(posittion);
                                            }
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }
            }
        });
        aedtThuKhachHang.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    // on focus off
                    String str = aedtThuKhachHang.getText().toString();

                    ListAdapter listAdapter = aedtThuKhachHang.getAdapter();
                    for(int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if(str.compareToIgnoreCase(temp) == 0) {
                            aedtThuKhachHang.setText(temp);
                            return;
                        }
                    }

                    aedtThuKhachHang.setText("");

                }
            }
        });
        //edtThuGiaTri.addTextChangedListener(new NumberTextWatcher(edtThuGiaTri));
        //edtThuDaTra.addTextChangedListener(new NumberTextWatcher(edtThuDaTra));
        edtThuGiaTri.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    edtThuGiaTri.setText(formatNumber(edtThuGiaTri.getText()+""));
                }
            }
        });
        edtThuDaTra.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    edtThuDaTra.setText(formatNumber(edtThuDaTra.getText()+""));
                }
            }
        });
        tvThuDatra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!IS_ADMIN || edit_mode=="VIEW"){return;}
                if (edtThuGiaTri.getText()+""!=""){
                    edtThuDaTra.setText(edtThuGiaTri.getText()+"");
                }
                ArrayList<BanHSDetail>arrBanHSDetail=crudLocaldb.BanHSDetail_getAllBanHSDetailofRkeyThuTong(longGet(tvRkeyThu.getText()+""));
                if (arrBanHSDetail.size()==0){
                    return;
                }
                // Hỏi trước khi xóa.
                new AlertDialog.Builder(ThuActivity.this)
                        .setMessage("Xóa BanHSDetail liên quan?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                XoaBanHSDetail(arrBanHSDetail);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

//        imgThuHoaDon.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                mGestureDetector.onTouchEvent(event);
//                return true;
//            }
//        });
        imgThuHoaDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgThuHoaDon.getDrawable() == null && edit_mode != "VIEW") {
                    dispatchTakePictureIntent(false);
                } else {
                    if (realPhotoPath.length()>4 ) {
                        if (realPhotoPath.substring(0,4).equals("http")){
                            Intent intent = new Intent(ThuActivity.this, FullscreenImageActivity.class);
                            intent.putExtra("realPhotoPath", realPhotoPath);
                            startActivity(intent);
                        }else{
                            viewHDImage(realPhotoPath);
                        }
                    }
                }
            }
        });
        edtThuNgayPs.setOnClickListener(new ThuActivity.MyEvents());
        edtThuNgayPs.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edtThuNgayPs.setText("");
                return false;
            }
        });
        btnThuNewImg.setOnClickListener(new MyEvents());
        btnThuDetail.setOnClickListener(new MyEvents());
        tvThuHoaDon.setOnClickListener(new MyEvents());

        spnImgPath.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ImgStore imgstore=customImgPathAdapterData.get(position);
                if (imgstore.getImgpath().length()>4){
                    realPhotoPath=imgstore.getImgpath();
                    ImgStoreWrokingID=imgstore.getId();
                    if (!realPhotoPath.substring(0,4).equals("http")){
                        File file=new File(realPhotoPath);
                        if (file.exists()) {
                            grabImage(imgThuHoaDon, realPhotoPath);
                        }
                    }else{
                        try {
                            //imageLoader.displayImage(realPhotoPath, imgThuHoaDon);
                            loadImage(realPhotoPath);
                        } catch (Exception e){
                            Log.e(TAG, "initData: "+e.toString() );
                        }

                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void XoaBanHSDetail(ArrayList<BanHSDetail> arrBanHSDetail){
        WantDeleteFromServer wdfs=new WantDeleteFromServer();
        BanHSDetail banhsdetail=new BanHSDetail();
        ArrayList<Long>arrRkey=new ArrayList<>();
        ArrayList<Integer>arrServerkey=new ArrayList<>();
        for (int i=0;i<arrBanHSDetail.size();i++){
            banhsdetail=arrBanHSDetail.get(i);
            arrRkey.add(banhsdetail.getRkey());
            if (banhsdetail.getServerkey()!=0){
                arrServerkey.add(banhsdetail.getServerkey());
            }
        }
        if (arrRkey.size()>0){
            for (int i=0; i<arrRkey.size();i++){
                crudLocaldb.BanHSDetail_deleteBanHSDetail(arrRkey.get(i));
            }
        }
        if (arrServerkey.size()>0){
            for (int i=0;i<arrServerkey.size();i++){
                wdfs.setmServerkey(arrServerkey.get(i));
                wdfs.setmTablename("banhsdetail");
                crudLocaldb.WDFS_addWDFS(wdfs);
            }
        }

    }

    private void addControls() {
        imgThuHoaDon = findViewById(R.id.img_ThuHoaDon);
        tvThuHoaDon=findViewById(R.id.tv_ThuHoaDon);
        tvThuUsername=findViewById(R.id.tv_ThuUsername);
        btnThuNewImg = findViewById(R.id.btn_ThuTakePhoTo);
        btnThuDetail=findViewById(R.id.btn_ThuDetail);
        aedtThuChuyenBien = (AutoCompleteTextView) findViewById(R.id.aedt_ThuChuyenBien);
        aedtThuKhachHang = (AutoCompleteTextView) findViewById(R.id.aedt_ThuKhachHang);
        aedtThuLyDo = findViewById(R.id.aedt_ThuLyDo);
        edtThuGiaTri = findViewById(R.id.edt_ThuGiaTri);
        edtThuNgayPs = findViewById(R.id.edt_ThuNgayPS);
        tvRkeyThu = findViewById(R.id.tv_RkeyThu);
        tvIdThu=findViewById(R.id.tv_IdThu);
        tvServerKey=findViewById(R.id.tv_ThuServerKey);
        tvThuDatra=findViewById(R.id.tv_ThuDaTra);
        edtThuDaTra = findViewById(R.id.edt_ThuDaTra);
        spnImgPath=findViewById(R.id.spn_ThuImagePath);
        tvThuHoaDon.setPaintFlags(tvThuHoaDon.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

    }

    private class MyEvents implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.edt_ThuNgayPS:
                    showDatePickerDialog(edtThuNgayPs);
                    break;
                case R.id.tv_ThuHoaDon:
                    if (edit_mode != "VIEW"){
                        intent=new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent,REQUEST_CHOOSE_PHOTO);
                    }
                    break;
                case R.id.btn_ThuDetail:
                    if (aedtThuKhachHang.getText()+""=="" || aedtThuChuyenBien.getText()+""=="" || edtThuNgayPs.getText()+""==""){
                        Toast.makeText(ThuActivity.this, "Cần nhập đủ dữ liệu.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    long rkeyKhachHang=crudLocaldb.KhachHang_getRkeyKhachHang(aedtThuKhachHang.getText()+"");
                    if (rkeyKhachHang==0){
                        aedtThuKhachHang.setText("0ther");
                        rkeyKhachHang=crudLocaldb.KhachHang_getRkeyKhachHang(aedtThuKhachHang.getText()+"");
                    }
                    if (intGet(tvIdThu.getText()+"")==0 && rkeyKhachHang!=0){
                        edit_mode = "NEW";
                        UpdateSave();
                    }
                    long idChuyenBien = crudLocaldb.ChuyenBien_getRkeyChuyenBien(getEditText(ThuActivity.this, aedtThuChuyenBien));
                    intent = new Intent(ThuActivity.this, ThuDetailActivity.class);
                    intent.putExtra("rkeyThu", longGet(tvRkeyThu.getText()+""));
                    intent.putExtra("rkeyKhachHang",rkeyKhachHang);
                    intent.putExtra("rkeyChuyenBien",idChuyenBien);
                    String s="";
                    if (StringUtils.containsIgnoreCase(aedtThuLyDo.getText().toString().trim(),"Cá phân")){
                        s="Cá phân";
                    }else if (StringUtils.containsIgnoreCase(aedtThuLyDo.getText().toString().trim(),"Cá chợ")){
                        s="Cá chợ";
                    }else if (StringUtils.containsIgnoreCase(aedtThuLyDo.getText().toString().trim(),"Mực khô")){
                        s="Mực khô";
                    }else{
                        s="All";
                    }
                    intent.putExtra("phanLoai",s);
                    if (longGet(edtThuDaTra.getText()+"")!=0){
                        intent.putExtra("hideAllMenu",true);
                    }
                    intent.putExtra("daChia",daChia);
                    startActivityForResult(intent,REQUEST_THU_DETAIL);
                    break;
                case R.id.btn_ThuTakePhoTo:
                    if (edit_mode != "VIEW") {
                        dispatchTakePictureIntent(false);
                    }
                    break;
            }

        }
    }

    private void loadImage(String imageUri){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true)
                .build();
        imageLoader.displayImage(imageUri, imgThuHoaDon, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                tvThuHoaDon.setPaintFlags(tvThuHoaDon.getPaintFlags() &   (~ Paint.UNDERLINE_TEXT_FLAG));
                tvThuHoaDon.setTextSize(16);
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                tvThuHoaDon.setTextSize(18);
                tvThuHoaDon.setText("Hóa đơn đính kèm");
                tvThuHoaDon.setPaintFlags(tvThuHoaDon.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
                tvThuHoaDon.setTypeface(tvThuHoaDon.getTypeface(), Typeface.BOLD_ITALIC);
            }
            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {

                tvThuHoaDon.setText("Đang tải hình ảnh hóa đơn... " + formatNumber(String.valueOf(current)) + "|"+formatNumber(String.valueOf(total)));
            }
        });
    }

    private String formatNumber(String tv) {
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        long lv = utils.longGet(tv);
        String get_value = formatter.format(lv);
        return get_value;
    }

    public void showDatePickerDialog(final EditText edtViewDate) {
        hideSoftKeyboard(ThuActivity.this);
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
        if (!utils.isDate(utils.getEditText(ThuActivity.this,edtViewDate))){
            //Dinh dang lai kieu ngay hien tai
            cal=Calendar.getInstance();
            SimpleDateFormat dft=new SimpleDateFormat("dd/MM/yyyy");
            //gan ngay thang hien tai da dc dinh dang cho s
            s=dft.format(cal.getTime());
        }else {
            s=utils.getEditText(ThuActivity.this,edtViewDate);
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

    private void UpdateSave() {
        if (daChia){
            Toast.makeText(this, "Dự án củ, dữ liệu chỉ được view", Toast.LENGTH_SHORT).show();
            return;
        }
        Thu thu = new Thu();
        lastIDKhachHang = crudLocaldb.KhachHang_getRkeyKhachHang(getEditText(this, aedtThuKhachHang));
        lastIDChuyenBien = crudLocaldb.ChuyenBien_getRkeyChuyenBien(getEditText(this, aedtThuChuyenBien));
        if (lastIDKhachHang == 0 || lastIDChuyenBien == 0) {
            Toast.makeText(ThuActivity.this, "Cần chính xác tên Dự án và Đối tác", Toast.LENGTH_SHORT).show();
            return;
        }


        String lydo = aedtThuLyDo.getText() + "";
        String ngayps = utils.getEditText(this, edtThuNgayPs);
        long giatri = longGet(this, edtThuGiaTri);
        long datra= longGet(this, edtThuDaTra);
        //int datra=Integer.valueOf(ckThuDaTra.)
        if (lastIDKhachHang == 0 || lastIDChuyenBien == 0 || !utils.isDate(ngayps)) {
            Toast.makeText(getApplicationContext(), "Cần nhập đủ dữ liệu", Toast.LENGTH_SHORT).show();
            return;
        }
        //dua sẳn du lieuj  vao thu
        thu.setRkeychuyenbien(lastIDChuyenBien);
        thu.setmTenChuyenBien(getEditText(this, aedtThuChuyenBien));
        thu.setmTenKhachHang(getEditText(this, aedtThuKhachHang));
        thu.setRkeykhachhang(lastIDKhachHang);
        thu.setLydo(lydo);
        thu.setNgayps(ngayps);
        thu.setGiatri(giatri+"");
        thu.setDatra(datra+"");
        thu.setUpdatetime(getCurrentTimeMiliS());
        thu.setUsername(intentUserName);

        if (imageUriFromShare!=null){
            File newFile;
            newFile=copyNewImage(imageUriFromShare);
            //make new compress file base on temp file and delete old
            realPhotoPath=newFile.getAbsolutePath();
            Bitmap myBitmap = BitmapFactory.decodeFile(realPhotoPath);
            //Bitmap myBitmap = utils.XoayImage(realPhotoPath);
            Bitmap newbmp = utils.resizeImage(myBitmap, 1280, 1280);
            byte[] bytes = utils.getByteArrayFromBitmap(newbmp, 100);
            //thang SavePhotoTask thay doi gia tri realPhotoPath tu cu thanh moi..
            new SavePhotoTask().execute(bytes);
            grabImage(imgThuHoaDon, realPhotoPath);
            if (realPhotoPath.length()>4){
                imgStore_mode="NEW";
            }
        }
        long newRkey=longGet(getCurrentTimeMiliS());
        if (imgThuHoaDon.getDrawable() != null && imgStore_mode=="NEW") {
            ImgStore imgstore=new ImgStore();
            imgstore.setServerkey(0);
            imgstore.setImgpath(realPhotoPath);
            imgstore.setNgayps(ngayps);
            imgstore.setUpdatetime(getCurrentTimeMiliS());
            imgstore.setUsername(intentUserName);
            imgstore.setFortable("thu");
            if (edit_mode == "NEW" && tvRkeyThu.getText() + "" == ""){
                imgstore.setStorekey(newRkey);
            }else {
                imgstore.setStorekey(longGet(tvRkeyThu.getText()+""));
            }
            long ImgStore_addImgStore=crudLocaldb.ImgStore_addImgStore(imgstore);
            if (ImgStore_addImgStore!=-1){
                arrImgPath=crudLocaldb.ImgStore_getImgStoreByForTableAndStoreKey("thu",longGet(imgstore.getStorekey()+""));
                updateSpnImgPath();
            }

        }
        //neu la tao moi
        if (edit_mode == "NEW" && tvRkeyThu.getText() + "" == "") {
            if (thu != null) {
                thu.setServerkey(0);
                thu.setRkey(newRkey);
                long i = crudLocaldb.Thu_addThu(thu);
                if (i != -1) {
                    // tinh tong no cho contact
                    String[] tongnokhachhang = new String[2];
                    tongnokhachhang = crudLocaldb.Thu_SumGiaTriKhachHang(lastIDKhachHang);
                    crudLocaldb.KhachHang_CapNhatNo(lastIDKhachHang, tongnokhachhang[0], tongnokhachhang[1], getCurrentTimeMiliS());
                    //capnhat lai so hui cho tàu
                    String tongthuchuyenbien="";
                    tongthuchuyenbien=crudLocaldb.Thu_SumGiaTriChuyenBien(lastIDChuyenBien);
                    crudLocaldb.ChuyenBien_CapNhatThu(lastIDChuyenBien,tongthuchuyenbien,getCurrentTimeMiliS());
                    this.needRefresh=true;

                    Toast.makeText(this, "Add new successfully", Toast.LENGTH_SHORT).show();
                    //goto record
                    arrThu=crudLocaldb.Thu_getAllThu();
                    gotoRec(arrThu.size()-1);
                    setAdapter();
                }
            }

        } else {
            if (edit_mode == "EDIT") {
                thu.setRkey(longGet(String.valueOf(tvRkeyThu.getText())));
                thu.setId(intGet(tvIdThu.getText()+""));
                thu.setServerkey(intGet(String.valueOf(tvServerKey.getText())));
                int result = crudLocaldb.Thu_updateThu(thu);
                if (result > 0) {
                    //cap nhat no doi tac
                    String[] tongno = new String[2];
                    tongno = crudLocaldb.Thu_SumGiaTriKhachHang(lastIDKhachHang);
                    crudLocaldb.KhachHang_CapNhatNo(lastIDKhachHang, tongno[0], tongno[1], getCurrentTimeMiliS());
                    //capnhat lai so hui cho tàu
                    String tongthuchuyenbien="";
                    tongthuchuyenbien=crudLocaldb.Thu_SumGiaTriChuyenBien(lastIDChuyenBien);
                    crudLocaldb.ChuyenBien_CapNhatThu(lastIDChuyenBien,tongthuchuyenbien,getCurrentTimeMiliS());
                    this.needRefresh=true;

                    //setEditMod(false);
                    Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show();
                    arrThu=crudLocaldb.Thu_getAllThu();
                    int position = -1;
                    for (int j = 0; j < arrThu.size(); j++) {
                        if (arrThu.get(j).getRkey() ==longGet(tvRkeyThu.getText()+"")) {
                            position = j;
                            gotoRec(position);
                            break;  // uncomment to get the first instance
                        }
                    }
                }
            }
        }
    }

    private File copyNewImage(Uri fromUri){
        File choosedFile, newFile;
        choosedFile = new File(utils.getRealPathFromURI(this,fromUri));
        //make temp file
        newFile=createImageFile();
        try {
            utils.copyFile(choosedFile,newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //createImageFile se gan lai gia tri cua realPhotoPath
        return newFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_TAKE_PHOTO_BY_CMENU) {
            if (resultCode==RESULT_OK){
                //copy file to new and resize it if needed
                final File file =new File(realPhotoPath);
                if(file.exists()){
                    //Bitmap myBitmap = BitmapFactory.decodeFile(realPhotoPath);
                    Bitmap myBitmap= null;
                    try {
                        myBitmap = utils.XoayImage(realPhotoPath);
                    } catch (Exception e) {
                        myBitmap = BitmapFactory.decodeFile(realPhotoPath);
                        e.printStackTrace();
                    }
                    //resize bitmap if needed
                    Bitmap newbmp=utils.resizeImage(myBitmap,1280,1280);
                    byte[] bytes=utils.getByteArrayFromBitmap(newbmp,100);
                    new ThuActivity.SavePhotoTask().execute(bytes);
                    grabImage(imgThuHoaDon,realPhotoPath);
                    if (requestCode==REQUEST_TAKE_PHOTO){
                        if (realPhotoPath.length()>4){
                            imgStore_mode="NEW";
                        }
                        UpdateSave();
                    }else{
                        if (ImgStoreWrokingID>0){
                            ImgStore imgStore=crudLocaldb.ImgStore_getImgStoreById(ImgStoreWrokingID);
                            if (StringUtils.left(imgStore.getImgpath(),4)!="http"){
                                crudLocaldb.ImgStore_deleteOldImg(imgStore.getImgpath());
                            }
                            imgStore.setImgpath(realPhotoPath);
                            imgStore.setUpdatetime(getCurrentTimeMiliS());
                            crudLocaldb.ImgStore_updateImgStore(imgStore);
                            arrImgPath=crudLocaldb.ImgStore_getImgStoreByForTableAndStoreKey("thu",longGet(tvRkeyThu.getText()+""));
                            updateSpnImgPath();
                        }
                    }

                }
            }
        }
        if (requestCode==REQUEST_CHOOSE_PHOTO || requestCode==REQUEST_CHOOSE_PHOTO_BY_CMENU) {
            if (resultCode==RESULT_OK && data !=null){
                Uri imageURI = data.getData();
                File newFile;
                newFile=copyNewImage(imageURI);
                //make new compress file base on temp file and delete old
                realPhotoPath=newFile.getAbsolutePath();
//            if(newFile.exists()) {
//                            InputStream is = null;
//            try {
//                is = getContentResolver().openInputStream(imageUri);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
                Bitmap myBitmap= null;
                try {
                    myBitmap = utils.XoayImage(realPhotoPath);
                } catch (Exception e) {
                    myBitmap = BitmapFactory.decodeFile(realPhotoPath);
                    e.printStackTrace();
                }
                //Bitmap myBitmap = utils.XoayImage(realPhotoPath);
                Bitmap newbmp = utils.resizeImage(myBitmap, 1280, 1280);
                byte[] bytes = utils.getByteArrayFromBitmap(newbmp, 100);
                //thang SavePhotoTask thay doi gia tri realPhotoPath tu cu thanh moi..
                new SavePhotoTask().execute(bytes);
                grabImage(imgThuHoaDon, realPhotoPath);
                UpdateSave();
                if (requestCode==REQUEST_CHOOSE_PHOTO){
                    if (realPhotoPath.length()>4){
                        imgStore_mode="NEW";
                    }
                    UpdateSave();
                }else{
                    if (ImgStoreWrokingID>0){
                        ImgStore imgStore=crudLocaldb.ImgStore_getImgStoreById(ImgStoreWrokingID);
                        if (StringUtils.left(imgStore.getImgpath(),4)!="http"){
                            crudLocaldb.ImgStore_deleteOldImg(imgStore.getImgpath());
                        }
                        imgStore.setImgpath(realPhotoPath);
                        imgStore.setUpdatetime(getCurrentTimeMiliS());
                        crudLocaldb.ImgStore_updateImgStore(imgStore);
                        arrImgPath=crudLocaldb.ImgStore_getImgStoreByForTableAndStoreKey("thu",longGet(tvRkeyThu.getText()+""));
                        updateSpnImgPath();
                    }
                }

            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_THU_DETAIL) {
            boolean needRefresh = data.getBooleanExtra("needRefresh",false);
            // Refresh ListView
            if(needRefresh) {
                this.needRefresh=true;
                arrThu = crudLocaldb.Thu_getAllThu();
                int position = -1;
                for (int j = 0; j < arrThu.size(); j++) {
                    if (arrThu.get(j).getRkey() ==longGet(tvRkeyThu.getText()+"")) {
                        position = j;
                        gotoRec(position);
                        break;  // uncomment to get the first instance
                    }
                }
            }
        }


    }

    class SavePhotoTask extends AsyncTask<byte[], String, String> {
        @Override
        protected String doInBackground(byte[]... jpg) {
            //File photo=new File(Environment.getExternalStorageDirectory(), "photo.jpg");
            final File oldFile=new File(realPhotoPath);
            File newFile = createImageFile();
            realPhotoPath = newFile.getPath();

            try {
                FileOutputStream fos = new FileOutputStream(newFile.getPath());

                fos.write(jpg[0]);
                fos.close();
            } catch (java.io.IOException e) {
                Log.e(TAG, "Exception in photoCallback", e);
            }
            //delete old file
            if (oldFile.exists()) {
                oldFile.delete();
            }
            return null;
        }
    }



    private void CheckAddNew() {
        tvIdThu.setText("");
        ImgStoreWrokingID=-1;
        tvRkeyThu.setText("");
        tvServerKey.setText("");
        tvThuUsername.setText(intentUserName);
        aedtThuChuyenBien.setText("");
        aedtThuKhachHang.setText("");
        aedtThuLyDo.setText("");
        edtThuNgayPs.setText("");
        edtThuGiaTri.setText("");
        edtThuDaTra.setText("");
        imgThuHoaDon.setImageResource(0);
        aedtThuChuyenBien.requestFocus();
        edit_mode = "NEW";
        setEditMod(true);
        arrImgPath=crudLocaldb.ImgStore_getImgStoreByForTableAndStoreKey("thu",longGet(tvRkeyThu.getText()+""));
        updateSpnImgPath();
    }

    private void CheckEdit() {
        edit_mode = "EDIT";
        setEditMod(true);
    }

    private void CheckDelete() {
        if (intGet(tvServerKey.getText() + "")!=0){
            WantDeleteFromServer wdfs=new WantDeleteFromServer();
            wdfs.setmServerkey(intGet(tvServerKey.getText() + ""));
            wdfs.setmTablename("thu");
            crudLocaldb.WDFS_addWDFS(wdfs);
        }
        arrImgPath=crudLocaldb.ImgStore_getImgStoreByForTableAndStoreKey("thu",longGet(tvRkeyThu.getText()+""));
        if (arrImgPath.size()>0){
            for (int i=0;i<arrImgPath.size();i++){
                ImgStore imgStore=new ImgStore();
                imgStore=arrImgPath.get(i);
                if (imgStore.getServerkey()!=0){
                    WantDeleteFromServer wdfs=new WantDeleteFromServer();
                    wdfs.setmServerkey(imgStore.getServerkey());
                    wdfs.setmTablename("imgstore");
                    crudLocaldb.WDFS_addWDFS(wdfs);
                }
            }
        }
        crudLocaldb.ImgStore_deleteImgStoreByForTableAndStoreKey("thu",longGet(tvRkeyThu.getText()+""));
        updateSpnImgPath();

        lastIDKhachHang = crudLocaldb.KhachHang_getRkeyKhachHang(aedtThuKhachHang.getText() + "");
        lastIDChuyenBien=crudLocaldb.ChuyenBien_getRkeyChuyenBien(aedtThuChuyenBien.getText()+"");
        crudLocaldb.Thu_deleteThu(longGet(tvRkeyThu.getText() + ""));
        //cap nhat nơ vào contac
        // tinh tong no cho Doitact
        String[] tongno = new String[2];
        tongno = crudLocaldb.Thu_SumGiaTriKhachHang(lastIDKhachHang);
        crudLocaldb.KhachHang_CapNhatNo(lastIDKhachHang, tongno[0], tongno[1], getCurrentTimeMiliS());
        //capnhat lai so hui cho tàu
        String tongthuchuyenbien;
        tongthuchuyenbien=this.crudLocaldb.Thu_SumGiaTriChuyenBien(lastIDChuyenBien);
        crudLocaldb.ChuyenBien_CapNhatThu(lastIDChuyenBien,tongthuchuyenbien,getCurrentTimeMiliS());
        this.needRefresh=true;
        //refesh screen
        tvIdThu.setText("");
        tvRkeyThu.setText("");
        tvServerKey.setText("");
        tvThuUsername.setText(intentUserName);
        aedtThuChuyenBien.setText("");
        aedtThuKhachHang.setText("");
        aedtThuLyDo.setText("");
        edtThuNgayPs.setText("");
        edtThuGiaTri.setText("");
        edtThuDaTra.setText("");
        imgThuHoaDon.setImageResource(0);
        aedtThuChuyenBien.requestFocus();
        ImgStoreWrokingID=-1;

    }

    private void setEditMod(boolean chohaykhong) {
        if (chohaykhong == true) {
            aedtThuKhachHang.setThreshold(1);
            aedtThuLyDo.setThreshold(1);
            mShowMenuSave=1;
            btnThuDetail.setEnabled(true);
            btnThuNewImg.setEnabled(true);
            aedtThuChuyenBien.setThreshold(1);
            if (longGet(edtThuGiaTri.getText()+"")!=0){
                if (checkThuDetail()){
                    if (IS_ADMIN){
                        new AlertDialog.Builder(this)
                                .setTitle("Cẩn trọng thao tác")
                                .setMessage("Record này có chi tiết liên quan, cẩn trọng khi edit. \n" +
                                        " Rất cần thiết bảo toàn tính nhất quán của dữ liệu. \n \n" +
                                        "Có muốn tiếp tục edit?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        edtThuGiaTri.setFocusable(true);
                                        edtThuGiaTri.setFocusableInTouchMode(true);
                                        btnThuDetail.setEnabled(false);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        edtThuGiaTri.setFocusable(false);
                                        btnThuDetail.setEnabled(true);
                                    }
                                })
                                .show();
                    }else{
                        edtThuGiaTri.setFocusable(false);
                        btnThuDetail.setEnabled(true);
                    }

                }else{
                    edtThuGiaTri.setFocusable(true);
                    edtThuGiaTri.setFocusableInTouchMode(true);
                    btnThuDetail.setEnabled(false);
                }
            }else{
                btnThuDetail.setEnabled(true);
                edtThuGiaTri.setFocusable(true);
                edtThuGiaTri.setFocusableInTouchMode(true);
            }
        } else {
            mShowMenuSave=0;
            btnThuDetail.setEnabled(false);
            btnThuNewImg.setEnabled(false);
            aedtThuChuyenBien.setThreshold(1000);
            aedtThuKhachHang.setThreshold(1000);
            aedtThuLyDo.setThreshold(1000);
            edit_mode = "VIEW";
            if (checkThuDetail()){
                btnThuDetail.setEnabled(true);
            }else{
                btnThuDetail.setEnabled(false);
            }
        }
        invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again
    }

    private void dispatchTakePictureIntent(boolean ByConTextMenu) {
        if (!isDate(String.valueOf(edtThuNgayPs.getText()))){
            Toast.makeText(this, "Cần nhập vào ngày phát sinh trước ", Toast.LENGTH_SHORT).show();
            return;
        }
        File photoFile=createImageFile();
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri outputUri = FileProvider.getUriForFile(this,
                this.getApplicationContext().getPackageName() + ".provider", photoFile);

        i.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ClipData clip =
                    ClipData.newUri(getContentResolver(), "A photo", outputUri);

            i.setClipData(clip);
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            List<ResolveInfo> resInfoList =
                    getPackageManager()
                            .queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, outputUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }

        try {
            if (ByConTextMenu){
                startActivityForResult(i, REQUEST_TAKE_PHOTO_BY_CMENU);
            }else{
                startActivityForResult(i, REQUEST_TAKE_PHOTO);
            }

        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "There are no camera on this device", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private File createImageFile(){
        String tenKhachHang= utils.loaiboDauVN(utils.getEditText(this,aedtThuKhachHang).replace(" ",""));
        String[] fdir=String.valueOf(edtThuNgayPs.getText()).split("/");
        String dirpath=fdir[2]+fdir[1]; //201704

        // String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        File dir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/" + dirpath);
        String imageFileName =tenKhachHang+ "-"+ getCurrentTimeMiliS();

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File image = null;
        try {
            image = File.createTempFile(imageFileName,".jpg",dir);
            realPhotoPath=image.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;


    }

    private void grabImage(ImageView imageView, String filePath) {
        //File outFile=new File(filePath);
        // Uri outputUri= FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", outFile);
        //this.getContentResolver().notifyChange(outputUri, null);
        // ContentResolver cr = this.getContentResolver();
        try
        {
            //bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, outputUri);
            Bitmap rotatedbitmap=utils.XoayImage(filePath);
            //Bitmap bmp= BitmapFactory.decodeFile(filePath);
            imageView.setImageBitmap(rotatedbitmap);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Image have problem, failed to load", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Failed to load", e);
        }
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
            startActivityForResult(i,REQUEST_VIEW_PHOTO);
        }
        catch (ActivityNotFoundException e) {
            Toast.makeText(this,"No any viwwer install on this device", Toast.LENGTH_SHORT).show();
        }

    }

    private void sendImage(String filePath){
        if (!filePath.substring(0,4).equals("http")){
            File outFile=new File(filePath);
            if (outFile.exists()){
                Intent i=new Intent(Intent.ACTION_SEND);
                Uri outputUri= FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", outFile);
                i.putExtra(Intent.EXTRA_STREAM, outputUri);
                i.setDataAndType(outputUri, "image/*");
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    startActivity(Intent.createChooser(i,"Share via..."));
                }
                catch (ActivityNotFoundException e) {
                    Toast.makeText(this,"No any viewer install on this device", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            Toast.makeText(this, "Download starting...", Toast.LENGTH_SHORT).show();
            String device_dir=getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            restfullAPI.downloadFile  downloadtask = new restfullAPI.downloadFile();
            downloadtask.setUpdateListener(new restfullAPI.downloadFile.OnUpdateListener() {
                @Override
                public void onUpdate(String realpath) throws ExecutionException, InterruptedException {
                    File outFile=new File(realpath);
                    if (outFile.exists()){
                        ImgStore imgStore=new ImgStore();
                        imgStore=crudLocaldb.ImgStore_getImgStoreById(ImgStoreWrokingID);
                        imgStore.setImgpath(realpath);
                        int u=crudLocaldb.ImgStore_updateImgStore(imgStore);

                        Intent i=new Intent(Intent.ACTION_SEND);
                        Uri outputUri= FileProvider.getUriForFile(ThuActivity.this, ThuActivity.this.getApplicationContext().getPackageName() + ".provider", outFile);
                        i.putExtra(Intent.EXTRA_STREAM, outputUri);
                        i.setDataAndType(outputUri, "image/*");
                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        try {
                            startActivity(Intent.createChooser(i,"Share via..."));
                        }
                        catch (ActivityNotFoundException e) {
                            Toast.makeText(ThuActivity.this,"No any viewer install on this device", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            downloadtask.execute(new String[]{filePath,device_dir});
        }

    }

    // Khi Activity này hoàn thành,
    // có thể cần gửi phản hồi gì đó về cho Activity đã gọi nó.
    @Override
    public void finish() {

        // Chuẩn bị dữ liệu Intent.
        Intent data = new Intent();
        // Yêu cầu MainActivity refresh lại ListView hoặc không.
        data.putExtra("needRefresh", needRefresh);
        if (aedtThuKhachHang.getText()+""!=""){
            lastIDKhachHang=crudLocaldb.KhachHang_getRkeyKhachHang(aedtThuKhachHang.getText()+"");
        }
        data.putExtra("rkeyKhachHang",lastIDKhachHang);
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

