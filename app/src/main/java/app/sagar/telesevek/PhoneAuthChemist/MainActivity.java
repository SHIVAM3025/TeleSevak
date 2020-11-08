package app.sagar.telesevek.PhoneAuthChemist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import app.sagar.telesevek.AddPatiant;
import app.sagar.telesevek.R;
import app.sagar.telesevek.ScratchCardNew;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    private Spinner spinner;
    private EditText editText;
    private FirebaseAuth firebaseAuth;
    String card;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_auth);

        card = getIntent().getStringExtra("cardpass");

        SharedPreferences prefs = getSharedPreferences("doctor", MODE_PRIVATE);
        String cname = prefs.getString("cadd", null);
        String cphone = prefs.getString("phone", null);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            //Starting the User Profile Activity if the user is already Logged in
            if(TextUtils.isEmpty(cname)){
            }
            else {
                if (Objects.equals(cname, "chemistname")) {
                    /*startActivity(new Intent(getApplicationContext(), AddPatiant.class));*/
                    Intent sendStuff = new Intent(MainActivity.this, app.sagar.telesevek.AddPatiant.class);
                    sendStuff.putExtra("cardpass", card);
                    sendStuff.putExtra("phonenumber", cphone);
                    startActivity(sendStuff);
                }
            }



        }

        spinner = findViewById(R.id.spinnerCountries);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));

        editText = findViewById(R.id.editTextPhone);

        findViewById(R.id.buttonContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];

                String number = editText.getText().toString().trim();

                if (number.isEmpty() || number.length() < 10) {
                    editText.setError("Valid number is required");
                    editText.requestFocus();
                    return;
                }

                String phoneNumber = "+" + code + number;

                Intent intent = new Intent(MainActivity.this, VerifyPhoneActivity.class);
                intent.putExtra("phonenumber", phoneNumber);
                intent.putExtra("cardpass", card);
                startActivity(intent);



            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


    }
}
