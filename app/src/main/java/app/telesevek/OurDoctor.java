package app.telesevek;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import app.telesevek.NewSceen.HomePatient;

public class OurDoctor extends AppCompatActivity {
    ProgressBar progressBar;
    RecyclerView friendList;
    Button past;
    Button buy;
    Button ourdoctor;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    Typeface b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_our_doctor);
        friendList = findViewById(R.id.friend_list);
        progressBar = findViewById(R.id.progress_bar);
        init();
        getFriendList();

        b= Typeface.createFromAsset(getAssets(),"font/Helvetica.ttf");

        BottomNavigationView bottomNav=findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.ourDoctors);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.pastConsult:
                        startActivity(new Intent(getApplicationContext(),PastConsultationNewLoginScreen.class));
                        overridePendingTransition(0,0);
                        return true;

                /*    case R.id.buyCard:
                        startActivity(new Intent(getApplicationContext(),Buycard.class));
                        overridePendingTransition(0,0);
                        return true;*/

                    case R.id.ourDoctors:
                        return true;

                    case R.id.consultDoctor:
                        startActivity(new Intent(getApplicationContext(), HomePatient.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        /*
        past = findViewById(R.id.Past);
        past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent chemistinten = new Intent(OurDoctor.this, MainActivity.class);
                startActivity(chemistinten);
            }
        });


        buy = findViewById(R.id.consult);
        buy.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Intent chemistinten = new Intent(OurDoctor.this, ScratchCardNew.class);
                        startActivity(chemistinten);
                    }
                });

        ourdoctor = findViewById(R.id.card);
        ourdoctor.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Intent chemistinten = new Intent(OurDoctor.this, Buycard.class);
                        startActivity(chemistinten);
                    }
                });

        */

    }
    private void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        friendList.setLayoutManager(linearLayoutManager);
        friendList.setLayoutManager(new GridLayoutManager(this, 2));
        int spanCount = 2; // 3 columns
        int spacing = 50; // 50px
        boolean includeEdge = true;
        friendList.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        db = FirebaseFirestore.getInstance();
    }

    private void getFriendList(){
        Query query = db.collection("Doctor").whereEqualTo("IsActive","true");
        FirestoreRecyclerOptions<DoctorResponse> response = new FirestoreRecyclerOptions.Builder<DoctorResponse>()
                .setQuery(query, DoctorResponse.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<DoctorResponse, OurDoctor.FriendsHolder>(response) {
            @Override
            public void onBindViewHolder(OurDoctor.FriendsHolder holder, int position, DoctorResponse model) {
                progressBar.setVisibility(View.GONE);
                holder.textName.setText(model.getName());
                holder.degree.setText(model.getDegree());
                holder.exp.setText(model.getExp());
                holder.city.setText(model.getFullAddress());

                holder.textName.setTypeface(b);
                holder.degree.setTypeface(b);
                holder.exp.setTypeface(b);
                holder.city.setTypeface(b);

                Glide.with(getApplicationContext()).load(model.getUrl()).into(holder.img);




               /* Glide.with(getApplicationContext())
                        .load(model.getImage())
                        .into(holder.imageView);*/

              /*  holder.itemView.setOnClickListener(v -> {
                    Snackbar.make(friendList, model.getName()+", "+model.getTitle()+" at "+model.getCompany(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                });*/
            }

            @Override
            public OurDoctor.FriendsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_item, group, false);



                return new OurDoctor.FriendsHolder(view);
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
        TextView degree;
        TextView exp;
        TextView city;

        ImageView img;



        public FriendsHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.name);
            degree = itemView.findViewById(R.id.degree);
            exp = itemView.findViewById(R.id.anubav);
            city = itemView.findViewById(R.id.city);

            img= itemView.findViewById(R.id.profile_image);


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