package com.example.andrew.musiceverywhere;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class User {
    private File userData;
    private String name;
    private int lati, longi;
    private String currentSong;

    public User(){
        userData = new File("user-data.txt");
        this.name = "";
        this.currentSong = "";
    }

    public User(String name){
        userData = new File("user-data.txt");
        this.name = name;
        this.currentSong = "";
    }

    public void updateData(Context context){
       try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("user-data.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(name + "\n");
            outputStreamWriter.write(currentSong + "\n");
            outputStreamWriter.close();
        }
        catch(IOException e){
           userData = new File("user-data.txt");
        }
    }

    public String readData(Context context){
        try {
            InputStream is = context.getAssets().open("user-data.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            System.out.println("READING A USER");
            if((line = reader.readLine()) != null){
                return line;
            }
            return "No data";
        }
        catch(IOException e){
            System.err.println("no data");
            return "ERROR";
        }
    }
}
