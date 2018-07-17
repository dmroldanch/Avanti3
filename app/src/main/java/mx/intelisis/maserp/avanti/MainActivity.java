package mx.intelisis.maserp.avanti;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;



public class MainActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {


    private EditText vendedor_texto, cliente_texto;

    String condicionPago, idagente = "000001", idcliente = null, fecha;
    String clientPDF,condicionPDF;
    TextView textview_Regular,textView_MSI,textView_Contado;
    ListView listado_V, listado_C, listado_agregados;
    ArrayAdapter<String> adapterlv, adapterlc, adapterspin;
    EditText txtDate;
    LinearLayout clienteLayout, vendedorLayout, fechaLayout;
    ImageButton boton_enviar,boton_guardar,boton_enviar_ok,boton_guardar_ok;
    static final int DIALOG_ID = 0;
    int year_x, day_x, month_x;
    RelativeLayout contendorMain;
    listaCarrito[] datos;
    Spinner condiciones;
    CardView cardcliente, cardvededor, card_Condicion, cardlista,card_Total,card_Titulos;
    String macAddress = null;
    String ip ="187.163.97.114:8080";
    DecimalFormat currency = new DecimalFormat("###,###.##");
    String usuario;
    int sucursal;

    ArrayList<String> list = new ArrayList<String>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recibirdatos();
        cargarmac();
        InicializarFecha();
        Calendario();
        inicializarComponentes();
        botonEnviar();
        botonGuardar();
        llenarcond();
        cargarpreferencias();
        //leerObjetosV();
        leerObjetosC();
        listaelegidos();


