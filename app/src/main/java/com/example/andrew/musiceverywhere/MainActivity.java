package com.example.andrew.musiceverywhere;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = this.getApplicationContext();
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        setContentView(R.layout.activity_main);
    }

    public void spotifyLogin(View view) {
        Intent intent = new Intent(this, Music.class);
        EditText editTextUser = (EditText) findViewById(R.id.text_username);
        EditText editTextPass = (EditText) findViewById(R.id.text_password);
        String userMessage = editTextUser.getText().toString();
        String passMessage = editTextPass.getText().toString();
        if(userMessage.equals("hack")&& passMessage.equals("umass")){
            startActivity(intent);
        }
        startActivity(intent);

    }

}
