package com.example.proyectoappmusica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class menuUsuario extends AppCompatActivity {

    private SessionUserData sessionUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_usuario);
        Intent intent = getIntent();
        sessionUserData = (SessionUserData) intent.getSerializableExtra("userSession");
    }

    public void insertar (View view){
        Intent intent = new Intent(getApplicationContext(), insertarCancion.class);
        intent.putExtra("operacion", "insertar");
        intent.putExtra("userSession", sessionUserData);
        startActivity(intent);
    }

    public void modificar (View view){
        Intent intent = new Intent(getApplicationContext(), listaCanciones.class);
        intent.putExtra("operacion", "modificar");
        intent.putExtra("userSession", sessionUserData);
        startActivity(intent);
    }

    public void listaPropia (View view){
        Intent intent = new Intent(getApplicationContext(), listaCanciones.class);
        intent.putExtra("operacion", "listar");
        intent.putExtra("userSession", sessionUserData);
        startActivity(intent);
    }


    public void listaGeneral (View view){
        Intent intent = new Intent(getApplicationContext(), listaCanciones.class);
        intent.putExtra("operacion", "listar");
        intent.putExtra("userSession", sessionUserData);
        startActivity(intent);
    }

}