        BorrarDeLista();
       // totalesDB();

    }

    public void recibirdatos(){
        Bundle bundle = this.getIntent().getExtras();
        usuario = bundle.getString("usuario");
        sucursal = bundle.getInt("sucursal");

      }

    //cargar componentes
    private void inicializarComponentes() {

        clienteLayout = (LinearLayout) this.findViewById(R.id.clientelayout);
      //  vendedorLayout = (LinearLayout) this.findViewById(R.id.vendedorLayout);
        fechaLayout = (LinearLayout) this.findViewById(R.id.fechaLayout);

        cardcliente = (CardView) findViewById(R.id.cardView_Cliente);
       // cardvededor = (CardView) findViewById(R.id.CardViewvendedor);
        card_Condicion = (CardView) findViewById(R.id.cardView_Condicion);
        cardlista = (CardView) findViewById(R.id.cardView_lista);
        card_Total = (CardView) findViewById(R.id.cardView_Total);
        card_Titulos  = (CardView) findViewById(R.id.cardView_Titulos);
       // vendedor_texto = (EditText) findViewById(R.id.Nombre_Vendedor);
       // listado_V = (ListView) findViewById(R.id.listaVendedor);//Listado vendedor


        cliente_texto = (EditText) findViewById(R.id.Nombre_cliente);
        listado_C = (ListView) findViewById(R.id.listaCliente);//Listado cliente

        listado_agregados = (ListView) findViewById(R.id.listView_Miembros);

        textView_Contado = (TextView)findViewById(R.id.contado_BD_cant);
        textView_MSI = (TextView)findViewById(R.id.msi_BD_cant);
        textview_Regular = (TextView)findViewById(R.id.total_BD_cant);

        boton_enviar_ok = (ImageButton) findViewById(R.id.boton_enviar_2);
        boton_guardar_ok = (ImageButton) findViewById(R.id.boton_guardar_2);

    }

    //se llena la lista de condiciones
   public void llenarcond() {
        SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = BH.getReadableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery("select * from Condicion", null);
            int cantidad = c.getCount();
            String[] arreglo = new String[cantidad];
            int i = 0;
            if (c.moveToFirst()) {
                do {
                    String linea = c.getString(0);

                    arreglo[i] = linea;

                    i++;
                } while (c.moveToNext());

            }
            adapterspin = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arreglo);
            condiciones = (Spinner) findViewById(R.id.condiciones_spinner);
            condiciones.setAdapter(adapterspin);
            condiciones.setOnItemSelectedListener(this);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        TextView myText = (TextView) view;
        condicionPago = (String) myText.getText();
        guardarpreferencias("condiciontexto", condicionPago);
        guardarprefesCond(position);
        listaelegidos();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onClick(View view) {


    }


    //se envia condicion de pago
    private void EnviarDatos() {

        Intent intent = new Intent(MainActivity.this, ProductoN.class);
        intent.putExtra("usuario", usuario);
        intent.putExtra("sucursal", sucursal);
        startActivity(intent);
    }

    public void cargarpreferencias() {
        SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
//        vendedor_texto.setText(preferencias.getString("nombre_v", ""));
        cliente_texto.setText(preferencias.getString("nombre_c", ""));
        clientPDF = preferencias.getString("nombre_c","");
        condiciones.setSelection(preferencias.getInt("condicion", 7));
       // idagente = preferencias.getString("id_v", "");
        idcliente = preferencias.getString("id_c", "");
       // macAddress = preferencias.getString("mac", "");
        condicionPago = preferencias.getString("condiciontexto", "Contado");
    //    ocultarlistaV();
     //   ocultarlistaC();
    }

    //carga Mac
    public void cargarmac(){
        SharedPreferences preferencias = getSharedPreferences("datos_mac",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        macAddress = preferencias.getString("mac", "");
    }

    public void guardarpreferencias(String nombre, String s) {
        SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString(nombre, s);
        editor.commit();
    }


    public void guardarprefesCond(Integer s) {
        SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putInt("condicion", s);
        editor.commit();
    }


    public void borrarpreferencias() {
        SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.clear();
        editor.commit();
    }


    public  void reiniciarActivity(Activity actividad) {
        Intent intent = new Intent();
        intent.setClass(actividad, actividad.getClass());
        intent.putExtra("usuario", usuario);
        intent.putExtra("sucursal", sucursal);
        //llamamos a la actividad
        actividad.startActivity(intent);
        //finalizamos la actividad actual
        actividad.finish();
    }


    private void botonEnviar() {

        boton_enviar = (ImageButton) findViewById(R.id.boton_enviar);
        boton_enviar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boton_enviar.setVisibility(LinearLayout.INVISIBLE);
                CargaListaC();
                agregarCliente();
                botonEnviarOk();
                ocultarlistaC();
            }
        });
    }

    private void botonGuardar() {
        boton_guardar = (ImageButton) findViewById(R.id.boton_guardar);
        boton_guardar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boton_guardar.setVisibility(LinearLayout.INVISIBLE);
                CargaListaC();
                agregarCliente();
                botonGuardarOk();
                ocultarlistaC();
            }
        });
    }

    private void botonEnviarOk() {



        boton_enviar_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                enviarEmail();
            }
        });
    }

    private void botonGuardarOk() {



        boton_guardar_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                enviarDatosjson();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Opciones menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.agregarCli) {
            Intent intent = new Intent(MainActivity.this, cliente_c.class);
            startActivity(intent);
        }


        if (id == R.id.NUevaCotizacion) {
            listaelegidosborrar();
            borrarpreferencias();
            reiniciarActivity(this);
            finish();
            Intent intent = new Intent(MainActivity.this, ProductoN.class);
            intent.putExtra("usuario", usuario);
            intent.putExtra("sucursal", sucursal);
            startActivity(intent);
            finish();
            return true;
        }


        if (id == R.id.Terminar) {
            borrarpreferencias();
            finish();
            System.exit(0);
        }

        if (id == R.id.carrito) {
            Intent intent = new Intent(MainActivity.this, ProductoN.class);
            intent.putExtra("usuario", usuario);
            intent.putExtra("sucursal", sucursal);
            startActivity(intent);
            finish();
        }



        return super.onOptionsItemSelected(item);
    }





  /*  public void leerObjetosV() {
        SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = BH.getReadableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery("select * from Agente", null);
            int cantidad = c.getCount();
            String[] arreglo = new String[cantidad];
            int i = 0;
            if (c.moveToFirst()) {
                do {
                    String nombre = c.getString(1);
                    arreglo[i] = nombre;
                    i++;
                } while (c.moveToNext());

            }
            adapterlv = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arreglo);
            listado_V = (ListView) findViewById(R.id.listaVendedor);
            listado_V.setAdapter(adapterlv);
            CargaListaV();
        }
    }*/

    public void BorrarDeLista() {

        SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
        final SQLiteDatabase db = BH.getReadableDatabase();

        listado_agregados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String elem_borrar = datos[position].getTitulo();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);


                builder.setTitle("¿Eliminar de la lista ?");
                builder.setMessage(elem_borrar);
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.execSQL("DELETE FROM Lista WHERE nombrelis='" + elem_borrar + "'");
                        listaelegidos();
                    }
                });
                builder.setNegativeButton("Cancelar", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        });


    }

