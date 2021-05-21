package app.telesevek.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.iid.FirebaseInstanceId;

import javax.annotation.Nullable;

import app.telesevek.DoctorSideNew;
import app.telesevek.Models.Doctor;
import app.telesevek.Models.FirebaseUserModel;
import app.telesevek.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button btnLogin;

    String currentDeviceId;

    Doctor user = Doctor.getInstance();

    FirebaseDatabase database;
    DatabaseReference usersRef;
    String token;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainchat);
        fStore = FirebaseFirestore.getInstance();


        SharedPreferences prefs = getSharedPreferences("User", MODE_PRIVATE);
        String phonenumber = prefs.getString("phone", null);

        token= FirebaseInstanceId.getInstance().getToken();
        fStore.collection("Doctor").document(phonenumber)
                .update("TokenFCM",token)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("tokenFCM","added successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("tokenFCM","cannot be added");
            }
        });


        if (phonenumber != null) {
            DocumentReference documentReference2 = fStore.collection("Doctor").document(phonenumber);
            documentReference2.addSnapshotListener(MainActivity.this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {

                        String dtype = documentSnapshot.getString("TypeOfDoctor");
                        String checkcovid = documentSnapshot.getString("CovidDoctor");


                        SharedPreferences.Editor doctorsave = getSharedPreferences("doctornamevideocall", MODE_PRIVATE).edit();
                        doctorsave.putString("dtype", dtype);
                        doctorsave.commit();


                    } else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });
        }


            SharedPreferences sharedpreferences = getSharedPreferences(Doctor.appPreferences, Context.MODE_PRIVATE);
        user.sharedpreferences = sharedpreferences;

        currentDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Doctor");

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

                    if (firebaseUserModel.getDeviceId().equals(currentDeviceId)) {
                        firebaseUserModel.setDeviceToken(FirebaseInstanceId.getInstance().getToken());
                        userSnapshot.getRef().removeValue();



                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Dialog.dismiss();
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });



    }

    public void moveToChattingScreen() {
        Intent intent = new Intent(this, DoctorSideNew.class);
        startActivity(intent);
        finish();
    }


    public void btnLoginTapped(View view) {
            final FirebaseUserModel firebaseUserModel = new FirebaseUserModel();
            firebaseUserModel.setDeviceId(currentDeviceId);
            firebaseUserModel.setDeviceToken(FirebaseInstanceId.getInstance().getToken());

            final ProgressDialog Dialog = new ProgressDialog(this);
            Dialog.setMessage("Please wait..");
            Dialog.setCancelable(false);
            Dialog.show();

            final DatabaseReference newRef = usersRef.push();
            newRef.setValue(firebaseUserModel, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Dialog.dismiss();
                    if (user.login(firebaseUserModel)) {
                        moveToChattingScreen();
                    }
                }
            });

    }



}