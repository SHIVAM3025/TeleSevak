package app.telesevek.uploadpkg;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.telesevek.AddPatiant;
import app.telesevek.Doctowillcallyou;
import app.telesevek.EmailSender;
import app.telesevek.Models.Doctor;
import app.telesevek.Models.FirebaseUserModel;

import app.telesevek.PatientFullImageShow;
import app.telesevek.R;
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

import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class ShowImageActivity extends AppCompatActivity {

    TextView tvPatient;
    TextView tvDoctor;
    TextView tvDate;
    TextView tvSymptoms;

    ImageView downloadedIV;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore fStore;
    DocumentReference objectDocumentReference;
    ProgressDialog pd;
    ProgressBar pb;
    String DocID;
    String Docuphone;
    int remain;
    String CARD;
    String result="";
    String pName;
    String fullName;
    String DoctorPhone;
    String dName;
    String patientPhone;
    String Symptoms;
    String DrPhone;
    boolean prescriptionUploaded;

    Doctor user = Doctor.getInstance();
    FirebaseDatabase database;
    DatabaseReference usersRef;
    Button followup;
    private static final String TAG = "AddPatiant";
    JSONArray registration_ids = new JSONArray();

    List<String> ls=new ArrayList<>();
    String allEmails="";

    public static String account_sid = "account_sid";
    public static String auth_token = "auth_token";
    public static String fromNumber="from";

    public static String ACCOUNT_SID ;
    public static String AUTH_TOKEN ;
    public static String from;

   Button imagepbutton;
   /*Button showpdf;*/
    String linkOfImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        /*ls.add("+917054466515");
        ls.add("+918840974859");
        ls.add("+919599225823");*/

        receiveCredentials();
        DoctorPhone = "123";

        followup = findViewById(R.id.btFollowUp);
        imagepbutton = findViewById(R.id.sendtodoctor);
     /*   showpdf = findViewById(R.id.showpdf);*/



        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Doctor");

        SharedPreferences sharedpreferences = getSharedPreferences(user.appPreferences, Context.MODE_PRIVATE);
        user.sharedpreferences = sharedpreferences;

        final ProgressDialog Dialog = new ProgressDialog(this);
        Dialog.setMessage("Please wait..");
        Dialog.setCancelable(false);
        Dialog.show();

        /*final FirebaseRemoteConfig remoteConfig=FirebaseRemoteConfig.getInstance();
        final String idOf=remoteConfig.getString(ACCOUNT_SID);
        String token=remoteConfig.getString(AUTH_TOKEN);
        Toast.makeText(getApplicationContext(), idOf+":-"+token, Toast.LENGTH_SHORT).show();*/

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


                    final com.google.firebase.database.ValueEventListener userValueEventListener = new com.google.firebase.database.ValueEventListener() {
                        @Override
                        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                            registration_ids = new JSONArray();

                            for (com.google.firebase.database.DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                System.out.println("Child: " + postSnapshot);
                                //Getting the data from snapshot
                                FirebaseUserModel firebaseUserModel = postSnapshot.getValue(FirebaseUserModel.class);
                                if (!firebaseUserModel.getDeviceToken().isEmpty()) {
                                    registration_ids.put(firebaseUserModel.getDeviceToken());
                                }
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            registration_ids = new JSONArray();


                            System.out.println("The read failed: " + databaseError.getMessage());
                        }
                    };

                    usersRef.addValueEventListener(userValueEventListener);



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Dialog.dismiss();
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });

        Bundle bundle = getIntent().getExtras();
        DocID = bundle.getString("DocuId");
        remain = bundle.getInt("remainconsult");
        CARD = bundle.getString("cardpass");
        Docuphone = bundle.getString("Docuphone");

      /*  showpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),BasicActivity.class);
                intent.putExtra("CID",DocID);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });*/

        if (0 == remain){
            followup.setVisibility(View.GONE);
        }
        fStore = FirebaseFirestore.getInstance();
        if (DocID != null){
            DocumentReference documentReference2 = fStore.collection("Consultation").document(DocID);
            documentReference2.addSnapshotListener(ShowImageActivity.this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot.exists()){


                        final String URL = documentSnapshot.getString("urlupatient");
                        if (URL != null){
                            imagepbutton.setVisibility(View.VISIBLE);
                            imagepbutton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent sendStuff = new Intent(ShowImageActivity.this, UploadImageFromPatient.class);
                                    sendStuff.putExtra("ConsultId", DocID);
                                    sendStuff.putExtra("Docuphone", Docuphone);
                                    startActivity(sendStuff);

                                }
                            });
                        }
                        else {
                            imagepbutton.setVisibility(View.VISIBLE);
                            Toast.makeText(ShowImageActivity.this, "please update app or new consulation ", Toast.LENGTH_SHORT).show();
                        }


                    }else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });
        }



        pd = new ProgressDialog(ShowImageActivity.this);
        pd.setMessage("loading..");

            downloadedIV = findViewById(R.id.downloadImage);
            tvDate=findViewById(R.id.tvDate);
            tvDoctor=findViewById(R.id.tvDoctor);
            tvPatient=findViewById(R.id.tvPatient);
            tvSymptoms=findViewById(R.id.tvSymptoms);
            pb=findViewById(R.id.pbImageLoading);


            firebaseStorage=FirebaseStorage.getInstance();
            pd.show();
            if(DocID!=null){
                objectDocumentReference=fStore.collection("Consultation").document(DocID);

                objectDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                         pName= (String) documentSnapshot.get("PName");
                         dName=(String)documentSnapshot.get("DoctorName");
                        String date=(String) documentSnapshot.get("Time");
                        Symptoms= (String) documentSnapshot.get("Symtoms");
                        patientPhone=documentSnapshot.getString("PatientPhone");
                        DoctorPhone=documentSnapshot.getString("DoctorId");

                        //adding doctor and admins
                        ls.add(DoctorPhone);
                        addAdmins();
                        addDoctor();

                        if(pName!=null){
                            tvPatient.setText(pName);
                        }
                        if(date!=null){
                            tvDate.setText(date);
                        }
                        if(dName!=null){
                            tvDoctor.setText(dName);
                        }
                        if(Symptoms!=null){
                            tvSymptoms.setText(Symptoms);
                        }




                        linkOfImage = documentSnapshot.getString("url");
                         if(linkOfImage !=null) {
                            //pb.setIndeterminate(true);
                            prescriptionUploaded=true;
                            /*result = getImageName(linkOfImage);*/
                            //String desc = documentSnapshot.getString("urldescription");
                            //nameOfImageET.setText(desc);
                            Glide.with(ShowImageActivity.this)
                                    .load(linkOfImage)
                                    .into(downloadedIV);
                            pb.setVisibility(View.GONE);

                            downloadedIV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent sendStuff = new Intent(ShowImageActivity.this, PatientFullImageShow.class);
                                    sendStuff.putExtra("url",linkOfImage);
                                    startActivity(sendStuff);
                                }
                            });
                        }
                        else {
                            prescriptionUploaded=false;
                            downloadedIV.setImageDrawable(getDrawable(R.drawable.ic_documentation_new_new));
                            pb.setVisibility(View.GONE);
                        }
                        pd.dismiss();



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getApplicationContext(), "Some error has occurred in loading", Toast.LENGTH_SHORT).show();

                        pd.dismiss();
                    }
                });


            }





           /* findViewById(R.id.btDownload).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    download();
                }
            });*/

            findViewById(R.id.btDownloadImage).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    download();
                }
            });

        followup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DocumentReference consultitem = fStore.collection("Consultation").document(DocID);
                    consultitem.update("TypeOfConsultation", "Primary")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(getApplicationContext(), "Follow up Successfully", Toast.LENGTH_SHORT).show();

                                    DocumentReference ststusup = fStore.collection("ScratchCard").document(CARD);
                                    ststusup.update("Code",CARD);
                                    ststusup.update("RemainingConsultations",remain)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "onSuccess: user Profile is created for "+ CARD);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.toString());
                                        }
                                    });

                                    fullName = pName;


                                    notification();
                                    sendEmails(pName,patientPhone,Symptoms,dName,DoctorPhone);
                                    sendSMS(pName,patientPhone);

                                    startActivity(new Intent(getApplicationContext(), Doctowillcallyou.class));

                                }

                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Please Check after some time", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });
                }
            });


    }

    public void download(){



        if(linkOfImage != null){
            downloadfiles(getApplicationContext(), "Prescription", ".jpg", DIRECTORY_DOWNLOADS, linkOfImage);
        }
        else {
            Toast.makeText(getApplicationContext(),"Could Not Download. Prescription has not been uploaded.",Toast.LENGTH_LONG).show();

/*
            Toast.makeText(getApplicationContext(), "Downloading", Toast.LENGTH_SHORT).show();
            StorageReference storageReference = firebaseStorage.getReference();
            Log.i("url", result);

            storageReference.child("ImageFolder")
                    .child(result + ".jpg")
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                             Toast.makeText(ShowImageActivity.this, "Downloaded Successfully!", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ShowImageActivity.this, "Could not Download. Please check your internet ", Toast.LENGTH_SHORT).show();
                }
            });*/
        }

    }

    public void downloadfiles(Context context,String fileName, String fileExtension, String destinationDirectory,String url){

        DownloadManager downloadManager=(DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri=Uri.parse(url);
        DownloadManager.Request request=new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,destinationDirectory,fileName+fileExtension);
        downloadManager.enqueue(request);

        Toast.makeText(this, "प्रिस्क्रिप्शन  डाउनलोड हो गया है", Toast.LENGTH_SHORT).show();
    }

    public String getImageName(String url){
        String s="";
        int l= url.indexOf(".jpg");
        s=url.substring(l-20,l);
        return s;
    }


    public void notification(){
        if (registration_ids.length() > 0) {

            String url = "https://fcm.googleapis.com/fcm/send";
            AsyncHttpClient client = new AsyncHttpClient();

            client.addHeader(HttpHeaders.AUTHORIZATION, "key=AAAAIz3KQd8:APA91bFJiG-094nuzkfO0xhkCoeCx6GQQv6nOoKrOc52za0afjY66dENqplOcke5zdJE7yrMBkKR_byfMWlcf3M4-GaSS2BlFv2HCvcT-ON8YIDdEQ6dC_rAOVjCyhi8T9Qo2WG2GVIo");
            client.addHeader(HttpHeaders.CONTENT_TYPE, RequestParams.APPLICATION_JSON);

            try {
                JSONObject params = new JSONObject();

                params.put("registration_ids", registration_ids);

                JSONObject notificationObject = new JSONObject();
                notificationObject.put("body", "Follow-up Name is "+fullName);
                notificationObject.put("title", "New Follow-up");


                params.put("notification", notificationObject);
                JSONObject dataObject=new JSONObject();
                dataObject.put("title","FollowUp");
                params.put("data",dataObject);

                StringEntity entity = new StringEntity(params.toString());

                client.post(getApplicationContext(), url, entity, RequestParams.APPLICATION_JSON, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                        Log.i(TAG, responseString);
                        Toast.makeText(getApplicationContext(), "failed Notification", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                        Log.i(TAG, responseString);
                        Toast.makeText(getApplicationContext(), "Notification sent!", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {

            }


        }
    }



    public void sendSMS(String Name,String Phone){

        Toast.makeText(this, "SMS sent!", Toast.LENGTH_SHORT).show();
        for(int j=0;j<ls.size();j++){

            String body = "Follow Up Request to "+dName +
                    ":  Patient Name: "+Name+" Patient Number: "+Phone;
            String from = "+17633258036";
            String to = ls.get(j);

            String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                    ( ACCOUNT_SID +":"+ AUTH_TOKEN).getBytes(), Base64.NO_WRAP
            );

            Map<String, String> data = new HashMap<>();
            data.put("From", from);
            data.put("To", to);
            data.put("Body", body);
            data.put("locale","hi");

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.twilio.com/2010-04-01/")
                    .build();
            AddPatiant.TwilioApi api = retrofit.create(AddPatiant.TwilioApi.class);

            api.sendMessage(ACCOUNT_SID, base64EncodedCredentials, data).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) Log.d("TAG", "onResponse->success");
                    else {Log.d("TAG", "onResponse->failure");
                    Log.e("TAG",response.toString());}
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("TAG", "onFailure");
                }
            });



        }

    }

    public void sendEmails(final String Name, final String phoneNumber, final String Symptoms, final String DoctorName,final String DoctorPhone){

        int l=allEmails.lastIndexOf(',');
        final String recipients=allEmails.substring(0,l);
        Log.i("Email",recipients);

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    EmailSender sender = new EmailSender("telesevak.developer@gmail.com",
                            "Sevak123");
                    sender.sendMail("Follow-up Request from Patient!",
                            "Hi"+"\n"+
                                    "\t"+"Your Patient just requested a follow-up Consultation. Details are given below."+"\n"+"\n"
                                    +"Patient Name: "+Name+"\n"+
                                    "Patient Phone Number: "+phoneNumber+"\n"+
                                    "Symptoms: "+Symptoms+"\n"+"\n"+
                                    "Doctor Name: "+DoctorName+"\n"+
                                    "Doctor Phone Number: "+DoctorPhone+"\n"+"\n"+
                                    "\t"+"Please make sure to accept the patient Follow-Up request on The TeleSevak App and call them as soon as possible. "+"\n"+
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

    public void addDoctor(){
        if (!DoctorPhone.isEmpty()) {
            DocumentReference reference = fStore.collection("Doctor").document(DoctorPhone);
            reference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    assert value != null;
                    if (value.exists()) {
                        String email = value.getString("Email");
                        /*   Log.i("Email",email);*/
                   /* if(!email.equals("")){
                        allEmails+=email+",";
                    }*/
                        /*Log.i("Email",allEmails);*/
                    } else {
                        Log.i("fail", "Could not get email");
                    }
                }
            });
        }
    }

    interface TwilioApi {
        @FormUrlEncoded
        @POST("Accounts/{ACCOUNT_SID}/Messages")
        Call<ResponseBody> sendMessage(
                @Path("ACCOUNT_SID") String accountSId,
                @Header("Authorization") String signature,
                @FieldMap Map<String, String> metadata
        );
    }

    /*public void downloadImage(View view) {
        pd.show();
        try {
            if (DocID != null) {
                objectDocumentReference = fStore.collection("Consultation")
                        .document(DocID);

                objectDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String linkOfImage = documentSnapshot.getString("url");
                        //String desc = documentSnapshot.getString("urldescription");
                        //nameOfImageET.setText(desc);
                        Glide.with(ShowImageActivity.this)
                                .load(linkOfImage)
                                .into(downloadedIV);
                        pd.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShowImageActivity.this, "Failed to get image", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
            }
            else {
                Toast.makeText(this, "Please enter image name", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }*/


    public static void receiveCredentials(){

        final FirebaseRemoteConfig remoteConfig= FirebaseRemoteConfig.getInstance();
        ACCOUNT_SID=remoteConfig.getString(account_sid);
        AUTH_TOKEN=remoteConfig.getString(auth_token);
        from=remoteConfig.getString(fromNumber);
        Log.i("remote",ACCOUNT_SID+"="+AUTH_TOKEN+"from"+from);
        //Toast.makeText(this, idOf, Toast.LENGTH_SHORT).show();
        remoteConfig.fetch(120)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            remoteConfig.fetchAndActivate();

                            //Toast.makeText(LogindcActivity.this, idOf, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}