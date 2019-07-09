package com.tidzamapp.sabri.tidzam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mukesh.tinydb.TinyDB;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sabri on 07/01/2018.
 */

public class SampleDatabaseBirdsActivity extends AppCompatActivity {

    public static SampleDatabaseBirdsActivity instence;
    private PieChart pieChart;
    private ArrayList<SensorCount> species;
    private List<Sensors> listsensors;
    private int find;
    private int inprogress;
    private ProgressDialog progressDialog;
    private String birds_name;
    private int total;
    private RecyclerView recycler;
    private Legend_Recycler leg_recycler;
    private int[] colors = {
            Color.MAGENTA, Color.CYAN, Color.BLUE, Color.BLACK, Color.DKGRAY, Color.LTGRAY, Color.GRAY,
            Color.rgb( 255, 140, 0 ), Color.GREEN, Color.RED, Color.rgb( 246, 171, 231 ),
            Color.rgb( 121, 82, 48 ), Color.rgb( 51, 153, 255 )
            , Color.rgb( 153, 255, 153 ), Color.rgb( 0, 102, 51 )};
    private TinyDB tinyDB;
    private boolean update;

    public SampleDatabaseBirdsActivity() {
        instence = this;
    }

    public static SampleDatabaseBirdsActivity getInstence() {
        return instence;
    }

