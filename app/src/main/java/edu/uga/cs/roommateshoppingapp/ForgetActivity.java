package edu.uga.cs.roommateshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * This lets the user reset their password if they forget it.
 */
public class ForgetActivity extends AppCompatActivity {

    private String email;
    private FirebaseAuth mAuth;
    private EditText emailText;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        emailText = (EditText)findViewById(R.id.emailForgot);
        submit = findViewById(R.id.forgotButton);

        email = emailText.getText().toString();

        mAuth = FirebaseAuth.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                String email = emailText.getText().toString().trim();

                // Sends user an email to reset password.
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter your email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgetActivity.this, "Check email to reset your password!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(view.getContext(), LoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(ForgetActivity.this, "Fail to send reset password email!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }
}