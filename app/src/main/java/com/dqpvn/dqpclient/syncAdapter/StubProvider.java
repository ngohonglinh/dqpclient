package com.dqpvn.dqpclient.syncAdapter;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by linh3 on 29/04/2018.
 */

public class StubProvider extends ContentProvider {
    private SQLiteDatabase db;
    private final String TAG = "DQP Client";
    private final static String DATABASE_NAME = "dqpclient.db";
    private final static int DATABASE_VERSION=1;


    public static final String PROVIDER_NAME = "com.dqpvn.dqpclient.syncAdapter.StubProvider";
    public static final Uri CONTENT_URI_WDFS = Uri.parse("content://" + PROVIDER_NAME + "/wdfs");
    public static final Uri CONTENT_URI_USERS = Uri.parse("content://" + PROVIDER_NAME + "/users");
    public static final Uri CONTENT_URI_CHUYENBIEN = Uri.parse("content://" + PROVIDER_NAME + "/chuyenbien");
    public static final Uri CONTENT_URI_DSTV = Uri.parse("content://" + PROVIDER_NAME + "/dstv");
    public static final Uri CONTENT_URI_DIEMDD = Uri.parse("content://" + PROVIDER_NAME + "/diemdd");
    public static final Uri CONTENT_URI_KHACHHANG = Uri.parse("content://" + PROVIDER_NAME + "/khachhang");
    public static final Uri CONTENT_URI_DOITAC = Uri.parse("content://" + PROVIDER_NAME + "/doitac");
    public static final Uri CONTENT_URI_TICKET = Uri.parse("content://" + PROVIDER_NAME + "/ticket");
    public static final Uri CONTENT_URI_TICKETDETAIL = Uri.parse("content://" + PROVIDER_NAME + "/ticketdetail");
    public static final Uri CONTENT_URI_THU = Uri.parse("content://" + PROVIDER_NAME + "/thu");
    public static final Uri CONTENT_URI_THUDETAIL = Uri.parse("content://" + PROVIDER_NAME + "/thudetail");
    public static final Uri CONTENT_URI_CHI = Uri.parse("content://" + PROVIDER_NAME + "/chi");
    public static final Uri CONTENT_URI_CHIDETAIL = Uri.parse("content://" + PROVIDER_NAME + "/chidetail");
    public static final Uri CONTENT_URI_DEBTBOOK = Uri.parse("content://" + PROVIDER_NAME + "/debtbook");
    public static final Uri CONTENT_URI_DMHAISAN = Uri.parse("content://" + PROVIDER_NAME + "/dmhaisan");
    public static final Uri CONTENT_URI_BANHSDETAIL = Uri.parse("content://" + PROVIDER_NAME + "/banhsdetail");
    public static final Uri CONTENT_URI_IMGSTORE = Uri.parse("content://" + PROVIDER_NAME + "/imgstore");


