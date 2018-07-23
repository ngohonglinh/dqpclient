package com.dqpvn.dqpclient.crudmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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


import com.dqpvn.dqpclient.models.Thu;
import com.dqpvn.dqpclient.models.ThuDetail;
import com.dqpvn.dqpclient.models.Ticket;
import com.dqpvn.dqpclient.models.TicketDetail;
import com.dqpvn.dqpclient.models.Users;
import com.dqpvn.dqpclient.models.WantDeleteFromServer;
import com.dqpvn.dqpclient.utils.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.dqpvn.dqpclient.utils.utils.comPare;
import static com.dqpvn.dqpclient.utils.utils.longGet;

/**
 * Created by linh3 on 11/04/2018.
 */

public class crudLocal extends SQLiteOpenHelper {

    private static volatile crudLocal instance;
    private SQLiteDatabase  db;
    private Context context;
    final private String TAG= getClass().getSimpleName();
    final static int DATABASE_VERSION=1;
    final static String DATABASE_NAME="dqpclient.db";



    private crudLocal(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    //Singleton Pattern
    public static crudLocal getInstance(Context c) {
        if (instance == null) {
            synchronized (crudLocal.class) {
                if (instance == null) {
                    instance = new crudLocal(c);
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Provide access to our database.
     */
    public void getDb() {
        db=instance.getWritableDatabase();
    }

    @Override
    public void finalize() throws Throwable {
        Log.d(TAG, "finalize: ----------crudLocal finalize-----------");
        super.finalize();
    }
//    public void close(){
//        if (db!=null && db.isOpen() && !db.isDbLockedByCurrentThread()){
//            db.close();
//            try {
//                finalize();
//            } catch (Throwable throwable) {
//                throwable.printStackTrace();
//            }
//
//        }
//    }


    //*******************************************************Doing with banhsdetail

    public long BanHSDetail_addBanHSDetail(BanHSDetail BanHSDetail) {
        ContentValues values = new ContentValues();
        values.put("serverkey", Integer.valueOf(BanHSDetail.getServerkey()));
        values.put("rkey", BanHSDetail.getRkey());
        values.put("rkeythu", BanHSDetail.getRkeythu());
        values.put("rkeythudetail", BanHSDetail.getRkeythudetail());
        values.put("tenhs", BanHSDetail.getTenhs());
        values.put("soluong", BanHSDetail.getSoluong());
        values.put("updatetime", BanHSDetail.getUpdatetime());
        long i=db.insert("banhsdetail", null, values);
        if (db!=null && db.isOpen()){
            //db.close();
        }
        if (i!=-1){
            Log.d(TAG, "addBanHSDetail Successfuly");
        }
        return i;
    }

    public ArrayList<BanHSDetail> BanHSDetail_getAllBanHSDetail() {
        ArrayList<BanHSDetail> arraylistBanHSDetail = new ArrayList<>();
        String selectQuery = "SELECT * FROM banhsdetail ORDER BY tenhs ASC";
        Cursor cursor = db.rawQuery(selectQuery,null);
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

                arraylistBanHSDetail.add(banhsdetail);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistBanHSDetail;
    }

    public ArrayList<BanHSDetail> BanHSDetail_getAllBanHSDetailofRkeyThuTong(long rkeyThuTong) {
        ArrayList<BanHSDetail> arraylistBanHSDetail = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyThuTong)};
                Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM banhsdetail WHERE rkeythu = ?", params);
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

                    arraylistBanHSDetail.add(banhsdetail);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistBanHSDetail;

        }catch (Exception e){
            Log.e(TAG, "getAllBanHSDetailofIdChiTong: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }


        //Cursor cursor = db.rawQuery("SELECT * FROM BanHSDetail",null);

    }
    public String BanHSDetail_SumSLHaiSanByRkeyThuDetail(long rkeyThuDetail){
        double result=0;
                //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT SUM(soluong) FROM banhsdetail WHERE rkeythudetail = ?", new String[] {String.valueOf(rkeyThuDetail)});
        // nocty
        if (cursor.moveToFirst()) {
            result=cursor.getDouble(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return String.valueOf(result);
    }

    public ArrayList<BanHSDetail> BanHSDetail_getBanHSDetailByRkeyThuDetail(long rkeyThuDetail) {
        ArrayList<BanHSDetail> arraylistBanHSDetail = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyThuDetail)};
                Cursor cursor=null;

        try {
            cursor = db.rawQuery("SELECT * FROM banhsdetail WHERE rkeythudetail = ?", params);
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


                    arraylistBanHSDetail.add(banhsdetail);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistBanHSDetail;

        }catch (Exception e){
            Log.e(TAG, "getBanHSDetailByDoiTac: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public boolean BanHSDetail_ExitsServerKey(int serverkey) {
        String[] params = new String[]{ String.valueOf(serverkey)};
                Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM banhsdetail WHERE serverkey = ?", params);
            if (cursor.moveToFirst()) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            Log.e(TAG, "getBanHSDetailByDoiTac: " + e.toString() );
            return false;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public int BanHSDetail_updateBanHSDetail(BanHSDetail BanHSDetail){
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("serverkey", Integer.valueOf(BanHSDetail.getServerkey()));
        values.put("rkey", BanHSDetail.getRkey());
        values.put("rkeythu", BanHSDetail.getRkeythu());
        values.put("rkeythudetail", BanHSDetail.getRkeythudetail());
        values.put("tenhs", BanHSDetail.getTenhs());
        values.put("soluong", BanHSDetail.getSoluong());
        values.put("updatetime", BanHSDetail.getUpdatetime());
        long i=db.insert("banhsdetail", null, values);
        int u= db.update("banhsdetail", values,"id =?",new String[]{String.valueOf(BanHSDetail.getId())});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return u;
    }
    public int BanHSDetail_deleteBanHSDetail(long rkey){
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
        int d= db.delete("banhsdetail","rkey =?",new String[] {String.valueOf(rkey)});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return d;
    }

    //*******************************************************Doing with chi

    public long Chi_addChi(Chi chi) {
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
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
        values.put("username",chi.getUsername());;
        long lastInsert=-1;
        try{
            lastInsert= db.insert("chi", null, values);
        }catch (Exception e){
            Log.e(TAG, "addChi: " + e.toString() );
        }finally {
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
        if (lastInsert!=-1){
            Log.d(TAG, "addChi Successfuly");
        }
        return lastInsert;
    }

    public String[] Chi_SumGiaTriDoiTac(long rkeyDoiTac){
        String [] result=new String [2];
                //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT SUM(datra) FROM chi WHERE rkeydoitac = ?", new String[] {String.valueOf(rkeyDoiTac)});
        result[0]="0";
        result[1]="0";
        // nocty
        if (cursor.moveToFirst()) {
            result[0]=String.valueOf(cursor.getLong(0));
        }
        //cursor = db.rawQuery("SELECT SUM(giatri) FROM "+ TABLE_NAME + " WHERE (datra = ? OR datra like ?) AND iddoitac = ?", new String[] {"0","",String.valueOf(idDoiTac)});
        cursor = db.rawQuery("SELECT SUM(giatri) FROM chi WHERE rkeydoitac = ?", new String[] {String.valueOf(rkeyDoiTac)});
        //ctyno
        if (cursor.moveToFirst()) {
            result[1]=String.valueOf(cursor.getLong(0));
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return result;
    }

    public String Chi_SumGiaTriChuyenBien(long rkeyChuyenBien){
        long result=0;
                //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT SUM(giatri) FROM chi WHERE rkeychuyenbien = ?", new String[] {String.valueOf(rkeyChuyenBien)});
        // nocty
        if (cursor.moveToFirst()) {
            result=cursor.getLong(0);
        }

        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }

        return String.valueOf(result);
    }

    public String Chi_SumDaChiTicket(long rkeyTicket){
        long result=0;
                //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT SUM(datra) FROM chi WHERE rkeyticket = ?", new String[] {String.valueOf(rkeyTicket)});
        // nocty
        if (cursor.moveToFirst()) {
            result=cursor.getLong(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }

        return String.valueOf(result);
    }

    public ArrayList<Chi> Chi_getAllChi() {
        ArrayList<Chi> arraylistChi = new ArrayList<>();

        String selectQuery = "SELECT * FROM chi";

                Cursor cursor = db.rawQuery(selectQuery,null);
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


                arraylistChi.add(chi);

            } while (cursor.moveToNext());
        }

        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }

        return arraylistChi;
    }
    public boolean Chi_ExitsServerKey(int serverkey) {
        String[] params = new String[]{ String.valueOf(serverkey)};
                Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM chi WHERE serverkey = ?", params);
            if (cursor.moveToFirst()) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            Log.e(TAG, "Chi_ExitsServerKey: " + e.toString() );
            return false;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<Chi> Chi_getChiByRkey(long rkeyChi) {
        ArrayList<Chi> arraylistChi = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyChi)};
                Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM chi WHERE rkey = ?", params);
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


                    arraylistChi.add(chi);

                } while (cursor.moveToNext());
            }
            //db.close();
            return arraylistChi;

        }catch (Exception e){
            Log.e(TAG, "getChiByDoiTac: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public int Chi_getTicketByChi(long rkeyChi) {
        int i=0;
        ArrayList<Chi> arraylistChi = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyChi)};
                Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT rkeyticket FROM chi WHERE rkey = ?", params);
            if (cursor.moveToFirst()) {
                i=cursor.getInt(0);
            }
            //db.close();
            return i;

        }catch (Exception e){
            Log.e(TAG, "getChiByDoiTac: " + e.toString() );
            return 0;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }


        //Cursor cursor = db.rawQuery("SELECT * FROM chi",null);

    }

    public ArrayList<Chi> Chi_getChiByDoiTac(long rkeyDoiTac) {
        ArrayList<Chi> arraylistChi = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyDoiTac)};
        
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM chi WHERE rkeydoitac = ?", params);
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


