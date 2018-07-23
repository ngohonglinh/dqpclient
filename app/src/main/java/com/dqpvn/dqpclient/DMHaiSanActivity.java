package com.dqpvn.dqpclient;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.customadapters.CustomAdapterDMHaiSan;
import com.dqpvn.dqpclient.models.DMHaiSan;
import com.dqpvn.dqpclient.models.WantDeleteFromServer;
import com.dqpvn.dqpclient.utils.MyContextWrapper;
import com.dqpvn.dqpclient.utils.NumberTextWatcher;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.function.Predicate;

import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_NAME;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.WHO_START;
import static com.dqpvn.dqpclient.utils.utils.getCurrentTimeMiliS;
import static com.dqpvn.dqpclient.utils.utils.hideSoftKeyboard;
import static com.dqpvn.dqpclient.utils.utils.intGet;
import static com.dqpvn.dqpclient.utils.utils.isBad;
import static com.dqpvn.dqpclient.utils.utils.longGet;

public class DMHaiSanActivity extends AppCompatActivity {
    final private String TAG= getClass().getSimpleName();
    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;
    private int lvPossition=-1;

    //base

    private int mShowMenuSave=0;
    private TextView tvId,tvRkey, tvServerKey;
    private EditText edtDonGia;
    private AutoCompleteTextView aedtTenHS, aedtPhanLoai;
    private ListView lvHaiSan;
    private CheckBox chkAuto;
    //database
    private crudLocal crudLocaldb=crudLocal.getInstance(this);
    private ArrayList<DMHaiSan> arrDMHaiSan=new ArrayList<>();
    private ArrayList<DMHaiSan> customadapterData=new ArrayList<>();
    private ArrayList<String> DMHaiSan_listHaiSan=new ArrayList<>();
    private ArrayList<String> DMHaiSan_listHaiSanAdapterData=new ArrayList<>();
    //Khai báo ArrayAdapter cho ListView
    private CustomAdapterDMHaiSan customAdapter;
    private ArrayAdapter<String> adapterHS;
    //Theo gioi cho phép Nhap lieu
    private String edit_mode="VIEW";

    //lam viec voi menu
    @Override
    public boolean onCreateOptionsMenu(Menu manu) {
        getMenuInflater().inflate(R.menu.record_menu, manu);
        MenuItem mSave = manu.findItem(R.id.save);
        if (WHO_START=="thuyenTruong"){
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

        switch(itemId)  {
            //ten cua cac id khi thiet ke cac resource menu
            case R.id.id_new :
                //wrtite ơn logic
                CheckAddNew();
                return true;
            case R.id.id_edit :
                if (arrDMHaiSan.size()==0){
                    return true;
                }
                edit_mode="EDIT";
                setEditMod(true);
                return true;
            case R.id.id_delete :
                //wrtite ơn logic
                if (arrDMHaiSan.size()==0){
                    return true;
                }
                //lay ra index hien tai cua listview
                if (lvPossition==-1){
                    Toast.makeText(this, "Chưa chọn đúng dữ liệu cần xóa", Toast.LENGTH_SHORT).show();
                }else {
                    final DMHaiSan dmhs =arrDMHaiSan.get(lvPossition);
                    // Hỏi trước khi xóa.
                    new AlertDialog.Builder(this)
                            .setTitle("DQP Client")
                            .setMessage(dmhs.getTenhs()+ "\n\n" + "Có chắc xóa?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    CheckDelete(dmhs);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
                return true;
            case R.id.save:
                UpdateDmHaiSan();
                hideSoftKeyboard(DMHaiSanActivity.this);
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
            lvPossition=lvHaiSan.pointToPosition( (int) x, (int) y );
            if (lvPossition<0){
                return true;
            }
            DMHaiSan dmhs=new DMHaiSan();
            dmhs=arrDMHaiSan.get(lvPossition);
            tvId.setText(dmhs.getId()+"");
            tvRkey.setText(dmhs.getRkey()+"");
            tvServerKey.setText(dmhs.getServerkey()+"");
            aedtTenHS.setText(dmhs.getTenhs()+"");
            aedtPhanLoai.setText(dmhs.getPhanloai()+"");
            edtDonGia.setText(dmhs.getDongia()+"");
            setEditMod(false);
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
            lvPossition=lvHaiSan.pointToPosition( (int) e1.getX(), (int) e1.getY() );
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (lvPossition<0){
                    return true;
                }
                Log.d(TAG, "Right to Left swipe performed");
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
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmhai_san);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);

        addControls();
        addEvents();

        arrDMHaiSan=  crudLocaldb.DMHaiSan_getAllDMHaiSan();

        customadapterData.addAll(arrDMHaiSan);
        DMHaiSan_listHaiSan=crudLocaldb.DMHaiSan_listHaiSan();
        DMHaiSan_listHaiSanAdapterData.addAll(DMHaiSan_listHaiSan);
        setAdapter();
        initData();

    }
    private void addControls(){
        tvId=findViewById(R.id.tv_DMHSid);
        tvServerKey=findViewById(R.id.tv_DMHSserverkey);
        tvRkey=findViewById(R.id.tv_DMHSrkey);
        aedtTenHS=findViewById(R.id.aedt_DMHStenhs);
        aedtPhanLoai=findViewById(R.id.aedt_DMHSphanloai);
        edtDonGia=findViewById(R.id.edt_DMHSdongia);
        lvHaiSan=findViewById(R.id.lv_DMHS);
        chkAuto=findViewById(R.id.chk_DMHSautotype);
    }
    private void addEvents(){

        edtDonGia.addTextChangedListener(new NumberTextWatcher(edtDonGia));
        chkAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (chkAuto.isChecked()){
                    aedtPhanLoai.setFocusableInTouchMode(false);
                    aedtPhanLoai.setFocusable(false);
                }else{
                    aedtPhanLoai.setFocusableInTouchMode(true);
                    aedtPhanLoai.setFocusable(true);
                }
            }
        });

