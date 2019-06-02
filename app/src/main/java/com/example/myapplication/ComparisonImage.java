package com.example.myapplication;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.ArrayList;
import java.util.List;


public class ComparisonImage extends AppCompatActivity {

    private ImageView oldImage, newImage;
    private String filenameObject, filenameScene;
    private TextView result;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);

        oldImage = findViewById(R.id.oldImageOpen);
        newImage = findViewById(R.id.newImageOpen);
        result = findViewById(R.id.resultComparison);

        Intent intent = getIntent();
        if(intent.hasExtra("oldImage"))
            oldImage.setImageResource(intent.getIntExtra("oldImage", R.drawable.dog));
        if(intent.hasExtra("newImage")){
            filenameScene = intent.getStringExtra("newImage");
            Uri uri = Uri.parse(filenameScene);
            newImage.setImageURI(uri);
        }
        if(intent.hasExtra("oldImagePath")){
            filenameObject = intent.getStringExtra("oldImagePath");
        }

        final boolean b = OpenCVLoader.initDebug();

        if(!b){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Library loading error", Toast.LENGTH_SHORT);
            toast.show();
        } else {
        }


        detectAndMatchObject();


    }

    public void detectAndMatchObject(){

        Mat img1 = Imgcodecs.imread(filenameObject);
        Mat img2 = Imgcodecs.imread(filenameScene);

        if (img1.empty() || img2.empty()) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Error loading images", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        ORB detector = ORB.create();
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();


        detector.detectAndCompute(img1, new Mat(), keypoints1, descriptors1);
        detector.detectAndCompute(img2, new Mat(), keypoints2, descriptors2);

        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        List<MatOfDMatch> knnMatches = new ArrayList<>();

        System.err.println(matcher);
        matcher.knnMatch(descriptors1, descriptors2, knnMatches,2);

        float ratioThresh = 0.7f;
        List<DMatch> listOfGoodMatches = new ArrayList<>();
        for (int i = 0; i < knnMatches.size(); i++) {
            if (knnMatches.get(i).rows() > 1) {
                DMatch[] matches = knnMatches.get(i).toArray();
                if (matches[0].distance < ratioThresh * matches[1].distance) {
                    listOfGoodMatches.add(matches[0]);
                }
            }
        }


        if(listOfGoodMatches.size()<=10)
            result.setText("Nope");
        else
            result.setText("Yeap");


    }


    public void onClick(View view) {

            Intent intent = new Intent(ComparisonImage.this, MainActivity.class);
            startActivity(intent);
            this.finish();
    }


}
