package app.sagar.telesevek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.sinch.android.rtc.SinchError;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import app.sagar.telesevek.PhoneAuthConsulation.MainActivity;
import app.sagar.telesevek.Status.StatusTwoActivity;
import app.sagar.telesevek.VideoPKG.activity.BaseActivity;
import app.sagar.telesevek.VideoPKG.service.SinchService;

import static app.sagar.telesevek.AddPatiant.TAG;
import static com.google.firebase.firestore.FirebaseFirestoreSettings.*;

public class ScratchCardNew extends AppCompatActivity{

   Date date1;
   Date date;


    Button past;
    Button buy;
    Button ourdoctor;

    private ProgressDialog mSpinner;
    FirebaseFirestore fStore;
    EditText scrached;
    Button submit;

    ProgressDialog pd;
    String oldtime;
    String oldcard;

    String Strname;
    String Strdate;
    String Strphone;
    String DoctorId;
    String ConsultationId;
    String cardnumber;
    String dateFOLLOW;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrach_card);

        pd = new ProgressDialog(ScratchCardNew.this);
        pd.setMessage("loading..");

        //


        //

        //

        scrached = findViewById(R.id.etScratch);
        submit = findViewById(R.id.submit);



        fStore = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        fStore.setFirestoreSettings(settings);

        BottomNavigationView bottomNav=findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.consultDoctor);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.pastConsult:
                        startActivity(new Intent(getApplicationContext(),PastConsultationNewLoginScreen.class));
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


        //



         //videocall




                submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardnumber= scrached.getText().toString();

                pd.show();

                if(TextUtils.isEmpty(cardnumber)){
                    scrached.setError("card is Required.");
                    pd.dismiss();
                    return;
                }




                DocumentReference documentReference = fStore.collection("ScratchCard").document(cardnumber);
                documentReference.addSnapshotListener(ScratchCardNew.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot.exists()) {
                            pd.dismiss();

                            oldcard = documentSnapshot.getString("ValidityStatus");
                            Toast.makeText(ScratchCardNew.this, ""+oldcard, Toast.LENGTH_SHORT).show();
                            if("inactive".equals(oldcard)){
                                Toast.makeText(ScratchCardNew.this, "your ScratchCard is inactive", Toast.LENGTH_SHORT).show();
                            }
                           /* else if ("1".equals(oldcard)){


                                SharedPreferences prefs = getSharedPreferences("Image", MODE_PRIVATE);
                                final String phonenumber = prefs.getString("pimageid", "nodata");

                               if(phonenumber != null){
                                    if (phonenumber.isEmpty()) {
                                        Toast.makeText(ScratchCardNew.this, "Please enter a name", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    if (!phonenumber.equals(getSinchServiceInterface().getUserName())) {
                                        getSinchServiceInterface().stopClient();
                                    }

                                    if (!getSinchServiceInterface().isStarted()) {
                                        getSinchServiceInterface().startClient(phonenumber);
                                    } else {
                                        openPlaceCallActivity();
                                    }
                                }

                            }
                            else if ("2".equals(oldcard)){
                               *//* Intent two = new Intent(ScratchCardNew.this, app.sagar.telesevek.Status.StatusTwoActivity.class);
                                startActivity(two);*//*
                                Intent sendStuff = new Intent(ScratchCardNew.this, StatusTwoActivity.class);
                                sendStuff.putExtra("card", cardnumber);
                                startActivity(sendStuff);
                            }*/
                            else if("active".equals(oldcard)){
                                /*Intent Doctowillcallyou = new Intent(ScratchCardNew.this, app.sagar.telesevek.Status.StatusThreeActivity.class);
                                startActivity(Doctowillcallyou);*/

                                /*Intent sendStuff = new Intent(ScratchCardNew.this, AddPatiant.class);
                                sendStuff.putExtra("cardpass", cardnumber);
                                startActivity(sendStuff);*/

                                status();
                            }
                            else if("expired".equals(oldcard)){
                                Toast.makeText(ScratchCardNew.this, "your ScratchCard is expired", Toast.LENGTH_SHORT).show();
                            }
                            else {

                                  }





                        } else {
                            Log.d("tag", "onEvent: Document do not exists");
                            pd.dismiss();
                            Toast.makeText(ScratchCardNew.this, "Scratch card Not Valid  OR please check internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }

    /*@Override
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
    }

    private void openPlaceCallActivity() {
        Intent Doctowillcallyou = new Intent(ScratchCardNew.this, app.sagar.telesevek.Doctowillcallyou.class);
        startActivity(Doctowillcallyou);
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }
*/
    public void status(){
        DocumentReference get = fStore.collection("ScratchCard").document(cardnumber);
        get.addSnapshotListener(ScratchCardNew.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    Integer oldcardNEW = (Integer) documentSnapshot.getLong("RemainingConsultations").intValue();
                    if(0 < oldcardNEW){
                        Integer carde = oldcardNEW - 1;
                        Intent sendStuff = new Intent(ScratchCardNew.this, AddPatiant.class);
                        sendStuff.putExtra("cardpass", cardnumber);
                        sendStuff.putExtra("remainconsult", carde);
                        startActivity(sendStuff);
                    }
                    else {
                        Toast.makeText(ScratchCardNew.this, "your ScratchCard is expired", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.d("tag", "onEvent: Document do not exists");
                    pd.dismiss();
                }
            }
        });
    }
}