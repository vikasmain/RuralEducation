package com.foodprotect.view;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.foodprotect.OfflineCapability;
import com.foodprotect.R;
import com.foodprotect.model.Villages;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dell on 31-03-2018.
 */

public class ShowFragment extends DialogFragment {
    RecyclerView rv;
    VillageAdapter adapter;
    Villages movie;
    RequestQueue queue;
    LinearLayoutManager lLayout;
    ProgressBar progressBar;
    TextView textView,textView2,textView3;
    private ArrayList<Villages> list=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.dialog_layout,container);
        progressBar=(ProgressBar) rootView.findViewById(R.id.progressbar);
        queue= OfflineCapability.getInstance().getRequestQueue();
        rv= (RecyclerView) rootView.findViewById(R.id.my_recycler_vi);
        rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        adapter=new VillageAdapter(this.getActivity(),list);
        rv.setAdapter(adapter);
        getPlaces();
        this.getDialog().setTitle("Villages Names");


        return rootView;
    }
    private void getPlaces()
    {
        String URL_PRODUCTS="https://vikasbajpayee.000webhostapp.com/showvillages.php";
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
                                movie = new Villages(obj.getInt("id"),
                                        obj.getString("name"));
                                list.add(movie);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();

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
}
