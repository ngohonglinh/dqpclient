package com.dqpvn.dqpclient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.restfullAPI;
import com.dqpvn.dqpclient.customadapters.CustomAdapterTicket;
import com.dqpvn.dqpclient.customadapters.CustomAdapterTicketDetail;
import com.dqpvn.dqpclient.models.Chi;
import com.dqpvn.dqpclient.models.DebtBook;
import com.dqpvn.dqpclient.models.Ticket;
import com.dqpvn.dqpclient.models.TicketDetail;
import com.dqpvn.dqpclient.models.Users;
import com.dqpvn.dqpclient.models.WantDeleteFromServer;
import com.dqpvn.dqpclient.utils.MyContextWrapper;
import com.dqpvn.dqpclient.utils.NumberTextWatcher;
import com.dqpvn.dqpclient.utils.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.function.Predicate;

import static com.dqpvn.dqpclient.crudmanager.SyncCheck.IS_ADMIN;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_NAME;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.MY_SERVER;
import static com.dqpvn.dqpclient.utils.utils.formatNumber;
import static com.dqpvn.dqpclient.utils.utils.getCurrentDate;
import static com.dqpvn.dqpclient.utils.utils.getCurrentTimeMiliS;
import static com.dqpvn.dqpclient.utils.utils.hideSoftKeyboard;
import static com.dqpvn.dqpclient.utils.utils.intGet;
import static com.dqpvn.dqpclient.utils.utils.isBad;
import static com.dqpvn.dqpclient.utils.utils.longGet;

public class TicketActivity extends AppCompatActivity {

    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;

    private int lvPossition=-1;
    private int mShowMenuSave=0;
    private boolean menuHideAll=false;
    private final int REQUEST_START_CHI=123;
    private final int REQUEST_START_TICKET_DETAIL=124;
    final private String TAG= getClass().getSimpleName();
    private TextView tvIdTicket, tvRkeyTicket, tvServerKey, tvSum1;
    LinearLayout lyUsed, lyReturn;
    private EditText edtAmount, edtUsed, edtOpendate, edtClosedate, edtLydo, edtReturn, edtSum2;
    private AutoCompleteTextView aedtUsername;
    private Spinner spnUser;
    private CheckBox chkStatus;
    private ListView lvTicket;
    //database
    private crudLocal crudLocaldb=crudLocal.getInstance(this);

    //Khai báo Datasource lưu trữ danh sách doi tac
    private ArrayList<Ticket> arrTicket=new ArrayList<>();
    private ArrayList<Users>arrUser=new ArrayList<>();
    private ArrayList<Ticket>customadapterData=new ArrayList<>();
    //Khai báo ArrayAdapter cho ListView
    private CustomAdapterTicket customAdapter;
    private ArrayAdapter<String> adapterUser;
    private ArrayList<String> listUser=new ArrayList<>();

    private ArrayList<TicketDetail> arrTicketDetail=new ArrayList<>();
    private ArrayList<TicketDetail>customadapterDataDetail=new ArrayList<>();
    private CustomAdapterTicketDetail customAdapterDetail;