                    arraylistChi.add(chi);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistChi;

        }catch (Exception e){
            Log.e(TAG, "getChiByDoiTac: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }


        //Cursor cursor = db.rawQuery("SELECT * FROM chi",null);

    }

    public ArrayList<Chi> Chi_getChiByChuyenBien(long rkeyChuyenBien) {
        ArrayList<Chi> arraylistChi = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyChuyenBien)};
                Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM chi WHERE rkeychuyenbien = ? ORDER BY lydo ASC", params);
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


                    arraylistChi.add(chi);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistChi;

        }catch (Exception e){
            Log.e(TAG, "getChiByDoiTac: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }


        //Cursor cursor = db.rawQuery("SELECT * FROM chi",null);

    }

    public ArrayList<Chi> Chi_getChiByTicket(long rkeyTicket) {
        ArrayList<Chi> arraylistChi = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyTicket)};
                Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM chi WHERE rkeyticket = ? ORDER BY datra ASC, rkeychuyenbien ASC, updatetime ASC", params);
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

                    arraylistChi.add(chi);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistChi;

        }catch (Exception e){
            Log.e(TAG, "getChiByDoiTac: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }

        }
    }

    public int Chi_CapNhatChi(long rkeyChi, String giatri, String datra, String updatetime){
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if (!comPare(giatri,"0")){
            contentValues.put("giatri",giatri);
        }
        if (!comPare(datra,"0")){
            contentValues.put("datra",datra);
        }
        contentValues.put("updatetime",updatetime);
        int u= db.update("chi",contentValues,"rkey=?",new String[]{String.valueOf(rkeyChi)});
        //db.close();
        return u;

    }

    public int Chi_updateChi(Chi chi){
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
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
        int u= db.update("chi", values,"id=?",new String[]{String.valueOf(chi.getId())});
        //db.close();
        return u;
    }
    public int Chi_deleteChi(long rkeyChi){
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
        int d=db.delete("chi","rkey=?",new String[] {String.valueOf(rkeyChi)});
        //db.close();
        return d;
    }

    //*******************************************************Doing with chidetail

    public long ChiDetail_addChiDetail(ChiDetail chidetail) {
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
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
        long i=db.insert("chidetail", null, values);
        if (db!=null && db.isOpen()){
            //db.close();
        }
        if (i!=-1){
            Log.d(TAG, "addChiDetail Successfuly");
        }
        return i;
    }

    public String ChiDetail_SumGiaTriChiTong(long rkeyChiTong){
        long result=0;
                //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT SUM(thanhtien) FROM chidetail WHERE rkeychi = ?", new String[] {String.valueOf(rkeyChiTong)});
        if (cursor.moveToFirst()) {
            result=cursor.getLong(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return String.valueOf(result);
    }

    public String ChiDetail_SumGiaTriDoiTac(String TenDoiTac){
        long result=0;
                //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT SUM(thanhtien) FROM chidetail WHERE tendoitac LIKE ?", new String[] {String.valueOf(TenDoiTac)});
        if (cursor.moveToFirst()) {
            result=cursor.getLong(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return String.valueOf(result);
    }

    public String ChiDetail_SumGiaTriChuyenBien(String TenChuyenBien){
        long result=0;
                //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT SUM(thanhtien) FROM chidetail WHERE tenchuyenbien LIKE ?", new String[] {String.valueOf(TenChuyenBien)});
        if (cursor.moveToFirst()) {
            result=cursor.getLong(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return String.valueOf(result);
    }

    public String ChiDetail_SumGiaTriChuyenBienAndChiTong(long rkeyChiTong, String TenChuyenBien){
        long result=0;
                //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT SUM(thanhtien) FROM chidetail WHERE tenchuyenbien LIKE ? AND rkeychi = ?", new String[] {String.valueOf(TenChuyenBien),String.valueOf(rkeyChiTong)});
        if (cursor.moveToFirst()) {
            result=cursor.getLong(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return String.valueOf(result);
    }

    public ArrayList<ChiDetail> ChiDetail_getAllChiDetail() {
        ArrayList<ChiDetail> arraylistChiDetail = new ArrayList<>();

        String selectQuery = "SELECT * FROM chidetail";

                Cursor cursor = db.rawQuery(selectQuery,null);
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

                arraylistChiDetail.add(chidetail);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistChiDetail;
    }

    public boolean ChiDetail_ExitsServerKey(int serverkey) {
        String[] params = new String[]{ String.valueOf(serverkey)};
                Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM chidetail WHERE serverkey = ?", params);
            if (cursor.moveToFirst()) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            Log.e(TAG, "ChiDetail_ExitsServerKey: " + e.toString() );
            return false;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<ChiDetail> ChiDetail_getAllChiDetailofRkeyChiTong(long rkeyChiTong) {
        ArrayList<ChiDetail> arraylistChiDetail = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyChiTong)};
                Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM chidetail WHERE rkeychi = ?", params);
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

                    arraylistChiDetail.add(chidetail);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistChiDetail;

        }catch (Exception e){
            Log.e(TAG, "getAllChiDetailofIdChiTong: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<ChiDetail> ChiDetail_getChiDetailByRkey(long rkeyChiDetail) {
        ArrayList<ChiDetail> arraylistChiDetail = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyChiDetail)};
                Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM chidetail WHERE rkey = ?", params);
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

                    arraylistChiDetail.add(chidetail);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistChiDetail;

        }catch (Exception e){
            Log.e(TAG, "getChiDetailByDoiTac: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<ChiDetail> ChiDetail_getChiDetailByDoiTac(String TenDoiTac) {
        ArrayList<ChiDetail> arraylistChiDetail = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(TenDoiTac)};
                Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM chidetail WHERE tendoitac LIKE ?", params);
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

                    arraylistChiDetail.add(chidetail);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistChiDetail;

        }catch (Exception e){
            Log.e(TAG, "getChiDetailByDoiTac: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }

        //Cursor cursor = db.rawQuery("SELECT * FROM chidetail",null);
    }

    public int ChiDetail_updateChiDetail(ChiDetail chidetail){
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
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
        int u= db.update("chidetail", values,"id=?",new String[]{String.valueOf(chidetail.getId())});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return u;
    }
    public int ChiDetail_deleteChiDetail(long rkey){
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
        int d= db.delete("chidetail","rkey=?",new String[] {String.valueOf(rkey)});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return d;
    }

    //*******************************************************Doing with chuyenbien

    public long ChuyenBien_addChuyenBien(ChuyenBien chuyenBien) {
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
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

        long i= db.insert("chuyenbien", null, values);
        if (db!=null && db.isOpen()){
            //db.close();
        }
        if (i!=-1){
            Log.d(TAG, "addChuyenbien Successfuly");
        }
        return i;
    }

    public String[] ChuyenBien_listChuyenBien (){
        List<String> lstData = new ArrayList<String>();
        String[] params = new String[]{"0"};
                Cursor cursor=null;
        try{
            cursor = db.rawQuery("SELECT * FROM chuyenbien WHERE dachia= ?",params);
            if (cursor.moveToFirst()) {
                do {
                    ChuyenBien chuyenBien = new ChuyenBien();
                    lstData.add(cursor.getString(cursor.getColumnIndex("chuyenbien")));
                } while (cursor.moveToNext());
            }
            //db.close();
            String[] arrData = new String[lstData.size()];
            arrData = lstData.toArray(arrData);
            //Arrays.asList(arrData).contains("28&98")
            return arrData;
        }catch (Exception e){
            Log.e("crudChuyenBien", "Exception: " + e.toString());
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public String[] ChuyenBien_listOnLyWorkingChuyenBien (){
        List<String> lstData = new ArrayList<String>();
        Cursor cursor=null;
        String[] params = new String[]{ "0th%", "0"};
                try{
            cursor = db.rawQuery("SELECT * FROM chuyenbien WHERE chuyenbien NOT LIKE ? AND dachia= ?",params);
            if (cursor.moveToFirst()) {
                do {
                    ChuyenBien chuyenBien = new ChuyenBien();
                    lstData.add(cursor.getString(cursor.getColumnIndex("chuyenbien")));
                } while (cursor.moveToNext());
            }
            //db.close();
            String[] arrData = new String[lstData.size()];
            arrData = lstData.toArray(arrData);
            //Arrays.asList(arrData).contains("28&98")
            return arrData;
        }catch (Exception e){
            Log.e("crudChuyenBien", "Exception: " + e.toString());
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public long ChuyenBien_getRkeyChuyenBien (String tenchuyenbien){
        long i=0;
                String[] params = new String[]{ String.valueOf(tenchuyenbien)};
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor=null;
        try{
            cursor = db.rawQuery("SELECT rkey FROM chuyenbien WHERE chuyenbien LIKE ?", params);
            if (cursor.moveToFirst()) {
                i= cursor.getLong(0);
            }
            //db.close();
        }catch (Exception e){
            Log.e("crudChuyenBien", "Exception: " + e.toString());
            return 0;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
        return i;
    }

    public long ChuyenBien_getRkeyByShipmaster (String username){
        long i=0;
        String[] params = new String[]{ String.valueOf(username), "0"};
                Cursor cursor=null;
        try{
            cursor = db.rawQuery("SELECT rkey FROM chuyenbien WHERE username LIKE ? AND dachia= ?",params);
            if (cursor.moveToFirst()) {
                i= cursor.getLong(0);
            }
            //db.close();
        }catch (Exception e){
            Log.e("crudChuyenBien", "Exception: " + e.toString());
            return 0;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
        return i;
    }

    public String ChuyenBien_getShipMater (long rkeyChuyenBien){
        String s="";
                String[] params = new String[]{ String.valueOf(rkeyChuyenBien)};
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = null;
        try{
            cursor=db.rawQuery("SELECT username FROM chuyenbien WHERE rkey = ?", params);
            if (cursor.moveToFirst()) {
                s= cursor.getString(0);
            }
            //db.close();
        }catch (Exception e){
            Log.e("crudChuyenBien", "Exception: " + e.toString());
            return "";
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
        return s;
    }

    public ArrayList<ChuyenBien> ChuyenBien_getChuyenBienByRkey(long rkeyChuyenBien) {
        ArrayList<ChuyenBien> arraylistChuyenbien = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyChuyenBien)};
                Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM chuyenbien WHERE rkey = ?", params);
            if (cursor.moveToFirst()) {
                do {
                    ChuyenBien chuyenBien = new ChuyenBien();

                    chuyenBien.setId(cursor.getInt(0));
                    chuyenBien.setServerkey(cursor.getInt(1));
                    chuyenBien.setRkey(cursor.getLong(2));
                    chuyenBien.setChuyenbien(cursor.getString(3));
                    chuyenBien.setTentau(cursor.getString(4));
                    chuyenBien.setNgaykhoihanh(cursor.getString(5));
                    chuyenBien.setNgayketchuyen(cursor.getString(6));
                    chuyenBien.setTongthu(cursor.getString(7));
                    chuyenBien.setTongchi(cursor.getString(8));
                    chuyenBien.setDachia(cursor.getInt(9));
                    chuyenBien.setUpdatetime((cursor.getString(10)));
                    chuyenBien.setUsername((cursor.getString(11)));

                    arraylistChuyenbien.add(chuyenBien);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistChuyenbien;

        }catch (Exception e){
            Log.e(TAG, "getChuyenBienbyID: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public String ChuyenBien_getTenChuyenBien (long rkeyChuyenBien){
        String s="";
                String[] params = new String[]{ String.valueOf(rkeyChuyenBien)};
        Cursor cursor=null;
        try{
            //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
            cursor = db.rawQuery("SELECT chuyenbien FROM chuyenbien WHERE rkey = ?", params);
            if (cursor.moveToFirst()) {
                s= cursor.getString(0);
            }
            //db.close();
        }catch (Exception e){
            Log.e("crudChuyenBien", "Exception: " + e.toString());
            return e.toString();
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
        return s;
    }

    public ArrayList<ChuyenBien> ChuyenBien_getAllChuyenBien() {
        ArrayList<ChuyenBien> arraylistChuyenbien = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM chuyenbien ORDER BY dachia ASC, id DESC",null);
        if (cursor.moveToFirst()) {
            do {
                ChuyenBien chuyenBien = new ChuyenBien();

                chuyenBien.setId(cursor.getInt(0));
                chuyenBien.setServerkey(cursor.getInt(1));
                chuyenBien.setRkey(cursor.getLong(2));
                chuyenBien.setChuyenbien(cursor.getString(3));
                chuyenBien.setTentau(cursor.getString(4));
                chuyenBien.setNgaykhoihanh(cursor.getString(5));
                chuyenBien.setNgayketchuyen(cursor.getString(6));
                chuyenBien.setTongthu(cursor.getString(7));
                chuyenBien.setTongchi(cursor.getString(8));
                chuyenBien.setDachia(cursor.getInt(9));
                chuyenBien.setUpdatetime((cursor.getString(10)));
                chuyenBien.setUsername((cursor.getString(11)));

                arraylistChuyenbien.add(chuyenBien);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistChuyenbien;
    }

    public boolean ChuyenBien_ExitsServerKey(int serverkey) {
        String[] params = new String[]{ String.valueOf(serverkey)};
                Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM chuyenbien WHERE serverkey = ?", params);
            if (cursor.moveToFirst()) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            Log.e(TAG, "ChuyenBien_ExitsServerKey: " + e.toString() );
            return false;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<ChuyenBien> ChuyenBien_getChuyenBienByShipMaster(String username) {
        ArrayList<ChuyenBien> arraylistChuyenbien = new ArrayList<>();
        String[] params = new String[]{String.valueOf(username), "0th%", "0"};
                Cursor cursor = db.rawQuery("SELECT * FROM chuyenbien WHERE username LIKE ? AND chuyenbien NOT LIKE ? AND dachia= ?",params);
        if (cursor.moveToFirst()) {
            do {
                ChuyenBien chuyenBien = new ChuyenBien();

                chuyenBien.setId(cursor.getInt(0));
                chuyenBien.setServerkey(cursor.getInt(1));
                chuyenBien.setRkey(cursor.getLong(2));
                chuyenBien.setChuyenbien(cursor.getString(3));
                chuyenBien.setTentau(cursor.getString(4));
                chuyenBien.setNgaykhoihanh(cursor.getString(5));
                chuyenBien.setNgayketchuyen(cursor.getString(6));
                chuyenBien.setTongthu(cursor.getString(7));
                chuyenBien.setTongchi(cursor.getString(8));
                chuyenBien.setDachia(cursor.getInt(9));
                chuyenBien.setUpdatetime((cursor.getString(10)));
                chuyenBien.setUsername((cursor.getString(11)));

                arraylistChuyenbien.add(chuyenBien);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistChuyenbien;
    }
    public ArrayList<ChuyenBien> ChuyenBien_getAllChuyenBienByShipMaster(String username) {
        ArrayList<ChuyenBien> arraylistChuyenbien = new ArrayList<>();
        String[] params = new String[]{String.valueOf(username), "0th%"};
        Cursor cursor = db.rawQuery("SELECT * FROM chuyenbien WHERE username LIKE ? AND chuyenbien NOT LIKE ? ORDER BY id DESC",params);
        if (cursor.moveToFirst()) {
            do {
                ChuyenBien chuyenBien = new ChuyenBien();

                chuyenBien.setId(cursor.getInt(0));
                chuyenBien.setServerkey(cursor.getInt(1));
                chuyenBien.setRkey(cursor.getLong(2));
                chuyenBien.setChuyenbien(cursor.getString(3));
                chuyenBien.setTentau(cursor.getString(4));
                chuyenBien.setNgaykhoihanh(cursor.getString(5));
                chuyenBien.setNgayketchuyen(cursor.getString(6));
                chuyenBien.setTongthu(cursor.getString(7));
                chuyenBien.setTongchi(cursor.getString(8));
                chuyenBien.setDachia(cursor.getInt(9));
                chuyenBien.setUpdatetime((cursor.getString(10)));
                chuyenBien.setUsername((cursor.getString(11)));

                //chi lay chuyenbien dachia gan day nhat cua tt nay
                if (chuyenBien.getDachia()==1){
                    arraylistChuyenbien.add(chuyenBien);
                    break;
                }
            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistChuyenbien;
    }

    public boolean ChuyenBien_isChuyenBienDaChia(String TenChuyenBien) {
        boolean is=false;
        String[] params = new String[]{TenChuyenBien};
        Cursor cursor = db.rawQuery("SELECT dachia FROM chuyenbien WHERE chuyenbien == ?",params);
        if (cursor.moveToFirst()) {
                if (cursor.getInt(0)==1){
                    is=true;
                }
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }

        return is;
    }



    public ArrayList<ChuyenBien> ChuyenBien_getOnlyShowChuyenBien() {
        ArrayList<ChuyenBien> arraylistChuyenbien = new ArrayList<>();
        String[] params = new String[]{ "0th%", "0"};
                Cursor cursor = db.rawQuery("SELECT * FROM chuyenbien WHERE chuyenbien NOT LIKE ? AND dachia= ? ORDER BY id DESC",params);
        if (cursor.moveToFirst()) {
            do {
                ChuyenBien chuyenBien = new ChuyenBien();

                chuyenBien.setId(cursor.getInt(0));
                chuyenBien.setServerkey(cursor.getInt(1));
                chuyenBien.setRkey(cursor.getLong(2));
                chuyenBien.setChuyenbien(cursor.getString(3));
                chuyenBien.setTentau(cursor.getString(4));
                chuyenBien.setNgaykhoihanh(cursor.getString(5));
                chuyenBien.setNgayketchuyen(cursor.getString(6));
                chuyenBien.setTongthu(cursor.getString(7));
                chuyenBien.setTongchi(cursor.getString(8));
                chuyenBien.setDachia(cursor.getInt(9));
                chuyenBien.setUpdatetime((cursor.getString(10)));
                chuyenBien.setUsername((cursor.getString(11)));

                arraylistChuyenbien.add(chuyenBien);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistChuyenbien;
    }

    public ArrayList<ChuyenBien> ChuyenBien_getOnlyWorkingChuyenBien() {
        ArrayList<ChuyenBien> arraylistChuyenbien = new ArrayList<>();
        String[] params = new String[]{"0"};
                Cursor cursor = db.rawQuery("SELECT * FROM chuyenbien WHERE dachia= ?",params);
        if (cursor.moveToFirst()) {
            do {
                ChuyenBien chuyenBien = new ChuyenBien();

                chuyenBien.setId(cursor.getInt(0));
                chuyenBien.setServerkey(cursor.getInt(1));
                chuyenBien.setRkey(cursor.getLong(2));
                chuyenBien.setChuyenbien(cursor.getString(3));
                chuyenBien.setTentau(cursor.getString(4));
                chuyenBien.setNgaykhoihanh(cursor.getString(5));
                chuyenBien.setNgayketchuyen(cursor.getString(6));
                chuyenBien.setTongthu(cursor.getString(7));
                chuyenBien.setTongchi(cursor.getString(8));
                chuyenBien.setDachia(cursor.getInt(9));
                chuyenBien.setUpdatetime((cursor.getString(10)));
                chuyenBien.setUsername((cursor.getString(11)));

                arraylistChuyenbien.add(chuyenBien);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistChuyenbien;
    }

    public int ChuyenBien_updateChuyenBien(ChuyenBien chuyenBien){
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
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
        int u= db.update("chuyenbien", values,"id=?",new String[]{String.valueOf(chuyenBien.getId())});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return u;
    }

    public int ChuyenBien_CapNhatChi(long rkeyChuyenBien, String tongchi, String updatetime){
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("tongchi",tongchi);
        contentValues.put("updatetime",updatetime);
        int u= db.update("chuyenbien",contentValues,"rkey=?",new String[]{String.valueOf(rkeyChuyenBien)});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return u;

    }

    public int ChuyenBien_CapNhatThu(long rkeyChuyenBien, String tongthu, String updatetime){
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("tongthu",tongthu);
        contentValues.put("updatetime",updatetime);
        int u= db.update("chuyenbien",contentValues,"rkey=?",new String[]{String.valueOf(rkeyChuyenBien)});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return u;

    }
    public int ChuyenBien_deleteChuyenbien(long rkeyChuyenBien){
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
        int d= db.delete("chuyenbien","rkey=?",new String[] {String.valueOf(rkeyChuyenBien)});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return d;
    }

    //*******************************************************Doing with debtbook

    public long DebtBook_addDebtBook(DebtBook debtbook) {
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
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
        long i=-1;
        try {
            i = db.insert("debtbook", null, values);
        }catch(Exception e){
            Log.e(TAG, "addDebtBook: "+e.getMessage().toString() );
        }finally {
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return i;
    }


    public ArrayList<DebtBook> DebtBook_getDebtBookByChuyenBien(String chuyenbien) {
        ArrayList<DebtBook> arraylistDebtBook = new ArrayList<>();
        String[] params = new String[]{chuyenbien};
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM debtbook WHERE chuyenbien = ? ORDER BY ten ASC", params);
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


                    arraylistDebtBook.add(debtbook);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistDebtBook;

        }catch (Exception e){
            Log.e(TAG, "getDebtBookbyID: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<DebtBook> DebtBook_getDebtBookByChuyenBienAndUserLuuSo(String chuyenbien, String nguoiluuso) {
        ArrayList<DebtBook> arraylistDebtBook = new ArrayList<>();
        String[] params = new String[]{chuyenbien, nguoiluuso};
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM debtbook WHERE chuyenbien = ? AND username = ? ORDER BY ten ASC", params);
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


                    arraylistDebtBook.add(debtbook);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistDebtBook;

        }catch (Exception e){
            Log.e(TAG, "getDebtBookbyID: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }
    public String DebtBook_SumForChuyenBienAndNguoiLuuSoAndNgayPS(String chuyenbien, String nguoiluuso, String NgayPS){
        long result=0;
        String[] params;
        Cursor cursor=null;
        if (StringUtils.compareIgnoreCase(nguoiluuso,"Tất cả...")==0 && StringUtils.compareIgnoreCase(NgayPS,"Tất cả...")==0){
            params = new String[]{chuyenbien};
            cursor = db.rawQuery("SELECT SUM(sotien) FROM debtbook WHERE chuyenbien = ? ORDER BY ten ASC", params);
        }else if(StringUtils.compareIgnoreCase(nguoiluuso,"Tất cả...")==0 && StringUtils.compareIgnoreCase(NgayPS,"Tất cả...")!=0){
            params = new String[]{chuyenbien, NgayPS};
            cursor = db.rawQuery("SELECT SUM(sotien) FROM debtbook WHERE chuyenbien = ? AND ngayps = ? ORDER BY ten ASC", params);
        }else if (StringUtils.compareIgnoreCase(nguoiluuso,"Tất cả...")!=0 && StringUtils.compareIgnoreCase(NgayPS,"Tất cả...")==0){
            params = new String[]{chuyenbien, nguoiluuso};
            cursor = db.rawQuery("SELECT SUM(sotien) FROM debtbook WHERE chuyenbien = ? AND username = ? ORDER BY ten ASC", params);
        }else{
            params = new String[]{chuyenbien, nguoiluuso, NgayPS};
            cursor = db.rawQuery("SELECT SUM(sotien) FROM debtbook WHERE chuyenbien = ? AND username = ? AND ngayps = ? ORDER BY ten ASC", params);
        }

        if (cursor.moveToFirst()) {
            result=cursor.getLong(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return String.valueOf(result);
    }


    public ArrayList<DebtBook> DebtBook_getDebtBookByChuyenBienAndUserLuuSoAndNgayPS(String chuyenbien, String nguoiluuso, String NgayPS) {
        ArrayList<DebtBook> arraylistDebtBook = new ArrayList<>();
        String[] params;
        Cursor cursor=null;
        if (StringUtils.compareIgnoreCase(nguoiluuso,"Tất cả...")==0 && StringUtils.compareIgnoreCase(NgayPS,"Tất cả...")==0){
            params = new String[]{chuyenbien};
            cursor = db.rawQuery("SELECT * FROM debtbook WHERE chuyenbien = ? ORDER BY ten ASC", params);
        }else if(StringUtils.compareIgnoreCase(nguoiluuso,"Tất cả...")==0 && StringUtils.compareIgnoreCase(NgayPS,"Tất cả...")!=0){
            params = new String[]{chuyenbien, NgayPS};
            cursor = db.rawQuery("SELECT * FROM debtbook WHERE chuyenbien = ? AND ngayps = ? ORDER BY ten ASC", params);
        }else if (StringUtils.compareIgnoreCase(nguoiluuso,"Tất cả...")!=0 && StringUtils.compareIgnoreCase(NgayPS,"Tất cả...")==0){
            params = new String[]{chuyenbien, nguoiluuso};
            cursor = db.rawQuery("SELECT * FROM debtbook WHERE chuyenbien = ? AND username = ? ORDER BY ten ASC", params);
        }else{
            params = new String[]{chuyenbien, nguoiluuso, NgayPS};
            cursor = db.rawQuery("SELECT * FROM debtbook WHERE chuyenbien = ? AND username = ? AND ngayps = ? ORDER BY ten ASC", params);
        }
        try {

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


                    arraylistDebtBook.add(debtbook);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistDebtBook;

        }catch (Exception e){
            Log.e(TAG, "getDebtBookbyID: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<String> DebtBook_getListNguoiDaLuuSoTrongChuyenBien(String chuyenbien) {
        ArrayList<String> arrListUserName = new ArrayList<>();
        String[] params = new String[]{chuyenbien};
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM debtbook WHERE chuyenbien = ? GROUP BY username ORDER BY username ASC ", params);
            if (cursor.moveToFirst()) {
                do {
                    arrListUserName.add(cursor.getString(11));
                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arrListUserName;

        }catch (Exception e){
            Log.e(TAG, "getDebtBookbyID: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<String> DebtBook_getListNgayUngTrongChuyenBien(String chuyenbien) {
        ArrayList<String> arrListNgayUng = new ArrayList<>();
        String[] params = new String[]{chuyenbien};
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT ngayps FROM debtbook WHERE chuyenbien = ? GROUP BY ngayps ORDER BY id ASC", params);
            if (cursor.moveToFirst()) {
                do {
                    arrListNgayUng.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arrListNgayUng;

        }catch (Exception e){
            Log.e(TAG, "getDebtBookbyID: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }
    public ArrayList<String> DebtBook_getListNgayUngTrongChuyenBienTheoNguoiLuuSo(String chuyenbien, String NguoiLuuSo) {
        ArrayList<String> arrListNgayUng = new ArrayList<>();
        String[] params = new String[]{chuyenbien, NguoiLuuSo};
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT ngayps FROM debtbook WHERE chuyenbien = ? AND username = ? GROUP BY ngayps ORDER BY id ASC", params);
            if (cursor.moveToFirst()) {
                do {
                    arrListNgayUng.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arrListNgayUng;

        }catch (Exception e){
            Log.e(TAG, "getDebtBookbyID: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<DebtBook> DebtBook_getDebtBookByAllWorkingChuyenBien(ArrayList<ChuyenBien> arrWorkingChuyenBien) {
        ArrayList<DebtBook> arraylistDebtBook = new ArrayList<>();
        ChuyenBien chuyenBien=new ChuyenBien();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM debtbook ORDER BY ten ASC",null);
            if (cursor.moveToFirst()) {
                do {
                    String strTenChuyenBien=cursor.getString(5);
                    for (int i=0;i<arrWorkingChuyenBien.size();i++){
                        if (comPare(arrWorkingChuyenBien.get(i).getChuyenbien(),strTenChuyenBien)){
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

                            arraylistDebtBook.add(debtbook);
                            break;
                        }
                    }

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistDebtBook;

        }catch (Exception e){
            Log.e(TAG, "getDebtBookbyID: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<DebtBook> DebtBook_getDebtBookByNgayPS(String chuyenbien, String ngayps) {
        ArrayList<DebtBook> arraylistDebtBook = new ArrayList<>();
        String[] params = new String[]{chuyenbien,ngayps};
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM debtbook WHERE chuyenbien = ? AND ngayps = ?", params);
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


                    arraylistDebtBook.add(debtbook);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistDebtBook;

        }catch (Exception e){
            Log.e(TAG, "getDebtBookbyID: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<DebtBook> DebtBook_getDebtBookByChuyenBienAndThuyenVien(String chuyenbien, long rkeyThuyenVien) {
        ArrayList<DebtBook> arraylistDebtBook = new ArrayList<>();
        String[] params = new String[]{chuyenbien,String.valueOf(rkeyThuyenVien)};
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM debtbook WHERE chuyenbien = ? AND rkeythuyenvien = ? ORDER BY id ASC", params);
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



                    arraylistDebtBook.add(debtbook);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistDebtBook;

        }catch (Exception e){
            Log.e(TAG, "getDebtBookbyID: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<DebtBook> DebtBook_getDebtBookByThuyenVien(long rkeyThuyenVien) {
        ArrayList<DebtBook> arraylistDebtBook = new ArrayList<>();
        String[] params = new String[]{String.valueOf(rkeyThuyenVien)};
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM debtbook WHERE rkeythuyenvien = ? ORDER BY id ASC", params);
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



                    arraylistDebtBook.add(debtbook);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistDebtBook;

        }catch (Exception e){
            Log.e(TAG, "getDebtBookbyID: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<DebtBook> DebtBook_getAllDebtBook() {
        ArrayList<DebtBook> arraylistDebtBook = new ArrayList<>();

        String selectQuery = "SELECT * FROM debtbook ORDER BY ten ASC";

        Cursor cursor = db.rawQuery(selectQuery,null);
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


                arraylistDebtBook.add(debtbook);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistDebtBook;
    }

    public boolean DebtBook_ExitsServerKey(int serverkey) {
        String[] params = new String[]{ String.valueOf(serverkey)};
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM debtbook WHERE serverkey = ?", params);
            if (cursor.moveToFirst()) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            Log.e(TAG, "DebtBook_ExitsServerKey: " + e.toString() );
            return false;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<DebtBook> DebtBook_getDebtBookByTicket(long rkeyTicket) {
        ArrayList<DebtBook> arraylistDebtBook = new ArrayList<>();
        String[] params = new String[]{String.valueOf(rkeyTicket)};
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM debtbook WHERE rkeyticket = ? ORDER BY ten ASC", params);
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



                    arraylistDebtBook.add(debtbook);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistDebtBook;

        }catch (Exception e){
            Log.e(TAG, "getDebtBookbyID: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }


    public int DebtBook_updateDebtBook(DebtBook debtbook){
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
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
        int u =db.update("debtbook",values,"id=?",new String[]{String.valueOf(debtbook.getId())});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return u;
    }

    public String DebtBook_SumDebtBookTicket(long rkeyTicket){
        long result=0;
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT SUM(sotien) FROM debtbook WHERE rkeyticket = ?", new String[] {String.valueOf(rkeyTicket)});
        // nocty
        if (cursor.moveToFirst()) {
            result=cursor.getLong(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return String.valueOf(result);
    }

    public String DebtBook_SumForThuyenVien(long rkeyThuyenVien, String Chuyenbien){
        long result=0;
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT SUM(sotien) FROM debtbook WHERE rkeythuyenvien = ? AND chuyenbien = ?", new String[] {String.valueOf(rkeyThuyenVien), Chuyenbien});
        // nocty
        if (cursor.moveToFirst()) {
            result=cursor.getLong(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return String.valueOf(result);
    }

    public String DebtBook_SumForChuyenBienAndNguoiLuuSo(String chuyenbien, String nguoiluuso){
        long result=0;
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT SUM(sotien) FROM debtbook WHERE chuyenbien = ? AND username = ?", new String[] {chuyenbien, nguoiluuso});
        // nocty
        if (cursor.moveToFirst()) {
            result=cursor.getLong(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return String.valueOf(result);
    }

    public String DebtBook_SumForChuyenBien(String chuyenbien){
        long result=0;
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT SUM(sotien) FROM debtbook WHERE chuyenbien = ?", new String[] {chuyenbien});
        // nocty
        if (cursor.moveToFirst()) {
            result=cursor.getLong(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return String.valueOf(result);
    }

    public int DebtBook_deleteDebtBook(long rkey){
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
        int d= db.delete("debtbook","rkey=?",new String[] {String.valueOf(rkey)});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return d;
    }

    //*******************************************************Doing with diemdd

    public long DiemDD_addDiemDD(DiemDD diemdd) {
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
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
        long lastInsert=-1;
        try{
            lastInsert= db.insert("diemdd", null, values);
        }catch (Exception e){
            Log.e(TAG, "addDiemDD: " + e.toString() );
        }finally {
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
        if (lastInsert!=-1){
            Log.d(TAG, "addDiemDD Successfuly");
        }

        return lastInsert;
    }

    public int DiemDD_SumDiemEater(long Eater){
        int result=0;
                //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT SUM(diemeater) FROM diemdd WHERE eater = ? AND chucvu <> ?", new String[] {String.valueOf(Eater), "Nhân lực"});
        // nocty
        if (cursor.moveToFirst()) {
            result=cursor.getInt(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return result;
    }
    public int DiemDD_SumDiemEaterTV(long Eater){
        int result=0;
                //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT SUM(diemeater) FROM diemdd WHERE eater = ? AND chucvu = ?", new String[] {String.valueOf(Eater), "Nhân lực"});
        // nocty
        if (cursor.moveToFirst()) {
            result=cursor.getInt(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return result;
    }
    public int DiemDD_SumDiemFeeder(long Feeder){
        int result=0;
                //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT SUM(diemfeeder) FROM diemdd WHERE feeder = ?", new String[] {String.valueOf(Feeder)});
        // nocty
        if (cursor.moveToFirst()) {
            result=cursor.getInt(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return result;
    }

    public ArrayList<DiemDD> DiemDD_getAllDiemDD() {
        ArrayList<DiemDD> arraylistDiemDD = new ArrayList<>();

        String selectQuery = "SELECT * FROM diemdd";

                Cursor cursor = db.rawQuery(selectQuery,null);
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


                arraylistDiemDD.add(diemdd);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistDiemDD;
    }

    public boolean DiemDD_ExitsServerKey(int serverkey) {
        String[] params = new String[]{ String.valueOf(serverkey)};
                Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM diemdd WHERE serverkey = ?", params);
            if (cursor.moveToFirst()) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            Log.e(TAG, "DiemDD_ExitsServerKey: " + e.toString() );
            return false;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public DiemDD DiemDD_getDiemDDByRkey(long rkeyDiemDD) {
        String[] params = new String[]{ String.valueOf(rkeyDiemDD)};
                Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM diemdd WHERE rkey = ?", params);
            DiemDD diemdd = new DiemDD();
            if (cursor.moveToFirst()) {

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

            }
            //db.close();
            return diemdd;

        }catch (Exception e){
            Log.e(TAG, "getDiemDDByRkey: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<DiemDD> DiemDD_getDiemDDByUserName(String UserName) {
        ArrayList<DiemDD> arraylistDiemDD = new ArrayList<>();
        String[] params = new String[]{UserName};
                Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM diemdd WHERE username = ?", params);
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


                    arraylistDiemDD.add(diemdd);

                } while (cursor.moveToNext());
            }
            //db.close();
            return arraylistDiemDD;

        }catch (Exception e){
            Log.e(TAG, "getDiemDDByDoiTac: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }


    public int DiemDD_updateDiemDD(DiemDD diemdd){
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
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
        int u= db.update("diemdd", values,"id=?",new String[]{String.valueOf(diemdd.getId())});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return u;
    }
    public int DiemDD_deleteDiemDD(long rkeyDiemDD){
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
        int d= db.delete("diemdd","rkey=?",new String[] {String.valueOf(rkeyDiemDD)});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return d;
    }

    //*******************************************************Doing with dmhaisan

    public long DMHaiSan_addDMHaiSan(DMHaiSan DMHaiSan) {
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("serverkey", Integer.valueOf(DMHaiSan.getServerkey()));
        values.put("rkey", DMHaiSan.getRkey());
        values.put("tenhs", DMHaiSan.getTenhs());
        values.put("phanloai", DMHaiSan.getPhanloai());
        values.put("dongia", DMHaiSan.getDongia());
        values.put("ngayps", DMHaiSan.getNgayps());
        values.put("notes", DMHaiSan.getNotes());
        values.put("updatetime", DMHaiSan.getUpdatetime());
        values.put("username",DMHaiSan.getUsername());
        long i=db.insert("dmhaisan", null, values);
        if (db!=null && db.isOpen()){
            //db.close();
        }
        if (i!=-1){
            Log.d(TAG, "addDMHaiSan Successfuly");
        }
        return i;
    }

    public ArrayList<DMHaiSan> DMHaiSan_getAllDMHaiSan() {
        ArrayList<DMHaiSan> arraylistDMHaiSan = new ArrayList<>();

        String selectQuery = "SELECT * FROM dmhaisan ORDER BY phanloai DESC, tenhs ASC";

                Cursor cursor = db.rawQuery(selectQuery,null);
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

                arraylistDMHaiSan.add(dmhaisan);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistDMHaiSan;
    }

    public boolean DMHaiSan_ExitsServerKey(int serverkey) {
        String[] params = new String[]{ String.valueOf(serverkey)};
                Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM dmhaisan WHERE serverkey = ?", params);
            if (cursor.moveToFirst()) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            Log.e(TAG, "DMHaiSan_ExitsServerKey: " + e.toString() );
            return false;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<DMHaiSan> DMHaiSan_getAllDMHaiSanofRkeyThuTong(long rkeyThuTong) {
        ArrayList<DMHaiSan> arraylistDMHaiSan = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyThuTong)};
        
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM dmhaisan WHERE rkeythu = ?", params);

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


                    arraylistDMHaiSan.add(dmhaisan);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistDMHaiSan;

        }catch (Exception e){
            Log.e(TAG, "getAllDMHaiSanofIdChiTong: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<DMHaiSan> DMHaiSan_getDMHaiSanByRkey(long rkeyDMHaiSan) {
        ArrayList<DMHaiSan> arraylistDMHaiSan = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyDMHaiSan)};
                Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM dmhaisan WHERE rkey = ?", params);
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


                    arraylistDMHaiSan.add(dmhaisan);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistDMHaiSan;

        }catch (Exception e){
            Log.e(TAG, "getDMHaiSanByDoiTac: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }


        //Cursor cursor = db.rawQuery("SELECT * FROM DMHaiSan",null);

    }

    public ArrayList<String> DMHaiSan_listHaiSan (){
        ArrayList<String> lstData = new ArrayList<String>();
        String selectQuery = "SELECT * FROM dmhaisan ORDER BY tenhs ASC";
                Cursor cursor=null;
        try{
            cursor = db.rawQuery(selectQuery,null);
            if (cursor.moveToFirst()) {
                do {
                    lstData.add(cursor.getString(cursor.getColumnIndex("tenhs")));
                } while (cursor.moveToNext());
            }
            //db.close();
            return lstData;
        }catch (Exception e){
            Log.e("crudDMHaiSan", "Exception: " + e.toString());
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<String> DMHaiSan_listHaiSanByPhanLoai (String PhanLoai){
        ArrayList<String> lstData = new ArrayList<String>();
                Cursor cursor=null;
        try{
            cursor = db.rawQuery("SELECT tenhs FROM dmhaisan WHERE phanloai = ? ORDER BY tenhs ASC",new String[]{PhanLoai});
            if (cursor.moveToFirst()) {
                do {
                    lstData.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            //db.close();
            return lstData;
        }catch (Exception e){
            Log.e("crudDMHaiSan", "Exception: " + e.toString());
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public DMHaiSan DMHaiSan_getDMHaiSanByTen(String TenSanPham) {
        ArrayList<DMHaiSan> arraylistDMHaiSan = new ArrayList<>();
                Cursor cursor = db.rawQuery("SELECT * FROM dmhaisan WHERE tenhs LIKE ?",new String[]{String.valueOf(TenSanPham)});
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

                arraylistDMHaiSan.add(dmhaisan);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        if (arraylistDMHaiSan.size()>0){
            return arraylistDMHaiSan.get(0);
        }else{
            return null;
        }

    }

    public String DMHaiSan_getDgiaHaiSanByTen(String TenSanPham) {
        String dgia="0";
                Cursor cursor = db.rawQuery("SELECT dongia FROM dmhaisan WHERE tenhs LIKE ?",new String[]{String.valueOf(TenSanPham)});
        if (cursor.moveToFirst()) {
            dgia=cursor.getString(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return dgia;
    }

    public long DMHaiSan_getRkeyByTenhs(String TenSanPham) {
        long rkey=0;
                Cursor cursor = db.rawQuery("SELECT rkey FROM dmhaisan WHERE tenhs LIKE ?",new String[]{String.valueOf(TenSanPham)});
        if (cursor.moveToFirst()) {
            rkey=cursor.getLong(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return rkey;
    }

    public int DMHaiSan_updateDMHaiSan(DMHaiSan DMHaiSan){
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("serverkey", Integer.valueOf(DMHaiSan.getServerkey()));
        values.put("rkey", DMHaiSan.getRkey());
        values.put("tenhs", DMHaiSan.getTenhs());
        values.put("phanloai", DMHaiSan.getPhanloai());
        values.put("dongia", DMHaiSan.getDongia());
        values.put("ngayps", DMHaiSan.getNgayps());
        values.put("notes", DMHaiSan.getNotes());
        values.put("updatetime", DMHaiSan.getUpdatetime());
        values.put("username",DMHaiSan.getUsername());
        int u= db.update("dmhaisan", values,"id=?",new String[]{String.valueOf(DMHaiSan.getId())});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return u;
    }
    public int DMHaiSan_deleteDMHaiSan(long rkey){
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
        int d= db.delete("dmhaisan","rkey=?",new String[] {String.valueOf(rkey)});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return d;
    }

    //*******************************************************Doing with doitac

    public long DoiTac_addDoiTac(DoiTac doitac) {
        ////SQLiteDatabase db = sInstance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("serverkey", doitac.getServerkey());
        values.put("rkey", doitac.getRkey());
        values.put("tendoitac", doitac.getTendoitac());
        values.put("sodienthoai", doitac.getSodienthoai());
        values.put("diachi", doitac.getDiachi());
        values.put("nocty",doitac.getNocty());
        values.put("ctyno",doitac.getCtyno());
        values.put("updatetime", doitac.getUpdatetime());

        long i=db.insert("doitac", null, values);
        if (db!=null && db.isOpen()){
            //db.close();
        }
        Log.d(TAG, "addDoiTac Successfuly");
        return i;
    }

    public ArrayList<DoiTac> DoiTac_getAllDoiTac() {
        ArrayList<DoiTac> arraylistDoiTac = new ArrayList<>();
        String selectQuery = "SELECT * FROM doitac ORDER BY tendoitac ASC";
                Cursor cursor = db.rawQuery(selectQuery,null);
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

                arraylistDoiTac.add(doitac);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistDoiTac;
    }

    public boolean DoiTac_ExitsServerKey(int serverkey) {
        String[] params = new String[]{ String.valueOf(serverkey)};
        
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM doitac WHERE serverkey = ?", params);
            if (cursor.moveToFirst()) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            Log.e(TAG, "DoiTac_ExitsServerKey: " + e.toString() );
            return false;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<DoiTac> DoiTac_getDoiTacByRkey(long rkeyDoiTac) {
        ArrayList<DoiTac> arraylistDoiTac = new ArrayList<>();
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM doitac WHERE rkey = ?",new String[]{String.valueOf(rkeyDoiTac)});
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

                arraylistDoiTac.add(doitac);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistDoiTac;
    }

    public DoiTac DoiTac_getDoiTacByTen(String TenDoiTac) {
        ArrayList<DoiTac> arraylistDoiTac = new ArrayList<>();
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM doitac WHERE tendoitac = ?",new String[]{String.valueOf(TenDoiTac)});
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

                arraylistDoiTac.add(doitac);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistDoiTac.get(0);
    }

    public String[] DoiTac_listDoiTac (){
        List<String> lstData = new ArrayList<String>();
        String selectQuery = "SELECT * FROM doitac ORDER BY tendoitac ASC";
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try{
            cursor = db.rawQuery(selectQuery,null);
            if (cursor.moveToFirst()) {
                do {
                    DoiTac doiTac = new DoiTac();
                    lstData.add(cursor.getString(cursor.getColumnIndex("tendoitac")));
                } while (cursor.moveToNext());
            }
            //db.close();
            String[] arrData = new String[lstData.size()];
            arrData = lstData.toArray(arrData);
            //Arrays.asList(arrData).contains("28&98")
//            for(String s : arrData)
//                Log.d(this.toString(), s );

            return arrData;
        }catch (Exception e){
            Log.e("crudDoiTac", "Exception: " + e.toString());
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public long DoiTac_getRkeyDoiTac (String tendoitac){
        long i=0;
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        String[] params = new String[]{ String.valueOf(tendoitac)};
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor=null;
        try{
            cursor = db.rawQuery("SELECT rkey FROM doitac WHERE tendoitac LIKE ?", params);
            if (cursor.moveToFirst()) {
                i= cursor.getLong(0);
            }
            //db.close();
        }catch (Exception e){
            Log.e(TAG, "Exception: " + e.toString());
            return 0;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
        return i;
    }

    public String DoiTac_getTenDoiTac (long rkeyDoiTac){
        String s="";
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        String[] params = new String[]{ String.valueOf(rkeyDoiTac)};
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor=null;
        try{
            cursor = db.rawQuery("SELECT tendoitac FROM doitac WHERE rkey = ?", params);
            if (cursor.moveToFirst()) {
                s= cursor.getString(0);
            }
            //db.close();
        }catch (Exception e){
            Log.e("crudDoiTac", "Exception: " + e.toString());
            return "";
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
        return s;
    }

    public int DoiTac_updateDoiTac(DoiTac doitac){
        //SQLiteDatabase db = sInstance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("serverkey", doitac.getServerkey());
        values.put("rkey", doitac.getRkey());
        values.put("tendoitac", doitac.getTendoitac());
        values.put("sodienthoai", doitac.getSodienthoai());
        values.put("diachi", doitac.getDiachi());
        values.put("nocty",doitac.getNocty());
        values.put("ctyno",doitac.getCtyno());
        values.put("updatetime", doitac.getUpdatetime());
        int u= db.update("doitac", values,"id=?",new String[]{String.valueOf(doitac.getId())});

        if (db!=null && db.isOpen()){
            //db.close();
        }
        return u;

    }

    public int DoiTac_CapNhatNo(long rkeyDoiTac, String nocty, String ctyno, String updatetime){
        //SQLiteDatabase db = sInstance.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nocty",nocty);
        contentValues.put("ctyno",ctyno);
        contentValues.put("updatetime",updatetime);
        int u= db.update("doitac",contentValues,"rkey=?",new String[]{String.valueOf(rkeyDoiTac)});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return u;

    }
    public int DoiTac_deleteDoiTac(long rkey){
        //SQLiteDatabase db = sInstance.getWritableDatabase();
        int d= db.delete("doitac","rkey=?",new String[] {String.valueOf(rkey)});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return d;
    }

    //*******************************************************Doing with dstv

    public long DSTV_addDSTV(DSTV dstv) {
        //SQLiteDatabase db = sInstance.getWritableDatabase();
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

        long i=db.insert("dstv", null, values);
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return i;
    }

    public String DSTV_CountByChuyenBien(long rkeyChuyenBien){
        String result="0";
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT COUNT(ten) FROM dstv WHERE rkeychuyenbien = ?", new String[] {String.valueOf(rkeyChuyenBien)});
        // nocty
        if (cursor.moveToFirst()) {
            result=cursor.getString(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return result;
    }


    public ArrayList<DSTV> DSTV_getDSTVbyRkey(long rkeyDSTV) {
        ArrayList<DSTV> arraylistDSTV = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyDSTV)};
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM dstv WHERE rkey = ?", params);
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


                    arraylistDSTV.add(dstv);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistDSTV;

        }catch (Exception e){
            Log.e(TAG, "getDSTVbyID: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public long DSTV_getRkeyThuyenVien (String tenthuyenvien, long rkeyChuyenBien){
        long i=0;
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        String[] params = new String[]{ String.valueOf(tenthuyenvien), String.valueOf(rkeyChuyenBien)};
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor=null;
        try{
            cursor = db.rawQuery("SELECT rkey FROM dstv WHERE ten LIKE ? AND rkeychuyenbien = ?", params);
            if (cursor.moveToFirst()) {
                i= cursor.getLong(0);
            }
            //db.close();
        }catch (Exception e){
            Log.e("crudChuyenBien", "Exception: " + e.toString());
            return 0;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
        return i;
    }

    public String DSTV_getTenThuyenVien (long rkeyThuyenVien){
        String s="";
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        String[] params = new String[]{ String.valueOf(rkeyThuyenVien)};
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor=null;
        try{
            cursor = db.rawQuery("SELECT ten FROM dstv WHERE rkey = ?", params);
            if (cursor.moveToFirst()) {
                s= cursor.getString(0);
            }
            //db.close();
        }catch (Exception e){
            Log.e("crudChuyenBien", "Exception: " + e.toString());
            return "";
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
        return s;
    }
    public double DSTV_SumTongDiem(long rkChuyenBien){
        double result=0;
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT SUM(diem) FROM dstv WHERE rkeychuyenbien = ?", new String[] {String.valueOf(rkChuyenBien)});
        // nocty
        if (cursor.moveToFirst()) {
            result=cursor.getDouble(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return result;
    }

    public ArrayList<DSTV> DSTV_getThuyenVienbyTenVsChuyenBien (String tenthuyenvien, long rkeyChuyenBien){
        ArrayList<DSTV>arraylistDSTV=new ArrayList<>();
        try{
            //SQLiteDatabase db = sInstance.getReadableDatabase();
            String[] params = new String[]{ String.valueOf(tenthuyenvien), String.valueOf(rkeyChuyenBien)};
            //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
            Cursor cursor = db.rawQuery("SELECT * FROM dstv WHERE ten LIKE ? AND rkeychuyenbien = ?", params);
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


                    arraylistDSTV.add(dstv);

                } while (cursor.moveToNext());
            }
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
            return arraylistDSTV;

        }catch (Exception e){
            Log.e("crudChuyenBien", "Exception: " + e.toString());
            return null;
        }
    }

    public String[] DSTV_listThuyenVien (long rkeyChuyenBien){
        List<String> lstData = new ArrayList<String>();
        String[] params = new String[]{String.valueOf(rkeyChuyenBien)};
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try{
            cursor = db.rawQuery("SELECT * FROM dstv WHERE rkeychuyenbien = ? ORDER BY ten ASC", params);
            if (cursor.moveToFirst()) {
                do {
                    DSTV dstv = new DSTV();
                    lstData.add(cursor.getString(cursor.getColumnIndex("ten")));
                } while (cursor.moveToNext());
            }
            //db.close();
            String[] arrData = new String[lstData.size()];
            arrData = lstData.toArray(arrData);
            //Arrays.asList(arrData).contains("28&98")
            return arrData;
        }catch (Exception e){
            Log.e("crudDSTV", "Exception: " + e.toString());
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<DSTV> DSTV_getDSTVByChuyenBien(long rkeyChuyenBien) {
        ArrayList<DSTV> arraylistDSTV = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyChuyenBien)};
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            //Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_NAME + " WHERE idchuyenbien = ?", params);
            cursor = db.rawQuery("SELECT * FROM dstv WHERE rkeychuyenbien = ? ORDER BY ten ASC", params);
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


                    arraylistDSTV.add(dstv);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistDSTV;

        }catch (Exception e){
            Log.e(TAG, "getDSTVbyID: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<DSTV> DSTV_getAllDSTV() {
        ArrayList<DSTV> arraylistDSTV = new ArrayList<>();

        String selectQuery = "SELECT * FROM dstv ORDER BY ten ASC";

        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
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

                arraylistDSTV.add(dstv);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistDSTV;
    }

    public boolean DSTV_ExitsServerKey(int serverkey) {
        String[] params = new String[]{ String.valueOf(serverkey)};
        
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM dstv WHERE serverkey = ?", params);
            if (cursor.moveToFirst()) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            Log.e(TAG, "DSTV_ExitsServerKey: " + e.toString() );
            return false;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public int DSTV_updateDSTV(DSTV dstv){
        //SQLiteDatabase db = sInstance.getWritableDatabase();
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
        int u= db.update("dstv",values,"id=?",new String[]{String.valueOf(dstv.getId())});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return u;
    }


    public int DSTV_CapNhatTien(long rkeyChuyenBien, String TenThuyenVien, String TienChia,
                           String TienMuon, String TienCanCa, String updatetime, String UserName){
        //lay ra gia tri cu truoc
        ArrayList<DSTV>arrThuyenVien=new ArrayList<>();
        arrThuyenVien=DSTV_getThuyenVienbyTenVsChuyenBien(TenThuyenVien,rkeyChuyenBien);
        if (arrThuyenVien.size()==1){
            DSTV dstv=new DSTV();
            dstv=arrThuyenVien.get(0);
            long tienchia, tienmuon, tiencanca, conlai;
            if (TienChia!="0"){
                tienchia=longGet(TienChia);
            }else{
                tienchia=longGet(dstv.getTienchia());
            }
            if (TienMuon!="0"){
                tienmuon=longGet(TienMuon);
            }else{
                tienmuon=longGet(dstv.getTienmuon());
            }
            if (TienCanCa!="0"){
                tiencanca=longGet(TienCanCa);
            }else{
                tiencanca=longGet(dstv.getTiencanca());
            }
            conlai=tienchia-tienmuon-tiencanca;

            //SQLiteDatabase db = sInstance.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("tienchia", tienchia);
            values.put("tienmuon", tienmuon);
            values.put("tiencanca", tiencanca);
            values.put("conlai", conlai);
            values.put("updatetime", updatetime);
            values.put("username", UserName);
            int u= db.update("dstv",values,"ten=? AND rkeychuyenbien=?",new String[]{TenThuyenVien, String.valueOf(rkeyChuyenBien)});
            if (db!=null && db.isOpen()){
                //db.close();
            }
            return u;
        }else{
            return 0;
        }
    }

    public int DSTV_deleteDSTV(long rkey){
        //SQLiteDatabase db = sInstance.getWritableDatabase();
        int d= db.delete("dstv","rkey=?",new String[] {String.valueOf(rkey)});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return d;
    }

    //*******************************************************Doing with imgstore

    public long ImgStore_addImgStore(ImgStore imgstore) {
        //SQLiteDatabase db = sInstance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("serverkey", imgstore.getServerkey());
        values.put("storekey", imgstore.getStorekey());
        values.put("fortable", imgstore.getFortable());
        values.put("imgpath",imgstore.getImgpath());
        values.put("ngayps", imgstore.getNgayps());
        values.put("updatetime", imgstore.getUpdatetime());
        values.put("username", imgstore.getUsername());
        long lastInsert = -1;
        try {
            lastInsert = db.insert("imgstore", null, values);
        } catch (Exception e) {
            Log.e(TAG, "addImgStore: " + e.toString());
        }finally {
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
        if (lastInsert != -1) {
            Log.d(TAG, "addImgStore Successfuly");
        }

        return lastInsert;
    }

    public ArrayList<ImgStore> ImgStore_getAllImgStore() {
        ArrayList<ImgStore> arraylistImgStore = new ArrayList<>();

        String selectQuery = "SELECT * FROM imgstore";

        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
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

                arraylistImgStore.add(imgstore);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistImgStore;
    }

    public boolean ImgStore_ExitsServerKey(int serverkey) {
        String[] params = new String[]{ String.valueOf(serverkey)};
        
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM imgstore WHERE serverkey = ?", params);
            if (cursor.moveToFirst()) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            Log.e(TAG, "ImgStore_ExitsServerKey: " + e.toString() );
            return false;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<ImgStore> ImgStore_getImgStoreByForTableAndStoreKey(String ForTable, long StoteKey) {
        ArrayList<ImgStore> arraylistImgStore = new ArrayList<>();
        String[] params = new String[]{ForTable, String.valueOf(StoteKey)};
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM imgstore WHERE fortable = ? AND storekey = ?", params);
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

                    arraylistImgStore.add(imgstore);

                } while (cursor.moveToNext());
            }
            return arraylistImgStore;

        } catch (Exception e) {
            Log.e(TAG, "error: " + e.toString());
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }


        //Cursor cursor = db.rawQuery("SELECT * FROM imgstore",null);

    }

    public long ImgStore_getStoreKeyByServerKey(int ServerKey) {
        long skey=-1;
        String[] params = new String[]{String.valueOf(ServerKey)};
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT storekey FROM imgstore WHERE serverkey = ?", params);
            if (cursor.moveToFirst()) {
                skey=cursor.getLong(0);
            }
            cursor.close();
            //db.close();
            return skey;

        } catch (Exception e) {
            Log.e(TAG, "error: " + e.toString());
            return skey;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }


    public ImgStore ImgStore_getImgStoreById(int idImgStore) {
        String[] params = new String[]{String.valueOf(idImgStore)};
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        ImgStore imgstore = new ImgStore();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM imgstore WHERE id = ?", params);
            if (cursor.moveToFirst()) {

                imgstore.setId(cursor.getInt(0));
                imgstore.setServerkey(cursor.getInt(1));
                imgstore.setStorekey(cursor.getLong(2));
                imgstore.setFortable(cursor.getString(3));
                imgstore.setImgpath(cursor.getString(4));
                imgstore.setNgayps(cursor.getString(5));
                imgstore.setUpdatetime(cursor.getString(6));
                imgstore.setUsername(cursor.getString(7));

                return imgstore;
            }else{
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "error: " + e.toString());
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }


    public int ImgStore_updateImgStore(ImgStore imgstore) {
        //SQLiteDatabase db = sInstance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("serverkey", imgstore.getServerkey());
        values.put("storekey", imgstore.getStorekey());
        values.put("fortable", imgstore.getFortable());
        values.put("imgpath",imgstore.getImgpath());
        values.put("ngayps", imgstore.getNgayps());
        values.put("updatetime", imgstore.getUpdatetime());
        values.put("username", imgstore.getUsername());
        int u= db.update("imgstore", values, "id=?", new String[]{String.valueOf(imgstore.getId())});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return u;
    }

    public void ImgStore_deleteOldImg(String imgPath){
        File file = new File(imgPath);
        if (file.exists()) {
            file.delete();
        }
    }


    public int ImgStore_deleteImgStore(int idImgStore) {
        ImgStore imgStore=ImgStore_getImgStoreById(idImgStore);
        String s=imgStore.getImgpath()+"";
        if (s.length()>4){
            ImgStore_deleteOldImg(s);
        }
        //SQLiteDatabase db = sInstance.getWritableDatabase();
        int d= db.delete("imgstore", "id=?", new String[]{String.valueOf(idImgStore)});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return d;
    }

    public int ImgStore_deleteImgStoreByForTableAndStoreKey(String ForTable, long StoreKey) {
        ArrayList<ImgStore> arrImgStore;
        arrImgStore = ImgStore_getImgStoreByForTableAndStoreKey(ForTable, StoreKey);
        int d=-1;
        for (int i = 0; i < arrImgStore.size(); i++) {
            String s = arrImgStore.get(i).getImgpath();
            if (s != null) {
                ImgStore_deleteOldImg(s);
                d=ImgStore_deleteImgStore(arrImgStore.get(i).getId());
            }
        }
        return d;
    }

    //*******************************************************Doing with khachhang

    public long KhachHang_addKhachHang(KhachHang khachhang) {
        //SQLiteDatabase db = sInstance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("serverkey", khachhang.getServerkey());
        values.put("rkey", khachhang.getRkey());
        values.put("tenkhach", khachhang.getTenkhach());
        values.put("sodienthoai", khachhang.getSodienthoai());
        values.put("diachi", khachhang.getDiachi());
        values.put("nocty",khachhang.getNocty());
        values.put("ctyno",khachhang.getCtyno());
        values.put("updatetime", khachhang.getUpdatetime());

        long i= db.insert("khachhang", null, values);
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return i;
    }

    public ArrayList<KhachHang> KhachHang_getAllKhachHang() {
        ArrayList<KhachHang> arraylistKhachHang = new ArrayList<>();
        String selectQuery = "SELECT * FROM khachhang ORDER BY tenkhach ASC";
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
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

                arraylistKhachHang.add(khachhang);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistKhachHang;
    }

    public boolean KhachHang_ExitsServerKey(int serverkey) {
        String[] params = new String[]{ String.valueOf(serverkey)};
        
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM khachhang WHERE serverkey = ?", params);
            if (cursor.moveToFirst()) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            Log.e(TAG, "KhachHang_ExitsServerKey: " + e.toString() );
            return false;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public KhachHang KhachHang_getKhachHangByTen(String TenKhachHang) {
        ArrayList<KhachHang> arraylistKhachHang = new ArrayList<>();
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM khachhang WHERE tenkhach = ?",new String[]{String.valueOf(TenKhachHang)});
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

                arraylistKhachHang.add(khachhang);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistKhachHang.get(0);
    }

    public ArrayList<KhachHang> KhachHang_getKhachHangByRkey(long rkeyKhachHang) {
        ArrayList<KhachHang> arraylistKhachHang = new ArrayList<>();
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM khachhang WHERE rkey = ?",new String[]{String.valueOf(rkeyKhachHang)});
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

                arraylistKhachHang.add(khachhang);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistKhachHang;
    }

    public String[] KhachHang_listKhachHang (){
        List<String> lstData = new ArrayList<String>();
        String selectQuery = "SELECT * FROM khachhang ORDER BY tenkhach ASC";
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try{
            cursor = db.rawQuery(selectQuery,null);
            if (cursor.moveToFirst()) {
                do {
                    KhachHang doiTac = new KhachHang();
                    lstData.add(cursor.getString(cursor.getColumnIndex("tenkhach")));
                } while (cursor.moveToNext());
            }
            //db.close();
            String[] arrData = new String[lstData.size()];
            arrData = lstData.toArray(arrData);
            //Arrays.asList(arrData).contains("28&98")
//            for(String s : arrData)
//                Log.d(this.toString(), s );

            return arrData;
        }catch (Exception e){
            Log.e("crudKhachHang", "Exception: " + e.toString());
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public long KhachHang_getRkeyKhachHang (String tenkhach){
        long i=0;
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        String[] params = new String[]{ String.valueOf(tenkhach)};
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor=null;
        try{
            cursor = db.rawQuery("SELECT rkey FROM khachhang WHERE tenkhach LIKE ?", params);
            if (cursor.moveToFirst()) {
                i= cursor.getLong(0);
            }
            //db.close();
        }catch (Exception e){
            Log.e(TAG, "Exception: " + e.toString());
            return 0;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
        return i;
    }

    public String KhachHang_getTenKhachHang (long rkeyKhachHang){
        String s="";
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        String[] params = new String[]{ String.valueOf(rkeyKhachHang)};
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor=null;
        try{
            cursor = db.rawQuery("SELECT tenkhach FROM khachhang WHERE rkey = ?", params);
            if (cursor.moveToFirst()) {
                s= cursor.getString(0);
            }
            //db.close();
        }catch (Exception e){
            Log.e("crudKhachHang", "Exception: " + e.toString());
            return "";
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
        return s;
    }

    public int KhachHang_updateKhachHang(KhachHang khachhang){
        //SQLiteDatabase db = sInstance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("serverkey", khachhang.getServerkey());
        values.put("rkey", khachhang.getRkey());
        values.put("tenkhach", khachhang.getTenkhach());
        values.put("sodienthoai", khachhang.getSodienthoai());
        values.put("diachi", khachhang.getDiachi());
        values.put("nocty",khachhang.getNocty());
        values.put("ctyno",khachhang.getCtyno());
        values.put("updatetime", khachhang.getUpdatetime());
        int u= db.update("khachhang", values,"id=?",new String[]{String.valueOf(khachhang.getId())});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return u;
    }

    public int KhachHang_CapNhatNo(long rKeyKhachHang, String nocty, String ctyno, String updatetime){
        //SQLiteDatabase db = sInstance.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nocty",nocty);
        contentValues.put("ctyno",ctyno);
        contentValues.put("updatetime",updatetime);
        int u= db.update("khachhang",contentValues,"rkey=?",new String[]{String.valueOf(rKeyKhachHang)});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return u;

    }
    public int KhachHang_deleteKhachHang(long rkey){
        //SQLiteDatabase db = sInstance.getWritableDatabase();
        int d= db.delete("khachhang","rkey=?",new String[] {String.valueOf(rkey)});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return d;
    }

    //*******************************************************Doing with thu

    public long Thu_addThu(Thu thu) {
        //SQLiteDatabase db = sInstance.getWritableDatabase();
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

        long i=db.insert("thu", null, values);
        //db.close();
        Log.d(TAG, "addThu Successfuly");
        return i;
    }

    public String[] Thu_SumGiaTriKhachHang(long rkeyKhachHang){
        String [] result=new String [2];
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT SUM(datra) FROM thu WHERE rkeykhachhang = ?", new String[] {String.valueOf(rkeyKhachHang)});
        result[0]="0";
        result[1]="0";
        // nocty
        if (cursor.moveToFirst()) {
            result[0]=String.valueOf(cursor.getLong(0));
        }
        //cursor = db.rawQuery("SELECT SUM(giatri) FROM "+ TABLE_NAME + " WHERE (datra = ? OR datra like ?) AND idkhachhang = ?", new String[] {"0","",String.valueOf(idKhachHang)});
        cursor = db.rawQuery("SELECT SUM(giatri) FROM thu WHERE rkeykhachhang = ?", new String[] {String.valueOf(rkeyKhachHang)});
        //ctyno
        if (cursor.moveToFirst()) {
            result[1]=String.valueOf(cursor.getLong(0));
        } if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return result;
    }

    public String Thu_SumGiaTriChuyenBien(long rkeyChuyenBien){
        long result=0;
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT SUM(giatri) FROM thu WHERE rkeychuyenbien = ?", new String[] {String.valueOf(rkeyChuyenBien)});
        // nocty
        if (cursor.moveToFirst()) {
            result=cursor.getLong(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return String.valueOf(result);
    }

    public ArrayList<Thu> Thu_getAllThu() {
        ArrayList<Thu> arraylistThu = new ArrayList<>();

        String selectQuery = "SELECT * FROM thu";

        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
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


                arraylistThu.add(thu);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistThu;
    }

    public boolean Thu_ExitsServerKey(int serverkey) {
        String[] params = new String[]{ String.valueOf(serverkey)};
        
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM thu WHERE serverkey = ?", params);
            if (cursor.moveToFirst()) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            Log.e(TAG, "Thu_ExitsServerKey: " + e.toString() );
            return false;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<Thu> Thu_getThuByRkey(long rkeyThu) {
        ArrayList<Thu> arrThu = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyThu)};
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM thu WHERE rkey = ?", params);
            if (cursor.moveToFirst()) {
                Thu thu=new Thu();
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
                arrThu.add(thu);
            }
            cursor.close();
            //db.close();
            return arrThu;

        }catch (Exception e){
            Log.e(TAG, "getThuByKhachHang: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<Thu> Thu_getThuByKhachHang(long rkeyKhachHang) {
        ArrayList<Thu> arraylistThu = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyKhachHang)};
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM thu WHERE rkeykhachhang = ?", params);
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


                    arraylistThu.add(thu);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistThu;

        }catch (Exception e){
            Log.e(TAG, "getThuByKhachHang: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<Thu> Thu_getThuByChuyenBien(long rkeyChuyenBien) {
        ArrayList<Thu> arraylistThu = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyChuyenBien)};
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM thu WHERE rkeychuyenbien = ?", params);
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


                    arraylistThu.add(thu);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistThu;

        }catch (Exception e){
            Log.e(TAG, "getThuByKhachHang: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }


        //Cursor cursor = db.rawQuery("SELECT * FROM thu",null);

    }

    public int Thu_updateThu(Thu thu){
        //SQLiteDatabase db = sInstance.getWritableDatabase();
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
        int u= db.update("thu", values,"id=?",new String[]{String.valueOf(thu.getId())});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return u;
    }
    public int Thu_deleteThu(long rkeyThu){
        //SQLiteDatabase db = sInstance.getWritableDatabase();
        int d= db.delete("thu","rkey=?",new String[] {String.valueOf(rkeyThu)});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return d;
    }

    //*******************************************************Doing with thudetail

    public long ThuDetail_addThuDetail(ThuDetail ThuDetail) {
        //SQLiteDatabase db = sInstance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("serverkey", Integer.valueOf(ThuDetail.getServerkey()));
        values.put("rkey", ThuDetail.getRkey());
        values.put("rkeythu", ThuDetail.getRkeythu());
        values.put("tenhs", ThuDetail.getTenhs());
        values.put("rkeyhs", ThuDetail.getRkeyhs());
        values.put("soluong", ThuDetail.getSoluong());
        values.put("dongia", ThuDetail.getDongia());
        values.put("thanhtien", ThuDetail.getThanhtien());
        values.put("updatetime",ThuDetail.getUpdatetime());
        values.put("username",ThuDetail.getUsername());
        long i=db.insert("thudetail", null, values);
        //db.close();
        if (i!=-1){
            Log.d(TAG, "addThuDetail Successfuly");
        }
        return i;
    }

    public ArrayList<ThuDetail> ThuDetail_getAllThuDetail() {
        ArrayList<ThuDetail> arraylistThuDetail = new ArrayList<>();

        String selectQuery = "SELECT * FROM thudetail";

        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
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

                arraylistThuDetail.add(thudetail);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistThuDetail;
    }

    public boolean ThuDetail_ExitsServerKey(int serverkey) {
        String[] params = new String[]{ String.valueOf(serverkey)};
        
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM thudetail WHERE serverkey = ?", params);
            if (cursor.moveToFirst()) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            Log.e(TAG, "ThuDetail_ExitsServerKey: " + e.toString() );
            return false;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<ThuDetail> ThuDetail_getAllThuDetailofRkeyThuTong(long rkeyThuTong) {
        ArrayList<ThuDetail> arraylistThuDetail = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyThuTong)};
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM thudetail WHERE rkeythu = ? ORDER BY tenhs", params);

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

                    arraylistThuDetail.add(thudetail);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistThuDetail;

        }catch (Exception e){
            Log.e(TAG, "getAllThuDetailofIdChiTong: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }


        //Cursor cursor = db.rawQuery("SELECT * FROM ThuDetail",null);

    }

    public String ThuDetail_SumSLHaiSanByRkeyThuTong(long rkeyThu){
        double result=0;
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT SUM(soluong) FROM thudetail WHERE rkeythu = ?", new String[] {String.valueOf(rkeyThu)});
        // nocty
        if (cursor.moveToFirst()) {
            result=cursor.getDouble(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return String.valueOf(result);
    }
    public String ThuDetail_getSoluongByRkey(long rkeyThuDetail){
        double result=0;
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT soluong FROM thudetail WHERE rkey = ?", new String[] {String.valueOf(rkeyThuDetail)});
        // nocty
        if (cursor.moveToFirst()) {
            result=cursor.getDouble(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return String.valueOf(result);
    }

    public String ThuDetail_SumTHANHTIENbyRkeyThuTong(long rkeyThu){
        long result=0;
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor = db.rawQuery("SELECT SUM(thanhtien) FROM thudetail WHERE rkeythu = ?", new String[] {String.valueOf(rkeyThu)});
        // nocty
        if (cursor.moveToFirst()) {
            result=cursor.getLong(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return String.valueOf(result);
    }

    public ThuDetail ThuDetail_getThuDetailByTenhsAndRkeyThuTong(String tenHS, long rkeyThuTong) {
        ThuDetail thudetail = new ThuDetail();
        String[] params = new String[]{tenHS, String.valueOf(rkeyThuTong)};
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM thudetail WHERE tenhs= ? AND rkeythu = ?", params);

            if (cursor.moveToFirst()) {
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
            }
            cursor.close();
            //db.close();
            return thudetail;

        }catch (Exception e){
            Log.e(TAG, "getAllThuDetailofIdChiTong: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<String> ThuDetail_getDSHaiSanbyRkeyTong(long rkeyThuTong) {
        ArrayList<String> arrStr = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyThuTong)};
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT tenhs FROM thudetail WHERE rkeythu = ? GROUP BY tenhs ORDER BY tenhs", params);
            if (cursor.moveToFirst()) {
                do {
                    arrStr.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arrStr;

        }catch (Exception e){
            Log.e(TAG, "getThuDetailByDoiTac: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }

    }


    public ThuDetail ThuDetail_getThuDetailByRkey(long rkeyThuDetail) {
        ThuDetail thudetail = new ThuDetail();
        String[] params = new String[]{ String.valueOf(rkeyThuDetail)};
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM thudetail WHERE rkey = ?", params);
            if (cursor.moveToFirst()) {
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
            }
            cursor.close();
            //db.close();
            return thudetail;

        }catch (Exception e){
            Log.e(TAG, "getThuDetailByDoiTac: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }

    }

    public int ThuDetail_updateThuDetail(ThuDetail ThuDetail){
        //SQLiteDatabase db = sInstance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("serverkey", Integer.valueOf(ThuDetail.getServerkey()));
        values.put("rkey", ThuDetail.getRkey());
        values.put("rkeythu", ThuDetail.getRkeythu());
        values.put("tenhs", ThuDetail.getTenhs());
        values.put("rkeyhs", ThuDetail.getRkeyhs());
        values.put("soluong", ThuDetail.getSoluong());
        values.put("dongia", ThuDetail.getDongia());
        values.put("thanhtien", ThuDetail.getThanhtien());
        values.put("updatetime",ThuDetail.getUpdatetime());
        values.put("username",ThuDetail.getUsername());
        int u= db.update("thudetail", values,"id=?",new String[]{String.valueOf(ThuDetail.getId())});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return u;
    }
    public int ThuDetail_deleteThuDetail(long rkey){
        //SQLiteDatabase db = sInstance.getWritableDatabase();
        int d= db.delete("thudetail","rkey=?",new String[] {String.valueOf(rkey)});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return d;
    }

    //*******************************************************Doing with ticket

    public long Ticket_addTicket(Ticket ticket) {
        //SQLiteDatabase db = sInstance.getWritableDatabase();
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

        long i=db.insert("ticket", null, values);
        //db.close();
        return i;
    }


    public ArrayList<Ticket> Ticket_getTicketByRkey(long rkeyTicket) {
        ArrayList<Ticket> arraylistTicket = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyTicket)};
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM ticket WHERE rkey = ?", params);
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

                    arraylistTicket.add(ticket);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistTicket;

        }catch (Exception e){
            Log.e(TAG, "getTicketbyID: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }



    public ArrayList<Ticket> Ticket_getAllTicket() {
        ArrayList<Ticket> arraylistTicket = new ArrayList<>();

        String selectQuery = "SELECT * FROM ticket ORDER BY finished ASC, username ASC";

        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
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

                arraylistTicket.add(ticket);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistTicket;
    }

    public boolean Ticket_ExitsServerKey(int serverkey) {
        String[] params = new String[]{ String.valueOf(serverkey)};
        
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM ticket WHERE serverkey = ?", params);
            if (cursor.moveToFirst()) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            Log.e(TAG, "Ticket_ExitsServerKey: " + e.toString() );
            return false;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<Ticket> Ticket_getAllNotFinishedTicket() {
        ArrayList<Ticket> arraylistTicket = new ArrayList<>();
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        String[] params = new String[]{"0"};
        Cursor cursor = db.rawQuery("SELECT * FROM ticket WHERE finished= ?", params);
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

                arraylistTicket.add(ticket);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistTicket;
    }

    public ArrayList<Ticket> Ticket_getAllTicketByUserName(String UserName) {
        if (StringUtils.containsIgnoreCase(UserName,"Tất cả")){
            return Ticket_getAllTicket();
        }
        ArrayList<Ticket> arraylistTicket = new ArrayList<>();
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        String[] params = new String[]{UserName};
        Cursor cursor = db.rawQuery("SELECT * FROM ticket WHERE username= ? ORDER BY finished ASC", params);
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

                arraylistTicket.add(ticket);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistTicket;
    }

    public ArrayList<String> Ticket_getListUserOpenTicket() {
        ArrayList<String> arr = new ArrayList<>();
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        String[] params = new String[]{"0"};
        Cursor cursor = db.rawQuery("SELECT * FROM ticket WHERE finished= ?", params);
        if (cursor.moveToFirst()) {
            do {
                arr.add(cursor.getString(11));
            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arr;
    }

    public ArrayList<String> Ticket_getListUser() {
        ArrayList<String> arr = new ArrayList<>();
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM ticket GROUP BY username",null);
        if (cursor.moveToFirst()) {
            do {
                arr.add(cursor.getString(11));
            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arr;
    }

    public ArrayList<Ticket> Ticket_getOpenTicketByUser(String username){
        ArrayList<Ticket> arraylistTicket = new ArrayList<>();
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        String[] params = new String[]{ username.trim(),"0"};
        Cursor cursor = db.rawQuery("SELECT * FROM ticket WHERE username = ? AND finished= ?", params);
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

                arraylistTicket.add(ticket);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistTicket;
    }



    public int Ticket_updateTicket(Ticket ticket){
        //SQLiteDatabase db = sInstance.getWritableDatabase();

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
        int u= db.update("ticket",values,"id=?",new String[]{String.valueOf(ticket.getId())});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return u;
    }

    public int Ticket_CapNhatChi(long rkeyTicket, String tongchi, String updatetime){
        //SQLiteDatabase db = sInstance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("used", tongchi);
        values.put("updatetime",updatetime);
        int u= db.update("ticket",values,"rkey=?",new String[]{String.valueOf(rkeyTicket)});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return u;
    }

public long [] Ticket_getAmountInfo(long rkeyticket){
    long [] resul=new long [2];
    long amount=0, used=0, res=0;
    //SQLiteDatabase db = sInstance.getReadableDatabase();
    String[] params = new String[]{ String.valueOf(rkeyticket)};
    Cursor cursor = db.rawQuery("SELECT amount FROM ticket WHERE rkey = ?", params);
    if (cursor.moveToFirst()) {
        amount=cursor.getLong(0);
    }
    cursor = db.rawQuery("SELECT used FROM ticket WHERE rkey = ?", params);
    if (cursor.moveToFirst()) {
        used=cursor.getLong(0);
    }
    cursor = db.rawQuery("SELECT comeback FROM ticket WHERE rkey = ?", params);
    if (cursor.moveToFirst()) {
        res=cursor.getLong(0);
    }
    if (cursor!=null && !cursor.isClosed()){
        cursor.close();
    }
    if (db!=null && db.isOpen()){
        //db.close();
    }
    resul[0]=amount;
    resul[1]=used+res;
    return resul;
}

    public String [] Ticket_getUserAndOpenDateByRkey(long rKey){
        String[] params = new String[]{ String.valueOf(rKey)};
        String [] reSult = new String[2];
        String strUserName="";
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT username, opendate FROM ticket WHERE rkey = ?", params);
        if (cursor.moveToFirst()) {
            reSult[0]=cursor.getString(0);
            reSult [1]=cursor.getString(1);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return reSult;
    }

    public int Ticket_deleteTicket(long rkey){
        //SQLiteDatabase db = sInstance.getWritableDatabase();
        int d= db.delete("ticket","rkey=?",new String[] {String.valueOf(rkey)});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return d;
    }

    //*******************************************************Doing with ticketdetail

    public long TicketDetail_addTicketDetail(TicketDetail ticketd) {
        //SQLiteDatabase db = sInstance.getWritableDatabase();
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

        long i= db.insert("ticketdetail", null, values);
        //db.close();
        return i;
    }


    public ArrayList<TicketDetail> TicketDetail_getTicketDetailByParentRkey(long rkeyTicket) {
        ArrayList<TicketDetail> arraylistTicketDetail = new ArrayList<>();
        String[] params = new String[]{ String.valueOf(rkeyTicket)};
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM ticketdetail WHERE rkeyticket = ? ORDER BY id ASC", params);
            if (cursor.moveToFirst()) {
                do {
                    TicketDetail ticketd = new TicketDetail();

                    ticketd.setId(cursor.getInt(0));
                    ticketd.setServerkey(cursor.getInt(1));
                    ticketd.setRkey(cursor.getLong(2));
                    ticketd.setRkeyticket(cursor.getLong(3));
                    ticketd.setForuser((cursor.getString(4)));
                    ticketd.setAmount(cursor.getString(5));
                    ticketd.setNgayps(cursor.getString(6));
                    ticketd.setNotes(cursor.getString(7));
                    ticketd.setUpdatetime(cursor.getString(8));
                    ticketd.setUsername(cursor.getString(9));

                    arraylistTicketDetail.add(ticketd);

                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
            return arraylistTicketDetail;

        }catch (Exception e){
            Log.e(TAG, "getTicketDetailbyParentRkey: " + e.toString() );
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<TicketDetail> TicketDetail_getAllTicketDetail() {
        ArrayList<TicketDetail> arraylistTicketDetail = new ArrayList<>();

        String selectQuery = "SELECT * FROM ticketdetail";

        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()) {
            do {
                TicketDetail ticketd = new TicketDetail();

                ticketd.setId(cursor.getInt(0));
                ticketd.setServerkey(cursor.getInt(1));
                ticketd.setRkey(cursor.getLong(2));
                ticketd.setRkeyticket(cursor.getLong(3));
                ticketd.setForuser((cursor.getString(4)));
                ticketd.setAmount(cursor.getString(5));
                ticketd.setNgayps(cursor.getString(6));
                ticketd.setNotes(cursor.getString(7));
                ticketd.setUpdatetime(cursor.getString(8));
                ticketd.setUsername(cursor.getString(9));

                arraylistTicketDetail.add(ticketd);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistTicketDetail;
    }

    public boolean TicketDetail_ExitsServerKey(int serverkey) {
        String[] params = new String[]{ String.valueOf(serverkey)};
        
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM ticketdetail WHERE serverkey = ?", params);
            if (cursor.moveToFirst()) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            Log.e(TAG, "TicketDetail_ExitsServerKey: " + e.toString() );
            return false;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }


    public int TicketDetail_updateTicketDetail(TicketDetail ticketd){
        //SQLiteDatabase db = sInstance.getWritableDatabase();

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
        int u= db.update("ticketdetail",values,"id=?",new String[]{String.valueOf(ticketd.getId())});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return u;
    }

    public String TicketDetail_getSumAmountByParentRkey(long RkeyTicket){
        long amount=0;
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        String[] params = new String[]{ String.valueOf(RkeyTicket)};
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM ticketdetail WHERE rkeyticket = ?", params);
        if (cursor.moveToFirst()) {
            amount=cursor.getLong(0);
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return String.valueOf(amount);
    }

    public int TicketDetail_deleteTicketDetail(long rkey){
        //SQLiteDatabase db = sInstance.getWritableDatabase();
        int d= db.delete("ticketdetail","rkey=?",new String[] {String.valueOf(rkey)});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return d;
    }

    //************************************************************** Doing with users

    public long Users_addUsers(Users users) {
        //SQLiteDatabase db = sInstance.getWritableDatabase();
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


        long i=db.insert("users", null, values);
        //db.close();
        return i;
    }

    public ArrayList<Users> Users_getAllUsers() {
        ArrayList<Users> arraylistUsers = new ArrayList<>();
        String selectQuery = "SELECT * FROM users ORDER BY honourname ASC";
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
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

                arraylistUsers.add(users);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistUsers;
    }

    public boolean Users_ExitsServerKey(int serverkey) {
        String[] params = new String[]{ String.valueOf(serverkey)};
        
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("SELECT * FROM users WHERE serverkey = ?", params);
            if (cursor.moveToFirst()) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            Log.e(TAG, "Users_ExitsServerKey: " + e.toString() );
            return false;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public int Users_updateUsers(Users users){
        //SQLiteDatabase db = sInstance.getWritableDatabase();
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
        int u= db.update("users", values,"id=?",new String[]{String.valueOf(users.getId())});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return u;

    }

    public ArrayList<Users> Users_getUsersByServerKey(int serverKey) {
        ArrayList<Users> arraylistUsers = new ArrayList<>();
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE serverkey = ?",new String[]{String.valueOf(serverKey)});
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

                arraylistUsers.add(users);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistUsers;
    }

    public Users Users_getUsersByEmail(String Email) {
        ArrayList<Users> arraylistUsers = new ArrayList<>();
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?",new String[]{String.valueOf(Email)});
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

                arraylistUsers.add(users);

            } while (cursor.moveToNext());
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return arraylistUsers.get(0);
    }

    public boolean Users_isAdmin(String LoginName){
        boolean is =false;
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT admin FROM users WHERE email = ?",new String[]{String.valueOf(LoginName)});
        if (cursor.moveToFirst()) {
            int adminPermission=cursor.getInt(cursor.getColumnIndex("admin"));
            if (adminPermission==1){
                is=true;
            }else{
                is=false;
            }
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return is;
    }

    public String[] Users_listUsersHonourName (ArrayList<ChuyenBien> arrayListWorkingChuyenBien){
        List<String> lstData = new ArrayList<String>();
        ArrayList<ChuyenBien>arrChuyenBien=arrayListWorkingChuyenBien;
        String selectQuery = "SELECT * FROM users ORDER BY honourname ASC";
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try{
            cursor = db.rawQuery(selectQuery,null);
            if (cursor.moveToFirst()) {
                do {
                    String StrHonourName=cursor.getString(cursor.getColumnIndex("honourname"));
                    String strEmail=cursor.getString(cursor.getColumnIndex("email"));
                    if (!utils.isBad(StrHonourName)){
                        for (int i=0;i<arrChuyenBien.size();i++){
                            if (utils.comPare(arrChuyenBien.get(i).getUsername(),strEmail) &&
                                    !arrChuyenBien.get(i).getUsername().substring(0,5).equals("admin")){
                                lstData.add(StrHonourName);
                            }
                        }
                    }
                } while (cursor.moveToNext());
            }
            //db.close();
            String[] arrData = new String[lstData.size()];
            arrData = lstData.toArray(arrData);
            //Arrays.asList(arrData).contains("28&98")
//            for(String s : arrData)
//                Log.d(this.toString(), s );

            return arrData;
        }catch (Exception e){
            Log.e("crudUsers", "Exception: " + e.toString());
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public ArrayList<String> Users_listAllUsers (){
        ArrayList<String> lstData = new ArrayList<String>();
        String selectQuery = "SELECT * FROM users ORDER BY honourname ASC";
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor=null;
        try{
            cursor = db.rawQuery(selectQuery,null);
            if (cursor.moveToFirst()) {
                do {
                    String strEmail=cursor.getString(cursor.getColumnIndex("email"));
                    lstData.add(strEmail);
                } while (cursor.moveToNext());
            }
            //db.close();
            return lstData;
        }catch (Exception e){
            Log.e("crudUsers", "Exception: " + e.toString());
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }

    public String[] Users_listLinhBoHonourName (ArrayList<ChuyenBien> arrayListWorkingChuyenBien){
        List<String> lstData = new ArrayList<String>();
        String selectQuery = "SELECT * FROM users ORDER BY honourname ASC";
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        ArrayList<ChuyenBien>arrChuyenBien=arrayListWorkingChuyenBien;
        Cursor cursor=null;
        try{
            cursor = db.rawQuery(selectQuery,null);
            if (cursor.moveToFirst()) {
                do {
                    String StrHonourName=cursor.getString(cursor.getColumnIndex("honourname"));
                    String strEmail=cursor.getString(cursor.getColumnIndex("email"));
                    if (!utils.isBad(StrHonourName)){
                        int timgap=0;
                        for (int i=0;i<arrChuyenBien.size();i++){
                            if (utils.comPare(arrChuyenBien.get(i).getUsername(),strEmail)){
                                timgap=1;
                                break;
                            }
                        }
                        if (timgap==0){
                            lstData.add(StrHonourName);
                        }
                    }
                } while (cursor.moveToNext());
            }
            //db.close();
            String[] arrData = new String[lstData.size()];
            arrData = lstData.toArray(arrData);
            //Arrays.asList(arrData).contains("28&98")
//            for(String s : arrData)
//                Log.d(this.toString(), s );

            return arrData;
        }catch (Exception e){
            Log.e("crudUsers", "Exception: " + e.toString());
            return null;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
    }


    public int Users_getServerKeyByEmail (String Email){
        int i=0;
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        String[] params = new String[]{Email};
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor=null;
        try{
            cursor = db.rawQuery("SELECT serverkey FROM users WHERE email = ?", params);
            if (cursor.moveToFirst()) {
                i= cursor.getInt(0);
            }
            //db.close();
        }catch (Exception e){
            Log.e(TAG, "Exception: " + e.toString());
            return 0;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
        return i;
    }

    public int Users_getServerKeyByHonourName (String HonourName){
        int i=0;
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        String[] params = new String[]{ String.valueOf(HonourName)};
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor=null;
        try{
            cursor = db.rawQuery("SELECT serverkey FROM users WHERE honourname LIKE ?", params);
            if (cursor.moveToFirst()) {
                i= cursor.getInt(0);
            }
            //db.close();
        }catch (Exception e){
            Log.e(TAG, "Exception: " + e.toString());
            return 0;
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
        return i;
    }

    public String Users_getHonourname (long serverKey){
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        String[] params = new String[]{ String.valueOf(serverKey)};
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        String s="";
        Cursor cursor=null;
        try{
            cursor = db.rawQuery("SELECT honourname FROM users WHERE serverkey = ?", params);
            if (cursor.moveToFirst()) {
                s= cursor.getString(0);
            }
            //db.close();
        }catch (Exception e){
            Log.e("crudUsers", "Exception: " + e.toString());
            return "";
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
        return s;
    }

    public String Users_getUserNameByHonourName (String HonourName){
        String s="";
        //SQLiteDatabase db = sInstance.getReadableDatabase();
        String[] params = new String[]{HonourName};
        //Cursor cursor = db.rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        Cursor cursor=null;
        try{
            cursor = db.rawQuery("SELECT email FROM users WHERE honourname = ?", params);
            if (cursor.moveToFirst()) {
                s= cursor.getString(0);
            }
            //db.close();
        }catch (Exception e){
            Log.e("crudUsers", "Exception: " + e.toString());
            return "";
        }finally {
            if (cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            if (db!=null && db.isOpen()){
                //db.close();
            }
        }
        return s;
    }

    public int Users_deleteUsers(int serverKey ){
        //SQLiteDatabase db = sInstance.getWritableDatabase();
        int d= db.delete("users","serverkey=?",new String[] {String.valueOf(serverKey)});
        if (db!=null && db.isOpen()){
            //db.close();
        }
        return d;
    }
    public int WDFS_addWDFS(WantDeleteFromServer wdfs) {
        ContentValues values = new ContentValues();
        //values.put(SERVER_KEY, Integer.valueOf(chuyenBien.getmServerkey()));
        //values.put(DIRTY, Integer.valueOf(chuyenBien.getmDirty()));
        values.put("serverkey", wdfs.getmServerkey());
        values.put("tablename", wdfs.getmTablename());
        db.insert("wdfs", null, values);
        Log.d(TAG, "addWDFS Successfuly");
        return 1;
    }

    public ArrayList<WantDeleteFromServer> WDFS_getAllWDFS() {
        ArrayList<WantDeleteFromServer> arraylistWDFS = new ArrayList<>();
        String selectQuery = "SELECT * FROM wdfs";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()) {
            do {
                WantDeleteFromServer wdfs = new WantDeleteFromServer();

                wdfs.setmId(cursor.getInt(0));
                wdfs.setmServerkey(cursor.getInt(1));
                wdfs.setmTablename(cursor.getString(2));
                arraylistWDFS.add(wdfs);

            } while (cursor.moveToNext());
        }
        return arraylistWDFS;
    }

    public int WDFS_deleteWDFS(int id) {
        return db.delete("wdfs", "id=?", new String[]{String.valueOf(id)});
    }

}
