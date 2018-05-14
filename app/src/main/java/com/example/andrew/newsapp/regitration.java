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
import com.mobsandgeeks.saripaar.annotation.Password;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class regitration extends AppCompatActivity implements Validator.ValidationListener, View.OnClickListener {

    private static final String URL = "jdbc:mysql://192.168.1.5/news";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "passwordET";

    protected Validator validator;
    protected boolean validated;

    private static String name, email, mobile, password, username;
    private int type = 0;

    private static boolean user;

    @NotEmpty(message = "Please enter Name")
    EditText name_user_reg;

    @NotEmpty
    @Email
    EditText email_user_reg;

    @NotEmpty
    @Password(min = 1, message = "Password should contain at least 6 characters", scheme = Password.Scheme.ALPHA_NUMERIC)
    EditText password_user_reg;

    @NotEmpty(message = "Please enter mobile number")
    EditText number_phone;

    Button Sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regitration);

        validator = new Validator(this);
        validator.setValidationListener(this);

        name_user_reg = (EditText) findViewById(R.id.et_name);
        email_user_reg = (EditText) findViewById(R.id.et_email);
        password_user_reg = (EditText) findViewById(R.id.et_password);
        number_phone = findViewById(R.id.et_phone);
        Sign_up = (Button) findViewById(R.id.btn_signup_reg);

        Sign_up.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (validate()) {

                    name = name_user_reg.getText().toString().trim();
                    email = email_user_reg.getText().toString().trim();
                    password = password_user_reg.getText().toString().trim();
                    mobile = number_phone.getText().toString().trim();

                    CheckUserExitsTask checkUserExitsTask = new CheckUserExitsTask();
                    checkUserExitsTask.execute();

                } else {
                    return;
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


    public class DataBaseConnectionTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                String query = "INSERT INTO `user`(`name`, `emailET`, `passwordET`, `type`, `mobile`) VALUES (?, ?, ?, ?, ?)";

                PreparedStatement preparedStmt = connection.prepareStatement(query);
                preparedStmt.setString(1, name);
                preparedStmt.setString(2, email);
                preparedStmt.setString(3, password);
                preparedStmt.setInt(4, type);
                preparedStmt.setString(5, mobile);

                preparedStmt.execute();
                connection.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
//                String query = "INSERT INTO `user`(`name`, `emailET`, `passwordET`, `type`, `mobile`) VALUES (" +
//                        "'" + name + "', '" + username + "', '" + emailET + "', " + passwordET + ", '" + type  + "', '" + mobile +  "');";
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "User Registered Successfully!", Toast.LENGTH_SHORT).show();
        }

    }

    public class CheckUserExitsTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                Statement statement = connection.createStatement();
                String sql = "SELECT * FROM user WHERE email = '" + email + "'";
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

            if (!user) {
                DataBaseConnectionTask dataBaseConnectionTask = new DataBaseConnectionTask();
                dataBaseConnectionTask.execute();
            } else {
                Toast.makeText(getApplicationContext(), "User Exists", Toast.LENGTH_SHORT).show();
            }
        }
    }
}