    //Theo gioi cho phép Nhap lieu
    private String edit_mode="VIEW", intentUserName;
    final String SHARED_PREFERENCES_NAME="dqpclient_preferences";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        TicketActivity.CustomGestureDetector customGestureDetector = new TicketActivity.CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);

        // Create an object of our Custom Gesture Detector Class
        addControls();
        initialization();
        addEvents();
        setAdapter();
        initData();
    }

    //lam viec voi menu
    @Override
    public boolean onCreateOptionsMenu(Menu manu) {
        getMenuInflater().inflate(R.menu.ticket_activity_menu, manu);
        MenuItem mSave = manu.findItem(R.id.save_ticket);
        MenuItem mDetail = manu.findItem(R.id.detail_ticket);
        if (menuHideAll){
            for (int i = 0; i < manu.size(); i++){
                manu.getItem(i).setVisible(false);
            }
            return true;
        }

        if (mShowMenuSave==1){
            mSave.setVisible(true);
            mDetail.setVisible(false);
        }else{
            mSave.setVisible(false);
            mDetail.setVisible(true);
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
            case R.id.id_new_ticket :
                if (isBad(LOGIN_NAME)){
                    return true;
                }
                //wrtite ơn logic
                CheckAddNew();
                return true;
            case R.id.id_edit_ticket :
                if (arrTicket.size()==0){
                    return true;
                }
                if (isBad(LOGIN_NAME)){
                    return true;
                }

                CheckEdit();
                return true;
            case R.id.id_delete_ticket :
                //wrtite ơn logic
                if (arrTicket.size()==0){
                    return true;
                }
                if (isBad(LOGIN_NAME)){
                    return true;
                }

                if (!LOGIN_NAME.substring(0,5).equals("admin")){
                    Toast.makeText(this, "Dữ liệu được bảo vệ...", Toast.LENGTH_SHORT).show();
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
                    final Ticket ticket =arrTicket.get(lvPossition);
                    // Hỏi trước khi xóa.
                    new AlertDialog.Builder(this)
                            .setTitle("DQP Client")
                            .setMessage(ticket.getUsername()+ "\n\n"+ "Có chắc xóa?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    CheckDelete(ticket);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                    return true;
                }

            case R.id.save_ticket:
                UpdateTicket();
                hideSoftKeyboard(TicketActivity.this);
                return true;
            case R.id.detail_ticket:
                if (!isBad(tvRkeyTicket.getText()+"")){
                        gotoTicketDetail();
                }
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
            lvPossition=lvTicket.pointToPosition( (int) x, (int) y );
            if (lvPossition<0){
                return true;
            }
            gotoTicket(lvPossition);
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
            lvPossition=lvTicket.pointToPosition( (int) e1.getX(), (int) e1.getY() );
            gotoTicket(lvPossition);

            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                gotolistChi();

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

    public void gotoTicket(int posittion){
        if (posittion<0 || !LOGIN_NAME.substring(0,5).equals("admin")){return;}

        Ticket ticket = new Ticket();

        ticket=arrTicket.get(posittion);
        tvIdTicket.setText(ticket.getId()+"");
        tvRkeyTicket.setText(String.valueOf(ticket.getRkey()));
        tvServerKey.setText(String.valueOf(ticket.getServerkey()));
        edtAmount.setText(utils.formatNumber(ticket.getAmount()));
        edtUsed.setText(ticket.getUsed());
        aedtUsername.setText(ticket.getUsername());
        long [] resul=crudLocaldb.Ticket_getAmountInfo(ticket.getRkey());
        if (resul[0]-resul[1]!=0){
            if (resul[0]-resul[1]<0){
                tvSum1.setText("Cty nợ: ");
                edtSum2.setText(String.valueOf(resul[1]-resul[0]));
                edtSum2.setTextColor(Color.parseColor("#3F51B5"));
            }else{
                tvSum1.setText("Nợ Cty: ");
                edtSum2.setText(String.valueOf(resul[0]-resul[1]));
                edtSum2.setTextColor(Color.parseColor("#F44336"));
            }
        }else{
            tvSum1.setText("Tồn nợ: ");
            edtSum2.setText("0");
            edtSum2.setTextColor(Color.BLACK);
        }
        edtOpendate.setText(ticket.getOpendate());
        edtLydo.setText(ticket.getLydo());
        edtReturn.setText(formatNumber(ticket.getComeback()));
        edtClosedate.setText(ticket.getClosedate());
        if (ticket.getFinished()==1){
            chkStatus.setChecked(true);
        }else{
            chkStatus.setChecked(false);
        }
        setEditMod(false);
    }




    private void initialization() {
//        // sua lai ticket k dc cap nhat do ly do nao do.
//        if (arrTicket.size()>0){
//            for (int i=0;i<arrTicket.size();i++){
//                Ticket ticket =new Ticket();
//                ticket=arrTicket.get(i);
//                if (ticket.getFinished()==0){
//                    String chiByChi=crudLocaldb.Chi_SumDaChiTicket(ticket.getRkey());
//                    String chiByDebt=crudLocaldb.DebtBook_SumDebtBookTicket(ticket.getRkey());
//                    long tongchi=longGet(chiByChi)+longGet(chiByDebt);
//                    if(longGet(ticket.getUsed())!=tongchi){
//                        //Cap nhat Ticket
//                        crudLocaldb.Ticket_CapNhatChi(ticket.getRkey(), String.valueOf(tongchi), ticket.getUpdatetime());
//                    }
//                }
//            }
//            updateListTicket();
//        }

        Intent intent=getIntent();
        intentUserName=intent.getStringExtra("userName");

        if (!LOGIN_NAME.substring(0,5).equals("admin")){
            menuHideAll=true;
            invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again
            arrTicket=crudLocaldb.Ticket_getOpenTicketByUser(LOGIN_NAME);
            if (arrTicket.size()>0) {
                arrTicketDetail = crudLocaldb.TicketDetail_getTicketDetailByParentRkey(arrTicket.get(0).getRkey());
                if (arrTicketDetail.size()>0){
                    customadapterDataDetail.addAll(arrTicketDetail);
                }else{
                    TicketDetail td=new TicketDetail();
                    td.setForuser(LOGIN_NAME);
                    td.setNgayps(getCurrentDate());
                    td.setNotes("Do admin lười...  ♥‿<");
                    td.setAmount("Vẫn chưa update");
                    customadapterDataDetail.add(td);
                }

                aedtUsername.setFocusable(false);
                aedtUsername.setFocusableInTouchMode(false);
            }else{
                Toast.makeText(this, "Ticket chưa được kích hoạt, sync lại để kích hoạt ticket", Toast.LENGTH_SHORT).show();
            }

            spnUser.setVisibility(View.GONE);

        }else{
            arrTicket=  crudLocaldb.Ticket_getAllTicket();
            //pull from server ******************************************************
            final String BASE_URL= MY_SERVER + "/dqpclient/user";
            restfullAPI.getUser  task = new restfullAPI.getUser();
            task.setUpdateListener(new restfullAPI.getUser.OnUpdateListener(){
                @Override
                public void onUpdate(ArrayList<Users> obj) {
                    if (obj!=null){
                        arrUser=obj;
                        for (int i=0; i<arrUser.size();i++){
                            listUser.add(arrUser.get(i).getEmail());
                        }
                    }else{
                       listUser=crudLocaldb.Users_listAllUsers();
                    }
                    updateUserSpiner();
                }
            });
            task.execute(BASE_URL);

            spnUser.setVisibility(View.VISIBLE);
            ArrayList<String> DSUser=crudLocaldb.Ticket_getListUser();
            DSUser.add("Tất cả...");
            ArrayAdapter<String> adapterUser=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,DSUser);
            spnUser.setAdapter(adapterUser);
            spnUser.setSelection(DSUser.size()-1);
        }

        customadapterData.addAll(arrTicket);
    }

    private void updateUserSpiner(){
        // cho autocopletwe
        adapterUser = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listUser);
        aedtUsername.setAdapter(adapterUser);
    }



    private void addControls() {
        tvIdTicket=findViewById(R.id.tv_IdTicket);
        tvRkeyTicket=findViewById(R.id.tv_RkeyTicket);
        tvServerKey=findViewById(R.id.tv_TicketServerkey);
        edtAmount=findViewById(R.id.edt_Amount);
        aedtUsername=findViewById(R.id.aedt_TicketUsername);
        edtOpendate=findViewById(R.id.edt_TicketOpendate);
        edtClosedate=findViewById(R.id.edt_TicketClosedate);
        chkStatus=findViewById(R.id.chk_TicketStatus);
        edtUsed=findViewById(R.id.edt_TicketUsed);
        lyUsed=findViewById(R.id.ly_TicketUsed);
        edtReturn=findViewById(R.id.edt_TicketReturn);
        lyReturn=findViewById(R.id.ly_TicketReturn);
        lvTicket=findViewById(R.id.lv_Ticket);
        tvSum1=findViewById(R.id.tv_TickSum1);
        edtSum2=findViewById(R.id.edt_TickSum2);
        edtLydo=findViewById(R.id.edt_TicketLydo);
        spnUser=findViewById(R.id.spn_TicketUserName);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addEvents() {
        edtOpendate.setOnClickListener(new MyEvent());
        edtClosedate.setOnClickListener(new MyEvent());
        //edtAmount.addTextChangedListener(new NumberTextWatcher(edtAmount));
        //edtReturn.addTextChangedListener(new NumberTextWatcher(edtReturn));
        edtAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    edtAmount.setText(formatNumber(edtAmount.getText()+""));
                }
            }
        });
        edtReturn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    edtReturn.setText(formatNumber(edtReturn.getText()+""));
                }
            }
        });
        edtUsed.addTextChangedListener(new NumberTextWatcher(edtUsed));
        edtSum2.addTextChangedListener(new NumberTextWatcher(edtSum2));
        edtOpendate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edtOpendate.setFocusableInTouchMode(true);
                edtOpendate.setFocusable(true);
                edtOpendate.requestFocus();
                return true;
            }
        });
        edtClosedate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edtClosedate.setFocusableInTouchMode(true);
                edtClosedate.setFocusable(true);
                edtClosedate.requestFocus();
                return true;
            }
        });

        aedtUsername.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edit_mode=="NEW"){
                    ArrayList<Ticket> arrTicket =new ArrayList<>();
                    arrTicket=crudLocaldb.Ticket_getOpenTicketByUser(s.toString());
                    if (arrTicket.size()>0){
                        aedtUsername.removeTextChangedListener(this);

                        Toast.makeText(TicketActivity.this, "Vẫn còn Ticket đang mở cho user này...", Toast.LENGTH_SHORT).show();
                        Ticket ticket = new Ticket();
                        ticket=arrTicket.get(0);
                        tvIdTicket.setText(ticket.getId()+"");
                        tvRkeyTicket.setText(String.valueOf(ticket.getRkey()));
                        tvServerKey.setText(String.valueOf(ticket.getServerkey()));
                        edtAmount.setText(formatNumber(ticket.getAmount()));
                        edtUsed.setText(ticket.getUsed());
                        aedtUsername.setText(ticket.getUsername());
                        long [] resul=crudLocaldb.Ticket_getAmountInfo(ticket.getRkey());
                        if (resul[0]-resul[1]!=0){
                            if (resul[0]-resul[1]<0){
                                tvSum1.setText("Cty nợ: ");
                                edtSum2.setText(String.valueOf(resul[1]-resul[0]));
                                edtSum2.setTextColor(Color.parseColor("#3F51B5"));
                            }else{
                                tvSum1.setText("Nợ Cty: ");
                                edtSum2.setText(String.valueOf(resul[0]-resul[1]));
                                edtSum2.setTextColor(Color.parseColor("#F44336"));
                            }
                        }else{
                            tvSum1.setText("Tồn nợ: ");
                            edtSum2.setText("0");
                            edtSum2.setTextColor(Color.BLACK);
                        }
                        edtOpendate.setText(ticket.getOpendate());
                        edtLydo.setText(ticket.getLydo());
                        edtReturn.setText(formatNumber(ticket.getComeback()));
                        edtClosedate.setText(ticket.getClosedate());
                        if (ticket.getFinished()==1){
                            chkStatus.setChecked(true);
                        }else{
                            chkStatus.setChecked(false);
                        }
                        CheckEdit();
                        aedtUsername.addTextChangedListener(this);
                    }
                }
            }
        });

        aedtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    // on focus off
                    String str = aedtUsername.getText().toString();

                    try {
                        ListAdapter listAdapter = aedtUsername.getAdapter();
                        for(int i = 0; i < listAdapter.getCount(); i++) {
                            String temp = listAdapter.getItem(i).toString();
                            if(str.compareTo(temp) == 0) {
                                ArrayList<String> arrTen=crudLocaldb.Ticket_getListUserOpenTicket();
                                return;
                            }
                        }

                        aedtUsername.setText("");
                    }catch(Exception e){
                        e.printStackTrace();
                    }


                }
            }
        });

        spnUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!LOGIN_NAME.substring(0,5).equals("admin")){
                    return;
                }
                arrTicket=crudLocaldb.Ticket_getAllTicketByUserName(spnUser.getAdapter().getItem(position).toString());
                customadapterData.clear();
                customadapterData.addAll(arrTicket);
                customAdapter.notifyDataSetChanged();
                gotoTicket(arrTicket.size()-1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lvTicket.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mGestureDetector.onTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
            }
        });

    }
    private boolean checkChi(){
        ArrayList<Chi>arrChi=new ArrayList<>();
        arrChi=crudLocaldb.Chi_getChiByTicket(longGet(tvRkeyTicket.getText()+""));
        ArrayList<DebtBook>arrDebtBook=new ArrayList<>();
        arrDebtBook=crudLocaldb.DebtBook_getDebtBookByTicket(longGet(tvRkeyTicket.getText()+""));
        ArrayList<TicketDetail>arrTicketDetail=new ArrayList<>();
        arrTicketDetail=crudLocaldb.TicketDetail_getTicketDetailByParentRkey(longGet(tvRkeyTicket.getText()+""));
        if (arrChi.size()>0 || arrDebtBook.size()>0 || arrTicketDetail.size()>0) {
            return true;

        }else{
            return false;

        }
    }


    private void gotolistChi() {
            ArrayList<Chi> arrChi=new ArrayList<>();
            ArrayList<DebtBook> arrDebtBook=new ArrayList<>();
            Intent intent;
            arrChi=crudLocaldb.Chi_getChiByTicket(longGet(tvRkeyTicket.getText()+""));
            arrDebtBook=crudLocaldb.DebtBook_getDebtBookByTicket(longGet(tvRkeyTicket.getText()+""));
            if (arrDebtBook.size()>0 || arrChi.size()>0){
                intent = new Intent(TicketActivity.this, ChiByTicketActivity.class);
                intent.putExtra("rkeyTicket", longGet(tvRkeyTicket.getText() + ""));
                intent.putExtra("userName", LOGIN_NAME);
                startActivityForResult(intent, REQUEST_START_CHI);
            }else{
                Toast.makeText(this, "Chưa có chi tiết phát sinh", Toast.LENGTH_SHORT).show();
            }
    }

    private void gotoTicketDetail(){
            Intent intent = new Intent(TicketActivity.this, TicketDetailActivity.class);
            intent.putExtra("rkeyTicket", longGet(tvRkeyTicket.getText() + ""));
            intent.putExtra("userName", LOGIN_NAME);
            intent.putExtra("finished",chkStatus.isChecked());
            if (longGet(edtAmount.getText()+"")==0 && !chkStatus.isChecked()){
                intent.putExtra("makeNew",true);
            }
            startActivityForResult(intent, REQUEST_START_TICKET_DETAIL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_START_CHI ) {
            boolean needRefresh = data.getBooleanExtra("needRefresh",false);
            // Refresh ListView
            if(needRefresh) {
                if (!LOGIN_NAME.substring(0,5).equals("admin")){
                    arrTicket=crudLocaldb.Ticket_getOpenTicketByUser(LOGIN_NAME);
                }else{
                    arrTicket=  crudLocaldb.Ticket_getAllTicket();
                }
                int position = -1;
                for (int j = 0; j < arrTicket.size(); j++) {
                    if (arrTicket.get(j).getRkey() ==longGet(tvRkeyTicket.getText()+"")) {
                        position = j;
                        gotoRec(position);
                        break;  // uncomment to get the first instance
                    }
                }
                updateListTicket();
                ////doSync("u");
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_START_TICKET_DETAIL ) {
            boolean needRefresh = data.getBooleanExtra("needRefresh",false);
            // Refresh ListView
            if(needRefresh) {
                if (!LOGIN_NAME.substring(0,5).equals("admin")){
                    arrTicket=crudLocaldb.Ticket_getOpenTicketByUser(LOGIN_NAME);
                    lvTicket.setAdapter(customAdapterDetail);
                }else{
                    arrTicket=  crudLocaldb.Ticket_getAllTicket();
                    lvTicket.setAdapter(customAdapter);
                }

                int position = -1;
                for (int j = 0; j < arrTicket.size(); j++) {
                    if (arrTicket.get(j).getRkey() ==longGet(tvRkeyTicket.getText()+"")) {
                        position = j;
                        gotoRec(position);
                        break;  // uncomment to get the first instance
                    }
                }
                updateListTicket();
                ////doSync("u");
            }
        }
    }

    private class MyEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.edt_TicketOpendate:
                    showDatePickerDialog(edtOpendate);
                    break;
                case R.id.edt_TicketClosedate:
                    showDatePickerDialog(edtClosedate);
                    break;

            }
        }
    }

    public void showDatePickerDialog(final EditText edtViewDate) {
        hideSoftKeyboard(TicketActivity.this);
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
        if (!utils.isDate(utils.getEditText(TicketActivity.this,edtViewDate))){
            //Dinh dang lai kieu ngay hien tai
            cal= Calendar.getInstance();
            SimpleDateFormat dft=new SimpleDateFormat("dd/MM/yyyy");
            //gan ngay thang hien tai da dc dinh dang cho s
            s=dft.format(cal.getTime());
        }else {
            s=utils.getEditText(TicketActivity.this,edtViewDate);
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
        Ticket ticket=new Ticket();
        String username=getEditText(aedtUsername);
        Boolean cons=false;
        if(username.equals("")) {
            Toast.makeText(getApplicationContext(), "Cần nhập vào Tên người dùng", Toast.LENGTH_SHORT).show();
            return;
        }
        long amount,used,res;
            amount = longGet(this, edtAmount);
            used = longGet(this, edtUsed);
            res = longGet(this, edtReturn);
        ticket.setAmount(amount+"");
        ticket.setUsed(used+"");
        ticket.setComeback(res+"");
        ticket.setOpendate(getEditText(edtOpendate));
        ticket.setClosedate(getEditText(edtClosedate));
        ticket.setLydo(getEditText(edtLydo));
        ticket.setUsername(username);
        if (chkStatus.isChecked()){
            ticket.setFinished(1);
        }else{
            ticket.setFinished(0);
        }
        ticket.setUpdatetime(getCurrentTimeMiliS());
        if (edit_mode=="NEW" && longGet(tvRkeyTicket.getText()+"")==0) {
            //chuyenbien.set;);= new ChuyenBien(tenchuyenbien,tentau,ngaykhoihanh);
            ticket.setServerkey(0);
            ticket.setRkey(longGet(getCurrentTimeMiliS()));
            if (ticket != null) {
                long i = crudLocaldb.Ticket_addTicket(ticket);
                if (i !=-1) {
                    setEditMod(false);
                    ////doSync("a");
                    updateListTicket();
                }
            }
            //cap nhat lai danh sach chuyen bien vi da co them record moi vao

            //}
        }else{
            if (edit_mode=="EDIT") {
                ticket.setRkey(longGet(String.valueOf(tvRkeyTicket.getText())));
                ticket.setId(intGet(tvIdTicket.getText()+""));
                ticket.setServerkey(intGet(String.valueOf(tvServerKey.getText())));
                int result = crudLocaldb.Ticket_updateTicket(ticket);
                if (result > 0) {
                    updateListTicket();
                    setEditMod(false);
                }
            }
        }
    }
    private void gotoRec(int position){
        if (!LOGIN_NAME.substring(0,5).equals("admin")){
            arrTicket=crudLocaldb.Ticket_getOpenTicketByUser(LOGIN_NAME);
        }else{
            arrTicket=  crudLocaldb.Ticket_getAllTicket();
        }
        if (arrTicket.size() >= 1) { //phong truong hop null k co record nao
            Ticket ticket;
            ticket = arrTicket.get(position);
            tvIdTicket.setText(ticket.getId()+"");
            tvRkeyTicket.setText(ticket.getRkey()+"");
            tvServerKey.setText(ticket.getServerkey()+"");
            edtAmount.setText(formatNumber(ticket.getAmount()));
            edtUsed.setText(ticket.getUsed());
            aedtUsername.setText(ticket.getUsername());
            edtOpendate.setText(ticket.getOpendate());
            edtLydo.setText(ticket.getLydo());
            edtClosedate.setText(ticket.getClosedate());
            edtReturn.setText(formatNumber(ticket.getComeback()));
            if (ticket.getFinished()==1){
                chkStatus.setChecked(true);
            }else{
                chkStatus.setChecked(false);
            }

            long [] resul=crudLocaldb.Ticket_getAmountInfo(ticket.getRkey());
            if (resul[0]-resul[1]!=0){
                if (resul[0]-resul[1]<0){
                    tvSum1.setText("Cty nợ: ");
                    edtSum2.setText(String.valueOf(resul[1]-resul[0]));
                    edtSum2.setTextColor(Color.parseColor("#3F51B5"));
                }else{
                    tvSum1.setText("Nợ Cty: ");
                    edtSum2.setText(String.valueOf(resul[0]-resul[1]));
                    edtSum2.setTextColor(Color.parseColor("#F44336"));
                }
            }else{
                tvSum1.setText("Tồn nợ: ");
                edtSum2.setText("0");
                edtSum2.setTextColor(Color.BLACK);
            }
            setEditMod(false);
        }
    }

    private void CheckAddNew(){
        tvRkeyTicket.setText("");
        tvIdTicket.setText("");
        tvServerKey.setText("");
        edtAmount.setText("");
        edtUsed.setText("");
        aedtUsername.setText("");
        edtOpendate.setText("");
        edtLydo.setText("");
        edtClosedate.setText("");
        edtReturn.setText("");
        chkStatus.setChecked(false);
        edit_mode="NEW";
        setEditMod(true);

    }

    private void CheckEdit(){
        edit_mode="EDIT";
        setEditMod(true);
    }

    private void CheckDelete(Ticket dt) {
        if (dt.getServerkey()!=0){
            WantDeleteFromServer wdfs=new WantDeleteFromServer();
            wdfs.setmServerkey(dt.getServerkey());
            wdfs.setmTablename("ticket");
            crudLocaldb.WDFS_addWDFS(wdfs);
        }
        crudLocaldb.Ticket_deleteTicket(dt.getRkey());
        //remove from array list
        Predicate<Ticket> personPredicate = p-> p.getId() == dt.getId();
        arrTicket.removeIf(personPredicate);
        //refesh screen
        tvRkeyTicket.setText("");
        tvIdTicket.setText("");
        tvServerKey.setText("");
        edtAmount.setText("");
        edtUsed.setText("");
        edtReturn.setText("");
        aedtUsername.setText("");
        edtOpendate.setText("");
        edtLydo.setText("");
        edtClosedate.setText("");
        chkStatus.setChecked(false);
        edtAmount.requestFocus();

        // Refresh ListView.
        updateListTicket();
    }

    private void setAdapter() {
        if (customAdapter == null) {
            // gan data source cho adapter
            customAdapter = new CustomAdapterTicket(TicketActivity.this, R.layout.customlist_ticket,customadapterData);
            customAdapterDetail=new CustomAdapterTicketDetail(TicketActivity.this,R.layout.customlist_ticketdetail,customadapterDataDetail);
            //gan adapter cho spinner
            if (!LOGIN_NAME.substring(0,5).equals("admin")){
                lvTicket.setAdapter(customAdapterDetail);
            }else{
                lvTicket.setAdapter(customAdapter);
            }

        }else{
            updateListTicket();
            if (LOGIN_NAME.substring(0,5).equals("admin")){
                lvTicket.setSelection(customAdapter.getCount()-1);
            }
        }
    }
    //gett all to list
    public void updateListTicket(){
        if (!LOGIN_NAME.substring(0,5).equals("admin")){
            arrTicket=crudLocaldb.Ticket_getOpenTicketByUser(LOGIN_NAME);
            arrTicketDetail=crudLocaldb.TicketDetail_getTicketDetailByParentRkey(arrTicket.get(0).getRkey());
            customadapterDataDetail.clear();
            customadapterDataDetail.addAll(arrTicketDetail);
            customAdapterDetail.notifyDataSetChanged();
        }else{
            arrTicket=  crudLocaldb.Ticket_getAllTicket();
            customadapterData.clear();
            customadapterData.addAll(arrTicket);
            customAdapter.notifyDataSetChanged();
        }

    }

    private  void initData(){
        if (arrTicket.size()>=1) { //phong truong hop null k co record nao
            Ticket ticket =arrTicket.get(arrTicket.size()-1);
            lvTicket.setSelection(arrTicket.size()-1);
            tvRkeyTicket.setText(ticket.getRkey()+"");
            tvIdTicket.setText(ticket.getId()+"");
            tvServerKey.setText(ticket.getServerkey()+"");
            edtAmount.setText(formatNumber(ticket.getAmount()));
            edtUsed.setText(ticket.getUsed());
            edtReturn.setText(formatNumber(ticket.getComeback()));
            aedtUsername.setText(ticket.getUsername());
            edtOpendate.setText(ticket.getOpendate());
            edtLydo.setText(ticket.getLydo());
            edtClosedate.setText(ticket.getClosedate());
            if (ticket.getFinished()==1){
                chkStatus.setChecked(true);
            }else{
                chkStatus.setChecked(false);
            }
            long [] resul=crudLocaldb.Ticket_getAmountInfo(ticket.getRkey());
            if (resul[0]-resul[1]!=0){
                if (resul[0]-resul[1]<0){
                    tvSum1.setText("Cty nợ: ");
                    edtSum2.setText(String.valueOf(resul[1]-resul[0]));
                    edtSum2.setTextColor(Color.parseColor("#3F51B5"));
                }else{
                    tvSum1.setText("Nợ Cty: ");
                    edtSum2.setText(String.valueOf(resul[0]-resul[1]));
                    edtSum2.setTextColor(Color.parseColor("#F44336"));
                }
            }else{
                tvSum1.setText("Tồn nợ: ");
                edtSum2.setText("0");
                edtSum2.setTextColor(Color.BLACK);
            }
            setEditMod(false);
        }
        if(!IS_ADMIN){
            lyReturn.setVisibility(View.GONE);
            lyUsed.setVisibility(View.GONE);
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
            aedtUsername.setThreshold(1);
        }else {
            mShowMenuSave=0;
            aedtUsername.setThreshold(1000);
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
