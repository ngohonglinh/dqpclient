package com.dqpvn.dqpclient;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Environment;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dqpvn.dqpclient.crudmanager.updateSoftwareService;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.crudmanager.restfullAPI;
import com.dqpvn.dqpclient.models.ImgStore;
import com.dqpvn.dqpclient.models.Users;
import com.dqpvn.dqpclient.utils.utils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static com.dqpvn.dqpclient.crudmanager.SyncCheck.CHO_PHEP_TRUY_CAP;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.IS_ADMIN;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_NAME;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_OK;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.LOGIN_PASSWORD;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.MY_SERVER;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.NGAY_LUU_ANH;
import static com.dqpvn.dqpclient.crudmanager.SyncCheck.SHARED_PREFERENCES_NAME;
import static com.dqpvn.dqpclient.utils.utils.comPare;
import static com.dqpvn.dqpclient.utils.utils.getEditText;
import static com.dqpvn.dqpclient.utils.utils.intGet;
import static com.dqpvn.dqpclient.utils.utils.isBad;
import static com.dqpvn.dqpclient.utils.utils.longGet;
import static com.dqpvn.dqpclient.utils.utils.readFromFile;
import static com.dqpvn.dqpclient.utils.utils.writeToFile;

public class MainActivity extends AppCompatActivity {
    // Global variables
    private KeyStore keyStore;
    // Variable used for storing the key in the Android Keystore container
    private static final String KEY_NAME = "androidHive";
    private Cipher cipher;
    private TextView tvError;
    private boolean logWithFinger = false;


