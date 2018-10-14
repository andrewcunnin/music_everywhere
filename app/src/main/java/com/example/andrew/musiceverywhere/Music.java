package com.example.andrew.musiceverywhere;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.Empty;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.PlayerContext;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.spotify.protocol.types.Types;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Music extends AppCompatActivity {

    private String albumName;
    private String songURI;
    private static Album album;

    private static final String PLAYLIST_ID = "spotify:user:spotify:playlist:37i9dQZF1DX2sUQwD7tbmL";
    private static final String CLIENT_ID = "168b4446175b4585abf5f0366d0c727a";
    private static final String REDIRECT_URI = "music-everywhere://callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        //Music muse = new Music("pat", "https://open.spotify.com/album/1M4anG49aEs4YimBdj96Oy");

    }

    public Music(String name, String uri){
        albumName = name;
        songURI = uri;
        album = new Album(albumName,songURI);
    }

    public static String getAlbumName(){
        return album.name;
    }
    public static String getAlbumURI(){
        return album.uri;
    }

    public void retrieveImage(ImageUri IURI){
        mSpotifyAppRemote.getImagesApi().getImage(IURI);
    }
    public void playPlaylist(View view) {

        mSpotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
        //Types.RequestId requestId = mSpotifyAppRemote.getUserApi().subscribeToUserStatus().getRequestId();
    }

    public void resumeSong(View view){
        mSpotifyAppRemote.getPlayerApi().resume();
    }
    public void pauseSong(View view){
        mSpotifyAppRemote.getPlayerApi().pause();
    }
    public void skipNextSong(View view){
        mSpotifyAppRemote.getPlayerApi().skipNext();
    }
    public void skipPreviousSong(View view){
        mSpotifyAppRemote.getPlayerApi().skipPrevious();
    }


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

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    private void connected() {
        mSpotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");


        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState().setEventCallback(new Subscription.EventCallback<PlayerState>() {
            @Override
            public void onEvent(PlayerState playerState) {
                final Track track = playerState.track;
                if (track != null) {
                    Log.d("MainActivity", track.name + " by " + track.artist.name);
                }
            }
        });



        /*final PlayerApi pApi = new PlayerApi() {
            @Override
            public CallResult<Empty> play(String s) {
                return null;
            }

            @Override
            public CallResult<Empty> queue(String s) {
                return null;
            }

            @Override
            public CallResult<Empty> resume() {
                return null;
            }

            @Override
            public CallResult<Empty> pause() {
                return null;
            }

            @Override
            public CallResult<Empty> skipNext() {
                return pApi.skipNext();
            }

            @Override
            public CallResult<Empty> skipPrevious() {
                return null;
            }

            @Override
            public CallResult<Empty> setShuffle(boolean b) {
                return null;
            }

            @Override
            public CallResult<Empty> setRepeat(int i) {
                return null;
            }

            @Override
            public CallResult<Empty> seekTo(long l) {
                return null;
            }

            @Override
            public CallResult<PlayerState> getPlayerState() {
                return null;
            }

            @Override
            public Subscription<PlayerState> subscribeToPlayerState() {
                return null;
            }

            @Override
            public Subscription<PlayerContext> subscribeToPlayerContext() {
                return null;
            }
        }*/
    }


    @Override
    protected void onStop(){
        super.onStop();
        SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
    }
}