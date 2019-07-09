package com.tidzamapp.sabri.tidzam;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mukesh.tinydb.TinyDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sabri on 13/12/2017.
 */

public class SplashScreen extends Activity {

    public static SplashScreen instence;
    private static int splash_out  = 6000;
    private List<Sensors> listsensors;

    private ArrayList<String> listdevice;
    private int ppp;
    private TinyDB tinyDB;
    private ArrayList<Device> devicetidmarsh;
    private ArrayList<TidmarshItemValide> valides;
    private ArrayList<Sensors> sensorstidmarsh;

    public SplashScreen() {
        instence = this;
    }

    public static SplashScreen getInstence() {
        return instence;
    }

    public ArrayList<Device> getDevicetidmarsh() {
        return devicetidmarsh;
    }

    public List<Sensors> getListsensors() {
        return listsensors;
    }

    public ArrayList<Sensors> getSensorstidmarsh() {
        return sensorstidmarsh;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.ecrandedemarrage );
        tinyDB = new TinyDB( SplashScreen.this );
        listdevice = new ArrayList<String>();
        listsensors = new ArrayList<Sensors>();
        valides = new ArrayList<TidmarshItemValide>();
        devicetidmarsh = new ArrayList<Device>();
        //tinyDB.remove( "tidmarshsensors" );
        ThreadParseJson TPJ = new ThreadParseJson();
        TidmarshItems tidmarshItems = new TidmarshItems();
        sensorstidmarsh = new ArrayList<Sensors>();
        if (tinyDB.getString( "bon" ).length() > 0)
        {
            Log.i( "debug", "taille du shared preferences " + tinyDB.getString( "bon" ) );
            try {
                JSONArray jsonObj = new JSONArray( tinyDB.getString( "bon" ) );
                Log.i( "debug", "taille du shared preferences " + jsonObj.length() );
                for (int i = 0; i < jsonObj.length(); i++) {
                    String url = jsonObj.getJSONObject( i ).getString( "href" );
                    String name = jsonObj.getJSONObject( i ).getString( "name" );
                    String device = jsonObj.getJSONObject( i ).getString( "device" );
                    listsensors.add( new Sensors( url, name, device ) );
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i( "debug", "shared " + tinyDB.getString( "tidmarshsensors" ) );
            if (tinyDB.getString( "tidmarshsensors" ).length() > 0) {
                try {
                    JSONArray jsontid = new JSONArray( tinyDB.getString( "tidmarshsensors" ) );
                    Log.i( "debug", "taille du shared preferences " + jsontid.length() );
                    for (int i = 0; i < jsontid.length(); i++) {
                        String url = jsontid.getJSONObject( i ).getString( "href" );
                        Log.i( "debug", "shared " + url );
                        String name = jsontid.getJSONObject( i ).getString( "name" );
                        Log.i( "debug", "shared " + url );
                        String device = jsontid.getJSONObject( i ).getString( "device" );
                        Log.i( "debug", "shared " + url );
                        double lat = jsontid.getJSONObject( i ).getDouble( "lat" );
                        Log.i( "debug", "shared " + url );
                        double lng = jsontid.getJSONObject( i ).getDouble( "lng" );
                        Log.i( "debug", "shared " + name + " " + url + " " + lat + " " + lng );
                        sensorstidmarsh.add( new Sensors( url, name, device, lat, lng ) );
                    }
                    JSONArray jsondev = new JSONArray( tinyDB.getString( "tidmarshdevice" ) );
                    Log.i( "debug", "taille du shared preferences " + jsondev.toString() );
                    for (int i = 0; i < jsondev.length(); i++) {
                        String name = jsondev.getJSONObject( i ).getString( "name" );
                        double lat = jsondev.getJSONObject( i ).getDouble( "latitude" );
                        double lng = jsondev.getJSONObject( i ).getDouble( "longitude" );
                        Log.i( "debug", "shared " + name + " " + lat + " " + lng );
                        devicetidmarsh.add( new Device( name, lat, lng ) );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    tidmarshItems.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );
                } else {
                    tidmarshItems.execute();
                }
            }


            new Handler().postDelayed( new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent( SplashScreen.this, MapActivity.class );
                    startActivity( i );
                    finish();
                }
            }, 2000 );
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                TPJ.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );
            } else {
                TPJ.execute();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                tidmarshItems.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );
            } else {
                tidmarshItems.execute();
            }
            new Handler().postDelayed( new Runnable() {
                @Override
                public void run() {
                    ppp = 0;
                    Intent i = new Intent( SplashScreen.this, MapActivity.class );
                    startActivity( i );
                    finish();
                }
            }, 10000 );
        }
    }

    private void ThreadinitializeSensors(JSONArray links) {
        try {
            int jj = 0;
            //loop to just keep the interesting devices and fetch information for each of them
            for (int i = 0; i < links.length(); i++) {
                    String deviceHref = links.getJSONObject( i ).getString( "href" );
                    String val[] = deviceHref.split( "devices/" );
                    String url = "http://chain-api.media.mit.edu/sensors/?device_id=" + val[1];
                    listdevice.add( links.getJSONObject( i ).getString( "title" ) );
                    SpeciesDevice sensorsdevice = new SpeciesDevice();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        sensorsdevice.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR, url );
                    } else {
                        sensorsdevice.execute( url );
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeSpecies(JSONArray links) {
        for (int i = 0; i < links.length(); i++) {
            try {
                String[] ter = links.getJSONObject( i ).getString( "href" ).split( "ors/" );
                String urlfinal = "http://chain-api.media.mit.edu/aggregate_data/?sensor_id=" + ter[1];
                listsensors.add( new Sensors( urlfinal, links.getJSONObject( i ).getString( "title" ), listdevice.get( ppp ) ) );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ppp++;
        Log.i( "debug", "shared " + listsensors.size() );
        String key = "bon";
        Gson gson = new GsonBuilder().create();
        String arrayListToJson = gson.toJson( listsensors );
        tinyDB.putString( key, arrayListToJson );
    }

    private void initializeTidmarshItems(JSONArray links) {
        try {
            int jj = 0;
            //loop to just keep the interesting devices and fetch information for each of them
            for (int i = 0; i < links.length(); i++) {
                Log.i( "debug", "shared " + links.getJSONObject( i ).getString( "title" ) );
                if (links.getJSONObject( i ).getString( "title" ).contains( "0x" )) {
                    String deviceHref = links.getJSONObject( i ).getString( "href" );
                    String url = deviceHref;
                    TidmarshSensorsValide sensorsdevice = new TidmarshSensorsValide();
                    Log.i( "debug", "shared url " + url );
                    sensorsdevice.execute( url );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ThreadParseJson extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected JSONArray doInBackground(String... urls) {
            JSONArray pp = new JSONArray();
            HttpHandler sh = new HttpHandler();
            String url = "http://chain-api.media.mit.edu/devices/?limit=1000&site_id=18&offset=0";
            String jsonStr = sh.makeServiceCall( url );
            Log.d( "json", jsonStr );
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject( jsonStr );
                    JSONArray links = jsonObj.getJSONObject( "_links" ).getJSONArray( "items" );
                    Log.d( "json", String.valueOf( links.length() ) );
                    return links;
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
            return pp;
        }

        @Override
        protected void onPostExecute(JSONArray links) {
            try {
                Log.d( "json", "flag is now true" );
                ThreadinitializeSensors( links );
                //function called once deviseArray is filled by the request
            } catch (Exception e) {
            }
        }
    }

    public class SpeciesDevice extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected JSONArray doInBackground(String... urls) {
            JSONArray r = new JSONArray();
            HttpHandler sh = new HttpHandler();
            String url = urls[0];
            String jsonStr = sh.makeServiceCall( url );
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject( jsonStr );
                    Log.i( "debug", jsonStr );
                    JSONArray spe = jsonObj.getJSONObject( "_links" ).getJSONArray( "items" );
                    return spe;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return r;
        }

        @Override
        protected void onPostExecute(JSONArray links) {
            Log.i( "debug", "Initialisation des especes" );
            initializeSpecies( links );
        }
    }

    public class TidmarshItems extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected JSONArray doInBackground(String... strings) {
            JSONArray pp = new JSONArray();
            HttpHandler sh = new HttpHandler();
            String url = "http://chain-api.media.mit.edu/devices/?limit=30000&site_id=7&offset=0";
            String jsonStr = sh.makeServiceCall( url );
            Log.d( "json", jsonStr );
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject( jsonStr );
                    JSONArray links = jsonObj.getJSONObject( "_links" ).getJSONArray( "items" );
                    Log.d( "debug", "taille du json Tidmarsh" + String.valueOf( links.length() ) );
                    return links;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return pp;
        }

        @Override
        protected void onPostExecute(JSONArray links) {
            Log.i( "debug", "Initialisation des especes" );
            initializeTidmarshItems( links );
        }
    }

    public class TidmarshSensorsValide extends AsyncTask<String, Void, TidmarshItemValide> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected TidmarshItemValide doInBackground(String... urls) {
            TidmarshItemValide valide = new TidmarshItemValide( "", "", 0, 0 );
            HttpHandler sh = new HttpHandler();
            String url = urls[0];
            String jsonStr = sh.makeServiceCall( url );
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject( jsonStr );
                    if (jsonObj.getJSONObject( "geoLocation" ).getString( "latitude" ).length() > 0 && jsonObj.getJSONObject( "geoLocation" ).getString( "longitude" ).length() > 0
                            && jsonObj.getString( "active" ).equals( "true" )) {
                        Log.i( "debug", "shared entrer" );
                        String name = jsonObj.getString( "name" );
                        String href = jsonObj.getJSONObject( "_links" ).getJSONObject( "ch:sensors" ).getString( "href" );
                        double lat = jsonObj.getJSONObject( "geoLocation" ).getDouble( "latitude" );
                        double lng = jsonObj.getJSONObject( "geoLocation" ).getDouble( "longitude" );
                        TidmarshItemValide valide1 = new TidmarshItemValide( name, href, lat, lng );
                        Log.i( "debug", "shared " + name + " " + href + " " + lat + " " + lng );
                        return valide1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return valide;
        }

        @Override
        protected void onPostExecute(TidmarshItemValide links) {
            if (links.getName().length() > 0) {
                valides.add( links );
                tinyDB.putInt( "tidmarshlistitem", valides.size() );
                devicetidmarsh.add( new Device( links.getName(), links.getLatitude(), links.getLongitude() ) );
                Log.i( "debug", "shared " + devicetidmarsh.size() );
                TidmarshItemsSensors ses = new TidmarshItemsSensors();
                Gson gson = new GsonBuilder().create();
                String arrayListToJson = gson.toJson( devicetidmarsh );
                tinyDB.putString( "tidmarshdevice", arrayListToJson );
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    ses.executeOnExecutor( THREAD_POOL_EXECUTOR, links.getHref(), links.getName(), String.valueOf( links.getLatitude() ), String.valueOf( links.getLongitude() ) );
                } else {
                    ses.execute( links.getHref(), links.getName(), String.valueOf( links.getLatitude() ), String.valueOf( links.getLongitude() ) );
                }
            }
        }
    }

    public class TidmarshItemsSensors extends AsyncTask<String, Void, ArrayList<Sensors>> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected ArrayList<Sensors> doInBackground(String... urls) {
            ArrayList<Sensors> valide = new ArrayList<Sensors>();
            HttpHandler sh = new HttpHandler();
            String url = urls[0];
            String jsonStr = sh.makeServiceCall( url );
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject( jsonStr );
                    Log.i( "debug", "shared " + jsonObj.toString() );
                    ArrayList<Sensors> valides = new ArrayList<Sensors>();
                    JSONArray jsonArray = jsonObj.getJSONObject( "_links" ).getJSONArray( "items" );
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String name = jsonArray.getJSONObject( i ).getString( "title" );
                        String href = jsonArray.getJSONObject( i ).getString( "href" );
                        valides.add( new Sensors( href, name, urls[1], Double.parseDouble( urls[2] ), Double.parseDouble( urls[3] ) ) );
                    }
                    return valides;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return valide;
        }

        @Override
        protected void onPostExecute(ArrayList<Sensors> links) {
            for (int i = 0; i < links.size(); i++) {
                sensorstidmarsh.add( links.get( i ) );
            }
            Log.i( "debug", "shared taille des sensors tidmarsh" + sensorstidmarsh.size() );
            Gson gson = new GsonBuilder().create();
            String arrayListToJson = gson.toJson( sensorstidmarsh );
            tinyDB.putString( "tidmarshsensors", arrayListToJson );
        }
    }
}
