package com.example.proyectoappmusica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class UserMain extends AppCompatActivity {

    private SessionUserData sessionUserData;
    private TextView tvUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        Intent intent = getIntent();
        tvUsuario = (TextView)findViewById(R.id.tvUsuario);
        sessionUserData = (SessionUserData) intent.getSerializableExtra("userSession");
        tvUsuario.setText(sessionUserData.getUserName());
    }
}