package app.sagar.telesevek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class PetientLoading extends AppCompatActivity {
    ProgressBar Progressbar;
    int progressBarValue = 0;
    Handler handler = new Handler();
    FirebaseFirestore fStore;
    String PatientAccept;
    String PatientPassId;
    String Aceeptdoctor;
    Thread t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petient_loading);
        Progressbar = (ProgressBar)findViewById(R.id.progress);
        fStore = FirebaseFirestore.getInstance();

        /*phonenumberintent = getIntent().getStringExtra("phonenumber");*/

        Bundle bundle = getIntent().getExtras();
        PatientPassId = bundle.getString("PatientPassId");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DocumentReference push = fStore.collection("Notification").document("PatientRequestNotification");
                push.delete();
            }
        }, 2000);


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




        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while(progressBarValue < 100)
                {
                    progressBarValue++;

                    handler.post(new Runnable() {

                        @Override
                        public void run() {

                            Progressbar.setProgress(progressBarValue);
                            DocumentReference push = fStore.collection("Notification").document("PatientRequestNotification");
                            push.delete();

                        }
                    });try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                }
            }
        }).start();
    }




    private void check(){
        DocumentReference documentReference2 = fStore.collection("Accept").document(PatientPassId);
        documentReference2.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    PatientAccept = documentSnapshot.getString("PatientPhoneNumber");
                    if (PatientPassId.equals(PatientAccept)){
                        Intent sendStuff = new Intent(PetientLoading.this, DoctorConfirm.class);
                        sendStuff.putExtra("PatientPassId", PatientPassId);
                        startActivity(sendStuff);
                        t.interrupt();
                    }
                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });
    }

}