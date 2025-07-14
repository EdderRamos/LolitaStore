/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EstructuraDeDatos.Colas;

import modelo.Venta;


/**
 *
 * @author lee_j
 */
public class NodoVenta {
    private Venta dato;
    private NodoVenta sig;
    
    public NodoVenta(Venta dato) {
        this.dato = dato;
        this.sig = null;
    }
    
    public Venta getDato() {
        return dato;
    }
    

    public NodoVenta getSig() {
        return sig;
    }


    public void setSig(NodoVenta sig ) {
        this.sig = sig;
    }

    @Override
    public String toString() {
        return "Nodo{" +
                "dato=" + dato +
                ", sig=" + sig +
                '}';
    }

}