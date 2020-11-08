package app.sagar.telesevek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

public class SplachScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splach_screen);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               Intent go = new Intent(SplachScreen.this,LogindcActivity.class);
               startActivity(go);
            }
        }, 2000);
    }
}