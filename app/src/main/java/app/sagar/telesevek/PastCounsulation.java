package app.sagar.telesevek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import app.sagar.telesevek.uploadpkg.ShowImageActivity;
import app.sagar.telesevek.uploadpkg.UploadImage;

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

}