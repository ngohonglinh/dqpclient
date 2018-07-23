package com.dqpvn.dqpclient.models;

/**
 * Created by linh3 on 31/03/2018.
 */

public class BanHSDetail {
    private int id;
    private int serverkey;
    private long rkey;
    private long rkeythu;
    private long rkeythudetail;
    private String tenhs;
    private String soluong;
    private String updatetime;

    public BanHSDetail() {
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

    public long getRkeythudetail() {
        return rkeythudetail;
    }

    public void setRkeythudetail(long rkeythudetail) {
        this.rkeythudetail = rkeythudetail;
    }

    public String getTenhs() {
        return tenhs;
    }

    public void setTenhs(String tenhs) {
        this.tenhs = tenhs;
    }

    public String getSoluong() {
        return soluong;
    }

    public void setSoluong(String soluong) {
        this.soluong = soluong;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }
}
