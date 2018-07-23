package com.dqpvn.dqpclient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.models.Ticket;
import com.dqpvn.dqpclient.utils.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.dqpvn.dqpclient.crudmanager.SyncCheck.RKEY_TICKET;

public class ReceiveContentActivity extends AppCompatActivity {
    private ImageView imgReceived;
    private ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
    private RadioGroup group;
    private Button btnOk;
    private String realPhotoPath, intentUserName;
    private Uri imageUriFromShare;
    final String SHARED_PREFERENCES_NAME="dqpclient_preferences";
    SharedPreferences sharedpre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_content);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .build();
        ImageLoader.getInstance().init(config);

        //Check Shared Preferences
        File filePre = new File("/data/data/"+ this.getApplicationContext().getPackageName()+ "/shared_prefs/"+SHARED_PREFERENCES_NAME +".xml");
        if (!filePre.exists()) {
            Intent intent = new Intent(ReceiveContentActivity.this, SettingActivity.class);
            startActivity(intent);
        } else {
            sharedpre = getSharedPreferences(SHARED_PREFERENCES_NAME, this.MODE_PRIVATE);
            intentUserName=sharedpre.getString("LoginName","");
        }

        crudLocal crudLocaldb=crudLocal.getInstance(this);
        crudLocaldb.getDb();
        ArrayList<Ticket> arrTicket=new ArrayList<>();
        arrTicket=crudLocaldb.Ticket_getOpenTicketByUser(intentUserName);
        if (arrTicket.size()>0){
            for (int i=0;i<arrTicket.size();i++){
                if (arrTicket.get(i).getFinished()==0){
                    RKEY_TICKET=arrTicket.get(i).getRkey();
                    break;
                }
            }
        }else{
            RKEY_TICKET=0;
        }

        addControls();
        addEvents();

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        }
    }

    private void addEvents() {
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                Bundle bundle;
                int isChecked=group.getCheckedRadioButtonId();
                switch (isChecked){
                    case R.id.radio_Chi:
                        intent = new Intent(ReceiveContentActivity.this, ChiActivity.class);
                        bundle=new Bundle();
                        //đưa dữ liệu riêng lẻ vào Bundle (chat hàng len container)
                        bundle.putParcelable("imageUriFromShare", imageUriFromShare);
                        //Đưa Bundle vào Intent
                        //intent.putExtras(bundle);
                        intent.putExtra("SharePackage",bundle);
                        intent.putExtra("userName",intentUserName);
                        intent.putExtra("rkeyTicket", RKEY_TICKET);
                        startActivity(intent);
                        onBackPressed();
                        break;
                    case R.id.radio_Thu:
                        intent = new Intent(ReceiveContentActivity.this, ThuActivity.class);
                        bundle=new Bundle();
                        //đưa dữ liệu riêng lẻ vào Bundle (chat hàng len container)
                        bundle.putParcelable("imageUriFromShare", imageUriFromShare);
                        //Đưa Bundle vào Intent
                        //intent.putExtras(bundle);
                        intent.putExtra("SharePackage",bundle);
                        intent.putExtra("userName",intentUserName);
                        intent.putExtra("rkeyTicket", RKEY_TICKET);
                        startActivity(intent);
                        onBackPressed();
                        break;
                    case R.id.radio_KhongDung:
                        onBackPressed();
                        break;
                }
            }
        });
    }

    private void addControls() {
        group =findViewById(R.id.radiogroup_ReceiveContent);
        btnOk=findViewById(R.id.btn_ReiceiveContentOk);
        imgReceived=findViewById(R.id.img_ReceiveContent);
    }

    void handleSendImage(Intent intent) {
        imageUriFromShare = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUriFromShare != null) {
            realPhotoPath= utils.getRealPathFromURI(this,imageUriFromShare);
//            InputStream is = null;
//            try {
//                is = getContentResolver().openInputStream(imageUri);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            Bitmap bmp= BitmapFactory.decodeStream(is);
            //imgReceived.setImageBitmap(bmp);
            //imageLoader.displayImage(String.valueOf(imageUri), imgReceived);
            Bitmap bmp= null;
            try {
                bmp = utils.XoayImage(realPhotoPath);
            } catch (Exception e) {
                bmp= BitmapFactory.decodeFile(realPhotoPath);
                e.printStackTrace();
            }
            imgReceived.setImageBitmap(bmp);
        }
    }

}
