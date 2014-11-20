// TutorialApp
// Created by Spotify on 25/02/14.
// Copyright (c) 2014 Spotify. All rights reserved.
package com.example.hellis.jukebox;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;
import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.Player;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;
import com.spotify.sdk.android.playback.PlayerState;

public class MyActivity extends Activity implements
        PlayerNotificationCallback, ConnectionStateCallback {

    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "2ac2d3abf554449191f22390ccc227df";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "jukebox://callback";

    private Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        SpotifyAuthentication.openAuthWindow(CLIENT_ID, "token", REDIRECT_URI,
                new String[]{"user-read-private", "streaming"}, null, this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null) {
            AuthenticationResponse response = SpotifyAuthentication.parseOauthResponse(uri);
            Spotify spotify = new Spotify(response.getAccessToken());
            mPlayer = spotify.getPlayer(this, "My Company Name", this, new Player.InitializationObserver() {
                @Override
                public void onInitialized() {
                    mPlayer.addConnectionStateCallback(MyActivity.this);
                    mPlayer.addPlayerNotificationCallback(MyActivity.this);
                    mPlayer.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V");
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e("MyActivity", "Could not initialize player: " + throwable.getMessage());
                }
            });
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MyActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MyActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MyActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MyActivity", "Temporary error occurred");
    }

    @Override
    public void onNewCredentials(String s) {
        Log.d("MyActivity", "User credentials blob received");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MyActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("MyActivity", "Playback event received: " + eventType.name());
        switch (eventType) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("MyActivity", "Playback error received: " + errorType.name());
        switch (errorType) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }
}