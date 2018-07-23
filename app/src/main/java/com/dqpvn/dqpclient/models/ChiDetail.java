package com.dqpvn.dqpclient.models;

/**
 * Created by linh3 on 13/12/2017.
 */

public class ChiDetail {
    private int id;
    private int serverkey;
    private long rkey;
    private long rkeychi;
    private String tenchuyenbien;
    private String tendoitac;
    private String sanpham;
    private String soluong;
    private String dongia;
    private String thanhtien;
    private String updatetime;
    private String username;

    public ChiDetail() {
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

    public long getRkeychi() {
        return rkeychi;
    }

    public void setRkeychi(long rkeychi) {
        this.rkeychi = rkeychi;
    }

    public String getTenchuyenbien() {
        return tenchuyenbien;
    }

    public void setTenchuyenbien(String tenchuyenbien) {
        this.tenchuyenbien = tenchuyenbien;
    }

    public String getTendoitac() {
        return tendoitac;
    }

    public void setTendoitac(String tendoitac) {
        this.tendoitac = tendoitac;
    }

    public String getSanpham() {
        return sanpham;
    }

    public void setSanpham(String sanpham) {
        this.sanpham = sanpham;
    }

    public String getSoluong() {
        return soluong;
    }

    public void setSoluong(String soluong) {
        this.soluong = soluong;
    }

    public String getDongia() {
        return dongia;
    }

    public void setDongia(String dongia) {
        this.dongia = dongia;
    }

    public String getThanhtien() {
        return thanhtien;
    }

    public void setThanhtien(String thanhtien) {
        this.thanhtien = thanhtien;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
