package com.example.andrew.musiceverywhere;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.firebase.FirebaseError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {
    private static LocationManager locationManager;
    private static User currentUser = new User("Jeff");
    private static DatabaseReference ref;
    private static DatabaseReference ref1;
    private static final int FINE_LOCATION_PERMISSION = 1;
    private static ArrayList<User> users = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_PERMISSION);
        }
        else{
            setLocationmanager();
        }
        DBClient.writeToUser(currentUser);

        FloatingActionButton getUser = (FloatingActionButton) findViewById(R.id.getUsers);
        getUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref1 = DBClient.db.getReference("users/");
                users = new ArrayList<User>();
                Query query = ref1.orderByChild("name").equalTo("Jeff");
                ((TextView) findViewById(R.id.textView)).setText("");
                query.addChildEventListener(new ChildEventListener() { //modify this to add users to a static list, and display the list each time a user is added/removed
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        //Do something with the individual node here`enter code here`
                        users.add(dataSnapshot.getValue(User.class));
                        displayUsers();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) { //bug where updating a user does not remove it
                        users.remove(dataSnapshot.getValue(User.class)); //this line doesn't work, need to remove by id or update things into users
                        users.add(dataSnapshot.getValue(User.class));
                        displayUsers();
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        users.remove(dataSnapshot.getValue(User.class));
                        displayUsers();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        displayUsers();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("QUERY", "CANCELLED");
                    }
                });
            }

        });

        Button changeSong = (Button)findViewById(R.id.changeSong);
        changeSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser.setCurrentSong(currentUser.getCurrentSong()+"f");
                DBClient.writeToUser(currentUser);
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLocationmanager();
                } else {
                  //disable location management
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void displayUsers(){
        ((TextView) findViewById(R.id.textView)).setText("");
        for(User user: users) {
            ((TextView) findViewById(R.id.textView)).append(user.getName() + " is listening to " + user.getCurrentSong() + "\n");
        }
    }

    public void setLocationmanager() throws SecurityException {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation = Login.locationManager.getLastKnownLocation(Login.locationManager.GPS_PROVIDER);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                currentUser.setLocation(location.getLongitude(), location.getLatitude());
                displayUsers();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                displayUsers();
            }

            public void onProviderEnabled(String provider) {
                displayUsers();
            }

            public void onProviderDisabled(String provider) {
                ((TextView) findViewById(R.id.textView)).setText("");
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500, 0, locationListener);

        if(ref == null) {
            ref = DBClient.db.getReference("users/" + currentUser.getId());

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class); //instead of this, get the user from the db and modify
                    DBClient.writeToUser(user);
                    ((TextView) findViewById(R.id.textView)).setText("");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    ((TextView) findViewById(R.id.textView)).setText("ERROR");
                    Log.d("ERROR: ", "CANCEL USER READ");
                }
            });
        }
    }

}
