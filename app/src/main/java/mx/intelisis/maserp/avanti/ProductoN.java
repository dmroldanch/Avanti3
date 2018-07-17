package mx.intelisis.maserp.avanti;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;




public class ProductoN extends ActionBarActivity implements NumberPicker.OnValueChangeListener, AdapterView.OnItemSelectedListener, View.OnClickListener {

    String ip = "187.163.97.114:8080";
    private EditText articulo_texto,Precio, Importe,Total,cantidad,observacionText;
    ListView listado_A,listsucursales;
    ImageButton botonagregar,botoncantidad,botonscan,botoncam;
    ArrayAdapter<String>  adapterArt,adapterGrupo,adapterSubgrupo,adapterOpciones,adapterTipo,adapterGrado;
    TextView Oferta,disponibleTitle,PoliticaRegular,PrecioRegular,PrecioContado,PoliticaContado,PrecioMSI,PoliticaMSI;
    /*Variables*/
    Spinner grupo,subGrupo,opciones,tipo,grado;
    String nombredb = null;
    float preciodb = 0,contado = 0,msi= 0,regular=0;

    String iddb = null;
    String almacen = "Stock Bodega Nueva";
    String condicionPago = "Contado";
    String observacion = "Sin observación";
    String nombreConsulta;
    String idalmacen = "85";
    String  grupoS="",subGrupoS="",opcionesS="",tipoS="",gradoS="";
    String usuario;
    int sucursal;
    LinearLayout precioLayout;

    DecimalFormat currency = new DecimalFormat("###,###.##");

    listaDisponible[] datos_almacen=null;

    float p1;
    float p2;
    float p3;
    int cant_int=1;
//Bluetooth

    BluetoothManager mBluetoothManager ;
    BluetoothAdapter mBluetoothAdapter;



