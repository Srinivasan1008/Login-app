package com.example.srinivasan.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordActivity extends AppCompatActivity {
    private EditText passwordemail;
    private Button resetpassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        passwordemail = (EditText)findViewById(R.id.etPasswordEmail);
        resetpassword = (Button)findViewById(R.id.btnPasswordReset);
        firebaseAuth = FirebaseAuth.getInstance();
        resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail = passwordemail.getText().toString().trim();
                if(useremail.equals(""))
                {
                    Toast.makeText(PasswordActivity.this,"please enter your registered email ID",Toast.LENGTH_SHORT).show();
                }
                else{
                    firebaseAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(PasswordActivity.this,"Password reset Email sent",Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(PasswordActivity.this,MainActivity.class));
                            }
                            else{
                                Toast.makeText(PasswordActivity.this,"Error in sending password reset email",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
