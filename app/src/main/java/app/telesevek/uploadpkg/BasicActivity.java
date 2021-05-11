package app.telesevek.uploadpkg;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import javax.annotation.Nullable;

import app.telesevek.R;

public class BasicActivity extends AppCompatActivity {
    WebView pdfview;
    FirebaseFirestore fStore;
    FloatingActionButton bt;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);

        fStore = FirebaseFirestore.getInstance();

        pdfview=(WebView)findViewById(R.id.viewpdf);
        pdfview.getSettings().setJavaScriptEnabled(true);
        bt = (FloatingActionButton)findViewById(R.id.add_fab);


        String ci=getIntent().getStringExtra("CID");

        if (ci != null) {
            DocumentReference documentReferencefollow = fStore.collection("Consultation").document(ci);
            documentReferencefollow.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        String fileurl = documentSnapshot.getString("urlpdf");

                        bt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(fileurl));
                                startActivity(intent);
                            }
                        });



                      showpdf(fileurl);


                    } else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });

        }





    }
    public  void showpdf(String fileurl){
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setTitle("Pdf");
        pd.setMessage("Opening....!!!");


        pdfview.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pd.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pd.dismiss();
            }
        });

        String url="";
        try {
            url= URLEncoder.encode(fileurl,"UTF-8");
        }catch (Exception ex)
        {}

        pdfview.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);

    }



}