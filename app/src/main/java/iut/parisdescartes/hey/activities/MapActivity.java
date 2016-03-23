package iut.parisdescartes.hey.activities;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import iut.parisdescartes.hey.R;
import iut.parisdescartes.hey.utilities.SimpleLocation;

/**
 * Created by Kévin on 03/03/2016.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static int ANIMATE_SPEEED_TURN = 7000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment);
        findViewById(R.id.layoutMapIntern).setVisibility(View.VISIBLE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        SimpleLocation location = new SimpleLocation(this);
        LatLng mLocation = new LatLng(location.getLatitude(), location.getLongitude());
        map.setMyLocationEnabled(true);
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 17));

        map.addMarker(new MarkerOptions()
                .title("Hey !")
                .snippet("Tu as été localisé ici et tes amis te verront ici ! ")
                .anchor(0.0f, 1.0f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.persondot))
                .position(mLocation));
        // Change to satellite view, slower but prettier
        //map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        animateMap(map, mLocation);
    }


    private void animateMap(GoogleMap map, LatLng mLocation) {
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(mLocation)
                        .bearing(45)
                        .tilt(90)
                        .zoom(17)
                        .build();

        map.animateCamera(
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