/*
    public void CargaListaV() {

        listado_V.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = (String) listado_V.getItemAtPosition(position);

                guardarpreferencias("nombre_v", value);
                vendedor_texto.setText(value);
                obtenerIDVendedor(value);
                ocultarlistaV();
            }
        });

        //Filtro vendedor
        vendedor_texto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                adapterlv.getFilter().filter(s);
                mostrarlistaV();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }*/

/*
    public void obtenerIDVendedor(String value) {
        SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = BH.getReadableDatabase();
        Cursor c = db.rawQuery("Select Agente FROM Agente WHERE nombrev='" + value + "'", null);

        if (c.moveToFirst()) {
            do {
                idagente = c.getString(0);
            } while (c.moveToNext());
            guardarpreferencias("id_v", idagente);
        }
    }
*/
    public void obtenerIDCliente(String value) {
        SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = BH.getReadableDatabase();
        Cursor c = db.rawQuery("Select cliente FROM Cliente WHERE nombrec='" + value + "'", null);

        if (c.moveToFirst()) {
            do {
                idcliente = c.getString(0);
            } while (c.moveToNext());
            guardarpreferencias("id_c", idcliente);
        }
    }

 /*   public void mostrarlistaV() {
        listado_V.setVisibility(ListView.VISIBLE);
        entrarproducton.setVisibility(LinearLayout.GONE);
        cardcliente.setVisibility(CardView.GONE);
        cardfecha.setVisibility(CardView.GONE);
        cardlista.setVisibility(CardView.GONE);
    }*/

    public void ocultarlistaV() {

//        listado_V.setVisibility(ListView.GONE);
        boton_enviar.setVisibility(LinearLayout.VISIBLE);
      //  cardcliente.setVisibility(CardView.VISIBLE);
    //    cardfecha.setVisibility(CardView.VISIBLE);
        cardlista.setVisibility(CardView.VISIBLE);
    }

    //carga la lista Cliente

    public void agregarCliente(){
        cardcliente.setVisibility(CardView.VISIBLE);
        card_Condicion.setVisibility(CardView.VISIBLE);
        cardlista.setVisibility(CardView.GONE);
        card_Titulos.setVisibility(CardView.GONE);
        card_Total.setVisibility(CardView.GONE);
    }

    public void leerObjetosC() {
        SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = BH.getReadableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery("select * from Cliente", null);
            int cantidad = c.getCount();
            String[] arreglo = new String[cantidad];
            int i = 0;
            if (c.moveToFirst()) {
                do {
                    String linea = c.getString(1);

                    arreglo[i] = linea;

                    i++;
                } while (c.moveToNext());

            }
            adapterlc = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arreglo);
            listado_C = (ListView) findViewById(R.id.listaCliente);
            listado_C.setAdapter(adapterlc);

        }
    }


    public void CargaListaC() {

        listado_C.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value_c = (String) listado_C.getItemAtPosition(position);
                guardarpreferencias("nombre_c", value_c);
                cliente_texto.setText(value_c);
                clientPDF = value_c;
                obtenerIDCliente(value_c);
                ocultarlistaC();
                mostrarBotones();
            }
        });

        //Filtro cliente
        cliente_texto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                adapterlc.getFilter().filter(s);
                mostrarlistaC();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void mostrarlistaC() {
        listado_C.setVisibility(ListView.VISIBLE);
        boton_enviar.setVisibility(LinearLayout.GONE);
        cardlista.setVisibility(CardView.GONE);
        card_Condicion.setVisibility(CardView.GONE);

 //       cardvededor.setVisibility(CardView.GONE);
    }

    public void ocultarlistaC() {

        listado_C.setVisibility(ListView.GONE);
        boton_enviar.setVisibility(LinearLayout.VISIBLE);
        cardlista.setVisibility(CardView.VISIBLE);
        card_Titulos.setVisibility(CardView.VISIBLE);
        card_Total.setVisibility(CardView.VISIBLE);
        card_Condicion.setVisibility(CardView.VISIBLE);

//        cardvededor.setVisibility(CardView.VISIBLE);
    }

