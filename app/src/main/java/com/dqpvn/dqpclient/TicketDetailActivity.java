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
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dqpvn.dqpclient.crudmanager.crudLocal;

import com.dqpvn.dqpclient.customadapters.CustomAdapterTicketDetail;
import com.dqpvn.dqpclient.models.Ticket;
import com.dqpvn.dqpclient.models.TicketDetail;
import com.dqpvn.dqpclient.models.WantDeleteFromServer;
import com.dqpvn.dqpclient.utils.MyContextWrapper;

import com.dqpvn.dqpclient.utils.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.function.Predicate;

import static com.dqpvn.dqpclient.utils.utils.formatNumber;
import static com.dqpvn.dqpclient.utils.utils.getCurrentTimeMiliS;
import static com.dqpvn.dqpclient.utils.utils.getEditText;
import static com.dqpvn.dqpclient.utils.utils.hideSoftKeyboard;
import static com.dqpvn.dqpclient.utils.utils.intGet;
import static com.dqpvn.dqpclient.utils.utils.isBad;
import static com.dqpvn.dqpclient.utils.utils.longGet;

public class TicketDetailActivity extends AppCompatActivity {

    final private String TAG= getClass().getSimpleName();
    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;
    private int lvPossition=-1;

    private int mShowMenuSave=0;
    private boolean mShowEdit=true;
    private boolean menuHideAll=false;
    private boolean needRefesh=false;

    private TextView tvId, tvRkey, tvRkeyParent, tvServerKey;
    private EditText edtAmount, edtNgayPs, edtNotes;
    private AutoCompleteTextView aedtForUser;
    private ListView lvTicketDetail;
    private CheckBox chkParentFinished;
    private boolean intentParentFinished;
    //database
    private crudLocal crudLocaldb=crudLocal.getInstance(this);
    //Khai báo Datasource lưu trữ danh sách doi tac
    private ArrayList<TicketDetail> arrTicketDetail=new ArrayList<>();
    private ArrayList<TicketDetail>customadapterData=new ArrayList<>();

    //Khai báo ArrayAdapter cho ListView
    private CustomAdapterTicketDetail customAdapter;
    private ArrayAdapter<String> adapterUser;
    private ArrayList<String> listUser=new ArrayList<>();
    //Theo gioi cho phép Nhap lieu
    private String edit_mode="VIEW", intentUserName;
    private long intentRkeyTicket;

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
            lvPossition=lvTicketDetail.pointToPosition( (int) x, (int) y );
            if (lvPossition<0){return true;}
            TicketDetail ticketd = new TicketDetail();
            ticketd=arrTicketDetail.get(lvPossition);
            tvId.setText(ticketd.getId()+"");
            tvRkey.setText(String.valueOf(ticketd.getRkey()));
            tvServerKey.setText(String.valueOf(ticketd.getServerkey()));
            tvRkeyParent.setText(String.valueOf(ticketd.getRkeyticket()));
            edtAmount.setText(formatNumber(ticketd.getAmount()));
            aedtForUser.setText(ticketd.getForuser());
            edtNgayPs.setText(ticketd.getNgayps());
            edtNotes.setText(ticketd.getNotes());
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
            lvPossition=lvTicketDetail.pointToPosition( (int) e1.getX(), (int) e1.getY() );
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
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

