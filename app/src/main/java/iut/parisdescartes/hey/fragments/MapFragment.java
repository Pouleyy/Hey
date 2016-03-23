package iut.parisdescartes.hey.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import iut.parisdescartes.hey.R;
import iut.parisdescartes.hey.utilities.SimpleLocation;


/**
 * Created by Kévin on 03/03/2016.
 */
public class MapFragment extends Fragment {
    private GoogleMap mMap;

    private Double latitude;
    private Double longitude;
    private static int ANIMATE_SPEEED_TURN = 7000;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);

        Context context = getActivity();
        SimpleLocation location = new SimpleLocation(context);
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        //Première méthode pour utiliser le
        /*SharedPreferences sharedPref = context.getSharedPreferences(
                MY_PREFS_NAME, Context.MODE_PRIVATE);

        latitude = Double.valueOf(sharedPref.getString("latitude","0.0"));
        longitude = Double.valueOf(sharedPref.getString("longitude", "0.0"));
        Toast.makeText(context, Double.toString(latitude), Toast.LENGTH_SHORT).show();
        */

        mMap = ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map)).getMap();

        setupMap();

        return view;
    }





    private void setupMap() {
        mMap.clear();

        LatLng mLocation = new LatLng(latitude, longitude);
        mMap.setMyLocationEnabled(true);

        mMap.addMarker(new MarkerOptions()
                .title("Hey !")
                .snippet("Tu as été localisé ici et tes amis te verront ici ! ")
                .anchor(0.0f, 1.0f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.persondot))
                .position(mLocation));
        animateMap(mLocation);
    }

    private void animateMap(LatLng mLocation) {
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(mLocation)
                        .bearing(45)
                        .tilt(90)
                        .zoom(17)
                        .build();

        mMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(cameraPosition),
                ANIMATE_SPEEED_TURN,
                new GoogleMap.CancelableCallback() {

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onCancel() {
                    }
                }
        );
    }




}

