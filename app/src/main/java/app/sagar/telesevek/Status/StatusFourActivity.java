package app.sagar.telesevek.Status;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import app.sagar.telesevek.Buycard;
import app.sagar.telesevek.OurDoctor;
import app.sagar.telesevek.PhoneAuthConsulation.MainActivity;
import app.sagar.telesevek.R;
import app.sagar.telesevek.ScratchCardNew;

public class StatusFourActivity extends AppCompatActivity {
    Button past;
    Button buy;
    Button our;
    Button consult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_four);


        BottomNavigationView bottomNav=findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.consultDoctor);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.pastConsult:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.buyCard:
                        startActivity(new Intent(getApplicationContext(),Buycard.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.ourDoctors:
                        startActivity(new Intent(getApplicationContext(),OurDoctor.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.consultDoctor:
                        return true;
                }
                return false;
            }
        });
        /*
        past = findViewById(R.id.Past);
        past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent chemistinten = new Intent(StatusFourActivity.this, MainActivity.class);
                startActivity(chemistinten);
            }
        });
        buy = findViewById(R.id.card);
        buy.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent chemistinten = new Intent(StatusFourActivity.this, Buycard.class);
                        startActivity(chemistinten);
                    }
                });

        our = findViewById(R.id.odoctor);
        our.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent chemistinten = new Intent(StatusFourActivity.this, OurDoctor.class);
                        startActivity(chemistinten);
                    }
                });

        consult = findViewById(R.id.consult);
        consult.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent chemistinten = new Intent(StatusFourActivity.this, ScratchCardNew.class);
                        startActivity(chemistinten);
                    }
                });
        */
    }
}