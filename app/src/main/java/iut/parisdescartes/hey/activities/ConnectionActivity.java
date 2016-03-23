package iut.parisdescartes.hey.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import iut.parisdescartes.hey.R;

/**
 * Created by Kévin on 09/03/2016.
 */
public class ConnectionActivity extends AppCompatActivity implements FacebookCallback<LoginResult> {

    private LoginButton loginButton;
    private CallbackManager manager;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private SharedPreferences sharedPreferences;

    //Will be prettier /w sweetAlertDialog
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.connection_layout);

        manager = CallbackManager.Factory.create();
        sharedPreferences = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        if (AccessToken.getCurrentAccessToken() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }

        loginButton = (LoginButton)findViewById(R.id.button_fb);
        loginButton.setReadPermissions("public_profile, publish_actions");
        loginButton.registerCallback(manager, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        manager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(final LoginResult loginResult) {
        progress = ProgressDialog.show(ConnectionActivity.this, "Connexion en cours",
                "Veuillez patienter....", true);
        progress.setCancelable(true);
        progress.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    LoginManager.getInstance().logOut();
                    progress.dismiss();
                    finish();
                    startActivity(getIntent());
                    return true;
                }
                return true;
            }
        });

        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        String name, firstName, id, picture;
                        try {
                            name = object.getString("last_name");
                            firstName = object.getString("first_name");
                            id = object.getString("id");
                            picture = object.getString("picture");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("idUser", id);
                            editor.putString("firstName", firstName);
                            editor.putString("lastName", name);
                            editor.putString("picture", picture);
                            editor.apply();
                        }catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }

                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,last_name,first_name,picture");
        request.setParameters(parameters);
        request.executeAsync();
        progress.dismiss();
        Intent intent = new Intent(ConnectionActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCancel() {
        LoginManager.getInstance().logOut();
        showSweetAlert(1);
    }

    @Override
    public void onError(FacebookException error) {
        showSweetAlert(0);
    }




    private void showSweetAlert(int number) {
        String title = "";
        String text = "Veuillez réessayer s'il vous plaît";
        if(number == 1) {
            title = "Connexion annulée";
            title = "Connexion impossible";
            text += " et verifier votre connextion réseau";
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(title)
                    .setContentText(text)
                    .setConfirmText("D'accord, ça marche !")
                    .show();
        }

        else {
            title = "Connexion impossible";
            text += " et verifier votre connextion réseau";
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(title)
                    .setContentText(text)
                    .setConfirmText("D'accord, ça marche !")
                    .show();
        }

    }


}