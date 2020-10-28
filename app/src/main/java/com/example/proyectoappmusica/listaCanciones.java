package com.example.proyectoappmusica;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.ArrayList;

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
    private ArrayList<String> listaCanciones;
    private EditText etTituloBusqueda;
    private EditText etAutorBusqueda;
    private EditText etUsuarioBusqueda;
    private RadioButton rbPropias;
    private RadioButton rbTodas;
    private RadioButton rbFiltrar;

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
        etTituloBusqueda = (EditText)findViewById(R.id.etTituloBusqueda);
        etAutorBusqueda = (EditText)findViewById(R.id.etAutorBusqueda);
        etUsuarioBusqueda = (EditText)findViewById(R.id.etUsuarioBusqueda);
        etTituloBusqueda.setVisibility(View.GONE);
        etAutorBusqueda.setVisibility(View.GONE);
        etUsuarioBusqueda.setVisibility(View.GONE);
        rbPropias = (RadioButton)findViewById(R.id.rbPropias);
        rbTodas = (RadioButton)findViewById(R.id.rbTodas);
        rbFiltrar = (RadioButton)findViewById(R.id.rbFiltrar);
        subOperacion = "listaPropia";

        if(operacion.equals("modificar") || operacion.equals("eliminar")){
            tvCabeceraListaCanciones.setText("LISTA DE CANCIONES PROPIAS");
            rbPropias.setVisibility(View.GONE);
            rbTodas.setVisibility(View.GONE);
            rbFiltrar.setVisibility(View.GONE);
        }
        else{

            //METER AQUI LISTENERS PARA FILTROS

            rbPropias.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etTituloBusqueda.setVisibility(View.GONE);
                    etAutorBusqueda.setVisibility(View.GONE);
                    etUsuarioBusqueda.setVisibility(View.GONE);
                    subOperacion = "listaPropia";
                    addJson(String.format("http://192.168.0.17/proyectoAndroid/Operaciones.php?getCancionesUsuario&nombreUsuario=%s&password=%s", sessionUserData.getUserName(), sessionUserData.getPassword()));
                }
            });

            rbTodas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etTituloBusqueda.setVisibility(View.GONE);
                    etAutorBusqueda.setVisibility(View.GONE);
                    etUsuarioBusqueda.setVisibility(View.GONE);
                    subOperacion = "listaTotal";
                    addJson("http://192.168.0.17/proyectoAndroid/Operaciones.php?getCanciones");
                }
            });

            rbFiltrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etTituloBusqueda.setVisibility(View.VISIBLE);
                    etAutorBusqueda.setVisibility(View.VISIBLE);
                    etUsuarioBusqueda.setVisibility(View.VISIBLE);
                    subOperacion = "listaFiltro";
                    //addJson(String.format("http://192.168.0.17/proyectoAndroid/Operaciones.php?getCancionesUsuario&nombreUsuario=%s&password=%s", sessionUserData.getUserName(), sessionUserData.getPassword()));
                }
            });
        }

        lstCanciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                operacion = intent.getStringExtra("operacion");
                if(operacion.equals("modificar")){
                    intent = new Intent(getApplicationContext(), insertarCancion.class);
                    intent.putExtra("operacion", operacion);
                    intent.putExtra("userSession", sessionUserData);
                    intent.putExtra("tituloCancion", adapter.getItem(position));
                    startActivityForResult(intent, 0);
                }
                else if(operacion.equals("eliminar")){
                    String tituloCancion = adapter.getItem(position);
                    eleccion(String.format("¿Seguro que quieres eliminar la canción %s?", tituloCancion), tituloCancion);
                }
                else{
                    intent = new Intent(getApplicationContext(), insertarCancion.class);
                    intent.putExtra("operacion", operacion);
                    intent.putExtra("userSession", sessionUserData);
                    intent.putExtra("tituloCancion", adapter.getItem(position));
                    System.out.println("VALOOOOOOOOOOOOOOR: "+adapter.getItem(position));
                    startActivity(intent);
                }
            }
        });

        //Cargar canciones del usuario por defecto
        addJson(String.format("http://192.168.0.17/proyectoAndroid/Operaciones.php?getCancionesUsuario&nombreUsuario=%s&password=%s", sessionUserData.getUserName(), sessionUserData.getPassword()));
    }



    private void eleccion(String cadena, final String tituloCancion){
        //se prepara la alerta creando nueva instancia
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        //seleccionamos la cadena a mostrar
        alertbox.setMessage(cadena);
        //elegimos un positivo SI y creamos un Listener
        alertbox.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            //Funcion llamada cuando se pulsa el boton Si
            public void onClick(DialogInterface arg0, int arg1) {
                subOperacion="";
                addJson(String.format("http:/192.168.0.17/proyectoAndroid/Operaciones.php?borrarCancion&nombreUsuario=%s&password=%s&tituloCancion=%s", sessionUserData.getUserName(), sessionUserData.getPassword(), tituloCancion));
            }
        });

        //elegimos un positivo NO y creamos un Listener
        alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
            //Funcion llamada cuando se pulsa el boton No
            public void onClick(DialogInterface arg0, int arg1) {}
        });
        //mostramos el alertbox
        alertbox.show();
    }


    private void addJson(String url){
        objetoJson = new JsonObjectRequest(Request.Method.GET, url, null,this,this);
        cola.add(objetoJson);
    }

    private void updateTable(){
        addJson(String.format("http://192.168.0.17/proyectoAndroid/Operaciones.php?getCancionesUsuario&nombreUsuario=%s&password=%s", sessionUserData.getUserName(), sessionUserData.getPassword()));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 0){
            updateTable();
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONArray arrayJson = response.optJSONArray("datos");
            JSONObject objetoJson = null;
            if (arrayJson.length() > 0) {
                if(subOperacion.equals("listaPropia") || subOperacion.equals("listaTotal") || subOperacion.equals("listaFiltro")){
                    listaCanciones = new ArrayList<String>();
                    for (int i = 0; i < arrayJson.length(); i++) {
                        objetoJson = arrayJson.getJSONObject(i);
                        listaCanciones.add(objetoJson.optString("tituloCancion"));
                    }
                    if(listaCanciones.size() == 0){
                        listaCanciones.clear();
                    }
                    adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, listaCanciones);
                    lstCanciones.setAdapter(adapter);
                }
                else{
                    objetoJson = arrayJson.getJSONObject(0);
                    String resultado = objetoJson.optString("resultado");
                    if (resultado.equals("TRUE")) {
                        subOperacion = "listaPropia";
                        updateTable();
                        Toast.makeText(getApplicationContext(), "Cancion borrada con éxito", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else{
                adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, new String[]{});
                lstCanciones.setAdapter(adapter);
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