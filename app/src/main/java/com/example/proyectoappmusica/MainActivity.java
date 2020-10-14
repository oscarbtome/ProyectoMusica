package com.example.proyectoappmusica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class MainActivity extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject> {

    private EditText etNombreUsuario;
    private EditText etPassword;
    private EditText etEmail;
    private CheckBox cbRegistrar;
    private String url;
    private JsonObjectRequest objetoJson;
    private Button btnAccion;
    private RequestQueue cola;
    private String operacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNombreUsuario = (EditText)findViewById(R.id.etNombreUsuario);
        etPassword = (EditText)findViewById(R.id.etPassword);
        etEmail = (EditText)findViewById(R.id.etEmail) ;
        etEmail.setVisibility(View.GONE);
        cbRegistrar = (CheckBox)findViewById(R.id.cbRegistrar);
        btnAccion = (Button)findViewById(R.id.btnAccion);
        cola = Volley.newRequestQueue(getApplicationContext());
    }


    public void listenerRegistrar(View view){
        if(cbRegistrar.isChecked()){
            etEmail.setVisibility(View.VISIBLE);
            etNombreUsuario.setHint("Usuario");
            btnAccion.setText("REGISTRAR");
        }
        else {
            etEmail.setVisibility(View.GONE);
            etNombreUsuario.setHint("Usuario o E-Mail");
            btnAccion.setText("ENTRAR");
        }
    }


    public void btnClick(View view){
        btnAccion.setEnabled(false);
        etNombreUsuario.setEnabled(false);
        etPassword.setEnabled(false);
        etEmail.setEnabled(false);
        if(etNombreUsuario.getText().toString().trim().length() > 0 && etPassword.getText().toString().trim().length() > 0){
            if(cbRegistrar.isChecked()){
                if(etEmail.getText().toString().trim().length() > 0){
                    //REGISTRAR USUARIO
                    url = String.format("http://192.168.0.17/proyectoAndroid/Login.php?comprobarLogin&nombreUsuario=%s&password=%s", etNombreUsuario.getText().toString(), etPassword.getText().toString());
                    operacion = "REGISTRAR";
                    objetoJson = new JsonObjectRequest(Request.Method.GET, url, null,this,this);
                    cola.add(objetoJson);
                }
                else{
                    Toast.makeText(getApplicationContext(), "[Error]: rellena el campo email.", Toast.LENGTH_LONG).show();
                }
            }
            else{
                //ACCEDER USUARIO
                url = String.format("http://192.168.0.17/proyectoAndroid/Login.php?comprobarLogin&nombreUsuario=%s&password=%s", etNombreUsuario.getText().toString(), etPassword.getText().toString());
                operacion = "ACCEDER";
                objetoJson = new JsonObjectRequest(Request.Method.GET, url, null,this,this);
                cola.add(objetoJson);
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "[Error]: rellena todos los campos", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        JSONArray arrayJson = response.optJSONArray("datosLogin");
        JSONObject objetoJson = null;

        if(arrayJson.length()>0) {
            try {
                objetoJson = arrayJson.getJSONObject(0);
                if(operacion.equals("ACCEDER")){
                    String nombreUsuario = objetoJson.optString("nombreUsuario");
                    String email = objetoJson.optString("emailUsuario");
                    String password = objetoJson.optString("password");
                    if(password.equals(etPassword.getText().toString()) && (nombreUsuario.equals(etNombreUsuario.getText().toString()) || email.equals(etNombreUsuario.getText().toString()))){
                        Toast.makeText(getApplicationContext(), "Usuario logueado", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrectas", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(operacion.equals("REGISTRAR")){
                    Toast.makeText(getApplicationContext(), "REGISTRAR", Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "[Error]: " +e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "[Error]: usuario o contraseña incorrecto", Toast.LENGTH_SHORT).show();
        }
        btnAccion.setEnabled(true);
        etNombreUsuario.setEnabled(true);
        etPassword.setEnabled(true);
        etEmail.setEnabled(true);

    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "[Error]: " + error.toString(), Toast.LENGTH_LONG).show();
    }
}