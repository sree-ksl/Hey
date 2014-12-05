package com.example.Hey;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hello on 14/08/14.
 */
public class ViewImageActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        ImageView imageView = (ImageView)findViewById(R.id.imageView);

        Uri imageUri = getIntent().getData();

        //using external library to view images from the web - Picasso lib from Square
        Picasso.with(this).load(imageUri.toString()).into(imageView);

        //to time out for image
        Timer timer = new Timer();
        //schedule the timer
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                finish(); //takes us back to inbox
            }
        }, 10*1000); //the time is taken in milliseconds so multiply by 1000 to get seconds

    }

    private void setupActionBar(){
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}