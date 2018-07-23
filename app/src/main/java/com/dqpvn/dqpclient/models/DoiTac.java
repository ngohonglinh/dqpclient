package com.dqpvn.dqpclient.models;

/**
 * Created by linh3t on 28/11/2017.
 */

public class DoiTac {
    private int id;
    private int serverkey;
    private long rkey;
    private String tendoitac;
    private String sodienthoai;
    private String diachi;
    private String nocty;
    private String ctyno;
    private String updatetime;

    public DoiTac() {
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

    public long getRkey() {
        return rkey;
    }

    public void setRkey(long rkey) {
        this.rkey = rkey;
    }

    public void setServerkey(int serverkey) {
        this.serverkey = serverkey;
    }

    public String getTendoitac() {
        return tendoitac;
    }

    public void setTendoitac(String tendoitac) {
        this.tendoitac = tendoitac;
    }

    public String getSodienthoai() {
        return sodienthoai;
    }

    public void setSodienthoai(String sodienthoai) {
        this.sodienthoai = sodienthoai;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getNocty() {
        return nocty;
    }

    public void setNocty(String nocty) {
        this.nocty = nocty;
    }

    public String getCtyno() {
        return ctyno;
    }

    public void setCtyno(String ctyno) {
        this.ctyno = ctyno;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }
}
