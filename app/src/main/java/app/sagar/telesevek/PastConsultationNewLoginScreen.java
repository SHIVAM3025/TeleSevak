package app.sagar.telesevek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import app.sagar.telesevek.PhoneAuthConsulation.MainActivity;
import app.sagar.telesevek.PhoneAuthConsulation.VerifyPhoneActivity;

public class PastConsultationNewLoginScreen extends AppCompatActivity {

    private EditText etNumber;
    private View btCard;
    private View btPhoneNumber;
    private Button btContinue;
    private FirebaseAuth firebaseAuth;
    private boolean isPhone=true;
    private String phoneNumber;
    private String cardNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_consultation_new_login_screen);


        SharedPreferences prefs = getSharedPreferences("past", MODE_PRIVATE);
        String dname = prefs.getString("passdata", null);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            //Starting the User Profile Activity if the user is already Logged in
            if(TextUtils.isEmpty(dname)){
            }
            else {
                if (Objects.equals(dname, "already")) {
                    startActivity(new Intent(getApplicationContext(), PastCounsulation.class));
                }
            }


        }

        etNumber=findViewById(R.id.etEnterNumber);
        btCard=findViewById(R.id.relCard);
        btPhoneNumber=findViewById(R.id.relButtons);
        btContinue=findViewById(R.id.btnContinue);
        final TextView tvCard=findViewById(R.id.tvCard);
        final TextView tvPhone=findViewById(R.id.tvPhone);
        final ImageView ivPhone=findViewById(R.id.ibCall);
        final ImageView ivCard=findViewById(R.id.ivCard);


        btPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btPhoneNumber.setBackgroundResource(R.drawable.button_clr_shp);
                btCard.setBackgroundResource(R.drawable.faded_shape);
                tvCard.setTextColor(Color.parseColor("#D1CDCD"));
                ivCard.setColorFilter(getResources().getColor(R.color.whitish));
                tvPhone.setTextColor(Color.parseColor("#ffffff"));
                ivPhone.setColorFilter(getResources().getColor(R.color.white));
                isPhone=true;
                etNumber.setHint("फ़ोन नंबर भरें");
            }
        });

        btCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btCard.setBackgroundResource(R.drawable.color_shape_right);
                btPhoneNumber.setBackgroundResource(R.drawable.faded_shape_left);
                tvPhone.setTextColor(Color.parseColor("#D1CDCD"));
                ivPhone.setColorFilter(getResources().getColor(R.color.whitish));
                tvCard.setTextColor(Color.parseColor("#ffffff"));
                ivCard.setColorFilter(getResources().getColor(R.color.white));
                isPhone=false;
                etNumber.setHint("कार्ड नंबर भरें");

            }
        });



        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isPhone){
                     phoneNumber = etNumber.getText().toString().trim();

                    if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {
                        etNumber.setError("Valid number is required");
                        etNumber.requestFocus();
                        return;
                    }
                    String phoneNumberNew = "+" + "91" + phoneNumber;

                    Intent intent = new Intent(getApplicationContext(), VerifyPhoneActivity.class);
                    intent.putExtra("phonenumber", phoneNumberNew);
                    startActivity(intent);

                }

                else {
                     cardNumber= etNumber.getText().toString().trim();

                    if(cardNumber.isEmpty()|| cardNumber.length()<6 ||cardNumber.length()>8){
                        etNumber.setError("Valid number is required");
                        etNumber.requestFocus();
                        return;
                    }

                    Intent intent=new Intent(getApplicationContext(),PastCounsulation.class);
                    intent.putExtra("cardNumber",cardNumber);
                    intent.putExtra("isScratchCard",true);
                    startActivity(intent);
                }
            }
        });


        BottomNavigationView bottomNav=findViewById(R.id.bottomNavPastLoginNew);
        bottomNav.setSelectedItemId(R.id.pastConsult);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.pastConsult:
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
                        startActivity(new Intent(getApplicationContext(),ScratchCardNew.class));
                        overridePendingTransition(0,0);
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

    @Override
    protected void onStart() {
        super.onStart();
    }
}