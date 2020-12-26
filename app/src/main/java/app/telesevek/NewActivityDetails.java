package app.telesevek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NewActivityDetails extends AppCompatActivity {

    Button newadd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_details);

        final String card = getIntent().getStringExtra("cardpass");
        final int remainconsult = getIntent().getIntExtra("remainconsult",0);
        String price = getIntent().getStringExtra("cardprice");
        String purane = getIntent().getStringExtra("puranecard");
        String date = getIntent().getStringExtra("validdate");

        int remainconsultall = getIntent().getIntExtra("remainconsultall",0);

        TextView card_txt = findViewById(R.id.carnumber);
        TextView price_txt = findViewById(R.id.pricenumber);
        TextView remainconust_text = findViewById(R.id.rmain);
        TextView puranecon_text = findViewById(R.id.puanecon);
        TextView vallid_txt = findViewById(R.id.validdate);

        card_txt.setText(card);
        price_txt.setText(price);
        puranecon_text.setText(purane);
        vallid_txt.setText(date);
        remainconust_text.setText(String.valueOf(remainconsultall));


        newadd = findViewById(R.id.submit);
        newadd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Intent sendStuff = new Intent(NewActivityDetails.this, AddPatiant.class);
                        sendStuff.putExtra("cardpass", card);
                        sendStuff.putExtra("remainconsult", remainconsult);
                        startActivity(sendStuff);
                    }
                });


        BottomNavigationView bottomNav=findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.consultDoctor);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.pastConsult:
                        startActivity(new Intent(getApplicationContext(),PastConsultationNewLoginScreen.class));
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
    }
}