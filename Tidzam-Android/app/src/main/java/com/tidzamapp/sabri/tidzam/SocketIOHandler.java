package com.tidzamapp.sabri.tidzam;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Georges on 12/12/2017.
 */

public class SocketIOHandler extends AsyncTask<String, Void, Void>
{
    private static SocketIOHandler instance;
    public MapActivity activity;
    private String SOCKET_URL = "//tidzam.media.mit.edu";
    private Socket socket;
    private List<Device> devices;
    private ArrayList<Predicitions> predicitionsArrayList;
    private ArrayList<Device> tidmarshdevice = SplashScreen.getInstence().getDevicetidmarsh();


    SocketIOHandler(MapActivity activity)
    {
        this.activity = activity;
    }

    public static SocketIOHandler getInstance() {
        return instance;
    }

    public static void setInstance(SocketIOHandler instance) {
        SocketIOHandler.instance = instance;
    }

    public ArrayList<Predicitions> getPredicitionsArrayList() {
        return predicitionsArrayList;
    }

    public void setPredicitionsArrayList(ArrayList<Predicitions> predicitionsArrayList) {
        this.predicitionsArrayList = predicitionsArrayList;
    }

    @Override
    protected void onPreExecute() {
        Log.d("georges", "OnPreExecuteCalled");
        predicitionsArrayList = new ArrayList<Predicitions>();
        try
        {
            socket = IO.socket(SOCKET_URL);
        }
        catch(Exception e)
        {
            Log.d("georges", e.getMessage());
        }

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("georges", "connected to tidzam socket");
            }

        }).on("sys", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                try
                {
                    Object json = null;
                    JSONArray jsonArray;
                    try
                    {
                        json = new JSONTokener(args[0].toString()).nextValue();
                    }
                    catch (JSONException e)
                    {
                       Log.d("georges", e.getMessage());
                    }
                    jsonArray = (JSONArray) json;
                    ArrayList<Predicitions> provisoire = new ArrayList<Predicitions>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //Log.d("georges", args[i].toString());
                        JSONObject data = jsonArray.getJSONObject( i );
                        JSONObject analysis = data.getJSONObject( "analysis" );
                        final String deviceName = data.getString( "chan" ).toString();
                        String result = analysis.getString( "result" ).toString();
                        JSONObject predicitions = analysis.optJSONObject( "predicitions" );
                        provisoire.add( new Predicitions( deviceName, predicitions ) );
                        // formatting of the string
                        result = result.replace( "[", "" );
                        result = result.replace( "]", "" );
                        result = result.replace( "\"", "" );
                        devices = activity.getDevices();
                        Log.i( "debug", deviceName );
                        Log.d( "demo", deviceName + " icon needs to be set to :" + result);
                        for (int j = 0; j < devices.size(); j++) {
                            if (deviceName.equals( devices.get( j ).getName() )) {
                                if (result.equals( "airplane" )) {
                                    devices.get( j ).setSpecie( "airplane" );
                                    devices.get( j ).setBmp( BitmapDescriptorFactory.fromResource( R.mipmap.airplane ) );
                                }
                                if (result.equals( "no_signal" )) {
                                    devices.get( j ).setSpecie( "no_signal" );
                                    devices.get( j ).setBmp( BitmapDescriptorFactory.fromResource( R.mipmap.no_signal ) );
                                }
                                if (result.equals( "unknown" )) {
                                    devices.get( j ).setSpecie( "unknown" );
                                    devices.get( j ).setBmp( BitmapDescriptorFactory.fromResource( R.mipmap.unknown ) );
                                }
                                if (result.equals( "birds" )) {
                                    devices.get( j ).setSpecie( "birds" );
                                    devices.get( j ).setBmp( BitmapDescriptorFactory.fromResource( R.mipmap.birds ) );
                                }
                                if (result.equals( "rain" )) {
                                    devices.get( j ).setSpecie( "rain" );
                                    devices.get( j ).setBmp( BitmapDescriptorFactory.fromResource( R.mipmap.rain ) );
                                }
                                if (result.equals( "wind" )) {
                                    devices.get( j ).setSpecie( "wind" );
                                    devices.get( j ).setBmp( BitmapDescriptorFactory.fromResource( R.mipmap.wind ) );
                                }
                                if(result.equals( "crickets" ))
                                {
                                    devices.get( j ).setSpecie( "crickets" );
                                    devices.get( j ).setBmp( BitmapDescriptorFactory.fromResource( R.mipmap.crickets ) );
                                }
                                if(result.equals( "mic_crackle" ))
                                {
                                    devices.get( j ).setSpecie( "mic_crackle" );
                                    devices.get( j ).setBmp( BitmapDescriptorFactory.fromResource( R.mipmap.mic_crackle ) );
                                }
                                if (result.equals( "cicadas" )) {
                                    devices.get( j ).setSpecie( "cicadas" );
                                    devices.get( j ).setBmp( BitmapDescriptorFactory.fromResource( R.mipmap.cicadas ) );
                                }
                                if (result.equals( "frog" )) {
                                    devices.get( j ).setSpecie( "frog" );
                                    devices.get( j ).setBmp( BitmapDescriptorFactory.fromResource( R.mipmap.frog ) );
                                }
                                if (result.equals( "quiet" )) {
                                    devices.get( j ).setSpecie( "quiet" );
                                    devices.get( j ).setBmp( BitmapDescriptorFactory.fromResource( R.drawable.ic_micro_maps ) );
                                }
                            }

                        }
                        if (activity.updatemap == true) {
                            activity.runOnUiThread( new Runnable() {
                                @Override
                                public void run() {
                                    GoogleMap map = activity.getmMap();
                                    map.clear();
                                    for (int i = 0; i < devices.size(); i++) {
                                        if (devices.get( i ).getSpecie() == "airplane") {
                                            try {
                                                map.addMarker( new MarkerOptions().position( new LatLng
                                                        ( devices.get( i ).getLatitude(), devices.get( i ).getLongitude() ) ).title
                                                        ( devices.get( i ).getName() ).icon( BitmapDescriptorFactory.fromResource( R.mipmap.airplane ) ) );
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else if (devices.get( i ).getSpecie() == "no_signal") {
                                            try {
                                                map.addMarker( new MarkerOptions().position( new LatLng
                                                        ( devices.get( i ).getLatitude(), devices.get( i ).getLongitude() ) ).title
                                                        ( devices.get( i ).getName() ).icon( BitmapDescriptorFactory.fromResource( R.mipmap.no_signal ) ) );
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else if (devices.get( i ).getSpecie() == "unknown") {
                                            try {
                                                map.addMarker( new MarkerOptions().position( new LatLng
                                                        ( devices.get( i ).getLatitude(), devices.get( i ).getLongitude() ) ).title
                                                        ( devices.get( i ).getName() ).icon( BitmapDescriptorFactory.fromResource( R.drawable.ic_micro_maps ) ) );
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else if (devices.get( i ).getSpecie() == "rain") {
                                            try {
                                                map.addMarker( new MarkerOptions().position( new LatLng
                                                        ( devices.get( i ).getLatitude(), devices.get( i ).getLongitude() ) ).title
                                                        ( devices.get( i ).getName() ).icon( BitmapDescriptorFactory.fromResource( R.mipmap.rain ) ) );
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else if (devices.get( i ).getSpecie() == "wind") {
                                            try {
                                                map.addMarker( new MarkerOptions().position( new LatLng
                                                        ( devices.get( i ).getLatitude(), devices.get( i ).getLongitude() ) ).title
                                                        ( devices.get( i ).getName() ).icon( BitmapDescriptorFactory.fromResource( R.mipmap.wind ) ) );
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else if (devices.get( i ).getSpecie() == "birds") {
                                            try {
                                                map.addMarker( new MarkerOptions().position( new LatLng
                                                        ( devices.get( i ).getLatitude(), devices.get( i ).getLongitude() ) ).title
                                                        ( devices.get( i ).getName() ).icon( BitmapDescriptorFactory.fromResource( R.mipmap.birds ) ) );
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        else if (devices.get( i ).getSpecie() == "crickets") {
                                            try {
                                                map.addMarker( new MarkerOptions().position( new LatLng
                                                        ( devices.get( i ).getLatitude(), devices.get( i ).getLongitude() ) ).title
                                                        ( devices.get( i ).getName() ).icon( BitmapDescriptorFactory.fromResource( R.mipmap.crickets ) ) );
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        else if (devices.get( i ).getSpecie() == "mic_crackle") {
                                            try {
                                                map.addMarker( new MarkerOptions().position( new LatLng
                                                        ( devices.get( i ).getLatitude(), devices.get( i ).getLongitude() ) ).title
                                                        ( devices.get( i ).getName() ).icon( BitmapDescriptorFactory.fromResource( R.mipmap.mic_crackle ) ) );
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else if (devices.get( i ).getSpecie() == "cicadas") {
                                            try {
                                                map.addMarker( new MarkerOptions().position( new LatLng
                                                        ( devices.get( i ).getLatitude(), devices.get( i ).getLongitude() ) ).title
                                                        ( devices.get( i ).getName() ).icon( BitmapDescriptorFactory.fromResource( R.mipmap.cicadas ) ) );
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else if (devices.get( i ).getSpecie() == "frog") {
                                            try {
                                                map.addMarker( new MarkerOptions().position( new LatLng
                                                        ( devices.get( i ).getLatitude(), devices.get( i ).getLongitude() ) ).title
                                                        ( devices.get( i ).getName() ).icon( BitmapDescriptorFactory.fromResource( R.mipmap.frog ) ) );
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else if (devices.get( i ).getSpecie() == "quiet") {
                                            try {
                                                map.addMarker( new MarkerOptions().position( new LatLng
                                                        ( devices.get( i ).getLatitude(), devices.get( i ).getLongitude() ) ).title
                                                        ( devices.get( i ).getName() ).icon( BitmapDescriptorFactory.fromResource( R.drawable.ic_micro_maps ) ) );
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else if (devices.get( i ).getSpecie() == "disconnected") {
                                            try {
                                                map.addMarker( new MarkerOptions().position( new LatLng
                                                        ( devices.get( i ).getLatitude(), devices.get( i ).getLongitude() ) ).title
                                                        ( devices.get( i ).getName() ).icon( BitmapDescriptorFactory.fromResource( R.drawable.micro_disconnected ) ) );
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    //Log.i("debug",devices.size()+"");
                                    activity.setmMap( map );
                                }
                            } );
                            //Log.d( "georges", "reussit" );
                        }
                    }
                    predicitionsArrayList = provisoire;
                    activity.setPredicitionsArrayList( predicitionsArrayList );
                    // Log.i( "debug", activity.getPredicitionsArrayList().size() + "" );
                    // Log.i( "debug", predicitionsArrayList.size() + "" );
                }
                catch(Exception e)
                {
                    Log.d( "georges sys error : ", e.getMessage() );
                }
                if(MapActivity.getMapActivity().isClose()==true)
                {
                    socket.close();
                    activity.runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            GoogleMap map = activity.getmMap();
                            map.clear();
                            for (int i = 0; i < tidmarshdevice.size(); i++) {
                                try {
                                    map.addMarker( new MarkerOptions().position( new LatLng
                                            ( tidmarshdevice.get( i ).getLatitude(), tidmarshdevice.get( i ).getLongitude() ) ).title
                                            ( tidmarshdevice.get( i ).getName() ).icon( BitmapDescriptorFactory.fromResource( R.mipmap.ic_info ) ) );
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            activity.setmMap( map );
                        }
                    } );
                }
            }

        }).on( Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("georges", "disconnected from tidzam socket io");
                socket.close();
            }

        });
        socket.connect();
        Log.d("georges", "trying to connect");
    }
    @Override
    protected Void doInBackground(String... urls)
    {
        return null;
    }
}