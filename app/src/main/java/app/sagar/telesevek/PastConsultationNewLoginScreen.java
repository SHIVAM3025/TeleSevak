package app.sagar.telesevek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import javax.annotation.Nullable;

import app.sagar.telesevek.PhoneAuthConsulation.MainActivity;
import app.sagar.telesevek.PhoneAuthConsulation.VerifyPhoneActivity;

public class PastConsultationNewLoginScreen extends AppCompatActivity {

    private EditText etNumber;
    private View btCard;
    private View btPhoneNumber;
    private Button btContinue;
    private FirebaseAuth firebaseAuth;
    private boolean isPhone=true;
    private String phoneNumber;
    private String cardNumber;
    private FirebaseFirestore fstore;
     private String id;
     boolean newCard;
     ProgressBar pb;
    String phoneNumberNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_consultation_new_login_screen);


        //disabling the auto detection of user

        /*SharedPreferences prefs = getSharedPreferences("past", MODE_PRIVATE);
        String dname = prefs.getString("passdata", null);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            //Starting the User Profile Activity if the user is already Logged in
            if(TextUtils.isEmpty(dname)){
            }
            else {
                if (Objects.equals(dname, "already")) {
                    startActivity(new Intent(getApplicationContext(), PastCounsulation.class));
                }
            }


        }*/
        pb=findViewById(R.id.progressbar);
        etNumber=findViewById(R.id.etEnterNumber);
        btCard=findViewById(R.id.relCard);
        btPhoneNumber=findViewById(R.id.relButtons);
        btContinue=findViewById(R.id.btnContinue);
        final TextView tvCard=findViewById(R.id.tvCard);
        final TextView tvPhone=findViewById(R.id.tvPhone);
        final ImageView ivPhone=findViewById(R.id.ibCall);
        final ImageView ivCard=findViewById(R.id.ivCard);


        btPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btPhoneNumber.setBackgroundResource(R.drawable.button_clr_shp);
                btCard.setBackgroundResource(R.drawable.faded_shape);
                tvCard.setTextColor(Color.parseColor("#D1CDCD"));
                ivCard.setColorFilter(getResources().getColor(R.color.whitish));
                tvPhone.setTextColor(Color.parseColor("#ffffff"));
                ivPhone.setColorFilter(getResources().getColor(R.color.white));
                isPhone=true;
                etNumber.setHint("फ़ोन नंबर भरें");
            }
        });

        btCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btCard.setBackgroundResource(R.drawable.color_shape_right);
                btPhoneNumber.setBackgroundResource(R.drawable.faded_shape_left);
                tvPhone.setTextColor(Color.parseColor("#D1CDCD"));
                ivPhone.setColorFilter(getResources().getColor(R.color.whitish));
                tvCard.setTextColor(Color.parseColor("#ffffff"));
                ivCard.setColorFilter(getResources().getColor(R.color.white));
                isPhone=false;
                etNumber.setHint("कार्ड नंबर भरें");

            }
        });

        fstore=FirebaseFirestore.getInstance();


        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isPhone) {
                    phoneNumber = etNumber.getText().toString().trim();

                    if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {
                        etNumber.setError("Valid number is required");
                        etNumber.requestFocus();
                        return;
                    }
                     phoneNumberNew = "+" + "91" + phoneNumber;
                    pb.setVisibility(View.VISIBLE);


                    fstore.collection("Patient").document(phoneNumber)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()) {
                                        Toast.makeText(getApplicationContext(),"Past consultation available",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), VerifyPhoneActivity.class);
                                        intent.putExtra("phonenumber", phoneNumberNew);
                                        startActivity(intent);
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),"New Number. No past Consultations",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), NoOldParamarsh.class);
                                        overridePendingTransition(0, 0);
                                        startActivity(intent);
                                    }

                                    pb.setVisibility(View.GONE);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Could not find number in database",Toast.LENGTH_SHORT).show();
                            pb.setVisibility(View.GONE);
                        }
                    });







                } else {
                    cardNumber = etNumber.getText().toString().trim();

                    if (cardNumber.isEmpty() || cardNumber.length() < 6 || cardNumber.length() > 8) {
                        etNumber.setError("Valid number is required");
                        etNumber.requestFocus();
                        return;
                    }

                    pb.setVisibility(View.VISIBLE);
                    fstore.collection("ScratchCard").document(cardNumber)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String s=documentSnapshot.getString("TotalConsultations");
                                    String t=documentSnapshot.get("RemainingConsultations").toString();

                                    if(s.equals(t)) {
                                        Toast.makeText(getApplicationContext(),"New Card. No past Consultations",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), NoOldParamarsh.class);
                                        overridePendingTransition(0, 0);
                                        startActivity(intent);
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),"Past consultation available",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), PastCounsulation.class);
                                        intent.putExtra("cardNumber", cardNumber);
                                        intent.putExtra("isScratchCard", true);
                                        startActivity(intent);
                                    }

                                    pb.setVisibility(View.GONE);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getApplicationContext(),"Could not get card in database",Toast.LENGTH_SHORT).show();
                            pb.setVisibility(View.GONE);
                        }
                    });


                    /*fstore.collection("Consultation").whereEqualTo("PatientCard",cardNumber)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(DocumentSnapshot docSnap:queryDocumentSnapshots){
                                if(docSnap.exists()){
                                    Toast.makeText(getApplicationContext(),"Card is present",Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(getApplicationContext(), "Could not find card in db", Toast.LENGTH_SHORT).show();
                                }
                            }
                            //Toast.makeText(getApplicationContext(),"Card is present",Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PastConsultationNewLoginScreen.this, "Could not find card in db", Toast.LENGTH_SHORT).show();
                        }
                    });*/




                  //  if(newCard){
                    //    Toast.makeText(getApplicationContext(), "Document not in Database", Toast.LENGTH_LONG).show();
                      //  Intent intent = new Intent(getApplicationContext(), NoOldParamarsh.class);
                        //overridePendingTransition(0, 0);
                        //startActivity(intent);}
                    /*else {
                        Intent intent = new Intent(getApplicationContext(), PastCounsulation.class);
                        intent.putExtra("cardNumber", cardNumber);
                        intent.putExtra("isScratchCard", true);
                        startActivity(intent);
                    }*/


                    /*fstore.collection("Consultation").whereEqualTo("PatientCard", cardNumber)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {


                                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                            if (document.exists()) {
                                                Intent intent = new Intent(getApplicationContext(), PastCounsulation.class);
                                                intent.putExtra("cardNumber", cardNumber);
                                                intent.putExtra("isScratchCard", true);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Document not in Database", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(getApplicationContext(), NoOldParamarsh.class);
                                                overridePendingTransition(0, 0);
                                                startActivity(intent);
                                            }
                                        }

                                            id= document.getId();

                                            DocumentReference documentReference=fstore.collection("Consultation").document(id);
                                            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if(documentSnapshot.exists()){
                                                        Intent intent=new Intent(getApplicationContext(),PastCounsulation.class);
                                                        intent.putExtra("cardNumber",cardNumber);
                                                        intent.putExtra("isScratchCard",true);
                                                        startActivity(intent);
                                                    }
                                                    String remainConsult=(String)documentSnapshot.get("RemainingConsultations");
                                                    String totalConsult=documentSnapshot.getString("TotalConsultations");

                                                    assert remainConsult != null;
                                                    if(remainConsult.equals(totalConsult)){
                                                        //means new scratch card
                                                        Intent intent=new Intent(getApplicationContext(),NoOldParamarsh.class);
                                                        overridePendingTransition(0,0);
                                                        startActivity(intent);

                                                    }
                                                    else{
                                                        Intent intent=new Intent(getApplicationContext(),PastCounsulation.class);
                                                        intent.putExtra("cardNumber",cardNumber);
                                                        intent.putExtra("isScratchCard",true);
                                                        startActivity(intent);
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    Toast.makeText(getApplicationContext(),"Document not in Database",Toast.LENGTH_LONG).show();
                                                    Intent intent=new Intent(getApplicationContext(),NoOldParamarsh.class);
                                                    overridePendingTransition(0,0);
                                                    startActivity(intent);
                                                }
                                            });
                                    } else {

                                        Toast.makeText(PastConsultationNewLoginScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), NoOldParamarsh.class);
                                        overridePendingTransition(0, 0);
                                        startActivity(intent);


                                    }
                                }


                            });*/


                            /*if(inConsultation){
                                Intent intent=new Intent(getApplicationContext(),PastCounsulation.class);
                                intent.putExtra("cardNumber",cardNumber);
                                intent.putExtra("isScratchCard",true);
                                startActivity(intent);
                            }else {
                                Intent intent=new Intent(getApplicationContext(),NoOldParamarsh.class);
                                overridePendingTransition(0,0);
                                startActivity(intent);
                            }*/

                }
            }
        });


        BottomNavigationView bottomNav=findViewById(R.id.bottomNavPastLoginNew);
        bottomNav.setSelectedItemId(R.id.pastConsult);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.pastConsult:
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
                        startActivity(new Intent(getApplicationContext(),ScratchCardNew.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent=new Intent(getApplicationContext(),ScratchCardNew.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(0,0);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void second(){
        fstore.collection("Consultation").whereEqualTo("PatientCard", cardNumber)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {


                            for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                if (document.exists()) {
                                    Intent intent = new Intent(getApplicationContext(), PastCounsulation.class);
                                    intent.putExtra("cardNumber", cardNumber);
                                    intent.putExtra("isScratchCard", true);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Document not in Database", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), NoOldParamarsh.class);
                                    overridePendingTransition(0, 0);
                                    startActivity(intent);
                                }
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Document not in Database", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), NoOldParamarsh.class);
                            overridePendingTransition(0, 0);
                            startActivity(intent);
                        }
                    }
                }
                );
    }
}