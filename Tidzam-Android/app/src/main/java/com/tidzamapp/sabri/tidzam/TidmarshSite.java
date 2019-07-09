package com.tidzamapp.sabri.tidzam;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Sabri on 21/01/2018.
 */

public class TidmarshSite extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private TextView day;
    private TextView period;
    private TextView date;
    private GoogleMap map;
    private TextView site;
    private TextView category;
    private LineChart lineChart;
    private ArrayList<Sensors> tidmarshsensors;
    private String[] siteintidmarsh = {"Impoundment", "Cell 3", "GreenHouse"};
    private String[] unities = {"hPa", "celsius", "celsius", "%", "lux", "celsius"};
    private String[] mesures = {"pressure", "temperature sht", "temperature bmp", "humidity", "illuminance", "water temperature"};
    private String[] name = {"pressure", "sht_temperature", "bmp_temperature", "sht_humidity", "illuminance", "water_temperature"};
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<String[]> chosedevice;
    private int[] colors = {Color.MAGENTA, Color.GREEN, Color.BLUE, Color.YELLOW, Color.RED, Color.rgb( 104, 34, 139 ), Color.rgb( 139, 69, 19 ), Color.BLACK, Color.GRAY};
    private String[] site1 = {"0x816F", "0x8179", "0x817A", "0x817C", "0x817F", "0x8183", "0x8189", "0x818C", "0x8196"};
    private String[] site2 = {"0x812A", "0x8133", "0x8141", "0x8152", "0x8167"};
    private String[] site3 = {"0x8119", "0x812C"};
    private ArrayList<Device> devices = SplashScreen.getInstence().getDevicetidmarsh();
    private String[] periode = {"All of time", "Year", "Month", "Week", "Day"};
    private String dateB;
    private String[] mois = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};
    private String[] daynumber = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
            "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    private int weeknumber;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_tidmarshsite );
        category = (TextView) findViewById( R.id.categorymesure );
        category.setText( "pressure" );
        chosedevice = new ArrayList<String[]>();
        chosedevice.add( site1 );
        chosedevice.add( site2 );
        chosedevice.add( site3 );
        getSupportActionBar().setTitle( getTitle().toString() );
        day = (TextView) findViewById( R.id.tidmarshsiteday );
        final ArrayList<String> selectsite = new ArrayList<String>();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.maptidmarsh );
        lineChart = (LineChart) findViewById( R.id.tidmarshsitelinechart );
        tidmarshsensors = SplashScreen.getInstence().getSensorstidmarsh();
        period = (TextView) findViewById( R.id.tidmarshsiteperiod );
        period.setText( "All of time" );
        period.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder( TidmarshSite.this );
                builder.setTitle( "Choose a period" ).setItems( periode, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        period.setText( periode[which] );
                        date.setText( "" );
                        if (period.getText().toString().equals( "All of time" )) {
                            lineChart.removeAllViews();
                            setupData();
                            find( selectsite );
                        }
                    }
                } );
                builder.create();
                builder.show();
            }
        } );
        date = (TextView) findViewById( R.id.tidmarshsitedate );
        date.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (period.getText().toString().equals( "Day" )) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get( Calendar.YEAR );
                    int mMonth = c.get( Calendar.MONTH );
                    int mDay = c.get( Calendar.DAY_OF_MONTH );
                    final DatePickerDialog adate = new DatePickerDialog( TidmarshSite.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            dateB = "" + year + "-";
                            if (month <= 9) {
                                dateB += "0";
                            }
                            dateB += (month + 1) + "-";
                            if (dayOfMonth <= 9) {
                                dateB += "0";
                            }
                            dateB += dayOfMonth;
                            Log.i( "debug", "Date B  :" + dateB );
                            date.setText( dateB );
                            runOnUiThread( new Runnable() {
                                @Override
                                public void run() {
                                    lineChart.removeAllViewsInLayout();
                                    day.setText( "" );
                                    setupData();
                                    find( selectsite );
                                }
                            } );
                        }
                    }, mYear, mMonth, mDay );
                    adate.show();
                }
                if (period.getText().toString().equals( "Month" )) {
                    final String[] period = {"January", "February", "March", "April", "May", "June", "July",
                            "August", "September", "October", "November", "December"};
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( TidmarshSite.this );
                    builder.setTitle( "Choose a month" )
                            .setItems( period, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    date.setText( period[which] );
                                    final String[] period1 = {"2017", "2018"};
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( TidmarshSite.this );
                                    builder.setTitle( "Choose a year" )
                                            .setItems( period1, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    date.setText( date.getText().toString() + " " + period1[which] );
                                                    runOnUiThread( new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            lineChart.removeAllViewsInLayout();
                                                            setupData();
                                                            find( selectsite );
                                                        }
                                                    } );
                                                }
                                            } );
                                    builder.create();
                                    builder.show();
                                }
                            } );
                    builder.create();
                    builder.show();
                }
                if (period.getText().toString().equals( "Week" )) {
                    final String[] period1 = {"2017", "2018"};
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( TidmarshSite.this );
                    builder.setTitle( "Choose a year" )
                            .setItems( period1, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    date.setText( period1[which] );
                                    final String[] period = {"January", "February", "March", "April", "May", "June", "July",
                                            "August", "September", "October", "November", "December"};
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( TidmarshSite.this );
                                    builder.setTitle( "Choose a month" )
                                            .setItems( period, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    date.setText( date.getText().toString() + " " + period[which] );
                                                    final int monthh = which + 1;
                                                    final String[] week = {"(1/" + monthh + " -> 7/" + monthh + ")",
                                                            "(8/" + monthh + " -> 14/" + monthh + ")",
                                                            "(15/" + monthh + " -> 21/" + monthh + ")",
                                                            "(22/" + monthh + " -> 28/" + monthh + ")",
                                                            "(29/" + monthh + " -> end of month"};
                                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( TidmarshSite.this );
                                                    builder.setTitle( "Choose a week" )
                                                            .setItems( week, new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, final int which) {
                                                                    date.setText( date.getText().toString() + " " + week[which] );
                                                                    weeknumber = which;
                                                                    runOnUiThread( new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            lineChart.removeAllViewsInLayout();
                                                                            setupData();
                                                                            find( selectsite );
                                                                        }
                                                                    } );
                                                                }
                                                            } );
                                                    builder.create();
                                                    builder.show();
                                                }
                                            } );
                                    builder.create();
                                    builder.show();
                                }
                            } );
                    builder.create();
                    builder.show();
                }
                if (period.getText().toString().equals( "Year" )) {
                    final String[] period1 = {"2017", "2018"};
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( TidmarshSite.this );
                    builder.setTitle( "Choose a year" )
                            .setItems( period1, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    date.setText( period1[which] );
                                    runOnUiThread( new Runnable() {
                                        @Override
                                        public void run() {
                                            lineChart.removeAllViewsInLayout();
                                            setupData();
                                            find( selectsite );
                                        }
                                    } );
                                }
                            } );
                    builder.create();
                    builder.show();
                }
            }
        } );
        category.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder( TidmarshSite.this );
                builder.setTitle( "Info" ).setItems( mesures, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        category.setText( mesures[which] );
                        for (int i = 0; i < selectsite.size(); i++) {
                            Log.i( "debug", selectsite.get( i ) );
                        }
                        lineChart.removeAllViewsInLayout();
                        setupData();
                        day.setText( "" );
                        find( selectsite );
                    }
                } );
                builder.create();
                builder.show();
            }
        } );
        site = (TextView) findViewById( R.id.sitetidmarsh );
        site.setText( siteintidmarsh[0] );
        site.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder( TidmarshSite.this );
                builder.setTitle( "Site" ).setItems( siteintidmarsh, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        site.setText( siteintidmarsh[which] );
                        final AlertDialog.Builder builder1 = new AlertDialog.Builder( TidmarshSite.this );
                        final ArrayList<Integer> choosesiteforgraph = new ArrayList<Integer>();
                        builder1.setTitle( "Device" ).setMultiChoiceItems( chosedevice.get( which ), null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                {
                                    if (isChecked) {
                                        // If the user checked the item, add it to the selected items
                                        choosesiteforgraph.add( which );
                                    } else if (choosesiteforgraph.contains( which )) {
                                        // Else, if the item is already in the array, remove it
                                        choosesiteforgraph.remove( Integer.valueOf( which ) );
                                    }
                                }
                            }
                        } ).setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectsite.clear();
                                for (int i = 0; i < choosesiteforgraph.size(); i++) {
                                    selectsite.add( site1[choosesiteforgraph.get( i )] );
                                }
                                lineChart.removeAllViewsInLayout();
                                setupData();
                                find( selectsite );
                                day.setText( "" );
                                runOnUiThread( new Runnable() {
                                    @Override
                                    public void run() {
                                        map.clear();
                                        for (int j = 0; j < selectsite.size(); j++) {
                                            for (int i = 0; i < devices.size(); i++) {
                                                if (devices.get( i ).getName().equals( selectsite.get( j ) )) {
                                                    map.addMarker( new MarkerOptions().position( new LatLng
                                                            ( devices.get( i ).getLatitude(), devices.get( i ).getLongitude() ) ).title
                                                            ( devices.get( i ).getName() ).icon( BitmapDescriptorFactory.fromResource( R.mipmap.ic_info ) ) );
                                                    map.moveCamera( CameraUpdateFactory.newLatLng( new LatLng( devices.get( i ).getLatitude(), devices.get( i ).getLongitude() ) ) );
                                                    map.setMinZoomPreference( 17.0f );
                                                } else {
                                                    Bitmap bitmap = BitmapFactory.decodeResource( getResources(), R.drawable.info );
                                                    BitmapDrawable drawable = new BitmapDrawable( getResources(), bitmap );
                                                    drawable.setAlpha( 100 );
                                                    map.addMarker( new MarkerOptions().position( new LatLng
                                                            ( devices.get( i ).getLatitude(), devices.get( i ).getLongitude() ) ).title
                                                            ( devices.get( i ).getName() ).icon( BitmapDescriptorFactory.fromBitmap( bitmap ) ).alpha( 0.25f ) );
                                                }
                                            }
                                        }
                                    }
                                } );
                            }
                        } ).setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        } );
                        builder1.create();
                        builder1.show();
                    }
                } );
                builder.create();
                builder.show();
            }
        } );
        mapFragment.getMapAsync( this );
        selectsite.clear();
    }

    public void find(final ArrayList<String> sitetid) {
        for (int j = 0; j < sitetid.size(); j++) {
            final int l = j;
            new Thread( new Runnable() {
                @Override
                public void run() {
                    int index = 0;
                    for (int i = 0; i < mesures.length; i++) {
                        if (category.getText().toString() == mesures[i]) {
                            index = i;
                        }
                    }
                    boolean findd = false;
                    int indexoftid = 0;
                    for (int i = 0; i < tidmarshsensors.size(); i++) {
                        if (tidmarshsensors.get( i ).getDevice().equals( sitetid.get( l ) )
                                && tidmarshsensors.get( i ).getName().contains( name[index] )) {
                            findd = true;
                            indexoftid = i;
                        }
                    }
                    final int jj = l;
                    final int indexx = indexoftid;
                    if (findd == true) {
                        new Thread( new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread( new Runnable() {
                                    @Override
                                    public void run() {
                                        ValueTidmarsh valueTidmarsh = new ValueTidmarsh();
                                        valueTidmarsh.execute( tidmarshsensors.get( indexx ).getHref(), Integer.toString( jj ), tidmarshsensors.get( indexx ).getDevice() );
                                        Log.i( "debug", "OKI " + Integer.toString( jj ) );
                                    }
                                } );
                            }
                        } ).start();
                    }
                }
            } ).start();
        }
    }

    private void setupData() {
        LineData data = new LineData();
        data.setValueTextColor( Color.BLACK );
        lineChart.setData( data );
    }

    private void buildgraphhour(final ArrayList<Data> donnees, final int index, final String label) {
        runOnUiThread( new Runnable() {
            @Override
            public void run() {
                Log.i( "debug", "OKI " );
                LineData data = lineChart.getData();
                if (data != null) {
                    ILineDataSet set = data.getDataSetByIndex( index );
                    if (set == null) {
                        set = CreateSet( label, colors[index] );
                        data.addDataSet( set );
                        set.setDrawValues( false );
                        set.isDrawCircleHoleEnabled();
                    }
                    DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss" );
                    int j = 0;
                    for (int i = 0; i < donnees.size(); i++) {
                        Date date = null;
                        try {
                            date = dateFormat.parse( donnees.get( i ).getDate().replace( "T", " " ).replace( "Z", "" ) );
                            long unixTime = (long) date.getTime() / 1000;
                            Log.i( "debug", "" + unixTime );
                            data.addEntry( new Entry( unixTime, (float) donnees.get( i ).getDonnees() ), index );
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    XAxis xl = lineChart.getXAxis();
                    xl.setTextColor( Color.BLACK );
                    xl.setDrawGridLines( false );
                    xl.setAvoidFirstLastClipping( true );
                    xl.setPosition( XAxis.XAxisPosition.BOTTOM );
                    xl.setGranularityEnabled( true );
                    xl.setEnabled( true );
                    xl.setValueFormatter( new IAxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            Date date = new Date( ((int) value) * 1000L );
                            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss z" );
                            String formattedDate = sdf.format( date );
                            sdf.setTimeZone( TimeZone.getTimeZone( "GMT-5" ) );
                            Log.i( "debug", formattedDate );
                            String finaldate = formattedDate.split( " " )[0];
                            finaldate = finaldate.split( "-" )[2] + "/" + finaldate.split( "-" )[1];
                            return finaldate;
                        }

                        @Override
                        public int getDecimalDigits() {
                            return 0;
                        }
                    } );
                    YAxis leftAxis = lineChart.getAxisLeft();
                    leftAxis.setTextColor( Color.BLACK );
                    leftAxis.setDrawGridLines( true );
                    YAxis rightAxis = lineChart.getAxisRight();
                    rightAxis.setEnabled( false );
                    // Add a limit line
                    // reset all limit lines to avoid overlapping lines
                    leftAxis.removeAllLimitLines();
                    // limit lines are drawn behind data (and not on top)
                    leftAxis.setDrawLimitLinesBehindData( true );
                    lineChart.getDescription().setEnabled( false );
                    lineChart.setTouchEnabled( true );
                    data.setDrawValues( false );
                    lineChart.setPinchZoom( true );
                    lineChart.setScaleEnabled( true );
                    lineChart.notifyDataSetChanged();
                    lineChart.getLegend().setEnabled( true );
                    lineChart.getLegend().setWordWrapEnabled( true );
                    lineChart.getLegend().setTextSize( 14f );
                    lineChart.getLegend().setPosition( Legend.LegendPosition.BELOW_CHART_CENTER );
                    data.notifyDataChanged();
                    lineChart.notifyDataSetChanged();
                    lineChart.animateY( 1500 );
                } else {
                    setupData();
                }
            }
        } );
    }


    private void Buildgraphbyperiod(final ArrayList<Data> data, final int index, final String label) {
        runOnUiThread( new Runnable() {
            @Override
            public void run() {
                if (period.getText().toString().equals( "All of time" )) {
                    lineChart.setVisibility( View.VISIBLE );
                    day.setVisibility( View.GONE );
                    buildgraphhour( data, index, label );
                } else if (period.getText().toString().equals( "Year" )) {
                    ArrayList<Data> data1 = new ArrayList<Data>();
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get( i ).getDate().contains( date.getText().toString() )) {
                            data1.add( data.get( i ) );
                        }
                    }
                    day.setVisibility( View.GONE );
                    lineChart.setVisibility( View.VISIBLE );
                    buildgraphhour( data1, index, label );
                } else if (period.getText().toString().equals( "Month" )) {
                    ArrayList<Data> data1 = new ArrayList<Data>();
                    String month = date.getText().toString().split( " " )[0];
                    String monthfinal = "";
                    for (int i = 0; i < mois.length; i++) {
                        if (month.equals( mois[i] )) {
                            if (i + 1 <= 9) {
                                monthfinal = "0" + String.valueOf( i + 1 );
                            } else {
                                monthfinal = String.valueOf( i + 1 );
                            }
                        }
                    }
                    String datefinal = date.getText().toString().split( " " )[1] + "-" + monthfinal;
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get( i ).getDate().contains( datefinal )) {
                            data1.add( data.get( i ) );
                        }
                    }
                    day.setVisibility( View.GONE );
                    lineChart.setVisibility( View.VISIBLE );
                    buildgraphhour( data1, index, label );
                } else if (period.getText().toString().equals( "Week" )) {
                    ArrayList<Data> data1 = new ArrayList<Data>();
                    ArrayList<String> datefinale = new ArrayList<String>();
                    String year = date.getText().toString().split( " " )[0];
                    String moiss = "";
                    for (int i = 0; i < mois.length; i++) {
                        if (mois[i].contains( date.getText().toString().split( " " )[1] )) {
                            if (i + 1 <= 9) {
                                moiss = "0" + String.valueOf( i + 1 );
                            } else {
                                moiss = String.valueOf( i + 1 );
                            }
                        }
                    }
                    for (int i = weeknumber * 7; i < ((weeknumber + 1) * 7) && i < daynumber.length; i++) {
                        datefinale.add( year + "-" + moiss + "-" + daynumber[i] );
                        Log.i( "debug", year + "-" + moiss + daynumber[i] );
                    }
                    int pas = 0;
                    for (int i = 0; i < data.size(); i++) {
                        if (pas < datefinale.size()) {
                            if (data.get( i ).getDate().contains( datefinale.get( pas ) )) {
                                data1.add( data.get( i ) );
                                pas++;
                            }
                        }
                    }
                    day.setVisibility( View.GONE );
                    lineChart.setVisibility( View.VISIBLE );
                    buildgraphhour( data1, index, label );
                } else if (period.getText().toString().equals( "Day" )) {
                    day.setGravity( View.TEXT_ALIGNMENT_CENTER );
                    day.setVisibility( View.VISIBLE );
                    lineChart.setVisibility( View.GONE );
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get( i ).getDate().contains( date.getText().toString() )) {
                            String date = data.get( i ).getDate();
                            date = date.split( "Z" )[0];
                            String jour = date.split( "T" )[0];
                            String heure = date.split( "T" )[1];
                            jour = jour.split( "-" )[2] + "/" + jour.split( "-" )[1] + "/" + jour.split( "-" )[0];
                            heure = heure.split( ":" )[0] + ":" + heure.split( ":" )[1];
                            date = jour + " (" + heure + ")";
                            int indexx = 0;
                            for (int j = 0; j < mesures.length; j++) {
                                if (category.getText().toString() == name[j]) {
                                    indexx = j;
                                }
                            }
                            day.setText( day.getText() + "\n" + data.get( i ).getLabel() + " : " + category.getText().toString() + " : " + data.get( i ).getDonnees() + " " + unities[indexx] + "  at " + date );
                            day.setTextSize( 15f );
                        }
                    }
                }
            }
        } );
    }

    private ILineDataSet CreateSet(String label, int color) {
        LineDataSet set = new LineDataSet( null, label );
        set.setAxisDependency( YAxis.AxisDependency.LEFT );
        set.setColors( color );
        // To show values of each point
        set.setDrawValues( false );
        set.setDrawCircles( false );
        return set;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType( GoogleMap.MAP_TYPE_SATELLITE );
        LatLng tidmarsh = new LatLng( 41.900445, -70.5703417 );
        map.moveCamera( CameraUpdateFactory.newLatLng( tidmarsh ) );
        map.setMinZoomPreference( 17.0f );
        for (int i = 0; i < devices.size(); i++) {
            map.addMarker( new MarkerOptions().position( new LatLng
                    ( devices.get( i ).getLatitude(), devices.get( i ).getLongitude() ) ).title
                    ( devices.get( i ).getName() ).icon( BitmapDescriptorFactory.fromResource( R.mipmap.ic_info ) ) );
        }
        map.setOnMarkerClickListener( this );

    }

    public class ValueTidmarsh extends AsyncTask<String, Void, ArrayList<Data>> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected ArrayList<Data> doInBackground(String... urls) {
            ArrayList<Data> arrayList = new ArrayList<Data>();
            HttpHandler sh = new HttpHandler();
            String url = urls[0];
            String[] val = url.split( "sensors/" );
            String urlfinal = "http://chain-api.media.mit.edu/aggregate_data/?sensor_id=" + val[1] + "&aggtime=1d";
            Log.i( "debug", "tidmarsh url " + urlfinal );
            double count = 0;
            String jsonStr = sh.makeServiceCall( urlfinal );
            Log.i( "debug", jsonStr );
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject( jsonStr );
                    JSONArray jsonArray = jsonObj.getJSONArray( "data" );
                    for (int i = 0; i < jsonArray.length(); i++) {
                        double doub = jsonArray.getJSONObject( i ).getDouble( "max" );
                        String time = jsonArray.getJSONObject( i ).getString( "timestamp" );
                        Log.i( "debug", "" + doub );
                        arrayList.add( new Data( time, doub, Integer.parseInt( urls[1] ), urls[2] ) );
                    }
                    return arrayList;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Data> count) {
            Log.i( "debug", count.size() + "" );
            if (count.size() > 0) {
                Buildgraphbyperiod( count, count.get( 0 ).getIndex(), count.get( 0 ).getLabel() );
            } else {
                lineChart.setVisibility( View.GONE );
            }
        }
    }
}
