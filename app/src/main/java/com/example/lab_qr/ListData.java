package com.example.lab_qr;

public class ListData {

    private String name;
    private String stid;
    private String start_time;
    private String finish_time;

    public ListData(String name, String stid, String start_time, String finish_time) {
        this.name = name;
        this.stid = stid;
        this.start_time = start_time;
        this.finish_time = finish_time;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStid() { return stid; }
    public void setStid(String stid) { this.stid = stid; }
    public String getStart_time() { return start_time; }
    public void setStart_time(String start_time) { this.start_time = start_time; }
    public String getFinish_time() { return finish_time; }
    public void setFinish_time(String finish_time) { this.finish_time = finish_time; }
}
