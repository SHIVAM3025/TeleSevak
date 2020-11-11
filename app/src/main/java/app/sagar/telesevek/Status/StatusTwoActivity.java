package app.sagar.telesevek.Status;

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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import app.sagar.telesevek.ActivityCallDoctortoP;
import app.sagar.telesevek.AddPatiant;
import app.sagar.telesevek.Buycard;
import app.sagar.telesevek.DoctorCallActivity;
import app.sagar.telesevek.DoctorSideNew;
import app.sagar.telesevek.Doctowillcallyou;
import app.sagar.telesevek.Models.Doctor;
import app.sagar.telesevek.Models.FirebaseUserModel;
import app.sagar.telesevek.OurDoctor;
import app.sagar.telesevek.PhoneAuthConsulation.MainActivity;
import app.sagar.telesevek.R;
import app.sagar.telesevek.ScratchCardNew;
import cz.msebera.android.httpclient.HttpHeaders;
import cz.msebera.android.httpclient.entity.StringEntity;

public class StatusTwoActivity extends AppCompatActivity {
    Button past;
    Button buy;
    Button our;
    Button consult;
    Button viewbill;
    Button followup;
    String dateFOLLOW;
    Date date1;
    Date date;
    String fullName;
    FirebaseFirestore fStore;
    ProgressDialog pd;
    String oldtime;
    String consultitemid;
    String patientid;
    String patiencard;
    public String Card;
    String ConsulID;
    String currentDeviceId;
    Doctor user = Doctor.getInstance();
    JSONArray registration_ids = new JSONArray();
    FirebaseDatabase database;
    DatabaseReference usersRef;
    private static final String TAG = "AddPatiant";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_two);

        viewbill = findViewById(R.id.viewbill);
        followup = findViewById(R.id.followup);

        Bundle bundle = getIntent().getExtras();
        Card = bundle.getString("card");



        pd = new ProgressDialog(StatusTwoActivity.this);
        pd.setMessage("loading..");

        fStore = FirebaseFirestore.getInstance();

        SharedPreferences prefs = getSharedPreferences("Image", MODE_PRIVATE);
        final String phonenumber = prefs.getString("pimageid", "nodata");

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



        pd.show();
        fStore.collection("Consultation")
                .whereEqualTo("PatientPhone",phonenumber)//looks for the corresponding value with the field
                // in the database
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            for (DocumentSnapshot document : task.getResult()) {
                             pd.dismiss();
                                oldtime = document.getString("Time");
                                patiencard = document.getString("PatientCard");
                                consultitemid = document.getString("ConsultationId");
                                patiencard = document.getString("PatientId");

                                   // These values must exactly match the fields you have in your db
                                // 5 min logic

                                if (oldtime != null) {
                                    Date date = new Date();
                                    SimpleDateFormat dateFormatWithZone = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    String currentDate = dateFormatWithZone.format(date);

                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    date = new Date(System.currentTimeMillis());
                                    try {
                                        date = formatter.parse(oldtime);
                                    } catch (ParseException ex) {
                                        ex.printStackTrace();
                                    }


                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");


                                    try {
                                        date1 = simpleDateFormat.parse(currentDate);
                                    } catch (ParseException ex) {
                                        ex.printStackTrace();
                                    }


                                    long difference = date.getTime() - date1.getTime();
                                    int days = (int) (difference / (1000 * 60 * 60 * 24));
                                    int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
                                    int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
                                    days = (days < 0 ? -days : days);

                                    Toast.makeText(StatusTwoActivity.this, ""+days, Toast.LENGTH_SHORT).show();

                                    if (days < 30) {
                                        viewbill.setVisibility(View.VISIBLE);
                                        followup.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        DocumentReference ststusup = fStore.collection("ScratchCard").document(patiencard);
                                        ststusup.update("Status","2");
                                        ststusup.update("ConsultationID",consultitemid)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(StatusTwoActivity.this, "changed status", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });


                                        DocumentReference consult = fStore.collection("Patient").document(patientid);
                                        consult.update("Status","2")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(StatusTwoActivity.this, "wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                        DocumentReference consultitem = fStore.collection("Consultation").document(consultitemid);
                                        consultitem.update("Status","2")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(StatusTwoActivity.this, "wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                    }
                       /* else {
                            Toast.makeText(ScratchCardNew.this, "your card code be already used", Toast.LENGTH_SHORT).show();
                        }*/

                                    //

                                }
                                else {
                                    pd.dismiss();
                                }


                            }



                            }
                        else {
                            pd.dismiss();
                        }


                    }
                });



        viewbill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chemistinten = new Intent(StatusTwoActivity.this, MainActivity.class);
                startActivity(chemistinten);
            }
        });


        followup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                     pd.show();
                        if (Card != null){
                            DocumentReference documentReference2 = fStore.collection("ScratchCard").document(Card);
                            documentReference2.addSnapshotListener(StatusTwoActivity.this, new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                    if(documentSnapshot.exists()){
                                        ConsulID = documentSnapshot.getString("ConsultationID");


                                        if (ConsulID != null) {
                                            DocumentReference consultitem = fStore.collection("Consultation").document(ConsulID);
                                            consultitem.update("Status", "3")
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            pd.dismiss();
                                                            Toast.makeText(StatusTwoActivity.this, "Follow up Successfully", Toast.LENGTH_SHORT).show();
                                                            DocumentReference ststusup = fStore.collection("ScratchCard").document(Card);
                                                            ststusup.update("Status","3")
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {

                                                                            if (ConsulID != null) {
                                                                                DocumentReference documentReferencefollow = fStore.collection("Consultation").document(ConsulID);
                                                                                documentReferencefollow.addSnapshotListener(StatusTwoActivity.this, new EventListener<DocumentSnapshot>() {
                                                                                    @Override
                                                                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                                                                        if (documentSnapshot.exists()) {
                                                                                            fullName = documentSnapshot.getString("PName");
                                                                                            
                                                                                            notification();

                                                                                        } else {
                                                                                            Log.d("tag", "onEvent: Document do not exists");
                                                                                        }
                                                                                    }
                                                                                });

                                                                            }

                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                }
                                                            });
                                                        }

                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(StatusTwoActivity.this, "Plase Check after some time", Toast.LENGTH_SHORT).show();
                                                    pd.dismiss();
                                                }
                                            });
                                        }

                                    }else {
                                        Log.d("tag", "onEvent: Document do not exists");
                                    }
                                }
                            });
                        }


                    }
                });



        BottomNavigationView bottomNav=findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.consultDoctor);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.pastConsult:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.buyCard:
                        startActivity(new Intent(getApplicationContext(),Buycard.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.ourDoctors:
                        startActivity(new Intent(getApplicationContext(),OurDoctor.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.consultDoctor:
                        return true;
                }
                return false;
            }
        });











        /*
        past = findViewById(R.id.Past);
        past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent chemistinten = new Intent(StatusTwoActivity.this, MainActivity.class);
                startActivity(chemistinten);
            }
        });
        buy = findViewById(R.id.card);
        buy.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent chemistinten = new Intent(StatusTwoActivity.this, Buycard.class);
                        startActivity(chemistinten);
                    }
                });

        our = findViewById(R.id.odoctor);
        our.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent chemistinten = new Intent(StatusTwoActivity.this, OurDoctor.class);
                        startActivity(chemistinten);
                    }
                });

        consult = findViewById(R.id.consult);
        consult.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent chemistinten = new Intent(StatusTwoActivity.this, ScratchCardNew.class);
                        startActivity(chemistinten);
                    }
                });
        */

    }
    public void notification(){
        if (registration_ids.length() > 0) {

            String url = "https://fcm.googleapis.com/fcm/send";
            AsyncHttpClient client = new AsyncHttpClient();

            client.addHeader(HttpHeaders.AUTHORIZATION, "key=AAAAIz3KQd8:APA91bFJiG-094nuzkfO0xhkCoeCx6GQQv6nOoKrOc52za0afjY66dENqplOcke5zdJE7yrMBkKR_byfMWlcf3M4-GaSS2BlFv2HCvcT-ON8YIDdEQ6dC_rAOVjCyhi8T9Qo2WG2GVIo");
            client.addHeader(HttpHeaders.CONTENT_TYPE, RequestParams.APPLICATION_JSON);

            try {
                JSONObject params = new JSONObject();

                params.put("registration_ids", registration_ids);

                JSONObject notificationObject = new JSONObject();
                notificationObject.put("body", "Followup Name is"+fullName);
                notificationObject.put("title", "New Followup");

                params.put("notification", notificationObject);

                StringEntity entity = new StringEntity(params.toString());

                client.post(getApplicationContext(), url, entity, RequestParams.APPLICATION_JSON, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                        Log.i(TAG, responseString);
                        Toast.makeText(StatusTwoActivity.this, "failed Notification", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                        Log.i(TAG, responseString);
                        Toast.makeText(StatusTwoActivity.this, "Send Notification", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {

            }


        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(StatusTwoActivity.this,ScratchCardNew.class);
                        startActivity(intent);
                        finish();

                        System.exit(0);
                    }
                }).setNegativeButton("no", null).show();
    }
}