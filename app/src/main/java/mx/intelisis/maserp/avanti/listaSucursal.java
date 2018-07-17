package mx.intelisis.maserp.avanti;

/**
 * Created by Roldan on 23/04/16.
 */
public class listaSucursal {

    private int sucursal;
    private String nombre;

    public listaSucursal(int sucursal, String nombre) {
        this.sucursal = sucursal;
        this.nombre = nombre;
    }

    public int getSucursal() {
        return sucursal;
    }

    public void setNombre(String nombre) {
        this.nombre= nombre;
    }

    public void setSucursal(int sucursal) {
        this.sucursal = sucursal;
    }

    public String getNombre() {
        return nombre;
    }


}
