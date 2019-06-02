package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ImageViews extends AppCompatActivity {


    private static int TAKE_PICTURE_REQUEST = 1;
    private ImageView sourceImage;
    private ImageView imageView;
    private Button buttonAnalize;
    private int srcImage;
    private File directory;
    private String pathPhotoFirst, pathPhotoSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity);

        ActionBar actionBar =  getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        createDirectory();
        imageView = findViewById(R.id.newImage);
        sourceImage = findViewById(R.id.oldImage);
        buttonAnalize = findViewById(R.id.buttonAnalize);
        buttonAnalize.setVisibility(View.GONE);

        Intent intent = getIntent();
        srcImage = intent.getIntExtra("image",R.drawable.dog);
        sourceImage.setImageResource(srcImage);
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

        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                    TAKE_PICTURE_REQUEST);
        }
    }

    public void onClickAnalize(View view) {

        Intent intent = new Intent(ImageViews.this, ComparisonImage.class);
        intent.putExtra("oldImage",srcImage);
        intent.putExtra("oldImagePath",pathPhotoSecond);
        intent.putExtra("newImage",pathPhotoFirst);
        startActivity(intent);

        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.hasExtra("data")) {
                    Bitmap bitmap=null;
                        Object obj = data.getExtras().get("data");
                        if (obj instanceof Bitmap) {
                            bitmap = (Bitmap) obj;
                            imageView.setImageBitmap(bitmap);
                        }

                        OutputStream fOut = null;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                        String currentDateandTime = sdf.format(new Date());

                        try{
                            File file = new File(directory, "IMG_" + currentDateandTime +".jpg"); // создать уникальное имя для файла основываясь на дате сохранения
                            pathPhotoFirst = file.getAbsolutePath();
                            fOut = new FileOutputStream(file);

                            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // сохранять картинку в jpeg-формате с 85% сжатия.
                            fOut.flush();
                            fOut.close();
                            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(),  file.getName());

                            BitmapDrawable drawable = (BitmapDrawable) sourceImage.getDrawable();
                            bitmap = drawable.getBitmap();

                            file = new File(directory,  "sourceImage.jpg");
                            pathPhotoSecond = file.getAbsolutePath();
                            fOut = new FileOutputStream(file);

                            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // сохранять картинку в jpeg-формате с 85% сжатия.
                            fOut.flush();
                            fOut.close();
                            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(),  file.getName());
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Unknown error", Toast.LENGTH_SHORT);
                toast.show();
            }
            buttonAnalize.setVisibility(View.VISIBLE);
        }
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
    }

    private void createDirectory() {
        directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Amage");

        if (!directory.exists())
            directory.mkdirs();
    }

}


