/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EstructuraDeDatos.ABB;

import Modelo.Producto;

/**
 *
 * @author lee_j
 */
public class NodoProducto {

    private Producto producto;
    private NodoProducto izq, der;

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public NodoProducto getIzq() {
        return izq;
    }

    public void setIzq(NodoProducto izq) {
        this.izq = izq;
    }

    public NodoProducto getDer() {
        return der;
    }

    public void setDer(NodoProducto der) {
        this.der = der;
    }

    public NodoProducto(Producto producto) {
        this.producto = producto;
        this.izq = this.der = null;
    }
}
