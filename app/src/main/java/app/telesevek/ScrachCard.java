package app.telesevek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import app.telesevek.PhoneAuthConsulation.MainActivity;

public class ScrachCard extends AppCompatActivity {
    String card1;
    String card2;
    String card3;
    FirebaseFirestore fStore;
    EditText scrached;
    Button submit;

    Button past;
    Button buy;
    Button ourdoctor;
    Button viewbill;
    Button followup;

    String date;
    String expiredatecheck;
    String expiredatecheck1;
    String expiredatecheck2;
    String expiredatecheck3;
    String expiredatecheck4;
    String expiredatecheck5;
    String expiredatecheck6;
    String expirecardcheck;

    String Strname;
    String Strdate;
    String Strphone;
    String DoctorId;
    String ConsultationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrach_card);

        SharedPreferences prefs = getSharedPreferences("Image", MODE_PRIVATE);
        final String phonenumber = prefs.getString("pimageid", null);






            long yourDateMillis = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000);
            Time yourDate = new Time();
            yourDate.set(yourDateMillis);
            final String finalexpiredate = yourDate.format("%Y-%m-%d");

            //first expiredate
            long yourDateMillis1 = System.currentTimeMillis() + (1 * 24 * 60 * 60 * 1000);
            Time yourDate1 = new Time();
            yourDate1.set(yourDateMillis1);
            final String finalexpiredate1 = yourDate1.format("%Y-%m-%d");

            //second expiredate2
            long yourDateMillis2 = System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000);
            Time yourDate2 = new Time();
            yourDate2.set(yourDateMillis2);
            final String finalexpiredate2 = yourDate2.format("%Y-%m-%d");

            //third expiredate3
            long yourDateMillis3 = System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000);
            Time yourDate3 = new Time();
            yourDate3.set(yourDateMillis3);
            final String finalexpiredate3 = yourDate3.format("%Y-%m-%d");

            //four expiredate4
            long yourDateMillis4 = System.currentTimeMillis() + (4 * 24 * 60 * 60 * 1000);
            Time yourDate4 = new Time();
            yourDate4.set(yourDateMillis4);
            final String finalexpiredate4 = yourDate4.format("%Y-%m-%d");

            //five expiredate5
            long yourDateMillis5 = System.currentTimeMillis() + (5 * 24 * 60 * 60 * 1000);
            Time yourDate5 = new Time();
            yourDate5.set(yourDateMillis5);
            final String finalexpiredate5 = yourDate5.format("%Y-%m-%d");

            //six expiredate6
            long yourDateMillis6 = System.currentTimeMillis() + (6 * 24 * 60 * 60 * 1000);
            Time yourDate6 = new Time();
            yourDate6.set(yourDateMillis6);
            final String finalexpiredate6 = yourDate6.format("%Y-%m-%d");

            date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        /*Date dNow = new Date(System.currentTimeMillis()+5*60*1000);
        SimpleDateFormat ft = new SimpleDateFormat ("MMM d, yyyy k:mm:ss");
        System.out.println(ft.format(dNow));
        Toast.makeText(this, ""+calendar.getTime(), Toast.LENGTH_SHORT).show();
*/


            scrached = findViewById(R.id.etScratch);
            submit = findViewById(R.id.submit);

            viewbill = findViewById(R.id.viewbill);
            followup = findViewById(R.id.followup);

        /*SharedPreferences prefs = getSharedPreferences("Image", MODE_PRIVATE);
        String phonenumber = prefs.getString("pimageid", null);*/


            followup.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            DocumentReference documentReference2 = fStore.collection("Consultation").document(phonenumber);
                            documentReference2.addSnapshotListener(ScrachCard.this, new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                    if (documentSnapshot.exists()) {
                                        Strname = documentSnapshot.getString("PName");
                                        Strdate = documentSnapshot.getString("DateTime");
                                        Strphone = documentSnapshot.getString("PatientId");
                                        DoctorId = documentSnapshot.getString("DoctorId");
                                        ConsultationId = documentSnapshot.getString("ConsultationId");


                                    } else {
                                        Log.d("tag", "onEvent: Document do not exists");
                                    }
                                }
                            });

                            date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault()).format(new Date());
                            DocumentReference documentReference = fStore.collection("Followup").document(Strphone);
                            Map<String,Object> user2 = new HashMap<>();
                            user2.put("ConsultationId",ConsultationId);
                            user2.put("PatientId",Strphone);
                            user2.put("PName",Strname);
                            user2.put("Prescription Link in FileStore","");
                            user2.put("DoctorId",DoctorId);
                            user2.put("Block","none");
                            user2.put("DateTime",date);
                            documentReference.set(user2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                        /*Intent sendStuff = new Intent(DoctorSide.this, ActivityCallDoctortoP.class);
                        sendStuff.putExtra("PatientPassId", pPhoneNumber);
                        startActivity(sendStuff);*/

                                    Toast.makeText(ScrachCard.this, "FollowUP Requested", Toast.LENGTH_SHORT).show();
                                    DocumentReference pushnotify = fStore.collection("FollowUPNotification").document("PatientRequestNotification");
                                    Map<String,Object> usernotify = new HashMap<>();
                                    usernotify.put("Notification","True");
                                    pushnotify.set(usernotify).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

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
                                }
                            });

                        }
                    });



                 SharedPreferences expirecheking = getSharedPreferences("Expire", MODE_PRIVATE);
            expiredatecheck = expirecheking.getString("scrachdate", null);
            expiredatecheck1 = expirecheking.getString("scrachdate1", null);
            expiredatecheck2 = expirecheking.getString("scrachdate2", null);
            expiredatecheck3 = expirecheking.getString("scrachdate3", null);
            expiredatecheck4 = expirecheking.getString("scrachdate4", null);
            expiredatecheck5 = expirecheking.getString("scrachdate5", null);
            expiredatecheck6 = expirecheking.getString("scrachdate6", null);
            expirecardcheck = expirecheking.getString("scrachname", null);


            if (date.equals(date) || date.equals(expiredatecheck) || date.equals(expiredatecheck1) || date.equals(expiredatecheck2)
                    || date.equals(expiredatecheck3) || date.equals(expiredatecheck4) || date.equals(expiredatecheck5)
                    || date.equals(expiredatecheck6)) {
                viewbill.setVisibility(View.GONE);
                followup.setVisibility(View.GONE);
            } else {
                viewbill.setVisibility(View.VISIBLE);
                followup.setVisibility(View.VISIBLE);
            }
