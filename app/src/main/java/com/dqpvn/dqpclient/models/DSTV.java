package com.dqpvn.dqpclient.models;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.dqpvn.dqpclient.syncAdapter.StubProvider;

/**
 * Created by linh3 on 25/12/2017.
 */

public class DSTV {

    private int id;
    private int serverkey;
    private long rkey;
    private long rkeychuyenbien;
    private String ten;
    private String diem;
    private String tienchia;
    private String tienmuon;
    private String tiencanca;
    private String conlai;
    private String notes;
    private String updatetime;
    private String username;

    public DSTV() {
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

    public long getRkeychuyenbien() {
        return rkeychuyenbien;
    }

    public void setRkeychuyenbien(long rkeychuyenbien) {
        this.rkeychuyenbien = rkeychuyenbien;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getTienchia() {
        return tienchia;
    }

    public void setTienchia(String tienchia) {
        this.tienchia = tienchia;
    }

    public String getTienmuon() {
        return tienmuon;
    }

    public void setTienmuon(String tienmuon) {
        this.tienmuon = tienmuon;
    }

    public String getTiencanca() {
        return tiencanca;
    }

    public void setTiencanca(String tiencanca) {
        this.tiencanca = tiencanca;
    }

    public String getConlai() {
        return conlai;
    }

    public void setConlai(String conlai) {
        this.conlai = conlai;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public String getDiem() {
        return diem;
    }

    public void setDiem(String diem) {
        this.diem = diem;
    }
}
