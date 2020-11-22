package app.sagar.telesevek;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import app.sagar.telesevek.Notify.ForegroundService;

public class YesterdayPatient extends AppCompatActivity {
    FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    ProgressBar progressBar;
    RecyclerView friendList;
    TextView accept;
    String oldpString;
    TextView fullname,symtom;
    String Name;
    FirebaseFirestore fStore;
    String notificationtrue;
    TextView Textdate;
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

    Button past;
    Button Followup;

    Button oneday;
    Button twoday;

    String Strfollowname;
    String Strdatefollow;


    TextView followname;
    TextView followdate;

    StorageReference storageReference;
    TextView decline;
    RelativeLayout LAYOUT;
    Thread t;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_side_new);

        startService();

        SharedPreferences prefsfollow = getSharedPreferences("PNumber", MODE_PRIVATE);
        String follownumber = prefsfollow.getString("no", null);

        followname = findViewById(R.id.followername);
        followdate = findViewById(R.id.dateFOLLOW);
        friendList = findViewById(R.id.friend_list);
        progressBar = findViewById(R.id.progress_bar);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        friendList.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        pd = new ProgressDialog(YesterdayPatient.this);
        pd.setMessage("loading..");


        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        //bottomNav

        BottomNavigationView bottomNavDoctor=findViewById(R.id.bottomNavigationView);
        bottomNavDoctor.setSelectedItemId(R.id.current_menu);

        bottomNavDoctor.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.viewPast_menu:
                        startActivity(new Intent(getApplicationContext(),DoctorSidePastConsulation.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.followUp_menu:
                        startActivity(new Intent(getApplicationContext(),DoctorSideFollowupConsulation.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.current_menu:
                        return true;
                }
                return false;
            }
        });

        //upNav
        BottomNavigationView upNav=findViewById(R.id.bottomNavigationView2);
        upNav.setSelectedItemId(R.id.oneDayAgo_UpDoctor_menu);

        upNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.oneDayAgo_UpDoctor_menu:
                        return true;

                    case R.id.twoDaysAgo_UpDoctor_menu:
                        startActivity(new Intent(getApplicationContext(),TommarowPatient.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.todayUpDoctor_menu:
                        startActivity(new Intent(getApplicationContext(),DoctorSideNew.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });


        /*
        past = findViewById(R.id.past);
        past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent chemistinten = new Intent(YesterdayPatient.this, DoctorSidePastConsulation.class);
                startActivity(chemistinten);
            }
        });

        Followup = findViewById(R.id.followup);
        Followup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent chemistinten = new Intent(YesterdayPatient.this, DoctorSideFollowupConsulation.class);
                startActivity(chemistinten);
            }
        });

        oneday = findViewById(R.id.today);
        oneday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent chemistinten = new Intent(YesterdayPatient.this, DoctorSideNew.class);
                startActivity(chemistinten);
            }
        });

        twoday = findViewById(R.id.tomorrow);
        twoday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent chemistinten = new Intent(YesterdayPatient.this, TommarowPatient.class);
                startActivity(chemistinten);
            }
        });
        */

        SharedPreferences prefs = getSharedPreferences("User", MODE_PRIVATE);
        final String phonenumber = prefs.getString("phone", null);

        SharedPreferences prefs2 = getSharedPreferences("PatientPhoneNumber", MODE_PRIVATE);
        String PatienNumber = prefs2.getString("PatientNumber", null);


        fStore = FirebaseFirestore.getInstance();


        if (pPhoneNumber != null) {
            DocumentReference documentReferencefollow = fStore.collection("Followup").document(follownumber);
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


        t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               /* check();*/
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();


        String datetime = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        fStore = FirebaseFirestore.getInstance();
        Query query = fStore.collection("Patient").whereEqualTo("Status","1").whereEqualTo("DateTime2",datetime);



        FirestoreRecyclerOptions<PatientResponse> response = new FirestoreRecyclerOptions.Builder<PatientResponse>()
                .setQuery(query, PatientResponse.class)
                .build();





        if (phonenumber != null){
            DocumentReference documentReference2 = fStore.collection("Doctor").document(phonenumber);
            documentReference2.addSnapshotListener(YesterdayPatient.this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot.exists()){
                        Did = documentSnapshot.getString("DoctorId");
                        Dname = documentSnapshot.getString("Name");
                        DNumber = documentSnapshot.getString("PhoneNumber");

                        SharedPreferences.Editor doctorsave = getSharedPreferences("doctornamevideocall", MODE_PRIVATE).edit();
                        doctorsave.putString("dnamecall",DNumber);
                        doctorsave.commit();



                    }else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });
        }

        adapter = new FirestoreRecyclerAdapter<PatientResponse, FriendsHolder>(response) {
            @Override
            public void onBindViewHolder(final FriendsHolder holder, int position, final PatientResponse model) {
                progressBar.setVisibility(View.GONE);
                holder.textName.setText(model.getName());
                holder.textTitle.setText(model.getSymptoms());
                holder.textCompany.setText(model.getDateTime());
                /*Glide.with(getApplicationContext())
                        .load(model.getImage())
                        .into(holder.imageView);*/


                holder.reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        holder.itemView.setVisibility(View.INVISIBLE);
                    }
                });


                if (phonenumber != null){
                    DocumentReference documentReference2 = fStore.collection("Doctor").document(phonenumber);
                    documentReference2.addSnapshotListener(YesterdayPatient.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if(documentSnapshot.exists()){
                                Did = documentSnapshot.getString("DoctorId");
                                Dname = documentSnapshot.getString("Name");
                                DNumber = documentSnapshot.getString("PhoneNumber");

                                SharedPreferences.Editor doctorsave = getSharedPreferences("doctornamevideocall", MODE_PRIVATE).edit();
                                doctorsave.putString("dnamecall",DNumber);
                                doctorsave.commit();



                            }else {
                                Log.d("tag", "onEvent: Document do not exists");
                            }
                        }
                    });
                }


                holder.call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pd.show();
                        final String id = fStore.collection("Consultation").document().getId();
                       /* DocumentReference documentReference = fStore.collection("Accept").document(id);
                        Map<String,Object> user = new HashMap<>();
                        user.put("PatientName",model.getName());
                        user.put("PatientPhoneNumber",model.getPhoneNumber());
                        user.put("PatientSymptoms", model.getSymptoms());
               *//* user.put("ChemistId",ChemistId);
                user.put("ChemistName",ChemistName);
                user.put("ChemistPhoneNumber",ChemistPhoneNumber);*//*
                        user.put("FullAddress",Did);
                        user.put("DoctorName",Dname);
                        user.put("DoctorPhoneNumber",DNumber);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent sendStuff = new Intent(DoctorSideNew.this, ActivityCallDoctortoP.class);
                                sendStuff.putExtra("PatientPassId", model.getPhoneNumber());
                                sendStuff.putExtra("PatientCard", model.getCard());
                                startActivity(sendStuff);



                                t.interrupt();

                                pd.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                            }
                        });*/

                        date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault()).format(new Date());
                        DocumentReference documentReference2 = fStore.collection("Consultation").document(id);
                        Map<String,Object> user2 = new HashMap<>();
                        user2.put("ConsultationId",id);
                        user2.put("PatientId",model.getItemId());
                        user2.put("PatientPhone",model.getPhoneNumber());
                        user2.put("ItemId",model.getItemId());
                        user2.put("PName",model.getName());
                        user2.put("PatientCard",model.getCard());
                        user2.put("Symtoms",model.getSymptoms());
                        user2.put("Age",model.getAge());
                        user2.put("Gender",model.getGender());
               /* user.put("ChemistId",ChemistId);
                user.put("ChemistName",ChemistName);
                user.put("ChemistPhoneNumber",ChemistPhoneNumber);*/
                        user2.put("DoctorId",Did);
                        user2.put("DoctorName",Dname);
                        user2.put("Block","none");
                        user2.put("DateTime",date);
                        user2.put("Status","1");
                        user2.put("Time",model.getTime());
                        user2.put("url","");
                        user2.put("urldescription","");
                        documentReference2.set(user2).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                        /*Intent sendStuff = new Intent(DoctorSide.this, ActivityCallDoctortoP.class);
                        sendStuff.putExtra("PatientPassId", pPhoneNumber);
                        startActivity(sendStuff);*/

                                Intent sendStuff = new Intent(YesterdayPatient.this, DoctorCallActivity.class);
                                sendStuff.putExtra("PatientPassId", model.getPhoneNumber());
                                sendStuff.putExtra("PatientCard", model.getCard());
                                sendStuff.putExtra("Pname", model.getName());
                                sendStuff.putExtra("Symtoms", model.getSymptoms());
                                sendStuff.putExtra("itemid", model.getItemId());
                                sendStuff.putExtra("page", model.getAge());
                                sendStuff.putExtra("pgender", model.getGender());
                                sendStuff.putExtra("consultitemid", id);
                                sendStuff.putExtra("Did", Dname);
                                startActivity(sendStuff);

                                t.interrupt();
                                SharedPreferences.Editor image = getSharedPreferences("PNumber", MODE_PRIVATE).edit();
                                image.putString("no", model.getPhoneNumber());
                                image.commit();

                                pd.dismiss();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                            }
                        });
                    }
                });



            }

            @Override
            public FriendsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_item_patient, group, false);

                return new FriendsHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
                pd.dismiss();
            }
        };


        adapter.notifyDataSetChanged();
        friendList.setAdapter(adapter);



        //2//

        adapter = new FirestoreRecyclerAdapter<PatientResponse, FriendsHolder>(response) {
            @Override
            public void onBindViewHolder(final FriendsHolder holder, int position, final PatientResponse model) {
                progressBar.setVisibility(View.GONE);
                holder.textName.setText(model.getName());
                holder.textTitle.setText(model.getSymptoms());
                holder.textCompany.setText(model.getDateTime());
                /*Glide.with(getApplicationContext())
                        .load(model.getImage())
                        .into(holder.imageView);*/


                holder.reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        holder.itemView.setVisibility(View.INVISIBLE);
                    }
                });

                SharedPreferences prefs = getSharedPreferences("User", MODE_PRIVATE);
                String phonenumber = prefs.getString("phone", null);


                if (phonenumber != null){
                    DocumentReference documentReference2 = fStore.collection("Doctor").document(phonenumber);
                    documentReference2.addSnapshotListener(YesterdayPatient.this, new EventListener<DocumentSnapshot>() {
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

                holder.call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pd.show();
                        final String id = fStore.collection("Consultation").document().getId();
                       /* DocumentReference documentReference = fStore.collection("Accept").document(id);
                        Map<String,Object> user = new HashMap<>();
                        user.put("PatientName",model.getName());
                        user.put("PatientPhoneNumber",model.getPhoneNumber());
                        user.put("PatientSymptoms", model.getSymptoms());
               *//* user.put("ChemistId",ChemistId);
                user.put("ChemistName",ChemistName);
                user.put("ChemistPhoneNumber",ChemistPhoneNumber);*//*
                        user.put("FullAddress",Did);
                        user.put("DoctorName",Dname);
                        user.put("DoctorPhoneNumber",DNumber);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent sendStuff = new Intent(DoctorSideNew.this, ActivityCallDoctortoP.class);
                                sendStuff.putExtra("PatientPassId", model.getPhoneNumber());
                                sendStuff.putExtra("PatientCard", model.getCard());
                                startActivity(sendStuff);



                                t.interrupt();

                                pd.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                            }
                        });*/

                        date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault()).format(new Date());
                        DocumentReference documentReference2 = fStore.collection("Consultation").document(id);
                        Map<String,Object> user2 = new HashMap<>();
                        user2.put("ConsultationId",id);
                        user2.put("PatientId",model.getItemId());
                        user2.put("PatientPhone",model.getPhoneNumber());
                        user2.put("ItemId",model.getItemId());
                        user2.put("PName",model.getName());
                        user2.put("PatientCard",model.getCard());
                        user2.put("Symtoms",model.getSymptoms());
                        user2.put("Age",model.getAge());
                        user2.put("Gender",model.getGender());
               /* user.put("ChemistId",ChemistId);
                user.put("ChemistName",ChemistName);
                user.put("ChemistPhoneNumber",ChemistPhoneNumber);*/
                        user2.put("DoctorId",Did);
                        user2.put("DoctorName",Dname);
                        user2.put("Block","none");
                        user2.put("DateTime",date);
                        user2.put("Status","1");
                        user2.put("Time",model.getTime());
                        user2.put("url","");
                        user2.put("urldescription","");
                        documentReference2.set(user2).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                        /*Intent sendStuff = new Intent(DoctorSide.this, ActivityCallDoctortoP.class);
                        sendStuff.putExtra("PatientPassId", pPhoneNumber);
                        startActivity(sendStuff);*/

                                Intent sendStuff = new Intent(YesterdayPatient.this, DoctorCallActivity.class);
                                sendStuff.putExtra("PatientPassId", model.getPhoneNumber());
                                sendStuff.putExtra("PatientCard", model.getCard());
                                sendStuff.putExtra("Pname", model.getName());
                                sendStuff.putExtra("Symtoms", model.getSymptoms());
                                sendStuff.putExtra("itemid", model.getItemId());
                                sendStuff.putExtra("page", model.getAge());
                                sendStuff.putExtra("pgender", model.getGender());
                                sendStuff.putExtra("consultitemid", id);
                                startActivity(sendStuff);

                                t.interrupt();
                                SharedPreferences.Editor image = getSharedPreferences("PNumber", MODE_PRIVATE).edit();
                                image.putString("no", model.getPhoneNumber());
                                image.commit();

                                pd.dismiss();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                            }
                        });
                    }
                });



            }

            @Override
            public FriendsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_item_patient, group, false);

                return new FriendsHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
                pd.dismiss();
            }
        };


        adapter.notifyDataSetChanged();
        friendList.setAdapter(adapter);

    }



    public class FriendsHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textTitle;
        TextView textCompany;
        TextView reject;
        TextView call;

        public FriendsHolder(View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.D_degree);
            textTitle = itemView.findViewById(R.id.D_address);
            textCompany = itemView.findViewById(R.id.date);
            reject = itemView.findViewById(R.id.reject);
            call = itemView.findViewById(R.id.call);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void check(){
        DocumentReference usernotify = fStore.collection("Notification").document("PatientRequestNotification");
        usernotify.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    notificationtrue = documentSnapshot.getString("Notification");
                    Name = documentSnapshot.getString("Name");
                    if ("True".equals(notificationtrue)){
                        NotificationCompat.Builder builder =
                                new NotificationCompat.Builder(YesterdayPatient.this)
                                        .setSmallIcon(R.mipmap.ic_launcher) //set icon for notification
                                        .setContentTitle("New Patient") //set title of notification
                                        .setContentText("Patient name is  "+Name)//this is notification message
                                        .setAutoCancel(true) // makes auto cancel of notification
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification
                        builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
                        Intent intent = new Intent(YesterdayPatient.this, YesterdayPatient.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(YesterdayPatient.this, 0, intent, 0);
                        builder.setContentIntent(pendingIntent);
                        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.notify(0, builder.build());

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

    public void startService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
    }


    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher) //set icon for notification
                        .setContentTitle("New Patient") //set title of notification
                        .setContentText("Patient name is"+Name)//this is notification message
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

                        Intent intent = new Intent(YesterdayPatient.this,LogindcActivity.class);
                        startActivity(intent);
                        finish();


                    }
                }).setNegativeButton("no", null).show();
    }




}