/*
        if (phonenumber == null){
            viewbill.setVisibility(View.GONE);
            viewbillpast.setVisibility(View.GONE);
        }
        else {
            viewbill.setVisibility(View.VISIBLE);
            viewbillpast.setVisibility(View.VISIBLE);
        }*/

            fStore = FirebaseFirestore.getInstance();
            DocumentReference documentReference = fStore.collection("Scratch").document("cardnumber");
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {

                        card1 = documentSnapshot.getString("card1");
                        card2 = documentSnapshot.getString("card2");
                        card3 = documentSnapshot.getString("card3");

                    } else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });

            past = findViewById(R.id.Past);
            past.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent chemistinten = new Intent(ScrachCard.this, MainActivity.class);
                    startActivity(chemistinten);
                }
            });


            buy = findViewById(R.id.card);
            buy.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent chemistinten = new Intent(ScrachCard.this, Buycard.class);
                            startActivity(chemistinten);
                        }
                    });

            ourdoctor = findViewById(R.id.odoctor);
            ourdoctor.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent chemistinten = new Intent(ScrachCard.this, OurDoctor.class);
                            startActivity(chemistinten);
                        }
                    });


            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (date.equals(date) || date.equals(expiredatecheck) || date.equals(expiredatecheck1) || date.equals(expiredatecheck2)
                            || date.equals(expiredatecheck3) || date.equals(expiredatecheck4) || date.equals(expiredatecheck5)
                            || date.equals(expiredatecheck6)) {

                        viewbill.setVisibility(View.GONE);
                        followup.setVisibility(View.GONE);

                        if (expirecardcheck != null) {

                            if (expirecardcheck.equals(scrached.getText().toString())) {
                                Toast.makeText(ScrachCard.this, "doctor call you shortly", Toast.LENGTH_SHORT).show();
                            } else {
                                if (scrached.getText().toString().equals(card1)) {
                                    Toast.makeText(ScrachCard.this, "Scratch card Accepted", Toast.LENGTH_SHORT).show();
                                    Intent chemistinten = new Intent(ScrachCard.this, AddPatiant.class);
                                    startActivity(chemistinten);

                                    SharedPreferences.Editor doctorsave = getSharedPreferences("Expire", MODE_PRIVATE).edit();
                                    doctorsave.putString("scrachdate", finalexpiredate);
                                    doctorsave.putString("scrachdate1", finalexpiredate1);
                                    doctorsave.putString("scrachdate2", finalexpiredate2);
                                    doctorsave.putString("scrachdate3", finalexpiredate3);
                                    doctorsave.putString("scrachdate4", finalexpiredate4);
                                    doctorsave.putString("scrachdate5", finalexpiredate5);
                                    doctorsave.putString("scrachdate6", finalexpiredate6);
                                    doctorsave.commit();

                                    Intent sendStuff = new Intent(ScrachCard.this, AddPatiant.class);
                                    sendStuff.putExtra("cardpass", card1);
                                    startActivity(sendStuff);
                                } else if (scrached.getText().toString().equals(card2)) {
                                    Toast.makeText(ScrachCard.this, "Scratch card Accepted", Toast.LENGTH_SHORT).show();
                                    Intent chemistinten = new Intent(ScrachCard.this, AddPatiant.class);
                                    startActivity(chemistinten);
                                    SharedPreferences.Editor doctorsave = getSharedPreferences("Expire", MODE_PRIVATE).edit();
                                    doctorsave.putString("scrachdate", finalexpiredate);
                                    doctorsave.putString("scrachdate1", finalexpiredate1);
                                    doctorsave.putString("scrachdate2", finalexpiredate2);
                                    doctorsave.putString("scrachdate3", finalexpiredate3);
                                    doctorsave.putString("scrachdate4", finalexpiredate4);
                                    doctorsave.putString("scrachdate5", finalexpiredate5);
                                    doctorsave.putString("scrachdate6", finalexpiredate6);
                                    doctorsave.commit();
                                    Intent sendStuff = new Intent(ScrachCard.this, AddPatiant.class);
                                    sendStuff.putExtra("cardpass", card2);
                                    startActivity(sendStuff);
                                } else if (scrached.getText().toString().equals(card3)) {
                                    Toast.makeText(ScrachCard.this, "Scratch card Accepted", Toast.LENGTH_SHORT).show();
                                    Intent chemistinten = new Intent(ScrachCard.this, AddPatiant.class);
                                    startActivity(chemistinten);
                                    SharedPreferences.Editor doctorsave = getSharedPreferences("Expire", MODE_PRIVATE).edit();
                                    doctorsave.putString("scrachdate", finalexpiredate);
                                    doctorsave.putString("scrachdate1", finalexpiredate1);
                                    doctorsave.putString("scrachdate2", finalexpiredate2);
                                    doctorsave.putString("scrachdate3", finalexpiredate3);
                                    doctorsave.putString("scrachdate4", finalexpiredate4);
                                    doctorsave.putString("scrachdate5", finalexpiredate5);
                                    doctorsave.putString("scrachdate6", finalexpiredate6);
                                    doctorsave.commit();

                                    Intent sendStuff = new Intent(ScrachCard.this, AddPatiant.class);
                                    sendStuff.putExtra("cardpass", card3);
                                    startActivity(sendStuff);
                                } else {
                                    Toast.makeText(ScrachCard.this, "Scratch card not Accepted", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } else {
                            if (scrached.getText().toString().equals(card1)) {
                                Toast.makeText(ScrachCard.this, "Scratch card Accepted", Toast.LENGTH_SHORT).show();
                                Intent chemistinten = new Intent(ScrachCard.this, AddPatiant.class);
                                startActivity(chemistinten);

                                SharedPreferences.Editor doctorsave = getSharedPreferences("Expire", MODE_PRIVATE).edit();
                                doctorsave.putString("scrachdate", finalexpiredate);
                                doctorsave.putString("scrachdate1", finalexpiredate1);
                                doctorsave.putString("scrachdate2", finalexpiredate2);
                                doctorsave.putString("scrachdate3", finalexpiredate3);
                                doctorsave.putString("scrachdate4", finalexpiredate4);
                                doctorsave.putString("scrachdate5", finalexpiredate5);
                                doctorsave.putString("scrachdate6", finalexpiredate6);
                                doctorsave.commit();

                                Intent sendStuff = new Intent(ScrachCard.this, AddPatiant.class);
                                sendStuff.putExtra("cardpass", card1);
                                startActivity(sendStuff);
                            } else if (scrached.getText().toString().equals(card2)) {
                                Toast.makeText(ScrachCard.this, "Scratch card Accepted", Toast.LENGTH_SHORT).show();
                                Intent chemistinten = new Intent(ScrachCard.this, AddPatiant.class);
                                startActivity(chemistinten);
                                SharedPreferences.Editor doctorsave = getSharedPreferences("Expire", MODE_PRIVATE).edit();
                                doctorsave.putString("scrachdate", finalexpiredate);
                                doctorsave.putString("scrachdate1", finalexpiredate1);
                                doctorsave.putString("scrachdate2", finalexpiredate2);
                                doctorsave.putString("scrachdate3", finalexpiredate3);
                                doctorsave.putString("scrachdate4", finalexpiredate4);
                                doctorsave.putString("scrachdate5", finalexpiredate5);
                                doctorsave.putString("scrachdate6", finalexpiredate6);
                                doctorsave.commit();
                                Intent sendStuff = new Intent(ScrachCard.this, AddPatiant.class);
                                sendStuff.putExtra("cardpass", card2);
                                startActivity(sendStuff);
                            } else if (scrached.getText().toString().equals(card3)) {
                                Toast.makeText(ScrachCard.this, "Scratch card Accepted", Toast.LENGTH_SHORT).show();
                                Intent chemistinten = new Intent(ScrachCard.this, AddPatiant.class);
                                startActivity(chemistinten);
                                SharedPreferences.Editor doctorsave = getSharedPreferences("Expire", MODE_PRIVATE).edit();
                                doctorsave.putString("scrachdate", finalexpiredate);
                                doctorsave.putString("scrachdate1", finalexpiredate1);
                                doctorsave.putString("scrachdate2", finalexpiredate2);
                                doctorsave.putString("scrachdate3", finalexpiredate3);
                                doctorsave.putString("scrachdate4", finalexpiredate4);
                                doctorsave.putString("scrachdate5", finalexpiredate5);
                                doctorsave.putString("scrachdate6", finalexpiredate6);
                                doctorsave.commit();

                                Intent sendStuff = new Intent(ScrachCard.this, AddPatiant.class);
                                sendStuff.putExtra("cardpass", card3);
                                startActivity(sendStuff);
                            } else {
                                Toast.makeText(ScrachCard.this, "Scratch card not Accepted", Toast.LENGTH_SHORT).show();
                            }

                        }

                    } else {
                        Toast.makeText(ScrachCard.this, "your card code be already used" + expirecardcheck, Toast.LENGTH_SHORT).show();
                        viewbill.setVisibility(View.VISIBLE);
                        followup.setVisibility(View.VISIBLE);

                    }


                }
            });


        }
    }
