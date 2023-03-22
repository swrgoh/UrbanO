package com.androiddevs.runningapp.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.androiddevs.runningapp.MainActivity;
import com.androiddevs.runningapp.R;
import com.androiddevs.runningapp.databinding.ActivityEmailLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Displays UI for users to sign in using Email.
 */
public class EmailLoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ActivityEmailLoginBinding mBinding;

    /**
     * initialize register,Login and forgotPassword button
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        mBinding = ActivityEmailLoginBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        bindButtons();

        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Sets logic for buttons.
     * if user click on register button, bring user to Register user activity.
     * if user click on signIn button, call userLogin() method to log user into account.
     * if user clicks forgotpassword button, bring user to forgotpassword page to reset password via email.
     */
    private void bindButtons() {
        mBinding.goBacktoStartFromLogin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(EmailLoginActivity.this, StartActivity.class));
            }
        });

        mBinding.btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
        mBinding.btForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailLoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    /**
     * executes when user clicks "login" button on EmailLoginActivity after filling up their email and password
     * checks for valid input for email and password
     * if valid check if email and password matches data in firebase.
     * If it matches, check if email is verified (first time users who logs in for the first time needs to go to their mailbox to verify email address)
     * If verified, log user into account and bring them to homepage
     */

    private void userLogin() {
        String email = mBinding.etLoginEmail.getText().toString().trim();
        String password = mBinding.etLoginPassword.getText().toString().trim();

        boolean isError = false;

        // error if email empty
        if (email.isEmpty()) {
            mBinding.etLoginEmail.setError("Input email");
            mBinding.etLoginPassword.requestFocus();
            isError = true;
        }

        // error if password empty
        if (password.isEmpty()) {
            mBinding.etLoginPassword.setError("Input password");
            mBinding.etLoginPassword.requestFocus();
            return;
        }

        // error if password not meet requirements
        if (password.length() < 6) {
            mBinding.etLoginPassword.setError("Input password");
            mBinding.etLoginPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(EmailLoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EmailLoginActivity.this, MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EmailLoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        /**
         // display a progress dialog box to indicate to user that the app is logging him into account
         //
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //Without this user can hide loader by tapping outside screen
        dialog.setCancelable(false);
        dialog.setMessage("Logging you in...");
        dialog.show();

         // log user into account by checking if their email and password matches data in firebase
         //
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)

                 // check if email address has been verified by the user or not
                if (task.isSuccessful()) {


                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    //if user email is verified, direct user to main activity once user logs in successfully with correct email and password
                    //else send email verification link to user

                    if (user.isEmailVerified()) {

                        dialog.dismiss();
                        finish();
                        startActivity(new Intent(EmailLoginActivity.this, MainActivity.class));

                    } else {

                        dialog.dismiss();
                        user.sendEmailVerification();
                        Toast.makeText(EmailLoginActivity.this, "Check your email to verify your account", Toast.LENGTH_LONG).show();
                    }


                     // if email or password do not match data in firebase, do not log user in and prompt an error message to user that their login credential is wrong
                     //
                } else {
                    dialog.dismiss();
                    Toast.makeText(EmailLoginActivity.this, "Failed to login. check credentials", Toast.LENGTH_LONG).show();
                }
            }
        });

        */
    }
}