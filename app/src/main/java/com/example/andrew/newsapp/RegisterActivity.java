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
import com.mobsandgeeks.saripaar.annotation.Password;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class RegisterActivity extends AppCompatActivity implements Validator.ValidationListener, View.OnClickListener {

//    WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
//    String ipAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

    protected Validator validator;
    protected boolean validated;

    private String name, email, mobile, password;

    private String registerData[] = new String[4];

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

                    registerData[0] = name;
                    registerData[1] = email;
                    registerData[2] = password;
                    registerData[3] = mobile;

                    RegisterServer registerServer = new RegisterServer();
                    registerServer.execute();
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

//    public static void startServer() {
//        (new Thread() {
//            @Override
//            public void run() {
//                ServerSocket ss;
//                try {
//                    ss = new ServerSocket(100);
//
//                    Socket socket = ss.accept();
//
//                    ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
//
//                    ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
//
//                    Register m = (Register) is.readObject();
//
//                    os.writeObject(m);
//
//                    socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

    @Override
    public void onClick(View v) {
        validator.validate();
    }


    public class RegisterServer extends AsyncTask<Void, Void, Void> {

        private String message;

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Socket socket = new Socket("10.0.2.2", 4444);

                ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                os.writeObject(registerData);
                os.flush();

                message = (String) ois.readObject();
//                        os.close();
//                        ois.close();
//
//                        socket.close();


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
            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
            if (message.equals("registtartion done")) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }

        }
    }
}