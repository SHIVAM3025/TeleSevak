package app.sagar.telesevek.uploadpkg;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.font.TextAttribute;

import app.sagar.telesevek.Doctowillcallyou;
import app.sagar.telesevek.Models.Doctor;
import app.sagar.telesevek.Models.FirebaseUserModel;
import app.sagar.telesevek.PastCounsulation;
import app.sagar.telesevek.R;
import app.sagar.telesevek.ScratchCardNew;
import cz.msebera.android.httpclient.HttpHeaders;
import cz.msebera.android.httpclient.entity.StringEntity;

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
    String result="";
    String pName;
    String fullName;
    boolean prescriptionUploaded;

    Doctor user = Doctor.getInstance();
    FirebaseDatabase database;
    DatabaseReference usersRef;

    private static final String TAG = "AddPatiant";
    JSONArray registration_ids = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Doctor");

        SharedPreferences sharedpreferences = getSharedPreferences(user.appPreferences, Context.MODE_PRIVATE);
        user.sharedpreferences = sharedpreferences;

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

        pd = new ProgressDialog(ShowImageActivity.this);
        pd.setMessage("loading..");

            downloadedIV = findViewById(R.id.downloadImage);
            tvDate=findViewById(R.id.tvDate);
            tvDoctor=findViewById(R.id.tvDoctor);
            tvPatient=findViewById(R.id.tvPatient);
            tvSymptoms=findViewById(R.id.tvSymptoms);
            pb=findViewById(R.id.pbImageLoading);

            fStore = FirebaseFirestore.getInstance();
            firebaseStorage=FirebaseStorage.getInstance();
            pd.show();
            if(DocID!=null){
                objectDocumentReference=fStore.collection("Consultation").document(DocID);

                objectDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                         pName= (String) documentSnapshot.get("PName");
                        String dName=(String)documentSnapshot.get("DoctorName");
                        String date=(String) documentSnapshot.get("Time");
                        String Symptoms= (String) documentSnapshot.get("Symtoms");

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




                        String linkOfImage = documentSnapshot.getString("url");
                        assert linkOfImage != null;
                        if(!linkOfImage.equals("")) {
                            //pb.setIndeterminate(true);
                            prescriptionUploaded=true;
                            result = getImageName(linkOfImage);
                            //String desc = documentSnapshot.getString("urldescription");
                            //nameOfImageET.setText(desc);
                            Glide.with(ShowImageActivity.this)
                                    .load(linkOfImage)
                                    .into(downloadedIV);
                            pb.setVisibility(View.GONE);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"No Prescription has been uploaded",Toast.LENGTH_LONG).show();
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

            findViewById(R.id.btDownload).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    download();
                }
            });

            findViewById(R.id.btFollowUp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DocumentReference consultitem = fStore.collection("Consultation").document(DocID);
                    consultitem.update("TypeOfConsultation", "Followup")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(getApplicationContext(), "Follow up Successfully", Toast.LENGTH_SHORT).show();

                                    fullName = pName;

                                    notification();
                                    startActivity(new Intent(getApplicationContext(), Doctowillcallyou.class));

                                }

                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Plase Check after some time", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });
                }
            });


    }

    public void download(){

        if(!prescriptionUploaded){
            Toast.makeText(getApplicationContext(),"Could Not Download. Prescription has not been uploaded.",Toast.LENGTH_LONG).show();
        }
        else {

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
                            downloadfiles(getApplicationContext(), "Prescription", ".jpg", DIRECTORY_DOWNLOADS, url);
                            Toast.makeText(ShowImageActivity.this, "Downloaded Successfully!", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ShowImageActivity.this, "Could not Download. Please check your internet ", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void downloadfiles(Context context,String fileName, String fileExtension, String destinationDirectory,String url){

        DownloadManager downloadManager=(DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri=Uri.parse(url);
        DownloadManager.Request request=new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,destinationDirectory,fileName+fileExtension);
        downloadManager.enqueue(request);
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
                notificationObject.put("body", "Followup Name is"+fullName);
                notificationObject.put("title", "New Followup");

                params.put("notification", notificationObject);

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
                        Toast.makeText(getApplicationContext(), "Send Notification", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {

            }


        }
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


}