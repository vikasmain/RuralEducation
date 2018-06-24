package com.foodprotect.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.foodprotect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PostFirstActivity extends AppCompatActivity {
private DatabaseReference databaseReference;
    EditText richEditor,post_second,post_price;
    FirebaseAuth mauth;
    StorageReference storageReference;
    private static int galleryrequest=1;
    DatabaseReference users,mDatabase;
    Button b1,b2;
    Uri imageuri=null;
    boolean doubleBackToExitPressedOnce = false;

    TextView lo;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_first);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mauth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Products");
        users= FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);
        users.keepSynced(true);
        storageReference= FirebaseStorage.getInstance().getReference();
        richEditor=(EditText) findViewById(R.id.editTextfirst);
        post_second=(EditText) findViewById(R.id.post_second);
        post_price=(EditText) findViewById(R.id.post_price);

        richEditor.setSelection(0);
        b1=(Button)findViewById(R.id.savefirst);

        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startFirstPost();
            }
        });
    }

    private void startFirstPost() {
        final String moet = richEditor.getText().toString();
        final String title = post_second.getText().toString();
        final String price = post_price.getText().toString();

        SharedPreferences prefs = getSharedPreferences("Hellonakoli", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("moet", moet);
        editor.putString("title", title);
        editor.putString("price", price);

        editor.commit();
        if (!TextUtils.isEmpty(moet)&&!TextUtils.isEmpty(title)&&!TextUtils.isEmpty(price)) {


            Intent intent = new Intent(PostFirstActivity.this, PostSecondActivity.class);

            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Please enter your product description", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            moveTaskToBack(true); //activity.moveTaskToBack(true);
//it will behave as Home Button is pressed



        }

        this.doubleBackToExitPressedOnce = true;
        Toast toast=Toast.makeText(this, "Press Back again to exit", Toast.LENGTH_SHORT);
        View view = toast.getView();
        view.setBackgroundColor(getResources().getColor(R.color.toast));
        TextView text = (TextView) view.findViewById(android.R.id.message);
        text.setTextColor(getResources().getColor(R.color.black));
/*Here you can do anything with above textview like text.setTextColor(Color.parseColor("#000000"));*/
        toast.show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}

