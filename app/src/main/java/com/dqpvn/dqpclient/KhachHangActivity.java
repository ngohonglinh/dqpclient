package com.dqpvn.dqpclient;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.customadapters.CustomAdapterKhachHang;
import com.dqpvn.dqpclient.models.Thu;
import com.dqpvn.dqpclient.models.KhachHang;
import com.dqpvn.dqpclient.models.WantDeleteFromServer;
import com.dqpvn.dqpclient.utils.MyContextWrapper;

import java.util.ArrayList;
import java.util.function.Predicate;

import static com.dqpvn.dqpclient.utils.utils.getCurrentTimeMiliS;
import static com.dqpvn.dqpclient.utils.utils.hideSoftKeyboard;
import static com.dqpvn.dqpclient.utils.utils.intGet;
import static com.dqpvn.dqpclient.utils.utils.longGet;

public class KhachHangActivity extends AppCompatActivity {

    final private String TAG= getClass().getSimpleName();
    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;
    private int lvPossition=-1;
    //base


    private final int REQUEST_START_NOCTY=111;
    private int mShowMenuSave=0;
    private TextView tvIdKhachHang, tvRkeyKhachHang, tvServerKey;
    private EditText edtDiaChiKhachHang, edtDienThoaiKhachHang;
    private AutoCompleteTextView aedtTenKhach;
    private ListView lvKhachHang;
    //database
    private crudLocal crudLocaldb=crudLocal.getInstance(this);
    //Khai báo Datasource lưu trữ danh sách doi tac
    private ArrayList<KhachHang> arrKhachHang=new ArrayList<>();
    private ArrayList<KhachHang>customadapterData=new ArrayList<>();
    private String [] KhachHang_listKhachHang;
    //Khai báo ArrayAdapter cho ListView
    private CustomAdapterKhachHang customAdapter;
    private ArrayAdapter<String> adapterKH;
    //Theo gioi cho phép Nhap lieu
    private String edit_mode="VIEW", intentUserName;

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
                //wrtite ơn logic
                CheckAddNew();
                return true;
            case R.id.id_edit :
                if (arrKhachHang.size()==0){
                    return true;
                }
                //wrtite ơn logic
                CheckEdit();
                return true;
            case R.id.id_delete :
                //wrtite ơn logic
                if (arrKhachHang.size()==0){
                    return true;
                }

