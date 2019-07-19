package com.example.srinivasan.login;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.support.annotation.NonNull;

import java.io.IOException;
public class SighupActivity extends AppCompatActivity {
    private EditText userName ,userPassword,userEmail,userAge;
    private Button regButton;
    private TextView userHaveacc;
    private FirebaseAuth firebaseAuth;
    private ImageView userProfilepic;
    String email,name,password,age;
    private FirebaseStorage firebaseStorage;
    private static int PICK_IMAGE  = 123;
    Uri imagePath;
    private StorageReference storageReference;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null)
        {
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver() , imagePath);
                userProfilepic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sighup);
        setupUIView();
        firebaseAuth =  FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();


        storageReference = firebaseStorage.getReference();


        userProfilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                //application/* audio/*
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Image"),PICK_IMAGE);
            }
        });





        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                {
                    //update data to database
                    String useremail = userEmail.getText().toString().trim();
                    String userpassword = userPassword.getText().toString().trim();
                    firebaseAuth.createUserWithEmailAndPassword(useremail,userpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                //Toast.makeText(SighupActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(SighupActivity.this,MainActivity.class));

                                sendEmailVerification();

                            }
                            else{
                                Toast.makeText(SighupActivity.this, "Registration unsuccessful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        userHaveacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startActivity(new Intent(SighupActivity.this,MainActivity.class));
            }
        });
    }
    private void setupUIView(){
        userName = (EditText)findViewById(R.id.Username);
        userPassword = (EditText)findViewById(R.id.userPassword);
        userEmail = (EditText)findViewById(R.id.Useremail);
        regButton = (Button)findViewById(R.id.btnSighup);
        userHaveacc = (TextView)findViewById(R.id.tvHaveacc);
        userAge = (EditText) findViewById(R.id.etAge);
        userProfilepic = (ImageView)findViewById(R.id.ivProfile);
    }
    private  boolean validate()
    {
        boolean result=false;
        name = userName.getText().toString();
        password = userPassword.getText().toString();
        email =userEmail.getText().toString();
        age = userAge.getText().toString();


        if(name.isEmpty() || password.isEmpty() || email.isEmpty() || age.isEmpty() || imagePath == null)
        {
            Toast.makeText(this,"Please enter all the details",Toast.LENGTH_SHORT).show();

        }
        else{
            result = true;
        }
        return result;
    }

    private void sendEmailVerification()
    {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null)
        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        sendUserData();
                        Toast.makeText(SighupActivity.this,"Successfully Registered,Verification mail sent!",Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();

                        startActivity(new Intent(SighupActivity.this,MainActivity.class));

                    }
                    else
                    {
                        Toast.makeText(SighupActivity.this,"Verification mail hasn't been sent",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }
    private void sendUserData()
    {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());
        StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile pic");//User Id/Images/profile_pic.png
        UploadTask uploadTask = imageReference.putFile(imagePath);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SighupActivity.this,"Upload failed",Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SighupActivity.this,"Upload successful",Toast.LENGTH_SHORT).show();

            }
        });
        UserProfile userProfile = new UserProfile(age,email,name);
        myRef.setValue(userProfile);
    }
}
