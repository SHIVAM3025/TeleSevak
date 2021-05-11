package app.telesevek.NewSceen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nullable;

import app.telesevek.OurDoctor;
import app.telesevek.PastConsultationNewLoginScreen;
import app.telesevek.R;
import app.telesevek.ScratchCardNew;
import app.telesevek.SpecialDoctors;
import app.telesevek.uploadpkg.ShowImageActivity;

public class HomePatient extends AppCompatActivity {
    Button bt_online,bt_scratch;
    Button parcha;
    TextView doctorwillcall;
    RelativeLayout doctorcall,drcall;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_patient);
        bt_online = findViewById(R.id.bt_online);
        bt_scratch = findViewById(R.id.helth);

        fStore = FirebaseFirestore.getInstance();
        parcha = findViewById(R.id.gopre);

        doctorcall = findViewById(R.id.call);
        doctorwillcall = findViewById(R.id.drnametitile);
        drcall = findViewById(R.id.fsubmit);

        bt_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i_o =new Intent(HomePatient.this, SpecialDoctors.class);
                startActivity(i_o);
            }
        });

        bt_scratch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i_o =new Intent(HomePatient.this, ScratchCardNew.class);
                startActivity(i_o);
            }
        });

        BottomNavigationView bottomNav=findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.consultDoctor);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.pastConsult:
                        startActivity(new Intent(getApplicationContext(), PastConsultationNewLoginScreen.class));
                        overridePendingTransition(0,0);
                        return true;

                   /* case R.id.buyCard:
                        startActivity(new Intent(getApplicationContext(),Buycard.class));
                        overridePendingTransition(0,0);
                        return true;*/

                    case R.id.ourDoctors:
                        startActivity(new Intent(getApplicationContext(), OurDoctor.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.consultDoctor:
                        return true;
                }
                return false;
            }
        });

        SharedPreferences prefs = getSharedPreferences("Consultpre", MODE_PRIVATE);
        String cid = prefs.getString("cid", "123");
        if (cid != null){
            DocumentReference documentReference2 = fStore.collection("Consultation").document(cid);
            documentReference2.addSnapshotListener(HomePatient.this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot.exists()){
                        String Did = documentSnapshot.getString("DoctorId");
                        String CurrentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        String FinalDate = documentSnapshot.getString("urludate");
                        String doctorid = documentSnapshot.getString("DoctorId");
                        String doctorname = documentSnapshot.getString("DoctorName");
                        String Stautus = documentSnapshot.getString("Status");
                        String typeofcon = documentSnapshot.getString("TypeOfConsultation");
                        final String scratcard = documentSnapshot.getString("PatientCard");

                        if (typeofcon.equals("Primary")){
                            drcall.setVisibility(View.VISIBLE);
                            doctorcall.setVisibility(View.GONE);
                            parcha.setVisibility(View.GONE);
                        }

                        if(doctorname.isEmpty()) {

                        }
                        else {

                            drcall.setVisibility(View.GONE);
                            doctorcall.setVisibility(View.VISIBLE);
                            parcha.setVisibility(View.GONE);
                            doctorwillcall.setText(doctorname + " आपको थोड़ी देर में सम्पर्क करेंगे");




                        }


                        if(Stautus.equals("Completed")) {
                            doctorcall.setVisibility(View.GONE);
                        }


                        parcha.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DocumentReference get = fStore.collection("ScratchCard").document(scratcard);
                                get.addSnapshotListener(HomePatient.this, new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                        if (documentSnapshot.exists()) {
                                            Integer oldcardNEW = (Integer) documentSnapshot.getLong("RemainingConsultations").intValue();
                                            if(0 < oldcardNEW){
                                                Integer carde = oldcardNEW - 1;
                                                Integer carde2 = oldcardNEW;
                                                Intent sendStuff = new Intent(HomePatient.this, ShowImageActivity.class);
                                                sendStuff.putExtra("cardpass", scratcard);
                                                sendStuff.putExtra("remainconsult", carde);
                                                sendStuff.putExtra("DocuId", cid);
                                                startActivity(sendStuff);
                                            }
                                            else {
                                                Intent sendStuff = new Intent(HomePatient.this, ShowImageActivity.class);
                                                sendStuff.putExtra("cardpass", scratcard);
                                                sendStuff.putExtra("remainconsult", "0");
                                                sendStuff.putExtra("DocuId", cid);
                                                startActivity(sendStuff);
                                            }

                                        } else {
                                            Log.d("tag", "onEvent: Document do not exists");
                                        }
                                    }
                                });
                            }
                        });

                        if(FinalDate.equals("null")) { }
                        else {
                            Date date1 = null;
                            Date date2 = null;
                            SimpleDateFormat dates = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                date1 = dates.parse(CurrentDate);
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                            try {
                                date2 = dates.parse(FinalDate);
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                            long difference = Math.abs(date1.getTime() - date2.getTime());
                            long differenceDates = difference / (24 * 60 * 60 * 1000);
                            String dayDifference = Long.toString(differenceDates);

                            if ("0".equals(dayDifference)){
                                parcha.setVisibility(View.VISIBLE);
                                doctorcall.setVisibility(View.GONE);
                                drcall.setVisibility(View.GONE);





                            }
                        }





                    }else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });
        }

    }
}