package com.tony.directions_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private TextInputLayout inputemailLogin, inputpasswordLogin;
    private Button btnLogin, forgotPassword, gotoRegister;
    FirebaseAuth mAuth;
    boolean check=false;
    ProgressDialog mLoadingBar;

    FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);




        inputemailLogin = findViewById(R.id.inputEmailLogin);
        inputpasswordLogin = findViewById(R.id.inputPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        gotoRegister = findViewById(R.id.gotoRegister);
        forgotPassword = findViewById(R.id.forgotPassword);

        mAuth = FirebaseAuth.getInstance();
        mLoadingBar = new ProgressDialog(this);

        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                check=true;
                onAuthStateChanged(mAuth);
                };

        });
    }

    private void LogIn() {
        String email = inputemailLogin.getEditText().getText().toString();
        String password = inputpasswordLogin.getEditText().getText().toString();

        if (email.isEmpty() || !email.contains("@gmail")){
            showError(inputemailLogin, "Email is not valid");
        }else  if (password.isEmpty() || password.length()<5){
            showError(inputpasswordLogin, "Password is not valid");
        }else {
            mLoadingBar.setTitle("Login");
            mLoadingBar.setMessage("Please wait while your credentials get verified");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        mLoadingBar.dismiss();
                        Toast.makeText(LogInActivity.this, "Login is successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else {
                        mLoadingBar.dismiss();
                        Toast.makeText(LogInActivity.this, "Email or Password Incorrect!!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void showError(TextInputLayout field, String text) {
        field.setError(text);
        field.requestFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(this);
    }



    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (mAuth.getCurrentUser() == null){
            if(check)
            LogIn();
        }else{
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
//            Toast.makeText(LogInActivity.this, mAuth.getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
    }
}