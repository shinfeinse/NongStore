package umn.ac.id.fbtest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    ImageButton CreateAccountButton;
    EditText InputNama, InputUsername, InputEmail, InputPassword;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        InputNama = (EditText) findViewById(R.id.etNama);
        InputUsername = (EditText) findViewById(R.id.etPhone);
        InputEmail = (EditText) findViewById(R.id.etEmail);
        InputPassword = (EditText) findViewById(R.id.etPassword);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar2);

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }

        CreateAccountButton = (ImageButton) findViewById(R.id.signup);
        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Nama = InputNama.getText().toString();
                final String Username = InputUsername.getText().toString();
                final String Email = InputEmail.getText().toString();
                String Password = InputPassword.getText().toString();

                if(TextUtils.isEmpty(Nama)){
                    InputNama.setError("Mohon tuliskan nama anda");
                    return;
                }
                if(TextUtils.isEmpty(Username)){
                    InputUsername.setError("Mohon tuliskan username anda");
                    return;
                }
                if(TextUtils.isEmpty(Email)){
                    InputEmail.setError("Mohon tuliskan nomor email anda");
                    return;
                }
                if(TextUtils.isEmpty(Password)){
                    InputPassword.setError("Mohon tuliskan nomor email anda");
                    return;
                }
                if(Password.length() < 8){
                    InputPassword.setError("Password tidak boleh kurang dari 6 karakter");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser fuser = fAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterActivity.this, "Email verfikasi telah terkirim.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email tidak terkirim " + e.getMessage());
                                }
                            });

                            Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("Nama", Nama);
                            user.put("username", Username);
                            user.put("email", Email);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: Profil user telah dibuat untuk "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            //jika sukses akan menuju ke login activity
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));

                        }else {
                            Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
}
