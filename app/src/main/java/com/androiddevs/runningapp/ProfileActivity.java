package com.androiddevs.runningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.androiddevs.runningapp.control.LocationManager;
import com.androiddevs.runningapp.databinding.ActivityProfileBinding;
import com.androiddevs.runningapp.databinding.ActivityRegisterBinding;
import com.androiddevs.runningapp.login.EmailLoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private ActivityProfileBinding mBinding;

    public BottomNavigationView bottomNavigationView;

    UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("User").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user = new UserModel((String) document.get("username"), (String) document.get("firstname"), (String) document.get("lastname"), (String)document.get("email"));
                        mBinding.etProfileUsername.setText(user.getUsername());
                        mBinding.etProfileFirstName.setText(user.getFirstname());
                        mBinding.etProfileLastName.setText(user.getLastname());

                        mBinding.tvEmailAddress.setText(user.getEmail());
                        mBinding.tvName.setText(user.getFirstname() + " " + user.getLastname());
                        mBinding.tvUsername.setText(user.getUsername());

                    } else {
                        user = null;
                    }
                }
            }
        });

        mBinding.btEditButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                mBinding.clDisplayProfileAttributes.setVisibility(View.GONE);
                mBinding.clEditProfileAttributes.setVisibility(View.VISIBLE);
            }

        });

        mBinding.btProfileCancel.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                mBinding.clDisplayProfileAttributes.setVisibility(View.VISIBLE);
                mBinding.clEditProfileAttributes.setVisibility(View.GONE);
            }

        });

        mBinding.btProfileSave.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                String username = mBinding.etProfileUsername.getText().toString().trim();
                String firstname = mBinding.etProfileFirstName.getText().toString().trim();
                String lastname = mBinding.etProfileLastName.getText().toString().trim();

                firebaseFirestore.collection("User")
                        .document(mAuth.getUid())
                                .update(
                                    "username", username,
                                        "firstname", firstname,
                                        "lastname", lastname
                                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ProfileActivity.this, "Update Profile Success!", Toast.LENGTH_SHORT).show();
                                user.setFirstname(firstname);
                                user.setLastname(lastname);
                                user.setUsername(username);

                                mBinding.tvName.setText(user.getFirstname() + " " + user.getLastname());
                                mBinding.tvUsername.setText(user.getUsername());

                                mBinding.clDisplayProfileAttributes.setVisibility(View.VISIBLE);
                                mBinding.clEditProfileAttributes.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                mBinding.clDisplayProfileAttributes.setVisibility(View.VISIBLE);
                                mBinding.clEditProfileAttributes.setVisibility(View.GONE);
                            }
                        });
            }

        });


        bottomNavigationView = mBinding.urbanNavview;
        bottomNavigationView.setSelectedItemId(R.id.urbanProfile);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() { //hacky fix to navigation issue
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.urbanHome:
                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                        return true;

                    case R.id.urbanSess:
                        return true;

                    case R.id.urbanProfile:
                        return true;
                }
                return false;
            }
        });
    }

}