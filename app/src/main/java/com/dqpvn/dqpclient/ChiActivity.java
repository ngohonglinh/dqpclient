package com.dqpvn.dqpclient;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dqpvn.dqpclient.models.DoiTac;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;


import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.restfullAPI;
import com.dqpvn.dqpclient.customadapters.CustomAdapterEmptyString;
import com.dqpvn.dqpclient.customadapters.CustomAdapterImgPath;
import com.dqpvn.dqpclient.models.Chi;
import com.dqpvn.dqpclient.models.ChiDetail;
import com.dqpvn.dqpclient.models.ChuyenBien;
import com.dqpvn.dqpclient.models.ImgStore;
import com.dqpvn.dqpclient.models.Ticket;
import com.dqpvn.dqpclient.models.WantDeleteFromServer;
import com.dqpvn.dqpclient.utils.MyContextWrapper;
import com.dqpvn.dqpclient.utils.NumberTextWatcher;
import com.dqpvn.dqpclient.utils.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
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

public class ChiActivity extends AppCompatActivity {

    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;


    final private String TAG = getClass().getSimpleName();

    private int mShowMenuSave=0;
    private boolean needRefresh, syncTableChiDetail=false,mHideAll=false, daChia=false;
    private Calendar cal;
    private ImageView imgChiHoaDon;
    private ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
    private DisplayImageOptions options;
    private final int REQUEST_TAKE_PHOTO = 123;
    private final int REQUEST_CHOOSE_PHOTO=132;
    private final int REQUEST_VIEW_PHOTO = 321;
    private final int REQUEST_TAKE_PHOTO_BY_CMENU=687;
    private final int REQUEST_CHOOSE_PHOTO_BY_CMENU = 786;
    private final int REQUEST_CHI_DETAIL = 555;
    private final int REQUEST_CHI_DEBT=333;
    private int ImgStoreWrokingID=-1;
    private AutoCompleteTextView aedtChiChuyenBien, aedtChiDoiTac;
    private Spinner spnNhomLydo;
    private Button btnChiDetail, btnChiNewImg;
    private EditText edtChiNgayPs, edtChiGiaTri, edtChiDaTra, edtChiLyDo;
    private TextView tvIdChi,tvRkeyChi, tvServerKey, tvChiHoaDon, tvRkeyTicket, tvUsername, tvChiGiaTri, tvChiDatra;
    private Spinner spnImgPath;
    long rkeyDoiTac, rkeyChuyenBien, rkeyTicket=0;
    String intentUserName="", intentWhoStart="", realPhotoPath = "";

    //database

    private crudLocal crudLocaldb=crudLocal.getInstance(this);
    private String[] listChuyenBien;
    private String[] listDoiTac;

    //Khai báo ArrayAdapter cho Autocomplete
    private CustomAdapterImgPath adapterImagePath;
    private ArrayList<Chi> arrChi = new ArrayList<>();

    private ArrayList<ImgStore> arrImgPath =new ArrayList<>();
    private ArrayList<ImgStore>customImgPathAdapterData=new ArrayList<>();

