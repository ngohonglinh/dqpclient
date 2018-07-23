package com.dqpvn.dqpclient.models;

/**
 * Created by linh3 on 10/01/2018.
 */

public class ImgStore {
    private  int id;
    private  int serverkey;
    private  long storekey;
    private String fortable;
    private String imgpath;
    private String ngayps;
    private String updatetime;
    private String username;

    public ImgStore() {
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

    public long getStorekey() {
        return storekey;
    }

    public void setStorekey(long storekey) {
        this.storekey = storekey;
    }

    public String getFortable() {
        return fortable;
    }

    public void setFortable(String fortable) {
        this.fortable = fortable;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
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
