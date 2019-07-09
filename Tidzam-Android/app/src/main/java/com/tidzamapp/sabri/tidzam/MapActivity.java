package com.tidzamapp.sabri.tidzam;

import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sabri on 05/11/2017.
 */


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener
{
    public static MapActivity mapActivity;
    public JSONArray deviceArray;
    public String lat;
    public String lng;
    public boolean updatemap;
    public ArrayList<Species> speciesArrayList;
    public JSONArray species;
    private boolean close;
    private SocketIOHandler socketIOHandler;
    private boolean activesocket;
    private GoogleMap mMap;
    private ParseJson parseJ;
    private int periodselect;
    private List<Device> devices;
    private Marker markerdevice;
    private String nom;
    private ArrayList<String> devicegps;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private ArrayList<Predicitions> predicitionsArrayList;
    private MediaPlayer player;
    private String dateA;
    private String dateB;
    private ArrayList<Sensors> tidmarshsensors;
    private NavigationView navigationView;
    private ArrayList<Device> devicetidmarsh;
    public MapActivity() {
        mapActivity = this;
    }

    public static MapActivity getMapActivity() {
        return mapActivity;
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public boolean isClose() {
        return close;
    }

    public void setUpdatemap(boolean updatemap) {
        this.updatemap = updatemap;
    }

    public SocketIOHandler getSocketIOHandler() {
        return socketIOHandler;
    }

    public void setSocketIOHandler(SocketIOHandler socketIOHandler) {
        this.socketIOHandler = socketIOHandler;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public int getPeriodselect() {
        return periodselect;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public Marker getMarkerdevice() {
        return markerdevice;
    }

    public String getNom() {
        return nom;
    }

    public ArrayList<Predicitions> getPredicitionsArrayList() {
        return predicitionsArrayList;
    }

    public void setPredicitionsArrayList(ArrayList<Predicitions> predicitionsArrayList) {
        this.predicitionsArrayList = predicitionsArrayList;
    }

    public ArrayList<Species> getSpeciesArrayList() {
        return speciesArrayList;
    }

    public void setSpeciesArrayList(ArrayList<Species> speciesArrayList) {
        this.speciesArrayList = speciesArrayList;
    }

    public JSONArray getSpecies() {
        return species;
    }

    public String getDateA() {
        return dateA;
    }

    public String getDateB() {
        return dateB;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        deviceArray = new JSONArray();
        devices = new ArrayList<Device>();
        devicetidmarsh = SplashScreen.getInstence().getDevicetidmarsh();
        close=false;
        lat = "";
        lng = "";
        nom = "";
        dateA="";
        dateB="";
        activesocket = true;
        socketIOHandler = new SocketIOHandler(MapActivity.this);
        navigationView = (NavigationView) findViewById( R.id.navviewmap );
        setupDrawerContent( navigationView );
        speciesArrayList = new ArrayList<Species>();
        devicegps = new ArrayList<String>();
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        predicitionsArrayList = new ArrayList<Predicitions>();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        tidmarshsensors = SplashScreen.getInstence().getSensorstidmarsh();
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mapFragment.getMapAsync(this);
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {togglePlay(mediaPlayer);
            }
        });
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem( menuItem );
                        return true;
                    }
                } );
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.maptidzam:
                close = false;
                socketIOHandler.cancel( true );
                SocketIOHandler socketIOHandler = new SocketIOHandler( MapActivity.this );
                socketIOHandler.execute();
                updatemap = true;
                break;
            case R.id.tidzamstats:
                startActivity( new Intent( MapActivity.this, StatsActivity.class ) );
                break;
            case R.id.databasetidzam:
                startActivity( new Intent( MapActivity.this, SampleDatabaseActivity.class ) );
                break;
            case R.id.livedetectiontidzam:
                Toast.makeText( MapActivity.this, "Comming Soon", Toast.LENGTH_LONG ).show();
                break;
            case R.id.tidmarshmapnav:
                close = true;
                break;
            case R.id.tidmarshcompare:
                startActivity( new Intent( MapActivity.this, TidmarshSite.class ) );
                break;
            case R.id.geolocalisation:
                break;
        }
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked( true );
        // Set action bar title
        setTitle( menuItem.getTitle() );
        // Close the navigation drawer
        mDrawerLayout.closeDrawers();
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void togglePlay(MediaPlayer mediaPlayer) {
        final AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        final int originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        } else {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
                }
            });
        }
    }

    private void preparesong(final Marker marker)
    {
        nom = marker.getTitle();
        String Stream = marker.getTitle().replace(':','-');
        String url = "http://tidzam.media.mit.edu:8000/"+Stream+".ogg";
        Log.i("debug",url);
        player.reset();
        try {
            player.setDataSource(url);
            player.prepareAsync();
        }
        catch(Exception e)
        {
            Log.e("ERROR", e.getMessage());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        LatLng tidmarsh = new LatLng( 41.900445, -70.5703417 );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tidmarsh));
        mMap.setMinZoomPreference( 15.5f );
        mMap.setOnMarkerClickListener(this);
        parseJ = new ParseJson();
        parseJ.execute();
    }

    private void initializeMarkers(JSONArray links)
    {
        try
        {
            //loop to just keep the interesting devices and fetch information for each of them
            for (int i = 0; i < links.length(); i++)
            {
                    //we add the devices we are looking for in the devices list
                    String deviceName = links.getJSONObject(i).getString("title");
                    String deviceHref = links.getJSONObject(i).getString("href");
                    Device newDevice = new Device(deviceName, deviceHref);
                    Log.d("georges", "Adding new device called "+newDevice.getName());
                    InfoDevice infoJson = new InfoDevice(newDevice);
                    infoJson.execute(  deviceHref );

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i("marker",marker.getTitle());
        if (marker.getTitle().contains( "0x" )) {
            markerdevice = marker;
            startActivity( new Intent( MapActivity.this, TidmarshDeviceActivity.class ) );
        } else
        {
            MapActivity.getMapActivity().setUpdatemap( false );
            markerdevice=marker;
            nom = markerdevice.getTitle();
            preparesong( marker );
            Intent speciestatbydevice = new Intent( MapActivity.this,StatsDeviceSpecies.class );
            startActivity( speciestatbydevice );
            return true;
        }
        return false;
    }

    public class ParseJson extends AsyncTask<String, Void,JSONArray>
    {
        @Override
        protected void onPreExecute()
        {
        }

        @Override
        protected JSONArray doInBackground(String... urls)
        {
            JSONArray pp = new JSONArray();
            HttpHandler sh = new HttpHandler();
            String url = "http://chain-api.media.mit.edu/devices/?limit=1000&site_id=18&offset=0";
            String jsonStr = sh.makeServiceCall(url);
            Log.d("json", jsonStr);
            if (jsonStr != null)
            {
                try
                {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray links = jsonObj.getJSONObject("_links").getJSONArray("items");
                    Log.d("json", String.valueOf(links.length()));
                    deviceArray = links;
                    return links;
                }
                catch (Exception e)
                {
                    e.printStackTrace();

                }
            }
            return pp;
        }
        @Override
        protected void onPostExecute(JSONArray links)
        {
            try
            {
                Log.d("json", "flag is now true");
                initializeMarkers(links); //function called once deviseArray is filled by the request
            }
            catch (Exception e)
            {

            }
        }
    }


    public class InfoDevice extends AsyncTask<String,Void,String[]>
    {
        private Device device;

        InfoDevice(Device myDevise)
        {
            this.device = myDevise;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String[] doInBackground(String... urls)
        {
            String[] coordonnees = {"",""};
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(urls[0]);
            if (jsonStr != null)
            {
                try
                {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    coordonnees[0] = jsonObj.getJSONObject("geoLocation").getString("latitude");
                    coordonnees[1] = jsonObj.getJSONObject("geoLocation").getString("longitude");
                    return coordonnees;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            return coordonnees;
        }
        @Override
        protected void onPostExecute(String[] coords)
        {
            if(!"".equals(coords[0]) && !"".equals(coords[1]))
            {
                //formatting the coordinates
                Double latitude = Double.parseDouble(coords[0]);
                Double longitude =  Double.parseDouble(coords[1]);
                LatLng formattedCoords = new LatLng(latitude,longitude);
                this.device.setLatitude(latitude);
                this.device.setLongitude(longitude);
                this.device.setSpecie( "disconnected" );
                BitmapDescriptor image = BitmapDescriptorFactory.fromResource(R.drawable.ic_micro_maps);
                MarkerOptions marker = new MarkerOptions();
                marker.position(formattedCoords);
                marker.title(device.getName());
                marker.icon( image );
                this.device.setMarker(marker);
                devices.add(device);
                mMap.addMarker(this.device.getMarker());
                mMap.addMarker( new MarkerOptions().position( formattedCoords ).icon( BitmapDescriptorFactory.fromResource( R.drawable.ic_next ) ).title( " " ) );
                try{

                    Log.d("georgesdebug", this.device.getName());
                }
                catch(Exception e)
                {
                    Log.d("georgesdebug", e.getMessage());
                }
            }
            else
            {
                Log.e("Error", "No coordinates were retrieved from the request.");
            }
            if (activesocket)
            {
                socketIOHandler.executeOnExecutor( THREAD_POOL_EXECUTOR );
                updatemap=true;
                activesocket = false;
            }
        }
    }
}