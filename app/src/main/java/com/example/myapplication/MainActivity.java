package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final int TAKE_PICTURE_REQUEST = 1;
    private static final int SELECT_PICTURE = 2;
    private String imagePath;
    private File appDirectory;
    private ImageView userPhoto;
    private Button buttonAnalysis;
    public static List<String> filenames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        createDirectory();
        userPhoto = findViewById(R.id.userPhoto);
        buttonAnalysis = findViewById(R.id.buttonFindPhoto);
        filenames = new ArrayList<>();

        buttonAnalysis.setEnabled(false);
        getPicturesFilename();
    }

    public void getPicturesFilename(){

        ArrayList<File> filOne = listFilesWithSubFolders(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera"));

        for(File file: filOne){
            filenames.add(file.getAbsolutePath());
        }

    }

    public ArrayList<File> listFilesWithSubFolders(File dir) {
        ArrayList<File> files = new ArrayList<>();
        for (File file : dir.listFiles()) {
            if (file.isDirectory())
                files.addAll(listFilesWithSubFolders(file));
            else
                if((!file.getPath().contains("/.")
                        && !file.getPath().contains("/com.")
                        && !file.getPath().contains("/org."))
                && (file.getPath().contains(".jpg")
                        || file.getPath().contains(".png")
                        || file.getPath().contains(".jpeg")))
                files.add(file);
        }
        return files;
    }


    public void onClick(View view) {

        switch (view.getId()){
            case R.id.buttonPhoto:
                takePhoto(view);
                break;
            case R.id.buttonChooser:
                takePicture();
                break;
            case R.id.buttonFindPhoto:
                findSimilarPicture();
                break;

        }

    }

    public void findSimilarPicture(){
        Intent intent = new Intent(this,ComparisonImage.class);
        intent.putExtra("image",imagePath);
        startActivity(intent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_OK) {
            switch(requestCode){
                case  TAKE_PICTURE_REQUEST:
                    if (data != null) {
                            Bitmap bitmap = null;
                            Object obj = data.getExtras().get("data");
                            if (obj instanceof Bitmap) {
                                bitmap = (Bitmap) obj;
                                userPhoto.setImageBitmap(bitmap);
                                saveAppPhoto(bitmap);
                                System.err.println(imagePath);
                            }
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Image is not found", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    break;
                case SELECT_PICTURE:
                    Uri selectedImageUri = data.getData();
                    imagePath = getPath(selectedImageUri);
                    userPhoto.setImageURI(selectedImageUri);
                    break;
            }
            buttonAnalysis.setEnabled(true);
        } else {

        }
    }

    public void saveAppPhoto(Bitmap bitmap){

        OutputStream fOut = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());

        try{
            File file = new File(appDirectory,  "Amage_" + currentDateandTime +".jpg");
            imagePath = file.getAbsolutePath();
            fOut = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(),  file.getName());

        } catch (Exception e){
            e.printStackTrace();
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Save is failed", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    public void takePicture(){

        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            choosePicture();

        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    SELECT_PICTURE);
        }
    }

    public void choosePicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }


    public String getPath(Uri uri) {

        String path = null;
            String[] projection = { MediaStore.Files.FileColumns.DATA };
            Cursor cursor = getContentResolver().query(uri, projection, null,
                    null, null);
            if(cursor == null)
                path = uri.getPath();
            else {
                cursor.moveToFirst();
                int column_index = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(column_index); cursor.close();
            }
        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }


    public void takePhoto(View view) {

        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            getPicture();

        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                    TAKE_PICTURE_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case TAKE_PICTURE_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPicture();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Permission is denied", Toast.LENGTH_SHORT);
                    return;
                }
                break;
            case  SELECT_PICTURE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosePicture();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Permission is denied", Toast.LENGTH_SHORT);
                    return;
                }
                break;
        }
    }

    private void getPicture() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE_REQUEST);
    }

    private void createDirectory() {
        appDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Amage");

        if (!appDirectory.exists())
            appDirectory.mkdirs();
    }
}