        aedtTenHS.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    // on focus off
                    String str = aedtTenHS.getText().toString();

                    ListAdapter listAdapter = aedtTenHS.getAdapter();
                    for(int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if(str.compareToIgnoreCase(temp) == 0) {
                            if (longGet(tvRkey.getText()+"")==0){
                                aedtTenHS.setText(temp);
                                DMHaiSan dmhs=new DMHaiSan();
                                dmhs=crudLocaldb.DMHaiSan_getDMHaiSanByTen(temp);
                                tvId.setText(dmhs.getId()+"");
                                tvRkey.setText(dmhs.getRkey()+"");
                                tvServerKey.setText(dmhs.getServerkey()+"");
                                aedtTenHS.setText(dmhs.getTenhs()+"");
                                aedtPhanLoai.setText(dmhs.getPhanloai()+"");
                                edtDonGia.setText(dmhs.getDongia()+"");
                                setEditMod(false);
                            }
                        }
                    }

                }
            }
        });
        edtDonGia.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (edit_mode!="VIEW"){
                        UpdateDmHaiSan();
                        CheckAddNew();
                        aedtTenHS.requestFocus();
                    }
                }
                return false;
            }
        });


        lvHaiSan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
            }
        });

    }

    private void CheckAddNew(){
        edit_mode="NEW";
        setEditMod(true);
        tvId.setText("");
        tvRkey.setText("");
        tvServerKey.setText("");
        aedtTenHS.setText("");
        aedtPhanLoai.setText("");
        edtDonGia.setText("");
        aedtTenHS.requestFocus();

    }

    private void CheckDelete(DMHaiSan dt) {
        if (dt.getServerkey()!=0){
            WantDeleteFromServer wdfs=new WantDeleteFromServer();
            wdfs.setmServerkey(dt.getServerkey());
            wdfs.setmTablename("dmhaisan");
            crudLocaldb.WDFS_addWDFS(wdfs);
        }
        crudLocaldb.DMHaiSan_deleteDMHaiSan(dt.getRkey());
        //remove from array list
        Predicate<DMHaiSan> personPredicate = p-> p.getId() == dt.getId();
        arrDMHaiSan.removeIf(personPredicate);
        //refesh screen
        aedtTenHS.setText("");
        aedtPhanLoai.setText("");
        edtDonGia.setText("");
        aedtTenHS.requestFocus();
        ////doSync("d");
        // Refresh ListView.
        updateListDmHaiSan();
        //this.customAdapter.notifyDataSetChanged();
    }
    private  void initData(){
        //   try {
        if (arrDMHaiSan.size()>=1) { //phong truong hop null k co record nao
            //lay ra index hien tai cua listview
            DMHaiSan dmhs =arrDMHaiSan.get(arrDMHaiSan.size()-1);
            ////xg record cuoi cungcua lv
            lvHaiSan.setSelection(arrDMHaiSan.size()-1);
            tvId.setText(dmhs.getId()+"");
            tvRkey.setText(dmhs.getRkey()+"");
            tvServerKey.setText(dmhs.getServerkey()+"");
            aedtTenHS.setText(dmhs.getTenhs()+"");
            aedtPhanLoai.setText(dmhs.getPhanloai()+"");
            edtDonGia.setText(dmhs.getDongia()+"");
            setEditMod(false);
        }
    }

    private  void setEditMod(boolean chohaykhong){
        if (chohaykhong==true){
            mShowMenuSave=1;
            aedtTenHS.setThreshold(1);
            aedtPhanLoai.setThreshold(1);
        }else {
            mShowMenuSave=0;
            aedtTenHS.setThreshold(1000);
            aedtPhanLoai.setThreshold(1000);
            edit_mode="VIEW";
        }
        invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again
    }

    private void UpdateDmHaiSan(){
        if(isBad(aedtTenHS.getText()+"")) {
            Toast.makeText(getApplicationContext(), "Cần nhập vào Tên sản phẩm ", Toast.LENGTH_SHORT).show();
            return;
        }
        DMHaiSan dmhs=new DMHaiSan();
        dmhs.setTenhs(aedtTenHS.getText()+"");
        if (chkAuto.isChecked()){
            dmhs.setPhanloai("Cá chợ");
        }else{
            dmhs.setPhanloai(aedtPhanLoai.getText()+"");
        }
        dmhs.setDongia(longGet(edtDonGia.getText()+"")+"");
        dmhs.setUpdatetime(getCurrentTimeMiliS());
        dmhs.setUsername(LOGIN_NAME);
        if (edit_mode=="NEW" && longGet(tvRkey.getText()+"")==0) {
            //chuyenbien.set;);= new ChuyenBien(tenchuyenbien,tentau,ngaykhoihanh);
            dmhs.setServerkey(0);
            dmhs.setRkey(longGet(getCurrentTimeMiliS()));
            if (dmhs != null) {
                long i = crudLocaldb.DMHaiSan_addDMHaiSan(dmhs);
                if (i !=-1) {
                    ////doSync("a");
                    updateListDmHaiSan();
                    setEditMod(false);
                }
            }
            //cap nhat lai danh sach chuyen bien vi da co them record moi vao
            //}
        }else{
            if (edit_mode=="EDIT") {
                ArrayList<DMHaiSan>arrDMHS=new ArrayList<>();
                dmhs.setId(intGet(tvId.getText()+""));
                dmhs.setRkey(longGet(String.valueOf(tvRkey.getText())));
                dmhs.setServerkey(intGet(String.valueOf(tvServerKey.getText())));
                int result = crudLocaldb.DMHaiSan_updateDMHaiSan(dmhs);
                if (result > 0) {
                    updateListDmHaiSan();
                    setEditMod(false);
                    ////doSync("u");
                }
            }
        }
    };

    private void setAdapter() {
        if (customAdapter == null) {
            // gan data source cho adapter
            customAdapter = new CustomAdapterDMHaiSan(this, R.layout.customlist_dmhaisan,customadapterData);
            //gan adapter cho spinner
            lvHaiSan.setAdapter(customAdapter);
            // cho autocopletwe
            adapterHS = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, DMHaiSan_listHaiSan);
            aedtTenHS.setAdapter(adapterHS);

        }else{
            updateListDmHaiSan();
        }
        lvHaiSan.setSelection(customAdapter.getCount()-1);

        String[] listPhanLoai = getResources().getStringArray(R.array.lydo_thu_array);
        ArrayAdapter<String> adapterLD = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listPhanLoai);
        aedtPhanLoai.setAdapter(adapterLD);
    }
    //gett all to list
    public void updateListDmHaiSan(){
        customadapterData.clear();
        DMHaiSan_listHaiSan.clear();
        arrDMHaiSan=crudLocaldb.DMHaiSan_getAllDMHaiSan();
        DMHaiSan_listHaiSan=crudLocaldb.DMHaiSan_listHaiSan();
        //thawng autocomplete nay buoc phai set adapter lai
        adapterHS = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, DMHaiSan_listHaiSan);
        aedtTenHS.setAdapter(adapterHS);
        //DMHaiSan_listHaiSanAdapterData.addAll(DMHaiSan_listHaiSan);
        //adapterHS.notifyDataSetChanged();
        customadapterData.addAll(arrDMHaiSan);
        customAdapter.notifyDataSetChanged();
        lvHaiSan.setSelection(customAdapter.getCount()-1);
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
