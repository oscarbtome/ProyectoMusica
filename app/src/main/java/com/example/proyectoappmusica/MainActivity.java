package com.example.proyectoappmusica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
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
    private CheckBox cbRegistrar;
    private String url;
    private JsonObjectRequest objetoJson;
    private RequestQueue cola;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNombreUsuario = (EditText)findViewById(R.id.etNombreUsuario);
        etPassword = (EditText)findViewById(R.id.etPassword);
        cbRegistrar = (CheckBox)findViewById(R.id.cbRegistrar);
        ctx = getApplicationContext();
        url = "http://192.168.0.17/proyectoAndroid/Login.php?comprobarLogin&username=Pedro1&password=1234";
        objetoJson = new JsonObjectRequest(Request.Method.GET, url, null,this,this);
        cola = Volley.newRequestQueue(getApplicationContext());
        cola.add(objetoJson);

    }

    @Override
    public void onResponse(JSONObject response) {
        JSONArray arrayJson = response.optJSONArray("datosLogin");
        JSONObject objetoJson = null;

        if(arrayJson.length()>0) {
            try {
                objetoJson = arrayJson.getJSONObject(0);
                String nombreUsuario = objetoJson.optString("nombreUsuario");
                Toast.makeText(getApplicationContext(), nombreUsuario, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "[Error]: usuario o contraseña incorrecto", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "[Error]: " + error.toString(), Toast.LENGTH_LONG).show();
    }
}