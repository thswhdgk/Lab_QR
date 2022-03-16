package com.example.lab_qr;

public class ListData {

    private String name;
    private String stid;
    private String population;
    private String start_time;
    private String finish_time;
    private String image_url;

    public ListData(String name, String stid, String population, String start_time, String finish_time, String image_url) {
        this.name = name;
        this.stid = stid;
        this.population = population;
        this.start_time = start_time;
        this.finish_time = finish_time;
        this.image_url = image_url;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStid() { return stid; }
    public void setStid(String stid) { this.stid = stid; }
    public String getPopulation() { return population; }
    public void setPopulation(String population) { this.population = population; }
    public String getStart_time() { return start_time; }
    public void setStart_time(String start_time) { this.start_time = start_time; }
    public String getFinish_time() { return finish_time; }
    public void setFinish_time(String finish_time) { this.finish_time = finish_time; }
    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }
}
