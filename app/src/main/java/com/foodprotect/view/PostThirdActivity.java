package com.foodprotect.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.foodprotect.R;
import com.foodprotect.model.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PostThirdActivity extends AppCompatActivity {

    ImageView iv_image;
    Uri imageuri;
    FirebaseAuth mauth;
    FirebaseUser mCurrentuser;
    DatabaseReference users, mDatabase;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    Button b;

    String title,pri,boardId,category;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;

    TextView textView,textView2,textView3,textView4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_third);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),PostSecondActivity.class));
            }
        });
        toolbar.setTitle("Your Product Details");
        mauth=FirebaseAuth.getInstance();
        mCurrentuser=mauth.getCurrentUser();
        users= FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase=FirebaseDatabase.getInstance().getReference().child("Products");
        users.keepSynced(true);
        mDatabase.keepSynced(true);
        storageReference = FirebaseStorage.getInstance().getReference();//root directory
        textView=(TextView)findViewById(R.id.t1) ;
        textView2=(TextView)findViewById(R.id.t2);
        textView3=(TextView)findViewById(R.id.t3);
        textView4=(TextView)findViewById(R.id.t4);
        progressDialog=new ProgressDialog(this);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        b = (Button) findViewById(R.id.sa);
        SharedPreferences prefs = getSharedPreferences("Hellonakoli",MODE_PRIVATE);
        title = prefs.getString("title","default_value_here_if_string_is_missing");
        pri = prefs.getString("price","default_value_here_if_string_is_missing");
        boardId = prefs.getString("moet","default_value_here_if_string_is_missing");
        category = prefs.getString("categ","default_value_here_if_string_is_missing");
        textView.setText(title);
        textView2.setText("Rs. "+pri);
        textView3.setText(boardId);
        textView4.setText(category);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startpost5();
            }
        });
    }
    private void startpost5() {
        progressDialog.setTitle("Wait");
        progressDialog.setMessage("Uploading Your Product");
        final String hello="n";
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(boardId) && !TextUtils.isEmpty(pri) &&imageuri!=null
                &&!TextUtils.isEmpty(category)&&!TextUtils.isEmpty(hello)) {
            progressDialog.show();
            StorageReference filepath = storageReference.child("Products/image").child(imageuri.getLastPathSegment());
            filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadurl = taskSnapshot.getDownloadUrl();
                    final DatabaseReference newpost = mDatabase.push();
                    users.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newpost.child("title").setValue(title);
                            newpost.child("desc").setValue(boardId);
                            newpost.child("price").setValue(pri);

                            newpost.child("image").setValue(downloadurl.toString());

                            newpost.child("category").setValue(category);
                            newpost.child("status").setValue(hello);

                            newpost.child("uid").setValue(mCurrentuser.getUid());
                            newpost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener
                                    (new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(PostThirdActivity.this, "Product saved", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(PostThirdActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                            }
                                        }
                                    });//datasnapshot returns everything inside random id(0dkm003kmd39iok) object in firebase
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(PostThirdActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take a Photo", "Choose from Gallery" };

        AlertDialog.Builder builder = new AlertDialog.Builder(PostThirdActivity.this);
        builder.setTitle("Choose Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(PostThirdActivity.this);

                if (items[item].equals("Take a Photo")) {
                    userChoosenTask ="Take a Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Gallery")) {
                    userChoosenTask ="Choose from Gallery";
                    if(result)
                        galleryIntent();

                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //converting bitmap to imageuri by taking its path
        imageuri=Uri.fromFile(destination);


        iv_image.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        imageuri=data.getData();

        iv_image.setImageURI(imageuri);
    }
}

