package iut.parisdescartes.hey.activities;


import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import android.app.Fragment;
import android.support.v4.app.Fragment;//Good one


//import com.google.android.gms.maps.MapFragment;
//Replace /w my own map
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.ProfilePictureView;

import cn.pedant.SweetAlert.SweetAlertDialog;
import iut.parisdescartes.hey.fragments.MapFragment;
import iut.parisdescartes.hey.R;
import iut.parisdescartes.hey.utilities.SimpleLocation;

public class MainActivity extends AppCompatActivity {

    private Button buttonSend;
    private EditText phoneNumber;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public final static String EXTRA_MESSAGE = "iut.parisdescartes.hey.MESSAGE";
    private SimpleLocation location;

    private MapFragment mapFragment;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private TextView nameText;
    private boolean justOneTime = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        location = new SimpleLocation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSend = (Button) findViewById(R.id.buttonSend);
        phoneNumber = (EditText) findViewById(R.id.numberTel);

        //NAVIGATION DRAWER PART
        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.setDrawerListener(drawerToggle);

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);


        //END OF NAVIGATION DRAWER PART


        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String number = phoneNumber.getText().toString();
                checkIfNumberIsOnlyNumber(number);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.FloatingActionBar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(findViewById(R.id.layoutMapIntern).getVisibility() == View.VISIBLE)) {
                    Snackbar.make(view, "Tu veux voir ta localisation ?\n Juste comme ça, " +
                            "un petit kiffe ?", Snackbar.LENGTH_LONG)
                            .setAction("Maps", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mapFragment = (MapFragment) getSupportFragmentManager()
                                            .findFragmentById(R.id.fragmentMap);
                                    displayMap(mapFragment);
                                }
                            })
                                    //BROKEN .setActionTextColor(getResources(getColor
                                    // (R.color.bleuTurquoise))
                            .show();
                } else
                    Toast.makeText(MainActivity.this, "Carte déjà montrée", Toast.LENGTH_SHORT)
                            .show();
            }
        });
        findViewById(R.id.linearLayoutMain).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if(justOneTime) {
                    changeNavHeader();
                    justOneTime = false;
                }
                return true;
            }
        });
    }

    private void changeNavHeader() {
        View headerLayout = nvDrawer.inflateHeaderView(R.layout.nav_header);
        ProfilePictureView profilePictureView;
        profilePictureView = (ProfilePictureView) headerLayout.findViewById(R.id.imageFB);
        profilePictureView.setProfileId(getUserId());
        nameText = (TextView)headerLayout.findViewById(R.id.nav_header_text);
        String nameByFB = getNameByFB();
        nameText.setText(nameByFB);
    }

    private String getNameByFB() {
        String name;
        SharedPreferences sharedPref = getSharedPreferences(
                MY_PREFS_NAME, Context.MODE_PRIVATE);
        name = sharedPref.getString("firstName", "Prénom");
        name += " " + sharedPref.getString("lastName", "Nom");
        return name;
    }
    private String getUserId() {
        String userId;
        SharedPreferences sharedPref = getSharedPreferences(
                MY_PREFS_NAME, Context.MODE_PRIVATE);
        userId = sharedPref.getString("idUser", "");

        return userId;
    }
    private void askLocation() {
        if (!location.hasLocationEnabled()) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Nous avons besoin de ta localisation")
                .setContentText("La donner ?")
                .setCancelText("Nope")
                .setConfirmText("Yup")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog){
                        finish();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        SimpleLocation.openSettings(MainActivity.this);
                        sDialog.cancel();
                    }
                })

                .show();
        }
    }

    private void checkIfNumberIsOnlyNumber(String number) {
        if (number.matches("[0-9]+") && (number.length() == 10))
            sendNewActivity(number);
        else {
            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Oups... :(")
                    .setContentText("Problème sur le numéro saisi, il doit contenir" +
                            " que des chiffres et 10 !")
                    .show();
        }


    }
    private void saveLocPref() {
        SharedPreferences sharedPref = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("latitude", Double.toString(location.getLatitude()));
        editor.putString("longitude", Double.toString(location.getLongitude()));
        editor.commit();
    }

    private void displayMap(Fragment fragment) {
        saveLocPref();
        getSupportFragmentManager().beginTransaction().show(fragment).commit();
        findViewById(R.id.layoutMapIntern).setVisibility(View.VISIBLE);
    }

    private void sendNewActivity(String message) {
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
        location.beginUpdates();
    }

    @Override
    protected void onPause() {
        location.endUpdates();
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    //NAVIGATION DRAWER PART
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {

        Intent intent;
        switch(menuItem.getItemId()) {
            case R.id.nav_first:
                break;
            case R.id.nav_second:
                intent = new Intent(MainActivity.this, MapActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.nav_third:
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.nav_fourth:
                intent = new Intent(MainActivity.this, AboutUsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
        }

        // Highlight the selected item, update the title, and close the drawer
        // Highlight the selected item has been done by NavigationView
        // menuItem.setChecked(true);
        //setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
        return actionBarDrawerToggle;
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE! Make sure to override the method with only a single `Bundle` argument
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        askLocation();
        drawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
