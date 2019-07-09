package com.tidzamapp.sabri.tidzam;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mukesh.tinydb.TinyDB;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sabri Lachihab on 18/11/2017.
 */

public class StatsDeviceSpecies extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static StatsDeviceSpecies statsDeviceSpecies;
    public List<Sensors> listsensors;
    public ArrayList<Stat> species;
    public ArrayList<Stat> speciescount;
    public int birds;
    private GoogleMap mMap;
    private Marker markerdevice;
    private ProgressDialog progressDialog;
    private String specie;
    private List<Device> deviceList;
    private TextView chooseperiod;
    private TextView choosedate;
    private String nom;
    private int weeknumber;
    private String dateB;
    private BarChart graph;
    private String[] month = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};
    private String[] daynumber = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
            "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    private int[] colors = {
            Color.MAGENTA, Color.CYAN, Color.BLUE, Color.rgb( 0, 102, 51 ), Color.LTGRAY, Color.GRAY,
            Color.rgb( 255, 140, 0 ), Color.GREEN, Color.RED};
    private String[] simplemonth = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July",
            "Aug", "Sept", "Oct", "Nov", "Dec"};
    private String[] namespecies = {"crickets", "rain", "birds", "unknow",
            "wind", "airplane", "cicadas", "frog", "mic_crackle"};
    private String[] annee = {"2017", "2018"};
    private RecyclerView recycler;
    private Legend_Recycler leg_recycler;
    private TinyDB tinyDB;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private ArrayList<Sensors> tidmarsh;
    private ArrayList<Device> tiddevice;
    private MediaPlayer player;

    public StatsDeviceSpecies() {
        statsDeviceSpecies = this;
    }

    public static StatsDeviceSpecies getStatsDeviceSpecies()
    {
        return statsDeviceSpecies;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public Marker getMarkerdevice() {
        return markerdevice;
    }

    public TextView getChooseperiod() {
        return chooseperiod;
    }

    public TextView getChoosedate() {
        return choosedate;
    }

    public String getNom() {
        return nom;
    }

    public ArrayList<Stat> getSpeciescount() {
        return speciescount;
    }

    public int getBirds() {
        return birds;
    }

    public String getSpecie() {
        return specie;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statsbydevicebyspecies);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapdevice);
        listsensors = SplashScreen.getInstence().getListsensors();
        markerdevice = MapActivity.getMapActivity().getMarkerdevice();
        tinyDB = new TinyDB( StatsDeviceSpecies.this );
        graph = (BarChart) findViewById( R.id.graphgeneral );
        species = new ArrayList<Stat>();
        chooseperiod = (TextView) findViewById( R.id.periodchosit );
        choosedate = (TextView) findViewById( R.id.datechoisit );
        tiddevice = SplashScreen.getInstence().getDevicetidmarsh();
        tidmarsh = SplashScreen.getInstence().getSensorstidmarsh();
        player = MapActivity.getMapActivity().getPlayer();
        chooseperiod.setText( "All of time" );
        chooseperiod.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] period = {"Day", "Week", "Month", "Year", "All of time"};
                AlertDialog.Builder builder = new AlertDialog.Builder( StatsDeviceSpecies.this );
                builder.setTitle( "Choose a period" )
                        .setItems( period, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                chooseperiod.setText( period[which] );
                                choosedate.setText( "" );
                                if (which == 4) {
                                    runOnUiThread( new Runnable() {
                                        @Override
                                        public void run() {
                                            statsview( speciescount );
                                        }
                                    } );
                                }
                            }
                        } );
                builder.create();
                builder.show();
            }
        } );
        choosedate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chooseperiod.getText().toString().equals( "Day" )) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get( Calendar.YEAR );
                    int mMonth = c.get( Calendar.MONTH );
                    int mDay = c.get( Calendar.DAY_OF_MONTH );
                    final DatePickerDialog adate = new DatePickerDialog( StatsDeviceSpecies.this, new DatePickerDialog.OnDateSetListener() {
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
                            choosedate.setText( dateB );
                            runOnUiThread( new Runnable() {
                                @Override
                                public void run() {
                                    statsview( speciescount );
                                }
                            } );
                        }
                    }, mYear, mMonth, mDay );
                    adate.show();
                }
                if (chooseperiod.getText().toString().equals( "Month" )) {
                    AlertDialog.Builder builder = new AlertDialog.Builder( StatsDeviceSpecies.this );
                    builder.setTitle( "Choose a month" )
                            .setItems( month, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    choosedate.setText( month[which] );
                                    final String[] period1 = {"2017", "2018"};
                                    AlertDialog.Builder builder = new AlertDialog.Builder( StatsDeviceSpecies.this );
                                    builder.setTitle( "Choose a year" )
                                            .setItems( period1, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    choosedate.setText( choosedate.getText().toString() + " " + period1[which] );
                                                    runOnUiThread( new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            statsview( speciescount );
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
                if (chooseperiod.getText().toString().equals( "Week" )) {
                    final String[] period1 = {"2017", "2018"};
                    AlertDialog.Builder builder = new AlertDialog.Builder( StatsDeviceSpecies.this );
                    builder.setTitle( "Choose a year" )
                            .setItems( period1, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    choosedate.setText( period1[which] );
                                    final String[] period = {"January", "February", "March", "April", "May", "June", "July",
                                            "August", "September", "October", "November", "December"};
                                    AlertDialog.Builder builder = new AlertDialog.Builder( StatsDeviceSpecies.this );
                                    builder.setTitle( "Choose a month" )
                                            .setItems( period, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    choosedate.setText( choosedate.getText().toString() + " " + period[which] );
                                                    final int monthh = which + 1;
                                                    final String[] week = {"(1/" + monthh + " -> 7/" + monthh + ")",
                                                            "(8/" + monthh + " -> 14/" + monthh + ")",
                                                            "(15/" + monthh + " -> 21/" + monthh + ")",
                                                            "(22/" + monthh + " -> 28/" + monthh + ")",
                                                            "(29/" + monthh + " -> end of month"};
                                                    AlertDialog.Builder builder = new AlertDialog.Builder( StatsDeviceSpecies.this );
                                                    builder.setTitle( "Choose a week" )
                                                            .setItems( week, new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, final int which) {
                                                                    choosedate.setText( choosedate.getText().toString() + " " + week[which] );
                                                                    weeknumber = which;
                                                                    runOnUiThread( new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            statsview( speciescount );
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
                if (chooseperiod.getText().toString().equals( "Year" )) {
                    final String[] period1 = {"2017", "2018"};
                    AlertDialog.Builder builder = new AlertDialog.Builder( StatsDeviceSpecies.this );
                    builder.setTitle( "Choose a year" )
                            .setItems( period1, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    choosedate.setText( period1[which] );
                                    runOnUiThread( new Runnable() {
                                        @Override
                                        public void run() {
                                            statsview( speciescount );
                                        }
                                    } );
                                }
                            } );
                    builder.create();
                    builder.show();
                }
            }
        } );
        speciescount = new ArrayList<Stat>();
        deviceList = MapActivity.getMapActivity().getDevices();
        getSupportActionBar().setTitle( markerdevice.getTitle().replace( ":", " " ).replace( "_", " " ) );
        Log.i("debug",markerdevice.getTitle());
        Log.i("debug",listsensors.size()+"");
        Log.i( "debug", "shared contenu " + listsensors.toString() );
        Log.i( "debug", "shared contenu " + markerdevice.getTitle() );
        updatestatlist();
        mapFragment.getMapAsync( StatsDeviceSpecies.this );
        recycler = findViewById( R.id.recyclerlegdevice );
        recycler.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
        leg_recycler = new Legend_Recycler( getApplicationContext(), namespecies, colors, new Legend_Recycler.RecyclerItemClickListenerSpecies() {
            @Override
            public void onClickListener(String species, int position) {
                if (species.equals( "birds" )) {
                    startActivity( new Intent( StatsDeviceSpecies.this, StatBirdsSpecies.class ) );
                } else if (!species.equals( "unknow" )) {
                    specie = species;
                    startActivity( new Intent( StatsDeviceSpecies.this, WikipediaActivitySpeciesDevice.class ) );
                }
            }
        } );
        recycler.setAdapter( leg_recycler );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.action_music, menu );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pausemusic:
                if (player.isPlaying()) {
                    item.setIcon( R.drawable.playmusic );
                    player.stop();
                } else {
                    item.setIcon( R.drawable.pausemusic );
                    player.start();
                }
                return true;

            default:
                return super.onOptionsItemSelected( item );

        }
    }

    public void statsview(final ArrayList<Stat> stats) {
        runOnUiThread( new Runnable() {
                           @Override
                           public void run() {
                               if (chooseperiod.getText().toString().equals( "Day" )) {
                                   ArrayList<BarEntry> yvalues = new ArrayList<BarEntry>();
                                   yvalues.add( new BarEntry( 0, CountDay( choosedate.getText().toString() ), "day" ) );
                                   BarDataSet dataset = new BarDataSet( yvalues, "" );
                                   BarData theData = new BarData( dataset );
                                   dataset.setColors( colors );
                                   Legend leg = graph.getLegend();
                                   leg.setEnabled( false );
                                   dataset.setAxisDependency( YAxis.AxisDependency.LEFT );
                                   theData.setValueFormatter( new LargeValueFormatter() );
                                   XAxis xAxis = graph.getXAxis();
                                   xAxis.setDrawGridLines( false );
                                   xAxis.setValueFormatter( new IAxisValueFormatter() {
                                       @Override
                                       public String getFormattedValue(float value, AxisBase axis) {
                                           if ((int) value == 0) {
                                               return choosedate.getText().toString();
                                           }
                                           return "";
                                       }

                                       @Override
                                       public int getDecimalDigits() {
                                           return 0;
                                       }
                                   } );
                                   dataset.setDrawValues( false );
                                   graph.notifyDataSetChanged();
                                   xAxis.setPosition( XAxis.XAxisPosition.BOTTOM );
                                   xAxis.setGranularityEnabled( true );
                                   graph.setData( theData );
                                   YAxis leftAxis = graph.getAxisLeft();
                                   YAxis rAxis = graph.getAxisRight();
                                   rAxis.setEnabled( false );
                                   leftAxis.removeAllLimitLines();
                                   graph.setDescription( null );
                                   graph.invalidate();
                                   graph.setHorizontalScrollBarEnabled( true );
                                   graph.animateY( 4000 );
                               }
                               if (chooseperiod.getText().toString().equals( "All of time" )) {
                                   ArrayList<BarEntry> yvalues = new ArrayList<BarEntry>();
                                   yvalues.add( new BarEntry( 0, CountbyYear( "2017" ), "2017" ) );
                                   yvalues.add( new BarEntry( 1, CountbyYear( "2018" ), "2018" ) );
                                   BarDataSet dataset = new BarDataSet( yvalues, "" );
                                   BarData theData = new BarData( dataset );
                                   dataset.setColors( colors );
                                   Legend leg = graph.getLegend();
                                   leg.setEnabled( false );
                                   dataset.setAxisDependency( YAxis.AxisDependency.LEFT );
                                   theData.setValueFormatter( new LargeValueFormatter() );
                                   XAxis xAxis = graph.getXAxis();
                                   xAxis.setDrawGridLines( false );
                                   xAxis.setValueFormatter( new IAxisValueFormatter() {
                                       @Override
                                       public String getFormattedValue(float value, AxisBase axis) {
                                           if ((int) value == 0) {
                                               return "2017";
                                           }
                                           if ((int) value == 1) {
                                               return "2018";
                                           }
                                           return "";
                                       }

                                       @Override
                                       public int getDecimalDigits() {
                                           return 0;
                                       }
                                   } );
                                   dataset.setDrawValues( false );
                                   graph.notifyDataSetChanged();
                                   xAxis.setPosition( XAxis.XAxisPosition.BOTTOM );
                                   xAxis.setGranularityEnabled( true );
                                   graph.setData( theData );
                                   YAxis leftAxis = graph.getAxisLeft();
                                   YAxis rAxis = graph.getAxisRight();
                                   rAxis.setEnabled( false );
                                   leftAxis.removeAllLimitLines();
                                   graph.setDescription( null );
                                   graph.invalidate();
                                   graph.setHorizontalScrollBarEnabled( true );
                                   graph.animateY( 4000 );
                                   graph.setOnChartValueSelectedListener( new OnChartValueSelectedListener() {
                                       @Override
                                       public void onValueSelected(Entry e, Highlight h) {
                                           chooseperiod.setText( "Year" );
                                           choosedate.setText( annee[(int) e.getX()] );
                                           graph.removeAllViewsInLayout();
                                           graph.removeAllViews();
                                           runOnUiThread( new Runnable() {
                                               @Override
                                               public void run() {
                                                   statsview( stats );
                                               }
                                           } );
                                       }

                                       @Override
                                       public void onNothingSelected() {

                                       }
                                   } );
                               }
                               if (chooseperiod.getText().toString().equals( "Year" )) {
                                   ArrayList<BarEntry> yvalues = new ArrayList<BarEntry>();
                                   String year = choosedate.getText().toString() + "-";
                                   final String Annee = choosedate.getText().toString();
                                   yvalues.add( new BarEntry( 0, CountbyMonth( year + "01" ) ) );
                                   yvalues.add( new BarEntry( 1, CountbyMonth( year + "02" ) ) );
                                   yvalues.add( new BarEntry( 2, CountbyMonth( year + "03" ) ) );
                                   yvalues.add( new BarEntry( 3, CountbyMonth( year + "04" ) ) );
                                   yvalues.add( new BarEntry( 4, CountbyMonth( year + "05" ) ) );
                                   yvalues.add( new BarEntry( 5, CountbyMonth( year + "06" ) ) );
                                   yvalues.add( new BarEntry( 6, CountbyMonth( year + "07" ) ) );
                                   yvalues.add( new BarEntry( 7, CountbyMonth( year + "08" ) ) );
                                   yvalues.add( new BarEntry( 8, CountbyMonth( year + "09" ) ) );
                                   yvalues.add( new BarEntry( 9, CountbyMonth( year + "10" ) ) );
                                   yvalues.add( new BarEntry( 10, CountbyMonth( year + "11" ) ) );
                                   yvalues.add( new BarEntry( 11, CountbyMonth( year + "12" ) ) );
                                   BarDataSet dataset = new BarDataSet( yvalues, "" );
                                   BarData theData = new BarData( dataset );
                                   dataset.setColors( colors );
                                   Legend leg = graph.getLegend();
                                   leg.setEnabled( false );
                                   dataset.setAxisDependency( YAxis.AxisDependency.LEFT );
                                   theData.setValueFormatter( new LargeValueFormatter() );
                                   XAxis xAxis = graph.getXAxis();
                                   xAxis.setDrawGridLines( false );
                                   xAxis.setValueFormatter( new IAxisValueFormatter() {
                                       @Override
                                       public String getFormattedValue(float value, AxisBase axis) {
                                           return simplemonth[(int) value];
                                       }

                                       @Override
                                       public int getDecimalDigits() {
                                           return 0;
                                       }
                                   } );
                                   dataset.setDrawValues( false );
                                   graph.notifyDataSetChanged();
                                   xAxis.setPosition( XAxis.XAxisPosition.BOTTOM );
                                   xAxis.setGranularityEnabled( true );
                                   graph.setData( theData );
                                   YAxis leftAxis = graph.getAxisLeft();
                                   YAxis rAxis = graph.getAxisRight();
                                   rAxis.setEnabled( false );
                                   leftAxis.removeAllLimitLines();
                                   graph.setDescription( null );
                                   graph.invalidate();
                                   graph.setHorizontalScrollBarEnabled( true );
                                   graph.animateY( 4000 );
                                   graph.setOnChartValueSelectedListener( new OnChartValueSelectedListener() {
                                       @Override
                                       public void onValueSelected(Entry e, Highlight h) {
                                           chooseperiod.setText( "Month" );
                                           choosedate.setText( month[(int) e.getX()] + " " + Annee );
                                           graph.removeAllViewsInLayout();
                                           graph.removeAllViews();
                                           runOnUiThread( new Runnable() {
                                               @Override
                                               public void run() {
                                                   statsview( stats );
                                               }
                                           } );
                                       }

                                       @Override
                                       public void onNothingSelected() {

                                       }
                                   } );
                               }
                               if (chooseperiod.getText().toString().equals( "Month" )) {
                                   ArrayList<BarEntry> yvalues = new ArrayList<BarEntry>();
                                   String[] date = choosedate.getText().toString().split( " " );
                                   String datefinale = date[1] + "-";
                                   for (int i = 0; i < month.length; i++) {
                                       if (date[0].equals( month[i] )) {
                                           datefinale += daynumber[i];
                                       }
                                   }
                                   final String daydate = datefinale;
                                   for (int i = 0; i < daynumber.length; i++) {
                                       yvalues.add( new BarEntry( i, CountDay( datefinale + "-" + daynumber[i] ) ) );
                                   }
                                   BarDataSet dataset = new BarDataSet( yvalues, "" );
                                   BarData theData = new BarData( dataset );
                                   dataset.setColors( colors );
                                   Legend leg = graph.getLegend();
                                   leg.setEnabled( false );
                                   dataset.setAxisDependency( YAxis.AxisDependency.LEFT );
                                   theData.setValueFormatter( new LargeValueFormatter() );
                                   XAxis xAxis = graph.getXAxis();
                                   xAxis.setDrawGridLines( false );
                                   xAxis.setValueFormatter( new IAxisValueFormatter() {
                                       @Override
                                       public String getFormattedValue(float value, AxisBase axis) {
                                           return "" + ((int) value + 1);
                                       }

                                       @Override
                                       public int getDecimalDigits() {
                                           return 0;
                                       }
                                   } );
                                   dataset.setDrawValues( false );
                                   graph.notifyDataSetChanged();
                                   xAxis.setPosition( XAxis.XAxisPosition.BOTTOM );
                                   xAxis.setGranularityEnabled( true );
                                   graph.setData( theData );
                                   YAxis leftAxis = graph.getAxisLeft();
                                   YAxis rAxis = graph.getAxisRight();
                                   rAxis.setEnabled( false );
                                   leftAxis.removeAllLimitLines();
                                   graph.setDescription( null );
                                   graph.invalidate();
                                   graph.setHorizontalScrollBarEnabled( true );
                                   graph.animateY( 4000 );
                                   graph.setOnChartValueSelectedListener( new OnChartValueSelectedListener() {
                                       @Override
                                       public void onValueSelected(Entry e, Highlight h) {
                                           chooseperiod.setText( "Day" );
                                           choosedate.setText( daydate + "-" + daynumber[(int) e.getX()] );
                                           graph.removeAllViewsInLayout();
                                           graph.removeAllViews();
                                           runOnUiThread( new Runnable() {
                                               @Override
                                               public void run() {
                                                   statsview( stats );
                                               }
                                           } );
                                       }

                                       @Override
                                       public void onNothingSelected() {

                                       }
                                   } );
                               }
                               if (chooseperiod.getText().toString().equals( "Week" )) {
                                   ArrayList<BarEntry> yvalues = new ArrayList<BarEntry>();
                                   String[] date = choosedate.getText().toString().split( " " );
                                   String datefinale = date[0] + "-";
                                   int moiss = 0;
                                   for (int i = 0; i < month.length; i++) {
                                       if (date[1].equals( month[i] )) {
                                           datefinale += daynumber[i];
                                           moiss = i;
                                       }
                                   }
                                   final int mois = moiss;
                                   final String daydate = datefinale;
                                   if (weeknumber < 4) {
                                       yvalues.add( new BarEntry( 0, CountbyDay( datefinale + "-" + daynumber[weeknumber * 7] ) ) );
                                       yvalues.add( new BarEntry( 1, CountbyDay( datefinale + "-" + daynumber[weeknumber * 7 + 1] ) ) );
                                       yvalues.add( new BarEntry( 2, CountbyDay( datefinale + "-" + daynumber[weeknumber * 7 + 2] ) ) );
                                       yvalues.add( new BarEntry( 3, CountbyDay( datefinale + "-" + daynumber[weeknumber * 7 + 3] ) ) );
                                       yvalues.add( new BarEntry( 4, CountbyDay( datefinale + "-" + daynumber[weeknumber * 7 + 4] ) ) );
                                       yvalues.add( new BarEntry( 5, CountbyDay( datefinale + "-" + daynumber[weeknumber * 7 + 5] ) ) );
                                       yvalues.add( new BarEntry( 6, CountbyDay( datefinale + "-" + daynumber[weeknumber * 7 + 6] ) ) );
                                   } else {
                                       yvalues.add( new BarEntry( 0, CountbyDay( datefinale + "-" + daynumber[weeknumber * 7] ) ) );
                                       yvalues.add( new BarEntry( 1, CountbyDay( datefinale + "-" + daynumber[weeknumber * 7 + 1] ) ) );
                                       yvalues.add( new BarEntry( 2, CountbyDay( datefinale + "-" + daynumber[weeknumber * 7 + 2] ) ) );
                                   }
                                   BarDataSet dataset = new BarDataSet( yvalues, "" );
                                   BarData theData = new BarData( dataset );
                                   dataset.setColors( colors );
                                   Legend leg = graph.getLegend();
                                   leg.setEnabled( false );
                                   dataset.setAxisDependency( YAxis.AxisDependency.LEFT );
                                   theData.setValueFormatter( new LargeValueFormatter() );
                                   XAxis xAxis = graph.getXAxis();
                                   xAxis.setDrawGridLines( false );
                                   xAxis.setValueFormatter( new IAxisValueFormatter() {
                                       @Override
                                       public String getFormattedValue(float value, AxisBase axis) {
                                           return (daynumber[((weeknumber * 7) + (int) value)]) + "/" + (mois + 1);
                                       }

                                       @Override
                                       public int getDecimalDigits() {
                                           return 0;
                                       }
                                   } );
                                   dataset.setDrawValues( false );
                                   xAxis.setPosition( XAxis.XAxisPosition.BOTTOM );
                                   xAxis.setGranularityEnabled( true );
                                   graph.setData( theData );
                                   graph.notifyDataSetChanged();
                                   YAxis leftAxis = graph.getAxisLeft();
                                   YAxis rAxis = graph.getAxisRight();
                                   rAxis.setEnabled( false );
                                   leftAxis.removeAllLimitLines();
                                   graph.setDescription( null );
                                   graph.invalidate();
                                   graph.setHorizontalScrollBarEnabled( true );
                                   graph.animateY( 4000 );
                                   graph.setOnChartValueSelectedListener( new OnChartValueSelectedListener() {
                                       @Override
                                       public void onValueSelected(Entry e, Highlight h) {
                                           chooseperiod.setText( "Day" );
                                           choosedate.setText( daydate + "-" + daynumber[(weeknumber * 7) + ((int) e.getX())] );
                                           graph.removeAllViewsInLayout();
                                           graph.removeAllViews();
                                           runOnUiThread( new Runnable() {
                                               @Override
                                               public void run() {
                                                   statsview( stats );
                                               }
                                           } );
                                       }

                                       @Override
                                       public void onNothingSelected() {

                                       }
                                   } );
                               }
                           }
                       }
        );
    }


    public void updatestatlist() {
        final boolean[] airplaneinliste = {false};
        new Thread( new Runnable() {
            @Override
            public void run() {
                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        progressDialog = ProgressDialog.show( StatsDeviceSpecies.this, markerdevice.getTitle().replace( ":", " " ).replace( "_", " " ), "Please Wait", true );
                        for (int i = 0; i < listsensors.size(); i++) {
                            if (listsensors.get( i ).getDevice().equals( markerdevice.getTitle() ) && listsensors.get( i ).getName().equals( "airplane" )) {
                                airplaneinliste[0] = true;
                                String urlfinal = listsensors.get( i ).getHref() + "&aggtime=1d";
                                Log.i( "debug", "shared " + urlfinal );
                                Updatelist getname = new Updatelist();
                                getname.execute( urlfinal );
                            }
                        }
                        if (airplaneinliste[0] == false) {
                            for (int i = 0; i < listsensors.size(); i++) {
                                Log.i( "debug", "shared" + listsensors.size() );
                                if (listsensors.get( i ).getDevice().equals( markerdevice.getTitle() )) {
                                    String urlfinal = listsensors.get( i ).getHref() + "&aggtime=1d";
                                    Log.i( "debug", "shared " + urlfinal );
                                    species.add( new Stat( listsensors.get( i ).getName(), new ArrayList<ArrayList<SensorsHistory>>() ) );
                                    ParseJsonstats getname = new ParseJsonstats();
                                    getname.execute( urlfinal, listsensors.get( i ).getName() );
                                }
                            }
                        }
                    }
                } );
            }
        } ).start();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mMap.setMapType( GoogleMap.MAP_TYPE_SATELLITE );
            BitmapDescriptor image = BitmapDescriptorFactory.fromResource( R.drawable.ic_micro_maps );
            for (int i = 0; i < deviceList.size(); i++) {
                if (deviceList.get( i ).getName().equals( markerdevice.getTitle() )) {
                    mMap.addMarker( new MarkerOptions().position( new LatLng
                            ( deviceList.get( i ).getLatitude(), deviceList.get( i ).getLongitude() ) ).title
                            ( deviceList.get( i ).getName() ).icon( deviceList.get( i ).getBmp() ) );
                } else {
                    mMap.addMarker( new MarkerOptions().position( new LatLng
                            ( deviceList.get( i ).getLatitude(), deviceList.get( i ).getLongitude() ) ).title
                            ( deviceList.get( i ).getName() ).icon( BitmapDescriptorFactory.fromResource( R.drawable.ic_micro_maps ) ) );
                }
            }
            LatLng device = new LatLng( markerdevice.getPosition().latitude, markerdevice.getPosition().longitude );
            MarkerOptions newMarker = new MarkerOptions().position( device ).title( markerdevice.getTitle() );
            newMarker.icon( image );
            mMap.moveCamera( CameraUpdateFactory.newLatLng( device ) );
            mMap.setMinZoomPreference( 16f );
            mMap.setOnMarkerClickListener( this );
            Circle circle = mMap.addCircle( new CircleOptions()
                    .center( markerdevice.getPosition() )
                    .radius( 1 )
                    .strokeColor( Color.RED )
                    .fillColor( Color.BLUE )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void build(boolean up) {
        if (tinyDB.getString( markerdevice.getTitle() ).length() > 0 && up == false) {
            new Thread( new Runnable() {
                @Override
                public void run() {
                    runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.i( "debug", "shared " + tinyDB.getString( markerdevice.getTitle() ).toString() );
                                JSONArray json = new JSONArray( tinyDB.getString( markerdevice.getTitle() ) );
                                Log.i( "debug", "taille du shared preferences " + json.length() );
                                Log.i( "debug", "contenu du shared " + json.toString() );
                                for (int l = 0; l < json.length(); l++) {
                                    JSONObject jsonArray = json.getJSONObject( l );
                                    JSONArray jsonObj = jsonArray.getJSONArray( "histories" );
                                    ArrayList<ArrayList<SensorsHistory>> arrayLists = new ArrayList<ArrayList<SensorsHistory>>();
                                    for (int i = 0; i < jsonObj.length(); i++) {
                                        Log.i( "debug", "shared in jsonObj" );
                                        JSONArray histories = jsonObj.getJSONArray( i );
                                        ArrayList<SensorsHistory> hist = new ArrayList<SensorsHistory>();
                                        for (int j = 0; j < histories.length(); j++) {
                                            JSONObject inhist = histories.getJSONObject( j );
                                            int count = inhist.getInt( "count" );
                                            String name = inhist.getString( "name" );
                                            String date = inhist.getString( "date" );
                                            hist.add( new SensorsHistory( count, date, name ) );
                                        }
                                        arrayLists.add( hist );
                                    }
                                    try {
                                        Log.i( "debug", "shared" + arrayLists.get( 0 ).get( 0 ).getName() );
                                        speciescount.add( new Stat( arrayLists.get( 0 ).get( 0 ).getName(), arrayLists ) );
                                    } catch (Exception e) {
                                    }
                                    Log.i( "debug", "shared " + speciescount.size() );
                                }
                                runOnUiThread( new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        MapActivity.getMapActivity().setUpdatemap( true );
                                        runOnUiThread( new Runnable() {
                                            @Override
                                            public void run() {
                                                statsview( speciescount );
                                                statsview( speciescount );
                                            }
                                        } );
                                    }
                                } );
                            } catch (Exception e) {

                            }
                        }
                    } );
                }
            } ).start();
        } else {
            species.clear();
            speciescount.clear();
            runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < listsensors.size(); i++) {
                        Log.i( "debug", "shared" + listsensors.size() );
                        if (listsensors.get( i ).getDevice().equals( markerdevice.getTitle() )) {
                            String urlfinal = listsensors.get( i ).getHref() + "&aggtime=1d";
                            Log.i( "debug", "shared " + urlfinal );
                            species.add( new Stat( listsensors.get( i ).getName(), new ArrayList<ArrayList<SensorsHistory>>() ) );
                            ParseJsonstats getname = new ParseJsonstats();
                            getname.execute( urlfinal, listsensors.get( i ).getName() );
                        }
                    }
                }
            } );
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i( "marker", marker.getTitle() );
        if (marker.getTitle().equals( markerdevice.getTitle() )) {
            markerdevice = marker;
            nom = markerdevice.getTitle();
            Intent speciestatbydevice = new Intent( StatsDeviceSpecies.this, PredictionActivity.class );
            startActivity( speciestatbydevice );
            return true;
        }
        return false;
    }

    public float[] CountDay(String name) {
        float[] day = new float[namespecies.length];
        for (int p = 0; p < namespecies.length; p++) {
            int count = 0;
            for (int i = 0; i < speciescount.size(); i++) {
                if (speciescount.get( i ).getName().equals( namespecies[p] )) {
                    for (int j = 0; j < speciescount.get( i ).getHistories().size(); j++) {
                        for (int l = 0; l < speciescount.get( i ).getHistories().get( j ).size(); l++) {
                            if (speciescount.get( i ).getHistories().get( j ).get( l ).getDate().contains( name )) {
                                count += speciescount.get( i ).getHistories().get( j ).get( l ).getCount();
                            }
                        }
                    }
                }
            }
            day[p] = count;
        }
        return day;
    }

    public float[] CountbyYear(String name) {
        float[] year = new float[namespecies.length];
        for (int p = 0; p < namespecies.length; p++) {
            int count = 0;
            for (int i = 0; i < speciescount.size(); i++) {
                if (speciescount.get( i ).getName().equals( namespecies[p] )) {
                    for (int j = 0; j < speciescount.get( i ).getHistories().size(); j++) {
                        for (int l = 0; l < speciescount.get( i ).getHistories().get( j ).size(); l++) {
                            if (speciescount.get( i ).getHistories().get( j ).get( l ).getDate().contains( name )) {
                                count += speciescount.get( i ).getHistories().get( j ).get( l ).getCount();
                            }
                        }
                    }
                }
            }
            year[p] = count;
        }
        return year;
    }

    public float[] CountbyMonth(String name) {
        float[] month = new float[namespecies.length];
        for (int p = 0; p < namespecies.length; p++) {
            int count = 0;
            for (int i = 0; i < speciescount.size(); i++) {
                if (speciescount.get( i ).getName().equals( namespecies[p] )) {
                    for (int j = 0; j < speciescount.get( i ).getHistories().size(); j++) {
                        for (int l = 0; l < speciescount.get( i ).getHistories().get( j ).size(); l++) {
                            if (speciescount.get( i ).getHistories().get( j ).get( l ).getDate().contains( name )) {
                                count += speciescount.get( i ).getHistories().get( j ).get( l ).getCount();
                            }
                        }
                    }
                }
            }
            month[p] = count;
        }
        return month;
    }

    public float[] CountbyWeek(String name, int daynombre) {
        float[] month = new float[namespecies.length];
        final int nochange = daynombre;
        for (int p = 0; p < namespecies.length; p++) {
            int count = 0;
            int www = 0;
            for (int i = 0; i < speciescount.size(); i++) {
                if (speciescount.get( i ).getName().equals( namespecies[p] )) {
                    for (int j = 0; j < speciescount.get( i ).getHistories().size(); j++) {
                        for (int l = 0; l < speciescount.get( i ).getHistories().get( j ).size(); l++) {
                            if (speciescount.get( i ).getHistories().get( j ).get( l ).getDate().contains( name + "-" + daynumber[daynombre] ) && www < 7) {
                                count += speciescount.get( i ).getHistories().get( j ).get( l ).getCount();
                                if (daynombre < 30) {
                                    daynombre++;
                                }
                                www++;
                            }
                        }
                    }
                    daynombre = nochange;
                    www = 0;
                }
            }
            month[p] = count;
        }
        return month;
    }

    public float[] CountbyDay(String name) {
        float[] month = new float[namespecies.length];
        for (int p = 0; p < namespecies.length; p++) {
            int count = 0;
            for (int i = 0; i < speciescount.size(); i++) {
                if (speciescount.get( i ).getName().equals( namespecies[p] )) {
                    for (int j = 0; j < speciescount.get( i ).getHistories().size(); j++) {
                        for (int l = 0; l < speciescount.get( i ).getHistories().get( j ).size(); l++) {
                            if (speciescount.get( i ).getHistories().get( j ).get( l ).getDate().contains( name )) {
                                count += speciescount.get( i ).getHistories().get( j ).get( l ).getCount();
                            }
                        }
                    }
                }
            }
            month[p] = count;
        }
        return month;
    }

    public class ParseJsonstats extends AsyncTask<String, Void, ArrayList<SensorsHistory>> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected ArrayList<SensorsHistory> doInBackground(String... urls) {
            ArrayList<SensorsHistory> sensorsHistories = new ArrayList<SensorsHistory>();
            JSONArray pp = new JSONArray();
            HttpHandler sh = new HttpHandler();
            String url = urls[0];
            int count = 0;
            String jsonStr = sh.makeServiceCall(url);
            Log.i("debug", jsonStr);
            if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray links = jsonObj.getJSONArray("data");
                        for(int i=0;i<links.length();i++)
                        {
                            count = links.getJSONObject( i ).getInt( "count" );
                            String time = links.getJSONObject( i ).getString( "timestamp" );
                            sensorsHistories.add( new SensorsHistory( count, time, urls[1] ) );
                            Log.i( "debug", sensorsHistories.size() + "" );
                        }
                        Log.i( "debug", urls[1] + " " + count );
                        return sensorsHistories;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            sensorsHistories = new ArrayList<SensorsHistory>();
            sensorsHistories.add( new SensorsHistory( 0, " ", " " ) );
            return sensorsHistories;
        }

        @Override
        protected void onPostExecute(ArrayList<SensorsHistory> links) {
            final ArrayList<SensorsHistory> linkss = links;
            runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    ArrayList<ArrayList<SensorsHistory>> l = new ArrayList<ArrayList<SensorsHistory>>();
                    l.add( linkss );
                    if (speciescount.size() == 0) {
                        speciescount.add( new Stat( species.get( 0 ).getName(), l ) );
                    } else {
                        speciescount.add( new Stat( species.get( speciescount.size() - 1 ).getName(), l ) );
                        progressDialog.setMessage( speciescount.get( speciescount.size() - 1 ).getName().replace( "_", " " ) + "   ( " + (speciescount.size() - 1) + " / " + species.size() + " )" );
                        if (species.size() == speciescount.size()) {
                            String key = markerdevice.getTitle();
                            Gson gson = new GsonBuilder().create();
                            String arrayListToJson = gson.toJson( speciescount );
                            tinyDB.putString( key, arrayListToJson );
                            new Thread( new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    MapActivity.getMapActivity().setUpdatemap( true );
                                    runOnUiThread( new Runnable() {
                                        @Override
                                        public void run() {
                                            statsview( speciescount );
                                        }
                                    } );
                                }
                            } ).start();
                        }
                    }
                }
            } );
        }
    }


    public class Updatelist extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(String... urls) {
            JSONArray pp = new JSONArray();
            HttpHandler sh = new HttpHandler();
            String url = urls[0];
            int count = 0;
            String jsonStr = sh.makeServiceCall( url );
            Log.i( "debug", jsonStr );
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject( jsonStr );
                    JSONArray links = jsonObj.getJSONArray( "data" );
                    count = links.length();
                    return count;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return count;
        }

        @Override
        protected void onPostExecute(Integer links) {
            boolean updatedatabase = false;
            if (tinyDB.getString( markerdevice.getTitle() + "count" ).length() > 0) {
                Log.i( "debug", "shared " + links );
                Log.i( "debug", "shared " + tinyDB.getString( markerdevice.getTitle() + "count" ) );
                Log.i( "debug", "shared" + Integer.parseInt( tinyDB.getString( markerdevice.getTitle() + "count" ) ) );
                Log.i( "debug", "shared " + tinyDB.getString( markerdevice.getTitle() ).toString() );
                if (Integer.parseInt( tinyDB.getString( markerdevice.getTitle() + "count" ) ) < links) {
                    tinyDB.putString( markerdevice.getTitle() + "count", Integer.toString( links ) );
                    updatedatabase = true;
                    build( updatedatabase );
                } else {
                    if (links > 0) {
                        build( updatedatabase );
                    } else {
                        progressDialog.dismiss();
                    }
                }
            } else {
                tinyDB.putString( markerdevice.getTitle() + "count", Integer.toString( links ) );
                updatedatabase = true;
                build( updatedatabase );
            }
        }
    }

}
