package iut.parisdescartes.hey.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;

import cn.pedant.SweetAlert.SweetAlertDialog;
import iut.parisdescartes.hey.R;

/**
 * Created by Kévin on 09/03/2016.
 */
public class SettingsActivity extends AppCompatActivity {

    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        loginButton = (LoginButton) findViewById(R.id.button_fb);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkLogOut();

            }
        });
    }

    private void checkLogOut() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Attention")
                .setContentText("Voulez vous vraiment vous déconnecter de facebook ? Si oui l'application se fermera")
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
                        LoginManager.getInstance().logOut();
                        sDialog.cancel();
                        SettingsActivity.this.finishAffinity();
                    }
                })
                .show();
    }

}

