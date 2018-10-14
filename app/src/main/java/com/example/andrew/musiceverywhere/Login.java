package com.example.andrew.musiceverywhere;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
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
    public static ArrayAdapter listAdapter;
    public static ArrayList<String> songIds;
    public static ArrayList<User> listItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //HttpURLConnection urlConnection = null; to be implemented to get Spotify profiles
        //new SpotifyClient().execute("https://api.spotify.com/v1/me");

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

        ListView mainListView = (ListView) findViewById( R.id.mainListView );
        // Create ArrayAdapter using the planet list.
        listAdapter = new ArrayAdapter<User>(this, R.layout.simplerow, listItems);
        mainListView.setAdapter(listAdapter);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                playSong(((User)arg0.getItemAtPosition(arg2)).getTrackURI());
                Log.d("SONG: ", ((User)arg0.getItemAtPosition(arg2)).getTrackURI());
            }

        });

        listAdapter.clear();
        EditText editText = (EditText) findViewById(R.id.name);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                currentUser.setName(v.getText().toString());
                DBClient.writeToUser(currentUser);
                return true;
            }
        });


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
        listAdapter.clear();
        for (User user : users) {
            //listAdapter.add(user.getName() + " is listening to " + user.getCurrentSong() + "\n");
            //listAdapter.add(user.getCurrentSong());
            listAdapter.add(currentUser);
        }
    }

    //if a user needs to be updated, pass in to update or user to be removed
    public void displayUsers(User toUpdate, Boolean remove) {
        listAdapter.clear();
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
            //listAdapter.add(curUser.getName() + " is listening to " + curUser.getCurrentSong() + "\n\n");
            //listAdapter.add(curUser.getCurrentSong());
            listAdapter.add(currentUser);
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
                listAdapter.clear();
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

        // Do not connect to Spotify twice
        if (mSpotifyAppRemote != null && mSpotifyAppRemote.isConnected()) {
            SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
        }

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

    public void playSong(String uri){
        if(uri != null && !uri.equals("Nothing")) {
            mSpotifyAppRemote.getPlayerApi().play(uri);
        }
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
                    //currentUser.setCurrentSong(track.name + " by " + track.artist.name + " from the album " + track.album.name);
                    currentUser.setCurrentSong(track.name+ " by " + track.artist.name + " off of " + track.album.name, track.uri);
                    DBClient.writeToUser(currentUser);
                }
            }
        });

    }
    /*public class SpotifyClient extends AsyncTask<String, Void, Void> {

        private Exception exception;

        private String name;

        protected Void doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                URLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader request = new BufferedReader(new InputStreamReader(in));
                String line = request.readLine();
                while(line != null){
                    currentUser.setName(currentUser.getName() + line);
                    line = request.readLine();
                }
            } catch (Exception e) {
                this.exception = e;
            } finally {
            }
            return null;
        }


        protected void onPostExecute(Void result) {
            //currentUser.setName(this.name);
        }

    }*/

}


