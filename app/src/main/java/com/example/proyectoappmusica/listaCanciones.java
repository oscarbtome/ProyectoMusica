package com.example.proyectoappmusica;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

public class listaCanciones extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject>{

    private ListView lstCanciones;
    private TextView tvCabeceraListaCanciones;
    private Intent intent;
    private SessionUserData sessionUserData;
    private String operacion;
    private String subOperacion;
    private JsonObjectRequest objetoJson;
    private RequestQueue cola;
    private ArrayAdapter<String> adapter;
    private String[] listaCanciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_canciones);

        lstCanciones = (ListView) findViewById(R.id.lstListaCanciones);
        tvCabeceraListaCanciones = (TextView) findViewById(R.id.tvLCabeceraLista);
        cola = Volley.newRequestQueue(getApplicationContext());
        intent = getIntent();
        sessionUserData = (SessionUserData) intent.getSerializableExtra("userSession");
        operacion = intent.getStringExtra("operacion");
        if(operacion.equals("modificar")){
            tvCabeceraListaCanciones.setText("LISTA DE CANCIONES PROPIAS");
            subOperacion = "listaPropia";
            addJson(String.format("http://192.168.0.17/proyectoAndroid/Operaciones.php?getCancionesUsuario&nombreUsuario=%s&password=%s", sessionUserData.getUserName(), sessionUserData.getPassword()));
        }
        else{
            subOperacion = "listaTotales";
            tvCabeceraListaCanciones.setText("LISTA DE CANCIONES");
            addJson("http://192.168.0.17/proyectoAndroid/Operaciones.php?getCanciones");
        }

        lstCanciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(operacion.equals("modificar")){
                    intent = new Intent(getApplicationContext(), insertarCancion.class);
                    intent.putExtra("operacion", operacion);
                    intent.putExtra("userSession", sessionUserData);
                    intent.putExtra("tituloCancion", adapter.getItem(position));
                    startActivityForResult(intent, 0);
                }
            }
        });
    }


    private void addJson(String url){
        objetoJson = new JsonObjectRequest(Request.Method.GET, url, null,this,this);
        cola.add(objetoJson);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 0){
            addJson(String.format("http://192.168.0.17/proyectoAndroid/Operaciones.php?getCancionesUsuario&nombreUsuario=%s&password=%s", sessionUserData.getUserName(), sessionUserData.getPassword()));
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONArray arrayJson = response.optJSONArray("datos");
            JSONObject objetoJson = null;

            if (arrayJson.length() > 0) {
                listaCanciones = new String[arrayJson.length()];
                for (int i = 0; i < listaCanciones.length; i++) {
                    objetoJson = arrayJson.getJSONObject(i);
                    listaCanciones[i] = objetoJson.optString("tituloCancion");
                }
                adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, listaCanciones);
                lstCanciones.setAdapter(adapter);
            }
            else{
                Toast.makeText(getApplicationContext(), "[Error]: no hay canciones.", Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "[Error]: " +e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "[Error]: " + error.toString(), Toast.LENGTH_LONG).show();
    }

}