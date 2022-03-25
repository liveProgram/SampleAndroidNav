package com.live.programming.an21_functionalapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.live.programming.an21_functionalapp.databinding.ActivityMainBinding;
import com.live.programming.an21_functionalapp.ui.profile.ProfileActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    TextInputLayout unLayout, emailLayout, codeLayout, contactLayout, passLayout, confirmLayout;
    TextInputEditText unInput, emailInput, contactInput, passInput, confirmInput;
    MaterialAutoCompleteTextView codeView;
    Button registerBtn;
    RadioGroup genderRadio;
    String g = "";
    String countryCode;
    String[] codes = new String[]{"+91", "+92"};
    FirebaseAuth auth;
    LocalSession local;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        unLayout = binding.nameLayout;
        emailInput = binding.emailInput;
        codeLayout = binding.codeLayout;
        contactInput = binding.mobileInput;
        emailLayout = binding.emailLayout;
        contactLayout = binding.mobileLayout;
        passLayout = binding.pwdLayout;
        passInput = binding.pwdInput;
        confirmInput = binding.confirmInput;
        confirmLayout = binding.confirmLayout;
        unInput = binding.nameInput;
        registerBtn = binding.btnRegister;
        genderRadio = binding.genderGroup;
        codeView = binding.codeOptions;

        auth = FirebaseAuth.getInstance();
        local = new LocalSession(MainActivity.this);

        // verifying whether the user had already logged in
        if(!local.readData(AppConstants.UID).isEmpty())
        {
            startActivity(new Intent(MainActivity.this, MainDrawerActivity.class));
            MainActivity.this.finish();
            return;
        }

        // adding data to dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                R.layout.support_simple_spinner_dropdown_item, codes);

        codeView.setAdapter(adapter);

        codeView.setOnItemClickListener((parent, view, position, id) ->
                countryCode = codes[position]
        );

        getSelectedGender();

        // adding on clicking event handling method for button (any view)
        registerBtn.setOnClickListener(v -> {
            // code that will help to submit the form

            setErrorBlank();
            String username = String.valueOf(unInput.getText()); // input->editable object ->string
            String email = String.valueOf(emailInput.getText());
            String contact = String.valueOf(contactInput.getText());
            String pass = String.valueOf(passInput.getText());
            String confirm = String.valueOf(confirmInput.getText());

            if (checkAllFields(username, email, contact, pass, confirm)) {
                loginProcess(email,pass);
                //authenticateUser(username, email, contact, pass );
               /* showNotifier("Registration done");
                // code for routing to next screen
                Intent obj = new Intent(MainActivity.this, MainDrawerActivity.class);
                startActivity(obj); // to launch a different activity*/
            }

        });
    }

    private void authenticateUser(String username, String email, String contact, String pass) {
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                String id = task.getResult().getUser().getUid();
                                auth.getCurrentUser().sendEmailVerification();
                                storeUserDetails(id, username, email, contact);
                            }
                            else
                                showNotifier(task.getException().getMessage());
                        }
                )
                .addOnFailureListener(e ->
                        showNotifier(e.getMessage())
                );
    }

    private void storeUserDetails(String id, String username, String email, String contact) {
        Map<String, String> obj = new HashMap<>();
        obj.put(AppConstants.UNAME, username);
        obj.put(AppConstants.U_EMAIL, email);
        obj.put(AppConstants.U_PHONE, countryCode+contact);
        obj.put(AppConstants.UID, id);
        obj.put(AppConstants.U_GENDER, g);

        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection("UserDetails")
                .document(id)
                .set(obj)
                .addOnCompleteListener(task -> {
                    if (task.isComplete()) {
                        showNotifier("Registration done");
                        // write the code to navigate to login activity
                    }
                });
    }

    private void setErrorBlank() {
        unLayout.setError("");
        emailLayout.setError("");
        confirmLayout.setError("");
        passLayout.setError("");
        contactLayout.setError("");
        codeLayout.setError("");
    }

    private boolean checkAllFields(String username, String email, String contact, String pass, String confirm) {

        if (username.isEmpty())
            unLayout.setError("Username can't be empty");
        else if (email.isEmpty())
            emailLayout.setError("Email should be provided");
        else if (contact.isEmpty())
            contactLayout.setError("Provide your Contact No.");
        else if (pass.isEmpty())
            passLayout.setError("Need to give a password");
        else if (confirm.isEmpty())
            confirmLayout.setError("Retype your password");
        else if (contact.length() < 10)
            contactLayout.setError("Contact no. should be of 10 digit");
        else if (!pass.equals(confirm)) {
            passLayout.setError("Password mismatched");
            confirmLayout.setError("Password mismatched");
        } else if (g.isEmpty())
            showNotifier("Gender need to be selected");
        else if (countryCode == null)
            codeLayout.setError("Select");
        else
            return true;

        return false;
    }

    private void showNotifier(String msg) {
        Snackbar.make(MainActivity.this, binding.getRoot(), msg, Snackbar.LENGTH_SHORT).show();
    }

    private void getSelectedGender() {
        // radio group this method applicable
        genderRadio.setOnCheckedChangeListener((group, checkedId) -> {
            if (group.getCheckedRadioButtonId() == R.id.radio_male)
                g = getString(R.string.male);
            else if (group.getCheckedRadioButtonId() == R.id.radio_female)
                g = getString(R.string.female);
        });
    }

    private void loginProcess(String email, String pass)
    {
        auth.signInWithEmailAndPassword(email,pass)
            .addOnCompleteListener(task -> {
                if(task.isComplete())
                {
                    if(auth.getCurrentUser().isEmailVerified())
                    // to store some data locally into user device
                    local.storeData(AppConstants.UID, task.getResult().getUser().getUid());
                    local.storeData(AppConstants.U_EMAIL, email);

                    showNotifier("Login successful");
                    // navigation to next screen
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                }
                else
                    showNotifier("Login failed");
            })
            .addOnFailureListener(e -> showNotifier(e.toString()));
    }

}