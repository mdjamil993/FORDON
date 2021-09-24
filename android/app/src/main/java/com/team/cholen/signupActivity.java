package com.sojib.cholen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class signupActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextEmail, editTextPassword;
    private EditText editTextName, editTextPhone;
    private TextView textViewLogin;
    private Button buttonSignUp;
    private ProgressBar progressBar;
    private RadioButton driver, customer;
    private RadioGroup radioGroup;

    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;

    int type = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        textViewLogin =  findViewById(R.id.textViewLogin);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        progressBar =  findViewById(R.id.progressbar);
        driver = findViewById(R.id.radio_driver);
        customer = findViewById(R.id.radio_customer);
        radioGroup = findViewById(R.id.radio_group);

        textViewLogin.setOnClickListener(this);
        buttonSignUp.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }
    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String name = editTextName.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }
        if (name.isEmpty()) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            editTextPhone.setError("Phone number is required");
            editTextPhone.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please Enter a valid Email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }


        if (password.length() < 6) {
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return;
        }
        if (phone.length() < 11 || phone.length() > 11 ) {
            editTextPhone.setError("Length of phone number must be 11 digits");
            editTextPhone.requestFocus();
            return;
        }

        if( type == 0 ) {
            Toast.makeText(this, "Check whether you'r a Driver or Customer", Toast.LENGTH_SHORT).show();
            radioGroup.requestFocus();
            return;
        }
        int cnt = 0;
        final String regex="^\\d{11,11}$";


        if( !phone.matches(regex) ) {
            editTextPhone.setError("Phone numbers can only contain digits");
            editTextPhone.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);



        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    finish();
                    String userID = mAuth.getCurrentUser().getUid();
                    Map mp = new HashMap();
                    mp.put("type", (type == 1)? "Driver": "Customer");
                    mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("type").child(userID);

                    mCustomerDatabase.setValue(mp);

                    //startActivity(new Intent(SignUpActivity.this, ProActivity.class));
                    SharedPreferences.Editor editor = getSharedPreferences("FORDON", MODE_PRIVATE).edit();
                    editor.putString("type", (type == 1)? "Driver": "Customer");
                    editor.apply();
                    editor.commit();
                    DatabaseReference mCustomerDeatilsDatabase;
                    if( type == 2 ) mCustomerDeatilsDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID);
                    else mCustomerDeatilsDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID);

                    Map userInfo = new HashMap();
                    userInfo.put("name", name);
                    userInfo.put("phone", phone);
                    mCustomerDeatilsDatabase.updateChildren(userInfo);

                    if( type == 1 ) {
                        startActivity(new Intent(signupActivity.this, DriverLoginActivity.class));
                    } else if( type == 2 ) {
                        startActivity(new Intent(signupActivity.this, CustomerMapActivity.class));
                    }

                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_driver:
                if (checked)
                    type = 1; // 1 means driver
                break;
            case R.id.radio_customer:
                if (checked)
                    type = 2; // 2 means customer
                break;
        }
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == buttonSignUp.getId()) {
            registerUser();
        } else if (v.getId() == textViewLogin.getId()) {
            //finish();
            //startActivity(new Intent(this, MainActivity.class));
            startActivity(new Intent(this, signinActivity.class));
            finish();
        }
    }
}
