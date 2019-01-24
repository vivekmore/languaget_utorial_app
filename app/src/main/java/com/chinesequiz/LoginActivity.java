package com.chinesequiz;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends Activity {
    private FirebaseAuth mAuth;
    TextView emailText,passwordText;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private SharedPreferences pref;
    private String email;
    private String password;

    private Typeface boldFontFace;
    private Typeface fontFace;
    TextView titleTv;
    TextView subTitleTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initValue();
        configureDesign();

        //-----Get locally saved email address for auto login
        email = pref.getString("Email", "");
        if(!email.equals("")) {
            password = pref.getString("Password", "");
            authenticateWithEmailAndPassword();
        }
        else {
            if(mAuth.getCurrentUser() != null){
                startActivity(new Intent(this, MainActivity.class));
            }
        }
    }

    private void initValue() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        boldFontFace = Typeface.createFromAsset(getAssets(), "JosefinSans-Bold.ttf");
        fontFace = Typeface.createFromAsset(getAssets(), "JosefinSans-Regular.ttf");
    }

    private void configureDesign() {
        Button signInBtn = (Button) findViewById(R.id.signInBtn);
        emailText = (TextView) findViewById(R.id.emailText);
        passwordText = (TextView) findViewById(R.id.passwordText);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailText.getText().toString();
                password = passwordText.getText().toString();
                authenticateWithEmailAndPassword();
            }
        });

        Button googleSignInBtn = (Button) findViewById(R.id.googleSignInBtn);
        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        titleTv = findViewById(R.id.textView);
        subTitleTv = findViewById(R.id.textView9);
        titleTv.setTypeface(boldFontFace);
        subTitleTv.setTypeface(fontFace);
        signInBtn.setTypeface(fontFace);
        googleSignInBtn.setTypeface(fontFace);
        emailText.setTypeface(fontFace);
        passwordText.setTypeface(fontFace);
    }

    private void authenticateWithEmailAndPassword() {
        Util.showProgressDialog("Loading..", LoginActivity.this);
        if(email.equals("") || password.equals("")) {
            Util.showToast("Please provide Email and Password.", LoginActivity.this);
            Util.hideProgressDialog();
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Authentication success.",
                                Toast.LENGTH_SHORT).show();
                        Util.hideProgressDialog();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        pref.edit().putString("Email", email).apply();
                        pref.edit().putString("Password", password).apply();
                    } else {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Util.showToast("New User created.", LoginActivity.this);
                                            Util.hideProgressDialog();
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));

                                            pref.edit().putString("Email", email).apply();
                                            pref.edit().putString("Password", password).apply();
                                        } else {
                                            Util.showToast("Authentication failed.", LoginActivity.this);
                                            Util.hideProgressDialog();
                                        }
                                    }
                                });
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Util.showToast(String.format("Google SignIn Account Errr: %s", e.getMessage()), LoginActivity.this);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Util.showProgressDialog("Loading..", LoginActivity.this);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Util.showToast("Authentication success.", LoginActivity.this);
                            Util.hideProgressDialog();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            Util.showToast(task.getException().toString(), LoginActivity.this);
                            Util.hideProgressDialog();
                        }
                    }
                });
    }
}
