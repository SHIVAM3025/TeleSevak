package app.telesevek;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sinch.android.rtc.SinchError;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import app.sinch.BaseActivity;
import app.sinch.SinchService;
import app.telesevek.Models.Doctor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static app.telesevek.AddPatiant.ACCOUNT_SID;
import static app.telesevek.AddPatiant.AUTH_TOKEN;
import static app.telesevek.AddPatiant.from;
import static app.telesevek.AddPatiant.receiveCredentials;

public class FormAfterPayment extends BaseActivity implements SinchService.StartFailedListener {

    public static final String TAG = "TAG";
    EditText mFullName,mSymptoms,mPhone;
    EditText mAge;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    private ProgressDialog mSpinner;
    Button past;
    Button buy;
    Button ourdoctor;
    FirebaseFirestore fStore;
    String card;
    int remainconsult;
    String phone;
    RadioGroup radioGroup;
    RadioButton radioButton;
    RadioButton radioButton2;
    String date;
    String date2;
    String userID,Chemistname,chemistid,chemistphonenumber;
    private RequestQueue mRequestQue;
    private String URL = "https://fcm.googleapis.com/fcm/send";
    ProgressDialog pd;
    String id_consult;

    private static final String TAG2 = "AddPatiant";

    private Button btnLogin;

    String currentDeviceId;

    Doctor user = Doctor.getInstance();
    JSONArray registration_ids = new JSONArray();

    List<String> ls=new ArrayList<>();
    String allEmails="";

