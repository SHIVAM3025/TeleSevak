package app.sagar.telesevek.VideoPKG.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.sinch.android.rtc.SinchError;


import app.sagar.telesevek.R;
import app.sagar.telesevek.VideoPKG.service.SinchService;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.MODIFY_AUDIO_SETTINGS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;

public class LoginActivity extends BaseActivity implements SinchService.StartFailedListener {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private View parentLayout;
    private Button mLoginButton;
    private EditText mLoginName;
    private ProgressDialog mSpinner;
    String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        parentLayout = findViewById(R.id.parentLayout);
        mLoginName = (EditText) findViewById(R.id.loginName);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setEnabled(false);
        userName = "DAGAR";
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClicked();
            }
        });
        if (!checkPermission()) {
            requestPermission();
        }
    }

    @Override
    protected void onServiceConnected() {
        mLoginButton.setEnabled(true);
        getSinchServiceInterface().setStartListener(this);
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
    public void onStarted() {
        openPlaceCallActivity();
    }

    private void loginClicked() {
        if (!checkPermission()) {
            requestPermission();
        } else {


            if (userName.isEmpty()) {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
                return;
            }

            if (!userName.equals(getSinchServiceInterface().getUserName())) {
                getSinchServiceInterface().stopClient();
            }

            if (!getSinchServiceInterface().isStarted()) {
                getSinchServiceInterface().startClient(userName);
                showSpinner();
            } else {
                openPlaceCallActivity();
            }
        }
    }

    private void openPlaceCallActivity() {
        Intent mainActivity = new Intent(this, PlaceCallActivity.class);
        startActivity(mainActivity);
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }

    private boolean checkPermission() {

        int resRecAudio = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        int resCamera = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int resNetState = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_NETWORK_STATE);
        int resModAudio = ContextCompat.checkSelfPermission(getApplicationContext(), MODIFY_AUDIO_SETTINGS);
        int resPhonState = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);

        return resCamera == PackageManager.PERMISSION_GRANTED
                && resRecAudio == PackageManager.PERMISSION_GRANTED
                && resNetState == PackageManager.PERMISSION_GRANTED
                && resModAudio == PackageManager.PERMISSION_GRANTED
                && resPhonState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{RECORD_AUDIO, CAMERA, ACCESS_NETWORK_STATE, MODIFY_AUDIO_SETTINGS, READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean resCameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean resRecAudioAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean resNetStateAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean resModAudioAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean resPhonStateAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    if (resCameraAccepted && resRecAudioAccepted && resNetStateAccepted && resModAudioAccepted && resPhonStateAccepted)
                        Snackbar.make(parentLayout, "Permission Granted, Now you can access location data and camera.", Snackbar.LENGTH_LONG).show();
                    else {
                        Snackbar.make(parentLayout, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_NETWORK_STATE)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{RECORD_AUDIO, CAMERA, ACCESS_NETWORK_STATE, MODIFY_AUDIO_SETTINGS, READ_PHONE_STATE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
