package com.tidzamapp.sabri.tidzam;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Sabri on 20/12/2017.
 */

public class SocketIOAudio extends AsyncTask<String, Void, Void> {

    private String SOCKET_URL = "//tidzam.media.mit.edu";
    private Socket socket;
    private TidmarshLikeShazam activity;
    private JSONObject object;
    private JSONObject subobject;

    public SocketIOAudio() {
        object = new JSONObject();
        subobject = new JSONObject();
        try {
            subobject.put( "add_livestream", true );
            object.put( "sys", subobject );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected Void doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onPreExecute() {
        Log.d("georges", "OnPreExecuteCalled");
        try
        {
            socket = IO.socket( SOCKET_URL );
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("georges", "connected to tidzam socket");
            }
        } ).emit( "sys", object ).on( "portname", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Object json = null;
                JSONArray jsonArray = new JSONArray();
                try {
                    json = new JSONTokener( args[0].toString() ).nextValue();
                    Log.i( "georges", "contenu" + json );
                    jsonArray = (JSONArray) json;
                } catch (JSONException e) {
                    Log.d( "georges", e.getMessage() );
                }
            }
        });
        socket.connect();
        Log.d( "georges", "trying to connect" );
        Log.i( "georges", object.toString() );
    }
}