public void mostrarBotones(){
    boton_enviar_ok.setVisibility(ImageButton.VISIBLE);
    boton_guardar_ok.setVisibility(ImageButton.VISIBLE);
}
    //calendario

    private void InicializarFecha() {

        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID)
            return new DatePickerDialog(this, dpickerListener, year_x, month_x, day_x);
        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListener
            = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear;
            day_x = dayOfMonth;
            fecha = day_x  + "/" +(month_x + 1) + "/" + year_x;
            txtDate.setText(fecha);
        }
    };

   public void Calendario() {
      //  txtDate = (EditText) findViewById(R.id.date);
        fecha =  day_x  + "/" +(month_x + 1) + "/" + year_x;
   /*     txtDate.setText(fecha);

        txtDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showDialog(DIALOG_ID);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });*/

    }


    public void listaelegidos() {

        SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = BH.getReadableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery("select * from Lista", null);
            int cantidad = c.getCount();
            datos = new listaCarrito[cantidad];
            //Cambia la lista dependiendo de la condicion de pago
            int condicion = condicionSwitch();
            int i = 0;
            if (c.moveToFirst()) {
                do {

                    datos[i] = new listaCarrito(c.getString(0), c.getString(1), c.getString(2), c.getInt(condicion), c.getString(3), c.getInt(4), c.getString(5),c.getString(6),c.getString(7));
                    i++;
                } while (c.moveToNext());

            }

            class AdapterTitulos extends ArrayAdapter<listaCarrito> {
                Activity context;

                public AdapterTitulos(Activity context) {
                    super(context, R.layout.formatocarrito, datos);
                    this.context = context;
                }

                public View getView(final int posicion, View view, ViewGroup parent) {
                    LayoutInflater inflater = context.getLayoutInflater();
                    View item = inflater.inflate(R.layout.formatocarrito, null);

                    TextView titulo = (TextView) item.findViewById(R.id.nombre_select);
                    titulo.setText(datos[posicion].getTitulo());

                    TextView importe = (TextView) item.findViewById(R.id.importe_select);
                    importe.setText("$ " + currency.format(datos[posicion].getPrecio()));

                    TextView cantidad = (TextView) item.findViewById(R.id.cantidad_select);
                    cantidad.setText(" " + datos[posicion].getCantidad());

                       TextView total = (TextView)item.findViewById(R.id.total_select);
                       total.setText(" $ "+ currency.format(mostrarTotal(datos[posicion].getPrecio(), datos[posicion].getCantidad())));

                  //  TextView total = (TextView) item.findViewById(R.id.total_select);
                 //   total.setText("Total: $" + datos[posicion].getPrecio());

             //       TextView sucursal = (TextView) item.findViewById(R.id.sucursal_dis);
            //        sucursal.setText("Almacen : " + datos[posicion].getAlmacen());

                   // infoboton = (ImageButton) item.findViewById(R.id.info_boton);

                /*   cantidad.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Cambiar Cantidad");
                            final EditText input = new EditText(MainActivity.this);
                            input.setInputType(InputType.TYPE_CLASS_NUMBER);
                            builder.setMessage(datos[posicion].getObservacion());

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                  //  cambiarCantidad(input.getText().toString());
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
                    });*/


                    return item;
                }
            }

            AdapterTitulos adaplist = new AdapterTitulos(this);
            listado_agregados.setAdapter(adaplist);
            db.close();
            totalesDB();

        }
    }


    public void listaelegidosborrar() {
        SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = BH.getReadableDatabase();
        db.execSQL("DELETE FROM Lista");
    }

    public float mostrarTotal(Integer precio, Integer cantidad) {

        float totalf = precio * cantidad;

        return totalf;
    }

    //Se agregan los totales en la tabla Regular, MSI , Contado
    public void totalesDB() {

        SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = BH.getReadableDatabase();

        if (db != null) {
            Cursor c = db.rawQuery("select SUM(Regular),SUM(MSI),SUM(Contado) from Lista", null);

            String sumatotal ;
            String regular ;
            String contado;
            if (c.moveToFirst()) {
                do {
                 textview_Regular.setText("$ "+ currency.format(c.getDouble(0)));
                 textView_MSI.setText("$ "+ currency.format(c.getDouble(1)));
                 textView_Contado.setText("$ "+ currency.format(c.getDouble(2)));

                } while (c.moveToNext());

            }
            db.close();

        }

    }

