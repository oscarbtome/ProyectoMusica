package com.example.proyectoappmusica;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject> {

    private EditText etNombreUsuario;
    private EditText etPassword;
    private EditText etPasswordRep;
    private EditText etEmail;
    private CheckBox cbUltimoLogin;
    private CheckBox cbRegistrar;
    private Button btnAccion;
    private JsonObjectRequest objetoJson;
    private RequestQueue cola;
    private String operacion;
    private MD5Encrypt md5Encrypt = new MD5Encrypt();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sEditor;
    private SessionUserData sessionUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etNombreUsuario = (EditText)findViewById(R.id.etNombreUsuario);
        etPassword = (EditText)findViewById(R.id.etPassword);
        etPasswordRep = (EditText)findViewById(R.id.etPasswordRep);
        etPasswordRep.setVisibility(View.GONE);
        etEmail = (EditText)findViewById(R.id.etEmail) ;
        etEmail.setVisibility(View.GONE);
        cbUltimoLogin = (CheckBox)findViewById(R.id.cbUltimoLogin);
        cbRegistrar = (CheckBox)findViewById(R.id.cbRegistrar);
        btnAccion = (Button)findViewById(R.id.btnAccion);
        cola = Volley.newRequestQueue(getApplicationContext());
        sharedPreferences = getSharedPreferences("properties", MODE_PRIVATE);
        sEditor = sharedPreferences.edit();
        if(sharedPreferences.getBoolean("ultimoLoginMarca", false)){
            cbUltimoLogin.setChecked(true);
            etNombreUsuario.setText(sharedPreferences.getString("ultimoLoginTexto", ""));
        }
    }


    public void listenerRecordarUsuario(View view){
        sEditor.putBoolean("ultimoLoginMarca", cbUltimoLogin.isChecked());
        sEditor.commit();
    }


    public void listenerRegistrar(View view){
        if(cbRegistrar.isChecked()){
            etEmail.setVisibility(View.VISIBLE);
            etPasswordRep.setVisibility(View.VISIBLE);
            etNombreUsuario.setHint("Introduce nombre de usuario");
            btnAccion.setText("REGISTRAR");
            cbUltimoLogin.setVisibility(View.GONE);
        }
        else {
            etEmail.setVisibility(View.GONE);
            etEmail.setText("");
            etPasswordRep.setVisibility(View.GONE);
            etPasswordRep.setText("");
            etNombreUsuario.setHint("Introduce usuario o E-mail");
            btnAccion.setText("ENTRAR");
            cbUltimoLogin.setVisibility(View.VISIBLE);
        }
    }


    public void btnClick(View view){
        if(etNombreUsuario.getText().toString().trim().length() > 0){
            if(cbUltimoLogin.isChecked()){ //Borrar ultimo nombre de usuario sera valido o no el nombre actual
                sEditor.putString("ultimoLoginTexto", etNombreUsuario.getText().toString());
            }
            else{
                sEditor.putString("ultimoLoginTexto", "");
            }
            sEditor.commit(); //Guardar cambios en SharedPreferences
            if(etPassword.getText().toString().trim().length() > 0) {
                try {
                    if (cbRegistrar.isChecked()) {
                        if (etPasswordRep.getText().toString().trim().length() > 0) {
                            if (etPassword.getText().toString().equals(etPasswordRep.getText().toString())) {
                                if(etEmail.getText().toString().trim().length() > 0) {
                                    //REGISTRAR USUARIO
                                    operacion = "REGISTRAR";
                                    addJson(String.format("http://192.168.0.17/proyectoAndroid/Login.php?registrarUsuario&nombreUsuario=%s&password=%s&emailUsuario=%s", etNombreUsuario.getText().toString(), md5Encrypt.generator(etPassword.getText().toString()), etEmail.getText().toString()));
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "[Error]: rellena el campo email.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "[Error]: las contraseñas no coinciden.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "[Error]: rellena el campo repetir contraseña.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        //ACCEDER USUARIO
                        operacion = "ACCEDER";
                        System.out.println(String.format("http://192.168.0.17/proyectoAndroid/Login.php?comprobarLogin&nombreUsuario=%s&password=%s", etNombreUsuario.getText().toString(), md5Encrypt.generator(etPassword.getText().toString())));

                        addJson(String.format("http://192.168.0.17/proyectoAndroid/Login.php?comprobarLogin&nombreUsuario=%s&password=%s&super=null", etNombreUsuario.getText().toString(), md5Encrypt.generator(etPassword.getText().toString())));
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "[Error]: no se pudo hasear la contraseña", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "[Error]: rellena el campo contraseña", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "[Error]: rellena el campo usuario", Toast.LENGTH_LONG).show();
        }
    }


    private void addJson(String url){
        objetoJson = new JsonObjectRequest(Request.Method.GET, url, null,this,this);
        cola.add(objetoJson);
        disableWidgets();
    }

    private void enableWidgets(){
        btnAccion.setEnabled(true);
        etNombreUsuario.setEnabled(true);
        etPassword.setEnabled(true);
        etPasswordRep.setEnabled(true);
        etEmail.setEnabled(true);
    }

    private void disableWidgets(){
        btnAccion.setEnabled(false);
        etNombreUsuario.setEnabled(false);
        etPassword.setEnabled(false);
        etPasswordRep.setEnabled(false);
        etEmail.setEnabled(false);
    }


    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONArray arrayJson = response.optJSONArray("datos");
            JSONObject objetoJson = null;
            if (arrayJson.length() > 0) {
                objetoJson = arrayJson.getJSONObject(0);
                String resultado = objetoJson.optString("resultado");
                if (resultado.equals("TRUE")) {
                    if (operacion.equals("ACCEDER")) {
                        try {
                            sessionUserData = new SessionUserData(etNombreUsuario.getText().toString(), md5Encrypt.generator(etPassword.getText().toString()));
                            Intent intent = new Intent(getApplicationContext(), menuUsuario.class);
                            intent.putExtra("userSession", sessionUserData);
                            startActivityForResult(intent, 0);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else { //Mostrar mensaje de usuario registrado
                        Toast.makeText(getApplicationContext(), "Usuario registrado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "[Error]: no hubo respuesta de la BBDD.", Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "[Error]: " +e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally {
            enableWidgets();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        enableWidgets();
        Toast.makeText(getApplicationContext(), "[Error]: " + error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Toast.makeText(getApplicationContext(), "¡Cuenta de usuario eliminada con éxito!", Toast.LENGTH_LONG).show();
        }
        etPassword.setText(""); //Limpiar campo contraseña
        sessionUserData = null; //Limpiar datos de sesion anterior
    }
}