package com.tidzamapp.sabri.tidzam;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Sabri on 27/12/2017.
 */

public class ShazamInatifActivity extends AppCompatActivity {

    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shazaminatif);
        imageButton = (ImageButton) findViewById( R.id.imageButton2 );
        imageButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketIOAudio audiosocket = new SocketIOAudio();
                audiosocket.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR, "" );
                startActivity( new Intent( ShazamInatifActivity.this,TidmarshLikeShazam.class ) );
            }
        } );
    }

}