    String token;
    String typeOfDoctor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_patiant);

        receiveCredentials();

        typeOfDoctor= Objects.requireNonNull(getIntent().getExtras()).getString("type");




        mFullName   = findViewById(R.id.ed_pname);
        mSymptoms      = findViewById(R.id.ed_symptom);
        mPhone      = findViewById(R.id.ed_pnumber);
        mAge      = findViewById(R.id.ed_age);
        mRegisterBtn= findViewById(R.id.submit);
        radioGroup = findViewById(R.id.gr);
        radioButton = findViewById(R.id.male);
        radioButton2 = findViewById(R.id.female);


        pd = new ProgressDialog(FormAfterPayment.this);
        pd.setMessage("loading..");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        addAdmins();

        //merging admin and doctors
        mergeAdminDoctors();


        Date dateonly = new Date();
        SimpleDateFormat dateFormatWithZone = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        final String currentTime = dateFormatWithZone.format(dateonly);



        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String symtoms = mSymptoms.getText().toString().trim();
                final String fullName = mFullName.getText().toString();
                phone    = mPhone.getText().toString();


                String result = "";
                result+= (radioButton.isChecked())?"male":(radioButton2.isChecked())?"female":"";




                final String Age = mAge.getText().toString();




                if(TextUtils.isEmpty(fullName)){
                    mFullName.setError("name is Required.");
                    return;
                }

                if(TextUtils.isEmpty(symtoms)){
                    mSymptoms.setError("symtoms is Required.");
                    return;
                }

                if(mPhone.length() < 10){
                    mPhone.setError("phone Must be >= 10 Number");
                    return;
                }

                if(TextUtils.isEmpty(Age)){
                    mAge.setError("Age is Required.");
                    return;
                }
                pd.show();

                String id = fStore.collection("Patient").document().getId();
                id_consult = fStore.collection("Consultation").document().getId();


                date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                long yourDateMillis1 = System.currentTimeMillis() + (1 * 24 * 60 * 60 * 1000);
                Time yourDate1 = new Time();
                yourDate1.set(yourDateMillis1);
                final String finalexpiredate1 = yourDate1.format("%Y-%m-%d");

                //second expiredate2
                long yourDateMillis2 = System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000);
                Time yourDate2 = new Time();
                yourDate2.set(yourDateMillis2);
                final String finalexpiredate2 = yourDate2.format("%Y-%m-%d");


                // register the user in firebase

                DocumentReference documentReference = fStore.collection("Patient").document(phone);
                Map<String,Object> user = new HashMap<>();
                user.put("Name",fullName);
                user.put("PhoneNumber","+91"+phone);
                user.put("Symptoms",symtoms);
                user.put("Age",Age);
                user.put("Gender",result);
                user.put("DateTime",date);
                user.put("Card",card);
                user.put("Time",currentTime);
                user.put("Status","Requested");
                user.put("ItemId",id);
                user.put("TypeOfConsultation","Primary");
                user.put("TypeOfDoctor",typeOfDoctor);
               /* user.put("ChemistId",chemistid);
                user.put("ChemistName",Chemistname);
                user.put("ChemistPhoneNumber",chemistphonenumber);*/
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                        /*Intent intent = new Intent(AddPatiant.this, PetientLoading.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("phonenumber", phone);*/


                        final String phone    = "+91"+mPhone.getText().toString();
                        if (fullName.isEmpty()) {
                            Toast.makeText(FormAfterPayment.this, "Please enter a name", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (!fullName.equals(getSinchServiceInterface().getUserName())) {
                            getSinchServiceInterface().stopClient();
                        }

                        if (!getSinchServiceInterface().isStarted()) {
                            getSinchServiceInterface().startClient(fullName);
                            showSpinner();
                        } else {
                            openPlaceCallActivity();
                        }

                        SharedPreferences.Editor image = getSharedPreferences("Image", MODE_PRIVATE).edit();
                        image.putString("pimageid", phone);
                        image.apply();

                        //sending emails
                        sendEmails(fullName,phone,symtoms,Age);

                        sendSMS(fullName,phone);

                        pd.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                        pd.dismiss();
                    }
                });

                //adding token to the collection
                token();
                Log.i("tokenFCM",token);

                date2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault()).format(new Date());
                DocumentReference documentReference2 = fStore.collection("Consultation").document(id_consult);
                Map<String,Object> user2 = new HashMap<>();
                user2.put("ConsultationId",id_consult);
                user2.put("PatientId",id);
                user2.put("PatientPhone","+91"+phone);
                user2.put("ItemId",id);
                user2.put("PName",fullName);
                user2.put("PatientCard",card);
                user2.put("Symtoms",symtoms);
                user2.put("Age",Age);
                user2.put("Gender",result);
               /* user.put("ChemistId",ChemistId);
                user.put("ChemistName",ChemistName);
                user.put("ChemistPhoneNumber",ChemistPhoneNumber);*/
                user2.put("DoctorId","");
                user2.put("DoctorName","");
                user2.put("Block","none");
                user2.put("DateTime",date2);
                user2.put("Status","Requested");
                user2.put("TypeOfConsultation","Primary");
                user2.put("TypeOfDoctor",typeOfDoctor);
                user2.put("Time",currentTime);
                user2.put("url","");
                user2.put("urldescription","");
                user2.put("urludate","null");
                documentReference2.set(user2).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        SharedPreferences.Editor image = getSharedPreferences("Consultpre", MODE_PRIVATE).edit();
                        image.putString("cid", id_consult);
                        image.apply();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                    }
                });




            }
        });



            }


    public void addAdmins(){

        fStore.collection("Admins").whereEqualTo("IsActive","true")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshots=queryDocumentSnapshots.getDocuments();
                        for ( DocumentSnapshot documents : snapshots){
                            String id=documents.getId();
                            ls.add(id);
                            String email=documents.getString("Email");
                            if (!email.equals("")){
                                allEmails+=email+",";
                            }
                        }
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("fail","Could not get admins documents");
                    }
                });
    }

    public void mergeAdminDoctors(){

        fStore.collection("Doctor").whereEqualTo("IsActive","true").whereEqualTo("TypeOfDoctor",typeOfDoctor)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshots=queryDocumentSnapshots.getDocuments();
                        for ( DocumentSnapshot documents : snapshots){
                            String id=documents.getId();
                            ls.add(id);
                            String email=documents.getString("Email");
                            if (!email.equals("")){
                                allEmails+=email+",";
                            }
                        }
                        /*for(int i=0;i<7;i++){
                            Log.i("Merge",ls.get(i));
                        }*/


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("fail","Could not get doctors documents");
            }
        });
    }

    public void sendEmails(final String Name, final String phoneNumber, final String Symptoms, final String age){

        int l=allEmails.lastIndexOf(',');
        final String recipients=allEmails.substring(0,l);
        Log.i("Email",recipients);

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    EmailSender sender = new EmailSender("telesevak.developer@gmail.com",
                            "Sevak123");
                    sender.sendMail("New Patient Added!",
                            "Hi"+"\n"+
                                    "\t"+"A new Patient just submitted the form. Details are given below."+"\n"+"\n"
                                    +"Patient Name: "+Name+"\n"+
                                    "Patient Phone Number: "+phoneNumber+"\n"+
                                    "Age: "+age+"\n"+
                                    "Symptoms: "+Symptoms+"\n"+"\n"+
                                    "\t"+"Please make sure to accept the patient request on The TeleSevak App and call them as soon as possible. "+"\n"+
                                    "\n"+"Regards,"
                                    +"\n"+"Team TeleSevak",
                            "telesevak.developer@gmail.com",
                            recipients);
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }

        }).start();
    }

    public void sendSMS(String Name,String Phone){
        for(int j=0;j<ls.size();j++){

            String body = "New Patient Request: " +
                    " Patient Name: "+Name+
                    " Patient Number: "+Phone;

            //String from = "+17633258036";
            String to = ls.get(j);

            String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                    ( ACCOUNT_SID +":"+ AUTH_TOKEN).getBytes(), Base64.NO_WRAP
            );
            Log.i("CRED",base64EncodedCredentials);

            Map<String, String> data = new HashMap<>();
            data.put("From", from);
            data.put("To", to);
            data.put("Body", body);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.twilio.com/2010-04-01/")
                    .build();
            AddPatiant.TwilioApi api = retrofit.create(AddPatiant.TwilioApi.class);

            api.sendMessage(ACCOUNT_SID, base64EncodedCredentials, data).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) Log.d("TAG", "onResponse->success");
                    else Log.d("TAG", "onResponse->failure");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("TAG", "onFailure");
                }
            });



        }

    }

    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
    }

    @Override
    protected void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }

        super.onPause();
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }


    @Override
    public void onStarted() {
        openPlaceCallActivity();
    }

    private void openPlaceCallActivity() {

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        Date date = calendar.getTime();

        int hr=calendar.get(Calendar.HOUR_OF_DAY);
        // 3 letter name form of the day
        String Day=new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime());
        Log.i("DAY",Day+" "+hr);
        if(Day.equals("Sun")){
            Log.i("DAY","Sat or Sun");
            startActivity(new Intent(getApplicationContext(),DoctorNotAvailable.class));

        }else
        {
            if (hr>=9&&hr<18){
                Log.i("HOUR",String.valueOf(hr));
                Intent intent = new Intent(FormAfterPayment.this, Doctowillcallyou.class);
                startActivity(intent);
                //status();
            }else {

                startActivity(new Intent(getApplicationContext(),DoctorNotAvailable.class));
            }
        }


    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }

    public void token(){
        token=FirebaseInstanceId.getInstance().getToken();
        fStore.collection("Consultation").document(id_consult)
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(),SpecialDoctors.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(0,0);
        startActivity(intent);
    }
}
