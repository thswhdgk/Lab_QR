package com.example.lab_qr;

public class ListData {

    private String tv_name;
    private String tv_studentnumber;
    private String tv_time;

    public ListData(String tv_name, String tv_studentnumber, String tv_time) {

        this.tv_name = tv_name;
        this.tv_studentnumber = tv_studentnumber;
        this.tv_time = tv_time;
    }

    public String getTv_name() {
        return tv_name;
    }

    public void setTv_name(String tv_name) {
        this.tv_name = tv_name;
    }

    public String getTv_studentnumber() {
        return tv_studentnumber;
    }

    public void setTv_studentnumber(String tv_studentnumber) {
        this.tv_studentnumber = tv_studentnumber;
    }

    public String getTv_time() {
        return tv_time;
    }

    public void setTv_time(String tv_time) {
        this.tv_time = tv_time;
    }
}
