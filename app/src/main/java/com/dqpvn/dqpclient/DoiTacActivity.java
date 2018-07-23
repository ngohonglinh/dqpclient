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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.restfullAPI;
import com.dqpvn.dqpclient.customadapters.CustomAdapterDoiTac;
import com.dqpvn.dqpclient.models.Chi;
import com.dqpvn.dqpclient.models.DoiTac;
import com.dqpvn.dqpclient.models.ResponseFromServer;
import com.dqpvn.dqpclient.models.WantDeleteFromServer;
import com.dqpvn.dqpclient.utils.MyContextWrapper;
import com.dqpvn.dqpclient.utils.utils;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_OK;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.MY_SERVER;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.RKEY_TICKET;
import static com.dqpvn.dqpclient.utils.utils.getCurrentTimeMiliS;
import static com.dqpvn.dqpclient.utils.utils.hideSoftKeyboard;
import static com.dqpvn.dqpclient.utils.utils.intGet;
import static com.dqpvn.dqpclient.utils.utils.longGet;

public class DoiTacActivity extends AppCompatActivity {

    final private String TAG= getClass().getSimpleName();
    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;
    private int lvPossition=-1;

    //base

    private int mShowMenuSave=0;
    private final int REQUEST_START_CTYNO=111;
    private TextView tvIdDoiTac,tvRkeyDoiTac, tvServerKey;
    private EditText edtDiaChiDoiTac, edtDienThoaiDoiTac;
    private AutoCompleteTextView aedtTenDoiTac;
    private ListView lvDoiTac;
    //database
    private crudLocal crudLocaldb=crudLocal.getInstance(this);
    //Khai báo Datasource lưu trữ danh sách doi tac
    private ArrayList<DoiTac> arrDoiTac=new ArrayList<>();
    private ArrayList<DoiTac> customadapterData=new ArrayList<>();
    private String [] DoiTac_listDoiTac;
    //Khai báo ArrayAdapter cho ListView
    private CustomAdapterDoiTac customAdapter;
    private ArrayAdapter<String> adapterDT;
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
                setEditMod(true);
                return true;
            case R.id.id_edit :
                if (arrDoiTac.size()==0){
                    return true;
                }
                CheckEdit();
                setEditMod(true);
                return true;
            case R.id.id_delete :
                //wrtite ơn logic
                if (arrDoiTac.size()==0){
                    return true;
                }

