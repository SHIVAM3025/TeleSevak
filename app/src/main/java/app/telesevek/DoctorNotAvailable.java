package app.telesevek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import app.telesevek.NewSceen.HomePatient;

public class DoctorNotAvailable extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_not_available);


        BottomNavigationView bottomNav=findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.consultDoctor);

        findViewById(R.id.btnHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), HomePatient.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                overridePendingTransition(0,0);
                startActivity(intent);
            }
        });

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.pastConsult:
                        startActivity(new Intent(getApplicationContext(),PastConsultationNewLoginScreen.class));
                        overridePendingTransition(0,0);
                        return true;

                    /*case R.id.buyCard:
                        startActivity(new Intent(getApplicationContext(),Buycard.class));
                        overridePendingTransition(0,0);
                        return true;*/

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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent=new Intent(getApplicationContext(),ScratchCardNew.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(0,0);
        startActivity(intent);
    }
}