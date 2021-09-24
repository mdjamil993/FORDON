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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class signinActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView textViewSignUp;
    private Button buttonLogin;
    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private String Ftype = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        getSupportActionBar().hide();

        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

        textViewSignUp = findViewById(R.id.textViewSignup);
        buttonLogin = findViewById(R.id.buttonLogin);
        progressBar = findViewById(R.id.progressbar);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        textViewSignUp.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == textViewSignUp.getId()) {
            //finish();
            startActivity(new Intent(this, signupActivity.class));
        } else if (v.getId() == buttonLogin.getId()) {
            userLogin();
        }
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
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

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    finish();
                    Intent intent;


                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("type").child(mAuth.getCurrentUser().getUid());

                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                            if(map.get("type")!=null){
                                Ftype = map.get("type").toString();

                                SharedPreferences.Editor editor = getSharedPreferences("FORDON", MODE_PRIVATE).edit();
                                editor.putString("type", Ftype);
                                editor.apply();
                                editor.commit();

                                Intent intent;
                                if( Ftype.equals("Driver") ) {
                                    intent = new Intent(signinActivity.this, DriverMapActivity.class);
                                } else {
                                    intent = new Intent(signinActivity.this, CustomerMapActivity.class);
                                }
                                startActivity(intent);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



/*                    SharedPreferences prefs = getSharedPreferences("FORDON", MODE_PRIVATE);
                    System.out.println("After commited: " + prefs.getString("type", null));

                    if( Ftype.equals("Driver") ) {
                        intent = new Intent(signinActivity.this, DriverMapActivity.class);
                    } else {
                        intent = new Intent(signinActivity.this, CustomerMapActivity.class);
                    }

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);*/
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {

            Intent intent;
            SharedPreferences prefs = getSharedPreferences("FORDON", MODE_PRIVATE);
            String restoredText = prefs.getString("type", null);
            if (restoredText != null) {
                Ftype = prefs.getString("type", "No name defined");//"No name defined" is the default value.
                if( Ftype.equals("Driver") ) {
                    intent = new Intent(signinActivity.this, DriverMapActivity.class);
                } else {
                    intent = new Intent(signinActivity.this, CustomerMapActivity.class);
                }
                startActivity(intent);
            }


        }


    }
}
