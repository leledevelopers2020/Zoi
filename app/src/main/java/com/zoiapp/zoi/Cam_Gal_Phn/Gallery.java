package com.zoiapp.zoi.Cam_Gal_Phn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ClipData;
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

import com.zoiapp.zoi.R;
import com.zoiapp.zoi.UserRequired.UserAddress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Gallery extends AppCompatActivity {
    private static final int PICK_IMG = 1;
    private static final int GALLERY_PERM_CODE = 106;
    public static final int GALLERY_REQUEST_CODE = 105;
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    static int count=0;
    static int imagelistcount;
    ClipData clipdata;
    Bitmap bitmap;
    Button addFromGallery,submit;
    ImageButton addMore,previmg,nextimg,delimg;
    LinearLayout linearlayoutimage;
    LayoutInflater inflater;
    ImageView slctdimg;
    private Uri singleuri;
    String currentPhotoPath,userID,name,address,phnNum,sName,sAddress,sPhnNum;
    public int type;//to check wheather you upoad data using gallery or camera.....1.uploading single pic through gallery 2.uploading multiple images through gallery  3.upload single/multiple pic through camera
    private static int currentimageposition ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
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
        submit = (Button) findViewById(R.id.submit);
        addFromGallery = (Button) findViewById(R.id.gallery_button);
        askGalleryPermission();
        addFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMG);

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetConnectivity()) {
                    Intent address = new Intent(Gallery.this, UserAddress.class);
                    address.putExtra("activity", "Gallery");
                    address.putParcelableArrayListExtra("images", (ArrayList<? extends Parcelable>) ImageList);
                    startActivity(address);
                }
                else {
                    Toast.makeText(Gallery.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
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
    private void  askGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_PERM_CODE);
        }

    } @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GALLERY_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED  && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(Gallery.this, "Permission is Required to select images from Gallery", Toast.LENGTH_LONG).show();
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
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.zoiapp.zoi.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, GALLERY_REQUEST_CODE);
                Log.d("tag","cameraimageselected"+photoURI);
            }
        }


    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //add from gallery
        Log.d("tag","request code"+requestCode+"result code"+resultCode+"data"+data);
        if (requestCode == PICK_IMG && resultCode == RESULT_OK) {
             clipdata = data.getClipData();

            if (clipdata != null) {
                bitmap = null;
                type = 2;
                imagelistcount = ImageList.size();
                int basiccount = clipdata.getItemCount();
                currentimageposition = imagelistcount;
                count = imagelistcount + basiccount;
                if (count > 1 && ImageList.size() == 0) {
                    nextimg.setVisibility(View.VISIBLE);
                } else {
                    nextimg.setVisibility(View.VISIBLE);
                    previmg.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < basiccount; i++) {
                    Uri imageuri = clipdata.getItemAt(i).getUri();
                    ImageList.add(imageuri);
                }
                imageconversion();
                Log.d("tag", "you have selected" + ImageList);
                Log.d("tag", "you have selected " + count);
                addFromGallery.setText("Add More from Gallery");
                submit.setVisibility(View.VISIBLE);
                delimg.setVisibility(View.VISIBLE );

            }
            else
            {
                if (ImageList.size() != 0) {
                    previmg.setVisibility(View.VISIBLE);
                } else {
                    previmg.setVisibility(View.INVISIBLE);
                }
                singleuri = data.getData();
                ImageList.add(singleuri);
                imagelistcount = ImageList.size();
                currentimageposition = imagelistcount - 1;
                count = count + 1;
                imageconversion();
                nextimg.setVisibility(View.INVISIBLE);
                addFromGallery.setText("Add More from Gallery");
                submit.setVisibility(View.VISIBLE);
                delimg.setVisibility(View.VISIBLE );
            }
        }

        nextimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentimageposition++;
                Log.d("tag", "currentimagepos" + currentimageposition);
                imageconversion();
                if (currentimageposition > 0) {
                    previmg.setVisibility(View.VISIBLE);
                }
                if (currentimageposition == count - 1) {
                    nextimg.setVisibility(View.INVISIBLE);
                }
            }
        });
        previmg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentimageposition--;
                Log.d("tag", "currentimagepos" + currentimageposition);
                imageconversion();
                if (currentimageposition <= 0) {
                    previmg.setVisibility(View.INVISIBLE);
                }
                if (currentimageposition < count - 1) {
                    nextimg.setVisibility(View.VISIBLE);
                }
            }


        });

        delimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = count - 1;

                if (ImageList.size() != 0) {
                    if (ImageList.size() - 1 == 0) {
                        ImageList.remove(currentimageposition);
                        ImageList.ensureCapacity(ImageList.size() - 1);
                        slctdimg.setImageDrawable(getResources().getDrawable(R.drawable.border_side));
                        submit.setVisibility(View.INVISIBLE);
                        delimg.setVisibility(View.INVISIBLE);
                        addFromGallery.setText("Upload from Gallery");
                    } else {
                        if (currentimageposition == ImageList.size() - 1) {
                            ImageList.remove(currentimageposition);
                            ImageList.ensureCapacity(ImageList.size() - 1);
                            currentimageposition = currentimageposition - 1;
                            imageconversion();
                        } else if (currentimageposition == 0) {
                            ImageList.remove(currentimageposition);
                            ImageList.ensureCapacity(ImageList.size() - 1);

                            imageconversion();
                            Log.d("tag", "currentimagepos" + currentimageposition);
                        } else if (currentimageposition > 0 && currentimageposition < ImageList.size() - 1) {
                            ImageList.remove(currentimageposition);
                            ImageList.ensureCapacity(ImageList.size() - 1);
                            Log.d("tag", "imagelistsize" + ImageList.size());
                            imageconversion();
                            Log.d("tag", "currentimagepos" + currentimageposition);
                        }
                    }
                    {
                        if (ImageList.size() == 1) {
                            previmg.setVisibility(View.INVISIBLE);
                            nextimg.setVisibility(View.INVISIBLE);
                        }
                        if (currentimageposition == count - 1) {
                            nextimg.setVisibility(View.INVISIBLE);
                        }
                        Log.d("tag", "currentcount" + count);
                        Log.d("tag", "currentimagelistsize" + ImageList.size());
                    }
                }
            }
        });
    }
    private void imageconversion()
    {
        if (ImageList.size() != 0) {
            Uri imageuri = ImageList.get(currentimageposition);

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
