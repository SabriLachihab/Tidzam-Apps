package com.tidzamapp.sabri.tidzam;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

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
 * Created by Sabri on 09/01/2018.
 */

public class PredictionActivity extends AppCompatActivity {


    private static final float TOTAL_MEMORY = 1.1f;
    private Spinner spinner;
    private ArrayList<Predicitions> predicitions;
    private LineChart lineChart;
    private String SOCKET_URL = "//tidzam.media.mit.edu";
    private Socket socket;
    private ImageView imageView;
    private TextView textView;
    private String specie;
    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_predictions );
        List<String> list = new ArrayList<String>();
        predicitions = MapActivity.getMapActivity().getPredicitionsArrayList();
        Log.i( "debug", "Liste " + predicitions.size() );
        for (int i = 0; i < predicitions.size(); i++) {
            list.add( predicitions.get( i ).getName() );
            Log.i( "debug", list.get( i ) );
        }
        specie = "";
        text = "";
        //ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_spinner_item, list );
        //spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item ); // The drop down view
        //spinner.setAdapter( spinnerArrayAdapter );
        textView = (TextView) findViewById( R.id.textprediction );
        imageView = (ImageView) findViewById( R.id.imageprediction );
        //spinner.setOnItemSelectedListener( new CustomOnItemSelectedListener() );
        lineChart = (LineChart) findViewById( R.id.linechart );
        setupChart();
        setupAxes();
        setupData();
        setLegend();
        SocketPrediction socketPrediction = new SocketPrediction();
        socketPrediction.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );
    }

    private void setupAxes() {
        XAxis xl = lineChart.getXAxis();
        xl.setTextColor( Color.WHITE );
        xl.setDrawGridLines( false );
        xl.setAvoidFirstLastClipping( true );
        xl.setEnabled( false );
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor( Color.WHITE );
        leftAxis.setAxisMaximum( TOTAL_MEMORY );
        leftAxis.setAxisMinimum( 0f );
        leftAxis.setDrawGridLines( true );
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled( false );
        // Add a limit line
        // reset all limit lines to avoid overlapping lines
        leftAxis.removeAllLimitLines();
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData( true );
    }

    private void setupChart() {
        lineChart.getDescription().setEnabled( false );
        lineChart.setTouchEnabled( true );
        lineChart.setPinchZoom( true );
        lineChart.setScaleEnabled( true );
        lineChart.setDrawGridBackground( false );
        lineChart.setBackgroundColor( Color.DKGRAY );
    }

    private void setupData() {
        LineData data = new LineData();
        data.setValueTextColor( Color.WHITE );
        // add empty data
        lineChart.setData( data );
    }

    private void addEntry() {
        for (int i = 0; i < predicitions.size(); i++) {
            if (predicitions.get( i ).getName().equals( StatsDeviceSpecies.getStatsDeviceSpecies().getNom() )) {
                LineData data = lineChart.getData();
                if (data != null) {
                    ILineDataSet set = data.getDataSetByIndex( 0 );
                    ILineDataSet set1 = data.getDataSetByIndex( 1 );
                    ILineDataSet set2 = data.getDataSetByIndex( 2 );
                    ILineDataSet set3 = data.getDataSetByIndex( 3 );
                    ILineDataSet set4 = data.getDataSetByIndex( 4 );
                    ILineDataSet set5 = data.getDataSetByIndex( 5 );
                    ILineDataSet set6 = data.getDataSetByIndex( 6 );
                    ILineDataSet set7 = data.getDataSetByIndex( 7 );
                    ILineDataSet set8 = data.getDataSetByIndex( 8 );
                    ILineDataSet set9 = data.getDataSetByIndex( 9 );
                    if (set == null) {
                        set = CreateSet( "Rain", Color.CYAN );
                        data.addDataSet( set );
                    }
                    if (set1 == null) {
                        set1 = CreateSet( "Bird", Color.BLUE );
                        data.addDataSet( set1 );
                    }
                    if (set2 == null) {
                        set2 = CreateSet( "No Signal", Color.BLACK );
                        data.addDataSet( set2 );
                    }
                    if (set3 == null) {
                        set3 = CreateSet( "Airplane", Color.LTGRAY );
                        data.addDataSet( set3 );
                    }
                    if (set4 == null) {
                        set4 = CreateSet( "Frog", Color.GREEN );
                        data.addDataSet( set4 );
                    }
                    if (set5 == null) {
                        set5 = CreateSet( "Mic crackle", Color.RED );
                        data.addDataSet( set5 );
                    }
                    if (set6 == null) {
                        set6 = CreateSet( "Cicadas", Color.YELLOW );
                        data.addDataSet( set6 );
                    }
                    if (set7 == null) {
                        set7 = CreateSet( "Crick", Color.MAGENTA );
                        data.addDataSet( set7 );
                    }
                    if (set8 == null) {
                        set8 = CreateSet( "Quiet", Color.WHITE );
                        data.addDataSet( set8 );
                    }
                    if (set9 == null) {
                        set9 = CreateSet( "Wind", Color.GRAY );
                        data.addDataSet( set9 );
                    }
                    try {
                        data.addEntry( new Entry( set.getEntryCount(), (float) predicitions.get( i ).getJsonObject().getDouble( "rain" ) ), 0 );
                        data.addEntry( new Entry( set.getEntryCount(), (float) predicitions.get( i ).getJsonObject().getDouble( "birds" ) ), 1 );
                        data.addEntry( new Entry( set.getEntryCount(), (float) predicitions.get( i ).getJsonObject().getDouble( "no_signal" ) ), 2 );
                        data.addEntry( new Entry( set.getEntryCount(), (float) predicitions.get( i ).getJsonObject().getDouble( "airplane" ) ), 3 );
                        data.addEntry( new Entry( set.getEntryCount(), (float) predicitions.get( i ).getJsonObject().getDouble( "frog" ) ), 4 );
                        data.addEntry( new Entry( set.getEntryCount(), (float) predicitions.get( i ).getJsonObject().getDouble( "mic_crackle" ) ), 5 );
                        data.addEntry( new Entry( set.getEntryCount(), (float) predicitions.get( i ).getJsonObject().getDouble( "cicadas" ) ), 6 );
                        data.addEntry( new Entry( set.getEntryCount(), (float) predicitions.get( i ).getJsonObject().getDouble( "crickets" ) ), 7 );
                        data.addEntry( new Entry( set.getEntryCount(), (float) predicitions.get( i ).getJsonObject().getDouble( "quiet" ) ), 8 );
                        data.addEntry( new Entry( set.getEntryCount(), (float) predicitions.get( i ).getJsonObject().getDouble( "wind" ) ), 9 );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // let the chart know it's data has changed
                    data.notifyDataChanged();
                    lineChart.notifyDataSetChanged();
                    // limit the number of visible entries
                    lineChart.setVisibleXRangeMaximum( 15 );
                    // move to the latest entry
                    lineChart.moveViewToX( data.getEntryCount() );
                }
            }
        }
    }

    private void setLegend() {
        // get the legend (only possible after setting data)
        Legend l = lineChart.getLegend();
        // modify the legend ...
        l.setForm( Legend.LegendForm.CIRCLE );
        l.setTextColor( Color.WHITE );
        l.setPosition( Legend.LegendPosition.RIGHT_OF_CHART_CENTER );
        l.setTextSize( 15.0f );
    }

    private LineDataSet CreateSet(String label, int color) {
        LineDataSet set = new LineDataSet( null, label );
        set.setAxisDependency( YAxis.AxisDependency.LEFT );
        set.setColors( color );
        set.setLineWidth( 2f );
        // To show values of each point
        set.setDrawValues( false );
        return set;
    }

    public class SocketPrediction extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            try {
                socket = IO.socket( SOCKET_URL );

            } catch (Exception e) {
                Log.d( "georges", e.getMessage() );
            }

            socket.on( Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d( "georges", "connected to tidzam socket" );
                }

            } ).on( "sys", new Emitter.Listener() {

                @Override
                public void call(final Object... args) {
                    try {

                        Object json = null;
                        JSONArray jsonArray;
                        try {
                            json = new JSONTokener( args[0].toString() ).nextValue();
                        } catch (JSONException e) {
                            Log.d( "georges", e.getMessage() );
                        }

                        jsonArray = (JSONArray) json;
                        ArrayList<Predicitions> provisoire = new ArrayList<Predicitions>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            //Log.d("georges", args[i].toString());
                            JSONObject data = jsonArray.getJSONObject( i );
                            JSONObject analysis = data.getJSONObject( "analysis" );
                            final String deviceName = data.getString( "chan" ).toString();
                            String result = analysis.getString( "result" ).toString();
                            String time = analysis.getString( "time" );
                            JSONObject predicitions = analysis.optJSONObject( "predicitions" );
                            provisoire.add( new Predicitions( deviceName, predicitions ) );
                            // formatting of the string
                            result = result.replace( "[", "" );
                            result = result.replace( "]", "" );
                            result = result.replace( "\"", "" );
                            if (StatsDeviceSpecies.getStatsDeviceSpecies().getNom().equals( deviceName )) {
                                specie = result;
                            /*String[] val = time.split( "." );
                            time = val[0];
                            String[] date = time.split( "T" );
                            String[] sep = date[0].split( "-" );
                            String period = sep[1]+"/"+sep[2]+"/"+sep[0]+" "+date[1];*/
                                text = deviceName + "\n\n" + "Result : " + specie + "\n\n" + time;
                                Log.i( "debug", "Result : " + specie + " " + time );
                            }
                        }
                        predicitions = provisoire;
                        new Thread( new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread( new Runnable() {
                                    @Override
                                    public void run() {
                                        addEntry();
                                        textView.setText( text );
                                        switch (specie) {
                                            case "airplane":
                                                imageView.setImageBitmap( BitmapFactory.decodeResource( getResources(), R.mipmap.airplane ) );
                                                break;
                                            case "birds":
                                                imageView.setImageBitmap( BitmapFactory.decodeResource( getResources(), R.mipmap.birds ) );
                                                break;
                                            case "wind":
                                                imageView.setImageBitmap( BitmapFactory.decodeResource( getResources(), R.mipmap.wind ) );
                                                break;
                                            case "frog":
                                                imageView.setImageBitmap( BitmapFactory.decodeResource( getResources(), R.mipmap.frog ) );
                                                break;
                                            case "cicadas":
                                                imageView.setImageBitmap( BitmapFactory.decodeResource( getResources(), R.mipmap.cicadas ) );
                                                break;
                                            case "rain":
                                                imageView.setImageBitmap( BitmapFactory.decodeResource( getResources(), R.mipmap.rain ) );
                                                break;
                                            case "no_signal":
                                                imageView.setImageBitmap( BitmapFactory.decodeResource( getResources(), R.mipmap.no_signal ) );
                                                break;
                                            case "mic_crackle":
                                                imageView.setImageBitmap( BitmapFactory.decodeResource( getResources(), R.mipmap.mic_crackle ) );
                                                break;
                                            case "unknown":
                                                imageView.setImageBitmap( BitmapFactory.decodeResource( getResources(), R.mipmap.unknown ) );
                                                break;
                                        }
                                    }
                                } );
                            }
                        } ).start();
                    } catch (Exception e) {
                        Log.d( "georges sys error : ", e.getMessage() );
                    }
                }

            } ).on( Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    socket.close();
                }
            } );
            socket.connect();
        }
    }

    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    lineChart.removeAllViews();
                    setupChart();
                    setupAxes();
                    setupData();
                    setLegend();
                }
            } );
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}