    public String getBirds_name() {
        return birds_name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_piechartdatabase );
        pieChart = (PieChart) findViewById( R.id.sampledatabase );
        String[] namespecies = {"mallard", "northern_cardinal", "red_winged_blackbird", "mourning_dove",
                "downy_woodpecker", "herring_gull", "american_crow", "song_sparrow", "american_robin",
                "black_capped_chickadee", "tufted_titmouse", "american_goldfinch", "canada_goose",
                "barn_swallow", "blue_jay"};
        species = new ArrayList<SensorCount>();
        MapActivity.getMapActivity().setUpdatemap( false );
        listsensors = SplashScreen.getInstence().getListsensors();
        Log.i( "debug", "" + listsensors.size() );
        find = 0;
        total = 0;
        inprogress = 0;
        tinyDB = new TinyDB( SampleDatabaseBirdsActivity.this );
        update = false;
        for (int i = 0; i < namespecies.length; i++) {
            species.add( new SensorCount( namespecies[i], 0 ) );
        }
        recycler = (RecyclerView) findViewById( R.id.recyclerlegbirds );
        recycler.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
        leg_recycler = new Legend_Recycler( getApplicationContext(), namespecies, colors, new Legend_Recycler.RecyclerItemClickListenerSpecies() {
            @Override
            public void onClickListener(String species, int position) {
                birds_name = "birds-";
                birds_name += species;
                startActivity( new Intent( SampleDatabaseBirdsActivity.this, WikiSampleBirds.class ) );

            }
        } );
        recycler.setAdapter( leg_recycler );
        PrimarySample( species );
    }

    public int FindintheList(List<Sensors> links, String name) {
        find = 0;
        for (int i = 0; i < listsensors.size(); i++) {
            if (links.get( i ).getName().contains( name )) {
                find++;
            }
        }
        return find;
    }

    public void RassemblementdesDonnees() {
        for (int i = 0; i < species.size(); i++) {
            int findd = 0;
            Log.i( "debug", species.get( i ).getName() );
            findd = FindintheList( listsensors, species.get( i ).getName() );
            while (findd == 0) {

            }
            Log.i( "debug", "" + findd );
            for (int l = 0; l < listsensors.size(); l++) {
                if (listsensors.get( l ).getName().contains( species.get( i ).getName() )) {
                    String urlfinal = listsensors.get( l ).getHref() + "&aggtime=1d";
                    ParseJsonstats getname = new ParseJsonstats();
                    getname.execute( urlfinal, species.get( i ).getName() );
                }
            }
            while (findd != inprogress) {

            }
            inprogress = 0;
        }
        Gson gson = new GsonBuilder().create();
        String arrayListToJson = gson.toJson( species );
        tinyDB.putString( "birdssample", arrayListToJson );
    }

    private void BuildPrimarySample(ArrayList<SensorCount> links) {
        ArrayList<PieEntry> yvalues = new ArrayList<PieEntry>();
        total = 0;
        for (int i = 0; i < links.size(); i++) {
            yvalues.add( new PieEntry( (float) links.get( i ).getCount(), links.get( i ).getName() ) );
            total += links.get( i ).getCount();
        }
        PieDataSet dataSet = new PieDataSet( yvalues, "" );
        dataSet.setSliceSpace( 2f );
        dataSet.setValueTextSize( 15f );
        dataSet.setValueTextColor( Color.WHITE );
        dataSet.setValueFormatter( new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                {
                    if (entry.getY() / total >= 0.03) {
                        return String.format( "%.1f", (entry.getY() / total) * 100 ) + " %";
                    } else {
                        return "";
                    }
                }
            }
        } );
        dataSet.setColors( colors );
        PieData data = new PieData( dataSet );
        pieChart.notifyDataSetChanged();
        pieChart.setRotationEnabled( true );
        pieChart.setDrawSliceText( false );
        pieChart.setDrawCenterText( false );
        Legend l = pieChart.getLegend();
        l.setEnabled( false );
        pieChart.getDescription().setText( "Birds Classes" );
        pieChart.getDescription().setTextSize( 15f );
        pieChart.setUsePercentValues( true );
        pieChart.setData( data );
        pieChart.getLegend().setWordWrapEnabled( true );
        pieChart.animateY( 5000 );
        pieChart.setOnChartValueSelectedListener( new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        } );

    }

    private void PrimarySample(ArrayList<SensorCount> links) {
        progressDialog = ProgressDialog.show( this, "Birds Sample", "", true );
        new Thread( (new Runnable() {
            @Override
            public void run() {
                if (tinyDB.getString( "birdssample" ).length() > 0) {
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
                            Log.i( "debug", "shared " + tinyDB.getString( "birdssample" ) );
                            JSONArray json = new JSONArray( tinyDB.getString( "birdssample" ) );
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject comptage = json.getJSONObject( i );
                                int count = comptage.getInt( "count" );
                                String name = comptage.getString( "name" );
                                species.add( new SensorCount( name, count ) );
                            }
                            runOnUiThread( new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    runOnUiThread( new Runnable() {
                                        @Override
                                        public void run() {
                                            BuildPrimarySample( species );
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
                                BuildPrimarySample( species );
                            }
                        } );
                    }
                } else {
                    RassemblementdesDonnees();
                    runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            BuildPrimarySample( species );
                        }
                    } );
                }
            }
        }) ).start();
    }

    public class ParseJsonstats extends AsyncTask<String, Void, SensorCount> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected SensorCount doInBackground(String... urls) {
            JSONArray pp = new JSONArray();
            HttpHandler sh = new HttpHandler();
            String url = urls[0];
            String jsonStr = sh.makeServiceCall( url );
            int count = 0;
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject( jsonStr );
                    JSONArray links = jsonObj.getJSONArray( "data" );
                    for (int i = 0; i < links.length(); i++) {
                        try {
                            count += links.getJSONObject( i ).getInt( "count" );
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            inprogress++;
            return new SensorCount( urls[1], count );
        }

        @Override
        protected void onPostExecute(SensorCount links) {
            for (int i = 0; i < species.size(); i++) {
                if (species.get( i ).getName().equals( links.getName() )) {
                    species.get( i ).setCount( species.get( i ).getCount() + links.getCount() );
                    total += links.getCount();
                    progressDialog.setMessage( species.get( i ).getName() + "   ( " + (i + 1) + " / " + species.size() + " )" );
                    Log.i( "debug", "" + species.get( i ).getName() + " : " + species.get( i ).getCount() );
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
            if (tinyDB.getString( "primarycount" ).length() > 0) {
                if (Integer.parseInt( tinyDB.getString( "primarycount" ) ) < links) {
                    tinyDB.putString( "primarycount", Integer.toString( links ) );
                    update = true;
                }
            } else {
                tinyDB.putString( "primarycount", Integer.toString( links ) );
                update = true;
            }
        }
    }
}
