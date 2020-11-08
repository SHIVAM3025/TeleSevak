package app.sagar.telesevek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class DoctorConfirm extends AppCompatActivity {
    FirebaseFirestore fStore;
    TextView Dname,Did;
    String PatientPassId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_confirm);


        Intent startingIntent = getIntent();
        PatientPassId = startingIntent.getStringExtra("PatientPassId");


        fStore = FirebaseFirestore.getInstance();
        Dname = findViewById(R.id.D_name);
        Did = findViewById(R.id.D_degree);
        DocumentReference documentReference2 = fStore.collection("Accept").document(PatientPassId);
        documentReference2.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    Dname.setText(documentSnapshot.getString("DoctorName"));
                    Did.setText(documentSnapshot.getString("FullAddress"));
                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });

    }
}