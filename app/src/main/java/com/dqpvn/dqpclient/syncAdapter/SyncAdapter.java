package com.dqpvn.dqpclient.syncAdapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Log;
import com.dqpvn.dqpclient.crudmanager.restfullAPI;
import com.dqpvn.dqpclient.models.BanHSDetail;
import com.dqpvn.dqpclient.models.Chi;
import com.dqpvn.dqpclient.models.ChiDetail;
import com.dqpvn.dqpclient.models.ChuyenBien;
import com.dqpvn.dqpclient.models.DMHaiSan;
import com.dqpvn.dqpclient.models.DSTV;
import com.dqpvn.dqpclient.models.DebtBook;
import com.dqpvn.dqpclient.models.DiemDD;
import com.dqpvn.dqpclient.models.DoiTac;
import com.dqpvn.dqpclient.models.ImgStore;
import com.dqpvn.dqpclient.models.KhachHang;
import com.dqpvn.dqpclient.models.ResponseFromServer;


import com.dqpvn.dqpclient.models.Thu;
import com.dqpvn.dqpclient.models.ThuDetail;
import com.dqpvn.dqpclient.models.Ticket;
import com.dqpvn.dqpclient.models.TicketDetail;
import com.dqpvn.dqpclient.models.Users;
import com.dqpvn.dqpclient.models.WantDeleteFromServer;
import com.dqpvn.dqpclient.utils.utils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.dqpvn.dqpclient.utils.utils.getCurrentTimeMiliS;
import static com.dqpvn.dqpclient.utils.utils.intGet;
import static com.dqpvn.dqpclient.utils.utils.longGet;
import static com.dqpvn.dqpclient.utils.utils.readFromFile;
import static com.dqpvn.dqpclient.utils.utils.writeToFile;


