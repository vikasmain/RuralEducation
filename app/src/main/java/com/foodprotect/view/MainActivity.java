package com.foodprotect.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.foodprotect.R;
import com.foodprotect.fragments.ItemThreeFragment;
import com.foodprotect.model.product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    private FirebaseAuth.AuthStateListener mauthstatelistener;
    private DatabaseReference users,products,md;
    public String value,gen;
    RecyclerView re;

    String first_name,last_name,imagetake,dateofjoin;
    FirebaseAuth mFirebaseAuth;
    private StaggeredGridLayoutManager mLayoutManager;
    ImageView displayuserprofile;
    TextView flst;
    FirebaseRecyclerAdapter<product,BlogViewholder> firebaseRecyclerAdapter;
    //search adapter functionality
    FirebaseRecyclerAdapter<product,BlogViewholder> searchadapter;
    List<String> suggestion=new ArrayList<>();
    MaterialSearchBar materialSearchBar;
    Toolbar toolbar;
    static String Extra="blog_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.loginsignup);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        displayuserprofile=(ImageView)hView.findViewById(R.id.displayuser);
        flst=(TextView)hView.findViewById(R.id.flastname);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Rural Education");
        toolbar.setBackgroundColor(getResources().getColor(R.color.white));
        mFirebaseAuth = FirebaseAuth.getInstance();

        mauthstatelistener=new FirebaseAuth.AuthStateListener() {//this is for checking whether user is logged in or not.
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){//means our user is not logged in
                    Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
            }
        };
        products= FirebaseDatabase.getInstance().getReference().child("Products");

        users= FirebaseDatabase.getInstance().getReference().child("Users");
        products.keepSynced(true);
        users.keepSynced(true);
        SharedPreferences prfs = getSharedPreferences("DATA", Context.MODE_PRIVATE);
        gen = prfs.getString("gend", "WRONG");
        if(gen.equals("WRONG")) {
            Bundle bundle = getIntent().getExtras();
            value = bundle.getString("gender");
            SharedPreferences preferences = getSharedPreferences("DATA", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("gend", value);
            editor.commit();
        }

        gen = prfs.getString("gend", "WRONG");
        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);



        if(gen.equals("Male"))
        {

            bottomNavigationView.inflateMenu(R.menu.bottom_navigation_ite);
        }
        else if(gen.equals("Female"))
        {

            bottomNavigationView.inflateMenu(R.menu.bottom_navigation_item2);
        }
         else if(gen.isEmpty()||gen=="")
        {
            bottomNavigationView.inflateMenu(R.menu.bottom_navigation_ite);

        }
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_item1:
                                
                                selectedFragment = ItemThreeFragment.newInstance();
                                break;
                            case R.id.action_item2:
                                Intent intent=new Intent(MainActivity.this,PostFirstActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.action_item3:
                                Intent intent2=new Intent(MainActivity.this,SetupAccount.class);
                                startActivity(intent2);
                                break;
                        }

                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        md=FirebaseDatabase.getInstance().getReference();
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {

        }
        else {
            String current=FirebaseAuth.getInstance().getCurrentUser().getUid();

            users.child(current).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    first_name = (String) dataSnapshot.child("Name").getValue();
                    imagetake = (String) dataSnapshot.child("image").getValue();
                    flst.setText(first_name);
                    Picasso.with(MainActivity.this).load(imagetake).transform(new CircleTransform()).into(displayuserprofile);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        materialSearchBar=(MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter Category...");
        re=(RecyclerView)findViewById(R.id.my_recycler_view);
        if (re != null) {
            //to enable optimization of recyclerview
            re.setHasFixedSize(true);
        }
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        re.setLayoutManager(mLayoutManager);
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<product, BlogViewholder>(
                product.class,
                R.layout.model_post,
                BlogViewholder.class,
                products
        ) {
            @Override
            protected void populateViewHolder(final BlogViewholder viewHolder, final product model, final int position) {
                final String post_key = getRef(position).getKey();
                //for retrieving each post key getRef() method is used for this.
                final DatabaseReference post_ref = getRef(position);
                final String nakoli_key = post_ref.getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setcategory(model.getCategory());

                viewHolder.setPri(getResources().getString(R.string.Rs) + " " + model.getPrice());
                viewHolder.setImage(getApplicationContext(), model.getImage());


//                float distanceInMeters = loc1.distanceTo(loc2);

                viewHolder.vi.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent newIntent = new Intent(MainActivity.this, ViewProduct.class);
                        newIntent.putExtra(MainActivity.Extra, nakoli_key);
                        startActivity(newIntent);
                    }
                });


            }

        };

        re.setAdapter(firebaseRecyclerAdapter);
        re.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && bottomNavigationView.isShown()) {
                    bottomNavigationView.setVisibility(View.GONE);
                } else if (dy < 0 ) {
                    bottomNavigationView.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        loadsuggestion();
        materialSearchBar.setLastSuggestions(suggestion);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> sugges=new ArrayList<>();
                for(String search:suggestion)
                {
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                    {
                        sugges.add(search);
                    }

                }
                materialSearchBar.setLastSuggestions(sugges);

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                                                        @Override
                                                        public void onSearchStateChanged(boolean enabled) {
                                                            if(!enabled)
                                                                re.setAdapter(firebaseRecyclerAdapter);
                                                        }

                                                        @Override
                                                        public void onSearchConfirmed(CharSequence text) {
                                                               startSearch(text);
                                                        }

                                                        @Override
                                                        public void onButtonClicked(int buttonCode) {

                                                        }
                                                    });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }

    private void startSearch(CharSequence text) {
        searchadapter=new FirebaseRecyclerAdapter<product, BlogViewholder>(
                product.class,
                R.layout.model_post,
                BlogViewholder.class,
                products.orderByChild("category").equalTo(text.toString())

        ) {
            @Override
            protected void populateViewHolder(BlogViewholder viewHolder, product model, final int position) {
                final String post_key = getRef(position).getKey();
                //for retrieving each post key getRef() method is used for this.
                final DatabaseReference post_ref = getRef(position);
                final String nakoli_key = post_ref.getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setcategory(model.getCategory());

                viewHolder.setPri(getResources().getString(R.string.Rs) + " " + model.getPrice());
                viewHolder.setImage(getApplicationContext(), model.getImage());


//                float distanceInMeters = loc1.distanceTo(loc2);

                viewHolder.vi.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent newIntent = new Intent(MainActivity.this, ViewProduct.class);
                        newIntent.putExtra(MainActivity.Extra, searchadapter.getRef(position).getKey());
                        startActivity(newIntent);
                    }
                });

            }
        };
