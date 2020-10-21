package com.example.proyectoappmusica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class insertarCancion extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject>{
    private EditText etTituloCancion;
    private EditText etAutorCancion;
    private EditText etDuracion;
    private EditText etEnlace;
    private SessionUserData sessionUserData;
    private String operacion;
    private String tituloCancionOld;
    private Button btnOperacion;
    private JsonObjectRequest objetoJson;
    private RequestQueue cola;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar_cancion);
        btnOperacion = (Button) findViewById(R.id.btnInsertar);
        etTituloCancion = (EditText)findViewById(R.id.etTituloCancion);
        etAutorCancion = (EditText)findViewById(R.id.etAutorCancion);
        etDuracion = (EditText)findViewById(R.id.etDuracion);
        etEnlace = (EditText)findViewById(R.id.etEnlace);
        cola = Volley.newRequestQueue(getApplicationContext());
        Intent intent = getIntent();
        sessionUserData = (SessionUserData) intent.getSerializableExtra("userSession");
        operacion = intent.getStringExtra("operacion");
        if(operacion.equals("insertar")){
            btnOperacion.setText("INSERTAR CANCIÓN");
        }
        else{
            btnOperacion.setText("MODIFICAR CANCIÓN");
            addJson(String.format("http://192.168.0.17/proyectoAndroid/Operaciones.php?getDatosCancion=%s", intent.getStringExtra("tituloCancion")));
        }
    }

    public void operacion(View view){
        if(etTituloCancion.getText().toString().trim().length() > 0 && etAutorCancion.getText().toString().trim().length() > 0 && etEnlace.getText().toString().trim().length() > 0 && etDuracion.getText().toString().trim().length() > 0){
            if(operacion.equals("insertar")){
                addJson(String.format("http://192.168.0.17/proyectoAndroid/Operaciones.php?insertarCancion&nombreUsuario=%s&password=%s&tituloCancion=%s&autorCancion=%s&enlace=%s&duracion=%s", sessionUserData.getUserName(), sessionUserData.getPassword(), etTituloCancion.getText().toString(), etAutorCancion.getText().toString(), etEnlace.getText().toString(), etDuracion.getText().toString()));
            }
            else{
                addJson(String.format("http://192.168.0.17/proyectoAndroid/Operaciones.php?actualizarCancion&nombreUsuario=%s&password=%s&tituloCancion=%s&tituloCancionOld=%s&autorCancion=%s&enlace=%s&duracion=%s", sessionUserData.getUserName(), sessionUserData.getPassword(), etTituloCancion.getText().toString(), tituloCancionOld, etAutorCancion.getText().toString(), etEnlace.getText().toString(), etDuracion.getText().toString()));
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "[Error]: rellena todos los campos", Toast.LENGTH_SHORT).show();
        }
    }


    private void addJson(String url){
        objetoJson = new JsonObjectRequest(Request.Method.GET, url, null,this,this);
        cola.add(objetoJson);
        disableWidgets();
    }

    private void enableWidgets(){
        btnOperacion.setEnabled(true);
        etTituloCancion.setEnabled(true);
        etAutorCancion.setEnabled(true);
        etDuracion.setEnabled(true);
        etEnlace.setEnabled(true);
    }

    private void disableWidgets(){
        btnOperacion.setEnabled(false);
        etTituloCancion.setEnabled(false);
        etAutorCancion.setEnabled(false);
        etDuracion.setEnabled(false);
        etEnlace.setEnabled(false);
    }


    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONArray arrayJson = response.optJSONArray("datos");
            JSONObject objetoJson = null;
            if (arrayJson.length() > 0) {

                if(operacion.equals("modificar")){
                    etTituloCancion.setText(objetoJson.optString("tituloCancion"));
                    etAutorCancion.setText(objetoJson.optString("autorCancion"));
                    etDuracion.setText(objetoJson.optString("duracion"));
                    etEnlace.setText(objetoJson.optString("enlace"));
                }
                else{
                    objetoJson = arrayJson.getJSONObject(0);
                    String resultado = objetoJson.optString("resultado");
                    if (resultado.equals("TRUE")) {
                        Toast.makeText(getApplicationContext(), "Canción insertada con éxito.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_SHORT).show();
                    }
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


}