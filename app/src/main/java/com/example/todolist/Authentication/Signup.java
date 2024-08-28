package com.example.todolist.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todolist.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Signup extends AppCompatActivity {

    TextInputEditText emailEditText,passwordEditText;
    MaterialButton googleSignUpButton,signUpButton,mobileSignUpButton,loginIntentButton;

    private GoogleSignInClient mGoogleSignInCLient;
     FirebaseAuth mAuth;
     private static final int RC_SIGN_IN =9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEditText=findViewById(R.id.emailEditText);
        passwordEditText=findViewById(R.id.passwordEditText);
        googleSignUpButton=findViewById(R.id.googleSignUpButton);
        signUpButton=findViewById(R.id.signUpButton);
        mobileSignUpButton=findViewById(R.id.mobileSignUpButton);
        loginIntentButton=findViewById(R.id.loginIntentButton);
        mAuth=FirebaseAuth.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInCLient = GoogleSignIn.getClient(this,gso);

        googleSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }
    public void signIn(){
        Intent signInIntent = mGoogleSignInCLient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode , Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account= task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            }catch (ApiException e ){

                Log.w("Google Sign In","Google sign in failed e");
                Toast.makeText(this,"Google sign in failed",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void firebaseAuthWithGoogle(String idToken){

        FirebaseAuth.getInstance().signInWithCredential(GoogleAuthProvider.getCredential(idToken,null))
                .addOnCompleteListener(this
                        , new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()){
                                    FirebaseUser user=mAuth.getCurrentUser();
                                    updateUI(user);
                                }
                            }
                        }
                );
    }
    public void updateUI(FirebaseUser user){

        if (user != null){
            Toast.makeText(this,"Welcome"+ user.getDisplayName(),Toast.LENGTH_LONG).show();

        }
        else {
            Toast.makeText(this,"Sign in failed",Toast.LENGTH_LONG).show();

        }
    }
}