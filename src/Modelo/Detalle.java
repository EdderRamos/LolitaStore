package Modelo;

public class Detalle {
    private int id;
    private String CodigoProducto;
    private int cantidad;
    private double precio;
    private int idVenta;
    
    public Detalle(){
        
    }

    public Detalle(int id, String codigoProducto, int cantidad, double precio, int idVenta) {
        this.id = id;
        this.CodigoProducto = codigoProducto;
        this.cantidad = cantidad;
        this.precio = precio;
        this.idVenta = idVenta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoProducto() {
        return CodigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.CodigoProducto = codigoProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVerta(int idVenta) {
        this.idVenta = idVenta;
    }
    
    
}
