package app.sagar.telesevek.uploadpkg;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.awt.font.TextAttribute;

import app.sagar.telesevek.R;
import app.sagar.telesevek.ScratchCardNew;

public class ShowImageActivity extends AppCompatActivity {

    TextView nameOfImageET;
    ImageView downloadedIV;

    FirebaseFirestore objectFirebaseFirestore;
    DocumentReference objectDocumentReference;
    ProgressDialog pd;
    String Docuidi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        Bundle bundle = getIntent().getExtras();
        Docuidi = bundle.getString("DocuId");

        pd = new ProgressDialog(ShowImageActivity.this);
        pd.setMessage("loading..");

        try {
            nameOfImageET = findViewById(R.id.desctext);
            downloadedIV = findViewById(R.id.downloadImage);

            objectFirebaseFirestore = FirebaseFirestore.getInstance();
        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void downloadImage(View view) {
        pd.show();
        try {
            if (Docuidi != null) {
                objectDocumentReference = objectFirebaseFirestore.collection("Consultation")
                        .document(Docuidi);

                objectDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String linkOfImage = documentSnapshot.getString("url");
                        String desc = documentSnapshot.getString("urldescription");
                        nameOfImageET.setText(desc);
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
    }
}