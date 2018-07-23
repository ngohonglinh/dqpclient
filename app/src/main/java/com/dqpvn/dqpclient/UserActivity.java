package com.dqpvn.dqpclient;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dqpvn.dqpclient.R;
import com.dqpvn.dqpclient.crudmanager.SyncCheck;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.restfullAPI;
import com.dqpvn.dqpclient.customadapters.CustomAdapterUser;
import com.dqpvn.dqpclient.models.ResponseFromServer;
import com.dqpvn.dqpclient.models.Ticket;
import com.dqpvn.dqpclient.models.Users;
import com.dqpvn.dqpclient.models.WantDeleteFromServer;
import com.dqpvn.dqpclient.utils.MyContextWrapper;
import com.dqpvn.dqpclient.utils.utils;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.dqpvn.dqpclient.crudmanager.SyncCheck.MY_SERVER;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.IS_ADMIN;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_NAME;
import static com.dqpvn.dqpclient.utils.utils.comPare;
import static com.dqpvn.dqpclient.utils.utils.getCurrentTimeMiliS;
import static com.dqpvn.dqpclient.utils.utils.hideSoftKeyboard;
import static com.dqpvn.dqpclient.utils.utils.intGet;
import static com.dqpvn.dqpclient.utils.utils.isBad;
import static com.dqpvn.dqpclient.utils.utils.longGet;

public class UserActivity extends AppCompatActivity {

    final private String TAG= getClass().getSimpleName();
    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 250;
    private static final int SWIPE_MAX_OFF_PATH = 500;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;
    private int lvPossition=-1;


    final String SHARED_PREFERENCES_NAME="dqpclient_preferences";
    private SharedPreferences sharedpre;

