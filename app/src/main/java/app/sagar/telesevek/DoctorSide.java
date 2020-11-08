package app.sagar.telesevek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

public class DoctorSide extends AppCompatActivity {
    TextView fullname,symtom;
    FirebaseFirestore fStore;
    String notificationtrue;
    String ChemistId;
    String ChemistName;
    String ChemistPhoneNumber;
    String pName;
    String pPhoneNumber;
    String pSymptoms;
    String PDate;
    String Dname;
    String Did;
    String DNumber;
    String date;
    String date2;
    String date3;

    String Strfollowname;
    String Strdatefollow;


    TextView followname;
    TextView followdate;

    StorageReference storageReference;
    TextView accept;
    TextView decline;
    TextView Textdate;
    RelativeLayout LAYOUT;
    Button past;
    Thread t;
    String oldpString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_side);

        fullname = findViewById(R.id.D_degree);
        Textdate = findViewById(R.id.date);
        symtom = findViewById(R.id.D_address);
        accept= findViewById(R.id.call);
        decline= findViewById(R.id.reject);
        LAYOUT= findViewById(R.id.box);

        followname = findViewById(R.id.followername);
        followdate = findViewById(R.id.dateFOLLOW);

        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


        if(date.equals(date) || date.equals(date2) || date.equals(date3)){

        }
        else {
            fullname.setText("Expire Request");
            symtom.setText("no data");
            Textdate.setText(date);
            accept.setEnabled(false);
        }


        past = findViewById(R.id.past);
        past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent chemistinten = new Intent(DoctorSide.this, DoctorSideFollowupConsulation.class);
                startActivity(chemistinten);
            }
        });




        final SharedPreferences oldp = getSharedPreferences("OLD", MODE_PRIVATE);
        oldpString = oldp.getString("c", null);










        SharedPreferences prefs = getSharedPreferences("User", MODE_PRIVATE);
        String phonenumber = prefs.getString("phone", null);

        SharedPreferences prefs2 = getSharedPreferences("PatientPhoneNumber", MODE_PRIVATE);
        String PatienNumber = prefs2.getString("PatientNumber", null);





       fStore = FirebaseFirestore.getInstance( );
        storageReference = FirebaseStorage.getInstance().getReference();
        DocumentReference documentReference = fStore.collection("Patient").document("PatientRequest");
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    fullname.setText(documentSnapshot.getString("Name"));
                    symtom.setText(documentSnapshot.getString("Symptoms"));

                   /* ChemistId = documentSnapshot.getString("ChemistId");
                    ChemistName = documentSnapshot.getString("ChemistName");
                    ChemistPhoneNumber = documentSnapshot.getString("ChemistPhoneNumber");*/
                    pName = documentSnapshot.getString("Name");
                    pPhoneNumber = documentSnapshot.getString("PhoneNumber");
                    pSymptoms = documentSnapshot.getString("Symptoms");
                    Textdate.setText(documentSnapshot.getString("DateTime"));
                    date2 = documentSnapshot.getString("DateTime2");
                    date3 = documentSnapshot.getString("DateTime3");

                    if (null == oldpString)
                    {

                    }
                    else {
                       if(oldpString.equals(pPhoneNumber)) {
                           fullname.setText("Request Rejected");
                           symtom.setText("Request Rejected");
                           Textdate.setText("Request Rejected");

                           accept.setEnabled(false);

                           Toast.makeText(DoctorSide.this, "rejected", Toast.LENGTH_SHORT).show();
                       }
                    }






                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SharedPreferences.Editor doctorsave = getSharedPreferences("OLD", MODE_PRIVATE).edit();
                doctorsave.putString("c",pPhoneNumber);
                doctorsave.commit();


                Intent chemistinten = new Intent(DoctorSide.this, DoctorSide.class);
                startActivity(chemistinten);
            }
        });






        if (phonenumber != null){
            DocumentReference documentReference2 = fStore.collection("Doctor").document(phonenumber);
            documentReference2.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot.exists()){
                        Did = documentSnapshot.getString("DoctorId");
                        Dname = documentSnapshot.getString("Name");
                        DNumber = documentSnapshot.getString("PhoneNumber");

                    }else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });
        }

        if (pPhoneNumber != null) {
            DocumentReference documentReferencefollow = fStore.collection("Followup").document(pPhoneNumber);
            documentReferencefollow.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        Strfollowname = documentSnapshot.getString("PName");
                        Strdatefollow = documentSnapshot.getString("DateTime");


                        followname.setText(Strfollowname);
                        followdate.setText(Strdatefollow);

                    } else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });

        }


        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DoctorSide.this, "Accept.", Toast.LENGTH_SHORT).show();
                DocumentReference documentReference = fStore.collection("Accept").document(pPhoneNumber);
                Map<String,Object> user = new HashMap<>();
                user.put("PatientName",pName);
                user.put("PatientPhoneNumber",pPhoneNumber);
                user.put("PatientSymptoms",pSymptoms);
               /* user.put("ChemistId",ChemistId);
                user.put("ChemistName",ChemistName);
                user.put("ChemistPhoneNumber",ChemistPhoneNumber);*/
                user.put("FullAddress",Did);
                user.put("DoctorName",Dname);
                user.put("DoctorPhoneNumber",DNumber);
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent sendStuff = new Intent(DoctorSide.this, ActivityCallDoctortoP.class);
                        sendStuff.putExtra("PatientPassId", pPhoneNumber);
                        startActivity(sendStuff);



                        t.interrupt();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

                date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault()).format(new Date());
                DocumentReference documentReference2 = fStore.collection("Consultation").document(pPhoneNumber);
                Map<String,Object> user2 = new HashMap<>();
                user2.put("ConsultationId",pPhoneNumber);
                user2.put("PatientId",pPhoneNumber);
                user2.put("PName",pName);
                user2.put("Prescription Link in FileStore","");
               /* user.put("ChemistId",ChemistId);
                user.put("ChemistName",ChemistName);
                user.put("ChemistPhoneNumber",ChemistPhoneNumber);*/
                user2.put("DoctorId",Did);
                user2.put("Block","none");
                user2.put("DateTime",date);
                documentReference2.set(user2).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        /*Intent sendStuff = new Intent(DoctorSide.this, ActivityCallDoctortoP.class);
                        sendStuff.putExtra("PatientPassId", pPhoneNumber);
                        startActivity(sendStuff);*/

                        t.interrupt();
                        SharedPreferences.Editor image = getSharedPreferences("PNumber", MODE_PRIVATE).edit();
                        image.putString("no", pPhoneNumber);
                        image.commit();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

            }
        });




         t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                check();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

    }

    private void check(){
        DocumentReference usernotify = fStore.collection("Notification").document("PatientRequestNotification");
        usernotify.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    notificationtrue = documentSnapshot.getString("Notification");
                    if ("True".equals(notificationtrue)){
                        addNotification();
                        DocumentReference push = fStore.collection("Notification").document("PatientRequestNotification");
                        push.delete();
                    }

                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });


        DocumentReference usernotify2 = fStore.collection("FollowUPNotification").document("PatientRequestNotification");
        usernotify2.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    notificationtrue = documentSnapshot.getString("Notification");
                    if ("True".equals(notificationtrue)){
                        addNotification2();
                        DocumentReference push = fStore.collection("FollowUPNotification").document("PatientRequestNotification");
                        push.delete();
                    }

                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });
    }


    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher) //set icon for notification
                        .setContentTitle("New Patient") //set title of notification
                        .setContentText("Patient name is"+fullname)//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    private void addNotification2() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher) //set icon for notification
                        .setContentTitle("New Followup") //set title of notification
                        .setContentText("Patient name is"+Strfollowname)//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }



    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        t.interrupt();

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                        System.exit(0);
                    }
                }).setNegativeButton("no", null).show();
    }
}