re.setAdapter(searchadapter);
    }

    private void loadsuggestion() {
        products.orderByChild("category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
               {
                   product product=dataSnapshot1.getValue(product.class);
                   suggestion.add(product.getCategory());
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
//    @Override
//    public boolean onQueryTextSubmit(String query) {
//        return false;
//    }
//
//    @Override
//    public boolean onQueryTextChange(String newText) {
//        Query myquery3 = d.orderByChild("category").startAt(newText).endAt(newText + "\uf8ff");
//        FirebaseRecyclerAdapter<product, BlogViewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<product, BlogViewholder>(
//
//                product.class,
//                R.layout.model_post,
//                BlogViewholder.class,
//                myquery3
//
//        ) {
//            @Override
//            protected void populateViewHolder(BlogViewholder viewHolder, product model, int position) {
//
//
//                final String post_key=getRef(position).getKey();
//                //for retrieving each post key getRef() method is used for this.
//                final DatabaseReference post_ref=getRef(position);
//                final String nakoli_key=post_ref.getKey();
//                viewHolder.setTitle(model.getTitle());
//                viewHolder.setPri(getResources().getString(R.string.Rs)+""+model.getPrice());
//                viewHolder.setImage(getApplicationContext(),model.getImage());
//
//
//
////                float distanceInMeters = loc1.distanceTo(loc2);
//
//                viewHolder.vi.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        Intent newIntent=new Intent(MainActivity.this,ViewProduct.class);
//                        newIntent.putExtra(MainActivity.Extra,nakoli_key);
//                        startActivity(newIntent);
//                    }
//                });
//            }
//        };
//
//        re.setAdapter(firebaseRecyclerAdapter);
//        return false;
//    }




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
    public static class BlogViewholder extends RecyclerView.ViewHolder{
        View vi;
        ImageButton like;
        TextView likesnum;
        DatabaseReference mdatabaselike;
        FirebaseAuth mauth;
        private SpannableString mBaconIpsumSpannableString;
        private LinearInterpolator mTypeWriterInterpolator;
        private HashSet<Object> mSpans = new HashSet<Object>();
        TextView usernamete;
        EditText e1;
        public BlogViewholder(View itemView) {
            super(itemView);
            vi=itemView;
           /* mdatabaselike=FirebaseDatabase.getInstance().getReference().child("Likes");
            mauth=FirebaseAuth.getInstance();
            mdatabaselike.keepSynced(true);
            like=(ImageButton) itemView.findViewById(R.id.iv_like);
            likesnum=(TextView)itemView.findViewById(R.id.tv_likes);
*/
        }
        public void setImage(Context context, String image){
            ImageView imageView=(ImageView)vi.findViewById(R.id.imageView1);
            Picasso.with(context).load(image).fit().centerCrop().into(imageView);

        }
        public void setTitle(String title)
        {
            TextView textView=(TextView) vi.findViewById(R.id.textView1);
            textView.setText(title);


        }
        public void setcategory(String category)
        {
            TextView textView=(TextView) vi.findViewById(R.id.textView3);
            textView.setText(category);


        }
        public void setPri(String price) {
            final TextView textView2 = (TextView) vi.findViewById(R.id.textView2);
            textView2.setText(price);

        }
        public void setDetails(String tit,String price,Context context,String img) {
            TextView textView=(TextView) vi.findViewById(R.id.textView1);
            textView.setText(tit);
            ImageView imageView=(ImageView)vi.findViewById(R.id.imageView1);
            Picasso.with(context).load(img).fit().centerCrop().into(imageView);

            final TextView textView2 = (TextView) vi.findViewById(R.id.textView2);
            textView2.setText(price);

        }

//        public void setLikeButton(final String post_key){
//            mdatabaselike.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.child(post_key).hasChild(mauth.getCurrentUser().getUid())) {//we are looking inside the like folder in firebase like exists or not
//                        like.setImageResource(R.mipmap.like_colored);
//                    }
//                    else
//                    {
//                        like.setImageResource(R.mipmap.like_gray);
//                    }
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
//        public void setuserrname(String username){
//            usernamete=(TextView)vi.findViewById(R.id.post_name);
//            usernamete.setText(username);
//
//        }



        private class ViewUpdater implements Runnable {
            private String mString;
            private TextView mView;

            public ViewUpdater(String string, TextView view){
                mString = string;
                mView = view;
            }

            @Override
            public void run() {
                mView.setText(mString);
            }
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void checkuserexists() {
        if(mFirebaseAuth.getCurrentUser() !=null) {
            final String uid = mFirebaseAuth.getCurrentUser().getUid();


            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(uid)) {
                        Toast.makeText(MainActivity.this, "Please complete your setup first....", Toast.LENGTH_SHORT).show();
                        Intent mintent = new Intent(MainActivity.this, SetupAccount.class);
                        mintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mintent);
                        finish();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    public void signOut() {
        // Firebase sign out
        AuthUI.getInstance()
                .signOut(MainActivity.this)
                .addOnCompleteListener(new OnCompleteListener<Void>(){

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        // do something here

                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mauthstatelistener);
        checkuserexists();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_signin,menu);
//        final MenuItem item = menu.findItem(R.id.search);
//        final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(item);
//        searchView.setOnQueryTextListener(this);
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==R.id.signout)
        {

        signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id==R.id.logo)
        {
           signOut();
        }
        else if(id==R.id.profile)
        {
            startActivity(new Intent(MainActivity.this,SetupAccount.class));
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}