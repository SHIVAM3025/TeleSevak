package app.telesevek;

import androidx.annotation.NonNull;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.StorageReference;
import com.sinch.android.rtc.SinchError;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import javax.annotation.Nullable;

import app.sinch.BaseActivity;
import app.sinch.PlaceCallActivity;
import app.sinch.SinchService;
import app.telesevek.uploadpkg.PreUpload;
import app.telesevek.uploadpkg.ShowImageActivity;
import app.telesevek.uploadpkg.UploadImage;
import app.telesevek.uploadpkg.ViewUploadsActivity;
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
import static app.telesevek.AddPatiant.ACCOUNT_SID;

import static app.telesevek.AddPatiant.AUTH_TOKEN;


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
    Button sendpdf;
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
    String DoctorNum;

    List<String> ls=new ArrayList<>();

    public static String account_sid = "account_sid";
    public static String auth_token = "auth_token";
    public static String fromNumber="from";

    public static String ACCOUNT_SID ;
    public static String AUTH_TOKEN ;
    public static String from;

    Button btnsshow;


    RequestQueue mRequestQue;
    String URL = "https://fcm.googleapis.com/fcm/send";
    String tokenID;
    FirebaseFirestore fstore;

    TextView txt_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_call);

        receiveCredentials();


        pname = findViewById(R.id.textpname);
        PSymtoms = findViewById(R.id.textpsymtoms);
        Pphone = findViewById(R.id.textphone);
        Pgender = findViewById(R.id.textgender);
        Page = findViewById(R.id.textage);
        btnsshow = findViewById(R.id.SHOWIMAGE);


        audio = findViewById(R.id.audio);
        video = findViewById(R.id.video);
        sendscan = findViewById(R.id.send);
        sendpdf = findViewById(R.id.sendpdf);
        txt_back = findViewById(R.id.backarrow);
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
        DoctorNum=bundle.getString("DoctorNum");

        mRequestQue= Volley.newRequestQueue(this);
        fstore= FirebaseFirestore.getInstance();
        getToken();

        Toast.makeText(this, ""+Patientcard, Toast.LENGTH_SHORT).show();

        txt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go = new Intent(DoctorCallActivity.this,DoctorSideNew.class);
                startActivity(go);
            }
        });

        if (consultitemid != null){
            DocumentReference documentReference2 = fStore.collection("Consultation").document(consultitemid);
            documentReference2.addSnapshotListener(DoctorCallActivity.this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot.exists()){


                        final String URL = documentSnapshot.getString("pre0");
                        final String URL1 = documentSnapshot.getString("pre1");
                        final String URL2 = documentSnapshot.getString("pre2");
                        final String URL3 = documentSnapshot.getString("pre3");
                        final String URL4 = documentSnapshot.getString("pre4");
                        if (URL != null){
                            btnsshow.setVisibility(View.VISIBLE);
                            btnsshow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    Intent sendStuff = new Intent(DoctorCallActivity.this, PatientFullImageShow.class);
                                    sendStuff.putExtra("url1", URL);
                                    sendStuff.putExtra("url2", URL1);
                                    sendStuff.putExtra("url3", URL2);
                                    sendStuff.putExtra("url4", URL3);
                                    sendStuff.putExtra("url5", URL4);
                                    startActivity(sendStuff);

                                }
                            });
                        }
                        else {
                            btnsshow.setVisibility(View.GONE);
                        }


                    }else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });
        }









        pname.setText(Pname);
        PSymtoms.setText(Symtoms);

        Pphone.setText(PatientPassId);
        Pgender.setText(gender);
        Page.setText(age);

        ls.add(PatientPassId);







        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+ PatientPassId));
                startActivity(intent);

                isVideo=false;

                if (Patientcard != null) {
                    DocumentReference ststusup = fStore.collection("ScratchCard").document(Patientcard);
                    /* ststusup.update("Status","2");*/
                    ststusup.update("ConsultationID", consultitemid)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(DoctorCallActivity.this, "changed status", Toast.LENGTH_SHORT).show();
                                    sendSMS(DoctorName, Pname);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }


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
        final String Dname = "DR";




        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (!checkPermission()) {
                    requestPermission();
                } else {


                    if (DoctorNum.isEmpty()) {
                        Toast.makeText(DoctorCallActivity.this, "Please enter a name", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (!DoctorNum.equals(getSinchServiceInterface().getUserName())) {
                        getSinchServiceInterface().stopClient();
                    }

                    if (!getSinchServiceInterface().isStarted()) {
                        getSinchServiceInterface().startClient(DoctorNum);
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
                sendStuff.putExtra("patientphone", PatientPassId);
                startActivity(sendStuff);
            }
        });

        sendpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendStuff = new Intent(DoctorCallActivity.this, ViewUploadsActivity.class);
                sendStuff.putExtra("cid", consultitemid);
                startActivity(sendStuff);
            }
        });




    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent=new Intent(getApplicationContext(),DoctorSideNew.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(0,0);
        startActivity(intent);
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
        sendStuff.putExtra("PatientPassId",PatientPassId);
        sendStuff.putExtra("PatientName",Pname);
        sendStuff.putExtra("DoctorName",DoctorName);
        sendStuff.putExtra("DoctorNum",DoctorNum);
        sendStuff.putExtra("Conid",consultitemid);
        startActivity(sendStuff);

        isVideo=true;



        if (Patientcard != null) {
            DocumentReference ststusup = fStore.collection("ScratchCard").document(Patientcard);
            /* ststusup.update("Status","Completed");*/
            ststusup.update("ConsultationID", consultitemid)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(DoctorCallActivity.this, "changed status", Toast.LENGTH_SHORT).show();
                            //sendSMS(DoctorName,Pname);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }


        try {
            sendNotificationToUser(DoctorName,Pname);
        } catch (JSONException e) {
            e.printStackTrace();
        }


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

            String body = "Patient- "+ PatientName+ " "+ PatientPassId + " just had a "+via+" with "+dName+" "+DoctorNum ;
            String from = "+17633258036";
            String to = ls.get(j);

            String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                    ( ACCOUNT_SID+":"+ AUTH_TOKEN).getBytes(), Base64.NO_WRAP
            );

            Map<String, String> data = new HashMap<>();
            data.put("From", from);
            data.put("To", to);
            data.put("Body", body);
            data.put("locale","hi");

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.twilio.com/2010-04-01/")
                    .build();
            AddPatiant.TwilioApi api = retrofit.create(AddPatiant.TwilioApi.class);

            api.sendMessage(ACCOUNT_SID, base64EncodedCredentials, data).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) Log.d("TAG", "onResponse->success");
                    else {
                        Log.d("TAG", "onResponse->failure");
                        Log.i("RESPONSE",response.toString());
                    }
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

    public static void receiveCredentials(){

        final FirebaseRemoteConfig remoteConfig= FirebaseRemoteConfig.getInstance();
        ACCOUNT_SID=remoteConfig.getString(account_sid);
        AUTH_TOKEN=remoteConfig.getString(auth_token);
        from=remoteConfig.getString(fromNumber);
        Log.i("remote",ACCOUNT_SID+"="+AUTH_TOKEN+"from"+from);
        //Toast.makeText(this, idOf, Toast.LENGTH_SHORT).show();
        remoteConfig.fetch(120)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            remoteConfig.fetchAndActivate();

                            //Toast.makeText(LogindcActivity.this, idOf, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void getToken(){
        fstore.collection("Consultation").document(consultitemid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        tokenID=documentSnapshot.getString("TokenFCM");
                        assert tokenID != null;
                        Log.i("token",tokenID+" SUCCESS");
                        Toast.makeText(DoctorCallActivity.this, ""+tokenID, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("token","failed to get");
                    }
                });
    }

    public void sendNotificationToUser(String callID,String username) throws JSONException {
        JSONObject fcm=new JSONObject();
        fcm.put("to",tokenID);

        JSONObject dataObject=new JSONObject();
        dataObject.put("title","Incoming Call");
        dataObject.put("body","You have a call from Doctor "+DoctorName);
        dataObject.put("callID",callID);
        dataObject.put("username",username);

        fcm.put("data",dataObject);

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, URL, fcm, response -> {


            Toast.makeText(this, "SUCCESS", Toast.LENGTH_SHORT).show();
            Log.i("response",response.toString());
        }, error -> {
            Toast.makeText(this, "FAIL", Toast.LENGTH_SHORT).show();
            Log.i("response",error.networkResponse.toString());
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("content-type","application/json");
                params.put("authorization","key=AAAAIz3KQd8:APA91bFJiG-094nuzkfO0xhkCoeCx6GQQv6nOoKrOc52za0afjY66dENqplOcke5zdJE7yrMBkKR_byfMWlcf3M4-GaSS2BlFv2HCvcT-ON8YIDdEQ6dC_rAOVjCyhi8T9Qo2WG2GVIo");
                return params;
            }
        };
        mRequestQue.add(request);

    }



}