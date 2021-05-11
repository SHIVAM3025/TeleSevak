package app.telesevek.uploadpkg;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.telesevek.OurDoctor;
import app.telesevek.R;
import app.telesevek.SendSMSnoPhone;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class UploadImageTeSTED extends AppCompatActivity {

    ImageView uploadPicIV;
    EditText uploadPicET;

    final int IMAGE_REQUST = 71;
    Uri imageLocationPath;

    StorageReference objectStorageReference;
    FirebaseFirestore objectFirebaseFirestore;
    ProgressDialog pd;
    String Docuidi;
    String Patientphone;
    String id;
    public static final String ACCOUNT_SID = "";
    public static final String AUTH_TOKEN = "";
    List<String> ls=new ArrayList<>();
    Button Nophone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadimage);
        pd = new ProgressDialog(UploadImageTeSTED.this);
        pd.setMessage("loading..");
        uploadPicET = findViewById(R.id.imageNameET);
        uploadPicIV = findViewById(R.id.imageID);
        Nophone = findViewById(R.id.SENDSMS);

        objectStorageReference = FirebaseStorage.getInstance().getReference("ImageFolder");
        objectFirebaseFirestore = FirebaseFirestore.getInstance();


      /*  Bundle bundle = getIntent().getExtras();
        Docuidi = bundle.getString("DocuId");
        Patientphone = bundle.getString("patientphone");
        ls.add(Patientphone);*/
       /* Nophone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendStuff = new Intent(UploadImageTeSTED.this, SendSMSnoPhone.class);
                sendStuff.putExtra("DocuId", Docuidi);
                sendStuff.putExtra("patientphone", Patientphone);
                startActivity(sendStuff);
            }
        });*/
    }

    public void selectImage(View view) {
        try {
            Intent objectIntent = new Intent();
            objectIntent.setType("image/*");

            objectIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(objectIntent, IMAGE_REQUST);
        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == IMAGE_REQUST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                imageLocationPath =data.getData();
                Bitmap objectBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageLocationPath);

                uploadPicIV.setImageBitmap(objectBitmap);
            }
        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void uploadImage(View view) {
        pd.show();
        try {
            if (imageLocationPath != null && uploadPicET.getText().toString() != null) {
                id = System.currentTimeMillis() + "." + getExtension(imageLocationPath);
                final StorageReference imageRef = objectStorageReference.child(id);

                UploadTask objectUploadTask = imageRef.putFile(imageLocationPath);
                objectUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return imageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                           /* Map<String, String> objectMap = new HashMap<>();
                            objectMap.u("url", task.getResult().toString());*/

                            objectFirebaseFirestore.collection("Doctor").document("+917020839159")
                                    .update("url",task.getResult().toString())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                           Intent go  = new Intent(UploadImageTeSTED.this, OurDoctor.class);
                                           startActivity(go);
                                            pd.dismiss();


                                      }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadImageTeSTED.this, "Image not upload" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    pd.dismiss();
                                }
                            });




                        }
                        else {
                            Toast.makeText(UploadImageTeSTED.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else {
                Toast.makeText(this, "Please provide name for image", Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String getExtension(Uri uri) {
        try {
            ContentResolver objectContentResolver = getContentResolver();
            MimeTypeMap objectMimeTypeMap = MimeTypeMap.getSingleton();

            return objectMimeTypeMap.getExtensionFromMimeType(objectContentResolver.getType(uri));
        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public void moveToShowImageAct(View view) {
        try {
            startActivity(new Intent(this, ShowImageActivity.class));
        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    /*public void sendSMS(String Name,String Phone){


        for(int j=0;j<ls.size();j++){

            String body = "Doctor Prescription the:"+Name;
            String from = "+17633258036";
            String to =  ls.get(j);

            String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                    (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP
            );

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

    }*/

    public interface TwilioApi {
        @FormUrlEncoded
        @POST("Accounts/{ACCOUNT_SID}/Messages")
        Call<ResponseBody> sendMessage(
                @Path("ACCOUNT_SID") String accountSId,
                @Header("Authorization") String signature,
                @FieldMap Map<String, String> metadata
        );
    }

}
