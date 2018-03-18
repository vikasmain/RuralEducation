package com.foodprotect.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.foodprotect.R;
import com.foodprotect.model.PostUsers;
import com.foodprotect.model.product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.Locale;

public class ViewProduct extends AppCompatActivity
{
    private static final String EXTRA_IMAGE ="com.foodprotect" ;
    DatabaseReference databaseReference,databaseReference2;
    String Extra="blog_id";
    FirebaseAuth mauth;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    String name;
    ImageView imageView,fabbutton;
    TextView t1,t2,t3,t4,t5,t6,t7,t10;
    String mpost_key,post_title,image,
            post_desc,post_uid,post_price
            ,post_category;
    ImageView floatingActionButton;
    Double lon1,lat1,lon2,lat2;
    private Menu menu;
    ProgressBar progressBar;
    Double dis;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        floatingActionButton=(ImageView)findViewById(R.id.fab2);

        progressBar=(ProgressBar)findViewById(R.id.progressbar);
        t1=(TextView) findViewById(R.id.t1);
        t2=(TextView)findViewById(R.id.t2);
        t3=(TextView)findViewById(R.id.t3);
        t4=(TextView)findViewById(R.id.t4);
        t5=(TextView)findViewById(R.id.t5);
        t6=(TextView)findViewById(R.id.t6);

        t7=(TextView)findViewById(R.id.t7);

        t10=(TextView)findViewById(R.id.t15);

        imageView=(ImageView)findViewById(R.id.image_view);
        fabbutton=(ImageView)findViewById(R.id.fab);
        progressBar.setVisibility(View.VISIBLE);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Products");
        databaseReference2= FirebaseDatabase.getInstance().getReference().child("Users");

        mauth= FirebaseAuth.getInstance();
if(getIntent()!=null)
{
    mpost_key=getIntent().getStringExtra(Extra);
}
if(!mpost_key.isEmpty())
{


    databaseReference.child(mpost_key).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            final product product=dataSnapshot.getValue(product.class);


//            post_title = (String) dataSnapshot.child("title").getValue();
//            post_desc = (String) dataSnapshot.child("desc").getValue();
//            post_price = (String) dataSnapshot.child("price").getValue();
//
//            image = (String) dataSnapshot.child("image").getValue();

            post_uid = (String) dataSnapshot.child("uid").getValue();

            Glide.with(getApplicationContext()).load(product.getImage())
                    .into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    zoomImageFromThumb(imageView,product.getImage());
                }
            });

            mShortAnimationDuration = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);
            t3.setText(getResources().getString(R.string.Rs) + " " + product.getPrice());
            t4.setText("Product Title: "+product.getTitle());
            t5.setText("Product Category: "+product.getCategory());
            t10.setText("Description: "+product.getDesc());
            if (post_uid != null) {


                databaseReference2.child(post_uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot2) {

                        String address = (String) dataSnapshot2.child("Shipping Address").child("Address1").getValue();
                        name = (String) dataSnapshot2.child("Name").getValue();

                        final PostUsers postUsers=dataSnapshot2.getValue(PostUsers.class);
                            t2.setText(postUsers.getName());
                            Picasso.with(getApplicationContext()).load(postUsers.getImage())
                                    .transform(new CircleTransform()).into(fabbutton);
                            lon1=dataSnapshot2.child("longitude").getValue(Double.class);
                            lat1=dataSnapshot2.child("latitude").getValue(Double.class);
                        SharedPreferences sharedPreferences=getSharedPreferences("Hellonakoli",MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        putDouble(editor,"long1",lon1);
                        putDouble(editor,"lati1",lat1);
                        editor.commit();
                            t7.setText("Address of User: "+address);
                            t7.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String geoUri = "http://maps.google.com/maps?q=loc:" + lat1 + "," + lon1;

                                    String uri = String.format(Locale.ENGLISH, geoUri, lat1, lon1);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                    startActivity(intent);
                                }
                            });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            } else {
                Toast.makeText(ViewProduct.this, "Product doesn't exists...", Toast.LENGTH_SHORT).show();
            }
        progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            progressBar.setVisibility(View.INVISIBLE);

        }
    });
}
else
{
    Toast.makeText(this, "Product id not found", Toast.LENGTH_SHORT).show();
}
        String current=mauth.getCurrentUser().getUid();

        databaseReference2.child(current).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot3)
            {
                lon2 = (Double) dataSnapshot3.child("longitude").getValue();
                lat2 = (Double) dataSnapshot3.child("latitude").getValue();
                SharedPreferences sharedPreferen=getSharedPreferences("Hellonakoli",MODE_PRIVATE);
                Double longitude1=getDouble(sharedPreferen,"long1",0.1);
                Double latitude1=getDouble(sharedPreferen,"lati1",0.1);

                if(longitude1!=null&&lat2!=null&&latitude1!=null&&lon2!=null)
                {
                    dis=distance(latitude1,longitude1,lat2,lon2);
//                String.format("%.2f", distance);
                    t6.setText(""+String.format( "%.2f", dis )+" km from here");
//                t3.setText(distance+" km");
                }
                else
                {
                    Toast.makeText(ViewProduct.this, "Lat Long not present here...", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    floatingActionButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(ViewProduct.this,ChatActivity.class);
            intent.putExtra("blogs",mpost_key);
            intent.putExtra("uids",name);

            startActivity(intent);
        }
    });
    }
   
    SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }
    double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        if ( !prefs.contains(key))
            return defaultValue;

        return Double.longBitsToDouble(prefs.getLong(key, 0));
    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
//        hideOption(R.id.action_info);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
         if (id == R.id.action_info) {
             Intent intent=new Intent(ViewProduct.this,ChatActivity.class);
             intent.putExtra("blogs",mpost_key);
             intent.putExtra("uids",name);

             startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
    private void zoomImageFromThumb(final View thumbView, String imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);
        Glide.with(getApplicationContext()).load(imageResId).into(expandedImageView);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.contain)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}
