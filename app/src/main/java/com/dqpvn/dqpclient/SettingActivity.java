package com.dqpvn.dqpclient;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dqpvn.dqpclient.crudmanager.restfullAPI;
import com.dqpvn.dqpclient.models.ResponseFromServer;
import com.dqpvn.dqpclient.models.Users;
import com.dqpvn.dqpclient.utils.utils;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_NAME;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_OK;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_PASSWORD;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.MY_SERVER;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.SHARED_PREFERENCES_NAME;
import static com.dqpvn.dqpclient.utils.utils.comPare;
import static com.dqpvn.dqpclient.utils.utils.getCurrentDate;
import static com.dqpvn.dqpclient.utils.utils.getCurrentTimeMiliS;
import static com.dqpvn.dqpclient.utils.utils.getEditText;
import static com.dqpvn.dqpclient.utils.utils.hideSoftKeyboard;
import static com.dqpvn.dqpclient.utils.utils.isBad;


public class SettingActivity extends AppCompatActivity {
    private EditText edtLogin, edtPassWord, edtTuNgay, edtServer, edtNewPass, edtRepeatNewPass;
    private Button btnChangePass;
    private Calendar cal;
    private Boolean needRefresh=false, changePass=false;
    private TextView tvNewPass, tvRepeatNewPass;
    private String BASE_URL;
    final private String TAG = getClass().getSimpleName();

    //lam viec voi menu
    @Override
    public boolean onCreateOptionsMenu(Menu manu) {
        getMenuInflater().inflate(R.menu.save_setting, manu);
        // return true so that the menu pop up is opened
        return true;
    }

