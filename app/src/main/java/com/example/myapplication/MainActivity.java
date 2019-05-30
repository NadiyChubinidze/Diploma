package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Random;


enum Image{
    ball,dog,cat,stop,chair
}

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    protected int randomImage(){

        int temp=-1;
        switch (Image.values()[new Random().nextInt(Image.values().length)]){
            case ball:
                temp=R.drawable.ball;
                break;
            case dog:
                temp=R.drawable.dog;
                break;
            case cat:
                temp=R.drawable.cat;
                break;
            case stop:
                temp=R.drawable.stop;
                break;
            case chair:
                temp=R.drawable.chair;
                break;
            default:
                temp=R.drawable.cat;

        }

       return temp;
    }

    public void onClick(View view) {

        if (view.getId() == findViewById(R.id.buttonLoad).getId()) {
            Intent intent = new Intent(MainActivity.this, ImageViews.class);
            intent.putExtra("image",randomImage());
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, ChooserPhoto.class);
            startActivity(intent);
        }
    }
}
