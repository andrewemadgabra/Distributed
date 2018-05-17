package com.example.andrew.newsapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements Validator.ValidationListener, View.OnClickListener {

    private static final String URL = "jdbc:mysql://192.168.1.5/news";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";


    private static String email, password;
    private String loginData[] = new String[2];
    boolean user;

    @NotEmpty
    @Email
    EditText emailET;

    @NotEmpty
    EditText passwordET;

    protected Validator validator;
    protected boolean validated;

    Button btn_login_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        validator = new Validator(this);
        validator.setValidationListener(this);

        emailET = (EditText) findViewById(R.id.et_email);
        passwordET = (EditText) findViewById(R.id.et_password);
        btn_login_input = findViewById(R.id.btn_login);


        btn_login_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
                if (validate()) {

                    email = emailET.getText().toString().trim();
                    password = passwordET.getText().toString().trim();

                    loginData[0] = email;
                    loginData[1] = password;

                    CheckUserExitsTask checkUserExitsTask = new CheckUserExitsTask();
                    checkUserExitsTask.execute();
                }

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
        final String email_user = emailET.getText().toString().trim();
        String password_user = passwordET.getText().toString().trim();
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
                view = ((LinearLayout) sp.getSelectedView()).getChildAt(0);// we are actually interested in the text view spinner has
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

    public class CheckUserExitsTask extends AsyncTask<Void, Void, Void> {

        private String message;
        private boolean loginSuccess;

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Socket socket = new Socket("10.0.2.2", 100);

                ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                os.writeObject(loginData);
                os.flush();

                loginSuccess = (boolean) ois.readObject();
//                os.close();
//                ois.close();
//
//                socket.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (loginSuccess) {
                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));

            } else {
                Toast.makeText(getApplicationContext(), "User Not Found!", Toast.LENGTH_LONG).show();
            }
        }
    }
}