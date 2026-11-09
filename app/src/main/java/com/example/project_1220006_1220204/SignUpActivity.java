package com.example.project_1220006_1220204;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Button buttonRegister;

    private TextInputLayout tilEmail;
    private TextInputLayout tilFirstName;
    private TextInputLayout tilLastName;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;

    private DataBaseHelper dataBaseHelper;

    // A password must be 6-12 characters long and contain at least one
    // digit, one lowercase letter and one uppercase letter. Writing this
    // as one regular expression instead of four separate if statements.
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,12}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextEmail = findViewById(R.id.editText_signup_email);
        editTextFirstName = findViewById(R.id.editText_signup_first_name);
        editTextLastName = findViewById(R.id.editText_signup_last_name);
        editTextPassword = findViewById(R.id.editText_signup_password);
        editTextConfirmPassword = findViewById(R.id.editText_signup_confirm_password);
        buttonRegister = findViewById(R.id.button_register);

        tilEmail = findViewById(R.id.til_signup_email);
        tilFirstName = findViewById(R.id.til_signup_first_name);
        tilLastName = findViewById(R.id.til_signup_last_name);
        tilPassword = findViewById(R.id.til_signup_password);
        tilConfirmPassword = findViewById(R.id.til_signup_confirm_password);

        dataBaseHelper = new DataBaseHelper(this);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });
    }

    //check them all then put the red color for all of them at the same time
    private void attemptRegistration() {

        String email = editTextEmail.getText().toString().trim();
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();


        tilEmail.setError(null);
        tilFirstName.setError(null);
        tilLastName.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        // collecting a separate message per invalid field instead of one
        // generic flag, so the toast can say exactly what went wrong
        List<String> errorMessages = new ArrayList<>();

        //email: must be a valid email format and not already used
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email address");
            errorMessages.add("Please enter a valid email address.");
        } else if (dataBaseHelper.isEmailRegistered(email)) {
            tilEmail.setError("Already registered");
            errorMessages.add("This email is already registered.");
        }

        //first name: 3 to 10 characters
        if (firstName.length() < 3 || firstName.length() > 10) {
            tilFirstName.setError("3-10 characters");
            errorMessages.add("First name must be between 3 and 10 characters.");
        }

        //last name: 3 to 10 characters
        if (lastName.length() < 3 || lastName.length() > 10) {
            tilLastName.setError("3-10 characters");
            errorMessages.add("Last name must be between 3 and 10 characters.");
        }

        //password: 6-12 characters, at least one digit/lowercase/uppercase
        if (!Pattern.matches(PASSWORD_PATTERN, password)) {
            tilPassword.setError("Needs a number, lowercase & uppercase letter");
            errorMessages.add("Password must be 6-12 characters long and include at least " +
                    "one number, one lowercase letter, and one uppercase letter.");
        }

        //Confirm password: must match the password field exactly
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Please confirm your password");
            errorMessages.add("Please confirm your password.");
        } else if (!confirmPassword.equals(password)) {
            tilConfirmPassword.setError("Passwords do not match");
            errorMessages.add("Passwords do not match.");
        }

        if (!errorMessages.isEmpty()) {
            showError(buildCombinedMessage(errorMessages));
            return;
        }

        User newUser = new User(email, firstName, lastName, password);
        boolean inserted = dataBaseHelper.insertUser(newUser);

        if (inserted) {
            Toast.makeText(this, "Account created! You can now sign in.",
                    Toast.LENGTH_LONG).show();
            finish();
        } else {
            showError("This email is already registered.");
        }
    }

    //the toast shows exactly which fields are wrong and why
    private String buildCombinedMessage(List<String> errorMessages) {
        StringBuilder combinedMessage = new StringBuilder();
        for (String message : errorMessages) {
            if (combinedMessage.length() > 0) {
                combinedMessage.append("\n");
            }
            combinedMessage.append(message);
        }
        return combinedMessage.toString();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}