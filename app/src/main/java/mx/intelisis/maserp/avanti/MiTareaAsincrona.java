package mx.intelisis.maserp.avanti;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by joseluisyanez on 15/10/15.
 */
public class MiTareaAsincrona extends AsyncTask<Void, Void, Void> {

    String ip = "187.163.97.114:8080";
    private final Context mContext;
    private final SQLHelper tb;
    private SQLiteDatabase db;

    public MiTareaAsincrona(Context context) {
        super();
        this.mContext = context;
        tb = new SQLHelper(mContext,"Avanti",null,1);
        db = tb.getWritableDatabase();
        System.out.println("entro_hilo");//imprime el numero de registros devueltos
        Toast.makeText(mContext, "Cargando Clientes", Toast.LENGTH_LONG).show();
    }
    @Override
    protected Void doInBackground(Void... params) {

        String jsonURL= "http://"+ip+"/consultaCliente.php";
        HttpClient httpClient = new DefaultHttpClient();

        HttpGet del = new HttpGet(jsonURL);

        del.setHeader("content-type", "application/json");

        try {
            HttpResponse resp = httpClient.execute(del);
            String respStr = EntityUtils.toString(resp.getEntity());

            SQLHelper baseHelper = new SQLHelper(mContext, "Avanti", null, 1);
            SQLiteDatabase db = baseHelper.getWritableDatabase();

            Cursor c1 = db.rawQuery("select * from Cliente", null);
            int cantidad2 = c1.getCount();

            JSONArray respJSON = new JSONArray(respStr);

            String[] clientes = new String[respJSON.length()];

        //    Toast.makeText(mContext, "Cargando Clientes", Toast.LENGTH_LONG).show();
        //    Toast.makeText(mContext, "Clientes : Datos en base local : " + cantidad2 + "\n" + "Datos en base remota : " + respJSON.length(), Toast.LENGTH_LONG).show();

            if (cantidad2 < respJSON.length()) {
                if (db != null) {
                    for (int i = 0; i <= respJSON.length(); i++) {

                        JSONObject obj = respJSON.getJSONObject(i);

                        String nombreC = obj.getString("Nombre");
                        String id = obj.getString("Cliente");

                        String sql = "INSERT INTO Cliente(cliente,nombrec) VALUES ('" + id + "','" + nombreC + "')";
                        System.out.println("entro_hilo" + i);
                        db.execSQL(sql);
                    }
                    db.close();
                }


            }
        }
        catch(Exception ex)
        {
            Log.e("ServicioRest", "Error!", ex);
        }





        Toast.makeText(mContext, "Clientes Cargados", Toast.LENGTH_LONG).show();

        return null;
    }
}