package com.example.devbytes;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail;
    private EditText loginPass;
    private Button loginBtn;
    private Button loginSignupBtn;
    private FirebaseAuth mAuth;
    private ProgressBar loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginEmail= (EditText) findViewById(R.id.emailLogin);
        loginPass= (EditText) findViewById(R.id.passLogin);
        loginBtn=(Button) findViewById(R.id.btnLogin);
        loginSignupBtn=(Button) findViewById(R.id.btnLoginSignup);
        mAuth=FirebaseAuth.getInstance();
        loginProgress=(ProgressBar)findViewById(R.id.loginprogressBar);


        loginSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToSignup();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=loginEmail.getText().toString();
                String pass=loginPass.getText().toString();
                if(!(TextUtils.isEmpty(email) && TextUtils.isEmpty(pass))){
                    loginProgress.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                               Log.d("msg","Logged in");
                                sendToMainActivity();
                            }
                            else{
                                Log.d("errmsg","Logging error");
                                String errorMsg=task.getException().getMessage();
                                Toast.makeText(LoginActivity.this,"Error: "+errorMsg,Toast.LENGTH_SHORT).show();
                            }
                            loginProgress.setVisibility((View.INVISIBLE));
                        }
                    });
                }
                else{
                    Toast.makeText(LoginActivity.this,"Error: Enter all fields",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser= mAuth.getCurrentUser();
        if(currentUser!=null){
            sendToMainActivity();
        }
        else{

        }
    }
    public void sendToMainActivity(){
        Intent mainIntent=new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
    public  void sendToSignup(){
        Intent intent =new Intent(LoginActivity.this,SignupActivity.class);
        startActivity(intent);
    }

}