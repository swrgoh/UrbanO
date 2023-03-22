package com.androiddevs.runningapp.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import com.androiddevs.runningapp.databinding.ActivityRegisterBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Register user info into firebase for first time users of the app
 */
public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding mBinding;
    /**
     * declares an instance of FireBaseAuth
     */
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mBinding.btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mBinding.etUsername.getText().toString().trim();
                String email = mBinding.etEmailAddress.getText().toString().trim();
                String password = mBinding.etPassword.getText().toString().trim();
                String firstname = mBinding.etFirstName.getText().toString().trim();
                String lastname = mBinding.etLastName.getText().toString().trim();
                verifyDetails(username, email, password, firstname, lastname);
            }
        });
        dialog = new ProgressDialog(this);
    }

    /**
     * validate if user inputs to register an account in the app is valid
     */
    private void verifyDetails(String username, String email, String password, String firstname, String lastname) {

        boolean isError = false;

        // error if username empty
        if(username.isEmpty())
        {
            mBinding.etUsername.setError("Input username.");
            mBinding.etUsername.requestFocus();
            isError = true;
        }

        // check if password empty
        if (password.isEmpty()) {
            mBinding.etPassword.setError("Password required");
            mBinding.etPassword.requestFocus();
            isError = true;
        }

        // check password length
        if (password.length() < 6) {
            mBinding.etPassword.setError("Minimum password length is 6 characters.");
            mBinding.etPassword.requestFocus();
            isError = true;
        }


        // error if first name empty
        if (firstname.isEmpty()) {
            mBinding.etFirstName.setError("Input first name.");
            mBinding.etFirstName.requestFocus();
            isError = true;
        }

        // error if first name empty
        if (lastname.isEmpty()) {
            mBinding.etLastName.setError("Input last name.");
            mBinding.etLastName.requestFocus();
            isError = true;
        }

        // error if email empty
        if (email.isEmpty()) {
            mBinding.etEmailAddress.setError("Input email.");
            mBinding.etEmailAddress.requestFocus();
            isError = true;
        }

        // check if valid email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mBinding.etEmailAddress.setError("Input valid email.");
            mBinding.etEmailAddress.requestFocus();
            isError = true;
        }

        // return if any error detected
        if(isError)
            return;

        displayLoadingUi();
        registerWithFirebase(email, username, password, firstname, lastname);
    }

    private void displayLoadingUi() {
        //set visibility of progress bar to true once hit register button
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //Without this user can hide loader by tapping outside screen
        dialog.setCancelable(false);
        dialog.setMessage("Creating user...");
        dialog.show();
    }

    /**
     * register user information into firebase
     * @param email user email address
     * @param username user username
     * @param password user password
     * @param firstname user firstname
     * @param lastname user lastname
     */
    private void registerWithFirebase(String email, String username, String password, String firstname, String lastname) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.sendEmailVerification();
                        registerDisplayName(username, user);
                        Snackbar snackbar = Snackbar.make(mBinding.getRoot(), "Check your email to verify your account.", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        finish();
                        startActivity(new Intent(this, StartActivity.class));
                    } else {
                        // User already exists in Firebase Auth
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Snackbar snackbar = Snackbar.make(mBinding.getRoot(), "User already exists. Please login instead.", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else { // For unhandled errors
                            Snackbar snackbar = Snackbar.make(mBinding.getRoot(), "Failed to register, error: " + task.getException().toString(), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }

                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.dismiss();
                    }
                });
    }

    /**
     * register full name of user into firebase database
     * @param firstname
     * @param user
     */
    private void registerDisplayName(String firstname, FirebaseUser user) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(firstname).build();
        user.updateProfile(profileUpdates);
    }
}