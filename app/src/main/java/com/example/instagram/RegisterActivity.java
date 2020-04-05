package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.RegexValidator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText mUsername,mName,mEmail,mPassword;
    private Button mRegister;
    private TextView mLoginUser;
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsername = findViewById(R.id.username);
        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mRegister = findViewById(R.id.register);
        mAuth = FirebaseAuth.getInstance();
        mLoginUser = findViewById(R.id.loginUser);

        registerButtonClick();
        loginUserButton();
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){

        }
    }*/

    public void registerButtonClick(){
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String name = mName.getText().toString();
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                if(TextUtils.isEmpty(email)|| TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this,"Some credentials are empty",Toast.LENGTH_SHORT).show();
                }else if(password.length()<6){
                    Toast.makeText(RegisterActivity.this,"Password too short",Toast.LENGTH_SHORT).show();
                }else{
                    registerUser(username,name,email,password);
                }
            }
        });
    }
    public void loginUserButton(){
        mLoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void registerUser(final String username, final String name, final String email, String password) {
        final ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
        pd.setMessage("Please Wait...");
        pd.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("name",name);
                    map.put("email",email);
                    map.put("username",username);
                    map.put("id",mAuth.getCurrentUser().getUid());
                    map.put("bio","");
                    map.put("imageUrl","default");
                    mRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this,"New User Registration successful. Now update the profile",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                            finish();
                        }
                    });

                }else{
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this,"New User Registration failed",Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(RegisterActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
