package com.example.lemonsoftbag;

public class ExpenseModel {


    int id;
    private String type;
    String buy,reason;


    public ExpenseModel(int id, String buy, String reason, long date, String type) {
        this.id = id;
        this.buy = buy;
        this.reason = reason;
        this.date = date;
        this.type = type;


    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBuy() {
        return buy;
    }

    public void setBuy(String buy) {
        this.buy = buy;
    }
    long date;




    public long getDate() {
        return date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getType() {
        return type;
    }
}

