package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.objdetect.*;


public class ComparisonImage extends AppCompatActivity {

    private ImageView oldImage, newImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);

        ActionBar actionBar =  getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        final boolean b = OpenCVLoader.initDebug();

        if(!b){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Library loading error", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClick(View view) {

            Intent intent = new Intent(ComparisonImage.this, MainActivity.class);
            startActivity(intent);
    }


}
