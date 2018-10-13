package com.example.andrew.musiceverywhere;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class User {
    //private File userData;
    private String name;
    private double lat, lon;
    public String id;
    private String currentSong;

    public User(){
        //userData = new File("user-data.txt");
        this.name = "";
        this.currentSong = "";
    }

    public String getID(){
        return id;
    }

    public void setID(String id){
        this.id = id;
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
        this.currentSong = "";
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
