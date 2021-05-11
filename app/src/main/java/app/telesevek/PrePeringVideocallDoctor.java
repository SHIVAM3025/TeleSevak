package app.telesevek;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.sinch.android.rtc.SinchError;

import app.sinch.BaseActivity;
import app.sinch.SinchService;
import app.telesevek.Activity.MainActivity;

public class PrePeringVideocallDoctor extends BaseActivity implements SinchService.StartFailedListener {
    private ProgressDialog mSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_pering_videocall);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                log();

            }
        }, 100);

    }

    @Override
    protected void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
    }


    @Override
    public void onStarted() {
        mSpinner.dismiss();

        Intent intent = new Intent(PrePeringVideocallDoctor.this, DoctorSideNew.class);
        startActivity(intent);
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Login");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }
    public void log(){

        SharedPreferences prefs = getSharedPreferences("User", MODE_PRIVATE);
        final String username = prefs.getString("phone", null);
        if (username != null) {
            if (username.isEmpty()) {
                Toast.makeText(PrePeringVideocallDoctor.this, "INVALID name", Toast.LENGTH_LONG).show();
                return;
            }

            if (!username.equals(getSinchServiceInterface().getUserName())) {
                getSinchServiceInterface().stopClient();
            }

            if (!getSinchServiceInterface().isStarted()) {
                getSinchServiceInterface().startClient(username);
                showSpinner();
                Intent intent = new Intent(PrePeringVideocallDoctor.this, DoctorSideNew.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(PrePeringVideocallDoctor.this, DoctorSideNew.class);
                startActivity(intent);
            }

        }
        else {
            Intent intent = new Intent(PrePeringVideocallDoctor.this, DoctorSideNew.class);
            startActivity(intent);
        }

    }

}