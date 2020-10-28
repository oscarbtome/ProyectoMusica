package com.example.proyectoappmusica;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class menuUsuario extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject> {

    private SessionUserData sessionUserData;
    private JsonObjectRequest objetoJson;
    private RequestQueue cola;
    private Context ctx;
    private String operacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_usuario);
        Intent intent = getIntent();
        sessionUserData = (SessionUserData) intent.getSerializableExtra("userSession");
        cola = Volley.newRequestQueue(getApplicationContext());
        ctx = getApplicationContext();
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

    public void eliminar (View view){
        Intent intent = new Intent(getApplicationContext(), listaCanciones.class);
        intent.putExtra("operacion", "eliminar");
        intent.putExtra("userSession", sessionUserData);
        startActivity(intent);
    }


    public void lista (View view){
        Intent intent = new Intent(getApplicationContext(), listaCanciones.class);
        intent.putExtra("operacion", "listar");
        intent.putExtra("userSession", sessionUserData);
        startActivity(intent);
    }


    public void modificarPassword(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Introduce contraseña actual");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if(MD5Encrypt.generator(input.getText().toString()).equals(sessionUserData.getPassword())){
                        operacion = "MODIFICAR";
                        pedirPasswordNueva();
                    }
                    else{
                        Toast.makeText(ctx, "[Error]: contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ctx, "[Error]: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    public void eliminarUsuario(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Introduce contraseña para verificar operación: ");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if(MD5Encrypt.generator(input.getText().toString()).equals(sessionUserData.getPassword())){
                        operacion = "ELIMINAR";
                        System.out.println(String.format("http://192.168.0.17/proyectoAndroid/Operaciones.php?eliminarUsuario&nombreUsuario=%s&password=%s", sessionUserData.getUserName(), sessionUserData.getPassword()));
                        addJson(String.format("http://192.168.0.17/proyectoAndroid/Operaciones.php?borrarUsuario&nombreUsuario=%s&password=%s", sessionUserData.getUserName(), sessionUserData.getPassword()));
                    }
                    else{
                        Toast.makeText(ctx, "[Error]: contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ctx, "[Error]: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }



    private void pedirPasswordNueva(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Introduce nueva contraseña");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText().toString().trim().length() > 0){
                    try {
                        addJson(String.format("http://192.168.0.17/proyectoAndroid/Operaciones.php?modificarPassword&nombreUsuario=%s&passwordOld=%s&password=%s", sessionUserData.getUserName(), sessionUserData.getPassword(), MD5Encrypt.generator(input.getText().toString())));
                    } catch (Exception e) {
                        Toast.makeText(ctx, "[Error]: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(ctx, "[Error]: no has introducido valores.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }


    private void addJson(String url){
        objetoJson = new JsonObjectRequest(Request.Method.GET, url, null,this,this);
        cola.add(objetoJson);
    }

    @Override
    public void onResponse(JSONObject response) {
        try{
            JSONArray arrayJson = response.optJSONArray("datos");
            JSONObject objetoJson = null;
            objetoJson = arrayJson.getJSONObject(0);
            String resultado = objetoJson.optString("resultado");
            if (resultado.equals("TRUE")) {
                if(operacion.equals("MODIFICAR")) {
                    Toast.makeText(getApplicationContext(), "Contraseña actualizada con exito.", Toast.LENGTH_LONG).show();
                }
                else{ //Cerrar ventana despues de operacion correcta de borrar el usuario
                    setResult(RESULT_OK);
                    finish();
                }
            }
            else{
                if(operacion.equals("MODIFICAR")){
                    Toast.makeText(ctx, "[Error]: no se pudo actualizar la contraseña.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(ctx, "[Error]: no se pudo borrar cuenta de usuario.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "[Error]: " +e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(ctx, "[Error]: "+error, Toast.LENGTH_SHORT).show();
    }


}