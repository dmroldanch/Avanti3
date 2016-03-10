package mx.intelisis.maserp.avanti;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class cliente_c extends ActionBarActivity {


    EditText nombreCliente,direccion,colonia,num_ext,num_int,cpostal,poblacion,pais,estado,rfc,telefono;
    String  nombreCliente_s,direccion_s,colonia_s,num_ext_s,num_int_s,cpostal_s,poblacion_s,pais_s,estado_s,rfc_s,telefono_s;
    ImageButton boton_cliente;
    String idc,respuesta=null,macAddress;
    String ip = "187.163.97.114:8080";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_c);
        cargarmac();
        inicializarComponentes();
        botonClienteNuevo();
    }

    //cargar componentes
    private void inicializarComponentes() {


        nombreCliente = (EditText) findViewById(R.id.cliente_n);
        direccion = (EditText) findViewById(R.id.direccion_n);
        colonia = (EditText) findViewById(R.id.colonia_n);
        num_ext = (EditText) findViewById(R.id.num_ext_n);
        num_int= (EditText) findViewById(R.id.num_int_n);
        cpostal= (EditText) findViewById(R.id.cpostal_n);
        poblacion= (EditText) findViewById(R.id.poblacion_n);
        pais= (EditText) findViewById(R.id.pais_n);
        estado= (EditText) findViewById(R.id.estado_n);
        rfc= (EditText) findViewById(R.id.rfc_n);
        telefono= (EditText) findViewById(R.id.telefono_n);
        boton_cliente = (ImageButton) findViewById(R.id.boton_cliente_n);
    }

    public void llenardatos() {

        nombreCliente_s = nombreCliente.getText().toString();
        direccion_s = direccion.getText().toString();
        colonia_s=colonia.getText().toString();
        num_ext_s=num_ext.getText().toString();
        num_int_s=num_int.getText().toString();
        cpostal_s =cpostal.getText().toString();
        poblacion_s=poblacion.getText().toString();
        pais_s =pais.getText().toString();
        estado_s=estado.getText().toString();
        rfc_s =rfc.getText().toString();
        telefono_s =telefono.getText().toString();

    }
    public void cargarmac(){
        SharedPreferences preferencias = getSharedPreferences("datos_mac", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        macAddress = preferencias.getString("mac", "");
    }



    public void botonClienteNuevo() {


        boton_cliente.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                llenardatos();
                enviarDatos();
            }
        });
    }

    public void regresarMain(){
        Intent intent = new Intent(cliente_c.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cliente_c, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void enviarDatos() {

        AsyncHttpClient client = new AsyncHttpClient();
        String url_data = "http://"+ip+"/CteApp.php";
        RequestParams parametros = new RequestParams();
        String nombrel = nombreCliente_s;
        String direl = direccion_s;
        String numel = num_ext_s;
        String numil = num_int_s;
        String coll = colonia_s;
        String poll= poblacion_s;
        String paisl = pais_s;
        String cposl = cpostal_s;
        String rfcl = rfc_s;

        try {
            JSONObject data = new JSONObject();

            data.put("MacAdress",macAddress);
            data.put("Empresa","RTM");
            data.put("Nombre",nombrel);
            data.put("Direccion",direl);
            data.put("DireccionNumero",numel );
            data.put("DireccionNumeroInt", numil);
            data.put("Colonia",coll);
            data.put("Poblacion", poll);
            data.put("Pais", paisl);
            data.put("Estado", estado_s);
            data.put("CodigoPostal", cposl);
            data.put("Telefonos",telefono_s);
            data.put("RFC", rfc_s);
            data.put("Estatus", "ALTA");

            // Toast.makeText(this, data.toString(), Toast.LENGTH_LONG).show();
            parametros.put("data",data);

           // Toast.makeText(this, parametros.toString(), Toast.LENGTH_LONG).show();

            client.post(url_data, parametros, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    //  llamar funcion ..
                   mensajeRegreso(new String(responseBody));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    System.out.print("No se envio");
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

   public void mensajeRegreso(String response) {
        Toast.makeText(this, "Se envio...esperando respuesta", Toast.LENGTH_LONG).show();
        try {

            JSONArray jsonArray = new JSONArray(response);

            idc = jsonArray.getJSONObject(0).getString("Cte") ;
            respuesta= jsonArray.getJSONObject(0).getString("Ref") ;

            Toast.makeText(this, "Respuesta : " + respuesta,Toast.LENGTH_LONG).show();

            if (respuesta.equals("Ok")){
                SQLHelper baseHelper = new SQLHelper(this, "Avanti", null, 1);
                SQLiteDatabase db = baseHelper.getWritableDatabase();

                ContentValues nuevoregistroA = new ContentValues();
                nuevoregistroA.put("cliente",idc);
                nuevoregistroA.put("nombrec",nombreCliente_s);

                db.insert("Cliente", null, nuevoregistroA);

                Toast.makeText(getApplicationContext(), "Cliente Agregado" , Toast.LENGTH_LONG).show();
                db.close();
                regresarMain();
            }

        } catch (Exception e) {

        }

    }

}
