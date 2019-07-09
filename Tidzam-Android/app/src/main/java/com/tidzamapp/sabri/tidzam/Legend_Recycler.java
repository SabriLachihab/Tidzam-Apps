package com.tidzamapp.sabri.tidzam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

/**
 * Created by Sabri on 12/01/2018.
 */

public class Legend_Recycler extends RecyclerView.Adapter<Legend_Recycler.SongVieHolder> {

    public static Legend_Recycler instance;
    final String[] birdsname = {"mallard", "northern_cardinal", "red_winged_blackbird", "mourning_dove",
            "downy_woodpecker", "herring_gull", "american_crow", "song_sparrow", "american_robin",
            "black_capped_chickadee", "tufted_titmouse", "american_goldfinch", "canada_goose",
            "barn_swallow", "blue_jay"};
    private final RecyclerItemClickListenerSpecies listener;
    private Context context;
    private String[] speciesArrayList;
    private int selection;
    private int[] colors;

    public Legend_Recycler(RecyclerItemClickListenerSpecies listener) {
        this.listener = listener;
        instance = this;
    }

    public Legend_Recycler(Context context, String[] speciesArrayList, int[] colors, RecyclerItemClickListenerSpecies listener) {
        this.context = context;
        this.colors = colors;
        this.speciesArrayList = speciesArrayList;
        this.listener = listener;
    }

    public static Legend_Recycler getInstance() {
        return instance;
    }

    public static Bitmap createImage(int width, int height, int color) {
        Bitmap bitmap = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888 );
        Canvas canvas = new Canvas( bitmap );
        Paint paint = new Paint();
        paint.setColor( color );
        canvas.drawRect( 0F, 0F, (float) width, (float) height, paint );
        return bitmap;
    }

    public int getSelection() {
        return selection;
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }

    @Override
    public SongVieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.legendrow, parent, false );
        return new SongVieHolder( view );
    }

    @Override
    public void onBindViewHolder(SongVieHolder holder, int position) {
        String species = speciesArrayList[position];
        if (species != null) {
            holder.tv_titre.setText( species );
            String specie = species;
            for (int i = 0; i < birdsname.length; i++) {
                if (specie.equals( birdsname[i] )) {
                    specie = "birds-" + species;
                }
            }
            if (species.equals( "unknow" )) {
                specie = "quiet";
            }
            String url = "https://tidzam.media.mit.edu/static/img/" + specie + ".png";
            holder.iv_color.setImageBitmap( createImage( 40, 40, colors[position] ) );
            Log.i( "debug url ", url );
            new DownloadImageTask( holder.iv_image ).executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR, url );
        }
        holder.bind( species, listener );
    }

    @Override
    public int getItemCount() {
        return speciesArrayList.length;
    }

    public interface RecyclerItemClickListenerSpecies {
        void onClickListener(String species, int position);
    }

    public static class SongVieHolder extends RecyclerView.ViewHolder {
        private TextView tv_titre;
        private ImageView iv_image;
        private ImageView iv_color;

        public SongVieHolder(View itemView) {
            super( itemView );
            tv_titre = itemView.findViewById( R.id.speciesleg );
            iv_image = itemView.findViewById( R.id.iv_species );
            iv_color = itemView.findViewById( R.id.iv_color );
        }

        public void bind(final String specie, final RecyclerItemClickListenerSpecies listener) {
            itemView.setOnClickListener( new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 listener.onClickListener( specie, getLayoutPosition() );
                                             }
                                         }
            );
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL( urldisplay ).openStream();
                mIcon11 = BitmapFactory.decodeStream( in );
            } catch (Exception e) {
                Log.e( "Error", e.getMessage() );
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap( result );
        }
    }
}
