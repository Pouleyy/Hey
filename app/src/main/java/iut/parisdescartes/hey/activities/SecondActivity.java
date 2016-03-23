package iut.parisdescartes.hey.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

import iut.parisdescartes.hey.R;
import iut.parisdescartes.hey.utilities.SimpleLocation;
import iut.parisdescartes.hey.fragments.MapFragment;

/**
 * Created by Kévin on 27/02/2016.
 */
public class SecondActivity extends AppCompatActivity {
    private Button buttonSend;
    private String phoneNumber;
    private EditText messageSMS;
    private String message;

    private SimpleLocation location;

    private MapFragment mapFragment;

    private static final String ACTION_SENT = "iut.parisdescartes.hey.SENT";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    //public final static String EXTRA_MESSAGE = "iut.parisdescartes.hey.MESSAGE";

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        messageSMS = (EditText) findViewById(R.id.messageSMS);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        location = new SimpleLocation(this);
        mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentMap);
        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(SecondActivity.this, "Passe Listener", Toast.LENGTH_SHORT).show();
                message = messageSMS.getText().toString();
                sendSMS();
            }
        });

    }


    private void sendSMS() {
        //Toast.makeText(this, "Passe Send sms", Toast.LENGTH_SHORT).show();
        PendingIntent sendIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_SENT), 0);
        SmsManager sms = SmsManager.getDefault();
        makeMessage();
        sms.sendTextMessage(phoneNumber, null, message, sendIntent, null);
    }

    private void makeMessage() {
        String cityName = getCity();
        String name;
        SharedPreferences sharedPref = getSharedPreferences(
                MY_PREFS_NAME, Context.MODE_PRIVATE);
        name = sharedPref.getString("firstName", "Prénom");
        if(!name.contains("Prénom"))
            message += "\nC'est " + name;

        if (cityName == null) {
            message += "\nJe suis ici : http://maps.google.com/maps?q=" + location.getLatitude() +
                    "," + location.getLongitude();
        } else {
            message += "\nJe suis à " + cityName +
                    ", ici exactement : http://maps.google.com/maps?q=" + location.getLatitude() +
                    "," + location.getLongitude();
        }
    }

    private String getCity() {
        String cityName = null;
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    private BroadcastReceiver sent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    //Toast.makeText(SecondActivity.this, "SMS Bien envoyé !", Toast.LENGTH_SHORT)
                    //        .show();
                    showSweetAlertDialog();

                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                case SmsManager.RESULT_ERROR_NULL_PDU:
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    new SweetAlertDialog(SecondActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oups... :(")
                            .setContentText("Problème lors de l'envoie du SMS, veuillez verifier" +
                                    " votre connexion réseau")
                            .show();
                    //Toast.makeText(SecondActivity.this, "Problème lors de l'envoie du SMS,"+
                    // " veuillez verifier votre connexion réseau", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    public void showSweetAlertDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("SMS Bien envoyé")
                .setContentText("Tu veux voir ta localisation ?")
                .setCancelText("Nope")
                .setConfirmText("Yup")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog){
                        sDialog.dismissWithAnimation();
                        finish();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                        displayMap(mapFragment);
                    }
                })

                .show();
    }

    private void displayMap(Fragment fragment) {

        if (!location.hasLocationEnabled()) {
            SimpleLocation.openSettings(this);
        }
        getSupportFragmentManager().beginTransaction().show(fragment).commit();
        findViewById(R.id.layoutMapIntern).setVisibility(View.VISIBLE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(sent, new IntentFilter(ACTION_SENT));
        location.beginUpdates();
    }

    @Override
    protected void onPause() {
        location.endUpdates();
        unregisterReceiver(sent);
        super.onPause();
    }

}



