package com.example.andrew.musiceverywhere;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
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

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {
    private static LocationManager locationmanager;
    private static User currentUser = new User("Jeff");
    private static DatabaseReference ref;
    private static ArrayList<User> users = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);

        FloatingActionButton writeUser = (FloatingActionButton) findViewById(R.id.writeUser);
        writeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                DBClient.writeToUser(currentUser);

                if(ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location lastKnownLocation = Login.locationmanager.getLastKnownLocation(Login.locationmanager.GPS_PROVIDER);
                    currentUser.setLocation(lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude());
                    if(ref == null) {
                        ref = DBClient.db.getReference("users/" + currentUser.getId());

                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                ((TextView) findViewById(R.id.textView)).setText(user.getName());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("ERROR: ", "CANCEL USER READ");
                            }
                        });
                    }
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

                ref = DBClient.db.getReference("users/");
                users = new ArrayList<User>();
                Query query = ref.orderByChild("name").equalTo("Jeff");
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
            }

        });
        /*FloatingActionButton getUser = (FloatingActionButton) findViewById(R.id.getUsers);
        getUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref = DBClient.db.getReference("users/");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ((TextView) findViewById(R.id.textView)).setText("");
                        for(DataSnapshot u: dataSnapshot.getChildren()){
                            User user = u.getValue(User.class);
                            ((TextView) findViewById(R.id.textView)).setText(((TextView)findViewById(R.id.textView)).getText() + user.getName() + "\n");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("ERROR: ", "CANCEL USER READ");
                    }
                });

            }
        });*/
    }

    public void displayUsers(){
        ((TextView) findViewById(R.id.textView)).setText("");
        for(User user: users){
            ((TextView) findViewById(R.id.textView)).append(user.getName() + " is listening to " + user.getCurrentSong() + "\n");
        }
    }

}