    //Theo gioi cho phép Nhap lieu
    private String edit_mode="VIEW", imgStore_mode="VIEW";
    private Uri imageUriFromShare;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu_thuchi, menu);
        if (mHideAll || daChia){
            for (int i = 0; i < menu.size(); i++){
                menu.getItem(i).setVisible(false);
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
                        imgChiHoaDon.setImageResource(0);
                        arrImgPath=crudLocaldb.ImgStore_getImgStoreByForTableAndStoreKey("chi",longGet(tvRkeyChi.getText()+""));
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
        if (mHideAll || daChia){
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
                if (arrChi.size()==0){
                    return true;
                }
                if (isBad(intentUserName)){
                    return true;
                }
                if (!comPare(intentUserName,tvUsername.getText()+"") && !intentUserName.substring(0,5).equals("admin")){
                    Toast.makeText(ChiActivity.this, "Dữ liệu được bảo vệ...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                CheckEdit();
                return true;
            case R.id.id_delete:
                //wrtite ơn logic
                if (arrChi.size()==0){
                    return true;
                }
                if (isBad(intentUserName)){
                    return true;
                }
                if (!comPare(intentUserName,tvUsername.getText()+"") && !intentUserName.substring(0,5).equals("admin")){
                    Toast.makeText(ChiActivity.this, "Dữ liệu được bảo vệ...", Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (checkChiDetail()){
                    Toast.makeText(this, "Không thể xóa vì đang có chi tiết liên quan", Toast.LENGTH_SHORT).show();
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
                hideSoftKeyboard(ChiActivity.this);
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
        setContentView(R.layout.activity_chi);

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
        registerForContextMenu(imgChiHoaDon);
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
                if (checkChiDetail() && edit_mode=="VIEW"){
                    long idDoiTac=crudLocaldb.DoiTac_getRkeyDoiTac(aedtChiDoiTac.getText()+"");
                    if (idDoiTac==0){
                        aedtChiDoiTac.setText("0ther");
                        idDoiTac=crudLocaldb.DoiTac_getRkeyDoiTac(aedtChiDoiTac.getText()+"");
                    }
                    long idChuyenBien = crudLocaldb.ChuyenBien_getRkeyChuyenBien(getEditText(ChiActivity.this, aedtChiChuyenBien));
                    Intent intent = new Intent(ChiActivity.this, ChiDetailActivity.class);
                    intent.putExtra("rkeyChi", longGet(tvRkeyChi.getText()+""));
                    intent.putExtra("rkeyDoiTac",idDoiTac);
                    intent.putExtra("userName",intentUserName);
                    intent.putExtra("rkeyChuyenBien",idChuyenBien);
                    intent.putExtra("rkeyTicket", rkeyTicket);
                    if (longGet(edtChiDaTra.getText()+"")!=0){
                        intent.putExtra("hideAllMenu",true);
                    }
                    startActivityForResult(intent,REQUEST_CHI_DETAIL);
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
        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .build();
        ImageLoader.getInstance().init(config);


        listChuyenBien = crudLocaldb.ChuyenBien_listChuyenBien();
        listDoiTac = crudLocaldb.DoiTac_listDoiTac();
        arrChi = crudLocaldb.Chi_getAllChi();

        Intent intent=getIntent();
        //có intent rồi thì lấy Bundle dựa vào MyPackage
        Bundle sharePackage=intent.getBundleExtra("SharePackage");
        intentUserName = intent.getStringExtra("userName");
        intentWhoStart=intent.getStringExtra("whoStart");
        daChia=intent.getBooleanExtra("daChia",false);
        rkeyTicket = intent.getLongExtra("rkeyTicket", 0);
        if (intent.getBooleanExtra("makeNew",false)){
            //re quest make new record
            CheckAddNew();
            if (intent.hasExtra("tenChuyenBien")){
                aedtChiChuyenBien.setText(intent.getStringExtra("tenChuyenBien"));
                aedtChiDoiTac.requestFocus();
            }
            return;
        }
        //gotoTicket();
        //Có Bundle rồi thì lấy các thông số dựa vào key
        if (sharePackage!=null){
            imageUriFromShare =sharePackage.getParcelable("imageUriFromShare");
            loadImage(String.valueOf(imageUriFromShare));
            //imageLoader.displayImage(String.valueOf(imageUriFromShare), imgChiHoaDon);

            setEditMod(true);
            CheckAddNew();
        }else{
            initData();
            if (edit_mode=="VIEW"){
                if (checkChiDetail()){
                    btnChiDetail.setEnabled(true);
                }else{
                    btnChiDetail.setEnabled(false);
                }
            }else{
                if (longGet(edtChiGiaTri.getText()+"")!=0){
                    if (checkChiDetail()){
                        edtChiGiaTri.setFocusable(false);
                        btnChiDetail.setEnabled(true);
                    }else{
                        edtChiGiaTri.setFocusable(true);
                        edtChiGiaTri.setFocusableInTouchMode(true);
                        btnChiDetail.setEnabled(false);
                    }
                }else{
                    btnChiDetail.setEnabled(true);
                    edtChiGiaTri.setFocusable(true);
                    edtChiGiaTri.setFocusableInTouchMode(true);
                }
            }
        }
    }

    private void loadImage(String imageUri){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true)
                .build();
        imageLoader.displayImage(imageUri, imgChiHoaDon, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                tvChiHoaDon.setPaintFlags(tvChiHoaDon.getPaintFlags() &   (~ Paint.UNDERLINE_TEXT_FLAG));
                tvChiHoaDon.setTextSize(16);
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                tvChiHoaDon.setTextSize(18);
                tvChiHoaDon.setText("Hóa đơn đính kèm");
                tvChiHoaDon.setPaintFlags(tvChiHoaDon.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
                tvChiHoaDon.setTypeface(tvChiHoaDon.getTypeface(), Typeface.BOLD_ITALIC);
            }
            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {

                tvChiHoaDon.setText("Đang tải hình ảnh hóa đơn... " + formatNumber(String.valueOf(current)) + "|"+formatNumber(String.valueOf(total)));
            }
        });
    }
    private String formatNumber(String str) {
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        long lv = utils.longGet(str);
        String get_value = formatter.format(lv);
        return get_value;
    }

    private boolean checkChiDetail(){
        ArrayList<ChiDetail>arrChiDetail=new ArrayList<>();
        long rkreyChiTong=longGet(tvRkeyChi.getText()+"");
        arrChiDetail=crudLocaldb.ChiDetail_getAllChiDetailofRkeyChiTong(rkreyChiTong);
        if (arrChiDetail.size()>0) {
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



        ArrayAdapter<String> adapterCB = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listChuyenBien);
        ArrayAdapter<String> adapterDT = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listDoiTac);
        aedtChiChuyenBien.setAdapter(adapterCB);
        aedtChiDoiTac.setAdapter(adapterDT);
        aedtChiDoiTac.setDropDownHeight(600);
        String[] listLyDo = getResources().getStringArray(R.array.lydo_array);
        CustomAdapterEmptyString<String> adapterNhomLyDo = new CustomAdapterEmptyString<String>(this,
                android.R.layout.simple_spinner_dropdown_item, listLyDo);
        spnNhomLydo.setAdapter(adapterNhomLyDo);
        spnNhomLydo.setSelection(spnNhomLydo.getCount()-1);

    }

    private void updateSpnImgPath(){
        customImgPathAdapterData.clear();
        customImgPathAdapterData.addAll(arrImgPath);
        if(adapterImagePath!= null){
            adapterImagePath.notifyDataSetChanged();

        }

    }

    private void initData() {
        if (arrChi.size() >= 1) { //phong truong hop null k co record nao
            setEditMod(false);
            aedtChiChuyenBien.requestFocus();
            Intent intent=getIntent();
            Chi chi;
            if (intent.hasExtra("chiRkey")){
                long rKeyChi=intent.getLongExtra("chiRkey",0);
                arrChi=crudLocaldb.Chi_getChiByRkey(rKeyChi);
                if (arrChi.size()>=1){
                    chi = arrChi.get(0);
                    //Chi chi = arrChi.get(arrChi.size() - 1);
                    String tencb = crudLocaldb.ChuyenBien_getTenChuyenBien(chi.getRkeychuyenbien());
                    String tendt = crudLocaldb.DoiTac_getTenDoiTac(chi.getRkeydoitac());
                    tvIdChi.setText(chi.getId()+"");
                    tvRkeyChi.setText(chi.getRkey() + "");
                    tvServerKey.setText(chi.getServerkey()+"");
                    tvRkeyTicket.setText(chi.getRkeyticket()+"");
                    tvUsername.setText(chi.getUsername());
                    aedtChiChuyenBien.setText(tencb);
                    aedtChiDoiTac.setText(tendt);
                    edtChiLyDo.setText(chi.getLydo());
                    edtChiNgayPs.setText(chi.getNgayps());
                    edtChiGiaTri.setText(formatNumber(chi.getGiatri() + ""));
                    edtChiDaTra.setText(formatNumber(chi.getDatra() + ""));

                    arrImgPath=crudLocaldb.ImgStore_getImgStoreByForTableAndStoreKey("chi",longGet(chi.getRkey()+""));
                    if (arrImgPath.size()>0){
                        setAdapter();
                        //goto last
                        ImgStore imgstore=new ImgStore();
                        imgstore=arrImgPath.get(arrImgPath.size()-1);
                        realPhotoPath=imgstore.getImgpath();
                        ImgStoreWrokingID=imgstore.getId();
                        if (realPhotoPath.length()>4){
                            if (!realPhotoPath.substring(0,4).equals("http")){
                                grabImage(imgChiHoaDon,realPhotoPath);
                            }else{
                                try {
                                    loadImage(realPhotoPath);
                                    //imageLoader.displayImage(realPhotoPath, imgChiHoaDon);
                                } catch (Exception e){
                                    Log.e(TAG, "initData: "+e.toString() );
                                }

                            }
                        }
                    }
                }
            } else {
                //xg record cuoi cungcua spiner
                gotoRec(arrChi.size()-1);
            }

        }
    }

    private void gotoRec(int position){
        imgStore_mode="VIEW";
        arrChi = crudLocaldb.Chi_getAllChi();
        if (arrChi.size() >= 1) { //phong truong hop null k co record nao
            Chi chi;
            chi = arrChi.get(position);
            //Chi chi = arrChi.get(arrChi.size() - 1);
            String tencb = crudLocaldb.ChuyenBien_getTenChuyenBien(chi.getRkeychuyenbien());
            String tendt = crudLocaldb.DoiTac_getTenDoiTac(chi.getRkeydoitac());
            tvIdChi.setText(chi.getId()+"");
            tvRkeyChi.setText(chi.getRkey() + "");
            tvServerKey.setText(chi.getServerkey() + "");
            tvRkeyTicket.setText(chi.getRkeyticket()+"");
            tvUsername.setText(chi.getUsername()+"");
            aedtChiChuyenBien.setText(tencb);
            aedtChiDoiTac.setText(tendt);
            edtChiLyDo.setText(chi.getLydo());
            edtChiNgayPs.setText(chi.getNgayps());
            edtChiGiaTri.setText(formatNumber(chi.getGiatri() + ""));
            edtChiDaTra.setText(formatNumber(chi.getDatra() + ""));
            arrImgPath=crudLocaldb.ImgStore_getImgStoreByForTableAndStoreKey("chi",chi.getRkey());
            if (arrImgPath.size()>0){
                setAdapter();
                //goto last
                ImgStore imgstore=new ImgStore();
                imgstore=arrImgPath.get(arrImgPath.size()-1);
                realPhotoPath=imgstore.getImgpath();
                ImgStoreWrokingID=imgstore.getId();
                if (realPhotoPath.length()>4){
                    if (!realPhotoPath.substring(0,4).equals("http")){
                        grabImage(imgChiHoaDon,realPhotoPath);
                    }else{
                        try {
                            loadImage(realPhotoPath);
                            //imageLoader.displayImage(realPhotoPath, imgChiHoaDon);
                        } catch (Exception e){
                            Log.e(TAG, "initData: "+e.toString() );
                        }

                    }
                }
            }
            //khjong cho drop donwn bậy bạ
            setEditMod(false);
            aedtChiChuyenBien.requestFocus();
        }
    }

    private void addEvents() {
        //edtChiGiaTri.addTextChangedListener(new NumberTextWatcher(edtChiGiaTri));
        //edtChiDaTra.addTextChangedListener(new NumberTextWatcher(edtChiDaTra));
        edtChiGiaTri.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    edtChiGiaTri.setText(formatNumber(edtChiGiaTri.getText()+""));
                }
            }
        });
        edtChiDaTra.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    edtChiDaTra.setText(formatNumber(edtChiDaTra.getText()+""));
                }
            }
        });
        tvChiDatra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_mode=="VIEW"){return;}
                edtChiDaTra.setText(edtChiGiaTri.getText() + "");
            }
        });

        spnNhomLydo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tmp=spnNhomLydo.getAdapter().getItem(position).toString();
                if (edit_mode=="VIEW" || position==spnNhomLydo.getAdapter().getCount()-1){return;}

                if (edtChiLyDo.getText()+""==""){
                    if (StringUtils.containsIgnoreCase(tmp,"khác")){
                        edtChiLyDo.setText("Khác - ");
                    }else{
                        edtChiLyDo.setText(tmp + " - ");
                    }
                }else{
                    for (int i=0;i<spnNhomLydo.getAdapter().getCount();i++){
                        String str1=spnNhomLydo.getAdapter().getItem(i).toString();
                        String str2=StringUtils.replaceAll(edtChiLyDo.getText()+""," - ","");
                        if (StringUtils.containsIgnoreCase(str1,str2)){
                            if (StringUtils.containsIgnoreCase(tmp,"khác")){
                                edtChiLyDo.setText("Khác - ");
                            }else{
                                edtChiLyDo.setText(tmp + " - ");
                            }
                            break;
                        }
                    }
                }
                edtChiLyDo.requestFocus();
                edtChiLyDo.setSelection(edtChiLyDo.getText().length());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imgChiHoaDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgChiHoaDon.getDrawable() == null && edit_mode != "VIEW") {
                    dispatchTakePictureIntent(false);
                } else {
                    if (realPhotoPath.length()>4 ) {
                        if (realPhotoPath.substring(0,4).equals("http")){
                            Intent intent = new Intent(ChiActivity.this, FullscreenImageActivity.class);
                            intent.putExtra("realPhotoPath", realPhotoPath);
                            startActivity(intent);
                        }else{
                            viewHDImage(realPhotoPath);

                        }
                    }
                }
            }
        });

        aedtChiChuyenBien.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    // on focus off
                    String str = aedtChiChuyenBien.getText().toString();

                    try {
                        ListAdapter listAdapter = aedtChiChuyenBien.getAdapter();
                        for(int i = 0; i < listAdapter.getCount(); i++) {
                            String temp = listAdapter.getItem(i).toString();
                            if(str.compareTo(temp) == 0) {
                                return;
                            }
                        }
                        if (!crudLocaldb.ChuyenBien_isChuyenBienDaChia(str)){
                            aedtChiChuyenBien.setText("");
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }


                }
            }
        });

        aedtChiDoiTac.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    // on focus off
                    String str = aedtChiDoiTac.getText().toString();

                    ListAdapter listAdapter = aedtChiDoiTac.getAdapter();
                    for(int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if(str.compareToIgnoreCase(temp) == 0) {
                            aedtChiDoiTac.setText(temp);
                            return;
                        }
                    }

                    //aedtChiDoiTac.setText("");
                    LinearLayout layout = new LinearLayout(ChiActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ChiActivity.this, R.style.AppTheme_MaterialDialogTheme);
                    final EditText edtSodt = new EditText(ChiActivity.this);
                    edtSodt.setHint("SDT, MST");
                    final EditText edtDiaChi = new EditText(ChiActivity.this);
                    edtDiaChi.setHint("Địa chỉ");
                    edtSodt.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                    edtDiaChi.setInputType(InputType.TYPE_CLASS_TEXT);
                    layout.addView(edtSodt);
                    layout.addView(edtDiaChi);
                    dialogBuilder.setTitle("Thêm NCC mới");
                    dialogBuilder.setMessage("- NCC: " +str+ "\n"+
                            "- Chưa có trong Danh Mục Nhà Cung Cấp\n\n"+
                            "Thêm mới bằng cách nhập vào SDT hay MST và Địa chỉ của NCC này rồi chọn NEW");
                    dialogBuilder.setView(layout);
                    dialogBuilder.setCancelable(false);
                    dialogBuilder.setPositiveButton("New", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String sodt = edtSodt.getText().toString();
                            String diachi=edtDiaChi.getText().toString();
                            DoiTac doitac=new DoiTac();
                            doitac.setServerkey(0);
                            doitac.setRkey(longGet(getCurrentTimeMiliS()));
                            doitac.setTendoitac(str);
                            doitac.setSodienthoai(sodt);
                            doitac.setDiachi(diachi);
                            doitac.setUpdatetime(getCurrentTimeMiliS());
                            long a=crudLocaldb.DoiTac_addDoiTac(doitac);
                            if (a!=-1){
                                Toast.makeText(ChiActivity.this, "Đã thêm mới NCC: "+ str +" vào Danh Mục NCC", Toast.LENGTH_SHORT).show();
                                listDoiTac = crudLocaldb.DoiTac_listDoiTac();
                                ArrayAdapter<String> adapterDT = new ArrayAdapter<String>(ChiActivity.this, android.R.layout.simple_list_item_1, listDoiTac);
                                aedtChiDoiTac.setAdapter(adapterDT);
                                aedtChiDoiTac.setText(str);
                            }
                        }
                    });
                    dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            aedtChiDoiTac.setText("");
                        }
                    });
                    dialogBuilder.show();
                    return;

                }
            }
        });

        edtChiNgayPs.setOnClickListener(new MyEvents());
        btnChiNewImg.setOnClickListener(new MyEvents());
        btnChiDetail.setOnClickListener(new MyEvents());
        tvChiHoaDon.setOnClickListener(new MyEvents());
        edtChiNgayPs.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edtChiNgayPs.setText("");
                return false;
            }
        });

        edtChiDaTra.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (edit_mode!="VIEW"){
                        UpdateSave();
                    }
                }
                return false;
            }
        });

        spnImgPath.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ImgStore imgstore=customImgPathAdapterData.get(position);
                if (imgstore.getImgpath().length()>4){
                    realPhotoPath=imgstore.getImgpath();
                    ImgStoreWrokingID=imgstore.getId();
                    if (!realPhotoPath.substring(0,4).equals("http")){
                        File file=new File(realPhotoPath);
                        if (file.exists()){
                            grabImage(imgChiHoaDon,realPhotoPath);
                        }
                    }else{
                        try {
                            loadImage(realPhotoPath);
                            //imageLoader.displayImage(realPhotoPath, imgChiHoaDon);
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

    private void addControls() {
        imgChiHoaDon = findViewById(R.id.img_ChiHoaDon);
        btnChiDetail = findViewById(R.id.btn_ChiDetail);
        btnChiNewImg = findViewById(R.id.btn_ChiTakePhoTo);
        aedtChiChuyenBien = (AutoCompleteTextView) findViewById(R.id.aedt_ChiChuyenBien);
        aedtChiDoiTac = (AutoCompleteTextView) findViewById(R.id.aedt_ChiDoiTac);
        edtChiLyDo = findViewById(R.id.edt_ChiLyDo);
        spnNhomLydo=findViewById(R.id.spn_ChiNhomLyDo);
        edtChiGiaTri = findViewById(R.id.edt_ChiGiaTri);
        edtChiNgayPs = findViewById(R.id.edt_ChiNgayPS);
        tvIdChi = findViewById(R.id.tv_IdChi);
        tvRkeyChi = findViewById(R.id.tv_RkeyChi);
        tvServerKey=findViewById(R.id.tv_ChiServerKey);
        tvRkeyTicket=findViewById(R.id.tv_RkeyChiTicket);
        tvUsername=findViewById(R.id.tv_ChiUsername);
        edtChiDaTra = findViewById(R.id.edt_ChiDaTra);
        tvChiGiaTri=findViewById(R.id.tv_ChiGiaTri);
        tvChiDatra=findViewById(R.id.tv_ChiDaTra);
        tvChiHoaDon=findViewById(R.id.tv_ChiHoaDon);
        spnImgPath=findViewById(R.id.spn_ChiImagePath);
        // dinh dang gajch chan cho text view
        tvChiHoaDon.setPaintFlags(tvChiHoaDon.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        edtChiLyDo.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        edtChiLyDo.setRawInputType(InputType.TYPE_CLASS_TEXT);


    }

    private class MyEvents implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = null;
            Boolean isImageFitToScreen = false;
            switch (v.getId()) {
                case R.id.edt_ChiNgayPS:
                    showDatePickerDialog(edtChiNgayPs);
                    break;
                case R.id.btn_ChiDetail:
                    if (aedtChiDoiTac.getText()+""=="" || aedtChiChuyenBien.getText()+""=="" || edtChiNgayPs.getText()+""==""){
                        Toast.makeText(ChiActivity.this, "Cần nhập đủ dữ liệu.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    long idDoiTac=crudLocaldb.DoiTac_getRkeyDoiTac(aedtChiDoiTac.getText()+"");
                    if (idDoiTac==0){
                        aedtChiDoiTac.setText("0ther");
                        idDoiTac=crudLocaldb.DoiTac_getRkeyDoiTac(aedtChiDoiTac.getText()+"");
                    }
                    if (intGet(tvIdChi.getText()+"")==0 && idDoiTac!=0){
                        edit_mode = "NEW";
                        UpdateSave();
                    }
                    long idChuyenBien = crudLocaldb.ChuyenBien_getRkeyChuyenBien(getEditText(ChiActivity.this, aedtChiChuyenBien));
                    intent = new Intent(ChiActivity.this, ChiDetailActivity.class);
                    intent.putExtra("rkeyChi", longGet(tvRkeyChi.getText()+""));
                    intent.putExtra("rkeyDoiTac",idDoiTac);
                    intent.putExtra("userName",intentUserName);
                    intent.putExtra("rkeyChuyenBien",idChuyenBien);
                    intent.putExtra("rkeyTicket", RKEY_TICKET);
                    if (longGet(edtChiDaTra.getText()+"")!=0){
                        intent.putExtra("hideAllMenu",true);
                    }
                    intent.putExtra("daChia",daChia);
                    startActivityForResult(intent,REQUEST_CHI_DETAIL);
                    break;
                case R.id.tv_ChiHoaDon:
                    if (edit_mode != "VIEW"){
                        intent=new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent,REQUEST_CHOOSE_PHOTO);
                    }
                    break;
                case R.id.btn_ChiTakePhoTo:
                    if (edit_mode != "VIEW") {
                        dispatchTakePictureIntent(false);
                    }
                    break;
            }

        }
    }

    public void showDatePickerDialog(final EditText edtViewDate) {
        hideSoftKeyboard(ChiActivity.this);
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
        if (!utils.isDate(utils.getEditText(ChiActivity.this,edtViewDate))){
            //Dinh dang lai kieu ngay hien tai
            cal=Calendar.getInstance();
            SimpleDateFormat dft=new SimpleDateFormat("dd/MM/yyyy");
            //gan ngay thang hien tai da dc dinh dang cho s
            s=dft.format(cal.getTime());
        }else {
            s=utils.getEditText(ChiActivity.this,edtViewDate);
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


    private void CheckAddNew() {
        tvIdChi.setText("");
        tvRkeyChi.setText("");
        tvServerKey.setText("");
        aedtChiChuyenBien.setText("");
        aedtChiDoiTac.setText("");
        tvRkeyTicket.setText(rkeyTicket+"");
        tvUsername.setText(intentUserName);
        tvUsername.setText("");
        edtChiLyDo.setText("");
        edtChiNgayPs.setText("");
        edtChiGiaTri.setText("");
        edtChiDaTra.setText("");
        imgChiHoaDon.setImageResource(0);
        aedtChiChuyenBien.requestFocus();
        arrImgPath=crudLocaldb.ImgStore_getImgStoreByForTableAndStoreKey("chi",longGet(tvRkeyChi.getText()+""));
        updateSpnImgPath();
        edit_mode = "NEW";
        setEditMod(true);

    }

    private void CheckEdit() {
        edit_mode = "EDIT";
        setEditMod(true);
        if (String.valueOf(aedtChiChuyenBien.getText()+"").substring(0,1).equals("0")){
            aedtChiChuyenBien.setFocusable(false);
        }else{
            aedtChiChuyenBien.setFocusable(true);
            aedtChiChuyenBien.setFocusableInTouchMode(true);
        }

    }

    private void CheckDelete() {
        if (Integer.valueOf(tvServerKey.getText() + "")!=0){
            WantDeleteFromServer wdfs=new WantDeleteFromServer();
            wdfs.setmServerkey(Integer.valueOf(tvServerKey.getText() + ""));
            wdfs.setmTablename("chi");
            crudLocaldb.WDFS_addWDFS(wdfs);
        }
        arrImgPath=crudLocaldb.ImgStore_getImgStoreByForTableAndStoreKey("chi",longGet(tvRkeyChi.getText()+""));
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
        crudLocaldb.ImgStore_deleteImgStoreByForTableAndStoreKey("chi",longGet(tvRkeyChi.getText()+""));
        updateSpnImgPath();

        rkeyDoiTac = crudLocaldb.DoiTac_getRkeyDoiTac(aedtChiDoiTac.getText() + "");
        rkeyChuyenBien=crudLocaldb.ChuyenBien_getRkeyChuyenBien(aedtChiChuyenBien.getText()+"");
        crudLocaldb.Chi_deleteChi(longGet(tvRkeyChi.getText() + ""));

        //cap nhat no doi tac
        CapNhatCongNo();
        //cap nhat so hui cho tau
        CapNhatSoHuiChoTau();
        //cap nhat Ticket
        CapNhatTicket();

        this.needRefresh=true;
        //refesh screen
        tvIdChi.setText("");
        tvRkeyChi.setText("");
        tvServerKey.setText("");
        aedtChiChuyenBien.setText("");
        aedtChiDoiTac.setText("");
        tvUsername.setText("");
        tvRkeyTicket.setText(rkeyTicket+"");
        tvUsername.setText(intentUserName);
        tvUsername.setText("");
        edtChiLyDo.setText("");
        edtChiNgayPs.setText("");
        edtChiGiaTri.setText("");
        edtChiDaTra.setText("");
        imgChiHoaDon.setImageResource(0);
        aedtChiChuyenBien.requestFocus();
    }

    private void UpdateSave() {
        if (daChia){
            Toast.makeText(this, "Dự án củ, dữ liệu chỉ được view", Toast.LENGTH_SHORT).show();
            return;
        }
        Chi chi = new Chi();
        rkeyDoiTac = crudLocaldb.DoiTac_getRkeyDoiTac(getEditText(this, aedtChiDoiTac));
        rkeyChuyenBien = crudLocaldb.ChuyenBien_getRkeyChuyenBien(getEditText(this, aedtChiChuyenBien));
        if (rkeyDoiTac == 0 || rkeyChuyenBien == 0) {
            Toast.makeText(ChiActivity.this, "Cần chính xác tên Dự án và Đối tác", Toast.LENGTH_SHORT).show();
            return;
        }
        String lydo = edtChiLyDo.getText() + "";
        String ngayps = utils.getEditText(this, edtChiNgayPs);
        long giatri=longGet(edtChiGiaTri.getText()+"");
        long datra=longGet(edtChiDaTra.getText()+"");

        //int datra=Integer.valueOf(ckChiDaTra.)
        if (rkeyDoiTac == 0 || rkeyChuyenBien == 0 || !utils.isDate(ngayps)) {
            Toast.makeText(getApplicationContext(), "Cần nhập đủ dữ liệu.", Toast.LENGTH_SHORT).show();
            return;
        }
        //dua sẳn du lieu  vao chi
        chi.setRkeychuyenbien(rkeyChuyenBien);
        chi.setmTenChuyenBien(getEditText(this, aedtChiChuyenBien));
        chi.setmTenDoiTac(getEditText(this, aedtChiDoiTac));
        chi.setRkeydoitac(rkeyDoiTac);
        chi.setRkeyticket(rkeyTicket);
        chi.setLydo(lydo);
        chi.setNgayps(ngayps);
        chi.setGiatri(giatri+"");
        chi.setDatra(datra+"");
        chi.setUpdatetime(getCurrentTimeMiliS());
        chi.setUsername(intentUserName);
        if (imageUriFromShare!=null){
            File newFile;
            newFile=copyNewImage(imageUriFromShare);
            //make new compress file base on temp file and delete old
            realPhotoPath=newFile.getAbsolutePath();
            //Bitmap myBitmap = BitmapFactory.decodeFile(realPhotoPath);
            Bitmap myBitmap = null;
            try {
                myBitmap = utils.XoayImage(realPhotoPath);
            } catch (FileNotFoundException e) {
                myBitmap = BitmapFactory.decodeFile(realPhotoPath);
                e.printStackTrace();
            }
            Bitmap newbmp = utils.resizeImage(myBitmap, 1280, 1280);
            byte[] bytes = utils.getByteArrayFromBitmap(newbmp, 100);
            //thang SavePhotoTask thay doi gia tri realPhotoPath tu cu thanh moi..
            new SavePhotoTask().execute(bytes);
            grabImage(imgChiHoaDon, realPhotoPath);
            if (realPhotoPath.length()>4){
                imgStore_mode="NEW";
            }
        }
        long newRkey=longGet(getCurrentTimeMiliS());
        if (imgChiHoaDon.getDrawable() != null && imgStore_mode=="NEW") {
            ImgStore imgstore=new ImgStore();
            imgstore.setServerkey(0);
            imgstore.setImgpath(realPhotoPath);
            imgstore.setNgayps(ngayps);
            imgstore.setUpdatetime(getCurrentTimeMiliS());
            imgstore.setUsername(intentUserName);
            imgstore.setFortable("chi");
            if (edit_mode == "NEW" && tvIdChi.getText() + "" == ""){
                imgstore.setStorekey(newRkey);
            }else {
                imgstore.setStorekey(longGet(tvRkeyChi.getText()+""));
            }
            long skey=imgstore.getStorekey();
            long imgAdd=crudLocaldb.ImgStore_addImgStore(imgstore);
            if (imgAdd!=-1){
                arrImgPath=crudLocaldb.ImgStore_getImgStoreByForTableAndStoreKey("chi",skey);
                updateSpnImgPath();
            }
        }
        //neu la tao moi
        if (edit_mode == "NEW" && tvIdChi.getText() + "" == "") {
            if (chi != null) {
                chi.setServerkey(0);
                chi.setRkey(newRkey);
                long i = crudLocaldb.Chi_addChi(chi);
                if (i!=-1) {
                    //cap nhat no doi tac
                    CapNhatCongNo();
                    //cap nhat so hui cho tau
                    CapNhatSoHuiChoTau();
                    //cap nhat Ticket
                    CapNhatTicket();

                    this.needRefresh=true;

                    arrChi=crudLocaldb.Chi_getAllChi();
                    gotoRec(arrChi.size()-1);
                    //setEditMod(false);
                    setAdapter();
                }
            }
        } else  if (edit_mode == "EDIT") {
            chi.setId(intGet(tvIdChi.getText()+""));
            chi.setServerkey(intGet(tvServerKey.getText()+""));
            chi.setRkey(longGet(tvRkeyChi.getText()+""));
            int result = crudLocaldb.Chi_updateChi(chi);
            if (result > 0) {
                //cap nhat no doi tac
                CapNhatCongNo();
                //cap nhat so hui cho tau
                CapNhatSoHuiChoTau();
                //cap nhat Ticket
                CapNhatTicket();
                //co the thay doi ten doi tac nen neu co thi that doi chidetail theo lun
                CapNhatChiDetail();
                this.needRefresh=true;
                // Refesh screen
                Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show();
                arrChi = crudLocaldb.Chi_getAllChi();
                int position = -1;
                for (int j = 0; j < arrChi.size(); j++) {
                    if (arrChi.get(j).getRkey() ==longGet(tvRkeyChi.getText()+"")) {
                        position = j;
                        gotoRec(position);
                        break;  // uncomment to get the first instance
                    }
                }

            }
        }
    }
    private void CapNhatTicket(){
        //sum by ticket
        String chiByChi=crudLocaldb.Chi_SumDaChiTicket(rkeyTicket);
        String chiByDebt=crudLocaldb.DebtBook_SumDebtBookTicket(rkeyTicket);
        long tongchi=longGet(chiByChi)+longGet(chiByDebt);
        //Cap nhat Ticket
        crudLocaldb.Ticket_CapNhatChi(rkeyTicket, String.valueOf(tongchi), getCurrentTimeMiliS());
//        if (!intentUserName.substring(0,5).equals("admin")){
//            String chiByChi=crudLocaldb.Chi_SumDaChiTicket(rkeyTicket);
//            String chiByDebt=crudLocaldb.DebtBook_SumDebtBookTicket(rkeyTicket);
//            long tongchi=longGet(chiByChi)+longGet(chiByDebt);
//            //Cap nhat Ticket
//            crudLocaldb.Ticket_CapNhatChi(rkeyTicket, String.valueOf(tongchi), getCurrentTimeMiliS());
//        }else{
//            ArrayList<Ticket>arrTicket=new ArrayList<>();
//            arrTicket=crudLocaldb.Ticket_getAllNotFinishedTicket();
//            if (arrTicket.size()>0){
//                for (int i=0;i<arrTicket.size();i++){
//                    String chiByChi=crudLocaldb.Chi_SumDaChiTicket(arrTicket.get(i).getRkey());
//                    String chiByDebt=crudLocaldb.DebtBook_SumDebtBookTicket(arrTicket.get(i).getRkey());
//                    long tongchi=longGet(chiByChi)+longGet(chiByDebt);
//                    //Cap nhat Ticket
//                    crudLocaldb.Ticket_CapNhatChi(arrTicket.get(i).getRkey(), String.valueOf(tongchi), getCurrentTimeMiliS());
//                }
//            }
//        }
    }
    private void CapNhatCongNo(){
        String[] tongno = new String[2];
        tongno = crudLocaldb.Chi_SumGiaTriDoiTac(rkeyDoiTac);
        crudLocaldb.DoiTac_CapNhatNo(rkeyDoiTac, tongno[0], tongno[1], getCurrentTimeMiliS());
    }
    private void CapNhatSoHuiChoTau(){
        ArrayList<ChiDetail>arrChiDetail=new ArrayList<>();
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

    private void CapNhatChiDetail(){
        if (!checkChiDetail()){return;}
        ArrayList<ChiDetail>arrChiDetail=new ArrayList<>();
        arrChiDetail=crudLocaldb.ChiDetail_getAllChiDetail();
        for (int i=0;i<arrChiDetail.size();i++){
            ChiDetail chidetail=new ChiDetail();
            chidetail=arrChiDetail.get(i);
            if (chidetail.getRkeychi()==longGet(tvRkeyChi.getText()+"")){
                if (chidetail.getTendoitac()==aedtChiDoiTac.getText()+""){
                    break;
                }else{
                    chidetail.setTendoitac(aedtChiDoiTac.getText()+"");
                    chidetail.setUpdatetime(getCurrentTimeMiliS());
                    crudLocaldb.ChiDetail_updateChiDetail(chidetail);
                    syncTableChiDetail=true;
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
                    new SavePhotoTask().execute(bytes);
                    grabImage(imgChiHoaDon,realPhotoPath);
                    if (requestCode==REQUEST_TAKE_PHOTO){
                        //save record
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
                            arrImgPath=crudLocaldb.ImgStore_getImgStoreByForTableAndStoreKey("chi",longGet(tvRkeyChi.getText()+""));
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
                //Bitmap myBitmap = BitmapFactory.decodeFile(realPhotoPath);
                Bitmap myBitmap = null;
                try {
                    myBitmap = utils.XoayImage(realPhotoPath);
                } catch (Exception e) {
                    myBitmap = BitmapFactory.decodeFile(realPhotoPath);
                    e.printStackTrace();
                }
                Bitmap newbmp = utils.resizeImage(myBitmap, 1280, 1280);
                byte[] bytes = utils.getByteArrayFromBitmap(newbmp, 100);
                //thang SavePhotoTask thay doi gia tri realPhotoPath tu cu thanh moi..
                new SavePhotoTask().execute(bytes);
                grabImage(imgChiHoaDon, realPhotoPath);
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
                        arrImgPath=crudLocaldb.ImgStore_getImgStoreByForTableAndStoreKey("chi",longGet(tvRkeyChi.getText()+""));
                        updateSpnImgPath();
                    }
                }

            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CHI_DETAIL ||
                resultCode == Activity.RESULT_OK && requestCode == REQUEST_CHI_DEBT  ) {
            boolean needRefresh = data.getBooleanExtra("needRefresh",true);
            // Refresh ListView
            if(needRefresh) {
                this.needRefresh=true;
                arrChi = crudLocaldb.Chi_getAllChi();
                int position = -1;
                for (int j = 0; j < arrChi.size(); j++) {
                    if (arrChi.get(j).getRkey() ==longGet(tvRkeyChi.getText()+"")) {
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

    private void setEditMod(boolean chohaykhong) {
        if (chohaykhong == true) {
            //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            mShowMenuSave = 1; // setting state
            btnChiDetail.setEnabled(true);
            btnChiNewImg.setEnabled(true);
            aedtChiChuyenBien.setThreshold(1);
            aedtChiDoiTac.setThreshold(1);
            if (longGet(edtChiGiaTri.getText()+"")!=0){
                if (checkChiDetail()){
                    if (IS_ADMIN){
                        new AlertDialog.Builder(this)
                                .setTitle("Cẩn trọng thao tác")
                                .setMessage("Record này có chi tiết liên quan, cẩn trọng khi edit." +
                                        " Rất cần thiết bảo toàn tính nhất quán của dữ liệu. \n\n" +
                                        "Có muốn tiếp tục edit?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        edtChiGiaTri.setFocusable(true);
                                        edtChiGiaTri.setFocusableInTouchMode(true);
                                        btnChiDetail.setEnabled(false);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        edtChiGiaTri.setFocusable(false);
                                        btnChiDetail.setEnabled(true);
                                    }
                                })
                                .show();
                    }else{
                        edtChiGiaTri.setFocusable(false);
                        btnChiDetail.setEnabled(true);
                    }
                }else{
                    edtChiGiaTri.setFocusable(true);
                    edtChiGiaTri.setFocusableInTouchMode(true);
                    btnChiDetail.setEnabled(false);
                }
            }else{
                btnChiDetail.setEnabled(true);
                edtChiGiaTri.setFocusable(true);
                edtChiGiaTri.setFocusableInTouchMode(true);
            }
        } else {
            mShowMenuSave = 0; // setting state
            //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            btnChiDetail.setEnabled(false);
            btnChiNewImg.setEnabled(false);
            aedtChiChuyenBien.setThreshold(1000);
            aedtChiDoiTac.setThreshold(1000);
            edit_mode = "VIEW";
            if (checkChiDetail()){
                btnChiDetail.setEnabled(true);
            }else{
                btnChiDetail.setEnabled(false);
            }
        }
        invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again
    }


    private void dispatchTakePictureIntent(boolean ByConTextMenu) {
        if (!isDate(String.valueOf(edtChiNgayPs.getText()))){
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
        String tenDoiTac= utils.loaiboDauVN(utils.getEditText(this,aedtChiDoiTac).replace(" ",""));
        String[] fdir=String.valueOf(edtChiNgayPs.getText()).split("/");
        String dirpath=fdir[2]+fdir[1]; //201704

        // String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        File dir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/" + dirpath);
        String imageFileName =tenDoiTac+ "-"+ getCurrentTimeMiliS();

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
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this,"No any viewer install on this device", Toast.LENGTH_SHORT).show();
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
                        Uri outputUri= FileProvider.getUriForFile(ChiActivity.this, ChiActivity.this.getApplicationContext().getPackageName() + ".provider", outFile);
                        i.putExtra(Intent.EXTRA_STREAM, outputUri);
                        i.setDataAndType(outputUri, "image/*");
                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        try {
                            startActivity(Intent.createChooser(i,"Share via..."));
                        }
                        catch (ActivityNotFoundException e) {
                            Toast.makeText(ChiActivity.this,"No any viewer install on this device", Toast.LENGTH_SHORT).show();
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
        data.putExtra("needRefresh", this.needRefresh);
        if (aedtChiDoiTac.getText()+""!=""){
            rkeyDoiTac=crudLocaldb.DoiTac_getRkeyDoiTac(aedtChiDoiTac.getText()+"");
        }

        data.putExtra("rkeyDoiTac",rkeyDoiTac);
        data.putExtra("rkeyTicket",rkeyTicket);

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