    // Method này sử lý sự kiện khi MenuItem được chọn.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId)  {
            //ten cua cac id khi thiet ke cac resource menu
            case R.id.saveSetting:
                SaveSetting();
                hideSoftKeyboard(SettingActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Request write SD Card permision to store Image file.
        utils.checkAndRequestPermissions(this);

        addControls();
        try {
            initData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        addEvents();
    }

    private  void initData() throws ParseException {
        //Check Shared Preferences
        File filePre = new File("/data/data/"+ this.getApplicationContext().getPackageName()+ "/shared_prefs/"+ SHARED_PREFERENCES_NAME +".xml");
        if (filePre.exists()) {
            SharedPreferences sharedpre = getSharedPreferences(SHARED_PREFERENCES_NAME, this.MODE_PRIVATE);
            MY_SERVER=sharedpre.getString("Server","");
            BASE_URL= MY_SERVER + "/dqpclient/user";
            edtServer.setText(sharedpre.getString("Server",""));
            edtLogin.setText(sharedpre.getString("LoginName",""));
            //edtPassWord.setText(sharedpre.getString("Password",""));
            LOGIN_PASSWORD=sharedpre.getString("Password","");
            Date mydate=new Date(sharedpre.getLong("TuNgay",0));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String s=sdf.format(mydate);
            edtTuNgay.setText(s);
        }else{
            if (isBad(edtTuNgay.getText()+"")){
                edtTuNgay.setText(getCurrentDate());
            }
        }
    }

    private void SaveSetting(){
        if (utils.getEditText(SettingActivity.this,edtServer).equals("")){
            Toast.makeText(SettingActivity.this, "Không thể để trống tên server", Toast.LENGTH_SHORT).show();
            return;
        }else{
            String s[]=utils.getEditText(SettingActivity.this,edtServer).split(":");
            if (s.length<3){
                Toast.makeText(SettingActivity.this, "Tên server có gì đó sai sai. :(", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (isBad(edtTuNgay.getText()+"")){
            edtTuNgay.setText(getCurrentDate());
        }
        String myDate = utils.DinhDangNgay(utils.getEditText(SettingActivity.this,edtTuNgay),"yyyy/mm/dd")+ " 00:00:00";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String timeTamp=String.valueOf(date.getTime()).substring(0,13);
        long TuNgayMillis = Long.parseLong(timeTamp);

        MY_SERVER=utils.getEditText(SettingActivity.this,edtServer);
        LOGIN_NAME=edtLogin.getText()+"";
        if (changePass){
            if (getEditText(SettingActivity.this,edtNewPass).length()<6){
                Toast.makeText(SettingActivity.this, "Password mới phải ít nhất 6 ký tự.. ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (comPare(getEditText(SettingActivity.this,edtNewPass),getEditText(SettingActivity.this,edtRepeatNewPass))){
                restfullAPI.getUser  task = new restfullAPI.getUser();
                task.setUpdateListener(new restfullAPI.getUser.OnUpdateListener(){
                    @Override
                    public void onUpdate(ArrayList<Users> arrUser) throws ExecutionException, InterruptedException {
                        int serverKey=0;
                        String fullName="",noCty="",ctyNo="", passwd="", honourName="";
                        if (arrUser!=null){
                            for (int i=0;i<arrUser.size();i++){
                                if (arrUser.get(i).getEmail().equals(utils.getEditText(SettingActivity.this,edtLogin))){
                                    serverKey=arrUser.get(i).getServerkey();
                                    fullName=arrUser.get(i).getFullname();
                                    honourName=arrUser.get(i).getHonourname();
                                    noCty=arrUser.get(i).getNocty();
                                    ctyNo=arrUser.get(i).getCtyno();
                                    passwd=arrUser.get(i).getPassword();
                                    break;
                                }
                            }
                            if (!comPare(getEditText(SettingActivity.this,edtPassWord),passwd)){
                                Toast.makeText(SettingActivity.this, "Password hiện tại chưa chính xác", Toast.LENGTH_SHORT).show();
                                return;
                            }else{
                                if (serverKey!=0){
                                    Users user=new Users();
                                    user.setEmail(edtLogin.getText()+"");
                                    user.setPassword(edtNewPass.getText()+"");
                                    user.setUpdatetime(getCurrentTimeMiliS());
                                    user.setServerkey(serverKey);
                                    user.setFullname(fullName);
                                    user.setHonourname(honourName);
                                    user.setNocty(noCty);
                                    user.setCtyno(ctyNo);

                                    ResponseFromServer res =new restfullAPI.putUser(user).execute(BASE_URL + "/update/"+ serverKey +"/").get();
                                    if (res!=null){
                                        if (res.getStatus()==0){
                                            LOGIN_PASSWORD=utils.getEditText(SettingActivity.this,edtNewPass);
                                            Toast.makeText(SettingActivity.this, "Thay đổi Password thành công, hãy ghi nhớ Password mới.", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(SettingActivity.this, "Thay đổi Password thất bại, liên hệ với admin để xử lý lổi này.", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                }
                            }
                        }else{
                            Toast.makeText(SettingActivity.this, "Server chưa hồi đáp... ", Toast.LENGTH_SHORT).show();
                            return;
                        }


                    }
                });
                task.execute(BASE_URL);
            }else{
                Toast.makeText(SettingActivity.this, "Password mới không trùng khớp, xin nhập lại...", Toast.LENGTH_SHORT).show();
                return;
            }

        }else{
            LOGIN_PASSWORD=utils.getEditText(SettingActivity.this,edtPassWord);
        }

        if (networkOK()){
            if (serverOK()){
                if (LoginDaihuuDSF()){
                    Toast.makeText(SettingActivity.this, "Đăng nhập thành công...:)", Toast.LENGTH_SHORT).show();
                    LOGIN_OK=true;
                }else{
                    Toast.makeText(SettingActivity.this, "Username hoặc Pasword không đúng, chưa thể đăng nhập...:(", Toast.LENGTH_SHORT).show();
                    LOGIN_OK=false;
                    return;
                }
            }else{
                Toast.makeText(SettingActivity.this, "Có thể server cùi bắp bị hacker đánh sập rùi. Hãy thhử đăng nhập lại... vài chục lần :)", Toast.LENGTH_SHORT).show();
                LOGIN_OK=false;
                return;
            }
        }else{
            Toast.makeText(SettingActivity.this, "Không có kết nối internet... không làm ăn gì được cả :(", Toast.LENGTH_SHORT).show();
            LOGIN_OK=false;
            return;
        }

        SharedPreferences sharedpre = getSharedPreferences(SHARED_PREFERENCES_NAME, SettingActivity.this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpre.edit();
        editor.putString("Server", utils.getEditText(SettingActivity.this,edtServer));
        editor.putString("LoginName", utils.getEditText(SettingActivity.this,edtLogin));
        editor.putString("Password", LOGIN_PASSWORD);
        editor.putBoolean("LoginOk",LOGIN_OK);
        editor.putLong("TuNgay", TuNgayMillis);
        editor.apply();

        SettingActivity.this.needRefresh=true;

        Boolean loginOk=sharedpre.getBoolean("LoginOk",false);
        if (loginOk){
            SettingActivity.this.finish();
        }else if (!loginOk){
            Toast.makeText(SettingActivity.this, "Quá trình đăng nhập thất bại...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
    private void addEvents() {
        edtServer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edtServer.setFocusable(true);
                edtServer.setFocusableInTouchMode(true);
                edtServer.requestFocus();
                return false;
            }
        });

        edtTuNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(edtTuNgay);
            }
        });

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restfullAPI.getUser  task = new restfullAPI.getUser();
                task.setUpdateListener(new restfullAPI.getUser.OnUpdateListener(){
                    @Override
                    public void onUpdate(ArrayList<Users> arrUser) throws ExecutionException, InterruptedException {
                        int serverKey=0;
                        String passwd="";
                        if (arrUser!=null){
                            for (int i=0;i<arrUser.size();i++){
                                if (arrUser.get(i).getEmail().equals(utils.getEditText(SettingActivity.this,edtLogin))){
                                    passwd=arrUser.get(i).getPassword();
                                    break;
                                }
                            }
                            if (!comPare(passwd,getEditText(SettingActivity.this,edtPassWord))){
                                Toast.makeText(SettingActivity.this, "Password hiện tại chưa chính xác", Toast.LENGTH_SHORT).show();
                                return;

                            }else{
                                if (!changePass){
                                    tvNewPass.setVisibility(View.VISIBLE);
                                    tvRepeatNewPass.setVisibility(View.VISIBLE);
                                    edtNewPass.setVisibility(View.VISIBLE);
                                    edtRepeatNewPass.setVisibility(View.VISIBLE);
                                    edtNewPass.requestFocus();
                                    changePass=true;
                                }else{
                                    tvNewPass.setVisibility(View.GONE);
                                    tvRepeatNewPass.setVisibility(View.GONE);
                                    edtNewPass.setVisibility(View.GONE);
                                    edtRepeatNewPass.setVisibility(View.GONE);
                                    edtPassWord.requestFocus();
                                    changePass=false;
                                }
                            }
                        }else{
                            Toast.makeText(SettingActivity.this, "Server chưa hồi đáp... ", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
                task.execute(BASE_URL);
            }
        });
    }

    private void addControls() {
        edtServer=findViewById(R.id.edt_SettingServerAddress);
        edtLogin=findViewById(R.id.edt_SettingLoginName);
        edtPassWord=findViewById(R.id.edt_SettingPassword);
        edtTuNgay=findViewById(R.id.edt_SettingTuNgay);
        tvNewPass=findViewById(R.id.tv_NewPass);
        tvRepeatNewPass=findViewById(R.id.tv_RepeatNewPass);
        edtNewPass=findViewById(R.id.edt_NewPass);
        edtRepeatNewPass=findViewById(R.id.edt_RepeatNewPass);
        btnChangePass=findViewById(R.id.btn_ChangePass);

        edtServer.setText(SettingActivity.this.getString(R.string.server_address));
    }


    public void showDatePickerDialog(final EditText edtViewDate) {
        hideSoftKeyboard(SettingActivity.this);
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
        if (!utils.isDate(utils.getEditText(SettingActivity.this,edtViewDate))){
            //Dinh dang lai kieu ngay hien tai
            cal= Calendar.getInstance();
            SimpleDateFormat dft=new SimpleDateFormat("dd/MM/yyyy");
            //gan ngay thang hien tai da dc dinh dang cho s
            s=dft.format(cal.getTime());
        }else {
            s=utils.getEditText(SettingActivity.this,edtViewDate);
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

    private Boolean serverOK(){
        Boolean is=false;
        try {
            is= new utils.isServerAvailable().execute(MY_SERVER).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return is;
    }

    private Boolean networkOK(){
        Boolean is=false;
        try {
            is= new utils.isInternetAvailable().execute("https://google.com:80").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return is;
    }

    private Boolean LoginDaihuuDSF(){
        ResponseFromServer res = new ResponseFromServer();
        String [] loginURL=new String[]{MY_SERVER + "/dqpclient/user/login", LOGIN_NAME, LOGIN_PASSWORD};
        try {
            res=new restfullAPI.loginDaiHuuDSF().execute(loginURL).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Boolean is=false;
            if (res.getStatus()==200) {
                is = true;
            }
      return is;
    }

    @Override
    public void finish() {
        // Khi Activity này hoàn thành,
        // có thể cần gửi phản hồi gì đó về cho Activity đã gọi nó.
        // Chuẩn bị dữ liệu Intent.
        Intent data = new Intent();
        // Yêu cầu MainActivity refresh lại ListView hoặc không.
        data.putExtra("needRefresh", needRefresh);
        //int idDoiTac=crudLocal.getIDDoiTac(aedtChiDoiTac.getText()+"");
        //data.putExtra("idDoiTac",idDoiTac);
        // Activity đã hoàn thành OK, trả về dữ liệu.
        this.setResult(Activity.RESULT_OK, data);
        super.finish();
    }

}
