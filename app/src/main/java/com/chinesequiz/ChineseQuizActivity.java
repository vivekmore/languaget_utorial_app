package com.chinesequiz;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChineseQuizActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chinese_quiz);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email= user.getEmail();
            boolean emailVerified = user.isEmailVerified();
            if(!emailVerified) {
                Toast.makeText(this,"Please verify your email",Toast.LENGTH_LONG).show();
                setTitle(email+"[*]");
            }
            else{
                setTitle(email);
            }
        }
        else{
            startActivity(new Intent(this,LoginActivity.class));
        }
//        FloatingActionButton logoutBtn = (FloatingActionButton) findViewById(R.id.logoutBtn);
//        logoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mAuth.signOut();
//                startActivity(new Intent(ChineseQuizActivity.this,LoginActivity.class));
//            }
//        });
    }
}
