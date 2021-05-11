package app.telesevek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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
import android.widget.Toast;

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

import app.telesevek.uploadpkg.ShowImageActivity;

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
    FirestoreRecyclerOptions<ConsultResponse> response;
    ProgressBar progressBar;
    RecyclerView friendList;
    boolean isNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_counsulation);


        friendList = findViewById(R.id.friend_list);
        progressBar = findViewById(R.id.progress_bar);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        friendList.setLayoutManager(linearLayoutManager);

        pd = new ProgressDialog(PastCounsulation.this);
        pd.setMessage("loading..");

        String scratchCardNumber=getIntent().getStringExtra("cardNumber");
        boolean isScratchCard=getIntent().getBooleanExtra("isScratchCard",false);

        SharedPreferences prefs = getSharedPreferences("past", MODE_PRIVATE);
        final String phonenumber = prefs.getString("pastphonenumber", "nodata");

        Toast.makeText(this, ""+phonenumber, Toast.LENGTH_SHORT).show();
        /*EditText etField=findViewById(R.id.etScratchOrPhone);
        if(etField.isInEditMode()){
        String Number=etField.getText().toString().trim();

        isNumber= Number.length() == 10;}*/

        fStore = FirebaseFirestore.getInstance();
        if(!isScratchCard) {
            Query query = fStore.collection("Consultation").whereEqualTo("PatientPhone", phonenumber).orderBy("Time",Query.Direction.DESCENDING);

             response = new FirestoreRecyclerOptions.Builder<ConsultResponse>()
                    .setQuery(query, ConsultResponse.class)
                    .build();

        }else {
            Query query = fStore.collection("Consultation").whereEqualTo("PatientCard", scratchCardNumber).orderBy("Time",Query.Direction.DESCENDING);
             response = new FirestoreRecyclerOptions.Builder<ConsultResponse>()
                    .setQuery(query, ConsultResponse.class)
                    .build();
        }



        adapter = new FirestoreRecyclerAdapter<ConsultResponse, PastCounsulation.FriendsHolder>(response) {
            @Override
            public void onBindViewHolder(final PastCounsulation.FriendsHolder holder, int position, final ConsultResponse model) {
                progressBar.setVisibility(View.GONE);
                holder.textName.setText(model.getPName());
                holder.textTitle.setText(model.getDoctorName());
                String s=model.getDateTime().substring(0,10);

                holder.textCompany.setText(s);
             /*   holder.tvsamasya.setText(model.getSymtoms());*/
                /*Glide.with(getApplicationContext())
                        .load(model.getImage())
                        .into(holder.imageView);*/


                SharedPreferences prefs = getSharedPreferences("User", MODE_PRIVATE);
                String phonenumber = prefs.getString("phone", null);



                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        DocumentReference get = fStore.collection("ScratchCard").document(model.getPatientCard());
                        get.addSnapshotListener(PastCounsulation.this, new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if (documentSnapshot.exists()) {
                                    Integer oldcardNEW = (Integer) documentSnapshot.getLong("RemainingConsultations").intValue();
                                    if(0 < oldcardNEW){
                                        Integer carde = oldcardNEW - 1;
                                        Integer carde2 = oldcardNEW;
                                        Intent sendStuff = new Intent(PastCounsulation.this, ShowImageActivity.class);
                                        sendStuff.putExtra("cardpass", model.getPatientCard());
                                        sendStuff.putExtra("remainconsult", carde);
                                        sendStuff.putExtra("DocuId", model.getConsultationId());
                                        sendStuff.putExtra("Docuphone", model.getDoctorId());
                                        startActivity(sendStuff);
                                    }
                                    else {
                                        Intent sendStuff = new Intent(PastCounsulation.this, ShowImageActivity.class);
                                        sendStuff.putExtra("cardpass", model.getPatientCard());
                                        sendStuff.putExtra("remainconsult", "0");
                                        sendStuff.putExtra("DocuId", model.getConsultationId());
                                        startActivity(sendStuff);
                                    }

                                } else {
                                    Log.d("tag", "onEvent: Document do not exists");
                                    pd.dismiss();
                                }
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


        BottomNavigationView bottomNav=findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.pastConsult);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.pastConsult:
                        return true;

                  /*  case R.id.buyCard:
                        startActivity(new Intent(getApplicationContext(),Buycard.class));
                        overridePendingTransition(0,0);
                        return true;*/

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





        /*
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
        */

    }

    public class FriendsHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textTitle;
        TextView textCompany;
        TextView tvsamasya;


        public FriendsHolder(View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.name);
            textTitle = itemView.findViewById(R.id.title);
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

                        Intent intent = new Intent(PastCounsulation.this,ScratchCardNew.class);
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