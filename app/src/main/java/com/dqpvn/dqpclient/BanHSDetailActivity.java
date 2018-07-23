package com.dqpvn.dqpclient;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.customadapters.CustomAdapterBanHSDetail;
import com.dqpvn.dqpclient.models.BanHSDetail;

import com.dqpvn.dqpclient.models.Thu;
import com.dqpvn.dqpclient.models.ThuDetail;
import com.dqpvn.dqpclient.models.WantDeleteFromServer;
import com.dqpvn.dqpclient.utils.MyContextWrapper;
import com.dqpvn.dqpclient.utils.NumberTextWatcher;
import com.dqpvn.dqpclient.utils.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.function.Predicate;

import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_NAME;
import static com.dqpvn.dqpclient.utils.utils.doubleGet;
import static com.dqpvn.dqpclient.utils.utils.getCurrentTimeMiliS;
import static com.dqpvn.dqpclient.utils.utils.hideSoftKeyboard;
import static com.dqpvn.dqpclient.utils.utils.intGet;
import static com.dqpvn.dqpclient.utils.utils.longGet;
import static com.dqpvn.dqpclient.utils.utils.round;

public class BanHSDetailActivity extends AppCompatActivity {

    final private String TAG = getClass().getSimpleName();

    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;



    private boolean mShowMenuSave=false;
    private int lvPossition=-1;
    private boolean needRefresh=false;
    private TextView tvID, tvRkey, tvServerKey;
    private EditText edtSoLuong;
    private ListView lvBanHS;
    private LinearLayout lySoLuong;
    //database
    private crudLocal crudLocaldb=crudLocal.getInstance(this);
    //Khai báo ArrayAdapter
    private ArrayList<BanHSDetail> arrBanHs = new ArrayList<>();
    private CustomAdapterBanHSDetail customAdapter;
    private ArrayList<BanHSDetail> customadapterData=new ArrayList<>();
    //Theo gioi cho phép Nhap lieu
    private String edit_mode="VIEW", intentTenhs;

    private long intentRkeyThuTong,intenRkeyThuDetail;

    //lam viec voi menu
    @Override
    public boolean onCreateOptionsMenu(Menu manu) {
        getMenuInflater().inflate(R.menu.banhs_menu, manu);
        MenuItem mSave = manu.findItem(R.id.save);
        MenuItem mDel = manu.findItem(R.id.id_delete);
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
            //ten cua cac id khi thiet ke cac resource menu
            case R.id.id_edit :
                if (arrBanHs.size()==0){
                    return true;
                }
                lySoLuong.setVisibility(View.VISIBLE);
                edit_mode="EDIT";
                edtSoLuong.requestFocus();
                mShowMenuSave=true;
                invalidateOptionsMenu();
                return true;
            case R.id.save:
                UpdateSave();
                hideSoftKeyboard(BanHSDetailActivity.this);
                return true;
            case R.id.id_delete :
                //wrtite ơn logic
                if (arrBanHs.size()==0){
                    return true;
                }
                //lay ra index hien tai cua listview
                if (lvPossition<0){
                    Toast.makeText(this, "Chưa chọn đúng dữ liệu cần xóa", Toast.LENGTH_SHORT).show();
                }else {
                    final BanHSDetail banhsdetail =arrBanHs.get(lvPossition);
                    // Hỏi trước khi xóa.
                    new AlertDialog.Builder(this)
                            .setTitle("DQP Client")
                            .setMessage(banhsdetail.getTenhs() + " | " + banhsdetail.getSoluong()+ "\n" + "Có chắc xóa?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    CheckDelete(banhsdetail);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    private void gotoRec(int posittion){
        if (posittion<0){
            return;
        }
        BanHSDetail banhsdetail=new BanHSDetail();
        banhsdetail=arrBanHs.get(lvPossition);
        tvID.setText(banhsdetail.getId()+"");
        tvRkey.setText(banhsdetail.getRkey()+"");
        tvServerKey.setText(banhsdetail.getServerkey()+"");
        edtSoLuong.setText(banhsdetail.getSoluong()+"");
        lySoLuong.setVisibility(View.GONE);
        //setTitle(intentTenhs + " | " + banhsdetail.getSoluong());
        edit_mode="VIEW";
        mShowMenuSave=false;
        invalidateOptionsMenu();
        // Tính tổng tại đây
        double z=0;
        for (int i=0;i<lvPossition+1;i++){
            BanHSDetail bhddt=arrBanHs.get(i);
            z+=doubleGet(bhddt.getSoluong());
        }
        String s1=formatNumber(String.valueOf(banhsdetail.getSoluong()));
        String s2=formatNumber(String.valueOf(z));
        setTitle(intentTenhs + ": " + s1 + " | " +  s2);
    }

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int x = (int) e.getX();
            int y = (int) e.getY();
            lvPossition=lvBanHS.pointToPosition( (int) x, (int) y );
            if (lvPossition<0){
                return true;
            }
            gotoRec(lvPossition);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(TAG, "onLongPress");
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            lvPossition=lvBanHS.pointToPosition( (int) e1.getX(), (int) e1.getY() );
            gotoRec(lvPossition);
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
//        mGestureDetector.onTouchEvent(event);
//        return super.onTouchEvent(event);
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        setContentView(R.layout.activity_banhsdetail);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);


        intentRkeyThuTong=intent.getLongExtra("rkeyThu",0);
        intenRkeyThuDetail=intent.getLongExtra("rkeyThuDetail",0);
        intentTenhs=intent.getStringExtra("tenHaiSan");
        setTitle(intentTenhs);


        edtSoLuong=findViewById(R.id.edt_BanHSDetailsoluong);
        lvBanHS=findViewById(R.id.lv_BanHSDetail);
        tvID=findViewById(R.id.tv_BanHSDetailID);
        tvRkey=findViewById(R.id.tv_BanHSDetailrkey);
        tvServerKey=findViewById(R.id.tv_BanHSDetailserverkey);
        lySoLuong=(LinearLayout) findViewById(R.id.ly_BanHSDetailsoluong_LY);
        lySoLuong.setVisibility(View.GONE);

        addEvents();

        arrBanHs=crudLocaldb.BanHSDetail_getBanHSDetailByRkeyThuDetail(intenRkeyThuDetail);
        customadapterData.addAll(arrBanHs);
        setAdapter();

    }

