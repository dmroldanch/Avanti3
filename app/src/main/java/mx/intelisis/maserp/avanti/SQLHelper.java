package mx.intelisis.maserp.avanti;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Roldan on 11/10/15.
 */
public class SQLHelper extends SQLiteOpenHelper {

    String sqlTableAgent = "CREATE TABLE Agente(" +
                                                 "Agente varchar(10)," +
                                                 "nombrev varchar(50))";
    String sqlTableClientes = "CREATE TABLE Cliente(" +
                                                 "cliente varchar(10)," +
                                                 "nombrec varchar(50))";
    String sqlTableArt = "CREATE TABLE Articulo(" +
                                                "id varchar(15)," +
                                                "nombrea varchar(50)," +
                                                "precio INTEGER)";
    String sqlTableListaArt = "CREATE TABLE Lista(" +
                                                 "ID INTEGER PRIMARY KEY," +
                                                 "idlis varchar(15)," +
                                                 "nombrelis varchar(50)," +
                                                 "preciolis INTEGER," +
                                                 "disponible INTEGER," +
                                                 "idalmacen varchar(10)," +
                                                 "almacen varchar(40)," +
                                                 "observacion varchar(80),"+
                                                 "Regular INTEGER,"+
                                                 "MSI INTEGER," +
                                                 "Contado INTEGER)";

    String sqlTableSucursales = "CREATE TABLE Sucursal(" +
                                                "sucursal varchar(10)," +
                                                "nombre int)";


    String sqlTableCondicion = "CREATE TABLE Condicion(" +
                                                  "condicion varchar(15))";

    String sqlTableOpciones = "CREATE TABLE ArtGrado(" +
                                                  "Articulo varchar(20)," +
                                                  "Grupo varchar(50)," +
                                                  "SubGrupo varchar(50)," +
                                                  "Opcion varchar(50)," +
                                                  "Tipo varchar(50) ," +
                                                  "Grado varchar(30)," +
                                                  "Precio INTEGER)";


    public SQLHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,"Avanti", factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlTableCondicion);
        db.execSQL(sqlTableAgent);
        db.execSQL(sqlTableClientes);
        db.execSQL(sqlTableArt);
        db.execSQL(sqlTableListaArt);
        db.execSQL(sqlTableOpciones);
        db.execSQL(sqlTableSucursales);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



}





