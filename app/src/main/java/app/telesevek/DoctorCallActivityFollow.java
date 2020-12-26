package app.telesevek;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.sinch.android.rtc.SinchError;


import app.sinch.BaseActivity;
import app.sinch.PlaceCallActivity;
import app.sinch.SinchService;
import app.telesevek.uploadpkg.UploadImage;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.MODIFY_AUDIO_SETTINGS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;

public class DoctorCallActivityFollow extends BaseActivity implements SinchService.StartFailedListener {
    String PatientPassId;
    String Patientcard;
    String Pname;
    String Symtoms;
    String itemid;
    String consultitemid;
    String gender;
    String age;
    TextView pname;
    TextView PSymtoms;
    TextView Pphone;
    TextView Pgender;
    TextView Page;
    String phone;
    private View parentLayout;
    FirebaseFirestore fStore;
    Button audio;
    Button video;
    Button sendscan;
    private static final int PERMISSION_REQUEST_CODE = 200;
    ImageView uploadPicIV;
    private ProgressDialog mSpinner;
    final int IMAGE_REQUST = 71;
    Uri imageLocationPath;
    Button past;
    Button Followup;
    Button Current;
    StorageReference objectStorageReference;
    FirebaseFirestore objectFirebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_call);

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
        gender = bundle.getString("pgender");
        age = bundle.getString("page");
        itemid = bundle.getString("itemid");
        consultitemid = bundle.getString("consultitemid");


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
                Intent chemistinten = new Intent(DoctorCallActivityFollow.this, DoctorSidePastConsulation.class);
                startActivity(chemistinten);
            }
        });

        Followup = findViewById(R.id.followup);
        Followup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent chemistinten = new Intent(DoctorCallActivityFollow.this, DoctorSideFollowupConsulation.class);
                startActivity(chemistinten);
            }
        });

        Current = findViewById(R.id.card);
        Current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent chemistinten = new Intent(DoctorCallActivityFollow.this, DoctorSideNew.class);
                startActivity(chemistinten);
            }
        });


        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+ PatientPassId));
                startActivity(intent);

                DocumentReference ststusup = fStore.collection("ScratchCard").document(Patientcard);
                /* ststusup.update("Status","2");*/
                ststusup.update("ConsultationID",consultitemid)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(DoctorCallActivityFollow.this, "changed status", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(DoctorCallActivityFollow.this, "wrong", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(DoctorCallActivityFollow.this, "wrong", Toast.LENGTH_SHORT).show();
                    }
                });







            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String Dname = "DR Jain";
                if (!checkPermission()) {
                    requestPermission();
                } else {


                    if (Dname.isEmpty()) {
                        Toast.makeText(DoctorCallActivityFollow.this, "Please enter a name", Toast.LENGTH_LONG).show();
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

               /* new AlertDialog.Builder(DoctorCallActivityFollow.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Telesevek App under Developmemt")
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

                Intent sendStuff = new Intent(DoctorCallActivityFollow.this, UploadImage.class);
                sendStuff.putExtra("DocuId", consultitemid);
                startActivity(sendStuff);
            }
        });


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
        Intent sendStuff = new Intent(DoctorCallActivityFollow.this, PlaceCallActivity.class);
        sendStuff.putExtra("PatientPassId",PatientPassId );
        sendStuff.putExtra("PatientName",Pname);
        sendStuff.putExtra("DoctorName","Dr Jain");
        startActivity(sendStuff);


        DocumentReference ststusup = fStore.collection("ScratchCard").document(Patientcard);
        /* ststusup.update("Status","Completed");*/
        ststusup.update("ConsultationID",consultitemid)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DoctorCallActivityFollow.this, "changed status", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DoctorCallActivityFollow.this, "wrong", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DoctorCallActivityFollow.this, "wrong", Toast.LENGTH_SHORT).show();
            }
        });


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
        new AlertDialog.Builder(DoctorCallActivityFollow.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(DoctorCallActivityFollow.this,DoctorSideNew.class);
                        startActivity(intent);
                        finish();

                        System.exit(0);
                    }
                }).setNegativeButton("no", null).show();
    }
}