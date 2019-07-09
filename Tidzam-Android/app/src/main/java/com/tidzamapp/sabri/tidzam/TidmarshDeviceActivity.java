package com.tidzamapp.sabri.tidzam;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Sabri on 19/01/2018.
 */

public class TidmarshDeviceActivity extends AppCompatActivity {

    private TextView options;
    private TextView info;
    private TextView period;
    private TextView date;
    private TextView day;
    private LineChart lineChart;
    private Marker marker;
    private ArrayList<Sensors> tidmarshsensors;
    private TextView infopoint;
    private int weeknumber;
    private String[] unities = {"hPa", "celsius", "celsius", "%", "lux", "celsius"};
    private String[] mesures = {"pressure", "temperature sht", "temperature bmp", "humidity", "illuminance", "water temperature"};
    private String[] name = {"bmp_pressure", "sht_temperature", "sht_temperature", "sht_humidity", "illuminance", "water_temperature"};
    private String[] periode = {"All of time", "Year", "Month", "Week", "Day"};
    private String dateB;
    private String[] mois = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};
    private String[] daynumber = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
            "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_tidmarshdevice );
        options = (TextView) findViewById( R.id.optiondevicetidmarsh );
        options.setText( "pressure" );
        info = (TextView) findViewById( R.id.infodevicetidmarsh );
        lineChart = (LineChart) findViewById( R.id.hourtidmarshdevice );
        day = (TextView) findViewById( R.id.tidmarshdeviceday );
        tidmarshsensors = SplashScreen.getInstence().getSensorstidmarsh();
        marker = MapActivity.getMapActivity().getMarkerdevice();
        infopoint = (TextView) findViewById( R.id.infopoint );
        period = (TextView) findViewById( R.id.tidmarshdeviceperiod );
        period.setText( "All of time" );
        date = (TextView) findViewById( R.id.tidmarshdevicedaate );
        period.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder( TidmarshDeviceActivity.this );
                builder.setTitle( "Choose a period" ).setItems( periode, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        period.setText( periode[which] );
                        date.setText( "" );
                        if (period.getText().toString().equals( "All of time" )) {
                            lineChart.removeAllViews();
                            setupData();
                            find();
                        }
                    }
                } );
                builder.create();
                builder.show();
            }
        } );
        date.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (period.getText().toString().equals( "Day" )) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get( Calendar.YEAR );
                    int mMonth = c.get( Calendar.MONTH );
                    int mDay = c.get( Calendar.DAY_OF_MONTH );
                    final DatePickerDialog adate = new DatePickerDialog( TidmarshDeviceActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                                    setupData();
                                    find();
                                }
                            } );
                        }
                    }, mYear, mMonth, mDay );
                    adate.show();
                }
                if (period.getText().toString().equals( "Month" )) {
                    final String[] period = {"January", "February", "March", "April", "May", "June", "July",
                            "August", "September", "October", "November", "December"};
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( TidmarshDeviceActivity.this );
                    builder.setTitle( "Choose a month" )
                            .setItems( period, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    date.setText( period[which] );
                                    final String[] period1 = {"2017", "2018"};
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( TidmarshDeviceActivity.this );
                                    builder.setTitle( "Choose a year" )
                                            .setItems( period1, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    date.setText( date.getText().toString() + " " + period1[which] );
                                                    runOnUiThread( new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            lineChart.removeAllViewsInLayout();
                                                            setupData();
                                                            find();
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
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( TidmarshDeviceActivity.this );
                    builder.setTitle( "Choose a year" )
                            .setItems( period1, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    date.setText( period1[which] );
                                    final String[] period = {"January", "February", "March", "April", "May", "June", "July",
                                            "August", "September", "October", "November", "December"};
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( TidmarshDeviceActivity.this );
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
                                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( TidmarshDeviceActivity.this );
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
                                                                            find();
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
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( TidmarshDeviceActivity.this );
                    builder.setTitle( "Choose a year" )
                            .setItems( period1, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    date.setText( period1[which] );
                                    runOnUiThread( new Runnable() {
                                        @Override
                                        public void run() {
                                            lineChart.removeAllViewsInLayout();
                                            setupData();
                                            find();
                                        }
                                    } );
                                }
                            } );
                    builder.create();
                    builder.show();
                }
            }
        } );
        options.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder( TidmarshDeviceActivity.this );
                builder.setTitle( "Info" ).setItems( mesures, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        options.setText( mesures[which] );
                        lineChart.removeAllViewsInLayout();
                        setupData();
                        find();
                    }
                } );
                builder.create();
                builder.show();
            }
        } );
        getSupportActionBar().setTitle( getTitle().toString() + " " + marker.getTitle() );
        options.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder( TidmarshDeviceActivity.this );
                builder.setTitle( "Info" ).setItems( mesures, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        options.setText( mesures[which] );
                        lineChart.removeAllViewsInLayout();
                        setupData();
                        find();
                    }
                } );
                builder.create();
                builder.show();
            }
        } );
        setupData();
        find();
    }

    public void find() {
        int index = 0;
        for (int i = 0; i < mesures.length; i++) {
            if (options.getText().toString() == mesures[i]) {
                index = i;
            }
        }
        boolean findd = false;
        int indexoftid = 0;
        for (int i = 0; i < tidmarshsensors.size(); i++) {
            if (tidmarshsensors.get( i ).getDevice().equals( marker.getTitle() )
                    && tidmarshsensors.get( i ).getName().contains( name[index] )) {
                findd = true;
                indexoftid = i;
            }
        }
        if (findd == true) {
            lineChart.setVisibility( View.VISIBLE );
            infopoint.setText( "" );
            ActuallyValueTidmarsh actuallyValueTidmarsh = new ActuallyValueTidmarsh();
            actuallyValueTidmarsh.execute( tidmarshsensors.get( indexoftid ).getHref() );
            ValueTidmarsh valueTidmarsh = new ValueTidmarsh();
            valueTidmarsh.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR, tidmarshsensors.get( indexoftid ).getHref() );
        } else {
            info.setText( "NO DATA FOUND" );
            lineChart.setVisibility( View.GONE );
            infopoint.setText( "" );
        }

    }

    private void buildgraphhour(final ArrayList<Data> donnees) {
        runOnUiThread( new Runnable() {
            @Override
            public void run() {
                LineData data = lineChart.getData();
                lineChart.clear();
                ILineDataSet set = data.getDataSetByIndex( 0 );
                if (set == null) {
                    set = CreateSet();
                    data.addDataSet( set );
                    set.setDrawValues( false );
                    set.isDrawCircleHoleEnabled();
                } else {
                    set.clear();
                    set = CreateSet();
                    data.addDataSet( set );
                    set.setDrawValues( false );
                    set.isDrawCircleHoleEnabled();
                }
                for (int i = 0; i < donnees.size(); i++) {
                    data.addEntry( new Entry( i, (float) donnees.get( i ).getDonnees() ), 0 );
                }
                lineChart.notifyDataSetChanged();
                lineChart.setData( data );
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
                        if ((int) value < donnees.size()) {
                            String date = donnees.get( (int) value ).getDate();
                            date = date.split( "T" )[0];
                            date = date.split( "-" )[2] + "/" + date.split( "-" )[1];
                            return date;
                        } else {
                            return "";
                        }
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
                rightAxis.setEnabled( true );
                // Add a limit line
                // reset all limit lines to avoid overlapping lines
                leftAxis.removeAllLimitLines();
                // limit lines are drawn behind data (and not on top)
                leftAxis.setDrawLimitLinesBehindData( true );
                lineChart.getDescription().setEnabled( false );
                lineChart.setTouchEnabled( true );
                lineChart.setPinchZoom( true );
                lineChart.setScaleEnabled( true );
                lineChart.getLegend().setEnabled( false );
                lineChart.animateY( 2000 );
                lineChart.setOnChartValueSelectedListener( new OnChartValueSelectedListener() {
                    @Override
                    public void onValueSelected(Entry e, Highlight h) {
                        String date = donnees.get( (int) e.getX() ).getDate();
                        date = date.split( "Z" )[0];
                        String jour = date.split( "T" )[0];
                        String heure = date.split( "T" )[1];
                        jour = jour.split( "-" )[2] + "/" + jour.split( "-" )[1] + "/" + jour.split( "-" )[0];
                        heure = heure.split( ":" )[0] + ":" + heure.split( ":" )[1];
                        date = jour + " (" + heure + ")";
                        int index = 0;
                        for (int i = 0; i < mesures.length; i++) {
                            if (options.getText().toString() == name[i]) {
                                index = i;
                            }
                        }
                        infopoint.setText( options.getText().toString() + " : " + e.getY() + " " + unities[index] + "  at " + date );
                        infopoint.setTextSize( 15f );

                    }

                    @Override
                    public void onNothingSelected() {
                    }
                } );
            }
        } );
    }

    private void Buildgraphbyperiod(ArrayList<Data> data) {
        if (period.getText().toString().equals( "All of time" )) {
            lineChart.setVisibility( View.VISIBLE );
            day.setVisibility( View.GONE );
            buildgraphhour( data );
        } else if (period.getText().toString().equals( "Year" )) {
            ArrayList<Data> data1 = new ArrayList<Data>();
            for (int i = 0; i < data.size(); i++) {
                if (data.get( i ).getDate().contains( date.getText().toString() )) {
                    data1.add( data.get( i ) );
                }
            }
            day.setVisibility( View.GONE );
            lineChart.setVisibility( View.VISIBLE );
            buildgraphhour( data1 );
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
            buildgraphhour( data1 );
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
            buildgraphhour( data1 );
        } else if (period.getText().toString().equals( "Day" )) {
            day.setText( "NO DATA FOUND" );
            day.setGravity( View.TEXT_ALIGNMENT_CENTER );
            day.setVisibility( View.VISIBLE );
            lineChart.setVisibility( View.GONE );
            infopoint.setText( "" );
            for (int i = 0; i < data.size(); i++) {
                if (data.get( i ).getDate().contains( date.getText().toString() )) {
                    String date = data.get( i ).getDate();
                    date = date.split( "Z" )[0];
                    String jour = date.split( "T" )[0];
                    String heure = date.split( "T" )[1];
                    jour = jour.split( "-" )[2] + "/" + jour.split( "-" )[1] + "/" + jour.split( "-" )[0];
                    heure = heure.split( ":" )[0] + ":" + heure.split( ":" )[1];
                    date = jour + " (" + heure + ")";
                    int index = 0;
                    for (int j = 0; j < mesures.length; j++) {
                        if (options.getText().toString() == name[j]) {
                            index = j;
                        }
                    }
                    day.setText( options.getText().toString() + " : " + data.get( i ).getDonnees() + " " + unities[index] + "  at " + date );
                    day.setTextSize( 15f );
                }
            }
        }
    }

    private void setupData() {
        LineData data = new LineData();
        data.setValueTextColor( Color.BLACK );
        lineChart.setData( data );
    }

    private ILineDataSet CreateSet() {
        LineDataSet set = new LineDataSet( null, options.getText().toString() );
        if (options.getText().toString().contains( "temperture" )) {
            set.setColor( Color.DKGRAY );
            set.setCircleColor( Color.RED );
        }
        if (options.getText().toString().contains( "illu" )) {
            set.setColor( Color.DKGRAY );
            set.setCircleColor( Color.YELLOW );
        }
        if (options.getText().toString().equals( "pressure" )) {
            set.setColor( Color.DKGRAY );
            set.setCircleColor( Color.GREEN );
        }
        if (options.getText().toString().equals( "humidity" )) {
            set.setColor( Color.DKGRAY );
            set.setCircleColor( Color.BLUE );
        }
        set.setAxisDependency( YAxis.AxisDependency.LEFT );
        // To show values of each point
        set.setDrawValues( true );
        set.setDrawCircles( true );
        return set;
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
                        arrayList.add( new Data( time, doub ) );
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
                Buildgraphbyperiod( count );
                //buildgraphhour( count );
            } else {
                lineChart.setVisibility( View.GONE );
                infopoint.setText( "NO DATA" );
            }
        }
    }

    public class ActuallyValueTidmarsh extends AsyncTask<String, Void, Double> {
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected Double doInBackground(String... urls) {
            JSONArray pp = new JSONArray();
            HttpHandler sh = new HttpHandler();
            String url = urls[0];
            double count = 0;
            String jsonStr = sh.makeServiceCall( url );
            Log.i( "debug", jsonStr );
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject( jsonStr );
                    count = jsonObj.getDouble( "value" );
                    return count;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return count;
        }
        @Override
        protected void onPostExecute(Double count) {
            int index = 0;
            for (int i = 0; i < mesures.length; i++) {
                if (options.getText().toString() == name[i]) {
                    index = i;
                }
            }
            info.setText( "Currently : " + count + " " + unities[index] );
            info.setTextSize( 15f );
        }
    }
}
