package com.example.andrew.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

public class regitration extends AppCompatActivity implements Validator.ValidationListener, View.OnClickListener  {

    protected Validator validator;
    protected boolean validated;

    @NotEmpty(message = "Please enter Name")
    EditText name_user_reg;

    @NotEmpty
    @Email
    EditText email_user_reg;

    @NotEmpty
    @Password(min=1,message = "Password should contain at least one character", scheme = Password.Scheme.ALPHA_NUMERIC)
    EditText password_user_reg;

    Button Sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regitration);

        name_user_reg = (EditText) findViewById(R.id.et_name);
        email_user_reg = (EditText) findViewById(R.id.et_email);
        password_user_reg = (EditText) findViewById(R.id.et_password);
        Sign_up = (Button) findViewById(R.id.btn_signup_reg);

        Sign_up.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                validator.validate();
            }
        });
    }
    protected boolean validate() {
        if (validator != null)
            validator.validate();
        return validated;           // would be set in one of the callbacks below
    }

    @Override
    public void onValidationSucceeded() {
        validated = true;
        String name_input_user_reg = name_user_reg.getText().toString().trim();
        final String email_input_user_reg = email_user_reg.getText().toString().trim();
        String password_input_user_reg = password_user_reg.getText().toString();
        validated = true;
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        validated = false;

        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            // Display error messages
            if (view instanceof Spinner) {
                Spinner sp = (Spinner) view;
                view = ((LinearLayout) sp.getSelectedView()).getChildAt(0);        // we are actually interested in the text view spinner has
            }

            if (view instanceof TextView) {
                TextView et = (TextView) view;
                et.setError(message);
            }
        }
    }

    @Override
    public void onClick(View v) {
        validator.validate();
    }


}
