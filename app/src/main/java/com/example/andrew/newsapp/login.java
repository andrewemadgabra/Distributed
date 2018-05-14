package com.example.andrew.newsapp;

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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class login extends AppCompatActivity implements Validator.ValidationListener, View.OnClickListener {

    private static final String URL = "jdbc:mysql://192.168.1.5/news";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private static String email, password;

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
                    password = passwordET.getText().toString().trim();
                    email = emailET.getText().toString().trim();

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


        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                Statement statement = connection.createStatement();
                String sql = "SELECT * FROM `user` WHERE email = '" + email + "' AND password = '" + password + "'";
                ResultSet resultSet = statement.executeQuery(sql);

                user = resultSet.next();

                connection.close();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (user) {
                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Not Valid User!", Toast.LENGTH_SHORT).show();
            }

        }
    }


}
