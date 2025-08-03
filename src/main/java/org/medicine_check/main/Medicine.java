package org.medicine_check.main;
public class Medicine {
   private  String name;
    private String dose;
    private String time;
    private String date;
    public Medicine(String name, String dose, String time, String date) {
        this.name = name;
        this.dose = dose;
        this.time = time;
        this.date = date;
    }
    public String getName() { return name; }
    public String getDose() { return dose; }
    public String getTime() { return time; }
    public String getDate() { return date; }
} 

