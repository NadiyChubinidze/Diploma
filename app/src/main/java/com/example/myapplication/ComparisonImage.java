package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.*;
import org.opencv.features2d.AgastFeatureDetector;
import org.opencv.features2d.FastFeatureDetector;

import org.opencv.core.Core;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;


public class ComparisonImage extends AppCompatActivity {

    private ImageView oldImage, newImage;
    private String filenameObject, filenameScene;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);

        final boolean b = OpenCVLoader.initDebug();

        oldImage = findViewById(R.id.oldImageOpen);
        newImage = findViewById(R.id.newImageOpen);

        Intent intent = getIntent();
        if(intent.hasExtra("oldImage"))
            oldImage.setImageResource(intent.getIntExtra("oldImage", R.drawable.dog));

        if(!b){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Library loading error", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        }

        filenameObject = "/res/drawable/dog.png";
        Mat imgObj = Imgcodecs.imread(filenameObject);

    }

    public void detectAndMatchObject(){

        ORB detector = ORB.create();





    }


    public void onClick(View view) {

            Intent intent = new Intent(ComparisonImage.this, MainActivity.class);
            startActivity(intent);
            this.finish();
    }


}
