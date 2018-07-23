package com.dqpvn.dqpclient.models;

import java.io.Serializable;

/**
 * Created by linh3t on 28/11/2017.
 */

public class Thu implements Serializable {
    private int id;
    private int serverkey;
    private long rkey;
    private long rkeychuyenbien;
    private long rkeykhachhang;
    private String lydo;
    private String ngayps;
    private String giatri;
    private String datra;
    private String mTenChuyenBien;
    private String mTenKhachHang;
    private String updatetime;
    private String username;

    public Thu() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServerkey() {
        return serverkey;
    }

    public void setServerkey(int serverkey) {
        this.serverkey = serverkey;
    }

    public long getRkey() {
        return rkey;
    }

    public void setRkey(long rkey) {
        this.rkey = rkey;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public long getRkeychuyenbien() {
        return rkeychuyenbien;
    }

    public void setRkeychuyenbien(long rkeychuyenbien) {
        this.rkeychuyenbien = rkeychuyenbien;
    }

    public long getRkeykhachhang() {
        return rkeykhachhang;
    }

    public void setRkeykhachhang(long rkeykhachhang) {
        this.rkeykhachhang = rkeykhachhang;
    }

    public String getLydo() {
        return lydo;
    }

    public void setLydo(String lydo) {
        this.lydo = lydo;
    }

    public String getNgayps() {
        return ngayps;
    }

    public void setNgayps(String ngayps) {
        this.ngayps = ngayps;
    }

    public String getGiatri() {
        return giatri;
    }

    public void setGiatri(String giatri) {
        this.giatri = giatri;
    }

    public String getDatra() {
        return datra;
    }

    public void setDatra(String datra) {
        this.datra = datra;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getmTenChuyenBien() {
        return mTenChuyenBien;
    }

    public void setmTenChuyenBien(String mTenChuyenBien) {
        this.mTenChuyenBien = mTenChuyenBien;
    }

    public String getmTenKhachHang() {
        return mTenKhachHang;
    }

    public void setmTenKhachHang(String mTenKhachHang) {
        this.mTenKhachHang = mTenKhachHang;
    }
}
