package com.tidzamapp.sabri.tidzam;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mukesh.tinydb.TinyDB;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sabri on 18/12/2017.
 */

public class StatsActivity extends AppCompatActivity {

    public static StatsActivity instance;
    private BarChart graph;
    private List<Sensors> listsensors;
    private ProgressDialog progressDialog;
    private String period;
    private String dateA;
    private String dateB;
    private int find;
    private int inprogress;
    private ArrayList<Stat> species;
    private int weeknumber;
    private ArrayList<Stat> birds;
    private TextView choosedate;
    private TextView chooseperiod;
    private RecyclerView recycler;
    private Legend_Recycler leg_recycler;
    private String[] month = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};
    private String[] simplemonth = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July",
            "Aug", "Sept", "Oct", "Nov", "Dec"};
    private String[] daynumber = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
            "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
            "32", "33", "34", "35"};
    private String[] namespecies = {"crickets", "rain", "birds", "unknow",
            "wind", "airplane", "cicadas", "frog", "mic_crackle"};
    private int[] colors = {
            Color.MAGENTA, Color.CYAN, Color.BLUE, Color.rgb( 0, 102, 51 ), Color.LTGRAY, Color.GRAY,
            Color.rgb( 255, 140, 0 ), Color.GREEN, Color.RED};
    private String[] annee = {"2017", "2018"};
    private String[] namesbirds = {"mallard", "northern_cardinal", "red_winged_blackbird", "mourning_dove",
            "downy_woodpecker", "herring_gull", "american_crow", "song_sparrow", "american_robin",
            "black_capped_chickadee", "tufted_titmouse", "american_goldfinch", "canada_goose",
            "barn_swallow", "blue_jay"};
    private String nomspecie;
    private TinyDB tinyDB;
    private boolean update;

    public StatsActivity() {
        instance = this;
    }

    public static StatsActivity getInstance() {
        return instance;
    }

    public int getWeeknumber() {
        return weeknumber;
    }

    public ArrayList<Stat> getSpecies() {
        return species;
    }

    public TextView getChoosedate() {
        return choosedate;
    }

    public TextView getChooseperiod() {
        return chooseperiod;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public ArrayList<Stat> getBirds() {
        return birds;
    }

    public String getNomspecie() {
        return nomspecie;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.statsperiod );
        graph = (BarChart) findViewById( R.id.graphperiod );
        species = new ArrayList<Stat>();
        MapActivity.getMapActivity().setUpdatemap( false );
        graph = (BarChart) findViewById( R.id.graphperiod );
        listsensors = SplashScreen.getInstence().getListsensors();
        period = "";
        Log.i( "debug", "" + listsensors.size() );
        find = 0;
        tinyDB = new TinyDB( StatsActivity.this );
        update = false;
        inprogress = 0;
        birds = new ArrayList<Stat>();
        chooseperiod = (TextView) findViewById( R.id.dateperiode );
        chooseperiod.setText( "All of time" );
        choosedate = (TextView) findViewById( R.id.datespinner );
        chooseperiod.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] period = {"Day", "Week", "Month", "Year", "All of time"};
                AlertDialog.Builder builder = new AlertDialog.Builder( StatsActivity.this );
                builder.setTitle( "Choose a period" )
                        .setItems( period, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                chooseperiod.setText( period[which] );
                                choosedate.setText( "" );
                                if (which == 4) {
                                    runOnUiThread( new Runnable() {
                                        @Override
                                        public void run() {
                                            statsview();
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
                    final DatePickerDialog adate = new DatePickerDialog( StatsActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                                    statsview();
                                }
                            } );
                        }
                    }, mYear, mMonth, mDay );
                    adate.show();
                }
                if (chooseperiod.getText().toString().equals( "Month" )) {
                    final String[] period = {"January", "February", "March", "April", "May", "June", "July",
                            "August", "September", "October", "November", "December"};
                    AlertDialog.Builder builder = new AlertDialog.Builder( StatsActivity.this );
                    builder.setTitle( "Choose a month" )
                            .setItems( period, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    choosedate.setText( period[which] );
                                    final String[] period1 = {"2017", "2018"};
                                    AlertDialog.Builder builder = new AlertDialog.Builder( StatsActivity.this );
                                    builder.setTitle( "Choose a year" )
                                            .setItems( period1, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    choosedate.setText( choosedate.getText().toString() + " " + period1[which] );
                                                    runOnUiThread( new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            statsview();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder( StatsActivity.this );
                    builder.setTitle( "Choose a year" )
                            .setItems( period1, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    choosedate.setText( period1[which] );
                                    final String[] period = {"January", "February", "March", "April", "May", "June", "July",
                                            "August", "September", "October", "November", "December"};
                                    AlertDialog.Builder builder = new AlertDialog.Builder( StatsActivity.this );
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
                                                    AlertDialog.Builder builder = new AlertDialog.Builder( StatsActivity.this );
                                                    builder.setTitle( "Choose a week" )
                                                            .setItems( week, new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, final int which) {
                                                                    choosedate.setText( choosedate.getText().toString() + " " + week[which] );
                                                                    weeknumber = which;
                                                                    runOnUiThread( new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            statsview();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder( StatsActivity.this );
                    builder.setTitle( "Choose a year" )
                            .setItems( period1, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    choosedate.setText( period1[which] );
                                    runOnUiThread( new Runnable() {
                                        @Override
                                        public void run() {
                                            statsview();
                                        }
                                    } );
                                }
                            } );
                    builder.create();
                    builder.show();
                }
            }
        } );
        for (int i = 0; i < namespecies.length; i++) {
            species.add( new Stat( namespecies[i], new ArrayList<ArrayList<SensorsHistory>>() ) );
        }
        for (int i = 0; i < namesbirds.length; i++) {
            birds.add( new Stat( namesbirds[i], new ArrayList<ArrayList<SensorsHistory>>() ) );
        }
        initializeSpecies();
        recycler = findViewById( R.id.recyclerleg );
        recycler.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
        leg_recycler = new Legend_Recycler( getApplicationContext(), namespecies, colors, new Legend_Recycler.RecyclerItemClickListenerSpecies() {
            @Override
            public void onClickListener(String species, int position) {
                if (species.equals( "birds" )) {
                    try {
                        Log.i( "debug", "shared " + tinyDB.getString( "birds" ) );
                        JSONArray json = new JSONArray( tinyDB.getString( "birds" ) );
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
                                birds.add( new Stat( arrayLists.get( 0 ).get( 0 ).getName(), arrayLists ) );
                            } catch (Exception e) {
                            }
                            Log.i( "debug", "shared " + birds.size() );
                        }
                    } catch (Exception e) {

                    }
                    startActivity( new Intent( StatsActivity.this, StatsBirdsActivity.class ) );
                } else if (!species.equals( "unknow" )) {
                    nomspecie = species;
                    startActivity( new Intent( StatsActivity.this, WikiStatsActivity.class ) );
                }
            }
        } );
        recycler.setAdapter( leg_recycler );
    }

    private void initializeSpecies() {
        progressDialog = ProgressDialog.show( this, "Species Sample", "Please Wait", true );
        new Thread( (new Runnable() {
            @Override
            public void run() {
                if (tinyDB.getString( "species" ).length() > 0) {
                    runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < listsensors.size(); i++) {
                                Log.i( "debug", "shared" + listsensors.size() );
                                if (listsensors.get( i ).getDevice().equals( "impoundment:out_1" ) && listsensors.get( i ).getName().equals( "airplane" )) {
                                    String urlfinal = listsensors.get( i ).getHref() + "&aggtime=1d";
                                    Log.i( "debug", "shared " + urlfinal );
                                    Updatelist getname = new Updatelist();
                                    getname.execute( urlfinal );
                                }
                            }
                        }
                    } );
                    if (update == false) {
                        try {
                            Log.i( "debug", "shared " + tinyDB.getString( "species" ) );
                            JSONArray json = new JSONArray( tinyDB.getString( "species" ) );
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
                                    species.add( new Stat( arrayLists.get( 0 ).get( 0 ).getName(), arrayLists ) );
                                } catch (Exception e) {
                                }
                                Log.i( "debug", "shared " + species.size() );
                            }
                            runOnUiThread( new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    MapActivity.getMapActivity().setUpdatemap( true );
                                    runOnUiThread( new Runnable() {
                                        @Override
                                        public void run() {
                                            statsview();
                                        }
                                    } );
                                }
                            } );
                        } catch (Exception e) {

                        }
                    } else {
                        RassemblementdesDonnees();
                        runOnUiThread( new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                statsview();
                                MapActivity.getMapActivity().setUpdatemap( true );
                            }
                        } );
                    }
                } else {
                    RassemblementdesDonnees();
                    runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            statsview();
                            MapActivity.getMapActivity().setUpdatemap( true );
                        }
                    } );
                }
            }
        }) ).start();
    }

    public int FindintheList(List<Sensors> links, String name, boolean isbird) {
        find = 0;
        if (isbird == false) {
            for (int i = 0; i < listsensors.size(); i++) {
                if (links.get( i ).getName().equals( name )) {
                    find++;
                }
            }
        } else {
            for (int i = 0; i < listsensors.size(); i++) {
                if (links.get( i ).getName().contains( name )) {
                    find++;
                }
            }
        }
        return find;
    }


    public void RassemblementdesDonnees() {
        for (int i = 0; i < species.size(); i++) {
            int findd = 0;
            Log.i( "debug", species.get( i ).getName() );
            findd = FindintheList( listsensors, species.get( i ).getName(), false );
            while (findd == 0) {

            }
            Log.i( "debug", "" + findd );
            for (int l = 0; l < listsensors.size(); l++) {
                if (listsensors.get( l ).getName().equals( species.get( i ).getName() )) {
                    String urlfinal = listsensors.get( l ).getHref() + "&aggtime=1d";
                    ParseJsonstats getname = new ParseJsonstats();
                    getname.execute( urlfinal, species.get( i ).getName() );
                }
            }
            while (findd != inprogress) {

            }
            inprogress = 0;
        }
        for (int i = 0; i < birds.size(); i++) {
            int findd = 0;
            Log.i( "debug", birds.get( i ).getName() );
            findd = FindintheList( listsensors, birds.get( i ).getName(), true );
            while (findd == 0) {

            }
            Log.i( "debug", "" + findd );
            for (int l = 0; l < listsensors.size(); l++) {
                if (listsensors.get( l ).getName().contains( birds.get( i ).getName() )) {
                    String urlfinal = listsensors.get( l ).getHref() + "&aggtime=1d";
                    ParseJsonstats getname = new ParseJsonstats();
                    getname.execute( urlfinal, birds.get( i ).getName() );
                }
            }
            while (findd != inprogress) {

            }
            inprogress = 0;
        }
        Gson gson = new GsonBuilder().create();
        String arrayListToJson = gson.toJson( species );
        tinyDB.putString( "species", arrayListToJson );
        Gson gsonbirds = new GsonBuilder().create();
        String arrayListToJsonbirds = gsonbirds.toJson( birds );
        tinyDB.putString( "birds", arrayListToJsonbirds );

    }

    public void statsview() {
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
                            statsview();
                        }
                    } );
                }

                @Override
                public void onNothingSelected() {

                }
            } );
        }
        if (chooseperiod.getText().toString().equals( "Year" )) {
            graph.removeAllViewsInLayout();
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
                            statsview();
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
                            statsview();
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
                            statsview();
                        }
                    } );
                }

                @Override
                public void onNothingSelected() {

                }
            } );
        }
    }

    private float[] CountDay(String name) {
        float[] day = new float[namespecies.length];
        for (int p = 0; p < namespecies.length; p++) {
            int count = 0;
            for (int i = 0; i < species.size(); i++) {
                if (species.get( i ).getName().equals( namespecies[p] )) {
                    for (int j = 0; j < species.get( i ).getHistories().size(); j++) {
                        for (int l = 0; l < species.get( i ).getHistories().get( j ).size(); l++) {
                            if (species.get( i ).getHistories().get( j ).get( l ).getDate().contains( name )) {
                                count += species.get( i ).getHistories().get( j ).get( l ).getCount();
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
            for (int i = 0; i < species.size(); i++) {
                if (species.get( i ).getName().equals( namespecies[p] )) {
                    for (int j = 0; j < species.get( i ).getHistories().size(); j++) {
                        for (int l = 0; l < species.get( i ).getHistories().get( j ).size(); l++) {
                            if (species.get( i ).getHistories().get( j ).get( l ).getDate().contains( name )) {
                                count += species.get( i ).getHistories().get( j ).get( l ).getCount();
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
            for (int i = 0; i < species.size(); i++) {
                if (species.get( i ).getName().equals( namespecies[p] )) {
                    for (int j = 0; j < species.get( i ).getHistories().size(); j++) {
                        for (int l = 0; l < species.get( i ).getHistories().get( j ).size(); l++) {
                            if (species.get( i ).getHistories().get( j ).get( l ).getDate().contains( name )) {
                                count += species.get( i ).getHistories().get( j ).get( l ).getCount();
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
            for (int i = 0; i < species.size(); i++) {
                if (species.get( i ).getName().equals( namespecies[p] )) {
                    for (int j = 0; j < species.get( i ).getHistories().size(); j++) {
                        for (int l = 0; l < species.get( i ).getHistories().get( j ).size(); l++) {
                            if (species.get( i ).getHistories().get( j ).get( l ).getDate().contains( name + "-" + daynumber[daynombre] ) && www < 7) {
                                count += species.get( i ).getHistories().get( j ).get( l ).getCount();
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
            for (int i = 0; i < species.size(); i++) {
                if (species.get( i ).getName().equals( namespecies[p] )) {
                    for (int j = 0; j < species.get( i ).getHistories().size(); j++) {
                        for (int l = 0; l < species.get( i ).getHistories().get( j ).size(); l++) {
                            if (species.get( i ).getHistories().get( j ).get( l ).getDate().contains( name )) {
                                count += species.get( i ).getHistories().get( j ).get( l ).getCount();
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
            inprogress++;
            int count = 0;
            String jsonStr = sh.makeServiceCall( url );
            Log.i( "debug", jsonStr );
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject( jsonStr );
                    JSONArray links = jsonObj.getJSONArray( "data" );
                    for (int i = 0; i < links.length(); i++) {
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
            for (int i = 0; i < species.size(); i++) {
                if (links.size() > 0) {
                    if (species.get( i ).getName().equals( links.get( 0 ).getName() )) {
                        species.get( i ).getHistories().add( links );
                        progressDialog.setMessage( species.get( i ).getName() + "   ( " + (i + 1) + " / " + species.size() + " )" );
                        Log.i( "debug", "" + species.get( i ).getName() + " : " + species.get( i ).getHistories().size() );
                    }
                }
            }
            for (int i = 0; i < birds.size(); i++) {
                if (links.size() > 0) {
                    if (birds.get( i ).getName().equals( links.get( 0 ).getName() )) {
                        birds.get( i ).getHistories().add( links );
                        progressDialog.setMessage( birds.get( i ).getName() + "   ( " + (i + 1) + " / " + birds.size() + " )" );
                        Log.i( "debug", "" + birds.get( i ).getName() + " : " + birds.get( i ).getHistories().size() );
                    }
                }
            }
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
            if (tinyDB.getString( "statscount" ).length() > 0) {
                Log.i( "debug", "shared taille " + Integer.parseInt( tinyDB.getString( "statscount" ) ) );
                if (Integer.parseInt( tinyDB.getString( "statscount" ) ) < links) {
                    tinyDB.putString( "statscount", Integer.toString( links ) );
                    update = true;
                }
            } else {
                tinyDB.putString( "statscount", Integer.toString( links ) );
                update = true;
            }
        }
    }
}