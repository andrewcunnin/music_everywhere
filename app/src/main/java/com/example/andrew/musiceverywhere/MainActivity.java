package com.example.andrew.musiceverywhere;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = this.getApplicationContext();
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},1);
        setContentView(R.layout.activity_main);
        final Button button = findViewById(R.id.update);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                User firstUser = new User("Jeff");
                Context context  = v.getContext();

                ((TextView) findViewById(R.id.textbox)).setText("HEY");
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    firstUser.updateData(context);
                }
                else{
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
                if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    ((TextView) findViewById(R.id.textbox)).setText(firstUser.readData(context));
                }
                else{
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    ((TextView) findViewById(R.id.textbox)).setText("NOOO");
                }
            }
        });
    }

}
