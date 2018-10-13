package com.example.andrew.musiceverywhere;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Login extends AppCompatActivity {
    public static LocationManager locationmanager;
    public static User currentUser = new User("Jeff");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DatabaseReference ref = DBClient.db.getReference("users");
        ref.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                  //List<User> user = dataSnapshot.getValue(List<User>.class);
                  //((TextView)findViewById(R.id.textView)).setText(dataSnapshot.getValue().toString());
              }

              @Override
                public void onCancelled(DatabaseError databaseError){
                  Log.d("ERROR: ", "CANCEL USER READ");
              }
          });
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);

        FloatingActionButton writeUser = (FloatingActionButton) findViewById(R.id.writeUser);
        writeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                currentUser.setName(currentUser.getName()+"f");
                DBClient.writeToUser(currentUser);

                if(ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location lastKnownLocation = Login.locationmanager.getLastKnownLocation(Login.locationmanager.GPS_PROVIDER);
                    currentUser.setLocation(lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude());
                }
                else{
                    Log.d("NO LOCATION FOUND", "TRUE");
                }
            }
        });
        FloatingActionButton getUser = (FloatingActionButton) findViewById(R.id.getUsers);
        getUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                //((TextView)findViewById(R.id.textView)).setText(DBClient.getUserById(currentUser.getId()));
            }
        });
    }

}
