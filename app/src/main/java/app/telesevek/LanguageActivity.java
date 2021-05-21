package app.telesevek;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {

    private Button hindi, english;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        hindi = findViewById(R.id.hindi);
        english = findViewById(R.id.english);

        hindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferencesEdit("hi");
                changeIntent();
            }
        });

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferencesEdit("en");
                changeIntent();
            }
        });

    }

    private void sharedPreferencesEdit(String langauge) {
        Locale.setDefault(new Locale(langauge));
        Configuration configuration = new Configuration();
        configuration.locale = new Locale(langauge);
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("Language", MODE_PRIVATE).edit();
        editor.putString("My_Lang", langauge);
        editor.apply();
    }

    private void changeIntent() {

        Intent intent = new Intent(this, LogindcActivity.class);
        startActivity(intent);
        finish();

    }


}