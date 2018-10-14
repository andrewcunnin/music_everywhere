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
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {
    private static LocationManager locationManager;
    private static User currentUser = new User("Somebody");
    private static DatabaseReference ref;
    private static final int FINE_LOCATION_PERMISSION = 1;
    private static ArrayList<User> users = new ArrayList<User>();
    private static final String CLIENT_ID = "168b4446175b4585abf5f0366d0c727a";
    private static final String REDIRECT_URI = "music-everywhere://callback";
    private SpotifyAppRemote mSpotifyAppRemote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //check permission for fine location, requests if not granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_PERMISSION);
        } else {
            setLocationmanager(); //sets location manager if granted
        }
        DBClient.writeToUser(currentUser);

        //sets up ref to user database
        ref = DBClient.db.getReference("users/");
        users = new ArrayList<User>();
        Query query = ref.orderByChild("name"); //temp query proof of concept for getting other users from db

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
                displayUsers(dataSnapshot.getValue(User.class), false);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                displayUsers(dataSnapshot.getValue(User.class), true);
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

    @Override //callback function to set location manager when permission granted
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
                    //TODO
                }
                return;
            }
        }
    }

    public void displayUsers() { //print all users in textview
        ((TextView) findViewById(R.id.textView)).setText("");
        for (User user : users) {
            ((TextView) findViewById(R.id.textView)).append(user.getName() + " is listening to " + user.getCurrentSong() + "\n");
        }
    }

    //if a user needs to be updated, pass in to update or user to be removed
    public void displayUsers(User toUpdate, Boolean remove) {
        ((TextView) findViewById(R.id.textView)).setText("");
        String id = toUpdate.getId();
        User curUser;
        for (int i = 0; i < users.size(); i++) {
            curUser = users.get(i);
            if (curUser.getId().equals(id)) {
                if(remove){
                    users.remove(i);
                }
                else {
                    users.set(i, toUpdate);
                    curUser = users.get(i);
                }
            }
            ((TextView) findViewById(R.id.textView)).append(curUser.getName() + " is listening to " + curUser.getCurrentSong() + "\n\n");
        }
    }

    //sets location manager to get locational updates
    public void setLocationmanager() throws SecurityException {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation = Login.locationManager.getLastKnownLocation(Login.locationManager.GPS_PROVIDER);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                currentUser.setLocation(location.getLongitude(), location.getLatitude()); //should write these changes instead of displaying users
                DBClient.writeToUser(currentUser);
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
    }

    //Spotify Connection
    @Override
    protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.CONNECTOR.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    @Override
    protected void onStop() {
        DBClient.db.getReference("users/" + currentUser.getId()).removeValue();
        super.onStop();
        SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
    }

    private void connected() {
        // Play a playlist
        // mSpotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState().setEventCallback(new Subscription.EventCallback<PlayerState>() {

            public void onEvent(PlayerState playerState) {
                final Track track = playerState.track;
                if (track != null) {
                    currentUser.setCurrentSong(track.name + " by " + track.artist.name + " from the album " + track.album.name);
                    DBClient.writeToUser(currentUser);
                }
            }
        });

    }
}