/**
 * Created by linh3 on 29/04/2018.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    // Global variables
    private static final String TAG = "DQPClient-syncAdapter";
    private Context mContext;
    private String MY_SERVER;
    private long NGAY_LUU_ANH;
    private ContentResolver mContentResolver;

    private String BASE_URL_USERS, BASE_URL_DSTV, BASE_URL_CHUYENBIEN, BASE_URL_DIEMDD,
            BASE_URL_KHACHHANG, BASE_URL_DOITAC, BASE_URL_TICKET, BASE_URL_TICKETDETAIL, BASE_URL_THU,
            BASE_URL_THUDETAIL, BASE_URL_CHI, BASE_URL_CHIDETAIL, BASE_URL_DEBTBOOK,
            BASE_URL_DMHAISAN, BASE_URL_BANHSDETAIL, BASE_URL_IMGSTORE;

    /**
     * This gives us access to our local data source.
     */


    public SyncAdapter(Context c, boolean autoInit) {
        this(c, autoInit, false);
        mContext=c;
        mContentResolver = c.getContentResolver();
    }

    public SyncAdapter(Context c, boolean autoInit, boolean parallelSync) {
        super(c, autoInit, parallelSync);
        mContext=c;
        mContentResolver = c.getContentResolver();

    }
    /**
     * This method is run by the Android framework, on a new Thread, to perform a sync.
     * @param account Current account
     * @param extras Bundle extras
     * @param authority Content authority
     * @param provider {@link ContentProviderClient}
     * @param syncResult Object to write stats to
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        if (!networkOK()){
            Log.d(TAG, "Network is not available..................................");
            return;
        }
        File filePre = new File("/data/data/"+ getContext().getPackageName()+ "/shared_prefs/dqpclient_preferences.xml");
        if (filePre.exists()) {
            getPre();
        }else{
            return;
        }
        if (!serverOK()){
            Log.d(TAG, "Server is downed............................................ ");
            return;
        }

        Log.w(TAG, "Starting synchronization...");


        BASE_URL_USERS= MY_SERVER + "/dqpclient/user";
        BASE_URL_CHUYENBIEN= MY_SERVER + "/dqpclient/chuyenbien";
        BASE_URL_DSTV= MY_SERVER + "/dqpclient/dstv";
        BASE_URL_DOITAC= MY_SERVER + "/dqpclient/doitac";
        BASE_URL_KHACHHANG= MY_SERVER + "/dqpclient/khachhang";
        BASE_URL_DEBTBOOK= MY_SERVER + "/dqpclient/debtbook";
        BASE_URL_DIEMDD= MY_SERVER + "/dqpclient/diemdd";
        BASE_URL_TICKET= MY_SERVER + "/dqpclient/ticket";
        BASE_URL_TICKETDETAIL= MY_SERVER + "/dqpclient/ticketdetail";
        BASE_URL_THU= MY_SERVER + "/dqpclient/thu";
        BASE_URL_THUDETAIL= MY_SERVER + "/dqpclient/thudetail";
        BASE_URL_CHI= MY_SERVER + "/dqpclient/chi";
        BASE_URL_CHIDETAIL= MY_SERVER + "/dqpclient/chidetail";
        BASE_URL_DMHAISAN= MY_SERVER + "/dqpclient/dmhaisan";
        BASE_URL_IMGSTORE= MY_SERVER + "/dqpclient/imgstore";
        BASE_URL_BANHSDETAIL= MY_SERVER + "/dqpclient/banhsdetail";

        String onCommand=extras.getString("onCommand");
        if (onCommand==null){onCommand="empty";}
        switch (onCommand){
            case "wantPull" :
                Log.d(TAG, "wantPull: This do only when want pull all data from server............................");
                SyncPullFromServer();
                if (ReadSyncType()==2){
                    WriteSyncType("3");
                }
                break;
            case "wantPost":
                Log.d(TAG, "wantPost: This do only when want post new data to server............................");
                SyncPostToServer();
                if (ReadSyncType()==4){
                    WriteSyncType("5");
                }
                break;
            case "wantUpdate":
                Log.d(TAG, "wantUpdate: This do only when want two ways SyncUpdate both sides............................");
                SyncEditTwoWay();
                if (ReadSyncType()==3){
                    WriteSyncType("4");
                }
                break;
            case "wantDelete":
                Log.d(TAG, "wantDelete: This do only when want two ways SyncDelete both sides............................");
                SyncDelete();
                if (ReadSyncType()==1){
                    WriteSyncType("2");
                }
                break;
            default:
                try {
                    syncNewsFeed(syncResult);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }

        try {
            Thread.sleep(10*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SyncTicketOnly();
        try {
            Thread.sleep(3*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        editImgStoreErrorLink();

        Log.w(TAG, "Finished synchronization!");
    }


    private void syncNewsFeed(SyncResult syncResult) throws IOException, JSONException, RemoteException, OperationApplicationException, ExecutionException, InterruptedException {
        switch (ReadSyncType()) {
            case 1:
                SyncDelete();
                WriteSyncType("2");
                break;
            case 2:
                SyncPullFromServer();
                WriteSyncType("3");
                break;
            case 3:
                SyncEditTwoWay();
                WriteSyncType("4");
                break;
            case 4:
                SyncPostToServer();
                WriteSyncType("5");
                break;
            case 5:
                doLocalRkeyChange();
                WriteSyncType("1");
                break;
        }
    }
    private int ReadSyncType(){
        String fileSyncType="synctype.log";
        String StrSyncType="80";
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        baseDir+="/Android/data/com.dqpvn.dqpclient";
        File file = new File(baseDir + "/" + fileSyncType);
        if (file.exists()){
            if (intGet(StrSyncType)>5 || intGet((StrSyncType))==0){
                return 3;
            }else{
                return intGet(StrSyncType);
            }
        }else{
            WriteSyncType("3");
            return 3;
        }

    }
    private void WriteSyncType(String FileContent){
        String fileSyncType="synctype.log";
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        baseDir+="/Android/data/com.dqpvn.dqpclient";
        File file = new File(baseDir + "/" + fileSyncType);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writeToFile(file,FileContent);
    }
    private long timeGet(String timeStamp){
        String s=timeStamp.trim().substring(0,13);
        return Long.parseLong(s);
    }
    private void getPre(){
        SharedPreferences sharedpre = getContext().getSharedPreferences("dqpclient_preferences", getContext().MODE_PRIVATE);
        MY_SERVER=sharedpre.getString("Server","");
        NGAY_LUU_ANH=sharedpre.getLong("TuNgay",0);
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

    private void doLocalRkeyChange() {
        Log.d(TAG, "doLocalRkeyChange: ----------------doLocalRkeyChange---------------");
        ArrayList<ChuyenBien> arrChuyenBien = new ArrayList<>();
        ArrayList<Ticket>arrTicket=new ArrayList<>();
        ArrayList<TicketDetail> arrTicketDetail = new ArrayList<>();
        ArrayList<DSTV> arrDSTV = new ArrayList<>();
        ArrayList<DoiTac> arrDoiTac = new ArrayList<>();
        ArrayList<KhachHang> arrKhachHang =  new ArrayList<>();
        ArrayList<DebtBook> arrDebtBook =  new ArrayList<>();
        ArrayList<Thu> arrThu =  new ArrayList<>();
        ArrayList<Chi> arrChi =  new ArrayList<>();
        ArrayList<ChiDetail> arrChiDetail =  new ArrayList<>();
        ArrayList<ImgStore> arrImgStore =  new ArrayList<>();
        ArrayList<DiemDD> arrDiemDD =  new ArrayList<>();
        ArrayList<DMHaiSan> arrDMHaiSan = new ArrayList<>();
        ArrayList<ThuDetail> arrThuDetail =  new ArrayList<>();
        ArrayList<BanHSDetail> arrBanHSDetail =  new ArrayList<>();

        //ChuyenBien rkey as master to all it child
        arrChuyenBien = ChuyenBien_getChuaEditRkey();
        arrDSTV = DSTV_getAllDSTV();
        arrChi = Chi_getAllChi();
        arrThu = Thu_getAllThu();
        for (int a = 0; a < arrChuyenBien.size(); a++) {
            ChuyenBien chuyenbien = new ChuyenBien();
            chuyenbien = arrChuyenBien.get(a);
            if (chuyenbien.getServerkey() != 0 && chuyenbien.getRkey() != chuyenbien.getServerkey()) {
                for (int b = 0; b < arrDSTV.size(); b++) {
                    DSTV dstv = new DSTV();
                    dstv = arrDSTV.get(b);
                    if (dstv.getRkeychuyenbien() == chuyenbien.getRkey()) {
                        dstv.setRkeychuyenbien(chuyenbien.getServerkey());
                        dstv.setUpdatetime(String.valueOf(longGet(dstv.getUpdatetime()) + 1));
                        int u = DSTV_updateDSTV(dstv);
                    }
                }
                for (int b = 0; b < arrChi.size(); b++) {
                    Chi chi = new Chi();
                    chi = arrChi.get(b);
                    if (chi.getRkeychuyenbien() == chuyenbien.getRkey()) {
                        chi.setRkeychuyenbien(chuyenbien.getServerkey());
                        chi.setUpdatetime(String.valueOf(longGet(chi.getUpdatetime()) + 1));
                        Chi_updateChi(chi);
                    }
                }
                for (int b = 0; b < arrThu.size(); b++) {
                    Thu thu = new Thu();
                    thu = arrThu.get(b);
                    if (thu.getRkeychuyenbien() == chuyenbien.getRkey()) {
                        thu.setRkeychuyenbien(chuyenbien.getServerkey());
                        thu.setUpdatetime(String.valueOf(longGet(thu.getUpdatetime()) + 1));
                        //crudLocaldb.Thu_updateThu(thu);
                        Thu_updateThu(thu);
                    }
                }
                chuyenbien.setRkey(chuyenbien.getServerkey());
                chuyenbien.setUpdatetime(String.valueOf(longGet(chuyenbien.getUpdatetime()) + 1));
                //int u =crudLocaldb.ChuyenBien_updateChuyenBien(chuyenbien);
                int u = ChuyenBien_updateChuyenBien(chuyenbien);
                if (u > 0) {
                    Log.d(TAG, "update rkey successful on ChuyenBien and it child: " + chuyenbien.getRkey() + "");
                }
            }
        }


        //DoiTac rkey as master to all it child
        arrDoiTac=DoiTac_getChuaEditRkey();
        arrChi=Chi_getAllChi();
        for (int a = 0; a < arrDoiTac.size(); a++) {
            DoiTac doitac = new DoiTac();
            doitac = arrDoiTac.get(a);
            if (doitac.getServerkey() != 0 && doitac.getRkey() != doitac.getServerkey()) {
                for (int b = 0; b < arrChi.size(); b++) {
                    Chi chi = new Chi();
                    chi = arrChi.get(b);
                    if (chi.getRkeydoitac() == doitac.getRkey()) {
                        chi.setRkeydoitac(doitac.getServerkey());
                        chi.setUpdatetime(String.valueOf(longGet(chi.getUpdatetime()) + 1));
                        //crudLocaldb.Chi_updateChi(chi);
                        Chi_updateChi(chi);
                    }
                }
                doitac.setRkey(doitac.getServerkey());
                doitac.setUpdatetime(String.valueOf(longGet(doitac.getUpdatetime()) + 1));
                //int u=crudLocaldb.DoiTac_updateDoiTac(doitac);
                int u = DoiTac_updateDoiTac(doitac);
                if (u > 0) {
                    Log.d(TAG, "update rkey successful on DoiTac and it child: " + doitac.getRkey() + "");
                }
            }
        }

        //KhachHang rkey as master to all it child
        arrKhachHang=KhachHang_getChuaEditRkey();
        arrThu=Thu_getAllThu();
        for (int a = 0; a < arrKhachHang.size(); a++) {
            KhachHang khachhang = new KhachHang();
            khachhang = arrKhachHang.get(a);
            if (khachhang.getServerkey() != 0 && khachhang.getRkey() != khachhang.getServerkey()) {
                for (int b = 0; b < arrThu.size(); b++) {
                    Thu thu = new Thu();
                    thu = arrThu.get(b);
                    if (thu.getRkeykhachhang() == khachhang.getRkey()) {
                        thu.setRkeykhachhang(khachhang.getServerkey());
                        thu.setUpdatetime(String.valueOf(longGet(thu.getUpdatetime()) + 1));
                        //crudLocaldb.Thu_updateThu(thu);
                        Thu_updateThu(thu);
                    }
                }
                khachhang.setRkey(khachhang.getServerkey());
                khachhang.setUpdatetime(String.valueOf(longGet(khachhang.getUpdatetime()) + 1));
                //int u=crudLocaldb.KhachHang_updateKhachHang(khachhang);
                int u = KhachHang_updateKhachHang(khachhang);
                if (u > 0) {
                    Log.d(TAG, "update rkey successful on KhachHang and it child: " + khachhang.getRkey() + "");
                }
            }
        }
        //Ticket rkey as master to all it child
        arrTicket=Ticket_getChuaEditRkey();
        arrTicketDetail=TicketDetail_getAllTicketDetail();
        arrChi=Chi_getAllChi();
        arrDebtBook=DebtBook_getAllDebtBook();
        for (int a = 0; a < arrTicket.size(); a++) {
            Ticket ticket = new Ticket();
            ticket = arrTicket.get(a);
            if (ticket.getServerkey() != 0 && ticket.getRkey() != ticket.getServerkey()) {
                for (int b = 0; b < arrChi.size(); b++) {
                    Chi chi = new Chi();
                    chi = arrChi.get(b);
                    if (chi.getRkeyticket() == ticket.getRkey()) {
                        chi.setRkeyticket(ticket.getServerkey());
                        chi.setUpdatetime(String.valueOf(longGet(chi.getUpdatetime()) + 1));
                        //crudLocaldb.Chi_updateChi(chi);
                        Chi_updateChi(chi);
                    }
                }
                for (int b = 0; b < arrDebtBook.size(); b++) {
                    DebtBook debtBook = new DebtBook();
                    debtBook = arrDebtBook.get(b);
                    if (debtBook.getRkeyticket() == ticket.getRkey()) {
                        debtBook.setRkeyticket(ticket.getServerkey());
                        debtBook.setUpdatetime(String.valueOf(longGet(debtBook.getUpdatetime()) + 1));
                        //crudLocaldb.DebtBook_updateDebtBook(debtBook);
                        DebtBook_updateDebtBook(debtBook);
                    }
                }
                for (int c = 0; c < arrTicketDetail.size(); c++) {
                    TicketDetail ticketDetail = new TicketDetail();
                    ticketDetail = arrTicketDetail.get(c);
                    if (ticketDetail.getRkeyticket() == ticket.getRkey()) {
                        ticketDetail.setRkeyticket(ticket.getServerkey());
                        ticketDetail.setUpdatetime(String.valueOf(longGet(ticketDetail.getUpdatetime()) + 1));
                        //crudLocaldb.TicketDetail_updateTicketDetail(ticketDetail);
                        TicketDetail_updateTicketDetail(ticketDetail);
                    }
                }
                ticket.setRkey(ticket.getServerkey());
                ticket.setUpdatetime(String.valueOf(longGet(ticket.getUpdatetime()) + 1));
                //int u=crudLocaldb.Ticket_updateTicket(ticket);
                int u = Ticket_updateTicket(ticket);
                if (u > 0) {
                    Log.d(TAG, "update rkey successful on Ticket and it child: " + ticket.getRkey() + "");
                }
            }
        }

        //DSTV rkey as master to all it child
        arrDSTV=DSTV_getChuaEditRkey();
        arrDebtBook=DebtBook_getAllDebtBook();
        arrDiemDD=DiemDD_getAllDiemDD();
        for (int a = 0; a < arrDSTV.size(); a++) {
            DSTV dstv = new DSTV();
            dstv = arrDSTV.get(a);
            if (dstv.getServerkey() != 0 && dstv.getRkey() != dstv.getServerkey()) {
                for (int b = 0; b < arrDebtBook.size(); b++) {
                    DebtBook debtBook = new DebtBook();
                    debtBook = arrDebtBook.get(b);
                    if (debtBook.getRkeythuyenvien() == dstv.getRkey()) {
                        debtBook.setRkeythuyenvien(dstv.getServerkey());
                        debtBook.setUpdatetime(String.valueOf(longGet(debtBook.getUpdatetime()) + 1));
                        //crudLocaldb.DebtBook_updateDebtBook(debtBook);
                        DebtBook_updateDebtBook(debtBook);
                    }
                }
                for (int c = 0; c < arrDiemDD.size(); c++) {
                    DiemDD diemdd = new DiemDD();
                    diemdd = arrDiemDD.get(c);
                    if (diemdd.getEater() == dstv.getRkey() && diemdd.getChucvu() == "Thuyền viên") {
                        diemdd.setEater(dstv.getServerkey());
                        diemdd.setUpdatetime(String.valueOf(longGet(diemdd.getUpdatetime()) + 1));
                        //crudLocaldb.DiemDD_updateDiemDD(diemdd);
                        DiemDD_updateDiemDD(diemdd);
                    }
                }
                dstv.setRkey(dstv.getServerkey());
                dstv.setUpdatetime(String.valueOf(longGet(dstv.getUpdatetime()) + 1));
                //int u=crudLocaldb.DSTV_updateDSTV(dstv);
                int u = DSTV_updateDSTV(dstv);
                if (u > 0) {
                    Log.d(TAG, "update rkey successful on DSTV and it child: " + dstv.getRkey() + "");
                }
            }
        }

        //DMHaiSan rkey as master to all it child
        arrDMHaiSan=DMHaiSan_getChuaEditRkey();
        arrThuDetail=ThuDetail_getAllThuDetail();
        for (int a = 0; a < arrDMHaiSan.size(); a++) {
            DMHaiSan dmhaisan = new DMHaiSan();
            dmhaisan = arrDMHaiSan.get(a);
            if (dmhaisan.getServerkey() != 0 && dmhaisan.getRkey() != dmhaisan.getServerkey()) {
                for (int b = 0; b < arrThuDetail.size(); b++) {
                    ThuDetail thudetail = new ThuDetail();
                    thudetail = arrThuDetail.get(b);
                    if (thudetail.getRkeyhs() == dmhaisan.getRkey()) {
                        thudetail.setRkeyhs(dmhaisan.getServerkey());
                        thudetail.setUpdatetime(String.valueOf(longGet(thudetail.getUpdatetime()) + 1));
                        //crudLocaldb.ThuDetail_updateThuDetail(thudetail);
                        ThuDetail_updateThuDetail(thudetail);
                    }
                }
                dmhaisan.setRkey(dmhaisan.getServerkey());
                dmhaisan.setUpdatetime(String.valueOf(longGet(dmhaisan.getUpdatetime()) + 1));
                //int u=crudLocaldb.DMHaiSan_updateDMHaiSan(dmhaisan);
                int u = DMHaiSan_updateDMHaiSan(dmhaisan);
                if (u > 0) {
                    Log.d(TAG, "update rkey successful on DMHaiSan and it child: " + dmhaisan.getRkey() + "");
                }
            }
        }

        //Chi rkey as master to all it child
        arrChi=Chi_getChuaEditRkey();
        arrChiDetail=ChiDetail_getAllChiDetail();
        arrImgStore=ImgStore_getAllImgStore();
        for (int a = 0; a < arrChi.size(); a++) {
            Chi chi = new Chi();
            chi = arrChi.get(a);
            if (chi.getServerkey() != 0 && chi.getRkey() != chi.getServerkey()) {
                for (int b = 0; b < arrChiDetail.size(); b++) {
                    ChiDetail chidetail = new ChiDetail();
                    chidetail = arrChiDetail.get(b);
                    if (chidetail.getRkeychi() == chi.getRkey()) {
                        chidetail.setRkeychi(chi.getServerkey());
                        chidetail.setUpdatetime(String.valueOf(longGet(chidetail.getUpdatetime()) + 1));
                        //crudLocaldb.ChiDetail_updateChiDetail(chidetail);
                        ChiDetail_updateChiDetail(chidetail);
                    }
                }
                for (int c = 0; c < arrImgStore.size(); c++) {
                    ImgStore imgStore = new ImgStore();
                    imgStore = arrImgStore.get(c);
                    if (imgStore.getStorekey() == chi.getRkey() && imgStore.getFortable().equals("chi")) {
                        imgStore.setStorekey(chi.getServerkey());
                        imgStore.setUpdatetime(String.valueOf(longGet(imgStore.getUpdatetime()) + 1));
                        //crudLocaldb.ImgStore_updateImgStore(imgStore);
                        ImgStore_updateImgStore(imgStore);
                    }
                }
                chi.setRkey(chi.getServerkey());
                chi.setUpdatetime(String.valueOf(longGet(chi.getUpdatetime()) + 1));
                //int u=crudLocaldb.Chi_updateChi(chi);
                int u = Chi_updateChi(chi);
                if (u > 0) {
                    Log.d(TAG, "update rkey successful on Chi and it child: " + chi.getRkey() + "");
                }
            }
        }

        //Thu rkey as master to all it child
        arrThu=Thu_getChuaEditRkey();
        arrThuDetail=ThuDetail_getAllThuDetail();
        arrImgStore=ImgStore_getAllImgStore();
        arrBanHSDetail=BanHSDetail_getAllBanHSDetail();
        for (int a = 0; a < arrThu.size(); a++) {
            Thu thu = new Thu();
            thu = arrThu.get(a);
            if (thu.getServerkey() != 0 && thu.getRkey() != thu.getServerkey()) {
                for (int b = 0; b < arrImgStore.size(); b++) {
                    ImgStore imgStore = new ImgStore();
                    imgStore = arrImgStore.get(b);
                    if (imgStore.getStorekey() == thu.getRkey() && imgStore.getFortable().equals("thu")) {
                        imgStore.setStorekey(thu.getServerkey());
                        imgStore.setUpdatetime(String.valueOf(longGet(imgStore.getUpdatetime()) + 1));
                        //crudLocaldb.ImgStore_updateImgStore(imgStore);
                        ImgStore_updateImgStore(imgStore);
                    }
                }
                for (int c = 0; c < arrThuDetail.size(); c++) {
                    ThuDetail thudetail = new ThuDetail();
                    thudetail = arrThuDetail.get(c);
                    if (thudetail.getRkeythu() == thu.getRkey()) {
                        thudetail.setRkeythu(thu.getServerkey());
                        thudetail.setUpdatetime(String.valueOf(longGet(thudetail.getUpdatetime()) + 1));
                        //crudLocaldb.ThuDetail_updateThuDetail(thudetail);
                        ThuDetail_updateThuDetail(thudetail);
                    }
                }
                for (int d = 0; d < arrBanHSDetail.size(); d++) {
                    BanHSDetail banhsdt = new BanHSDetail();
                    banhsdt = arrBanHSDetail.get(d);
                    if (banhsdt.getRkeythu() == thu.getRkey()) {
                        banhsdt.setRkeythu(thu.getServerkey());
                        banhsdt.setUpdatetime(String.valueOf(longGet(banhsdt.getUpdatetime()) + 1));
                        //crudLocaldb.BanHSDetail_updateBanHSDetail(banhsdt);
                        BanHSDetail_updateBanHSDetail(banhsdt);
                    }
                }
                thu.setRkey(thu.getServerkey());
                thu.setUpdatetime(String.valueOf(longGet(thu.getUpdatetime()) + 1));
                //int u=crudLocaldb.Thu_updateThu(thu);
                int u = Thu_updateThu(thu);
                if (u > 0) {
                    Log.d(TAG, "update rkey successful on Thu and it child: " + thu.getRkey() + "");
                }
            }
        }

        //ThuDetail rkey as master to all it child
        arrThuDetail=ThuDetail_getChuaEditRkey();
        arrBanHSDetail=BanHSDetail_getAllBanHSDetail();
        for (int a = 0; a < arrThuDetail.size(); a++) {
            ThuDetail thudetail = new ThuDetail();
            thudetail = arrThuDetail.get(a);
            if (thudetail.getServerkey() != 0 && thudetail.getRkey() != thudetail.getServerkey()) {
                for (int c = 0; c < arrBanHSDetail.size(); c++) {
                    BanHSDetail banhsdetail = new BanHSDetail();
                    banhsdetail = arrBanHSDetail.get(c);
                    if (banhsdetail.getRkeythudetail() == thudetail.getRkey()) {
                        banhsdetail.setRkeythudetail(thudetail.getServerkey());
                        banhsdetail.setUpdatetime(String.valueOf(longGet(banhsdetail.getUpdatetime() + 1)));
                        //crudLocaldb.BanHSDetail_updateBanHSDetail(banhsdetail);
                        BanHSDetail_updateBanHSDetail(banhsdetail);
                    }
                }
                thudetail.setRkey(thudetail.getServerkey());
                thudetail.setUpdatetime(String.valueOf(longGet(thudetail.getUpdatetime()) + 1));
                //int u=crudLocaldb.ThuDetail_updateThuDetail(thudetail);
                int u = ThuDetail_updateThuDetail(thudetail);
                if (u > 0) {
                    Log.d(TAG, "update rkey successful on ThuDetail and it child: " + thudetail.getRkey() + "");
                }
            }
        }

        //sua lai rkey cho nhung thang khong co child, tranh truong hop rkey tang cao qua field size
        //Cai nay khong can thiet chi phong ngua vay thoi, rkey khong child thi mun sua sao cung chang lien quan den ai

        //thang debtbook khong co con
        arrDebtBook=DebtBook_getChuaEditRkey();
        for (int i = 0; i < arrDebtBook.size(); i++) {
            DebtBook debtBook = new DebtBook();
            debtBook = arrDebtBook.get(i);
            if (debtBook.getServerkey() != debtBook.getRkey() && debtBook.getServerkey() != 0) {
                debtBook.setRkey(debtBook.getServerkey());
                debtBook.setUpdatetime(String.valueOf(longGet(debtBook.getUpdatetime()) + 1));
                //int u=crudLocaldb.DebtBook_updateDebtBook(debtBook);
                int u = DebtBook_updateDebtBook(debtBook);
            }

        }
        //Thang ChiDetail cung khong co con
        arrChiDetail=ChiDetail_getChuaEditRkey();
        for (int i = 0; i < arrChiDetail.size(); i++) {
            ChiDetail chiDetail = new ChiDetail();
            chiDetail = arrChiDetail.get(i);
            if (chiDetail.getServerkey() != chiDetail.getRkey() && chiDetail.getServerkey() != 0) {
                chiDetail.setRkey(chiDetail.getServerkey());
                chiDetail.setUpdatetime(String.valueOf(longGet(chiDetail.getUpdatetime()) + 1));
                //int u=crudLocaldb.ChiDetail_updateChiDetail(chiDetail);
                int u = ChiDetail_updateChiDetail(chiDetail);
            }
        }

        //thang diemdd khong co con
        arrDiemDD=DiemDD_getChuaEditRkey();
        for (int i = 0; i < arrDiemDD.size(); i++) {
            DiemDD diemdd = new DiemDD();
            diemdd = arrDiemDD.get(i);
            if (diemdd.getServerkey() != diemdd.getRkey() && diemdd.getServerkey() != 0) {
                diemdd.setRkey(diemdd.getServerkey());
                diemdd.setUpdatetime(String.valueOf(longGet(diemdd.getUpdatetime()) + 1));
                //int u=crudLocaldb.DiemDD_updateDiemDD(diemdd);
                int u = DiemDD_updateDiemDD(diemdd);

            }

        }

        //thang ticketdetail khong co con
        arrTicketDetail=TicketDetail_getChuaEditRkey();
        for (int i = 0; i < arrTicketDetail.size(); i++) {
            TicketDetail ticketDetail = new TicketDetail();
            ticketDetail = arrTicketDetail.get(i);
            if (ticketDetail.getServerkey() != ticketDetail.getRkey() && ticketDetail.getServerkey() != 0) {
                ticketDetail.setRkey(ticketDetail.getServerkey());
                ticketDetail.setUpdatetime(String.valueOf(longGet(ticketDetail.getUpdatetime()) + 1));
                //int u=crudLocaldb.TicketDetail_updateTicketDetail(ticketDetail);
                int u = TicketDetail_updateTicketDetail(ticketDetail);
            }

        }
    }

    //editImgStoreErrorLink repair error img link ******************editImgStoreErrorLink********************************************************

    private void editImgStoreErrorLink(){
        Log.d(TAG, "editImgStoreErrorLink: ----------------editImgStoreErrorLink---------------");
        // Edit error sync link for Image Store
        final String BAD_LINK=BASE_URL_IMGSTORE+ "/badlink";
        restfullAPI.getImgStore  repairTask = new restfullAPI.getImgStore();
        repairTask.setUpdateListener(new restfullAPI.getImgStore.OnUpdateListener(){
            @Override
            public void onUpdate(ArrayList<ImgStore> arrRestfullImgStore) throws ExecutionException, InterruptedException {
                if (arrRestfullImgStore==null){return;}
                if (arrRestfullImgStore.size()>0){
                    for (int i = 0; i < arrRestfullImgStore.size(); i++) {
                        String strPath=arrRestfullImgStore.get(i).getImgpath();
                        if (StringUtils.compareIgnoreCase(StringUtils.left(strPath,4),"http")!=0){
                            File file=new File(strPath);
                            Log.d(TAG, "editImgStoreErrorLink: Bad link: "+ strPath);
                            if (file.exists()){
                                ImgStore serverImgStore =new ImgStore();
                                serverImgStore=arrRestfullImgStore.get(i);
                                Log.d(TAG, "editImgStoreErrorLink: Error found and try repair--------------------- ");
                                //post new file to server
                                restfullAPI.postFile taskpostfile =new restfullAPI.postFile();
                                final ImgStore finalserverImgStore = serverImgStore;
                                taskpostfile.setUpdateListener(new restfullAPI.postFile.OnUpdateListener() {
                                    @Override
                                    public void onUpdate(ResponseFromServer obj) {
                                        if (obj==null){return;}
                                        if (obj.getStatus()==0){
                                            final String ImageHoaDonURL =obj.getMessage();
                                            finalserverImgStore.setImgpath(ImageHoaDonURL);
                                            finalserverImgStore.setUpdatetime(String.valueOf(longGet(finalserverImgStore.getUpdatetime())+1));
                                            //update link cho server img hoa don
                                            String myurl2= BASE_URL_IMGSTORE + "/update/"+ finalserverImgStore.getServerkey() +"/";
                                            new restfullAPI.putImgStore(finalserverImgStore).execute(myurl2);
                                            Log.d(TAG, "Error link on server ImgStore edited: from: " + strPath + " ------ to: "+ ImageHoaDonURL);
                                        }
                                    }
                                });
                                taskpostfile.execute(new String[]{BASE_URL_IMGSTORE +"/upload/"+String.valueOf(serverImgStore.getServerkey()),serverImgStore.getImgpath()});
                            }
                        }
                    }
                }
            }

        });
        repairTask.execute(BAD_LINK);

        try {
            Thread.sleep(10*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<ImgStore> arrImgStore=ImgStore_getAllBadLink();
        for (int i=0;i<arrImgStore.size();i++){
            final String strPath=arrImgStore.get(i).getImgpath();
            final int svKey=arrImgStore.get(i).getServerkey();
            if (StringUtils.compareIgnoreCase(StringUtils.left(strPath,4),"http")!=0){
                File file=new File(strPath);
                if (!file.exists()){
                    Log.d(TAG, "editImgStoreErrorLink: File Not found: "+ strPath);
                    //pull from server ******************************************************
                    final String myurl=BASE_URL_IMGSTORE+"/read/"+ svKey +"/";
                    restfullAPI.getImgStore  task = new restfullAPI.getImgStore();
                    task.setUpdateListener(new restfullAPI.getImgStore.OnUpdateListener(){
                        @Override
                        public void onUpdate(ArrayList<ImgStore> arrServerImgStore) {
                            if (arrServerImgStore==null) {return;}
                            if (arrServerImgStore.size()>0){
                                final String serverImgPath=arrImgStore.get(0).getImgpath();
                                if (serverImgPath.substring(0,4).equals("http")){
                                    ImgStore localImgstore=arrImgStore.get(0);
                                    localImgstore.setImgpath(serverImgPath);
                                    int u=ImgStore_updateImgStore(localImgstore);
                                }
                            }
                        }
                    });
                    task.execute(myurl);
                }
            }
        }
    }
    // **********************************SYNC WHAT DELETE****************************************
    private void SyncDelete(){
        Log.d(TAG, "SyncDelete: ------------------------SyncDelete------------------------------");

        // ********************************************server xoa theo client*****************************
        ArrayList<WantDeleteFromServer>arrWDFS=new ArrayList<>();
        arrWDFS=WDFS_getAllWDFS();
        if (arrWDFS.size()>0){
            for (int i = 0; i < arrWDFS.size(); i++) {
                final WantDeleteFromServer wdfs=arrWDFS.get(i);

                if (wdfs.getmTablename().equals("imgstore")){
                    try{
                        //Yeu cau server xoa file anh cu truoc.
                        new restfullAPI.deleteFileOnServer().execute(BASE_URL_IMGSTORE + "/deletefile/"+wdfs.getmServerkey()+"/");
                        //Xoa record.
                        String DeleteURL=BASE_URL_IMGSTORE+ "/delete/" + wdfs.getmServerkey() + "/";
                        restfullAPI.deleteImgStore  task = new restfullAPI.deleteImgStore();
                        task.setUpdateListener(new restfullAPI.deleteImgStore.OnUpdateListener(){

                            @Override
                            public void onUpdate(ResponseFromServer res) {

                                if (res.getStatus()==0){
                                    //xoa trong danh sach
                                    int d=WDFS_deleteWDFS(wdfs.getmId());
                                    if (d!=0){
                                        Log.d(TAG, "Synchronize ImgStore, delete like client: ---------------- " + d +"");
                                    }
                                }
                            }
                        });
                        task.execute(DeleteURL);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "on Synchronize: "+e.toString() );
                    }
                }

                if (wdfs.getmTablename().equals("users")){
                    try{
                        String DeleteURL=BASE_URL_USERS+ "/delete/" + wdfs.getmServerkey() + "/";
                        restfullAPI.deleteUser  task = new restfullAPI.deleteUser();
                        task.setUpdateListener(new restfullAPI.deleteUser.OnUpdateListener(){
                            @Override
                            public void onUpdate(ResponseFromServer res) {
                                if (res==null){return;}
                                if (res.getStatus()==0){
                                    //xoa trong danh sach
                                    int d=WDFS_deleteWDFS(wdfs.getmId());
                                    if (d!=0){
                                        Log.d(TAG, "Synchronize Users, delete like client: ---------------- " + d +"");
                                    }
                                }
                            }
                        });
                        task.execute(DeleteURL);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "on Synchronize: "+e.toString() );
                    }
                }
                if (wdfs.getmTablename().equals("dstv")){
                    try{
                        String DeleteURL=BASE_URL_DSTV+ "/delete/" + wdfs.getmServerkey() + "/";
                        restfullAPI.deleteDSTV  task = new restfullAPI.deleteDSTV();
                        task.setUpdateListener(new restfullAPI.deleteDSTV.OnUpdateListener(){
                            @Override
                            public void onUpdate(ResponseFromServer res) {
                                if (res==null){return;}
                                if (res.getStatus()==0){
                                    //xoa trong danh sach
                                    int d=WDFS_deleteWDFS(wdfs.getmId());
                                    if (d!=0){
                                        Log.d(TAG, "Synchronize DSTV, delete like client: ---------------- " + d +"");
                                    }
                                }
                            }
                        });
                        task.execute(DeleteURL);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "on Synchronize: "+e.toString() );
                    }
                }
                if (wdfs.getmTablename().equals("chuyenbien")){
                    try{
                        String DeleteURL=BASE_URL_CHUYENBIEN+ "/delete/" + wdfs.getmServerkey() + "/";
                        restfullAPI.deleteChuyenBien  task = new restfullAPI.deleteChuyenBien();
                        task.setUpdateListener(new restfullAPI.deleteChuyenBien.OnUpdateListener(){
                            @Override
                            public void onUpdate(ResponseFromServer res) {
                                if (res==null){return;}
                                if (res.getStatus()==0){
                                    //xoa trong danh sach
                                    int d=WDFS_deleteWDFS(wdfs.getmId());
                                    if (d!=0){
                                        Log.d(TAG, "Synchronize ChuyenBien, delete like client: ---------------- " + d +"");
                                    }
                                }
                            }
                        });
                        task.execute(DeleteURL);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "on Synchronize: "+e.toString() );
                    }
                }
                if (wdfs.getmTablename().equals("diemdd")){
                    try{
                        String DeleteURL=BASE_URL_DIEMDD + "/delete/" + wdfs.getmServerkey() + "/";
                        restfullAPI.deleteDiemDD  task = new restfullAPI.deleteDiemDD();
                        task.setUpdateListener(new restfullAPI.deleteDiemDD.OnUpdateListener(){
                            @Override
                            public void onUpdate(ResponseFromServer res) {
                                if (res==null){return;}
                                if (res.getStatus()==0){
                                    //xoa trong danh sach
                                    int d=WDFS_deleteWDFS(wdfs.getmId());
                                    if (d!=0){
                                        Log.d(TAG, "Synchronize DiemDD, delete like client: ---------------- " + d +"");
                                    }
                                }
                            }
                        });
                        task.execute(DeleteURL);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "on Synchronize: "+e.toString() );
                    }
                }
                if (wdfs.getmTablename().equals("khachhang")){
                    try{
                        String DeleteURL=BASE_URL_KHACHHANG+ "/delete/" + wdfs.getmServerkey() + "/";
                        restfullAPI.deleteKhachHang  task = new restfullAPI.deleteKhachHang();
                        task.setUpdateListener(new restfullAPI.deleteKhachHang.OnUpdateListener(){
                            @Override
                            public void onUpdate(ResponseFromServer res) {
                                if (res==null){return;}
                                if (res.getStatus()==0){
                                    //xoa trong danh sach
                                    int d=WDFS_deleteWDFS(wdfs.getmId());
                                    if (d!=0){
                                        Log.d(TAG, "Synchronize KhachHang, delete like client: ---------------- " + d +"");
                                    }
                                }
                            }
                        });
                        task.execute(DeleteURL);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "on Synchronize: "+e.toString() );
                    }
                }
                if (wdfs.getmTablename().equals("doitac")){
                    try{
                        String DeleteURL=BASE_URL_DOITAC + "/delete/" + wdfs.getmServerkey() + "/";
                        restfullAPI.deleteDoiTac  task = new restfullAPI.deleteDoiTac();
                        task.setUpdateListener(new restfullAPI.deleteDoiTac.OnUpdateListener(){
                            @Override
                            public void onUpdate(ResponseFromServer res) {
                                if (res==null){return;}
                                if (res.getStatus()==0){
                                    //xoa trong danh sach
                                    int d=WDFS_deleteWDFS(wdfs.getmId());
                                    if (d!=0){
                                        Log.d(TAG, "Synchronize DoiTac, delete like client: ---------------- " + d +"");
                                    }
                                }
                            }
                        });
                        task.execute(DeleteURL);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "on Synchronize: "+e.toString() );
                    }
                }
                if (wdfs.getmTablename().equals("ticket")){
                    try{
                        String DeleteURL=BASE_URL_TICKET+ "/delete/" + wdfs.getmServerkey() + "/";
                        restfullAPI.deleteTicket  task = new restfullAPI.deleteTicket();
                        task.setUpdateListener(new restfullAPI.deleteTicket.OnUpdateListener(){
                            @Override
                            public void onUpdate(ResponseFromServer res) {
                                if (res==null){return;}
                                if (res.getStatus()==0){
                                    //xoa trong danh sach
                                    int d=WDFS_deleteWDFS(wdfs.getmId());
                                    if (d!=0){
                                        Log.d(TAG, "Synchronize Ticket, delete like client: ---------------- " + d +"");
                                    }
                                }
                            }
                        });
                        task.execute(DeleteURL);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "on Synchronize: "+e.toString() );
                    }
                }
                if (wdfs.getmTablename().equals("ticketdetail")){
                    try{
                        String DeleteURL=BASE_URL_TICKETDETAIL+ "/delete/" + wdfs.getmServerkey() + "/";
                        restfullAPI.deleteTicketDetail  task = new restfullAPI.deleteTicketDetail();
                        task.setUpdateListener(new restfullAPI.deleteTicketDetail.OnUpdateListener(){
                            @Override
                            public void onUpdate(ResponseFromServer res) {
                                if (res==null){return;}
                                if (res.getStatus()==0){
                                    //xoa trong danh sach
                                    int d=WDFS_deleteWDFS(wdfs.getmId());
                                    if (d!=0){
                                        Log.d(TAG, "Synchronize TicketDetail, delete like client: ---------------- " + d +"");
                                    }
                                }
                            }
                        });
                        task.execute(DeleteURL);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "on Synchronize: "+e.toString() );
                    }
                }
                if (wdfs.getmTablename().equals("thu")){
                    try{
                        String DeleteURL=BASE_URL_THU+ "/delete/" + wdfs.getmServerkey() + "/";
                        restfullAPI.deleteThu  task = new restfullAPI.deleteThu();
                        task.setUpdateListener(new restfullAPI.deleteThu.OnUpdateListener(){
                            @Override
                            public void onUpdate(ResponseFromServer res) {
                                if (res==null){return;}
                                if (res.getStatus()==0){
                                    //xoa trong danh sach
                                    int d=WDFS_deleteWDFS(wdfs.getmId());
                                    if (d!=0){
                                        Log.d(TAG, "Synchronize Thu, delete like client: ---------------- " + d +"");
                                    }
                                }
                            }
                        });
                        task.execute(DeleteURL);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "on Synchronize: "+e.toString() );
                    }
                }
                if (wdfs.getmTablename().equals("thudetail")){
                    try{
                        String DeleteURL=BASE_URL_THUDETAIL+ "/delete/" + wdfs.getmServerkey() + "/";
                        restfullAPI.deleteThuDetail  task = new restfullAPI.deleteThuDetail();
                        task.setUpdateListener(new restfullAPI.deleteThuDetail.OnUpdateListener(){
                            @Override
                            public void onUpdate(ResponseFromServer res) {
                                if (res==null){return;}
                                if (res.getStatus()==0){
                                    //xoa trong danh sach
                                    int d=WDFS_deleteWDFS(wdfs.getmId());
                                    if (d!=0){
                                        Log.d(TAG, "Synchronize ThuDetail, delete like client: ---------------- " + d +"");
                                    }
                                }
                            }
                        });
                        task.execute(DeleteURL);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "on Synchronize: "+e.toString() );
                    }
                }
                if (wdfs.getmTablename().equals("chi")){
                    try{
                        String DeleteURL=BASE_URL_CHI + "/delete/" + wdfs.getmServerkey() + "/";
                        restfullAPI.deleteChi  task = new restfullAPI.deleteChi();
                        task.setUpdateListener(new restfullAPI.deleteChi.OnUpdateListener(){
                            @Override
                            public void onUpdate(ResponseFromServer res) {
                                if (res==null){return;}
                                if (res.getStatus()==0){
                                    //xoa trong danh sach
                                    int d=WDFS_deleteWDFS(wdfs.getmId());
                                    if (d!=0){
                                        Log.d(TAG, "Synchronize Chi, delete like client: ---------------- " + d +"");
                                    }
                                }
                            }
                        });
                        task.execute(DeleteURL);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "on Synchronize: "+e.toString() );
                    }
                }
                if (wdfs.getmTablename().equals("chidetail")){
                    try{
                        String DeleteURL=BASE_URL_CHIDETAIL+ "/delete/" + wdfs.getmServerkey() + "/";
                        restfullAPI.deleteChiDetail  task = new restfullAPI.deleteChiDetail();
                        task.setUpdateListener(new restfullAPI.deleteChiDetail.OnUpdateListener(){
                            @Override
                            public void onUpdate(ResponseFromServer res) {
                                if (res==null){return;}
                                if (res.getStatus()==0){
                                    //xoa trong danh sach
                                    int d=WDFS_deleteWDFS(wdfs.getmId());
                                    if (d!=0){
                                        Log.d(TAG, "Synchronize ChiDetail, delete like client: ---------------- " + d +"");
                                    }
                                }
                            }
                        });
                        task.execute(DeleteURL);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "on Synchronize: "+e.toString() );
                    }
                }
                if (wdfs.getmTablename().equals("debtbook")){
                    try{
                        String DeleteURL=BASE_URL_DEBTBOOK+ "/delete/" + wdfs.getmServerkey() + "/";
                        restfullAPI.deleteDebtBook  task = new restfullAPI.deleteDebtBook();
                        task.setUpdateListener(new restfullAPI.deleteDebtBook.OnUpdateListener(){
                            @Override
                            public void onUpdate(ResponseFromServer res) {
                                if (res==null){return;}
                                if (res.getStatus()==0){
                                    //xoa trong danh sach
                                    int d=WDFS_deleteWDFS(wdfs.getmId());
                                    if (d!=0){
                                        Log.d(TAG, "Synchronize DebtBook, delete like client: ---------------- " + d +"");
                                    }
                                }
                            }
                        });
                        task.execute(DeleteURL);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "on Synchronize: "+e.toString() );
                    }
                }
                if (wdfs.getmTablename().equals("dmhaisan")){
                    try{
                        String DeleteURL=BASE_URL_DMHAISAN + "/delete/" + wdfs.getmServerkey() + "/";
                        restfullAPI.deleteDMHaiSan  task = new restfullAPI.deleteDMHaiSan();
                        task.setUpdateListener(new restfullAPI.deleteDMHaiSan.OnUpdateListener(){
                            @Override
                            public void onUpdate(ResponseFromServer res) {
                                if (res==null){return;}
                                if (res.getStatus()==0){
                                    //xoa trong danh sach
                                    int d=WDFS_deleteWDFS(wdfs.getmId());
                                    if (d!=0){
                                        Log.d(TAG, "Synchronize DMHaiSan, delete like client: ---------------- " + d +"");
                                    }
                                }
                            }
                        });
                        task.execute(DeleteURL);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "on Synchronize: "+e.toString() );
                    }
                }
                if (wdfs.getmTablename().equals("banhsdetail")){
                    try{
                        String DeleteURL=BASE_URL_BANHSDETAIL+ "/delete/" + wdfs.getmServerkey() + "/";
                        restfullAPI.deleteBanHSDetail  task = new restfullAPI.deleteBanHSDetail();
                        task.setUpdateListener(new restfullAPI.deleteBanHSDetail.OnUpdateListener(){
                            @Override
                            public void onUpdate(ResponseFromServer res) {
                                if (res==null){return;}
                                if (res.getStatus()==0){
                                    //xoa trong danh sach
                                    int d=WDFS_deleteWDFS(wdfs.getmId());
                                    if (d!=0){
                                        Log.d(TAG, "Synchronize BanHSdetail, delete like client: ---------------- " + d +"");
                                    }
                                }
                            }
                        });
                        task.execute(DeleteURL);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "on Synchronize: "+e.toString() );
                    }
                }
            }

        }

        // ****************************************************Xoa theo server****************************
        ArrayList<Users>arrUsers=Users_getAllUsers();
        ArrayList<DSTV>arrDSTV=DSTV_getAllDSTV();
        ArrayList<ChuyenBien>arrChuyenBien=ChuyenBien_getAllChuyenBien();
        ArrayList<DiemDD>arrDiemDD=DiemDD_getAllDiemDD();
        ArrayList<KhachHang>arrKhachHang=KhachHang_getAllKhachHang();
        ArrayList<DoiTac>arrDoiTac=DoiTac_getAllDoiTac();
        ArrayList<Ticket>arrTicket=Ticket_getAllTicket();
        ArrayList<TicketDetail>arrTicketDetail=TicketDetail_getAllTicketDetail();
        ArrayList<Thu>arrThu=Thu_getAllThu();
        ArrayList<ImgStore> arrImgStore=ImgStore_getAllImgStore();
        ArrayList<BanHSDetail>arrBanHSDetail=BanHSDetail_getAllBanHSDetail();
        ArrayList<DMHaiSan>arrDMHaiSan=DMHaiSan_getAllDMHaiSan();
        ArrayList<DebtBook>arrDebtBook=DebtBook_getAllDebtBook();
        ArrayList<Chi>arrChi=Chi_getAllChi();
        ArrayList<ChiDetail>arrChiDetail=ChiDetail_getAllChiDetail();
        ArrayList<ThuDetail>arrThuDetail=ThuDetail_getAllThuDetail();

        for (int i = 0; i < arrUsers.size(); i++) {
            final Users users =arrUsers.get(i);
            if (users.getServerkey()!=0){
                String myurl=BASE_URL_USERS +"/count/"+ users.getServerkey() +"/";
                restfullAPI.countOnServer  countTask = new restfullAPI.countOnServer();
                countTask.setUpdateListener(new restfullAPI.countOnServer.OnUpdateListener(){
                    @Override
                    public void onUpdate(ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            //xoa trong danh sach
                            int d=Users_deleteUsers(users.getId());
                            if (d!=0){
                                Log.d(TAG, "Synchronize Users, delete like server: ---------------- " + d +"");
                            }
                        }
                    }
                });
                countTask.execute(myurl);
            }
        }
        for (int i = 0; i < arrDSTV.size(); i++) {
            final DSTV dstv=arrDSTV.get(i);
            if (dstv.getServerkey()!=0){
                String myurl=BASE_URL_DSTV+"/count/"+ dstv.getServerkey() +"/";
                restfullAPI.countOnServer  countTask = new restfullAPI.countOnServer();
                countTask.setUpdateListener(new restfullAPI.countOnServer.OnUpdateListener(){
                    @Override
                    public void onUpdate( ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            //xoa trong danh sach
                            int d=DSTV_deleteDSTV(dstv.getId());
                            if (d!=0){
                                Log.d(TAG, "Synchronize DSTV, delete like server: ---------------- " + d +"");
                            }
                        }
                    }
                });
                countTask.execute(myurl);
            }
        }
        for (int i = 0; i < arrChuyenBien.size(); i++) {
            final ChuyenBien chuyenbien=arrChuyenBien.get(i);
            if (chuyenbien.getServerkey()!=0){
                String myurl=BASE_URL_CHUYENBIEN+"/count/"+ chuyenbien.getServerkey() +"/";
                restfullAPI.countOnServer  countTask = new restfullAPI.countOnServer();
                countTask.setUpdateListener(new restfullAPI.countOnServer.OnUpdateListener(){
                    @Override
                    public void onUpdate( ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            //xoa trong danh sach
                            int d=DSTV_deleteDSTV(chuyenbien.getId());
                            if (d!=0){
                                Log.d(TAG, "Synchronize ChuyenBien, delete like server: ---------------- " + d +"");
                            }
                        }
                    }
                });
                countTask.execute(myurl);
            }
        }

        for (int i = 0; i < arrDiemDD.size(); i++) {
            final DiemDD diemdd=arrDiemDD.get(i);
            if (diemdd.getServerkey()!=0){
                final String myurl=BASE_URL_DIEMDD+"/count/"+ diemdd.getServerkey() +"/";
                restfullAPI.countOnServer  countTask = new restfullAPI.countOnServer();
                countTask.setUpdateListener(new restfullAPI.countOnServer.OnUpdateListener(){
                    @Override
                    public void onUpdate( ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            //xoa trong danh sach
                            int d=DiemDD_deleteDiemDD(diemdd.getId());
                            if (d!=0){
                                Log.d(TAG, "Synchronize DiemDD, delete like server: ---------------- " + d +"");
                            }
                        }
                    }
                });
                countTask.execute(myurl);
            }
        }

        for (int i = 0; i < arrKhachHang.size(); i++) {
            final KhachHang khachhang=arrKhachHang.get(i);
            if (khachhang.getServerkey()!=0){
                final String myurl=BASE_URL_KHACHHANG+"/count/"+ khachhang.getServerkey() +"/";
                restfullAPI.countOnServer  countTask = new restfullAPI.countOnServer();
                countTask.setUpdateListener(new restfullAPI.countOnServer.OnUpdateListener(){
                    @Override
                    public void onUpdate( ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            //xoa trong danh sach
                            int d=KhachHang_deleteKhachHang(khachhang.getId());
                            if (d!=0){
                                Log.d(TAG, "Synchronize KhachHang, delete like server: ---------------- " + d +"");
                            }
                        }
                    }
                });
                countTask.execute(myurl);
            }
        }

        for (int i = 0; i < arrDoiTac.size(); i++) {
            final DoiTac doitac=arrDoiTac.get(i);
            if (doitac.getServerkey()!=0){
                final String myurl=BASE_URL_DOITAC+"/count/"+ doitac.getServerkey() +"/";
                restfullAPI.countOnServer  countTask = new restfullAPI.countOnServer();
                countTask.setUpdateListener(new restfullAPI.countOnServer.OnUpdateListener(){
                    @Override
                    public void onUpdate( ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            //xoa trong danh sach
                            int d=DoiTac_deleteDoiTac(doitac.getId());
                            if (d!=0){
                                Log.d(TAG, "Synchronize DoiTac, delete like server: ---------------- " + d +"");
                            }
                        }
                    }
                });
                countTask.execute(myurl);
            }
        }

        for (int i = 0; i < arrTicket.size(); i++) {
            final Ticket ticket=arrTicket.get(i);
            if (ticket.getServerkey()!=0){
                final String myurl=BASE_URL_TICKET+"/count/"+ ticket.getServerkey() +"/";
                restfullAPI.countOnServer  countTask = new restfullAPI.countOnServer();
                countTask.setUpdateListener(new restfullAPI.countOnServer.OnUpdateListener(){
                    @Override
                    public void onUpdate( ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            //xoa trong danh sach
                            int d=Ticket_deleteTicket(ticket.getId());
                            if (d!=0){
                                Log.d(TAG, "Synchronize Ticket, delete like server: ---------------- " + d +"");
                            }
                        }
                    }
                });
                countTask.execute(myurl);
            }
        }

        for (int i = 0; i < arrTicketDetail.size(); i++) {
            final TicketDetail ticketdetail=arrTicketDetail.get(i);
            if (ticketdetail.getServerkey()!=0){
                final String myurl=BASE_URL_TICKETDETAIL+"/count/"+ ticketdetail.getServerkey() +"/";
                restfullAPI.countOnServer  countTask = new restfullAPI.countOnServer();
                countTask.setUpdateListener(new restfullAPI.countOnServer.OnUpdateListener(){
                    @Override
                    public void onUpdate( ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            //xoa trong danh sach
                            int d=TicketDetail_deleteTicketDetail(ticketdetail.getId());
                            if (d!=0){
                                Log.d(TAG, "Synchronize TicketDetail, delete like server: ---------------- " + d +"");
                            }
                        }
                    }
                });
                countTask.execute(myurl);
            }
        }

        for (int i = 0; i < arrThu.size(); i++) {
            final Thu thu=arrThu.get(i);
            if (thu.getServerkey()!=0){
                final String myurl=BASE_URL_THU+"/count/"+ thu.getServerkey() +"/";
                restfullAPI.countOnServer  countTask = new restfullAPI.countOnServer();
                countTask.setUpdateListener(new restfullAPI.countOnServer.OnUpdateListener(){
                    @Override
                    public void onUpdate( ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            //xoa trong danh sach
                            int d=Thu_deleteThu(thu.getId());
                            if (d!=0){
                                Log.d(TAG, "Synchronize Thu, delete like server: ---------------- " + d +"");
                            }
                        }
                    }
                });
                countTask.execute(myurl);
            }
        }

        for (int i = 0; i < arrThuDetail.size(); i++) {
            final ThuDetail thudetail=arrThuDetail.get(i);
            if (thudetail.getServerkey()!=0){
                final String myurl=BASE_URL_THUDETAIL+"/count/"+ thudetail.getServerkey() +"/";
                restfullAPI.countOnServer  countTask = new restfullAPI.countOnServer();
                countTask.setUpdateListener(new restfullAPI.countOnServer.OnUpdateListener(){
                    @Override
                    public void onUpdate( ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            //xoa trong danh sach
                            int d=ThuDetail_deleteThuDetail(thudetail.getId());
                            if (d!=0){
                                Log.d(TAG, "Synchronize ThuDetail, delete like server: ---------------- " + d +"");
                            }
                        }
                    }
                });
                countTask.execute(myurl);
            }
        }
        for (int i = 0; i < arrChi.size(); i++) {
            final Chi chi=arrChi.get(i);
            if (chi.getServerkey()!=0){
                final String myurl=BASE_URL_CHI +"/count/"+ chi.getServerkey() +"/";
                restfullAPI.countOnServer  countTask = new restfullAPI.countOnServer();
                countTask.setUpdateListener(new restfullAPI.countOnServer.OnUpdateListener(){
                    @Override
                    public void onUpdate( ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            //xoa trong danh sach
                            int d=Chi_deleteChi(chi.getId());
                            if (d!=0){
                                Log.d(TAG, "Synchronize Chi, delete like server: ---------------- " + d +"");
                            }
                        }
                    }
                });
                countTask.execute(myurl);
            }
        }

        for (int i = 0; i < arrChiDetail.size(); i++) {
            final ChiDetail chidetail=arrChiDetail.get(i);
            if (chidetail.getServerkey()!=0){
                final String myurl=BASE_URL_CHIDETAIL+"/count/"+ chidetail.getServerkey() +"/";
                restfullAPI.countOnServer  countTask = new restfullAPI.countOnServer();
                countTask.setUpdateListener(new restfullAPI.countOnServer.OnUpdateListener(){
                    @Override
                    public void onUpdate( ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            //xoa trong danh sach
                            int d=ChiDetail_deleteChiDetail(chidetail.getId());
                            if (d!=0){
                                Log.d(TAG, "Synchronize ChiDetail, delete like server: ---------------- " + d +"");
                            }
                        }
                    }
                });
                countTask.execute(myurl);
            }
        }

        for (int i = 0; i < arrDebtBook.size(); i++) {
            final DebtBook debtbook=arrDebtBook.get(i);
            if (debtbook.getServerkey()!=0){
                final String myurl=BASE_URL_DEBTBOOK+"/count/"+ debtbook.getServerkey() +"/";
                restfullAPI.countOnServer  countTask = new restfullAPI.countOnServer();
                countTask.setUpdateListener(new restfullAPI.countOnServer.OnUpdateListener(){
                    @Override
                    public void onUpdate( ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            //xoa trong danh sach
                            int d=DebtBook_deleteDebtBook(debtbook.getId());
                            if (d!=0){
                                Log.d(TAG, "Synchronize DebtBook, delete like server: ---------------- " + d +"");
                            }
                        }
                    }
                });
                countTask.execute(myurl);
            }
        }

        for (int i = 0; i < arrDMHaiSan.size(); i++) {
            final DMHaiSan dmhaisan=arrDMHaiSan.get(i);
            if (dmhaisan.getServerkey()!=0){
                final String myurl=BASE_URL_DMHAISAN+"/count/"+ dmhaisan.getServerkey() +"/";
                restfullAPI.countOnServer  countTask = new restfullAPI.countOnServer();
                countTask.setUpdateListener(new restfullAPI.countOnServer.OnUpdateListener(){
                    @Override
                    public void onUpdate( ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            //xoa trong danh sach
                            int d=DMHaiSan_deleteDMHaiSan(dmhaisan.getId());
                            if (d!=0){
                                Log.d(TAG, "Synchronize DMHaiSan, delete like server: ---------------- " + d +"");
                            }
                        }
                    }
                });
                countTask.execute(myurl);
            }
        }

        for (int i = 0; i < arrBanHSDetail.size(); i++) {
            final BanHSDetail banhsdetail=arrBanHSDetail.get(i);
            if (banhsdetail.getServerkey()!=0){
                final String myurl=BASE_URL_BANHSDETAIL+"/count/"+ banhsdetail.getServerkey() +"/";
                restfullAPI.countOnServer  countTask = new restfullAPI.countOnServer();
                countTask.setUpdateListener(new restfullAPI.countOnServer.OnUpdateListener(){
                    @Override
                    public void onUpdate( ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            //xoa trong danh sach
                            int d=BanHSDetail_deleteBanHSDetail(banhsdetail.getId());
                            if (d!=0){
                                Log.d(TAG, "Synchronize BánHDetail, delete like server: ---------------- " + d +"");
                            }
                        }
                    }
                });
                countTask.execute(myurl);
            }
        }

        for (int i = 0; i < arrImgStore.size(); i++) {
            final ImgStore imgstore=arrImgStore.get(i);
            if (imgstore.getServerkey()!=0){
                final String myurl=BASE_URL_IMGSTORE + "/count/" + imgstore.getServerkey() +"/";
                restfullAPI.countOnServer  countTask = new restfullAPI.countOnServer();
                countTask.setUpdateListener(new restfullAPI.countOnServer.OnUpdateListener(){
                    @Override
                    public void onUpdate( ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            //xoa trong danh sach
                            int d=ImgStore_deleteImgStore(imgstore);
                            if (d!=0){
                                Log.d(TAG, "Synchronize ImgStore, delete like server: ---------------- " + d +"");
                            }
                        }
                    }
                });
                countTask.execute(myurl);
            }
        }
    }

    //*****************************PULL WHAT NEW FROM SERVER*********************************

    private void SyncPullFromServer(){
        ArrayList<WantDeleteFromServer>arrWDFS=WDFS_getAllWDFS();
        Log.d(TAG, "SyncPullFromServer: ------------------------SyncPullFromServer------------------------------");
        restfullAPI.getUser  pullUsers = new restfullAPI.getUser();
        pullUsers.setUpdateListener(new restfullAPI.getUser.OnUpdateListener(){
            ArrayList<Users> arrServerUsers=new ArrayList<>();
            @Override
            public void onUpdate(ArrayList<Users> obj) {
                if (obj==null){return;}
                arrServerUsers=obj;
                ArrayList<Users>arrLocalUsers=new ArrayList<>();
                arrLocalUsers=Users_getAllUsers();
                if (arrServerUsers.size()>arrLocalUsers.size()){
                    for (int i=0; i<arrServerUsers.size();i++){
                        final Users serverUsers=arrServerUsers.get(i);
                        if (serverUsers.getServerkey()!=0){
                            if (!Users_Exits(serverUsers.getServerkey())){
                                if (!WDFS_Exits(arrWDFS,"users",serverUsers.getServerkey())){
                                    Uri uri = Users_addUsers(serverUsers);
                                    if (uri!=null){
                                        Log.d(TAG, "Pull Users From Server: ----------------------" + serverUsers.getServerkey());
                                    }
                                }
                            }
                        }

                    }

                }
            }
        });
        pullUsers.execute(BASE_URL_USERS);

        restfullAPI.getDSTV  pullDSTV = new restfullAPI.getDSTV();
        pullDSTV.setUpdateListener(new restfullAPI.getDSTV.OnUpdateListener(){
            ArrayList<DSTV> arrServerDSTV=new ArrayList<>();
            @Override
            public void onUpdate(ArrayList<DSTV> obj) {
                if (obj==null){return;}
                arrServerDSTV=obj;
                ArrayList<DSTV>arrLocalDSTV=DSTV_getAllDSTV();
                if (arrServerDSTV.size()>arrLocalDSTV.size()){
                    for (int i=0; i<arrServerDSTV.size();i++){
                        final DSTV serverDSTV=arrServerDSTV.get(i);
                        if (serverDSTV.getServerkey()!=0){
                            if (!DSTV_Exits(serverDSTV.getServerkey())){
                                if (!WDFS_Exits(arrWDFS,"dstv",serverDSTV.getServerkey())){
                                    Uri uri = DSTV_addDSTV(serverDSTV);
                                    if (uri!=null){
                                        Log.d(TAG, "Pull DSTV From Server: ----------------------" + serverDSTV.getServerkey());
                                    }
                                }
                            }
                        }

                    }

                }
            }
        });
        pullDSTV.execute(BASE_URL_DSTV);

        restfullAPI.getChuyenBien  pullChuyenBien = new restfullAPI.getChuyenBien();
        pullChuyenBien.setUpdateListener(new restfullAPI.getChuyenBien.OnUpdateListener(){
            ArrayList<ChuyenBien> arrServerChuyenBien=new ArrayList<>();
            @Override
            public void onUpdate(ArrayList<ChuyenBien> obj) {
                if (obj==null){return;}
                arrServerChuyenBien=obj;
                ArrayList<ChuyenBien>arrLocalChuyenBien=new ArrayList<>();
                arrLocalChuyenBien=ChuyenBien_getAllChuyenBien();
                if (arrServerChuyenBien.size()>arrLocalChuyenBien.size()){
                    for (int i=0; i<arrServerChuyenBien.size();i++){
                        final ChuyenBien serverChuyenBien=arrServerChuyenBien.get(i);
                        if (serverChuyenBien.getServerkey()!=0){
                            if (!ChuyenBien_Exits(serverChuyenBien.getServerkey())){
                                if (!WDFS_Exits(arrWDFS,"chuyenbien",serverChuyenBien.getServerkey())){
                                    Uri uri = ChuyenBien_addChuyenBien(serverChuyenBien);
                                    if (uri!=null){
                                        Log.d(TAG, "Pull ChuyenBien From Server: ----------------------" + serverChuyenBien.getServerkey());
                                    }
                                }
                            }
                        }

                    }

                }
            }
        });
        pullChuyenBien.execute(BASE_URL_CHUYENBIEN);

        restfullAPI.getDiemDD  pullDiemDD = new restfullAPI.getDiemDD();
        pullDiemDD.setUpdateListener(new restfullAPI.getDiemDD.OnUpdateListener(){
            ArrayList<DiemDD> arrServerDiemDD=new ArrayList<>();
            @Override
            public void onUpdate(ArrayList<DiemDD> obj) {
                if (obj==null){return;}
                arrServerDiemDD=obj;
                ArrayList<DiemDD>arrLocalDiemDD=new ArrayList<>();
                arrLocalDiemDD=DiemDD_getAllDiemDD();
                if (arrServerDiemDD.size()>arrLocalDiemDD.size()){
                    for (int i=0; i<arrServerDiemDD.size();i++){
                        final DiemDD serverDiemDD=arrServerDiemDD.get(i);
                        if (serverDiemDD.getServerkey()!=0){
                            if (!DiemDD_Exits(serverDiemDD.getServerkey())){
                                if (!WDFS_Exits(arrWDFS,"diemdd",serverDiemDD.getServerkey())){
                                    Uri uri = DiemDD_addDiemDD(serverDiemDD);
                                    if (uri!=null){
                                        Log.d(TAG, "Pull DiemDD From Server: ----------------------" + serverDiemDD.getServerkey());
                                    }
                                }
                            }
                        }

                    }

                }
            }
        });
        pullDiemDD.execute(BASE_URL_DIEMDD);

        restfullAPI.getKhachHang  pullKhachHang = new restfullAPI.getKhachHang();
        pullKhachHang.setUpdateListener(new restfullAPI.getKhachHang.OnUpdateListener(){
            ArrayList<KhachHang> arrServerKhachHang=new ArrayList<>();
            @Override
            public void onUpdate(ArrayList<KhachHang> obj) {
                if (obj==null){return;}
                arrServerKhachHang=obj;
                ArrayList<KhachHang>arrLocalKhachHang=new ArrayList<>();
                arrLocalKhachHang=KhachHang_getAllKhachHang();
                if (arrServerKhachHang.size()>arrLocalKhachHang.size()){
                    for (int i=0; i<arrServerKhachHang.size();i++){
                        final KhachHang serverKhachHang=arrServerKhachHang.get(i);
                        if (serverKhachHang.getServerkey()!=0){
                            if (!KhachHang_Exits(serverKhachHang.getServerkey())){
                                if (!WDFS_Exits(arrWDFS,"khachhang",serverKhachHang.getServerkey())){
                                    Uri uri = KhachHang_addKhachHang(serverKhachHang);
                                    if (uri!=null){
                                        Log.d(TAG, "Pull KhachHang From Server: ----------------------" + serverKhachHang.getServerkey());
                                    }
                                }
                            }
                        }

                    }

                }
            }
        });
        pullKhachHang.execute(BASE_URL_KHACHHANG);

        restfullAPI.getDoiTac  pullDoiTac = new restfullAPI.getDoiTac();
        pullDoiTac.setUpdateListener(new restfullAPI.getDoiTac.OnUpdateListener(){
            ArrayList<DoiTac> arrServerDoiTac=new ArrayList<>();
            @Override
            public void onUpdate(ArrayList<DoiTac> obj) {
                if (obj==null){return;}
                arrServerDoiTac=obj;
                ArrayList<DoiTac>arrLocalDoiTac=new ArrayList<>();
                arrLocalDoiTac=DoiTac_getAllDoiTac();
                if (arrServerDoiTac.size()>arrLocalDoiTac.size()){
                    for (int i=0; i<arrServerDoiTac.size();i++){
                        final DoiTac serverDoiTac=arrServerDoiTac.get(i);
                        if (serverDoiTac.getServerkey()!=0){
                            if (!DoiTac_Exits(serverDoiTac.getServerkey())){
                                if (!WDFS_Exits(arrWDFS,"doitac",serverDoiTac.getServerkey())){
                                    Uri uri = DoiTac_addDoiTac(serverDoiTac);
                                    if (uri!=null){
                                        Log.d(TAG, "Pull DoiTac From Server: ----------------------" + serverDoiTac.getServerkey());
                                    }
                                }
                            }
                        }

                    }

                }
            }
        });
        pullDoiTac.execute(BASE_URL_DOITAC);

        restfullAPI.getTicket  pullTicket = new restfullAPI.getTicket();
        pullTicket.setUpdateListener(new restfullAPI.getTicket.OnUpdateListener(){
            ArrayList<Ticket> arrServerTicket=new ArrayList<>();
            @Override
            public void onUpdate(ArrayList<Ticket> obj) {
                if (obj==null){return;}
                arrServerTicket=obj;
                ArrayList<Ticket>arrLocalTicket=new ArrayList<>();
                arrLocalTicket=Ticket_getAllTicket();
                if (arrServerTicket.size()>arrLocalTicket.size()){
                    for (int i=0; i<arrServerTicket.size();i++){
                        final Ticket serverTicket=arrServerTicket.get(i);
                        if (serverTicket.getServerkey()!=0){
                            if (!Ticket_Exits(serverTicket.getServerkey())){
                                if (!WDFS_Exits(arrWDFS,"ticket",serverTicket.getServerkey())){
                                    Uri uri = Ticket_addTicket(serverTicket);
                                    if (uri!=null){
                                        Log.d(TAG, "Pull Ticket From Server: ----------------------" + serverTicket.getServerkey());
                                    }
                                }
                            }
                        }

                    }

                }
            }
        });
        pullTicket.execute(BASE_URL_TICKET);

        restfullAPI.getTicketDetail  pullTicketDetail = new restfullAPI.getTicketDetail();
        pullTicketDetail.setUpdateListener(new restfullAPI.getTicketDetail.OnUpdateListener(){
            ArrayList<TicketDetail> arrServerTicketDetail=new ArrayList<>();
            @Override
            public void onUpdate(ArrayList<TicketDetail> obj) {
                if (obj==null){return;}
                arrServerTicketDetail=obj;
                ArrayList<TicketDetail>arrLocalTicketDetail=new ArrayList<>();
                arrLocalTicketDetail=TicketDetail_getAllTicketDetail();
                if (arrServerTicketDetail.size()>arrLocalTicketDetail.size()){
                    for (int i=0; i<arrServerTicketDetail.size();i++){
                        final TicketDetail serverTicketDetail=arrServerTicketDetail.get(i);
                        if (serverTicketDetail.getServerkey()!=0){
                            if (!TicketDetail_Exits(serverTicketDetail.getServerkey())){
                                if (!WDFS_Exits(arrWDFS,"ticketdetail",serverTicketDetail.getServerkey())){
                                    Uri uri = TicketDetail_addTicketDetail(serverTicketDetail);
                                    if (uri!=null){
                                        Log.d(TAG, "Pull TicketDetail From Server: ----------------------" + serverTicketDetail.getServerkey());
                                    }
                                }
                            }
                        }

                    }

                }
            }
        });
        pullTicketDetail.execute(BASE_URL_TICKETDETAIL);

        restfullAPI.getThu  pullThu = new restfullAPI.getThu();
        pullThu.setUpdateListener(new restfullAPI.getThu.OnUpdateListener(){
            ArrayList<Thu> arrServerThu=new ArrayList<>();
            @Override
            public void onUpdate(ArrayList<Thu> obj) {
                if (obj==null){return;}
                arrServerThu=obj;
                ArrayList<Thu>arrLocalThu=new ArrayList<>();
                arrLocalThu=Thu_getAllThu();
                if (arrServerThu.size()>arrLocalThu.size()){
                    for (int i=0; i<arrServerThu.size();i++){
                        final Thu serverThu=arrServerThu.get(i);
                        if (serverThu.getServerkey()!=0){
                            if (!Thu_Exits(serverThu.getServerkey())){
                                if (!WDFS_Exits(arrWDFS,"thu",serverThu.getServerkey())){
                                    Uri uri = Thu_addThu(serverThu);
                                    if (uri!=null){
                                        Log.d(TAG, "Pull Thu From Server: ----------------------" + serverThu.getServerkey());
                                    }
                                }
                            }
                        }

                    }

                }
            }
        });
        pullThu.execute(BASE_URL_THU);

        restfullAPI.getThuDetail  pullThuDetail = new restfullAPI.getThuDetail();
        pullThuDetail.setUpdateListener(new restfullAPI.getThuDetail.OnUpdateListener(){
            ArrayList<ThuDetail> arrServerThuDetail=new ArrayList<>();
            @Override
            public void onUpdate(ArrayList<ThuDetail> obj) {
                if (obj==null){return;}
                arrServerThuDetail=obj;
                ArrayList<ThuDetail>arrLocalThuDetail=new ArrayList<>();
                arrLocalThuDetail=ThuDetail_getAllThuDetail();
                if (arrServerThuDetail.size()>arrLocalThuDetail.size()){
                    for (int i=0; i<arrServerThuDetail.size();i++){
                        final ThuDetail serverThuDetail=arrServerThuDetail.get(i);
                        if (serverThuDetail.getServerkey()!=0){
                            if (!ThuDetail_Exits(serverThuDetail.getServerkey())){
                                if (!WDFS_Exits(arrWDFS,"thudetail",serverThuDetail.getServerkey())){
                                    Uri uri = ThuDetail_addThuDetail(serverThuDetail);
                                    if (uri!=null){
                                        Log.d(TAG, "Pull ThuDetail From Server: ----------------------" + serverThuDetail.getServerkey());
                                    }
                                }
                            }
                        }

                    }

                }
            }
        });
        pullThuDetail.execute(BASE_URL_THUDETAIL);

        restfullAPI.getChi  pullChi = new restfullAPI.getChi();
        pullChi.setUpdateListener(new restfullAPI.getChi.OnUpdateListener(){
            ArrayList<Chi> arrServerChi=new ArrayList<>();
            @Override
            public void onUpdate(ArrayList<Chi> obj) {
                if (obj==null){return;}
                arrServerChi=obj;
                ArrayList<Chi>arrLocalChi=new ArrayList<>();
                arrLocalChi=Chi_getAllChi();
                if (arrServerChi.size()>arrLocalChi.size()){
                    for (int i=0; i<arrServerChi.size();i++){
                        final Chi serverChi=arrServerChi.get(i);
                        if (serverChi.getServerkey()!=0){
                            if (!Chi_Exits(serverChi.getServerkey())){
                                if (!WDFS_Exits(arrWDFS,"chi",serverChi.getServerkey())){
                                    Uri uri = Chi_addChi(serverChi);
                                    if (uri!=null){
                                        Log.d(TAG, "Pull Chi From Server: ----------------------" + serverChi.getServerkey());
                                    }
                                }
                            }
                        }

                    }

                }
            }
        });
        pullChi.execute(BASE_URL_CHI);

        restfullAPI.getChiDetail  pullChiDetail = new restfullAPI.getChiDetail();
        pullChiDetail.setUpdateListener(new restfullAPI.getChiDetail.OnUpdateListener(){
            ArrayList<ChiDetail> arrServerChiDetail=new ArrayList<>();
            @Override
            public void onUpdate(ArrayList<ChiDetail> obj) {
                if (obj==null){return;}
                arrServerChiDetail=obj;
                ArrayList<ChiDetail>arrLocalChiDetail=new ArrayList<>();
                arrLocalChiDetail=ChiDetail_getAllChiDetail();
                if (arrServerChiDetail.size()>arrLocalChiDetail.size()){
                    for (int i=0; i<arrServerChiDetail.size();i++){
                        final ChiDetail serverChiDetail=arrServerChiDetail.get(i);
                        if (serverChiDetail.getServerkey()!=0){
                            if (!ChiDetail_Exits(serverChiDetail.getServerkey())){
                                if (!WDFS_Exits(arrWDFS,"chidetail",serverChiDetail.getServerkey())){
                                    Uri uri = ChiDetail_addChiDetail(serverChiDetail);
                                    if (uri!=null){
                                        Log.d(TAG, "Pull ChiDetail From Server: ----------------------" + serverChiDetail.getServerkey());
                                    }
                                }
                            }
                        }

                    }

                }
            }
        });
        pullChiDetail.execute(BASE_URL_CHIDETAIL);

        restfullAPI.getDebtBook  pullDebtBook = new restfullAPI.getDebtBook();
        pullDebtBook.setUpdateListener(new restfullAPI.getDebtBook.OnUpdateListener(){
            ArrayList<DebtBook> arrServerDebtBook=new ArrayList<>();
            @Override
            public void onUpdate(ArrayList<DebtBook> obj) {
                if (obj==null){return;}
                arrServerDebtBook=obj;
                ArrayList<DebtBook>arrLocalDebtBook=new ArrayList<>();
                arrLocalDebtBook=DebtBook_getAllDebtBook();
                if (arrServerDebtBook.size()>arrLocalDebtBook.size()){
                    for (int i=0; i<arrServerDebtBook.size();i++){
                        final DebtBook serverDebtBook=arrServerDebtBook.get(i);
                        if (serverDebtBook.getServerkey()!=0){
                            if (!DebtBook_Exits(serverDebtBook.getServerkey())){
                                if (!WDFS_Exits(arrWDFS,"debtbook",serverDebtBook.getServerkey())){
                                    Uri uri = DebtBook_addDebtBook(serverDebtBook);
                                    if (uri!=null){
                                        Log.d(TAG, "Pull DebtBook From Server: ----------------------" + serverDebtBook.getServerkey());
                                    }
                                }
                            }
                        }

                    }

                }
            }
        });
        pullDebtBook.execute(BASE_URL_DEBTBOOK);

        restfullAPI.getDMHaiSan  pullDMHaiSan = new restfullAPI.getDMHaiSan();
        pullDMHaiSan.setUpdateListener(new restfullAPI.getDMHaiSan.OnUpdateListener(){
            ArrayList<DMHaiSan> arrServerDMHaiSan=new ArrayList<>();
            @Override
            public void onUpdate(ArrayList<DMHaiSan> obj) {
                if (obj==null){return;}
                arrServerDMHaiSan=obj;
                ArrayList<DMHaiSan>arrLocalDMHaiSan=new ArrayList<>();
                arrLocalDMHaiSan=DMHaiSan_getAllDMHaiSan();
                if (arrServerDMHaiSan.size()>arrLocalDMHaiSan.size()){
                    for (int i=0; i<arrServerDMHaiSan.size();i++){
                        final DMHaiSan serverDMHaiSan=arrServerDMHaiSan.get(i);
                        if (serverDMHaiSan.getServerkey()!=0){
                            if (!DMHaiSan_Exits(serverDMHaiSan.getServerkey())){
                                if (!WDFS_Exits(arrWDFS,"dmhaisan",serverDMHaiSan.getServerkey())){
                                    Uri uri = DMHaiSan_addDMHaiSan(serverDMHaiSan);
                                    if (uri!=null){
                                        Log.d(TAG, "Pull DMHaiSan From Server: ----------------------" + serverDMHaiSan.getServerkey());
                                    }
                                }
                            }
                        }

                    }

                }
            }
        });
        pullDMHaiSan.execute(BASE_URL_DMHAISAN);

        restfullAPI.getBanHSDetail  pullBanHSDetail = new restfullAPI.getBanHSDetail();
        pullBanHSDetail.setUpdateListener(new restfullAPI.getBanHSDetail.OnUpdateListener(){
            ArrayList<BanHSDetail> arrServerBanHSDetail=new ArrayList<>();
            @Override
            public void onUpdate(ArrayList<BanHSDetail> obj) {
                if (obj==null){return;}
                arrServerBanHSDetail=obj;
                ArrayList<BanHSDetail>arrLocalBanHSDetail=new ArrayList<>();
                arrLocalBanHSDetail=BanHSDetail_getAllBanHSDetail();
                if (arrServerBanHSDetail.size()>arrLocalBanHSDetail.size()){
                    for (int i=0; i<arrServerBanHSDetail.size();i++){
                        final BanHSDetail serverBanHSDetail=arrServerBanHSDetail.get(i);
                        if (serverBanHSDetail.getServerkey()!=0){
                            if (!BanHSDetail_Exits(serverBanHSDetail.getServerkey())){
                                if (!WDFS_Exits(arrWDFS,"banhsdetail",serverBanHSDetail.getServerkey())){
                                    Uri uri = BanHSDetail_addBanHSDetail(serverBanHSDetail);
                                    if (uri!=null){
                                        Log.d(TAG, "Pull BanHSDetail From Server: ----------------------" + serverBanHSDetail.getServerkey());
                                    }
                                }
                            }
                        }

                    }

                }
            }
        });
        pullBanHSDetail.execute(BASE_URL_BANHSDETAIL);

        restfullAPI.getImgStore  pullImgStore = new restfullAPI.getImgStore();
        pullImgStore.setUpdateListener(new restfullAPI.getImgStore.OnUpdateListener(){
            ArrayList<ImgStore> arrServerImgStore=new ArrayList<>();
            @Override
            public void onUpdate(ArrayList<ImgStore> obj) {
                if (obj==null){return;}
                arrServerImgStore=obj;
                ArrayList<ImgStore>arrLocalImgStore=new ArrayList<>();
                arrLocalImgStore=ImgStore_getAllImgStore();
                if (arrServerImgStore.size()>arrLocalImgStore.size()){
                    for (int i=0; i<arrServerImgStore.size();i++){
                        final ImgStore serverImgStore=arrServerImgStore.get(i);
                        if (serverImgStore.getServerkey()!=0){
                            if (!ImgStore_Exits(serverImgStore.getServerkey())){
                                if (!WDFS_Exits(arrWDFS,"imgstore",serverImgStore.getServerkey())){
                                    Uri uri = ImgStore_addImgStore(serverImgStore);
                                    if (uri!=null){
                                        Log.d(TAG, "Pull ImgStore From Server: ----------------------" + serverImgStore.getServerkey());
                                    }
                                }
                            }
                        }

                    }

                }
            }
        });
        pullImgStore.execute(BASE_URL_IMGSTORE);
    }

    //*******************************DETECT TWO WAYS EDIT AND SYNC******************************************

    private void SyncEditTwoWay(){
        Log.d(TAG, "SyncEditTwoWay: ------------------------SyncEditTwoWay------------------------------");

        final ArrayList<Users>arrUsers=Users_getAllUsers();
        final ArrayList<DSTV>arrDSTV=DSTV_getAllDSTV();
        final ArrayList<ChuyenBien>arrChuyenBien=ChuyenBien_getAllChuyenBien();
        final ArrayList<DiemDD>arrDiemDD=DiemDD_getAllDiemDD();
        final ArrayList<KhachHang>arrKhachHang=KhachHang_getAllKhachHang();
        final ArrayList<DoiTac>arrDoiTac=DoiTac_getAllDoiTac();
        final ArrayList<Ticket>arrTicket=Ticket_getAllTicket();
        final ArrayList<TicketDetail>arrTicketDetail=TicketDetail_getAllTicketDetail();
        final ArrayList<Thu>arrThu=Thu_getAllThu();
        final ArrayList<ImgStore> arrImgStore=ImgStore_getAllImgStore();
        final ArrayList<BanHSDetail>arrBanHSDetail=BanHSDetail_getAllBanHSDetail();
        final ArrayList<DMHaiSan>arrDMHaiSan=DMHaiSan_getAllDMHaiSan();
        final ArrayList<DebtBook>arrDebtBook=DebtBook_getAllDebtBook();
        final ArrayList<Chi>arrChi=Chi_getAllChi();
        final ArrayList<ChiDetail>arrChiDetail=ChiDetail_getAllChiDetail();
        final ArrayList<ThuDetail>arrThuDetail=ThuDetail_getAllThuDetail();

        restfullAPI.getImgStore  syncImgStore = new restfullAPI.getImgStore();
        syncImgStore.setUpdateListener(new restfullAPI.getImgStore.OnUpdateListener(){
            @Override
            public void onUpdate(ArrayList<ImgStore> arrServerImgStore) throws ExecutionException, InterruptedException {
                if (arrServerImgStore==null || arrImgStore.size()==0){return;}
                if (arrServerImgStore.size()>0){
                    for (int i = 0; i < arrServerImgStore.size(); i++) {
                        final ImgStore serverImgStore=arrServerImgStore.get(i);
                        String serverimg="";

                        if (serverImgStore.getImgpath()!=null){
                            String[] sServer=serverImgStore.getImgpath().split("/");
                            serverimg=sServer[sServer.length-1];
                        }

                        if (arrImgStore.size()>0){
                            for (int j=0; j<arrImgStore.size();j++){
                                final ImgStore localImgstore =arrImgStore.get(j);
                                if (localImgstore.getServerkey()==serverImgStore.getServerkey()){
                                    if (timeGet(localImgstore.getUpdatetime())==timeGet(serverImgStore.getUpdatetime())){
                                        break;
                                    }
                                    if (timeGet(localImgstore.getUpdatetime())>timeGet(serverImgStore.getUpdatetime())){
                                        //sua theo clent
                                        String myurl= BASE_URL_IMGSTORE + "/update/"+ localImgstore.getServerkey() +"/";
                                        final int skey=serverImgStore.getServerkey();
                                        String clientimg="";
                                        if (localImgstore.getImgpath()!=null){
                                            if (!localImgstore.getImgpath().equals("")) {
                                                if (!localImgstore.getImgpath().substring(0,4).equals("http")){
                                                    String[] sCli=localImgstore.getImgpath().split("/");
                                                    clientimg=sCli[sCli.length-1];
                                                }
                                            }
                                        }
                                        //yeu cau server xoa file anh cu truoc neu co
                                        if (serverimg!=clientimg && clientimg!=""){
                                            //yeu cau server xoa file anh cu truoc
                                            new restfullAPI.deleteFileOnServer().execute(BASE_URL_IMGSTORE + "/deletefile/"+skey+"/");
                                        }
                                        //Cap nhat thong tin text moi cho server
                                        new restfullAPI.putImgStore(localImgstore).execute(myurl);

                                        if(serverimg!=clientimg && clientimg!=""){
                                            if (!localImgstore.getImgpath().substring(0,4).equals("http")){
                                                //post new file to server
                                                restfullAPI.postFile taskpostfile =new restfullAPI.postFile();
                                                final int finalJ = j;
                                                ImgStore finalImgStore = localImgstore;
                                                taskpostfile.setUpdateListener(new restfullAPI.postFile.OnUpdateListener() {
                                                    @Override
                                                    public void onUpdate(ResponseFromServer obj) {
                                                        if (obj==null){return;}
                                                        if (obj.getStatus()==0){
                                                            final String ImageHoaDonURL =obj.getMessage();
                                                            if (timeGet(finalImgStore.getUpdatetime())<=NGAY_LUU_ANH){
                                                                String deletefilePath=finalImgStore.getImgpath();
                                                                finalImgStore.setServerkey(skey);
                                                                finalImgStore.setImgpath(ImageHoaDonURL);
                                                                //cap nhat imghoadon cho client theo link server
                                                                int u=ImgStore_updateImgStore(finalImgStore);
                                                                if (u!=0){
                                                                    //delete local file
                                                                    //utils.deleteFile(deletefilePath);
                                                                }
                                                            }
                                                            ImgStore serverImgStore2=finalImgStore;
                                                            serverImgStore2.setImgpath(ImageHoaDonURL);
                                                            //update link cho server img hoa don
                                                            final String myurl2= BASE_URL_IMGSTORE + "/update/"+ finalImgStore.getServerkey() +"/";
                                                            final String oldUpTime=serverImgStore2.getUpdatetime();
                                                            final String newUpTime=finalImgStore.getUpdatetime();
                                                            restfullAPI.putImgStore taskPut = new restfullAPI.putImgStore(serverImgStore2);
                                                            taskPut.setUpdateListener(new restfullAPI.putImgStore.OnUpdateListener(){;
                                                                @Override
                                                                public void onUpdate(ResponseFromServer res) {
                                                                    if (res==null){return;}
                                                                    if (res.getStatus()==0){
                                                                        Log.d(TAG, "ImgStore two ways sync, do like local: ------------local: "+newUpTime+"-----------server: " + oldUpTime);
                                                                    }
                                                                }
                                                            });
                                                            taskPut.execute(myurl2);



                                                        }
                                                    }
                                                });
                                                taskpostfile.execute(new String[]{BASE_URL_IMGSTORE +"/upload/"+String.valueOf(skey),localImgstore.getImgpath()});
                                            }
                                        }
                                    }else {
                                        //sua theo server
                                        ImgStore localImgstore2=serverImgStore;
                                        localImgstore2.setId(localImgstore.getId());
                                        int u=ImgStore_updateImgStore(localImgstore2);
                                        if (u!=0){
                                            Log.d(TAG, "ImgStore two ways sync, do like server: -----------local: "+localImgstore.getUpdatetime()+"-----------server: " + serverImgStore.getUpdatetime());
                                        }
                                    }
                                }
                            }
                        }

                    }
                }

            }
        });
        syncImgStore.execute(BASE_URL_IMGSTORE);

        restfullAPI.getUser syncUsers = new restfullAPI.getUser();
        syncUsers.setUpdateListener(new restfullAPI.getUser.OnUpdateListener(){
            @Override
            public void onUpdate(ArrayList<Users> arrServerUsers) throws ExecutionException, InterruptedException {
                if (arrServerUsers==null || arrUsers.size()==0){return;}
                if (arrServerUsers.size()>0){
                    for (int i = 0; i < arrServerUsers.size(); i++) {
                        final Users serverusers=arrServerUsers.get(i);
                        if (arrUsers.size()>0){
                            for (int j=0; j<arrUsers.size();j++){
                                final Users users=arrUsers.get(j);
                                if (users.getServerkey()==serverusers.getServerkey()){
                                    if (timeGet(users.getUpdatetime())==timeGet(serverusers.getUpdatetime())){
                                        break;
                                    }
                                    if (timeGet(users.getUpdatetime())>timeGet(serverusers.getUpdatetime())){
                                        //sua theo clent
                                        final String myurl=BASE_URL_USERS + "/update/"+ users.getServerkey() +"/";
                                        final String oldUpTime=serverusers.getUpdatetime();
                                        final String newUpTime=users.getUpdatetime();
                                        restfullAPI.putUser taskPut = new restfullAPI.putUser(users);
                                        taskPut.setUpdateListener(new restfullAPI.putUser.OnUpdateListener(){;
                                            @Override
                                            public void onUpdate(ResponseFromServer res) {
                                                if (res==null){return;}
                                                if (res.getStatus()==0){
                                                    Log.d(TAG, "two ways sync Users, do like local: ------------local: " + newUpTime + "-----------server: " + oldUpTime);
                                                }
                                            }
                                        });
                                        taskPut.execute(myurl);
                                    }else{
                                        //sua theo server
                                        final String oldUpTime=users.getUpdatetime();
                                        Users users2local=serverusers;
                                        //sua lai id cho hop ly, k the lay id cua thang server dc
                                        users2local.setId(users.getId());
                                        int u=Users_updateUsers(users2local);
                                        if (u!=0){
                                            Log.d(TAG, "two ways sync Users, do like server: -----------local: "+oldUpTime+"-----------server: " + serverusers.getUpdatetime());
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        });
        syncUsers.execute(BASE_URL_USERS);

        restfullAPI.getDSTV syncDSTV = new restfullAPI.getDSTV();
        syncDSTV.setUpdateListener(new restfullAPI.getDSTV.OnUpdateListener(){
            @Override
            public void onUpdate(ArrayList<DSTV> arrServerDSTV) throws ExecutionException, InterruptedException {
                if (arrServerDSTV==null || arrDSTV.size()==0){return;}
                if (arrServerDSTV.size()>0){
                    for (int i = 0; i < arrServerDSTV.size(); i++) {
                        final DSTV serverdstv=arrServerDSTV.get(i);
                        if (arrDSTV.size()>0){
                            for (int j=0; j<arrDSTV.size();j++){
                                final DSTV dstv=arrDSTV.get(j);
                                if (dstv.getServerkey()==serverdstv.getServerkey()){
                                    if (timeGet(dstv.getUpdatetime())==timeGet(serverdstv.getUpdatetime())){
                                        break;
                                    }
                                    if (timeGet(dstv.getUpdatetime())>timeGet(serverdstv.getUpdatetime())){
                                        //sua theo clent
                                        final String myurl=BASE_URL_DSTV + "/update/"+ dstv.getServerkey() +"/";
                                        final String oldUpTime=serverdstv.getUpdatetime();
                                        final String newUpTime=dstv.getUpdatetime();
                                        restfullAPI.putDSTV taskPut = new restfullAPI.putDSTV(dstv);
                                        taskPut.setUpdateListener(new restfullAPI.putDSTV.OnUpdateListener(){;
                                            @Override
                                            public void onUpdate(ResponseFromServer res) {
                                                if (res==null){return;}
                                                if (res.getStatus()==0){
                                                    Log.d(TAG, "two ways sync DSTV, do like local: ------------local: " + newUpTime + "-----------server: " + oldUpTime);
                                                }
                                            }
                                        });
                                        taskPut.execute(myurl);
                                    }else{
                                        //sua theo server
                                        final String oldUpTime=dstv.getUpdatetime();
                                        DSTV dstv2local=serverdstv;
                                        //sua lai id cho hop ly, k the lay id cua thang server dc
                                        dstv2local.setId(dstv.getId());
                                        int u=DSTV_updateDSTV(dstv2local);
                                        if (u!=0){
                                            Log.d(TAG, "two ways sync DSTV, do like server: -----------local: "+oldUpTime+"-----------server: " + serverdstv.getUpdatetime());
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        });
        syncDSTV.execute(BASE_URL_DSTV);

        restfullAPI.getChuyenBien syncChuyenBien = new restfullAPI.getChuyenBien();
        syncChuyenBien.setUpdateListener(new restfullAPI.getChuyenBien.OnUpdateListener(){
            @Override
            public void onUpdate(ArrayList<ChuyenBien> arrServerChuyenBien) throws ExecutionException, InterruptedException {
                if (arrServerChuyenBien==null || arrChuyenBien.size()==0){return;}
                if (arrServerChuyenBien.size()>0){
                    for (int i = 0; i < arrServerChuyenBien.size(); i++) {
                        final ChuyenBien serverchuyenbien=arrServerChuyenBien.get(i);
                        if (arrChuyenBien.size()>0){
                            for (int j=0; j<arrChuyenBien.size();j++){
                                final ChuyenBien chuyenbien=arrChuyenBien.get(j);
                                if (chuyenbien.getServerkey()==serverchuyenbien.getServerkey()){
                                    if (timeGet(chuyenbien.getUpdatetime())==timeGet(serverchuyenbien.getUpdatetime())){
                                        break;
                                    }
                                    if (timeGet(chuyenbien.getUpdatetime())>timeGet(serverchuyenbien.getUpdatetime())){
                                        //sua theo clent
                                        final String myurl=BASE_URL_CHUYENBIEN + "/update/"+ chuyenbien.getServerkey() +"/";
                                        final String oldUpTime=serverchuyenbien.getUpdatetime();
                                        final String newUpTime=chuyenbien.getUpdatetime();
                                        restfullAPI.putChuyenBien taskPut = new restfullAPI.putChuyenBien(chuyenbien);
                                        taskPut.setUpdateListener(new restfullAPI.putChuyenBien.OnUpdateListener(){;
                                            @Override
                                            public void onUpdate(ResponseFromServer res) {
                                                if (res==null){return;}
                                                if (res.getStatus()==0){
                                                    Log.d(TAG, "two ways sync ChuyenBien, do like local: ------------local: " + newUpTime + "-----------server: " + oldUpTime);
                                                }
                                            }
                                        });
                                        taskPut.execute(myurl);
                                    }else{
                                        //sua theo server
                                        final String oldUpTime=chuyenbien.getUpdatetime();
                                        ChuyenBien chuyenbien2local=serverchuyenbien;
                                        //sua lai id cho hop ly, k the lay id cua thang server dc
                                        chuyenbien2local.setId(chuyenbien.getId());
                                        int u=ChuyenBien_updateChuyenBien(chuyenbien2local);
                                        if (u!=0){
                                            Log.d(TAG, "two ways sync ChuyenBien, do like server: -----------local: "+oldUpTime+"-----------server: " + serverchuyenbien.getUpdatetime());
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        });
        syncChuyenBien.execute(BASE_URL_CHUYENBIEN);

        restfullAPI.getDiemDD syncDiemDD = new restfullAPI.getDiemDD();
        syncDiemDD.setUpdateListener(new restfullAPI.getDiemDD.OnUpdateListener(){
            @Override
            public void onUpdate(ArrayList<DiemDD> arrServerDiemDD) throws ExecutionException, InterruptedException {
                if (arrServerDiemDD==null || arrDiemDD.size()==0){return;}
                if (arrServerDiemDD.size()>0){
                    for (int i = 0; i < arrServerDiemDD.size(); i++) {
                        final DiemDD serverdiemdd=arrServerDiemDD.get(i);
                        if (arrDiemDD.size()>0){
                            for (int j=0; j<arrDiemDD.size();j++){
                                final DiemDD diemdd=arrDiemDD.get(j);
                                if (diemdd.getServerkey()==serverdiemdd.getServerkey()){
                                    if (timeGet(diemdd.getUpdatetime())==timeGet(serverdiemdd.getUpdatetime())){
                                        break;
                                    }
                                    if (timeGet(diemdd.getUpdatetime())>timeGet(serverdiemdd.getUpdatetime())){
                                        //sua theo clent
                                        final String myurl=BASE_URL_DIEMDD + "/update/"+ diemdd.getServerkey() +"/";
                                        final String oldUpTime=serverdiemdd.getUpdatetime();
                                        final String newUpTime=diemdd.getUpdatetime();
                                        restfullAPI.putDiemDD taskPut = new restfullAPI.putDiemDD(diemdd);
                                        taskPut.setUpdateListener(new restfullAPI.putDiemDD.OnUpdateListener(){;
                                            @Override
                                            public void onUpdate(ResponseFromServer res) {
                                                if (res==null){return;}
                                                if (res.getStatus()==0){
                                                    Log.d(TAG, "two ways sync DiemDD, do like local: ------------local: " + newUpTime + "-----------server: " + oldUpTime);
                                                }
                                            }
                                        });
                                        taskPut.execute(myurl);
                                    }else{
                                        //sua theo server
                                        final String oldUpTime=diemdd.getUpdatetime();
                                        DiemDD diemdd2local=serverdiemdd;
                                        //sua lai id cho hop ly, k the lay id cua thang server dc
                                        diemdd2local.setId(diemdd.getId());
                                        int u=DiemDD_updateDiemDD(diemdd2local);
                                        if (u!=0){
                                            Log.d(TAG, "two ways sync DiemDD, do like server: -----------local: "+oldUpTime+"-----------server: " + serverdiemdd.getUpdatetime());
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        });
        syncDiemDD.execute(BASE_URL_DIEMDD);

        restfullAPI.getKhachHang syncKhachHang = new restfullAPI.getKhachHang();
        syncKhachHang.setUpdateListener(new restfullAPI.getKhachHang.OnUpdateListener(){
            @Override
            public void onUpdate(ArrayList<KhachHang> arrServerKhachHang) throws ExecutionException, InterruptedException {
                if (arrServerKhachHang==null || arrKhachHang.size()==0){return;}
                if (arrServerKhachHang.size()>0){
                    for (int i = 0; i < arrServerKhachHang.size(); i++) {
                        final KhachHang serverkhachhang=arrServerKhachHang.get(i);
                        if (arrKhachHang.size()>0){
                            for (int j=0; j<arrKhachHang.size();j++){
                                final KhachHang khachhang=arrKhachHang.get(j);
                                if (khachhang.getServerkey()==serverkhachhang.getServerkey()){
                                    if (timeGet(khachhang.getUpdatetime())==timeGet(serverkhachhang.getUpdatetime())){
                                        break;
                                    }
                                    if (timeGet(khachhang.getUpdatetime())>timeGet(serverkhachhang.getUpdatetime())){
                                        //sua theo clent
                                        final String myurl=BASE_URL_KHACHHANG + "/update/"+ khachhang.getServerkey() +"/";
                                        final String oldUpTime=serverkhachhang.getUpdatetime();
                                        final String newUpTime=khachhang.getUpdatetime();
                                        restfullAPI.putKhachHang taskPut = new restfullAPI.putKhachHang(khachhang);
                                        taskPut.setUpdateListener(new restfullAPI.putKhachHang.OnUpdateListener(){;
                                            @Override
                                            public void onUpdate(ResponseFromServer res) {
                                                if (res==null){return;}
                                                if (res.getStatus()==0){
                                                    Log.d(TAG, "two ways sync KhachHang, do like local: ------------local: " + newUpTime + "-----------server: " + oldUpTime);
                                                }
                                            }
                                        });
                                        taskPut.execute(myurl);
                                    }else{
                                        //sua theo server
                                        final String oldUpTime=khachhang.getUpdatetime();
                                        KhachHang khachhang2local=serverkhachhang;
                                        //sua lai id cho hop ly, k the lay id cua thang server dc
                                        khachhang2local.setId(khachhang.getId());
                                        int u=KhachHang_updateKhachHang(khachhang2local);
                                        if (u!=0){
                                            Log.d(TAG, "two ways sync KhachHang, do like server: -----------local: "+oldUpTime+"-----------server: " + serverkhachhang.getUpdatetime());
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        });
        syncKhachHang.execute(BASE_URL_KHACHHANG);

        restfullAPI.getDoiTac syncDoiTac = new restfullAPI.getDoiTac();
        syncDoiTac.setUpdateListener(new restfullAPI.getDoiTac.OnUpdateListener(){
            @Override
            public void onUpdate(ArrayList<DoiTac> arrServerDoiTac) throws ExecutionException, InterruptedException {
                if (arrServerDoiTac==null || arrDoiTac.size()==0){return;}
                if (arrServerDoiTac.size()>0){
                    for (int i = 0; i < arrServerDoiTac.size(); i++) {
                        final DoiTac serverdoitac=arrServerDoiTac.get(i);
                        if (arrDoiTac.size()>0){
                            for (int j=0; j<arrDoiTac.size();j++){
                                final DoiTac doitac=arrDoiTac.get(j);
                                if (doitac.getServerkey()==serverdoitac.getServerkey()){
                                    if (timeGet(doitac.getUpdatetime())==timeGet(serverdoitac.getUpdatetime())){
                                        break;
                                    }
                                    if (timeGet(doitac.getUpdatetime())>timeGet(serverdoitac.getUpdatetime())){
                                        //sua theo clent
                                        final String myurl=BASE_URL_DOITAC + "/update/"+ doitac.getServerkey() +"/";
                                        final String oldUpTime=serverdoitac.getUpdatetime();
                                        final String newUpTime=doitac.getUpdatetime();
                                        restfullAPI.putDoiTac taskPut = new restfullAPI.putDoiTac(doitac);
                                        taskPut.setUpdateListener(new restfullAPI.putDoiTac.OnUpdateListener(){;
                                            @Override
                                            public void onUpdate(ResponseFromServer res) {
                                                if (res==null){return;}
                                                if (res.getStatus()==0){
                                                    Log.d(TAG, "two ways sync DoiTac, do like local: ------------local: " + newUpTime + "-----------server: " + oldUpTime);
                                                }
                                            }
                                        });
                                        taskPut.execute(myurl);
                                    }else{
                                        //sua theo server
                                        final String oldUpTime=doitac.getUpdatetime();
                                        DoiTac doitac2local=serverdoitac;
                                        //sua lai id cho hop ly, k the lay id cua thang server dc
                                        doitac2local.setId(doitac.getId());
                                        int u=DoiTac_updateDoiTac(doitac2local);
                                        if (u!=0){
                                            Log.d(TAG, "two ways sync DoiTac, do like server: -----------local: "+oldUpTime+"-----------server: " + serverdoitac.getUpdatetime());
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        });
        syncDoiTac.execute(BASE_URL_DOITAC);

        restfullAPI.getTicket syncTicket = new restfullAPI.getTicket();
        syncTicket.setUpdateListener(new restfullAPI.getTicket.OnUpdateListener(){
            @Override
            public void onUpdate(ArrayList<Ticket> arrServerTicket) throws ExecutionException, InterruptedException {
                if (arrServerTicket==null || arrTicket.size()==0){return;}
                if (arrServerTicket.size()>0){
                    for (int i = 0; i < arrServerTicket.size(); i++) {
                        final Ticket serverticket=arrServerTicket.get(i);
                        if (arrTicket.size()>0){
                            for (int j=0; j<arrTicket.size();j++){
                                final Ticket ticket=arrTicket.get(j);
                                if (ticket.getServerkey()==serverticket.getServerkey()){
                                    if (timeGet(ticket.getUpdatetime())==timeGet(serverticket.getUpdatetime())){
                                        break;
                                    }
                                    if (timeGet(ticket.getUpdatetime())>timeGet(serverticket.getUpdatetime())){
                                        //sua theo clent
                                        final String myurl=BASE_URL_TICKET + "/update/"+ ticket.getServerkey() +"/";
                                        final String oldUpTime=serverticket.getUpdatetime();
                                        final String newUpTime=ticket.getUpdatetime();
                                        restfullAPI.putTicket taskPut = new restfullAPI.putTicket(ticket);
                                        taskPut.setUpdateListener(new restfullAPI.putTicket.OnUpdateListener(){;
                                            @Override
                                            public void onUpdate(ResponseFromServer res) {
                                                if (res==null){return;}
                                                if (res.getStatus()==0){
                                                    Log.d(TAG, "two ways sync Ticket, do like local: ------------local: " + newUpTime + "-----------server: " + oldUpTime);
                                                }
                                            }
                                        });
                                        taskPut.execute(myurl);
                                    }else{
                                        //sua theo server
                                        final String oldUpTime=ticket.getUpdatetime();
                                        Ticket ticket2local=serverticket;
                                        //sua lai id cho hop ly, k the lay id cua thang server dc
                                        ticket2local.setId(ticket.getId());
                                        int u=Ticket_updateTicket(ticket2local);
                                        if (u!=0){
                                            Log.d(TAG, "two ways sync Ticket, do like server: -----------local: "+oldUpTime+"-----------server: " + serverticket.getUpdatetime());
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        });
        syncTicket.execute(BASE_URL_TICKET);

        restfullAPI.getTicketDetail syncTicketDetail = new restfullAPI.getTicketDetail();
        syncTicketDetail.setUpdateListener(new restfullAPI.getTicketDetail.OnUpdateListener(){
            @Override
            public void onUpdate(ArrayList<TicketDetail> arrServerTicketDetail) throws ExecutionException, InterruptedException {
                if (arrServerTicketDetail==null || arrTicketDetail.size()==0){return;}
                if (arrServerTicketDetail.size()>0){
                    for (int i = 0; i < arrServerTicketDetail.size(); i++) {
                        final TicketDetail serverticketdetail=arrServerTicketDetail.get(i);
                        if (arrTicketDetail.size()>0){
                            for (int j=0; j<arrTicketDetail.size();j++){
                                final TicketDetail ticketdetail=arrTicketDetail.get(j);
                                if (ticketdetail.getServerkey()==serverticketdetail.getServerkey()){
                                    if (timeGet(ticketdetail.getUpdatetime())==timeGet(serverticketdetail.getUpdatetime())){
                                        break;
                                    }
                                    if (timeGet(ticketdetail.getUpdatetime())>timeGet(serverticketdetail.getUpdatetime())){
                                        //sua theo clent
                                        final String myurl=BASE_URL_TICKETDETAIL + "/update/"+ ticketdetail.getServerkey() +"/";
                                        final String oldUpTime=serverticketdetail.getUpdatetime();
                                        final String newUpTime=ticketdetail.getUpdatetime();
                                        restfullAPI.putTicketDetail taskPut = new restfullAPI.putTicketDetail(ticketdetail);
                                        taskPut.setUpdateListener(new restfullAPI.putTicketDetail.OnUpdateListener(){;
                                            @Override
                                            public void onUpdate(ResponseFromServer res) {
                                                if (res==null){return;}
                                                if (res.getStatus()==0){
                                                    Log.d(TAG, "two ways sync TicketDetail, do like local: ------------local: " + newUpTime + "-----------server: " + oldUpTime);
                                                }
                                            }
                                        });
                                        taskPut.execute(myurl);
                                    }else{
                                        //sua theo server
                                        final String oldUpTime=ticketdetail.getUpdatetime();
                                        TicketDetail ticketdetail2local=serverticketdetail;
                                        //sua lai id cho hop ly, k the lay id cua thang server dc
                                        ticketdetail2local.setId(ticketdetail.getId());
                                        int u=TicketDetail_updateTicketDetail(ticketdetail2local);
                                        if (u!=0){
                                            Log.d(TAG, "two ways sync TicketDetail, do like server: -----------local: "+oldUpTime+"-----------server: " + serverticketdetail.getUpdatetime());
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        });
        syncTicketDetail.execute(BASE_URL_TICKETDETAIL);

        restfullAPI.getThu syncThu = new restfullAPI.getThu();
        syncThu.setUpdateListener(new restfullAPI.getThu.OnUpdateListener(){
            @Override
            public void onUpdate(ArrayList<Thu> arrServerThu) throws ExecutionException, InterruptedException {
                if (arrServerThu==null || arrThu.size()==0){return;}
                if (arrServerThu.size()>0){
                    for (int i = 0; i < arrServerThu.size(); i++) {
                        final Thu serverthu=arrServerThu.get(i);
                        if (arrThu.size()>0){
                            for (int j=0; j<arrThu.size();j++){
                                final Thu thu=arrThu.get(j);
                                if (thu.getServerkey()==serverthu.getServerkey()){
                                    if (timeGet(thu.getUpdatetime())==timeGet(serverthu.getUpdatetime())){
                                        break;
                                    }
                                    if (timeGet(thu.getUpdatetime())>timeGet(serverthu.getUpdatetime())){
                                        //sua theo clent
                                        final String myurl=BASE_URL_THU + "/update/"+ thu.getServerkey() +"/";
                                        final String oldUpTime=serverthu.getUpdatetime();
                                        final String newUpTime=thu.getUpdatetime();
                                        restfullAPI.putThu taskPut = new restfullAPI.putThu(thu);
                                        taskPut.setUpdateListener(new restfullAPI.putThu.OnUpdateListener(){;
                                            @Override
                                            public void onUpdate(ResponseFromServer res) {
                                                if (res==null){return;}
                                                if (res.getStatus()==0){
                                                    Log.d(TAG, "two ways sync Thu, do like local: ------------local: " + newUpTime + "-----------server: " + oldUpTime);
                                                }
                                            }
                                        });
                                        taskPut.execute(myurl);
                                    }else{
                                        //sua theo server
                                        final String oldUpTime=thu.getUpdatetime();
                                        Thu thu2local=serverthu;
                                        //sua lai id cho hop ly, k the lay id cua thang server dc
                                        thu2local.setId(thu.getId());
                                        int u=Thu_updateThu(thu2local);
                                        if (u!=0){
                                            Log.d(TAG, "two ways sync Thu, do like server: -----------local: "+oldUpTime+"-----------server: " + serverthu.getUpdatetime());
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        });
        syncThu.execute(BASE_URL_THU);

        restfullAPI.getThuDetail syncThuDetail = new restfullAPI.getThuDetail();
        syncThuDetail.setUpdateListener(new restfullAPI.getThuDetail.OnUpdateListener(){
            @Override
            public void onUpdate(ArrayList<ThuDetail> arrServerThuDetail) throws ExecutionException, InterruptedException {
                if (arrServerThuDetail==null || arrThuDetail.size()==0){return;}
                if (arrServerThuDetail.size()>0){
                    for (int i = 0; i < arrServerThuDetail.size(); i++) {
                        final ThuDetail serverthudetail=arrServerThuDetail.get(i);
                        if (arrThuDetail.size()>0){
                            for (int j=0; j<arrThuDetail.size();j++){
                                final ThuDetail thudetail=arrThuDetail.get(j);
                                if (thudetail.getServerkey()==serverthudetail.getServerkey()){
                                    if (timeGet(thudetail.getUpdatetime())==timeGet(serverthudetail.getUpdatetime())){
                                        break;
                                    }
                                    if (timeGet(thudetail.getUpdatetime())>timeGet(serverthudetail.getUpdatetime())){
                                        //sua theo clent
                                        final String myurl=BASE_URL_THUDETAIL + "/update/"+ thudetail.getServerkey() +"/";
                                        final String oldUpTime=serverthudetail.getUpdatetime();
                                        final String newUpTime=thudetail.getUpdatetime();
                                        restfullAPI.putThuDetail taskPut = new restfullAPI.putThuDetail(thudetail);
                                        taskPut.setUpdateListener(new restfullAPI.putThuDetail.OnUpdateListener(){;
                                            @Override
                                            public void onUpdate(ResponseFromServer res) {
                                                if (res==null){return;}
                                                if (res.getStatus()==0){
                                                    Log.d(TAG, "two ways sync ThuDetail, do like local: ------------local: " + newUpTime + "-----------server: " + oldUpTime);
                                                }
                                            }
                                        });
                                        taskPut.execute(myurl);
                                    }else{
                                        //sua theo server
                                        final String oldUpTime=thudetail.getUpdatetime();
                                        ThuDetail thudetail2local=serverthudetail;
                                        //sua lai id cho hop ly, k the lay id cua thang server dc
                                        thudetail2local.setId(thudetail.getId());
                                        int u=ThuDetail_updateThuDetail(thudetail2local);
                                        if (u!=0){
                                            Log.d(TAG, "two ways sync ThuDetail, do like server: -----------local: "+oldUpTime+"-----------server: " + serverthudetail.getUpdatetime());
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        });
        syncThuDetail.execute(BASE_URL_THUDETAIL);

        restfullAPI.getChi syncChi = new restfullAPI.getChi();
        syncChi.setUpdateListener(new restfullAPI.getChi.OnUpdateListener(){
            @Override
            public void onUpdate(ArrayList<Chi> arrServerChi) throws ExecutionException, InterruptedException {
                if (arrServerChi==null || arrChi.size()==0){return;}
                if (arrServerChi.size()>0){
                    for (int i = 0; i < arrServerChi.size(); i++) {
                        final Chi serverchi=arrServerChi.get(i);
                        if (arrChi.size()>0){
                            for (int j=0; j<arrChi.size();j++){
                                final Chi chi=arrChi.get(j);
                                if (chi.getServerkey()==serverchi.getServerkey()){
                                    if (timeGet(chi.getUpdatetime())==timeGet(serverchi.getUpdatetime())){
                                        break;
                                    }
                                    if (timeGet(chi.getUpdatetime())>timeGet(serverchi.getUpdatetime())){
                                        //sua theo clent
                                        final String myurl=BASE_URL_CHI + "/update/"+ chi.getServerkey() +"/";
                                        final String oldUpTime=serverchi.getUpdatetime();
                                        final String newUpTime=chi.getUpdatetime();
                                        restfullAPI.putChi taskPut = new restfullAPI.putChi(chi);
                                        taskPut.setUpdateListener(new restfullAPI.putChi.OnUpdateListener(){;
                                            @Override
                                            public void onUpdate(ResponseFromServer res) {
                                                if (res==null){return;}
                                                if (res.getStatus()==0){
                                                    Log.d(TAG, "two ways sync Chi, do like local: ------------local: " + newUpTime + "-----------server: " + oldUpTime);
                                                }
                                            }
                                        });
                                        taskPut.execute(myurl);
                                    }else{
                                        //sua theo server
                                        final String oldUpTime=chi.getUpdatetime();
                                        Chi chi2local=serverchi;
                                        //sua lai id cho hop ly, k the lay id cua thang server dc
                                        chi2local.setId(chi.getId());
                                        int u=Chi_updateChi(chi2local);
                                        if (u!=0){
                                            Log.d(TAG, "two ways sync Chi, do like server: -----------local: "+oldUpTime+"-----------server: " + serverchi.getUpdatetime());
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        });
        syncChi.execute(BASE_URL_CHI);

        restfullAPI.getChiDetail syncChiDetail = new restfullAPI.getChiDetail();
        syncChiDetail.setUpdateListener(new restfullAPI.getChiDetail.OnUpdateListener(){
            @Override
            public void onUpdate(ArrayList<ChiDetail> arrServerChiDetail) throws ExecutionException, InterruptedException {
                if (arrServerChiDetail==null || arrChiDetail.size()==0){return;}
                if (arrServerChiDetail.size()>0){
                    for (int i = 0; i < arrServerChiDetail.size(); i++) {
                        final ChiDetail serverchidetail=arrServerChiDetail.get(i);
                        if (arrChiDetail.size()>0){
                            for (int j=0; j<arrChiDetail.size();j++){
                                final ChiDetail chidetail=arrChiDetail.get(j);
                                if (chidetail.getServerkey()==serverchidetail.getServerkey()){
                                    if (timeGet(chidetail.getUpdatetime())==timeGet(serverchidetail.getUpdatetime())){
                                        break;
                                    }
                                    if (timeGet(chidetail.getUpdatetime())>timeGet(serverchidetail.getUpdatetime())){
                                        //sua theo clent
                                        final String myurl=BASE_URL_CHIDETAIL + "/update/"+ chidetail.getServerkey() +"/";
                                        final String oldUpTime=serverchidetail.getUpdatetime();
                                        final String newUpTime=chidetail.getUpdatetime();
                                        restfullAPI.putChiDetail taskPut = new restfullAPI.putChiDetail(chidetail);
                                        taskPut.setUpdateListener(new restfullAPI.putChiDetail.OnUpdateListener(){;
                                            @Override
                                            public void onUpdate(ResponseFromServer res) {
                                                if (res==null){return;}
                                                if (res.getStatus()==0){
                                                    Log.d(TAG, "two ways sync ChiDetail, do like local: ------------local: " + newUpTime + "-----------server: " + oldUpTime);
                                                }
                                            }
                                        });
                                        taskPut.execute(myurl);
                                    }else{
                                        //sua theo server
                                        final String oldUpTime=chidetail.getUpdatetime();
                                        ChiDetail chidetail2local=serverchidetail;
                                        //sua lai id cho hop ly, k the lay id cua thang server dc
                                        chidetail2local.setId(chidetail.getId());
                                        int u=ChiDetail_updateChiDetail(chidetail2local);
                                        if (u!=0){
                                            Log.d(TAG, "two ways sync ChiDetail, do like server: -----------local: "+oldUpTime+"-----------server: " + serverchidetail.getUpdatetime());
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        });
        syncChiDetail.execute(BASE_URL_CHIDETAIL);

        restfullAPI.getDebtBook syncDebtBook = new restfullAPI.getDebtBook();
        syncDebtBook.setUpdateListener(new restfullAPI.getDebtBook.OnUpdateListener(){
            @Override
            public void onUpdate(ArrayList<DebtBook> arrServerDebtBook) throws ExecutionException, InterruptedException {
                if (arrServerDebtBook==null || arrDebtBook.size()==0){return;}
                if (arrServerDebtBook.size()>0){
                    for (int i = 0; i < arrServerDebtBook.size(); i++) {
                        final DebtBook serverdebtbook=arrServerDebtBook.get(i);
                        if (arrDebtBook.size()>0){
                            for (int j=0; j<arrDebtBook.size();j++){
                                final DebtBook debtbook=arrDebtBook.get(j);
                                if (debtbook.getServerkey()==serverdebtbook.getServerkey()){
                                    if (timeGet(debtbook.getUpdatetime())==timeGet(serverdebtbook.getUpdatetime())){
                                        break;
                                    }
                                    if (timeGet(debtbook.getUpdatetime())>timeGet(serverdebtbook.getUpdatetime())){
                                        //sua theo clent
                                        final String myurl=BASE_URL_DEBTBOOK + "/update/"+ debtbook.getServerkey() +"/";
                                        final String oldUpTime=serverdebtbook.getUpdatetime();
                                        final String newUpTime=debtbook.getUpdatetime();
                                        restfullAPI.putDebtBook taskPut = new restfullAPI.putDebtBook(debtbook);
                                        taskPut.setUpdateListener(new restfullAPI.putDebtBook.OnUpdateListener(){;
                                            @Override
                                            public void onUpdate(ResponseFromServer res) {
                                                if (res==null){return;}
                                                if (res.getStatus()==0){
                                                    Log.d(TAG, "two ways sync DebtBook, do like local: ------------local: " + newUpTime + "-----------server: " + oldUpTime);
                                                }
                                            }
                                        });
                                        taskPut.execute(myurl);
                                    }else{
                                        //sua theo server
                                        final String oldUpTime=debtbook.getUpdatetime();
                                        DebtBook debtbook2local=serverdebtbook;
                                        //sua lai id cho hop ly, k the lay id cua thang server dc
                                        debtbook2local.setId(debtbook.getId());
                                        int u=DebtBook_updateDebtBook(debtbook2local);
                                        if (u!=0){
                                            Log.d(TAG, "two ways sync DebtBook, do like server: -----------local: "+oldUpTime+"-----------server: " + serverdebtbook.getUpdatetime());
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        });
        syncDebtBook.execute(BASE_URL_DEBTBOOK);

        restfullAPI.getDMHaiSan syncDMHaiSan = new restfullAPI.getDMHaiSan();
        syncDMHaiSan.setUpdateListener(new restfullAPI.getDMHaiSan.OnUpdateListener(){
            @Override
            public void onUpdate(ArrayList<DMHaiSan> arrServerDMHaiSan) throws ExecutionException, InterruptedException {
                if (arrServerDMHaiSan==null || arrDMHaiSan.size()==0){return;}
                if (arrServerDMHaiSan.size()>0){
                    for (int i = 0; i < arrServerDMHaiSan.size(); i++) {
                        final DMHaiSan serverdmhaisan=arrServerDMHaiSan.get(i);
                        if (arrDMHaiSan.size()>0){
                            for (int j=0; j<arrDMHaiSan.size();j++){
                                final DMHaiSan dmhaisan=arrDMHaiSan.get(j);
                                if (dmhaisan.getServerkey()==serverdmhaisan.getServerkey()){
                                    if (timeGet(dmhaisan.getUpdatetime())==timeGet(serverdmhaisan.getUpdatetime())){
                                        break;
                                    }
                                    if (timeGet(dmhaisan.getUpdatetime())>timeGet(serverdmhaisan.getUpdatetime())){
                                        //sua theo clent
                                        final String myurl=BASE_URL_DMHAISAN + "/update/"+ dmhaisan.getServerkey() +"/";
                                        final String oldUpTime=serverdmhaisan.getUpdatetime();
                                        final String newUpTime=dmhaisan.getUpdatetime();
                                        restfullAPI.putDMHaiSan taskPut = new restfullAPI.putDMHaiSan(dmhaisan);
                                        taskPut.setUpdateListener(new restfullAPI.putDMHaiSan.OnUpdateListener(){;
                                            @Override
                                            public void onUpdate(ResponseFromServer res) {
                                                if (res==null){return;}
                                                if (res.getStatus()==0){
                                                    Log.d(TAG, "two ways sync DMHaiSan, do like local: ------------local: " + newUpTime + "-----------server: " + oldUpTime);
                                                }
                                            }
                                        });
                                        taskPut.execute(myurl);
                                    }else{
                                        //sua theo server
                                        final String oldUpTime=dmhaisan.getUpdatetime();
                                        DMHaiSan dmhaisan2local=serverdmhaisan;
                                        //sua lai id cho hop ly, k the lay id cua thang server dc
                                        dmhaisan2local.setId(dmhaisan.getId());
                                        int u=DMHaiSan_updateDMHaiSan(dmhaisan2local);
                                        if (u!=0){
                                            Log.d(TAG, "two ways sync DMHaiSan, do like server: -----------local: "+oldUpTime+"-----------server: " + serverdmhaisan.getUpdatetime());
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        });
        syncDMHaiSan.execute(BASE_URL_DMHAISAN);


        restfullAPI.getBanHSDetail syncBanHSDetail = new restfullAPI.getBanHSDetail();
        syncBanHSDetail.setUpdateListener(new restfullAPI.getBanHSDetail.OnUpdateListener(){
            @Override
            public void onUpdate(ArrayList<BanHSDetail> arrServerBanHSDetail) throws ExecutionException, InterruptedException {
                if (arrServerBanHSDetail==null || arrBanHSDetail.size()==0){return;}
                if (arrServerBanHSDetail.size()>0){
                    for (int i = 0; i < arrServerBanHSDetail.size(); i++) {
                        final BanHSDetail serverbanhsdetail=arrServerBanHSDetail.get(i);
                        if (arrBanHSDetail.size()>0){
                            for (int j=0; j<arrBanHSDetail.size();j++){
                                final BanHSDetail banhsdetail=arrBanHSDetail.get(j);
                                if (banhsdetail.getServerkey()==serverbanhsdetail.getServerkey()){
                                    if (timeGet(banhsdetail.getUpdatetime())==timeGet(serverbanhsdetail.getUpdatetime())){
                                        break;
                                    }
                                    if (timeGet(banhsdetail.getUpdatetime())>timeGet(serverbanhsdetail.getUpdatetime())){
                                        //sua theo clent
                                        final String myurl=BASE_URL_BANHSDETAIL + "/update/"+ banhsdetail.getServerkey() +"/";
                                        final String oldUpTime=serverbanhsdetail.getUpdatetime();
                                        final String newUpTime=banhsdetail.getUpdatetime();
                                        restfullAPI.putBanHSDetail taskPut = new restfullAPI.putBanHSDetail(banhsdetail);
                                        taskPut.setUpdateListener(new restfullAPI.putBanHSDetail.OnUpdateListener(){;
                                            @Override
                                            public void onUpdate(ResponseFromServer res) {
                                                if (res==null){return;}
                                                if (res.getStatus()==0){
                                                    Log.d(TAG, "two ways sync BanHSDetail, do like local: ------------local: " + newUpTime + "-----------server: " + oldUpTime);
                                                }
                                            }
                                        });
                                        taskPut.execute(myurl);
                                    }else{
                                        //sua theo server
                                        final String oldUpTime=banhsdetail.getUpdatetime();
                                        BanHSDetail banhsdetail2local=serverbanhsdetail;
                                        //sua lai id cho hop ly, k the lay id cua thang server dc
                                        banhsdetail2local.setId(banhsdetail.getId());
                                        int u=BanHSDetail_updateBanHSDetail(banhsdetail2local);
                                        if (u!=0){
                                            Log.d(TAG, "two ways sync BanHSDetail, do like server: -----------local: "+oldUpTime+"-----------server: " + serverbanhsdetail.getUpdatetime());
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        });
        syncBanHSDetail.execute(BASE_URL_BANHSDETAIL);
    }

    //********************************POST NEW FROM LOCAL TO SERVER**************************
    private void SyncPostToServer(){
        Log.d(TAG, "SyncPostToServer: ------------------------SyncPostToServer------------------------------");
        final String URLcreateUsers=BASE_URL_USERS + "/create/";
        final String URLcreateDSTV=BASE_URL_DSTV + "/create/";
        final String URLcreateChuyenBien=BASE_URL_CHUYENBIEN + "/create/";
        final String URLcreateDiemDD=BASE_URL_DIEMDD + "/create/";
        final String URLcreateDoiTac=BASE_URL_DOITAC + "/create/";
        final String URLcreateKhachHang=BASE_URL_KHACHHANG + "/create/";
        final String URLcreateTicket=BASE_URL_TICKET + "/create/";
        final String URLcreateTicketDetail=BASE_URL_TICKETDETAIL + "/create/";
        final String URLcreateThu=BASE_URL_THU+ "/create/";
        final String URLcreateThuDetail=BASE_URL_THUDETAIL + "/create/";
        final String URLcreateChi=BASE_URL_CHI + "/create/";
        final String URLcreateChiDetail=BASE_URL_CHIDETAIL + "/create/";
        final String URLcreateDMHaiSan=BASE_URL_DMHAISAN + "/create/";
        final String URLcreateDebtBook=BASE_URL_DEBTBOOK + "/create/";
        final String URLcreateBanHSDetail=BASE_URL_BANHSDETAIL + "/create/";
        final String URLURLcreateImgStore=BASE_URL_IMGSTORE + "/create/";
        ArrayList<Users>arrUsers=Users_getAllUsers();
        for (int i = 0; i < arrUsers.size(); i++) {
            final Users finalLocalUsers=arrUsers.get(i);
            if (finalLocalUsers.getServerkey()==0){
                restfullAPI.postUser task2 = new restfullAPI.postUser(finalLocalUsers);
                task2.setUpdateListener(new restfullAPI.postUser.OnUpdateListener(){;
                    @Override
                    public void onUpdate(ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            finalLocalUsers.setServerkey(res.getServerkey());
                            //cap nhat serverkey cho client
                            int u=Users_updateUsers(finalLocalUsers);
                            if (u!=0) {
                                Log.d(TAG, "Users postToServer - Updated ServerKey On Client: ----------------------" + String.valueOf(u) + "==========" + finalLocalUsers.getServerkey());
                            }
                        }
                    }
                });
                task2.execute(URLcreateUsers);
            }
        }
        ArrayList<DSTV>arrDSTV=DSTV_getAllDSTV();
        for (int i = 0; i < arrDSTV.size(); i++) {
            final DSTV finalLocalDSTV=arrDSTV.get(i);
            if (finalLocalDSTV.getServerkey()==0){
                restfullAPI.postDSTV task2 = new restfullAPI.postDSTV(finalLocalDSTV);
                task2.setUpdateListener(new restfullAPI.postDSTV.OnUpdateListener(){;
                    @Override
                    public void onUpdate(ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            finalLocalDSTV.setServerkey(res.getServerkey());
                            //cap nhat serverkey cho client
                            int u=DSTV_updateDSTV(finalLocalDSTV);
                            if (u!=0) {
                                Log.d(TAG, "DSTV postToServer - Updated ServerKey On Client: ----------------------" + String.valueOf(u) + "==========" + finalLocalDSTV.getServerkey());
                            }
                        }
                    }
                });
                task2.execute(URLcreateDSTV);
            }
        }
        ArrayList<ChuyenBien>arrChuyenBien=ChuyenBien_getAllChuyenBien();
        for (int i = 0; i < arrChuyenBien.size(); i++) {
            final ChuyenBien finalLocalChuyenBien=arrChuyenBien.get(i);
            if (finalLocalChuyenBien.getServerkey()==0){
                restfullAPI.postChuyenBien task2 = new restfullAPI.postChuyenBien(finalLocalChuyenBien);
                task2.setUpdateListener(new restfullAPI.postChuyenBien.OnUpdateListener(){;
                    @Override
                    public void onUpdate(ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            finalLocalChuyenBien.setServerkey(res.getServerkey());
                            //cap nhat serverkey cho client
                            int u=ChuyenBien_updateChuyenBien(finalLocalChuyenBien);
                            if (u!=0) {
                                Log.d(TAG, "ChuyenBien postToServer - Updated ServerKey On Client: ----------------------" + String.valueOf(u) + "==========" + finalLocalChuyenBien.getServerkey());
                            }
                        }
                    }
                });
                task2.execute(URLcreateChuyenBien);
            }
        }
        ArrayList<DiemDD>arrDiemDD=DiemDD_getAllDiemDD();
        for (int i = 0; i < arrDiemDD.size(); i++) {
            final DiemDD finalLocalDiemDD=arrDiemDD.get(i);
            if (finalLocalDiemDD.getServerkey()==0){
                restfullAPI.postDiemDD task2 = new restfullAPI.postDiemDD(finalLocalDiemDD);
                task2.setUpdateListener(new restfullAPI.postDiemDD.OnUpdateListener(){;
                    @Override
                    public void onUpdate(ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            finalLocalDiemDD.setServerkey(res.getServerkey());
                            //cap nhat serverkey cho client
                            int u=DiemDD_updateDiemDD(finalLocalDiemDD);
                            if (u!=0) {
                                Log.d(TAG, "DiemDD postToServer - Updated ServerKey On Client: ----------------------" + String.valueOf(u) + "==========" + finalLocalDiemDD.getServerkey());
                            }
                        }
                    }
                });
                task2.execute(URLcreateDiemDD);
            }
        }
        ArrayList<KhachHang>arrKhachHang=KhachHang_getAllKhachHang();
        for (int i = 0; i < arrKhachHang.size(); i++) {
            final KhachHang finalLocalKhachHang=arrKhachHang.get(i);
            if (finalLocalKhachHang.getServerkey()==0){
                restfullAPI.postKhachHang task2 = new restfullAPI.postKhachHang(finalLocalKhachHang);
                task2.setUpdateListener(new restfullAPI.postKhachHang.OnUpdateListener(){;
                    @Override
                    public void onUpdate(ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            finalLocalKhachHang.setServerkey(res.getServerkey());
                            //cap nhat serverkey cho client
                            int u=KhachHang_updateKhachHang(finalLocalKhachHang);
                            if (u!=0) {
                                Log.d(TAG, "KhachHang postToServer - Updated ServerKey On Client: ----------------------" + String.valueOf(u) + "==========" + finalLocalKhachHang.getServerkey());
                            }
                        }
                    }
                });
                task2.execute(URLcreateKhachHang);
            }
        }
        ArrayList<DoiTac>arrDoiTac=DoiTac_getAllDoiTac();
        for (int i = 0; i < arrDoiTac.size(); i++) {
            final DoiTac finalLocalDoiTac=arrDoiTac.get(i);
            if (finalLocalDoiTac.getServerkey()==0){
                restfullAPI.postDoiTac task2 = new restfullAPI.postDoiTac(finalLocalDoiTac);
                task2.setUpdateListener(new restfullAPI.postDoiTac.OnUpdateListener(){;
                    @Override
                    public void onUpdate(ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            finalLocalDoiTac.setServerkey(res.getServerkey());
                            //cap nhat serverkey cho client
                            int u=DoiTac_updateDoiTac(finalLocalDoiTac);
                            if (u!=0) {
                                Log.d(TAG, "DoiTac postToServer - Updated ServerKey On Client: ----------------------" + String.valueOf(u) + "==========" + finalLocalDoiTac.getServerkey());
                            }
                        }
                    }
                });
                task2.execute(URLcreateDoiTac);
            }
        }
        ArrayList<Ticket>arrTicket=Ticket_getAllTicket();
        for (int i = 0; i < arrTicket.size(); i++) {
            final Ticket finalLocalTicket=arrTicket.get(i);
            if (finalLocalTicket.getServerkey()==0){
                restfullAPI.postTicket task2 = new restfullAPI.postTicket(finalLocalTicket);
                task2.setUpdateListener(new restfullAPI.postTicket.OnUpdateListener(){;
                    @Override
                    public void onUpdate(ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            finalLocalTicket.setServerkey(res.getServerkey());
                            //cap nhat serverkey cho client
                            int u=Ticket_updateTicket(finalLocalTicket);
                            if (u!=0) {
                                Log.d(TAG, "Ticket postToServer - Updated ServerKey On Client: ----------------------" + String.valueOf(u) + "==========" + finalLocalTicket.getServerkey());
                            }
                        }
                    }
                });
                task2.execute(URLcreateTicket);
            }
        }
        ArrayList<TicketDetail>arrTicketDetail=TicketDetail_getAllTicketDetail();
        for (int i = 0; i < arrTicketDetail.size(); i++) {
            final TicketDetail finalLocalTicketDetail=arrTicketDetail.get(i);
            if (finalLocalTicketDetail.getServerkey()==0){
                restfullAPI.postTicketDetail task2 = new restfullAPI.postTicketDetail(finalLocalTicketDetail);
                task2.setUpdateListener(new restfullAPI.postTicketDetail.OnUpdateListener(){;
                    @Override
                    public void onUpdate(ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            finalLocalTicketDetail.setServerkey(res.getServerkey());
                            //cap nhat serverkey cho client
                            int u=TicketDetail_updateTicketDetail(finalLocalTicketDetail);
                            if (u!=0) {
                                Log.d(TAG, "TicketDetail postToServer - Updated ServerKey On Client: ----------------------" + String.valueOf(u) + "==========" + finalLocalTicketDetail.getServerkey());
                            }
                        }
                    }
                });
                task2.execute(URLcreateTicketDetail);
            }
        }
        ArrayList<Thu>arrThu=Thu_getAllThu();
        for (int i = 0; i < arrThu.size(); i++) {
            final Thu finalLocalThu=arrThu.get(i);
            if (finalLocalThu.getServerkey()==0){
                restfullAPI.postThu task2 = new restfullAPI.postThu(finalLocalThu);
                task2.setUpdateListener(new restfullAPI.postThu.OnUpdateListener(){;
                    @Override
                    public void onUpdate(ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            finalLocalThu.setServerkey(res.getServerkey());
                            //cap nhat serverkey cho client
                            int u=Thu_updateThu(finalLocalThu);
                            if (u!=0) {
                                Log.d(TAG, "Thu postToServer - Updated ServerKey On Client: ----------------------" + String.valueOf(u) + "==========" + finalLocalThu.getServerkey());
                            }
                        }
                    }
                });
                task2.execute(URLcreateThu);
            }
        }
        ArrayList<ThuDetail>arrThuDetail=ThuDetail_getAllThuDetail();
        for (int i = 0; i < arrThuDetail.size(); i++) {
            final ThuDetail finalLocalThuDetail=arrThuDetail.get(i);
            if (finalLocalThuDetail.getServerkey()==0){
                restfullAPI.postThuDetail task2 = new restfullAPI.postThuDetail(finalLocalThuDetail);
                task2.setUpdateListener(new restfullAPI.postThuDetail.OnUpdateListener(){;
                    @Override
                    public void onUpdate(ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            finalLocalThuDetail.setServerkey(res.getServerkey());
                            //cap nhat serverkey cho client
                            int u=ThuDetail_updateThuDetail(finalLocalThuDetail);
                            if (u!=0) {
                                Log.d(TAG, "ThuDetail postToServer - Updated ServerKey On Client: ----------------------" + String.valueOf(u) + "==========" + finalLocalThuDetail.getServerkey());
                            }
                        }
                    }
                });
                task2.execute(URLcreateThuDetail);
            }
        }
        ArrayList<Chi>arrChi=Chi_getAllChi();
        for (int i = 0; i < arrChi.size(); i++) {
            final Chi finalLocalChi=arrChi.get(i);
            if (finalLocalChi.getServerkey()==0){
                restfullAPI.postChi task2 = new restfullAPI.postChi(finalLocalChi);
                task2.setUpdateListener(new restfullAPI.postChi.OnUpdateListener(){;
                    @Override
                    public void onUpdate(ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            finalLocalChi.setServerkey(res.getServerkey());
                            //cap nhat serverkey cho client
                            int u=Chi_updateChi(finalLocalChi);
                            if (u!=0) {
                                Log.d(TAG, "Chi postToServer - Updated ServerKey On Client: ----------------------" + String.valueOf(u) + "==========" + finalLocalChi.getServerkey());
                            }
                        }
                    }
                });
                task2.execute(URLcreateChi);
            }
        }
        ArrayList<ChiDetail>arrChiDetail=ChiDetail_getAllChiDetail();
        for (int i = 0; i < arrChiDetail.size(); i++) {
            final ChiDetail finalLocalChiDetail=arrChiDetail.get(i);
            if (finalLocalChiDetail.getServerkey()==0){
                restfullAPI.postChiDetail task2 = new restfullAPI.postChiDetail(finalLocalChiDetail);
                task2.setUpdateListener(new restfullAPI.postChiDetail.OnUpdateListener(){;
                    @Override
                    public void onUpdate(ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            finalLocalChiDetail.setServerkey(res.getServerkey());
                            //cap nhat serverkey cho client
                            int u=ChiDetail_updateChiDetail(finalLocalChiDetail);
                            if (u!=0) {
                                Log.d(TAG, "ChiDetail postToServer - Updated ServerKey On Client: ----------------------" + String.valueOf(u) + "==========" + finalLocalChiDetail.getServerkey());
                            }
                        }
                    }
                });
                task2.execute(URLcreateChiDetail);
            }
        }
        ArrayList<DebtBook>arrDebtBook=DebtBook_getAllDebtBook();
        for (int i = 0; i < arrDebtBook.size(); i++) {
            final DebtBook finalLocalDebtBook=arrDebtBook.get(i);
            if (finalLocalDebtBook.getServerkey()==0){
                restfullAPI.postDebtBook task2 = new restfullAPI.postDebtBook(finalLocalDebtBook);
                task2.setUpdateListener(new restfullAPI.postDebtBook.OnUpdateListener(){;
                    @Override
                    public void onUpdate(ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            finalLocalDebtBook.setServerkey(res.getServerkey());
                            //cap nhat serverkey cho client
                            int u=DebtBook_updateDebtBook(finalLocalDebtBook);
                            if (u!=0) {
                                Log.d(TAG, "DebtBook postToServer - Updated ServerKey On Client: ----------------------" + String.valueOf(u) + "==========" + finalLocalDebtBook.getServerkey());
                            }
                        }
                    }
                });
                task2.execute(URLcreateDebtBook);
            }
        }
        ArrayList<DMHaiSan>arrDMHaiSan=DMHaiSan_getAllDMHaiSan();
        for (int i = 0; i < arrDMHaiSan.size(); i++) {
            final DMHaiSan finalLocalDMHaiSan=arrDMHaiSan.get(i);
            if (finalLocalDMHaiSan.getServerkey()==0){
                restfullAPI.postDMHaiSan task2 = new restfullAPI.postDMHaiSan(finalLocalDMHaiSan);
                task2.setUpdateListener(new restfullAPI.postDMHaiSan.OnUpdateListener(){;
                    @Override
                    public void onUpdate(ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            finalLocalDMHaiSan.setServerkey(res.getServerkey());
                            //cap nhat serverkey cho client
                            int u=DMHaiSan_updateDMHaiSan(finalLocalDMHaiSan);
                            if (u!=0) {
                                Log.d(TAG, "DMHaiSan postToServer - Updated ServerKey On Client: ----------------------" + String.valueOf(u) + "==========" + finalLocalDMHaiSan.getServerkey());
                            }
                        }
                    }
                });
                task2.execute(URLcreateDMHaiSan);
            }
        }

        ArrayList<BanHSDetail>arrBanHSDetail=BanHSDetail_getAllBanHSDetail();
        for (int i = 0; i < arrBanHSDetail.size(); i++) {
            final BanHSDetail finalLocalBanHSDetail=arrBanHSDetail.get(i);
            if (finalLocalBanHSDetail.getServerkey()==0){
                restfullAPI.postBanHSDetail task2 = new restfullAPI.postBanHSDetail(finalLocalBanHSDetail);
                task2.setUpdateListener(new restfullAPI.postBanHSDetail.OnUpdateListener(){;
                    @Override
                    public void onUpdate(ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            finalLocalBanHSDetail.setServerkey(res.getServerkey());
                            //cap nhat serverkey cho client
                            int u=BanHSDetail_updateBanHSDetail(finalLocalBanHSDetail);
                            if (u!=0) {
                                Log.d(TAG, "BanHSDetail postToServer - Updated ServerKey On Client: ----------------------" + String.valueOf(u) + "==========" + finalLocalBanHSDetail.getServerkey());
                            }
                        }
                    }
                });
                task2.execute(URLcreateBanHSDetail);
            }
        }
        ArrayList<ImgStore>arrImgStore=ImgStore_getAllImgStore();
        for (int i = 0; i < arrImgStore.size(); i++) {
            final ImgStore imgstorefirst=arrImgStore.get(i);
            if (imgstorefirst.getServerkey()==0){
                //post du lieu text
                restfullAPI.postImgStore task2 = new restfullAPI.postImgStore(imgstorefirst);
                final int finalI = i;
                task2.setUpdateListener(new restfullAPI.postImgStore.OnUpdateListener(){;
                    @Override
                    public void onUpdate(ResponseFromServer res) {
                        if (res==null){return;}
                        if (res.getStatus()==0){
                            final int svkey=res.getServerkey();
                            //update serverKey for ImgStore
                            imgstorefirst.setServerkey(svkey);
                            int u=ImgStore_updateImgStore(imgstorefirst);
                            if (imgstorefirst.getImgpath()!=null && !StringUtils.left(imgstorefirst.getImgpath(),4).equals("http")){
                                File file=new File(imgstorefirst.getImgpath());
                                if (!file.exists()){
                                    return;
                                }
                                //post image file
                                restfullAPI.postFile taskpostfile =new restfullAPI.postFile();
                                taskpostfile.setUpdateListener(new restfullAPI.postFile.OnUpdateListener() {
                                    int f=finalI;
                                    @Override
                                    public void onUpdate(ResponseFromServer obj) {
                                        if (obj==null){return;}
                                        if (obj.getStatus()==0){
                                            final String ImageHoaDonURL =obj.getMessage();
                                            if (timeGet(imgstorefirst.getUpdatetime())<NGAY_LUU_ANH){
                                                final String realPath=imgstorefirst.getImgpath();
                                                imgstorefirst.setServerkey(svkey);
                                                imgstorefirst.setImgpath(ImageHoaDonURL);
                                                //cap nhat imghoadon cho client theo link server
                                                int u=ImgStore_updateImgStore(imgstorefirst);
                                                if (u!=0){
                                                    //delete local file
                                                    utils.deleteFile(realPath);
                                                }
                                            }
                                            //update server imagehoadon link
                                            ArrayList<ImgStore> arrImgStore;
                                            arrImgStore=ImgStore_getAllImgStore();
                                            ImgStore serverImgStore=new ImgStore();
                                            ImgStore imgstorefinal=arrImgStore.get(f);
                                            serverImgStore=imgstorefinal;
                                            serverImgStore.setImgpath(ImageHoaDonURL);
                                            //update link cho dg img hoa don
                                            String myurl2= BASE_URL_IMGSTORE + "/update/"+ imgstorefinal.getServerkey() +"/";
                                            new restfullAPI.putImgStore(serverImgStore).execute(myurl2);
                                            Log.d(TAG, "ImgStore postToServer - Updated ServerKey On Client: ----------------------" + imgstorefinal.getServerkey());
                                        }
                                    }
                                });
                                taskpostfile.execute(new String[]{BASE_URL_IMGSTORE +"/upload/"+String.valueOf(svkey),imgstorefirst.getImgpath()});
                            }
                        }

                    }
                });
                task2.execute(URLURLcreateImgStore);
            }
        }
    }

    private void SyncTicketOnly(){
        Log.d(TAG, "SyncTicketOnly: ----------------------------------------------------------");
        final ArrayList<Ticket>arrTicket=Ticket_getAllTicket();
        restfullAPI.getTicket syncTicket = new restfullAPI.getTicket();
        syncTicket.setUpdateListener(new restfullAPI.getTicket.OnUpdateListener(){
            @Override
            public void onUpdate(ArrayList<Ticket> arrServerTicket) throws ExecutionException, InterruptedException {
                if (arrServerTicket==null || arrTicket.size()==0){return;}
                if (arrServerTicket.size()>0){
                    for (int i = 0; i < arrServerTicket.size(); i++) {
                        final Ticket serverticket=arrServerTicket.get(i);
                        if (arrTicket.size()>0){
                            for (int j=0; j<arrTicket.size();j++){
                                final Ticket ticket=arrTicket.get(j);
                                if (ticket.getServerkey()==serverticket.getServerkey()){
                                    if (timeGet(ticket.getUpdatetime())==timeGet(serverticket.getUpdatetime())){
                                        break;
                                    }
                                    if (timeGet(ticket.getUpdatetime())>timeGet(serverticket.getUpdatetime())){
                                        //sua theo clent
                                        final String myurl=BASE_URL_TICKET + "/update/"+ ticket.getServerkey() +"/";
                                        final String oldUpTime=serverticket.getUpdatetime();
                                        final String newUpTime=ticket.getUpdatetime();
                                        restfullAPI.putTicket taskPut = new restfullAPI.putTicket(ticket);
                                        taskPut.setUpdateListener(new restfullAPI.putTicket.OnUpdateListener(){;
                                            @Override
                                            public void onUpdate(ResponseFromServer res) {
                                                if (res==null){return;}
                                                if (res.getStatus()==0){
                                                    Log.d(TAG, "two ways sync Ticket, do like local: ------------local: " + newUpTime + "-----------server: " + oldUpTime);
                                                }
                                            }
                                        });
                                        taskPut.execute(myurl);
                                    }else{
                                        //sua theo server
                                        final String oldUpTime=ticket.getUpdatetime();
                                        Ticket ticket2local=serverticket;
                                        //sua lai id cho hop ly, k the lay id cua thang server dc
                                        ticket2local.setId(ticket.getId());
                                        int u=Ticket_updateTicket(ticket2local);
                                        if (u!=0){
                                            Log.d(TAG, "two ways sync Ticket, do like server: -----------local: "+oldUpTime+"-----------server: " + serverticket.getUpdatetime());
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        });
        syncTicket.execute(BASE_URL_TICKET);
    }

    //************************************************WDFS*********************************
    private Uri WDFS_addWDSF(WantDeleteFromServer wdfs){
        ContentValues values = new ContentValues();
        //values.put(SERVER_KEY, Integer.valueOf(chuyenBien.getmServerkey()));
        //values.put(DIRTY, Integer.valueOf(chuyenBien.getmDirty()));
        values.put("serverkey", wdfs.getmServerkey());
        values.put("tablename", wdfs.getmTablename());
        Uri uri = null;
        uri = mContentResolver.insert(StubProvider.CONTENT_URI_WDFS, values);
        return uri;
    }
    private int WDFS_updateWDSF(WantDeleteFromServer wdfs){
        ContentValues values = new ContentValues();
        //values.put(SERVER_KEY, Integer.valueOf(chuyenBien.getmServerkey()));
        //values.put(DIRTY, Integer.valueOf(chuyenBien.getmDirty()));
        values.put("serverkey", wdfs.getmServerkey());
        values.put("tablename", wdfs.getmTablename());
        int u= 0;
        u = mContentResolver.update(StubProvider.CONTENT_URI_WDFS, values,"id=?", new String[]{ String.valueOf(wdfs.getmId())});
        return u;
    }
    private int WDFS_deleteWDFS(int ID){
        return mContentResolver.delete(StubProvider.CONTENT_URI_WDFS, "id=?", new String[]{ String.valueOf(ID)});
    }
    private ArrayList<WantDeleteFromServer>WDFS_getAllWDFS(){
        ArrayList<WantDeleteFromServer>arrWdfs=new ArrayList<>();
        Cursor cursor = mContentResolver.query(StubProvider.CONTENT_URI_WDFS, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    WantDeleteFromServer wdfs = new WantDeleteFromServer();
                    wdfs.setmId(cursor.getInt(0));
                    wdfs.setmServerkey(cursor.getInt(1));
                    wdfs.setmTablename(cursor.getString(2));
                    arrWdfs.add(wdfs);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrWdfs;
    }
    private boolean WDFS_Exits(ArrayList<WantDeleteFromServer>arrayList,String TableName, int serverKey){
        boolean r=false;
        for (int i=0;i<arrayList.size();i++){
            if (arrayList.get(i).getmTablename().equals(TableName) && arrayList.get(i).getmServerkey()==serverKey){
                r=true;
                break;
            }
        }
        return r;
    }
    //**********************************************USers*********************************
    private Uri Users_addUsers(Users users){
        ContentValues values = new ContentValues();
        values.put("serverkey", users.getServerkey());
        values.put("rkey", users.getRkey());
        values.put("fullname", users.getFullname());
        values.put("honourname", users.getHonourname());
        values.put("email", users.getEmail());
        values.put("password", users.getPassword());
        values.put("nocty",users.getNocty());
        values.put("ctyno",users.getCtyno());
        values.put("updatetime", users.getUpdatetime());
        values.put("admin", users.getAdmin());
        Uri uri = null;
        uri = mContentResolver.insert(StubProvider.CONTENT_URI_USERS, values);
        return uri;
    }
    private int Users_deleteUsers(int ID){
        return mContentResolver.delete(StubProvider.CONTENT_URI_USERS, "id=?", new String[]{ String.valueOf(ID)});
    }
    private ArrayList<Users>Users_getAllUsers(){
        ArrayList<Users>arrLocalUsers=new ArrayList<>();
        Cursor cursor = mContentResolver.query(StubProvider.CONTENT_URI_USERS, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Users users = new Users();
                    users.setId(cursor.getInt(0));
                    users.setServerkey(cursor.getInt(1));
                    users.setRkey(cursor.getInt(2));
                    users.setFullname(cursor.getString(3));
                    users.setHonourname(cursor.getString(4));
                    users.setEmail(cursor.getString(5));
                    users.setPassword(cursor.getString(6));
                    users.setNocty(cursor.getString(7));
                    users.setCtyno(cursor.getString(8));
                    users.setUpdatetime(cursor.getString(9));
                    users.setAdmin(cursor.getInt(10));
                    arrLocalUsers.add(users);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalUsers;
    }
    private ArrayList<Users>Users_getChuaEditRkey(){
        ArrayList<Users>arrLocalUsers=new ArrayList<>();
        Uri URI_ChuaEditRkey = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/users/chuaEditRkey");
        Cursor cursor = mContentResolver.query(URI_ChuaEditRkey, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Users users = new Users();
                    users.setId(cursor.getInt(0));
                    users.setServerkey(cursor.getInt(1));
                    users.setRkey(cursor.getInt(2));
                    users.setFullname(cursor.getString(3));
                    users.setHonourname(cursor.getString(4));
                    users.setEmail(cursor.getString(5));
                    users.setPassword(cursor.getString(6));
                    users.setNocty(cursor.getString(7));
                    users.setCtyno(cursor.getString(8));
                    users.setUpdatetime(cursor.getString(9));
                    users.setAdmin(cursor.getInt(10));
                    arrLocalUsers.add(users);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalUsers;
    }
    private int Users_updateUsers(Users users){
        ContentValues values = new ContentValues();
        values.put("serverkey", users.getServerkey());
        values.put("rkey", users.getRkey());
        values.put("fullname", users.getFullname());
        values.put("honourname", users.getHonourname());
        values.put("email", users.getEmail());
        values.put("password", users.getPassword());
        values.put("nocty",users.getNocty());
        values.put("ctyno",users.getCtyno());
        values.put("updatetime", users.getUpdatetime());
        values.put("admin", users.getAdmin());
        int u= 0;
        u = mContentResolver.update(StubProvider.CONTENT_URI_USERS, values,"id=?", new String[]{ String.valueOf(users.getId())});
        return u;
    }
    private boolean Users_Exits(int serverKey){
        boolean r=false;
        Uri SYNCTABLE_URI_ID = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/users/"+serverKey);
        Cursor cursor = null;
        cursor = mContentResolver.query(SYNCTABLE_URI_ID, null, null, null, null);
        if (cursor.moveToFirst()){
            r=true;
        }
        if (cursor!=null ){cursor.close();}
        return r;
    }
    //**********************************************DSTV************************************
    private Uri DSTV_addDSTV(DSTV dstv){
        ContentValues values = new ContentValues();
        values.put("serverkey", dstv.getServerkey());
        values.put("rkey", dstv.getRkey());
        values.put("rkeychuyenbien", dstv.getRkeychuyenbien());
        values.put("ten", dstv.getTen());
        values.put("diem", dstv.getDiem());
        values.put("tienchia", dstv.getTienchia());
        values.put("tienmuon", dstv.getTienmuon());
        values.put("tiencanca", dstv.getTiencanca());
        values.put("conlai", dstv.getConlai());
        values.put("notes", dstv.getNotes());
        values.put("updatetime",dstv.getUpdatetime());
        values.put("username", dstv.getUsername());
        Uri uri = null;
        uri = mContentResolver.insert(StubProvider.CONTENT_URI_DSTV, values);
        return uri;
    }
    private int DSTV_deleteDSTV(int ID){
        return mContentResolver.delete(StubProvider.CONTENT_URI_DSTV, "id=?", new String[]{ String.valueOf(ID)});
    }
    private ArrayList<DSTV>DSTV_getAllDSTV(){
        ArrayList<DSTV>arrLocalDSTV=new ArrayList<>();
        Cursor cursor = mContentResolver.query(StubProvider.CONTENT_URI_DSTV, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    DSTV dstv = new DSTV();
                    dstv.setId(cursor.getInt(0));
                    dstv.setServerkey(cursor.getInt(1));
                    dstv.setRkey(cursor.getLong(2));
                    dstv.setRkeychuyenbien((cursor.getLong(3)));
                    dstv.setTen(cursor.getString(4));
                    dstv.setDiem(cursor.getString(5));
                    dstv.setTienchia(cursor.getString(6));
                    dstv.setTienmuon(cursor.getString(7));
                    dstv.setTiencanca(cursor.getString(8));
                    dstv.setConlai(cursor.getString(9));
                    dstv.setNotes(cursor.getString(10));
                    dstv.setUpdatetime(cursor.getString(11));
                    dstv.setUsername(cursor.getString(12));
                    arrLocalDSTV.add(dstv);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalDSTV;
    }
    private ArrayList<DSTV>DSTV_getChuaEditRkey(){
        ArrayList<DSTV>arrLocalDSTV=new ArrayList<>();
        Uri URI_ChuaEditRkey = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/dstv/chuaEditRkey");
        Cursor cursor = mContentResolver.query(URI_ChuaEditRkey, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    DSTV dstv = new DSTV();
                    dstv.setId(cursor.getInt(0));
                    dstv.setServerkey(cursor.getInt(1));
                    dstv.setRkey(cursor.getLong(2));
                    dstv.setRkeychuyenbien((cursor.getLong(3)));
                    dstv.setTen(cursor.getString(4));
                    dstv.setDiem(cursor.getString(5));
                    dstv.setTienchia(cursor.getString(6));
                    dstv.setTienmuon(cursor.getString(7));
                    dstv.setTiencanca(cursor.getString(8));
                    dstv.setConlai(cursor.getString(9));
                    dstv.setNotes(cursor.getString(10));
                    dstv.setUpdatetime(cursor.getString(11));
                    dstv.setUsername(cursor.getString(12));
                    arrLocalDSTV.add(dstv);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalDSTV;
    }
    private int DSTV_updateDSTV(DSTV dstv){
        ContentValues values = new ContentValues();
        values.put("serverkey", dstv.getServerkey());
        values.put("rkey", dstv.getRkey());
        values.put("rkeychuyenbien", dstv.getRkeychuyenbien());
        values.put("ten", dstv.getTen());
        values.put("diem", dstv.getDiem());
        values.put("tienchia", dstv.getTienchia());
        values.put("tienmuon", dstv.getTienmuon());
        values.put("tiencanca", dstv.getTiencanca());
        values.put("conlai", dstv.getConlai());
        values.put("notes", dstv.getNotes());
        values.put("updatetime",dstv.getUpdatetime());
        values.put("username", dstv.getUsername());
        int u= 0;
        u = mContentResolver.update(StubProvider.CONTENT_URI_DSTV, values,"id=?", new String[]{ String.valueOf(dstv.getId())});
        return u;
    }
    private boolean DSTV_Exits(int serverKey) {
        boolean r=false;
        Uri SYNCTABLE_URI_ID = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/dstv/"+serverKey);
        Cursor cursor = null;
        cursor = mContentResolver.query(SYNCTABLE_URI_ID, null, null, null, null);
        if (cursor.moveToFirst()){
            r=true;
        }
        if (cursor!=null ){cursor.close();}
        return r;
    }
    //***************************************************ChuyenBien***************************
    private Uri ChuyenBien_addChuyenBien(ChuyenBien chuyenBien){
        ContentValues values = new ContentValues();
        values.put("serverkey", chuyenBien.getServerkey());
        values.put("rkey", chuyenBien.getRkey());
        values.put("chuyenbien", chuyenBien.getChuyenbien());
        values.put("tentau", chuyenBien.getTentau());
        values.put("ngaykhoihanh", chuyenBien.getNgaykhoihanh());
        values.put("ngayketchuyen", chuyenBien.getNgayketchuyen());
        values.put("tongthu",chuyenBien.getTongthu());
        values.put("tongchi",chuyenBien.getTongchi());
        values.put("dachia",chuyenBien.getDachia());
        values.put("updatetime",chuyenBien.getUpdatetime());
        values.put("username",chuyenBien.getUsername());
        Uri uri = null;
        uri = mContentResolver.insert(StubProvider.CONTENT_URI_CHUYENBIEN, values);
        return uri;
    }
    private int ChuyenBien_deleteChuyenBien(int ID){
        return mContentResolver.delete(StubProvider.CONTENT_URI_CHUYENBIEN, "id=?", new String[]{ String.valueOf(ID)});
    }
    private ArrayList<ChuyenBien>ChuyenBien_getAllChuyenBien(){
        ArrayList<ChuyenBien>arrLocalChuyenBien=new ArrayList<>();
        Cursor cursor = mContentResolver.query(StubProvider.CONTENT_URI_CHUYENBIEN, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ChuyenBien chuyenbien = new ChuyenBien();
                    chuyenbien.setId(cursor.getInt(0));
                    chuyenbien.setServerkey(cursor.getInt(1));
                    chuyenbien.setRkey(cursor.getLong(2));
                    chuyenbien.setChuyenbien(cursor.getString(3));
                    chuyenbien.setTentau(cursor.getString(4));
                    chuyenbien.setNgaykhoihanh(cursor.getString(5));
                    chuyenbien.setNgayketchuyen(cursor.getString(6));
                    chuyenbien.setTongthu(cursor.getString(7));
                    chuyenbien.setTongchi(cursor.getString(8));
                    chuyenbien.setDachia(cursor.getInt(9));
                    chuyenbien.setUpdatetime((cursor.getString(10)));
                    chuyenbien.setUsername((cursor.getString(11)));
                    arrLocalChuyenBien.add(chuyenbien);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalChuyenBien;
    }
    private ArrayList<ChuyenBien>ChuyenBien_getChuaEditRkey(){
        ArrayList<ChuyenBien>arrLocalChuyenBien=new ArrayList<>();
        Uri URI_ChuaEditRkey = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/chuyenbien/chuaEditRkey");
        Cursor cursor = mContentResolver.query(URI_ChuaEditRkey, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ChuyenBien chuyenbien = new ChuyenBien();
                    chuyenbien.setId(cursor.getInt(0));
                    chuyenbien.setServerkey(cursor.getInt(1));
                    chuyenbien.setRkey(cursor.getLong(2));
                    chuyenbien.setChuyenbien(cursor.getString(3));
                    chuyenbien.setTentau(cursor.getString(4));
                    chuyenbien.setNgaykhoihanh(cursor.getString(5));
                    chuyenbien.setNgayketchuyen(cursor.getString(6));
                    chuyenbien.setTongthu(cursor.getString(7));
                    chuyenbien.setTongchi(cursor.getString(8));
                    chuyenbien.setDachia(cursor.getInt(9));
                    chuyenbien.setUpdatetime((cursor.getString(10)));
                    chuyenbien.setUsername((cursor.getString(11)));
                    arrLocalChuyenBien.add(chuyenbien);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalChuyenBien;
    }
    private int ChuyenBien_updateChuyenBien(ChuyenBien chuyenBien){
        ContentValues values = new ContentValues();
        values.put("serverkey", chuyenBien.getServerkey());
        values.put("rkey", chuyenBien.getRkey());
        values.put("chuyenbien", chuyenBien.getChuyenbien());
        values.put("tentau", chuyenBien.getTentau());
        values.put("ngaykhoihanh", chuyenBien.getNgaykhoihanh());
        values.put("ngayketchuyen", chuyenBien.getNgayketchuyen());
        values.put("tongthu",chuyenBien.getTongthu());
        values.put("tongchi",chuyenBien.getTongchi());
        values.put("dachia",chuyenBien.getDachia());
        values.put("updatetime",chuyenBien.getUpdatetime());
        values.put("username",chuyenBien.getUsername());
        int u= 0;
        u = mContentResolver.update(StubProvider.CONTENT_URI_CHUYENBIEN, values,"id=?", new String[]{ String.valueOf(chuyenBien.getId())});
        return u;
    }
    private boolean ChuyenBien_Exits(int serverKey){
        boolean r=false;
        Uri SYNCTABLE_URI_ID = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/chuyenbien/"+serverKey);
        Cursor cursor = null;
        cursor = mContentResolver.query(SYNCTABLE_URI_ID, null, null, null, null);
        if (cursor.moveToFirst()){
            r=true;
        }
        if (cursor!=null ){cursor.close();}
        return r;
    }
    //****************************************************Ticket****************************
    private Uri Ticket_addTicket(Ticket ticket){
        ContentValues values = new ContentValues();
        values.put("serverkey", ticket.getServerkey());
        values.put("rkey", ticket.getRkey());
        values.put("amount", ticket.getAmount());
        values.put("used", ticket.getUsed());
        values.put("opendate", ticket.getOpendate());
        values.put("lydo", ticket.getLydo());
        values.put("closedate", ticket.getClosedate());
        values.put("finished", ticket.getFinished());
        values.put("comeback", ticket.getComeback());
        values.put("updatetime",ticket.getUpdatetime());
        values.put("username", ticket.getUsername());
        Uri uri = null;
        uri = mContentResolver.insert(StubProvider.CONTENT_URI_TICKET, values);
        return uri;
    }
    private int Ticket_deleteTicket(int ID){
        return mContentResolver.delete(StubProvider.CONTENT_URI_TICKET, "id=?", new String[]{ String.valueOf(ID)});
    }
    private ArrayList<Ticket>Ticket_getAllTicket() {
        ArrayList<Ticket> arrLocalTicket = new ArrayList<>();
        Cursor cursor = mContentResolver.query(StubProvider.CONTENT_URI_TICKET, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Ticket ticket = new Ticket();
                    ticket.setId(cursor.getInt(0));
                    ticket.setServerkey(cursor.getInt(1));
                    ticket.setRkey(cursor.getLong(2));
                    ticket.setAmount((cursor.getString(3)));
                    ticket.setUsed(cursor.getString(4));
                    ticket.setOpendate(cursor.getString(5));
                    ticket.setLydo(cursor.getString(6));
                    ticket.setClosedate(cursor.getString(7));
                    ticket.setFinished(cursor.getInt(8));
                    ticket.setComeback(cursor.getString(9));
                    ticket.setUpdatetime(cursor.getString(10));
                    ticket.setUsername(cursor.getString(11));
                    arrLocalTicket.add(ticket);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return arrLocalTicket;
    }
    private ArrayList<Ticket>Ticket_getChuaEditRkey() {
        ArrayList<Ticket> arrLocalTicket = new ArrayList<>();
        Uri URI_ChuaEditRkey = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/ticket/chuaEditRkey");
        Cursor cursor = mContentResolver.query(URI_ChuaEditRkey, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Ticket ticket = new Ticket();
                    ticket.setId(cursor.getInt(0));
                    ticket.setServerkey(cursor.getInt(1));
                    ticket.setRkey(cursor.getLong(2));
                    ticket.setAmount((cursor.getString(3)));
                    ticket.setUsed(cursor.getString(4));
                    ticket.setOpendate(cursor.getString(5));
                    ticket.setLydo(cursor.getString(6));
                    ticket.setClosedate(cursor.getString(7));
                    ticket.setFinished(cursor.getInt(8));
                    ticket.setComeback(cursor.getString(9));
                    ticket.setUpdatetime(cursor.getString(10));
                    ticket.setUsername(cursor.getString(11));
                    arrLocalTicket.add(ticket);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return arrLocalTicket;
    }
    private int Ticket_updateTicket(Ticket ticket){
        ContentValues values = new ContentValues();
        values.put("serverkey", ticket.getServerkey());
        values.put("rkey", ticket.getRkey());
        values.put("amount", ticket.getAmount());
        values.put("used", ticket.getUsed());
        values.put("opendate", ticket.getOpendate());
        values.put("lydo", ticket.getLydo());
        values.put("closedate", ticket.getClosedate());
        values.put("finished", ticket.getFinished());
        values.put("comeback", ticket.getComeback());
        values.put("updatetime",ticket.getUpdatetime());
        values.put("username", ticket.getUsername());
        int u= 0;
        u = mContentResolver.update(StubProvider.CONTENT_URI_TICKET, values,"id=?", new String[]{ String.valueOf(ticket.getId())});
        return u;
    }
    private boolean Ticket_Exits(int serverKey){
        boolean r=false;
        Uri SYNCTABLE_URI_ID = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/ticket/"+serverKey);
        Cursor cursor = null;
        cursor = mContentResolver.query(SYNCTABLE_URI_ID, null, null, null, null);
        if (cursor.moveToFirst()){
            r=true;
        }
        if (cursor!=null ){cursor.close();}
        return r;
    }
    //******************************************************TicketDetail************************
    private Uri TicketDetail_addTicketDetail(TicketDetail ticketd){
        ContentValues values = new ContentValues();
        values.put("serverkey", ticketd.getServerkey());
        values.put("rkey", ticketd.getRkey());
        values.put("rkeyticket", ticketd.getRkeyticket());
        values.put("amount", ticketd.getAmount());
        values.put("foruser", ticketd.getForuser());
        values.put("ngayps", ticketd.getNgayps());
        values.put("notes", ticketd.getNotes());
        values.put("updatetime",ticketd.getUpdatetime());
        values.put("username", ticketd.getUsername());
        Uri uri = null;
        uri = mContentResolver.insert(StubProvider.CONTENT_URI_TICKETDETAIL, values);
        return uri;
    }
    private int TicketDetail_deleteTicketDetail(int ID){
        return mContentResolver.delete(StubProvider.CONTENT_URI_TICKETDETAIL, "id=?", new String[]{ String.valueOf(ID)});
    }
    private ArrayList<TicketDetail>TicketDetail_getAllTicketDetail(){
        ArrayList<TicketDetail>arrLocalTicketDetail=new ArrayList<>();
        Cursor cursor = mContentResolver.query(StubProvider.CONTENT_URI_TICKETDETAIL, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    TicketDetail ticketdetail = new TicketDetail();
                    ticketdetail.setId(cursor.getInt(0));
                    ticketdetail.setServerkey(cursor.getInt(1));
                    ticketdetail.setRkey(cursor.getLong(2));
                    ticketdetail.setRkeyticket(cursor.getLong(3));
                    ticketdetail.setForuser((cursor.getString(4)));
                    ticketdetail.setAmount(cursor.getString(5));
                    ticketdetail.setNgayps(cursor.getString(6));
                    ticketdetail.setNotes(cursor.getString(7));
                    ticketdetail.setUpdatetime(cursor.getString(8));
                    ticketdetail.setUsername(cursor.getString(9));
                    arrLocalTicketDetail.add(ticketdetail);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalTicketDetail;
    }
    private ArrayList<TicketDetail>TicketDetail_getChuaEditRkey(){
        ArrayList<TicketDetail>arrLocalTicketDetail=new ArrayList<>();
        Uri URI_ChuaEditRkey = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/ticketdetail/chuaEditRkey");
        Cursor cursor = mContentResolver.query(URI_ChuaEditRkey, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    TicketDetail ticketdetail = new TicketDetail();
                    ticketdetail.setId(cursor.getInt(0));
                    ticketdetail.setServerkey(cursor.getInt(1));
                    ticketdetail.setRkey(cursor.getLong(2));
                    ticketdetail.setRkeyticket(cursor.getLong(3));
                    ticketdetail.setForuser((cursor.getString(4)));
                    ticketdetail.setAmount(cursor.getString(5));
                    ticketdetail.setNgayps(cursor.getString(6));
                    ticketdetail.setNotes(cursor.getString(7));
                    ticketdetail.setUpdatetime(cursor.getString(8));
                    ticketdetail.setUsername(cursor.getString(9));
                    arrLocalTicketDetail.add(ticketdetail);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalTicketDetail;
    }
    private int TicketDetail_updateTicketDetail(TicketDetail ticketd){
        ContentValues values = new ContentValues();
        values.put("serverkey", ticketd.getServerkey());
        values.put("rkey", ticketd.getRkey());
        values.put("rkeyticket", ticketd.getRkeyticket());
        values.put("amount", ticketd.getAmount());
        values.put("foruser", ticketd.getForuser());
        values.put("ngayps", ticketd.getNgayps());
        values.put("notes", ticketd.getNotes());
        values.put("updatetime",ticketd.getUpdatetime());
        values.put("username", ticketd.getUsername());
        int u= 0;
        u = mContentResolver.update(StubProvider.CONTENT_URI_TICKETDETAIL, values,"id=?", new String[]{ String.valueOf(ticketd.getId())});
        return u;
    }
    private boolean TicketDetail_Exits(int serverKey){
        boolean r=false;
        Uri SYNCTABLE_URI_ID = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/ticketdetail/"+serverKey);
        Cursor cursor = null;
        cursor = mContentResolver.query(SYNCTABLE_URI_ID, null, null, null, null);
        if (cursor.moveToFirst()){
            r=true;
        }
        if (cursor!=null ){cursor.close();}
        return r;
    }
    //*************************************************DOiTac*****************************
    private Uri DoiTac_addDoiTac(DoiTac doitac){
        ContentValues values = new ContentValues();
        values.put("serverkey", doitac.getServerkey());
        values.put("rkey", doitac.getRkey());
        values.put("tendoitac", doitac.getTendoitac());
        values.put("sodienthoai", doitac.getSodienthoai());
        values.put("diachi", doitac.getDiachi());
        values.put("nocty",doitac.getNocty());
        values.put("ctyno",doitac.getCtyno());
        values.put("updatetime", doitac.getUpdatetime());
        Uri uri = null;
        uri = mContentResolver.insert(StubProvider.CONTENT_URI_DOITAC, values);
        return uri;
    }
    private int DoiTac_deleteDoiTac(int ID){
        return mContentResolver.delete(StubProvider.CONTENT_URI_DOITAC, "id=?", new String[]{ String.valueOf(ID)});
    }
    private ArrayList<DoiTac>DoiTac_getAllDoiTac(){
        ArrayList<DoiTac>arrLocalDoiTac=new ArrayList<>();
        Cursor cursor = mContentResolver.query(StubProvider.CONTENT_URI_DOITAC, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    DoiTac doitac = new DoiTac();
                    doitac.setId(cursor.getInt(0));
                    doitac.setServerkey(cursor.getInt(1));
                    doitac.setRkey(cursor.getLong(2));
                    doitac.setTendoitac(cursor.getString(3));
                    doitac.setSodienthoai(cursor.getString(4));
                    doitac.setDiachi(cursor.getString(5));
                    doitac.setNocty(cursor.getString(6));
                    doitac.setCtyno(cursor.getString(7));
                    doitac.setUpdatetime(cursor.getString(8));
                    arrLocalDoiTac.add(doitac);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalDoiTac;
    }
    private ArrayList<DoiTac>DoiTac_getChuaEditRkey(){
        ArrayList<DoiTac>arrLocalDoiTac=new ArrayList<>();
        Uri URI_ChuaEditRkey = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/doitac/chuaEditRkey");
        Cursor cursor = mContentResolver.query(URI_ChuaEditRkey, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    DoiTac doitac = new DoiTac();
                    doitac.setId(cursor.getInt(0));
                    doitac.setServerkey(cursor.getInt(1));
                    doitac.setRkey(cursor.getLong(2));
                    doitac.setTendoitac(cursor.getString(3));
                    doitac.setSodienthoai(cursor.getString(4));
                    doitac.setDiachi(cursor.getString(5));
                    doitac.setNocty(cursor.getString(6));
                    doitac.setCtyno(cursor.getString(7));
                    doitac.setUpdatetime(cursor.getString(8));
                    arrLocalDoiTac.add(doitac);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalDoiTac;
    }
    private int DoiTac_updateDoiTac(DoiTac doitac){
        ContentValues values = new ContentValues();
        values.put("serverkey", doitac.getServerkey());
        values.put("rkey", doitac.getRkey());
        values.put("tendoitac", doitac.getTendoitac());
        values.put("sodienthoai", doitac.getSodienthoai());
        values.put("diachi", doitac.getDiachi());
        values.put("nocty",doitac.getNocty());
        values.put("ctyno",doitac.getCtyno());
        values.put("updatetime", doitac.getUpdatetime());
        int u= 0;
        u = mContentResolver.update(StubProvider.CONTENT_URI_DOITAC, values,"id=?", new String[]{ String.valueOf(doitac.getId())});
        return u;
    }
    private boolean DoiTac_Exits(int serverKey){
        boolean r=false;
        Uri SYNCTABLE_URI_ID = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/doitac/"+serverKey);
        Cursor cursor = null;
        cursor = mContentResolver.query(SYNCTABLE_URI_ID, null, null, null, null);
        if (cursor.moveToFirst()){
            r=true;
        }
        if (cursor!=null ){cursor.close();}
        return r;
    }
    //*****************************************************KhachHang***********************
    private Uri KhachHang_addKhachHang(KhachHang khachhang){
        ContentValues values = new ContentValues();
        values.put("serverkey", khachhang.getServerkey());
        values.put("rkey", khachhang.getRkey());
        values.put("tenkhach", khachhang.getTenkhach());
        values.put("sodienthoai", khachhang.getSodienthoai());
        values.put("diachi", khachhang.getDiachi());
        values.put("nocty",khachhang.getNocty());
        values.put("ctyno",khachhang.getCtyno());
        values.put("updatetime", khachhang.getUpdatetime());
        Uri uri = null;
        uri = mContentResolver.insert(StubProvider.CONTENT_URI_KHACHHANG, values);
        return uri;
    }
    private int KhachHang_deleteKhachHang(int ID){
        return mContentResolver.delete(StubProvider.CONTENT_URI_KHACHHANG, "id=?", new String[]{ String.valueOf(ID)});
    }
    private ArrayList<KhachHang>KhachHang_getAllKhachHang(){
        ArrayList<KhachHang>arrLocalKhachHang=new ArrayList<>();
        Cursor cursor = mContentResolver.query(StubProvider.CONTENT_URI_KHACHHANG, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    KhachHang khachhang = new KhachHang();
                    khachhang.setId(cursor.getInt(0));
                    khachhang.setServerkey(cursor.getInt(1));
                    khachhang.setRkey(cursor.getLong(2));
                    khachhang.setTenkhach(cursor.getString(3));
                    khachhang.setSodienthoai(cursor.getString(4));
                    khachhang.setDiachi(cursor.getString(5));
                    khachhang.setNocty(cursor.getString(6));
                    khachhang.setCtyno(cursor.getString(7));
                    khachhang.setUpdatetime(cursor.getString(8));
                    arrLocalKhachHang.add(khachhang);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalKhachHang;
    }
    private ArrayList<KhachHang>KhachHang_getChuaEditRkey(){
        ArrayList<KhachHang>arrLocalKhachHang=new ArrayList<>();
        Uri URI_ChuaEditRkey = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/khachhang/chuaEditRkey");
        Cursor cursor = mContentResolver.query(URI_ChuaEditRkey, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    KhachHang khachhang = new KhachHang();
                    khachhang.setId(cursor.getInt(0));
                    khachhang.setServerkey(cursor.getInt(1));
                    khachhang.setRkey(cursor.getLong(2));
                    khachhang.setTenkhach(cursor.getString(3));
                    khachhang.setSodienthoai(cursor.getString(4));
                    khachhang.setDiachi(cursor.getString(5));
                    khachhang.setNocty(cursor.getString(6));
                    khachhang.setCtyno(cursor.getString(7));
                    khachhang.setUpdatetime(cursor.getString(8));
                    arrLocalKhachHang.add(khachhang);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalKhachHang;
    }
    private int KhachHang_updateKhachHang(KhachHang khachhang){
        ContentValues values = new ContentValues();
        values.put("serverkey", khachhang.getServerkey());
        values.put("rkey", khachhang.getRkey());
        values.put("tenkhach", khachhang.getTenkhach());
        values.put("sodienthoai", khachhang.getSodienthoai());
        values.put("diachi", khachhang.getDiachi());
        values.put("nocty",khachhang.getNocty());
        values.put("ctyno",khachhang.getCtyno());
        values.put("updatetime", khachhang.getUpdatetime());
        int u= 0;
        u = mContentResolver.update(StubProvider.CONTENT_URI_KHACHHANG, values,"id=?", new String[]{ String.valueOf(khachhang.getId())});
        return u;
    }
    private boolean KhachHang_Exits(int serverKey){
        boolean r=false;
        Uri SYNCTABLE_URI_ID = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/khachhang/"+serverKey);
        Cursor cursor = null;
        cursor = mContentResolver.query(SYNCTABLE_URI_ID, null, null, null, null);
        if (cursor.moveToFirst()){
            r=true;
        }
        if (cursor!=null ){cursor.close();}
        return r;
    }
    //**********************************************DebtBook**************************
    private Uri DebtBook_addDebtBook(DebtBook debtbook){
        ContentValues values = new ContentValues();
        values.put("serverkey", debtbook.getServerkey());
        values.put("rkey", debtbook.getRkey());
        values.put("rkeythuyenvien", debtbook.getRkeythuyenvien());
        values.put("rkeyticket", debtbook.getRkeyticket());
        values.put("chuyenbien", debtbook.getChuyenbien());
        values.put("ten", debtbook.getTen());
        values.put("sotien", debtbook.getSotien());
        values.put("ngayps", debtbook.getNgayps());
        values.put("lydo", debtbook.getLydo());
        values.put("updatetime",debtbook.getUpdatetime());
        values.put("username", debtbook.getUsername());
        Uri uri = null;
        uri = mContentResolver.insert(StubProvider.CONTENT_URI_DEBTBOOK, values);
        return uri;
    }
    private int DebtBook_deleteDebtBook(int ID){
        return mContentResolver.delete(StubProvider.CONTENT_URI_DEBTBOOK, "id=?", new String[]{ String.valueOf(ID)});
    }
    private ArrayList<DebtBook>DebtBook_getAllDebtBook(){
        ArrayList<DebtBook>arrLocalDebtBook=new ArrayList<>();
        Cursor cursor = mContentResolver.query(StubProvider.CONTENT_URI_DEBTBOOK, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    DebtBook debtbook = new DebtBook();
                    debtbook.setId(cursor.getInt(0));
                    debtbook.setServerkey(cursor.getInt(1));
                    debtbook.setRkey(cursor.getLong(2));
                    debtbook.setRkeythuyenvien((cursor.getLong(3)));
                    debtbook.setRkeyticket(cursor.getLong(4));
                    debtbook.setChuyenbien(cursor.getString(5));
                    debtbook.setTen(cursor.getString(6));
                    debtbook.setSotien(cursor.getString(7));
                    debtbook.setNgayps(cursor.getString(8));
                    debtbook.setLydo(cursor.getString(9));
                    debtbook.setUpdatetime(cursor.getString(10));
                    debtbook.setUsername(cursor.getString(11));
                    arrLocalDebtBook.add(debtbook);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalDebtBook;
    }
    private ArrayList<DebtBook>DebtBook_getChuaEditRkey(){
        ArrayList<DebtBook>arrLocalDebtBook=new ArrayList<>();
        Uri URI_ChuaEditRkey = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/debtbook/chuaEditRkey");
        Cursor cursor = mContentResolver.query(URI_ChuaEditRkey, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    DebtBook debtbook = new DebtBook();
                    debtbook.setId(cursor.getInt(0));
                    debtbook.setServerkey(cursor.getInt(1));
                    debtbook.setRkey(cursor.getLong(2));
                    debtbook.setRkeythuyenvien((cursor.getLong(3)));
                    debtbook.setRkeyticket(cursor.getLong(4));
                    debtbook.setChuyenbien(cursor.getString(5));
                    debtbook.setTen(cursor.getString(6));
                    debtbook.setSotien(cursor.getString(7));
                    debtbook.setNgayps(cursor.getString(8));
                    debtbook.setLydo(cursor.getString(9));
                    debtbook.setUpdatetime(cursor.getString(10));
                    debtbook.setUsername(cursor.getString(11));
                    arrLocalDebtBook.add(debtbook);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalDebtBook;
    }
    private int DebtBook_updateDebtBook(DebtBook debtbook){
        ContentValues values = new ContentValues();
        values.put("serverkey", debtbook.getServerkey());
        values.put("rkey", debtbook.getRkey());
        values.put("rkeythuyenvien", debtbook.getRkeythuyenvien());
        values.put("rkeyticket", debtbook.getRkeyticket());
        values.put("chuyenbien", debtbook.getChuyenbien());
        values.put("ten", debtbook.getTen());
        values.put("sotien", debtbook.getSotien());
        values.put("ngayps", debtbook.getNgayps());
        values.put("lydo", debtbook.getLydo());
        values.put("updatetime",debtbook.getUpdatetime());
        values.put("username", debtbook.getUsername());
        int u= 0;
        u = mContentResolver.update(StubProvider.CONTENT_URI_DEBTBOOK, values,"id=?", new String[]{ String.valueOf(debtbook.getId())});
        return u;
    }
    private boolean DebtBook_Exits(int serverKey){
        boolean r=false;
        Uri SYNCTABLE_URI_ID = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/debtbook/"+serverKey);
        Cursor cursor = null;
        cursor = mContentResolver.query(SYNCTABLE_URI_ID, null, null, null, null);
        if (cursor.moveToFirst()){
            r=true;
        }
        if (cursor!=null ){cursor.close();}
        return r;
    }
    //********************************************************Thu****************************
    private Uri Thu_addThu(Thu thu){
        ContentValues values = new ContentValues();
        values.put("serverkey", thu.getServerkey());
        values.put("rkey", thu.getRkey());
        values.put("rkeychuyenbien", thu.getRkeychuyenbien());
        values.put("rkeykhachhang", thu.getRkeykhachhang());
        values.put("lydo", thu.getLydo());
        values.put("ngayps", thu.getNgayps());
        values.put("giatri", thu.getGiatri());
        values.put("datra", thu.getDatra());
        values.put("updatetime",thu.getUpdatetime());
        values.put("username",thu.getUsername());
        Uri uri = null;
        uri = mContentResolver.insert(StubProvider.CONTENT_URI_THU, values);
        return uri;
    }
    private int Thu_deleteThu(int ID){
        return mContentResolver.delete(StubProvider.CONTENT_URI_THU, "id=?", new String[]{ String.valueOf(ID)});
    }
    private ArrayList<Thu>Thu_getAllThu(){
        ArrayList<Thu>arrLocalThu=new ArrayList<>();
        Cursor cursor = mContentResolver.query(StubProvider.CONTENT_URI_THU, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Thu thu = new Thu();
                    thu.setId(cursor.getInt(0));
                    thu.setServerkey(cursor.getInt(1));
                    thu.setRkey(cursor.getLong(2));
                    thu.setRkeychuyenbien(cursor.getLong(3));
                    thu.setRkeykhachhang(cursor.getLong(4));
                    thu.setLydo(cursor.getString(5));
                    thu.setNgayps(cursor.getString(6));
                    thu.setGiatri(cursor.getString(7));
                    thu.setDatra(cursor.getString(8));
                    thu.setUpdatetime(cursor.getString(9));
                    thu.setUsername(cursor.getString(10));
                    arrLocalThu.add(thu);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalThu;
    }
    private ArrayList<Thu>Thu_getChuaEditRkey(){
        ArrayList<Thu>arrLocalThu=new ArrayList<>();
        Uri URI_ChuaEditRkey = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/thu/chuaEditRkey");
        Cursor cursor = mContentResolver.query(URI_ChuaEditRkey, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Thu thu = new Thu();
                    thu.setId(cursor.getInt(0));
                    thu.setServerkey(cursor.getInt(1));
                    thu.setRkey(cursor.getLong(2));
                    thu.setRkeychuyenbien(cursor.getLong(3));
                    thu.setRkeykhachhang(cursor.getLong(4));
                    thu.setLydo(cursor.getString(5));
                    thu.setNgayps(cursor.getString(6));
                    thu.setGiatri(cursor.getString(7));
                    thu.setDatra(cursor.getString(8));
                    thu.setUpdatetime(cursor.getString(9));
                    thu.setUsername(cursor.getString(10));
                    arrLocalThu.add(thu);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalThu;
    }
    private int Thu_updateThu(Thu thu){
        ContentValues values = new ContentValues();
        values.put("serverkey", thu.getServerkey());
        values.put("rkey", thu.getRkey());
        values.put("rkeychuyenbien", thu.getRkeychuyenbien());
        values.put("rkeykhachhang", thu.getRkeykhachhang());
        values.put("lydo", thu.getLydo());
        values.put("ngayps", thu.getNgayps());
        values.put("giatri", thu.getGiatri());
        values.put("datra", thu.getDatra());
        values.put("updatetime",thu.getUpdatetime());
        values.put("username",thu.getUsername());
        int u= 0;
        u = mContentResolver.update(StubProvider.CONTENT_URI_THU, values,"id=?", new String[]{ String.valueOf(thu.getId())});
        return u;
    }
    private boolean Thu_Exits(int serverKey){
        boolean r=false;
        Uri SYNCTABLE_URI_ID = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/thu/"+serverKey);
        Cursor cursor = null;
        cursor = mContentResolver.query(SYNCTABLE_URI_ID, null, null, null, null);
        if (cursor.moveToFirst()){
            r=true;
        }
        if (cursor!=null ){cursor.close();}
        return r;
    }
    //*************************************************ThuDetail******************************
    private Uri ThuDetail_addThuDetail(ThuDetail thudetail){
        ContentValues values = new ContentValues();
        values.put("serverkey", Integer.valueOf(thudetail.getServerkey()));
        values.put("rkey", thudetail.getRkey());
        values.put("rkeythu", thudetail.getRkeythu());
        values.put("tenhs", thudetail.getTenhs());
        values.put("rkeyhs", thudetail.getRkeyhs());
        values.put("soluong", thudetail.getSoluong());
        values.put("dongia", thudetail.getDongia());
        values.put("thanhtien", thudetail.getThanhtien());
        values.put("updatetime",thudetail.getUpdatetime());
        values.put("username",thudetail.getUsername());
        Uri uri = null;
        uri = mContentResolver.insert(StubProvider.CONTENT_URI_THUDETAIL, values);
        return uri;
    }
    private int ThuDetail_deleteThuDetail(int ID){
        return mContentResolver.delete(StubProvider.CONTENT_URI_THUDETAIL, "id=?", new String[]{ String.valueOf(ID)});
    }
    private ArrayList<ThuDetail>ThuDetail_getAllThuDetail(){
        ArrayList<ThuDetail>arrLocalThuDetail=new ArrayList<>();
        Cursor cursor = mContentResolver.query(StubProvider.CONTENT_URI_THUDETAIL, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ThuDetail thudetail = new ThuDetail();
                    thudetail.setId(cursor.getInt(0));
                    thudetail.setServerkey(cursor.getInt(1));
                    thudetail.setRkey(cursor.getLong(2));
                    thudetail.setRkeythu(cursor.getLong(3));
                    thudetail.setTenhs(cursor.getString(4));
                    thudetail.setRkeyhs(cursor.getLong(5));
                    thudetail.setSoluong(cursor.getString(6));
                    thudetail.setDongia(cursor.getString(7));
                    thudetail.setThanhtien(cursor.getString(8));
                    thudetail.setUpdatetime(cursor.getString(9));
                    thudetail.setUsername(cursor.getString(10));
                    arrLocalThuDetail.add(thudetail);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalThuDetail;
    }
    private ArrayList<ThuDetail>ThuDetail_getChuaEditRkey(){
        ArrayList<ThuDetail>arrLocalThuDetail=new ArrayList<>();
        Uri URI_ChuaEditRkey = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/thudetail/chuaEditRkey");
        Cursor cursor = mContentResolver.query(URI_ChuaEditRkey, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ThuDetail thudetail = new ThuDetail();
                    thudetail.setId(cursor.getInt(0));
                    thudetail.setServerkey(cursor.getInt(1));
                    thudetail.setRkey(cursor.getLong(2));
                    thudetail.setRkeythu(cursor.getLong(3));
                    thudetail.setTenhs(cursor.getString(4));
                    thudetail.setRkeyhs(cursor.getLong(5));
                    thudetail.setSoluong(cursor.getString(6));
                    thudetail.setDongia(cursor.getString(7));
                    thudetail.setThanhtien(cursor.getString(8));
                    thudetail.setUpdatetime(cursor.getString(9));
                    thudetail.setUsername(cursor.getString(10));
                    arrLocalThuDetail.add(thudetail);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalThuDetail;
    }
    private int ThuDetail_updateThuDetail(ThuDetail thudetail){
        ContentValues values = new ContentValues();
        values.put("serverkey", Integer.valueOf(thudetail.getServerkey()));
        values.put("rkey", thudetail.getRkey());
        values.put("rkeythu", thudetail.getRkeythu());
        values.put("tenhs", thudetail.getTenhs());
        values.put("rkeyhs", thudetail.getRkeyhs());
        values.put("soluong", thudetail.getSoluong());
        values.put("dongia", thudetail.getDongia());
        values.put("thanhtien", thudetail.getThanhtien());
        values.put("updatetime",thudetail.getUpdatetime());
        values.put("username",thudetail.getUsername());
        int u= 0;
        u = mContentResolver.update(StubProvider.CONTENT_URI_THUDETAIL, values,"id=?", new String[]{ String.valueOf(thudetail.getId())});
        return u;
    }
    private boolean ThuDetail_Exits(int serverKey){
        boolean r=false;
        Uri SYNCTABLE_URI_ID = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/thudetail/"+serverKey);
        Cursor cursor = null;
        cursor = mContentResolver.query(SYNCTABLE_URI_ID, null, null, null, null);
        if (cursor.moveToFirst()){
            r=true;
        }
        if (cursor!=null ){cursor.close();}
        return r;
    }
    //*************************************************Chi********************************
    private Uri Chi_addChi(Chi chi){
        ContentValues values = new ContentValues();
        values.put("serverkey", Integer.valueOf(chi.getServerkey()));
        values.put("rkey", chi.getRkey());
        values.put("rkeychuyenbien", chi.getRkeychuyenbien());
        values.put("rkeydoitac", chi.getRkeydoitac());
        values.put("rkeyticket", chi.getRkeyticket());
        values.put("lydo", chi.getLydo());
        values.put("ngayps", chi.getNgayps());
        values.put("giatri", chi.getGiatri());
        values.put("datra", chi.getDatra());
        values.put("updatetime",chi.getUpdatetime());
        values.put("username",chi.getUsername());
        Uri uri = null;
        uri = mContentResolver.insert(StubProvider.CONTENT_URI_CHI, values);
        return uri;
    }
    private int Chi_deleteChi(int ID){
        return mContentResolver.delete(StubProvider.CONTENT_URI_CHI, "id=?", new String[]{ String.valueOf(ID)});
    }
    private ArrayList<Chi>Chi_getAllChi(){
        ArrayList<Chi>arrLocalChi=new ArrayList<>();
        Cursor cursor = mContentResolver.query(StubProvider.CONTENT_URI_CHI, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Chi chi = new Chi();
                    chi.setId(cursor.getInt(0));
                    chi.setServerkey(cursor.getInt(1));
                    chi.setRkey(cursor.getLong(2));
                    chi.setRkeychuyenbien(cursor.getLong(3));
                    chi.setRkeydoitac(cursor.getLong(4));
                    chi.setRkeyticket(cursor.getLong(5));
                    chi.setLydo(cursor.getString(6));
                    chi.setNgayps(cursor.getString(7));
                    chi.setGiatri(cursor.getString(8));
                    chi.setDatra(cursor.getString(9));
                    chi.setUpdatetime(cursor.getString(10));
                    chi.setUsername(cursor.getString(11));
                    arrLocalChi.add(chi);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalChi;
    }
    private ArrayList<Chi>Chi_getChuaEditRkey(){
        ArrayList<Chi>arrLocalChi=new ArrayList<>();
        Uri URI_ChuaEditRkey = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/chi/chuaEditRkey");
        Cursor cursor = mContentResolver.query(URI_ChuaEditRkey, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Chi chi = new Chi();
                    chi.setId(cursor.getInt(0));
                    chi.setServerkey(cursor.getInt(1));
                    chi.setRkey(cursor.getLong(2));
                    chi.setRkeychuyenbien(cursor.getLong(3));
                    chi.setRkeydoitac(cursor.getLong(4));
                    chi.setRkeyticket(cursor.getLong(5));
                    chi.setLydo(cursor.getString(6));
                    chi.setNgayps(cursor.getString(7));
                    chi.setGiatri(cursor.getString(8));
                    chi.setDatra(cursor.getString(9));
                    chi.setUpdatetime(cursor.getString(10));
                    chi.setUsername(cursor.getString(11));
                    arrLocalChi.add(chi);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalChi;
    }
    private int Chi_updateChi(Chi chi){
        ContentValues values = new ContentValues();
        values.put("serverkey", Integer.valueOf(chi.getServerkey()));
        values.put("rkey", chi.getRkey());
        values.put("rkeychuyenbien", chi.getRkeychuyenbien());
        values.put("rkeydoitac", chi.getRkeydoitac());
        values.put("rkeyticket", chi.getRkeyticket());
        values.put("lydo", chi.getLydo());
        values.put("ngayps", chi.getNgayps());
        values.put("giatri", chi.getGiatri());
        values.put("datra", chi.getDatra());
        values.put("updatetime",chi.getUpdatetime());
        values.put("username",chi.getUsername());
        int u= 0;
        u = mContentResolver.update(StubProvider.CONTENT_URI_CHI, values,"id=?", new String[]{ String.valueOf(chi.getId())});
        return u;
    }
    private boolean Chi_Exits(int serverKey){
        boolean r=false;
        Uri SYNCTABLE_URI_ID = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/chi/"+serverKey);
        Cursor cursor = null;
        cursor = mContentResolver.query(SYNCTABLE_URI_ID, null, null, null, null);
        if (cursor.moveToFirst()){
            r=true;
        }
        if (cursor!=null ){cursor.close();}
        return r;
    }
    //*********************************************ChiDetail****************************
    private Uri ChiDetail_addChiDetail(ChiDetail chidetail){
        ContentValues values = new ContentValues();
        values.put("serverkey", Integer.valueOf(chidetail.getServerkey()));
        values.put("rkey", chidetail.getRkey());
        values.put("rkeychi", chidetail.getRkeychi());
        values.put("tenchuyenbien", chidetail.getTenchuyenbien());
        values.put("tendoitac", chidetail.getTendoitac());
        values.put("sanpham", chidetail.getSanpham());
        values.put("soluong", chidetail.getSoluong());
        values.put("dongia", chidetail.getDongia());
        values.put("thanhtien", chidetail.getThanhtien());
        values.put("updatetime",chidetail.getUpdatetime());
        values.put("username",chidetail.getUsername());
        Uri uri = null;
        uri = mContentResolver.insert(StubProvider.CONTENT_URI_CHIDETAIL, values);
        return uri;
    }
    private int ChiDetail_deleteChiDetail(int ID){
        return mContentResolver.delete(StubProvider.CONTENT_URI_CHIDETAIL, "id=?", new String[]{ String.valueOf(ID)});
    }
    private ArrayList<ChiDetail>ChiDetail_getAllChiDetail(){
        ArrayList<ChiDetail>arrLocalChiDetail=new ArrayList<>();
        Cursor cursor = mContentResolver.query(StubProvider.CONTENT_URI_CHIDETAIL, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ChiDetail chidetail = new ChiDetail();
                    chidetail.setId(cursor.getInt(0));
                    chidetail.setServerkey(cursor.getInt(1));
                    chidetail.setRkey(cursor.getLong(2));
                    chidetail.setRkeychi(cursor.getLong(3));
                    chidetail.setTenchuyenbien(cursor.getString(4));
                    chidetail.setTendoitac(cursor.getString(5));
                    chidetail.setSanpham(cursor.getString(6));
                    chidetail.setSoluong(cursor.getString(7));
                    chidetail.setDongia(cursor.getString(8));
                    chidetail.setThanhtien(cursor.getString(9));
                    chidetail.setUpdatetime(cursor.getString(10));
                    chidetail.setUsername(cursor.getString(11));
                    arrLocalChiDetail.add(chidetail);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalChiDetail;
    }
    private ArrayList<ChiDetail>ChiDetail_getChuaEditRkey(){
        ArrayList<ChiDetail>arrLocalChiDetail=new ArrayList<>();
        Uri URI_ChuaEditRkey = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/chidetail/chuaEditRkey");
        Cursor cursor = mContentResolver.query(URI_ChuaEditRkey, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ChiDetail chidetail = new ChiDetail();
                    chidetail.setId(cursor.getInt(0));
                    chidetail.setServerkey(cursor.getInt(1));
                    chidetail.setRkey(cursor.getLong(2));
                    chidetail.setRkeychi(cursor.getLong(3));
                    chidetail.setTenchuyenbien(cursor.getString(4));
                    chidetail.setTendoitac(cursor.getString(5));
                    chidetail.setSanpham(cursor.getString(6));
                    chidetail.setSoluong(cursor.getString(7));
                    chidetail.setDongia(cursor.getString(8));
                    chidetail.setThanhtien(cursor.getString(9));
                    chidetail.setUpdatetime(cursor.getString(10));
                    chidetail.setUsername(cursor.getString(11));
                    arrLocalChiDetail.add(chidetail);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalChiDetail;
    }
    private int ChiDetail_updateChiDetail(ChiDetail chidetail){
        ContentValues values = new ContentValues();
        values.put("serverkey", Integer.valueOf(chidetail.getServerkey()));
        values.put("rkey", chidetail.getRkey());
        values.put("rkeychi", chidetail.getRkeychi());
        values.put("tenchuyenbien", chidetail.getTenchuyenbien());
        values.put("tendoitac", chidetail.getTendoitac());
        values.put("sanpham", chidetail.getSanpham());
        values.put("soluong", chidetail.getSoluong());
        values.put("dongia", chidetail.getDongia());
        values.put("thanhtien", chidetail.getThanhtien());
        values.put("updatetime",chidetail.getUpdatetime());
        values.put("username",chidetail.getUsername());
        int u= 0;
        u = mContentResolver.update(StubProvider.CONTENT_URI_CHIDETAIL, values,"id=?", new String[]{ String.valueOf(chidetail.getId())});
        return u;
    }
    private boolean ChiDetail_Exits(int serverKey){
        boolean r=false;
        Uri SYNCTABLE_URI_ID = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/chidetail/"+serverKey);
        Cursor cursor = null;
        cursor = mContentResolver.query(SYNCTABLE_URI_ID, null, null, null, null);
        if (cursor.moveToFirst()){
            r=true;
        }
        if (cursor!=null ){cursor.close();}
        return r;
    }
    //************************************************ImgStore*******************************
    private Uri ImgStore_addImgStore(ImgStore imgstore){
        ContentValues values = new ContentValues();
        values.put("serverkey", imgstore.getServerkey());
        values.put("storekey", imgstore.getStorekey());
        values.put("fortable", imgstore.getFortable());
        values.put("imgpath",imgstore.getImgpath());
        values.put("ngayps", imgstore.getNgayps());
        values.put("updatetime", imgstore.getUpdatetime());
        values.put("username", imgstore.getUsername());
        Uri uri = null;
        uri = mContentResolver.insert(StubProvider.CONTENT_URI_IMGSTORE, values);
        return uri;
    }
    private int ImgStore_deleteImgStore(ImgStore imgStore){
        ImgStore imgs=ImgStore_getImgStoreByServerKey(imgStore.getServerkey());
        if (imgs==null){
            return 0;
        }
        String s=imgs.getImgpath()+"";
        if (s.length()>4){
            File file = new File(s);
            if (file.exists()) {
                file.delete();
            }
        }
        return mContentResolver.delete(StubProvider.CONTENT_URI_IMGSTORE, "id=?", new String[]{ String.valueOf(imgStore.getId())});
    }
    private ArrayList<ImgStore>ImgStore_getAllImgStore(){
        ArrayList<ImgStore>arrLocalImgStore=new ArrayList<>();
        Cursor cursor = mContentResolver.query(StubProvider.CONTENT_URI_IMGSTORE, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ImgStore imgstore = new ImgStore();
                    imgstore.setId(cursor.getInt(0));
                    imgstore.setServerkey(cursor.getInt(1));
                    imgstore.setStorekey(cursor.getLong(2));
                    imgstore.setFortable(cursor.getString(3));
                    imgstore.setImgpath(cursor.getString(4));
                    imgstore.setNgayps(cursor.getString(5));
                    imgstore.setUpdatetime(cursor.getString(6));
                    imgstore.setUsername(cursor.getString(7));
                    arrLocalImgStore.add(imgstore);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalImgStore;
    }
    private ArrayList<ImgStore>ImgStore_getChuaEditRkey(){
        ArrayList<ImgStore>arrLocalImgStore=new ArrayList<>();
        Uri URI_ChuaEditRkey = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/imgstore/chuaEditRkey");
        Cursor cursor = mContentResolver.query(URI_ChuaEditRkey, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ImgStore imgstore = new ImgStore();
                    imgstore.setId(cursor.getInt(0));
                    imgstore.setServerkey(cursor.getInt(1));
                    imgstore.setStorekey(cursor.getLong(2));
                    imgstore.setFortable(cursor.getString(3));
                    imgstore.setImgpath(cursor.getString(4));
                    imgstore.setNgayps(cursor.getString(5));
                    imgstore.setUpdatetime(cursor.getString(6));
                    imgstore.setUsername(cursor.getString(7));
                    arrLocalImgStore.add(imgstore);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalImgStore;
    }
    private ArrayList<ImgStore>ImgStore_getAllBadLink(){
        ArrayList<ImgStore>arrLocalImgStore=new ArrayList<>();
        Uri URI_ChuaEditRkey = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/imgstore/badlink");
        Cursor cursor = mContentResolver.query(URI_ChuaEditRkey, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ImgStore imgstore = new ImgStore();
                    imgstore.setId(cursor.getInt(0));
                    imgstore.setServerkey(cursor.getInt(1));
                    imgstore.setStorekey(cursor.getLong(2));
                    imgstore.setFortable(cursor.getString(3));
                    imgstore.setImgpath(cursor.getString(4));
                    imgstore.setNgayps(cursor.getString(5));
                    imgstore.setUpdatetime(cursor.getString(6));
                    imgstore.setUsername(cursor.getString(7));
                    arrLocalImgStore.add(imgstore);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalImgStore;
    }
    private ImgStore ImgStore_getImgStoreByServerKey(int ServerKey){
        ImgStore imgstore = new ImgStore();
        Uri ImgStore_URI_ID = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/imgstore/"+ServerKey);
        Cursor cursor = null;
        cursor = mContentResolver.query(ImgStore_URI_ID, null, null, null, null);
        if (cursor.moveToFirst()) {
            imgstore.setId(cursor.getInt(0));
            imgstore.setServerkey(cursor.getInt(1));
            imgstore.setStorekey(cursor.getLong(2));
            imgstore.setFortable(cursor.getString(3));
            imgstore.setImgpath(cursor.getString(4));
            imgstore.setNgayps(cursor.getString(5));
            imgstore.setUpdatetime(cursor.getString(6));
            imgstore.setUsername(cursor.getString(7));
        }else{
            return null;
        }
        if (cursor!=null ){cursor.close();}
        return imgstore;
    }
    private int ImgStore_updateImgStore(ImgStore imgstore){
        ContentValues values = new ContentValues();
        values.put("serverkey", imgstore.getServerkey());
        values.put("storekey", imgstore.getStorekey());
        values.put("fortable", imgstore.getFortable());
        values.put("imgpath",imgstore.getImgpath());
        values.put("ngayps", imgstore.getNgayps());
        values.put("updatetime", imgstore.getUpdatetime());
        values.put("username", imgstore.getUsername());
        int u= 0;
        u = mContentResolver.update(StubProvider.CONTENT_URI_IMGSTORE, values,"id=?", new String[]{ String.valueOf(imgstore.getId())});
        return u;
    }
    private boolean ImgStore_Exits(int serverKey){
        boolean r=false;
        Uri SYNCTABLE_URI_ID = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/imgstore/"+serverKey);
        Cursor cursor = null;
        cursor = mContentResolver.query(SYNCTABLE_URI_ID, null, null, null, null);
        if (cursor.moveToFirst()){
            r=true;
        }
        if (cursor!=null ){cursor.close();}
        return r;
    }
    //******************************************DiemDD**************************************
    private Uri DiemDD_addDiemDD(DiemDD diemdd){
        ContentValues values = new ContentValues();
        values.put("serverkey", Integer.valueOf(diemdd.getServerkey()));
        values.put("rkey", diemdd.getRkey());
        values.put("eater", diemdd.getEater());
        values.put("eatername", diemdd.getEatername());
        values.put("chuyenbien", diemdd.getChuyenbien());
        values.put("diemeater", Integer.valueOf(diemdd.getDiemeater()));
        values.put("lydo", diemdd.getLydo());
        values.put("chucvu", diemdd.getChucvu());
        values.put("feeder", diemdd.getFeeder());
        values.put("diemfeeder", Integer.valueOf(diemdd.getDiemfeeder()));
        values.put("ngayps", diemdd.getNgayps());
        values.put("updatetime",diemdd.getUpdatetime());
        values.put("username",diemdd.getUsername());
        Uri uri = null;
        uri = mContentResolver.insert(StubProvider.CONTENT_URI_DIEMDD, values);
        return uri;
    }
    private int DiemDD_deleteDiemDD(int ID){
        return mContentResolver.delete(StubProvider.CONTENT_URI_DIEMDD, "id=?", new String[]{ String.valueOf(ID)});
    }
    private ArrayList<DiemDD>DiemDD_getAllDiemDD(){
        ArrayList<DiemDD>arrLocalDiemDD=new ArrayList<>();
        Cursor cursor = mContentResolver.query(StubProvider.CONTENT_URI_DIEMDD, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    DiemDD diemdd = new DiemDD();
                    diemdd.setId(cursor.getInt(0));
                    diemdd.setServerkey(cursor.getInt(1));
                    diemdd.setRkey(cursor.getLong(2));
                    diemdd.setEater(cursor.getLong(3));
                    diemdd.setEatername(cursor.getString(4));
                    diemdd.setChuyenbien(cursor.getString(5));
                    diemdd.setDiemeater(cursor.getInt(6));
                    diemdd.setLydo(cursor.getString(7));
                    diemdd.setChucvu(cursor.getString(8));
                    diemdd.setFeeder(cursor.getLong(9));
                    diemdd.setDiemfeeder(cursor.getInt(10));
                    diemdd.setNgayps(cursor.getString(11));
                    diemdd.setUpdatetime(cursor.getString(12));
                    diemdd.setUsername(cursor.getString(13));
                    arrLocalDiemDD.add(diemdd);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalDiemDD;
    }
    private ArrayList<DiemDD>DiemDD_getChuaEditRkey(){
        ArrayList<DiemDD>arrLocalDiemDD=new ArrayList<>();
        Uri URI_ChuaEditRkey = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/diemdd/chuaEditRkey");
        Cursor cursor = mContentResolver.query(URI_ChuaEditRkey, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    DiemDD diemdd = new DiemDD();
                    diemdd.setId(cursor.getInt(0));
                    diemdd.setServerkey(cursor.getInt(1));
                    diemdd.setRkey(cursor.getLong(2));
                    diemdd.setEater(cursor.getLong(3));
                    diemdd.setEatername(cursor.getString(4));
                    diemdd.setChuyenbien(cursor.getString(5));
                    diemdd.setDiemeater(cursor.getInt(6));
                    diemdd.setLydo(cursor.getString(7));
                    diemdd.setChucvu(cursor.getString(8));
                    diemdd.setFeeder(cursor.getLong(9));
                    diemdd.setDiemfeeder(cursor.getInt(10));
                    diemdd.setNgayps(cursor.getString(11));
                    diemdd.setUpdatetime(cursor.getString(12));
                    diemdd.setUsername(cursor.getString(13));
                    arrLocalDiemDD.add(diemdd);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalDiemDD;
    }
    private int DiemDD_updateDiemDD(DiemDD diemdd){
        ContentValues values = new ContentValues();
        values.put("serverkey", Integer.valueOf(diemdd.getServerkey()));
        values.put("rkey", diemdd.getRkey());
        values.put("eater", diemdd.getEater());
        values.put("eatername", diemdd.getEatername());
        values.put("chuyenbien", diemdd.getChuyenbien());
        values.put("diemeater", Integer.valueOf(diemdd.getDiemeater()));
        values.put("lydo", diemdd.getLydo());
        values.put("chucvu", diemdd.getChucvu());
        values.put("feeder", diemdd.getFeeder());
        values.put("diemfeeder", Integer.valueOf(diemdd.getDiemfeeder()));
        values.put("ngayps", diemdd.getNgayps());
        values.put("updatetime",diemdd.getUpdatetime());
        values.put("username",diemdd.getUsername());
        int u= 0;
        u = mContentResolver.update(StubProvider.CONTENT_URI_DIEMDD, values,"id=?", new String[]{ String.valueOf(diemdd.getId())});
        return u;
    }
    private boolean DiemDD_Exits(int serverKey){
        boolean r=false;
        Uri SYNCTABLE_URI_ID = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/diemdd/"+serverKey);
        Cursor cursor = null;
        cursor = mContentResolver.query(SYNCTABLE_URI_ID, null, null, null, null);
        if (cursor.moveToFirst()){
            r=true;
        }
        if (cursor!=null ){cursor.close();}
        return r;
    }
    //******************************************DMHaiSan***********************************
    private Uri DMHaiSan_addDMHaiSan(DMHaiSan dmhaisan){
        ContentValues values = new ContentValues();
        values.put("serverkey", Integer.valueOf(dmhaisan.getServerkey()));
        values.put("rkey", dmhaisan.getRkey());
        values.put("tenhs", dmhaisan.getTenhs());
        values.put("phanloai", dmhaisan.getPhanloai());
        values.put("dongia", dmhaisan.getDongia());
        values.put("ngayps", dmhaisan.getNgayps());
        values.put("notes", dmhaisan.getNotes());
        values.put("updatetime", dmhaisan.getUpdatetime());
        values.put("username",dmhaisan.getUsername());
        Uri uri = null;
        uri = mContentResolver.insert(StubProvider.CONTENT_URI_DMHAISAN, values);
        return uri;
    }
    private int DMHaiSan_deleteDMHaiSan(int ID){
        return mContentResolver.delete(StubProvider.CONTENT_URI_DMHAISAN, "id=?", new String[]{ String.valueOf(ID)});
    }
    private ArrayList<DMHaiSan>DMHaiSan_getAllDMHaiSan(){
        ArrayList<DMHaiSan>arrLocalDMHaiSan=new ArrayList<>();
        Cursor cursor = mContentResolver.query(StubProvider.CONTENT_URI_DMHAISAN, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    DMHaiSan dmhaisan = new DMHaiSan();
                    dmhaisan.setId(cursor.getInt(0));
                    dmhaisan.setServerkey(cursor.getInt(1));
                    dmhaisan.setRkey(cursor.getLong(2));
                    dmhaisan.setTenhs(cursor.getString(3));
                    dmhaisan.setPhanloai(cursor.getString(4));
                    dmhaisan.setDongia(cursor.getString(5));
                    dmhaisan.setNgayps(cursor.getString(6));
                    dmhaisan.setNotes(cursor.getString(7));
                    dmhaisan.setUpdatetime(cursor.getString(8));
                    dmhaisan.setUsername(cursor.getString(9));
                    arrLocalDMHaiSan.add(dmhaisan);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalDMHaiSan;
    }
    private ArrayList<DMHaiSan>DMHaiSan_getChuaEditRkey(){
        ArrayList<DMHaiSan>arrLocalDMHaiSan=new ArrayList<>();
        Uri URI_ChuaEditRkey = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/dmhaisan/chuaEditRkey");
        Cursor cursor = mContentResolver.query(URI_ChuaEditRkey, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    DMHaiSan dmhaisan = new DMHaiSan();
                    dmhaisan.setId(cursor.getInt(0));
                    dmhaisan.setServerkey(cursor.getInt(1));
                    dmhaisan.setRkey(cursor.getLong(2));
                    dmhaisan.setTenhs(cursor.getString(3));
                    dmhaisan.setPhanloai(cursor.getString(4));
                    dmhaisan.setDongia(cursor.getString(5));
                    dmhaisan.setNgayps(cursor.getString(6));
                    dmhaisan.setNotes(cursor.getString(7));
                    dmhaisan.setUpdatetime(cursor.getString(8));
                    dmhaisan.setUsername(cursor.getString(9));
                    arrLocalDMHaiSan.add(dmhaisan);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalDMHaiSan;
    }
    private int DMHaiSan_updateDMHaiSan(DMHaiSan dmhaisan){
        ContentValues values = new ContentValues();
        values.put("serverkey", Integer.valueOf(dmhaisan.getServerkey()));
        values.put("rkey", dmhaisan.getRkey());
        values.put("tenhs", dmhaisan.getTenhs());
        values.put("phanloai", dmhaisan.getPhanloai());
        values.put("dongia", dmhaisan.getDongia());
        values.put("ngayps", dmhaisan.getNgayps());
        values.put("notes", dmhaisan.getNotes());
        values.put("updatetime", dmhaisan.getUpdatetime());
        values.put("username",dmhaisan.getUsername());
        int u= 0;
        u = mContentResolver.update(StubProvider.CONTENT_URI_DMHAISAN, values,"id=?", new String[]{ String.valueOf(dmhaisan.getId())});
        return u;
    }
    private boolean DMHaiSan_Exits(int serverKey){
        boolean r=false;
        Uri SYNCTABLE_URI_ID = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/dmhaisan/"+serverKey);
        Cursor cursor = null;
        cursor = mContentResolver.query(SYNCTABLE_URI_ID, null, null, null, null);
        if (cursor.moveToFirst()){
            r=true;
        }
        if (cursor!=null ){cursor.close();}
        return r;
    }
    //**********************************BanHSDetail************************************
    private Uri BanHSDetail_addBanHSDetail(BanHSDetail banhsdetail){
        ContentValues values = new ContentValues();
        values.put("serverkey", Integer.valueOf(banhsdetail.getServerkey()));
        values.put("rkey", banhsdetail.getRkey());
        values.put("rkeythu", banhsdetail.getRkeythu());
        values.put("rkeythudetail", banhsdetail.getRkeythudetail());
        values.put("tenhs", banhsdetail.getTenhs());
        values.put("soluong", banhsdetail.getSoluong());
        values.put("updatetime", banhsdetail.getUpdatetime());
        Uri uri = null;
        uri = mContentResolver.insert(StubProvider.CONTENT_URI_BANHSDETAIL, values);
        return uri;
    }
    private int BanHSDetail_deleteBanHSDetail(int ID){
        return mContentResolver.delete(StubProvider.CONTENT_URI_BANHSDETAIL, "id=?", new String[]{ String.valueOf(ID)});
    }
    private ArrayList<BanHSDetail>BanHSDetail_getAllBanHSDetail() {
        ArrayList<BanHSDetail>arrLocalBanHSDetail=new ArrayList<>();
        Cursor cursor = mContentResolver.query(StubProvider.CONTENT_URI_BANHSDETAIL, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    BanHSDetail banhsdetail = new BanHSDetail();
                    banhsdetail.setId(cursor.getInt(0));
                    banhsdetail.setServerkey(cursor.getInt(1));
                    banhsdetail.setRkey(cursor.getLong(2));
                    banhsdetail.setRkeythu(cursor.getLong(3));
                    banhsdetail.setRkeythudetail(cursor.getLong(4));
                    banhsdetail.setTenhs(cursor.getString(5));
                    banhsdetail.setSoluong(cursor.getString(6));
                    banhsdetail.setUpdatetime(cursor.getString(7));
                    arrLocalBanHSDetail.add(banhsdetail);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalBanHSDetail;
    }
    private ArrayList<BanHSDetail>BanHSDetail_getChuaEditRkey() {
        ArrayList<BanHSDetail>arrLocalBanHSDetail=new ArrayList<>();
        Uri URI_ChuaEditRkey = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/banhsdetail/chuaEditRkey");
        Cursor cursor = mContentResolver.query(URI_ChuaEditRkey, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    BanHSDetail banhsdetail = new BanHSDetail();
                    banhsdetail.setId(cursor.getInt(0));
                    banhsdetail.setServerkey(cursor.getInt(1));
                    banhsdetail.setRkey(cursor.getLong(2));
                    banhsdetail.setRkeythu(cursor.getLong(3));
                    banhsdetail.setRkeythudetail(cursor.getLong(4));
                    banhsdetail.setTenhs(cursor.getString(5));
                    banhsdetail.setSoluong(cursor.getString(6));
                    banhsdetail.setUpdatetime(cursor.getString(7));
                    arrLocalBanHSDetail.add(banhsdetail);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return  arrLocalBanHSDetail;
    }
    private int BanHSDetail_updateBanHSDetail(BanHSDetail banhsdetail){
        ContentValues values = new ContentValues();
        values.put("serverkey", Integer.valueOf(banhsdetail.getServerkey()));
        values.put("rkey", banhsdetail.getRkey());
        values.put("rkeythu", banhsdetail.getRkeythu());
        values.put("rkeythudetail", banhsdetail.getRkeythudetail());
        values.put("tenhs", banhsdetail.getTenhs());
        values.put("soluong", banhsdetail.getSoluong());
        values.put("updatetime", banhsdetail.getUpdatetime());
        int u= 0;
        u = mContentResolver.update(StubProvider.CONTENT_URI_BANHSDETAIL, values,"id=?", new String[]{ String.valueOf(banhsdetail.getId())});
        return u;
    }
    private boolean BanHSDetail_Exits(int serverKey){
        boolean r=false;
        Uri SYNCTABLE_URI_ID = Uri.parse("content://com.dqpvn.dqpclient.syncAdapter.StubProvider/banhsdetail/"+serverKey);
        Cursor cursor = null;
        cursor = mContentResolver.query(SYNCTABLE_URI_ID, null, null, null, null);
        if (cursor.moveToFirst()){
            r=true;
        }
        if (cursor!=null ){cursor.close();}
        return r;
    }
}

