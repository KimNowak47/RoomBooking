package com.example.roombooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.TextValueSanitizer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button signUp;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.emailSignUpText);
        password = findViewById(R.id.passwordSignUpText);
        signUp = findViewById(R.id.signUpBtn);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String e = email.getText().toString();
                String p = password.getText().toString();

                mAuth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful())
                        {

                            Toast.makeText(SignUpActivity.this, "Sign up didnt work", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        }
                    }
                });
            }
        });

    }
}
