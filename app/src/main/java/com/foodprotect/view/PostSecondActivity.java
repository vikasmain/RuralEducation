package com.foodprotect.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.foodprotect.OfflineCapability;
import com.foodprotect.R;
import com.foodprotect.model.category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostSecondActivity extends AppCompatActivity {


    private static final String URL_PRODUCTS = "https://vikasbajpayee.000webhostapp.com/apiconn.php";
    ProgressBar progressBar;
    List<category> list;
    RequestQueue queue;

    RecyclerView recyclerView;
    private StaggeredGridLayoutManager lLayout;
    boolean doubleBackToExitPressedOnce=false;
    categoryadapter albumAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_second);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),PostFirstActivity.class));
            }
        });
        toolbar.setTitle("Rural Education");
        //getting the recyclerview from xml
        recyclerView=(RecyclerView)findViewById(R.id.my_recycler_vi);
        progressBar=(ProgressBar)findViewById(R.id.progressbar);
        list=new ArrayList<>();
        albumAdapter=new categoryadapter(this,list);
        lLayout = new StaggeredGridLayoutManager( 2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lLayout);
        recyclerView.setAdapter(albumAdapter);

        queue= OfflineCapability.getInstance().getRequestQueue();
        loadcategory();
    }
    private void loadcategory()
    {
        progressBar.setVisibility(View.VISIBLE);
        JsonArrayRequest stringRequest = new JsonArrayRequest(URL_PRODUCTS,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response)
                    {
                        progressBar.setVisibility(View.INVISIBLE);

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                category movie = new category(obj.getInt("id"),
                                        obj.getString("title"),
                                        obj.getString("image"));




                           /*     JSONArray genreArry = obj.getJSONArray("genre");
                                ArrayList<String> genre = new ArrayList<String>();
                                for (int j = 0; j < genreArry.length(); j++) {
                                    genre.add((String) genreArry.get(j));
                                }
                                movie.setGenre(genre);
                             */
                                list.add(movie);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        albumAdapter.notifyDataSetChanged();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                });

        //adding our stringrequest to queue
       queue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            moveTaskToBack(true); //activity.moveTaskToBack(true);

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