                if (checkThu()){
                    Toast.makeText(this, "Không thể xóa vì đang có chi tiết liên quan", Toast.LENGTH_SHORT).show();
                    return true;
                }
                //lay ra index hien tai cua listview
                if (lvPossition==-1){
                    Toast.makeText(this, "Chưa chọn đúng dữ liệu cần xóa", Toast.LENGTH_SHORT).show();
                }else {
                    final KhachHang khachhang =arrKhachHang.get(lvPossition);
                    // Hỏi trước khi xóa.
                    new AlertDialog.Builder(this)
                            .setTitle("DQP Client")
                            .setMessage(khachhang.getTenkhach()+ "\n\n" + "Có chắc xóa?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    CheckDelete(khachhang);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
                return true;
            case R.id.save:
                UpdateKhachHang();
                hideSoftKeyboard(KhachHangActivity.this);
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

    private boolean checkThu(){
        ArrayList<Thu>arrThu=new ArrayList<>();
        long idKhachHang=0;
        idKhachHang=longGet(tvRkeyKhachHang.getText()+"");
        arrThu=crudLocaldb.Thu_getThuByKhachHang(idKhachHang);
        if (arrThu.size()>0) {
            return true;

        }else{
            return false;

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
            lvPossition=lvKhachHang.pointToPosition( (int) x, (int) y );
            if (lvPossition<0){
                return true;
            }
            KhachHang khachHang = new KhachHang();
            khachHang=arrKhachHang.get(lvPossition);
            tvIdKhachHang.setText(khachHang.getId()+"");
            tvRkeyKhachHang.setText(String.valueOf(khachHang.getRkey()));
            tvServerKey.setText(String.valueOf(khachHang.getServerkey()));
            aedtTenKhach.setText(khachHang.getTenkhach());
            edtDienThoaiKhachHang.setText(khachHang.getSodienthoai());
            edtDiaChiKhachHang.setText(khachHang.getDiachi());
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
            lvPossition=lvKhachHang.pointToPosition( (int) e1.getX(), (int) e1.getY() );
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (lvPossition<0){
                    return true;
                }
                KhachHang khachHang = new KhachHang();
                khachHang=arrKhachHang.get(lvPossition);
                tvIdKhachHang.setText(khachHang.getId()+"");
                tvRkeyKhachHang.setText(String.valueOf(khachHang.getRkey()));
                tvServerKey.setText(String.valueOf(khachHang.getServerkey()));
                aedtTenKhach.setText(khachHang.getTenkhach());
                edtDienThoaiKhachHang.setText(khachHang.getSodienthoai());
                edtDiaChiKhachHang.setText(khachHang.getDiachi());
                gotoNoCty();
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
        setContentView(R.layout.activity_khach_hang);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);

        Intent intent=getIntent();
        intentUserName=intent.getStringExtra("userName");

        addControls();
        addEvents();

        arrKhachHang=  crudLocaldb.KhachHang_getAllKhachHang();
        customadapterData.addAll(arrKhachHang);
        //this.arrKhachHang.addAll(list);
        this.KhachHang_listKhachHang=crudLocaldb.KhachHang_listKhachHang();
        setAdapter();
        initData();
    }

    private void addControls() {
        tvIdKhachHang=findViewById(R.id.tv_IdKH);
        tvRkeyKhachHang=findViewById(R.id.tv_RkeyKH);
        tvServerKey=findViewById(R.id.tv_KHServerKey);
        lvKhachHang=findViewById(R.id.lv_KhachHang);
        edtDiaChiKhachHang=findViewById(R.id.edt_DiaChiKhachHang);
        edtDienThoaiKhachHang=findViewById(R.id.edt_SoDienThoaiKhachHang);
        aedtTenKhach=findViewById(R.id.aedt_TenKhach);
    }

    private void addEvents() {
        aedtTenKhach.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    // on focus off
                    String str = aedtTenKhach.getText().toString();

                    ListAdapter listAdapter = aedtTenKhach.getAdapter();
                    for(int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if(str.compareToIgnoreCase(temp) == 0) {
                            if (longGet(tvRkeyKhachHang.getText()+"")==0){
                                aedtTenKhach.setText(temp);
                                KhachHang khachhang=new KhachHang();
                                khachhang=crudLocaldb.KhachHang_getKhachHangByTen(temp);
                                tvIdKhachHang.setText(khachhang.getId()+"");
                                tvRkeyKhachHang.setText(khachhang.getRkey()+"");
                                tvServerKey.setText(khachhang.getServerkey()+"");
                                aedtTenKhach.setText(khachhang.getTenkhach()+"");
                                edtDienThoaiKhachHang.setText(khachhang.getSodienthoai()+"");
                                edtDiaChiKhachHang.setText(khachhang.getDiachi()+"");
                                setEditMod(false);
                            }
                        }
                    }

                }
            }
        });
        edtDienThoaiKhachHang.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (edit_mode!="VIEW"){
                        UpdateKhachHang();
                        CheckAddNew();
                    }
                }
                return false;
            }
        });
        lvKhachHang.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                 mGestureDetector.onTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
            }
        });


    }

    private void gotoNoCty() {
        final long idkhachhang=longGet(tvRkeyKhachHang.getText()+"");
        final ArrayList<Thu>arrThu=crudLocaldb.Thu_getThuByKhachHang(idkhachhang);
        if (arrThu.size()>=1){
            Intent intent = new Intent(KhachHangActivity.this, NoCtyActivity.class);
            //muon gui theo kieu nay thi clas Thu phai dc element thang Serializable
            //Khai báo Bundle (thue 1 container)
            Bundle bundle=new Bundle();
            //đưa dữ liệu riêng lẻ vào Bundle (chat hàng len container)
            bundle.putSerializable("arrThu",arrThu);
            bundle.putString("tenKhachHang",getEditText(aedtTenKhach));
            //Đưa Bundle vào Intent
            //intent.putExtras(bundle);
            intent.putExtra("ThuPackage",bundle);
            intent.putExtra("userName",intentUserName);
            this.startActivityForResult(intent,REQUEST_START_NOCTY);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_START_NOCTY ) {
            boolean needRefresh = data.getBooleanExtra("needRefresh",true);
            // Refresh ListView
            if(needRefresh) {
               updateListKhachHang();
            }
        }
    }

    private void UpdateKhachHang(){
        KhachHang khachHang=new KhachHang();

        //if (chuyenbien != null) {
        //   crudLocaldb.ChuyenBien_addChuyenBien(chuyenbien);
        //}
        //cap nhat lai danh sach chuyen bien vi da co them record moi vao
        // updateListChuyenBien();
        //setAdapter();
        String tenkhachhang=getEditText(aedtTenKhach);
        String dienthoai=getEditText(edtDienThoaiKhachHang);
        String diachi=getEditText(edtDiaChiKhachHang);
        if(tenkhachhang.equals("")) {
            Toast.makeText(getApplicationContext(), "Cần nhập vào Tên đối tác", Toast.LENGTH_SHORT).show();
            return;
        }
        khachHang.setTenkhach(tenkhachhang);
        khachHang.setSodienthoai(dienthoai);
        khachHang.setDiachi(diachi);
        khachHang.setUpdatetime(getCurrentTimeMiliS());
        if (edit_mode=="NEW" && longGet(tvRkeyKhachHang.getText()+"")==0) {
            //chuyenbien.set;);= new ChuyenBien(tenchuyenbien,tentau,ngaykhoihanh);
            khachHang.setServerkey(0);
            khachHang.setRkey(longGet(getCurrentTimeMiliS()));
            if (khachHang != null) {
                long i = crudLocaldb.KhachHang_addKhachHang(khachHang);
                if (i !=-1) {
                    setEditMod(false);
                    ////doSync("a");
                    updateListKhachHang();
                }
            }
        }else{
            if (edit_mode=="EDIT") {
                ArrayList<KhachHang>arrKH=new ArrayList<>();
                arrKH=crudLocaldb.KhachHang_getKhachHangByRkey(longGet(tvRkeyKhachHang.getText()+""));
                khachHang.setNocty(arrKH.get(0).getNocty()+"");
                khachHang.setCtyno(arrKH.get(0).getCtyno()+"");
                khachHang.setRkey(longGet(String.valueOf(tvRkeyKhachHang.getText())));
                khachHang.setId(intGet(tvIdKhachHang.getText()+""));
                khachHang.setServerkey(intGet(String.valueOf(tvServerKey.getText())));
                int result = crudLocaldb.KhachHang_updateKhachHang(khachHang);
                if (result > 0) {
                    setEditMod(false);
                    ////doSync("d");
                    updateListKhachHang();
                }
            }
        }
    };

    private void CheckAddNew(){
        tvRkeyKhachHang.setText("");
        tvIdKhachHang.setText("");
        tvServerKey.setText("");
        aedtTenKhach.setText("");
        edtDienThoaiKhachHang.setText("");
        edtDiaChiKhachHang.setText("");
        aedtTenKhach.requestFocus();
        edit_mode="NEW";
        setEditMod(true);

    }

    private void CheckEdit(){
        edit_mode="EDIT";
        setEditMod(true);
    }

    private void CheckDelete(KhachHang dt) {
        if (dt.getServerkey()!=0){
            WantDeleteFromServer wdfs=new WantDeleteFromServer();
            wdfs.setmServerkey(dt.getServerkey());
            wdfs.setmTablename("khachhang");
            crudLocaldb.WDFS_addWDFS(wdfs);
        }
        crudLocaldb.KhachHang_deleteKhachHang(dt.getRkey());
        //remove from array list
        Predicate<KhachHang> personPredicate = p-> p.getId() == dt.getId();
        arrKhachHang.removeIf(personPredicate);
        //refesh screen
        aedtTenKhach.setText("");
        edtDienThoaiKhachHang.setText("");
        edtDiaChiKhachHang.setText("");
        aedtTenKhach.requestFocus();
        // Refresh ListView.
        //doSync("d");
        setAdapter();
        updateListKhachHang();
    }

    private void setAdapter() {
        if (customAdapter == null) {
            // gan data source cho adapter
            customAdapter = new CustomAdapterKhachHang(KhachHangActivity.this, R.layout.customlist_dtkh,customadapterData);
            //gan adapter cho spinner
            lvKhachHang.setAdapter(customAdapter);
            // cho autocopletwe
            adapterKH = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, KhachHang_listKhachHang);
            aedtTenKhach.setAdapter(adapterKH);

        }else{
            updateListKhachHang();
            lvKhachHang.setSelection(customAdapter.getCount()-1);
        }
    }
    //gett all to list
    public void updateListKhachHang(){
        customadapterData.clear();
        arrKhachHang=  crudLocaldb.KhachHang_getAllKhachHang();
        customadapterData.addAll(arrKhachHang);
        customAdapter.notifyDataSetChanged();
    }

    private  void initData(){
        //   try {
        if (arrKhachHang.size()>=1) { //phong truong hop null k co record nao
            //lay ra index hien tai cua listview
            KhachHang khachHang =arrKhachHang.get(arrKhachHang.size()-1);
            ////xg record cuoi cungcua lv
            lvKhachHang.setSelection(arrKhachHang.size()-1);
            tvIdKhachHang.setText(khachHang.getId()+"");
            tvRkeyKhachHang.setText(khachHang.getRkey()+"");
            tvServerKey.setText(khachHang.getServerkey()+"");
            aedtTenKhach.setText(khachHang.getTenkhach()+"");
            edtDienThoaiKhachHang.setText(khachHang.getSodienthoai()+"");
            edtDiaChiKhachHang.setText(khachHang.getDiachi()+"");
            setEditMod(false);
        }

        //    } catch (Exception e){
        //        Log.e(TAG, e.toString() );
        //    }

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
    private  void setEditMod(boolean chohaykhong){
        if (chohaykhong==true){
            mShowMenuSave=1;
            aedtTenKhach.setThreshold(1);
        }else {
            mShowMenuSave=0;
            aedtTenKhach.setThreshold(1000);
            edit_mode="VIEW";
        }
        invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again
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
