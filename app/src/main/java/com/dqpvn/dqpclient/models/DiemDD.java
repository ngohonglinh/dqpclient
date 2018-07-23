package com.dqpvn.dqpclient.models;

/**
 * Created by linh3 on 12/03/2018.
 */

public class DiemDD {
    private int id;
    private int serverkey;
    private long rkey;
    private long eater;
    private String eatername;
    private String chuyenbien;
    private int diemeater; //he so 1
    private String lydo;
    private String chucvu;
    private long feeder;
    private int diemfeeder; // he so 0.2
    private String ngayps;
    private String updatetime;
    private String username;

    public DiemDD() {
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

    public long getEater() {
        return eater;
    }

    public void setEater(long eater) {
        this.eater = eater;
    }

    public String getChucvu() {
        return chucvu;
    }

    public void setChucvu(String chucvu) {
        this.chucvu = chucvu;
    }

    public int getDiemeater() {
        return diemeater;
    }

    public void setDiemeater(int diemeater) {
        this.diemeater = diemeater;
    }

    public long getFeeder() {
        return feeder;
    }

    public void setFeeder(long feeder) {
        this.feeder = feeder;
    }

    public int getDiemfeeder() {
        return diemfeeder;
    }

    public void setDiemfeeder(int diemfeeder) {
        this.diemfeeder = diemfeeder;
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

    public String getLydo() {
        return lydo;
    }

    public void setLydo(String lydo) {
        this.lydo = lydo;
    }

    public String getEatername() {
        return eatername;
    }

    public void setEatername(String eatername) {
        this.eatername = eatername;
    }

    public String getChuyenbien() {
        return chuyenbien;
    }

    public void setChuyenbien(String chuyenbien) {
        this.chuyenbien = chuyenbien;
    }
}
