package com.androiddevs.runningapp.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.androiddevs.runningapp.MainActivity;
import com.androiddevs.runningapp.R;
import com.androiddevs.runningapp.databinding.ActivityStartBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Displays UI for users to sign in using Google account or Email.
 */
public class StartActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1234;

    private ActivityStartBinding mBinding;
    private GoogleSignInClient mSignInClient;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mBinding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        bindButtons();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(this, gso);
        mFirebaseAuth = FirebaseAuth.getInstance();
        checkIfSignedIn();
    }

    /**
     * Assign logic to buttons.
     */
    private void bindButtons() {
        //mBinding.signInButton.setOnClickListener(view -> signInGoogle());
        mBinding.btStartRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
            }
        });

        mBinding.btStartLogin.setOnClickListener(view -> navigateToEmailSignIn());
    }

    /**
     * Check if user is already signed in.
     */
    private void checkIfSignedIn() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            mFirebaseAuth.signOut();
            /*
            if (user.isEmailVerified()) {
                startActivity(new Intent(StartActivity.this, MainActivity.class));
            }*/
        }
    }

    /**
     * Prompt Google Sign In popup.
     */
    private void signInGoogle() {
        Intent signInIntent = mSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * when user click emailSignInButton, navigate app to EmailLoginActivity
     */
    private void navigateToEmailSignIn() {
        startActivity(new Intent(this, EmailLoginActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                authWithGoogle(account);
            } catch (ApiException e) {

            }
        }
    }

    /**
     * Authenticate user's Google account
     * @param acct User's Google account
     */
    private void authWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(this, authResult -> {
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(this, e -> Toast.makeText(StartActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show());
    }
}