package app.sagar.telesevek;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import app.sagar.telesevek.uploadpkg.UploadImage;

public class DoctorCallActivityFollow extends AppCompatActivity {
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
    FirebaseFirestore fStore;
    Button audio;
    Button video;
    Button sendscan;

    ImageView uploadPicIV;

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
                ststusup.update("Status","4");
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
                consult.update("Status","4")
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
                consultitem.update("Status","4")
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

                new AlertDialog.Builder(DoctorCallActivityFollow.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Telesevek App under Developmemt")
                        .setMessage("App is Under Construction")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setNegativeButton("close", null).show();


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