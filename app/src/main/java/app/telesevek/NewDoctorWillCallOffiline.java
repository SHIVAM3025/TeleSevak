package app.telesevek;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import app.sinch.BaseActivity;
import app.telesevek.NewSceen.HomePatient;

public class NewDoctorWillCallOffiline extends BaseActivity{
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
        setContentView(R.layout.activity_new_doctor_will_call_ofline);



        findViewById(R.id.btHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), HomePatient.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                overridePendingTransition(0,0);
                startActivity(intent);
            }
        });

        SharedPreferences settings = getApplicationContext().getSharedPreferences("Drselected", Context.MODE_PRIVATE);
        settings.edit().clear().commit();





    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent=new Intent(getApplicationContext(),ScratchCardNew.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(0,0);
        startActivity(intent);
    }
}