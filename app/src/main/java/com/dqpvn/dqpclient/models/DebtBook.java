package com.dqpvn.dqpclient.models;

/**
 * Created by linh3 on 25/12/2017.
 */

public class DebtBook {
    private int id;
    private int serverkey;
    private long rkey;
    private long rkeythuyenvien;
    private long rkeyticket;
    private String chuyenbien;
    private String ten;
    private String sotien;
    private String ngayps;
    private String lydo;
    private String updatetime;
    private String username;

    public DebtBook() {
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
    public long getRkeythuyenvien() {
        return rkeythuyenvien;
    }

    public void setRkeythuyenvien(long rkeythuyenvien) {
        this.rkeythuyenvien = rkeythuyenvien;
    }

    public long getRkeyticket() {
        return rkeyticket;
    }

    public void setRkeyticket(long rkeyticket) {
        this.rkeyticket = rkeyticket;
    }

    public String getChuyenbien() {
        return chuyenbien;
    }

    public void setChuyenbien(String chuyenbien) {
        this.chuyenbien = chuyenbien;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getSotien() {
        return sotien;
    }

    public void setSotien(String sotien) {
        this.sotien = sotien;
    }

    public String getNgayps() {
        return ngayps;
    }

    public void setNgayps(String ngayps) {
        this.ngayps = ngayps;
    }

    public String getLydo() {
        return lydo;
    }

    public void setLydo(String lydo) {
        this.lydo = lydo;
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
