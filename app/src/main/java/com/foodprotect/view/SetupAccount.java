package com.foodprotect.view;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.foodprotect.R;
import com.foodprotect.model.LocationTrack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

@SuppressWarnings("deprecation")
public class SetupAccount extends AppCompatActivity  {
    private static final int ALL_PERMISSIONS_RESULT =123 ;
    EditText ed1, ed2, ed3, ed5, ed6, ed7, ed8, ed9;
    Button b1;
    DatabaseReference mDatabase;
    String name, email, uid;
    boolean type;
    String selection;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener AuthListener;
    AlertDialog levelDialog;
    public String gen;
    String uri;
    String g, i;
    ProgressDialog progress;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    double longitude,latitude;
   LocationTrack locationTrack;
    final CharSequence[] items = {" Male ", " Female "};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account);
        //setting up multiple permissions together
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissions.add(WRITE_EXTERNAL_STORAGE);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }


        auth = FirebaseAuth.getInstance();
        progress = new ProgressDialog(this);
        b1 = (Button) findViewById(R.id.btn_save);
        ed1 = (EditText) findViewById(R.id.input_state);
        ed2 = (EditText) findViewById(R.id.input_City);
        ed3 = (EditText) findViewById(R.id.input_add1);
        ed5 = (EditText) findViewById(R.id.input_landmark);
        ed6 = (EditText) findViewById(R.id.input_pincode);
        ed7 = (EditText) findViewById(R.id.input_name);
        ed8 = (EditText) findViewById(R.id.input_mail);
        ed9 = (EditText) findViewById(R.id.input_phone);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            ed7.setText(auth.getCurrentUser().getDisplayName());
            ed8.setText(auth.getCurrentUser().getEmail());
            g = auth.getCurrentUser().getDisplayName();
            i = auth.getCurrentUser().getEmail();
            uri = currentUser.getPhotoUrl().toString();

        }


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationTrack = new LocationTrack(SetupAccount.this);


                if (locationTrack.canGetLocation()) {


                     longitude = locationTrack.getLongitude();
                     latitude = locationTrack.getLatitude();

                    Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
                } else {

                    locationTrack.showSettingsAlert();
                }


                String a = ed1.getText().toString();
                String b = ed2.getText().toString();
                String c = ed3.getText().toString();
                String e = ed5.getText().toString();
                String f = ed6.getText().toString();
                String h = ed9.getText().toString();
                final String uid = auth.getCurrentUser().getUid();
                if(g==null||i==null)
                {
                  g=ed7.getText().toString();
                  i=ed8.getText().toString();
                }
                if (!TextUtils.isEmpty(a) && !TextUtils.isEmpty(b) && !TextUtils.isEmpty(c)
                        && !TextUtils.isEmpty(e) && !TextUtils.isEmpty(f)
                        && !TextUtils.isEmpty(h) && uri != null && !uri.isEmpty()
                        && !TextUtils.isEmpty(g) && !TextUtils.isEmpty(i) && uid != null
                        && !TextUtils.isEmpty(Double.toString(longitude)) && !TextUtils.isEmpty(Double.toString(latitude))) {
                    progress.setMessage("Finishing Setup...");
                    progress.show();
                    mDatabase.child(uid).child("Name").setValue(g);
                    mDatabase.child(uid).child("uid").setValue(uid);
                    mDatabase.child(uid).child("Phone-no").setValue(h);
                    mDatabase.child(uid).child("Email").setValue(i);
                    mDatabase.child(uid).child("image").setValue(uri);
                    mDatabase.child(uid).child("latitude").setValue(longitude);
                    mDatabase.child(uid).child("longitude").setValue(latitude);

                    mDatabase.child(uid).child("Shipping Address").child("State").setValue(a);
                    mDatabase.child(uid).child("Shipping Address").child("City").setValue(b);
                    mDatabase.child(uid).child("Shipping Address").child("Address1").setValue(c);
                    mDatabase.child(uid).child("Shipping Address").child("Landmark").setValue(e);
                    mDatabase.child(uid).child("Shipping Address").child("Pincode").setValue(f);
                    mDatabase.child(uid).child("Subscription").setValue(selection);
                    AlertDialog.Builder builder = new AlertDialog.Builder(SetupAccount.this);
                    builder.setTitle("Select your gender");
                    builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {


                            switch (item) {
                                case 0:
                                    gen = "Male";
                                    levelDialog.dismiss();
                                    progress.dismiss();

                                    Intent in = new Intent(SetupAccount.this, MainActivity.class);
                                    in.putExtra("gender", gen);
                                    startActivity(in);
                                    break;
                                case 1:
                                    gen = "Female";
                                    levelDialog.dismiss();
                                    progress.dismiss();

                                    Intent in2 = new Intent(SetupAccount.this, MainActivity.class);
                                    in2.putExtra("gender", gen);
                                    startActivity(in2);
                                    break;
                            }
                        }
                    });
                    levelDialog = builder.create();
                    levelDialog.show();
                } else {
                    progress.dismiss();

                    Toast.makeText(getApplicationContext(), "Fields are empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SetupAccount.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationTrack!=null)
        locationTrack.stopListener();
    }
}