    // build up a tree of UriMatcher objects ===================================
    // To known details look : http://developer.android.com/reference/android/content/UriMatcher.html
    private static final int WDFS = 1;
    private static final int WDFS_ID = 2;
    private static final int USERS = 3;
    private static final int USERS_SK = 4;
    private static final int USERS_CHUAEDITRKEY = 5;
    private static final int CHUYENBIEN = 6;
    private static final int CHUYENBIEN_SK = 7;
    private static final int CHUYENBIEN_CHUAEDITRKEY = 8;
    private static final int DSTV = 9;
    private static final int DSTV_SK = 10;
    private static final int DSTV_CHUAEDITRKEY = 11;
    private static final int DIEMDD = 12;
    private static final int DIEMDD_SK = 13;
    private static final int DIEMDD_CHUAEDITRKEY = 14;
    private static final int KHACHHANG = 15;
    private static final int KHACHHANG_SK = 16;
    private static final int KHACHHANG_CHUAEDITRKEY = 17;
    private static final int DOITAC = 18;
    private static final int DOITAC_SK = 19;
    private static final int DOITAC_CHUAEDITRKEY = 20;
    private static final int TICKET = 21;
    private static final int TICKET_SK = 22;
    private static final int TICKET_CHUAEDITRKEY = 23;
    private static final int TICKETDETAIL = 24;
    private static final int TICKETDETAIL_SK = 25;
    private static final int TICKETDETAIL_CHUAEDITRKEY = 26;
    private static final int THU = 27;
    private static final int THU_SK = 28;
    private static final int THU_CHUAEDITRKEY = 29;
    private static final int THUDETAIL = 30;
    private static final int THUDETAIL_SK = 31;
    private static final int THUDETAIL_CHUAEDITRKEY = 32;
    private static final int CHI = 33;
    private static final int CHI_SK = 34;
    private static final int CHI_CHUAEDITRKEY = 35;
    private static final int CHIDETAIL = 36;
    private static final int CHIDETAIL_SK = 37;
    private static final int CHIDETAIL_CHUAEDITRKEY = 38;
    private static final int DEBTBOOK = 39;
    private static final int DEBTBOOK_SK = 40;
    private static final int DEBTBOOK_CHUAEDITRKEY = 41;
    private static final int DMHAISAN = 42;
    private static final int DMHAISAN_SK = 43;
    private static final int DMHAISAN_CHUAEDITRKEY = 44;
    private static final int BANHSDETAIL = 48;
    private static final int BANHSDETAIL_SK = 49;
    private static final int BANHSDETAIL_CHUAEDITRKEY = 50;
    private static final int IMGSTORE = 51;
    private static final int IMGSTORE_SK = 52;
    private static final int IMGSTORE_CHUAEDITRKEY = 53;
    private static final int IMGSTORE_BADLINK = 54;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "wdfs", WDFS);
        uriMatcher.addURI(PROVIDER_NAME, "wdfs/#", WDFS_ID);
        uriMatcher.addURI(PROVIDER_NAME, "users", USERS);
        uriMatcher.addURI(PROVIDER_NAME, "users/#", USERS_SK);
        uriMatcher.addURI(PROVIDER_NAME, "users/chuaEditRkey", USERS_CHUAEDITRKEY);
        uriMatcher.addURI(PROVIDER_NAME, "chuyenbien", CHUYENBIEN);
        uriMatcher.addURI(PROVIDER_NAME, "chuyenbien/#", CHUYENBIEN_SK);
        uriMatcher.addURI(PROVIDER_NAME, "chuyenbien/chuaEditRkey", CHUYENBIEN_CHUAEDITRKEY);
        uriMatcher.addURI(PROVIDER_NAME, "dstv", DSTV);
        uriMatcher.addURI(PROVIDER_NAME, "dstv/#", DSTV_SK);
        uriMatcher.addURI(PROVIDER_NAME, "dstv/chuaEditRkey", DSTV_CHUAEDITRKEY);
        uriMatcher.addURI(PROVIDER_NAME, "diemdd", DIEMDD);
        uriMatcher.addURI(PROVIDER_NAME, "diemdd/#", DIEMDD_SK);
        uriMatcher.addURI(PROVIDER_NAME, "diemdd/chuaEditRkey", DIEMDD_CHUAEDITRKEY);
        uriMatcher.addURI(PROVIDER_NAME, "khachhang", KHACHHANG);
        uriMatcher.addURI(PROVIDER_NAME, "khachhang/#", KHACHHANG_SK);
        uriMatcher.addURI(PROVIDER_NAME, "khachhang/chuaEditRkey", KHACHHANG_CHUAEDITRKEY);
        uriMatcher.addURI(PROVIDER_NAME, "doitac", DOITAC);
        uriMatcher.addURI(PROVIDER_NAME, "doitac/#", DOITAC_SK);
        uriMatcher.addURI(PROVIDER_NAME, "doitac/chuaEditRkey", DOITAC_CHUAEDITRKEY);
        uriMatcher.addURI(PROVIDER_NAME, "ticket", TICKET);
        uriMatcher.addURI(PROVIDER_NAME, "ticket/#", TICKET_SK);
        uriMatcher.addURI(PROVIDER_NAME, "ticket/chuaEditRkey", TICKET_CHUAEDITRKEY);
        uriMatcher.addURI(PROVIDER_NAME, "ticketdetail", TICKETDETAIL);
        uriMatcher.addURI(PROVIDER_NAME, "ticketdetail/#", TICKETDETAIL_SK);
        uriMatcher.addURI(PROVIDER_NAME, "ticketdetail/chuaEditRkey", TICKETDETAIL_CHUAEDITRKEY);
        uriMatcher.addURI(PROVIDER_NAME, "thu", THU);
        uriMatcher.addURI(PROVIDER_NAME, "thu/#", THU_SK);
        uriMatcher.addURI(PROVIDER_NAME, "thu/chuaEditRkey", THU_CHUAEDITRKEY);
        uriMatcher.addURI(PROVIDER_NAME, "thudetail", THUDETAIL);
        uriMatcher.addURI(PROVIDER_NAME, "thudetail/#", THUDETAIL_SK);
        uriMatcher.addURI(PROVIDER_NAME, "thudetail/chuaEditRkey", THUDETAIL_CHUAEDITRKEY);
        uriMatcher.addURI(PROVIDER_NAME, "chi", CHI);
        uriMatcher.addURI(PROVIDER_NAME, "chi/#", CHI_SK);
        uriMatcher.addURI(PROVIDER_NAME, "chi/chuaEditRkey", CHI_CHUAEDITRKEY);
        uriMatcher.addURI(PROVIDER_NAME, "chidetail", CHIDETAIL);
        uriMatcher.addURI(PROVIDER_NAME, "chidetail/#", CHIDETAIL_SK);
        uriMatcher.addURI(PROVIDER_NAME, "chidetail/chuaEditRkey", CHIDETAIL_CHUAEDITRKEY);
        uriMatcher.addURI(PROVIDER_NAME, "debtbook", DEBTBOOK);
        uriMatcher.addURI(PROVIDER_NAME, "debtbook/#", DEBTBOOK_SK);
        uriMatcher.addURI(PROVIDER_NAME, "debtbook/chuaEditRkey", DEBTBOOK_CHUAEDITRKEY);
        uriMatcher.addURI(PROVIDER_NAME, "dmhaisan", DMHAISAN);
        uriMatcher.addURI(PROVIDER_NAME, "dmhaisan/#", DMHAISAN_SK);
        uriMatcher.addURI(PROVIDER_NAME, "dmhaisan/chuaEditRkey", DMHAISAN_CHUAEDITRKEY);
        uriMatcher.addURI(PROVIDER_NAME, "banhsdetail", BANHSDETAIL);
        uriMatcher.addURI(PROVIDER_NAME, "banhsdetail/#", BANHSDETAIL_SK);
        uriMatcher.addURI(PROVIDER_NAME, "banhsdetail/chuaEditRkey", BANHSDETAIL_CHUAEDITRKEY);
        uriMatcher.addURI(PROVIDER_NAME, "imgstore", IMGSTORE);
        uriMatcher.addURI(PROVIDER_NAME, "imgstore/#", IMGSTORE_SK);
        uriMatcher.addURI(PROVIDER_NAME, "imgstore/chuaEditRkey", IMGSTORE_CHUAEDITRKEY);
        uriMatcher.addURI(PROVIDER_NAME, "imgstore/badlink", IMGSTORE_BADLINK);
    }

    // Your content provider uses a SQLite Database to store the persons.
    private static class DatabaseClient extends SQLiteOpenHelper {
        private static volatile DatabaseClient instance;
        private final SQLiteDatabase db;

        private final String SQLWDFS = "CREATE TABLE IF NOT EXISTS wdfs (id INTEGER PRIMARY KEY, " +
                "serverkey INTEGER, tablename TEXT)";

        private final String SQLChuyenBien = "CREATE TABLE IF NOT EXISTS chuyenbien (id INTEGER PRIMARY KEY, " +
                "serverkey INTEGER, rkey LONG, chuyenbien TEXT, tentau TEXT, ngaykhoihanh TEXT, " +
                "ngayketchuyen TEXT, tongthu LONG DEFAULT 0, tongchi LONG DEFAULT 0, " +
                "dachia INT DEFAULT 0, updatetime LONG NOT NULL, username TEXT)";

        private final String SQLDoiTac = "CREATE TABLE IF NOT EXISTS doitac (id INTEGER PRIMARY KEY, " +
                "serverkey INTEGER, rkey LONG, tendoitac TEXT, sodienthoai TEXT, diachi TEXT, " +
                "nocty LONG DEFAULT 0, ctyno LONG DEFAULT 0, updatetime LONG NOT NULL)";

        private final String SQLKhachHang = "CREATE TABLE IF NOT EXISTS khachhang (id INTEGER PRIMARY KEY, " +
                "serverkey INTEGER, rkey LONG, tenkhach TEXT, sodienthoai TEXT, diachi TEXT, " +
                "nocty LONG DEFAULT 0, ctyno  LONG DEFAULT 0, updatetime LONG NOT NULL)";

        private final String SQLChi="CREATE TABLE IF NOT EXISTS chi (id INTEGER PRIMARY KEY, " +
                "serverkey INTEGER, rkey LONG, rkeychuyenbien LONG NOT NULL REFERENCES chuyenbien(rkey) ON DELETE CASCADE, " +
                "rkeydoitac LONG NOT NULL REFERENCES doitac(rkey) ON DELETE CASCADE, rkeyticket LONG, lydo TEXT, ngayps TEXT, " +
                "giatri LONG DEFAULT 0, datra LONG DEFAULT 0, updatetime LONG NOT NULL, username TEXT)";

        private final String SQLChiDetail="CREATE TABLE IF NOT EXISTS chidetail (id INTEGER PRIMARY KEY, " +
                "serverkey INTEGER, rkey LONG, rkeychi LONG NOT NULL REFERENCES chi(rkey) ON DELETE CASCADE, " +
                "tenchuyenbien TEXT, tendoitac TEXT, sanpham TEXT, soluong LONG DEFAULT 0, " +
                "dongia LONG DEFAULT 0, thanhtien LONG DEFAULT 0, updatetime LONG NOT NULL, username TEXT)";

        private final String SQLThu="CREATE TABLE IF NOT EXISTS thu (id INTEGER PRIMARY KEY, " +
                "serverkey INTEGER, rkey LONG, rkeychuyenbien INTEGER NOT NULL REFERENCES chuyenbien(rkey) ON DELETE CASCADE, " +
                "rkeykhachhang LONG NOT NULL REFERENCES khachhang(rkey) ON DELETE CASCADE, lydo TEXT, ngayps TEXT, " +
                "giatri LONG DEFAULT 0, datra LONG DEFAULT 0, updatetime LONG NOT NULL, username TEXT)";

        private final String SQLthudetail="CREATE TABLE IF NOT EXISTS thudetail (id INTEGER PRIMARY KEY, " +
                "serverkey INTEGER, rkey LONG, rkeythu LONG NOT NULL REFERENCES thu(rkey) ON DELETE CASCADE, " +
                "tenhs TEXT, rkeyhs LONG, soluong TEXT, dongia LONG DEFAULT 0, " +
                "thanhtien LONG DEFAULT 0, updatetime LONG NOT NULL, username TEXT)";

        private final String SQLTicket="CREATE TABLE IF NOT EXISTS ticket (id INTEGER PRIMARY KEY, " +
                "serverkey INTEGER, rkey LONG, amount LONG DEFAULT 0, " +
                "used LONG DEFAULT 0, opendate TEXT, lydo TEXT, closedate TEXT , " +
                "finished INT DEFAULT 0, comeback LONG DEFAULT 0, updatetime LONG NOT NULL, username TEXT)";

        private final String SQLTicketDetail="CREATE TABLE IF NOT EXISTS ticketdetail (id INTEGER PRIMARY KEY, " +
                "serverkey INTEGER, rkey INTEGER, rkeyticket LONG, foruser TEXT, amount LONG DEFAULT 0, " +
                "ngayps TEXT, notes TEXT, updatetime LONG NOT NULL, username TEXT)";

        private final String SQLdstv="CREATE TABLE IF NOT EXISTS dstv (id INTEGER PRIMARY KEY, " +
                "serverkey INTEGER, rkey LONG, rkeychuyenbien LONG, " +
                "ten TEXT, diem TEXT, tienchia LONG DEFAULT 0, tienmuon LONG DEFAULT 0, tiencanca LONG DEFAULT 0, " +
                "conlai LONG DEFAULT 0, notes TEXT, updatetime LONG NOT NULL, username TEXT)";

        private final String SQLdebtbook="CREATE TABLE IF NOT EXISTS debtbook (id INTEGER PRIMARY KEY, " +
                "serverkey INTEGER, rkey LONG, rkeythuyenvien LONG, " +
                "rkeyticket LONG, chuyenbien TEXT, ten TEXT, sotien LONG DEFAULT 0, " +
                "ngayps TEXT, lydo TEXT, updatetime LONG NOT NULL, username TEXT)";

        private final String SQLdiemdd="CREATE TABLE IF NOT EXISTS diemdd (id INTEGER PRIMARY KEY, " +
                "serverkey INTEGER, rkey LONG, eater LONG, eatername TEXT, chuyenbien TEXT, diemeater INTEGER, lydo TEXT,  chucvu TEXT, " +
                "feeder LONG, diemfeeder INTEGER, ngayps TEXT, updatetime LONG NOT NULL, username TEXT)";

        private final String SQLimgstote="CREATE TABLE IF NOT EXISTS imgstore (id INTEGER PRIMARY KEY, " +
                "serverkey INTEGER, storekey LONG, fortable TEXT, imgpath TEXT, ngayps TEXT, updatetime LONG NOT NULL, " +
                "username TEXT)";

        private final String SQLusers="CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, " +
                "serverkey INTEGER, rkey LONG, fullname TEXT, honourname TEXT, email TEXT, password TEXT, nocty LONG DEFAULT 0, " +
                "ctyno LONG DEFAULT 0, updatetime LONG NOT NULL, admin INTEGER DEFAULT 0)";

        private final String SQLdmhaisan="CREATE TABLE IF NOT EXISTS dmhaisan (id INTEGER PRIMARY KEY, " +
                "serverkey INTEGER, rkey LONG, tenhs TEXT, " +
                "phanloai TEXT, dongia LONG, ngayps TEXT, notes TEXT, updatetime LONG NOT NULL, username TEXT)";

        private final String SQLbanhsdetail="CREATE TABLE IF NOT EXISTS banhsdetail (id INTEGER PRIMARY KEY, " +
                "serverkey INTEGER, rkey LONG, rkeythu LONG, rkeythudetail LONG, tenhs TEXT, " +
                "soluong DOUBLE, updatetime LONG NOT NULL)";

        private final String SQLsumyear="CREATE TABLE IF NOT EXISTS sumyear (id INTEGER PRIMARY KEY, " +
                "serverkey INTEGER, rkey LONG, tentau TEXT, whichyear TEXT, voncodinh LONG DEFAULT 0, vonluudong LONG DEFAULT 0, " +
                "tongdoanhthu LONG DEFAULT 0, tthadlike INTEGER, tthaddislike INTEGER, tvsumlike INTEGER " +
                "updatetime LONG NOT NULL, username TEXT)";

        private DatabaseClient(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.db = getWritableDatabase();
        }

        /**
         * We use a Singleton to prevent leaking the SQLiteDatabase or Context.
         * @return {@link DatabaseClient}
         */
        public static DatabaseClient getInstance(Context context) {
            if (instance == null) {
                synchronized (DatabaseClient.class) {
                    if (instance == null) {
                        instance = new DatabaseClient(context);
                    }
                }
            }
            return instance;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQLWDFS);
            db.execSQL(SQLimgstote);
            db.execSQL(SQLChuyenBien);
            db.execSQL(SQLDoiTac);
            db.execSQL(SQLKhachHang);
            db.execSQL(SQLTicket);
            db.execSQL(SQLTicketDetail);
            db.execSQL(SQLdstv);
            db.execSQL(SQLdebtbook);
            db.execSQL(SQLChi);
            db.execSQL(SQLChiDetail);
            db.execSQL(SQLdmhaisan);
            db.execSQL(SQLThu);
            db.execSQL(SQLthudetail);
            db.execSQL(SQLbanhsdetail);
            db.execSQL(SQLusers);
            db.execSQL(SQLdiemdd);
            db.execSQL(SQLsumyear);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onCreate(db);
        }

        /**
         * Provide access to our database.
         */
        public SQLiteDatabase getDb() {
            return db;
        }
    }

    @Override
    public boolean onCreate() {
        this.db = DatabaseClient.getInstance(getContext()).getDb();
        return true;
    }
    /*
     * Return no type for MIME type
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c=null;
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)){
            case WDFS:
                sqlBuilder.setTables("wdfs");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case WDFS_ID:
                sqlBuilder.setTables("wdfs");
                sqlBuilder.appendWhere("id = " + uri.getPathSegments().get(1));
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case USERS:
                sqlBuilder.setTables("users");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case USERS_SK:
                sqlBuilder.setTables("users");
                sqlBuilder.appendWhere("serverkey = " + uri.getPathSegments().get(1));
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case USERS_CHUAEDITRKEY:
                sqlBuilder.setTables("users");
                sqlBuilder.appendWhere("serverkey <> 0 AND serverkey <> rkey");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case CHUYENBIEN:
                sqlBuilder.setTables("chuyenbien");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case CHUYENBIEN_SK:
                sqlBuilder.setTables("chuyenbien");
                sqlBuilder.appendWhere("serverkey = " + uri.getPathSegments().get(1));
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case CHUYENBIEN_CHUAEDITRKEY:
                sqlBuilder.setTables("chuyenbien");
                sqlBuilder.appendWhere("serverkey <> 0 AND serverkey <> rkey");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case DSTV:
                sqlBuilder.setTables("dstv");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case DSTV_SK:
                sqlBuilder.setTables("dstv");
                sqlBuilder.appendWhere("serverkey = " + uri.getPathSegments().get(1));
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case DSTV_CHUAEDITRKEY:
                sqlBuilder.setTables("dstv");
                sqlBuilder.appendWhere("serverkey <> 0 AND serverkey <> rkey");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case DIEMDD:
                sqlBuilder.setTables("diemdd");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case DIEMDD_SK:
                sqlBuilder.setTables("diemdd");
                sqlBuilder.appendWhere("serverkey = " + uri.getPathSegments().get(1));
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case DIEMDD_CHUAEDITRKEY:
                sqlBuilder.setTables("diemdd");
                sqlBuilder.appendWhere("serverkey <> 0 AND serverkey <> rkey");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case KHACHHANG:
                sqlBuilder.setTables("khachhang");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case KHACHHANG_SK:
                sqlBuilder.setTables("khachhang");
                sqlBuilder.appendWhere("serverkey = " + uri.getPathSegments().get(1));
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case KHACHHANG_CHUAEDITRKEY:
                sqlBuilder.setTables("khachhang");
                sqlBuilder.appendWhere("serverkey <> 0 AND serverkey <> rkey");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case DOITAC:
                sqlBuilder.setTables("doitac");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case DOITAC_SK:
                sqlBuilder.setTables("doitac");
                sqlBuilder.appendWhere("serverkey = " + uri.getPathSegments().get(1));
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case DOITAC_CHUAEDITRKEY:
                sqlBuilder.setTables("doitac");
                sqlBuilder.appendWhere("serverkey <> 0 AND serverkey <> rkey");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case TICKET:
                sqlBuilder.setTables("ticket");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case TICKET_SK:
                sqlBuilder.setTables("ticket");
                sqlBuilder.appendWhere("serverkey = " + uri.getPathSegments().get(1));
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case TICKET_CHUAEDITRKEY:
                sqlBuilder.setTables("ticket");
                sqlBuilder.appendWhere("serverkey <> 0 AND serverkey <> rkey");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case TICKETDETAIL:
                sqlBuilder.setTables("ticketdetail");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case TICKETDETAIL_SK:
                sqlBuilder.setTables("ticketdetail");
                sqlBuilder.appendWhere("serverkey = " + uri.getPathSegments().get(1));
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case TICKETDETAIL_CHUAEDITRKEY:
                sqlBuilder.setTables("ticketdetail");
                sqlBuilder.appendWhere("serverkey <> 0 AND serverkey <> rkey");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case THU:
                sqlBuilder.setTables("thu");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case THU_SK:
                sqlBuilder.setTables("thu");
                sqlBuilder.appendWhere("serverkey = " + uri.getPathSegments().get(1));
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case THU_CHUAEDITRKEY:
                sqlBuilder.setTables("thu");
                sqlBuilder.appendWhere("serverkey <> 0 AND serverkey <> rkey");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case THUDETAIL:
                sqlBuilder.setTables("thudetail");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case THUDETAIL_SK:
                sqlBuilder.setTables("thudetail");
                sqlBuilder.appendWhere("serverkey = " + uri.getPathSegments().get(1));
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case THUDETAIL_CHUAEDITRKEY:
                sqlBuilder.setTables("thudetail");
                sqlBuilder.appendWhere("serverkey <> 0 AND serverkey <> rkey");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case CHI:
                sqlBuilder.setTables("chi");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case CHI_SK:
                sqlBuilder.setTables("chi");
                sqlBuilder.appendWhere("serverkey = " + uri.getPathSegments().get(1));
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case CHI_CHUAEDITRKEY:
                sqlBuilder.setTables("chi");
                sqlBuilder.appendWhere("serverkey <> 0 AND serverkey <> rkey");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case CHIDETAIL:
                sqlBuilder.setTables("chidetail");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case CHIDETAIL_SK:
                sqlBuilder.setTables("chidetail");
                sqlBuilder.appendWhere("serverkey = " + uri.getPathSegments().get(1));
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case CHIDETAIL_CHUAEDITRKEY:
                sqlBuilder.setTables("chidetail");
                sqlBuilder.appendWhere("serverkey <> 0 AND serverkey <> rkey");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case DEBTBOOK:
                sqlBuilder.setTables("debtbook");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case DEBTBOOK_SK:
                sqlBuilder.setTables("debtbook");
                sqlBuilder.appendWhere("serverkey = " + uri.getPathSegments().get(1));
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case DEBTBOOK_CHUAEDITRKEY:
                sqlBuilder.setTables("debtbook");
                sqlBuilder.appendWhere("serverkey <> 0 AND serverkey <> rkey");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case DMHAISAN:
                sqlBuilder.setTables("dmhaisan");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case DMHAISAN_SK:
                sqlBuilder.setTables("dmhaisan");
                sqlBuilder.appendWhere("serverkey = " + uri.getPathSegments().get(1));
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case DMHAISAN_CHUAEDITRKEY:
                sqlBuilder.setTables("dmhaisan");
                sqlBuilder.appendWhere("serverkey <> 0 AND serverkey <> rkey");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case BANHSDETAIL:
                sqlBuilder.setTables("banhsdetail");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case BANHSDETAIL_SK:
                sqlBuilder.setTables("banhsdetail");
                sqlBuilder.appendWhere("serverkey = " + uri.getPathSegments().get(1));
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case BANHSDETAIL_CHUAEDITRKEY:
                sqlBuilder.setTables("banhsdetail");
                sqlBuilder.appendWhere("serverkey <> 0 AND serverkey <> rkey");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case IMGSTORE:
                sqlBuilder.setTables("imgstore");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case IMGSTORE_SK:
                sqlBuilder.setTables("imgstore");
                sqlBuilder.appendWhere("serverkey = " + uri.getPathSegments().get(1));
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case IMGSTORE_CHUAEDITRKEY:
                sqlBuilder.setTables("imgstore");
                sqlBuilder.appendWhere("serverkey <> 0 AND serverkey <> rkey");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case IMGSTORE_BADLINK:
                sqlBuilder.setTables("imgstore");
                sqlBuilder.appendWhere("SUBSTR(imgpath,1,4) <> 'http'");
                c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, null);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            default: throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID=0;
        Uri _uri = null;
        switch (uriMatcher.match(uri)) {
            case USERS:
                rowID = db.insert("users", "", values);
                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_USERS, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case DSTV:
                rowID = db.insert("dstv", "", values);
                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_DSTV, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case CHUYENBIEN:
                rowID = db.insert("chuyenbien", "", values);
                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_CHUYENBIEN, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case DIEMDD:
                rowID = db.insert("diemdd", "", values);
                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_DIEMDD, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case KHACHHANG:
                rowID = db.insert("khachhang", "", values);
                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_KHACHHANG, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case DOITAC:
                rowID = db.insert("doitac", "", values);
                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_DOITAC, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case TICKET:
                rowID = db.insert("ticket", "", values);
                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_TICKET, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case TICKETDETAIL:
                rowID = db.insert("ticketdetail", "", values);
                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_TICKETDETAIL, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case THU:
                rowID = db.insert("thu", "", values);
                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_THU, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case THUDETAIL:
                rowID = db.insert("thudetail", "", values);
                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_THUDETAIL, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case CHI:
                rowID = db.insert("chi", "", values);
                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_CHI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case CHIDETAIL:
                rowID = db.insert("chidetail", "", values);
                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_CHIDETAIL, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case DEBTBOOK:
                rowID = db.insert("debtbook", "", values);
                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_DEBTBOOK, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case DMHAISAN:
                rowID = db.insert("dmhaisan", "", values);
                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_DMHAISAN, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case BANHSDETAIL:
                rowID = db.insert("banhsdetail", "", values);
                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_BANHSDETAIL, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case IMGSTORE:
                rowID = db.insert("imgstore", "", values);
                if(rowID > 0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_IMGSTORE, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            default: throw new IllegalArgumentException("Unknown URI " + uri);
        }
        throw new SQLException("Faild to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case WDFS:
                count = db.delete("wdfs", selection, selectionArgs);
                break;
            case USERS:
                count = db.delete("users", selection, selectionArgs);
                break;
            case CHUYENBIEN:
                count = db.delete("chuyenbien", selection, selectionArgs);
                break;
            case DSTV:
                count = db.delete("dstv", selection, selectionArgs);
                break;
            case DIEMDD:
                count = db.delete("diemdd", selection, selectionArgs);
                break;
            case KHACHHANG:
                count = db.delete("khachhang", selection, selectionArgs);
                break;
            case DOITAC:
                count = db.delete("doitac", selection, selectionArgs);
                break;
            case TICKET:
                count = db.delete("ticket", selection, selectionArgs);
                break;
            case TICKETDETAIL:
                count = db.delete("ticketdetail", selection, selectionArgs);
                break;
            case THU:
                count = db.delete("thu", selection, selectionArgs);
                break;
            case THUDETAIL:
                count = db.delete("thudetail", selection, selectionArgs);
                break;
            case CHI:
                count = db.delete("chi", selection, selectionArgs);
                break;
            case CHIDETAIL:
                count = db.delete("chidetail", selection, selectionArgs);
                break;
            case DEBTBOOK:
                count = db.delete("debtbook", selection, selectionArgs);
                break;
            case DMHAISAN:
                count = db.delete("dmhaisan", selection, selectionArgs);
                break;
            case BANHSDETAIL:
                count = db.delete("banhsdetail", selection, selectionArgs);
                break;
            case IMGSTORE:
                count = db.delete("imgstore", selection, selectionArgs);
                break;
            default: throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case WDFS:
                count = db.update("wdfs", values, selection, selectionArgs);
                break;
            case USERS:
                count = db.update("users", values, selection, selectionArgs);
                break;
            case CHUYENBIEN:
                count = db.update("chuyenbien", values, selection, selectionArgs);
                break;
            case DSTV:
                count = db.update("dstv", values, selection, selectionArgs);
                break;
            case DIEMDD:
                count = db.update("diemdd", values, selection, selectionArgs);
                break;
            case KHACHHANG:
                count = db.update("khachhang", values, selection, selectionArgs);
                break;
            case DOITAC:
                count = db.update("doitac", values, selection, selectionArgs);
                break;
            case TICKET:
                count = db.update("ticket", values, selection, selectionArgs);
                break;
            case TICKETDETAIL:
                count = db.update("ticketdetail", values, selection, selectionArgs);
                break;
            case THU:
                count = db.update("thu", values, selection, selectionArgs);
                break;
            case THUDETAIL:
                count = db.update("thudetail", values, selection, selectionArgs);
                break;
            case CHI:
                count = db.update("chi", values, selection, selectionArgs);
                break;
            case CHIDETAIL:
                count = db.update("chidetail", values, selection, selectionArgs);
                break;
            case DEBTBOOK:
                count = db.update("debtbook", values, selection, selectionArgs);
                break;
            case DMHAISAN:
                count = db.update("dmhaisan", values, selection, selectionArgs);
                break;
            case BANHSDETAIL:
                count = db.update("banhsdetail", values, selection, selectionArgs);
                break;
            case IMGSTORE:
                count = db.update("imgstore", values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


}
