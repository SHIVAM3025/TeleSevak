package app.sagar.telesevek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import app.sagar.telesevek.Models.Doctor;
import app.sagar.telesevek.Models.FirebaseUserModel;
import app.sagar.telesevek.Status.StatusTwoActivity;
import app.sagar.telesevek.uploadpkg.ShowImageActivity;
import app.sagar.telesevek.uploadpkg.UploadImage;
import cz.msebera.android.httpclient.HttpHeaders;
import cz.msebera.android.httpclient.entity.StringEntity;

public class PastCounsulation extends AppCompatActivity {
    CardView overlaybrands;
    Button past;
    Button buy;
    Button ourdoctor;
    FirebaseFirestore fStore;
    String mFirstName;
    TextView tvfirstname;
    ProgressDialog pd;
    FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    ProgressBar progressBar;
    RecyclerView friendList;
    String fullName;
    Doctor user = Doctor.getInstance();
    FirebaseDatabase database;
    DatabaseReference usersRef;
    private static final String TAG = "AddPatiant";
    JSONArray registration_ids = new JSONArray();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_counsulation);

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Doctor");

        SharedPreferences sharedpreferences = getSharedPreferences(user.appPreferences, Context.MODE_PRIVATE);
        user.sharedpreferences = sharedpreferences;

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



        friendList = findViewById(R.id.friend_list);
        progressBar = findViewById(R.id.progress_bar);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        friendList.setLayoutManager(linearLayoutManager);

        pd = new ProgressDialog(PastCounsulation.this);
        pd.setMessage("loading..");

        SharedPreferences prefs = getSharedPreferences("past", MODE_PRIVATE);
        final String phonenumber = prefs.getString("pastphonenumber", "nodata");

        Toast.makeText(this, ""+phonenumber, Toast.LENGTH_SHORT).show();

        fStore = FirebaseFirestore.getInstance();
        Query query = fStore.collection("Consultation").whereEqualTo("PatientPhone",phonenumber);

        FirestoreRecyclerOptions<ConsultResponse> response = new FirestoreRecyclerOptions.Builder<ConsultResponse>()
                .setQuery(query, ConsultResponse.class)
                .build();



        adapter = new FirestoreRecyclerAdapter<ConsultResponse, PastCounsulation.FriendsHolder>(response) {
            @Override
            public void onBindViewHolder(final PastCounsulation.FriendsHolder holder, int position, final ConsultResponse model) {
                progressBar.setVisibility(View.GONE);
                holder.textName.setText(model.getPName());
                holder.textTitle.setText(model.getDoctorId());
                holder.textCompany.setText(model.getDateTime());
                /*Glide.with(getApplicationContext())
                        .load(model.getImage())
                        .into(holder.imageView);*/


                SharedPreferences prefs = getSharedPreferences("User", MODE_PRIVATE);
                String phonenumber = prefs.getString("phone", null);



                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent sendStuff = new Intent(PastCounsulation.this, ShowImageActivity.class);
                        sendStuff.putExtra("DocuId", model.getConsultationId());
                        startActivity(sendStuff);
                    }
                });

                holder.folloup_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DocumentReference consultitem = fStore.collection("Consultation").document(model.getConsultationId());
                        consultitem.update("TypeOfConsultation", "Followup")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        pd.dismiss();
                                        Toast.makeText(PastCounsulation.this, "Follow up Successfully", Toast.LENGTH_SHORT).show();

                                                          fullName = model.getPName();

                                                                        notification();

                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PastCounsulation.this, "Plase Check after some time", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        });

                    }
                });




            }

            @Override
            public PastCounsulation.FriendsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_item_consulat, group, false);

                return new PastCounsulation.FriendsHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };


        adapter.notifyDataSetChanged();
        friendList.setAdapter(adapter);



       /* overlaybrands = findViewById(R.id.overlaybrands);
        overlaybrands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent chemistinten = new Intent(PastCounsulation.this, PatientFullImageShow.class);
                startActivity(chemistinten);
            }
        });*/
      /* tvfirstname = findViewById(R.id.pname);
        fStore = FirebaseFirestore.getInstance();
        fStore.collection("Consultation")
                .whereEqualTo("ConsultationId","teststatus")//looks for the corresponding value with the field
                // in the database
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {

                                tvfirstname.setText((CharSequence) document.get("DateTime"));
                                // These values must exactly match the fields you have in your db

                            }
                        }

                    }
        });
*/
        //string checking outside the docRef.get().addOnCompleteListener code
        //commented it out because it causes a java.lang.NullPointerException: println needs a message
        //Log.v("NAME", mFirstName);
        //Log.v("NAME", mLastName);

        // sets the text on the TextViews





        past = findViewById(R.id.consult);
        past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent chemistinten = new Intent(PastCounsulation.this, ScratchCardNew.class);
                startActivity(chemistinten);
            }
        });


        buy = findViewById(R.id.card);
        buy.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Intent chemistinten = new Intent(PastCounsulation.this, Buycard.class);
                        startActivity(chemistinten);
                    }
                });

        ourdoctor = findViewById(R.id.odoctor);
        ourdoctor.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Intent chemistinten = new Intent(PastCounsulation.this, OurDoctor.class);
                        startActivity(chemistinten);
                    }
                });

    }

    public class FriendsHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textTitle;
        TextView textCompany;
        Button folloup_btn;


        public FriendsHolder(View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.name);
            textTitle = itemView.findViewById(R.id.title);
            textCompany = itemView.findViewById(R.id.company);
            folloup_btn = itemView.findViewById(R.id.followup);

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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(PastCounsulation.this,ScratchCardNew.class);
                        startActivity(intent);
                        finish();

                        System.exit(0);
                    }
                }).setNegativeButton("no", null).show();
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
                        Toast.makeText(PastCounsulation.this, "failed Notification", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                        Log.i(TAG, responseString);
                        Toast.makeText(PastCounsulation.this, "Send Notification", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {

            }


        }
    }

}