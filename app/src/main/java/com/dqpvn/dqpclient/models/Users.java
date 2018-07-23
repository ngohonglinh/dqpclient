package com.dqpvn.dqpclient.models;

/**
 * Created by linh3 on 19/12/2017.
 */

public class Users {
    private int id;
    private int serverkey;
    private long rkey;
    private String fullname;
    private String honourname;
    private String email;
    private String password;
    private String nocty;
    private String ctyno;
    private String updatetime;
    private int admin;

    public Users() {
    }


    public String getCtyno() {
        return ctyno;
    }

    public void setCtyno(String ctyno) {
        this.ctyno = ctyno;
    }
    public String getNocty() {
        return nocty;
    }


    public void setNocty(String nocty) {
        this.nocty = nocty;
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

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHonourname() {
        return honourname;
    }

    public void setHonourname(String honourname) {
        this.honourname = honourname;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }
}