    //lam viec voi menu
    @Override
    public boolean onCreateOptionsMenu(Menu manu) {
        getMenuInflater().inflate(R.menu.record_menu, manu);
        MenuItem mSave = manu.findItem(R.id.save);
        MenuItem mAdd=manu.findItem(R.id.id_new);
        MenuItem mEdit=manu.findItem(R.id.id_edit);
        if (menuHideAll){
            for (int i = 0; i < manu.size(); i++){
                manu.getItem(i).setVisible(false);
            }
            return true;
        }
        if (mShowEdit){
            mAdd.setVisible(true);
            mEdit.setVisible(true);
        }else{
            mAdd.setVisible(false);
            mEdit.setVisible(false);
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
                if (isBad(intentUserName)){
                    return true;
                }
                //wrtite ơn logic
                CheckAddNew();
                return true;
            case R.id.id_edit :
                if (arrTicketDetail.size()==0){
                    return true;
                }
                if (isBad(intentUserName)){
                    return true;
                }
                CheckEdit();
                return true;
            case R.id.id_delete :
                //wrtite ơn logic
                if (arrTicketDetail.size()==0){
                    return true;
                }
                if (isBad(intentUserName)){
                    return true;
                }

                if (!intentUserName.substring(0,5).equals("admin")){
                    Toast.makeText(this, "Dữ liệu được bảo vệ...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                //lay ra index hien tai cua listview
                if (lvPossition==-1){
                    Toast.makeText(this, "Chưa chọn đúng dữ liệu cần xóa", Toast.LENGTH_SHORT).show();
                }else {
                    final TicketDetail ticketd =arrTicketDetail.get(lvPossition);
                    // Hỏi trước khi xóa.
                    new AlertDialog.Builder(this)
                            .setTitle("DQP Client")
                            .setMessage(ticketd.getAmount()+ "\n\n"+ "Có chắc xóa?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    CheckDelete(ticketd);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
                return true;
            case R.id.save:
                UpdateTicket();
                hideSoftKeyboard(TicketDetailActivity.this);
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
        setContentView(R.layout.activity_ticket_detail);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);


        addControls();
        initialization();
        addEvents();
        setAdapter();
        initData();
    }

    private void addControls() {
        tvId=findViewById(R.id.tv_IdTicketDetail);
        tvRkey=findViewById(R.id.tv_TicketDetailRkey);
        tvServerKey=findViewById(R.id.tv_TicketDetailServerkey);
        tvRkeyParent=findViewById(R.id.tv_RkeyTicket);
        edtAmount=findViewById(R.id.edt_Amount);
        edtNgayPs=findViewById(R.id.edt_TicketDetailNgayPS);
        aedtForUser=findViewById(R.id.aedt_TicketDetailForUser);
        edtNotes=findViewById(R.id.edt_TicketDetailNotes);
        lvTicketDetail=findViewById(R.id.lv_TicketDetail);
        chkParentFinished=findViewById(R.id.chk_TicketDetailParentFisned);

    }
    private void initialization() {

        listUser=crudLocaldb.Ticket_getListUserOpenTicket();
        adapterUser = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listUser);
        aedtForUser.setAdapter(adapterUser);

        Intent intent=getIntent();
        intentUserName=intent.getStringExtra("userName");
        intentRkeyTicket=intent.getLongExtra("rkeyTicket",0);
        if (intent.hasExtra("finished")){
            intentParentFinished=intent.getBooleanExtra("finished",false);
            chkParentFinished.setChecked(intentParentFinished);
            if (intentParentFinished){
                mShowEdit=false;
                mShowMenuSave=0;
            }else{
                mShowMenuSave=1;
                mShowEdit=true;
            }
            invalidateOptionsMenu();
        }


        arrTicketDetail=crudLocaldb.TicketDetail_getTicketDetailByParentRkey(intentRkeyTicket);
        customadapterData.addAll(arrTicketDetail);

        if (intent.hasExtra("makeNew")){
            CheckAddNew();
            String [] result=new String [2];
            result=crudLocaldb.Ticket_getUserAndOpenDateByRkey(intentRkeyTicket);
            aedtForUser.setText(result[0]);
            edtNgayPs.setText(result[1]);
        }

        if (!intentUserName.substring(0,5).equals("admin")){
            menuHideAll=true;
            invalidateOptionsMenu();
        }
    }
    private void addEvents() {
        //edtAmount.addTextChangedListener(new NumberTextWatcher(edtAmount));
        edtAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    edtAmount.setText(formatNumber(edtAmount.getText()+""));
                }
            }
        });
        aedtForUser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    // on focus off
                    String str = aedtForUser.getText().toString();

