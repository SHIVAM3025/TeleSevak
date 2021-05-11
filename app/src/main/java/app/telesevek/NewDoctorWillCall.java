package app.telesevek;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.sinch.android.rtc.calling.Call;

import javax.annotation.Nullable;

import app.sinch.BaseActivity;
import app.sinch.CallScreenActivity;
import app.sinch.SinchService;
import app.telesevek.NewSceen.HomePatient;
import app.telesevek.uploadpkg.ShowImageActivity;

public class NewDoctorWillCall extends BaseActivity{
    String card;
    FirebaseFirestore fStore;
    ProgressBar progressBar;
    RecyclerView friendList;
    Button past;
    Button buy;
    Button ourdoctor;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_doctor_will_call);

        friendList = findViewById(R.id.friend_list);
        progressBar = findViewById(R.id.progress_bar);
        init();


        fStore = FirebaseFirestore.getInstance();
        card = getIntent().getStringExtra("scartchcardno");

        if ("call".equals(card)){
            getFriendList();
        }
        findViewById(R.id.btHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), HomePatient.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                overridePendingTransition(0,0);
                startActivity(intent);
            }
        });



    }

    private void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        friendList.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    private void getFriendList(){
        Query query = db.collection("Doctor").whereEqualTo("IsActive","true");
        FirestoreRecyclerOptions<DoctorResponse> response = new FirestoreRecyclerOptions.Builder<DoctorResponse>()
                .setQuery(query, DoctorResponse.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<DoctorResponse, NewDoctorWillCall.FriendsHolder>(response) {
            @Override
            public void onBindViewHolder(NewDoctorWillCall.FriendsHolder holder, int position, DoctorResponse model) {
                progressBar.setVisibility(View.GONE);
                holder.textName.setText(model.getName());
                holder.textCompany.setText(model.getFullAddress());
               /* Glide.with(getApplicationContext())
                        .load(model.getImage())
                        .into(holder.imageView);*/

              holder.itemView.setOnClickListener(v -> {
                  if (model.getPhoneNumber().isEmpty()) {
                      Toast.makeText(NewDoctorWillCall.this, "Please enter a user to call", Toast.LENGTH_LONG).show();
                      return;
                  }

                  Call call = getSinchServiceInterface().callUserVideo(model.getPhoneNumber());
                  String callId = call.getCallId();

                  Intent callScreen = new Intent(NewDoctorWillCall.this, CallScreenActivity.class);
                  callScreen.putExtra(SinchService.CALL_ID, callId);
                  startActivity(callScreen);
                });
            }

            @Override
            public NewDoctorWillCall.FriendsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_item_doctor_call, group, false);



                return new NewDoctorWillCall.FriendsHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        friendList.setAdapter(adapter);
    }

    public class FriendsHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textCompany;


        public FriendsHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.name);
            textCompany = itemView.findViewById(R.id.company);
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
    /*@Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(OurDoctor.this,ScratchCardNew.class);
                        startActivity(intent);
                        finish();

                        System.exit(0);
                    }
                }).setNegativeButton("no", null).show();
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent=new Intent(getApplicationContext(),ScratchCardNew.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(0,0);
        startActivity(intent);
    }
}