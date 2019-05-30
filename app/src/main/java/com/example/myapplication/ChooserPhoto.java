package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

public class ChooserPhoto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_pictures);

        ActionBar actionBar =  getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
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

    public void onClickChoose(View view){

        Intent intent = new Intent(ChooserPhoto.this,ImageViews.class);
        switch (view.getId()){
            case R.id.ballImage:
                intent.putExtra("image",R.drawable.ball);
                break;
            case R.id.dogImage:
                intent.putExtra("image",R.drawable.dog);
                break;
            case R.id.catImage:
                intent.putExtra("image",R.drawable.cat);
                break;
            case R.id.stopImage:
                intent.putExtra("image",R.drawable.stop);
                break;
            case R.id.chairImage:
                intent.putExtra("image",R.drawable.chair);
                break;
        }

        startActivity(intent);
    }


}