    CardView cardopciones;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_producto_n);
        recibirdatos();
        inicializarComponentes();

        leerObjetosA();
        botonAgregar();
        botonCantidad();
      //  editarPrecio();
        camaraCodigo();
        scanCodigo();
        InputMethodManager inputManager = (InputMethodManager)this.getSystemService(INPUT_METHOD_SERVICE);
        inputManager.restartInput(observacionText);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_resumen, menu);
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

        if (id == R.id.cotizacion) {
            Intent intent = new Intent(ProductoN.this, MainActivity.class);
            intent.putExtra("usuario", usuario);
            intent.putExtra("sucursal", sucursal);
            startActivity(intent);
            finish();
        }


        return super.onOptionsItemSelected(item);
    }



   public void recibirdatos(){
        Bundle bundle = this.getIntent().getExtras();
       usuario = bundle.getString("usuario");
       sucursal = bundle.getInt("sucursal");

    }

    public void inicializarprecios (){
       // articulo_texto.setText(null);
        disponibleTitle.setVisibility(TextView.GONE);
        listsucursales.setAdapter(null);
       // Oferta.setText("")condicionPago;
        contado = 200;
        msi = 0;
        regular =0;
        cantidad.setText("1");
        Precio.setText("$ 0.0");
        Importe.setText("$ 0.0");
        Total.setText("$ 0.0");
        preciodb= 0;
        grupoS="";subGrupoS="";opcionesS="";tipoS="";gradoS="";
        PoliticaRegular.setText("");PrecioRegular.setText("");PrecioContado.setText("");PoliticaContado.setText("");PrecioMSI.setText("");PoliticaMSI.setText("");;
        //listsucursales.clear
      //  almacen = null;

    }


   private void botonAgregar() {


        botonagregar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ponerObservacion();
                cargarabd();
           //     startActivity(new Intent(ProductoN.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
             //   inicializarprecios();
                reiniciarActivity(ProductoN.this);
            }
        });
    }

    public void cargarabd(){
        SQLHelper baseHelper = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = baseHelper.getWritableDatabase();

        ContentValues nuevoregistroA = new ContentValues();
        nuevoregistroA.put("idlis",iddb);
        nuevoregistroA.put("disponible",cant_int);
        nuevoregistroA.put("nombrelis", nombredb);
        nuevoregistroA.put("preciolis", preciodb);
        nuevoregistroA.put("preciolis", preciodb);
        nuevoregistroA.put("idalmacen", idalmacen);
        nuevoregistroA.put("almacen", almacen);
        nuevoregistroA.put("observacion", observacion);
        nuevoregistroA.put("Regular",regular);
        nuevoregistroA.put("MSI",msi);
        nuevoregistroA.put("Contado",contado);

        db.insert("Lista", null, nuevoregistroA);

        Toast.makeText(getApplicationContext(), "Articulo Agregado" , Toast.LENGTH_LONG).show();
        db.close();
    }



    private void inicializarComponentes() {

        //  Oferta = (TextView) findViewById(R.id.oferta);
        disponibleTitle = (TextView) findViewById(R.id.disp_title);
        PrecioRegular = (TextView) findViewById(R.id.PrecioRegular);
        PoliticaRegular = (TextView) findViewById(R.id.PoliticaRegular);
        PrecioContado = (TextView) findViewById(R.id.PrecioContado);
        PoliticaContado = (TextView) findViewById(R.id.PoliticaContado);
        PrecioMSI = (TextView) findViewById(R.id.PrecioMSI);
        PoliticaMSI = (TextView) findViewById(R.id.PoliticaMSI);

        precioLayout = (LinearLayout) this.findViewById(R.id.preciolayout);

        Precio = (EditText) findViewById(R.id.Producto_Precio_n);
        Importe = (EditText) findViewById(R.id.Producto_Importe_n);
        Total = (EditText) findViewById(R.id.Total);
        articulo_texto = (EditText) findViewById(R.id.Art_editText);
        cantidad = (EditText) findViewById(R.id.cant_prod);
        observacionText = (EditText) findViewById(R.id.obser);

        cant_int = Integer.valueOf(cantidad.getText().toString());

        botoncantidad = (ImageButton) findViewById(R.id.can_boton);
        botonagregar = (ImageButton) findViewById(R.id.botonagregar);
        botoncam = (ImageButton) findViewById(R.id.botoncamara);
        botonscan = (ImageButton) findViewById(R.id.botonScanner);


        listado_A = (ListView) findViewById(R.id.articuloLista);//Listado vendedor
        listsucursales = (ListView) findViewById(R.id.listDisp);

        cardopciones = (CardView) findViewById(R.id.CardViewOpciones);

      //  mBluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
      //  mBluetoothAdapter = mBluetoothManager.getAdapter();
     //   mBluetoothAdapter.cancelDiscovery();

    }




// Se usa camara para detectar codigo
    public void camaraCodigo() {

        botoncam.setOnClickListener(this);
    }
//Se  abre la aplicacion Scan
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.botoncamara) {
            //Se instancia un objeto de la clase IntentIntegrator
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            //Se procede con el proceso de scaneo
            scanIntegrator.initiateScan();
        }
    }

    // Se usa scanner fisico para detectar codigo
    public void scanCodigo() {


        botonscan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ProductoN.this);
                builder.setTitle("Usar Scanner Fisico");

