package app.sagar.telesevek;

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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.sinch.android.rtc.SinchError;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.sagar.telesevek.VideoPKG.activity.BaseActivity;
import app.sagar.telesevek.VideoPKG.activity.LoginActivity;
import app.sagar.telesevek.VideoPKG.activity.PlaceCallActivity;
import app.sagar.telesevek.VideoPKG.service.SinchService;
import app.sagar.telesevek.uploadpkg.UploadImage;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.MODIFY_AUDIO_SETTINGS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static app.sagar.telesevek.AddPatiant.ACCOUNT_SID;
import static app.sagar.telesevek.AddPatiant.AUTH_TOKEN;


public class DoctorCallActivity extends BaseActivity implements SinchService.StartFailedListener{
    String PatientPassId;
    String Patientcard;
    String Pname;
    String Symtoms;
    String itemid;
    String consultitemid;
    String gender;
    String age;
    String Did;
    TextView pname;
    TextView PSymtoms;
    TextView Pphone;
    TextView Pgender;
    TextView Page;
    String phone;
    FirebaseFirestore fStore;
    Button audio;
    Button video;
    Button sendscan;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private View parentLayout;
    ImageView uploadPicIV;

    final int IMAGE_REQUST = 71;
    Uri imageLocationPath;
    Button past;
    Button Followup;
    Button Current;
    StorageReference objectStorageReference;
    FirebaseFirestore objectFirebaseFirestore;
    private ProgressDialog mSpinner;
    boolean isVideo;
    String DoctorName;

    List<String> ls=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_call);

        ls.add("+917054466515");
        ls.add("+918840974859");
        ls.add("+919599225823");

        pname = findViewById(R.id.textpname);
        PSymtoms = findViewById(R.id.textpsymtoms);
        Pphone = findViewById(R.id.textphone);
        Pgender = findViewById(R.id.textgender);
        Page = findViewById(R.id.textage);



        audio = findViewById(R.id.audio);
        video = findViewById(R.id.video);
        sendscan = findViewById(R.id.send);
        fStore = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        PatientPassId = bundle.getString("PatientPassId");
        Patientcard = bundle.getString("PatientCard");
        Pname = bundle.getString("Pname");
        Symtoms = bundle.getString("Symtoms");
        itemid = bundle.getString("itemid");
        consultitemid = bundle.getString("consultitemid");
        gender = bundle.getString("pgender");
        age = bundle.getString("page");
        DoctorName=bundle.getString("DoctorName");



        pname.setText(Pname);
        PSymtoms.setText(Symtoms);
        Pphone.setText(PatientPassId);
        Pgender.setText(gender);
        Page.setText(age);





        past = findViewById(R.id.past);
        past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent chemistinten = new Intent(DoctorCallActivity.this, DoctorSidePastConsulation.class);
                startActivity(chemistinten);
            }
        });

        Followup = findViewById(R.id.followup);
        Followup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent chemistinten = new Intent(DoctorCallActivity.this, DoctorSideFollowupConsulation.class);
                startActivity(chemistinten);
            }
        });

        Current = findViewById(R.id.card);
        Current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent chemistinten = new Intent(DoctorCallActivity.this, DoctorSideNew.class);
                startActivity(chemistinten);
            }
        });


        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+ PatientPassId));
                startActivity(intent);

                isVideo=false;

                DocumentReference ststusup = fStore.collection("ScratchCard").document(Patientcard);
               /* ststusup.update("Status","2");*/
                ststusup.update("ConsultationID",consultitemid)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(DoctorCallActivity.this, "changed status", Toast.LENGTH_SHORT).show();
                                sendSMS(DoctorName,Pname);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


                DocumentReference consult = fStore.collection("Patient").document(itemid);
                consult.update("Status","Completed")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DoctorCallActivity.this, "wrong", Toast.LENGTH_SHORT).show();
                    }
                });


                DocumentReference consultitem = fStore.collection("Consultation").document(consultitemid);
                consultitem.update("Status","Completed")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DoctorCallActivity.this, "wrong", Toast.LENGTH_SHORT).show();
                    }
                });







            }
        });

        SharedPreferences prefs2 = getSharedPreferences("doctornamevideocall", MODE_PRIVATE);
        /*final String Dname = prefs2.getString("dnamecall", "sagar");*/
        final String Dname = "DR Jain";




        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (!checkPermission()) {
                    requestPermission();
                } else {


                    if (Dname.isEmpty()) {
                        Toast.makeText(DoctorCallActivity.this, "Please enter a name", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (!Dname.equals(getSinchServiceInterface().getUserName())) {
                        getSinchServiceInterface().stopClient();
                    }

                    if (!getSinchServiceInterface().isStarted()) {
                        getSinchServiceInterface().startClient(Dname);
                        showSpinner();
                    } else {
                        openPlaceCallActivity();
                    }
                }

              /*  new AlertDialog.Builder(DoctorCallActivity.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Telesevek App under Developmemt")
                        .setMessage("App is Under Construction")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setNegativeButton("close", null).show();
*/

            }
        });


        sendscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendStuff = new Intent(DoctorCallActivity.this, UploadImage.class);
                sendStuff.putExtra("DocuId", consultitemid);
                startActivity(sendStuff);
            }
        });




    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(DoctorCallActivity.this,DoctorSideNew.class);
                        startActivity(intent);
                        finish();

                        System.exit(0);
                    }
                }).setNegativeButton("no", null).show();
    }


    @Override
    protected void onServiceConnected() {
        video.setEnabled(true);
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

    private void openPlaceCallActivity() {
        Intent sendStuff = new Intent(DoctorCallActivity.this, PlaceCallActivity.class);
        sendStuff.putExtra("PatientPassId",PatientPassId );
        startActivity(sendStuff);

        isVideo=true;


        DocumentReference ststusup = fStore.collection("ScratchCard").document(Patientcard);
       /* ststusup.update("Status","Completed");*/
        ststusup.update("ConsultationID",consultitemid)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DoctorCallActivity.this, "changed status", Toast.LENGTH_SHORT).show();
                        sendSMS(DoctorName,Pname);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        DocumentReference consult = fStore.collection("Patient").document(itemid);
        consult.update("Status","Completed")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DoctorCallActivity.this, "wrong", Toast.LENGTH_SHORT).show();
            }
        });


        DocumentReference consultitem = fStore.collection("Consultation").document(consultitemid);
        consultitem.update("Status","Completed")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DoctorCallActivity.this, "wrong", Toast.LENGTH_SHORT).show();
            }
        });


    }


    public void sendSMS(String dName,String PatientName){

        String via;
        if(isVideo){via="Video Call";}
        else {
            via="Phone Call";
        }

        Toast.makeText(this, "SMS sent!", Toast.LENGTH_SHORT).show();
        for(int j=0;j<ls.size();j++){

            String body = "Patient- "+ PatientName+ "just had a "+via+" with "+dName;
            String from = "+15302703337";
            String to = ls.get(j);

            String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                    (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP
            );

            Map<String, String> data = new HashMap<>();
            data.put("From", from);
            data.put("To", to);
            data.put("Body", body);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.twilio.com/2010-04-01/")
                    .build();
            AddPatiant.TwilioApi api = retrofit.create(AddPatiant.TwilioApi.class);

            api.sendMessage(ACCOUNT_SID, base64EncodedCredentials, data).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) Log.d("TAG", "onResponse->success");
                    else Log.d("TAG", "onResponse->failure");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("TAG", "onFailure");
                }
            });



        }

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
        new AlertDialog.Builder(DoctorCallActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}