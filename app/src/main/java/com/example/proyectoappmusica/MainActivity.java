package com.example.proyectoappmusica;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText etNombreUsuario;
    private EditText etPassword;
    private CheckBox cbRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNombreUsuario = (EditText)findViewById(R.id.etNombreUsuario);
        etPassword = (EditText)findViewById(R.id.etPassword);
        cbRegistrar = (CheckBox)findViewById(R.id.cbRegistrar);

    }
}