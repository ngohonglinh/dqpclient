package com.dqpvn.dqpclient.models;

/**
 * Created by linh3 on 31/03/2018.
 */

public class ThuDetail {
    private int id;
    private int serverkey;
    private long rkey;
    private long rkeythu;
    private String tenhs;
    private long rkeyhs;
    private String soluong;
    private String dongia;
    private String thanhtien;
    private String updatetime;
    private String username;

    public ThuDetail() {
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

    public long getRkeythu() {
        return rkeythu;
    }

    public void setRkeythu(long rkeythu) {
        this.rkeythu = rkeythu;
    }

    public String getTenhs() {
        return tenhs;
    }

    public void setTenhs(String tenhs) {
        this.tenhs = tenhs;
    }

    public long getRkeyhs() {
        return rkeyhs;
    }

    public void setRkeyhs(long rkeyhs) {
        this.rkeyhs = rkeyhs;
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
