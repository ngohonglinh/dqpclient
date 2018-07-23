package com.dqpvn.dqpclient.models;

/**
 * Created by linh3 on 18/03/2018.
 */

public class TicketDetail {
    private int id;
    private int serverkey;
    private long rkey;
    private long rkeyticket;
    private String foruser;
    private String amount;
    private String ngayps;
    private String notes;
    private String updatetime;
    private String username;

    public TicketDetail() {
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

    public long getRkeyticket() {
        return rkeyticket;
    }

    public void setRkeyticket(long rkeyticket) {
        this.rkeyticket = rkeyticket;
    }

    public String getForuser() {
        return foruser;
    }

    public void setForuser(String foruser) {
        this.foruser = foruser;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNgayps() {
        return ngayps;
    }

    public void setNgayps(String ngayps) {
        this.ngayps = ngayps;
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

    public long getRkey() {
        return rkey;
    }

    public void setRkey(long rkey) {
        this.rkey = rkey;
    }
}
