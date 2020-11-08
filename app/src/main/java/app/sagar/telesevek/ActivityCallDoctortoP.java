package app.sagar.telesevek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import app.sagar.telesevek.uploadpkg.ShowImageActivity;
import app.sagar.telesevek.uploadpkg.UploadImage;

public class ActivityCallDoctortoP extends AppCompatActivity {
    String PatientPassId;
    String Patientcard;
    String Pname;
    String Symtoms;
    String itemid;
    String consultitemid;
    TextView pname;
    TextView PSymtoms;
    String phone;
    FirebaseFirestore fStore;
    Button audio;
    Button video;

    ImageView uploadPicIV;

    final int IMAGE_REQUST = 71;
    Uri imageLocationPath;

    StorageReference objectStorageReference;
    FirebaseFirestore objectFirebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_doctorto_p);

        pname = findViewById(R.id.D_degree);
        PSymtoms = findViewById(R.id.D_address);

        audio = findViewById(R.id.audio);
        video = findViewById(R.id.video);
        fStore = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        PatientPassId = bundle.getString("PatientPassId");
        Patientcard = bundle.getString("PatientCard");
        Pname = bundle.getString("Pname");
        Symtoms = bundle.getString("Symtoms");
        itemid = bundle.getString("itemid");
        consultitemid = bundle.getString("consultitemid");

      /*  DocumentReference documentReference2 = fStore.collection("Accept").document(PatientPassId);
        documentReference2.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    pname.setText(documentSnapshot.getString("PatientName"));
                    PSymtoms.setText(documentSnapshot.getString("PatientSymptoms"));
                    phone = documentSnapshot.getString("PatientPhoneNumber");
                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });*/

        pname.setText(Pname);
        PSymtoms.setText(Symtoms);

        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+ PatientPassId));
                startActivity(intent);

                DocumentReference ststusup = fStore.collection("ScratchCard").document(Patientcard);
                ststusup.update("Status","2");
                ststusup.update("ConsultationID",consultitemid)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ActivityCallDoctortoP.this, "changed status", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


                DocumentReference consult = fStore.collection("Patient").document(itemid);
                consult.update("Status","2")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ActivityCallDoctortoP.this, "wrong", Toast.LENGTH_SHORT).show();
                    }
                });


                DocumentReference consultitem = fStore.collection("Consultation").document(consultitemid);
                consultitem.update("Status","2")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ActivityCallDoctortoP.this, "wrong", Toast.LENGTH_SHORT).show();
                    }
                });







            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(ActivityCallDoctortoP.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Telesevek App under Developmemt")
                        .setMessage("App is Under Construction")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setNegativeButton("close", null).show();


            }
        });


        uploadPicIV = findViewById(R.id.imageID);

        objectStorageReference = FirebaseStorage.getInstance().getReference("ImageFolder");
        objectFirebaseFirestore = FirebaseFirestore.getInstance();

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
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
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
        try {
            if (!phone.isEmpty() && imageLocationPath != null) {
                String nameOfImage = phone + "." + getExtension(imageLocationPath);
                final StorageReference imageRef = objectStorageReference.child(nameOfImage);

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
                            Map<String, String> objectMap = new HashMap<>();
                            objectMap.put("url", task.getResult().toString());

                            objectFirebaseFirestore.collection("Accept").document(PatientPassId)
                                    .set(objectMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ActivityCallDoctortoP.this, "Image is uploaded", Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ActivityCallDoctortoP.this, "Image not upload" + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else {
                            Toast.makeText(ActivityCallDoctortoP.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else {
                Toast.makeText(this, "Please provide name for image", Toast.LENGTH_LONG).show();
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

    }
