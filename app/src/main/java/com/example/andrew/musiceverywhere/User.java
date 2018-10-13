package com.example.andrew.musiceverywhere;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class User {
    private File userData;
    private String name;
    private int lati, longi;
    private String currentSong;

    public User(){
        userData = new File("user-data.txt");
    }

    public void updateData(){
       /* try {
            //OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("user-data.txt", Context.MODE_PRIVATE));
            //outputStreamWriter.write(data);
            //outputStreamWriter.close();
        }
        //catch(IOException e){
          //  userData = new File("user-data.txt");
        //}*/
    }
}
