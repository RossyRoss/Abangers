package com.example.pc_user.abang.registration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pc_user.abang.R;

public class LoginActivity extends AppCompatActivity {
    private String userName;
    private String userPass;
    private Button btnLogin;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        refIds();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPass)) {
                    Toast.makeText(getApplication(), "Input all fields!",Toast.LENGTH_LONG).show();
                }
                else {
                    if(userName.equals("Charles") && userPass.equals("123")) {
                        Toast.makeText(getApplication(), "Invalid Credentials",Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplication(), "Invalid Credentials",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }
    private void refIds() {
        EditText etusername = (EditText) findViewById(R.id.etusername);
        EditText etpassword = (EditText) findViewById(R.id.etpassword);
        btnLogin = (Button) findViewById(R.id.btnlogin);
        btnRegister = (Button) findViewById(R.id.btnregister);
        userName = etusername.getText().toString();
        userPass = etpassword.getText().toString();
    }
}
