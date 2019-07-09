package com.tidzamapp.sabri.tidzam;

import android.os.AsyncTask;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

/**
 * Created by Sabri on 21/01/2018.
 */

public class WebSocketImageStream extends AsyncTask<Void, String, Void> {

    private URI uri;

    @Override
    protected void onPreExecute() {
        try {
            uri = new URI( "wss://tidmarsh.media.mit.edu/cam/ws" );
        } catch (Exception e) {
            Log.i( "debug", "fail" );
            e.printStackTrace();
        }
        try {
            WebSocketClient client = new WebSocketClient( uri ) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {

                }

                @Override
                public void onMessage(String message) {
                    JSONObject json = null;
                    try {
                        json = new JSONObject( message );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i( "debug", json.toString() );
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {

                }

                @Override
                public void onError(Exception ex) {

                }
            };
            client.connect();
        } catch (Exception e) {
            Log.i( "debug", "fail" );
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }
}
