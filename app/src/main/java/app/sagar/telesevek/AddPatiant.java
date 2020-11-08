package app.sagar.telesevek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sinch.android.rtc.SinchError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import app.sagar.telesevek.Models.FirebaseUserModel;
import app.sagar.telesevek.Models.Doctor;
import app.sagar.telesevek.PhoneAuthConsulation.MainActivity;
import app.sagar.telesevek.VideoPKG.activity.BaseActivity;
import app.sagar.telesevek.VideoPKG.activity.PlaceCallActivity;
import app.sagar.telesevek.VideoPKG.service.SinchService;
import cz.msebera.android.httpclient.HttpHeaders;
import cz.msebera.android.httpclient.entity.StringEntity;

public class AddPatiant extends BaseActivity implements SinchService.StartFailedListener {
    public static final String TAG = "TAG";
    EditText mFullName,mSymptoms,mPhone;
    EditText mAge;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    private ProgressDialog mSpinner;
    Button past;
    Button buy;
    Button ourdoctor;
    FirebaseFirestore fStore;
    String card;
    String phone;
    RadioGroup radioGroup;
    RadioButton radioButton;
    RadioButton radioButton2;
    String date;
    String userID,Chemistname,chemistid,chemistphonenumber;
    private RequestQueue mRequestQue;
    private String URL = "https://fcm.googleapis.com/fcm/send";
    ProgressDialog pd;

    private static final String TAG2 = "AddPatiant";

    private Button btnLogin;

    String currentDeviceId;

    Doctor user = Doctor.getInstance();
    JSONArray registration_ids = new JSONArray();

