package com.example.todolist.Authentication;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todolist.MainActivity;
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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Signup extends AppCompatActivity {

    TextInputEditText emailEditText, passwordEditText, nameEditText, mobilenoEditText;
    MaterialButton googleSignUpButton, signUpButton, mobileSignUpButton, loginIntentButton;

    //GoogleSignInClient mGoogleSignInCLient;
    FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    String verificationId;
    GoogleSignInClient mGoogleSignInClient;

   public String name,email;
   public Uri photoUri;


    public void onStart(){
        super.onStart();

        FirebaseUser currentUser=mAuth.getCurrentUser();
        if (currentUser != null){
            Intent i = new Intent(Signup.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }


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

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        mobilenoEditText = findViewById(R.id.mobilenoEditText);
        googleSignUpButton = findViewById(R.id.googleSignUpButton);
        signUpButton = findViewById(R.id.signUpButton);
        mobileSignUpButton = findViewById(R.id.mobileSignUpButton);
        loginIntentButton = findViewById(R.id.loginIntentButton);
        mAuth = FirebaseAuth.getInstance();

        loginIntentButton.setOnClickListener(view -> {
            Intent i = new Intent(Signup.this, Login.class);
            startActivity(i);
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // replace with your actual web client ID
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignUpButton.setOnClickListener(view -> signIn());

        signUpButton.setOnClickListener(view -> signupUserByEmailAndPassword());

        mobileSignUpButton.setOnClickListener(view -> {
            String phoneNumber = "+91" + mobilenoEditText.getText().toString().trim();

            if (phoneNumber.isEmpty()) {
                mobilenoEditText.setError("Phone number is required!");
                mobilenoEditText.requestFocus();
                return;
            }

            sendVerificationCode(phoneNumber);

            // Inflate OTP layout and set it to the content view
            LayoutInflater inflater = getLayoutInflater();
            View otpView = inflater.inflate(R.layout.otpvarification, null);
            setContentView(otpView);

            EditText[] otpDigits = {
                    otpView.findViewById(R.id.otpDigit1),
                    otpView.findViewById(R.id.otpDigit2),
                    otpView.findViewById(R.id.otpDigit3),
                    otpView.findViewById(R.id.otpDigit4),
                    otpView.findViewById(R.id.otpDigit5),
                    otpView.findViewById(R.id.otpDigit6)
            };
            setupOtpInputs(otpDigits);

            Button verifyButton = otpView.findViewById(R.id.verifyButton);
            verifyButton.setOnClickListener(view1 -> {
                StringBuilder otpCode = new StringBuilder();
                for (EditText otpDigit : otpDigits) {
                    otpCode.append(otpDigit.getText().toString().trim());
                }
                if (otpCode.length() == 6) {
                    verifycode(otpCode.toString());
                } else {
                    Toast.makeText(Signup.this, "Enter valid OTP", Toast.LENGTH_LONG).show();
                }
            });
        });
    }


    // this methods for google sign up


    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {

                Log.w("Google Sign In", "Google sign in failed e");
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void firebaseAuthWithGoogle(String idToken) {

        FirebaseAuth.getInstance().signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent i = new Intent(Signup.this, MainActivity.class);
                                    startActivity(i);
                                }
                            }
                        }
                );
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try {
            GoogleSignInAccount account= completedTask.getResult(ApiException.class);
            if (account != null){
                 name = account.getDisplayName();
                 email = account.getEmail();
                 photoUri = account.getPhotoUrl();

                String photoUrl = (photoUri != null) ? photoUri.toString() : null;

                Log.d("Google Sign-In", "Name: " + name);
                Log.d("Google Sign-In", "Email: " + email);
                Log.d("Google Sign-In", "Photo URL: " + photoUrl);
            }

        }catch (ApiException e){
            Log.w("Google Sign-In", "signInResult:failed code=" + e.getStatusCode());
        }
    }


    // this method is for email sign up


    public void signupUserByEmailAndPassword() {
        String Email = emailEditText.getText().toString().trim();
        String Password = passwordEditText.getText().toString().trim();

        if (Email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            emailEditText.setError("Enter valid Email adress");
            emailEditText.requestFocus();
            return;
        }
        if (Password.isEmpty()) {
            passwordEditText.setError("Enter password");
            passwordEditText.requestFocus();
            return;
        }
        if (Password.length() < 6) {
            passwordEditText.setError("Enter password at least 6 character long");
            passwordEditText.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Signup.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                            // add intent here user has been registered
                        } else {
                            Toast.makeText(Signup.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // this method is for phone number sign up

    public void sendVerificationCode(String phoneNumber) {

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(Signup.this,"hii gajendra"+ e.getMessage(), Toast.LENGTH_LONG).show();
            Log.i(TAG, "onVerificationFailed: "+e.getMessage());
        }

        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken token) {

            verificationId = s;
            Intent intent = new Intent(Signup.this, Login.class);
            intent.putExtra("verificationId", verificationId);
            startActivity(intent);
        }
    };


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Signup.this, "Sign in successful", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Signup.this, "Sign up failed :" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
    private void verifycode(String code){

        Toast.makeText(Signup.this,"verifycode method calls ",Toast.LENGTH_LONG).show();
        PhoneAuthCredential credential= PhoneAuthProvider.getCredential(verificationId,code);
        signInWithPhoneAuthCredential(credential);

        Intent i = new Intent(Signup.this, MainActivity.class);
        startActivity(i);
    }

    private void setupOtpInputs(EditText[] otpDigits) {

        for (int i = 0; i <otpDigits.length;i++){
            int index =i;
            otpDigits[index].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    if (!editable.toString().isEmpty() && index< otpDigits.length-1){
                        otpDigits[index+1].requestFocus();
                    }
                }
            });

        }
    }
}