    final private String TAG = getClass().getSimpleName();
    private int wrongPassTime = 0;
    final int REQUEST_SETTING = 321, REQUEST_NAV_MENU = 222, RC_OVERLAY = 666;
    private EditText edtLogin, edtPassword;
    private TextView tvTryLog;
    private crudLocal crudLocaldb = crudLocal.getInstance(this);
    ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.dqplogo2);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        addControls();
        addEvents();
        crudLocaldb.getDb();

        //Check Shared Preferences
        File filePre = new File("/data/data/" + this.getApplicationContext().getPackageName() + "/shared_prefs/" + SHARED_PREFERENCES_NAME + ".xml");
        if (!filePre.exists()) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivityForResult(intent, REQUEST_SETTING);
            return;
        } else {
            getPre();
            requestInstantSync("wantPull");
        }

        initdata();
        checkUpDate();
        workWithFinger();
    }

    private Boolean networkOK() {
        Boolean is = false;
        try {
            is = new utils.isInternetAvailable().execute("https://google.com:80").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return is;
    }

    private void checkUpDate() {
        Intent serviceIntent = new Intent(getApplicationContext(), updateSoftwareService.class);
        serviceIntent.putExtra("checkUpdate", "checkUpdate");
        this.startService(serviceIntent);
    }

    private void workWithFinger() {
        // Initializing both Android Keyguard Manager and Fingerprint Manager
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        tvError = (TextView) findViewById(R.id.tv_MainErrorText);

        // Check whether the device has a Fingerprint sensor.
        if (!fingerprintManager.isHardwareDetected()) {
            /**
             * An error message will be displayed if the device does not contain the fingerprint hardware.
             * However if you plan to implement a default authentication method,
             * you can redirect the user to a default authentication activity from here.
             * Example:
             * Intent intent = new Intent(this, DefaultAuthenticationActivity.class);
             * startActivity(intent);
             */
            tvError.setText("Your Device does not have a Fingerprint Sensor");
        } else {
            // Checks whether fingerprint permission is set on manifest
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                tvError.setText("Fingerprint authentication permission not enabled");
            } else {
                // Check whether at least one fingerprint is registered
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    tvError.setText("Register at least one fingerprint in Settings");
                } else {
                    // Checks whether lock screen security is enabled or not
                    if (!keyguardManager.isKeyguardSecure()) {
                        tvError.setText("Lock screen security not enabled in Settings");
                    } else {
                        generateKey();

                        if (cipherInit()) {
                            FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                            FingerprintHandler helper = new FingerprintHandler(this);
                            helper.startAuth(fingerprintManager, cryptoObject);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        Locale local = new Locale("en");
        setLocale(local);
        super.onResume();
        edtPassword.setText("");
        checkDrawOverlayPermission();
    }

    @SuppressWarnings("deprecation")
    private void setLocale(Locale locale) {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getApplicationContext().createConfigurationContext(configuration);
        } else {
            resources.updateConfiguration(configuration, displayMetrics);
        }
    }


    public void checkDrawOverlayPermission() {
        /** check if we already  have permission to draw over other apps */
        if (!Settings.canDrawOverlays(this)) {
            /** if not construct intent to request permission */
            final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            /** request permission via start activity for result */
            try {
                startActivityForResult(intent, RC_OVERLAY);
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    public static Account CreateSyncAccount(Context context, String STRING_ACCOUNT_NAME) {
        // An account type, in the form of a domain name
        final String STRING_ACCOUNT_TYPE = "dqpvn.com-DQPCLIENT";
        // Create the account type and default account
        Account newAccount = new Account(
                STRING_ACCOUNT_NAME, STRING_ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

    private void getPre() {
        SharedPreferences sharedpre = getSharedPreferences(SHARED_PREFERENCES_NAME, this.MODE_PRIVATE);
        MY_SERVER = sharedpre.getString("Server", "");
        NGAY_LUU_ANH = sharedpre.getLong("TuNgay", 0);
        LOGIN_NAME = sharedpre.getString("LoginName", "");
        LOGIN_OK = sharedpre.getBoolean("LoginOk", false);
        LOGIN_PASSWORD = sharedpre.getString("Password", "");
    }

    private void initdata() {
        edtLogin.setText(LOGIN_NAME);
        edtPassword.setText("");
        edtPassword.requestFocus();
    }

    private void requestInstantSync(String onCommand) {
        final String STRING_AUTHORITY = "com.dqpvn.dqpclient.syncAdapter.StubProvider";
        // The account name
        final String STRING_ACCOUNT = "PeriodicSync";
        Account mAccount = CreateSyncAccount(this, STRING_ACCOUNT);
        //do sync intantly, // Perform a manual sync by calling this:
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        settingsBundle.putString("onCommand", onCommand);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.setIsSyncable(mAccount, STRING_AUTHORITY, 1);
        ContentResolver.requestSync(mAccount, STRING_AUTHORITY, settingsBundle);
    }

    private void addControls() {
        edtLogin = findViewById(R.id.edt_MainLoginName);
        edtPassword = findViewById(R.id.edt_MainPassword);
        tvTryLog = findViewById(R.id.tv_MainTryLog);
    }

    private void addEvents() {

        edtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (comPare(LOGIN_PASSWORD, edtPassword.getText() + "") &&
                            comPare(LOGIN_NAME, getEditText(MainActivity.this, edtLogin))) {
                        edtPassword.setText("");
                        edtPassword.setHint("Password");
                        startNAV();
                        finish();
                    } else {
                        tvTryLog.setVisibility(View.VISIBLE);
                        tryOtherAcc();
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.login) {
            logInByPassWord();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logInByPassWord() {
        if (comPare(LOGIN_PASSWORD, getEditText(MainActivity.this, edtPassword)) &&
                comPare(LOGIN_NAME, getEditText(MainActivity.this, edtLogin))) {
            edtPassword.setText("");
            edtPassword.setHint("Password");
            startNAV();
            this.finish();

        } else {
            tvTryLog.setVisibility(View.VISIBLE);
            tryOtherAcc();
        }
    }

    private void logInByFingerScaner() {
        //Check Shared Preferences
        File filePre = new File("/data/data/" + this.getApplicationContext().getPackageName() + "/shared_prefs/" + SHARED_PREFERENCES_NAME + ".xml");
        if (!filePre.exists()) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivityForResult(intent, REQUEST_SETTING);
            return;
        } else {
            //reset info
            getPre();
            startNAV();
            logWithFinger = true;
            this.finish();
        }
    }

    private void startNAV() {
        if (!utils.doesDatabaseExist(this, "dqpclient.db")) {
            Toast.makeText(this, "Có lổi xảy ra trong quá trình khởi tạo dữ liệu, ứng dụng không thể tiếp tục", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(MainActivity.this, NavDrawerActivity.class);
//        intent.putExtra("loginName",LOGIN_NAME);
        startActivityForResult(intent, REQUEST_NAV_MENU);
    }

    private void tryOtherAcc() {
        File filePre = new File("/data/data/" + this.getApplicationContext().getPackageName() + "/shared_prefs/" + SHARED_PREFERENCES_NAME + ".xml");
        if (!utils.doesDatabaseExist(this, "dqpclient.db") || !filePre.exists()) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivityForResult(intent, REQUEST_SETTING);
            return;
        }
        ArrayList<Users> arrUser = new ArrayList<>();
        arrUser = crudLocaldb.Users_getAllUsers();
        if (arrUser.size() > 0) {
            boolean passOk = false;
            for (int i = 0; i < arrUser.size(); i++) {
                LOGIN_NAME = edtLogin.getText() + "";
                if (comPare(arrUser.get(i).getEmail(), LOGIN_NAME) &&
                        comPare(arrUser.get(i).getPassword(), edtPassword.getText() + "")) {
                    if (arrUser.get(i).getAdmin() == 1) {
                        IS_ADMIN = true;
                    } else {
                        IS_ADMIN = false;
                    }
                    edtPassword.setText("");
                    CHO_PHEP_TRUY_CAP = true;
                    LOGIN_OK = true;
                    passOk = true;
                    startNAV();
                    finish();
                    break;
                }
            }
            if (!passOk) {
                wrongPassTime += 1;
                Toast.makeText(MainActivity.this, "Sai mật khẩu lần: " + wrongPassTime, Toast.LENGTH_SHORT).show();
                if (wrongPassTime == 8) {
                    int pid = android.os.Process.myPid();
                    android.os.Process.killProcess(pid);
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                }
            }
            tvTryLog.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SETTING) {
            boolean needRefresh = data.getBooleanExtra("needRefresh", true);
            // Refresh ListView
            if (needRefresh) {
                edtLogin.setText(LOGIN_NAME);
                edtPassword.setText(LOGIN_PASSWORD);
                getPre();
                if (networkOK()) {
                    requestInstantSync("wantPull");
                    syncImgStoreImageFileByDate();
                }
            }
        }
        if (requestCode == RC_OVERLAY) {
            final boolean overlayEnabled = Settings.canDrawOverlays(this);
            // continue here - permission was granted
        }
    }

    private void syncImgStoreImageFileByDate() {
        //viec download nam ngoai kiem soat nodejs server, co url la down thoi
        //donwload image file from server ******************************************************
        String BASE_URL = MY_SERVER + "/dqpclient/imgstore";
        ArrayList<ImgStore> arrImgStore = new ArrayList<>();
        ImgStore imgstore = new ImgStore();
        arrImgStore = crudLocaldb.ImgStore_getAllImgStore();
        if (arrImgStore.size() > 0) {
            imgstore = new ImgStore();
            for (int i = 0; i < arrImgStore.size(); i++) {
                imgstore = arrImgStore.get(i);
                String s1 = "";
                String s2 = "";
                if (!isBad(imgstore.getImgpath())) {
                    String[] arrImglocal = imgstore.getImgpath().split("/");
                    s1 = arrImglocal[arrImglocal.length - 1];
                    s2 = imgstore.getImgpath();
                }
                final String imglocal = s1;
                final String imglocalPath = s2;
                final int finalI = i;
                if (longGet(imgstore.getUpdatetime()) >= NGAY_LUU_ANH) {
                    restfullAPI.getImgStore getImgStoreTask = new restfullAPI.getImgStore();
                    getImgStoreTask.setUpdateListener(new restfullAPI.getImgStore.OnUpdateListener() {
                        @Override
                        public void onUpdate(ArrayList<ImgStore> obj) throws ExecutionException, InterruptedException {
                            if (obj == null) {
                                return;
                            }
                            ArrayList<ImgStore> arrServerImgStore = new ArrayList<>();
                            arrServerImgStore = obj;
                            if (arrServerImgStore.size() > 0) {
                                if (!isBad(arrServerImgStore.get(0).getImgpath())) {
                                    //xet truong hop da dơn roi thi bo qua
                                    String[] arrImgserver = arrServerImgStore.get(0).getImgpath().split("/");
                                    String imgserver = arrImgserver[arrImgserver.length - 1];
                                    if (!imglocalPath.equals(null) && !imglocalPath.equals("")) {
                                        if (imglocal.equals(imgserver) && !imglocalPath.substring(0, 4).equals("http")) {
                                            return;
                                        }
                                    }
                                    final String imageUrl = arrServerImgStore.get(0).getImgpath();
                                    ArrayList<ImgStore> arrImgStore = new ArrayList<>();
                                    arrImgStore = crudLocaldb.ImgStore_getAllImgStore();
                                    final ImgStore imgstore = arrImgStore.get(finalI);
                                    String device_dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
                                    restfullAPI.downloadFile downloadtask = new restfullAPI.downloadFile();
                                    downloadtask.setUpdateListener(new restfullAPI.downloadFile.OnUpdateListener() {
                                        @Override
                                        public void onUpdate(String realpath) throws ExecutionException, InterruptedException {
                                            imgstore.setImgpath(realpath);
                                            int u = crudLocaldb.ImgStore_updateImgStore(imgstore);
                                            if (u != 0) {
                                                Log.d(TAG, "syncImgStoreImageFileByDate---------Fetch images from server sucessfully: " + realpath);
                                            }
                                        }
                                    });
                                    downloadtask.execute(new String[]{imageUrl, device_dir});
                                }

                            }
                        }
                    });
                    getImgStoreTask.execute(BASE_URL + "/read/" + imgstore.getServerkey() + "/");

                } else {
                    restfullAPI.getImgStore getImgStoreTask = new restfullAPI.getImgStore();
                    getImgStoreTask.setUpdateListener(new restfullAPI.getImgStore.OnUpdateListener() {
                        @Override
                        public void onUpdate(ArrayList<ImgStore> obj) throws ExecutionException, InterruptedException {
                            if (obj == null) {
                                return;
                            }
                            ArrayList<ImgStore> arrServerImgStore = new ArrayList<>();
                            arrServerImgStore = obj;
                            if (arrServerImgStore.size() > 0) {
                                if (!isBad(arrServerImgStore.get(0).getImgpath())) {
                                    String[] arrImgserver = arrServerImgStore.get(0).getImgpath().split("/");
                                    String imgserver = arrImgserver[arrImgserver.length - 1];
                                    if (!imglocalPath.equals(null) && !imglocalPath.equals("")) {
                                        if (imglocal.equals(imgserver) && imglocalPath.substring(0, 4).equals("http")) {
                                            return;
                                        }
                                    }
                                    final String imageUrl = arrServerImgStore.get(0).getImgpath();
                                    ArrayList<ImgStore> arrImgStore = new ArrayList<>();
                                    arrImgStore = crudLocaldb.ImgStore_getAllImgStore();
                                    final ImgStore imgstore = arrImgStore.get(finalI);
                                    String s = imgstore.getImgpath();
                                    if (s != null) {
                                        File file = new File(s);
                                        if (file.exists()) {
                                            file.delete();
                                        }
                                    }
                                    imgstore.setImgpath(imageUrl);
                                    int u = crudLocaldb.ImgStore_updateImgStore(imgstore);
                                    if (u != 0) {
                                        Log.d(TAG, "syncImgStoreImageFileByDate------------Clean local images sucessfully: " + imageUrl);
                                    }
                                }
                            }
                        }
                    });
                    getImgStoreTask.execute(BASE_URL + "/read/" + imgstore.getServerkey() + "/");
                }
            }

        }
    }

    private class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

        private Context context;

        // Constructor
        public FingerprintHandler(Context mContext) {
            context = mContext;
        }

        public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
            CancellationSignal cancellationSignal = new CancellationSignal();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        }

        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            this.update("Fingerprint Authentication error\n" + errString, false);
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            this.update("Fingerprint Authentication help\n" + helpString, false);
        }

        @Override
        public void onAuthenticationFailed() {
            this.update("Fingerprint Authentication failed.", false);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            this.update("Fingerprint Authentication succeeded.", true);
            logInByFingerScaner();
        }

        public void update(String e, Boolean success) {
            TextView textView = (TextView) ((Activity) context).findViewById(R.id.tv_MainErrorText);
            textView.setText(e);
            if (success) {
                textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            } else {
                textView.setTextColor(ContextCompat.getColor(context, R.color.errorText));
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    @Override
    public void finish() {
        super.finish();
    }
}