                    try {
                        ListAdapter listAdapter = aedtForUser.getAdapter();
                        for(int i = 0; i < listAdapter.getCount(); i++) {
                            String temp = listAdapter.getItem(i).toString();
                            if(str.compareTo(temp) == 0) {
                                return;
                            }
                        }

                        aedtForUser.setText("");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        edtNgayPs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(edtNgayPs);
            }
        });
        edtNgayPs.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edtNgayPs.setText("");
                return true;
            }
        });

        lvTicketDetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                 mGestureDetector.onTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    public void showDatePickerDialog(final EditText edtViewDate) {
        hideSoftKeyboard(TicketDetailActivity.this);
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
        Calendar cal;
        String s;
        if (!utils.isDate(utils.getEditText(TicketDetailActivity.this,edtViewDate))){
            //Dinh dang lai kieu ngay hien tai
            cal= Calendar.getInstance();
            SimpleDateFormat dft=new SimpleDateFormat("dd/MM/yyyy");
            //gan ngay thang hien tai da dc dinh dang cho s
            s=dft.format(cal.getTime());
        }else {
            s=utils.getEditText(TicketDetailActivity.this,edtViewDate);
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

    private void UpdateTicket(){
        TicketDetail ticketd=new TicketDetail();
        String username=getEditText(aedtForUser);
        Boolean cons=false;
        if(username.equals("")) {
            Toast.makeText(getApplicationContext(), "Cần nhập vào Tên người dùng", Toast.LENGTH_SHORT).show();
            return;
        }
        long amount = longGet(this, edtAmount);
       
        ticketd.setAmount(amount+"");
        ticketd.setForuser(aedtForUser.getText()+"");
        ticketd.setNgayps(edtNgayPs.getText()+"");
        ticketd.setNotes(edtNotes.getText()+"");
        ticketd.setRkeyticket(intentRkeyTicket);
        ticketd.setUsername(intentUserName);
        ticketd.setUpdatetime(getCurrentTimeMiliS());
        if (edit_mode=="NEW" && longGet(tvRkey.getText()+"")==0) {
            //chuyenbien.set;);= new ChuyenBien(tenchuyenbien,tentau,ngaykhoihanh);
            ticketd.setServerkey(0);
            ticketd.setRkey(longGet(getCurrentTimeMiliS()));
            if (ticketd != null) {
                long i = crudLocaldb.TicketDetail_addTicketDetail(ticketd);
                if (i != -1) {
                    setEditMod(false);
                    updateListTicket();
                    updateParent();
                }
            }
        }else{
            if (edit_mode=="EDIT") {
                ticketd.setRkey(longGet(String.valueOf(tvRkey.getText())));
                ticketd.setId(intGet(tvId.getText()+""));
                ticketd.setServerkey(intGet(String.valueOf(tvServerKey.getText())));
                int result = crudLocaldb.TicketDetail_updateTicketDetail(ticketd);
                if (result > 0) {
                    updateListTicket();
                    updateParent();
                    setEditMod(false);
                }
            }
        }
    }

    private void updateParent(){
        ArrayList<Ticket>arrTicket=new ArrayList<>();
        Ticket ticket =new Ticket();
        arrTicket=crudLocaldb.Ticket_getTicketByRkey(intentRkeyTicket);
        ticket=arrTicket.get(0);
        ticket.setAmount(crudLocaldb.TicketDetail_getSumAmountByParentRkey(intentRkeyTicket));
        ticket.setUpdatetime(getCurrentTimeMiliS());
        int result = crudLocaldb.Ticket_updateTicket(ticket);
        if (result>0){
            this.needRefesh=true;
        }
    }

    private void CheckAddNew(){
        tvId.setText("");
        tvRkey.setText("");
        tvServerKey.setText("");
        tvRkeyParent.setText("");
        edtAmount.setText("");
        aedtForUser.setText("");
        edtNgayPs.setText("");
        edtNotes.setText("");
        edtAmount.requestFocus();
        edit_mode="NEW";
        setEditMod(true);

    }

    private void CheckEdit(){
        edit_mode="EDIT";
        setEditMod(true);
    }

    private void CheckDelete(TicketDetail dt) {
        if (dt.getServerkey()!=0){
            WantDeleteFromServer wdfs=new WantDeleteFromServer();
            wdfs.setmServerkey(dt.getServerkey());
            wdfs.setmTablename("ticketdetail");
            crudLocaldb.WDFS_addWDFS(wdfs);
        }
        crudLocaldb.TicketDetail_deleteTicketDetail(dt.getRkey());
        //remove from array list
        Predicate<TicketDetail> personPredicate = p-> p.getId() == dt.getId();
        arrTicketDetail.removeIf(personPredicate);
        //refesh screen
        tvId.setText("");
        tvRkey.setText("");
        tvServerKey.setText("");
        tvRkeyParent.setText("");
        edtAmount.setText("");
        aedtForUser.setText("");
        edtNgayPs.setText("");
        edtNotes.setText("");
        edtAmount.requestFocus();

        // Refresh ListView.
        updateListTicket();
        updateParent();
    }

    private void setAdapter() {
        if (customAdapter == null) {
            // gan data source cho adapter
            customAdapter = new CustomAdapterTicketDetail(TicketDetailActivity.this, R.layout.customlist_ticketdetail,customadapterData);
            //gan adapter cho spinner
            lvTicketDetail.setAdapter(customAdapter);
        }else{
            updateListTicket();
            lvTicketDetail.setSelection(customAdapter.getCount()-1);
        }
    }
    //gett all to list
    public void updateListTicket(){
        customadapterData.clear();
        arrTicketDetail=  crudLocaldb.TicketDetail_getTicketDetailByParentRkey(intentRkeyTicket);
        customadapterData.addAll(arrTicketDetail);
        customAdapter.notifyDataSetChanged();
    }

    private  void initData(){
        if (arrTicketDetail.size()>=1) { //phong truong hop null k co record nao
            TicketDetail ticketd =arrTicketDetail.get(arrTicketDetail.size()-1);
            lvTicketDetail.setSelection(arrTicketDetail.size()-1);
            tvId.setText(ticketd.getId()+"");
            tvRkey.setText(String.valueOf(ticketd.getRkey()));
            tvServerKey.setText(String.valueOf(ticketd.getServerkey()));
            tvRkeyParent.setText(String.valueOf(ticketd.getRkeyticket()));
            edtAmount.setText(formatNumber(ticketd.getAmount()));
            aedtForUser.setText(ticketd.getForuser());
            edtNgayPs.setText(ticketd.getNgayps());
            edtNotes.setText(ticketd.getNotes());
            setEditMod(false);
        }

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
            aedtForUser.setThreshold(1);
        }else {
            mShowMenuSave=0;
            aedtForUser.setThreshold(1000);
            edit_mode="VIEW";
        }
        invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again
    }

    @Override
    public void finish() {
        // Chuẩn bị dữ liệu Intent.
        Intent data = new Intent();
        // Yêu cầu MainActivity refresh lại ListView hoặc không.
        data.putExtra("needRefresh", this.needRefesh);
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
