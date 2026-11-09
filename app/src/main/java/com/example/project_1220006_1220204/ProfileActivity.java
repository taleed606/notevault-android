package com.example.project_1220006_1220204;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {

    private TextView textViewEmail;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextNewPassword;
    private EditText editTextConfirmPassword;
    private Button buttonSaveProfile;

    private TextInputLayout tilFirstName;
    private TextInputLayout tilLastName;
    private TextInputLayout tilNewPassword;
    private TextInputLayout tilConfirmPassword;

    private DataBaseHelper dataBaseHelper;
    private String userEmail;

    //same password rule used at sign up
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,12}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar_profile);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        textViewEmail = findViewById(R.id.textView_profile_email);
        editTextFirstName = findViewById(R.id.editText_profile_first_name);
        editTextLastName = findViewById(R.id.editText_profile_last_name);
        editTextNewPassword = findViewById(R.id.editText_profile_new_password);
        editTextConfirmPassword = findViewById(R.id.editText_profile_confirm_password);
        buttonSaveProfile = findViewById(R.id.button_save_profile);

        tilFirstName = findViewById(R.id.til_profile_first_name);
        tilLastName = findViewById(R.id.til_profile_last_name);
        tilNewPassword = findViewById(R.id.til_profile_new_password);
        tilConfirmPassword = findViewById(R.id.til_profile_confirm_password);

        dataBaseHelper = new DataBaseHelper(this);

        userEmail = getIntent().getStringExtra("USER_EMAIL");

        //fill the screen with what is stored
        User currentUser = dataBaseHelper.getUserByEmail(userEmail);
        if (currentUser != null) {
            textViewEmail.setText(currentUser.getmEmail());
            editTextFirstName.setText(currentUser.getmFirstName());
            editTextLastName.setText(currentUser.getmLastName());
        }
        // The password fields are blank

        buttonSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSaveProfile();
            }
        });
    }

    private void attemptSaveProfile() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        tilFirstName.setError(null);
        tilLastName.setError(null);
        tilNewPassword.setError(null);
        tilConfirmPassword.setError(null);

        List<String> errorMessages = new ArrayList<>();

        //Same rules as sign up
        if (firstName.length() < 3 || firstName.length() > 10) {
            tilFirstName.setError("3-10 characters");
            errorMessages.add("First name must be between 3 and 10 characters.");
        }

        if (lastName.length() < 3 || lastName.length() > 10) {
            tilLastName.setError("3-10 characters");
            errorMessages.add("Last name must be between 3 and 10 characters.");
        }

        boolean isChangingPassword =
                !TextUtils.isEmpty(newPassword) || !TextUtils.isEmpty(confirmPassword);

        if (isChangingPassword) {
            if (!Pattern.matches(PASSWORD_PATTERN, newPassword)) {
                tilNewPassword.setError("Needs a number, lowercase & uppercase letter");
                errorMessages.add("New password must be 6-12 characters long and include " +
                        "at least one number, one lowercase letter, and one uppercase letter.");
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                tilConfirmPassword.setError("Please confirm your new password");
                errorMessages.add("Please confirm your new password.");
            }
            else if (!confirmPassword.equals(newPassword)) {
                tilConfirmPassword.setError("Passwords do not match");
                errorMessages.add("Passwords do not match.");
            }
        }

        if (!errorMessages.isEmpty()) {
            showValidationErrors(errorMessages);
            return;
        }

        //save the new password, or keep the original one
        User currentUser = dataBaseHelper.getUserByEmail(userEmail);
        String passwordToSave = isChangingPassword ? newPassword : currentUser.getmPassword();

        User updatedUser = new User(userEmail, firstName, lastName, passwordToSave);
        boolean saved = dataBaseHelper.updateUser(updatedUser);

        if (saved) {
            Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            List<String> saveFailed = new ArrayList<>();
            saveFailed.add("Something went wrong while saving. Please try again.");
            showValidationErrors(saveFailed);
        }
    }

    private void showValidationErrors(List<String> errorMessages) {
        StringBuilder combinedMessage = new StringBuilder();
        for (String message : errorMessages) {
            if (combinedMessage.length() > 0) {
                combinedMessage.append("\n");
            }
            combinedMessage.append(message);
        }

        Toast.makeText(this, combinedMessage.toString(), Toast.LENGTH_LONG).show();
    }
}