    FirebaseDatabase database;
    DatabaseReference usersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patiant);


        SharedPreferences sharedpreferences = getSharedPreferences(user.appPreferences, Context.MODE_PRIVATE);
        user.sharedpreferences = sharedpreferences;

        currentDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Doctor");

        final ProgressDialog Dialog = new ProgressDialog(this);
        Dialog.setMessage("Please wait..");
        Dialog.setCancelable(false);
        Dialog.show();

        usersRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                Dialog.dismiss();

                for (com.google.firebase.database.DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    //Getting the data from snapshot
                    FirebaseUserModel firebaseUserModel = userSnapshot.getValue(FirebaseUserModel.class);


                    firebaseUserModel.setDeviceToken(FirebaseInstanceId.getInstance().getToken());
                    user.login(firebaseUserModel);
                    user.saveFirebaseKey(userSnapshot.getKey());


                    final com.google.firebase.database.ValueEventListener userValueEventListener = new com.google.firebase.database.ValueEventListener() {
                        @Override
                        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                            registration_ids = new JSONArray();

                            for (com.google.firebase.database.DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                System.out.println("Child: " + postSnapshot);
                                //Getting the data from snapshot
                                FirebaseUserModel firebaseUserModel = postSnapshot.getValue(FirebaseUserModel.class);
                                if (!firebaseUserModel.getDeviceToken().isEmpty()) {
                                    registration_ids.put(firebaseUserModel.getDeviceToken());
                                }
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            registration_ids = new JSONArray();


                            System.out.println("The read failed: " + databaseError.getMessage());
                        }
                    };

                    usersRef.addValueEventListener(userValueEventListener);



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Dialog.dismiss();
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });






        mRequestQue = Volley.newRequestQueue(this);
        FirebaseMessaging.getInstance().subscribeToTopic("news");




        mFullName   = findViewById(R.id.ed_pname);
        mSymptoms      = findViewById(R.id.ed_symptom);
        mPhone      = findViewById(R.id.ed_pnumber);
        mAge      = findViewById(R.id.ed_age);
        mRegisterBtn= findViewById(R.id.submit);
        radioGroup = findViewById(R.id.gr);
        radioButton = findViewById(R.id.male);
        radioButton2 = findViewById(R.id.female);


        pd = new ProgressDialog(AddPatiant.this);
        pd.setMessage("loading..");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        past = findViewById(R.id.Past);
        past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent chemistinten = new Intent(AddPatiant.this, MainActivity.class);
                startActivity(chemistinten);
            }
        });


        buy = findViewById(R.id.card);
        buy.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent chemistinten = new Intent(AddPatiant.this, Buycard.class);
                startActivity(chemistinten);
            }
        });

        ourdoctor = findViewById(R.id.odoctor);
        ourdoctor.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Intent chemistinten = new Intent(AddPatiant.this, OurDoctor.class);
                        startActivity(chemistinten);
                    }
                });


        SharedPreferences prefs = getSharedPreferences("User", MODE_PRIVATE);
        String phonenumber = prefs.getString("phone", null);


        /*DocumentReference documentReference = fStore.collection("Chemist").document(phonenumber);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    chemistid = documentSnapshot.getString("ChemistId");
                    Chemistname= documentSnapshot.getString("ChemistName");
                    chemistphonenumber = documentSnapshot.getString("ChemistPhoneNumber");

                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });*/

        card = getIntent().getStringExtra("cardpass");
       /* phone = getIntent().getStringExtra("phonenumber");*/

       /* mPhone.setText(phone);
        mPhone.setEnabled(false);*/


        Date dateonly = new Date();
        SimpleDateFormat dateFormatWithZone = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        final String currentTime = dateFormatWithZone.format(dateonly);

        Toast.makeText(this, ""+currentTime, Toast.LENGTH_SHORT).show();

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String symtoms = mSymptoms.getText().toString().trim();
                final String fullName = mFullName.getText().toString();
               final String phone    = mPhone.getText().toString();


                String result = "";
                result+= (radioButton.isChecked())?"male":(radioButton2.isChecked())?"female":"";




                final String Age = mAge.getText().toString();




                if(TextUtils.isEmpty(fullName)){
                    mFullName.setError("name is Required.");
                    return;
                }

                if(TextUtils.isEmpty(symtoms)){
                    mSymptoms.setError("symtoms is Required.");
                    return;
                }

                if(mPhone.length() < 10){
                    mPhone.setError("phone Must be >= 10 Number");
                    return;
                }

                if(TextUtils.isEmpty(Age)){
                    mAge.setError("Age is Required.");
                    return;
                }
                pd.show();;

                String id = fStore.collection("Patient").document().getId();


                date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                long yourDateMillis1 = System.currentTimeMillis() + (1 * 24 * 60 * 60 * 1000);
                Time yourDate1 = new Time();
                yourDate1.set(yourDateMillis1);
                final String finalexpiredate1 = yourDate1.format("%Y-%m-%d");

                //second expiredate2
                long yourDateMillis2 = System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000);
                Time yourDate2 = new Time();
                yourDate2.set(yourDateMillis2);
                final String finalexpiredate2 = yourDate2.format("%Y-%m-%d");


                // register the user in firebase

                DocumentReference documentReference = fStore.collection("Patient").document(id);
                Map<String,Object> user = new HashMap<>();
                user.put("Name",fullName);
                user.put("PhoneNumber","+91"+phone);
                user.put("Symptoms",symtoms);
                user.put("Age",Age);
                user.put("Gender",result);
                user.put("DateTime",date);
                user.put("Card",card);
                user.put("DateTime2",finalexpiredate1);
                user.put("DateTime3",finalexpiredate2);
                user.put("Time",currentTime);
                user.put("Status","1");
                user.put("ItemId",id);
               /* user.put("ChemistId",chemistid);
                user.put("ChemistName",Chemistname);
                user.put("ChemistPhoneNumber",chemistphonenumber);*/
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                        /*Intent intent = new Intent(AddPatiant.this, PetientLoading.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("phonenumber", phone);*/


                        final String phone    = "+91"+mPhone.getText().toString();
                        if (phone.isEmpty()) {
                            Toast.makeText(AddPatiant.this, "Please enter a name", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (!phone.equals(getSinchServiceInterface().getUserName())) {
                            getSinchServiceInterface().stopClient();
                        }

                        if (!getSinchServiceInterface().isStarted()) {
                            getSinchServiceInterface().startClient(phone);
                            showSpinner();
                        } else {
                            openPlaceCallActivity();
                        }

                        SharedPreferences.Editor image = getSharedPreferences("Image", MODE_PRIVATE).edit();
                        image.putString("pimageid", phone);
                        image.commit();



                        if (registration_ids.length() > 0) {

                            String url = "https://fcm.googleapis.com/fcm/send";
                            AsyncHttpClient client = new AsyncHttpClient();

                            client.addHeader(HttpHeaders.AUTHORIZATION, "key=AAAAIz3KQd8:APA91bFJiG-094nuzkfO0xhkCoeCx6GQQv6nOoKrOc52za0afjY66dENqplOcke5zdJE7yrMBkKR_byfMWlcf3M4-GaSS2BlFv2HCvcT-ON8YIDdEQ6dC_rAOVjCyhi8T9Qo2WG2GVIo");
                            client.addHeader(HttpHeaders.CONTENT_TYPE, RequestParams.APPLICATION_JSON);

                            try {
                                JSONObject params = new JSONObject();

                                params.put("registration_ids", registration_ids);

                                JSONObject notificationObject = new JSONObject();
                                notificationObject.put("body", "Patient Name is"+fullName);
                                notificationObject.put("title", "New Patient");

                                params.put("notification", notificationObject);

                                StringEntity entity = new StringEntity(params.toString());

                                client.post(getApplicationContext(), url, entity, RequestParams.APPLICATION_JSON, new TextHttpResponseHandler() {
                                    @Override
                                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                                        Log.i(TAG, responseString);
                                        Toast.makeText(AddPatiant.this, "failed Notification", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                                        Log.i(TAG, responseString);
                                        Toast.makeText(AddPatiant.this, "Send Notification", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } catch (Exception e) {

                            }


                        }


                        pd.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                        pd.dismiss();
                    }
                });


                DocumentReference ststusup = fStore.collection("ScratchCard").document(card);
                ststusup.update("Code",card);
                ststusup.update("Status","1")
               .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });


                DocumentReference pushnotify = fStore.collection("Notification").document("PatientRequestNotification");
                Map<String,Object> usernotify = new HashMap<>();
                usernotify.put("Notification","True");
                usernotify.put("Name",fullName);
                pushnotify.set(usernotify).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });


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

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                        System.exit(0);
                    }
                }).setNegativeButton("no", null).show();
    }






    @Override
    protected void onServiceConnected() {
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
         Intent intent = new Intent(AddPatiant.this, Doctowillcallyou.class);
         startActivity(intent);
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }
}
