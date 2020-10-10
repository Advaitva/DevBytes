package com.example.devbytes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class SignupActivity extends AppCompatActivity {

    private EditText signupEmail;
    private EditText signupPass;
    private EditText signupConfPass;
    private Button signupBtn;
    private Button signupLoginBtn;
    private ProgressBar signupProgress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupEmail=(EditText) findViewById(R.id.emailSignup);
        signupPass=(EditText) findViewById(R.id.passSignup);
        signupConfPass=(EditText) findViewById(R.id.pass2Signup);
        signupBtn=(Button) findViewById(R.id.btnSignup);
        signupLoginBtn=(Button) findViewById(R.id.btnSignupLogin);
        signupProgress=(ProgressBar) findViewById(R.id.signup_progress);

        mAuth=FirebaseAuth.getInstance();

        signupLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLogin();
            }
        });
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=signupEmail.getText().toString();
                String pass=signupPass.getText().toString();
                String pass2=signupConfPass.getText().toString();

                if(!(TextUtils.isEmpty(email) && TextUtils.isEmpty(pass) && TextUtils.isEmpty(pass2))){

                    if(pass.equals(pass2)){
                        signupProgress.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                              if(task.isSuccessful()){
                                Intent intent=new Intent(SignupActivity.this,Setup.class);
                                startActivity(intent);
                                finish();
                              }
                              else{
                                  String errMsg=task.getException().getMessage();
                                  Toast.makeText(SignupActivity.this,errMsg,Toast.LENGTH_SHORT).show();
                              }
                            }
                        });
                    }
                    else{
                        Toast.makeText(SignupActivity.this,"Passwords do not match",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(SignupActivity.this,"All fields are mandatory..",Toast.LENGTH_SHORT).show();
                }
                signupProgress.setVisibility(View.INVISIBLE);


            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            sendToMainActivity();
        }
    }
    public void sendToMainActivity(){
        Intent mainIntent=new Intent(SignupActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
    public void sendToLogin(){
        Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}