    private void CheckDelete(BanHSDetail dt) {
        if (intGet(tvServerKey.getText() + "")!=0){
            WantDeleteFromServer wdfs=new WantDeleteFromServer();
            wdfs.setmServerkey(intGet(tvServerKey.getText() + ""));
            wdfs.setmTablename("banhsdetail");
            crudLocaldb.WDFS_addWDFS(wdfs);
        }
        crudLocaldb.BanHSDetail_deleteBanHSDetail(dt.getRkey());
        this.needRefresh=true;
        //remove from array list
        Predicate<BanHSDetail> personPredicate = p-> p.getId() == dt.getId();
        arrBanHs.removeIf(personPredicate);
        //refesh screen
        edtSoLuong.setText("");
        updateListBanHSDetail();
        capNhatThuDetail();
        if (arrBanHs.size()==0){this.finish();}
    }
    private void addEvents(){
        //edtSoLuong.addTextChangedListener(new NumberTextWatcher(edtSoLuong));

        lvBanHS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                lvPossition=position;
                gotoRec(lvPossition);
            }
        });

        lvBanHS.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mGestureDetector.onTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
                // return true;
            }
        });
    }

    private void setAdapter() {
        if (customAdapter == null) {
            // gan data source cho adapter
            customAdapter = new CustomAdapterBanHSDetail(this, R.layout.customlist_banhsdetail,customadapterData);
            //gan adapter cho spinner
            lvBanHS.setAdapter(customAdapter);
        }else{
            updateListBanHSDetail();
        }
        lvBanHS.setSelection(customAdapter.getCount()-1);
    }
    //gett all to list
    public void updateListBanHSDetail(){
        customadapterData.clear();
        arrBanHs=crudLocaldb.BanHSDetail_getBanHSDetailByRkeyThuDetail(intenRkeyThuDetail);
        customadapterData.addAll(arrBanHs);
        customAdapter.notifyDataSetChanged();
        lvBanHS.setSelection(customAdapter.getCount()-1);
    }

    private void UpdateSave() {
        if (doubleGet(edtSoLuong.getText()+"")==0) {
            return;
        }
        BanHSDetail banhsdetail =new BanHSDetail();
        banhsdetail.setId(intGet(tvID.getText()+""));
        banhsdetail.setRkey(longGet(tvRkey.getText()+""));
        banhsdetail.setServerkey(intGet(tvServerKey.getText()+""));
        banhsdetail.setRkeythu(intentRkeyThuTong);
        banhsdetail.setRkeythudetail(intenRkeyThuDetail);
        banhsdetail.setTenhs(intentTenhs);
        banhsdetail.setSoluong(doubleGet(edtSoLuong.getText()+"")+"");
        banhsdetail.setUpdatetime(getCurrentTimeMiliS());
        long b=crudLocaldb.BanHSDetail_updateBanHSDetail(banhsdetail);
        if (b>0){
            capNhatThuDetail();
            lvBanHS.setSelection(arrBanHs.size()-1);
            updateListBanHSDetail();
            this.needRefresh=true;
        }
        edit_mode="VIEW";
    }

    private void capNhatThuDetail(){
        ThuDetail thudetail = new ThuDetail();
        thudetail=crudLocaldb.ThuDetail_getThuDetailByRkey(intenRkeyThuDetail);
        String sumSoLuongHS=crudLocaldb.BanHSDetail_SumSLHaiSanByRkeyThuDetail(intenRkeyThuDetail);
        //double thanhtien=doubleGet(sumSoLuongHS)*longGet(thudetail.getDongia());
        String tt=String.valueOf(doubleGet(sumSoLuongHS)*longGet(thudetail.getDongia()));
        //vi tt se cho ra so khoa hoc dang 9.18E+09 nen can chuyen doi
        long thanhtien=Double.valueOf(tt).longValue();
        thudetail.setSoluong(sumSoLuongHS+"");
        thudetail.setThanhtien(thanhtien+"");
        thudetail.setUpdatetime(getCurrentTimeMiliS());
        thudetail.setUsername(LOGIN_NAME);
        int result = crudLocaldb.ThuDetail_updateThuDetail(thudetail);
        if (result>0){
            capNhatThuTong();
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
