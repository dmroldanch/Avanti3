package mx.intelisis.maserp.avanti;

import java.text.SimpleDateFormat;

/**
 * Created by Roldan on 26/10/15.
 */
public class listaCarrito {
    private String id;
    private String articulo;
    private String titulo;
    private Integer precio;
    private String total;
    private Integer cantidad;
    private String almacen;
    private String idAlmacen;
    private String observacion;

    public listaCarrito(String id,String articulo,String titulo, Integer precio, String total, Integer cantidad,String idAlmacen,String almacen,String observacion){
        this.id=id;
        this.articulo=articulo;
        this.almacen=almacen;
        this.titulo = titulo;
        this.precio = precio;
        this.total  = total;
        this.cantidad = cantidad;
        this.observacion=observacion;
        this.idAlmacen= idAlmacen;
    }



    public String getId() {return id;}

    public String getArticulo() {return articulo;}

    public Integer getPrecio() {return precio;}

    public String getTitulo() {return titulo;}

    public String getTotal() {return total;}

    public Integer getCantidad(){return cantidad;}

    public String getAlmacen() {return almacen;}

    public String getObservacion() {return observacion;}

    public void setId(String id) {this.id = id;}

    public void setArticulo(String articulo) {this.articulo = articulo;}

    public void setTitulo(String titulo) {this.titulo = titulo;}

    public void setPrecio(Integer precio) {this.precio = precio;}

    public void setTotal(String total) {this.total = total;}

    public void setCantidad(Integer cantidad){this.cantidad= cantidad;}

    public void setObservacion(String observacion) {this.observacion = observacion;}


    public void setAlmacen(String sucursal) {this.almacen = sucursal;}

}