//Se envian los datos a la BD
    public void enviarDatosjson() {

        AsyncHttpClient client = new AsyncHttpClient();
        String url_data = "http://"+ip+"/json_multi.php";
        RequestParams parametros = new RequestParams();
        String maclocal = macAddress;
        String idc = idcliente;
        String idv = idagente;
        String condicionlocal = condicionPago;
        String usuarioLocal = usuario;
        int sucursalLocal = sucursal;
        JSONArray productos = new JSONArray();

        try {

            llenararregloJSON(productos);
            JSONObject data = new JSONObject();
            data.put("MacAdress",maclocal);
            data.put("Empresa", "RTM");
            data.put("Sucursal", sucursalLocal);
            data.put("FechaEmision", fecha);
            data.put("Cliente", idc);
            data.put("Condicion", condicionlocal);
            data.put("Agente", idv);
            data.put("Usuario",usuarioLocal);
            data.put("productos", productos);

         //   Toast.makeText(this, data.toString(), Toast.LENGTH_LONG).show();

            parametros.put("data", data);

           // Toast.makeText(this,"SE ENVIO ARREGLO A LA BASE DE DATOS"+ parametros.toString(), Toast.LENGTH_LONG).show();

            client.post(url_data, parametros, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    //  llamar funcion ..
                    mensajeRegreso(new String(responseBody));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    System.out.print("No se logro conectar al servidor");
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void mensajeRegreso(String response) {
        Toast.makeText(this,"Se envio...esperando respuesta",Toast.LENGTH_LONG).show();
        try {
            String mensaje;
            JSONArray jsonArray = new JSONArray(response);

                mensaje = jsonArray.getJSONObject(0).getString("Mensaje") ;
                Toast.makeText(this,mensaje,Toast.LENGTH_LONG).show();

            if (mensaje.equals("Proceso Concluido")){
                listaelegidosborrar();
                borrarpreferencias();
              //  reiniciarActivity(this);

                Intent intent = new Intent(MainActivity.this, ProductoN.class);
                intent.putExtra("usuario", usuario);
                intent.putExtra("sucursal", sucursal);
                startActivity(intent);
                finish();
            }

        } catch (Exception e) {
            Toast.makeText(this,"No se recibe respuesta: ",Toast.LENGTH_LONG).show();
        }

    }





// Llena el arreglo para JSON a la Base de Datos
     public void llenararregloJSON(JSONArray jsonArray) {
         SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
         SQLiteDatabase db = BH.getReadableDatabase();

         if (db != null) {
                 Cursor c = db.rawQuery("select * from Lista", null);


            if (c.moveToFirst()) {
                //seleccion el precio de la condicion
                    int condicion = condicionSwitch();
                do {
                    try {
                        JSONObject pro = new JSONObject();
                        pro.put("Articulo", c.getString(1));
                        pro.put("Cantidad", c.getString(4));
                        pro.put("Precio", c.getString(condicion));
                        pro.put("Almacen", c.getString(5));
                        pro.put("Renglon", c.getString(0));
                        pro.put("Observaciones", c.getString(7));

                        jsonArray.put(pro);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } while (c.moveToNext());

            }
        }
    }



    //Se envian los datos para crear PDF
    public void enviarDatosPDF(String email) {

        AsyncHttpClient client = new AsyncHttpClient();
        String url_data = "http://"+ip+"/api_json/";
        RequestParams parametros = new RequestParams();
        JSONArray productosPDF = new JSONArray();


        try {

            llenararregloPDF(productosPDF);
            JSONObject data = new JSONObject();

            data.put("FechaEmision", fecha);
            data.put("Cliente",clientPDF);
            data.put("Email",email);
            data.put("Condicion", condicionPago);
            data.put("productos", productosPDF);

           //  Toast.makeText(this, data.toString(), Toast.LENGTH_LONG).show();

            parametros.put("data", data);

            // Toast.makeText(this,"SE ENVIO ARREGLO A LA BASE DE DATOS"+ parametros.toString(), Toast.LENGTH_LONG).show();

            client.post(url_data, parametros, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    //  llamar funcion ...
                    mensajeRegresoPDF(new String(responseBody));
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

    public void mensajeRegresoPDF(String response) {
        Toast.makeText(this,"Se envio...esperando respuesta",Toast.LENGTH_LONG).show();
        list.clear();
        try {
            String mensaje;
            JSONArray jsonArray = new JSONArray(response);

            mensaje = jsonArray.getJSONObject(0).getString("message") ;

            Toast.makeText(this,mensaje,Toast.LENGTH_LONG).show();

            if (mensaje.equals("Enviado")){
                Toast.makeText(this,"Se envio e-mail",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,"No se envio e-mail,Reintentar",Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {

        }

    }


    public void llenararregloPDF(JSONArray J) {
        SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = BH.getReadableDatabase();



        if (db != null) {
            Cursor c = db.rawQuery("select * from Lista", null);
            if (c.moveToFirst()) {
                int condicion =condicionSwitch();
                do {
                    try {
                        JSONObject pro = new JSONObject();
                        pro.put("Articulo", c.getString(2));
                        pro.put("Cantidad", c.getString(4));
                        pro.put("Precio", c.getString(condicion));

                        J.put(pro);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } while (c.moveToNext());

            }
        }
    }

    public int condicionSwitch(){
        int condicion = 8;
        switch(condicionPago){

            case "12 Meses":
                condicion=9;

                break;
            case "Contado":
                condicion=10;
                break;

            default:
                condicion=8;
                break;
        }
        return condicion;
    }



//Alert para pedir E-mail y datos JSON

    public void enviarEmail(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.email_input, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.email_edit);

        dialogBuilder.setTitle("Enviar Cotización");

        dialogBuilder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
              String email = edt.getText().toString();
              enviarDatosPDF(email);
            }
        });
        dialogBuilder.setNegativeButton("Cancelar",null );

        AlertDialog b = dialogBuilder.create();
        b.show();
    }



}


