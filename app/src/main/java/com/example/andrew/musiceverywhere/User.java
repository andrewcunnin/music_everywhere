package com.example.andrew.musiceverywhere;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class User {
    //private File userData;
    public String id;
    private double lat, lon;
    private String name;
    private String currentSong;

    public User(){
        this.name = "";
        this.currentSong = "nothing";
        lat = 0;
        lon = 0;
    }

    public String getCurrentSong(){
        return currentSong;
    }

    public void setCurrentSong(String song){
        this.currentSong = song;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setLocation(double lon, double lat){
        this.lon = lon;
        this.lat = lat;
    }

    public double getLatitude(){
        return lat;
    }

    public double getLongitude(){
        return lon;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public User(String name){
        //userData = new File("user-data.txt");
        this.name = name;
        this.currentSong = "Nothing";
        this.lon = 0;
        this.lat = 0;
    }
    public User(String name, double lon, double lat){
        //userData = new File("user-data.txt");
        this.name = name;
        this.currentSong = "";
        this.lon = lon;
        this.lat = lat;
    }

    /*public String updateData(Context context){
       try {
            FileOutputStream out = new FileOutputStream(userData);
            out.write(name.getBytes());
            out.close();
            return name;
        }
        catch(IOException e){
           userData = new File("user-data.txt");
           return "Problem with writing";
        }

    }

    public String readData(Context context){
        try {
            FileInputStream in = new FileInputStream(userData);
            int length = (int) userData.length();
            byte[] bytes = new byte[length];
            in.read(bytes);
            in.close();
            return new String(bytes);
        }
        catch(IOException e){
            System.err.println("no data");
            return "ERROR";
        }
    }*/
}