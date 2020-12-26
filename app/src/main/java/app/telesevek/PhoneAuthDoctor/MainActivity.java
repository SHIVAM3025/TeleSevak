package app.telesevek.PhoneAuthDoctor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import app.telesevek.Models.Doctor;
import app.telesevek.Models.FirebaseUserModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.iid.FirebaseInstanceId;

import app.telesevek.DoctorSideNew;
import app.telesevek.R;

import java.util.Objects;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    private Spinner spinner;
    private EditText editText;
    private FirebaseAuth firebaseAuth;
    String DNumber;
    String Dname;
    String Did;
    FirebaseFirestore fStore;
    String currentDeviceId;
    Doctor user = Doctor.getInstance();

    String phoneNumber;
    FirebaseDatabase database;
    DatabaseReference usersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_auth);

        fStore = FirebaseFirestore.getInstance();

        SharedPreferences sharedpreferences = getSharedPreferences(user.appPreferences, Context.MODE_PRIVATE);
        user.sharedpreferences = sharedpreferences;

        currentDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");



        SharedPreferences prefs = getSharedPreferences("chemist", MODE_PRIVATE);
        String dname = prefs.getString("dadd", null);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            //Starting the User Profile Activity if the user is already Logged in
            if(TextUtils.isEmpty(dname)){
            }
            else {
                if (Objects.equals(dname, "doctorname")) {
                    startActivity(new Intent(getApplicationContext(), DoctorSideNew.class));
                }
            }


        }



        spinner = findViewById(R.id.spinnerCountries);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));

        editText = findViewById(R.id.editTextPhone);

        findViewById(R.id.buttonContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String code = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];

                final String number = editText.getText().toString().trim();


                if (number.isEmpty() || number.length() < 10) {
                    editText.setError("Valid number is required");
                    editText.requestFocus();
                    return;
                }
                phoneNumber = "+" + code + number;

                DocumentReference documentReference2 = fStore.collection("Doctor").document(phoneNumber);
                documentReference2.addSnapshotListener(MainActivity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot.exists()){


                            Intent intent = new Intent(MainActivity.this, VerifyPhoneActivity.class);
                            intent.putExtra("phonenumber", phoneNumber);
                            startActivity(intent);

                        }else {
                            Log.d("tag", "onEvent: Document do not exists");
                            Toast.makeText(MainActivity.this, "invalid phone number", Toast.LENGTH_SHORT).show();
                        }
                    }
                });




            }
        });
    }

    public void SubscribeToken(){

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
                    Toast.makeText(MainActivity.this, "accept", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Dialog.dismiss();
                System.out.println("The read failed: " + databaseError.getMessage());
                Toast.makeText(MainActivity.this, "failde", Toast.LENGTH_SHORT).show();
            }
        });

       /* final ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
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

                    Toast.makeText(MainActivity.this, "enter", Toast.LENGTH_SHORT).show();

                    if (firebaseUserModel.getDeviceId().equals(currentDeviceId)) {
                        firebaseUserModel.setDeviceToken(FirebaseInstanceId.getInstance().getToken());
                        user.login(firebaseUserModel);
                        user.saveFirebaseKey(userSnapshot.getKey());
                        Toast.makeText(MainActivity.this, "ewual", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "else", Toast.LENGTH_SHORT).show();
                        final FirebaseUserModel firebaseUserModel2 = new FirebaseUserModel();
                        firebaseUserModel2.setDeviceId(currentDeviceId);
                        firebaseUserModel2.setDeviceToken(FirebaseInstanceId.getInstance().getToken());
                        final DatabaseReference newRef = usersRef.push();
                        newRef.setValue(firebaseUserModel2, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (user.login(firebaseUserModel2)) {

                                    Toast.makeText(MainActivity.this, "goto", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Dialog.dismiss();
                Toast.makeText(MainActivity.this, "complet:The read failed", Toast.LENGTH_SHORT).show();

                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();


    }
}
