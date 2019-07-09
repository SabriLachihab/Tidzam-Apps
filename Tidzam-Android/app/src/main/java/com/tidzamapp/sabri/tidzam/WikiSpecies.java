package com.tidzamapp.sabri.tidzam;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Sabri on 15/12/2017.
 */

public class WikiSpecies extends AppCompatActivity {
    private TextView info;
    private TextView name;
    private ImageView imageView;
    private ArrayList<Integer> comptageannuel;
    private int compt;
    private String namespecies;
    private int week;
    private ArrayList<StatsByMonth> months;
    private BarChart graph;
    private List<Sensors> sensors;
    private String[] stringmonth = {"January","February","March","April","May","June","July",
            "August", "September", "October", "November", "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wikipage);
        info = (TextView) findViewById(R.id.description);
        name = (TextView) findViewById(R.id.namespecies);
        imageView = (ImageView) findViewById(R.id.imageViewspecies);
        comptageannuel = new ArrayList<Integer>();
        compt =0;
        week = 0;
        months = new ArrayList<StatsByMonth>();
        for(int i =1;i<13;i++)
        {
            months.add( new StatsByMonth( i,0 ));
        }
        namespecies = SampleDatabaseActivity.getInstence().getName();
        name.setText(namespecies);
        sensors = SplashScreen.getInstence().getListsensors();
        String url = "https://tidzam.media.mit.edu/static/img/"+ namespecies +".png";
        Log.i("debug",url);
        DownloadImageTask downimage = new DownloadImageTask();
        downimage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);
        graph = (BarChart) findViewById( R.id.barmonth );
        WikipediaExtract(namespecies);
        Animation a = AnimationUtils.loadAnimation(WikiSpecies.this, R.anim.downappear);
        Animation b = AnimationUtils.loadAnimation(WikiSpecies.this, R.anim.leftappear);
        Animation c = AnimationUtils.loadAnimation(WikiSpecies.this, R.anim.rightappear);
        a.reset();
        b.reset();
        c.reset();
        info.clearAnimation();
        info.startAnimation(b);
        imageView.clearAnimation();
        imageView.startAnimation(a);
        name.clearAnimation();
        name.startAnimation(c);
        initializeSpecies( sensors);

    }

    public void WikipediaExtract(String specie) {
        WikipediaJson wikipediaJson = new WikipediaJson();
        if (specie.contains("birds-")) {
            String[] val = specie.split("birds-");
            specie = val[1];
            if (specie == "red_winged_blackbird") {
                specie = "Red-winged_blackbird";
            }
        }
        if(specie.equals( "spring_peepers" ))
        {
            specie="spring_peeper";
        }
        if (specie.equals( "crickets" )) {
            specie = "Cricket_(insect)";
        }
        wikipediaJson.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,specie);
    }


    private void initializeSpecies(List<Sensors> links) {

        for (int i = 0;i<links.size();i++)
        {
            try {
                if(links.get(i).getName().equals(namespecies))
                {
                    Log.i( "debug",links.get( i ).getHref() );
                    String[] ter = links.get(i).getHref().split("id=");
                    String urlfinal= "http://chain-api.media.mit.edu/aggregate_data/?sensor_id="+ter[1]+"&aggtime=1d";
                    Log.i("debug", urlfinal);
                    ParseJsonstats getname = new ParseJsonstats();
                    getname.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,urlfinal);
                }
            }
            catch (Exception e )
            {
                e.printStackTrace();
            }
        }
    }

    private void PrintGraph(ArrayList<StatsByMonth> links) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        for(int i=0;i<months.size();i++)
        {
            Log.i("debug", ""+months.get( i ).getCount());
            entries.add( new BarEntry( months.get( i ).getMonth(),months.get(i).getCount(),""+months.get( i ).getMonth()));
        }
        BarDataSet dataSet = new BarDataSet( entries,"Count" );
        BarData data = new BarData( dataSet );
        Legend leg = graph.getLegend();
        leg.setEnabled(false);
        dataSet.setValueFormatter(new LargeValueFormatter());
        XAxis xAxis = graph.getXAxis();
        xAxis.setDrawGridLines(false);
        graph.notifyDataSetChanged();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        graph.setData(data);
        YAxis leftAxis = graph.getAxisLeft();
        YAxis rAxis = graph.getAxisRight();
        rAxis.setEnabled(false);
        leftAxis.removeAllLimitLines();
        graph.setDescription(null);
        graph.invalidate();
        graph.setHorizontalScrollBarEnabled( true );
        graph.animateY(4000);
        graph.setOnChartValueSelectedListener( new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                final AlertDialog.Builder info = new AlertDialog.Builder( WikiSpecies.this);
                String message = "";
                for(int i=0;i<months.size();i++)
                {
                    Log.i("debug",months.get( i ).getMonth() +" "+months.get( i ).getCount());
                    message+=stringmonth[i]+" : "+months.get( i ).getCount();
                    message+="\n";
                }
                info.setNegativeButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                } );
                info.setMessage( message );
                info.create();
                info.show();
            }
            @Override
            public void onNothingSelected() {

            }
        } );
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                Log.i("debug","MAMA");
                return mIcon11;
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

    public class ParseJsonstats extends AsyncTask<String, Void,ArrayList<StatsByMonth>> {
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected ArrayList<StatsByMonth> doInBackground(String... urls) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            JSONArray pp = new JSONArray();
            HttpHandler sh = new HttpHandler();
            String url = urls[0];
            int count=0;
            String jsonStr = sh.makeServiceCall(url);
            Log.i("debug", jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray links = jsonObj.getJSONArray("data");
                    for (int i=0;i<links.length();i++)
                    {
                        try {
                            count = links.getJSONObject( i ).getInt( "count" );
                            String timestamp = links.getJSONObject( i ).getString( "timestamp" );
                            {
                                if(timestamp.contains( ""+year ))
                                {
                                    String[] val = timestamp.split( "-" );
                                    int mois = Integer.parseInt(val[1]);
                                    Log.i("debug",mois+"");
                                    months.get( mois-1 ).setCount( months.get(mois-1).getCount()+count);
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    return months;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return months;
        }
        @Override
        protected void onPostExecute(ArrayList<StatsByMonth> links) {
            PrintGraph(links);
        }
    }

    public class WikipediaJson extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {
            HttpHandler sh = new HttpHandler();
            String url = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=" + urls[0];
            Log.i( "debug", url );
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject links = jsonObj.getJSONObject("query").getJSONObject("pages");
                    Iterator<String> keys = links.keys();
                    String key = null;
                    if (keys.hasNext())
                    {
                        key = (String) keys.next(); // First key in your json object
                    }
                    String extract = "";
                    if (key != null) {
                        JSONObject objPagesNo = links.getJSONObject(key);
                        extract = objPagesNo.getString("extract");
                    }
                    //JSONObject a = new JSONObject(links);
                    return extract;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return "No Wikipedia text";
        }
        @Override
        protected void onPostExecute(String links) {
            info.setText(links);
        }
    }
}
