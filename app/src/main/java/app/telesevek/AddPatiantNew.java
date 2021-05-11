package app.telesevek;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sinch.android.rtc.SinchError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Nullable;

import app.sinch.BaseActivity;
import app.sinch.SinchService;
import app.telesevek.Models.Doctor;
import app.telesevek.Models.FirebaseUserModel;
import app.telesevek.NewSceen.HomePatient;
import cz.msebera.android.httpclient.HttpHeaders;
import cz.msebera.android.httpclient.entity.StringEntity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class AddPatiantNew extends AppCompatActivity {
    EditText mFullName, mSymptoms, mPhone;
    EditText mAge;
    Button mRegisterBtn;

    RadioGroup radioGroup;
    RadioButton radioButton,radioButton2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patiantnew);


        mFullName = findViewById(R.id.ed_pname);
        mPhone = findViewById(R.id.ed_pnumber);
        mAge = findViewById(R.id.ed_age);
        mRegisterBtn = findViewById(R.id.submit);
        radioGroup = findViewById(R.id.gr);
        radioButton = findViewById(R.id.male);
        radioButton2 = findViewById(R.id.female);

        RelativeLayout back = findViewById(R.id.appbar);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go = new Intent(AddPatiantNew.this, HomePatient.class);
                startActivity(go);
            }
        });

       /* BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.consultDoctor);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.pastConsult:
                        startActivity(new Intent(getApplicationContext(), PastConsultationNewLoginScreen.class));
                        overridePendingTransition(0, 0);
                        return true;

                  *//*  case R.id.buyCard:
                        startActivity(new Intent(getApplicationContext(),Buycard.class));
                        overridePendingTransition(0,0);
                        return true;*//*

                    case R.id.ourDoctors:
                        startActivity(new Intent(getApplicationContext(), OurDoctor.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.consultDoctor:
                        return true;
                }
                return false;
            }
        });
*/

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               final String card = getIntent().getStringExtra("cardpass");
                int remainconsult = getIntent().getIntExtra("remainconsult",0);

                //sendNotification(token,"Incoming Call","Incoming Call");
                final String fullName = mFullName.getText().toString();
                final String phone = mPhone.getText().toString();

                String result = "";
                result += (radioButton.isChecked()) ? "male" : (radioButton2.isChecked()) ? "female" : "";
                final String Age = mAge.getText().toString();


                if (TextUtils.isEmpty(fullName)) {
                    mFullName.setError("कृपया मरीज का नाम लिखें.");
                    return;
                }

                if (mPhone.length() < 10) {
                    mPhone.setError("कृपया मोबाइल नंबर लिखें");
                    return;
                }

                if (TextUtils.isEmpty(Age)) {
                    mAge.setError("Age is Required.");
                    return;
                }

                Intent sendStuff = new Intent(AddPatiantNew.this, AddPatiantSymtom.class);
                sendStuff.putExtra("Pname", fullName);
                sendStuff.putExtra("Pnum", phone);
                sendStuff.putExtra("Page", Age);
                sendStuff.putExtra("Pgender", result);
                sendStuff.putExtra("cardpass", card);
                sendStuff.putExtra("remainconsult", remainconsult);
                startActivity(sendStuff);


            }


        });
    }
}