                if (checkChi()){
                    Toast.makeText(this, "Không thể xóa vì đang có chi tiết liên quan", Toast.LENGTH_SHORT).show();
                    return true;
                }
                //lay ra index hien tai cua listview
                if (lvPossition==-1){
                    Toast.makeText(this, "Chưa chọn đúng dữ liệu cần xóa", Toast.LENGTH_SHORT).show();
                }else {
                    final DoiTac doitac =arrDoiTac.get(lvPossition);
                    // Hỏi trước khi xóa.
                    new AlertDialog.Builder(this)
                            .setTitle("DQP Client")
                            .setMessage(doitac.getTendoitac()+ "\n\n" + "Có chắc xóa?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    CheckDelete(doitac);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
                return true;
            case R.id.save:
                UpdateDoiTac();
                hideSoftKeyboard(DoiTacActivity.this);
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

    private boolean checkChi(){
        ArrayList<Chi>arrChi=new ArrayList<>();
        long rkeyDoiTac=0;
        rkeyDoiTac=longGet(tvRkeyDoiTac.getText()+"");
        arrChi=crudLocaldb.Chi_getChiByDoiTac(rkeyDoiTac);
        if (arrChi.size()>0) {
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
            lvPossition=lvDoiTac.pointToPosition( (int) x, (int) y );

            if (lvPossition<0){
                return true;
            }
            DoiTac doiTac = arrDoiTac.get(lvPossition);
            tvIdDoiTac.setText(String.valueOf(doiTac.getId()));
            tvRkeyDoiTac.setText(String.valueOf(doiTac.getRkey()));
            tvServerKey.setText(String.valueOf(doiTac.getServerkey()));
            aedtTenDoiTac.setText(doiTac.getTendoitac());
            edtDienThoaiDoiTac.setText(doiTac.getSodienthoai());
            edtDiaChiDoiTac.setText(doiTac.getDiachi());

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
            lvPossition=lvDoiTac.pointToPosition( (int) e1.getX(), (int) e1.getY() );
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (lvPossition<0){
                    return true;
                }
                DoiTac doiTac = arrDoiTac.get(lvPossition);
                tvIdDoiTac.setText(String.valueOf(doiTac.getId()));
                tvRkeyDoiTac.setText(String.valueOf(doiTac.getRkey()));
                tvServerKey.setText(String.valueOf(doiTac.getServerkey()));
                aedtTenDoiTac.setText(doiTac.getTendoitac());
                edtDienThoaiDoiTac.setText(doiTac.getSodienthoai());
                edtDiaChiDoiTac.setText(doiTac.getDiachi());
                gotoCtyNo();
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
        setContentView(R.layout.activity_doi_tac);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);


        addControls();
        addEvents();

        arrDoiTac=  crudLocaldb.DoiTac_getAllDoiTac();

        //resum no doi tac if wrong
        String[] tongno = new String[2];
        for (int i=0;i<arrDoiTac.size();i++){
            DoiTac doiTac=new DoiTac();
            doiTac=arrDoiTac.get(i);
            long rkeyDoiTac=doiTac.getRkey();
            tongno = crudLocaldb.Chi_SumGiaTriDoiTac(rkeyDoiTac);
            crudLocaldb.DoiTac_CapNhatNo(rkeyDoiTac, tongno[0], tongno[1], doiTac.getUpdatetime());
        }

        customadapterData.addAll(arrDoiTac);
        //this.arrDoiTac.addAll(list);
        Intent intent=getIntent();
        intentUserName=intent.getStringExtra("userName");
        this.DoiTac_listDoiTac=crudLocaldb.DoiTac_listDoiTac();
        setAdapter();
        initData();
    }

    private void addControls() {
        tvIdDoiTac=findViewById(R.id.tv_IdDoiTac);
        tvRkeyDoiTac=findViewById(R.id.tv_RkeyDoiTac);
        tvServerKey=findViewById(R.id.tv_DTServerKey);
        lvDoiTac=findViewById(R.id.lv_DoiTac);
        edtDiaChiDoiTac=findViewById(R.id.edt_DiaChiDoiTac);
        edtDienThoaiDoiTac=findViewById(R.id.edt_SoDienThoaiDoiTac);
        aedtTenDoiTac=findViewById(R.id.aedt_TenDoiTac);
    }

    private void addEvents() {
        aedtTenDoiTac.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    // on focus off
                    String str = aedtTenDoiTac.getText().toString();

                    ListAdapter listAdapter = aedtTenDoiTac.getAdapter();
                    for(int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if(str.compareToIgnoreCase(temp) == 0) {
                            if (longGet(tvRkeyDoiTac.getText()+"")==0){
                                aedtTenDoiTac.setText(temp);
                                DoiTac doiTac=new DoiTac();
                                doiTac=crudLocaldb.DoiTac_getDoiTacByTen(temp);
                                tvIdDoiTac.setText(doiTac.getId()+"");
                                tvRkeyDoiTac.setText(doiTac.getRkey()+"");
                                tvServerKey.setText(doiTac.getServerkey()+"");
                                aedtTenDoiTac.setText(doiTac.getTendoitac()+"");
                                edtDienThoaiDoiTac.setText(doiTac.getSodienthoai()+"");
                                edtDiaChiDoiTac.setText(doiTac.getDiachi()+"");
                                setEditMod(false);
                            }
                        }
                    }

                }
            }
        });
        edtDienThoaiDoiTac.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (edit_mode!="VIEW"){
                    UpdateDoiTac();
                    CheckAddNew();
                }
                return false;
            }
        });

        lvDoiTac.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                 mGestureDetector.onTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
            }
        });


    }

    private void gotoCtyNo() {
        final long iddoitac=longGet(tvRkeyDoiTac.getText()+"");

        final ArrayList<Chi>arrChi=crudLocaldb.Chi_getChiByDoiTac(iddoitac);
        if (arrChi.size()>=1){
            Intent intent = new Intent(DoiTacActivity.this, CtyNoActivity.class);
            //muon gui theo kieu nay thi clas Chi phai dc element thang Serializable
            //Khai báo Bundle (thue 1 container)
            Bundle bundle=new Bundle();
            //đưa dữ liệu riêng lẻ vào Bundle (chat hàng len container)
            bundle.putSerializable("arrChi",arrChi);
            bundle.putString("tenDoiTac",getEditText(aedtTenDoiTac));
            //Đưa Bundle vào Intent
            //intent.putExtras(bundle);
            intent.putExtra("ChiPackage",bundle);
            intent.putExtra("userName",intentUserName);
            intent.putExtra("rkeyTicket", RKEY_TICKET);
            this.startActivityForResult(intent,REQUEST_START_CTYNO);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_START_CTYNO ) {
            boolean needRefresh = data.getBooleanExtra("needRefresh",false);
            // Refresh ListView
            if(needRefresh) {
                updateListDoiTac();
            }
        }
    }

    private void UpdateDoiTac(){
        DoiTac doiTac=new DoiTac();

        //if (chuyenbien != null) {
        //   crudLocaldb.ChuyenBien_addChuyenBien(chuyenbien);
        //}
        //cap nhat lai danh sach chuyen bien vi da co them record moi vao
        // updateListChuyenBien();
        //setAdapter();
        String tendoitac=getEditText(aedtTenDoiTac);
        String dienthoai=getEditText(edtDienThoaiDoiTac);
        String diachi=getEditText(edtDiaChiDoiTac);
        if(tendoitac.equals("")) {
            Toast.makeText(getApplicationContext(), "Cần nhập vào Tên đối tác", Toast.LENGTH_SHORT).show();
            return;
        }
        doiTac.setTendoitac(tendoitac);
        doiTac.setSodienthoai(dienthoai);
        doiTac.setDiachi(diachi);
        doiTac.setUpdatetime(getCurrentTimeMiliS());
        if (edit_mode=="NEW" && longGet(tvRkeyDoiTac.getText()+"")==0) {
            //chuyenbien.set;);= new ChuyenBien(tenchuyenbien,tentau,ngaykhoihanh);
            doiTac.setServerkey(0);
            doiTac.setRkey(longGet(getCurrentTimeMiliS()));
            if (doiTac != null) {
                long i = crudLocaldb.DoiTac_addDoiTac(doiTac);
                if (i !=-1) {
                    setEditMod(false);
                    ////doSync("a");
                    updateListDoiTac();
                    setAdapter();
                }
            }
            //cap nhat lai danh sach chuyen bien vi da co them record moi vao
            //}
        }else{
            if (edit_mode=="EDIT") {
                ArrayList<DoiTac>arrDT=new ArrayList<>();
                arrDT=crudLocaldb.DoiTac_getDoiTacByRkey(longGet(tvRkeyDoiTac.getText()+""));
                doiTac.setNocty(arrDT.get(0).getNocty()+"");
                doiTac.setCtyno(arrDT.get(0).getCtyno()+"");
                doiTac.setId(intGet(tvIdDoiTac.getText()+""));
                doiTac.setRkey(longGet(String.valueOf(tvRkeyDoiTac.getText())));
                doiTac.setServerkey(intGet(String.valueOf(tvServerKey.getText())));
                int result = crudLocaldb.DoiTac_updateDoiTac(doiTac);
                if (result > 0) {
                    updateListDoiTac();
                    setEditMod(false);
                    ////doSync("u");
                }
            }
        }
    };

    private void CheckAddNew(){
        edit_mode="NEW";
        tvRkeyDoiTac.setText("");
        tvIdDoiTac.setText("");
        tvServerKey.setText("");
        aedtTenDoiTac.setText("");
        edtDienThoaiDoiTac.setText("");
        edtDiaChiDoiTac.setText("");
        aedtTenDoiTac.requestFocus();

    }

    private void CheckEdit(){
        edit_mode="EDIT";
    }

    private void CheckDelete(DoiTac dt) {
        if (dt.getServerkey()!=0){
            WantDeleteFromServer wdfs=new WantDeleteFromServer();
            wdfs.setmServerkey(dt.getServerkey());
            wdfs.setmTablename("doitac");
            crudLocaldb.WDFS_addWDFS(wdfs);
        }
        crudLocaldb.DoiTac_deleteDoiTac(dt.getRkey());
        //remove from array list
        Predicate<DoiTac> personPredicate = p-> p.getId() == dt.getId();
        arrDoiTac.removeIf(personPredicate);
        //refesh screen
        aedtTenDoiTac.setText("");
        edtDienThoaiDoiTac.setText("");
        edtDiaChiDoiTac.setText("");
        aedtTenDoiTac.requestFocus();
        ////doSync("d");
        // Refresh ListView.
        updateListDoiTac();
        //this.customAdapter.notifyDataSetChanged();
    }

    private void setAdapter() {
        if (customAdapter == null) {
            // gan data source cho adapter
            customAdapter = new CustomAdapterDoiTac(this, R.layout.customlist_dtkh,customadapterData);
            //gan adapter cho spinner
            lvDoiTac.setAdapter(customAdapter);
            // cho autocopletwe
            adapterDT = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, DoiTac_listDoiTac);
            aedtTenDoiTac.setAdapter(adapterDT);

        }else{
            updateListDoiTac();
            adapterDT.notifyDataSetChanged();
            //cho troi xg record duoi cung
            lvDoiTac.setSelection(customAdapter.getCount()-1);
        }
    }
    //gett all to list
    public void updateListDoiTac(){
        customadapterData.clear();
        arrDoiTac=crudLocaldb.DoiTac_getAllDoiTac();
        customadapterData.addAll(arrDoiTac);
        customAdapter.notifyDataSetChanged();

    }

    private  void initData(){
     //   try {
            if (arrDoiTac.size()>=1) { //phong truong hop null k co record nao
                //lay ra index hien tai cua listview
                DoiTac doiTac =arrDoiTac.get(arrDoiTac.size()-1);
                ////xg record cuoi cungcua lv
                lvDoiTac.setSelection(arrDoiTac.size()-1);
                tvIdDoiTac.setText(doiTac.getId()+"");
                tvRkeyDoiTac.setText(doiTac.getRkey()+"");
                tvServerKey.setText(doiTac.getServerkey()+"");
                aedtTenDoiTac.setText(doiTac.getTendoitac()+"");
                edtDienThoaiDoiTac.setText(doiTac.getSodienthoai()+"");
                edtDiaChiDoiTac.setText(doiTac.getDiachi()+"");
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
            aedtTenDoiTac.setThreshold(1);
        }else {
            mShowMenuSave=0;
            aedtTenDoiTac.setThreshold(1000);
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
