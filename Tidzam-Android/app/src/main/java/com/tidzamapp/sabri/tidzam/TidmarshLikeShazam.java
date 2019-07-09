package com.tidzamapp.sabri.tidzam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Sabri on 20/12/2017.
 */

public class TidmarshLikeShazam extends AppCompatActivity {

    private ImageButton button;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_shaztidmarsh );
        button = (ImageButton) findViewById( R.id.imageButton );
        result = (TextView) findViewById( R.id.shazamspecies );
        button.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity( new Intent( TidmarshLikeShazam.this,ShazamInatifActivity.class ) );
                }
            } );
    }
}
