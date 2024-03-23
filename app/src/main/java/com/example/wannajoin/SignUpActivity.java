package com.example.wannajoin;

import static com.example.wannajoin.FBRef.refUsers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignUpActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;


    private FirebaseAuth auth;
    private EditText etUsername, etEmail, etPassword, etConPassword, etPhoneNum, etStatus;
    private Button btnSignup;
    private TextView tvLoginRedirect;

    private ImageView profileImageView;
    private Uri imageUri;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        etUsername = findViewById(R.id.usernameSuEditText);
        etEmail = findViewById(R.id.emailSuEditText);
        etPassword = findViewById(R.id.passwordSuEditText);
        etConPassword = findViewById(R.id.confirmPasswordSuEditText);
        etPhoneNum = findViewById(R.id.phoneNumSuEditText);
        etStatus = findViewById(R.id.statusSuEditText);
        btnSignup = findViewById(R.id.registerButton);
        tvLoginRedirect = findViewById(R.id.alreadyUserTextView);

        profileImageView = findViewById(R.id.profileImageView);
        storageRef = FirebaseStorage.getInstance().getReference();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String conPassword = etConPassword.getText().toString().trim();
                String phoneNum = etPhoneNum.getText().toString().trim();
                String status = etStatus.getText().toString().trim();

                boolean allValid = true;
                //tvGenderError.setVisibility(View.INVISIBLE);
                if (username.isEmpty())
                {
                    etUsername.setError("username cannot be empty!");
                    allValid = false;
                }
                if (email.isEmpty())
                {
                    etEmail.setError("email cannot be empty!");
                    allValid = false;
                }
                if (password.isEmpty())
                {
                    etPassword.setError("password cannot be empty!");
                    allValid = false;
                }
                if (conPassword.isEmpty())
                {
                    etConPassword.setError("confirm password cannot be empty!");
                    allValid = false;
                }
                if (phoneNum.isEmpty())
                {
                    etPhoneNum.setError("phone number cannot be empty!");
                    allValid = false;
                }
                if (allValid)
                {
                    if (password.equals(conPassword)) {
                        auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            FirebaseUser currentUser = auth.getCurrentUser();
                                            Log.d("SignUpTry", "createUserWithEmail:success");
                                            Toast.makeText(SignUpActivity.this, "User successfully created, email sent to you for verification!.",
                                                    Toast.LENGTH_SHORT).show();
                                            if (imageUri != null)
                                            {
                                                uploadImageAndSignUp(currentUser.getUid(), username, email, phoneNum, status);
                                            }
                                            else {
                                                saveUserData(currentUser.getUid(), username, email, phoneNum, status,"DEFAULT_PICTURE");
                                            }
                                            currentUser.sendEmailVerification()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d("Sending Verification Message!", "Email sent.");
                                                            }
                                                        }
                                                    });
                                            /*auth.signInWithEmailAndPassword(email, password)
                                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                // Sign in success, update UI with the signed-in user's information
                                                                Log.d("LogInTry", "signInWithEmail:success");
                                                                Toast.makeText(getApplicationContext(), "Authentication succeeded.",
                                                                        Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                            } else {
                                                                // If sign in fails, display a message to the user.
                                                                Log.w("LogInTry", "signInWithEmail:failure", task.getException());
                                                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });*/
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w("SignUpTry", "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(SignUpActivity.this, "UserCreate failed.",
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                    }
                    else {
                        etConPassword.setError("password does not match!");
                    }
                    }
                }
            });

        tvLoginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LogInActivity.class));
            }
        });
    }

    public void selectImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }


    private void uploadImageAndSignUp(String Uid , String username, String email, String phoneNum, String status) {
        final StorageReference imageRef = storageRef.child("/Images/UsersPFP/" + Uid + ".jpg");
        UploadTask uploadTask = imageRef.putFile(imageUri);

        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    saveUserData(Uid, username, email, phoneNum, status,  downloadUri.toString());
                } else {
                    // Handle failures
                }
            }
        });
    }

    private void saveUserData(String Uid , String username, String email, String phoneNum, String status, String profileImageUrl) {
        DBCollection.User user = new DBCollection.User(Uid, username, email, phoneNum, status, profileImageUrl);
        refUsers.child(Uid).setValue(user);
    }

}