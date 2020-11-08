package app.sagar.telesevek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PatientFullImageShow extends AppCompatActivity {
    ImageView downloadedIV;
    String pidimage;
    FirebaseFirestore objectFirebaseFirestore;
    DocumentReference objectDocumentReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_full_image_show);

        try {
            downloadedIV = findViewById(R.id.downloadImage);

            Glide.with(this).load(R.raw.gif).into(downloadedIV);

            objectFirebaseFirestore = FirebaseFirestore.getInstance();
        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        SharedPreferences prefs = getSharedPreferences("Image", MODE_PRIVATE);
        pidimage = prefs.getString("pimageid", null);
        try {
            if (!pidimage.isEmpty()) {
                objectDocumentReference = objectFirebaseFirestore.collection("Accept")
                        .document(pidimage);

                objectDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String linkOfImage = documentSnapshot.getString("url");
                        Glide.with(PatientFullImageShow.this)
                                .load(linkOfImage)
                                .into(downloadedIV);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PatientFullImageShow.this, "Failed to get image", Toast.LENGTH_SHORT).show();
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
    }



}