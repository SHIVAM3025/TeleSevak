package app.sagar.telesevek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.NotificationManager;
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
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import javax.annotation.Nullable;

public class DoctorSideFollowupConsulation extends AppCompatActivity {

    FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    ProgressBar progressBar;
    RecyclerView friendList;

    Button current;
    Button past;
    FirebaseFirestore fStore;
    String Strname;
    String Strphone;
    String Strdate;

    String Strfollowname;
    String Strdatefollow;

    TextView name;
    TextView phone;
    TextView date;

    TextView followname;
    TextView followdate;

    Thread t;

    String notificationtrue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_side_follow_consulation);


       /* name = findViewById(R.id.D_degree);
        phone = findViewById(R.id.D_address);
        date = findViewById(R.id.date);

        followname = findViewById(R.id.followername);
        followdate = findViewById(R.id.dateFOLLOW);

        fStore = FirebaseFirestore.getInstance();
        SharedPreferences prefs = getSharedPreferences("PNumber", MODE_PRIVATE);
        String phonenumber = prefs.getString("no", null);
*/
        friendList = findViewById(R.id.friend_list);
        progressBar = findViewById(R.id.progress_bar);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        friendList.setLayoutManager(linearLayoutManager);


        BottomNavigationView bottomNavDoctor = findViewById(R.id.bottomNavFollow);
        bottomNavDoctor.setSelectedItemId(R.id.followUp_menu);

        bottomNavDoctor.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.viewPast_menu:
                        startActivity(new Intent(getApplicationContext(), DoctorSidePastConsulation.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.followUp_menu:
                        return true;

                    case R.id.current_menu:
                        startActivity(new Intent(getApplicationContext(), DoctorSideNew.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });


        /*
        current = findViewById(R.id.card);
        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chemistinten = new Intent(DoctorSideFollowupConsulation.this, DoctorSideNew.class);
                startActivity(chemistinten);
            }
        });


        past = findViewById(R.id.past);
        past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chemistinten = new Intent(DoctorSideFollowupConsulation.this, DoctorSidePastConsulation.class);
                startActivity(chemistinten);
            }
        });
        */


        SharedPreferences prefs = getSharedPreferences("User", MODE_PRIVATE);
        String phonenumber = prefs.getString("phone", null);


        fStore = FirebaseFirestore.getInstance();
        Query query = fStore.collection("Consultation").whereEqualTo("TypeOfConsultation", "Followup").whereEqualTo("DoctorId", phonenumber);

        FirestoreRecyclerOptions<ConsultResponse> response = new FirestoreRecyclerOptions.Builder<ConsultResponse>()
                .setQuery(query, ConsultResponse.class)
                .build();


        adapter = new FirestoreRecyclerAdapter<ConsultResponse, DoctorSideFollowupConsulation.FriendsHolder>(response) {
            @Override
            public void onBindViewHolder(final DoctorSideFollowupConsulation.FriendsHolder holder, int position, final ConsultResponse model) {
                progressBar.setVisibility(View.GONE);
                holder.textName.setText(model.getPName());
                holder.textCompany.setText(model.getDateTime());


                holder.reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.itemView.setVisibility(View.INVISIBLE);
                    }
                });


                holder.call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sendStuff = new Intent(DoctorSideFollowupConsulation.this, DoctorCallActivityFollow.class);
                        sendStuff.putExtra("PatientPassId", model.getPatientPhone());
                        sendStuff.putExtra("PatientCard", model.getPatientCard());
                        sendStuff.putExtra("Pname", model.getPName());
                        sendStuff.putExtra("Symtoms", model.getSymtoms());
                        sendStuff.putExtra("page", model.getAge());
                        sendStuff.putExtra("pgender", model.getGender());
                        sendStuff.putExtra("itemid", model.getItemId());
                        sendStuff.putExtra("consultitemid", model.getConsultationId());
                        startActivity(sendStuff);


                    }
                });


            }

            @Override
            public FriendsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_item_consultation_follow, group, false);

                return new FriendsHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };


        adapter.notifyDataSetChanged();
        friendList.setAdapter(adapter);

       /* if (phonenumber != null) {
            DocumentReference documentReference2 = fStore.collection("Consultation").document(phonenumber);
            documentReference2.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        Strname = documentSnapshot.getString("PName");
                        Strdate = documentSnapshot.getString("DateTime");
                        Strphone = documentSnapshot.getString("PatientId");

                        name.setText(Strname);
                        phone.setText(Strphone);
                        date.setText(Strdate);

                    } else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });

            if (phonenumber != null) {
                DocumentReference documentReferencefollow = fStore.collection("Followup").document(phonenumber);
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
        }*/

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

    public class FriendsHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textCompany;
        TextView reject;
        TextView call;

        public FriendsHolder(View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.D_degree);
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

    private void check() {
        DocumentReference usernotify = fStore.collection("FollowUPNotification").document("PatientRequestNotification");
        usernotify.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    notificationtrue = documentSnapshot.getString("Notification");
                    if ("True".equals(notificationtrue)) {
                        addNotification();
                        DocumentReference push = fStore.collection("FollowUPNotification").document("PatientRequestNotification");
                        push.delete();
                    }

                } else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });
    }


    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher) //set icon for notification
                        .setContentTitle("New Followup") //set title of notification
                        .setContentText("New Followup name is" + Strfollowname)//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), DoctorSideNew.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}