package com.sojib.cholen.historyRecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sojib.cholen.CustomerMapActivity;
import com.sojib.cholen.R;

public class PaymentActivity extends AppCompatActivity {

    Button customerPayCash, customerPayBkash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        customerPayBkash = findViewById(R.id.customer_pay_bkash);
        customerPayCash = findViewById(R.id.customer_pay_cash);

        customerPayCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PaymentActivity.this, CustomerMapActivity.class));
            }
        });

        customerPayBkash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent BkashIntent = getPackageManager().getLaunchIntentForPackage("com.bKash.customerapp");
                if(BkashIntent != null){
                    startActivity(BkashIntent);
                }else{
                     BkashIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel: *247"+Uri.encode("#")));
                    startActivity(BkashIntent);
                }
            }
        });



    }
}
