package com.example.wannajoin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText etEmail, etPassword;
    private CheckBox rememberMe;
    private Button btnLogin;
    private TextView tvSignupRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        SharedPreferences sharedPreferencess = getSharedPreferences("RememberMe",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencess.edit();
        editor.remove("StayConnect");
        editor.remove("Email");
        editor.remove("Password");
        editor.apply();




        auth = FirebaseAuth.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("RememberMe",MODE_PRIVATE);
        if (sharedPreferences.getBoolean("StayConnect", false))
        {
            String email = sharedPreferences.getString("Email", "");
            String password = sharedPreferences.getString("Password", "");
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser userConnected = auth.getCurrentUser();
                                if (userConnected.isEmailVerified())
                                {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("LogInTry", "signInWithEmail:success");
                                    Toast.makeText(getApplicationContext(), "Authentication succeeded.",
                                            Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                }
                                else {
                                    auth.signOut();
                                    Toast.makeText(getApplicationContext(), "Email Not Verified, please check your email.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("LogInTry", "signInWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        etEmail = findViewById(R.id.emailEditText);
        etPassword = findViewById(R.id.passwordEditText);
        rememberMe = findViewById(R.id.rememberMeCheckBox);
        btnLogin = findViewById(R.id.loginButton);
        tvSignupRedirect = findViewById(R.id.dontHaveAccountTextView);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!password.isEmpty()) {
                        auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser userConnected = auth.getCurrentUser();
                                            if (userConnected.isEmailVerified())
                                            {
                                                // Sign in success, update UI with the signed-in user's information
                                                Log.d("LogInTry", "signInWithEmail:success");
                                                Toast.makeText(getApplicationContext(), "Authentication succeeded.",
                                                        Toast.LENGTH_SHORT).show();

                                                if(rememberMe.isChecked())
                                                {
                                                    SharedPreferences sharedPreferences = getSharedPreferences("RememberMe",MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putBoolean("StayConnect", true);
                                                    editor.putString("Email", email);
                                                    editor.putString("Password", password);
                                                    editor.commit();
                                                }
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            }
                                            else {
                                                auth.signOut();
                                                Toast.makeText(getApplicationContext(), "Email Not Verified, please check your email.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w("LogInTry", "signInWithEmail:failure", task.getException());
                                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    } else {
                        etPassword.setError("password cannot be empty!");
                    }

                } else if (email.isEmpty())
                {
                    etEmail.setError("email cannot be empty!");
                }
                else
                {
                    etEmail.setError("please enter valid email!");
                }
            }
        });

        tvSignupRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });

    }

}