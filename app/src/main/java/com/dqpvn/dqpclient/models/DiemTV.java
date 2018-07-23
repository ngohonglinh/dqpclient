package com.dqpvn.dqpclient.models;

/**
 * Created by linh3 on 18/03/2018.
 */

public class DiemTV {
    private int id;
    private int serverkey;
    private long rkey;
    private long rkeythuyenvien;
    private long rkeychuyenbien;
    private String diembt;
    private int diemdd;
    private String ngayps;
    private String updatetime;
    private String username;

    public DiemTV() {
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

    public long getRkeychuyenbien() {
        return rkeychuyenbien;
    }

    public void setRkeychuyenbien(long rkeychuyenbien) {
        this.rkeychuyenbien = rkeychuyenbien;
    }

    public String getDiembt() {
        return diembt;
    }

    public void setDiembt(String diembt) {
        this.diembt = diembt;
    }

    public int getDiemdd() {
        return diemdd;
    }

    public void setDiemdd(int diemdd) {
        this.diemdd = diemdd;
    }

    public String getNgayps() {
        return ngayps;
    }

    public void setNgayps(String ngayps) {
        this.ngayps = ngayps;
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
