package com.dqpvn.dqpclient.models;

import java.io.Serializable;

/**
 * Created by linh3 on 16/12/2017.
 */

public class ThuByChuyenBien {
    private int mId;
    private long mRkeyThu;
    private String mKhachHang;
    private String mLydo;
    private String mNgayPS;
    private String mGiaTri;
    private String mDaTra;
    private String mConLai;

    public ThuByChuyenBien() {
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public long getmRkeyThu() {
        return mRkeyThu;
    }

    public void setmRkeyThu(long mRkeyThu) {
        this.mRkeyThu = mRkeyThu;
    }

    public String getmKhachHang() {
        return mKhachHang;
    }

    public void setmKhachHang(String mKhachHang) {
        this.mKhachHang = mKhachHang;
    }

    public String getmLydo() {
        return mLydo;
    }

    public void setmLydo(String mLydo) {
        this.mLydo = mLydo;
    }

    public String getmNgayPS() {
        return mNgayPS;
    }

    public void setmNgayPS(String mNgayPS) {
        this.mNgayPS = mNgayPS;
    }

    public String getmGiaTri() {
        return mGiaTri;
    }

    public void setmGiaTri(String mGiaTri) {
        this.mGiaTri = mGiaTri;
    }

    public String getmDaTra() {
        return mDaTra;
    }

    public void setmDaTra(String mDaTra) {
        this.mDaTra = mDaTra;
    }

    public String getmConLai() {
        return mConLai;
    }

    public void setmConLai(String mConLai) {
        this.mConLai = mConLai;
    }
}
