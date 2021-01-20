package com.example.talkie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.talkie.sinch.SinchService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;

public class LoginActivity extends AppCompatActivity {

    EditText usernameETLogin, passwordETLogin;
    Button LoginBtn,regLoginBtn;
    //firebase
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();

        //check user, save user
        if (user != null) {

            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameETLogin = findViewById(R.id.usernameEt);
        passwordETLogin = findViewById(R.id.passwordEt);
        LoginBtn = findViewById(R.id.loginButton);
        regLoginBtn = findViewById(R.id.regLoginButton);

        //firebase
        auth = FirebaseAuth.getInstance();


        //login button
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_text = usernameETLogin.getText().toString();
                String password_text = passwordETLogin.getText().toString();

                if(TextUtils.isEmpty(email_text) || TextUtils.isEmpty(password_text)){
                    Toast.makeText(LoginActivity.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                }else{
                    auth.signInWithEmailAndPassword(email_text,password_text).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();
                            }else{
                                Toast.makeText(LoginActivity.this, "Login Failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        regLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });
    }

}