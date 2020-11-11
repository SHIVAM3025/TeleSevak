package app.sagar.telesevek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import app.sagar.telesevek.PhoneAuthConsulation.MainActivity;
import app.sagar.telesevek.Status.StatusTwoActivity;

public class Buycard extends AppCompatActivity {
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
        setContentView(R.layout.activity_buycard);
        friendList = findViewById(R.id.friend_list);
        progressBar = findViewById(R.id.progress_bar);
        init();
        getFriendList();

        BottomNavigationView bottomNav=findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.buyCard);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.pastConsult:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.buyCard:
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


        /*
        past = findViewById(R.id.Past);
        past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent chemistinten = new Intent(Buycard.this, MainActivity.class);
                startActivity(chemistinten);
            }
        });


        buy = findViewById(R.id.consult);
        buy.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Intent chemistinten = new Intent(Buycard.this, ScratchCardNew.class);
                        startActivity(chemistinten);
                    }
                });

        ourdoctor = findViewById(R.id.odoctor);
        ourdoctor.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Intent chemistinten = new Intent(Buycard.this, OurDoctor.class);
                        startActivity(chemistinten);
                    }
                });

        */

    }
    private void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        friendList.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    private void getFriendList(){
        Query query = db.collection("Chemist");
        FirestoreRecyclerOptions<ChemistResponse> response = new FirestoreRecyclerOptions.Builder<ChemistResponse>()
                .setQuery(query, ChemistResponse.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ChemistResponse, Buycard.FriendsHolder>(response) {
            @Override
            public void onBindViewHolder(Buycard.FriendsHolder holder, int position, ChemistResponse model) {
                progressBar.setVisibility(View.GONE);
                holder.textName.setText(model.getChemistName());
                holder.textCompany.setText(model.getFullAddress());
                holder.phone.setText(model.getChemistPhoneNumber());
               /* Glide.with(getApplicationContext())
                        .load(model.getImage())
                        .into(holder.imageView);*/

              /*  holder.itemView.setOnClickListener(v -> {
                    Snackbar.make(friendList, model.getName()+", "+model.getTitle()+" at "+model.getCompany(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                });*/
            }

            @Override
            public Buycard.FriendsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_item_buy, group, false);



                return new Buycard.FriendsHolder(view);
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
        TextView phone;


        public FriendsHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.name);
            textCompany = itemView.findViewById(R.id.company);
            phone = itemView.findViewById(R.id.phone);
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

                        Intent intent = new Intent(Buycard.this,ScratchCardNew.class);
                        startActivity(intent);
                        finish();

                        System.exit(0);
                    }
                }).setNegativeButton("no", null).show();
    }
}