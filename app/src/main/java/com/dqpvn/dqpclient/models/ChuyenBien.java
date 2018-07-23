package com.dqpvn.dqpclient.models;

/**
 * Created by linh3 on 21/11/2017.
 */

public class ChuyenBien {
    private int id;
    private int serverkey;
    private long rkey;
    private String chuyenbien;
    private String tentau;
    private String ngaykhoihanh;
    private String ngayketchuyen;
    private String tongthu;
    private String tongchi;
    private int dachia;
    private String updatetime;
    private String username;

    public ChuyenBien (){

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

    public String getChuyenbien() {
        return chuyenbien;
    }

    public void setChuyenbien(String chuyenbien) {
        this.chuyenbien = chuyenbien;
    }

    public String getTentau() {
        return tentau;
    }

    public void setTentau(String tentau) {
        this.tentau = tentau;
    }

    public String getNgaykhoihanh() {
        return ngaykhoihanh;
    }

    public void setNgaykhoihanh(String ngaykhoihanh) {
        this.ngaykhoihanh = ngaykhoihanh;
    }

    public String getNgayketchuyen() {
        return ngayketchuyen;
    }

    public void setNgayketchuyen(String ngayketchuyen) {
        this.ngayketchuyen = ngayketchuyen;
    }

    public String getTongthu() {
        return tongthu;
    }

    public void setTongthu(String tongthu) {
        this.tongthu = tongthu;
    }

    public String getTongchi() {
        return tongchi;
    }

    public void setTongchi(String tongchi) {
        this.tongchi = tongchi;
    }

    public int getDachia() {
        return dachia;
    }

    public void setDachia(int dachia) {
        this.dachia = dachia;
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
