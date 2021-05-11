package app.telesevek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NoOldParamarsh extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_old_paramarsh);

      /*  findViewById(R.id.btStartNewConsult).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getApplicationContext(),ScratchCardNew.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                overridePendingTransition(0,0);
                startActivity(intent);
            }
        });*/


        BottomNavigationView bottomNav=findViewById(R.id.bottomNavPastLoginNew);
        bottomNav.setSelectedItemId(R.id.pastConsult);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.pastConsult:
                        return true;

                   /* case R.id.buyCard:
                        startActivity(new Intent(getApplicationContext(),Buycard.class));
                        overridePendingTransition(0,0);
                        return true;*/

                    case R.id.ourDoctors:
                        startActivity(new Intent(getApplicationContext(),OurDoctor.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.consultDoctor:
                        startActivity(new Intent(getApplicationContext(),ScratchCardNew.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }
}