package com.foodprotect.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.foodprotect.R;
import com.foodprotect.model.LocationTrack;
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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import gun0912.tedbottompicker.TedBottomPicker;

public class PostThirdActivity extends AppCompatActivity {
    public RequestManager mGlideRequestManager;
    ImageView iv_image;
    ArrayList<Uri> selectedUriList;
    Uri selectedUri;
    FirebaseAuth mauth;
    FirebaseUser mCurrentuser;
    DatabaseReference mda, users, mDatabase;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    private ViewGroup mSelectedImagesContainer;
    Button b;
    String title,pri,boardId,category;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    LocationTrack locationTrack;
    private static final int ALL_PERMISSIONS_RESULT =123 ;

    TextView textView,textView2,textView3,textView4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_third);

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

        mGlideRequestManager = Glide.with(this);
        progressDialog=new ProgressDialog(this);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        mSelectedImagesContainer = (ViewGroup) findViewById(R.id.selected_photos_container);
        setSingleShowButton();
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
        progressDialog.setMessage("Uploading Your Image and Text");
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(boardId) && !TextUtils.isEmpty(pri) && selectedUri != null
                &&!TextUtils.isEmpty(category)) {
            progressDialog.show();
            StorageReference filepath = storageReference.child("Products/image").child(selectedUri.getLastPathSegment());
            filepath.putFile(selectedUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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







    private void setSingleShowButton() {
        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.r1);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(PostThirdActivity.this)
                                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                                    @Override
                                    public void onImageSelected(final Uri uri) {
                                        Log.d("ted", "uri: " + uri);
                                        Log.d("ted", "uri.getPath(): " + uri.getPath());
                                        selectedUri = uri;

                                        iv_image.setVisibility(View.VISIBLE);
                                        mSelectedImagesContainer.setVisibility(View.GONE);
                                        iv_image.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                mGlideRequestManager
                                                        .load(uri)
                                                        .into(iv_image);
                                            }
                                        });
                                        /*
                                        Glide.with(MainActivity.this)
                                                //.load(uri.toString())
                                                .load(uri)
                                                .into(iv_image);
                                         */
                                    }
                                })
                                //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                                .setSelectedUri(selectedUri)
                                //.showVideoMedia()
                                .setPeekHeight(1200)
                                .create();

                        bottomSheetDialogFragment.show(getSupportFragmentManager());


                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        Toast.makeText(PostThirdActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                    }


                };

                new TedPermission(PostThirdActivity.this)
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();

            }
        });
    }

//    private void showUriList(ArrayList<Uri> uriList) {
//        // Remove all views before
//        // adding the new ones.
//        mSelectedImagesContainer.removeAllViews();
//
//        iv_image.setVisibility(View.GONE);
//        mSelectedImagesContainer.setVisibility(View.VISIBLE);
//
//        int wdpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
//        int htpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
//
//
//        for (Uri uri : uriList) {
//
//            View imageHolder = LayoutInflater.from(this).inflate(R.layout.image_item, null);
//            ImageView thumbnail = (ImageView) imageHolder.findViewById(R.id.media_image);
//
//            Glide.with(this)
//                    .load(uri.toString())
//                    .fitCenter()
//                    .into(thumbnail);
//
//            mSelectedImagesContainer.addView(imageHolder);
//
//            thumbnail.setLayoutParams(new FrameLayout.LayoutParams(wdpx, htpx));
//
//
//        }
//
//    }
}

