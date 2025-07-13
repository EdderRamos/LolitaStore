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
public class ArbolProducto {

    private NodoProducto raiz;

    public void insertar(Producto producto) {
       raiz = insertar(raiz, producto);
    }

    public NodoProducto insertar(NodoProducto nodo, Producto producto) {
        if (nodo == null) {
            return new NodoProducto(producto);
        }
        int cmp = producto.getCodigo().compareTo(nodo.getProducto().getCodigo());

        if (cmp < 0) {
            nodo.setIzq(insertar(nodo.getIzq(), producto));
        } else {
            nodo.setDer(insertar(nodo.getDer(), producto));
        }
        return nodo;
    }

    public Producto buscar(String codigo) {
         return buscar(raiz, codigo);
    }

    public Producto buscar(NodoProducto nodo, String codigo) {
        if (nodo == null) {
            return null;
        }
        int cmp = codigo.compareTo(nodo.getProducto().getCodigo());

        if (cmp == 0) {
            return nodo.getProducto();
        }
        if (cmp < 0) {
            return buscar(nodo.getIzq(), codigo);
        }
        return buscar(nodo.getDer(), codigo);

    }

    public void inOrden() {
        inOrden(raiz);
    }

    private void inOrden(NodoProducto nodo) {
        if (nodo != null) {
            inOrden(nodo.getIzq());
            System.out.println(nodo.getProducto());
            inOrden(nodo.getDer());
        }
    }
}
