package com.tidzamapp.sabri.tidzam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

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

import java.util.ArrayList;

/**
 * Created by Sabri on 08/01/2018.
 */

public class StatsBirdsActivity extends AppCompatActivity {
    public static StatsBirdsActivity instence;
    final String[] birdsname = {"mallard", "northern_cardinal", "red_winged_blackbird", "mourning_dove",
            "downy_woodpecker", "herring_gull", "american_crow", "song_sparrow", "american_robin",
            "black_capped_chickadee", "tufted_titmouse", "american_goldfinch", "canada_goose",
            "barn_swallow", "blue_jay"};
    private PieChart pieChart;
    private ArrayList<Stat> species;
    private ArrayList<Sensors> listsensors;
    private int find;
    private int inprogress;
    private ProgressDialog progressDialog;
    private String period;
    private ArrayList<StatsCount> birds;
    private String birds_name;
    private TextView choosedate;
    private TextView chooseperiod;
    private int weeknumber;
    private String[] month = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};
    private String[] daynumber = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
            "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    private int[] colors = {
            Color.MAGENTA, Color.CYAN, Color.BLUE, Color.BLACK, Color.DKGRAY, Color.LTGRAY, Color.GRAY,
            Color.rgb( 255, 140, 0 ), Color.GREEN, Color.RED, Color.rgb( 246, 171, 231 ),
            Color.rgb( 121, 82, 48 ), Color.rgb( 51, 153, 255 )
            , Color.rgb( 153, 255, 153 ), Color.rgb( 0, 102, 51 )};
    private RecyclerView recycler;
    private Legend_Recycler leg_recycler;
    private String nomspecie;

    public StatsBirdsActivity() {
        instence = this;
    }

    public static StatsBirdsActivity getInstence() {
        return instence;
    }

    public String getBirds_name() {
        return birds_name;
    }

    public String getNomspecie() {
        return nomspecie;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_piechartdatabase );
        pieChart = (PieChart) findViewById( R.id.sampledatabase );
        species = StatsActivity.getInstance().getBirds();
        choosedate = StatsActivity.getInstance().getChoosedate();
        chooseperiod = StatsActivity.getInstance().getChooseperiod();
        weeknumber = StatsActivity.getInstance().getWeeknumber();
        recycler = (RecyclerView) findViewById( R.id.recyclerlegbirds );
        recycler.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
        leg_recycler = new Legend_Recycler( getApplicationContext(), birdsname, colors, new Legend_Recycler.RecyclerItemClickListenerSpecies() {
            @Override
            public void onClickListener(String species, int position) {
                nomspecie = species;
                startActivity( new Intent( StatsBirdsActivity.this, WikiBirdsStatActvity.class ) );

            }
        } );
        recycler.setAdapter( leg_recycler );
        PrimarySample();
    }

    private void BuildPrimarySample() {
        ArrayList<PieEntry> yvalues = new ArrayList<PieEntry>();
        int total = 0;
        for (int i = 0; i < birdsname.length; i++) {
            yvalues.add( new PieEntry( (float) Countexact( birdsname[i], species ), birdsname[i].replace( "_", " " ) ) );
            total += Countexact( birdsname[i], species );
            }
        final int tot = total;
        PieDataSet dataSet = new PieDataSet( yvalues, "" );
        dataSet.setValueFormatter( new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                {
                    if (entry.getY() / tot >= 0.03) {
                        return String.format( "%.1f", (entry.getY() / tot) * 100 ) + " %";
                    } else {
                        return "";
                    }
                }
            }
        } );
        dataSet.setSliceSpace( 2f );
        dataSet.setValueTextSize( 15f );
        dataSet.setValueTextColor( Color.WHITE );
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
        pieChart.animateY( 4000 );
        pieChart.setOnChartValueSelectedListener( new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                birds_name = "";
                PieEntry pe = (PieEntry) e;
                birds_name += pe.getLabel().replace( "_", " " );
                birds_name += "\n " + (String.format( "%.1f", (e.getY() / tot) * 100 ) + " %");
                pieChart.setCenterText( birds_name );
                pieChart.setCenterTextColor( Color.BLACK );
            }
            @Override
            public void onNothingSelected() {

            }
        } );
    }

    private void PrimarySample() {
        new Thread( (new Runnable() {
            @Override
            public void run() {
                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        BuildPrimarySample();
                    }
                } );
            }
        }) ).start();
    }

    public int Countexact(String name, ArrayList<Stat> stats) {
        int count = 0;
        int reste = 0;
        ArrayList<String> rech = new ArrayList<String>();
        if (chooseperiod.getText().toString().equals( "Week" )) {
            String[] val = choosedate.getText().toString().split( "Week" );
            int week0 = weeknumber + 1;
            String[] datemoisannee = choosedate.getText().toString().split( " " );
            String recherche = datemoisannee[0] + "-";
            for (int m = 0; m < month.length; m++) {
                if (datemoisannee[1].equals( month[m] )) {
                    if (m <= 8) {
                        recherche += "0" + (m + 1);
                    }
                }
            }
            if (week0 <= 4) {
                week0--;
                for (int m = (week0) * 7; m < (week0 + 1) * 7; m++) {
                    rech.add( recherche + "-" + daynumber[m] );
                }
            } else {
                rech.add( recherche + "-" + "29" );
                rech.add( recherche + "-" + "30" );
                rech.add( recherche + "-" + "31" );
            }
        }
        for (int i = 0; i < stats.size(); i++) {
            if (stats.get( i ).getName().contains( name )) {
                for (int j = 0; j < stats.get( i ).getHistories().size(); j++) {
                    boolean passe = false;
                    int p = 0;
                    int fini = stats.get( i ).getHistories().get( j ).size();
                    for (int l = 0; l < stats.get( i ).getHistories().get( j ).size(); l++) {
                        if (chooseperiod.getText().toString().equals( "Day" )) {
                            if (stats.get( i ).getHistories().get( j ).get( l ).getDate().contains( choosedate.getText().toString() )) {
                                count += stats.get( i ).getHistories().get( j ).get( l ).getCount();
                            }
                        } else if (chooseperiod.getText().toString().equals( "Year" )) {
                            if (stats.get( i ).getHistories().get( j ).get( l ).getDate().contains( choosedate.getText().toString() )) {
                                count += stats.get( i ).getHistories().get( j ).get( l ).getCount();
                            }
                        } else if (chooseperiod.getText().toString().equals( "Month" )) {
                            String[] val = choosedate.getText().toString().split( " " );
                            String recherche = val[1] + "-";
                            for (int m = 0; m < month.length; m++) {
                                if (val[0].equals( month[m] )) {
                                    if (m <= 8) {
                                        recherche += "0" + (m + 1);
                                    }
                                }
                            }
                            if (stats.get( i ).getHistories().get( j ).get( l ).getDate().contains( recherche )) {
                                count += stats.get( i ).getHistories().get( j ).get( l ).getCount();
                            }
                        } else if (chooseperiod.getText().toString().equals( "Week" ) && passe == false) {
                            if (stats.get( i ).getHistories().get( j ).get( l ).getDate().contains( rech.get( reste ) )) {
                                if (l + 7 >= fini) {
                                    for (int t = l; t < fini; t++) {
                                        count += stats.get( i ).getHistories().get( j ).get( t ).getCount();
                                    }
                                } else {
                                    for (int t = l; t < l + 8; t++) {
                                        count += stats.get( i ).getHistories().get( j ).get( t ).getCount();
                                    }
                                    passe = true;
                                }
                            }
                        } else if (chooseperiod.getText().toString().equals( "All of time" )) {
                            count += stats.get( i ).getHistories().get( j ).get( l ).getCount();
                        }
                    }
                }
            }
        }
        return count;
    }
}
