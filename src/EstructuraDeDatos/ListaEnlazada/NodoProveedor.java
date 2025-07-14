/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EstructuraDeDatos.ListaEnlazada;

import modelo.Proveedor;

/**
 *
 * @author lee_j
 */
public class NodoProveedor {

    private Proveedor proveedor;
    private NodoProveedor siguiente;

    public NodoProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
        this.siguiente = null;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public NodoProveedor getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoProveedor siguiente) {
        this.siguiente = siguiente;
    }

}
