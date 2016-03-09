package mx.intelisis.maserp.avanti;

/**
 * Created by Roldan on 22/11/15.
 */
public class listaDisponible {

    private String almacen;
    private String disponible;
    private String idalmacen;



    public listaDisponible(String idalmacen,String almacen, String disponible){
        this.almacen = almacen;
        this.disponible = disponible;
        this.idalmacen = idalmacen;
    }


    public String getAlmacen() {return almacen;}

    public void setAlmacen(String sucursal) {this.almacen= almacen;}

    public String getDisponible() {return disponible;}

    public void setDisponible(String disponible) {this.disponible = disponible;}

    public String getIdalmacen() {return idalmacen;}

    public void setIdalmacen(String idalmacen) {this.idalmacen = idalmacen;}
}