// Set up the input
                final EditText input = new EditText(ProductoN.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ponerNombre(input.getText().toString());
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
        });
    }




    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //Se obtiene el resultado del proceso de scaneo y se parsea
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            //Quiere decir que se obtuvo resultado pro lo tanto:
            //Desplegamos en pantalla el contenido del código de barra scaneado
            String scanContent = scanningResult.getContents();
            ponerNombre(scanContent);
          //  String scanFormat = scanningResult.getFormatName();
            //     Toast.makeText(this,scanFormat,Toast.LENGTH_LONG).show();
        }else{
            //Quiere decir que NO se obtuvo resultado
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No se ha recibido datos del scaneo!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void ponerObservacion(){
        observacion = observacionText.getText().toString();
        observacion += " "+grupoS+" "+subGrupoS+" "+opcionesS+" "+tipoS+" "+gradoS;
    }


    public void botonCantidad() {


            botoncantidad.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    cant_int = Integer.valueOf(cantidad.getText().toString());// se toma la cantidad antes de cambiar el precio
                    if(preciodb==0){//Verifica que tenga precio  , si existe es por que lo trajo de las opciones
                        ponerId();
                        PonerPrecio();
                    }else{
                        ponerId();
                        Float precio = cant_int * preciodb;
                             Precio.setText("$ "+ preciodb);
                             Importe.setText("$ "+precio);
                              Total.setText("$ " +precio);
                        cardopciones.setVisibility(CardView.GONE);
                        disponible();
                    }
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);//Se usa para quitar el teclado al dar click

                    inputMethodManager.hideSoftInputFromWindow(botoncantidad.getWindowToken(), 0);//Se usa para quitar el teclado al dar click

                    botonagregar.setVisibility(LinearLayout.VISIBLE);
                }
            });
        }



    public void leerObjetosA() {
        SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = BH.getReadableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery("select * from Articulo", null);
            int cantidad = c.getCount();
            String[] arreglo = new String[cantidad];
            int i = 0;
            if (c.moveToFirst()) {
                do {
                    String linea =c.getString(1);

                    arreglo[i] = linea;

                    i++;
                } while (c.moveToNext());

            }
            adapterArt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arreglo);
            listado_A.setAdapter(adapterArt);
            CargaListaA();

        }

    }


    public void PonerPrecio(){
        precio(iddb);
    }


    public void ponerId(){

        SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = BH.getReadableDatabase();
        if (db != null) {
            nombredb = nombreConsulta;
            Cursor c = db.rawQuery("select * from Articulo where nombrea ='" + nombreConsulta + "'", null);

            if (c.moveToFirst()) {
                do {
                    iddb = c.getString(0);

                } while (c.moveToNext());

            }


        }
    }


    public void ponerNombre(String s){

        SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = BH.getReadableDatabase();
        if (db != null) {
                iddb = s;
            Cursor c = db.rawQuery("select nombrea from Articulo where id ='" + iddb + "'", null);

            if (c.moveToFirst()) {
                do {
                    nombredb = c.getString(0);
                    articulo_texto.setText(nombredb);

                } while (c.moveToNext());

            }


        }
    }




    public void CargaListaA() {

        listado_A.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = (String) listado_A.getItemAtPosition(position);
                articulo_texto.setText(value);
                nombreConsulta = value;
                ocultarlistaA();
                verificarOpciones();
            }
        });

        //Filtro vendedor
        articulo_texto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterArt.getFilter().filter(s);
                mostrarlistaA();
                inicializarprecios();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void mostrarlistaA(){
            listado_A.setVisibility(ListView.VISIBLE);
            precioLayout.setVisibility(LinearLayout.GONE);
            botonagregar.setVisibility(LinearLayout.GONE);
            cardopciones.setVisibility(CardView.GONE);
            botoncam.setVisibility(ImageButton.VISIBLE);
            botonscan.setVisibility(ImageButton.VISIBLE);
    }

    public void ocultarlistaA(){
        listado_A.setVisibility(ListView.GONE);
        precioLayout.setVisibility(LinearLayout.VISIBLE);
        botoncam.setVisibility(ImageButton.GONE);
        botonscan.setVisibility(ImageButton.GONE);

     //   botonagregar.setVisibility(LinearLayout.VISIBLE);
    }


 /*   public void editarPrecio() {


        //Cambiar los precios
        Precio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                float f = Float.valueOf(s.toString());
                float total = f * cant_int;

                String s2 =String.valueOf(total);
                  imprimirPrecio(s2);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void imprimirPrecio(String s){

        Importe.setText(s);

        Total.setText(s);
    }*/

    /*Verifica si hay opciones*/

    public void verificarOpciones() {
        SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = BH.getReadableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery("Select Articulo from ArtGrado where Articulo = '" + nombreConsulta + "'", null);
            int cantidadopc = c.getCount();

            if (cantidadopc == 0) {
                    cardopciones.setVisibility(CardView.GONE);

                } else{
                   cardopciones.setVisibility(CardView.VISIBLE);
                   llenargrupo();
            }

            }
        }



    public void precio(String id_producto) {

        AsyncHttpClient client = new AsyncHttpClient();
        String url_v = "http://"+ip+"/procedimiento.php";
        RequestParams parametros = new RequestParams();
        parametros.add("id_producto", id_producto);
        parametros.add("condicion", condicionPago);
        parametros.add("cantidad", String.valueOf(cant_int));

        client.post(url_v, parametros, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    //  llamar funcion ..
                    precio1(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                 Toast.makeText(getApplicationContext(),"Sin una Respuesta en el Precio", Toast.LENGTH_SHORT).show();
            }
        });
        disponible();
    }

    public void precio1(String response) {

        float precioLista;
        String Descuento ;
      //  float p1;
       // float p2;
        //float p3;
        try {
            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++) {

                preciodb = (float) jsonArray.getJSONObject(i).getDouble("Precio");

            //    precioLista = (float) jsonArray.getJSONObject(i).getDouble("Precio");



              //  p1 = preciodb;

                //float f1 = p1;
               // String precioFromato =currency.format(f1);
               // Precio.setText("$ "+precioFromato);

            //    String p4 =currency.format(p1 * cant_int);
           //     Importe.setText("$ "+ p4);
            //    Total.setText("$ "+ p4);

             //   p2 = precioLista;
             //   p3 = p2 - p1;
              //  Descuento = jsonArray.getJSONObject(i).getString("Descuento");
             //   if(p3>0) {
                 //   Oferta.setText(Descuento + "\n\n" + "Precio original: $ " + precioLista + "\n\n" + "Usted ahorra por producto: $ " + p3);
              //  }else{
                  //  Oferta.setText("");
            //}
                //Inserta precio regular

                regular = (float) jsonArray.getJSONObject(i).getDouble("PrecioRegular");
                PrecioRegular.setText( "$ "+currency.format(regular));
                PoliticaRegular.setText( jsonArray.getJSONObject(i).getString("PoliticaRegular"));
                //Inserta precio Contado
                contado = (float) jsonArray.getJSONObject(i).getDouble("PrecioContado");
                PrecioContado.setText( "$ "+currency.format(contado));
                PoliticaContado.setText(jsonArray.getJSONObject(i).getString("PoliticaContado"));
                //Inserta precio MSI
                msi = (float) jsonArray.getJSONObject(i).getDouble("PrecioMSI");
                PrecioMSI.setText( "$ "+currency.format(msi));
                PoliticaMSI.setText(jsonArray.getJSONObject(i).getString("PoliticaMSI"));
            }

        } catch (Exception e) {

        }

    }




    public void disponible() {

        AsyncHttpClient client = new AsyncHttpClient();
        String url_v = "http://"+ip+"/artDisponible.php";
        RequestParams parametros = new RequestParams();
        parametros.add("Art_clave", iddb);

        client.post(url_v, parametros, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    //  llamar funcion ..
                    disponiblelist(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "Sin Respuesta en la Disponiblidad", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void disponiblelist(String response) {

        String sucursal ;
        String cantidad ;
        String id_almacen;

        try {

            JSONArray jsonArray = new JSONArray(response);

            datos_almacen = new listaDisponible[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {

                sucursal = jsonArray.getJSONObject(i).getString("NombreS") ;
                cantidad = jsonArray.getJSONObject(i).getString("Disponible") ;
                id_almacen = jsonArray.getJSONObject(i).getString("Almacen") ;// Solo se guarda
                datos_almacen[i] = new listaDisponible(id_almacen,sucursal,cantidad);// se llena el arreglo para mostrar la lista
            }

            if(jsonArray.length()>0){
                disponibleTitle.setVisibility(TextView.VISIBLE);
                llenardis();
            }

        } catch (Exception e) {

        }

    }




    public void llenardis(){

        class AdapterTitulos extends ArrayAdapter<listaDisponible>{

            Activity context ;
            public  AdapterTitulos(Activity context){

                super(context,R.layout.formatodisponible,datos_almacen);
                this.context = context;
            }

            public View getView(int posicion, View view, ViewGroup parent){
                LayoutInflater inflater = context.getLayoutInflater();
                View item = inflater.inflate(R.layout.formatodisponible, null);

                TextView sucursal = (TextView)item.findViewById(R.id.sucursal);
                sucursal.setText(datos_almacen[posicion].getAlmacen());

                TextView disponible = (TextView)item.findViewById(R.id.cantidad_dis);
                disponible.setText(datos_almacen[posicion].getDisponible());

                return item;
            }
        }

        AdapterTitulos adaplist = new AdapterTitulos(this);
        listsucursales.setAdapter(adaplist);
        seleccionarSucursal();
    }


    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
       System.out.print("Hola mundo esto cambia"); //set the value to textview
    }


    public void seleccionarSucursal() {
        listsucursales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                almacen = datos_almacen[position].getAlmacen();
                idalmacen = datos_almacen[position].getIdalmacen();
                Toast.makeText(getApplicationContext(), "Seleccionate " + idalmacen + "-" + almacen, Toast.LENGTH_SHORT).show();
            }

        });

    }

    //llenar Grupo

    public void llenargrupo() {
        SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = BH.getReadableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery("Select Grupo from ArtGrado  where Articulo = '"+ nombreConsulta +"' Group by Grupo", null);
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
            adapterGrupo = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arreglo);
            grupo = (Spinner) findViewById(R.id.grupo_spinner);
            grupo.setAdapter(adapterGrupo);
            grupo.setOnItemSelectedListener(this);

        }
    }


    public void llenarSubGrupo() {
        SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = BH.getReadableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery("Select SubGrupo from ArtGrado  where Articulo = '"+ nombreConsulta +"' and Grupo = '"+ grupoS +"'Group by SubGrupo", null);
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
            adapterSubgrupo = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arreglo);
            subGrupo = (Spinner) findViewById(R.id.subgrupo_spinner);
            subGrupo.setAdapter(adapterSubgrupo);
            subGrupo.setOnItemSelectedListener(this);
        }
    }

    public void llenarOpciones() {
        SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = BH.getReadableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery("Select Opcion from ArtGrado  where Articulo = '"+ nombreConsulta +"' and Grupo = '"+ grupoS +"'and SubGrupo = '"+subGrupoS +"' Group by Opcion", null);
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
            adapterOpciones = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arreglo);
            opciones = (Spinner) findViewById(R.id.opciones_spinner);
            opciones.setAdapter(adapterOpciones);
            opciones.setOnItemSelectedListener(this);
        }
    }

    public void llenarTipo() {
        SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = BH.getReadableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery("Select Tipo from ArtGrado  where Articulo = '"+ nombreConsulta +"'and Grupo = '"+ grupoS +"'and SubGrupo = '"+subGrupoS +"'and Opcion = '"+opcionesS+"' Group by Tipo", null);
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
            adapterTipo = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arreglo);
            tipo = (Spinner) findViewById(R.id.tipo_spinner);
            tipo.setAdapter(adapterTipo);
            tipo.setOnItemSelectedListener(this);
        }
    }

    public void llenarGrado() {
        SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = BH.getReadableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery("Select Grado from ArtGrado  where Articulo = '"+ nombreConsulta +"'and Grupo = '"+ grupoS +"'and SubGrupo = '"+subGrupoS +"'and Opcion = '"+opcionesS+"' and Tipo = '"+tipoS+"' Group by Grado", null);
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
            adapterGrado = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arreglo);
            grado = (Spinner) findViewById(R.id.grado_spinner);
            grado.setAdapter(adapterGrado);
            grado.setOnItemSelectedListener(this);
        }
    }





    @Override
    public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
        TextView myText = (TextView) view;


        switch (adapter.getId()) {
            case R.id.grupo_spinner:
                grupoS = (String) myText.getText();
                llenarSubGrupo();
                break;

            case R.id.subgrupo_spinner:
                subGrupoS = (String) myText.getText();
                llenarOpciones();
                break;

            case R.id.opciones_spinner:
                opcionesS = (String) myText.getText();
                llenarTipo();
                break;

            case R.id.tipo_spinner:
                tipoS = (String) myText.getText();
                llenarGrado();
                break;

            case R.id.grado_spinner:
                gradoS = (String) myText.getText();
                precioOpciones();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    public void precioOpciones() {

        AsyncHttpClient client = new AsyncHttpClient();
        String url_v = "http://"+ip+"/procedimientoNolinea.php";
        RequestParams parametros = new RequestParams();
        parametros.add("id_producto",nombreConsulta);
        parametros.add("condicion", condicionPago);
        parametros.add("grupoS", grupoS);
        parametros.add("subGrupoS", subGrupoS);
        parametros.add("opcionesS", opcionesS);
        parametros.add("tipoS", tipoS);
        parametros.add("gradoS", gradoS);

        client.post(url_v, parametros, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    //  llamar funcion ..
                    precioNolinea(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(),"Sin Respuesta en el Precio", Toast.LENGTH_SHORT).show();
            }
        });
        disponible();
    }

    public void precioNolinea(String response) {

        float precioLista;

        try {
            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++) {

                preciodb = (float) jsonArray.getJSONObject(i).getDouble("Precio");

                precioLista = (float) jsonArray.getJSONObject(i).getDouble("Precio");



            //    p1 = preciodb;

            //    float f1 = p1;
             //   String precioFromato =currency.format(f1);
             //   Precio.setText("$ "+precioFromato);

              //  String p4 =currency.format(p1 * cant_int);
              //  Importe.setText("$ "+ p4);
              //  Total.setText("$ "+ p4);

              //  p2 = precioLista;
              //  p3 = p2 - p1;
                //  Descuento = jsonArray.getJSONObject(i).getString("Descuento");
              //  if(p3>0) {
                    //   Oferta.setText(Descuento + "\n\n" + "Precio original: $ " + precioLista + "\n\n" + "Usted ahorra por producto: $ " + p3);
               // }else{
                    //  Oferta.setText("");
              //  }

                regular = (float) jsonArray.getJSONObject(i).getDouble("PrecioRegular");
                PrecioRegular.setText( "$ "+currency.format(regular));
                PoliticaRegular.setText( jsonArray.getJSONObject(i).getString("PoliticaRegular"));
                //Inserta precio Contado
                contado = (float) jsonArray.getJSONObject(i).getDouble("PrecioContado");
                PrecioContado.setText( "$ "+currency.format(contado));
                PoliticaContado.setText(jsonArray.getJSONObject(i).getString("PoliticaContado"));
                //Inserta precio MSI
                msi = (float) jsonArray.getJSONObject(i).getDouble("PrecioMSI");
                PrecioMSI.setText( "$ "+currency.format(msi));
                PoliticaMSI.setText(jsonArray.getJSONObject(i).getString("PoliticaMSI"));

            }

        } catch (Exception e) {

        }

    }

    public void reiniciarActivity(Activity actividad){
        Intent intent=new Intent();
        intent.setClass(actividad, actividad.getClass());

        intent.putExtra("usuario", usuario);
        intent.putExtra("sucursal", sucursal);
        //llamamos a la actividad
        actividad.startActivity(intent);
        //finalizamos la actividad actual

        actividad.finish();
    }




}
