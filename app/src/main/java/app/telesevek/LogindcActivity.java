package app.telesevek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.sinch.android.rtc.SinchError;

import java.util.List;


import javax.annotation.Nullable;

import app.telesevek.RemoteConfig.UpdateHelper;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.MODIFY_AUDIO_SETTINGS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;

public class LogindcActivity extends AppCompatActivity implements UpdateHelper.OnUpdateCheckListener {
    Button mButton;
    TextView mButton2;
    ImageView icondoctor;
    Button textclick;
    private FirebaseAuth firebaseAuth;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private View parentLayout;
    private ProgressDialog mSpinner;
    int c=0;
    FirebaseFirestore fStore;
   /* public static String ACCOUNT_SID="account_sid";
    public static String AUTH_TOKEN="auth_token";*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logindc);
        parentLayout = findViewById(R.id.parentLayout);
        if (!checkPermission()) {
            requestPermission();
        }

        //updateHelper
        UpdateHelper.with(this)
                .OnUpdateCheck(this)
                .check();


       /* final FirebaseRemoteConfig remoteConfig=FirebaseRemoteConfig.getInstance();
        final String idOf=remoteConfig.getString(ACCOUNT_SID);
        Toast.makeText(LogindcActivity.this, idOf, Toast.LENGTH_SHORT).show();*/
        /*remoteConfig.fetch(5)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            remoteConfig.fetchAndActivate();
                            Toast.makeText(LogindcActivity.this, idOf, Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/

       /* String url = "https://fcm.googleapis.com/fcm/send";
        AsyncHttpClient client = new AsyncHttpClient();

        client.addHeader(HttpHeaders.AUTHORIZATION, "key=AAAAIz3KQd8:APA91bFJiG-094nuzkfO0xhkCoeCx6GQQv6nOoKrOc52za0afjY66dENqplOcke5zdJE7yrMBkKR_byfMWlcf3M4-GaSS2BlFv2HCvcT-ON8YIDdEQ6dC_rAOVjCyhi8T9Qo2WG2GVIo");
        client.addHeader(HttpHeaders.CONTENT_TYPE, RequestParams.APPLICATION_JSON);

        try {
            JSONObject params = new JSONObject();


            JSONObject notificationObject = new JSONObject();
            notificationObject.put("body", "TEST");
            notificationObject.put("title","SAGAR");

            params.put("notification", notificationObject);

            StringEntity entity = new StringEntity(params.toString());

            client.post(getApplicationContext(), url, entity, RequestParams.APPLICATION_JSON, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(LogindcActivity.this, "failed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                    Toast.makeText(LogindcActivity.this, "success", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {

        }*/


       /* icondoctor = findViewById(R.id.iconp);
        icondoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SharedPreferences prefs = getSharedPreferences("Image", MODE_PRIVATE);
                final String phonenumber = prefs.getString("pimageid", null);

                if(phonenumber != null) {
                    if (phonenumber.isEmpty()) {
                        Toast.makeText(LogindcActivity.this, "Please enter a name", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (!phonenumber.equals(getSinchServiceInterface().getUserName())) {
                        getSinchServiceInterface().stopClient();
                    }

                    if (!getSinchServiceInterface().isStarted()) {
                        getSinchServiceInterface().startClient(phonenumber);
                        showSpinner();
                    } else {
                        openPlaceCallActivity();
                    }
                }
                else {
                    openPlaceCallActivity();
                }
            }
        });*/

        textclick = findViewById(R.id.btConsultDoctor);
        textclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SharedPreferences prefs = getSharedPreferences("Image", MODE_PRIVATE);
                final String phonenumber = prefs.getString("pimageid", null);

                Intent Doctowillcallyou = new Intent(LogindcActivity.this, app.telesevek.ScratchCardNew.class);
                startActivity(Doctowillcallyou);

               /* if(phonenumber != null) {
                    if (phonenumber.isEmpty()) {
                        Toast.makeText(LogindcActivity.this, "Please enter a name", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (!phonenumber.equals(getSinchServiceInterface().getUserName())) {
                        getSinchServiceInterface().stopClient();
                    }

                    if (!getSinchServiceInterface().isStarted()) {
                        getSinchServiceInterface().startClient(phonenumber);
                        showSpinner();
                    } else {
                        openPlaceCallActivity();
                    }
                }
                else {
                    openPlaceCallActivity();
                }*/
            }
        });



        mButton2 = findViewById(R.id.tvIAmDoctor);
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent chemistinten = new Intent(LogindcActivity.this,  app.telesevek.PhoneAuthDoctor.MainActivity.class);
                startActivity(chemistinten);
            }
        });
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
        new AlertDialog.Builder(LogindcActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void openPlaceCallActivity() {
        Intent Doctowillcallyou = new Intent(LogindcActivity.this, app.telesevek.ScratchCardNew.class);
        startActivity(Doctowillcallyou);
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }

    @Override
    public void onUpdateCheckListener(final String urlApp) {

        AlertDialog alertDialog=new AlertDialog.Builder(this).
                setTitle("New Update Available")
                .setMessage("Please update to the latest version to use all our features")
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        try{
                            Toast.makeText(getApplicationContext(),"Opening app in PlayStore",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlApp)));

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);

    }

    public void update(){

        FirebaseFirestore fstore=FirebaseFirestore.getInstance();
        fstore.collection("ScratchCardNew").whereEqualTo("Value","299")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshots=queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot:snapshots){
                            c++;
                        }
                        Toast.makeText(getApplicationContext(),String.valueOf(c),Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ERROR","error");
                    }
                });
    }
}