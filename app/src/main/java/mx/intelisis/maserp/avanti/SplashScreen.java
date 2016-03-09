package mx.intelisis.maserp.avanti;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.cache.Resource;

/**
 * Created by Roldan on 16/10/15.
 */
public class SplashScreen extends Activity {

    boolean hilo = true ;

    String ip= "187.163.97.114:8080" ;
    String macAddress;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        cargarpreferencias();
        macAddress();
       // hilo = false;
       if (hilo == true ) {

            Toast.makeText(getApplicationContext(), "Se cargara la base de datos de los Clientes esto puede demorar unos minutos", Toast.LENGTH_LONG).show();
            MiTareaAsincrona tareaHilo = new MiTareaAsincrona(this.getApplicationContext());
            tareaHilo.execute();
            hilo = false;
            condicionhilo("hilo",hilo);
        }else {
          //   terminarActividad();
           ObjDatosV();
        }

    }

    public void macAddress(){

        if(macAddress == null) {
            WifiManager wimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            macAddress = wimanager.getConnectionInfo().getMacAddress();
            if (macAddress == null) {
                macAddress = "EMULADOR";
            }
            guardarpreferencias("mac", macAddress);
        }
        Toast.makeText(this,"Su MacAddress es: "+ macAddress,Toast.LENGTH_LONG).show();
    }

    public void guardarpreferencias(String nombre,String s){
        SharedPreferences preferencias = getSharedPreferences("datos_mac",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString(nombre, s);
        editor.commit();
    }


    public void condicionhilo(String nombre,boolean s){
        SharedPreferences preferencias = getSharedPreferences("datos_hilo",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putBoolean(nombre, s);
        editor.commit();
    }

    public void cargarpreferencias(){
        SharedPreferences preferencias = getSharedPreferences("datos_hilo", Context.MODE_PRIVATE);
        hilo = preferencias.getBoolean("hilo",true);
    }


    public void ObjDatosV() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url_v = "http://"+ip+"/consultaAgente.php";//
        RequestParams parametros = new RequestParams();

        client.post(url_v, parametros, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    //  llamar funcion ..
                    objDatosJSONV(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    public void objDatosJSONV(String response) {

        SQLHelper baseHelper = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = baseHelper.getWritableDatabase();

        Cursor c1 = db.rawQuery("select * from Agente", null);
        int cantidad = c1.getCount();


        String texto_v;

        try {
            JSONArray jsonArray = new JSONArray(response);
            Toast.makeText(getApplicationContext(), "Cargando Agentes", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Datos en base local : " + cantidad + "\n" + "Datos en base remota : " + jsonArray.length(), Toast.LENGTH_LONG).show();


            if(cantidad<jsonArray.length()) {
                for (int i = 0; i < jsonArray.length(); i++) {


                    ContentValues nuevoregistroA = new ContentValues();
                    nuevoregistroA.put("Agente", jsonArray.getJSONObject(i).getString("Agente"));
                    nuevoregistroA.put("nombrev", jsonArray.getJSONObject(i).getString("Nombre"));
                    db.insert("Agente", null, nuevoregistroA);
                 //   System.out.println("entro hilo" + i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        Toast.makeText(getApplicationContext(), "Agentes Cargados" , Toast.LENGTH_LONG).show();
        ObjDatosA();
    }

    public void ObjDatosA() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url_v = "http://"+ip+"/consultaArticulo.php";
        RequestParams parametros = new RequestParams();

        client.post(url_v, parametros, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    //  llamar funcion ..
                    objDatosJSONA(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    public void objDatosJSONA(String response) {

        SQLHelper baseHelper = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = baseHelper.getWritableDatabase();

        Cursor c1 = db.rawQuery("select * from Articulo", null);
        int cantidad2 = c1.getCount();

        try {
            JSONArray jsonArray2 = new JSONArray(response);
            Toast.makeText(getApplicationContext(), "Cargando Articulos", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Datos en base local : " + cantidad2 + "\n" + "Datos en base remota : " + jsonArray2.length(), Toast.LENGTH_LONG).show();

            if(cantidad2<jsonArray2.length()) {
                for (int i = 0; i < jsonArray2.length(); i++) {

                    ContentValues nuevoregistroA = new ContentValues();
                    nuevoregistroA.put("id",jsonArray2.getJSONObject(i).getString("Articulo"));
                    nuevoregistroA.put("nombrea",jsonArray2.getJSONObject(i).getString("Descripcion"));
                    nuevoregistroA.put("precio",jsonArray2.getJSONObject(i).getString("Precio") );

                    db.insert("Articulo", null,nuevoregistroA);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "Articulos Cargados" , Toast.LENGTH_LONG).show();
        db.close();
        ObjDatosCon();
    }


    public void ObjDatosCon() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url_con = "http://"+ip+"/Condiciones.php";
        RequestParams parametros = new RequestParams();

        client.post(url_con, parametros, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    //  llamar funcion ..
                    Condiciones(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    public void Condiciones(String response) {

        SQLHelper baseHelper = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = baseHelper.getWritableDatabase();

        Cursor c1 = db.rawQuery("select * from Condicion", null);
        int cantidadcon = c1.getCount();


        try {
            JSONArray jsonArray = new JSONArray(response);
            Toast.makeText(getApplicationContext(), "Cargando Condiciones", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Datos en base local : " + cantidadcon + "\n" + "Datos en base remota : " + jsonArray.length(), Toast.LENGTH_LONG).show();


            if(cantidadcon<jsonArray.length()) {
                for (int i = 0; i < jsonArray.length(); i++) {


                    ContentValues nuevoregistroCon = new ContentValues();
                    nuevoregistroCon.put("condicion",jsonArray.getJSONObject(i).getString("Condicion") );
                    db.insert("Condicion", null, nuevoregistroCon);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "Condiciones Cargadas" , Toast.LENGTH_LONG).show();
        db.close();
       ObjDatosOpc();
    }


    public void ObjDatosOpc() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url_con = "http://"+ip+"/consultaOpciones.php";
        RequestParams parametros = new RequestParams();

        client.post(url_con, parametros, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    //  llamar funcion ..
                    Opciones(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    public void Opciones(String response) {

        SQLHelper baseHelper = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = baseHelper.getWritableDatabase();

        Cursor c1 = db.rawQuery("select * from ArtGrado", null);
        int cantidadopc = c1.getCount();


        try {
            JSONArray jsonArray = new JSONArray(response);
            Toast.makeText(getApplicationContext(), "Cargando Opciones", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Datos en base local : " + cantidadopc + "\n" + "Datos en base remota : " + jsonArray.length(), Toast.LENGTH_LONG).show();


            if(cantidadopc<jsonArray.length()) {
                for (int i = 0; i < jsonArray.length(); i++) {


                    ContentValues nuevoregistroOpc = new ContentValues();
                    nuevoregistroOpc.put("Articulo",jsonArray.getJSONObject(i).getString("Articulo") );
                    nuevoregistroOpc.put("Grupo",jsonArray.getJSONObject(i).getString("Grupo") );
                    nuevoregistroOpc.put("Subgrupo",jsonArray.getJSONObject(i).getString("Subgrupo") );
                    nuevoregistroOpc.put("Opcion",jsonArray.getJSONObject(i).getString("Opcion") );
                    nuevoregistroOpc.put("Tipo",jsonArray.getJSONObject(i).getString("Tipo") );
                    nuevoregistroOpc.put("Grado",jsonArray.getJSONObject(i).getString("Grado") );
                    nuevoregistroOpc.put("Precio",jsonArray.getJSONObject(i).getDouble("Precio") );
                    db.insert("ArtGrado", null, nuevoregistroOpc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "Opciones Cargadas" , Toast.LENGTH_LONG).show();
        db.close();
        terminarActividad();
    }


    public void terminarActividad(){
        Intent nuevaActivity = new Intent(SplashScreen.this,MainActivity.class);
        startActivity(nuevaActivity);
        finish();

    }

}
