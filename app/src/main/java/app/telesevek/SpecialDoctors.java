package app.telesevek;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class SpecialDoctors extends AppCompatActivity implements PaymentResultWithDataListener {

    ImageView female;
    ImageView child;
    ImageView common;
    ImageView shalya;
    String amt;
    String des;
    String URL="https://api.razorpay.com/v1/orders";
    RequestQueue mRequestQueue;
    String id;
    String type;
    ProgressBar pb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_doctors);

        Checkout.preload(getApplicationContext());

        pb=findViewById(R.id.pbPayment);

        female=findViewById(R.id.ivFemaleDoctor);
        child=findViewById(R.id.ivChildDoctor);
        shalya=findViewById(R.id.ivShalyaDoctor);
        common=findViewById(R.id.ivCommonDoctor);

        mRequestQueue= Volley.newRequestQueue(this);

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

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb.setVisibility(View.VISIBLE);
                amt="29900";
                des="स्त्री रोग विशेषज्ञ";
                type="1";
                order(amt,des);
                pb.setVisibility(View.GONE);

            }
        });
        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb.setVisibility(View.VISIBLE);
                amt="29900";
                des="बच्चों का चिकित्सक";
                type="2";
                order(amt,des);
                pb.setVisibility(View.GONE);
            }
        });

        common.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb.setVisibility(View.VISIBLE);
                amt="14900";
                des="सामान्य चिकित्सक";
                type="3";
                order(amt,des);
                pb.setVisibility(View.GONE);
            }
        });

        shalya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb.setVisibility(View.VISIBLE);
                amt="29900";
                des="शल्य चिकित्सक";
                type="4";
                order(amt,des);
                pb.setVisibility(View.GONE);
            }
        });

    }

    public void order(String amount,String des)  {

        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount); // amount in the smallest currency unit
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_rcptid_11");

            JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, URL, orderRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Toast.makeText(SpecialDoctors.this, "SUCCESS ORDER", Toast.LENGTH_SHORT).show();
                    try {
                        id=response.getString("id");
                        startPayment(amount,des,id);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(SpecialDoctors.this, "FAIL ORDER", Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    String cred=String.format("%s:%s","rzp_live_kVdwpjtDFeNxWV","CQZ8WbMl41I36bTbSnXlcpD5");
                    String auth="Basic "+ Base64.encodeToString(cred.getBytes(),Base64.NO_WRAP);
                    params.put("content-type","application/json");
                    params.put("authorization",auth);
                    return params;
                }
            };
            mRequestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }





    }



    public void startPayment(String amount,String description,String id){

        final Activity activity=this;
        Checkout co=new Checkout();

        JSONObject paymentObject=new JSONObject();

        try {
            paymentObject.put("name","TeleSevak");
            paymentObject.put("description",description);
            paymentObject.put("currency","INR");
            paymentObject.put("amount",amount);
            paymentObject.put("order_id",id);


            JSONObject method=new JSONObject();
            method.put("card","1");
            method.put("netbanking","1");
            method.put("upi","0");
            method.put("wallet","1");
            //checkout.put("method",method);
            // options.put("checkout",checkout);
            paymentObject.put("method",method);

//            JSONObject prefill=new JSONObject();
//            prefill.put("email","rishavi1999@gmail.com");
//            prefill.put("contact","8604182882");

            //paymentObject.put("prefill",prefill);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        co.open(activity,paymentObject);

    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {

        try {
            Toast.makeText(this, "Payment Successful! Order ID: "+paymentData.getOrderId(), Toast.LENGTH_LONG).show();
            //startActivity(new Intent(getApplicationContext(),AddPatiant.class));
            Intent intent=new Intent(getApplicationContext(),FormAfterPayment.class);
            intent.putExtra("type",type);
            intent.putExtra("orderID",paymentData.getOrderId());
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Exception onPaymentSuccess", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

        Toast.makeText(this, "Payment Failed!", Toast.LENGTH_SHORT).show();
    }
}
