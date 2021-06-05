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

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout inputemailRegister, inputpasswordRegister;
    private Button btnRegister, gotoLogin;
    FirebaseAuth mAuth;
    ProgressDialog mLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        inputemailRegister = findViewById(R.id.inputEmailRegister);
        inputpasswordRegister = findViewById(R.id.inputPasswordRegister);
        btnRegister = findViewById(R.id.btnRegister);
        gotoLogin = findViewById(R.id.gotoLogin);

        mAuth = FirebaseAuth.getInstance();
        mLoadingBar = new ProgressDialog(this);
        
        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(intent);
            }
        });
        
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterUsers();
            }
        });
    }

    private void RegisterUsers() {
        String email = inputemailRegister.getEditText().getText().toString();
        String password = inputpasswordRegister.getEditText().getText().toString();

        if (email.isEmpty() || !email.contains("@gmail")){
            showError(inputemailRegister, "Email is not valid");
        }else  if (password.isEmpty() || password.length()<5){
            showError(inputpasswordRegister, "Password must be greater than 5 letters");
        }else {
            mLoadingBar.setTitle("Registration");
            mLoadingBar.setMessage("Please wait while your credentials get verified");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mLoadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Registration is successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else {
                        mLoadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Registarion Unsuccessful..Try Again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showError(TextInputLayout field, String text) {
        field.setError(text);
        field.requestFocus();
    }
}