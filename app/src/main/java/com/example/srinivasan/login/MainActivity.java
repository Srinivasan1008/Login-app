package com.example.srinivasan.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {
    private EditText Name;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private Button fingerpt;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private TextView forgotPassword;

    private int counter = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Name = (EditText) findViewById(R.id.etName);
        Password = (EditText) findViewById(R.id.etPassword);
        Info = (TextView) findViewById(R.id.tvinfo);
        Login = (Button) findViewById(R.id.btnLogin);
        forgotPassword = (TextView)findViewById(R.id.tvForgotPassword);
        fingerpt = (Button) findViewById(R.id.btnfp);

        Info.setText("No of attempts remaining: 5");
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!= null )
        {
            finish();//destorys the activity
            startActivity(new Intent(MainActivity.this,SecondActivity.class));
        }

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String Mname = Name.getText().toString();
               String Mpassword = Password.getText().toString();
                if(Mname.isEmpty() || Mpassword.isEmpty()) {
                    Toast.makeText(MainActivity.this,"Please enter all the details",Toast.LENGTH_SHORT).show();
                }
                  else {
                    validate(Mname, Mpassword);
                }


            }
        });
        fingerpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //startActivity(new Intent(MainActivity.this,Fingerprint.class));

            }
        });


        Button UserSighup;
        UserSighup = (Button) findViewById(R.id.btnca);
        UserSighup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextpage();

            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,PasswordActivity.class));
            }
        });


    }

    private void nextpage() {
        Intent intent = new Intent(MainActivity.this, SighupActivity.class);
        startActivity(intent);
    }


    private void validate(String userName, String userPassword) {
        progressDialog.setMessage("My first app ...wait for verification");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    //Toast.makeText(MainActivity.this,"Login successfull",Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(MainActivity.this,SecondActivity.class));
                    checkEmailVerificatioin();

                }
                else {

                    Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    counter--;
                    Info.setText("No of attempts remaining: "+ counter);
                    progressDialog.dismiss();
                    if (counter == 0)
                    {
                        Login.setEnabled(false);
                    }
                }

            }
        });

    }
    private void checkEmailVerificatioin()
    {
    FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
    Boolean emailflag = firebaseUser.isEmailVerified();
    if(emailflag)
    {
        finish();
        startActivity(new Intent(MainActivity.this,SecondActivity.class));
    }
    else{
        Toast.makeText(this,"verify your email",Toast.LENGTH_SHORT).show();
        firebaseAuth.signOut();
    }
    }
}
