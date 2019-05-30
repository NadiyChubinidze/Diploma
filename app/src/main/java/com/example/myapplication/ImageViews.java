package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;



public class ImageViews extends AppCompatActivity {


    private static int TAKE_PICTURE_REQUEST = 1;
    private ImageView sourceImage;
    private ImageView imageView;
    private Button buttonAnalize;
    private Uri outputFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity);

        ActionBar actionBar =  getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.newImage);
        sourceImage = findViewById(R.id.oldImage);
        buttonAnalize = findViewById(R.id.buttonAnalize);
        buttonAnalize.setVisibility(View.GONE);

        Intent intent = getIntent();

        sourceImage.setImageResource(intent.getIntExtra("image",R.drawable.cat));
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

    public void onClickPhoto(View view) {

        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            getThumbnailPicture();
            //saveFullImage();

        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                    TAKE_PICTURE_REQUEST);
        }
    }

    public void onClickAnalize(View view) {

        ImageView oldImage = (ImageView) findViewById(R.id.oldImage);
        ImageView newImage = (ImageView) findViewById(R.id.newImage);

        Intent intent = new Intent(ImageViews.this, ComparisonImage.class);

        intent.putExtra("oldImage",oldImage.getId());
        intent.putExtra("newImage",newImage.getId());

        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
            // Проверяем, содержит ли результат маленькую картинку
            if (data != null) {
                if (data.hasExtra("data")) {
                    Bitmap thumbnailBitmap = data.getParcelableExtra("data");
                    imageView.setImageBitmap(thumbnailBitmap);

                }
            } else {
                // Какие-то действия с полноценным изображением,
                // сохраненным по адресу outputFileUri
                imageView.setImageURI(outputFileUri);
            }
        }

        buttonAnalize.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getThumbnailPicture();

                } else {
                    // permission denied
                }
                return;
        }
    }

    //Этот метод работает
    private void getThumbnailPicture() {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, TAKE_PICTURE_REQUEST);

            System.out.println(Environment.DIRECTORY_PICTURES);


    }

    private void saveFullImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(),
                "test.jpg");

        outputFileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, TAKE_PICTURE_REQUEST); //  java.lang.IllegalStateException: Could not execute method for android:onClick
    }

}


