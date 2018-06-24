package com.foodprotect.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.foodprotect.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by dell on 28-03-2018.
 */

public class Checkiit  extends AppCompatActivity implements OnMapReadyCallback, DirectionCallback {
    private GoogleMap googleMap;
    private String serverKey = "AIzaSyAXLaO40dndd7OddvFgib7rjHSqVnwKLeg";
    private LatLng origin,destination;
    Double latitude1,latitude2,longitude1,longitude2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkiti);
        if(getIntent()!=null)
        {
            latitude1=getIntent().getDoubleExtra("lat1",0.0);
            latitude2=getIntent().getDoubleExtra("lat2",0.0);
            longitude1=getIntent().getDoubleExtra("lon1",0.0);
            longitude2=getIntent().getDoubleExtra("lon2",0.0);
            origin = new LatLng(latitude2, longitude2);
            destination = new LatLng(latitude1, longitude1);
        }

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        requestDirection();
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
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }



    public void requestDirection() {
        double dis=distance(latitude1,longitude1,latitude2,longitude2);

        if(dis==0)
        {
            Toast.makeText(this, "Product posted by yourself", Toast.LENGTH_SHORT).show();
        }

        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        if (direction.isOK()) {
            Route route = direction.getRouteList().get(0);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_a);
            BitmapDescriptor icon2 = BitmapDescriptorFactory.fromResource(R.drawable.marker_b);

            googleMap.addMarker(new MarkerOptions().position(origin).icon(icon));
            googleMap.addMarker(new MarkerOptions().position(destination).icon(icon2));

            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
            googleMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.RED));
            setCameraWithCoordinationBounds(route);

        } else {
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Toast.makeText(this, "Direction not found", Toast.LENGTH_SHORT).show();
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }
}