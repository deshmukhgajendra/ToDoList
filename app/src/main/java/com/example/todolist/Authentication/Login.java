package com.example.todolist.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todolist.MainActivity;
import com.example.todolist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    TextInputEditText emailEditText,passwordEditText;
    MaterialButton signInButton,googleSignInButton,mobileSignInButton;
    Button signupIntentButton;
    FirebaseAuth mAuth;

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser =mAuth.getCurrentUser();
        if (currentUser != null){
            Intent intent= new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEditText=findViewById(R.id.emailEditText);
        passwordEditText=findViewById(R.id.passwordEditText);
        signInButton=findViewById(R.id.signInButton);
        mobileSignInButton=findViewById(R.id.mobileSignInButton);
        signupIntentButton=findViewById(R.id.signupIntentButton);
        mAuth=FirebaseAuth.getInstance();


        signupIntentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this,Signup.class);
                startActivity(i);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUserUsingEmailandPassword();
            }
        });


    }

    public void signInUserUsingEmailandPassword(){
        String Email = emailEditText.getText().toString().trim();
        String Password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(Email)){
            emailEditText.setError("Please Enter valid email address");
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            emailEditText.setError("Please Enter valid email address");
            emailEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(Password)){
            passwordEditText.setError("Enter password");
            passwordEditText.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(Email,Password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent i = new Intent(Login.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else {
                            Toast.makeText(Login.this,"Authentication failed !"+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}