    private int mShowMenuSave=0;
    private final int REQUEST_START_TICKET=123;
    private TextView tvServerKey, tvRkey;
    private EditText edtFullName, edtHonourname, edtLogin, edtPassword, edtCtyNo, edtNoCty;
    private ListView lvUsers;
    private CheckBox chk_Admin;
    //Khai báo Datasource lưu trữ danh sách doi tac
    private ArrayList<Users>arrUser=new ArrayList<>();
    private ArrayList<Users>customadapterData=new ArrayList<>();
    private crudLocal crudLocaldb=crudLocal.getInstance(this);
    //Khai báo ArrayAdapter cho ListView
    private CustomAdapterUser customAdapter;
    //Theo gioi cho phép Nhap lieu
    private String edit_mode="VIEW";
    String BASE_URL;


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
            lvPossition=lvUsers.pointToPosition( (int) x, (int) y );
            if (lvPossition<0){return true;}
            Users user = new Users();
            user=arrUser.get(lvPossition);
            tvServerKey.setText(String.valueOf(user.getServerkey()));
            tvRkey.setText(String.valueOf(user.getRkey()));
            edtFullName.setText(user.getFullname());
            edtHonourname.setText(user.getHonourname());
            edtLogin.setText(user.getEmail());
            edtPassword.setText(user.getPassword());
            edtCtyNo.setText(user.getCtyno());
            edtNoCty.setText(user.getNocty());
            if (user.getAdmin()==1){
                chk_Admin.setChecked(true);
            }else{
                chk_Admin.setChecked(false);
            }
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
            lvPossition=lvUsers.pointToPosition( (int) e1.getX(), (int) e1.getY() );
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (lvPossition<0){
                    return true;
                }
                gotoTicket();

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
                if (arrUser.size()==0){
                    return true;
                }
                CheckEdit();
                return true;
            case R.id.id_delete :
                //wrtite ơn logic
                if (arrUser.size()==0){
                    return true;
                }
                String emailLogin=edtLogin.getText()+"";
                if (emailLogin.substring(0,5).equals("admin")){
                    Toast.makeText(this, "Account: " + emailLogin + " không thể xóa", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (checkTicket()){
                    Toast.makeText(this, "Không thể xóa vì đang có chi tiết liên quan", Toast.LENGTH_SHORT).show();
                    return true;
                }

                //lay ra index hien tai cua listview
                if (lvPossition==-1){
                    Toast.makeText(this, "Chưa chọn đúng dữ liệu cần xóa", Toast.LENGTH_SHORT).show();
                }else {
                    final Users user =arrUser.get(lvPossition);
                    // Hỏi trước khi xóa.
                    new AlertDialog.Builder(this)
                            .setTitle("DQP Client")
                            .setMessage(user.getEmail()+ "\n\n"+ "Có chắc xóa?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    CheckDelete(user.getServerkey());
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
                return true;
            case R.id.save:
                try {
                    UpdateUser();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hideSoftKeyboard(UserActivity.this);
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
        setContentView(R.layout.activity_user);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        mGestureDetector.setOnDoubleTapListener(customGestureDetector);

        initialization();
        addControls();
        addEvents();
        try {
            setAdapter();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        initData();
    }

    private  void initData(){
        if (arrUser.size()>=1) { //phong truong hop null k co record nao
            Users user =arrUser.get(arrUser.size()-1);
            lvUsers.setSelection(arrUser.size()-1);
            tvServerKey.setText(user.getServerkey()+"");
            tvRkey.setText(String.valueOf(user.getRkey()));
            edtFullName.setText(user.getFullname());
            edtHonourname.setText(user.getHonourname());
            edtLogin.setText(user.getEmail());
            edtPassword.setText(user.getPassword());
            edtCtyNo.setText(user.getCtyno());
            edtNoCty.setText(user.getNocty());
            if (user.getAdmin()==1){
                chk_Admin.setChecked(true);
            }else{
                chk_Admin.setChecked(false);
            }
            setEditMod(false);
        }

    }

    private void addEvents() {
        lvUsers.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                 mGestureDetector.onTouchEvent(event);
                return mGestureDetector.onTouchEvent(event);
            }
        });

        chk_Admin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!comPare(tvServerKey.getText()+"","")){
                    //wrtite ơn logic
                    CheckEdit();
                }

            }
        });


    }

    private void UpdateUser() throws ExecutionException, InterruptedException {
        Users user=new Users();
        if(isBad(edtLogin.getText()+"")) {
            Toast.makeText(getApplicationContext(), "Cần nhập vào Login Name", Toast.LENGTH_SHORT).show();
            return;
        }
        user.setFullname(edtFullName.getText()+"");
        user.setHonourname(edtHonourname.getText()+"");
        user.setEmail(edtLogin.getText()+"");
        user.setPassword(edtPassword.getText()+"");
        user.setUpdatetime(getCurrentTimeMiliS());
        if (chk_Admin.isChecked()){
            user.setAdmin(1);
        }else{
            user.setAdmin(0);
        }
        if (edit_mode=="NEW" && intGet(tvServerKey.getText()+"")==0) {
            user.setServerkey(0);
            user.setRkey(0);
            if (user != null) {
                ResponseFromServer res =new restfullAPI.postUser(user).execute(BASE_URL + "/create/").get();
                if (res!=null){
                    if (res.getStatus()==0){
                        user.setServerkey(res.getServerkey());
                        user.setRkey(res.getServerkey());
                        user.setUpdatetime(String.valueOf(longGet(getCurrentTimeMiliS())+1));
                        ResponseFromServer res2 =new restfullAPI.putUser(user).execute(BASE_URL + "/update/"+ res.getServerkey() +"/").get();
                        if (res2!=null){
                            if (res2.getStatus()==0){
                                setEditMod(false);
                                updateListUser();
                            }
                        }
                    }
                }
            }
        }else{
            if (edit_mode=="EDIT") {
                user.setServerkey(intGet(String.valueOf(tvServerKey.getText())));
                user.setRkey(longGet(String.valueOf(tvRkey.getText())));
                ResponseFromServer res =new restfullAPI.putUser(user).execute(BASE_URL + "/update/"+ user.getServerkey() +"/").get();
                if (res!=null){
                    if (res.getStatus()==0){
                        setEditMod(false);
                        updateListUser();
                        //doSync("u");
                    }
                }
            }
        }
    }
    private void gotoRec(int position) throws ExecutionException, InterruptedException {
        arrUser=new restfullAPI.getUser().execute(BASE_URL).get();
        if (arrUser.size() >= 1) { //phong truong hop null k co record nao
            Users user;
            user = arrUser.get(position);
            tvServerKey.setText(user.getServerkey()+"");
            tvRkey.setText(String.valueOf(user.getRkey()));
            edtFullName.setText(user.getFullname());
            edtHonourname.setText(user.getHonourname()+"");
            edtLogin.setText(user.getEmail());
            edtPassword.setText(user.getPassword());
            edtNoCty.setText(user.getNocty());
            edtCtyNo.setText(user.getCtyno());
            if (user.getAdmin()==1){
                chk_Admin.setChecked(true);
            }else{
                chk_Admin.setChecked(false);
            }
            setEditMod(false);
        }
    }

    private  void setEditMod(boolean chohaykhong){
        if (chohaykhong==true){
            mShowMenuSave=1;
        }else {
            mShowMenuSave=0;
            edit_mode="VIEW";
        }
        invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again
    }

    private void CheckAddNew(){
        tvServerKey.setText("");
        tvRkey.setText("");
        edtFullName.setText("");
        edtHonourname.setText("");
        edtPassword.setText("");
        edtLogin.setText("");
        edtCtyNo.setText("");
        edtNoCty.setText("");
        chk_Admin.setChecked(false);


        edit_mode="NEW";
        setEditMod(true);

    }

    private void CheckEdit(){
        edit_mode="EDIT";
        setEditMod(true);
    }

    private void CheckDelete(int Serverkey) {
        try {
            ResponseFromServer res=new restfullAPI.deleteUser().execute(BASE_URL+ "/delete/" + Serverkey + "/").get();
            if (res!=null){
                if (res.getStatus()==0){
                    tvServerKey.setText("");
                    tvRkey.setText("");
                    edtFullName.setText("");
                    edtHonourname.setText("");
                    edtPassword.setText("");
                    edtLogin.setText("");
                    edtCtyNo.setText("");
                    edtNoCty.setText("");
                    chk_Admin.setChecked(true);
                    chk_Admin.setChecked(false);

                    updateListUser();
                    //doSync("d");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    private boolean checkTicket(){
        ArrayList<Ticket>arrTicket=new ArrayList<>();
        arrTicket=crudLocaldb.Ticket_getOpenTicketByUser(edtLogin.getText()+"");
        if (arrTicket.size()>0) {
            return true;
        }else{
            return false;

        }
    }

    private void gotoTicket() {
        ArrayList<Ticket> arrTicket=new ArrayList<>();
        Intent intent;
        intent = new Intent(UserActivity.this, TicketActivity.class);
        intent.putExtra("userName", edtLogin.getText()+"");
        arrTicket=crudLocaldb.Ticket_getOpenTicketByUser(edtLogin.getText()+"");
        if (arrTicket.size()==0){
            intent.putExtra("makeNew", true);
        }
        startActivityForResult(intent, REQUEST_START_TICKET);
    }

    private void addControls() {
        tvServerKey=findViewById(R.id.tv_UserServerkey);
        tvRkey=findViewById(R.id.tv_Userrkey);
        edtCtyNo=findViewById(R.id.edt_UserCtyNo);
        edtNoCty=findViewById(R.id.edt_UserNoCty);
        edtFullName=findViewById(R.id.edt_UserFullName);
        edtHonourname=findViewById(R.id.edt_UserHonourname);
        edtLogin=findViewById(R.id.edt_UserLoginName);
        edtPassword=findViewById(R.id.edt_UserPassword);
        lvUsers=findViewById(R.id.lv_UserUser);
        chk_Admin=findViewById(R.id.chk_UsersAdmin);
    }

    private void initialization() {
        customadapterData.addAll(arrUser);
        sharedpre = getSharedPreferences(SHARED_PREFERENCES_NAME, this.MODE_PRIVATE);
        MY_SERVER=sharedpre.getString("Server","");
        BASE_URL= MY_SERVER + "/dqpclient/user";
        restfullAPI.getUser  task = new restfullAPI.getUser();
        task.setUpdateListener(new restfullAPI.getUser.OnUpdateListener(){
            @Override
            public void onUpdate(ArrayList<Users> obj) throws ExecutionException, InterruptedException {
                if (obj!=null){
                    arrUser=obj;
                    customadapterData.addAll(arrUser);
                    setAdapter();
                }
            }
        });
        task.execute(BASE_URL);

    }

    private void setAdapter() throws ExecutionException, InterruptedException {
        if (customAdapter == null) {
            // gan data source cho adapter
            customAdapter = new CustomAdapterUser(UserActivity.this, R.layout.customlist_user,customadapterData);
            //gan adapter cho spinner
            lvUsers.setAdapter(customAdapter);
        }else{
            updateListUser();
            lvUsers.setSelection(customAdapter.getCount()-1);
        }
    }
    //gett all to list
    public void updateListUser() throws ExecutionException, InterruptedException {
        customadapterData.clear();
        arrUser=new restfullAPI.getUser().execute(BASE_URL).get();
        customadapterData.addAll(arrUser);
        customAdapter.notifyDataSetChanged();
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
