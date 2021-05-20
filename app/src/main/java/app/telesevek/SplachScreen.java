package app.telesevek;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SplachScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splach_screen);

        //Setting Hindi By default

        Locale.setDefault(new Locale("hi"));
        Configuration configuration = new Configuration();
        configuration.locale = new Locale("hi");
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        //Handler for 2 seconds

        new Handler().postDelayed(() -> {
            Intent go = new Intent(SplachScreen.this, LanguageActivity.class);
            startActivity(go);
            finish();
        }, 2000);


    }
}