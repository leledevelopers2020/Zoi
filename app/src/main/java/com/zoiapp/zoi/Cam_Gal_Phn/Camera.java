package com.zoiapp.zoi.Cam_Gal_Phn;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.zoiapp.zoi.R;
import com.zoiapp.zoi.UserRequired.UserAddress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Camera extends AppCompatActivity {
    private static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    private ArrayList<Uri> CameraImageList = new ArrayList<Uri>();
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    static int count=0;
    static int imagelistcount;
    Bitmap bitmap;
    Button submit,addFromCamera;
    ImageButton addMore,previmg,nextimg,delimg,retake;
    LinearLayout linearlayoutimage;
    LayoutInflater inflater;
    ImageView slctdimg;
    private Uri singleuri;
    String currentPhotoPath,userID;
    public int type;//to check wheather you upoad data using gallery or camera.....1.uploading single pic through gallery 2.uploading multiple images through gallery  3.upload single/multiple pic through camera
    public static int no_of_capture_images=0;
    private static int currentimageposition ;
    private static int retakepressed=0;
    File image;

    FileOutputStream outputStream;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        inflater=LayoutInflater.from(this);
        final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid()).child("Position");
        linearlayoutimage=(LinearLayout)findViewById(R.id.llimg);
        //EditText

        slctdimg=(ImageView)findViewById(R.id.slctdimg);
        //image buttons for next and prev images
        nextimg=(ImageButton)findViewById(R.id.nextimg);
        previmg=(ImageButton)findViewById(R.id.previmg);
        delimg=(ImageButton)findViewById(R.id.delimg);
        retake=(ImageButton)findViewById(R.id.retakeimg);
        //buttons
        addFromCamera = (Button)findViewById(R.id.camerabtn);
        submit = (Button) findViewById(R.id.submit);
        addMore = (ImageButton) findViewById(R.id.addmore_button);


        addFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tag","camerapic");
                askCameraPermission();
                addMore.setVisibility(View.VISIBLE);
                addFromCamera.setVisibility(View.VISIBLE);

            }
        });
        addMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                delimg.setVisibility(View.VISIBLE);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetConnectivity()) {
                    Intent address = new Intent(Camera.this, UserAddress.class);
                    address.putExtra("activity", "Camera");
                    address.putParcelableArrayListExtra("images", (ArrayList<? extends Parcelable>) CameraImageList);
                    startActivity(address);
                }
                else {
                    Toast.makeText(Camera.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private Boolean internetConnectivity()
    {
        boolean connectivityInfo=false;
        ConnectivityManager connection=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=connection.getActiveNetworkInfo();
        if(activeNetwork!=null) {
            if (activeNetwork.getType()==ConnectivityManager.TYPE_MOBILE||activeNetwork.getType()==ConnectivityManager.TYPE_WIFI) {
                connectivityInfo = true;
            }
        }
        return connectivityInfo;
    }

    private void  askCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERM_CODE);
        } else {
            Log.d("tag","camera askCamera");
            dispatchTakePictureIntent();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(Camera.this, "Camera Permission is Requried to Use Camers", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.d("tag","camera "+photoFile);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.zoiapp.zoi.fileprovider",
                        photoFile);
                Log.d("tag","camera "+photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }

    }


    //this method is used for lunching camera and gets the image for stored location

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.d("tag","camera "+storageDir);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        Log.d("tag","camera "+image);
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        Log.d("tag","camera onActivity1");
        Log.d("tag","camera onActivity1 "+requestCode+ " "+resultCode+ " "+data);
        super.onActivityResult(requestCode, resultCode,data);
       // Bundle
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK )
        {
            Log.d("tag","camera onActivity2");
            retake.setVisibility(View.VISIBLE);
            File f = new File(currentPhotoPath);
            if(retakepressed==1)
            {
                type = 3;

                Log.d("tag","camera onActivity3");
                Uri imageuri = Uri.fromFile(f);
                linearlayoutimage.setVisibility(View.VISIBLE);
                CameraImageList.set(currentimageposition,imageuri);
                imageconversion();
                retakepressed=0;

            }

            else
            {
                type = 3;
                imagelistcount = CameraImageList.size();
                currentimageposition = CameraImageList.size();
                count = CameraImageList.size() + 1;
                Uri imageuri = Uri.fromFile(f);
                linearlayoutimage.setVisibility(View.VISIBLE);
                if (CameraImageList.size() > 0) {
                    previmg.setVisibility(View.VISIBLE);
                }
                CameraImageList.add(imageuri);
                imageconversion();
                no_of_capture_images++;
                nextimg.setVisibility(View.INVISIBLE);
            }
            //the below code is used to store the capture imges to phone gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
            submit.setVisibility(View.VISIBLE);
            addFromCamera.setText("add more from camera -->`");
            delimg.setVisibility(View.VISIBLE);

        }
        else
        {
            Log.d("tag","camera onActivity4");
        }
        retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retakepressed=1;
                dispatchTakePictureIntent();
                //   delimg.setVisibility(View.VISIBLE);
            }
        });
        nextimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentimageposition++;
                Log.d("tag","currentimagepos"+currentimageposition);
                imageconversion();
                if(currentimageposition>0)
                {
                    previmg.setVisibility(View.VISIBLE);
                }
                if(currentimageposition==count-1)
                {
                    nextimg.setVisibility(View.INVISIBLE);
                }
            }
        });
        previmg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                currentimageposition--;
                Log.d("tag","currentimagepos"+currentimageposition);
                imageconversion();
                if (currentimageposition <= 0) {
                    previmg.setVisibility(View.INVISIBLE);
                }
                if (currentimageposition < count - 1) {
                    nextimg.setVisibility(View.VISIBLE);
                }
                //   Log.d("tag","current image pos changed"+currentimageposition);
            }


        });

        delimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = count - 1;
                if(CameraImageList.size()!=0)
                {
                    {
                        if (CameraImageList.size() - 1 == 0) {
                            CameraImageList.remove(currentimageposition);
                            CameraImageList.ensureCapacity(CameraImageList.size() - 1);
                            //  slctdimg.setImageBitmap(null);
                            submit.setVisibility(View.INVISIBLE);
                            delimg.setVisibility(View.INVISIBLE);
                            retake.setVisibility(View.INVISIBLE);
                            slctdimg.setImageDrawable(getResources().getDrawable(R.drawable.border_side));
                            addFromCamera.setText("Camera");
                        } else {
                            if (currentimageposition == CameraImageList.size() - 1) {
                                CameraImageList.remove(currentimageposition);
                                CameraImageList.ensureCapacity(CameraImageList.size() - 1);
                                currentimageposition = currentimageposition - 1;
                                imageconversion();
                            } else if (currentimageposition == 0) {
                                CameraImageList.remove(currentimageposition);
                                CameraImageList.ensureCapacity(CameraImageList.size() - 1);

                                imageconversion();
                                Log.d("tag", "currentimagepos" + currentimageposition);
                            } else if (currentimageposition > 0 && currentimageposition < CameraImageList.size() - 1) {
                                CameraImageList.remove(currentimageposition);
                                CameraImageList.ensureCapacity(CameraImageList.size() - 1);
                                Log.d("tag", "imagelistsize" + CameraImageList.size());
                                imageconversion();
                                Log.d("tag", "currentimagepos" + currentimageposition);
                            }
                        }
                        {
                            if (CameraImageList.size() == 1) {
                                previmg.setVisibility(View.INVISIBLE);
                                nextimg.setVisibility(View.INVISIBLE);
                            }
                            if (currentimageposition == count - 1) {
                                nextimg.setVisibility(View.INVISIBLE);
                            }
                            Log.d("tag", "currentcount" + count);
                            Log.d("tag", "currentimagelistsize" + CameraImageList.size());
                        }
                    }
                }
            }
        });

    }

    private void imageconversion()
    {
        if(CameraImageList.size()!=0)
        {
            Uri imageuri = CameraImageList.get(currentimageposition);
            try {
                InputStream is = getContentResolver().openInputStream(imageuri);
                bitmap = BitmapFactory.decodeStream(is);
                linearlayoutimage.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            slctdimg.setImageBitmap(bitmap);
        }
    }

}