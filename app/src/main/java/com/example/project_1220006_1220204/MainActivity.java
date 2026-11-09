package com.example.project_1220006_1220204;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_1220006_1220204.DataBaseHelper;
import com.example.project_1220006_1220204.R;
import com.example.project_1220006_1220204.SharedPrefManager;


public class MainActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private CheckBox checkBoxRememberMe;
    private TextView textViewLoginError;
    private Button buttonSignIn;
    private Button buttonSignUp;

    private DataBaseHelper dataBaseHelper;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmail = findViewById(R.id.editText_email);
        editTextPassword = findViewById(R.id.editText_password);
        checkBoxRememberMe = findViewById(R.id.checkBox_remember_me);
        textViewLoginError = findViewById(R.id.textView_login_error);
        buttonSignIn = findViewById(R.id.button_sign_in);
        buttonSignUp = findViewById(R.id.button_sign_up);

        dataBaseHelper = new DataBaseHelper(this);
        sharedPrefManager = SharedPrefManager.getInstance(this);

        // If remembered an email from a previous visit, prefill it and
        // tick the checkbox so the user does not have to retype it
        String rememberedEmail = sharedPrefManager.getRememberedEmail();
        if (!TextUtils.isEmpty(rememberedEmail)) {
            editTextEmail.setText(rememberedEmail);
            checkBoxRememberMe.setChecked(true);
        }

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignIn();
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Simply moves the user to the sign-up form.
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });
    }


    // Read what user typed, check in the database and sign in (go to welcome activity) or raise an error
    private void attemptSignIn() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showLoginError("Please enter both email and password.");
            return;
        }

        boolean credentialsAreValid = dataBaseHelper.isValidLogin(email, password);

        if (!credentialsAreValid) {
            showLoginError("Incorrect email or password.");
            return;
        }

        //handle the "remember me" checkbox
        if (checkBoxRememberMe.isChecked()) {
            sharedPrefManager.rememberEmail(email);
        }
        else{
            sharedPrefManager.forgetEmail();
        }

        hideLoginError() ;

        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
        intent.putExtra("USER_EMAIL", email);
        startActivity(intent);
        finish();
    }

    private void showLoginError(String message) {
        textViewLoginError.setText(message);
        textViewLoginError.setVisibility(View.VISIBLE);
    }

    private void hideLoginError() {
        textViewLoginError.setVisibility(View.GONE);
    }
}