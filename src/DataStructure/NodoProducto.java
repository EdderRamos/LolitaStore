/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DataStructure;

import Modelo.Productos;

/**
 *
 * @author lee_j
 */
public class NodoProducto {

    private Productos producto;
    private NodoProducto izq, der;

    public Productos getProducto() {
        return producto;
    }

    public void setProducto(Productos producto) {
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

    public NodoProducto(Productos producto) {
        this.producto = producto;
        this.izq = this.der = null;
    }
}
