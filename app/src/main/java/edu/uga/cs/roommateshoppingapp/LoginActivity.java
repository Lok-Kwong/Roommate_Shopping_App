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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";

    private EditText emailEditText;
    private EditText passworEditText;
    private Button LoginButton;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        emailEditText = (EditText) findViewById( R.id.emailText );
        passworEditText = (EditText) findViewById( R.id.passwordText );

        LoginButton = (Button) findViewById( R.id.registerButton);
        LoginButton.setOnClickListener( new LoginButtonClickListener() );

        // Check if user is signed in and if signed in, sign the user out before proceeding.
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if( currentUser != null )
            mAuth.signOut();
    }

    private class LoginButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick( View view ) {

            Log.d(TAG, "signUp");
            if (!validateForm()) {
                return;
            }
            final String email = emailEditText.getText().toString();
            final String password = passworEditText.getText().toString();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener( LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText( getApplicationContext(),
                                        "Logged in as user: " + email,
                                        Toast.LENGTH_SHORT ).show();

                                startActivity(new Intent(LoginActivity.this, ShoppingListActivity.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Sign In Failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // Check form
    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(emailEditText.getText().toString())) {
            emailEditText.setError("Required");
            result = false;
        }
        else if(!(emailEditText.getText().toString().contains("@"))) {
            emailEditText.setError("Not valid email");
            result = false;
        }
        else {
            emailEditText.setError(null);
        }

        if (TextUtils.isEmpty(passworEditText.getText().toString())) {
            passworEditText.setError("Required");
            result = false;
        } else {
            passworEditText.setError(null);
        }

        return result;
    }


    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.registerText) {
            Intent intent = new Intent( LoginActivity.this, RegisterActivity.class );
            startActivity( intent );
        } else if (i == R.id.forgetText) {
//            Intent intent = new Intent(v.getContext(), ForgetActivty.class);
//            v.getContext().startActivity(intent);
        }
    }

}