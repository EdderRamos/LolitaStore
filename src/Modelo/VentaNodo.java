/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author laptop
 */
public class VentaNodo {
    private Venta dato;
    private VentaNodo sig;

    public VentaNodo(Venta dato) {
        this.dato = dato;
        this.sig = null;
    }

    public Venta getDato() {
        return dato;
    }

    public void setDato(Venta dato) {
        this.dato = dato;
    }

    public VentaNodo getSig() {
        return sig;
    }

    public void setSig(VentaNodo sig) {
        this.sig = sig;
    }
}

