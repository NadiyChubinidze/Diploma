package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

    private List<String> filenameSource;
    private ImageView newImage;
    private String filenameObject;
    private TextView result;
    private String goodImage;
    private ProgressDialog pd;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);

        filenameSource = MainActivity.filenames;


        newImage = findViewById(R.id.newImageOpen);
        result = findViewById(R.id.resultComparison);
        goodImage="";

        Intent intent = getIntent();

        if(intent.hasExtra("image")){
            filenameObject = intent.getStringExtra("image");
            Uri uri = Uri.parse(filenameObject);
            newImage.setImageURI(uri);
        }


        final boolean b = OpenCVLoader.initDebug();

        if(!b){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Library loading error", Toast.LENGTH_SHORT);
            toast.show();
        } else {
        }

        pd = new ProgressDialog(this);
        pd.setTitle("Please, wait!");
        pd.setMessage("Check image");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMax(filenameSource.size());
        pd.setIndeterminate(true);
        pd.show();
        new Thread(myThread).start();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(pd.getProgress()<pd.getMax())
                    pd.incrementProgressBy(1);
                else {
                    pd.dismiss();

                    if(goodImage.length()==0) {
                        result.setText("Image not found");
                        Uri uri = Uri.parse(filenameObject);
                        newImage.setImageURI(uri);
                    }
                    else {
                        result.setText("This more similar");
                        Uri uri = Uri.parse(goodImage);
                        newImage.setImageURI(uri);
                    }
                }
            }
        };

    }

    private Runnable myThread = new Runnable() {
        @Override
        public void run() {
                try {
                    pd.setIndeterminate(false);
                    detectAndMatchObject();

                } catch (Throwable t) {
                }
        }
    };

    public void detectAndMatchObject(){

        ORB detector = ORB.create();
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        List<MatOfDMatch> knnMatches = new ArrayList<>();
        float ratioThresh = 0.7f;
        List<DMatch> listOfGoodMatches = new ArrayList<>();
        int goodCount=0;

        Mat img1 = Imgcodecs.imread(filenameObject);
        if (img1.empty()){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Error loading your images", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        Mat descriptors1 = new Mat();
        detector.detectAndCompute(img1, new Mat(), keypoints1, descriptors1);
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        Mat descriptors2 = new Mat();

        for(int k=0; k<filenameSource.size();++k) {

            handler.sendMessage(handler.obtainMessage());


            if(filenameSource.get(k).equals(filenameObject))
                continue;

            Mat img2 = Imgcodecs.imread(filenameSource.get(k));

            if (img2.empty()) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Error loading images", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            detector.detectAndCompute(img2, new Mat(), keypoints2, descriptors2);
            matcher.knnMatch(descriptors1, descriptors2, knnMatches, 2);

            for (int i = 0; i < knnMatches.size(); i++) {
                if (knnMatches.get(i).rows() > 1) {
                    DMatch[] matches = knnMatches.get(i).toArray();
                    if (matches[0].distance < ratioThresh * matches[1].distance) {
                        listOfGoodMatches.add(matches[0]);
                    }
                }
            }
            if(listOfGoodMatches.size()!=0 && listOfGoodMatches.size()>goodCount){
                goodImage = filenameSource.get(k);
                goodCount = listOfGoodMatches.size();
            }
            listOfGoodMatches.clear();
            knnMatches.clear();

        }

        if(goodCount<10)
            goodImage="";

    }


}
